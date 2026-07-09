<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BetterHR - Đánh giá hiệu suất</title>
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
                    <h2>Hiệu suất theo nhân viên</h2>
                    <table class="dept-table">
                        <thead>
                            <tr><th>Mã NV</th><th>Họ tên</th><th>Vị trí</th><th>Trạng thái</th></tr>
                        </thead>
                        <tbody>
                            <c:forEach var="emp" items="${employees}">
                                <tr>
                                    <td>${emp.employeeId}</td>
                                    <td>${emp.fullName}</td>
                                    <td>${emp.position}</td>
                                    <td>${emp.status}</td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty employees}">
                                <tr><td colspan="4">Không có nhân viên để đánh giá.</td></tr>
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
