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
import java.util.Set;

@WebFilter(filterName = "SessionSecurityFilter", urlPatterns = {"/*"})
public class SessionSecurityFilter implements Filter {

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "",
            "/",
            "/login",
            "/register",
            "/auth/google",
            "/auth/google/callback",
            "/loginByGmail",
            "/logout",
            "/homepage",
            "/ForgotPassword",
            "/changepassRE",
            "/Views/Login.jsp",
            "/Views/Register.jsp",
            "/Views/Homepage.jsp",
            "/Views/AccessDenied.jsp",
            "/Recovery",
            "/ForgotPassword.jsp",
            "/Views/ForgotPassword.jsp"
    );

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

        if (httpRequest.isRequestedSessionIdFromURL()) {
            HttpSession hijackedSession = httpRequest.getSession(false);
            if (hijackedSession != null) {
                hijackedSession.invalidate();
            }
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }

        if (isStaticResource(path) || isPublicPath(httpRequest, path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        SystemUser systemUser = session != null ? (SystemUser) session.getAttribute("systemUser") : null;

        if (systemUser == null) {
            if (isJsonRequest(httpRequest)) {
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                String loginUrl = contextPath + "/login";
                if (isRecruitmentApplyPath(httpRequest, path)) {
                    String query = httpRequest.getQueryString();
                    String target = requestUri + (query != null && !query.isBlank() ? "?" + query : "");
                    httpRequest.getSession(true).setAttribute("redirectAfterLogin", target);
                    loginUrl += "?error=login_required";
                }
                httpResponse.sendRedirect(loginUrl);
            }
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // no-op
    }

    private boolean isPublicPath(HttpServletRequest request, String path) {
        if (path == null) {
            return true;
        }
        if (PUBLIC_PATHS.contains(path)) {
            return true;
        }
        if (path.startsWith("/public/") || path.startsWith("/Views/public/")) {
            return true;
        }
        if (path.startsWith("/RecruitmentController")) {
            String action = request.getParameter("action");
            return action == null || action.isBlank() || "list".equals(action) || "view".equals(action);
        }
        if (path.startsWith("/Views/Recruitment")) {
            return true;
        }
        return false;
    }

    private boolean isRecruitmentApplyPath(HttpServletRequest request, String path) {
        if (path == null) {
            return false;
        }
        if (path.startsWith("/Views/ApplyForm") || path.startsWith("/Views/Success")) {
            return true;
        }
        if (path.startsWith("/RecruitmentController")) {
            String action = request.getParameter("action");
            return "apply".equals(action) || "submitApplication".equals(action);
        }
        return false;
    }

    private boolean isStaticResource(String path) {
        if (path == null) {
            return false;
        }
        String lowerPath = path.toLowerCase();
        return lowerPath.endsWith(".css")
                || lowerPath.endsWith(".js")
                || lowerPath.endsWith(".png")
                || lowerPath.endsWith(".jpg")
                || lowerPath.endsWith(".jpeg")
                || lowerPath.endsWith(".gif")
                || lowerPath.endsWith(".svg")
                || lowerPath.endsWith(".ico")
                || lowerPath.endsWith(".woff")
                || lowerPath.endsWith(".woff2")
                || lowerPath.endsWith(".ttf")
                || lowerPath.endsWith(".eot")
                || lowerPath.endsWith(".map")
                || lowerPath.startsWith("/css/")
                || lowerPath.startsWith("/js/")
                || lowerPath.startsWith("/image/")
                || lowerPath.startsWith("/images/")
                || lowerPath.startsWith("/fonts/")
                || lowerPath.startsWith("/Upload/");
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

