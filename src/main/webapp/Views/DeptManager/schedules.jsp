<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Lich lam viec phong ban</title>
    <%@ include file="_DeptManagerStyles.jspf" %>
</head>
<body>
<div class="dept-shell">
    <%@ include file="_DeptManagerSidebar.jspf" %>
    <main class="dept-main">
        <%@ include file="_DeptManagerTopbar.jspf" %>
        <section class="dept-content">
            <h1 class="dept-title">L&#7883;ch l&#224;m vi&#7879;c ph&#242;ng ban</h1>
            <c:if test="${not empty sessionScope.deptSuccess}"><div class="alert success">${sessionScope.deptSuccess}</div><c:remove var="deptSuccess" scope="session"/></c:if>
            <c:if test="${not empty sessionScope.deptError}"><div class="alert error">${sessionScope.deptError}</div><c:remove var="deptError" scope="session"/></c:if>
            <section class="dept-card">
                <form method="get" action="${pageContext.request.contextPath}/dept/schedules" class="dept-filter-row">
                    <label>Th&#225;ng <input type="number" name="month" min="1" max="12" value="${month}"></label>
                    <label>N&#259;m <input type="number" name="year" min="2020" max="2100" value="${year}"></label>
                    <button type="submit">L&#7885;c</button>
                </form>
            </section>
            <section class="dept-card">
                <h2>G&#225;n l&#7883;ch</h2>
                <form method="post" action="${pageContext.request.contextPath}/dept/schedules" class="dept-filter-row">
                    <input type="hidden" name="action" value="assign">
                    <select name="employeeId" required><c:forEach var="e" items="${employees}"><option value="${e.employeeId}">${e.fullName}</option></c:forEach></select>
                    <select name="scheduleId" required><c:forEach var="s" items="${workSchedules}"><option value="${s.scheduleId}">${s.scheduleCode} - ${s.scheduleName}</option></c:forEach></select>
                    <input type="date" name="workDate" required>
                    <input type="text" name="note" placeholder="Ghi chu">
                    <button type="submit">L&#432;u</button>
                </form>
            </section>
            <section class="dept-card">
                <table class="dept-table">
                    <thead><tr><th>Ng&#224;y</th><th>Nh&#226;n vi&#234;n</th><th>Ph&#242;ng ban</th><th>M&#227; ca</th><th>T&#234;n ca</th><th>B&#7855;t &#273;&#7847;u</th><th>K&#7871;t th&#250;c</th><th>Ghi ch&#250;</th><th>H&#224;nh &#273;&#7897;ng</th></tr></thead>
                    <tbody>
                    <c:forEach var="item" items="${employeeSchedules}">
                        <tr>
                            <form method="post" action="${pageContext.request.contextPath}/dept/schedules">
                                <input type="hidden" name="assignmentId" value="${item.assignmentId}">
                                <td><input type="date" name="workDate" value="${item.workDate}"></td>
                                <td>${item.employeeName}</td><td>${item.departmentName}</td><td>${item.scheduleCode}</td><td>${item.scheduleName}</td><td>${item.startTime}</td><td>${item.endTime}</td>
                                <td><input type="text" name="note" value="${item.note}"></td>
                                <td><select name="scheduleId"><c:forEach var="s" items="${workSchedules}"><option value="${s.scheduleId}" ${s.scheduleId == item.scheduleId ? 'selected' : ''}>${s.scheduleCode}</option></c:forEach></select><button name="action" value="update">S&#7917;a</button><button name="action" value="delete">X&#243;a</button></td>
                            </form>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty employeeSchedules}"><tr><td colspan="9">Ch&#432;a c&#243; l&#7883;ch.</td></tr></c:if>
                    </tbody>
                </table>
            </section>
        </section>
    </main>
</div>
</body>
</html>
