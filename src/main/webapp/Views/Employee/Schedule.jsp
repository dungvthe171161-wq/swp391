<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BetterHR - Lich lam viec</title>
    <%@ include file="_EmployeeStyles.jspf" %>
</head>
<body>
<div class="employee-shell">
    <%@ include file="_EmployeeSidebar.jspf" %>
    <main class="employee-main">
        <%@ include file="_EmployeeTopbar.jspf" %>
        <div class="content">
            <h1 class="page-title">L&#7883;ch l&#224;m vi&#7879;c</h1>
            <section class="panel">
                <div class="panel-inner">
                    <h2 class="page-title" style="font-size:22px;">H&#244;m nay</h2>
                    <c:choose>
                        <c:when test="${not empty todaySchedule}">
                            <div class="stat-row">
                                <div class="stat"><span>M&#227; ca</span><strong>${todaySchedule.scheduleCode}</strong></div>
                                <div class="stat"><span>T&#234;n ca</span><strong>${todaySchedule.scheduleName}</strong></div>
                                <div class="stat"><span>B&#7855;t &#273;&#7847;u</span><strong>${todaySchedule.startTime}</strong></div>
                                <div class="stat"><span>K&#7871;t th&#250;c</span><strong>${todaySchedule.endTime}</strong></div>
                                <div class="stat"><span>Ghi ch&#250;</span><strong>${todaySchedule.note}</strong></div>
                            </div>
                        </c:when>
                        <c:otherwise><p class="page-note">H&#244;m nay b&#7841;n ch&#432;a c&#243; l&#7883;ch l&#224;m vi&#7879;c &#273;&#432;&#7907;c ph&#226;n c&#244;ng.</p></c:otherwise>
                    </c:choose>
                </div>
            </section>
            <section class="panel" style="margin-top:22px;">
                <div class="panel-inner">
                    <form method="get" action="${pageContext.request.contextPath}/employee/schedule" class="button-row">
                        <label>Th&#225;ng <input type="number" name="month" min="1" max="12" value="${month}"></label>
                        <label>N&#259;m <input type="number" name="year" min="2020" max="2100" value="${year}"></label>
                        <button class="primary-button" type="submit">L&#7885;c</button>
                    </form>
                    <table class="table" style="margin-top:18px;">
                        <thead><tr><th>Ng&#224;y</th><th>M&#227; ca</th><th>T&#234;n ca</th><th>Gi&#7901; b&#7855;t &#273;&#7847;u</th><th>Gi&#7901; k&#7871;t th&#250;c</th><th>Ghi ch&#250;</th></tr></thead>
                        <tbody>
                        <c:forEach var="item" items="${monthlySchedules}">
                            <tr><td>${item.workDate}</td><td>${item.scheduleCode}</td><td>${item.scheduleName}</td><td>${item.startTime}</td><td>${item.endTime}</td><td>${item.note}</td></tr>
                        </c:forEach>
                        <c:if test="${empty monthlySchedules}"><tr><td colspan="6">Ch&#432;a c&#243; l&#7883;ch trong th&#225;ng n&#224;y.</td></tr></c:if>
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    </main>
</div>
</body>
</html>
