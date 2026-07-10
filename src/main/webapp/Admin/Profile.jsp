<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
                <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;500;600;700;800&display=swap" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet"><meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Hồ sơ quản trị - HRMS</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/Admin_home.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/unified-layout.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/user-menu.css">
        <style>
            .profile-container {
                display: grid;
                grid-template-columns: 1fr 2fr;
                gap: 24px;
                margin-bottom: 24px;
            }

            .profile-card {
                background: white;
                border: 1px solid #e5e7eb;
                border-radius: 12px;
                padding: 24px;
                box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
            }

            .profile-header-card {
                text-align: center;
            }

            .profile-avatar {
                width: 120px;
                height: 120px;
                border-radius: 50%;
                background: #00754a;
                display: flex;
                align-items: center;
                justify-content: center;
                color: white;
                font-size: 48px;
                font-weight: 700;
                margin: 0 auto 20px;
                border: 4px solid white;
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
            }

            .profile-name {
                font-size: 24px;
                font-weight: 600;
                color: #111827;
                margin-bottom: 8px;
            }

            .profile-role {
                font-size: 16px;
                color: #6b7280;
                margin-bottom: 16px;
            }

            .profile-status {
                display: inline-block;
                padding: 6px 12px;
                border-radius: 20px;
                font-size: 14px;
                font-weight: 500;
                margin-bottom: 20px;
            }

            .profile-status.active {
                background: rgba(34, 197, 94, 0.1);
                color: #16a34a;
            }

            .profile-status.inactive {
                background: rgba(239, 68, 68, 0.1);
                color: #dc2626;
            }

            .info-section {
                margin-bottom: 24px;
            }

            .info-section:last-child {
                margin-bottom: 0;
            }

            .info-section-title {
                font-size: 18px;
                font-weight: 600;
                color: #111827;
                margin-bottom: 16px;
                display: flex;
                align-items: center;
                gap: 8px;
            }

            .info-item {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 12px 0;
                border-bottom: 1px solid #f3f4f6;
            }

            .info-item:last-child {
                border-bottom: none;
            }

            .info-label {
                font-size: 14px;
                color: #6b7280;
                font-weight: 500;
            }

            .info-value {
                font-size: 14px;
                color: #111827;
                font-weight: 500;
                text-align: right;
            }

            .form-group {
                margin-bottom: 20px;
            }

            .form-label {
                display: block;
                font-size: 14px;
                font-weight: 600;
                color: #374151;
                margin-bottom: 8px;
            }

            .form-control {
                width: 100%;
                padding: 10px 12px;
                border: 1px solid #d1d5db;
                border-radius: 8px;
                font-size: 14px;
                transition: all 0.2s;
            }

            .form-control:focus {
                outline: none;
                border-color: #00754a;
                box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
            }

            .form-control:disabled {
                background-color: #f9fafb;
                color: #6b7280;
                cursor: not-allowed;
            }

            .btn-group {
                display: flex;
                gap: 12px;
                margin-top: 24px;
            }

            .btn {
                padding: 10px 20px;
                border-radius: 8px;
                font-size: 14px;
                font-weight: 600;
                cursor: pointer;
                border: none;
                transition: all 0.2s;
            }

            .btn-primary {
                background: #00754a;
                color: white;
            }

            .btn-primary:hover {
                transform: translateY(-2px);
                box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
            }

            .btn-secondary {
                background: white;
                color: #00754a;
                border: 2px solid #00754a;
            }

            .btn-secondary:hover {
                background: #f9fafb;
            }

            .activity-list {
                display: flex;
                flex-direction: column;
                gap: 12px;
            }

            .activity-item {
                display: flex;
                align-items: flex-start;
                gap: 12px;
                padding: 12px;
                background: #f9fafb;
                border-radius: 8px;
                border: 1px solid #f3f4f6;
            }

            .activity-icon {
                width: 36px;
                height: 36px;
                border-radius: 8px;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 16px;
                flex-shrink: 0;
            }

            .activity-icon.login {
                background: rgba(34, 197, 94, 0.1);
                color: #16a34a;
            }

            .activity-icon.update {
                background: rgba(59, 130, 246, 0.1);
                color: #3b82f6;
            }

            .activity-icon.password {
                background: rgba(251, 191, 36, 0.1);
                color: #f59e0b;
            }

            .activity-icon.default {
                background: rgba(107, 114, 128, 0.1);
                color: #6b7280;
            }

            .activity-content {
                flex: 1;
            }

            .activity-action {
                font-size: 14px;
                font-weight: 500;
                color: #111827;
                margin-bottom: 4px;
            }

            .activity-details {
                font-size: 12px;
                color: #6b7280;
                margin-bottom: 4px;
            }

            .activity-time {
                font-size: 12px;
                color: #9ca3af;
            }

            .empty-state {
                text-align: center;
                padding: 40px 20px;
                color: #9ca3af;
            }

            .empty-state-icon {
                font-size: 48px;
                margin-bottom: 12px;
                opacity: 0.5;
            }

            @media (max-width: 1024px) {
                .profile-container {
                    grid-template-columns: 1fr;
                }
            }

            .alert {
                padding: 12px 16px;
                border-radius: 8px;
                margin-bottom: 20px;
                display: flex;
                align-items: center;
                gap: 12px;
            }

            .alert-success {
                background: rgba(34, 197, 94, 0.1);
                color: #16a34a;
                border: 1px solid rgba(34, 197, 94, 0.2);
            }

            .alert-error {
                background: rgba(239, 68, 68, 0.1);
                color: #dc2626;
                border: 1px solid rgba(239, 68, 68, 0.2);
            }
        </style>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/admin-shared-theme.css?v=20260618d">
    </head>
    <body class="admin-page">
        <div class="dashboard-container">
            <!-- Sidebar -->
            <aside class="sidebar">
                <div class="sidebar-header">
                    <div class="logo">
                        <img src="https://cdn-icons-png.flaticon.com/512/847/847969.png" alt="Logo" width="32">
                        <span>Quản trị</span>
                    </div>
                </div>

                <div class="sidebar-nav">
                    <a href="${pageContext.request.contextPath}/admin?action=dashboard"
                       class="nav-item ${activePage == 'dashboard' ? 'active' : ''}">
                        <span class="material-symbols-outlined">dashboard</span>
                        <span>Tổng quan</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin?action=departments"
                       class="nav-item ${activePage == 'departments' ? 'active' : ''}">
                        <span class="material-symbols-outlined">domain</span>
                        <span>Phòng ban</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/users"
                       class="nav-item ${activePage == 'users' ? 'active' : ''}">
                        <span class="material-symbols-outlined">group</span>
                        <span>Người dùng</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin?action=role-permissions"
                       class="nav-item ${activePage == 'role-permissions' ? 'active' : ''}">
                        <span class="material-symbols-outlined">admin_panel_settings</span>
                        <span>Phân quyền</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin?action=audit-log"
                       class="nav-item ${activePage == 'audit-log' ? 'active' : ''}">
                        <span class="material-symbols-outlined">history</span>
                        <span>Nhật ký hệ thống</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin?action=profile"
                       class="nav-item ${activePage == 'profile' ? 'active' : ''}">
                        <span class="material-symbols-outlined">person</span>
                        <span>Hồ sơ</span>
                    </a>
                </div>            </aside>

            <!-- Main Content -->
            <main class="main-content">
                <!-- Top Bar -->
                <header class="top-bar">
                    <div class="search-box">
                        <span class="material-symbols-outlined search-icon">search</span>
                        <input class="search-input" type="text" placeholder="Tìm kiếm...">
                    </div>
                    <div class="top-bar-actions">
                        <div class="user-menu" onclick="toggleUserMenu()">
                            <div class="user-info">
                                <img src="${pageContext.request.contextPath}/Admin/images/admin-user-avatar.png" alt="Quản trị viên">
                                <span>Admin</span>
                                <span class="dropdown-arrow material-symbols-outlined">expand_more</span>
                            </div>
                            <div class="dropdown-menu" id="userDropdown">
                                <a href="${pageContext.request.contextPath}/admin?action=profile" class="dropdown-item">
                                    <span class="material-symbols-outlined">person</span> Hồ sơ
                                </a>
                                <a href="${pageContext.request.contextPath}/homepage" class="dropdown-item">
                                    <span class="material-symbols-outlined">home</span> Trang chủ
                                </a>
                                <div class="dropdown-divider"></div>
                                <a href="${pageContext.request.contextPath}/logout" class="dropdown-item">
                                    <span class="material-symbols-outlined">logout</span> Đăng xuất
                                </a>
                            </div>
                        </div>
                    </div>
                </header>

                <!-- Nội dung tổng quan -->
                <section class="dashboard-content">
                    <h1 class="page-title">Hồ sơ của tôi</h1>

                    <!-- Success/Error Messages -->
                    <c:if test="${not empty successMessage}">
                        <div class="alert alert-success">
                            <span>✓</span>
                            <span>${successMessage}</span>
                        </div>
                    </c:if>
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-error">
                            <span>!</span>
                            <span>${errorMessage}</span>
                        </div>
                    </c:if>

                    <div class="profile-container">
                        <!-- Thẻ đầu hồ sơ -->
                        <div class="profile-card profile-header-card">
                            <div class="profile-avatar">
                                <c:choose>
                                    <c:when test="${not empty currentUser.employee and not empty currentUser.employee.fullName}">
                                        ${fn:substring(currentUser.employee.fullName, 0, 1)}
                                    </c:when>
                                    <c:otherwise>
                                        ${fn:substring(currentUser.username, 0, 1)}
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="profile-name">
                                <c:choose>
                                    <c:when test="${not empty currentUser.employee and not empty currentUser.employee.fullName}">
                                        ${currentUser.employee.fullName}
                                    </c:when>
                                    <c:otherwise>
                                        ${currentUser.username}
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="profile-role">
                                <c:choose>
                                    <c:when test="${not empty currentUser.role}">
                                        ${currentUser.role.roleName}
                                    </c:when>
                                    <c:otherwise>
                                        Admin
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="profile-status ${currentUser.isActive ? 'active' : 'inactive'}">
                                ${currentUser.isActive ? 'Hoạt động' : 'Không hoạt động'}
                            </div>

                            <!-- Account Information -->
                            <div class="info-section" style="text-align: left; margin-top: 24px;">
                                <div class="info-item">
                                    <span class="info-label">Mã người dùng</span>
                                    <span class="info-value">#${currentUser.userId}</span>
                                </div>
                                <div class="info-item">
                                    <span class="info-label">Tên đăng nhập</span>
                                    <span class="info-value">${currentUser.username}</span>
                                </div>
                                <c:if test="${not empty currentUser.employee}">
                                    <c:if test="${not empty currentUser.employee.email}">
                                        <div class="info-item">
                                            <span class="info-label">Email</span>
                                            <span class="info-value">${currentUser.employee.email}</span>
                                        </div>
                                    </c:if>
                                    <c:if test="${not empty currentUser.employee.phone}">
                                        <div class="info-item">
                                            <span class="info-label">Phone</span>
                                            <span class="info-value">${currentUser.employee.phone}</span>
                                        </div>
                                    </c:if>
                                </c:if>
                                <c:if test="${not empty currentUser.department}">
                                    <div class="info-item">
                                        <span class="info-label">Department</span>
                                        <span class="info-value">${currentUser.department.deptName}</span>
                                    </div>
                                </c:if>
                                <c:if test="${not empty createdDateTimestamp}">
                                    <div class="info-item">
                                        <span class="info-label">Member Since</span>
                                        <span class="info-value">
                                            <fmt:formatDate value="${createdDateTimestamp}" pattern="MMM dd, yyyy"/>
                                        </span>
                                    </div>
                                </c:if>
                                <c:if test="${not empty lastLoginTimestamp}">
                                    <div class="info-item">
                                        <span class="info-label">Lần đăng nhập cuối</span>
                                        <span class="info-value">
                                            <fmt:formatDate value="${lastLoginTimestamp}" pattern="MMM dd, yyyy HH:mm"/>
                                        </span>
                                    </div>
                                </c:if>
                            </div>
                        </div>

                        <!-- Thẻ chi tiết hồ sơ -->
                        <div class="profile-card">
                            <div class="info-section">
                                <h2 class="info-section-title">
                                    <span class="material-symbols-outlined">lock_reset</span>
                                    Đổi mật khẩu
                                </h2>
                                <form action="${pageContext.request.contextPath}/Xems/ChangePassword.jsp" method="GET">
                                    <div class="form-group">
                                        <label class="form-label" for="currentPassword">Mật khẩu hiện tại</label>
                                        <input type="password" id="currentPassword" class="form-control" placeholder="Nhập mật khẩu hiện tại" disabled>
                                    </div>
                                    <div class="form-group">
                                        <label class="form-label" for="newPassword">Mật khẩu mới</label>
                                        <input type="password" id="newPassword" class="form-control" placeholder="Nhập mật khẩu mới" disabled>
                                    </div>
                                    <div class="form-group">
                                        <label class="form-label" for="confirmPassword">Xác nhận mật khẩu mới</label>
                                        <input type="password" id="confirmPassword" class="form-control" placeholder="Xác nhận mật khẩu mới" disabled>
                                    </div>
                                    <div class="btn-group">
                                        <button type="submit" class="btn btn-primary">Đổi mật khẩu</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>

                    <!-- Recent Activity -->
                    <div class="profile-card" style="margin-top: 24px;">
                        <h2 class="info-section-title">
                            <span class="material-symbols-outlined">history</span>
                            Recent Activity
                        </h2>
                        <div class="activity-list">
                            <c:choose>
                                <c:when test="${not empty recentLogs}">
                                    <c:forEach var="log" items="${recentLogs}">
                                        <div class="activity-item">
                                            <div class="activity-icon
                                                <c:choose>
                                                    <c:when test="${log.action == 'LOGIN'}">login</c:when>
                                                    <c:when test="${log.action == 'PROFILE_UPDATE' || log.action == 'UPDATE'}">update</c:when>
                                                    <c:when test="${log.action == 'PASSWORD_CHANGE' || log.action == 'CHANGE_PASSWORD'}">password</c:when>
                                                    <c:otherwise>default</c:otherwise>
                                                </c:choose>">
                                                <c:choose>
                                                    <c:when test="${log.action == 'LOGIN'}"><span class="material-symbols-outlined">login</span></c:when>
                                                    <c:when test="${log.action == 'PROFILE_UPDATE' || log.action == 'UPDATE'}"><span class="material-symbols-outlined">edit</span></c:when>
                                                    <c:when test="${log.action == 'PASSWORD_CHANGE' || log.action == 'CHANGE_PASSWORD'}"><span class="material-symbols-outlined">key</span></c:when>
                                                    <c:otherwise><span class="material-symbols-outlined">settings</span></c:otherwise>
                                                </c:choose>
                                            </div>
                                            <div class="activity-content">
                                                <div class="activity-action">${log.action}</div>
                                                <c:if test="${not empty log.newValue}">
                                                    <div class="activity-details">${log.newValue}</div>
                                                </c:if>
                                                <c:if test="${not empty log.objectType}">
                                                    <div class="activity-details">Object: ${log.objectType}</div>
                                                </c:if>
                                                <div class="activity-time">
                                                    <c:choose>
                                                        <c:when test="${not empty log.timestampDate}">
                                                            <fmt:formatDate value="${log.timestampDate}" pattern="MMM dd, yyyy HH:mm:ss" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            ${log.timestamp}
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <div class="empty-state">
                                        <div class="empty-state-icon"><span class="material-symbols-outlined">inbox</span></div>
                                        <div>Chưa có hoạt động gần đây</div>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </section>
            </main>
        </div>

        <script>
            function toggleUserMenu() {
                const userMenu = document.querySelector('.user-menu');
                userMenu.classList.toggle('active');
            }

            document.addEventListener('click', function (event) {
                if (!event.target.closest('.user-menu')) {
                    const userMenu = document.querySelector('.user-menu');
                    if (userMenu.classList.contains('active')) {
                        userMenu.classList.remove('active');
                    }
                }
            });

            setTimeout(function() {
                const alerts = document.querySelectorAll('.alert');
                alerts.forEach(alert => {
                    alert.style.opacity = '0';
                    alert.style.transition = 'opacity 0.5s';
                    setTimeout(() => alert.remove(), 500);
                });
            }, 5000);
        </script>
    </body>
</html>
