package com.hrm.controller.dept;

import com.hrm.dao.DAO;
import com.hrm.dao.EmployeeDAO;
import com.hrm.model.entity.Employee;
import com.hrm.util.DeptManagerScope;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet(name = "postTask", urlPatterns = {"/postTask"})
public class PostTask extends HttpServlet {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DeptManagerScope scope = DeptManagerScope.from(request, employeeDAO);
        if (scope == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (!scope.hasDepartment()) {
            response.sendRedirect(request.getContextPath() + "/dept");
            return;
        }

        List<Employee> employeeList = DAO.getInstance().loadEmpFollowDepartment(scope.getDepartmentId());
        request.setAttribute("employeeList", employeeList);
        request.setAttribute("activePage", "tasks");
        request.setAttribute("pageTitle", "Tạo công việc");
        request.setAttribute("pageSubtitle", "Giao công việc cho nhân viên trong phòng ban.");
        if (scope.getEmployee() != null) {
            request.setAttribute("userName", scope.getEmployee().getFullName());
            request.setAttribute("userPosition", scope.getEmployee().getPosition());
        }
        request.getRequestDispatcher("/Views/DeptManager/postTask.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DeptManagerScope scope = DeptManagerScope.from(request, employeeDAO);
        if (scope == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (!scope.hasDepartment()) {
            response.sendRedirect(request.getContextPath() + "/dept");
            return;
        }

        String title = clean(request.getParameter("title"));
        String description = clean(request.getParameter("description"));
        String startDate = clean(request.getParameter("startDate"));
        String dueDate = clean(request.getParameter("dueDate"));

        String error = validate(title, description, startDate, dueDate);
        if (error != null) {
            response.sendRedirect(request.getContextPath() + "/postTask?error=" + URLEncoder.encode(error, StandardCharsets.UTF_8));
            return;
        }

        int taskId = DAO.getInstance().createTask(title, description, scope.getApproverEmployeeId(), startDate, dueDate);
        if (taskId <= 0) {
            response.sendRedirect(request.getContextPath() + "/postTask?error=" + URLEncoder.encode("Không thể tạo công việc", StandardCharsets.UTF_8));
            return;
        }

        String[] assignToIds = request.getParameterValues("assignTo");
        if (assignToIds != null) {
            for (String empIdStr : assignToIds) {
                int empId = parseInt(empIdStr, -1);
                Employee assignee = DAO.getInstance().getEmp(empId);
                if (assignee != null && assignee.getDepartmentId() == scope.getDepartmentId()) {
                    DAO.getInstance().assignTaskToEmployee(taskId, empId);
                }
            }
        }

        response.sendRedirect(request.getContextPath() + "/taskManager?mess=" + URLEncoder.encode("Đã tạo công việc thành công", StandardCharsets.UTF_8));
    }

    private String validate(String title, String description, String startDate, String dueDate) {
        if (title == null || title.isBlank() || title.length() > 50) {
            return "Title is required and must be 50 characters or fewer";
        }
        if (description != null && description.length() > 1000) {
            return "Description must be 1000 characters or fewer";
        }
        if (startDate == null || startDate.isBlank() || dueDate == null || dueDate.isBlank()) {
            return "Start date and due date are required";
        }
        if (startDate.compareTo(dueDate) > 0) {
            return "Start date must be before due date";
        }
        return null;
    }

    private String clean(String value) {
        return value == null ? null : value.trim();
    }

    private int parseInt(String value, int fallback) {
        try {
            return value == null || value.isBlank() ? fallback : Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }
}
