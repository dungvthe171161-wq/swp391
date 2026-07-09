/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.hrm.controller.hr;

import com.hrm.dao.ContractDAO;
import com.hrm.dao.ContractDocumentDAO;
import com.hrm.dao.EmployeeDAO;
import com.hrm.dao.SystemLogDAO;
import com.hrm.model.entity.Contract;
import com.hrm.model.entity.ContractDocument;
import com.hrm.model.entity.Employee;
import com.hrm.model.entity.Notification;
import com.hrm.model.entity.SystemLog;
import com.hrm.model.entity.SystemUser;
import com.hrm.service.NotificationRecipientService;
import com.hrm.service.NotificationService;
import com.hrm.util.PermissionUtil;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author admin
 */
@WebServlet(name="ApproveRejectContractController", urlPatterns={"/hr/approve-reject-contracts"})
public class ApproveRejectContractController extends HttpServlet {
   
    private static final String APPROVE_REJECT_CONTRACT_JSP = "/Views/hr/ApproveRejectContract.jsp";
    private static final String STATUS_PENDING_APPROVAL = "Pending_Approval";
    private static final String STATUS_ACTIVE = "Active";
    private static final String STATUS_REJECTED = "Rejected";
    private static final String ERROR_ATTRIBUTE = "error";
    private static final String SUCCESS_ATTRIBUTE = "success";
    private static final String REQUIRED_PERMISSION = "VIEW_CONTRACTS";
    private static final String DENIED_MESSAGE = "You do not have permission to review or approve contracts.";
    
    private final transient ContractDAO contractDAO = new ContractDAO();
    private final transient ContractDocumentDAO contractDocumentDAO = new ContractDocumentDAO();
    private final transient SystemLogDAO systemLogDAO = new SystemLogDAO();
    private final transient NotificationService notificationService = new NotificationService();
    private final transient NotificationRecipientService notificationRecipientService = new NotificationRecipientService();
    private static final Pattern SALARY_PATTERN = Pattern.compile("(\\d{1,3}(?:,\\d{3})*(?:\\.\\d+)?)\\s*VND");

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
        if ("document".equals(request.getParameter("action"))) {
            downloadContractDocument(request, response);
            return;
        }
        try {
            // Get all contracts with status Pending_Approval
            List<Map<String, Object>> pendingContracts = contractDAO.getContractsByStatus(STATUS_PENDING_APPROVAL);
            
            // For each contract, get previous contract to show changes
            List<Map<String, Object>> contractsWithChanges = new ArrayList<>();
            for (Map<String, Object> contract : pendingContracts) {
                Map<String, Object> contractWithChanges = new HashMap<>(contract);
                
                Integer employeeId = (Integer) contract.get("employeeId");
                Integer contractId = (Integer) contract.get("contractId");
                List<String> changes = new ArrayList<>();
                
                if (employeeId != null && contractId != null) {
                    BigDecimal currentSalary = (BigDecimal) contract.get("baseSalary");
                    
                    // Always check for previous Active contract first to show change from original salary
                    Contract previousContract = contractDAO.getPreviousActiveContract(employeeId, contractId);
                    
                    if (previousContract != null) {
                        BigDecimal previousSalary = previousContract.getBaseSalary();
                        
                        // Always show salary comparison with previous active contract (original salary)
                        // This ensures we always show the change from the original active contract salary
                        if (previousSalary != null && currentSalary != null) {
                            if (previousSalary.compareTo(currentSalary) != 0) {
                                if (currentSalary.compareTo(previousSalary) > 0) {
                                    changes.add(String.format("💰 Salary Changed: Increased from %s VND → %s VND", 
                                        formatCurrency(previousSalary), formatCurrency(currentSalary)));
                                } else {
                                    changes.add(String.format("💰 Salary Changed: Decreased from %s VND → %s VND", 
                                        formatCurrency(previousSalary), formatCurrency(currentSalary)));
                                }
                            } else {
                                // Same salary as previous active contract
                                changes.add(String.format("💰 Salary: %s VND (same as previous active contract)", 
                                    formatCurrency(currentSalary)));
                            }
                        }
                        
                        // Compare contract type changes
                        String currentType = (String) contract.get("contractType");
                        String previousType = previousContract.getContractType();
                        
                        if (previousType != null && currentType != null && 
                            !previousType.equals(currentType)) {
                            changes.add(String.format("Contract type changed from %s → %s", 
                                previousType, currentType));
                        }
                        
                        contractWithChanges.put("previousContract", previousContract);
                    } else {
                        // New contract (no previous active contract)
                        // Check SystemLog for any salary changes if this is a new contract that was edited
                        SystemLog salaryChangeLog = systemLogDAO.getLatestSalaryChangeForContract(contractId);
                        
                        if (salaryChangeLog != null && salaryChangeLog.getOldValue() != null && salaryChangeLog.getNewValue() != null) {
                            // Parse old and new salary from SystemLog
                            String oldSalaryStr = extractSalaryFromLog(salaryChangeLog.getOldValue());
                            String newSalaryStr = extractSalaryFromLog(salaryChangeLog.getNewValue());
                            
                            if (oldSalaryStr != null && newSalaryStr != null) {
                                try {
                                    BigDecimal oldSalary = parseSalary(oldSalaryStr);
                                    BigDecimal newSalary = parseSalary(newSalaryStr);
                                    
                                    // Verify the new salary matches current contract salary
                                    if (currentSalary != null && newSalary.compareTo(currentSalary) == 0) {
                                        if (newSalary.compareTo(oldSalary) > 0) {
                                            changes.add(String.format("💰 Salary Changed: Increased from %s VND → %s VND", 
                                                formatCurrency(oldSalary), formatCurrency(newSalary)));
                                        } else if (newSalary.compareTo(oldSalary) < 0) {
                                            changes.add(String.format("💰 Salary Changed: Decreased from %s VND → %s VND", 
                                                formatCurrency(oldSalary), formatCurrency(newSalary)));
                                        }
                                    }
                                } catch (Exception e) {
                                    // If parsing fails, try to extract from strings
                                    changes.add(String.format("💰 Salary Changed: %s → %s", oldSalaryStr, newSalaryStr));
                                }
                            }
                        }
                        
                        // If no changes found, this is a new contract
                        String employeeName = (String) contract.get("employeeName");
                        if (employeeName != null && changes.isEmpty()) {
                            changes.add("Create new contract for " + employeeName);
                        } else if (changes.isEmpty()) {
                            changes.add("Create new contract");
                        }
                    }
                }
                ContractDocument document = contractId != null
                        ? contractDocumentDAO.getLatestByContractId(contractId)
                        : null;
                contractWithChanges.put("contractDocument", document);
                contractWithChanges.put("changes", changes);
                contractsWithChanges.add(contractWithChanges);
            }
            
            request.setAttribute("contracts", contractsWithChanges);
            request.setAttribute("pendingCount", pendingContracts.size());
            
            // Check for success/error messages from URL parameters
            String success = request.getParameter("success");
            String error = request.getParameter("error");
            if (success != null && !success.trim().isEmpty()) {
                request.setAttribute(SUCCESS_ATTRIBUTE, success);
            }
            if (error != null && !error.trim().isEmpty()) {
                request.setAttribute(ERROR_ATTRIBUTE, error);
            }
            
            request.getRequestDispatcher(APPROVE_REJECT_CONTRACT_JSP).forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute(ERROR_ATTRIBUTE, "Error loading contract list: " + e.getMessage());
            request.getRequestDispatcher(APPROVE_REJECT_CONTRACT_JSP).forward(request, response);
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
            String contractIdStr = request.getParameter("contractId");
            
            if (contractIdStr == null || contractIdStr.trim().isEmpty()) {
                redirectWithMessage(response, request.getContextPath() + "/hr/approve-reject-contracts", 
                    "error", "Contract not found");
                return;
            }
            
            int contractId = Integer.parseInt(contractIdStr);
            Contract contract = contractDAO.getContractById(contractId);
            
            if (contract == null) {
                redirectWithMessage(response, request.getContextPath() + "/hr/approve-reject-contracts", 
                    "error", "Contract does not exist");
                return;
            }
            
            if (!STATUS_PENDING_APPROVAL.equals(contract.getStatus())) {
                redirectWithMessage(response, request.getContextPath() + "/hr/approve-reject-contracts", 
                    "error", "Contract is not in pending approval status");
                return;
            }
            
            if ("approve".equals(action)) {
                if (!contractDocumentDAO.hasReadableDocument(contractId)) {
                    redirectWithMessage(response, request.getContextPath() + "/hr/approve-reject-contracts",
                            "error", "Contract document is missing. Please ask HR Staff to add it before approval.");
                    return;
                }
                // HR approval moves the contract to employee signature before it becomes active.
                boolean success = contractDAO.markPendingSignature(contractId);
                
                if (success) {
                    // Get employee name for success message
                    String employeeName = getEmployeeName(contract.getEmployeeId());
                    notifyHrStaffAboutContractDecision(request, contractId, employeeName, STATUS_ACTIVE);
                    notifyEmployeeContractNeedsSignature(request, contract.getEmployeeId(), contractId, employeeName);
                    
                    String successMsg = "✅ Contract approved successfully!" + 
                        (employeeName.isEmpty() ? "" : " Contract for " + employeeName + " is waiting for employee signature.");
                    redirectWithMessage(response, request.getContextPath() + "/hr/approve-reject-contracts", "success", successMsg);
                } else {
                    redirectWithMessage(response, request.getContextPath() + "/hr/approve-reject-contracts", "error", 
                        "❌ Unable to approve contract. Please try again.");
                }
                
            } else if ("reject".equals(action)) {
                // Reject contract - set status to Rejected and add reason
                String rejectionReason = request.getParameter("rejectionReason");
                contract.setStatus(STATUS_REJECTED);
                
                // Append rejection reason to notes
                String currentNote = contract.getNote();
                String newNote = (currentNote != null && !currentNote.trim().isEmpty() 
                    ? currentNote + "\n\n" : "") + 
                    "Rejection reason: " + (rejectionReason != null ? rejectionReason : "No reason provided");
                
                contract.setNote(newNote);
                boolean success = contractDAO.updateContract(contract);
                
                if (success) {
                    // Get employee name for success message
                    String employeeName = getEmployeeName(contract.getEmployeeId());
                    notifyHrStaffAboutContractDecision(request, contractId, employeeName, STATUS_REJECTED);
                    
                    String successMsg = "✅ Contract rejected successfully!" + 
                        (employeeName.isEmpty() ? "" : " Contract for " + employeeName + " has been rejected.") +
                        (rejectionReason != null && !rejectionReason.trim().isEmpty() ? 
                            " Reason: " + rejectionReason : "");
                    redirectWithMessage(response, request.getContextPath() + "/hr/approve-reject-contracts", "success", successMsg);
                } else {
                    redirectWithMessage(response, request.getContextPath() + "/hr/approve-reject-contracts", "error", 
                        "❌ Unable to reject contract. Please try again.");
                }
                
            } else {
                redirectWithMessage(response, request.getContextPath() + "/hr/approve-reject-contracts", 
                    "error", "Invalid action");
            }
            
        } catch (NumberFormatException e) {
            redirectWithMessage(response, request.getContextPath() + "/hr/approve-reject-contracts", 
                "error", "Invalid contract ID");
        } catch (Exception e) {
            e.printStackTrace();
            redirectWithMessage(response, request.getContextPath() + "/hr/approve-reject-contracts", 
                "error", "Error: " + e.getMessage());
        }
    }

    /**
     * Format currency for display
     */
    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0";
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        return formatter.format(amount);
    }

    /**
     * Extract salary value from log string (e.g., "Contract ID: 1, Old Salary: 10,000 VND" -> "10,000")
     */
    private String extractSalaryFromLog(String logValue) {
        if (logValue == null) return null;
        
        // Look for "Old Salary: " or "New Salary: " followed by the amount
        Pattern pattern = Pattern.compile("(?:Old|New)\\s+Salary:\\s*([\\d,]+(?:\\.[\\d]+)?)");
        Matcher matcher = pattern.matcher(logValue);
        
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        // Fallback: try to extract any number followed by VND
        matcher = SALARY_PATTERN.matcher(logValue);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return null;
    }

    /**
     * Parse salary string to BigDecimal (handles comma-separated numbers)
     */
    private BigDecimal parseSalary(String salaryStr) {
        if (salaryStr == null || salaryStr.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        // Remove commas and parse
        String cleaned = salaryStr.replace(",", "").trim();
        try {
            return new BigDecimal(cleaned);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Get employee name by ID
     */
    private String getEmployeeName(int employeeId) {
        try {
            EmployeeDAO employeeDAO = new EmployeeDAO();
            Employee employee = employeeDAO.getById(employeeId);
            if (employee != null && employee.getFullName() != null) {
                return employee.getFullName();
            }
        } catch (Exception e) {
            // Ignore, return empty name
        }
        return "";
    }

    /**
     * Redirect with success or error message
     */
    private void redirectWithMessage(HttpServletResponse response, String url, String type, String message) 
            throws IOException {
        try {
            String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
            response.sendRedirect(url + "?" + type + "=" + encodedMessage);
        } catch (UnsupportedEncodingException e) {
            // Fallback to default encoding if UTF-8 is not supported (should not happen)
            response.sendRedirect(url + "?" + type + "=" + URLEncoder.encode(message, "UTF-8"));
        }
    }

    private boolean ensureAccess(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        return PermissionUtil.ensurePermission(request, response, REQUIRED_PERMISSION, DENIED_MESSAGE);
    }

    private void notifyHrStaffAboutContractDecision(HttpServletRequest request,
                                                    int contractId,
                                                    String employeeName,
                                                    String decision) {
        SystemUser currentUser = PermissionUtil.getCurrentUser(request);
        notificationService.notifyContractDecisionForHrStaff(
                notificationRecipientService.hrStaffUsers(),
                currentUser != null ? currentUser.getUserId() : 0,
                contractId,
                employeeName,
                decision
        );
    }

    private void notifyEmployeeContractNeedsSignature(HttpServletRequest request,
                                                      int employeeId,
                                                      int contractId,
                                                      String employeeName) {
        Integer employeeUserId = notificationRecipientService.activeUserByEmployeeId(employeeId);
        if (employeeUserId == null || employeeUserId <= 0) {
            return;
        }
        SystemUser currentUser = PermissionUtil.getCurrentUser(request);
        Notification notification = notificationService.buildNotification(
                employeeUserId,
                currentUser != null ? currentUser.getUserId() : null,
                "Contract",
                contractId,
                "Contract",
                "Hop dong can ky",
                "Hop dong cua " + (employeeName == null || employeeName.isBlank() ? "ban" : employeeName)
                        + " da duoc HR duyet. Vui long kiem tra va ky xac nhan.",
                "/employee/contract",
                "High"
        );
        notificationService.notifyUser(notification);
    }

    private void downloadContractDocument(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int contractId;
        try {
            contractId = Integer.parseInt(request.getParameter("contractId"));
        } catch (NumberFormatException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        ContractDocument document = contractDocumentDAO.getLatestByContractId(contractId);
        if (document == null || document.getFileData() == null || document.getFileData().length == 0) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType(document.getContentType() != null ? document.getContentType() : "application/octet-stream");
        response.setHeader("Content-Disposition", "inline; filename=\"" + safeDownloadFileName(document.getFileName()) + "\"");
        response.setContentLengthLong(document.getFileData().length);
        response.getOutputStream().write(document.getFileData());
    }

    private String safeDownloadFileName(String fileName) {
        String cleaned = fileName == null || fileName.isBlank() ? "contract-document" : fileName.trim();
        return cleaned.replace("\\", "_").replace("/", "_").replace("\"", "").replace("\r", "").replace("\n", "");
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Approve Reject Contract Controller";
    }
}
