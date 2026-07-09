package com.hrm.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class NotificationRedirectUtilTest {

    @Test
    void resolvesInternalNotificationTargetWithContextPath() {
        assertEquals(
                "/HRMS/candidates?applicationId=12",
                NotificationRedirectUtil.resolve("/HRMS", "/candidates?applicationId=12", "/HRMS/hrstaff"));
    }

    @Test
    void keepsFallbackWhenTargetIsExternal() {
        assertEquals(
                "/HRMS/hrstaff",
                NotificationRedirectUtil.resolve("/HRMS", "https://example.com", "/HRMS/hrstaff"));
    }

    @Test
    void rejectsProtocolRelativeAndNewlineTargets() {
        assertEquals("/HRMS/hrstaff", NotificationRedirectUtil.resolve("/HRMS", "//evil.test", "/HRMS/hrstaff"));
        assertEquals("/HRMS/hrstaff", NotificationRedirectUtil.resolve("/HRMS", "/candidates\nSet-Cookie:x", "/HRMS/hrstaff"));
    }

    @Test
    void fallsBackToHrStaffWhenNoSafePathExists() {
        assertEquals("/HRMS/hrstaff", NotificationRedirectUtil.resolve("/HRMS", null, "https://example.com"));
    }
}
