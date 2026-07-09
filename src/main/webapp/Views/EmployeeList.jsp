<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HRM - Danh sách nhân viên</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        :root {
            --primary-color: #667eea;
            --secondary-color: #764ba2;
        }
        body {
            background-color: #f8f9fa;
        }
        .sidebar {
            background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
            min-height: 100vh;
            color: white;
        }
        .sidebar .nav-link {
            color: rgba(255, 255, 255, 0.8);
            padding: 12px 20px;
            margin: 5px 10px;
            border-radius: 10px;
            transition: all 0.3s;
        }
        .sidebar .nav-link:hover,
        .sidebar .nav-link.active {
            background: rgba(255, 255, 255, 0.2);
            color: white;
        }
        .main-content {
            padding: 20px;
        }
    </style>
</head>
<body>
<div class="container-fluid">
    <div class="row">
        <!-- Sidebar -->
        <nav class="col-md-3 col-lg-2 d-md-block sidebar collapse">
            <div class="position-sticky pt-3">
                <div class="text-center mb-4">
                    <h4><i class="fas fa-users-cog me-2"></i>HRM System</h4>
                </div>
                <ul class="nav flex-column">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/Views/Home.jsp">
                            <i class="fas fa-home me-2"></i> Trang chủ
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/Views/Dashboard.jsp">
                            <i class="fas fa-tachometer-alt me-2"></i> Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/admin/employees">
                            <i class="fas fa-users me-2"></i> Danh sách nhân viên
                        </a>
                    </li>
                     <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/admin/employees?action=new">
                            <i class="fas fa-user-plus me-2"></i> Thêm nhân viên
                        </a>
                    </li>
                </ul>
                <hr class="text-white-50">
<div class="user-info p-3 d-flex align-items-center">
                    <div class="user-avatar me-2">
                       <i class="fas fa-user-circle fa-2x"></i>
                    </div>
                    <div>
                        <div class="fw-bold">${sessionScope.systemUser.username}</div>
                    </div>
                </div>
                <ul class="nav flex-column">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/profile">
                            <i class="fas fa-user me-2"></i> Hồ sơ
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/change-password">
                            <i class="fas fa-key me-2"></i> Đổi mật khẩu
                        </a>
                    </li>
                    <li class="nav-item">
                         <a class="nav-link" href="${pageContext.request.contextPath}/logout">
                            <i class="fas fa-sign-out-alt me-2"></i> Đăng xuất
                        </a>
                    </li>
                </ul>
            </div>
        </nav>

        <!-- Main content -->
        <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4 main-content">
            <c:if test="${not empty param.success}">
                <div class="alert alert-success alert-dismissible fade show mt-3" role="alert">
                    <c:choose>
                        <c:when test="${param.success == 'created'}">Đã thêm nhân viên thành công.</c:when>
                        <c:when test="${param.success == 'updated'}">Đã cập nhật nhân viên thành công.</c:when>
                        <c:when test="${param.success == 'deleted'}">Đã xóa nhân viên thành công.</c:when>
                        <c:otherwise>Thao tác thành công.</c:otherwise>
                    </c:choose>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>
            <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                <h1 class="h2">Danh sách nhân viên</h1>
                <div class="btn-toolbar mb-2 mb-md-0">
                    <a href="${pageContext.request.contextPath}/admin/employees?action=new" class="btn btn-sm btn-outline-secondary">
                        Thêm nhân viên
                    </a>
                </div>
            </div>

            <div class="table-responsive">
                <table class="table table-striped table-sm">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Họ và tên</th>
                            <th>Giới tính</th>
<th>Ngày sinh</th>
                            <th>Phòng ban</th>
                            <th>Chức vụ</th>
                            <th>Ngày vào làm</th>
                            <th>Lương</th>
                            <th>Email</th>
                            <th>Trạng thái</th>
                            <th>Hành động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="e" items="${employees}">
                            <tr>
                                <td>${e.employeeId}</td>
                                <td>${e.fullName}</td>
                                <td>${e.gender}</td>
                                <td>${e.dob}</td>
                                <td>${e.departmentName}</td>
                                <td>${e.position}</td>
                                <td>${e.hireDate}</td>
                                <td>${e.salary}</td>
                                <td>${e.email}</td>
                                <td><c:out value="${e.active ? 'Hoạt động' : 'Không hoạt động'}"/></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/employees?action=edit&id=${e.employeeId}">Sửa</a> |
                                    <a href="${pageContext.request.contextPath}/admin/employees?action=delete&id=${e.employeeId}"
                                       onclick="return confirm('Bạn có chắc chắn muốn xóa nhân viên này?')">
                                        Xóa
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </main>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
