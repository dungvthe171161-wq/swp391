<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BetterHR - Chi tiết công việc</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <%@ include file="_DeptManagerStyles.jspf" %>
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/chatbot.css">
    <link href="https://cdn.jsdelivr.net/npm/tom-select@2.2.2/dist/css/tom-select.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/tom-select@2.2.2/dist/js/tom-select.complete.min.js"></script>
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
                    <form action="${pageContext.request.contextPath}/viewTask" method="post">
                        <input type="hidden" name="taskId" value="${task.taskId}">
                        <div class="dept-form-grid" style="grid-template-columns:2fr 1fr 1fr;">
                            <div class="dept-field">
                                <label>Tên công việc</label>
                                <input type="text" name="title" value="${task.title}" required maxlength="50">
                            </div>
                            <div class="dept-field">
                                <label>Trạng thái chung</label>
                                <input type="text" value="${task.status}" readonly>
                            </div>
                            <div class="dept-field">
                                <label>Mức độ ưu tiên</label>
                                <select name="priority" required>
                                    <option value="Low" ${task.priority eq 'Low' ? 'selected' : ''}>Thấp</option>
                                    <option value="Normal" ${empty task.priority || task.priority eq 'Normal' ? 'selected' : ''}>Thường</option>
                                    <option value="High" ${task.priority eq 'High' ? 'selected' : ''}>Cao</option>
                                </select>
                            </div>
                        </div>
                        <div class="dept-field" style="margin-top:14px;">
                            <label>Mô tả</label>
                            <textarea name="description" maxlength="1000">${task.description}</textarea>
                        </div>
                        <div class="dept-form-grid" style="margin-top:14px;">
                            <div class="dept-field">
                                <label>Thời gian bắt đầu</label>
                                <input type="datetime-local" name="startDate" id="taskStartDate" value="${task.startDate}" required>
                            </div>
                            <div class="dept-field">
                                <label>Deadline</label>
                                <input type="datetime-local" name="dueDate" id="taskDueDate" value="${task.dueDate}" required>
                            </div>
                        </div>
                        <c:if test="${not empty task.attachmentPath}">
                            <div class="dept-field" style="margin-top:14px;">
                                <label>File đính kèm</label>
                                <a href="${pageContext.request.contextPath}/${task.attachmentPath}" target="_blank">${task.attachmentPath}</a>
                            </div>
                        </c:if>
                        <div class="dept-field" style="margin-top:14px; margin-bottom:20px;">
                            <label>Giao cho</label>
                            <select id="assignToSelect" name="assignTo" multiple placeholder="Tìm và chọn nhân viên..." autocomplete="off" required>
                                <c:forEach var="emp" items="${employeeList}">
                                    <c:set var="isSelected" value="false" />
                                    <c:forEach var="assignedId" items="${assignedEmployeeIds}">
                                        <c:if test="${assignedId == emp.employeeId}">
                                            <c:set var="isSelected" value="true" />
                                        </c:if>
                                    </c:forEach>
                                    <option value="${emp.employeeId}" ${isSelected ? 'selected' : ''}>${emp.fullName} - ${emp.position}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div style="display:flex; justify-content:flex-end; gap:10px; margin-top:18px;">
                            <a class="dept-btn secondary" href="${pageContext.request.contextPath}/taskManager">Quay lại</a>
                            <button class="dept-btn" type="submit">Lưu thay đổi</button>
                        </div>
                    </form>
                </div>
            </section>
        </section>
    </main>
</div>
    <%@ include file="../AI/AI_Assistant_Widget.jspf" %>
    <script charset="UTF-8" src="${pageContext.request.contextPath}/js/chatbot.js"></script>
<script>
const startInput = document.getElementById('taskStartDate');
const dueInput = document.getElementById('taskDueDate');
if (startInput && dueInput) {
    const syncDueMin = () => {
        dueInput.min = startInput.value || '';
        if (dueInput.value && startInput.value && dueInput.value < startInput.value) {
            dueInput.value = startInput.value;
        }
    };
    syncDueMin();
    startInput.addEventListener('change', syncDueMin);
}

// Khởi tạo Tom Select cho bộ chọn nhân viên
if (document.getElementById('assignToSelect')) {
    new TomSelect('#assignToSelect', {
        plugins: ['remove_button'],
        maxItems: null,
        persist: false,
        create: false
    });
}
</script>
</body>
</html>
