package com.hrm.service;

import com.hrm.dao.ChatbotRoleDataDAO;
import com.hrm.dao.ChatbotRoleDataRepository;
import com.hrm.dao.ChatbotRoleDataRepository.EmployeeSnapshot;
import com.hrm.dao.ChatbotRoleDataRepository.GuestSnapshot;
import com.hrm.dao.ChatbotRoleDataRepository.HrSnapshot;
import com.hrm.dao.ChatbotRoleDataRepository.ManagerSnapshot;
import com.hrm.model.entity.SystemUser;
import com.hrm.util.PermissionUtil;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatbotRoleDataService {

    private static final Logger LOGGER = Logger.getLogger(ChatbotRoleDataService.class.getName());
    private static final int ROLE_GUEST = 6;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ChatbotRoleDataRepository repository;
    private final PermissionChecker permissionChecker;

    public ChatbotRoleDataService() {
        this(new ChatbotRoleDataDAO(), PermissionUtil::hasPermission);
    }

    ChatbotRoleDataService(ChatbotRoleDataRepository repository, PermissionChecker permissionChecker) {
        this.repository = repository;
        this.permissionChecker = permissionChecker;
    }

    public Optional<String> answer(String intent, SystemUser user, String roleName) {
        if (intent == null || intent.isBlank() || user == null) {
            return Optional.empty();
        }

        try {
            int roleId = user.getRoleId();
            if (roleId == ROLE_GUEST || isGuestRole(roleName)) {
                return answerGuest(intent, repository.loadGuestSnapshot(user.getUserId()));
            }
            if (roleId == PermissionUtil.ROLE_EMPLOYEE) {
                return answerEmployee(intent, user);
            }
            if (roleId == PermissionUtil.ROLE_DEPT_MANAGER) {
                return answerManager(intent, user);
            }
            if (roleId == PermissionUtil.ROLE_HR_STAFF
                    || roleId == PermissionUtil.ROLE_HR_MANAGER) {
                return answerHr(intent, user);
            }
        } catch (SQLException | RuntimeException ex) {
            LOGGER.log(Level.WARNING,
                    "Cannot load permission-scoped chatbot data; using FAQ fallback.", ex);
        }
        return Optional.empty();
    }

    private Optional<String> answerGuest(String intent, GuestSnapshot snapshot) {
        return switch (intent) {
            case "application_status" -> Optional.of(applicationReply(snapshot));
            case "interview_help" -> Optional.of(interviewReply(snapshot));
            case "offer_help" -> Optional.of(offerReply(snapshot));
            default -> Optional.empty();
        };
    }

    private Optional<String> answerEmployee(String intent, SystemUser user) throws SQLException {
        Integer employeeId = user.getEmployeeId();
        if (employeeId == null || employeeId <= 0) {
            return isEmployeeIntent(intent)
                    ? Optional.of("Tài khoản của bạn chưa được liên kết với hồ sơ nhân viên. Vui lòng liên hệ HR để kiểm tra.")
                    : Optional.empty();
        }
        EmployeeSnapshot snapshot = repository.loadEmployeeSnapshot(employeeId);
        return switch (intent) {
            case "leave_request" -> Optional.of(employeeLeaveReply(snapshot));
            case "task_view" -> Optional.of(employeeTaskReply(snapshot));
            case "contract_view" -> Optional.of(employeeContractReply(snapshot));
            case "payroll_view" -> Optional.of(employeePayrollReply(snapshot));
            default -> Optional.empty();
        };
    }

    private Optional<String> answerManager(String intent, SystemUser user) throws SQLException {
        if (!permissionChecker.hasPermission(user, "VIEW_DEPARTMENTS")) {
            return managerIntent(intent)
                    ? Optional.of("Bạn chưa được cấp quyền xem dữ liệu phòng ban. Vui lòng liên hệ Admin hoặc HR.")
                    : Optional.empty();
        }
        Integer employeeId = user.getEmployeeId();
        if (employeeId == null || employeeId <= 0) {
            return managerIntent(intent)
                    ? Optional.of("Tài khoản quản lý chưa liên kết với hồ sơ nhân viên nên chưa xác định được phạm vi phòng ban.")
                    : Optional.empty();
        }
        ManagerSnapshot snapshot = repository.loadManagerSnapshot(employeeId);
        if (snapshot.departmentName() == null || snapshot.departmentName().isBlank()) {
            return managerIntent(intent)
                    ? Optional.of("Bạn chưa được cấu hình là quản lý của phòng ban nào trong hệ thống.")
                    : Optional.empty();
        }
        return switch (intent) {
            case "leave_request" -> Optional.of(managerLeaveReply(snapshot));
            case "task_view" -> Optional.of(managerTaskReply(snapshot));
            default -> Optional.empty();
        };
    }

    private Optional<String> answerHr(String intent, SystemUser user) throws SQLException {
        boolean allowed = switch (intent) {
            case "candidate_help", "interview_help", "offer_help" ->
                hasAnyPermission(user, "MANAGE_APPLICANTS", "VIEW_RECRUITMENT");
            case "leave_request" -> permissionChecker.hasPermission(user, "VIEW_LEAVES");
            case "payroll_view" -> permissionChecker.hasPermission(user, "VIEW_PAYROLLS");
            case "contract_view" -> permissionChecker.hasPermission(user, "VIEW_CONTRACTS");
            default -> false;
        };
        if (!isHrDataIntent(intent)) {
            return Optional.empty();
        }
        if (!allowed) {
            return Optional.of(permissionDenied());
        }

        HrSnapshot snapshot = repository.loadHrSnapshot();
        return switch (intent) {
            case "candidate_help" -> Optional.of(hrCandidateReply(snapshot));
            case "interview_help" -> Optional.of(hrInterviewReply(snapshot));
            case "offer_help" -> Optional.of(hrOfferReply(snapshot));
            case "leave_request" -> Optional.of("Hiện có " + snapshot.pendingLeaveCount()
                    + " đơn nghỉ đang chờ xử lý trong phạm vi HR.");
            case "payroll_view" -> Optional.of("Hiện có " + snapshot.pendingPayrollCount()
                    + " bảng lương đang chờ xử lý. Chatbot không hiển thị số tiền lương.");
            case "contract_view" -> Optional.of("Hiện có " + snapshot.pendingContractCount()
                    + " hợp đồng đang chờ phê duyệt hoặc chữ ký.");
            default -> Optional.empty();
        };
    }
    private String applicationReply(GuestSnapshot snapshot) {
        if (snapshot.applicationCount() == 0) {
            return "Bạn chưa có hồ sơ ứng tuyển nào gắn với tài khoản hiện tại.";
        }
        return "Bạn có " + snapshot.applicationCount()
                + " hồ sơ ứng tuyển. Hồ sơ gần nhất đang ở trạng thái "
                + statusLabel(snapshot.latestApplicationStatus())
                + ", bước " + statusLabel(snapshot.currentStep()) + ".";
    }

    private String interviewReply(GuestSnapshot snapshot) {
        if (snapshot.nextInterviewAt() == null) {
            return "Bạn hiện không có lịch phỏng vấn sắp tới gắn với tài khoản này.";
        }
        return "Lịch phỏng vấn tiếp theo của bạn vào "
                + DATE_TIME_FORMAT.format(snapshot.nextInterviewAt())
                + ", trạng thái " + statusLabel(snapshot.interviewStatus()) + ".";
    }

    private String offerReply(GuestSnapshot snapshot) {
        if (snapshot.offerStatus() == null) {
            return "Bạn chưa có offer gắn với hồ sơ ứng tuyển hiện tại.";
        }
        String startDate = snapshot.offerStartDate() != null
                ? ", ngày bắt đầu dự kiến " + DATE_FORMAT.format(snapshot.offerStartDate()) : "";
        return "Offer gần nhất của bạn đang ở trạng thái "
                + statusLabel(snapshot.offerStatus()) + startDate
                + ". Chatbot không hiển thị mức lương offer.";
    }

    private String employeeLeaveReply(EmployeeSnapshot snapshot) {
        if (snapshot.latestLeaveStatus() == null) {
            return "Bạn chưa có đơn nghỉ phép nào. Bạn có thể tạo đơn tại mục Nghỉ phép.";
        }
        return "Bạn có " + snapshot.pendingLeaveCount()
                + " đơn nghỉ đang chờ xử lý. Đơn gần nhất có trạng thái "
                + statusLabel(snapshot.latestLeaveStatus()) + ".";
    }

    private String employeeTaskReply(EmployeeSnapshot snapshot) {
        return "Nhiệm vụ của bạn: " + snapshot.waitingTaskCount() + " đang chờ, "
                + snapshot.inProgressTaskCount() + " đang thực hiện và "
                + snapshot.completedTaskCount() + " đã hoàn thành.";
    }

    private String employeeContractReply(EmployeeSnapshot snapshot) {
        if (snapshot.latestContractStatus() == null) {
            return "Tài khoản của bạn chưa có hợp đồng lao động trong hệ thống.";
        }
        String endDate = snapshot.contractEndDate() != null
                ? ", ngày kết thúc " + DATE_FORMAT.format(snapshot.contractEndDate()) : "";
        return "Hợp đồng gần nhất của bạn: " + safeValue(snapshot.contractType())
                + ", trạng thái " + statusLabel(snapshot.latestContractStatus())
                + endDate + ". Chatbot không hiển thị lương hoặc nội dung hợp đồng.";
    }

    private String employeePayrollReply(EmployeeSnapshot snapshot) {
        if (snapshot.latestPayrollPeriod() == null) {
            return "Bạn chưa có bảng lương trong hệ thống.";
        }
        return "Bảng lương gần nhất của bạn thuộc kỳ " + snapshot.latestPayrollPeriod()
                + ", trạng thái " + statusLabel(snapshot.latestPayrollStatus())
                + ". Vui lòng vào mục Bảng lương để xem chi tiết; chatbot không hiển thị số tiền.";
    }

    private String managerLeaveReply(ManagerSnapshot snapshot) {
        return "Phạm vi phòng ban " + snapshot.departmentName() + " hiện có "
                + snapshot.pendingLeaveCount() + " đơn nghỉ đang chờ duyệt.";
    }

    private String managerTaskReply(ManagerSnapshot snapshot) {
        return "Nhiệm vụ trong phạm vi " + snapshot.departmentName() + ": "
                + snapshot.waitingTaskCount() + " đang chờ, "
                + snapshot.inProgressTaskCount() + " đang thực hiện và "
                + snapshot.completedTaskCount() + " đã hoàn thành.";
    }

    private String hrCandidateReply(HrSnapshot snapshot) {
        return "Hệ thống có " + snapshot.applicationCount() + " hồ sơ ứng tuyển: "
                + snapshot.screeningApplicationCount() + " đang sàng lọc, "
                + snapshot.interviewApplicationCount() + " ở bước phỏng vấn và "
                + snapshot.offeredApplicationCount() + " đã sang bước offer.";
    }

    private String hrInterviewReply(HrSnapshot snapshot) {
        return "Hiện có " + snapshot.scheduledInterviewCount()
                + " lịch phỏng vấn sắp tới ở trạng thái Scheduled hoặc Rescheduled.";
    }

    private String hrOfferReply(HrSnapshot snapshot) {
        return "Offer hiện tại: " + snapshot.draftOfferCount() + " bản nháp và "
                + snapshot.sentOfferCount() + " đã gửi. Chatbot không hiển thị mức lương offer.";
    }

    private boolean hasAnyPermission(SystemUser user, String... permissionCodes) {
        for (String permissionCode : permissionCodes) {
            if (permissionChecker.hasPermission(user, permissionCode)) {
                return true;
            }
        }
        return false;
    }

    private boolean isGuestRole(String roleName) {
        if (roleName == null) {
            return false;
        }
        String normalized = roleName.toLowerCase(Locale.ROOT);
        return normalized.contains("guest")
                || normalized.contains("candidate")
                || normalized.contains("ứng viên");
    }

    private boolean isEmployeeIntent(String intent) {
        return "leave_request".equals(intent)
                || "task_view".equals(intent)
                || "contract_view".equals(intent)
                || "payroll_view".equals(intent);
    }

    private boolean managerIntent(String intent) {
        return "leave_request".equals(intent) || "task_view".equals(intent);
    }
    private boolean isHrDataIntent(String intent) {
        return "candidate_help".equals(intent)
                || "interview_help".equals(intent)
                || "offer_help".equals(intent)
                || "leave_request".equals(intent)
                || "payroll_view".equals(intent)
                || "contract_view".equals(intent);
    }

    private String permissionDenied() {
        return "Bạn chưa được cấp quyền xem dữ liệu này. Vui lòng mở đúng màn hình nghiệp vụ hoặc liên hệ Admin.";
    }

    private String safeValue(String value) {
        return value == null || value.isBlank() ? "chưa cập nhật" : value;
    }

    private String statusLabel(String value) {
        if (value == null || value.isBlank()) {
            return "chưa cập nhật";
        }
        return switch (value) {
            case "Applied" -> "Đã nộp";
            case "Screening" -> "Đang sàng lọc";
            case "Interview" -> "Phỏng vấn";
            case "Offer", "Offered" -> "Offer";
            case "Hired" -> "Đã tuyển";
            case "Rejected" -> "Từ chối";
            case "Withdrawn" -> "Đã rút";
            case "Scheduled" -> "Đã lên lịch";
            case "Rescheduled" -> "Đã đổi lịch";
            case "Completed" -> "Hoàn thành";
            case "Cancelled" -> "Đã hủy";
            case "Pending", "Pending_Approval", "Pending_Signature" -> "Đang chờ xử lý";
            case "Approved", "Active", "Accepted", "Paid" -> "Đã duyệt";
            case "Draft" -> "Bản nháp";
            case "Sent" -> "Đã gửi";
            case "Expired" -> "Hết hạn";
            case "In Progress" -> "Đang thực hiện";
            case "Waiting" -> "Đang chờ";
            default -> value;
        };
    }

    @FunctionalInterface
    interface PermissionChecker {
        boolean hasPermission(SystemUser user, String permissionCode);
    }
}
