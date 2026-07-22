package com.hrm.service;

import com.hrm.model.entity.Notification;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationServiceTest {

    @Test
    void buildNotificationPopulatesRoutingFields() {
        NotificationService service = new NotificationService(new FakeNotificationRepository());

        Notification notification = service.buildNotification(
                7,
                3,
                "Task",
                12,
                "Task",
                "New task",
                "You have a new task",
                "/employee/tasks",
                "High"
        );

        assertEquals(7, notification.getUserId());
        assertEquals(Integer.valueOf(3), notification.getActorUserId());
        assertEquals("Task", notification.getEntityType());
        assertEquals(Integer.valueOf(12), notification.getEntityId());
        assertEquals("Task", notification.getType());
        assertEquals("New task", notification.getTitle());
        assertEquals("You have a new task", notification.getMessage());
        assertEquals("/employee/tasks", notification.getTargetUrl());
        assertEquals("High", notification.getPriority());
        assertFalse(notification.isRead());
    }

    @Test
    void notifyUsersCreatesOneNotificationPerRecipient() {
        FakeNotificationRepository repository = new FakeNotificationRepository();
        NotificationService service = new NotificationService(repository);
        Notification template = service.buildNotification(
                0,
                9,
                "Payroll",
                44,
                "Payroll",
                "Payroll approved",
                "Your payroll was approved",
                "/employee/payroll",
                "Normal"
        );

        int created = service.notifyUsers(List.of(11, 12, 13), template);

        assertEquals(3, created);
        assertEquals(List.of(11, 12, 13), repository.createdUserIds);
    }

    @Test
    void normalizesUnsafeTargetUrlToNull() {
        NotificationService service = new NotificationService(new FakeNotificationRepository());

        Notification notification = service.buildNotification(
                7,
                null,
                "System",
                null,
                "System",
                "External url",
                "Should not redirect outside the app",
                "https://example.com",
                null
        );

        assertEquals("Normal", notification.getPriority());
        assertTrue(notification.getTargetUrl() == null || notification.getTargetUrl().isBlank());
    }

    @Test
    void notifiesHrStaffAboutNewApplication() {
        FakeNotificationRepository repository = new FakeNotificationRepository();
        NotificationService service = new NotificationService(repository);

        int created = service.notifyNewApplicationForHrStaff(
                List.of(21, 22),
                5,
                99,
                "Nguyen Van A",
                "Java Developer"
        );

        assertEquals(2, created);
        assertEquals(List.of(21, 22), repository.createdUserIds);
        assertEquals("Application", repository.createdNotifications.get(0).getType());
        assertEquals("Application", repository.createdNotifications.get(0).getEntityType());
        assertEquals(Integer.valueOf(99), repository.createdNotifications.get(0).getEntityId());
        assertEquals("/candidates?applicationId=99", repository.createdNotifications.get(0).getTargetUrl());
        assertEquals("Co ho so ung tuyen moi", repository.createdNotifications.get(0).getTitle());
        assertEquals("Nguyen Van A vua nop ho so cho vi tri Java Developer.",
                repository.createdNotifications.get(0).getMessage());
    }

    @Test
    void notifiesDeptManagersAboutNewLeaveRequest() {
        FakeNotificationRepository repository = new FakeNotificationRepository();
        NotificationService service = new NotificationService(repository);

        int created = service.notifyNewLeaveRequestForDeptManagers(
                List.of(31, 32),
                15,
                120,
                "Tran Thi B",
                "2026-07-02",
                "2026-07-03"
        );

        assertEquals(2, created);
        Notification notification = repository.createdNotifications.get(0);
        assertEquals("Leave", notification.getType());
        assertEquals("Leave", notification.getEntityType());
        assertEquals(Integer.valueOf(120), notification.getEntityId());
        assertEquals("/dept/leaves?status=Pending", notification.getTargetUrl());
        assertEquals("Co don nghi phep moi", notification.getTitle());
        assertEquals("Tran Thi B da gui don nghi phep tu 2026-07-02 den 2026-07-03.",
                notification.getMessage());
    }

    @Test
    void notifiesEmployeeAboutLeaveDecision() {
        FakeNotificationRepository repository = new FakeNotificationRepository();
        NotificationService service = new NotificationService(repository);

        int created = service.notifyLeaveDecisionForEmployee(
                41,
                7,
                121,
                "Approved"
        );

        assertEquals(1, created);
        Notification notification = repository.createdNotifications.get(0);
        assertEquals(41, notification.getUserId());
        assertEquals(Integer.valueOf(7), notification.getActorUserId());
        assertEquals("Leave", notification.getType());
        assertEquals("Leave", notification.getEntityType());
        assertEquals(Integer.valueOf(121), notification.getEntityId());
        assertEquals("/employee/leaves", notification.getTargetUrl());
        assertEquals("Don nghi phep da duoc duyet", notification.getTitle());
    }

    @Test
    void notifiesEmployeesAboutAssignedTask() {
        FakeNotificationRepository repository = new FakeNotificationRepository();
        NotificationService service = new NotificationService(repository);

        int created = service.notifyTaskAssignedToEmployees(
                List.of(51, 52),
                8,
                501,
                "Bao cao tuan"
        );

        assertEquals(2, created);
        Notification notification = repository.createdNotifications.get(0);
        assertEquals("Task", notification.getType());
        assertEquals("Task", notification.getEntityType());
        assertEquals(Integer.valueOf(501), notification.getEntityId());
        assertEquals("/employee/tasks", notification.getTargetUrl());
        assertEquals("Bạn có công việc mới", notification.getTitle());
        assertEquals("Công việc mới: Bao cao tuan.", notification.getMessage());
    }

    @Test
    void notifiesDeptManagersAboutTaskStatusUpdate() {
        FakeNotificationRepository repository = new FakeNotificationRepository();
        NotificationService service = new NotificationService(repository);

        int created = service.notifyTaskStatusUpdatedForManagers(
                List.of(61),
                12,
                502,
                "Bao cao tuan",
                "Completed",
                "Tran Thi B"
        );

        assertEquals(1, created);
        Notification notification = repository.createdNotifications.get(0);
        assertEquals("Task", notification.getType());
        assertEquals("Task", notification.getEntityType());
        assertEquals(Integer.valueOf(502), notification.getEntityId());
        assertEquals("/taskManager", notification.getTargetUrl());
        assertEquals("Cong viec duoc cap nhat", notification.getTitle());
        assertEquals("Tran Thi B da cap nhat Bao cao tuan sang Completed.", notification.getMessage());
    }

    @Test
    void notifiesHrManagersAboutPendingPayroll() {
        FakeNotificationRepository repository = new FakeNotificationRepository();
        NotificationService service = new NotificationService(repository);

        int created = service.notifyPayrollPendingForHrManagers(List.of(71), 9, 601, "2026-06");

        assertEquals(1, created);
        Notification notification = repository.createdNotifications.get(0);
        assertEquals("Payroll", notification.getType());
        assertEquals("Payroll", notification.getEntityType());
        assertEquals(Integer.valueOf(601), notification.getEntityId());
        assertEquals("/hr/payroll-approval", notification.getTargetUrl());
        assertEquals("Payroll cho duyet", notification.getTitle());
    }

    @Test
    void notifiesHrStaffAboutPayrollDecision() {
        FakeNotificationRepository repository = new FakeNotificationRepository();
        NotificationService service = new NotificationService(repository);

        int created = service.notifyPayrollDecisionForHrStaff(List.of(81, 82), 10, 602, "Rejected");

        assertEquals(2, created);
        Notification notification = repository.createdNotifications.get(0);
        assertEquals("Payroll", notification.getType());
        assertEquals("Payroll", notification.getEntityType());
        assertEquals(Integer.valueOf(602), notification.getEntityId());
        assertEquals("/hrstaff/payroll", notification.getTargetUrl());
        assertEquals("Payroll da bi tu choi", notification.getTitle());
    }

    @Test
    void notifiesHrManagersAboutPendingRecruitment() {
        FakeNotificationRepository repository = new FakeNotificationRepository();
        NotificationService service = new NotificationService(repository);

        int created = service.notifyRecruitmentPendingForHrManagers(List.of(91), 11, 701, "Java Developer");

        assertEquals(1, created);
        Notification notification = repository.createdNotifications.get(0);
        assertEquals("Recruitment", notification.getType());
        assertEquals("Recruitment", notification.getEntityType());
        assertEquals(Integer.valueOf(701), notification.getEntityId());
        assertEquals("/viewRecruitment", notification.getTargetUrl());
        assertEquals("Tin tuyen dung cho duyet", notification.getTitle());
    }

    @Test
    void notifiesHrStaffAboutRecruitmentDecision() {
        FakeNotificationRepository repository = new FakeNotificationRepository();
        NotificationService service = new NotificationService(repository);

        int created = service.notifyRecruitmentDecisionForHrStaff(List.of(101), 12, 702, "Java Developer", "Applied");

        assertEquals(1, created);
        Notification notification = repository.createdNotifications.get(0);
        assertEquals("Recruitment", notification.getType());
        assertEquals("Recruitment", notification.getEntityType());
        assertEquals(Integer.valueOf(702), notification.getEntityId());
        assertEquals("/postRecruitments", notification.getTargetUrl());
        assertEquals("Tin tuyen dung da duoc duyet", notification.getTitle());
    }

    @Test
    void notifiesHrManagersAboutPendingContract() {
        FakeNotificationRepository repository = new FakeNotificationRepository();
        NotificationService service = new NotificationService(repository);

        int created = service.notifyContractPendingForHrManagers(List.of(111), 13, 801, "Nguyen Van C");

        assertEquals(1, created);
        Notification notification = repository.createdNotifications.get(0);
        assertEquals("Contract", notification.getType());
        assertEquals("Contract", notification.getEntityType());
        assertEquals(Integer.valueOf(801), notification.getEntityId());
        assertEquals("/hr/approve-reject-contracts", notification.getTargetUrl());
        assertEquals("Hop dong cho duyet", notification.getTitle());
    }

    @Test
    void notifiesHrStaffAboutContractDecision() {
        FakeNotificationRepository repository = new FakeNotificationRepository();
        NotificationService service = new NotificationService(repository);

        int created = service.notifyContractDecisionForHrStaff(List.of(121), 14, 802, "Nguyen Van C", "Rejected");

        assertEquals(1, created);
        Notification notification = repository.createdNotifications.get(0);
        assertEquals("Contract", notification.getType());
        assertEquals("Contract", notification.getEntityType());
        assertEquals(Integer.valueOf(802), notification.getEntityId());
        assertEquals("/hrstaff/contracts", notification.getTargetUrl());
        assertEquals("Hop dong da bi tu choi", notification.getTitle());
    }

    @Test
    void notifiesCandidateAboutApplicationStatusChange() {
        FakeNotificationRepository repository = new FakeNotificationRepository();
        NotificationService service = new NotificationService(repository);

        int created = service.notifyApplicationStatusChangedForCandidate(131, 15, 901, "Java Developer", "Rejected");

        assertEquals(1, created);
        Notification notification = repository.createdNotifications.get(0);
        assertEquals(131, notification.getUserId());
        assertEquals("Application", notification.getType());
        assertEquals("Application", notification.getEntityType());
        assertEquals(Integer.valueOf(901), notification.getEntityId());
        assertEquals("/guest/applications", notification.getTargetUrl());
        assertEquals("Ho so ung tuyen bi tu choi", notification.getTitle());
    }

    @Test
    void notifiesAdminsAboutUserStatusChange() {
        FakeNotificationRepository repository = new FakeNotificationRepository();
        NotificationService service = new NotificationService(repository);

        int created = service.notifyUserStatusChangedForAdmins(List.of(141), 16, 1001, "admin2", false);

        assertEquals(1, created);
        Notification notification = repository.createdNotifications.get(0);
        assertEquals("User", notification.getType());
        assertEquals("SystemUser", notification.getEntityType());
        assertEquals(Integer.valueOf(1001), notification.getEntityId());
        assertEquals("/admin/users", notification.getTargetUrl());
        assertEquals("User da bi khoa", notification.getTitle());
        assertEquals("High", notification.getPriority());
    }

    @Test
    void notifiesAdminsAboutRolePermissionChange() {
        FakeNotificationRepository repository = new FakeNotificationRepository();
        NotificationService service = new NotificationService(repository);

        int created = service.notifyRolePermissionChangedForAdmins(List.of(151), 17, 3, true, 2);

        assertEquals(1, created);
        Notification notification = repository.createdNotifications.get(0);
        assertEquals("Permission", notification.getType());
        assertEquals("Role", notification.getEntityType());
        assertEquals(Integer.valueOf(3), notification.getEntityId());
        assertEquals("/admin?action=role-permissions", notification.getTargetUrl());
        assertEquals("Quyền đã được cấp", notification.getTitle());
        assertEquals("Vai trò #3 vừa được cấp 2 quyền.", notification.getMessage());
    }

    private static class FakeNotificationRepository implements NotificationService.NotificationRepository {
        private final List<Integer> createdUserIds = new ArrayList<>();
        private final List<Notification> createdNotifications = new ArrayList<>();

        @Override
        public int create(Notification notification) {
            createdUserIds.add(notification.getUserId());
            createdNotifications.add(notification);
            return createdUserIds.size();
        }

        @Override
        public List<Notification> findByUserId(int userId, int limit) {
            return List.of();
        }

        @Override
        public int countUnreadByUserId(int userId) {
            return 0;
        }

        @Override
        public boolean markRead(int notificationId, int userId) {
            return true;
        }

        @Override
        public boolean markAllRead(int userId) {
            return true;
        }
    }
}
