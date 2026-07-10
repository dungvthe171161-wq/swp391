<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BetterHR - Cong viec</title>
    <%@ include file="_EmployeeStyles.jspf" %>
</head>
<body>
<div class="employee-shell">
    <%@ include file="_EmployeeSidebar.jspf" %>
    <main class="employee-main">
        <div class="content">
            <h1 class="page-title">Cong viec cua toi</h1>
            <p class="page-note">Theo dõi deadline và nộp kết quả công việc được giao.</p>

            <c:if test="${not empty employeeSuccess}">
                <div class="alert success">${employeeSuccess}</div>
            </c:if>
            <c:if test="${not empty employeeError}">
                <div class="alert error">${employeeError}</div>
            </c:if>

            <section class="panel">
                <div class="panel-inner">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Cong viec</th>
                                <th>Uu tien</th>
                                <th>Deadline</th>
                                <th>Trang thai</th>
                                <th>Cap nhat</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="task" items="${tasks}">
                                <tr>
                                    <td>
                                        <strong>${task.title}</strong><br>
                                        <span style="color:var(--bh-muted);">${task.description}</span>
                                        <c:if test="${not empty task.attachmentPath}">
                                            <br><a href="${pageContext.request.contextPath}/${task.attachmentPath}" target="_blank">File dinh kem</a>
                                        </c:if>
                                        <c:if test="${not empty task.feedback}">
                                            <br><span style="color:var(--bh-danger);">Phan hoi: ${task.feedback}</span>
                                        </c:if>
                                    </td>
                                    <td>${task.priority}</td>
                                    <td>
                                        ${task.dueDate}
                                        <c:if test="${not empty task.dueReminder}">
                                            <br><span style="color:var(--bh-danger);">Con ${task.dueReminder} de submit</span>
                                        </c:if>
                                    </td>
                                    <td>${task.status}</td>
                                    <td>
                                        <form method="post" action="${pageContext.request.contextPath}/employee/tasks" class="button-row" style="align-items:flex-start;">
                                            <input type="hidden" name="taskId" value="${task.taskId}">
                                            <select name="status">
                                                <option value="Waiting" ${task.assignmentStatus eq 'Waiting' ? 'selected' : ''}>Cho nhan</option>
                                                <option value="In Progress" ${task.assignmentStatus eq 'In Progress' ? 'selected' : ''}>Dang thuc hien</option>
                                                <option value="Submitted" ${task.assignmentStatus eq 'Submitted' ? 'selected' : ''}>Gui hoan thanh</option>
                                            </select>
                                            <textarea name="feedback" maxlength="1000" placeholder="Ghi chú công việc">${task.feedback}</textarea>
                                            <button class="secondary-button" type="submit">Luu</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty tasks}">
                                <tr><td colspan="5">Chua co cong viec duoc giao.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    </main>
</div>
</body>
</html>
