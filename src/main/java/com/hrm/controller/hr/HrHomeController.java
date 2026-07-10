/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.hrm.controller.hr;

import com.hrm.dao.DepartmentDAO;
import com.hrm.dao.EmployeeDAO;
import com.hrm.dao.PayrollDAO;
import com.hrm.model.entity.Department;
import com.hrm.model.entity.Employee;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private final PayrollDAO payrollDAO = new PayrollDAO();
   
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
            
            List<Employee> employees = employeeDAO.getAll();
            System.out.println("HrHomeController: Loaded " + employees.size() + " employees");
            
            List<Department> departments = departmentDAO.getAll();
            System.out.println("HrHomeController: Loaded " + departments.size() + " departments");
            
            String section = request.getParameter("section");
            String payrollStatus = request.getParameter("payrollStatus");
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
