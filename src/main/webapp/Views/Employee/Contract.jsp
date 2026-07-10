<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BetterHR - Hợp đồng</title>
    <%@ include file="_EmployeeStyles.jspf" %>
</head>
<body>
<div class="employee-shell">
    <%@ include file="_EmployeeSidebar.jspf" %>
    <main class="employee-main">
        <div class="content">
            <h1 class="page-title">Hợp đồng</h1>
            <p class="page-note">Hiển thị hợp đồng mới nhất theo EmployeeID của nhân viên.</p>
            <section class="panel">
                <div class="panel-inner">
                    <c:choose>
                        <c:when test="${not empty contract}">
                            <div class="stat-row">
                                <div class="stat"><span>Mã hợp đồng</span><strong>${contract.contractId}</strong></div>
                                <div class="stat"><span>Ngày bắt đầu</span><strong>${contract.startDate}</strong></div>
                                <div class="stat"><span>Ngày kết thúc</span><strong>${contract.endDate}</strong></div>
                                <div class="stat"><span>Lương cơ bản</span><strong>${contract.baseSalary}</strong></div>
                                <div class="stat"><span>Phụ cấp</span><strong>${contract.allowance}</strong></div>
                                <div class="stat"><span>Loại hợp đồng</span><strong>${contract.contractType}</strong></div>
                                <div class="stat"><span>Trạng thái</span><strong>${contract.status}</strong></div>
                                <div class="stat"><span>Ghi chú</span><strong>${contract.note}</strong></div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <p>Chưa có hợp đồng cho nhân viên này.</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </section>
        </div>
    </main>
</div>
</body>
</html>
