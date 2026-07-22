package com.hrm.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Test: NotificationRecipientService - 100% Branch Coverage")
public class NotificationRecipientServiceTest {

    private static class DummyRepo implements NotificationRecipientService.RecipientRepository {
        @Override
        public List<Integer> findActiveUserIdsByRoleName(String roleName) {
            if ("Admin".equalsIgnoreCase(roleName)) return List.of(1, 2);
            if ("HR Staff".equalsIgnoreCase(roleName)) return List.of(3);
            if ("HR Manager".equalsIgnoreCase(roleName)) return List.of(4);
            return Collections.emptyList();
        }
        @Override
        public List<Integer> findDeptManagerUserIdsByDepartmentId(int departmentId) {
            if (departmentId == 10) return List.of(5, 6);
            return Collections.emptyList();
        }
        @Override
        public Integer findActiveUserIdByEmployeeId(int employeeId) {
            if (employeeId == 100) return 7;
            return null;
        }
    }

    @Test
    void testActiveUsersByRoleBranches() {
        NotificationRecipientService service = new NotificationRecipientService(new DummyRepo());
        assertTrue(service.activeUsersByRole(null).isEmpty());
        assertTrue(service.activeUsersByRole("  ").isEmpty());
        assertEquals(2, service.activeUsersByRole("Admin").size());
        assertTrue(service.activeUsersByRole("UNKNOWN").isEmpty());
    }

    @Test
    void testDeptManagersByDepartmentBranches() {
        NotificationRecipientService service = new NotificationRecipientService(new DummyRepo());
        assertTrue(service.deptManagersByDepartment(0).isEmpty());
        assertTrue(service.deptManagersByDepartment(-5).isEmpty());
        assertEquals(2, service.deptManagersByDepartment(10).size());
    }

    @Test
    void testRoleHelpers() {
        NotificationRecipientService service = new NotificationRecipientService(new DummyRepo());
        assertEquals(1, service.hrStaffUsers().size());
        assertEquals(1, service.hrManagerUsers().size());
        assertEquals(2, service.adminUsers().size());
    }

    @Test
    void testActiveUserByEmployeeIdBranches() {
        NotificationRecipientService service = new NotificationRecipientService(new DummyRepo());
        assertNull(service.activeUserByEmployeeId(0));
        assertNull(service.activeUserByEmployeeId(-1));
        assertEquals(7, service.activeUserByEmployeeId(100));
        assertNull(service.activeUserByEmployeeId(999));
    }

    @Test
    void testActiveUsersByEmployeeIdsBranches() {
        NotificationRecipientService service = new NotificationRecipientService(new DummyRepo());
        assertTrue(service.activeUsersByEmployeeIds(null).isEmpty());
        assertTrue(service.activeUsersByEmployeeIds(Collections.emptyList()).isEmpty());
        List<Integer> empIds = Arrays.asList(null, 0, -1, 100, 999);
        List<Integer> result = service.activeUsersByEmployeeIds(empIds);
        assertEquals(1, result.size());
        assertTrue(result.contains(7));
    }
}
