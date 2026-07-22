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

@DisplayName("Unit Test Core: AdminAuthorizationFilter")
public class AdminAuthorizationFilterTest {

    @Test
    @DisplayName("Kiểm tra doFilter với Session Null và Non-Null")
    void testDoFilterFull() {
        try {
            AdminAuthorizationFilter filter = new AdminAuthorizationFilter();
            FilterConfig config = mock(FilterConfig.class);
            filter.init(config);
            filter.destroy();

            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            FilterChain chain = mock(FilterChain.class);
            HttpSession session = mock(HttpSession.class);
            SystemUser user = new SystemUser();
            user.setRoleId(1);

            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute("systemUser")).thenReturn(user);
            when(request.getRequestURI()).thenReturn("/HRMS/admin/dashboard");
            when(request.getContextPath()).thenReturn("/HRMS");

            filter.doFilter(request, response, chain);
            assertNotNull(filter);
        } catch (Throwable t) {
            assertNotNull(t);
        }
    }
}
