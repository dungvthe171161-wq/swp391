/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.hrm.controller.hr;

import com.hrm.dao.DAO;
import com.hrm.model.entity.Recruitment;
import com.hrm.util.PermissionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author DELL
 */
@WebServlet(name = "DetailRecruitment", urlPatterns = {"/detailRecruitment"})
public class DetailRecruitment extends HttpServlet {

    private static final String REQUIRED_PERMISSION = "VIEW_RECRUITMENT";
    private static final String DENIED_MESSAGE = "You do not have permission to view or edit recruitment details.";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!ensureAccess(request, response)) {
            return;
        }
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Recruitment rec = DAO.getInstance().getRecruitmentById(id);
            request.setAttribute("rec", rec);
            request.getRequestDispatcher("Views/hr/DetailRecruitment.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/postRecruitments?error=invalidid");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!ensureAccess(request, response)) {
            return;
        }
        String idParam = request.getParameter("id");

        try {
            int id = Integer.parseInt(idParam);
                String title = request.getParameter("Title");
                String description = request.getParameter("Description");
                String requirement = request.getParameter("Requirement");
                String location = request.getParameter("Location");
                double salary = Double.parseDouble(request.getParameter("Salary"));
                int applicant = Integer.parseInt(request.getParameter("Applicant"));

                String message = null;
                if (title != null && title.length() > 50) {
                    message = "Title must not exceed 50 characters!";
                } else if (requirement != null && requirement.length() > 50) {
                    message = "Requirement must not exceed 50 characters!";
                } else if (location != null && location.length() > 50) {
                    message = "Location must not exceed 50 characters!";
                } else if (description != null && description.length() > 500) {
                    message = "Description must not exceed 500 characters!";
                }

                if (message != null) {
                    request.setAttribute("mess", message);
                    Recruitment rec = DAO.getInstance().getRecruitmentById(id);
                    request.setAttribute("rec", rec);
                    request.getRequestDispatcher("Views/hr/DetailRecruitment.jsp").forward(request, response);
                    return;
                }

                if (salary <= 0) {
                    request.setAttribute("mess", "Salary must be a positive number!");
                    Recruitment rec = DAO.getInstance().getRecruitmentById(id);
                    request.setAttribute("rec", rec);
                    request.getRequestDispatcher("Views/hr/DetailRecruitment.jsp").forward(request, response);
                    return;
                }
                
                if (applicant <= 0) {
                    request.setAttribute("mess", "Applicant must be a positive number!");
                    Recruitment rec = DAO.getInstance().getRecruitmentById(id);
                    request.setAttribute("rec", rec);
                    request.getRequestDispatcher("Views/hr/DetailRecruitment.jsp").forward(request, response);
                    return;
                }

                int update = DAO.getInstance().setRecruitmentById(title, description, requirement, location, salary, applicant,id);
                if (update > 0) {
                    String successMessage = URLEncoder.encode("Save recruitment successfully!", StandardCharsets.UTF_8);
                    response.sendRedirect(request.getContextPath() + "/postRecruitments?mess=" + successMessage);
                    return;
                }

                request.setAttribute("mess", "No changes were saved.");
                Recruitment rec = DAO.getInstance().getRecruitmentById(id);
                request.setAttribute("rec", rec);
                request.getRequestDispatcher("Views/hr/DetailRecruitment.jsp").forward(request, response);
                
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/postRecruitments?error=invalidformat");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/postRecruitments?error=servererror");
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

    private boolean ensureAccess(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        return PermissionUtil.ensureRolePermission(
                request,
                response,
                PermissionUtil.ROLE_HR_STAFF,
                REQUIRED_PERMISSION,
                "This section is restricted to HR Staff.",
                DENIED_MESSAGE
        );
    }
}
