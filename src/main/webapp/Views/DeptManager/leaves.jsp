<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BetterHR - Yêu cầu nghỉ phép</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <%@ include file="_DeptManagerStyles.jspf" %>
</head>
<body>
<div class="dept-shell">
    <%@ include file="_DeptManagerSidebar.jspf" %>
    <main class="dept-main">
        <%@ include file="_DeptManagerTopbar.jspf" %>
        <section class="dept-content">
            <c:if test="${not empty deptLeaveSuccess}">
                <div class="dept-alert success">${deptLeaveSuccess}</div>
            </c:if>
            <c:if test="${not empty deptLeaveError}">
                <div class="dept-alert error">${deptLeaveError}</div>
            </c:if>

            <div style="display:flex; gap:10px; margin-bottom:16px; flex-wrap:wrap;">
                <a class="dept-btn secondary" href="${pageContext.request.contextPath}/dept/leaves?status=Pending">Chờ duyệt</a>
                <a class="dept-btn secondary" href="${pageContext.request.contextPath}/dept/leaves?status=Approved">Đã duyệt</a>
                <a class="dept-btn secondary" href="${pageContext.request.contextPath}/dept/leaves?status=Rejected">Từ chối</a>
                <a class="dept-btn secondary" href="${pageContext.request.contextPath}/dept/leaves?status=All">Tất cả</a>
            </div>

            <section class="dept-panel">
                <div class="dept-panel-inner">
                    <table class="dept-table">
                        <thead>
                            <tr>
                                <th>Nhân viên</th>
                                <th>Loai</th>
                                <th>Từ ngày</th>
                                <th>Đến ngày</th>
                                <th>Lý do</th>
                                <th>Trạng thái</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${leaveRequests}">
                                <tr>
                                    <td><strong>${item.employeeName}</strong><br>${item.employeeEmail}</td>
                                    <td>${item.leaveType}</td>
                                    <td>${item.startDate}</td>
                                    <td>${item.endDate}</td>
                                    <td>${item.reason}</td>
                                    <td>${item.status}</td>
                                    <td>
                                        <c:if test="${item.status eq 'Pending'}">
                                            <form method="post" action="${pageContext.request.contextPath}/dept/leaves" style="display:inline-flex; gap:8px;">
                                                <input type="hidden" name="requestId" value="${item.requestId}">
                                                <button class="dept-btn" type="submit" name="decision" value="Approved">Duyệt</button>
                                                <button class="dept-btn danger" type="submit" name="decision" value="Rejected">Từ chối</button>
                                            </form>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty leaveRequests}">
                                <tr><td colspan="7">Không có đơn nghỉ phép.</td></tr>
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
