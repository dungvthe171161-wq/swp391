<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BetterHR - Báo cáo phòng ban</title>
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
                <article class="dept-kpi"><span>Tổng nhân viên</span><strong>${dashboardCounts.totalEmployees}</strong></article>
                <article class="dept-kpi"><span>Đang làm việc</span><strong>${dashboardCounts.activeEmployees}</strong></article>
                <article class="dept-kpi"><span>Công việc hoàn thành</span><strong>${dashboardCounts.completedTasks}</strong></article>
                <article class="dept-kpi"><span>Công việc quá hạn</span><strong>${dashboardCounts.overdueTasks}</strong></article>
            </div>
            <section class="dept-panel">
                <div class="dept-panel-inner">
                    <h2>Tổng hợp nghỉ phép</h2>
                    <p>Đơn nghỉ chờ duyệt: <strong>${dashboardCounts.pendingLeaves}</strong></p>
                </div>
            </section>
        </section>
    </main>
</div>
</body>
</html>
