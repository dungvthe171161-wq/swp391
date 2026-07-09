package com.hrm.controller.hrstaff;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hrm.dao.EmployeeAllowanceDAO;
import com.hrm.util.PermissionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Provides allowance details as JSON for HR staff payroll page.
 */
@WebServlet(name = "PayrollAllowanceApiController", urlPatterns = {"/api/allowance/*"})
public class PayrollAllowanceApiController extends HttpServlet {

    private final EmployeeAllowanceDAO employeeAllowanceDAO = new EmployeeAllowanceDAO();
    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!PermissionUtil.ensureRolePermissionJson(
                request,
                response,
                PermissionUtil.ROLE_HR_STAFF,
                "VIEW_PAYROLLS",
                "API này chỉ dành cho nhân viên nhân sự.",
                "Bạn không có quyền xem phụ cấp lương.")) {
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Thiếu mã phụ cấp");
            return;
        }

        try {
            int allowanceId = Integer.parseInt(pathInfo.substring(1));
            Map<String, Object> allowance = employeeAllowanceDAO.getById(allowanceId);
            if (allowance == null) {
                sendError(response, HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy phụ cấp");
                return;
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(gson.toJson(allowance));
        } catch (NumberFormatException ex) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Mã phụ cấp không hợp lệ");
        }
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\":\"" + message.replace("\"", "\\\"") + "\"}");
    }
}

