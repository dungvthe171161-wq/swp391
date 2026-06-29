# Dept Manager Portal Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Hoan thien cong Dept Manager voi shell chung, task management theo phong ban, danh sach nhan vien, duyet nghi phep, lich nhom, performance va report.

**Architecture:** Giu Servlet/JSP/JDBC hien co, them cac controller/DAO nho theo module Dept Manager thay vi day them logic moi vao JSP. Moi request Dept Manager di qua helper scope de lay `DepartmentID`, sau do DAO loc bang department scope truoc khi hien thi hoac update.

**Tech Stack:** Java 17, Jakarta Servlet, JSP/JSTL, JDBC DAO, MySQL, Maven.

---

## File Structure

- Modify: `src/main/java/com/hrm/controller/dept/DeptController.java`
  - Dieu phoi dashboard, employees, calendar, performance, reports neu giu mot controller tong.
- Modify: `src/main/java/com/hrm/controller/dept/TaskManager.java`
  - Dua auth/scope len dau request va loc task theo Dept Manager.
- Modify: `src/main/java/com/hrm/controller/dept/PostTask.java`
  - Tao task voi validation va assignee cung phong ban.
- Modify: `src/main/java/com/hrm/controller/dept/ViewTask.java`
  - Xem/sua task voi scope guard.
- Create: `src/main/java/com/hrm/controller/dept/DeptLeaveController.java`
  - Trang duyet/tui choi don nghi phep theo phong ban.
- Create: `src/main/java/com/hrm/util/DeptManagerScope.java`
  - Helper lay current user, employee, department va check role.
- Create: `src/main/java/com/hrm/dao/DeptDashboardDAO.java`
  - Count KPI dashboard/report.
- Create or modify: `src/main/java/com/hrm/dao/DeptTaskDAO.java`
  - Task queries scoped by manager/department. Neu project da co `TaskDAO` dung tot, bo sung vao `TaskDAO` thay vi tao file moi.
- Modify: `src/main/java/com/hrm/dao/MailRequestDAO.java`
  - Them methods lay/update leave theo department.
- Modify: `src/main/java/com/hrm/dao/EmployeeDAO.java`
  - Them methods list/count employee theo department neu chua co.
- Create: `src/main/webapp/Views/DeptManager/_DeptManagerSidebar.jspf`
- Create: `src/main/webapp/Views/DeptManager/_DeptManagerTopbar.jspf`
- Create: `src/main/webapp/Views/DeptManager/_DeptManagerStyles.jspf`
- Modify: `src/main/webapp/Views/DeptManager/deptHome.jsp`
- Modify: `src/main/webapp/Views/DeptManager/taskManager.jsp`
- Modify: `src/main/webapp/Views/DeptManager/postTask.jsp`
- Modify: `src/main/webapp/Views/DeptManager/viewTask.jsp`
- Create: `src/main/webapp/Views/DeptManager/employees.jsp`
- Create: `src/main/webapp/Views/DeptManager/leaves.jsp`
- Create: `src/main/webapp/Views/DeptManager/calendar.jsp`
- Create: `src/main/webapp/Views/DeptManager/performance.jsp`
- Create: `src/main/webapp/Views/DeptManager/reports.jsp`
- Modify: `src/main/java/com/hrm/filter/RoleAuthorizationFilter.java`
  - Them route `/dept/employees`, `/dept/leaves`, `/dept/calendar`, `/dept/performance`, `/dept/reports` neu dung route moi.

---

### Task 1: Add Dept Manager Scope Helper

**Files:**
- Create: `src/main/java/com/hrm/util/DeptManagerScope.java`
- Modify: `src/main/java/com/hrm/controller/dept/DeptController.java`
- Compile check: `mvn -q compile`

- [ ] **Step 1: Create the scope helper**

Create `src/main/java/com/hrm/util/DeptManagerScope.java`:

```java
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
        if (employeeId == null) {
            return new DeptManagerScope(user, null, 0);
        }

        Employee employee = employeeDAO.getById(employeeId);
        Integer departmentId = employee != null ? employee.getDepartmentId() : null;
        return new DeptManagerScope(user, employee, departmentId != null ? departmentId : 0);
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
```

- [ ] **Step 2: Use the helper in `DeptController`**

Replace repeated session/role logic in `DeptController.doGet` with:

```java
DeptManagerScope scope = DeptManagerScope.from(request, employeeDAO);
if (scope == null) {
    response.sendRedirect(request.getContextPath() + "/login");
    return;
}
if (!scope.hasDepartment() && !scope.isAdmin()) {
    request.setAttribute("activePage", "dashboard");
    request.setAttribute("scopeError", "Tai khoan chua duoc gan phong ban.");
    request.getRequestDispatcher("/Views/DeptManager/deptHome.jsp").forward(request, response);
    return;
}
```

Keep Admin fallback by allowing dashboard counts for all departments only if existing Admin behavior requires it; otherwise show the same scope error when Admin has no employee department.

- [ ] **Step 3: Compile**

Run:

```powershell
& 'C:\Program Files\NetBeans-17\netbeans\java\maven\bin\mvn.cmd' -q compile
```

Expected: build succeeds. If imports are missing, add:

```java
import com.hrm.util.DeptManagerScope;
```

---

### Task 2: Add Shared Dept Manager JSP Shell

**Files:**
- Create: `src/main/webapp/Views/DeptManager/_DeptManagerStyles.jspf`
- Create: `src/main/webapp/Views/DeptManager/_DeptManagerSidebar.jspf`
- Create: `src/main/webapp/Views/DeptManager/_DeptManagerTopbar.jspf`
- Modify Dept Manager pages in the following tasks to include these files.

- [ ] **Step 1: Create shared styles include**

Create `src/main/webapp/Views/DeptManager/_DeptManagerStyles.jspf`:

```jsp
<style>
    :root {
        --dept-primary: #00482f;
        --dept-primary-strong: #006241;
        --dept-mint: #97f6c0;
        --dept-soft: #e4f7ec;
        --dept-canvas: #edebe9;
        --dept-surface: #fbf9f4;
        --dept-card: #ffffff;
        --dept-border: #d8d1c7;
        --dept-border-soft: #e7e1d8;
        --dept-text: #1b1c19;
        --dept-muted: #647068;
        --dept-danger: #c82014;
        --dept-warning: #cba258;
    }
    * { box-sizing: border-box; }
    body {
        margin: 0;
        min-height: 100vh;
        font-family: Inter, "Segoe UI", Arial, sans-serif;
        background: var(--dept-canvas);
        color: var(--dept-text);
    }
    a { color: inherit; text-decoration: none; }
    .dept-shell {
        min-height: 100vh;
        display: grid;
        grid-template-columns: 304px minmax(0, 1fr);
    }
    .dept-sidebar {
        position: sticky;
        top: 0;
        height: 100vh;
        display: flex;
        flex-direction: column;
        padding: 28px 20px;
        background: var(--dept-primary);
        color: #ffffff;
    }
    .dept-brand strong { display: block; font-size: 23px; font-weight: 800; }
    .dept-brand span { display: block; margin-top: 8px; color: rgba(255,255,255,.78); font-size: 14px; font-weight: 600; }
    .dept-nav { display: grid; gap: 10px; margin-top: 30px; }
    .dept-nav a {
        min-height: 50px;
        display: flex;
        align-items: center;
        gap: 14px;
        padding: 0 16px;
        border-radius: 10px;
        color: rgba(255,255,255,.82);
        font-weight: 800;
    }
    .dept-nav a.active {
        background: var(--dept-mint);
        color: var(--dept-primary);
    }
    .dept-profile {
        margin-top: auto;
        padding-top: 24px;
        border-top: 1px solid rgba(255,255,255,.16);
        display: flex;
        align-items: center;
        gap: 12px;
    }
    .dept-avatar {
        width: 48px;
        height: 48px;
        display: grid;
        place-items: center;
        border-radius: 50%;
        background: var(--dept-mint);
        color: var(--dept-primary);
    }
    .dept-profile strong { display: block; font-size: 14px; }
    .dept-profile span { display: block; margin-top: 4px; color: rgba(255,255,255,.78); font-size: 13px; }
    .dept-main { min-width: 0; background: var(--dept-canvas); }
    .dept-topbar {
        min-height: 76px;
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 18px;
        padding: 16px 34px;
        background: rgba(251,249,244,.96);
        border-bottom: 1px solid var(--dept-border-soft);
    }
    .dept-topbar h1 { margin: 0; color: var(--dept-primary); font-size: 22px; font-weight: 800; }
    .dept-topbar p { margin: 5px 0 0; color: var(--dept-muted); font-size: 14px; font-weight: 600; }
    .dept-content { width: min(100%, 1280px); margin: 0 auto; padding: 26px 34px 40px; }
    .dept-panel {
        border: 1px solid var(--dept-border);
        border-radius: 12px;
        background: var(--dept-card);
        box-shadow: 0 14px 30px rgba(30,57,50,.08);
    }
    .dept-panel-inner { padding: 22px; }
    .dept-table { width: 100%; border-collapse: collapse; }
    .dept-table th, .dept-table td { padding: 13px 12px; border-bottom: 1px solid var(--dept-border-soft); text-align: left; vertical-align: top; }
    .dept-table th { color: var(--dept-primary); font-size: 12px; font-weight: 800; text-transform: uppercase; }
    .dept-btn {
        min-height: 40px;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        gap: 8px;
        padding: 0 14px;
        border: 0;
        border-radius: 10px;
        background: var(--dept-primary);
        color: #fff;
        font-weight: 800;
        cursor: pointer;
    }
    .dept-btn.secondary { background: #f3f0ea; color: var(--dept-primary); border: 1px solid var(--dept-border-soft); }
    .dept-btn.danger { background: var(--dept-danger); }
    .dept-grid { display: grid; gap: 18px; }
    .dept-kpis { grid-template-columns: repeat(4, minmax(0, 1fr)); }
    .dept-kpi { padding: 20px; border: 1px solid var(--dept-border); border-radius: 12px; background: var(--dept-card); }
    .dept-kpi span { color: var(--dept-muted); font-size: 13px; font-weight: 800; }
    .dept-kpi strong { display: block; margin-top: 8px; color: var(--dept-primary); font-size: 28px; font-weight: 800; }
    .dept-alert { padding: 13px 15px; border-radius: 10px; margin-bottom: 16px; font-weight: 800; }
    .dept-alert.error { background: #fdebea; color: var(--dept-danger); }
    .dept-alert.success { background: var(--dept-soft); color: var(--dept-primary); }
    @media (max-width: 900px) {
        .dept-shell { grid-template-columns: 1fr; }
        .dept-sidebar { position: static; height: auto; }
        .dept-kpis { grid-template-columns: repeat(2, minmax(0, 1fr)); }
    }
</style>
```

- [ ] **Step 2: Create sidebar include**

Create `src/main/webapp/Views/DeptManager/_DeptManagerSidebar.jspf`:

```jsp
<aside class="dept-sidebar">
    <div class="dept-brand">
        <strong>BetterHR</strong>
        <span>He thong HRM noi bo</span>
    </div>
    <nav class="dept-nav" aria-label="Dept Manager navigation">
        <a class="${activePage eq 'dashboard' ? 'active' : ''}" href="${pageContext.request.contextPath}/dept"><i class="fa-solid fa-users-gear"></i> Tong quan nhom</a>
        <a class="${activePage eq 'employees' ? 'active' : ''}" href="${pageContext.request.contextPath}/dept?action=employees"><i class="fa-solid fa-id-badge"></i> Nhan vien phong ban</a>
        <a class="${activePage eq 'tasks' ? 'active' : ''}" href="${pageContext.request.contextPath}/taskManager"><i class="fa-solid fa-list-check"></i> Quan ly cong viec</a>
        <a class="${activePage eq 'calendar' ? 'active' : ''}" href="${pageContext.request.contextPath}/dept?action=calendar"><i class="fa-solid fa-calendar-days"></i> Lich nhom</a>
        <a class="${activePage eq 'leaves' ? 'active' : ''}" href="${pageContext.request.contextPath}/dept/leaves"><i class="fa-solid fa-calendar-xmark"></i> Yeu cau nghi phep</a>
        <a class="${activePage eq 'performance' ? 'active' : ''}" href="${pageContext.request.contextPath}/dept?action=performance"><i class="fa-solid fa-chart-line"></i> Danh gia hieu suat</a>
        <a class="${activePage eq 'reports' ? 'active' : ''}" href="${pageContext.request.contextPath}/dept?action=reports"><i class="fa-solid fa-square-poll-vertical"></i> Bao cao phong ban</a>
    </nav>
    <div class="dept-profile">
        <div class="dept-avatar"><i class="fa-solid fa-user-tie"></i></div>
        <div>
            <strong>${not empty userName ? userName : sessionScope.systemUser.username}</strong>
            <span>${not empty userPosition ? userPosition : 'Dept Manager'}</span>
        </div>
    </div>
</aside>
```

- [ ] **Step 3: Create topbar include**

Create `src/main/webapp/Views/DeptManager/_DeptManagerTopbar.jspf`:

```jsp
<header class="dept-topbar">
    <div>
        <h1>${pageTitle}</h1>
        <p>${pageSubtitle}</p>
    </div>
    <div style="display:flex; gap:10px; align-items:center;">
        <a class="dept-btn secondary" href="${pageContext.request.contextPath}/dept">Dashboard</a>
        <a class="dept-btn" href="${pageContext.request.contextPath}/postTask"><i class="fa-solid fa-plus"></i> Tao cong viec</a>
        <a class="dept-btn secondary" href="${pageContext.request.contextPath}/logout"><i class="fa-solid fa-right-from-bracket"></i></a>
    </div>
</header>
```

---

### Task 3: Add Department-Scoped DAO Methods

**Files:**
- Modify: `src/main/java/com/hrm/dao/EmployeeDAO.java`
- Modify: `src/main/java/com/hrm/dao/MailRequestDAO.java`
- Create: `src/main/java/com/hrm/dao/DeptDashboardDAO.java`

- [ ] **Step 1: Add employee list by department**

Add to `EmployeeDAO`:

```java
public List<Employee> getByDepartmentId(int departmentId) {
    List<Employee> employees = new ArrayList<>();
    String sql = """
        SELECT e.*, d.DeptName
        FROM Employee e
        LEFT JOIN Department d ON e.DepartmentID = d.DepartmentID
        WHERE e.DepartmentID = ?
        ORDER BY e.FullName
    """;
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, departmentId);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return employees;
}
```

If `mapResultSetToEmployee` is private and already exists, reuse it. If it does not exist, map the fields the same way existing `getAll()` or `getById()` maps `Employee`.

- [ ] **Step 2: Add scoped leave list and update**

Add to `MailRequestDAO`:

```java
public List<Map<String, Object>> getLeaveRequestsByDepartment(int departmentId, String status) {
    List<Map<String, Object>> list = new ArrayList<>();
    StringBuilder sql = new StringBuilder("""
        SELECT mr.RequestID, mr.EmployeeID, e.FullName, e.Email,
               mr.LeaveType, mr.StartDate, mr.EndDate, mr.Reason,
               mr.Status, mr.ApprovedBy
        FROM MailRequest mr
        JOIN Employee e ON e.EmployeeID = mr.EmployeeID
        WHERE mr.RequestType = 'Leave'
          AND e.DepartmentID = ?
    """);
    List<Object> params = new ArrayList<>();
    params.add(departmentId);
    if (status != null && !status.isBlank() && !"All".equalsIgnoreCase(status)) {
        sql.append(" AND mr.Status = ?");
        params.add(status);
    }
    sql.append(" ORDER BY mr.RequestID DESC");

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql.toString())) {
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("requestId", rs.getInt("RequestID"));
                row.put("employeeId", rs.getInt("EmployeeID"));
                row.put("employeeName", rs.getString("FullName"));
                row.put("employeeEmail", rs.getString("Email"));
                row.put("leaveType", rs.getString("LeaveType"));
                row.put("startDate", rs.getDate("StartDate"));
                row.put("endDate", rs.getDate("EndDate"));
                row.put("reason", rs.getString("Reason"));
                row.put("status", rs.getString("Status"));
                row.put("approvedBy", rs.getObject("ApprovedBy"));
                list.add(row);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return list;
}

public boolean updateLeaveStatusByDepartment(int requestId, int departmentId, String status, int approverId) {
    if (!List.of("Approved", "Rejected").contains(status)) {
        return false;
    }
    String sql = """
        UPDATE MailRequest mr
        JOIN Employee e ON e.EmployeeID = mr.EmployeeID
        SET mr.Status = ?, mr.ApprovedBy = ?
        WHERE mr.RequestID = ?
          AND mr.RequestType = 'Leave'
          AND mr.Status = 'Pending'
          AND e.DepartmentID = ?
    """;
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, status);
        ps.setInt(2, approverId);
        ps.setInt(3, requestId);
        ps.setInt(4, departmentId);
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}
```

- [ ] **Step 3: Add dashboard DAO**

Create `src/main/java/com/hrm/dao/DeptDashboardDAO.java`:

```java
package com.hrm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DeptDashboardDAO {

    public Map<String, Integer> getDashboardCounts(int departmentId, int managerEmployeeId) {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("totalEmployees", countEmployees(departmentId, null));
        counts.put("activeEmployees", countEmployees(departmentId, "Active"));
        counts.put("pendingLeaves", countPendingLeaves(departmentId));
        counts.put("totalTasks", countTasks(managerEmployeeId, null));
        counts.put("completedTasks", countTasks(managerEmployeeId, "Completed"));
        counts.put("waitingTasks", countTasks(managerEmployeeId, "Waiting"));
        counts.put("overdueTasks", countOverdueTasks(managerEmployeeId));
        return counts;
    }

    private int countEmployees(int departmentId, String status) {
        String sql = status == null
                ? "SELECT COUNT(*) FROM Employee WHERE DepartmentID = ?"
                : "SELECT COUNT(*) FROM Employee WHERE DepartmentID = ? AND Status = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            if (status != null) {
                ps.setString(2, status);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int countPendingLeaves(int departmentId) {
        String sql = """
            SELECT COUNT(*)
            FROM MailRequest mr
            JOIN Employee e ON e.EmployeeID = mr.EmployeeID
            WHERE mr.RequestType = 'Leave'
              AND mr.Status = 'Pending'
              AND e.DepartmentID = ?
        """;
        return countOneInt(sql, departmentId);
    }

    private int countTasks(int managerEmployeeId, String status) {
        String sql = status == null
                ? "SELECT COUNT(*) FROM Task WHERE AssignedBy = ?"
                : "SELECT COUNT(*) FROM Task WHERE AssignedBy = ? AND Status = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, managerEmployeeId);
            if (status != null) {
                ps.setString(2, status);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int countOverdueTasks(int managerEmployeeId) {
        String sql = """
            SELECT COUNT(*)
            FROM Task
            WHERE AssignedBy = ?
              AND DueDate < CURRENT_DATE()
              AND Status NOT IN ('Completed', 'Rejected')
        """;
        return countOneInt(sql, managerEmployeeId);
    }

    private int countOneInt(String sql, int value) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
```

- [ ] **Step 4: Compile**

Run:

```powershell
& 'C:\Program Files\NetBeans-17\netbeans\java\maven\bin\mvn.cmd' -q compile
```

Expected: PASS. If `EmployeeDAO` lacks imports, add `java.util.List`, `java.util.ArrayList`, `java.sql.Connection`, `java.sql.PreparedStatement`, `java.sql.ResultSet`, `java.sql.SQLException`.

---

### Task 4: Rebuild Dashboard With Real Data And Shared Shell

**Files:**
- Modify: `src/main/java/com/hrm/controller/dept/DeptController.java`
- Modify: `src/main/webapp/Views/DeptManager/deptHome.jsp`

- [ ] **Step 1: Populate dashboard attributes**

In `DeptController`, add fields:

```java
private final EmployeeDAO employeeDAO = new EmployeeDAO();
private final DepartmentDAO departmentDAO = new DepartmentDAO();
private final DeptDashboardDAO deptDashboardDAO = new DeptDashboardDAO();
private final MailRequestDAO mailRequestDAO = new MailRequestDAO();
```

In `loadDashboard`, set:

```java
request.setAttribute("activePage", "dashboard");
request.setAttribute("pageTitle", "Tong quan nhom");
request.setAttribute("pageSubtitle", "Theo doi nhan vien, cong viec va nghi phep trong phong ban.");

int departmentId = scope.getDepartmentId();
Map<String, Integer> counts = deptDashboardDAO.getDashboardCounts(departmentId, scope.getApproverEmployeeId());
request.setAttribute("dashboardCounts", counts);
request.setAttribute("employees", employeeDAO.getByDepartmentId(departmentId));
request.setAttribute("departmentName", departmentDAO.getById(departmentId).getDeptName());
```

Change the method signature to:

```java
private void loadDashboard(HttpServletRequest request, HttpServletResponse response, DeptManagerScope scope)
        throws ServletException, IOException
```

- [ ] **Step 2: Rewrite `deptHome.jsp` to use shell**

Replace the top-level body structure with:

```jsp
<body>
<div class="dept-shell">
    <%@ include file="_DeptManagerSidebar.jspf" %>
    <main class="dept-main">
        <%@ include file="_DeptManagerTopbar.jspf" %>
        <section class="dept-content">
            <c:if test="${not empty scopeError}">
                <div class="dept-alert error">${scopeError}</div>
            </c:if>

            <div class="dept-grid dept-kpis">
                <article class="dept-kpi"><span>Tong nhan vien</span><strong>${dashboardCounts.totalEmployees}</strong></article>
                <article class="dept-kpi"><span>Dang lam viec</span><strong>${dashboardCounts.activeEmployees}</strong></article>
                <article class="dept-kpi"><span>Cong viec dang mo</span><strong>${dashboardCounts.totalTasks - dashboardCounts.completedTasks}</strong></article>
                <article class="dept-kpi"><span>Don nghi cho duyet</span><strong>${dashboardCounts.pendingLeaves}</strong></article>
            </div>

            <section class="dept-panel" style="margin-top:18px;">
                <div class="dept-panel-inner">
                    <h2>Nhân viên phòng ban</h2>
                    <table class="dept-table">
                        <thead>
                            <tr><th>Ma NV</th><th>Ho ten</th><th>Email</th><th>Vi tri</th><th>Trang thai</th></tr>
                        </thead>
                        <tbody>
                            <c:forEach var="emp" items="${employees}">
                                <tr>
                                    <td>${emp.employeeId}</td>
                                    <td>${emp.fullName}</td>
                                    <td>${emp.email}</td>
                                    <td>${emp.position}</td>
                                    <td>${emp.status}</td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty employees}">
                                <tr><td colspan="5">Khong co nhan vien trong phong ban.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </section>
        </section>
    </main>
</div>
</body>
```

Keep the existing `<head>` imports for JSTL and Font Awesome, and include `_DeptManagerStyles.jspf`.

- [ ] **Step 3: Compile**

Run:

```powershell
& 'C:\Program Files\NetBeans-17\netbeans\java\maven\bin\mvn.cmd' -q compile
```

Expected: PASS.

---

### Task 5: Secure And Restyle Task Management

**Files:**
- Modify: `src/main/java/com/hrm/controller/dept/TaskManager.java`
- Modify: `src/main/java/com/hrm/controller/dept/PostTask.java`
- Modify: `src/main/java/com/hrm/controller/dept/ViewTask.java`
- Modify: `src/main/webapp/Views/DeptManager/taskManager.jsp`
- Modify: `src/main/webapp/Views/DeptManager/postTask.jsp`
- Modify: `src/main/webapp/Views/DeptManager/viewTask.jsp`

- [ ] **Step 1: Move auth before actions in `TaskManager`**

At the top of `doGet`, before reading `action`, add:

```java
DeptManagerScope scope = DeptManagerScope.from(request, new EmployeeDAO());
if (scope == null) {
    response.sendRedirect(request.getContextPath() + "/login");
    return;
}
if (!scope.hasDepartment()) {
    response.sendRedirect(request.getContextPath() + "/dept");
    return;
}
int employeeId = scope.getApproverEmployeeId();
```

Then process `viewAssignees`, `reject`, and `send`. For every action with task id, verify ownership:

```java
Task task = DAO.getInstance().getTaskById(taskId);
if (task == null || task.getAssignedBy() != employeeId) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN);
    return;
}
```

- [ ] **Step 2: Fix confusing send/delete labels**

In `taskManager.jsp`, replace the old action labels:

```jsp
<a href="${pageContext.request.contextPath}/taskManager?action=send&id=${task.taskId}" class="dept-btn secondary">Gui</a>
<a href="${pageContext.request.contextPath}/taskManager?action=reject&id=${task.taskId}" class="dept-btn danger">Huy</a>
```

Do not show text saying "Delete" when action updates status to `Rejected`.

- [ ] **Step 3: Validate task creation**

In `PostTask.doPost`, after reading parameters, add:

```java
if (title == null || title.trim().isEmpty() || title.length() > 50) {
    response.sendRedirect(request.getContextPath() + "/postTask?error=Title is required and must be 50 characters or fewer");
    return;
}
if (description != null && description.length() > 1000) {
    response.sendRedirect(request.getContextPath() + "/postTask?error=Description must be 1000 characters or fewer");
    return;
}
if (startDate == null || dueDate == null || startDate.compareTo(dueDate) > 0) {
    response.sendRedirect(request.getContextPath() + "/postTask?error=Start date must be before due date");
    return;
}
```

Before assigning each employee, verify same department:

```java
Employee assignee = DAO.getInstance().getEmp(empId);
if (assignee != null && assignee.getDepartmentId() == scope.getDepartmentId()) {
    DAO.getInstance().assignTaskToEmployee(taskId, empId);
}
```

- [ ] **Step 4: Restyle task JSP pages with shell**

For `taskManager.jsp`, `postTask.jsp`, and `viewTask.jsp`, use this body pattern:

```jsp
<body>
<div class="dept-shell">
    <%@ include file="_DeptManagerSidebar.jspf" %>
    <main class="dept-main">
        <%@ include file="_DeptManagerTopbar.jspf" %>
        <section class="dept-content">
            <!-- page-specific forms and tables go here -->
        </section>
    </main>
</div>
</body>
```

Set controller attributes before forwarding:

```java
request.setAttribute("activePage", "tasks");
request.setAttribute("pageTitle", "Quan ly cong viec");
request.setAttribute("pageSubtitle", "Tao, giao va theo doi cong viec cua phong ban.");
```

- [ ] **Step 5: Compile**

Run:

```powershell
& 'C:\Program Files\NetBeans-17\netbeans\java\maven\bin\mvn.cmd' -q compile
```

Expected: PASS.

---

### Task 6: Add Department Employees Page

**Files:**
- Modify: `src/main/java/com/hrm/controller/dept/DeptController.java`
- Create: `src/main/webapp/Views/DeptManager/employees.jsp`

- [ ] **Step 1: Add `employees` action**

In `DeptController.doGet`, add switch case:

```java
case "employees":
    loadEmployees(request, response, scope);
    break;
```

Add method:

```java
private void loadEmployees(HttpServletRequest request, HttpServletResponse response, DeptManagerScope scope)
        throws ServletException, IOException {
    request.setAttribute("activePage", "employees");
    request.setAttribute("pageTitle", "Nhan vien phong ban");
    request.setAttribute("pageSubtitle", "Danh sach nhan vien thuoc phong ban cua ban.");
    request.setAttribute("employees", employeeDAO.getByDepartmentId(scope.getDepartmentId()));
    request.getRequestDispatcher("/Views/DeptManager/employees.jsp").forward(request, response);
}
```

- [ ] **Step 2: Create employees JSP**

Create `src/main/webapp/Views/DeptManager/employees.jsp`:

```jsp
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>BetterHR - Nhan vien phong ban</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <%@ include file="_DeptManagerStyles.jspf" %>
</head>
<body>
<div class="dept-shell">
    <%@ include file="_DeptManagerSidebar.jspf" %>
    <main class="dept-main">
        <%@ include file="_DeptManagerTopbar.jspf" %>
        <section class="dept-content">
            <section class="dept-panel">
                <div class="dept-panel-inner">
                    <table class="dept-table">
                        <thead>
                            <tr><th>Ma NV</th><th>Ho ten</th><th>Email</th><th>Dien thoai</th><th>Vi tri</th><th>Trang thai</th></tr>
                        </thead>
                        <tbody>
                            <c:forEach var="emp" items="${employees}">
                                <tr>
                                    <td>${emp.employeeId}</td>
                                    <td>${emp.fullName}</td>
                                    <td>${emp.email}</td>
                                    <td>${emp.phone}</td>
                                    <td>${emp.position}</td>
                                    <td>${emp.status}</td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty employees}">
                                <tr><td colspan="6">Khong co nhan vien trong phong ban.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </section>
        </section>
    </main>
</div>
</body>
</html>
```

- [ ] **Step 3: Compile**

Run:

```powershell
& 'C:\Program Files\NetBeans-17\netbeans\java\maven\bin\mvn.cmd' -q compile
```

Expected: PASS.

---

### Task 7: Add Department Leave Approval

**Files:**
- Create: `src/main/java/com/hrm/controller/dept/DeptLeaveController.java`
- Create: `src/main/webapp/Views/DeptManager/leaves.jsp`
- Modify: `src/main/java/com/hrm/filter/RoleAuthorizationFilter.java`

- [ ] **Step 1: Create controller**

Create `src/main/java/com/hrm/controller/dept/DeptLeaveController.java`:

```java
package com.hrm.controller.dept;

import com.hrm.dao.EmployeeDAO;
import com.hrm.dao.MailRequestDAO;
import com.hrm.model.entity.SystemUser;
import com.hrm.util.DeptManagerScope;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "DeptLeaveController", urlPatterns = {"/dept/leaves"})
public class DeptLeaveController extends HttpServlet {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
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
            response.sendRedirect(request.getContextPath() + "/dept");
            return;
        }

        String status = request.getParameter("status");
        if (status == null || status.isBlank()) {
            status = "Pending";
        }

        pullFlashMessages(request);
        request.setAttribute("activePage", "leaves");
        request.setAttribute("pageTitle", "Yeu cau nghi phep");
        request.setAttribute("pageSubtitle", "Duyet hoac tu choi don nghi phep cua nhan vien trong phong ban.");
        request.setAttribute("statusFilter", status);
        request.setAttribute("leaveRequests", mailRequestDAO.getLeaveRequestsByDepartment(scope.getDepartmentId(), status));
        request.getRequestDispatcher("/Views/DeptManager/leaves.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DeptManagerScope scope = DeptManagerScope.from(request, employeeDAO);
        if (scope == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (!scope.hasDepartment() || scope.getApproverEmployeeId() <= 0) {
            response.sendRedirect(request.getContextPath() + "/dept");
            return;
        }

        int requestId = parseInt(request.getParameter("requestId"), -1);
        String decision = request.getParameter("decision");
        boolean success = requestId > 0
                && ("Approved".equals(decision) || "Rejected".equals(decision))
                && mailRequestDAO.updateLeaveStatusByDepartment(
                        requestId,
                        scope.getDepartmentId(),
                        decision,
                        scope.getApproverEmployeeId());

        HttpSession session = request.getSession();
        session.setAttribute(success ? "deptLeaveSuccess" : "deptLeaveError",
                success ? "Da cap nhat don nghi phep." : "Khong the cap nhat don nay.");
        response.sendRedirect(request.getContextPath() + "/dept/leaves");
    }

    private void pullFlashMessages(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        Object success = session.getAttribute("deptLeaveSuccess");
        Object error = session.getAttribute("deptLeaveError");
        if (success != null) {
            request.setAttribute("deptLeaveSuccess", success);
            session.removeAttribute("deptLeaveSuccess");
        }
        if (error != null) {
            request.setAttribute("deptLeaveError", error);
            session.removeAttribute("deptLeaveError");
        }
    }

    private int parseInt(String value, int fallback) {
        try {
            return value == null || value.isBlank() ? fallback : Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }
}
```

- [ ] **Step 2: Create leaves JSP**

Create `src/main/webapp/Views/DeptManager/leaves.jsp`:

```jsp
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>BetterHR - Yeu cau nghi phep</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <%@ include file="_DeptManagerStyles.jspf" %>
</head>
<body>
<div class="dept-shell">
    <%@ include file="_DeptManagerSidebar.jspf" %>
    <main class="dept-main">
        <%@ include file="_DeptManagerTopbar.jspf" %>
        <section class="dept-content">
            <c:if test="${not empty deptLeaveSuccess}">
                <div class="dept-alert success">${deptLeaveSuccess}</div>
            </c:if>
            <c:if test="${not empty deptLeaveError}">
                <div class="dept-alert error">${deptLeaveError}</div>
            </c:if>
            <div style="display:flex; gap:10px; margin-bottom:16px;">
                <a class="dept-btn secondary" href="${pageContext.request.contextPath}/dept/leaves?status=Pending">Cho duyet</a>
                <a class="dept-btn secondary" href="${pageContext.request.contextPath}/dept/leaves?status=Approved">Da duyet</a>
                <a class="dept-btn secondary" href="${pageContext.request.contextPath}/dept/leaves?status=Rejected">Tu choi</a>
                <a class="dept-btn secondary" href="${pageContext.request.contextPath}/dept/leaves?status=All">Tat ca</a>
            </div>
            <section class="dept-panel">
                <div class="dept-panel-inner">
                    <table class="dept-table">
                        <thead>
                            <tr><th>Nhan vien</th><th>Loai</th><th>Tu ngay</th><th>Den ngay</th><th>Ly do</th><th>Trang thai</th><th>Thao tac</th></tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${leaveRequests}">
                                <tr>
                                    <td><strong>${item.employeeName}</strong><br>${item.employeeEmail}</td>
                                    <td>${item.leaveType}</td>
                                    <td>${item.startDate}</td>
                                    <td>${item.endDate}</td>
                                    <td>${item.reason}</td>
                                    <td>${item.status}</td>
                                    <td>
                                        <c:if test="${item.status eq 'Pending'}">
                                            <form method="post" action="${pageContext.request.contextPath}/dept/leaves" style="display:inline-flex; gap:8px;">
                                                <input type="hidden" name="requestId" value="${item.requestId}">
                                                <button class="dept-btn" type="submit" name="decision" value="Approved">Duyet</button>
                                                <button class="dept-btn danger" type="submit" name="decision" value="Rejected">Tu choi</button>
                                            </form>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty leaveRequests}">
                                <tr><td colspan="7">Khong co don nghi phep.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </section>
        </section>
    </main>
</div>
</body>
</html>
```

- [ ] **Step 3: Update authorization filter**

In `RoleAuthorizationFilter`, ensure the Dept pattern includes `/dept/leaves`:

```java
addPattern(Set.of(1, 3), "/dept", "/dept/", "/taskManager", "/viewTask", "/postTask",
        "/Dept", "/DeptController");
```

Because `/dept/` already matches `/dept/leaves`, no change is needed if this pattern remains. If route matching is changed later, keep `/dept/leaves` in the Dept role set.

- [ ] **Step 4: Compile**

Run:

```powershell
& 'C:\Program Files\NetBeans-17\netbeans\java\maven\bin\mvn.cmd' -q compile
```

Expected: PASS.

---

### Task 8: Add Calendar, Performance, And Reports Views

**Files:**
- Modify: `src/main/java/com/hrm/controller/dept/DeptController.java`
- Create: `src/main/webapp/Views/DeptManager/calendar.jsp`
- Create: `src/main/webapp/Views/DeptManager/performance.jsp`
- Create: `src/main/webapp/Views/DeptManager/reports.jsp`

- [ ] **Step 1: Add controller cases**

In `DeptController.doGet`, add:

```java
case "calendar":
    loadSimpleDeptPage(request, response, scope, "calendar", "Lich nhom", "Task den han va lich nghi cua phong ban.", "/Views/DeptManager/calendar.jsp");
    break;
case "performance":
    loadSimpleDeptPage(request, response, scope, "performance", "Danh gia hieu suat", "Theo doi hieu suat theo task cua tung nhan vien.", "/Views/DeptManager/performance.jsp");
    break;
case "reports":
    loadSimpleDeptPage(request, response, scope, "reports", "Bao cao phong ban", "Tong hop nhan vien, task va nghi phep.", "/Views/DeptManager/reports.jsp");
    break;
```

Add helper:

```java
private void loadSimpleDeptPage(HttpServletRequest request, HttpServletResponse response, DeptManagerScope scope,
                                String activePage, String title, String subtitle, String jsp)
        throws ServletException, IOException {
    request.setAttribute("activePage", activePage);
    request.setAttribute("pageTitle", title);
    request.setAttribute("pageSubtitle", subtitle);
    request.setAttribute("employees", employeeDAO.getByDepartmentId(scope.getDepartmentId()));
    request.setAttribute("dashboardCounts", deptDashboardDAO.getDashboardCounts(scope.getDepartmentId(), scope.getApproverEmployeeId()));
    request.setAttribute("approvedLeaves", mailRequestDAO.getLeaveRequestsByDepartment(scope.getDepartmentId(), "Approved"));
    request.getRequestDispatcher(jsp).forward(request, response);
}
```

- [ ] **Step 2: Create calendar JSP**

Create `src/main/webapp/Views/DeptManager/calendar.jsp`:

```jsp
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>BetterHR - ${pageTitle}</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <%@ include file="_DeptManagerStyles.jspf" %>
</head>
<body>
<div class="dept-shell">
    <%@ include file="_DeptManagerSidebar.jspf" %>
    <main class="dept-main">
        <%@ include file="_DeptManagerTopbar.jspf" %>
        <section class="dept-content">
            <section class="dept-panel">
                <div class="dept-panel-inner">
                    <h2>Lich nghi da duyet</h2>
                    <table class="dept-table">
                        <thead>
                            <tr><th>Nhan vien</th><th>Loai nghi</th><th>Tu ngay</th><th>Den ngay</th><th>Ly do</th></tr>
                        </thead>
                        <tbody>
                            <c:forEach var="leave" items="${approvedLeaves}">
                                <tr>
                                    <td>${leave.employeeName}</td>
                                    <td>${leave.leaveType}</td>
                                    <td>${leave.startDate}</td>
                                    <td>${leave.endDate}</td>
                                    <td>${leave.reason}</td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty approvedLeaves}">
                                <tr><td colspan="5">Chua co lich nghi da duyet.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </section>
        </section>
    </main>
</div>
</body>
</html>
```

- [ ] **Step 3: Create performance JSP**

Create `src/main/webapp/Views/DeptManager/performance.jsp`:

```jsp
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>BetterHR - ${pageTitle}</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <%@ include file="_DeptManagerStyles.jspf" %>
</head>
<body>
<div class="dept-shell">
    <%@ include file="_DeptManagerSidebar.jspf" %>
    <main class="dept-main">
        <%@ include file="_DeptManagerTopbar.jspf" %>
        <section class="dept-content">
            <section class="dept-panel">
                <div class="dept-panel-inner">
                    <h2>Hieu suat theo nhan vien</h2>
                    <table class="dept-table">
                        <thead>
                            <tr><th>Ma NV</th><th>Ho ten</th><th>Vi tri</th><th>Trang thai</th></tr>
                        </thead>
                        <tbody>
                            <c:forEach var="emp" items="${employees}">
                                <tr>
                                    <td>${emp.employeeId}</td>
                                    <td>${emp.fullName}</td>
                                    <td>${emp.position}</td>
                                    <td>${emp.status}</td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty employees}">
                                <tr><td colspan="4">Khong co nhan vien de danh gia.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </section>
        </section>
    </main>
</div>
</body>
</html>
```

- [ ] **Step 4: Create reports JSP**

Create `src/main/webapp/Views/DeptManager/reports.jsp`:

```jsp
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>BetterHR - ${pageTitle}</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <%@ include file="_DeptManagerStyles.jspf" %>
</head>
<body>
<div class="dept-shell">
    <%@ include file="_DeptManagerSidebar.jspf" %>
    <main class="dept-main">
        <%@ include file="_DeptManagerTopbar.jspf" %>
        <section class="dept-content">
            <div class="dept-grid dept-kpis">
                <article class="dept-kpi"><span>Tong nhan vien</span><strong>${dashboardCounts.totalEmployees}</strong></article>
                <article class="dept-kpi"><span>Dang lam viec</span><strong>${dashboardCounts.activeEmployees}</strong></article>
                <article class="dept-kpi"><span>Task hoan thanh</span><strong>${dashboardCounts.completedTasks}</strong></article>
                <article class="dept-kpi"><span>Task qua han</span><strong>${dashboardCounts.overdueTasks}</strong></article>
            </div>
            <section class="dept-panel" style="margin-top:18px;">
                <div class="dept-panel-inner">
                    <h2>Tong hop nghi phep</h2>
                    <p>Don nghi cho duyet: <strong>${dashboardCounts.pendingLeaves}</strong></p>
                </div>
            </section>
        </section>
    </main>
</div>
</body>
</html>
```

- [ ] **Step 5: Compile**

Run:

```powershell
& 'C:\Program Files\NetBeans-17\netbeans\java\maven\bin\mvn.cmd' -q compile
```

Expected: PASS.

---

### Task 9: Final Verification

**Files:**
- No new source files.

- [ ] **Step 1: Compile cleanly**

Run:

```powershell
& 'C:\Program Files\NetBeans-17\netbeans\java\maven\bin\mvn.cmd' -q compile
```

Expected: command exits with code 0.

- [ ] **Step 2: Manual route checks**

Start the app from NetBeans/Tomcat, then verify:

```text
/dept
/dept?action=employees
/taskManager
/postTask
/dept/leaves
/dept?action=calendar
/dept?action=performance
/dept?action=reports
```

Expected:

- Dept Manager sees sidebar on every page.
- Task Manager no longer appears as the old standalone Bootstrap page.
- Dept Manager sees only department employees.
- Dept Manager can approve/reject only pending department leave requests.
- Employee, HR Staff, and Guest are redirected away from Dept routes.
- HR Manager `/hr/leaves` still works.

- [ ] **Step 3: Database behavior checks**

Use MySQL to inspect a leave update:

```sql
SELECT RequestID, EmployeeID, Status, ApprovedBy
FROM MailRequest
WHERE RequestType = 'Leave'
ORDER BY RequestID DESC;
```

Expected: approving/rejecting from `/dept/leaves` changes only one pending row and sets `ApprovedBy` to the Dept Manager employee id.

---

## Self-Review

- Spec coverage:
  - Shared sidebar/topbar: Task 2.
  - Remove standalone Task Manager layout: Task 5.
  - Dashboard real data: Task 3 and Task 4.
  - Department employee list: Task 6.
  - Task scope/security: Task 5.
  - Leave approval for Dept Manager: Task 7.
  - Calendar/performance/reports pages: Task 8.
  - Compile/manual verification: Task 9.
- Placeholder scan:
  - No `TBD`, `TODO`, or deferred implementation notes remain. Calendar, performance, and report pages have concrete JSP content in Task 8.
- Type consistency:
  - `DeptManagerScope`, `DeptDashboardDAO`, and `MailRequestDAO` method names are used consistently across tasks.
