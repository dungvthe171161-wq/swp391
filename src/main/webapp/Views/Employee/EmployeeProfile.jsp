<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BetterHR - Hồ sơ cá nhân</title>
    <%@ include file="_EmployeeStyles.jspf" %>
</head>
<body>
<div class="employee-shell">
    <%@ include file="_EmployeeSidebar.jspf" %>
    <main class="employee-main">
        <div class="content">
            <h1 class="page-title">Hồ sơ cá nhân</h1>
            <p class="page-note">Thông tin nhân sự được lấy từ hồ sơ Employee của tài khoản đang đăng nhập.</p>

            <section class="panel">
                <div class="panel-inner">
                    <div class="grid-2">
                        <div>
                            <div class="field">
                                <label>Mã nhân viên</label>
                                <input value="NV-${currentEmployee.employeeId}" readonly>
                            </div>
                            <div class="field">
                                <label>Họ và tên</label>
                                <input value="${currentEmployee.fullName}" readonly>
                            </div>
                            <div class="field">
                                <label>Giới tính</label>
                                <input value="${currentEmployee.gender}" readonly>
                            </div>
                            <div class="field">
                                <label>Ngày sinh</label>
                                <input value="${currentEmployee.dob}" readonly>
                            </div>
                            <div class="field">
                                <label>Địa chỉ</label>
                                <textarea rows="3" readonly>${currentEmployee.address}</textarea>
                            </div>
                        </div>
                        <div>
                            <div class="field">
                                <label>Email</label>
                                <input value="${currentEmployee.email}" readonly>
                            </div>
                            <div class="field">
                                <label>Số điện thoại</label>
                                <input value="${currentEmployee.phone}" readonly>
                            </div>
                            <div class="field">
                                <label>Phòng ban</label>
                                <input value="${currentEmployee.departmentName}" readonly>
                            </div>
                            <div class="field">
                                <label>Vị trí</label>
                                <input value="${currentEmployee.position}" readonly>
                            </div>
                            <div class="field">
                                <label>Trạng thái</label>
                                <input value="${currentEmployee.status}" readonly>
                            </div>
                        </div>
                    </div>
                </div>
            </section>
        </div>
    </main>
</div>
</body>
</html>
