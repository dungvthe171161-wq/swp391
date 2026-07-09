/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.hrm.controller.hrManager;

import com.hrm.dao.DAO;
import com.hrm.model.entity.Recruitment;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author DELL
 */
@WebServlet(name="detailWaitingRecruitment", urlPatterns={"/detailWaitingRecruitment"})
public class DetailWaitingRecruitment extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Recruitment rec = DAO.getInstance().getRecruitmentById(id);
            request.setAttribute("rec", rec);
            request.getRequestDispatcher("Views/hr/DetailWaitingRecruitment.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
    } 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
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
                    message = "Title cannot exceed 50 characters.";
                } else if (requirement != null && requirement.length() > 50) {
                    message = "Requirement cannot exceed 50 characters.";
                } else if (location != null && location.length() > 50) {
                    message = "Location cannot exceed 50 characters.";
                } else if (description != null && description.length() > 500) {
                    message = "Description cannot exceed 500 characters.";
                }

                if (message != null) {
                    request.setAttribute("mess", message);
                    Recruitment rec = DAO.getInstance().getRecruitmentById(id);
                    request.setAttribute("rec", rec);
                    request.getRequestDispatcher("Views/hr/DetailWaitingRecruitment.jsp").forward(request, response);
                    return;
                }

                if (salary <= 0 || applicant<=0) {
                    request.setAttribute("mess", "Salary and applicant must be a positive number.");
                    Recruitment rec = DAO.getInstance().getRecruitmentById(id);
                    request.setAttribute("rec", rec);
                    request.getRequestDispatcher("Views/hr/DetailWaitingRecruitment.jsp").forward(request, response);
                    return;
                }

                DAO.getInstance().setRecruitmentById(title, description, requirement, location, salary, applicant,id);
                response.sendRedirect(request.getContextPath() + "/detailWaitingRecruitment?id=" + id);
                
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
