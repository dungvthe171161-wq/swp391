package com.hrm.filter;

import com.hrm.model.entity.SystemUser;
import com.hrm.util.PermissionUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Bộ lọc trách nhiệm kiểm soát quyền truy cập cho các module nhạy cảm:
 * admin, dept và employee. Tận dụng kho permission động thông qua PermissionUtil.
 */
// request  đến filter 
@WebFilter(filterName = "ModulePermissionFilter", urlPatterns = {
        "/admin", "/admin/*",
        "/dept", "/dept/*",
        "/taskManager", "/postTask", "/viewTask",
        "/employee", "/employee/*"
})
public class ModulePermissionFilter extends HttpFilter {
    private static final String DEFAULT_ROLE_MESSAGE = "Bạn không có quyền phù hợp để truy cập khu vực này.";
    private static final String DEFAULT_PERMISSION_MESSAGE = "Bạn thiếu quyền thao tác cần thiết.";
    private static final String PERMISSION_VIEW_DEPARTMENTS = "VIEW_DEPARTMENTS";
    private static final String MESSAGE_DEPARTMENT_PERMISSION = "Bạn thiếu quyền xem phòng ban.";

    private static final List<ModuleRule> RULES = List.of(
            ModuleRule.jsonAware(
                    "/admin/role-permissions/api",
                    null,
                    "MANAGE_ROLE_PERMISSIONS",
                    "Chỉ quản trị viên mới được quản lý phân quyền.",
                    "Bạn thiếu quyền MANAGE_ROLE_PERMISSIONS."),
            ModuleRule.jsonAware(
                    "/admin/role",
                    null,
                    "VIEW_ROLES",
                    "Chỉ quản trị viên mới được quản lý vai trò.",
                    "Bạn thiếu quyền VIEW_ROLES.",
                    ModulePermissionFilter::isRoleApiRequest),
            ModuleRule.jsonAware(
                    "/admin/users",
                    null,
                    "VIEW_USERS",
                    "Chỉ quản trị viên mới được quản lý người dùng.",
                    "Bạn thiếu quyền VIEW_USERS.",
                    ModulePermissionFilter::isAdminUsersApiRequest),
            ModuleRule.htmlOnly(
                    "/admin",
                    null,
                    "MANAGE_SYSTEM",
                    "Chỉ quản trị viên mới được truy cập trang quản trị.",
                    "Bạn thiếu quyền MANAGE_SYSTEM."),
            ModuleRule.htmlOnly(
                    "/dept",
                    null,
                    PERMISSION_VIEW_DEPARTMENTS,
                    "Chỉ trưởng phòng mới được truy cập khu vực phòng ban.",
                    MESSAGE_DEPARTMENT_PERMISSION),
            ModuleRule.htmlOnly(
                    "/taskManager",
                    null,
                    PERMISSION_VIEW_DEPARTMENTS,
                    "Chỉ trưởng phòng mới được quản lý công việc.",
                    MESSAGE_DEPARTMENT_PERMISSION),
            ModuleRule.htmlOnly(
                    "/postTask",
                    null,
                    PERMISSION_VIEW_DEPARTMENTS,
                    "Chỉ trưởng phòng mới được giao nhiệm vụ.",
                    MESSAGE_DEPARTMENT_PERMISSION),
            ModuleRule.htmlOnly(
                    "/viewTask",
                    null,
                    PERMISSION_VIEW_DEPARTMENTS,
                    "Chỉ trưởng phòng mới được xem nhiệm vụ.",
                    MESSAGE_DEPARTMENT_PERMISSION),
            ModuleRule.htmlOnly(
                    "/employee",
                    null,
                    "VIEW_EMPLOYEE_DETAIL",
                    "Chỉ nhân viên được phép truy cập khu vực này.",
                    "Bạn thiếu quyền xem thông tin nhân viên.")
    );

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String path = getRequestPath(request);
        ModuleRule rule = RULES.stream()
                .filter(r -> r.matches(path))
                .findFirst()
                .orElse(null);

        if (rule != null) {
            boolean allowed;
            if (rule.requiresRoleCheck()) {
                if (rule.shouldReturnJson(request, path)) {
                    allowed = PermissionUtil.ensureRolePermissionJson(
                            request,
                            response,
                            rule.requiredRoleId,
                            rule.permissionCode,
                            rule.missingRoleMessage,
                            rule.missingPermissionMessage);
                } else {
                    allowed = PermissionUtil.ensureRolePermission(
                            request,
                            response,
                            rule.requiredRoleId,
                            rule.permissionCode,
                            rule.missingRoleMessage,
                            rule.missingPermissionMessage);
                }
            } else if (rule.shouldReturnJson(request, path)) {
                allowed = ensurePermissionJson(request, response, rule.permissionCode, rule.missingPermissionMessage);
            } else {
                allowed = PermissionUtil.ensurePermission(
                        request,
                        response,
                        rule.permissionCode,
                        rule.missingPermissionMessage);
            }

            if (!allowed) {
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean ensurePermissionJson(HttpServletRequest request,
                                         HttpServletResponse response,
                                         String permissionCode,
                                         String deniedMessage) throws IOException {
        SystemUser currentUser = PermissionUtil.getCurrentUser(request);
        if (PermissionUtil.hasPermission(currentUser, permissionCode)) {
            return true;
        }
        PermissionUtil.handleJsonForbidden(
                response,
                (deniedMessage == null || deniedMessage.isBlank()) ? DEFAULT_PERMISSION_MESSAGE : deniedMessage);
        return false;
    }

    private static String getRequestPath(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && uri.startsWith(contextPath)) {
            return uri.substring(contextPath.length());
        }
        return uri;
    }

    private static boolean isRoleApiRequest(HttpServletRequest request, String path) {
        if (path.startsWith("/admin/role/") && path.length() > "/admin/role/".length()) {
            return true;
        }
        String method = request.getMethod();
        return !"GET".equalsIgnoreCase(method);
    }

    private static boolean isAdminUsersApiRequest(HttpServletRequest request, String path) {
        String action = request.getParameter("action");
        if ("checkUsername".equalsIgnoreCase(action)
                || "toggleStatus".equalsIgnoreCase(action)
                || "delete".equalsIgnoreCase(action)
                || "save".equalsIgnoreCase(action)
                || "update".equalsIgnoreCase(action)
                || "resetPassword".equalsIgnoreCase(action)) {
            return true;
        }
        String accept = request.getHeader("Accept");
        return accept != null && accept.contains("application/json");
    }

    private static final class ModuleRule {
        private final String pathPrefix;
        private final Integer requiredRoleId;
        private final String permissionCode;
        private final String missingRoleMessage;
        private final String missingPermissionMessage;
        private final JsonDecider jsonDecider;

        private ModuleRule(String pathPrefix,
                           Integer requiredRoleId,
                           String permissionCode,
                           String missingRoleMessage,
                           String missingPermissionMessage,
                           JsonDecider jsonDecider) {
            this.pathPrefix = pathPrefix;
            this.requiredRoleId = requiredRoleId;
            this.permissionCode = permissionCode;
            this.missingRoleMessage = missingRoleMessage != null ? missingRoleMessage : DEFAULT_ROLE_MESSAGE;
            this.missingPermissionMessage = missingPermissionMessage != null ? missingPermissionMessage : DEFAULT_PERMISSION_MESSAGE;
            this.jsonDecider = jsonDecider;
        }

        static ModuleRule jsonAware(String pathPrefix,
                                    Integer requiredRoleId,
                                    String permissionCode,
                                    String missingRoleMessage,
                                    String missingPermissionMessage) {
            return new ModuleRule(pathPrefix, requiredRoleId, permissionCode, missingRoleMessage, missingPermissionMessage,
                    (req, path) -> true);
        }

        static ModuleRule jsonAware(String pathPrefix,
                                    Integer requiredRoleId,
                                    String permissionCode,
                                    String missingRoleMessage,
                                    String missingPermissionMessage,
                                    JsonDecider decider) {
            return new ModuleRule(pathPrefix, requiredRoleId, permissionCode, missingRoleMessage, missingPermissionMessage,
                    decider);
        }

        static ModuleRule htmlOnly(String pathPrefix,
                                   Integer requiredRoleId,
                                   String permissionCode,
                                   String missingRoleMessage,
                                   String missingPermissionMessage) {
            return new ModuleRule(pathPrefix, requiredRoleId, permissionCode, missingRoleMessage, missingPermissionMessage,
                    (req, path) -> false);
        }

        boolean matches(String requestPath) {
            if (requestPath.equals(pathPrefix)) {
                return true;
            }
            return requestPath.startsWith(pathPrefix + "/");
        }

        boolean shouldReturnJson(HttpServletRequest request, String path) {
            return jsonDecider != null && jsonDecider.shouldReturnJson(request, path);
        }

        boolean requiresRoleCheck() {
            return requiredRoleId != null && requiredRoleId > 0;
        }
    }

    @FunctionalInterface
    private interface JsonDecider {
        boolean shouldReturnJson(HttpServletRequest request, String path);
    }
}


