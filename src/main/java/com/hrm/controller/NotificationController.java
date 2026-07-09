package com.hrm.controller;

import com.hrm.model.entity.SystemUser;
import com.hrm.service.NotificationService;
import com.hrm.util.NotificationRedirectUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "NotificationController", urlPatterns = {"/notifications/read", "/notifications/read-all"})
public class NotificationController extends HttpServlet {

    private final NotificationService notificationService = new NotificationService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        SystemUser systemUser = session != null ? (SystemUser) session.getAttribute("systemUser") : null;
        if (systemUser == null || systemUser.getUserId() <= 0) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int userId = systemUser.getUserId();
        String servletPath = request.getServletPath();
        if ("/notifications/read-all".equals(servletPath)) {
            notificationService.markAllRead(userId);
            redirect(request, response, null);
            return;
        }

        int notificationId = parseInt(request.getParameter("notificationId"));
        notificationService.markRead(notificationId, userId);
        redirect(request, response, request.getParameter("targetUrl"));
    }

    private void redirect(HttpServletRequest request, HttpServletResponse response, String targetUrl) throws IOException {
        String fallbackUrl = request.getParameter("redirect");
        response.sendRedirect(NotificationRedirectUtil.resolve(request.getContextPath(), targetUrl, fallbackUrl));
    }

    private int parseInt(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}
