package com.hrm.util;

import com.hrm.dao.RolePermissionDAO;
import com.hrm.dao.UserPermissionDAO;
import com.hrm.model.entity.SystemUser;
import java.util.Optional;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility helpers for checking runtime permissions.
 */
public final class PermissionUtil {
    private static final Logger logger = Logger.getLogger(PermissionUtil.class.getName());
    private static final RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();
    private static final UserPermissionDAO userPermissionDAO = new UserPermissionDAO();
    public static final int ROLE_ADMIN = 1;
    public static final int ROLE_HR_MANAGER = 2;
    public static final int ROLE_DEPT_MANAGER = 3;
    public static final int ROLE_HR_STAFF = 4;
    public static final int ROLE_EMPLOYEE = 5;
    private static final String DEFAULT_DENIED_MESSAGE = "You do not have permission to access this resource.";

    private PermissionUtil() {
    }

    /**
     * Get the logged-in SystemUser from session (attribute "systemUser").
     */
    public static SystemUser getCurrentUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object user = session.getAttribute("systemUser");
        if (user instanceof SystemUser) {
            return (SystemUser) user;
        }
        return null;
    }

    /**
     * Check whether the current user has the given permission code.
     */
    public static boolean hasPermission(SystemUser user, String permissionCode) {
        if (user == null || permissionCode == null || permissionCode.isBlank()) {
            return false;
        }
        Integer roleId = user.getRoleId();
        if (roleId != null && roleId == ROLE_ADMIN) {
            return true;
        }
        Integer userId = user.getUserId();
        if (userId != null && userId > 0) {
            try {
                Optional<Boolean> userOverride = userPermissionDAO.getPermissionOverride(userId, permissionCode);
                if (userOverride.isPresent()) {
                    return userOverride.get();
                }
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Failed to verify user-specific permission {0} for user {1}",
                        new Object[]{permissionCode, userId});
                logger.log(Level.FINE, "SQLException stack trace", ex);
            }
        }
        if (roleId == null || roleId <= 0) {
            return false;
        }
        try {
            return rolePermissionDAO.hasPermissionCode(roleId, permissionCode);
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Failed to verify permission {0} for role {1}",
                    new Object[]{permissionCode, roleId});
            logger.log(Level.FINE, "SQLException stack trace", ex);
            return false;
        }
    }

    /**
     * Ensure user has both the required role and permission for HTML responses.
     */
    public static boolean ensureRolePermission(HttpServletRequest request,
                                               HttpServletResponse response,
                                               int requiredRoleId,
                                               String permissionCode,
                                               String missingRoleMessage,
                                               String missingPermissionMessage)
            throws ServletException, IOException {
        SystemUser currentUser = getCurrentUser(request);
        if (!hasRole(currentUser, requiredRoleId)) {
            handleHtmlForbidden(request, response,
                    (missingRoleMessage == null || missingRoleMessage.isBlank())
                            ? DEFAULT_DENIED_MESSAGE
                            : missingRoleMessage);
            return false;
        }
        if (!hasPermission(currentUser, permissionCode)) {
            handleHtmlForbidden(request, response,
                    (missingPermissionMessage == null || missingPermissionMessage.isBlank())
                            ? DEFAULT_DENIED_MESSAGE
                            : missingPermissionMessage);
            return false;
        }
        return true;
    }

    /**
     * Ensure user has required role and permission for JSON responses.
     */
    public static boolean ensureRolePermissionJson(HttpServletRequest request,
                                                   HttpServletResponse response,
                                                   int requiredRoleId,
                                                   String permissionCode,
                                                   String missingRoleMessage,
                                                   String missingPermissionMessage) throws IOException {
        SystemUser currentUser = getCurrentUser(request);
        if (!hasRole(currentUser, requiredRoleId)) {
            handleJsonForbidden(response,
                    (missingRoleMessage == null || missingRoleMessage.isBlank())
                            ? DEFAULT_DENIED_MESSAGE
                            : missingRoleMessage);
            return false;
        }
        if (!hasPermission(currentUser, permissionCode)) {
            handleJsonForbidden(response,
                    (missingPermissionMessage == null || missingPermissionMessage.isBlank())
                            ? DEFAULT_DENIED_MESSAGE
                            : missingPermissionMessage);
            return false;
        }
        return true;
    }

    private static boolean hasRole(SystemUser user, int requiredRoleId) {
        if (user == null) {
            return false;
        }
        Integer roleId = user.getRoleId();
        return roleId != null && (roleId == ROLE_ADMIN || roleId == requiredRoleId);
    }

    /**
     * Convenience method to ensure the current user has the permission.
     * If not, renders an Access Denied page and returns false.
     */
    public static boolean ensurePermission(HttpServletRequest request,
                                           HttpServletResponse response,
                                           String permissionCode,
                                           String message)
            throws ServletException, IOException {
        SystemUser currentUser = getCurrentUser(request);
        if (hasPermission(currentUser, permissionCode)) {
            return true;
        }
        handleHtmlForbidden(request, response, message);
        return false;
    }

    /**
     * Forward to the shared Access Denied page.
     */
    public static void handleHtmlForbidden(HttpServletRequest request,
                                           HttpServletResponse response,
                                           String message)
            throws ServletException, IOException {
        request.setAttribute("errorMessage",
                (message == null || message.isBlank()) ? DEFAULT_DENIED_MESSAGE : message);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        request.getRequestDispatcher("/Views/Common/AccessDenied.jsp").forward(request, response);
    }

    /**
     * Send a JSON 403 response.
     */
    public static void handleJsonForbidden(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String safeMessage = (message == null || message.isBlank()) ? DEFAULT_DENIED_MESSAGE : message;
        response.getWriter().write("{\"status\":\"error\",\"message\":\"" + escapeJson(safeMessage) + "\"}");
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

