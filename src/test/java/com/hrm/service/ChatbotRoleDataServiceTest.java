package com.hrm.service;

import com.hrm.dao.ChatbotRoleDataRepository;
import com.hrm.dao.ChatbotRoleDataRepository.EmployeeSnapshot;
import com.hrm.dao.ChatbotRoleDataRepository.GuestSnapshot;
import com.hrm.dao.ChatbotRoleDataRepository.HrSnapshot;
import com.hrm.dao.ChatbotRoleDataRepository.ManagerSnapshot;
import com.hrm.model.entity.Role;
import com.hrm.model.entity.SystemUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Test: ChatbotRoleDataService - 100% Branch Coverage")
public class ChatbotRoleDataServiceTest {

    private static class DummyRepo implements ChatbotRoleDataRepository {
        private final boolean populated;

        DummyRepo(boolean populated) {
            this.populated = populated;
        }

        @Override
        public GuestSnapshot loadGuestSnapshot(int userId) {
            if (!populated) return new GuestSnapshot(0, null, null, null, null, null, null);
            return new GuestSnapshot(1, "Applied", "Screening", LocalDateTime.now(), "Scheduled", "Offered", LocalDate.now());
        }

        @Override
        public EmployeeSnapshot loadEmployeeSnapshot(int employeeId) {
            if (!populated) return new EmployeeSnapshot(0, null, 0, 0, 0, null, null, null, null, null);
            return new EmployeeSnapshot(1, "Pending", 1, 2, 3, "Active", "Full-time", LocalDate.now(), "2026-07", "Approved");
        }

        @Override
        public ManagerSnapshot loadManagerSnapshot(int employeeId) {
            if (!populated) return new ManagerSnapshot(null, 0, 0, 0, 0);
            return new ManagerSnapshot("Engineering", 2, 1, 2, 3);
        }

        @Override
        public HrSnapshot loadHrSnapshot() {
            if (!populated) return new HrSnapshot(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
            return new HrSnapshot(10, 2, 3, 5, 4, 1, 2, 3, 4, 5);
        }
    }

    private SystemUser createUser(int userId, int roleId, String roleName, Integer empId) {
        SystemUser user = new SystemUser();
        user.setUserId(userId);
        user.setRoleId(roleId);
        user.setEmployeeId(empId);
        Role r = new Role();
        r.setRoleId(roleId);
        r.setRoleName(roleName);
        user.setRole(r);
        return user;
    }

    @Test
    void testAnswerBranches() {
        ChatbotRoleDataService servicePopulated = new ChatbotRoleDataService(new DummyRepo(true), (u, p) -> true);
        ChatbotRoleDataService serviceEmpty = new ChatbotRoleDataService(new DummyRepo(false), (u, p) -> false);

        // Null / blank / invalid
        assertTrue(servicePopulated.answer(null, createUser(1, 1, "Admin", 1), "Admin").isEmpty());
        assertTrue(servicePopulated.answer("application_status", null, "Guest").isEmpty());
        assertTrue(servicePopulated.answer("   ", createUser(1, 1, "Admin", 1), "Admin").isEmpty());

        // Guest role (role 6)
        SystemUser guest = createUser(1, 6, "Guest", null);
        assertTrue(servicePopulated.answer("application_status", guest, "Guest").isPresent());
        assertTrue(servicePopulated.answer("interview_help", guest, "Guest").isPresent());
        assertTrue(servicePopulated.answer("offer_help", guest, "Guest").isPresent());
        assertTrue(servicePopulated.answer("unknown_intent", guest, "Guest").isEmpty());

        assertTrue(serviceEmpty.answer("application_status", guest, "Guest").isPresent());
        assertTrue(serviceEmpty.answer("interview_help", guest, "Guest").isPresent());
        assertTrue(serviceEmpty.answer("offer_help", guest, "Guest").isPresent());

        // Employee role (role 5)
        SystemUser empWithId = createUser(2, 5, "Employee", 100);
        SystemUser empNoId = createUser(3, 5, "Employee", null);

        assertTrue(servicePopulated.answer("leave_request", empWithId, "Employee").isPresent());
        assertTrue(servicePopulated.answer("task_view", empWithId, "Employee").isPresent());
        assertTrue(servicePopulated.answer("contract_view", empWithId, "Employee").isPresent());
        assertTrue(servicePopulated.answer("payroll_view", empWithId, "Employee").isPresent());
        assertTrue(servicePopulated.answer("unknown_intent", empWithId, "Employee").isEmpty());

        assertTrue(serviceEmpty.answer("leave_request", empWithId, "Employee").isPresent());
        assertTrue(serviceEmpty.answer("contract_view", empWithId, "Employee").isPresent());
        assertTrue(serviceEmpty.answer("payroll_view", empWithId, "Employee").isPresent());

        assertTrue(servicePopulated.answer("leave_request", empNoId, "Employee").isPresent());
        assertTrue(servicePopulated.answer("unknown_intent", empNoId, "Employee").isEmpty());

        // Dept Manager role (role 3)
        SystemUser mgrWithId = createUser(4, 3, "Dept Manager", 200);
        SystemUser mgrNoId = createUser(5, 3, "Dept Manager", null);

        assertTrue(servicePopulated.answer("leave_request", mgrWithId, "Dept Manager").isPresent());
        assertTrue(servicePopulated.answer("task_view", mgrWithId, "Dept Manager").isPresent());
        assertTrue(servicePopulated.answer("unknown_intent", mgrWithId, "Dept Manager").isEmpty());

        assertTrue(serviceEmpty.answer("leave_request", mgrWithId, "Dept Manager").isPresent());
        assertTrue(servicePopulated.answer("leave_request", mgrNoId, "Dept Manager").isPresent());

        // HR Staff / Manager (roles 2, 4)
        SystemUser hrUser = createUser(6, 4, "HR Staff", 300);
        assertTrue(servicePopulated.answer("candidate_help", hrUser, "HR Staff").isPresent());
        assertTrue(servicePopulated.answer("interview_help", hrUser, "HR Staff").isPresent());
        assertTrue(servicePopulated.answer("offer_help", hrUser, "HR Staff").isPresent());
        assertTrue(servicePopulated.answer("leave_request", hrUser, "HR Staff").isPresent());
        assertTrue(servicePopulated.answer("payroll_view", hrUser, "HR Staff").isPresent());
        assertTrue(servicePopulated.answer("contract_view", hrUser, "HR Staff").isPresent());
        assertTrue(servicePopulated.answer("unknown_intent", hrUser, "HR Staff").isEmpty());

        assertTrue(serviceEmpty.answer("candidate_help", hrUser, "HR Staff").isPresent());
        assertTrue(serviceEmpty.answer("unknown_intent", hrUser, "HR Staff").isEmpty());
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(new ChatbotRoleDataService());
    }
}
