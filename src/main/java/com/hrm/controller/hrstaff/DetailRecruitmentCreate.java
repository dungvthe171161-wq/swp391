/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.hrm.controller.hrstaff;

import com.hrm.dao.DAO;
import com.hrm.model.entity.SystemUser;
import com.hrm.util.PermissionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 *
 * @author DELL
 */
@WebServlet(name = "DetailRecruitmentCreate", urlPatterns = {"/detailRecruitmentCreate"})
public class DetailRecruitmentCreate extends HttpServlet {

    private static final String REQUIRED_PERMISSION = "VIEW_RECRUITMENT";
    private static final String REQUIRED_ROLE_MESSAGE = "Khu vực này chỉ dành cho nhân viên nhân sự.";
    private static final String PERMISSION_DENIED_MESSAGE = "Bạn không có quyền quản lý tin tuyển dụng.";
    private static final String LOGIN_PATH = "/login";
    private static final String CREATE_VIEW = "/Views/HrStaff/CreateNewRecruitment.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!ensureAccess(request, response)) {
            return;
        }
        request.getRequestDispatcher(CREATE_VIEW).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!ensureAccess(request, response)) {
            return;
        }
        String title = request.getParameter("Title");
        String description = request.getParameter("Description");
        String requirement = request.getParameter("Requirement");
        String location = request.getParameter("Location");
        String salaryStr = request.getParameter("Salary");
        String applicantStr = request.getParameter("Applicant");

        request.setAttribute("title", title);
        request.setAttribute("description", description);
        request.setAttribute("requirement", requirement);
        request.setAttribute("location", location);
        request.setAttribute("salary", salaryStr);
        request.setAttribute("applicant", applicantStr);

        double salary;
        int applicant;
        try {
            salary = Double.parseDouble(salaryStr);
            applicant = Integer.parseInt(applicantStr);
        } catch (NumberFormatException ex) {
            request.setAttribute("mess", "Mức lương và số lượng tuyển phải là số hợp lệ.");
            request.getRequestDispatcher(CREATE_VIEW).forward(request, response);
            return;
        }

        if (salary <= 0 || applicant <= 0) {
            request.setAttribute("mess", "Mức lương và số lượng tuyển phải là số dương!");
            request.getRequestDispatcher(CREATE_VIEW).forward(request, response);
            return;
        }

        int result = DAO.getInstance()
                .createRecruitment(title, description, requirement, location, salary, LocalDateTime.now(), applicant);

        if (result > 0) {
            response.sendRedirect(request.getContextPath() + "/postRecruitments");
        } else {
            request.setAttribute("mess", "Không thể tạo tin tuyển dụng. Vui lòng thử lại.");
            request.getRequestDispatcher(CREATE_VIEW).forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Tạo tin tuyển dụng";
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

