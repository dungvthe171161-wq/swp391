package com.hrm.util;

import com.hrm.dao.EmployeeDAO;
import com.hrm.model.entity.Employee;
import com.hrm.model.entity.SystemUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public final class DeptManagerScope {

    private final SystemUser user;
    private final Employee employee;
    private final int departmentId;

    private DeptManagerScope(SystemUser user, Employee employee, int departmentId) {
        this.user = user;
        this.employee = employee;
        this.departmentId = departmentId;
    }

    public static DeptManagerScope from(HttpServletRequest request, EmployeeDAO employeeDAO) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        SystemUser user = (SystemUser) session.getAttribute("systemUser");
        if (user == null) {
            return null;
        }

        int roleId = user.getRoleId();
        if (roleId != PermissionUtil.ROLE_ADMIN && roleId != PermissionUtil.ROLE_DEPT_MANAGER) {
            return null;
        }

        Integer employeeId = user.getEmployeeId();
        if (employeeId == null || employeeId <= 0) {
            return new DeptManagerScope(user, null, 0);
        }

        Employee employee = employeeDAO.getById(employeeId);
        int departmentId = employee != null ? employee.getDepartmentId() : 0;
        return new DeptManagerScope(user, employee, departmentId);
    }

    public boolean hasDepartment() {
        return departmentId > 0;
    }

    public boolean isAdmin() {
        return user != null && user.getRoleId() == PermissionUtil.ROLE_ADMIN;
    }

    public SystemUser getUser() {
        return user;
    }

    public Employee getEmployee() {
        return employee;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public int getApproverEmployeeId() {
        return employee != null ? employee.getEmployeeId() : 0;
    }
}
