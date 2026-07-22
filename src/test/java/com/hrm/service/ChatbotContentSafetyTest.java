package com.hrm.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Test: ChatbotContentSafety - 100% Branch Coverage")
public class ChatbotContentSafetyTest {

    @Test
    void testSanitizeForHistoryBranches() {
        // Null message
        assertEquals("", ChatbotContentSafety.sanitizeForHistory(null));

        // Normal safe message
        assertEquals("Xin chào HR", ChatbotContentSafety.sanitizeForHistory("Xin chào HR"));

        // Sensitive message (password assignment)
        assertEquals(ChatbotContentSafety.REDACTED_MESSAGE, ChatbotContentSafety.sanitizeForHistory("mật khẩu là 123456"));
    }

    @Test
    void testContainsSensitiveValueBranches() {
        // Null / blank
        assertFalse(ChatbotContentSafety.containsSensitiveValue(null));
        assertFalse(ChatbotContentSafety.containsSensitiveValue("   "));

        // Sensitive pattern: Bearer token
        assertTrue(ChatbotContentSafety.containsSensitiveValue("Bearer abcdef123456789"));

        // Sensitive pattern: JWT token
        assertTrue(ChatbotContentSafety.containsSensitiveValue("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0"));

        // Sensitive pattern: Provider key
        assertTrue(ChatbotContentSafety.containsSensitiveValue("sk-1234567890abcdef123"));

        // Sensitive pattern: JDBC URL
        assertTrue(ChatbotContentSafety.containsSensitiveValue("jdbc:mysql://localhost:3306/db"));

        // Conceptual question (not actual sensitive value)
        assertFalse(ChatbotContentSafety.containsSensitiveValue("mật khẩu là gì?"));
        assertFalse(ChatbotContentSafety.containsSensitiveValue("password là gì"));
        assertFalse(ChatbotContentSafety.containsSensitiveValue("secret là what"));
    }
}
