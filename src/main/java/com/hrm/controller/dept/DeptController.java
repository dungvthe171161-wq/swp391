package com.hrm.controller.dept;

import com.hrm.dao.DepartmentDAO;
import com.hrm.dao.DeptDashboardDAO;
import com.hrm.dao.EmployeeDAO;
import com.hrm.dao.MailRequestDAO;
import com.hrm.model.entity.Department;
import com.hrm.model.entity.Employee;
import com.hrm.util.DeptManagerScope;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "DeptController", urlPatterns = {"/dept"})
public class DeptController extends HttpServlet {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final DeptDashboardDAO deptDashboardDAO = new DeptDashboardDAO();
    private final MailRequestDAO mailRequestDAO = new MailRequestDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DeptManagerScope scope = DeptManagerScope.from(request, employeeDAO);
        if (scope == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (!scope.hasDepartment()) {
            request.setAttribute("activePage", "dashboard");
            request.setAttribute("pageTitle", "Tổng quan nhóm");
            request.setAttribute("pageSubtitle", "Tài khoản cần được gán với nhân viên và phòng ban.");
            request.setAttribute("scopeError", "Tài khoản chưa được gán phòng ban.");
            request.getRequestDispatcher("/Views/DeptManager/deptHome.jsp").forward(request, response);
            return;
        }

        String action = request.getParameter("action");
        if (action == null || action.isBlank()) {
            action = "dashboard";
        }

        switch (action) {
            case "employees":
                loadEmployees(request, response, scope);
                break;
            case "calendar":
                loadSimpleDeptPage(request, response, scope, "calendar", "Lịch nhóm",
                        "Lịch nghỉ đã duyệt và các mốc công việc của phòng ban.",
                        "/Views/DeptManager/calendar.jsp");
                break;
            case "performance":
                loadSimpleDeptPage(request, response, scope, "performance", "Đánh giá hiệu suất",
                        "Theo dõi hiệu suất theo nhân viên trong phòng ban.",
                        "/Views/DeptManager/performance.jsp");
                break;
            case "reports":
                loadSimpleDeptPage(request, response, scope, "reports", "Báo cáo phòng ban",
                        "Tổng hợp nhân viên, công việc và nghỉ phép.",
                        "/Views/DeptManager/reports.jsp");
                break;
            case "dashboard":
            default:
                loadDashboard(request, response, scope);
                break;
        }
    }

    private void loadDashboard(HttpServletRequest request, HttpServletResponse response, DeptManagerScope scope)
            throws ServletException, IOException {
        prepareCommonAttributes(request, scope, "dashboard", "Tổng quan nhóm",
                "Theo dõi nhân viên, công việc và nghỉ phép trong phòng ban.");
        request.getRequestDispatcher("/Views/DeptManager/deptHome.jsp").forward(request, response);
    }

    private void loadEmployees(HttpServletRequest request, HttpServletResponse response, DeptManagerScope scope)
            throws ServletException, IOException {
        prepareCommonAttributes(request, scope, "employees", "Nhân viên phòng ban",
                "Danh sách nhân viên thuộc phòng ban của bạn.");
        request.getRequestDispatcher("/Views/DeptManager/employees.jsp").forward(request, response);
    }

    private void loadSimpleDeptPage(HttpServletRequest request, HttpServletResponse response, DeptManagerScope scope,
                                    String activePage, String title, String subtitle, String jsp)
            throws ServletException, IOException {
        prepareCommonAttributes(request, scope, activePage, title, subtitle);
        request.getRequestDispatcher(jsp).forward(request, response);
    }

    private void prepareCommonAttributes(HttpServletRequest request, DeptManagerScope scope,
                                         String activePage, String title, String subtitle) {
        int departmentId = scope.getDepartmentId();
        int managerEmployeeId = scope.getApproverEmployeeId();
        Department department = departmentDAO.getById(departmentId);
        Employee employee = scope.getEmployee();
        List<Employee> employees = employeeDAO.getByDepartmentId(departmentId);
        Map<String, Integer> dashboardCounts = deptDashboardDAO.getDashboardCounts(departmentId, managerEmployeeId);

        request.setAttribute("activePage", activePage);
        request.setAttribute("pageTitle", title);
        request.setAttribute("pageSubtitle", subtitle);
        request.setAttribute("departmentName", department != null ? department.getDeptName() : "N/A");
        request.setAttribute("dashboardCounts", dashboardCounts);
        request.setAttribute("employees", employees);
        request.setAttribute("approvedLeaves", mailRequestDAO.getLeaveRequestsByDepartment(departmentId, "Approved"));
        request.setAttribute("userName", employee != null && employee.getFullName() != null
                ? employee.getFullName()
                : scope.getUser().getUsername());
        request.setAttribute("userPosition", employee != null && employee.getPosition() != null
                ? employee.getPosition()
                : "Dept Manager");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
