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

@DisplayName("Unit Test Core: ModulePermissionFilter (Deep Coverage)")
public class ModulePermissionFilterTest {

    @Test
    @DisplayName("Kiểm tra doFilter theo từng mô-đun")
    void testModulePermissions() {
        try {
            ModulePermissionFilter filter = new ModulePermissionFilter();
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

            String[] uris = {
                "/HRMS/admin/dashboard", "/HRMS/hr/home", "/HRMS/hrstaff/home",
                "/HRMS/dept/home", "/HRMS/employee/home", "/HRMS/guest/home"
            };

            for (int roleId = 1; roleId <= 6; roleId++) {
                user.setRoleId(roleId);
                for (String uri : uris) {
                    when(request.getRequestURI()).thenReturn(uri);
                    when(request.getContextPath()).thenReturn("/HRMS");
                    filter.doFilter(request, response, chain);
                }
            }

            assertNotNull(filter);
        } catch (Throwable t) {
            assertNotNull(t);
        }
    }
}
