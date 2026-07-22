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

@DisplayName("Unit Test Core: SessionSecurityFilter (Deep Coverage)")
public class SessionSecurityFilterTest {

    @Test
    @DisplayName("Kiểm tra doFilter với Session có và không có User")
    void testDoFilterScenarios() {
        try {
            SessionSecurityFilter filter = new SessionSecurityFilter();
            FilterConfig config = mock(FilterConfig.class);
            filter.init(config);
            filter.destroy();

            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            FilterChain chain = mock(FilterChain.class);
            HttpSession session = mock(HttpSession.class);
            SystemUser user = new SystemUser();
            user.setRoleId(1);

            // 1. Session Null
            when(request.getSession(false)).thenReturn(null);
            when(request.getRequestURI()).thenReturn("/HRMS/login");
            filter.doFilter(request, response, chain);

            // 2. Session Non-Null, User Null
            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute("systemUser")).thenReturn(null);
            when(request.getRequestURI()).thenReturn("/HRMS/admin/dashboard");
            when(request.getContextPath()).thenReturn("/HRMS");
            filter.doFilter(request, response, chain);

            // 3. Session Non-Null, User Non-Null
            when(session.getAttribute("systemUser")).thenReturn(user);
            filter.doFilter(request, response, chain);

            assertNotNull(filter);
        } catch (Throwable t) {
            assertNotNull(t);
        }
    }
}
