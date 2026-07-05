package com.hrm.service;

import com.hrm.dao.NotificationDAO;
import com.hrm.model.entity.Notification;
import java.util.List;

public class NotificationService {

    private final NotificationRepository repository;

    public NotificationService() {
        this(new NotificationDaoRepository(new NotificationDAO()));
    }

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public Notification buildNotification(int userId,
                                          Integer actorUserId,
                                          String entityType,
                                          Integer entityId,
                                          String type,
                                          String title,
                                          String message,
                                          String targetUrl,
                                          String priority) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setActorUserId(actorUserId);
        notification.setEntityType(blankToNull(entityType));
        notification.setEntityId(entityId);
        notification.setType(defaultText(type, "System"));
        notification.setTitle(defaultText(title, "Thong bao"));
        notification.setMessage(defaultText(message, ""));
        notification.setTargetUrl(normalizeTargetUrl(targetUrl));
        notification.setPriority(normalizePriority(priority));
        notification.setRead(false);
        return notification;
    }

    public int notifyUser(Notification notification) {
        if (notification == null || notification.getUserId() <= 0) {
            return 0;
        }
        return repository.create(notification);
    }

    public int notifyUsers(List<Integer> userIds, Notification template) {
        if (userIds == null || userIds.isEmpty() || template == null) {
            return 0;
        }
        int created = 0;
        for (Integer userId : userIds) {
            if (userId == null || userId <= 0) {
                continue;
            }
            Notification notification = copyForUser(template, userId);
            if (repository.create(notification) > 0) {
                created++;
            }
        }
        return created;
    }

    public int notifyNewApplicationForHrStaff(List<Integer> hrStaffUserIds,
                                              int actorUserId,
                                              int applicationId,
                                              String candidateName,
                                              String jobTitle) {
        Notification template = buildNotification(
                0,
                actorUserId > 0 ? actorUserId : null,
                "Application",
                applicationId,
                "Application",
                "Co ho so ung tuyen moi",
                defaultText(candidateName, "Ung vien") + " vua nop ho so cho vi tri "
                        + defaultText(jobTitle, "dang tuyen") + ".",
                "/candidates?applicationId=" + applicationId,
                "Normal"
        );
        template.setApplicationId(applicationId > 0 ? applicationId : null);
        return notifyUsers(hrStaffUserIds, template);
    }

    public int notifyNewLeaveRequestForDeptManagers(List<Integer> managerUserIds,
                                                    int actorUserId,
                                                    int requestId,
                                                    String employeeName,
                                                    String startDate,
                                                    String endDate) {
        Notification template = buildNotification(
                0,
                actorUserId > 0 ? actorUserId : null,
                "Leave",
                requestId,
                "Leave",
                "Co don nghi phep moi",
                defaultText(employeeName, "Nhan vien") + " da gui don nghi phep tu "
                        + defaultText(startDate, "ngay bat dau") + " den "
                        + defaultText(endDate, "ngay ket thuc") + ".",
                "/dept/leaves?status=Pending",
                "Normal"
        );
        return notifyUsers(managerUserIds, template);
    }

    public int notifyLeaveDecisionForEmployee(int employeeUserId,
                                              int actorUserId,
                                              int requestId,
                                              String decision) {
        String normalizedDecision = "Rejected".equals(decision) ? "Rejected" : "Approved";
        String title = "Rejected".equals(normalizedDecision)
                ? "Don nghi phep da bi tu choi"
                : "Don nghi phep da duoc duyet";
        String message = "Rejected".equals(normalizedDecision)
                ? "Don nghi phep cua ban da bi tu choi."
                : "Don nghi phep cua ban da duoc duyet.";
        Notification notification = buildNotification(
                employeeUserId,
                actorUserId > 0 ? actorUserId : null,
                "Leave",
                requestId,
                "Leave",
                title,
                message,
                "/employee/leaves",
                "Normal"
        );
        return notifyUser(notification);
    }

    public int notifyTaskAssignedToEmployees(List<Integer> employeeUserIds,
                                             int actorUserId,
                                             int taskId,
                                             String taskTitle) {
        Notification template = buildNotification(
                0,
                actorUserId > 0 ? actorUserId : null,
                "Task",
                taskId,
                "Task",
                "Ban co cong viec moi",
                "Cong viec moi: " + defaultText(taskTitle, "Cong viec") + ".",
                "/employee/tasks",
                "Normal"
        );
        return notifyUsers(employeeUserIds, template);
    }

    public int notifyTaskStatusUpdatedForManagers(List<Integer> managerUserIds,
                                                  int actorUserId,
                                                  int taskId,
                                                  String taskTitle,
                                                  String status,
                                                  String employeeName) {
        Notification template = buildNotification(
                0,
                actorUserId > 0 ? actorUserId : null,
                "Task",
                taskId,
                "Task",
                "Cong viec duoc cap nhat",
                defaultText(employeeName, "Nhan vien") + " da cap nhat "
                        + defaultText(taskTitle, "cong viec") + " sang "
                        + defaultText(status, "trang thai moi") + ".",
                "/taskManager",
                "Normal"
        );
        return notifyUsers(managerUserIds, template);
    }

    public int notifyPayrollPendingForHrManagers(List<Integer> hrManagerUserIds,
                                                 int actorUserId,
                                                 int payrollId,
                                                 String payPeriod) {
        Notification template = buildNotification(
                0,
                actorUserId > 0 ? actorUserId : null,
                "Payroll",
                payrollId,
                "Payroll",
                "Payroll cho duyet",
                "Payroll ky " + defaultText(payPeriod, "moi") + " dang cho phe duyet.",
                "/hr/payroll-approval",
                "Normal"
        );
        return notifyUsers(hrManagerUserIds, template);
    }

    public int notifyPayrollDecisionForHrStaff(List<Integer> hrStaffUserIds,
                                               int actorUserId,
                                               int payrollId,
                                               String decision) {
        boolean rejected = "Rejected".equals(decision);
        Notification template = buildNotification(
                0,
                actorUserId > 0 ? actorUserId : null,
                "Payroll",
                payrollId,
                "Payroll",
                rejected ? "Payroll da bi tu choi" : "Payroll da duoc duyet",
                rejected ? "Mot payroll da bi HR Manager tu choi." : "Mot payroll da duoc HR Manager duyet.",
                "/hrstaff/payroll",
                "Normal"
        );
        return notifyUsers(hrStaffUserIds, template);
    }

    public int notifyRecruitmentPendingForHrManagers(List<Integer> hrManagerUserIds,
                                                     int actorUserId,
                                                     int recruitmentId,
                                                     String jobTitle) {
        Notification template = buildNotification(
                0,
                actorUserId > 0 ? actorUserId : null,
                "Recruitment",
                recruitmentId,
                "Recruitment",
                "Tin tuyen dung cho duyet",
                "Tin tuyen dung " + defaultText(jobTitle, "moi") + " dang cho phe duyet.",
                "/viewRecruitment",
                "Normal"
        );
        return notifyUsers(hrManagerUserIds, template);
    }

    public int notifyRecruitmentDecisionForHrStaff(List<Integer> hrStaffUserIds,
                                                   int actorUserId,
                                                   int recruitmentId,
                                                   String jobTitle,
                                                   String decision) {
        boolean rejected = "Rejected".equals(decision);
        Notification template = buildNotification(
                0,
                actorUserId > 0 ? actorUserId : null,
                "Recruitment",
                recruitmentId,
                "Recruitment",
                rejected ? "Tin tuyen dung da bi tu choi" : "Tin tuyen dung da duoc duyet",
                "Tin tuyen dung " + defaultText(jobTitle, "moi") + (rejected ? " da bi tu choi." : " da duoc duyet."),
                "/postRecruitments",
                "Normal"
        );
        return notifyUsers(hrStaffUserIds, template);
    }

    public int notifyContractPendingForHrManagers(List<Integer> hrManagerUserIds,
                                                  int actorUserId,
                                                  int contractId,
                                                  String employeeName) {
        Notification template = buildNotification(
                0,
                actorUserId > 0 ? actorUserId : null,
                "Contract",
                contractId,
                "Contract",
                "Hop dong cho duyet",
                "Hop dong cua " + defaultText(employeeName, "nhan vien") + " dang cho phe duyet.",
                "/hr/approve-reject-contracts",
                "Normal"
        );
        return notifyUsers(hrManagerUserIds, template);
    }

    public int notifyContractDecisionForHrStaff(List<Integer> hrStaffUserIds,
                                                int actorUserId,
                                                int contractId,
                                                String employeeName,
                                                String decision) {
        boolean rejected = "Rejected".equals(decision);
        Notification template = buildNotification(
                0,
                actorUserId > 0 ? actorUserId : null,
                "Contract",
                contractId,
                "Contract",
                rejected ? "Hop dong da bi tu choi" : "Hop dong da duoc duyet",
                "Hop dong cua " + defaultText(employeeName, "nhan vien") + (rejected ? " da bi tu choi." : " da duoc duyet."),
                "/hrstaff/contracts",
                "Normal"
        );
        return notifyUsers(hrStaffUserIds, template);
    }

    public int notifyApplicationStatusChangedForCandidate(int candidateUserId,
                                                          int actorUserId,
                                                          int guestId,
                                                          String jobTitle,
                                                          String status) {
        boolean rejected = "Rejected".equals(status);
        Notification notification = buildNotification(
                candidateUserId,
                actorUserId > 0 ? actorUserId : null,
                "Application",
                guestId,
                "Application",
                rejected ? "Ho so ung tuyen bi tu choi" : "Ho so ung tuyen duoc cap nhat",
                rejected
                        ? "Ho so cua ban cho vi tri " + defaultText(jobTitle, "dang tuyen") + " da bi tu choi."
                        : "Ho so cua ban cho vi tri " + defaultText(jobTitle, "dang tuyen") + " da qua vong sang loc.",
                "/guest/applications",
                "Normal"
        );
        return notifyUser(notification);
    }

    public int notifyUserStatusChangedForAdmins(List<Integer> adminUserIds,
                                                int actorUserId,
                                                int userId,
                                                String username,
                                                boolean active) {
        Notification template = buildNotification(
                0,
                actorUserId > 0 ? actorUserId : null,
                "SystemUser",
                userId,
                "User",
                active ? "User da duoc mo khoa" : "User da bi khoa",
                "Tai khoan " + defaultText(username, "user") + (active ? " da duoc mo khoa." : " da bi khoa."),
                "/admin/users",
                "High"
        );
        return notifyUsers(adminUserIds, template);
    }

    public int notifyUserRoleChangedForAdmins(List<Integer> adminUserIds,
                                              int actorUserId,
                                              int userId,
                                              String username) {
        Notification template = buildNotification(
                0,
                actorUserId > 0 ? actorUserId : null,
                "SystemUser",
                userId,
                "User",
                "Role user da thay doi",
                "Tai khoan " + defaultText(username, "user") + " vua duoc cap nhat role.",
                "/admin/users",
                "High"
        );
        return notifyUsers(adminUserIds, template);
    }

    public int notifyRolePermissionChangedForAdmins(List<Integer> adminUserIds,
                                                    int actorUserId,
                                                    int roleId,
                                                    boolean granted,
                                                    int permissionCount) {
        Notification template = buildNotification(
                0,
                actorUserId > 0 ? actorUserId : null,
                "Role",
                roleId,
                "Permission",
                granted ? "Permission da duoc cap" : "Permission da duoc thu hoi",
                "Role #" + roleId + (granted ? " vua duoc cap " : " vua bi thu hoi ")
                        + Math.max(1, permissionCount) + " permission.",
                "/admin?action=role-permissions",
                "High"
        );
        return notifyUsers(adminUserIds, template);
    }

    public List<Notification> recentForUser(int userId, int limit) {
        if (userId <= 0) {
            return List.of();
        }
        return repository.findByUserId(userId, Math.max(1, limit));
    }

    public int unreadCount(int userId) {
        return userId <= 0 ? 0 : repository.countUnreadByUserId(userId);
    }

    public boolean markRead(int notificationId, int userId) {
        return notificationId > 0 && userId > 0 && repository.markRead(notificationId, userId);
    }

    public boolean markAllRead(int userId) {
        return userId > 0 && repository.markAllRead(userId);
    }

    private Notification copyForUser(Notification template, int userId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setActorUserId(template.getActorUserId());
        notification.setApplicationId(template.getApplicationId());
        notification.setEntityType(template.getEntityType());
        notification.setEntityId(template.getEntityId());
        notification.setTitle(template.getTitle());
        notification.setMessage(template.getMessage());
        notification.setType(template.getType());
        notification.setTargetUrl(template.getTargetUrl());
        notification.setPriority(template.getPriority());
        notification.setRead(template.isRead());
        notification.setExpiresAt(template.getExpiresAt());
        return notification;
    }

    private String normalizeTargetUrl(String targetUrl) {
        String value = blankToNull(targetUrl);
        if (value == null || !value.startsWith("/") || value.startsWith("//")) {
            return null;
        }
        return value;
    }

    private String normalizePriority(String priority) {
        String value = blankToNull(priority);
        if ("Low".equals(value) || "High".equals(value)) {
            return value;
        }
        return "Normal";
    }

    private String defaultText(String value, String fallback) {
        String clean = blankToNull(value);
        return clean == null ? fallback : clean;
    }

    private String blankToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public interface NotificationRepository {
        int create(Notification notification);

        List<Notification> findByUserId(int userId, int limit);

        int countUnreadByUserId(int userId);

        boolean markRead(int notificationId, int userId);

        boolean markAllRead(int userId);
    }

    private static class NotificationDaoRepository implements NotificationRepository {
        private final NotificationDAO dao;

        NotificationDaoRepository(NotificationDAO dao) {
            this.dao = dao;
        }

        @Override
        public int create(Notification notification) {
            return dao.create(notification);
        }

        @Override
        public List<Notification> findByUserId(int userId, int limit) {
            return dao.findByUserId(userId, limit);
        }

        @Override
        public int countUnreadByUserId(int userId) {
            return dao.countUnreadByUserId(userId);
        }

        @Override
        public boolean markRead(int notificationId, int userId) {
            return dao.markRead(notificationId, userId);
        }

        @Override
        public boolean markAllRead(int userId) {
            return dao.markAllRead(userId);
        }
    }
}
