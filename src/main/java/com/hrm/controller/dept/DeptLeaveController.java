package com.hrm.controller.dept;

import com.hrm.dao.EmployeeDAO;
import com.hrm.dao.MailRequestDAO;
import com.hrm.service.NotificationRecipientService;
import com.hrm.service.NotificationService;
import com.hrm.util.DeptManagerScope;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@WebServlet(name = "DeptLeaveController", urlPatterns = {"/dept/leaves"})
public class DeptLeaveController extends HttpServlet {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final MailRequestDAO mailRequestDAO = new MailRequestDAO();
    private final NotificationService notificationService = new NotificationService();
    private final NotificationRecipientService notificationRecipientService = new NotificationRecipientService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DeptManagerScope scope = DeptManagerScope.from(request, employeeDAO);
        if (scope == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (!scope.hasDepartment()) {
            response.sendRedirect(request.getContextPath() + "/dept");
            return;
        }

        String status = request.getParameter("status");
        if (status == null || status.isBlank()) {
            status = "Pending";
        }

        pullFlashMessages(request);
        request.setAttribute("activePage", "leaves");
        request.setAttribute("pageTitle", "Yêu cầu nghỉ phép");
        request.setAttribute("pageSubtitle", "Duyệt hoặc từ chối đơn nghỉ phép của nhân viên trong phòng ban.");
        request.setAttribute("statusFilter", status);
        request.setAttribute("leaveRequests", mailRequestDAO.getLeaveRequestsByDepartment(scope.getDepartmentId(), status));
        if (scope.getEmployee() != null) {
            request.setAttribute("userName", scope.getEmployee().getFullName());
            request.setAttribute("userPosition", scope.getEmployee().getPosition());
        }
        request.getRequestDispatcher("/Views/DeptManager/leaves.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DeptManagerScope scope = DeptManagerScope.from(request, employeeDAO);
        if (scope == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (!scope.hasDepartment() || scope.getApproverEmployeeId() <= 0) {
            response.sendRedirect(request.getContextPath() + "/dept");
            return;
        }

        int requestId = parseInt(request.getParameter("requestId"), -1);
        String decision = request.getParameter("decision");
        Map<String, Object> leaveSummary = mailRequestDAO.getLeaveRequestSummaryById(requestId);
        boolean success = requestId > 0
                && ("Approved".equals(decision) || "Rejected".equals(decision))
                && mailRequestDAO.updateLeaveStatusByDepartment(
                        requestId,
                        scope.getDepartmentId(),
                        decision,
                        scope.getApproverEmployeeId());

        if (success && leaveSummary != null) {
            Object employeeIdValue = leaveSummary.get("employeeId");
            int employeeId = employeeIdValue instanceof Number ? ((Number) employeeIdValue).intValue() : 0;
            Integer employeeUserId = notificationRecipientService.activeUserByEmployeeId(employeeId);
            if (employeeUserId != null) {
                notificationService.notifyLeaveDecisionForEmployee(
                        employeeUserId,
                        scope.getUser() != null ? scope.getUser().getUserId() : 0,
                        requestId,
                        decision
                );
            }
        }

        HttpSession session = request.getSession();
        session.setAttribute(success ? "deptLeaveSuccess" : "deptLeaveError",
                success ? "Đã cập nhật đơn nghỉ phép." : "Không thể cập nhật đơn này.");
        response.sendRedirect(request.getContextPath() + "/dept/leaves");
    }

    private void pullFlashMessages(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        Object success = session.getAttribute("deptLeaveSuccess");
        Object error = session.getAttribute("deptLeaveError");
        if (success != null) {
            request.setAttribute("deptLeaveSuccess", success);
            session.removeAttribute("deptLeaveSuccess");
        }
        if (error != null) {
            request.setAttribute("deptLeaveError", error);
            session.removeAttribute("deptLeaveError");
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
