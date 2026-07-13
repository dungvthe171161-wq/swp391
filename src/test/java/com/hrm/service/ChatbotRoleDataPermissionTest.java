package com.hrm.service;

import com.hrm.dao.ChatbotRoleDataRepository;
import com.hrm.model.entity.SystemUser;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatbotRoleDataPermissionTest {

    @Test
    void hrPermissionIsCheckedBeforeRepositoryQuery() {
        ChatbotRoleDataService service = new ChatbotRoleDataService(
                new FailingHrRepository(), (user, code) -> false);
        SystemUser hr = new SystemUser();
        hr.setUserId(4);
        hr.setRoleId(4);
        hr.setEmployeeId(6);

        String reply = service.answer("candidate_help", hr, "HR Staff").orElseThrow();

        assertTrue(reply.contains("chưa được cấp quyền"));
    }

    private static class FailingHrRepository implements ChatbotRoleDataRepository {
        @Override
        public GuestSnapshot loadGuestSnapshot(int userId) {
            throw new AssertionError("Guest repository must not be called.");
        }

        @Override
        public EmployeeSnapshot loadEmployeeSnapshot(int employeeId) {
            throw new AssertionError("Employee repository must not be called.");
        }

        @Override
        public ManagerSnapshot loadManagerSnapshot(int managerEmployeeId) {
            throw new AssertionError("Manager repository must not be called.");
        }

        @Override
        public HrSnapshot loadHrSnapshot() throws SQLException {
            throw new AssertionError("HR repository must not run before permission check.");
        }
    }
}
