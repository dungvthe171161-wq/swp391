package com.hrm.filter;

import com.hrm.model.entity.SystemUser;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@WebFilter(filterName = "RoleAuthorizationFilter", urlPatterns = {"/*"})
public class RoleAuthorizationFilter implements Filter {

    private static final Map<String, Set<Integer>> ROLE_PATTERNS = new LinkedHashMap<>();

    static {
        addPattern(Set.of(1, 2), "/hr/", "/Views/hr/", "/HrHomeController", "/postRecruitments",
                "/detailRecruitment", "/detailRecruitmentCreate", "/viewRecruitment",
                "/viewCV", "/candidates", "/detailWaitingRecruitment", "/SimpleHrController");

        addPattern(Set.of(1, 4), "/postRecruitments", "/candidates", "/viewCV",
                "/detailRecruitmentCreate", "/detailRecruitment");

        addPattern(Set.of(1, 4), "/hrstaff", "/hrstaff/", "/Views/HrStaff/", "/hrstaff/payroll",
                "/hrstaff/contracts", "/hrstaff/tasks");

        addPattern(Set.of(1, 3), "/dept", "/dept/", "/taskManager", "/viewTask", "/postTask",
                "/Dept", "/DeptController");

        addPattern(Set.of(1, 5), "/Views/Employee/", "/employee", "/employee/", "/ViewPayroll",
                "/ViewContract", "/EmployeeHome", "/LeaveManagement");

        addPattern(Set.of(1, 6), "/guest", "/guest/", "/Views/Guest/");
    }

    private static void addPattern(Set<Integer> roles, String... patterns) {
        for (String pattern : patterns) {
            ROLE_PATTERNS.computeIfAbsent(pattern, key -> new LinkedHashSet<>()).addAll(roles);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // no-op
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String contextPath = httpRequest.getContextPath();
        String requestUri = httpRequest.getRequestURI();
        String path = requestUri.substring(contextPath.length());

        Set<Integer> allowedRoles = resolveAllowedRoles(path);

        if (allowedRoles == null || allowedRoles.isEmpty()) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        SystemUser currentUser = session != null ? (SystemUser) session.getAttribute("systemUser") : null;

        if (currentUser != null && allowedRoles.contains(currentUser.getRoleId())) {
            chain.doFilter(request, response);
            return;
        }

        if (isJsonRequest(httpRequest)) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            httpResponse.sendRedirect(contextPath + "/login");
        }
    }

    @Override
    public void destroy() {
        // no-op
    }

    private Set<Integer> resolveAllowedRoles(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }

        for (Map.Entry<String, Set<Integer>> entry : ROLE_PATTERNS.entrySet()) {
            String pattern = entry.getKey();
            if (matches(path, pattern)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private boolean matches(String path, String pattern) {
        if (pattern.endsWith("/")) {
            return path.startsWith(pattern);
        }
        return path.equals(pattern) || path.startsWith(pattern + "/");
    }

    private boolean isJsonRequest(HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equalsIgnoreCase(requestedWith)) {
            return true;
        }
        String accept = request.getHeader("Accept");
        return accept != null && accept.toLowerCase().contains("application/json");
    }
}

