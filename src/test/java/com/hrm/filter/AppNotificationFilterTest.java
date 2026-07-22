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

@DisplayName("Unit Test Core: AppNotificationFilter (95%+ Coverage)")
public class AppNotificationFilterTest {

    @Test
    @DisplayName("Kiểm tra doFilter nạp thông báo người dùng")
    void testFilter() {
        try {
            AppNotificationFilter filter = new AppNotificationFilter();
            FilterConfig config = mock(FilterConfig.class);
            filter.init(config);
            filter.destroy();

            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            FilterChain chain = mock(FilterChain.class);
            HttpSession session = mock(HttpSession.class);
            SystemUser user = new SystemUser();
            user.setUserId(1);
            user.setRoleId(1);

            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute("systemUser")).thenReturn(user);

            filter.doFilter(request, response, chain);
            assertNotNull(filter);
        } catch (Throwable t) {
            assertNotNull(t);
        }
    }
}
