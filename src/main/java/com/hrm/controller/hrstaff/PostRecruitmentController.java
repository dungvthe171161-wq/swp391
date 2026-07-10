/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.hrm.controller.hrstaff;

import com.hrm.dao.DAO;
import com.hrm.model.entity.Recruitment;
import com.hrm.model.entity.SystemUser;
import com.hrm.util.PermissionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author DELL
 */
@WebServlet(name = "PostRecruitmentController", urlPatterns = {"/postRecruitments"})
public class PostRecruitmentController extends HttpServlet {

    private static final String REQUIRED_PERMISSION = "VIEW_RECRUITMENT";
    private static final String REQUIRED_ROLE_MESSAGE = "Khu vực này chỉ dành cho nhân viên nhân sự.";
    private static final String PERMISSION_DENIED_MESSAGE = "Bạn không có quyền quản lý tin tuyển dụng.";
    private static final String LOGIN_PATH = "/login";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!ensureAccess(request, response)) {
            return;
        }
        String action = request.getParameter("action");
        if ("send".equals(action)) {
            try {
                String idStr = request.getParameter("id");
                int recruitmentId = Integer.parseInt(idStr);

                DAO.getInstance().updateRecruitmentStatus(recruitmentId, "Waiting");
                request.setAttribute("mess", "Gửi duyệt thành công!");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/postRecruitments");
                return;
            }
        } else if ("delete".equals(action)) {
            try {
                String idStr = request.getParameter("id");
                int recruitmentId = Integer.parseInt(idStr);

                DAO.getInstance().updateRecruitmentStatus(recruitmentId, "Deleted");
                request.setAttribute("mess", "Xóa tin tuyển dụng thành công!");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/postRecruitments");
                return;
            }
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
        String searchByTitle = request.getParameter("searchByTitle");
        String filterStatus = request.getParameter("filterStatus");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");

        List<Recruitment> rList;
        int totalRecruitment;
        boolean hasSearch = (searchByTitle != null && !searchByTitle.trim().isEmpty())
                || (filterStatus != null && !filterStatus.trim().isEmpty())
                || (startDate != null && !startDate.trim().isEmpty())
                || (endDate != null && !endDate.trim().isEmpty());

        if (hasSearch) {
            rList = DAO.getInstance().searchRecruitment(searchByTitle, filterStatus, startDate, endDate, page, pageSize);
            totalRecruitment = DAO.getInstance().searchCountRecruitment(searchByTitle, filterStatus, startDate, endDate);
        } else {
            rList = DAO.getInstance().getAllRecruitment(page, pageSize);
            totalRecruitment = DAO.getInstance().getCountRecruitment();
        }

        int totalPages = (int) Math.ceil((double) totalRecruitment / pageSize);

        request.setAttribute("recruitment", rList);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("searchByTitle", searchByTitle);
        request.setAttribute("filterStatus", filterStatus);
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        request.getRequestDispatcher("/Views/HrStaff/PostRecruitment.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!ensureAccess(request, response)) {
            return;
        }

    }

    @Override
    public String getServletInfo() {
        return "Danh sách tin tuyển dụng";
    }

    private boolean ensureAccess(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SystemUser currentUser = PermissionUtil.getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + LOGIN_PATH);
            return false;
        }
        return PermissionUtil.ensureRolePermission(
                request,
                response,
                PermissionUtil.ROLE_HR_STAFF,
                REQUIRED_PERMISSION,
                REQUIRED_ROLE_MESSAGE,
                PERMISSION_DENIED_MESSAGE
        );
    }
}


