/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.hrm.controller.hr;

import com.hrm.dao.DAO;
import com.hrm.dao.DepartmentDAO;
import com.hrm.dao.EmployeeDAO;
import com.hrm.dao.GuestDAO;
import com.hrm.model.entity.Department;
import com.hrm.model.entity.Employee;
import com.hrm.model.entity.Guest;
import com.hrm.model.entity.SystemUser;
import com.hrm.util.PermissionUtil;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author admin
 */
@WebServlet(name="CreateEmployeeController", urlPatterns={"/hr/create-employee"})
public class CreateEmployeeController extends HttpServlet {
   
    private static final String CREATE_EMPLOYEE_JSP = "/Views/hr/CreateEmployee.jsp";
    private static final String ERROR_ATTRIBUTE = "error";
    private static final String REQUIRED_PERMISSION = "VIEW_EMPLOYEES";
    private static final String DENIED_MESSAGE = "You do not have permission to create employees.";
    
    private final transient DAO dao = DAO.getInstance();
    private final transient DepartmentDAO departmentDAO = new DepartmentDAO();
    private final transient EmployeeDAO employeeDAO = new EmployeeDAO();
    private final transient GuestDAO guestDAO = new GuestDAO();

    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if CreateEmployeeController servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        if (!ensureAccess(request, response)) {
            return;
        }
        try {
            List<Guest> hiredGuests = guestDAO.findOfferAcceptedGuestsReadyForEmployee();
            for (Guest guest : hiredGuests) {
                guest.setStatus("Offer Accepted");
            }
            
            // Get all departments for the dropdown, excluding Human Resources
            // (This page is for creating regular employees, not HR staff)
            List<Department> allDepartments = departmentDAO.getAll();
            List<Department> departments = new ArrayList<>();
            for (Department dept : allDepartments) {
                // Exclude Human Resources department
                if (dept.getDeptName() != null && !dept.getDeptName().equalsIgnoreCase("Human Resources")) {
                    departments.add(dept);
                }
            }
            
            // Set attributes for JSP
            request.setAttribute("guests", hiredGuests);
            request.setAttribute("departments", departments);
            
            // Forward to the create employee page
            request.getRequestDispatcher(CREATE_EMPLOYEE_JSP).forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute(ERROR_ATTRIBUTE, "Error loading create employee page: " + e.getMessage());
            try {
                request.getRequestDispatcher(CREATE_EMPLOYEE_JSP).forward(request, response);
            } catch (ServletException | IOException ex) {
                ex.printStackTrace();
            }
        }
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if CreateEmployeeController servlet-specific error occurs
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
            
            if ("create".equals(action)) {
                createEmployee(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/hr/create-employee");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute(ERROR_ATTRIBUTE, "Error processing request: " + e.getMessage());
            try {
                request.getRequestDispatcher(CREATE_EMPLOYEE_JSP).forward(request, response);
            } catch (ServletException | IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private void createEmployee(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Get form parameters
            String guestIdStr = request.getParameter("guestId");
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String gender = request.getParameter("gender");
            String dobStr = request.getParameter("dob");
            String address = request.getParameter("address");
            String departmentIdStr = request.getParameter("departmentId");
            String position = request.getParameter("position");
            String status = "Probation";
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String hireDateStr = request.getParameter("hireDate");
            String endDateStr = request.getParameter("endDate");
            
            // Validate required fields. Username/password are only required when the candidate has no existing account.
            if (guestIdStr == null || guestIdStr.trim().isEmpty() ||
                fullName == null || fullName.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                departmentIdStr == null || departmentIdStr.trim().isEmpty() ||
                hireDateStr == null || hireDateStr.trim().isEmpty()) {
                
                request.setAttribute(ERROR_ATTRIBUTE, "Please fill in all required fields.");
                doGet(request, response);
                return;
            }
            
            // Parse and validate data
            int guestId = Integer.parseInt(guestIdStr);
            int departmentId = Integer.parseInt(departmentIdStr);
            LocalDate dob = dobStr != null && !dobStr.trim().isEmpty() ? LocalDate.parse(dobStr) : null;
            LocalDate hireDate = LocalDate.parse(hireDateStr); // Required field, already validated
            LocalDate endDate = endDateStr != null && !endDateStr.trim().isEmpty() ? LocalDate.parse(endDateStr) : hireDate.plusDays(7);

            if (!guestDAO.isOfferAcceptedGuestReadyForEmployee(guestId)) {
                request.setAttribute(ERROR_ATTRIBUTE,
                        "á»¨ng viÃªn chá»‰ Ä‘Æ°á»£c táº¡o nhÃ¢n viÃªn sau khi Ä‘Ã£ pass phá»ng váº¥n, nháº­n offer vÃ  cháº¥p nháº­n offer.");
                doGet(request, response);
                return;
            }

            Guest selectedGuest = guestDAO.findOfferAcceptedGuestReadyForEmployeeByGuestId(guestId);
            if (selectedGuest == null) {
                request.setAttribute(ERROR_ATTRIBUTE, "KhÃ´ng tÃ¬m tháº¥y á»©ng viÃªn guest Ä‘Ã£ chá»n.");
                doGet(request, response);
                return;
            }
            if (fullName == null || fullName.isBlank()) {
                fullName = selectedGuest.getFullName();
            }
            if (email == null || email.isBlank()) {
                email = selectedGuest.getEmail();
            }
            if (phone == null || phone.isBlank()) {
                phone = selectedGuest.getPhone();
            }
            if (dob == null && selectedGuest.getDateOfBirth() != null) {
                dob = selectedGuest.getDateOfBirth();
            }
            if ((address == null || address.isBlank())
                    && selectedGuest.getAddress() != null
                    && !selectedGuest.getAddress().isBlank()) {
                address = selectedGuest.getAddress();
            }
            
            // Validate end date is after start date if both are provided
            if (endDate != null && endDate.isBefore(hireDate)) {
                request.setAttribute(ERROR_ATTRIBUTE, "End date must be after or equal to start date.");
                doGet(request, response);
                return;
            }
            
            // Create employment period from start date and end date
            String employmentPeriod = "";
            if (hireDateStr != null && !hireDateStr.trim().isEmpty()) {
                employmentPeriod = hireDateStr;
                if (endDate != null) {
                    employmentPeriod += " - " + endDate;
                }
            }
            
            email = email.trim().toLowerCase();
            username = username != null ? username.trim() : "";
            password = password != null ? password.trim() : "";

            SystemUser existingUserByEmail = dao.getAccountByEmail(email);
            boolean promoteExistingAccount = existingUserByEmail != null;
            if (!promoteExistingAccount) {
                username = generateEmployeeUsername(email, fullName);
                password = generateTemporaryPassword(guestId);
            }

            int employeeRoleId = dao.getOrCreateRoleIdByName("Employee");
            if (existingUserByEmail != null && existingUserByEmail.getEmployeeId() != null) {
                request.setAttribute(ERROR_ATTRIBUTE, "This account is already linked to an employee.");
                doGet(request, response);
                return;
            }
            // Create employee object
            Employee employee = new Employee();
            // Don't set EmployeeID - let database auto-generate it
            employee.setFullName(fullName.trim());
            employee.setGender(gender);
            employee.setDob(dob);
            employee.setAddress(address);
            employee.setPhone(phone);
            employee.setEmail(email);
            employee.setDepartmentId(departmentId);
            employee.setPosition(position != null ? position : "Employee");
            employee.setHireDate(hireDate);
            employee.setEmploymentPeriod(employmentPeriod); // Set from start date and end date
            employee.setStatus(status);
            
            // Insert employee into database
            System.out.println("Attempting to insert employee: " + employee.getFullName());
            System.out.println("Employee details: " + employee.toString());
            boolean employeeInserted = employeeDAO.insert(employee);
            
            if (!employeeInserted) {
                System.err.println("Failed to insert employee into database");
                System.err.println("Employee details that failed to insert: " + employee.toString());
                request.setAttribute(ERROR_ATTRIBUTE, "Failed to create employee. Please check the console for details.");
                doGet(request, response);
                return;
            }
            
            // Get the generated employee ID after successful insertion
            Employee insertedEmployee = employeeDAO.getByEmail(email);
            if (insertedEmployee == null) {
                System.err.println("Employee was inserted but could not be retrieved by email: " + email);
                request.setAttribute(ERROR_ATTRIBUTE, "Employee created but could not be retrieved. Please contact administrator.");
                doGet(request, response);
                return;
            }
            
            System.out.println("Employee inserted successfully with ID: " + insertedEmployee.getEmployeeId());
            
            boolean userCreated;
            if (promoteExistingAccount) {
                userCreated = dao.promoteExistingUserToEmployee(
                        existingUserByEmail.getUserId(),
                        insertedEmployee.getEmployeeId(),
                        employeeRoleId);
            } else {
                userCreated = dao.createEmployeeUser(insertedEmployee.getEmployeeId(), username, password, employeeRoleId);
            }
            
            if (!userCreated) {
                // Rollback: delete the employee if user creation failed
                employeeDAO.delete(insertedEmployee.getEmployeeId());
                request.setAttribute(ERROR_ATTRIBUTE, "Failed to create user account. Employee creation rolled back.");
                doGet(request, response);
                return;
            }
            
            // Keep Guest/Application history for audit; hide from this screen through eligibility query.
            guestDAO.updateStatus(guestId, "Converted");
            
            // Set success message and redirect to employee list
            request.getSession().setAttribute("success", "Employee created successfully! Name: " + fullName);
            
            // Redirect to employee list page
            response.sendRedirect(request.getContextPath() + "/hr/employee-list");
            
        } catch (NumberFormatException e) {
            request.setAttribute(ERROR_ATTRIBUTE, "Invalid number format. Please check your input.");
            doGet(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute(ERROR_ATTRIBUTE, "Error creating employee: " + e.getMessage());
            doGet(request, response);
        }
    }
    
    private String generateEmployeeUsername(String email, String fullName) {
        String base = email != null && email.contains("@")
                ? email.substring(0, email.indexOf('@'))
                : fullName;
        if (base == null || base.isBlank()) {
            base = "employee";
        }
        base = base.trim().toLowerCase().replaceAll("[^a-z0-9]+", "");
        if (base.isBlank()) {
            base = "employee";
        }

        String candidate = base;
        int suffix = 1;
        while (dao.getAccountByUsername(candidate) != null) {
            candidate = base + suffix++;
        }
        return candidate;
    }

    private String generateTemporaryPassword(int guestId) {
        return "Temp@" + guestId + "123";
    }
    private boolean ensureAccess(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        return PermissionUtil.ensurePermission(request, response, REQUIRED_PERMISSION, DENIED_MESSAGE);
    }
    
    /** 
     * Returns CreateEmployeeController short description of the servlet.
     * @return CreateEmployeeController String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Create Employee Controller";
    }
}

