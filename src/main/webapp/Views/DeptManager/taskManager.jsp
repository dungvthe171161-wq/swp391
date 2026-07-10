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
            <c:if test="${not empty param.mess}">
                <div class="dept-alert success">${param.mess}</div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="dept-alert error">${param.error}</div>
            </c:if>

            <section class="dept-panel">
                <div class="dept-panel-inner">
                    <form action="${pageContext.request.contextPath}/taskManager" method="get" class="dept-form-grid">
                        <div class="dept-field">
                            <label>Tên công việc</label>
                            <input type="text" name="searchByTitle" value="${searchByTitle}" placeholder="Nhập tên...">
                        </div>
                        <div class="dept-field">
                            <label>Trang thai</label>
                            <select name="filterStatus">
                                <option value="all" ${filterStatus eq 'all' ? 'selected' : ''}>Tat ca</option>
                                <option value="Waiting" ${filterStatus eq 'Waiting' ? 'selected' : ''}>Chờ nhận</option>
                                <option value="In Progress" ${filterStatus eq 'In Progress' ? 'selected' : ''}>Đang thực hiện</option>
                                <option value="Submitted" ${filterStatus eq 'Submitted' ? 'selected' : ''}>Đã Nộp</option>
                                <option value="Approved" ${filterStatus eq 'Approved' ? 'selected' : ''}>Da duyet</option>
                                <option value="Rejected" ${filterStatus eq 'Rejected' ? 'selected' : ''}>Yeu cau lam lai</option>
                                <option value="Overdue" ${filterStatus eq 'Overdue' ? 'selected' : ''}>Qua han</option>
                                <option value="Cancelled" ${filterStatus eq 'Cancelled' ? 'selected' : ''}>Da huy</option>
                            </select>
                        </div>
                        <div class="dept-field">
                            <label>Từ ngày</label>
                            <input type="date" name="startDate" value="${startDate}">
                        </div>
                        <div class="dept-field">
                            <label>Đến ngày</label>
                            <input type="date" name="endDate" value="${endDate}">
                        </div>
                        <button class="dept-btn" type="submit"><i class="fa-solid fa-magnifying-glass"></i> Lọc</button>
                        <a class="dept-btn secondary" href="${pageContext.request.contextPath}/taskManager">Xoá lọc</a>
                    </form>
                </div>
            </section>

            <section class="dept-panel">
                <div class="dept-panel-inner">
                    <h2 style="margin-top:0;">Danh sách công việc phòng ban</h2>
                    <table class="dept-table">
                        <thead>
                            <tr>
                                <th>Tiêu đề</th>
                                <th>Ưu tiên</th>
                                <th>Bắt đầu</th>
                                <th>Deadline</th>
                                <th>Trạng thái chung</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="task" items="${tasks}">
                                <tr>
                                    <td><strong>${task.title}</strong><br><span style="color:var(--dept-muted);">${task.description}</span></td>
                                    <td>${task.priority}</td>
                                    <td>${task.startDate}</td>
                                    <td>${task.dueDate}</td>
                                    <td>${task.status}</td>
                                    <td>
                                        <div style="display:flex; flex-wrap:wrap; gap:8px;">
                                            <a class="dept-btn secondary" href="${pageContext.request.contextPath}/viewTask?id=${task.taskId}">Xem/Sửa</a>
                                            <button class="dept-btn secondary" type="button" onclick="loadAssignees(${task.taskId})">Thành viên</button>
                                            <c:choose>
                                                <c:when test="${task.status eq 'Cancelled'}">
                                                    <a class="dept-btn danger" href="${pageContext.request.contextPath}/taskManager?action=delete&id=${task.taskId}" onclick="return confirm('Xóa vĩnh viễn công việc đã hủy này?');">Xóa</a>
                                                </c:when>
                                                <c:otherwise>
                                                    <a class="dept-btn danger" href="${pageContext.request.contextPath}/taskManager?action=cancel&id=${task.taskId}" onclick="return confirm('Hủy công việc này?');">Hủy</a>
                                                </c:otherwise>
                                            </c:choose>
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

            <section class="dept-panel">
                <div class="dept-panel-inner">
                    <h2 style="margin-top:0;">Lịch công việc phòng ban</h2>
                    <table class="dept-table">
                        <thead>
                            <tr>
                                <th>Nhân viên</th>
                                <th>Công việc đang làm</th>
                                <th>Trạng thái</th>
                                <th>Deadline</th>
                                <th>Duyệt kết quả</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="row" items="${workloadRows}">
                                <tr>
                                    <td>
                                        <strong>${row.fullName}</strong><br>
                                        <span style="color:var(--dept-muted);">${row.position}</span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty row.taskId}">${row.title}</c:when>
                                            <c:otherwise><span style="color:var(--dept-muted);">Dang ranh</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>${row.displayStatus}</td>
                                    <td>${row.dueDate}</td>
                                    <td>
                                        <c:if test="${row.displayStatus eq 'Submitted'}">
                                            <form method="get" action="${pageContext.request.contextPath}/taskManager" style="display:grid; gap:8px; min-width:220px;">
                                                <input type="hidden" name="id" value="${row.taskId}">
                                                <input type="hidden" name="employeeId" value="${row.employeeId}">
                                                <textarea name="feedback" placeholder="Lý do nếu cần làm lại" maxlength="1000"></textarea>
                                                <div style="display:flex; gap:8px;">
                                                    <button class="dept-btn" name="action" value="approve" type="submit">Duyệt</button>
                                                    <button class="dept-btn danger" name="action" value="reject" type="submit">Làm lại</button>
                                                </div>
                                            </form>
                                        </c:if>
                                        <c:if test="${row.displayStatus ne 'Submitted'}">
                                            <span style="color:var(--dept-muted);">${row.feedback}</span>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty workloadRows}">
                                <tr><td colspan="5">Chưa có nhân viên trong phòng ban.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </section>
        </section>
    </main>
</div>

<div id="assigneePanel" class="dept-panel" style="display:none; position:fixed; right:24px; bottom:24px; width:min(420px, calc(100vw - 48px)); z-index:40;">
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
