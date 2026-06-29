package com.hrm.controller.hr;

import com.hrm.dao.MailRequestDAO;
import com.hrm.model.entity.SystemUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LeaveApprovalController", urlPatterns = {"/hr/leaves"})
public class LeaveApprovalController extends HttpServlet {

    private final MailRequestDAO mailRequestDAO = new MailRequestDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String status = request.getParameter("status");
        if (status == null || status.isBlank()) {
            status = "Pending";
        }
        request.setAttribute("statusFilter", status);
        request.setAttribute("leaveRequests", mailRequestDAO.getAllLeaveRequests(status));
        pullFlashMessages(request);
        request.getRequestDispatcher("/Views/hr/LeaveRequests.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        SystemUser currentUser = session != null ? (SystemUser) session.getAttribute("systemUser") : null;
        if (currentUser == null || currentUser.getEmployeeId() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int requestId = parseInt(request.getParameter("requestId"), -1);
        String decision = request.getParameter("decision");
        boolean success = requestId > 0
                && ("Approved".equals(decision) || "Rejected".equals(decision))
                && mailRequestDAO.updateLeaveStatus(requestId, decision, currentUser.getEmployeeId());

        session.setAttribute(success ? "hrLeaveSuccess" : "hrLeaveError",
                success ? "Da cap nhat don nghi phep." : "Khong the cap nhat don nghi phep nay.");
        response.sendRedirect(request.getContextPath() + "/hr/leaves");
    }

    private void pullFlashMessages(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        Object success = session.getAttribute("hrLeaveSuccess");
        Object error = session.getAttribute("hrLeaveError");
        if (success != null) {
            request.setAttribute("hrLeaveSuccess", success);
            session.removeAttribute("hrLeaveSuccess");
        }
        if (error != null) {
            request.setAttribute("hrLeaveError", error);
            session.removeAttribute("hrLeaveError");
        }
    }

    private int parseInt(String value, int fallback) {
        try {
            return value == null || value.isBlank() ? fallback : Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }
}
