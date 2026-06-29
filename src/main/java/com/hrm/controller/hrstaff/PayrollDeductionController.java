package com.hrm.controller.hrstaff;

import com.hrm.dao.EmployeeDeductionDAO;
import com.hrm.dao.PayrollDAO;
import com.hrm.util.PermissionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Controller for Employee Deduction
 * Handles POST /hrstaff/payroll/deduction - Create/Update deduction
 * @author admin
 */
@WebServlet(name = "PayrollDeductionController", urlPatterns = {"/hrstaff/payroll/deduction", "/hrstaff/payroll/deduction/delete"})
public class PayrollDeductionController extends HttpServlet {

    private final EmployeeDeductionDAO employeeDeductionDAO = new EmployeeDeductionDAO();
    private final PayrollDAO payrollDAO = new PayrollDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!ensureAccess(request, response)) {
            return;
        }
        try {
            String deductionIdStr = request.getParameter("deductionId");
            String employeeIdStr = request.getParameter("employeeId");
            String deductionTypeIdStr = request.getParameter("deductionTypeId");
            String amountStr = request.getParameter("amount");
            String month = request.getParameter("month");

            // Validate required fields
            if (employeeIdStr == null || deductionTypeIdStr == null || amountStr == null || month == null) {
                request.setAttribute("error", "Thiếu thông tin bắt buộc.");
                response.sendRedirect(request.getContextPath() + "/hrstaff/payroll?tab=deduction");
                return;
            }

            int employeeId = Integer.parseInt(employeeIdStr);
            int deductionTypeId = Integer.parseInt(deductionTypeIdStr);
            BigDecimal amount = new BigDecimal(amountStr);

            // Check payroll status before allowing deduction creation/update
            String payrollStatus = payrollDAO.getPayrollStatusByEmployeeAndPeriod(employeeId, month);
            if (payrollStatus != null && isPayrollStatusBlocked(payrollStatus)) {
                String statusDisplay = getStatusDisplayName(payrollStatus);
                String errorMessage = String.format(
                    "Không thể tạo/sửa khấu trừ!\n\n" +
                    "Bảng lương của nhân viên này cho tháng %s đã ở trạng thái '%s'.\n\n" +
                    "Để thêm khấu trừ:\n" +
                    "1. Nếu bảng lương đang chờ duyệt: yêu cầu quản lý nhân sự từ chối bảng lương\n" +
                    "2. Nếu bảng lương đã duyệt: tạo bảng lương mới cho tháng tiếp theo hoặc bảng điều chỉnh\n" +
                    "3. Nếu bảng lương đã thanh toán: liên hệ quản lý để xử lý",
                    month, statusDisplay
                );
                request.getSession().setAttribute("error", errorMessage);
                response.sendRedirect(request.getContextPath() + "/hrstaff/payroll?tab=deduction");
                return;
            }

            boolean success;
            if (deductionIdStr != null && !deductionIdStr.trim().isEmpty()) {
                // Update existing deduction
                int deductionId = Integer.parseInt(deductionIdStr);
                success = employeeDeductionDAO.update(deductionId, deductionTypeId, amount, month);
            } else {
                // Create new deduction
                success = employeeDeductionDAO.create(employeeId, deductionTypeId, amount, month);
            }

            if (success) {
                request.getSession().setAttribute("success", "Lưu khấu trừ thành công!");
            } else {
                request.getSession().setAttribute("error", "Không thể lưu khấu trừ.");
            }

            response.sendRedirect(request.getContextPath() + "/hrstaff/payroll?tab=deduction");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "Lỗi khi xử lý khấu trừ: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/hrstaff/payroll?tab=deduction");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!ensureAccess(request, response)) {
            return;
        }
        String path = request.getServletPath();
        if (path != null && path.contains("/delete")) {
            doDelete(request, response);
        }
    }
    
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!ensureAccess(request, response)) {
            return;
        }
        try {
            String deductionIdStr = request.getParameter("deductionId");
            if (deductionIdStr == null) {
                request.getSession().setAttribute("error", "Thiếu mã khấu trừ.");
                response.sendRedirect(request.getContextPath() + "/hrstaff/payroll?tab=deduction");
                return;
            }
            
            int deductionId = Integer.parseInt(deductionIdStr);
            
            // Get deduction info to check payroll status
            var deductionInfo = employeeDeductionDAO.getById(deductionId);
            if (deductionInfo != null) {
                Integer employeeId = (Integer) deductionInfo.get("employeeId");
                String month = (String) deductionInfo.get("month");
                
                if (employeeId != null && month != null) {
                    // Check payroll status before allowing deletion
                    String payrollStatus = payrollDAO.getPayrollStatusByEmployeeAndPeriod(employeeId, month);
                    if (payrollStatus != null && isPayrollStatusBlocked(payrollStatus)) {
                        String statusDisplay = getStatusDisplayName(payrollStatus);
                        String errorMessage = String.format(
                            "Không thể xóa khấu trừ!\n\n" +
                            "Bảng lương của nhân viên này cho tháng %s đã ở trạng thái '%s'.\n\n" +
                            "Để xóa khấu trừ:\n" +
                            "1. Nếu bảng lương đang chờ duyệt: yêu cầu quản lý nhân sự từ chối bảng lương\n" +
                            "2. Nếu bảng lương đã duyệt: tạo bảng lương mới cho tháng tiếp theo hoặc bảng điều chỉnh\n" +
                            "3. Nếu bảng lương đã thanh toán: liên hệ quản lý để xử lý",
                            month, statusDisplay
                        );
                        request.getSession().setAttribute("error", errorMessage);
                        response.sendRedirect(request.getContextPath() + "/hrstaff/payroll?tab=deduction");
                        return;
                    }
                }
            }
            
            boolean success = employeeDeductionDAO.delete(deductionId);
            
            if (success) {
                request.getSession().setAttribute("success", "Xóa khấu trừ thành công!");
            } else {
                request.getSession().setAttribute("error", "Không thể xóa khấu trừ.");
            }
            
            response.sendRedirect(request.getContextPath() + "/hrstaff/payroll?tab=deduction");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "Lỗi khi xóa khấu trừ: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/hrstaff/payroll?tab=deduction");
        }
    }

    /**
     * Check if payroll status blocks allowance/deduction modification
     * Blocked statuses: Pending, Approved, Paid
     * Allowed statuses: Draft, Rejected, null (no payroll)
     */
    private boolean isPayrollStatusBlocked(String status) {
        if (status == null) {
            return false; // No payroll exists, allow modification
        }
        return "Pending".equals(status) || "Approved".equals(status) || "Paid".equals(status);
    }
    
    /**
     * Get display name for payroll status
     */
    private String getStatusDisplayName(String status) {
        if (status == null) return "Không có";
        switch (status) {
            case "Draft": return "Nháp";
            case "Pending": return "Đang chờ duyệt";
            case "Approved": return "Đã duyệt";
            case "Rejected": return "Đã từ chối";
            case "Paid": return "Đã thanh toán";
            default: return status;
        }
    }

    private boolean ensureAccess(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        return PermissionUtil.ensureRolePermission(
                request,
                response,
                PermissionUtil.ROLE_HR_STAFF,
                "VIEW_PAYROLLS",
                "Trang này chỉ dành cho nhân viên nhân sự.",
                "Bạn không có quyền quản lý khấu trừ lương."
        );
    }
}

