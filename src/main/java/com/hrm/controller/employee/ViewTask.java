/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.hrm.controller.employee;

import com.hrm.dao.DAO;
import com.hrm.model.entity.Employee;
import com.hrm.model.entity.Task;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author DELL
 */
@WebServlet(name="ViewTask", urlPatterns={"/viewTask"})
public class ViewTask extends HttpServlet {
   
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
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("systemUser") == null) {
            response.sendRedirect(request.getContextPath() + "/Views/Login.jsp");
            return;
        }
        
        // Kiểm tra action parameter - nếu là "view" thì dùng logic này (cho employee)
        String action = request.getParameter("action");
        if (action != null && "view".equals(action)) {
            String taskIdStr = request.getParameter("id");
            if (taskIdStr == null || taskIdStr.trim().isEmpty()) {
                request.setAttribute("error", "Task ID is required!");
                request.getRequestDispatcher("/Views/Employee/ViewTask.jsp").forward(request, response);
                return;
            }

            try {
                int taskId = Integer.parseInt(taskIdStr);
                Task task = DAO.getInstance().getTaskById(taskId);
                
                if (task == null) {
                    request.setAttribute("error", "Task not found!");
                    request.getRequestDispatcher("/Views/Employee/ViewTask.jsp").forward(request, response);
                    return;
                }

                // Lấy thông tin người assigned by
                Employee assignedByEmployee = null;
                if (task.getAssignedBy() > 0) {
                    assignedByEmployee = DAO.getInstance().getEmp(task.getAssignedBy());
                }

                // Lấy danh sách employees được assign cho task này
                List<Integer> assignedEmpIds = DAO.getInstance().getEmployeeIdsByTaskId(taskId);
                List<Employee> assignedEmployees = new ArrayList<>();
                for (Integer empId : assignedEmpIds) {
                    Employee assignedEmp = DAO.getInstance().getEmp(empId);
                    if (assignedEmp != null) {
                        assignedEmployees.add(assignedEmp);
                    }
                }

                request.setAttribute("task", task);
                request.setAttribute("assignedByEmployee", assignedByEmployee);
                request.setAttribute("assignedEmployees", assignedEmployees);
                request.getRequestDispatcher("/Views/Employee/ViewTask.jsp").forward(request, response);
                
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid Task ID!");
                request.getRequestDispatcher("/Views/Employee/ViewTask.jsp").forward(request, response);
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "An error occurred: " + e.getMessage());
                request.getRequestDispatcher("/Views/Employee/ViewTask.jsp").forward(request, response);
            }
        } else {
            // Nếu không phải action=view, forward đến dept ViewTask (nếu có)
            // Hoặc hiển thị lỗi
            request.setAttribute("error", "Invalid action!");
            request.getRequestDispatcher("/Views/Employee/ViewTask.jsp").forward(request, response);
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
        // Employee chỉ xem, không có POST action
        doGet(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Employee View Task Controller";
    }
}
