<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BetterHR - Tạo công việc</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <%@ include file="_DeptManagerStyles.jspf" %>
</head>
<body>
<div class="dept-shell">
    <%@ include file="_DeptManagerSidebar.jspf" %>
    <main class="dept-main">
        <%@ include file="_DeptManagerTopbar.jspf" %>
        <section class="dept-content">
            <c:if test="${not empty param.error}">
                <div class="dept-alert error">${param.error}</div>
            </c:if>
            <section class="dept-panel">
                <div class="dept-panel-inner">
                    <form action="${pageContext.request.contextPath}/postTask" method="post">
                        <div class="dept-field" style="margin-bottom:14px;">
                            <label>Tên công việc</label>
                            <input type="text" name="title" required maxlength="50">
                        </div>
                        <div class="dept-field" style="margin-bottom:14px;">
                            <label>Mô tả</label>
                            <textarea name="description" maxlength="1000"></textarea>
                        </div>
                        <div class="dept-form-grid" style="margin-bottom:14px;">
                            <div class="dept-field">
                                <label>Ngày bắt đầu</label>
                                <input type="date" name="startDate" required>
                            </div>
                            <div class="dept-field">
                                <label>Ngày hết hạn</label>
                                <input type="date" name="dueDate" required>
                            </div>
                        </div>
                        <div class="dept-field" style="margin-bottom:14px;">
                            <label>Giao cho</label>
                            <div class="dept-grid" style="grid-template-columns:repeat(auto-fit,minmax(220px,1fr));">
                                <c:forEach var="emp" items="${employeeList}">
                                    <label style="display:flex; gap:10px; align-items:center; padding:12px; border:1px solid var(--dept-border-soft); border-radius:10px; background:#fff;">
                                        <input type="checkbox" name="assignTo" value="${emp.employeeId}">
                                        <span>${emp.fullName}</span>
                                    </label>
                                </c:forEach>
                            </div>
                        </div>
                        <div style="display:flex; justify-content:flex-end; gap:10px;">
                            <a class="dept-btn secondary" href="${pageContext.request.contextPath}/taskManager">Quay lại</a>
                            <button class="dept-btn" type="submit">Tạo công việc</button>
                        </div>
                    </form>
                </div>
            </section>
        </section>
    </main>
</div>
</body>
</html>
