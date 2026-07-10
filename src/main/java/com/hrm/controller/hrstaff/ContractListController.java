/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.hrm.controller.hrstaff;

import com.hrm.controller.EmailSender;
import com.hrm.dao.ContractDAO;
import com.hrm.dao.ContractDocumentDAO;
import com.hrm.dao.EmployeeDAO;
import com.hrm.dao.SystemLogDAO;
import com.hrm.model.entity.Contract;
import com.hrm.model.entity.ContractDocument;
import com.hrm.model.entity.Employee;
import com.hrm.model.entity.SystemUser;
import com.hrm.util.PermissionUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.time.LocalDate;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

/**
 *
 * @author admin
 */
@WebServlet(name="ContractListController", urlPatterns={"/hrstaff/contracts"})
@MultipartConfig(maxFileSize = 10 * 1024 * 1024, maxRequestSize = 12 * 1024 * 1024)
public class ContractListController extends HttpServlet {
    
    private static final String CONTRACT_LIST_JSP = "/Views/HrStaff/ContractList.jsp";
    private static final String ERROR_ATTRIBUTE = "error";
    private static final String SUCCESS_ATTRIBUTE = "success";
    private static final String STATUS_DRAFT = "Draft";
    private static final String STATUS_PENDING = "Pending_Approval";
    private static final String STATUS_ACTIVE = "Active";
    private static final int PAGE_SIZE = 10; // 5 contracts per page
    private static final long MAX_DOCUMENT_FILE_SIZE = 10L * 1024L * 1024L;
   
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
            ContractDAO contractDAO = new ContractDAO();
            
            // Get search parameters
            String keyword = request.getParameter("keyword");
            String statusFilter = request.getParameter("status");
            String contractTypeFilter = request.getParameter("contractType");
            String sortBy = request.getParameter("sortBy");
            
            // Get page parameter (default to page 1)
            String pageStr = request.getParameter("page");
            int currentPage = 1;
            try {
                if (pageStr != null && !pageStr.trim().isEmpty()) {
                    currentPage = Integer.parseInt(pageStr);
                    if (currentPage < 1) {
                        currentPage = 1;
                    }
                }
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
            
            // Normalize and validate search parameters
            if (keyword != null) {
                keyword = keyword.trim();
                if (keyword.isEmpty()) {
                    keyword = null;
                }
            }
            
            if (statusFilter != null && statusFilter.equals("All")) {
                statusFilter = null;
            }
            
            if (contractTypeFilter != null && contractTypeFilter.equals("All")) {
                contractTypeFilter = null;
            }
            
            // Calculate pagination
            int offset = (currentPage - 1) * PAGE_SIZE;
            
            // Get contracts with filters and pagination
            // Search by employee name (FullName) only when keyword is provided
            // Always use searchContracts if any filter is active
            List<java.util.Map<String, Object>> contracts;
            int totalContracts;
            boolean hasKeyword = keyword != null && !keyword.isEmpty();
            boolean hasStatusFilter = statusFilter != null && !statusFilter.isEmpty();
            boolean hasContractTypeFilter = contractTypeFilter != null && !contractTypeFilter.isEmpty();
            
            if (hasKeyword || hasStatusFilter || hasContractTypeFilter) {
                // Use searchContracts - it will search by FullName only (not Email)
                contracts = contractDAO.searchContracts(keyword, statusFilter, contractTypeFilter, sortBy, offset, PAGE_SIZE);
                totalContracts = contractDAO.countContracts(keyword, statusFilter, contractTypeFilter);
            } else {
                // No filters, get all contracts
                contracts = contractDAO.getAllContractsWithEmployee(sortBy, offset, PAGE_SIZE);
                totalContracts = contractDAO.countAllContracts();
            }
            
            // Calculate total pages
            int totalPages = (int) Math.ceil((double) totalContracts / PAGE_SIZE);
            
            request.setAttribute("contracts", contracts);
            request.setAttribute("keyword", keyword);
            request.setAttribute("statusFilter", statusFilter);
            request.setAttribute("contractTypeFilter", contractTypeFilter);
            request.setAttribute("sortBy", sortBy);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalContracts", totalContracts);
            request.setAttribute("pageSize", PAGE_SIZE);
            
            // Get all employees for edit form
            EmployeeDAO employeeDAO = new EmployeeDAO();
            List<Employee> employees = employeeDAO.getAll();
            request.setAttribute("employees", employees);
            
            // Handle delete request
            String deleteIdStr = request.getParameter("deleteId");
            if (deleteIdStr != null && !deleteIdStr.trim().isEmpty()) {
                try {
                    int contractId = Integer.parseInt(deleteIdStr);
                    Contract contract = contractDAO.getContractById(contractId);
                    if (contract == null) {
                        response.sendRedirect(request.getContextPath() + "/hrstaff/contracts?deleteError=1");
                        return;
                    } else {
                        // Only allow deletion of Draft, Rejected, or Expired contracts
                        String status = contract.getStatus();
                        if (status == null) status = "Draft";
                        
                        if (!status.equals(STATUS_DRAFT) && !status.equals("Rejected") && !status.equals("Expired")) {
                            response.sendRedirect(request.getContextPath() + "/hrstaff/contracts?deleteError=2");
                            return;
                        } else {
                            boolean deleted = contractDAO.deleteContract(contractId);
                            if (deleted) {
                                // Redirect with success message
                                response.sendRedirect(request.getContextPath() + "/hrstaff/contracts?deleteSuccess=1");
                            } else {
                                // Redirect with error message
                                response.sendRedirect(request.getContextPath() + "/hrstaff/contracts?deleteError=1");
                            }
                            return;
                        }
                    }
                } catch (NumberFormatException e) {
                    response.sendRedirect(request.getContextPath() + "/hrstaff/contracts?deleteError=1");
                    return;
                }
            }
            
            // Handle success/error messages from redirect
            if (request.getParameter("success") != null) {
                request.setAttribute(SUCCESS_ATTRIBUTE, "Thao tac hop dong thanh cong.");
            }
            if (request.getParameter("deleteSuccess") != null) {
                request.setAttribute(SUCCESS_ATTRIBUTE, "Xóa hợp đồng thành công!");
            }
            if (request.getParameter("deleteError") != null) {
                String errorCode = request.getParameter("deleteError");
                if ("2".equals(errorCode)) {
                    request.setAttribute(ERROR_ATTRIBUTE, 
                        "Chỉ có thể xóa hợp đồng ở trạng thái bản nháp, bị từ chối hoặc hết hạn.");
                } else {
                    request.setAttribute(ERROR_ATTRIBUTE, "Không thể xóa hợp đồng. Vui lòng thử lại.");
                }
            }
            
            // Get contract for editing if contractId is provided
            String contractIdStr = request.getParameter("editId");
            if (contractIdStr != null && !contractIdStr.trim().isEmpty()) {
                try {
                    int contractId = Integer.parseInt(contractIdStr);
                    Contract contract = contractDAO.getContractById(contractId);
                    if (contract != null) {
                        request.setAttribute("editingContract", contract);
                        request.setAttribute("editingContractDocument", new ContractDocumentDAO().getLatestByContractId(contractId));
                    }
                } catch (NumberFormatException e) {
                    request.setAttribute(ERROR_ATTRIBUTE, "Mã hợp đồng không hợp lệ.");
                }
            }
            
            request.getRequestDispatcher(CONTRACT_LIST_JSP).forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute(ERROR_ATTRIBUTE, "Lỗi khi tải danh sách hợp đồng: " + e.getMessage());
            try {
                request.getRequestDispatcher(CONTRACT_LIST_JSP).forward(request, response);
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
            String action = request.getParameter("action");
            if ("submitDraft".equals(action)) {
                submitDraftForApproval(request, response);
                return;
            }
            updateContract(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute(ERROR_ATTRIBUTE, "Lỗi khi cập nhật hợp đồng: " + e.getMessage());
            doGet(request, response);
        }
    }
    
    private void updateContract(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            String contractIdStr = request.getParameter("contractId");
            if (contractIdStr == null || contractIdStr.trim().isEmpty()) {
                request.setAttribute(ERROR_ATTRIBUTE, "Mã hợp đồng không được để trống.");
                doGet(request, response);
                return;
            }
            
            int contractId = Integer.parseInt(contractIdStr);
            ContractDAO contractDAO = new ContractDAO();
            
            // Get existing contract to check status and compare values
            Contract existingContract = contractDAO.getContractById(contractId);
            if (existingContract == null) {
                request.setAttribute(ERROR_ATTRIBUTE, "Không tìm thấy hợp đồng.");
                doGet(request, response);
                return;
            }
            
            // Check if contract can be edited (Draft, Pending_Approval, or Active)
            String currentStatus = existingContract.getStatus();
            if (currentStatus != null && 
                !currentStatus.equals(STATUS_DRAFT) && 
                !currentStatus.equals(STATUS_PENDING) &&
                !currentStatus.equals(STATUS_ACTIVE)) {
                request.setAttribute(ERROR_ATTRIBUTE, 
                    "Chỉ có thể chỉnh sửa hợp đồng ở trạng thái bản nháp, chờ phê duyệt hoặc đang hiệu lực.");
                doGet(request, response);
                return;
            }
            
            // Get form parameters
            String employeeIdStr = request.getParameter("employeeId");
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");
            String baseSalaryStr = request.getParameter("baseSalary");
            String allowanceStr = request.getParameter("allowance");
            String contractType = request.getParameter("contractType");
            String note = request.getParameter("note");
            String documentTitle = request.getParameter("documentTitle");
            String documentContent = request.getParameter("documentContent");
            Part documentFile = getDocumentFilePart(request);
            ContractDocument existingDocument = new ContractDocumentDAO().getLatestByContractId(contractId);
            
            // Validate required fields
            if (employeeIdStr == null || employeeIdStr.trim().isEmpty() ||
                startDateStr == null || startDateStr.trim().isEmpty() ||
                baseSalaryStr == null || baseSalaryStr.trim().isEmpty() ||
                contractType == null || contractType.trim().isEmpty()) {
                
                request.setAttribute(ERROR_ATTRIBUTE, "Vui lòng nhập đầy đủ các trường bắt buộc.");
                doGet(request, response);
                return;
            }
            
            // Parse data
            int employeeId = Integer.parseInt(employeeIdStr);
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = endDateStr != null && !endDateStr.trim().isEmpty() 
                ? LocalDate.parse(endDateStr) : null;
            BigDecimal baseSalary = new BigDecimal(baseSalaryStr);
            BigDecimal allowance = allowanceStr != null && !allowanceStr.trim().isEmpty() 
                ? new BigDecimal(allowanceStr) : BigDecimal.ZERO;
            
            // Validate notes length
            if (note != null && note.trim().length() > 1000) {
                request.setAttribute(ERROR_ATTRIBUTE, "Ghi chú không được vượt quá 1000 ký tự.");
                doGet(request, response);
                return;
            }
            
            if ((documentContent == null || documentContent.trim().isEmpty())
                    && documentFile == null
                    && !hasReadableDocument(existingDocument)) {
                request.setAttribute(ERROR_ATTRIBUTE, "Vui long nhap noi dung hoac them tep hop dong.");
                doGet(request, response);
                return;
            }

            if (STATUS_ACTIVE.equals(currentStatus)
                    && !hasReadableDocument(existingDocument)
                    && isSameContractData(existingContract, employeeId, startDate, endDate, baseSalary, allowance, contractType, note)) {
                ContractDocument document = buildDocument(contractId, documentTitle, documentContent, documentFile, existingDocument, currentUserId(request));
                boolean documentSaved = new ContractDocumentDAO().replaceLatest(document);
                if (documentSaved) {
                    response.sendRedirect(request.getContextPath() + "/hrstaff/contracts?success=1");
                } else {
                    request.setAttribute(ERROR_ATTRIBUTE, "Khong the luu van ban hop dong. Vui long thu lai.");
                    doGet(request, response);
                }
                return;
            }

            if (STATUS_ACTIVE.equals(currentStatus)) {
                createPendingProposalFromActive(
                        request,
                        response,
                        contractDAO,
                        existingContract,
                        employeeId,
                        startDate,
                        endDate,
                        baseSalary,
                        allowance,
                        contractType,
                        note,
                        documentTitle,
                        documentContent,
                        documentFile,
                        existingDocument
                );
                return;
            }

            // Check if important fields changed (BaseSalary)
            // If contract is already Pending_Approval, compare with the last Active contract (original salary)
            // Otherwise, compare with current contract value
            boolean importantFieldChanged = false;
            BigDecimal oldSalary = null;
            BigDecimal currentContractSalary = existingContract.getBaseSalary();
            
            // If contract is Pending_Approval, get the original salary from Active contract
            if (STATUS_PENDING.equals(currentStatus)) {
                Contract previousActiveContract = contractDAO.getPreviousActiveContract(employeeId, contractId);
                if (previousActiveContract != null && previousActiveContract.getBaseSalary() != null) {
                    // Compare new salary with original active contract salary
                    if (!previousActiveContract.getBaseSalary().equals(baseSalary)) {
                        importantFieldChanged = true;
                        oldSalary = previousActiveContract.getBaseSalary(); // Use original salary from Active contract
                    }
                } else {
                    // No previous active contract, compare with current contract value
                    if (currentContractSalary != null && !currentContractSalary.equals(baseSalary)) {
                        importantFieldChanged = true;
                        oldSalary = currentContractSalary;
                    }
                }
            } else {
                // Contract is Draft or Active, compare with current contract value
                if (currentContractSalary != null && !currentContractSalary.equals(baseSalary)) {
                    importantFieldChanged = true;
                    oldSalary = currentContractSalary;
                }
            }
            
            // Create updated contract
            Contract contract = new Contract();
            contract.setContractId(contractId);
            contract.setEmployeeId(employeeId);
            contract.setStartDate(startDate);
            contract.setEndDate(endDate);
            contract.setBaseSalary(baseSalary);
            contract.setAllowance(allowance);
            contract.setContractType(contractType);
            contract.setNote(note != null ? note.trim() : null);
            
            // Set status: if important field changed, set to Pending_Approval
            // If contract is already Pending_Approval and salary changed, keep it as Pending_Approval
            if (importantFieldChanged) {
                contract.setStatus(STATUS_PENDING);
            } else {
                // Keep existing status
                contract.setStatus(existingContract.getStatus());
                // However, if contract is already Pending_Approval and we're editing it,
                // keep it as Pending_Approval even if no salary change detected
                // (this handles cases where salary was already changed but we're editing other fields)
                if (STATUS_PENDING.equals(currentStatus)) {
                    contract.setStatus(STATUS_PENDING);
                }
            }
            
            // Update contract
            boolean success = contractDAO.updateContract(contract);
            
            if (success) {
                ContractDocument document = buildDocument(contractId, documentTitle, documentContent, documentFile, existingDocument, currentUserId(request));
                boolean documentSaved = new ContractDocumentDAO().replaceLatest(document);
                if (!documentSaved) {
                    request.setAttribute(ERROR_ATTRIBUTE, "Khong the luu van ban hop dong. Vui long thu lai.");
                    doGet(request, response);
                    return;
                }
                if (importantFieldChanged) {
                    contractDAO.clearSignature(contractId);
                }
                // Log salary change to SystemLog if salary was changed
                if (importantFieldChanged && oldSalary != null) {
                    try {
                        HttpSession session = request.getSession(false);
                        if (session != null) {
                            SystemUser currentUser = (SystemUser) session.getAttribute("systemUser");
                            if (currentUser != null) {
                                SystemLogDAO systemLogDAO = new SystemLogDAO();
                                NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                                String oldValue = "Mã hợp đồng: " + contractId + ", Lương cũ: " + nf.format(oldSalary) + " VNĐ";
                                String newValue = "Mã hợp đồng: " + contractId + ", Lương mới: " + nf.format(baseSalary) + " VNĐ";
                                
                                systemLogDAO.insertSystemLog(
                                    currentUser.getUserId(),
                                    "Cập nhật lương",
                                    "Hợp đồng",
                                    oldValue,
                                    newValue
                                );
                            }
                        }
                    } catch (Exception e) {
                        // Log error but don't fail the update
                        System.err.println("Error logging salary change: " + e.getMessage());
                    }
                }
                
                // Send notification to HR Manager if status changed to Pending_Approval
                if (importantFieldChanged) {
                    try {
                        String hrManagerEmail = contractDAO.getHrManagerEmail();
                        if (hrManagerEmail != null && !hrManagerEmail.trim().isEmpty()) {
                            NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                            String subject = "Hợp đồng cần phê duyệt - Mã hợp đồng: " + contractId;
                            String salaryChangeInfo = "";
                            if (oldSalary != null) {
                                salaryChangeInfo = String.format(
                                    "\nThay đổi lương:\n" +
                                    "- Lương cũ: %s VNĐ\n" +
                                    "- Lương mới: %s VNĐ\n",
                                    nf.format(oldSalary),
                                    nf.format(baseSalary)
                                );
                            }
                            String content = String.format(
                                "Hợp đồng mã %d đã được chỉnh sửa và cần phê duyệt.\n\n" +
                                "Thông tin hợp đồng:\n" +
                                "- ID: %d\n" +
                                "- Mã nhân viên: %d\n" +
                                "- Lương cơ bản: %s VNĐ\n" +
                                "%s" +
                                "- Trạng thái: Chờ phê duyệt\n\n" +
                                "Vui lòng đăng nhập hệ thống để phê duyệt hợp đồng.",
                                contractId, contractId, employeeId, nf.format(baseSalary), salaryChangeInfo
                            );
                            EmailSender.sendEmail(hrManagerEmail, subject, content);
                        }
                    } catch (Exception e) {
                        // Log error but don't fail the update
                        System.err.println("Error sending notification email: " + e.getMessage());
                    }
                }
                
                request.setAttribute(SUCCESS_ATTRIBUTE, 
                    importantFieldChanged ? 
                        "Cập nhật hợp đồng thành công! Hợp đồng đã chuyển sang trạng thái chờ phê duyệt và đã gửi thông báo cho quản lý nhân sự." :
                        "Cập nhật hợp đồng thành công!");
                
                // Redirect to avoid resubmission
                response.sendRedirect(request.getContextPath() + "/hrstaff/contracts?success=1");
            } else {
                request.setAttribute(ERROR_ATTRIBUTE, "Không thể cập nhật hợp đồng. Vui lòng thử lại.");
                doGet(request, response);
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute(ERROR_ATTRIBUTE, "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại.");
            doGet(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute(ERROR_ATTRIBUTE, "Lỗi: " + e.getMessage());
            doGet(request, response);
        }
    }
    
    private void submitDraftForApproval(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int contractId;
        try {
            contractId = Integer.parseInt(request.getParameter("contractId"));
        } catch (NumberFormatException ex) {
            request.setAttribute(ERROR_ATTRIBUTE, "MĂ£ há»£p Ä‘á»“ng khĂ´ng há»£p lá»‡.");
            doGet(request, response);
            return;
        }

        ContractDAO contractDAO = new ContractDAO();
        Contract contract = contractDAO.getContractById(contractId);
        if (contract == null) {
            request.setAttribute(ERROR_ATTRIBUTE, "KhĂ´ng tĂ¬m tháº¥y há»£p Ä‘á»“ng.");
            doGet(request, response);
            return;
        }
        if (!STATUS_DRAFT.equals(contract.getStatus())) {
            request.setAttribute(ERROR_ATTRIBUTE, "Chá»‰ cĂ³ thá»ƒ gá»­i duyá»‡t há»£p Ä‘á»“ng báº£n nhĂ¡p.");
            doGet(request, response);
            return;
        }

        if (!new ContractDocumentDAO().hasReadableDocument(contractId)) {
            request.setAttribute(ERROR_ATTRIBUTE, "Vui long them van ban hop dong truoc khi gui duyet.");
            doGet(request, response);
            return;
        }

        contract.setStatus(STATUS_PENDING);
        boolean success = contractDAO.updateContract(contract);
        if (success) {
            notifyHrManagerAboutPendingContract(contractDAO, contractId, contract.getEmployeeId(), contract.getBaseSalary(), null);
            response.sendRedirect(request.getContextPath() + "/hrstaff/contracts?success=1");
        } else {
            request.setAttribute(ERROR_ATTRIBUTE, "KhĂ´ng thá»ƒ gá»­i duyá»‡t há»£p Ä‘á»“ng. Vui lĂ²ng thá»­ láº¡i.");
            doGet(request, response);
        }
    }

    private void createPendingProposalFromActive(HttpServletRequest request,
                                                 HttpServletResponse response,
                                                 ContractDAO contractDAO,
                                                 Contract existingContract,
                                                 int employeeId,
                                                 LocalDate startDate,
                                                 LocalDate endDate,
                                                 BigDecimal baseSalary,
                                                 BigDecimal allowance,
                                                 String contractType,
                                                 String note,
                                                 String documentTitle,
                                                 String documentContent,
                                                 Part documentFile,
                                                 ContractDocument sourceDocument)
            throws IOException, ServletException {
        Contract proposal = new Contract();
        proposal.setEmployeeId(employeeId);
        proposal.setStartDate(startDate);
        proposal.setEndDate(endDate);
        proposal.setBaseSalary(baseSalary);
        proposal.setAllowance(allowance);
        proposal.setContractType(contractType);
        proposal.setNote(note != null ? note.trim() : null);
        proposal.setStatus(STATUS_PENDING);

        int proposalId = contractDAO.createAndReturnId(proposal);
        if (proposalId > 0) {
            Integer userId = currentUserId(request);
            ContractDocument document = buildDocument(proposalId, documentTitle, documentContent, documentFile, sourceDocument, userId);
            boolean documentSaved = new ContractDocumentDAO().create(document);
            if (!documentSaved) {
                contractDAO.deleteContract(proposalId);
                request.setAttribute(ERROR_ATTRIBUTE, "Khong the luu van ban hop dong. Vui long thu lai.");
                doGet(request, response);
                return;
            }
            notifyHrManagerAboutPendingContract(
                    contractDAO,
                    proposalId,
                    employeeId,
                    baseSalary,
                    existingContract.getBaseSalary()
            );
            response.sendRedirect(request.getContextPath() + "/hrstaff/contracts?success=1");
        } else {
            request.setAttribute(ERROR_ATTRIBUTE, "KhĂ´ng thá»ƒ táº¡o báº£n Ä‘á» xuáº¥t há»£p Ä‘á»“ng. Vui lĂ²ng thá»­ láº¡i.");
            doGet(request, response);
        }
    }

    private void notifyHrManagerAboutPendingContract(ContractDAO contractDAO,
                                                     int contractId,
                                                     int employeeId,
                                                     BigDecimal baseSalary,
                                                     BigDecimal oldSalary) {
        try {
            String hrManagerEmail = contractDAO.getHrManagerEmail();
            if (hrManagerEmail == null || hrManagerEmail.trim().isEmpty()) {
                return;
            }
            NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
            String subject = "Há»£p Ä‘á»“ng cáº§n phĂª duyá»‡t - MĂ£ há»£p Ä‘á»“ng: " + contractId;
            String salaryChangeInfo = "";
            if (oldSalary != null) {
                salaryChangeInfo = String.format(
                        "\nThay Ä‘á»•i lÆ°Æ¡ng:\n- LÆ°Æ¡ng cÅ©: %s VNÄ\n- LÆ°Æ¡ng má»›i: %s VNÄ\n",
                        nf.format(oldSalary),
                        nf.format(baseSalary)
                );
            }
            String content = String.format(
                    "Há»£p Ä‘á»“ng mĂ£ %d Ä‘ang chá» phĂª duyá»‡t.\n\n"
                            + "ThĂ´ng tin há»£p Ä‘á»“ng:\n"
                            + "- ID: %d\n"
                            + "- MĂ£ nhĂ¢n viĂªn: %d\n"
                            + "- LÆ°Æ¡ng cÆ¡ báº£n: %s VNÄ\n"
                            + "%s"
                            + "- Tráº¡ng thĂ¡i: Chá» phĂª duyá»‡t\n\n"
                            + "Vui lĂ²ng Ä‘Äƒng nháº­p há»‡ thá»‘ng Ä‘á»ƒ phĂª duyá»‡t há»£p Ä‘á»“ng.",
                    contractId,
                    contractId,
                    employeeId,
                    nf.format(baseSalary),
                    salaryChangeInfo
            );
            EmailSender.sendEmail(hrManagerEmail, subject, content);
        } catch (Exception e) {
            System.err.println("Error sending notification email: " + e.getMessage());
        }
    }

    private ContractDocument buildDocument(int contractId,
                                           String title,
                                           String content,
                                           Part uploadedFile,
                                           ContractDocument sourceDocument,
                                           Integer userId) throws IOException {
        ContractDocument document = new ContractDocument();
        document.setContractId(contractId);
        document.setTitle(title);
        document.setContent(resolveDocumentContent(content, uploadedFile, sourceDocument));
        document.setCreatedBy(userId);

        if (uploadedFile != null) {
            applyDocumentFile(document, uploadedFile);
        } else {
            copyDocumentFile(document, sourceDocument);
        }
        return document;
    }

    private String resolveDocumentContent(String content, Part uploadedFile, ContractDocument sourceDocument) {
        if (content != null && !content.trim().isEmpty()) {
            return content.trim();
        }
        if (sourceDocument != null && sourceDocument.getContent() != null && !sourceDocument.getContent().trim().isEmpty()) {
            return sourceDocument.getContent().trim();
        }
        if (uploadedFile != null) {
            return "Van ban hop dong duoc dinh kem trong tep: " + cleanFileName(uploadedFile.getSubmittedFileName());
        }
        if (sourceDocument != null && sourceDocument.getFileName() != null && !sourceDocument.getFileName().trim().isEmpty()) {
            return "Van ban hop dong duoc dinh kem trong tep: " + sourceDocument.getFileName().trim();
        }
        return "";
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

    private void copyDocumentFile(ContractDocument target, ContractDocument source) {
        if (source == null || source.getFileData() == null || source.getFileData().length == 0) {
            return;
        }
        target.setFileName(source.getFileName());
        target.setContentType(source.getContentType());
        target.setFileSize(source.getFileSize());
        target.setFileData(source.getFileData());
    }

    private boolean hasReadableDocument(ContractDocument document) {
        return document != null
                && ((document.getContent() != null && !document.getContent().trim().isEmpty())
                    || (document.getFileName() != null && !document.getFileName().trim().isEmpty()
                        && document.getFileData() != null && document.getFileData().length > 0));
    }

    private boolean isSameContractData(Contract existingContract,
                                       int employeeId,
                                       LocalDate startDate,
                                       LocalDate endDate,
                                       BigDecimal baseSalary,
                                       BigDecimal allowance,
                                       String contractType,
                                       String note) {
        return existingContract != null
                && existingContract.getEmployeeId() == employeeId
                && sameDate(existingContract.getStartDate(), startDate)
                && sameDate(existingContract.getEndDate(), endDate)
                && sameMoney(existingContract.getBaseSalary(), baseSalary)
                && sameMoney(existingContract.getAllowance(), allowance)
                && sameText(existingContract.getContractType(), contractType)
                && sameText(existingContract.getNote(), note);
    }

    private boolean sameDate(LocalDate left, LocalDate right) {
        return left == null ? right == null : left.equals(right);
    }

    private boolean sameMoney(BigDecimal left, BigDecimal right) {
        BigDecimal safeLeft = left != null ? left : BigDecimal.ZERO;
        BigDecimal safeRight = right != null ? right : BigDecimal.ZERO;
        return safeLeft.compareTo(safeRight) == 0;
    }

    private boolean sameText(String left, String right) {
        String safeLeft = left == null ? "" : left.trim();
        String safeRight = right == null ? "" : right.trim();
        return safeLeft.equals(safeRight);
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

    private Integer currentUserId(HttpServletRequest request) {
        SystemUser currentUser = PermissionUtil.getCurrentUser(request);
        return currentUser != null ? currentUser.getUserId() : null;
    }

    private boolean ensureAccess(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        return PermissionUtil.ensureRolePermission(
                request,
                response,
                PermissionUtil.ROLE_HR_STAFF,
                "VIEW_CONTRACTS",
                "Trang này chỉ dành cho nhân viên nhân sự.",
                "Bạn không có quyền quản lý hợp đồng."
        );
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Danh sách hợp đồng";
    }
}
