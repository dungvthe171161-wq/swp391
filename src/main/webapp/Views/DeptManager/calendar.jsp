<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BetterHR - Lịch nhóm</title>
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
                    <h2>Lịch nghỉ đã duyệt</h2>
                    <table class="dept-table">
                        <thead>
                            <tr><th>Nhân viên</th><th>Loại nghỉ</th><th>Từ ngày</th><th>Đến ngày</th><th>Lý do</th></tr>
                        </thead>
                        <tbody>
                            <c:forEach var="leave" items="${approvedLeaves}">
                                <tr>
                                    <td>${leave.employeeName}</td>
                                    <td>${leave.leaveType}</td>
                                    <td>${leave.startDate}</td>
                                    <td>${leave.endDate}</td>
                                    <td>${leave.reason}</td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty approvedLeaves}">
                                <tr><td colspan="5">Chưa có lịch nghỉ đã duyệt.</td></tr>
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
