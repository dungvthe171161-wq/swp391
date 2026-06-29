<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BetterHR - Tổng quan phòng ban</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <%@ include file="_DeptManagerStyles.jspf" %>
</head>
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
                <article class="dept-kpi">
                    <span>Tổng nhân viên</span>
                    <strong>${dashboardCounts.totalEmployees != null ? dashboardCounts.totalEmployees : 0}</strong>
                </article>
                <article class="dept-kpi">
                    <span>Đang làm việc</span>
                    <strong>${dashboardCounts.activeEmployees != null ? dashboardCounts.activeEmployees : 0}</strong>
                </article>
                <article class="dept-kpi">
                    <span>Công việc đang mở</span>
                    <strong>${(dashboardCounts.totalTasks != null ? dashboardCounts.totalTasks : 0) - (dashboardCounts.completedTasks != null ? dashboardCounts.completedTasks : 0)}</strong>
                </article>
                <article class="dept-kpi">
                    <span>Đơn nghỉ chờ duyệt</span>
                    <strong>${dashboardCounts.pendingLeaves != null ? dashboardCounts.pendingLeaves : 0}</strong>
                </article>
            </div>

            <section class="dept-panel">
                <div class="dept-panel-inner">
                    <h2>Phòng ban ${departmentName}</h2>
                    <table class="dept-table">
                        <thead>
                            <tr>
                                <th>Mã NV</th>
                                <th>Họ tên</th>
                                <th>Email</th>
                                <th>Vị trí</th>
                                <th>Trạng thái</th>
                            </tr>
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
                                <tr>
                                    <td colspan="5">Không có nhân viên trong phòng ban.</td>
                                </tr>
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
