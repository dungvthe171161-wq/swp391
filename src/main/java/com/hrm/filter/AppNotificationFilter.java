package com.hrm.filter;

import com.hrm.model.entity.SystemUser;
import com.hrm.service.NotificationService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "AppNotificationFilter", urlPatterns = {
        "/employee",
        "/employee/*",
        "/dept",
        "/dept/*",
        "/postTask",
        "/taskManager",
        "/HrHomeController",
        "/SimpleHrController",
        "/hr/*",
        "/viewRecruitment",
        "/detailWaitingRecruitment",
        "/guest",
        "/guest/*",
        "/admin",
        "/admin/*"
})
public class AppNotificationFilter implements Filter {

    private final NotificationService notificationService = new NotificationService();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            populateNotificationAttributes(httpRequest);
        }
        chain.doFilter(request, response);
    }

    private void populateNotificationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        SystemUser systemUser = session != null ? (SystemUser) session.getAttribute("systemUser") : null;
        if (systemUser == null || systemUser.getUserId() <= 0) {
            request.setAttribute("appNotificationCount", "0");
            request.setAttribute("appNotifications", java.util.List.of());
            return;
        }

        int userId = systemUser.getUserId();
        request.setAttribute("appNotificationCount", String.valueOf(notificationService.unreadCount(userId)));
        request.setAttribute("appNotifications", notificationService.recentForUser(userId, 5));
    }
}
