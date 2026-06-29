/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.hrm.controller.hrstaff;

import com.hrm.dao.ContractDAO;
import com.hrm.dao.EmployeeDAO;
import com.hrm.model.entity.Contract;
import com.hrm.model.entity.Employee;
import com.hrm.util.PermissionUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *
 * @author admin
 */
@WebServlet(name="CreateContractController", urlPatterns={"/hrstaff/contracts/create"})
public class CreateContractController extends HttpServlet {
   
    private static final String CREATE_CONTRACT_JSP = "/Views/HrStaff/CreateContract.jsp";
    private static final String ERROR_ATTRIBUTE = "error";
    private static final String SUCCESS_ATTRIBUTE = "success";
    private static final String DEFAULT_STATUS = "Draft";
    private static final String STATUS_PENDING = "Pending_Approval";

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
        if (!ensureAccess(request, response)) {
            return;
        }
        try {
            // Get all employees for the dropdown
            EmployeeDAO employeeDAO = new EmployeeDAO();
            List<Employee> employees = employeeDAO.getAll();
            request.setAttribute("employees", employees);
            
            // Forward to the create contract page
            request.getRequestDispatcher(CREATE_CONTRACT_JSP).forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute(ERROR_ATTRIBUTE, "Lỗi khi tải trang tạo hợp đồng: " + e.getMessage());
            try {
                request.getRequestDispatcher(CREATE_CONTRACT_JSP).forward(request, response);
            } catch (ServletException | IOException ex) {
                ex.printStackTrace();
            }
        }
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
        if (!ensureAccess(request, response)) {
            return;
        }
        try {
            createContract(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute(ERROR_ATTRIBUTE, "Lỗi khi tạo hợp đồng: " + e.getMessage());
            doGet(request, response);
        }
    }

    private void createContract(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Get form parameters
            String employeeIdStr = request.getParameter("employeeId");
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");
            String baseSalaryStr = request.getParameter("baseSalary");
            String allowanceStr = request.getParameter("allowance");
            String contractType = request.getParameter("contractType");
            String status = request.getParameter("status");
            String note = request.getParameter("note");
            
            // Validate required fields
            if (employeeIdStr == null || employeeIdStr.trim().isEmpty() ||
                startDateStr == null || startDateStr.trim().isEmpty() ||
                baseSalaryStr == null || baseSalaryStr.trim().isEmpty() ||
                contractType == null || contractType.trim().isEmpty()) {
                
                request.setAttribute(ERROR_ATTRIBUTE, "Vui lòng nhập đầy đủ các trường bắt buộc.");
                doGet(request, response);
                return;
            }
            
            // Parse and validate data
            int employeeId = Integer.parseInt(employeeIdStr);
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = endDateStr != null && !endDateStr.trim().isEmpty() 
                ? LocalDate.parse(endDateStr) : null;
            BigDecimal baseSalary = new BigDecimal(baseSalaryStr);
            BigDecimal allowance = allowanceStr != null && !allowanceStr.trim().isEmpty() 
                ? new BigDecimal(allowanceStr) : BigDecimal.ZERO;
            
            // Set default status if not provided
            if (status == null || status.trim().isEmpty()) {
                status = DEFAULT_STATUS;
            }
            
            // Validate status values
            if (!status.equals(DEFAULT_STATUS) && !status.equals(STATUS_PENDING)) {
                status = DEFAULT_STATUS;
            }
            
            // Validate notes length
            if (note != null && note.trim().length() > 1000) {
                request.setAttribute(ERROR_ATTRIBUTE, "Ghi chú không được vượt quá 1000 ký tự.");
                doGet(request, response);
                return;
            }
            
            // Create contract object
            Contract contract = new Contract();
            contract.setEmployeeId(employeeId);
            contract.setStartDate(startDate);
            contract.setEndDate(endDate);
            contract.setBaseSalary(baseSalary);
            contract.setAllowance(allowance);
            contract.setContractType(contractType);
            contract.setNote(note != null ? note.trim() : null);
            
            // Save to database
            ContractDAO contractDAO = new ContractDAO();
            
            // Check if employee has an active contract
            Contract activeContract = contractDAO.getActiveContractByEmployeeId(employeeId);
            boolean hasActiveContract = activeContract != null;
            
            // If employee has an active contract, expire it and set new contract status to Pending_Approval
            if (hasActiveContract) {
                // Expire the old contract
                contractDAO.expireContract(activeContract.getContractId());
                // New contract must be Pending_Approval when replacing an active contract
                contract.setStatus(STATUS_PENDING);
            } else {
                // No active contract, use the status from form (or default)
                contract.setStatus(status);
            }
            
            boolean success = contractDAO.create(contract);
            
            if (success) {
                String successMessage = hasActiveContract 
                    ? "Tạo hợp đồng thành công! Hợp đồng trước đó đã được đánh dấu hết hạn và hợp đồng mới đang chờ phê duyệt." 
                    : "Tạo hợp đồng thành công!";
                request.setAttribute(SUCCESS_ATTRIBUTE, successMessage);
                response.sendRedirect(request.getContextPath() + "/hrstaff/contracts");
            } else {
                request.setAttribute(ERROR_ATTRIBUTE, "Không thể tạo hợp đồng. Vui lòng thử lại.");
                doGet(request, response);
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute(ERROR_ATTRIBUTE, "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại.");
            try {
                doGet(request, response);
            } catch (ServletException | IOException ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute(ERROR_ATTRIBUTE, "Lỗi: " + e.getMessage());
            try {
                doGet(request, response);
            } catch (ServletException | IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private boolean ensureAccess(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        return PermissionUtil.ensureRolePermission(
                request,
                response,
                PermissionUtil.ROLE_HR_STAFF,
                "VIEW_CONTRACTS",
                "Trang này chỉ dành cho nhân viên nhân sự.",
                "Bạn không có quyền tạo hợp đồng."
        );
    }

    @Override
    public String getServletInfo() {
        return "Tạo hợp đồng";
    }
}
