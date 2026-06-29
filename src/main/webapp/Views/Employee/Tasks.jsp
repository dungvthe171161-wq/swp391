<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BetterHR - C&#244;ng vi&#7879;c</title>
    <%@ include file="_EmployeeStyles.jspf" %>
</head>
<body>
<div class="employee-shell">
    <%@ include file="_EmployeeSidebar.jspf" %>
    <main class="employee-main">
        <div class="content">
            <h1 class="page-title">C&#244;ng vi&#7879;c c&#7911;a t&#244;i</h1>
            <p class="page-note">Danh s&#225;ch l&#7845;y t&#7915; Task v&#224; assignList theo EmployeeID c&#7911;a b&#7841;n.</p>

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
                                <th>Ti&#234;u &#273;&#7873;</th>
                                <th>Ng&#224;y b&#7855;t &#273;&#7847;u</th>
                                <th>H&#7841;n</th>
                                <th>Tr&#7841;ng th&#225;i</th>
                                <th>C&#7853;p nh&#7853;t</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="task" items="${tasks}">
                                <tr>
                                    <td>
                                        <strong>${task.title}</strong><br>
                                        <span style="color:var(--bh-muted);">${task.description}</span>
                                    </td>
                                    <td>${task.startDate}</td>
                                    <td>${task.dueDate}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${task.status eq 'Waiting'}">Ch&#7901; x&#7917; l&#253;</c:when>
                                            <c:when test="${task.status eq 'In Progress'}">&#272;ang th&#7921;c hi&#7879;n</c:when>
                                            <c:when test="${task.status eq 'Completed'}">Ho&#224;n th&#224;nh</c:when>
                                            <c:when test="${task.status eq 'Rejected'}">T&#7915; ch&#7889;i</c:when>
                                            <c:otherwise>${task.status}</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <form method="post" action="${pageContext.request.contextPath}/employee/tasks" class="button-row">
                                            <input type="hidden" name="taskId" value="${task.taskId}">
                                            <select name="status">
                                                <option value="Waiting">Ch&#7901; x&#7917; l&#253;</option>
                                                <option value="In Progress">&#272;ang th&#7921;c hi&#7879;n</option>
                                                <option value="Completed">Ho&#224;n th&#224;nh</option>
                                                <option value="Rejected">T&#7915; ch&#7889;i</option>
                                            </select>
                                            <button class="secondary-button" type="submit">L&#432;u</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty tasks}">
                                <tr><td colspan="5">Ch&#432;a c&#243; c&#244;ng vi&#7879;c &#273;&#432;&#7907;c giao.</td></tr>
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
