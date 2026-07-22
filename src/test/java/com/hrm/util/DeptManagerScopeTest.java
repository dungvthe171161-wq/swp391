package com.hrm.util;

import com.hrm.dao.EmployeeDAO;
import com.hrm.model.entity.Employee;
import com.hrm.model.entity.Role;
import com.hrm.model.entity.SystemUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Unit Test: DeptManagerScope - 100% Branch Coverage")
public class DeptManagerScopeTest {

    private SystemUser createUser(int userId, int roleId, Integer employeeId) {
        SystemUser user = new SystemUser();
        user.setUserId(userId);
        user.setRoleId(roleId);
        user.setEmployeeId(employeeId);
        Role r = new Role();
        r.setRoleId(roleId);
        user.setRole(r);
        return user;
    }

    @Test
    void testFromBranches() {
        EmployeeDAO dao = mock(EmployeeDAO.class);
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpSession sess = mock(HttpSession.class);

        // Session null
        when(req.getSession(false)).thenReturn(null);
        assertNull(DeptManagerScope.from(req, dao));

        // Session user null
        when(req.getSession(false)).thenReturn(sess);
        when(sess.getAttribute("systemUser")).thenReturn(null);
        assertNull(DeptManagerScope.from(req, dao));

        // Invalid role (Employee role 5)
        SystemUser empUser = createUser(1, 5, 10);
        when(sess.getAttribute("systemUser")).thenReturn(empUser);
        assertNull(DeptManagerScope.from(req, dao));

        // Valid Dept Manager role (role 3) with null employeeId
        SystemUser deptMgrNoEmp = createUser(2, 3, null);
        when(sess.getAttribute("systemUser")).thenReturn(deptMgrNoEmp);
        DeptManagerScope scope1 = DeptManagerScope.from(req, dao);
        assertNotNull(scope1);
        assertFalse(scope1.hasDepartment());
        assertEquals(0, scope1.getApproverEmployeeId());

        // Valid Dept Manager role (role 3) with valid employeeId & employee in DB
        SystemUser deptMgr = createUser(3, 3, 100);
        Employee emp = new Employee();
        emp.setEmployeeId(100);
        emp.setDepartmentId(10);
        when(sess.getAttribute("systemUser")).thenReturn(deptMgr);
        when(dao.getById(100)).thenReturn(emp);

        DeptManagerScope scope2 = DeptManagerScope.from(req, dao);
        assertNotNull(scope2);
        assertTrue(scope2.hasDepartment());
        assertFalse(scope2.isAdmin());
        assertEquals(10, scope2.getDepartmentId());
        assertEquals(100, scope2.getApproverEmployeeId());
        assertEquals(deptMgr, scope2.getUser());
        assertEquals(emp, scope2.getEmployee());

        // Admin role (role 1)
        SystemUser admin = createUser(4, 1, 200);
        when(sess.getAttribute("systemUser")).thenReturn(admin);
        when(dao.getById(200)).thenReturn(null);
        DeptManagerScope scope3 = DeptManagerScope.from(req, dao);
        assertNotNull(scope3);
        assertTrue(scope3.isAdmin());
    }
}
