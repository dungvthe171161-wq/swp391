package com.hrm.service;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AiChatService {
    private static final Logger LOGGER = Logger.getLogger(AiChatService.class.getName());
    private static final int MAX_PROVIDER_INPUT_LENGTH = 500;
    private final AiChatProvider provider;

    public AiChatService() {
        this(new GeminiGenerateContentProvider());
    }

    AiChatService(AiChatProvider provider) {
        this.provider = provider;
    }

    public Optional<String> answerFallback(String intent, String message, String roleName) {
        if (!"fallback".equals(intent) || message == null || message.isBlank()
                || message.length() > MAX_PROVIDER_INPUT_LENGTH
                || ChatbotContentSafety.containsSensitiveValue(message)) {
            return Optional.empty();
        }
        try {
            return provider.answer(message.trim(), roleName).filter(reply -> !reply.isBlank());
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "AI provider unavailable; using chatbot fallback", ex);
            return Optional.empty();
        }
    }
}
