package com.hrm.service;

import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiChatServiceTest {
    @Test
    void callsProviderOnlyForFallback() {
        AiChatService service = new AiChatService((message, role) -> Optional.of("AI reply"));
        assertEquals("AI reply", service.answerFallback("fallback", "Quy trình onboarding là gì?", "Employee").orElseThrow());
        assertTrue(service.answerFallback("leave_request", "Cách xin nghỉ?", "Employee").isEmpty());
    }

    @Test
    void doesNotSendSecretToProvider() {
        AiChatService service = new AiChatService((message, role) -> {
            throw new AssertionError("Provider must not be called");
        });
        assertTrue(service.answerFallback("fallback", "api key=not-a-real-key", "Admin").isEmpty());
    }

    @Test
    void providerFailureKeepsStaticFallback() {
        AiChatService service = new AiChatService((message, role) -> {
            throw new IllegalStateException("offline");
        });
        assertTrue(service.answerFallback("fallback", "Quy định văn phòng?", "Guest").isEmpty());
    }
}
