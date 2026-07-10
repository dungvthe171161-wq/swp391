/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.hrm.controller.hrManager;

import com.hrm.dao.DAO;
import com.hrm.model.entity.Recruitment;
import com.hrm.util.PermissionUtil;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *
 * @author DELL
 */
@WebServlet(name = "ViewRecruitment", urlPatterns = {"/viewRecruitment"})
public class ViewRecruitment extends HttpServlet {

    private static final String REQUIRED_PERMISSION = "VIEW_RECRUITMENT";
    private static final String DENIED_MESSAGE = "You do not have permission to Post Recruitment.";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!ensureAccess(request, response)) {
            return;
        }
        String action = request.getParameter("action");
        if ("apply".equals(action)) {
            try {
                String idStr = request.getParameter("id");
                int recruitmentId = Integer.parseInt(idStr);

                DAO.getInstance().updateRecruitmentStatus(recruitmentId,"Applied");
                request.setAttribute("mess", "Apply recruitment successfully!");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/viewRecruitment");

            }
        }else if("reject".equals(action)){
            try {
                String idStr = request.getParameter("id");
                int recruitmentId = Integer.parseInt(idStr);

                DAO.getInstance().updateRecruitmentStatus(recruitmentId,"Rejected");
                request.setAttribute("mess", "Reject recruitment successfully!");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/viewRecruitment");
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
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");

        List<Recruitment> rList;
        int totalRecruitment;
        boolean hasSearch = (searchByTitle != null && !searchByTitle.trim().isEmpty())
                || (startDate != null && !startDate.trim().isEmpty())
                || (endDate != null && !endDate.trim().isEmpty());

        if (hasSearch) {
            rList = DAO.getInstance().searchRecruitmentWaiting(searchByTitle, startDate, endDate, page, pageSize);
            totalRecruitment = DAO.getInstance().searchCountRecruitmentWaiting(searchByTitle, startDate, endDate);
        } else {
            rList = DAO.getInstance().getAllRecruitmentWaiting(page, pageSize);
            totalRecruitment = DAO.getInstance().getCountRecruitmentWaiting();
        }

        int totalPages = (int) Math.ceil((double) totalRecruitment / pageSize);

        request.setAttribute("recruitment", rList);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("searchByTitle", searchByTitle);
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);

        request.getRequestDispatcher("/Views/hr/ViewRecruitment.jsp").forward(request, response);
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
        return "Short description";
    }

    private boolean ensureAccess(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        return PermissionUtil.ensurePermission(request, response, REQUIRED_PERMISSION, DENIED_MESSAGE);
    }
}
