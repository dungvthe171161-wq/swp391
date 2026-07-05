/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.hrm.controller.hrstaff;

import com.hrm.dao.ApplicationDAO;
import com.hrm.model.entity.SystemUser;
import com.hrm.util.PermissionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DELL
 */
@WebServlet(name = "ViewCandidateController", urlPatterns = {"/candidates"})
public class ViewCandidateController extends HttpServlet {

    private static final String REQUIRED_PERMISSION = "VIEW_RECRUITMENT";
    private static final String REQUIRED_ROLE_MESSAGE = "Khu vực này chỉ dành cho nhân viên nhân sự.";
    private static final String PERMISSION_DENIED_MESSAGE = "Bạn không có quyền xem thông tin ứng viên.";
    private static final String LOGIN_PATH = "/login";
    private static final String PARAM_SEARCH_BY_NAME = "searchByName";
    private static final String PARAM_FILTER_STATUS = "filterStatus";
    private static final String PARAM_START_DATE = "startDate";
    private static final String PARAM_END_DATE = "endDate";
    private final transient ApplicationDAO applicationDAO = new ApplicationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!ensureAccess(request, response)) {
            return;
        }
        int page = 1;
        int pageSize = 5;
        String pageStr = request.getParameter("page");

        if (pageStr != null) {
            try {
                page = Integer.parseInt(pageStr);
            } catch (Exception e) {
                page = 1;
            }
        }

        // Get search parameters
        String searchByName = request.getParameter(PARAM_SEARCH_BY_NAME);
        String filterStatus = request.getParameter(PARAM_FILTER_STATUS);
        String startDate = request.getParameter(PARAM_START_DATE);
        String endDate = request.getParameter(PARAM_END_DATE);
        var applications = applicationDAO.findCandidateApplicationsForHr(
                searchByName, filterStatus, startDate, endDate, page, pageSize);
        int totalCandidates = applicationDAO.countCandidateApplicationsForHr(
                searchByName, filterStatus, startDate, endDate);

        int totalPages = (int) Math.ceil((double) totalCandidates / pageSize);

        request.setAttribute("applications", applications);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        // Preserve search parameters in request for form
        request.setAttribute(PARAM_SEARCH_BY_NAME, searchByName);
        request.setAttribute(PARAM_FILTER_STATUS, filterStatus);
        request.setAttribute(PARAM_START_DATE, startDate);
        request.setAttribute(PARAM_END_DATE, endDate);
        setMessages(request);
        request.getRequestDispatcher("/Views/HrStaff/ViewCandidate.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!ensureAccess(request, response)) {
            return;
        }
        String searchByName = request.getParameter(PARAM_SEARCH_BY_NAME);
        String filterStatus = request.getParameter(PARAM_FILTER_STATUS);
        String startDate = request.getParameter(PARAM_START_DATE);
        String endDate = request.getParameter(PARAM_END_DATE);

        response.sendRedirect(buildRedirectUrl(request, "/candidates", searchByName, filterStatus, startDate, endDate));
    }

    @Override
    public String getServletInfo() {
        return "Danh sách ứng viên";
    }

    private boolean ensureAccess(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SystemUser currentUser = PermissionUtil.getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + LOGIN_PATH);
            return false;
        }
        return PermissionUtil.ensurePermission(request, response, REQUIRED_PERMISSION, PERMISSION_DENIED_MESSAGE);
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String buildRedirectUrl(HttpServletRequest request,
                                    String path,
                                    String searchByName,
                                    String filterStatus,
                                    String startDate,
                                    String endDate) {
        StringBuilder redirectUrl = new StringBuilder(request.getContextPath()).append(path);
        List<String> queryParts = new ArrayList<>();

        if (searchByName != null && !searchByName.trim().isEmpty()) {
            queryParts.add(PARAM_SEARCH_BY_NAME + "=" + encode(searchByName));
        }
        if (filterStatus != null && !filterStatus.trim().isEmpty()) {
            queryParts.add(PARAM_FILTER_STATUS + "=" + encode(filterStatus));
        }
        if (startDate != null && !startDate.trim().isEmpty()) {
            queryParts.add(PARAM_START_DATE + "=" + encode(startDate));
        }
        if (endDate != null && !endDate.trim().isEmpty()) {
            queryParts.add(PARAM_END_DATE + "=" + encode(endDate));
        }

        if (!queryParts.isEmpty()) {
            redirectUrl.append("?").append(String.join("&", queryParts));
        }
        return redirectUrl.toString();
    }

    private void setMessages(HttpServletRequest request) {
        if ("1".equals(request.getParameter("interviewScheduled"))) {
            request.setAttribute("success", "Đã đặt lịch phỏng vấn cho ứng viên.");
        }
        if ("failed".equals(request.getParameter("mail"))) {
            request.setAttribute("warning", "Lịch đã lưu nhưng chưa gửi được email cho ứng viên. Vui lòng kiểm tra cấu hình mail.");
        }
        String error = request.getParameter("error");
        if ("invalid_application".equals(error)) {
            request.setAttribute("mess", "Không tìm thấy hồ sơ ứng tuyển.");
        }
    }
}


