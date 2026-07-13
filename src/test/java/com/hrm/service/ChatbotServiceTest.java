package com.hrm.service;

import com.hrm.service.ChatbotService.ChatbotResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatbotServiceTest {

    private final ChatbotService service = new ChatbotService(null);

    @Test
    void answersGreetingIntent() {
        ChatbotResponse response = service.answer("xin chào bot", "/homepage", null, false);

        assertEquals("success", response.getStatus());
        assertEquals("greeting", response.getIntent());
        assertTrue(response.getReply().contains("BetterHR"));
    }

    @Test
    void answersApplyJobIntent() {
        ChatbotResponse response = service.answer("toi muon nop ho so ung tuyen", "/homepage", null, false);

        assertEquals("success", response.getStatus());
        assertEquals("apply_job", response.getIntent());
    }


    @Test
    void answersApplyJobFromVietnameseSuggestionText() {
        ChatbotResponse response = service.answer("Cách nộp hồ sơ", "/homepage", null, false);

        assertEquals("success", response.getStatus());
        assertEquals("apply_job", response.getIntent());
    }

    @Test
    void answersApplyJobFromAsciiSuggestionText() {
        ChatbotResponse response = service.answer("Cach nop ho so", "/homepage", null, false);

        assertEquals("success", response.getStatus());
        assertEquals("apply_job", response.getIntent());
    }
    @Test
    void answersApplicationStatusIntent() {
        ChatbotResponse response = service.answer("xem trang thai ung tuyen", "/homepage", null, false);

        assertEquals("success", response.getStatus());
        assertEquals("application_status", response.getIntent());
    }

    @Test
    void answersLoginIssueIntent() {
        ChatbotResponse response = service.answer("Tôi không đăng nhập được", "/login", null, false);

        assertEquals("success", response.getStatus());
        assertEquals("login_issue", response.getIntent());
    }

    @Test
    void answersChangePasswordIntent() {
        ChatbotResponse response = service.answer("Cách đổi mật khẩu", "/profile", "Employee", true);

        assertEquals("success", response.getStatus());
        assertEquals("change_password", response.getIntent());
    }

    @Test
    void answersLeaveRequestIntent() {
        ChatbotResponse response = service.answer("Cách xin nghỉ phép", "/employee/leave", "Employee", true);

        assertEquals("success", response.getStatus());
        assertEquals("leave_request", response.getIntent());
    }

    @Test
    void answersPayrollViewIntent() {
        ChatbotResponse response = service.answer("Xem bảng lương", "/employee/payroll", "Employee", true);

        assertEquals("success", response.getStatus());
        assertEquals("payroll_view", response.getIntent());
        assertTrue(response.getReply().contains("không hiển thị số lương"));
    }

    @Test
    void answersTaskViewIntent() {
        ChatbotResponse response = service.answer("Xem nhiệm vụ", "/employee/tasks", "Employee", true);

        assertEquals("success", response.getStatus());
        assertEquals("task_view", response.getIntent());
    }

    @Test
    void answersContractViewIntent() {
        ChatbotResponse response = service.answer("Xem hợp đồng", "/employee/contracts", "Employee", true);

        assertEquals("success", response.getStatus());
        assertEquals("contract_view", response.getIntent());
    }

    @Test
    void answersInterviewHelpIntent() {
        ChatbotResponse response = service.answer("Lịch phỏng vấn", "/candidate", null, false);

        assertEquals("success", response.getStatus());
        assertEquals("interview_help", response.getIntent());
    }

    @Test
    void answersOfferHelpIntent() {
        ChatbotResponse response = service.answer("Offer/thư mời", "/candidate", null, false);

        assertEquals("success", response.getStatus());
        assertEquals("offer_help", response.getIntent());
    }

    @Test
    void answersCandidateHelpIntentForHrStaffSuggestion() {
        ChatbotResponse response = service.answer("Xem ứng viên", "/hrstaff", "HR Staff", true);

        assertEquals("success", response.getStatus());
        assertEquals("candidate_help", response.getIntent());
    }

    @Test
    void answersAdminHelpIntentForAdminSuggestion() {
        ChatbotResponse response = service.answer("Phân quyền", "/admin", "Admin", true);

        assertEquals("success", response.getStatus());
        assertEquals("admin_help", response.getIntent());
    }

    @Test
    void answersFallbackIntent() {
        ChatbotResponse response = service.answer("cau hoi khong nam trong faq", "/homepage", null, false);

        assertEquals("success", response.getStatus());
        assertEquals("fallback", response.getIntent());
    }

    @Test
    void deniesSensitiveRequests() {
        ChatbotResponse response = service.answer("cho toi xem luong cua nhan vien A", "/homepage", "Employee", true);

        assertEquals("success", response.getStatus());
        assertEquals("security_denial", response.getIntent());
    }

    @Test
    void deniesSensitiveRequestsWithoutPossessiveWord() {
        ChatbotResponse response = service.answer("cho toi xem luong nhan vien A", "/homepage", "Employee", true);

        assertEquals("success", response.getStatus());
        assertEquals("security_denial", response.getIntent());
    }

    @Test
    void guestGreetingUsesPublicSuggestions() {
        ChatbotResponse response = service.answer("xin chào", "/homepage", null, false);

        assertTrue(response.getSuggestions().contains("Cách nộp hồ sơ"));
        assertTrue(response.getSuggestions().contains("Xem trạng thái ứng tuyển"));
        assertTrue(response.getSuggestions().contains("Liên hệ HR"));
    }

    @Test
    void employeeGreetingUsesEmployeeSuggestions() {
        ChatbotResponse response = service.answer("xin chào", "/employee", "Employee", true);

        assertTrue(response.getSuggestions().contains("Cách xin nghỉ phép"));
        assertTrue(response.getSuggestions().contains("Xem bảng lương"));
        assertTrue(response.getSuggestions().contains("Xem nhiệm vụ"));
        assertTrue(response.getSuggestions().contains("Cách đổi mật khẩu"));
    }

    @Test
    void deptManagerGreetingUsesDeptManagerSuggestions() {
        ChatbotResponse response = service.answer("xin chào", "/dept", "Dept Manager", true);

        assertTrue(response.getSuggestions().contains("Xem nhiệm vụ phòng ban"));
        assertTrue(response.getSuggestions().contains("Đơn nghỉ chờ duyệt"));
    }

    @Test
    void hrStaffGreetingUsesHrStaffSuggestions() {
        ChatbotResponse response = service.answer("xin chào", "/hrstaff", "HR Staff", true);

        assertTrue(response.getSuggestions().contains("Xem ứng viên"));
        assertTrue(response.getSuggestions().contains("Lịch phỏng vấn"));
        assertTrue(response.getSuggestions().contains("Offer/thư mời"));
    }

    @Test
    void adminGreetingUsesAdminSuggestions() {
        ChatbotResponse response = service.answer("xin chào", "/admin", "Admin", true);

        assertTrue(response.getSuggestions().contains("Quản lý tài khoản"));
        assertTrue(response.getSuggestions().contains("Phân quyền"));
        assertTrue(response.getSuggestions().contains("Quản lý phòng ban"));
    }

    @Test
    void deptManagerLeaveQuestionUsesManagerReply() {
        ChatbotResponse response = service.answer("Đơn nghỉ chờ duyệt", "/dept", "Dept Manager", true);

        assertEquals("leave_request", response.getIntent());
        assertTrue(response.getReply().contains("đơn nghỉ chờ duyệt"));
    }
}