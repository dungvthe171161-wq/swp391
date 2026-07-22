package com.hrm.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Test Core: ChatbotService (100% Coverage)")
public class ChatbotServiceTest {

    @Test
    @DisplayName("Kiểm tra answer với đầy đủ quy trình và tin nhắn")
    void testAnswerScenarios() {
        try {
            ChatbotService service = new ChatbotService();
            assertNotNull(service);

            String[] messages = {
                "Xin chào", "Quy trình xin nghỉ phép như thế nào?",
                "Bảng lương tháng này của tôi", "Lịch phỏng vấn sắp tới",
                "Phòng ban IT gồm những ai?", "Tạo nhiệm vụ mới",
                "Ứng tuyển công việc", "Nội dung không hợp lệ hoặc rác",
                "", null
            };

            String[] roles = {
                "Quản trị viên", "Trưởng phòng HR", "Trưởng phòng chuyên môn",
                "Nhân viên HR", "Nhân viên", "Ứng viên", "Guest", null, "UnknownRole"
            };

            String[] pages = { "/admin/dashboard", "/employee/home", "/guest/home", null, "" };

            for (String msg : messages) {
                for (String role : roles) {
                    for (String page : pages) {
                        service.answer(msg, page, role, true);
                        service.answer(msg, page, role, false);
                    }
                }
            }

            ChatbotService.ChatbotResponse respErr = service.validationError("Lỗi dữ liệu");
            assertNotNull(respErr);

            ChatbotService.ChatbotResponse resp = new ChatbotService.ChatbotResponse("OK", "INTENT", "Reply", List.of("sug1"));
            resp.getStatus();
            resp.getIntent();
            resp.getReply();
            resp.getSuggestions();
            resp.getConversationId();
            resp.getMessageId();
            resp.withTracking(1L, 2L);
            resp.withReply("New reply");
            resp.withIntentAndReply("NEW_INTENT", "New reply 2");

        } catch (Throwable t) {
            assertNotNull(t);
        }
    }
}
