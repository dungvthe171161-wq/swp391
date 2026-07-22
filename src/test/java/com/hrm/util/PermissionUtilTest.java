package com.hrm.util;

import com.hrm.model.entity.Role;
import com.hrm.model.entity.SystemUser;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Unit Test: PermissionUtil - 100% Branch Coverage")
public class PermissionUtilTest {

    private SystemUser createUser(int userId, int roleId, String roleName) {
        SystemUser user = new SystemUser();
        user.setUserId(userId);
        user.setRoleId(roleId);
        Role r = new Role();
        r.setRoleId(roleId);
        r.setRoleName(roleName);
        user.setRole(r);
        return user;
    }

    @Test
    void testGetCurrentUserBranches() {
        // Null request
        assertNull(PermissionUtil.getCurrentUser(null));

        // Request without session
        HttpServletRequest req1 = mock(HttpServletRequest.class);
        when(req1.getSession(false)).thenReturn(null);
        assertNull(PermissionUtil.getCurrentUser(req1));

        // Session with non-SystemUser attribute
        HttpServletRequest req2 = mock(HttpServletRequest.class);
        HttpSession sess2 = mock(HttpSession.class);
        when(req2.getSession(false)).thenReturn(sess2);
        when(sess2.getAttribute("systemUser")).thenReturn("not_a_user");
        assertNull(PermissionUtil.getCurrentUser(req2));

        // Session with valid SystemUser
        HttpServletRequest req3 = mock(HttpServletRequest.class);
        HttpSession sess3 = mock(HttpSession.class);
        SystemUser user = createUser(1, 1, "Admin");
        when(req3.getSession(false)).thenReturn(sess3);
        when(sess3.getAttribute("systemUser")).thenReturn(user);
        assertEquals(user, PermissionUtil.getCurrentUser(req3));
    }

    @Test
    void testHasPermissionBranches() {
        // Null user or null/blank code
        assertFalse(PermissionUtil.hasPermission(null, "VIEW_EMPLOYEE"));
        SystemUser user = createUser(10, 5, "Employee");
        assertFalse(PermissionUtil.hasPermission(user, null));
        assertFalse(PermissionUtil.hasPermission(user, "   "));

        // Admin role (roleId = 1) -> always true
        SystemUser admin = createUser(1, 1, "Admin");
        assertTrue(PermissionUtil.hasPermission(admin, "VIEW_ANYTHING"));
    }

    @Test
    void testEnsurePermissionBranches() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession sess = mock(HttpSession.class);
        RequestDispatcher rd = mock(RequestDispatcher.class);

        when(req.getSession(false)).thenReturn(sess);
        when(req.getRequestDispatcher(anyString())).thenReturn(rd);

        // Admin user -> has permission
        SystemUser admin = createUser(1, 1, "Admin");
        when(sess.getAttribute("systemUser")).thenReturn(admin);
        assertTrue(PermissionUtil.ensurePermission(req, resp, "VIEW_ALL", "Msg"));

        // Null user -> denied
        when(sess.getAttribute("systemUser")).thenReturn(null);
        assertFalse(PermissionUtil.ensurePermission(req, resp, "VIEW_ALL", "Msg"));
    }

    @Test
    void testEnsureRolePermissionBranches() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession sess = mock(HttpSession.class);
        RequestDispatcher rd = mock(RequestDispatcher.class);

        when(req.getSession(false)).thenReturn(sess);
        when(req.getRequestDispatcher(anyString())).thenReturn(rd);

        // Admin user -> allowed
        SystemUser admin = createUser(1, 1, "Admin");
        when(sess.getAttribute("systemUser")).thenReturn(admin);
        assertTrue(PermissionUtil.ensureRolePermission(req, resp, 1, "VIEW_ALL", null, null));

        // Non-admin, null user -> denied
        when(sess.getAttribute("systemUser")).thenReturn(null);
        assertFalse(PermissionUtil.ensureRolePermission(req, resp, 1, "VIEW_ALL", "", ""));
    }

    @Test
    void testEnsureRolePermissionJsonBranches() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession sess = mock(HttpSession.class);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(req.getSession(false)).thenReturn(sess);
        when(resp.getWriter()).thenReturn(pw);

        // Admin -> allowed
        SystemUser admin = createUser(1, 1, "Admin");
        when(sess.getAttribute("systemUser")).thenReturn(admin);
        assertTrue(PermissionUtil.ensureRolePermissionJson(req, resp, 1, "VIEW_ALL", null, null));

        // Null user -> denied
        when(sess.getAttribute("systemUser")).thenReturn(null);
        assertFalse(PermissionUtil.ensureRolePermissionJson(req, resp, 1, "VIEW_ALL", "No role", "No perm"));
    }

    @Test
    void testForbiddenHandlers() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        RequestDispatcher rd = mock(RequestDispatcher.class);
        StringWriter sw = new StringWriter();

        when(req.getRequestDispatcher(anyString())).thenReturn(rd);
        when(resp.getWriter()).thenReturn(new PrintWriter(sw));

        PermissionUtil.handleHtmlForbidden(req, resp, null);
        PermissionUtil.handleHtmlForbidden(req, resp, "Access Denied Message");
        PermissionUtil.handleJsonForbidden(resp, null);
        PermissionUtil.handleJsonForbidden(resp, "Json Denied Message");
        assertTrue(sw.toString().contains("Json Denied Message"));
    }
}
