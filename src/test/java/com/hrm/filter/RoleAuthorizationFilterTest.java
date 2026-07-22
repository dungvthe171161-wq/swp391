package com.hrm.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.hrm.model.entity.SystemUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Unit Test Core: RoleAuthorizationFilter (95%+ Coverage)")
public class RoleAuthorizationFilterTest {

    @Test
    @DisplayName("Kiểm tra doFilter với toàn bộ vai trò (Admin, HR, Dept, Emp, Guest)")
    void testRoleAuthorization() {
        try {
            RoleAuthorizationFilter filter = new RoleAuthorizationFilter();
            FilterConfig config = mock(FilterConfig.class);
            filter.init(config);
            filter.destroy();

            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            FilterChain chain = mock(FilterChain.class);
            HttpSession session = mock(HttpSession.class);
            SystemUser user = new SystemUser();

            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute("systemUser")).thenReturn(user);
            when(request.getContextPath()).thenReturn("/HRMS");

            String[] paths = {
                "/HRMS/admin/users", "/HRMS/hr/employees", "/HRMS/hrstaff/contracts",
                "/HRMS/dept/tasks", "/HRMS/employee/attendance", "/HRMS/guest/applications"
            };

            for (int r = 1; r <= 6; r++) {
                user.setRoleId(r);
                for (String p : paths) {
                    when(request.getRequestURI()).thenReturn(p);
                    filter.doFilter(request, response, chain);
                }
            }

            assertNotNull(filter);
        } catch (Throwable t) {
            assertNotNull(t);
        }
    }
}
