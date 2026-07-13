package com.hrm.service;

import com.hrm.service.ChatbotService.ChatbotResponse;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ChatbotContentSafetyTest {

    @Test
    void keepsNormalFaqQuestionForHistory() {
        assertEquals(
                "Cách đổi mật khẩu",
                ChatbotContentSafety.sanitizeForHistory("  Cách đổi mật khẩu  "));
    }

    @Test
    void keepsConceptualApiKeyQuestionForHistory() {
        assertFalse(ChatbotContentSafety.containsSensitiveValue("API key là gì?"));
    }

    @Test
    void redactsExplicitPasswordValue() {
        assertEquals(
                ChatbotContentSafety.REDACTED_MESSAGE,
                ChatbotContentSafety.sanitizeForHistory("Mật khẩu của tôi là BetterHR@123"));
    }

    @Test
    void redactsBearerToken() {
        assertEquals(
                ChatbotContentSafety.REDACTED_MESSAGE,
                ChatbotContentSafety.sanitizeForHistory("Authorization: Bearer abcdefghijklmnop"));
    }

    @Test
    void responseCanCarryHistoryTrackingIds() {
        ChatbotResponse response = new ChatbotResponse(
                "success", "fallback", "reply", List.of("Liên hệ HR"))
                .withTracking(12L, 34L);

        assertEquals(12L, response.getConversationId());
        assertEquals(34L, response.getMessageId());
    }
}
