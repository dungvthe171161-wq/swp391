package com.hrm.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Test: NotificationRedirectUtil - 100% Branch Coverage")
public class NotificationRedirectUtilTest {

    @Test
    void testResolveBranches() {
        // Valid target URL with leading slash
        assertEquals("/HRMS/dashboard", NotificationRedirectUtil.resolve("/HRMS", "/dashboard", "/fallback"));

        // Valid target URL starting with context path
        assertEquals("/HRMS/dashboard", NotificationRedirectUtil.resolve("/HRMS", "/HRMS/dashboard", "/fallback"));

        // Null target, valid fallback
        assertEquals("/HRMS/fallback", NotificationRedirectUtil.resolve("/HRMS", null, "/fallback"));

        // Invalid target (empty, //, newline), valid fallback
        assertEquals("/HRMS/fallback", NotificationRedirectUtil.resolve("/HRMS", "   ", "/fallback"));
        assertEquals("/HRMS/fallback", NotificationRedirectUtil.resolve("/HRMS", "//malicious.com", "/fallback"));
        assertEquals("/HRMS/fallback", NotificationRedirectUtil.resolve("/HRMS", "/path\nbreak", "/fallback"));

        // Both target and fallback invalid -> default path
        assertEquals("/HRMS/hrstaff", NotificationRedirectUtil.resolve("/HRMS", null, null));
        assertEquals("/HRMS/hrstaff", NotificationRedirectUtil.resolve("/HRMS", "  ", "  "));

        // Null context path
        assertEquals("/dashboard", NotificationRedirectUtil.resolve(null, "/dashboard", null));
        assertEquals("/hrstaff", NotificationRedirectUtil.resolve(null, null, null));
    }
}
