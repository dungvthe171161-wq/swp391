package com.hrm.controller.dept;

import com.hrm.dao.EmployeeDAO;
import com.hrm.dao.WorkScheduleDAO;
import com.hrm.model.entity.SystemUser;
import com.hrm.util.DeptManagerScope;
import com.hrm.util.PermissionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;

@WebServlet(name = "DeptWorkScheduleController", urlPatterns = {"/dept/schedules"})
public class DeptWorkScheduleController extends HttpServlet {
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final WorkScheduleDAO workScheduleDAO = new WorkScheduleDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DeptManagerScope scope = DeptManagerScope.from(request, employeeDAO);
        if (scope == null || !scope.hasDepartment()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        SystemUser user = PermissionUtil.getCurrentUser(request);
        if (!PermissionUtil.hasPermission(user, "VIEW_WORK_SCHEDULE")) {
            PermissionUtil.handleHtmlForbidden(request, response, "Ban thieu quyen xem lich lam viec.");
            return;
        }
        YearMonth target = parseMonth(request);
        request.setAttribute("activePage", "schedules");
        request.setAttribute("employees", employeeDAO.getByDepartmentId(scope.getDepartmentId()));
        request.setAttribute("workSchedules", workScheduleDAO.getAllActiveSchedules());
        request.setAttribute("employeeSchedules", workScheduleDAO.getByDepartmentMonth(
                scope.getDepartmentId(), target.getYear(), target.getMonthValue()));
        request.setAttribute("month", target.getMonthValue());
        request.setAttribute("year", target.getYear());
        request.getRequestDispatcher("/Views/DeptManager/schedules.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DeptManagerScope scope = DeptManagerScope.from(request, employeeDAO);
        if (scope == null || !scope.hasDepartment()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        SystemUser user = PermissionUtil.getCurrentUser(request);
        if (!PermissionUtil.hasPermission(user, "MANAGE_WORK_SCHEDULE")) {
            PermissionUtil.handleHtmlForbidden(request, response, "Ban thieu quyen quan ly lich lam viec.");
            return;
        }
        String action = request.getParameter("action");
        boolean success = false;
        try {
            if ("assign".equals(action)) {
                int employeeId = parseInt(request.getParameter("employeeId"));
                if (workScheduleDAO.employeeBelongsToDepartment(employeeId, scope.getDepartmentId())) {
                    success = workScheduleDAO.assignSchedule(employeeId, parseInt(request.getParameter("scheduleId")),
                            LocalDate.parse(request.getParameter("workDate")), request.getParameter("note"),
                            scope.getApproverEmployeeId());
                }
            } else if ("update".equals(action)) {
                success = workScheduleDAO.updateAssignmentScoped(parseInt(request.getParameter("assignmentId")),
                        scope.getDepartmentId(), parseInt(request.getParameter("scheduleId")),
                        LocalDate.parse(request.getParameter("workDate")), request.getParameter("note"));
            } else if ("delete".equals(action)) {
                success = workScheduleDAO.deleteAssignmentScoped(parseInt(request.getParameter("assignmentId")),
                        scope.getDepartmentId());
            }
        } catch (RuntimeException ex) {
            success = false;
        }
        request.getSession().setAttribute(success ? "deptSuccess" : "deptError",
                success ? "Da cap nhat lich lam viec." : "Khong the cap nhat lich lam viec.");
        response.sendRedirect(request.getContextPath() + "/dept/schedules");
    }

    private YearMonth parseMonth(HttpServletRequest request) {
        try {
            int month = Integer.parseInt(request.getParameter("month"));
            int year = Integer.parseInt(request.getParameter("year"));
            return YearMonth.of(year, month);
        } catch (RuntimeException ex) {
            return YearMonth.now();
        }
    }

    private int parseInt(String value) {
        return Integer.parseInt(value);
    }
}
