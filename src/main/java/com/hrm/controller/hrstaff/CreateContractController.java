/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.hrm.controller.hrstaff;

import com.hrm.dao.ContractDAO;
import com.hrm.dao.ContractDocumentDAO;
import com.hrm.dao.EmployeeDAO;
import com.hrm.model.entity.Contract;
import com.hrm.model.entity.ContractDocument;
import com.hrm.model.entity.Employee;
import com.hrm.model.entity.SystemUser;
import com.hrm.service.NotificationRecipientService;
import com.hrm.service.NotificationService;
import com.hrm.util.PermissionUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.util.List;

/**
 *
 * @author admin
 */
@WebServlet(name="CreateContractController", urlPatterns={"/hrstaff/contracts/create"})
@MultipartConfig(maxFileSize = 10 * 1024 * 1024, maxRequestSize = 12 * 1024 * 1024)
public class CreateContractController extends HttpServlet {
   
    private static final String CREATE_CONTRACT_JSP = "/Views/HrStaff/CreateContract.jsp";
    private static final String ERROR_ATTRIBUTE = "error";
    private static final String SUCCESS_ATTRIBUTE = "success";
    private static final String DEFAULT_STATUS = "Draft";
    private static final String STATUS_PENDING = "Pending_Approval";
    private static final long MAX_DOCUMENT_FILE_SIZE = 10L * 1024L * 1024L;
    private final NotificationService notificationService = new NotificationService();
    private final NotificationRecipientService notificationRecipientService = new NotificationRecipientService();

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
            String documentTitle = request.getParameter("documentTitle");
            String documentContent = request.getParameter("documentContent");
            Part documentFile = getDocumentFilePart(request);
            
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
            
            if ((documentContent == null || documentContent.trim().isEmpty()) && documentFile == null) {
                request.setAttribute(ERROR_ATTRIBUTE, "Vui long nhap noi dung hoac them tep hop dong de nhan vien doc truoc khi ky.");
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
            
            // If employee has an active contract, keep it active until the new contract is signed.
            if (hasActiveContract) {
                // New replacement contracts must be approved and then signed by the employee.
                contract.setStatus(STATUS_PENDING);
            } else {
                // No active contract, use the status from form (or default)
                contract.setStatus(status);
            }
            
            int contractId = contractDAO.createAndReturnId(contract);
            boolean success = contractId > 0;
            
            if (success) {
                ContractDocument document = new ContractDocument();
                document.setContractId(contractId);
                document.setTitle(documentTitle);
                document.setContent(cleanDocumentContent(documentContent, documentFile));
                applyDocumentFile(document, documentFile);
                document.setVersionNo(1);
                SystemUser currentUser = PermissionUtil.getCurrentUser(request);
                document.setCreatedBy(currentUser != null ? currentUser.getUserId() : null);
                boolean documentSaved = new ContractDocumentDAO().create(document);
                if (!documentSaved) {
                    contractDAO.deleteContract(contractId);
                    request.setAttribute(ERROR_ATTRIBUTE, "Khong the luu van ban hop dong. Vui long thu lai.");
                    doGet(request, response);
                    return;
                }

                if (STATUS_PENDING.equals(contract.getStatus())) {
                    notifyHrManagersAboutContract(request, contractId, employeeId);
                }
                String successMessage = hasActiveContract 
                    ? "Tạo hợp đồng thành công! Hợp đồng mới đang chờ phê duyệt; hợp đồng hiện tại vẫn có hiệu lực cho tới khi nhân viên ký." 
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

    private void notifyHrManagersAboutContract(HttpServletRequest request, int contractId, int employeeId) {
        if (contractId <= 0) {
            return;
        }
        Employee employee = new EmployeeDAO().getById(employeeId);
        SystemUser currentUser = PermissionUtil.getCurrentUser(request);
        notificationService.notifyContractPendingForHrManagers(
                notificationRecipientService.hrManagerUsers(),
                currentUser != null ? currentUser.getUserId() : 0,
                contractId,
                employee != null ? employee.getFullName() : "Nhan vien"
        );
    }

    private Part getDocumentFilePart(HttpServletRequest request) throws IOException, ServletException {
        Part part = request.getPart("documentFile");
        String fileName = part != null ? cleanFileName(part.getSubmittedFileName()) : "";
        if (part == null || part.getSize() <= 0 || fileName.isEmpty()) {
            return null;
        }
        if (!isAllowedDocumentFile(fileName)) {
            throw new ServletException("Chi ho tro tep PDF, DOC, DOCX, TXT hoac RTF.");
        }
        if (part.getSize() > MAX_DOCUMENT_FILE_SIZE) {
            throw new ServletException("Tep hop dong khong duoc vuot qua 10MB.");
        }
        return part;
    }

    private void applyDocumentFile(ContractDocument document, Part part) throws IOException {
        if (part == null) {
            return;
        }
        document.setFileName(cleanFileName(part.getSubmittedFileName()));
        document.setContentType(part.getContentType());
        document.setFileSize(part.getSize());
        document.setFileData(part.getInputStream().readAllBytes());
    }

    private String cleanDocumentContent(String content, Part part) {
        if (content != null && !content.trim().isEmpty()) {
            return content.trim();
        }
        return part != null
                ? "Van ban hop dong duoc dinh kem trong tep: " + cleanFileName(part.getSubmittedFileName())
                : "";
    }

    private String cleanFileName(String submittedFileName) {
        if (submittedFileName == null) {
            return "";
        }
        String normalized = submittedFileName.replace("\\", "/");
        int slashIndex = normalized.lastIndexOf('/');
        return slashIndex >= 0 ? normalized.substring(slashIndex + 1).trim() : normalized.trim();
    }

    private boolean isAllowedDocumentFile(String fileName) {
        String lower = fileName.toLowerCase();
        return lower.endsWith(".pdf")
                || lower.endsWith(".doc")
                || lower.endsWith(".docx")
                || lower.endsWith(".txt")
                || lower.endsWith(".rtf");
    }

    @Override
    public String getServletInfo() {
        return "Tạo hợp đồng";
    }
}
