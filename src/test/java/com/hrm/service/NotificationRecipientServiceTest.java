package com.hrm.service;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotificationRecipientServiceTest {

    @Test
    void returnsActiveUsersForRoleName() {
        NotificationRecipientService service = new NotificationRecipientService(
                new FakeRecipientRepository(List.of(4, 8), List.of(), null)
        );

        List<Integer> recipients = service.activeUsersByRole("HR Staff");

        assertEquals(List.of(4, 8), recipients);
    }

    @Test
    void ignoresBlankRoleName() {
        NotificationRecipientService service = new NotificationRecipientService(
                new FakeRecipientRepository(List.of(4, 8), List.of(), null)
        );

        List<Integer> recipients = service.activeUsersByRole(" ");

        assertEquals(List.of(), recipients);
    }

    @Test
    void returnsDepartmentManagersForDepartment() {
        NotificationRecipientService service = new NotificationRecipientService(
                new FakeRecipientRepository(List.of(), List.of(11, 12), 0)
        );

        List<Integer> recipients = service.deptManagersByDepartment(3);

        assertEquals(List.of(11, 12), recipients);
    }

    @Test
    void returnsActiveUserForEmployeeId() {
        NotificationRecipientService service = new NotificationRecipientService(
                new FakeRecipientRepository(List.of(), List.of(), 42)
        );

        Integer recipient = service.activeUserByEmployeeId(7);

        assertEquals(42, recipient);
    }

    private record FakeRecipientRepository(List<Integer> roleUsers, List<Integer> deptManagers, Integer employeeUserId)
            implements NotificationRecipientService.RecipientRepository {

        @Override
        public List<Integer> findActiveUserIdsByRoleName(String roleName) {
            return roleUsers;
        }

        @Override
        public List<Integer> findDeptManagerUserIdsByDepartmentId(int departmentId) {
            return deptManagers;
        }

        @Override
        public Integer findActiveUserIdByEmployeeId(int employeeId) {
            return employeeUserId;
        }
    }
}
