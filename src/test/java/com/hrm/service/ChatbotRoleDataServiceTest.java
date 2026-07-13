package com.hrm.service;

import com.hrm.dao.ChatbotRoleDataRepository;
import com.hrm.model.entity.SystemUser;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatbotRoleDataServiceTest {

    @Test
    void guestOnlyReceivesOwnRecruitmentSummary() {
        FakeRepository repository = new FakeRepository();
        repository.guest = new ChatbotRoleDataRepository.GuestSnapshot(
                2, "Interview", "Interview", LocalDateTime.of(2026, 7, 20, 9, 30),
                "Scheduled", "Sent", LocalDate.of(2026, 8, 1));
        ChatbotRoleDataService service = service(repository, true);
        SystemUser guest = user(42, 6, null);

        String reply = service.answer("application_status", guest, "Guest").orElseThrow();

        assertTrue(reply.contains("2 hồ sơ"));
        assertTrue(reply.contains("Phỏng vấn"));
    }

    @Test
    void employeePayrollSummaryNeverContainsSalaryAmount() {
        FakeRepository repository = new FakeRepository();
        repository.employee = new ChatbotRoleDataRepository.EmployeeSnapshot(
                1, "Pending", 2, 1, 3, "Active", "Full-time",
                LocalDate.of(2027, 1, 1), "2026-06", "Paid");
        ChatbotRoleDataService service = service(repository, true);
        SystemUser employee = user(12, 5, 7);

        String reply = service.answer("payroll_view", employee, "Employee").orElseThrow();

        assertTrue(reply.contains("2026-06"));
        assertTrue(reply.contains("không hiển thị số tiền"));
        assertFalse(reply.matches(".*\\d{7,}.*"));
    }

    @Test
    void departmentManagerReceivesDepartmentCountsOnly() {
        FakeRepository repository = new FakeRepository();
        repository.manager = new ChatbotRoleDataRepository.ManagerSnapshot(
                "Engineering", 3, 4, 2, 8);
        ChatbotRoleDataService service = service(repository, true);
        SystemUser manager = user(8, 3, 3);

        String reply = service.answer("task_view", manager, "Dept Manager").orElseThrow();

        assertTrue(reply.contains("Engineering"));
        assertTrue(reply.contains("4 đang chờ"));
    }

    @Test
    void departmentManagerWithoutPermissionGetsDenial() {
        FakeRepository repository = new FakeRepository();
        ChatbotRoleDataService service = service(repository, false);
        SystemUser manager = user(8, 3, 3);

        String reply = service.answer("leave_request", manager, "Dept Manager").orElseThrow();

        assertTrue(reply.contains("chưa được cấp quyền"));
    }

    @Test
    void hrCandidateSummaryRequiresPermissionAndReturnsCounts() {
        FakeRepository repository = new FakeRepository();
        repository.hr = new ChatbotRoleDataRepository.HrSnapshot(
                10, 3, 2, 1, 4, 2, 1, 5, 2, 3);
        ChatbotRoleDataService service = service(repository, true);
        SystemUser hr = user(3, 4, 2);

        String reply = service.answer("candidate_help", hr, "HR Staff").orElseThrow();

        assertTrue(reply.contains("10 hồ sơ"));
        assertTrue(reply.contains("3 đang sàng lọc"));
    }

    @Test
    void unauthenticatedUserNeverLoadsRoleData() {
        ChatbotRoleDataService service = service(new FakeRepository(), true);

        assertTrue(service.answer("payroll_view", null, null).isEmpty());
    }

    private ChatbotRoleDataService service(FakeRepository repository, boolean permission) {
        return new ChatbotRoleDataService(repository, (user, code) -> permission);
    }

    private SystemUser user(int userId, int roleId, Integer employeeId) {
        SystemUser user = new SystemUser();
        user.setUserId(userId);
        user.setRoleId(roleId);
        user.setEmployeeId(employeeId);
        return user;
    }

    private static class FakeRepository implements ChatbotRoleDataRepository {
        private GuestSnapshot guest = new GuestSnapshot(0, null, null, null, null, null, null);
        private EmployeeSnapshot employee = new EmployeeSnapshot(
                0, null, 0, 0, 0, null, null, null, null, null);
        private ManagerSnapshot manager = new ManagerSnapshot(null, 0, 0, 0, 0);
        private HrSnapshot hr = new HrSnapshot(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

        @Override
        public GuestSnapshot loadGuestSnapshot(int userId) throws SQLException {
            return guest;
        }

        @Override
        public EmployeeSnapshot loadEmployeeSnapshot(int employeeId) throws SQLException {
            return employee;
        }

        @Override
        public ManagerSnapshot loadManagerSnapshot(int managerEmployeeId) throws SQLException {
            return manager;
        }

        @Override
        public HrSnapshot loadHrSnapshot() throws SQLException {
            return hr;
        }
    }
}
