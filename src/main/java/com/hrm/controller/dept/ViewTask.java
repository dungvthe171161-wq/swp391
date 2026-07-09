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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ViewTask", urlPatterns = {"/viewTask"})
public class ViewTask extends HttpServlet {

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

        int taskId = parseInt(request.getParameter("id"), -1);
        Task task = DAO.getInstance().getTaskById(taskId);
        if (!isOwnedByManager(task, scope.getApproverEmployeeId())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        List<Employee> employeeList = DAO.getInstance().loadEmpFollowDepartment(scope.getDepartmentId());
        List<Integer> assignedEmployeeIds = DAO.getInstance().getEmployeeIdsByTaskId(taskId);
        List<Employee> assignedEmployees = new ArrayList<>();
        for (Integer empId : assignedEmployeeIds) {
            Employee assignedEmp = DAO.getInstance().getEmp(empId);
            if (assignedEmp != null && assignedEmp.getDepartmentId() == scope.getDepartmentId()) {
                assignedEmployees.add(assignedEmp);
            }
        }

        request.setAttribute("task", task);
        request.setAttribute("employeeList", employeeList);
        request.setAttribute("assignedEmployeeIds", assignedEmployeeIds);
        request.setAttribute("assignedEmployees", assignedEmployees);
        request.setAttribute("activePage", "tasks");
        request.setAttribute("pageTitle", "Chi tiết công việc");
        request.setAttribute("pageSubtitle", "Cập nhật nội dung và người phụ trách trong phòng ban.");
        if (scope.getEmployee() != null) {
            request.setAttribute("userName", scope.getEmployee().getFullName());
            request.setAttribute("userPosition", scope.getEmployee().getPosition());
        }
        request.getRequestDispatcher("/Views/DeptManager/viewTask.jsp").forward(request, response);
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

        int taskId = parseInt(request.getParameter("taskId"), -1);
        Task task = DAO.getInstance().getTaskById(taskId);
        if (!isOwnedByManager(task, scope.getApproverEmployeeId())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String title = clean(request.getParameter("title"));
        String description = clean(request.getParameter("description"));
        String startDate = clean(request.getParameter("startDate"));
        String dueDate = clean(request.getParameter("dueDate"));

        if (title == null || title.isBlank() || title.length() > 50
                || startDate == null || dueDate == null || startDate.compareTo(dueDate) > 0) {
            response.sendRedirect(request.getContextPath() + "/viewTask?id=" + taskId + "&error=" + URLEncoder.encode("Dữ liệu công việc không hợp lệ", StandardCharsets.UTF_8));
            return;
        }

        boolean success = DAO.getInstance().updateTask(taskId, title, description, startDate, dueDate);
        if (success) {
            DAO.getInstance().deleteTaskAssignments(taskId);
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
            response.sendRedirect(request.getContextPath() + "/viewTask?id=" + taskId + "&mess=" + URLEncoder.encode("Đã cập nhật công việc thành công", StandardCharsets.UTF_8));
        } else {
            response.sendRedirect(request.getContextPath() + "/viewTask?id=" + taskId + "&error=" + URLEncoder.encode("Không thể cập nhật công việc", StandardCharsets.UTF_8));
        }
    }

    private boolean isOwnedByManager(Task task, int managerEmployeeId) {
        return task != null && task.getAssignedBy() == managerEmployeeId;
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
