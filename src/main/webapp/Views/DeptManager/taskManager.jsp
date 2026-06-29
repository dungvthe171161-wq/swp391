<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BetterHR - Quản lý công việc</title>
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
                    <form action="${pageContext.request.contextPath}/taskManager" method="get" class="dept-form-grid">
                        <div class="dept-field">
                            <label>Tên công việc</label>
                            <input type="text" name="searchByTitle" value="${searchByTitle}" placeholder="Nhap ten...">
                        </div>
                        <div class="dept-field">
                            <label>Trạng thái</label>
                            <select name="filterStatus">
                                <option value="all" ${filterStatus eq 'all' ? 'selected' : ''}>Tất cả</option>
                                <option value="Waiting" ${filterStatus eq 'Waiting' ? 'selected' : ''}>Chờ xử lý</option>
                                <option value="In Progress" ${filterStatus eq 'In Progress' ? 'selected' : ''}>Đang thực hiện</option>
                                <option value="Completed" ${filterStatus eq 'Completed' ? 'selected' : ''}>Hoàn thành</option>
                                <option value="Rejected" ${filterStatus eq 'Rejected' ? 'selected' : ''}>Đã hủy</option>
                            </select>
                        </div>
                        <div class="dept-field">
                            <label>Tu ngay</label>
                            <input type="date" name="startDate" value="${startDate}">
                        </div>
                        <div class="dept-field">
                            <label>Den ngay</label>
                            <input type="date" name="endDate" value="${endDate}">
                        </div>
                        <button class="dept-btn" type="submit"><i class="fa-solid fa-magnifying-glass"></i> Lọc</button>
                        <a class="dept-btn secondary" href="${pageContext.request.contextPath}/taskManager">Xóa lọc</a>
                    </form>
                </div>
            </section>

            <section class="dept-panel">
                <div class="dept-panel-inner">
                    <table class="dept-table">
                        <thead>
                            <tr>
                                <th>Tieu de</th>
                                <th>Mô tả</th>
                                <th>Bắt đầu</th>
                                <th>Hết hạn</th>
                                <th>Trạng thái</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="task" items="${tasks}">
                                <tr>
                                    <td><strong>${task.title}</strong></td>
                                    <td>${task.description}</td>
                                    <td>${task.startDate}</td>
                                    <td>${task.dueDate}</td>
                                    <td>${task.status}</td>
                                    <td>
                                        <div style="display:flex; flex-wrap:wrap; gap:8px;">
                                            <a class="dept-btn secondary" href="${pageContext.request.contextPath}/viewTask?id=${task.taskId}">Xem/Sửa</a>
                                            <button class="dept-btn secondary" type="button" onclick="loadAssignees(${task.taskId})">Thành viên</button>
                                            <a class="dept-btn" href="${pageContext.request.contextPath}/taskManager?action=send&id=${task.taskId}">Gửi</a>
                                            <a class="dept-btn danger" href="${pageContext.request.contextPath}/taskManager?action=reject&id=${task.taskId}" onclick="return confirm('Hủy công việc này?');">Hủy</a>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty tasks}">
                                <tr><td colspan="6">Không có công việc.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </section>
        </section>
    </main>
</div>

<div id="assigneePanel" class="dept-panel" style="display:none; position:fixed; right:24px; bottom:24px; width:min(360px, calc(100vw - 48px)); z-index:40;">
    <div class="dept-panel-inner">
        <div style="display:flex; justify-content:space-between; gap:12px; align-items:center;">
            <h2>Thành viên tham gia</h2>
            <button class="dept-btn secondary" type="button" onclick="document.getElementById('assigneePanel').style.display='none'">Đóng</button>
        </div>
        <div id="assigneeList"></div>
    </div>
</div>

<script>
function loadAssignees(taskId) {
    const panel = document.getElementById('assigneePanel');
    const target = document.getElementById('assigneeList');
    panel.style.display = 'block';
    target.innerHTML = 'Đang tải...';
    fetch('${pageContext.request.contextPath}/taskManager?action=viewAssignees&id=' + encodeURIComponent(taskId), {
        headers: { 'X-Requested-With': 'XMLHttpRequest' }
    })
        .then(response => response.text())
        .then(html => { target.innerHTML = html; })
        .catch(() => { target.innerHTML = '<div class="dept-alert error">Không tải được danh sách.</div>'; });
}
</script>
</body>
</html>
