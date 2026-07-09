/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.hrm.controller.hr;

import com.hrm.dao.EmployeeDAO;
import com.hrm.dao.DepartmentDAO;
import com.hrm.dao.SystemUserDAO;
import com.hrm.model.entity.Employee;
import com.hrm.model.entity.Department;
import com.hrm.model.entity.SystemUser;
import com.hrm.util.PermissionUtil;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author admin
 */
@WebServlet(name="EmployeeListController", urlPatterns={"/hr/employee-list"})
public class EmployeeListController extends HttpServlet {
   
    private static final String REQUIRED_PERMISSION = "VIEW_EMPLOYEES";
    private static final String DENIED_MESSAGE = "You do not have permission to view or manage employee records.";
    
    private final transient EmployeeDAO employeeDAO = new EmployeeDAO();
    private final transient DepartmentDAO departmentDAO = new DepartmentDAO();
    private final transient SystemUserDAO systemUserDAO = new SystemUserDAO();
    
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if EmployeeListController servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        if (!ensureAccess(request, response)) {
            return;
        }
        HttpSession session = request.getSession();
        try {
            // Fixed page size
            final int pageSize = 5;
            
            // Get search parameter - only search by name and position
            String searchKeyword = request.getParameter("search");
            
            // Get current page number
            int page = 1;
            try {
                String pageParam = request.getParameter("page");
                if (pageParam != null && !pageParam.trim().isEmpty()) {
                    page = Integer.parseInt(pageParam);
                    if (page < 1) page = 1;
                }
            } catch (NumberFormatException e) {
                // Use default page = 1
            }
            
            // Get all employees (filtered if search keyword exists) - for statistics
            List<Employee> allEmployees;
            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                allEmployees = getFilteredEmployees(searchKeyword);
            } else {
                allEmployees = employeeDAO.getAll();
            }
            
            // Get total count for pagination
            int totalCount = allEmployees.size();
            
            // Calculate pagination
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);
            if (page > totalPages && totalPages > 0) {
                page = totalPages;
            }
            
            // Calculate offset
            int offset = (page - 1) * pageSize;
            
            // Get employees for current page only (for table display)
            List<Employee> employees = allEmployees.stream()
                .skip(offset)
                .limit(pageSize)
                .toList();
            
            // Get departments (might be needed for other purposes, but not for filtering)
            List<Department> departments = departmentDAO.getAll();
            
            // Set attributes
            request.setAttribute("employees", employees); // Only 5 employees for current page
            request.setAttribute("allEmployeesForStats", allEmployees); // All employees for statistics
            request.setAttribute("departments", departments);
            request.setAttribute("currentPage", page);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalCount", totalCount);
            request.setAttribute("searchKeyword", searchKeyword != null ? searchKeyword : "");
            
            // Check for success/error messages in session
          
            if (session != null) {
                String successMessage = (String) session.getAttribute("success");
                String errorMessage = (String) session.getAttribute("error");
                
                if (successMessage != null) {
                    request.setAttribute("success", successMessage);
                    session.removeAttribute("success");
                }
                if (errorMessage != null) {
                    request.setAttribute("error", errorMessage);
                    session.removeAttribute("error");
                }
            }
            
            // Forward to EmployeeList.jsp
            request.getRequestDispatcher("/Views/hr/EmployeeList.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading employee list: " + e.getMessage());
            request.getRequestDispatcher("/Views/hr/EmployeeList.jsp").forward(request, response);
        }
    }
    
    /**
     * Filter employees by name and position only
     */
    private List<Employee> getFilteredEmployees(String searchKeyword) {
        List<Employee> allEmployees = employeeDAO.getAll();
        String keyword = searchKeyword.trim().toLowerCase();
        
        // Filter by name and position only
        return allEmployees.stream()
            .filter(emp -> 
                    (emp.getFullName() != null && emp.getFullName().toLowerCase().contains(keyword)) ||
                    (emp.getPosition() != null && emp.getPosition().toLowerCase().contains(keyword)))
            .toList();
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if EmployeeListController servlet-specific error occurs
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
     * @throws ServletException if EmployeeListController servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        if (!ensureAccess(request, response)) {
            return;
        }
        try {
            String action = request.getParameter("action");
            System.out.println("POST request received with action: " + action);
            
            if ("updateStatus".equals(action)) {
                handleStatusUpdate(request, response);
            } else if ("editEmployee".equals(action)) {
                handleEmployeeEdit(request, response);
            } else if ("deleteEmployee".equals(action)) {
                handleEmployeeDelete(request, response);
            } else {
                System.out.println("Unknown action: " + action);
                processRequest(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            processRequest(request, response);
        }
    }
    
    private void handleStatusUpdate(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession session = request.getSession();
        try {
            int employeeId = Integer.parseInt(request.getParameter("employeeId"));
            String newStatus = request.getParameter("status");
            
            System.out.println("Updating status for employee ID: " + employeeId + " to: " + newStatus);
            boolean success = employeeDAO.updateEmployeeStatus(employeeId, newStatus);
            
            if (success) {
                session.setAttribute("success", "Employee status updated successfully!");
                System.out.println("Status update successful");
            } else {
                session.setAttribute("error", "Failed to update employee status!");
                System.out.println("Status update failed");
            }
            
            response.sendRedirect(request.getContextPath() + "/hr/employee-list");
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Error updating employee status: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/hr/employee-list");
        }
    }
    
    private void handleEmployeeEdit(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession session = request.getSession();
        try {
            String employeeIdStr = request.getParameter("employeeId");
            System.out.println("Received employeeId parameter: " + employeeIdStr);
            
            if (employeeIdStr == null || employeeIdStr.trim().isEmpty()) {
                session.setAttribute("error", "Employee ID is missing!");
                response.sendRedirect(request.getContextPath() + "/hr/employee-list");
                return;
            }
            
            int employeeId = Integer.parseInt(employeeIdStr);
            System.out.println("Parsed employeeId: " + employeeId);
            
            Employee employee = employeeDAO.getById(employeeId);
            System.out.println("Found employee: " + (employee != null ? employee.getFullName() : "null"));
            
            if (employee != null) {
                // Update employee information (excluding position since it's readonly)
                employee.setFullName(request.getParameter("fullName"));
                employee.setEmail(request.getParameter("email"));
                employee.setPhone(request.getParameter("phone"));
                
                // Handle start and end dates
                String startDate = request.getParameter("startDate");
                String endDate = request.getParameter("endDate");
                String employmentPeriod = "";
                if (startDate != null && !startDate.trim().isEmpty()) {
                    employmentPeriod = startDate;
                    if (endDate != null && !endDate.trim().isEmpty()) {
                        employmentPeriod += " - " + endDate;
                    }
                }
                employee.setEmploymentPeriod(employmentPeriod);
                
                System.out.println("Updating employee with:");
                System.out.println("FullName: " + employee.getFullName());
                System.out.println("Email: " + employee.getEmail());
                System.out.println("Phone: " + employee.getPhone());
                System.out.println("StartDate: " + startDate);
                System.out.println("EndDate: " + endDate);
                System.out.println("EmploymentPeriod: " + employee.getEmploymentPeriod());
                System.out.println("Position (unchanged): " + employee.getPosition());
                
                boolean success = employeeDAO.updateEmployeeInfo(employee);
                System.out.println("EmployeeDAO.updateEmployeeInfo result: " + success);
                
                if (success) {
                    session.setAttribute("success", "Employee information updated successfully!");
                    System.out.println("Success message set in session");
                } else {
                    session.setAttribute("error", "Failed to update employee information!");
                    System.out.println("Error message set in session");
                }
            } else {
                session.setAttribute("error", "Employee not found!");
                System.out.println("Employee not found for ID: " + employeeId);
            }
            
            response.sendRedirect(request.getContextPath() + "/hr/employee-list");
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Error updating employee: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/hr/employee-list");
        }
    }
    
    private void handleEmployeeDelete(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession session = request.getSession();
        try {
            String employeeIdStr = request.getParameter("employeeId");
            
            if (employeeIdStr == null || employeeIdStr.trim().isEmpty()) {
                session.setAttribute("error", "Employee ID is missing!");
                response.sendRedirect(request.getContextPath() + "/hr/employee-list");
                return;
            }
            
            int employeeId = Integer.parseInt(employeeIdStr);
            
            // Get employee to get name for success message
            Employee employee = employeeDAO.getById(employeeId);
            String employeeName = employee != null ? employee.getFullName() : "Employee";
            
            // First, delete SystemUser if exists (to maintain referential integrity)
            SystemUser systemUser = systemUserDAO.getByEmployeeId(employeeId);
            if (systemUser != null) {
                boolean userDeleted = systemUserDAO.delete(systemUser.getUserId());
                if (!userDeleted) {
                    System.out.println("Warning: Failed to delete SystemUser for employee ID: " + employeeId);
                }
            }
            
            // Then delete the employee
            boolean success = employeeDAO.delete(employeeId);
            
            if (success) {
                session.setAttribute("success", "Employee '" + employeeName + "' deleted successfully!");
            } else {
                session.setAttribute("error", "Failed to delete employee!");
            }
            
            response.sendRedirect(request.getContextPath() + "/hr/employee-list");
            
        } catch (NumberFormatException e) {
            session.setAttribute("error", "Invalid employee ID format!");
            response.sendRedirect(request.getContextPath() + "/hr/employee-list");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Error deleting employee: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/hr/employee-list");
        }
    }

    /** 
     * Returns EmployeeListController short description of the servlet.
     * @return EmployeeListController String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Employee List Controller";
    }// </editor-fold>

    private boolean ensureAccess(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        return PermissionUtil.ensurePermission(request, response, REQUIRED_PERMISSION, DENIED_MESSAGE);
    }

}
