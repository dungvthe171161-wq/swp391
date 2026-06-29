package com.hrm.controller.hr;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hrm.dao.PayrollDAO;
import com.hrm.dao.EmployeeDAO;
import com.hrm.model.entity.SystemUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.hrm.model.entity.Employee;
import com.hrm.util.PermissionUtil;

/**
 * Controller for Payroll Approval by HR Manager
 * Handles GET /hr/payroll-approval - Display payroll approval page
 * Handles POST /hr/payroll-approval - Approve/Reject payroll
 * Handles POST /hr/payroll-approval/batch-approve - Batch approve payrolls
 * Handles POST /hr/payroll-approval/batch-reject - Batch reject payrolls
 * @author admin
 */
@WebServlet(name = "PayrollApprovalController", urlPatterns = {
    "/hr/payroll-approval",
    "/hr/payroll-approval/batch-approve",
    "/hr/payroll-approval/batch-reject"
})
public class PayrollApprovalController extends HttpServlet {

    private static final String STATUS_PENDING = "Pending";
    private static final String REQUIRED_PERMISSION = "VIEW_USERS";
    
    private final PayrollDAO payrollDAO = new PayrollDAO();
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String statusParam = request.getParameter("status");
            String employeeFilter = request.getParameter("employeeFilter");
            String monthFilter = request.getParameter("payPeriod");
            String success = request.getParameter("success");
            String error = request.getParameter("error");

            String ajax = request.getParameter("ajax");
            if ("true".equalsIgnoreCase(ajax)) {
                handleAjaxDetails(request, response);
                return;
            }

            if (success != null && !success.trim().isEmpty()) {
                request.setAttribute("success", success);
            }
            if (error != null && !error.trim().isEmpty()) {
                request.setAttribute("error", error);
            }

            String statusForQuery;
            if (statusParam == null) {
                statusParam = STATUS_PENDING;
                statusForQuery = STATUS_PENDING;
            } else if (statusParam.trim().isEmpty() || "ALL".equalsIgnoreCase(statusParam)) {
                statusParam = "";
                statusForQuery = null;
            } else {
                statusForQuery = statusParam;
            }

            Integer employeeId = null;
            if (employeeFilter != null && !employeeFilter.trim().isEmpty()) {
                try {
                    employeeId = Integer.parseInt(employeeFilter);
                } catch (NumberFormatException ignored) {
                }
            }

            int pageSize = 10;
            int currentPage = 1;
            try {
                String pageParam = request.getParameter("page");
                if (pageParam != null && !pageParam.isBlank()) {
                    currentPage = Math.max(1, Integer.parseInt(pageParam));
                }
            } catch (NumberFormatException ignored) {
                currentPage = 1;
            }

            try {
                String pageSizeParam = request.getParameter("pageSize");
                if (pageSizeParam != null && !pageSizeParam.isBlank()) {
                    pageSize = Math.min(50, Math.max(5, Integer.parseInt(pageSizeParam)));
                }
            } catch (NumberFormatException ignored) {
                pageSize = 10;
            }

            String sortBy = request.getParameter("sortBy");
            String sortOrder = request.getParameter("sortOrder");
            if (sortBy == null || sortBy.isBlank()) {
                sortBy = "PayPeriod";
            }
            if (sortOrder == null || sortOrder.isBlank()) {
                sortOrder = "DESC";
            }

            int totalCount = payrollDAO.getTotalPayrollCount(employeeId, statusForQuery, monthFilter);
            int totalPages = totalCount == 0 ? 1 : (int) Math.ceil(totalCount / (double) pageSize);
            if (currentPage > totalPages) {
                currentPage = totalPages;
            }
            int offset = (currentPage - 1) * pageSize;

            List<Map<String, Object>> payrolls = payrollDAO.getPagedPayrolls(
                    offset, pageSize, employeeId, statusForQuery, sortBy, sortOrder, monthFilter);
            List<Employee> employees = employeeDAO.getAll();

            int pendingCount = payrollDAO.getTotalPayrollCount(null, STATUS_PENDING, monthFilter);
            int approvedCount = payrollDAO.getTotalPayrollCount(null, "Approved", monthFilter);
            int rejectedCount = payrollDAO.getTotalPayrollCount(null, "Rejected", monthFilter);
            int paidCount = payrollDAO.getTotalPayrollCount(null, "Paid", monthFilter);

            request.setAttribute("payrolls", payrolls);
            request.setAttribute("employees", employees);
            request.setAttribute("statusFilter", statusParam);
            request.setAttribute("employeeFilter", employeeFilter);
            request.setAttribute("payPeriodFilter", monthFilter);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("totalCount", totalCount);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("sortBy", sortBy);
            request.setAttribute("sortOrder", sortOrder);
            request.setAttribute("pendingCount", pendingCount);
            request.setAttribute("approvedCount", approvedCount);
            request.setAttribute("rejectedCount", rejectedCount);
            request.setAttribute("paidCount", paidCount);

            request.getRequestDispatcher("/Views/hr/PayrollManagement.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading payroll list: " + e.getMessage());
            request.getRequestDispatcher("/Views/hr/PayrollManagement.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check authentication
        HttpSession session = request.getSession();
        SystemUser currentUser = (SystemUser) session.getAttribute("systemUser");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/Views/Login.jsp");
            return;
        }
        
        // Check if this is a batch operation
        String servletPath = request.getServletPath();
        if (servletPath != null && servletPath.contains("/batch-approve")) {
            handleBatchApprove(request, response);
            return;
        } else if (servletPath != null && servletPath.contains("/batch-reject")) {
            handleBatchReject(request, response);
            return;
        }
        
        try {
            String action = request.getParameter("action");
            String payrollIdStr = request.getParameter("payrollId");
            
            String currentStatus = request.getParameter("status");
            if (currentStatus == null || currentStatus.trim().isEmpty()) {
                currentStatus = STATUS_PENDING;
            }
            String employeeFilter = request.getParameter("employeeFilter");
            String monthFilter = request.getParameter("payPeriod");
            if (monthFilter == null || monthFilter.isBlank()) {
                monthFilter = request.getParameter("monthFilter");
            }

            if (payrollIdStr == null || payrollIdStr.trim().isEmpty()) {
                redirectToPayrollPage(request, response, currentStatus, employeeFilter, monthFilter,
                        "error", "Payroll ID is required");
                return;
            }
            
            int payrollId = Integer.parseInt(payrollIdStr);
            
            // Get payroll to verify it exists and is in correct status
            com.hrm.model.entity.Payroll payroll = payrollDAO.getById(payrollId);
            if (payroll == null) {
                redirectToPayrollPage(request, response, currentStatus, employeeFilter, monthFilter,
                        "error", "Payroll not found");
                return;
            }
            
            // Get current user ID (from employeeId if available, otherwise userId)
            Integer approvedBy = currentUser.getEmployeeId();
            if (approvedBy == null) {
                // If no employeeId, use userId as fallback
                approvedBy = currentUser.getUserId();
            }
            
            if ("approve".equals(action)) {
                // Verify payroll is in Pending status
                if (!STATUS_PENDING.equals(payroll.getStatus())) {
                    redirectToPayrollPage(request, response, currentStatus, employeeFilter, monthFilter,
                            "error", "Payroll is not in Pending status. Current status: " + payroll.getStatus());
                    return;
                }
                
                // Approve payroll
                boolean success = payrollDAO.approvePayroll(payrollId, approvedBy, LocalDate.now());
                
                if (success) {
                    redirectToPayrollPage(request, response, "Approved", employeeFilter, monthFilter,
                            "success", "Payroll approved successfully!");
                } else {
                    redirectToPayrollPage(request, response, currentStatus, employeeFilter, monthFilter,
                            "error", "Unable to approve payroll. Please try again.");
                }
                
            } else if ("reject".equals(action)) {
                // Verify payroll is in Pending status
                if (!STATUS_PENDING.equals(payroll.getStatus())) {
                    redirectToPayrollPage(request, response, currentStatus, employeeFilter, monthFilter,
                            "error", "Payroll is not in Pending status. Current status: " + payroll.getStatus());
                    return;
                }
                
                // Get rejection note
                String rejectNote = request.getParameter("rejectNote");
                if (rejectNote != null) {
                    rejectNote = rejectNote.trim();
                }
                
                // Reject payroll with note
                boolean success = payrollDAO.rejectPayroll(payrollId, approvedBy, LocalDate.now(), rejectNote);
                
                if (success) {
                    redirectToPayrollPage(request, response, "Rejected", employeeFilter, monthFilter,
                            "success", "Payroll rejected successfully!");
                } else {
                    redirectToPayrollPage(request, response, currentStatus, employeeFilter, monthFilter,
                            "error", "Unable to reject payroll. Please try again.");
                }
                
            } else {
                redirectToPayrollPage(request, response, currentStatus, employeeFilter, monthFilter,
                        "error", "Invalid action");
            }
            
        } catch (NumberFormatException e) {
            redirectToPayrollPage(request, response, STATUS_PENDING, null, null,
                    "error", "Invalid payroll ID");
        } catch (Exception e) {
            e.printStackTrace();
            redirectToPayrollPage(request, response, STATUS_PENDING, null, null,
                    "error", "Error: " + e.getMessage());
        }
    }
    
    private void handleAjaxDetails(HttpServletRequest request,
                                   HttpServletResponse response) throws IOException {
        if (!PermissionUtil.ensureRolePermissionJson(
                request,
                response,
                PermissionUtil.ROLE_HR_MANAGER,
                REQUIRED_PERMISSION,
                "This section is restricted to HR Manager.",
                "You do not have permission to view payroll details.")) {
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String payrollIdStr = request.getParameter("payrollId");
            if (payrollIdStr == null || payrollIdStr.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Missing payroll ID\"}");
                return;
            }

            int payrollId = Integer.parseInt(payrollIdStr);
            Map<String, Object> details = payrollDAO.getDetailsById(payrollId);

            if (details == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Payroll not found\"}");
                return;
            }

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd")
                    .create();
            response.getWriter().write(gson.toJson(details));

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid payroll ID\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error retrieving payroll details: " + e.getMessage().replace("\"", "\\\"") + "\"}");
        }
    }

    /**
     * Handle batch approve for multiple payrolls
     */
    private void handleBatchApprove(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        SystemUser currentUser = (SystemUser) session.getAttribute("systemUser");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/Views/Login.jsp");
            return;
        }
        
        try {
            String[] payrollIdStrs = request.getParameterValues("payrollIds");
            if (payrollIdStrs == null || payrollIdStrs.length == 0) {
                redirectToPayrollPage(request, response, STATUS_PENDING, null, null,
                        "error", "No payroll IDs provided");
                return;
            }
            
            List<Integer> payrollIds = new ArrayList<>();
            for (String idStr : payrollIdStrs) {
                try {
                    payrollIds.add(Integer.parseInt(idStr.trim()));
                } catch (NumberFormatException e) {
                    // Skip invalid IDs
                }
            }
            
            if (payrollIds.isEmpty()) {
                redirectToPayrollPage(request, response, STATUS_PENDING, null, null,
                        "error", "No valid payroll IDs provided");
                return;
            }
            
            // Get current user ID
            Integer approvedBy = currentUser.getEmployeeId();
            if (approvedBy == null) {
                approvedBy = currentUser.getUserId();
            }
            
            int successCount = 0;
            int failCount = 0;
            int skippedCount = 0;
            
            for (Integer payrollId : payrollIds) {
                com.hrm.model.entity.Payroll payroll = payrollDAO.getById(payrollId);
                if (payroll == null) {
                    skippedCount++;
                    continue;
                }
                
                // Only allow approving Pending payrolls
                if (!STATUS_PENDING.equals(payroll.getStatus())) {
                    skippedCount++;
                    continue;
                }
                
                boolean success = payrollDAO.approvePayroll(payrollId, approvedBy, LocalDate.now());
                if (success) {
                    successCount++;
                } else {
                    failCount++;
                }
            }
            
            // Build result message
            StringBuilder message = new StringBuilder();
            if (successCount > 0) {
                message.append(successCount).append(" payroll(s) approved successfully. ");
            }
            if (failCount > 0) {
                message.append(failCount).append(" payroll(s) failed to approve. ");
            }
            if (skippedCount > 0) {
                message.append(skippedCount).append(" payroll(s) skipped (not in Pending status). ");
            }
            
            String currentStatus = request.getParameter("status");
            if (currentStatus == null || currentStatus.trim().isEmpty()) {
                currentStatus = STATUS_PENDING;
            }
            String employeeFilter = request.getParameter("employeeFilter");
            String monthFilter = request.getParameter("payPeriod");
            
            if (successCount > 0) {
                redirectToPayrollPage(request, response, "Approved", employeeFilter, monthFilter,
                        "success", message.toString().trim());
            } else {
                redirectToPayrollPage(request, response, currentStatus, employeeFilter, monthFilter,
                        "error", message.toString().trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectToPayrollPage(request, response, STATUS_PENDING, null, null,
                    "error", "Error batch approving payrolls: " + e.getMessage());
        }
    }
    
    /**
     * Handle batch reject for multiple payrolls
     */
    private void handleBatchReject(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        SystemUser currentUser = (SystemUser) session.getAttribute("systemUser");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/Views/Login.jsp");
            return;
        }
        
        try {
            String[] payrollIdStrs = request.getParameterValues("payrollIds");
            if (payrollIdStrs == null || payrollIdStrs.length == 0) {
                redirectToPayrollPage(request, response, STATUS_PENDING, null, null,
                        "error", "No payroll IDs provided");
                return;
            }
            
            List<Integer> payrollIds = new ArrayList<>();
            for (String idStr : payrollIdStrs) {
                try {
                    payrollIds.add(Integer.parseInt(idStr.trim()));
                } catch (NumberFormatException e) {
                    // Skip invalid IDs
                }
            }
            
            if (payrollIds.isEmpty()) {
                redirectToPayrollPage(request, response, STATUS_PENDING, null, null,
                        "error", "No valid payroll IDs provided");
                return;
            }
            
            // Get rejection note
            String rejectNote = request.getParameter("rejectNote");
            if (rejectNote == null) {
                rejectNote = "";
            } else {
                rejectNote = rejectNote.trim();
            }
            
            if (rejectNote.isEmpty()) {
                redirectToPayrollPage(request, response, STATUS_PENDING, null, null,
                        "error", "Rejection reason is required");
                return;
            }
            
            // Get current user ID
            Integer approvedBy = currentUser.getEmployeeId();
            if (approvedBy == null) {
                approvedBy = currentUser.getUserId();
            }
            
            int successCount = 0;
            int failCount = 0;
            int skippedCount = 0;
            
            for (Integer payrollId : payrollIds) {
                com.hrm.model.entity.Payroll payroll = payrollDAO.getById(payrollId);
                if (payroll == null) {
                    skippedCount++;
                    continue;
                }
                
                // Only allow rejecting Pending payrolls
                if (!STATUS_PENDING.equals(payroll.getStatus())) {
                    skippedCount++;
                    continue;
                }
                
                boolean success = payrollDAO.rejectPayroll(payrollId, approvedBy, LocalDate.now(), rejectNote);
                if (success) {
                    successCount++;
                } else {
                    failCount++;
                }
            }
            
            // Build result message
            StringBuilder message = new StringBuilder();
            if (successCount > 0) {
                message.append(successCount).append(" payroll(s) rejected successfully. ");
            }
            if (failCount > 0) {
                message.append(failCount).append(" payroll(s) failed to reject. ");
            }
            if (skippedCount > 0) {
                message.append(skippedCount).append(" payroll(s) skipped (not in Pending status). ");
            }
            
            String currentStatus = request.getParameter("status");
            if (currentStatus == null || currentStatus.trim().isEmpty()) {
                currentStatus = STATUS_PENDING;
            }
            String employeeFilter = request.getParameter("employeeFilter");
            String monthFilter = request.getParameter("payPeriod");
            
            if (successCount > 0) {
                redirectToPayrollPage(request, response, "Rejected", employeeFilter, monthFilter,
                        "success", message.toString().trim());
            } else {
                redirectToPayrollPage(request, response, currentStatus, employeeFilter, monthFilter,
                        "error", message.toString().trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectToPayrollPage(request, response, STATUS_PENDING, null, null,
                    "error", "Error batch rejecting payrolls: " + e.getMessage());
        }
    }
    
    private void redirectToPayrollPage(HttpServletRequest request,
                                     HttpServletResponse response,
                                     String status,
                                     String employeeFilter,
                                     String monthFilter,
                                     String messageType,
                                     String message) throws IOException {
        StringBuilder redirectUrl = new StringBuilder(request.getContextPath())
                .append("/hr/payroll-approval");

        List<String> params = new ArrayList<>();
        if (status != null && !status.isBlank()) {
            params.add("status=" + URLEncoder.encode(status, StandardCharsets.UTF_8));
        }
        if (employeeFilter != null && !employeeFilter.isBlank()) {
            params.add("employeeFilter=" + URLEncoder.encode(employeeFilter, StandardCharsets.UTF_8));
        }
        if (monthFilter != null && !monthFilter.isBlank()) {
            params.add("payPeriod=" + URLEncoder.encode(monthFilter, StandardCharsets.UTF_8));
        }
        if (messageType != null && message != null && !message.isBlank()) {
            params.add(messageType + "=" + URLEncoder.encode(message, StandardCharsets.UTF_8));
        }

        if (!params.isEmpty()) {
            redirectUrl.append("?").append(String.join("&", params));
        }

        response.sendRedirect(redirectUrl.toString());
    }
}


