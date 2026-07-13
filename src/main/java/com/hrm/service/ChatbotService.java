package com.hrm.service;

import com.hrm.dao.ChatbotFaqDAO;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class ChatbotService {

    private final ChatbotFaqDAO faqDAO;

    private static final Pattern DIACRITIC_PATTERN = Pattern.compile("\\p{M}+");

    private static final List<String> PUBLIC_SUGGESTIONS = List.of(
            "Cách nộp hồ sơ",
            "Xem trạng thái ứng tuyển",
            "Liên hệ HR"
    );
    private static final List<String> EMPLOYEE_SUGGESTIONS = List.of(
            "Cách xin nghỉ phép",
            "Xem bảng lương",
            "Xem nhiệm vụ",
            "Cách đổi mật khẩu"
    );
    private static final List<String> DEPT_MANAGER_SUGGESTIONS = List.of(
            "Xem nhiệm vụ phòng ban",
            "Đơn nghỉ chờ duyệt",
            "Liên hệ HR"
    );
    private static final List<String> HR_STAFF_SUGGESTIONS = List.of(
            "Xem ứng viên",
            "Lịch phỏng vấn",
            "Offer/thư mời"
    );
    private static final List<String> ADMIN_SUGGESTIONS = List.of(
            "Quản lý tài khoản",
            "Phân quyền",
            "Quản lý phòng ban"
    );
    public ChatbotService() {
        this(new ChatbotFaqDAO());
    }

    ChatbotService(ChatbotFaqDAO faqDAO) {
        this.faqDAO = faqDAO;
    }
    public ChatbotResponse answer(String message, String page, String roleName, boolean authenticated) {
        String normalized = normalize(message);
        List<String> roleSuggestions = suggestionsForRole(roleName, authenticated);

        if (normalized.isBlank()) {
            return error("validation_error", "Vui lòng nhập câu hỏi để tôi hỗ trợ.", roleSuggestions);
        }

        if (containsAny(normalized, "mat khau admin", "password admin", "api key", "secret", "token",
                "database password", "connection string")) {
            return success(
                    "security_denial",
                    "Tôi không thể cung cấp mật khẩu, token hoặc thông tin cấu hình nhạy cảm. Vui lòng liên hệ quản trị viên nếu bạn cần hỗ trợ.",
                    List.of("Cách đổi mật khẩu", "Liên hệ HR", "Tôi không đăng nhập được")
            );
        }

        if (containsAny(normalized, "luong cua nhan vien", "luong nhan vien", "xem luong nhan vien",
                "bang luong cua nhan vien", "bang luong nhan vien", "payroll cua",
                "luong nguoi khac", "luong dong nghiep", "thong tin nhan vien khac",
                "nhan vien phong khac")) {
            return success(
                    "security_denial",
                    "Tôi không thể cung cấp thông tin ngoài quyền truy cập của bạn. Vui lòng vào đúng màn hình nghiệp vụ hoặc liên hệ HR để được hỗ trợ.",
                    List.of("Xem bảng lương của tôi", "Liên hệ HR", "Xem nhiệm vụ của tôi")
            );
        }

        if (isGreeting(normalized)) {
            return greeting(roleName, authenticated);
        }

        if (containsAny(normalized, "khong dang nhap duoc", "dang nhap khong duoc", "loi dang nhap",
                "khong login duoc", "login fail", "tai khoan bi khoa", "khong vao duoc tai khoan")) {
            return faqOrSuccess(
                    "login_issue",
                    roleName,
                    "Nếu bạn không đăng nhập được, hãy kiểm tra lại email/tên đăng nhập và mật khẩu. Nếu tài khoản bị khóa hoặc quên mật khẩu, dùng chức năng quên mật khẩu hoặc liên hệ HR/Admin để được mở khóa.",
                    List.of("Cách đổi mật khẩu", "Liên hệ HR", "Quản lý tài khoản")
            );
        }

        if (containsAny(normalized, "lich phong van", "phong van", "interview", "lich hen")) {
            return faqOrSuccess(
                    "interview_help",
                    roleName,
                    "Bạn có thể theo dõi lịch phỏng vấn trong khu vực ứng viên hoặc màn hình tuyển dụng phù hợp với vai trò hiện tại. Chatbot MVP chỉ hướng dẫn thao tác, chưa đọc lịch phỏng vấn trực tiếp từ database.",
                    List.of("Xem trạng thái ứng tuyển", "Offer/thư mời", "Liên hệ HR")
            );
        }

        if (containsAny(normalized, "offer", "thu moi", "thu de nghi", "de nghi nhan viec")) {
            return faqOrSuccess(
                    "offer_help",
                    roleName,
                    "Thông tin offer/thư mời cần được xem trong cổng ứng viên hoặc màn hình HR Staff phụ trách tuyển dụng. Chatbot MVP không hiển thị chi tiết offer trong khung chat để tránh lộ dữ liệu.",
                    List.of("Lịch phỏng vấn", "Xem trạng thái ứng tuyển", "Liên hệ HR")
            );
        }

        if (containsAny(normalized, "trang thai ung tuyen", "ho so cua toi", "application status",
                "ket qua ung tuyen", "tinh trang ho so")) {
            return faqOrSuccess(
                    "application_status",
                    roleName,
                    "Bạn hãy đăng nhập cổng ứng viên để xem trạng thái ứng tuyển của chính mình. Vì lý do bảo mật, chatbot MVP chưa hiển thị chi tiết hồ sơ trực tiếp trong khung chat.",
                    List.of("Cách nộp hồ sơ", "Lịch phỏng vấn", "Liên hệ HR")
            );
        }

        if (containsAny(normalized, "cach nop ho so", "nop ho so", "ung tuyen", "viec lam", "tuyen dung", "apply")) {
            return faqOrSuccess(
                    "apply_job",
                    roleName,
                    "Bạn có thể vào mục Việc làm trên trang chủ để xem vị trí đang tuyển và nộp hồ sơ. Nếu đã có tài khoản ứng viên, hãy đăng nhập để theo dõi hồ sơ của mình.",
                    List.of("Xem trạng thái ứng tuyển", "Lịch phỏng vấn", "Liên hệ HR")
            );
        }

        if (containsAny(normalized, "ung vien", "candidate", "ho so ung vien", "danh sach ung vien")) {
            return faqOrSuccess(
                    "candidate_help",
                    roleName,
                    "HR Staff có thể vào khu vực tuyển dụng để xem danh sách ứng viên, hồ sơ, lịch phỏng vấn và các bước xử lý tiếp theo. Chatbot MVP chưa lọc hoặc hiển thị dữ liệu ứng viên trực tiếp.",
                    List.of("Lịch phỏng vấn", "Offer/thư mời", "Liên hệ HR")
            );
        }

        if (containsAny(normalized, "lien he hr", "phong nhan su", "email hr", "hotline", "ho tro hr")) {
            return faqOrSuccess(
                    "contact_hr",
                    roleName,
                    "Bạn có thể liên hệ HR qua mục Liên hệ trên trang chủ hoặc gửi yêu cầu cho bộ phận nhân sự trong hệ thống.",
                    List.of("Cách đổi mật khẩu", "Cách nộp hồ sơ", "Cách xin nghỉ phép")
            );
        }

        if (containsAny(normalized, "doi mat khau", "quen mat khau", "password", "change password")) {
            return faqOrSuccess(
                    "change_password",
                    roleName,
                    "Nếu bạn đang đăng nhập, hãy vào trang hồ sơ hoặc mục đổi mật khẩu để cập nhật mật khẩu. Nếu quên mật khẩu, dùng chức năng quên mật khẩu ở màn hình đăng nhập.",
                    List.of("Tôi không đăng nhập được", "Liên hệ HR", "Xem hồ sơ cá nhân")
            );
        }

        if (containsAny(normalized, "nghi phep", "xin nghi", "don nghi", "leave", "ngay nghi")) {
            return faqOrSuccess(
                    "leave_request",
                    roleName,
                    leaveReplyForRole(roleName),
                    List.of("Xem nhiệm vụ", "Xem bảng lương", "Liên hệ HR")
            );
        }

        if (containsAny(normalized, "bang luong", "phieu luong", "payroll", "xem luong", "luong")) {
            return faqOrSuccess(
                    "payroll_view",
                    roleName,
                    "Bạn có thể vào cổng nhân viên và chọn mục Bảng lương để xem thông tin của mình. Vì lý do bảo mật, chatbot không hiển thị số lương trực tiếp trong khung chat.",
                    List.of("Cách xin nghỉ phép", "Liên hệ HR", "Xem hợp đồng")
            );
        }

        if (containsAny(normalized, "task", "nhiem vu", "cong viec duoc giao", "viec cua toi",
                "nhiem vu phong ban", "cong viec phong ban")) {
            return faqOrSuccess(
                    "task_view",
                    roleName,
                    taskReplyForRole(roleName),
                    List.of("Cách xin nghỉ phép", "Xem bảng lương", "Liên hệ HR")
            );
        }

        if (containsAny(normalized, "hop dong", "contract", "hdld", "hop dong lao dong")) {
            return faqOrSuccess(
                    "contract_view",
                    roleName,
                    "Bạn có thể xem hợp đồng trong khu vực hồ sơ nhân viên hoặc màn hình hợp đồng tương ứng với vai trò của mình. Chatbot MVP chỉ hướng dẫn nơi xem, không hiển thị chi tiết hợp đồng trong khung chat.",
                    List.of("Xem bảng lương", "Liên hệ HR", "Cách đổi mật khẩu")
            );
        }

        if (containsAny(normalized, "quan ly tai khoan", "phan quyen", "role", "permission",
                "quan ly phong ban", "department", "phong ban")) {
            return faqOrSuccess(
                    "admin_help",
                    roleName,
                    "Admin có thể quản lý tài khoản, phân quyền và phòng ban trong khu vực quản trị. Chatbot MVP chỉ hướng dẫn thao tác, không thay đổi dữ liệu hệ thống trực tiếp.",
                    ADMIN_SUGGESTIONS
            );
        }

        return success(
                "fallback",
                "Tôi chưa có thông tin phù hợp cho câu hỏi này. Bạn có thể chọn một nội dung gợi ý hoặc liên hệ HR để được hỗ trợ.",
                roleSuggestions
        );
    }

    public ChatbotResponse validationError(String message) {
        return error("validation_error", message, PUBLIC_SUGGESTIONS);
    }

    private ChatbotResponse greeting(String roleName, boolean authenticated) {
        String audience = authenticated ? "tài khoản " + safeRoleName(roleName) : "khách/ứng viên";
        return success(
                "greeting",
                "Xin chào! Tôi là trợ lý BetterHR cho " + audience + ". Tôi có thể hướng dẫn về ứng tuyển, nghỉ phép, bảng lương, nhiệm vụ, hợp đồng, phỏng vấn, offer, đổi mật khẩu và liên hệ HR.",
                suggestionsForRole(roleName, authenticated)
        );
    }

    private String leaveReplyForRole(String roleName) {
        if (isDeptManagerRole(roleName)) {
            return "Dept Manager có thể vào khu vực quản lý phòng ban để xem các đơn nghỉ chờ duyệt và xử lý theo quyền được cấp. Chatbot MVP chưa hiển thị danh sách đơn nghỉ trực tiếp.";
        }
        return "Nhân viên có thể vào cổng nhân viên và chọn mục Nghỉ phép để tạo hoặc theo dõi đơn nghỉ. Chatbot MVP chỉ hướng dẫn thao tác, chưa hiển thị dữ liệu đơn nghỉ riêng tư.";
    }

    private String taskReplyForRole(String roleName) {
        if (isDeptManagerRole(roleName)) {
            return "Dept Manager có thể vào khu vực quản lý phòng ban để xem nhiệm vụ phòng ban và nhiệm vụ của nhân viên thuộc phạm vi quản lý.";
        }
        return "Bạn có thể vào cổng nhân viên hoặc khu vực quản lý phù hợp với vai trò hiện tại để xem nhiệm vụ được giao.";
    }

    private List<String> suggestionsForRole(String roleName, boolean authenticated) {
        if (!authenticated) {
            return PUBLIC_SUGGESTIONS;
        }

        String role = normalize(roleName);
        if (containsAny(role, "admin", "administrator", "quan tri")) {
            return ADMIN_SUGGESTIONS;
        }
        if (containsAny(role, "hr staff", "hrstaff", "hr manager", "hrmanager", "nhan su")) {
            return HR_STAFF_SUGGESTIONS;
        }
        if (containsAny(role, "dept manager", "department manager", "truong phong", "quan ly phong ban")) {
            return DEPT_MANAGER_SUGGESTIONS;
        }
        if (containsAny(role, "employee", "nhan vien")) {
            return EMPLOYEE_SUGGESTIONS;
        }
        if (containsAny(role, "guest", "public", "candidate", "ung vien")) {
            return PUBLIC_SUGGESTIONS;
        }
        return PUBLIC_SUGGESTIONS;
    }

    private boolean isDeptManagerRole(String roleName) {
        String role = normalize(roleName);
        return containsAny(role, "dept manager", "department manager", "truong phong", "quan ly phong ban");
    }

    private boolean isGreeting(String value) {
        return containsAny(value, "xin chao", "hello", "bot oi", "tro giup")
                || "chao".equals(value)
                || "hi".equals(value);
    }
    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        String trimmed = value.trim().toLowerCase(Locale.ROOT);
        String decomposed = Normalizer.normalize(trimmed, Normalizer.Form.NFD);
        return DIACRITIC_PATTERN.matcher(decomposed)
                .replaceAll("")
                .replace('đ', 'd')
                .replaceAll("\\s+", " ");
    }

    private boolean containsAny(String value, String... keywords) {
        for (String keyword : keywords) {
            if (value.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String safeRoleName(String roleName) {
        return roleName == null || roleName.isBlank() ? "người dùng" : roleName.trim();
    }
    private ChatbotResponse faqOrSuccess(String intent, String roleName, String reply, List<String> suggestions) {
        if (faqDAO != null) {
            return faqDAO.findActiveAnswer(intent, roleName)
                    .map(answer -> success(
                            intent,
                            answer.getAnswer(),
                            answer.getSuggestions().isEmpty() ? suggestions : answer.getSuggestions()
                    ))
                    .orElseGet(() -> success(intent, reply, suggestions));
        }
        return success(intent, reply, suggestions);
    }
    private ChatbotResponse success(String intent, String reply, List<String> suggestions) {
        return new ChatbotResponse("success", intent, reply, suggestions);
    }

    private ChatbotResponse error(String intent, String reply, List<String> suggestions) {
        return new ChatbotResponse("error", intent, reply, suggestions);
    }

    public static class ChatbotResponse {
        private final String status;
        private final String intent;
        private final String reply;
        private final List<String> suggestions;
        private final Long conversationId;
        private final Long messageId;

        public ChatbotResponse(String status, String intent, String reply, List<String> suggestions) {
            this(status, intent, reply, suggestions, null, null);
        }

        public ChatbotResponse(String status,
                               String intent,
                               String reply,
                               List<String> suggestions,
                               Long conversationId,
                               Long messageId) {
            this.status = status;
            this.intent = intent;
            this.reply = reply;
            this.suggestions = suggestions;
            this.conversationId = conversationId;
            this.messageId = messageId;
        }

        public String getStatus() {
            return status;
        }

        public String getIntent() {
            return intent;
        }

        public String getReply() {
            return reply;
        }

        public List<String> getSuggestions() {
            return suggestions;
        }

        public Long getConversationId() {
            return conversationId;
        }

        public Long getMessageId() {
            return messageId;
        }

        public ChatbotResponse withTracking(long trackedConversationId, long trackedMessageId) {
            return new ChatbotResponse(
                    status, intent, reply, suggestions, trackedConversationId, trackedMessageId);
        }
        public ChatbotResponse withReply(String roleAwareReply) {
            return new ChatbotResponse(
                    status, intent, roleAwareReply, suggestions, conversationId, messageId);
        }
        public ChatbotResponse withIntentAndReply(String responseIntent, String responseReply) {
            return new ChatbotResponse(
                    status, responseIntent, responseReply, suggestions, conversationId, messageId);
        }
    }
}