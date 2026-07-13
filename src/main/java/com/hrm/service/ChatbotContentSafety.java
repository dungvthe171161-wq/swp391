package com.hrm.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ChatbotContentSafety {

    public static final String REDACTED_MESSAGE = "[Nội dung nhạy cảm đã được ẩn]";

    private static final Pattern SENSITIVE_ASSIGNMENT = Pattern.compile(
            "(?iu)\\b(password|mật khẩu|mat khau|api[ _-]?key|token|secret|cookie|connection string)\\b"
            + "\\s*(?:của tôi\\s*|cua toi\\s*)?(?:=|:|là|la)\\s*(.+)");
    private static final Pattern BEARER_TOKEN = Pattern.compile("(?iu)\\bbearer\\s+[a-z0-9._~+/-]{8,}");
    private static final Pattern JWT_TOKEN = Pattern.compile("\\beyJ[a-zA-Z0-9_-]{10,}\\.[a-zA-Z0-9_-]{8,}");
    private static final Pattern PROVIDER_KEY = Pattern.compile("\\bsk-[a-zA-Z0-9_-]{12,}");
    private static final Pattern JDBC_URL = Pattern.compile("(?iu)jdbc:(mysql|postgresql|sqlserver):\\S+");

    private ChatbotContentSafety() {
    }

    public static String sanitizeForHistory(String message) {
        if (message == null) {
            return "";
        }
        String trimmed = message.trim();
        if (containsSensitiveValue(trimmed)) {
            return REDACTED_MESSAGE;
        }
        return trimmed;
    }

    public static boolean containsSensitiveValue(String message) {
        if (message == null || message.isBlank()) {
            return false;
        }
        Matcher assignment = SENSITIVE_ASSIGNMENT.matcher(message);
        boolean hasSensitiveAssignment = assignment.find()
                && !isConceptualQuestion(assignment.group(2));
        return hasSensitiveAssignment
                || BEARER_TOKEN.matcher(message).find()
                || JWT_TOKEN.matcher(message).find()
                || PROVIDER_KEY.matcher(message).find()
                || JDBC_URL.matcher(message).find();
    }

    private static boolean isConceptualQuestion(String assignedValue) {
        String value = assignedValue == null ? "" : assignedValue.trim().toLowerCase();
        return value.startsWith("gì")
                || value.startsWith("gi?")
                || value.startsWith("gi ")
                || value.startsWith("what");
    }
}
