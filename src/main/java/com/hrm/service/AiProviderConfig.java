package com.hrm.service;

public final class AiProviderConfig {
    private static final String DEFAULT_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/";
    private static final String DEFAULT_MODEL = "gemini-3.1-flash-lite";

    private AiProviderConfig() {
    }

    public static GeminiSettings gemini() {
        String apiKey = value("GEMINI_API_KEY", "");
        if (apiKey.isBlank()) {
            apiKey = value("GOOGLE_API_KEY", "");
        }
        return new GeminiSettings(apiKey, value("GEMINI_MODEL", DEFAULT_MODEL),
                value("GEMINI_API_URL", DEFAULT_ENDPOINT));
    }

    private static String value(String key, String fallback) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            value = System.getProperty(key);
        }
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    public record GeminiSettings(String apiKey, String model, String endpoint) {
        public boolean enabled() {
            return apiKey != null && !apiKey.isBlank();
        }
    }
}
