package com.hrm.controller.dept;

import com.hrm.dao.DAO;
import com.hrm.dao.EmployeeDAO;
import com.hrm.model.entity.Employee;
import com.hrm.model.entity.Task;
import com.hrm.util.DeptManagerScope;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "TaskManager", urlPatterns = {"/taskManager"})
public class TaskManager extends HttpServlet {

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

        int managerEmployeeId = scope.getApproverEmployeeId();
        String action = request.getParameter("action");

        if ("viewAssignees".equals(action)) {
            renderAssignees(request, response, scope, managerEmployeeId);
            return;
        }
        if ("reject".equals(action) || "send".equals(action)) {
            updateTaskStatus(request, response, managerEmployeeId, action);
            return;
        }

        int page = parseInt(request.getParameter("page"), 1);
        int pageSize = 5;
        String searchByTitle = request.getParameter("searchByTitle");
        String filterStatus = request.getParameter("filterStatus");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");

        boolean hasSearch = hasText(searchByTitle)
                || (hasText(filterStatus) && !"all".equalsIgnoreCase(filterStatus))
                || hasText(startDate)
                || hasText(endDate);

        List<Task> tasks;
        int total;
        if (hasSearch) {
            tasks = DAO.getInstance().searchTasks(managerEmployeeId, searchByTitle, filterStatus, startDate, endDate, page, pageSize);
            total = DAO.getInstance().searchCountTasks(managerEmployeeId, searchByTitle, filterStatus, startDate, endDate);
        } else {
            tasks = DAO.getInstance().getAllTasks(managerEmployeeId, page, pageSize);
            total = DAO.getInstance().getCountTasks(managerEmployeeId);
        }

        int totalPages = Math.max(1, (int) Math.ceil((double) total / pageSize));
        request.setAttribute("tasks", tasks);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("searchByTitle", searchByTitle);
        request.setAttribute("filterStatus", filterStatus);
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        request.setAttribute("activePage", "tasks");
        request.setAttribute("pageTitle", "Quản lý công việc");
        request.setAttribute("pageSubtitle", "Tạo, giao và theo dõi công việc của phòng ban.");
        if (scope.getEmployee() != null) {
            request.setAttribute("userName", scope.getEmployee().getFullName());
            request.setAttribute("userPosition", scope.getEmployee().getPosition());
        }

        request.getRequestDispatcher("/Views/DeptManager/taskManager.jsp").forward(request, response);
    }

    private void renderAssignees(HttpServletRequest request, HttpServletResponse response,
                                 DeptManagerScope scope, int managerEmployeeId) throws IOException {
        int taskId = parseInt(request.getParameter("id"), -1);
        Task task = DAO.getInstance().getTaskById(taskId);
        if (!isOwnedByManager(task, managerEmployeeId)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        List<Employee> employees = new ArrayList<>();
        for (Integer empId : DAO.getInstance().getEmployeeIdsByTaskId(taskId)) {
            Employee emp = DAO.getInstance().getEmp(empId);
            if (emp != null && emp.getDepartmentId() == scope.getDepartmentId()) {
                employees.add(emp);
            }
        }

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        if (employees.isEmpty()) {
            out.println("<div class='dept-alert error'>Chưa có nhân viên được giao.</div>");
            return;
        }
        for (Employee emp : employees) {
            out.println("<div class='employee-name'>" + escapeHtml(emp.getFullName()) + "</div>");
        }
    }

    private void updateTaskStatus(HttpServletRequest request, HttpServletResponse response,
                                  int managerEmployeeId, String action) throws IOException {
        int taskId = parseInt(request.getParameter("id"), -1);
        Task task = DAO.getInstance().getTaskById(taskId);
        if (!isOwnedByManager(task, managerEmployeeId)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String status = "send".equals(action) ? "Waiting" : "Rejected";
        DAO.getInstance().updateTaskStatus(taskId, status);
        response.sendRedirect(request.getContextPath() + "/taskManager");
    }

    private boolean isOwnedByManager(Task task, int managerEmployeeId) {
        return task != null && task.getAssignedBy() == managerEmployeeId;
    }

    private int parseInt(String value, int fallback) {
        try {
            return value == null || value.isBlank() ? fallback : Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
