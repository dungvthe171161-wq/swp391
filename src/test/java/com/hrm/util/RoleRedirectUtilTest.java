package com.hrm.util;

import com.hrm.model.entity.SystemUser;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Unit Test: RoleRedirectUtil - 100% Branch Coverage")
public class RoleRedirectUtilTest {

    @Test
    void testGetDashboardPathBranches() {
        // Null user
        assertEquals("/homepage", RoleRedirectUtil.getDashboardPath(null));

        // Invalid role id <= 0
        SystemUser user0 = new SystemUser();
        user0.setRoleId(0);
        assertEquals("/homepage", RoleRedirectUtil.getDashboardPath(user0));

        // Role 1 -> Admin
        SystemUser user1 = new SystemUser(); user1.setRoleId(1);
        assertTrue(RoleRedirectUtil.getDashboardPath(user1).contains("admin"));

        // Role 2 -> HR Home
        SystemUser user2 = new SystemUser(); user2.setRoleId(2);
        assertTrue(RoleRedirectUtil.getDashboardPath(user2).contains("HrHome"));

        // Role 3 -> Dept
        SystemUser user3 = new SystemUser(); user3.setRoleId(3);
        assertTrue(RoleRedirectUtil.getDashboardPath(user3).contains("dept"));

        // Role 4 -> HR Staff
        SystemUser user4 = new SystemUser(); user4.setRoleId(4);
        assertTrue(RoleRedirectUtil.getDashboardPath(user4).contains("hrstaff"));

        // Role 5 -> Employee
        SystemUser user5 = new SystemUser(); user5.setRoleId(5);
        assertTrue(RoleRedirectUtil.getDashboardPath(user5).contains("employee"));

        // Role 6 -> Guest
        SystemUser user6 = new SystemUser(); user6.setRoleId(6);
        assertTrue(RoleRedirectUtil.getDashboardPath(user6).contains("guest"));

        // Unknown role -> default
        SystemUser user99 = new SystemUser(); user99.setRoleId(99);
        assertEquals("/homepage", RoleRedirectUtil.getDashboardPath(user99));
    }

    @Test
    void testGetDashboardUrl() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getContextPath()).thenReturn("/HRMS");
        SystemUser user = new SystemUser();
        user.setRoleId(1);
        assertEquals("/HRMS/admin?action=dashboard", RoleRedirectUtil.getDashboardUrl(req, user));
    }
}
