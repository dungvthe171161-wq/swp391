package com.hrm.util;

public final class NotificationRedirectUtil {

    private static final String DEFAULT_PATH = "/hrstaff";

    private NotificationRedirectUtil() {
    }

    public static String resolve(String contextPath, String targetUrl, String fallbackUrl) {
        String context = contextPath == null ? "" : contextPath;
        String target = safeInternalPath(context, targetUrl);
        if (target != null) {
            return target;
        }
        String fallback = safeInternalPath(context, fallbackUrl);
        return fallback != null ? fallback : context + DEFAULT_PATH;
    }

    private static String safeInternalPath(String contextPath, String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()
                || trimmed.startsWith("//")
                || trimmed.contains("\r")
                || trimmed.contains("\n")) {
            return null;
        }
        if (!contextPath.isEmpty() && trimmed.startsWith(contextPath + "/")) {
            return trimmed;
        }
        if (trimmed.startsWith("/") && !trimmed.startsWith("//")) {
            return contextPath + trimmed;
        }
        return null;
    }
}
