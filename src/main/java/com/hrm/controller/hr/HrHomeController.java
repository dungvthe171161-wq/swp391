/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.hrm.controller.hr;

import com.hrm.dao.DepartmentDAO;
import com.hrm.dao.EmployeeDAO;
import com.hrm.dao.MailRequestDAO;
import com.hrm.dao.PayrollDAO;
import com.hrm.model.entity.Department;
import com.hrm.model.entity.Employee;
import com.hrm.model.entity.SystemUser;
import com.hrm.service.NotificationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author admin
 */
@WebServlet(name="HrHomeController", urlPatterns={"/HrHomeController"})
public class HrHomeController extends HttpServlet {
    
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final MailRequestDAO mailRequestDAO = new MailRequestDAO();
    private final PayrollDAO payrollDAO = new PayrollDAO();
    private final NotificationService notificationService = new NotificationService();
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        try {
            System.out.println("HrHomeController: Starting processRequest...");
            populateNotificationAttributes(request);
            
            List<Employee> employees = employeeDAO.getAll();
            System.out.println("HrHomeController: Loaded " + employees.size() + " employees");
            
            List<Department> departments = departmentDAO.getAll();
            System.out.println("HrHomeController: Loaded " + departments.size() + " departments");
            
            String section = request.getParameter("section");
            String payrollStatus = request.getParameter("payrollStatus");
            String requestStatus = request.getParameter("requestStatus");
            String employeeFilter = request.getParameter("employeeFilter");
            String monthFilter = request.getParameter("monthFilter");
            String successMessage = request.getParameter("success");
            String errorMessage = request.getParameter("error");
            
            if ("payroll-management".equals(section) || payrollStatus != null) {
                int pendingCount = payrollDAO.getTotalPayrollCount(null, "Pending");
                int approvedCount = payrollDAO.getTotalPayrollCount(null, "Approved");
                int rejectedCount = payrollDAO.getTotalPayrollCount(null, "Rejected");
                int paidCount = payrollDAO.getTotalPayrollCount(null, "Paid");

                if (payrollStatus == null || payrollStatus.trim().isEmpty()) {
                    payrollStatus = "Pending";
                }
                
                Integer employeeId = null;
                if (employeeFilter != null && !employeeFilter.trim().isEmpty()) {
                    try {
                        employeeId = Integer.parseInt(employeeFilter);
                    } catch (NumberFormatException e) {
                        System.out.println("HrHomeController: Invalid employee filter provided: " + employeeFilter);
                    }
                }
                
                List<Map<String, Object>> payrolls = payrollDAO.getAll(employeeId, payrollStatus);
                
                if (monthFilter != null && !monthFilter.trim().isEmpty()) {
                    payrolls.removeIf(p -> !monthFilter.equals(p.get("payPeriod")));
                }
                
                request.setAttribute("payrolls", payrolls);
                request.setAttribute("payrollStatus", payrollStatus);
                request.setAttribute("payrollEmployeeFilter", employeeFilter);
                request.setAttribute("payrollMonthFilter", monthFilter);
                request.setAttribute("pendingCount", pendingCount);
                request.setAttribute("approvedCount", approvedCount);
                request.setAttribute("rejectedCount", rejectedCount);
                request.setAttribute("paidCount", paidCount);
            } else {
                request.setAttribute("payrolls", new java.util.ArrayList<>());
                request.setAttribute("payrollStatus", "Pending");
                request.setAttribute("payrollEmployeeFilter", "");
                request.setAttribute("payrollMonthFilter", "");
            }
            
            populateRequestApprovalAttributes(request, requestStatus);
            pullRequestFlashMessages(request);
            request.setAttribute("employees", employees);
            request.setAttribute("departments", departments);
            request.setAttribute("section", section != null ? section : "hr-home");
            request.setAttribute("controllerMessage", "HrHomeController executed successfully!");
            if (successMessage != null && !successMessage.isBlank()) {
                request.setAttribute("successMessage", successMessage);
            }
            if (errorMessage != null && !errorMessage.isBlank()) {
                request.setAttribute("errorMessage", errorMessage);
            }
            
            System.out.println("HrHomeController: Forwarding to HrHome.jsp");
            request.getRequestDispatcher("/Views/hr/HrHome.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("HrHomeController: Error occurred: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error loading HR dashboard data: " + e.getMessage());
            request.getRequestDispatcher("/Views/hr/HrHome.jsp").forward(request, response);
        }
    } 

    private void populateNotificationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        SystemUser systemUser = session != null ? (SystemUser) session.getAttribute("systemUser") : null;
        if (systemUser == null || systemUser.getUserId() <= 0) {
            request.setAttribute("appNotificationCount", "0");
            request.setAttribute("appNotifications", java.util.Collections.emptyList());
            return;
        }
        request.setAttribute("appNotificationCount",
                String.valueOf(notificationService.unreadCount(systemUser.getUserId())));
        request.setAttribute("appNotifications",
                notificationService.recentForUser(systemUser.getUserId(), 5));
    }


    private void populateRequestApprovalAttributes(HttpServletRequest request, String requestedStatus) {
        String status = normalizeRequestStatus(requestedStatus);
        request.setAttribute("requestStatus", status);
        request.setAttribute("mailRequests", mailRequestDAO.getAllRequests(status));
        request.setAttribute("requestPendingCount", mailRequestDAO.countRequestsByStatus("Pending"));
        request.setAttribute("requestApprovedCount", mailRequestDAO.countRequestsByStatus("Approved"));
        request.setAttribute("requestRejectedCount", mailRequestDAO.countRequestsByStatus("Rejected"));
    }

    private void handleRequestDecision(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        SystemUser currentUser = session != null ? (SystemUser) session.getAttribute("systemUser") : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int requestId = parseInt(request.getParameter("requestId"), -1);
        String decision = request.getParameter("decision");
        boolean validDecision = "Approved".equals(decision) || "Rejected".equals(decision);
        boolean success = requestId > 0
                && validDecision
                && mailRequestDAO.updateRequestStatus(requestId, decision, resolveApproverId(currentUser));

        session.setAttribute(success ? "hrRequestSuccess" : "hrRequestError",
                success
                        ? ("Approved".equals(decision) ? "Da duyet yeu cau moi nhat." : "Da tu choi yeu cau.")
                        : "Khong the cap nhat yeu cau nay. Co the yeu cau da duoc xu ly truoc do.");

        String status = normalizeRequestStatus(request.getParameter("requestStatus"));
        response.sendRedirect(request.getContextPath()
                + "/HrHomeController?section=requests-approval&requestStatus=" + status);
    }

    private void pullRequestFlashMessages(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        Object success = session.getAttribute("hrRequestSuccess");
        Object error = session.getAttribute("hrRequestError");
        if (success != null) {
            request.setAttribute("hrRequestSuccess", success);
            session.removeAttribute("hrRequestSuccess");
        }
        if (error != null) {
            request.setAttribute("hrRequestError", error);
            session.removeAttribute("hrRequestError");
        }
    }

    private Integer resolveApproverId(SystemUser currentUser) {
        if (currentUser.getEmployeeId() != null) {
            return currentUser.getEmployeeId();
        }
        if (currentUser.getEmployee() != null) {
            return currentUser.getEmployee().getEmployeeId();
        }
        return null;
    }

    private String normalizeRequestStatus(String status) {
        if ("Approved".equals(status) || "Rejected".equals(status)) {
            return status;
        }
        return "Pending";
    }

    private int parseInt(String value, int fallback) {
        try {
            return value == null || value.isBlank() ? fallback : Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        if ("updateRequestStatus".equals(request.getParameter("action"))) {
            handleRequestDecision(request, response);
            return;
        }
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
