package com.hrm.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Test: AiProviderConfig - 100% Branch Coverage")
public class AiProviderConfigTest {

    @Test
    void testGeminiConfigBranches() {
        AiProviderConfig.GeminiSettings settings = AiProviderConfig.gemini();
        assertNotNull(settings);
        assertNotNull(settings.model());
        assertNotNull(settings.endpoint());

        // Test GeminiSettings enabled() method branches
        AiProviderConfig.GeminiSettings enabledSettings = new AiProviderConfig.GeminiSettings("key123", "model", "endpoint");
        assertTrue(enabledSettings.enabled());
        assertEquals("key123", enabledSettings.apiKey());

        AiProviderConfig.GeminiSettings disabledSettings = new AiProviderConfig.GeminiSettings("", "model", "endpoint");
        assertFalse(disabledSettings.enabled());

        AiProviderConfig.GeminiSettings nullSettings = new AiProviderConfig.GeminiSettings(null, "model", "endpoint");
        assertFalse(nullSettings.enabled());
    }
}
