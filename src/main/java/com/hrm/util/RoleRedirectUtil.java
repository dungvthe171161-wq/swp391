package com.hrm.util;

import com.hrm.model.entity.SystemUser;
import jakarta.servlet.http.HttpServletRequest;

public final class RoleRedirectUtil {

    private RoleRedirectUtil() {
    }

    public static String getDashboardPath(SystemUser user) {
        if (user == null || user.getRoleId() <= 0) {
            return "/homepage";
        }

        return switch (user.getRoleId()) {
            case 1 -> "/admin?action=dashboard";
            case 2 -> "/HrHomeController";
            case 3 -> "/dept?action=dashboard";
            case 4 -> "/hrstaff";
            case 5 -> "/employee";
            case 6 -> "/guest/dashboard";
            default -> "/homepage";
        };
    }

    public static String getDashboardUrl(HttpServletRequest request, SystemUser user) {
        return request.getContextPath() + getDashboardPath(user);
    }
}
