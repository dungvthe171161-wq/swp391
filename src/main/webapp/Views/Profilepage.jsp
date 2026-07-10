<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>BetterHR - Hồ sơ cá nhân</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
        <style>
            :root {
                --bh-green: #006241;
                --bh-accent: #00754A;
                --bh-house: #1E3932;
                --bh-canvas: #f2f0eb;
                --bh-ceramic: #edebe9;
                --bh-mint: #d4e9e2;
                --bh-white: #ffffff;
                --bh-text: rgba(0, 0, 0, .87);
                --bh-soft-text: rgba(0, 0, 0, .68);
                --bh-muted: rgba(0, 0, 0, .54);
                --bh-border: rgba(0, 0, 0, .12);
                --bh-danger: #c82014;
                --bh-danger-soft: #fff4f2;
            }

            * {
                box-sizing: border-box;
                margin: 0;
                padding: 0;
            }

            body {
                min-height: 100vh;
                background: var(--bh-canvas);
                color: var(--bh-text);
                font-family: "Inter", "Helvetica Neue", Arial, sans-serif;
                line-height: 1.55;
            }

            a {
                color: inherit;
                text-decoration: none;
            }

            .topbar {
                background: var(--bh-house);
                color: var(--bh-white);
                border-bottom: 1px solid rgba(255, 255, 255, .12);
            }

            .topbar-inner {
                width: min(1180px, calc(100% - 32px));
                min-height: 76px;
                margin: 0 auto;
                display: flex;
                align-items: center;
                justify-content: space-between;
                gap: 18px;
            }

            .brand {
                display: inline-flex;
                align-items: center;
                gap: 12px;
                font-size: 1.35rem;
                font-weight: 800;
                letter-spacing: 0;
            }

            .brand-mark,
            .nav-avatar,
            .profile-avatar {
                display: inline-flex;
                align-items: center;
                justify-content: center;
                border-radius: 50%;
            }

            .brand-mark {
                width: 40px;
                height: 40px;
                background: var(--bh-accent);
                color: var(--bh-white);
                box-shadow: inset 0 0 0 1px rgba(255, 255, 255, .18);
            }

            .topnav {
                display: flex;
                align-items: center;
                gap: 14px;
            }

            .home-link {
                display: inline-flex;
                align-items: center;
                gap: 8px;
                padding: 10px 14px;
                border-radius: 999px;
                color: rgba(255, 255, 255, .9);
                font-weight: 700;
                transition: background .2s ease, color .2s ease;
            }

            .home-link:hover {
                background: rgba(255, 255, 255, .12);
                color: var(--bh-white);
            }

            .profile-dropdown {
                position: relative;
            }

            .nav-avatar {
                width: 42px;
                height: 42px;
                border: 1px solid rgba(255, 255, 255, .24);
                background: rgba(255, 255, 255, .12);
                color: var(--bh-white);
                cursor: pointer;
                transition: background .2s ease, transform .2s ease;
            }

            .nav-avatar:hover {
                background: var(--bh-accent);
                transform: translateY(-1px);
            }

            .profile-dropdown-menu {
                position: absolute;
                top: calc(100% + 12px);
                right: 0;
                width: 230px;
                padding: 8px;
                background: var(--bh-white);
                border: 1px solid var(--bh-border);
                border-radius: 12px;
                box-shadow: 0 18px 40px rgba(0, 0, 0, .16);
                opacity: 0;
                visibility: hidden;
                transform: translateY(-8px);
                transition: opacity .2s ease, transform .2s ease, visibility .2s ease;
                z-index: 30;
            }

            .profile-dropdown-menu.show {
                opacity: 1;
                visibility: visible;
                transform: translateY(0);
            }

            .profile-dropdown-item {
                display: flex;
                align-items: center;
                gap: 10px;
                padding: 11px 12px;
                border-radius: 8px;
                color: var(--bh-text);
                font-size: .95rem;
                font-weight: 600;
            }

            .profile-dropdown-item i {
                width: 18px;
                color: var(--bh-green);
                text-align: center;
            }

            .profile-dropdown-item:hover {
                background: #f1f8f5;
                color: var(--bh-green);
            }

            .profile-dropdown-item.logout,
            .profile-dropdown-item.logout i {
                color: var(--bh-danger);
            }

            .profile-dropdown-item.logout:hover {
                background: var(--bh-danger-soft);
            }

            .profile-shell {
                width: min(1180px, calc(100% - 32px));
                margin: 0 auto;
                padding: 34px 0 56px;
            }

            .alert {
                margin-bottom: 18px;
                border-radius: 10px;
                border-width: 1px;
            }

            .profile-hero {
                position: relative;
                overflow: hidden;
                display: grid;
                grid-template-columns: minmax(0, 1fr) auto;
                gap: 28px;
                align-items: center;
                min-height: 256px;
                padding: 36px;
                background: var(--bh-white);
                border: 1px solid var(--bh-border);
                border-radius: 14px;
                box-shadow: 0 1px 2px rgba(0, 0, 0, .06), 0 18px 36px rgba(0, 0, 0, .08);
            }

            .profile-hero::before {
                content: "";
                position: absolute;
                inset: 0 0 auto 0;
                height: 8px;
                background: var(--bh-green);
            }

            .profile-main {
                display: grid;
                grid-template-columns: auto minmax(0, 1fr);
                gap: 24px;
                align-items: center;
                position: relative;
                z-index: 1;
            }

            .profile-avatar {
                width: 124px;
                height: 124px;
                background: var(--bh-green);
                color: var(--bh-white);
                font-size: 3.5rem;
                box-shadow: 0 12px 28px rgba(0, 98, 65, .18);
                border: 6px solid var(--bh-mint);
            }

            .eyebrow {
                margin-bottom: 8px;
                color: var(--bh-green);
                font-size: .82rem;
                font-weight: 800;
                letter-spacing: .08em;
                text-transform: uppercase;
            }

            .profile-name {
                margin-bottom: 8px;
                color: var(--bh-text);
                font-size: clamp(2rem, 4vw, 3rem);
                font-weight: 800;
                line-height: 1.1;
                letter-spacing: 0;
            }

            .profile-role {
                display: inline-flex;
                align-items: center;
                gap: 8px;
                padding: 8px 14px;
                border-radius: 999px;
                background: var(--bh-mint);
                color: var(--bh-house);
                font-weight: 800;
            }

            .profile-summary {
                margin-top: 16px;
                color: var(--bh-soft-text);
                font-size: 1rem;
            }

            .stat-panel {
                position: relative;
                z-index: 1;
                display: grid;
                grid-template-columns: repeat(2, minmax(130px, 1fr));
                gap: 12px;
                min-width: 300px;
            }

            .stat-card {
                padding: 20px;
                background: var(--bh-canvas);
                border: 1px solid var(--bh-border);
                border-radius: 12px;
            }

            .stat-number {
                display: block;
                margin-bottom: 4px;
                color: var(--bh-green);
                font-size: 2rem;
                font-weight: 800;
                line-height: 1;
            }

            .stat-label {
                color: var(--bh-muted);
                font-size: .92rem;
                font-weight: 700;
            }

            .content-grid {
                display: grid;
                grid-template-columns: minmax(0, 1fr) minmax(360px, .82fr);
                gap: 22px;
                margin-top: 24px;
            }

            .left-stack,
            .right-stack {
                display: grid;
                gap: 22px;
                align-content: start;
            }

            .profile-card {
                padding: 28px;
                background: var(--bh-white);
                border: 1px solid var(--bh-border);
                border-radius: 14px;
                box-shadow: 0 1px 2px rgba(0, 0, 0, .05), 0 12px 28px rgba(0, 0, 0, .06);
            }

            .card-title {
                display: flex;
                align-items: center;
                gap: 12px;
                margin-bottom: 22px;
                color: var(--bh-text);
                font-size: 1.3rem;
                font-weight: 800;
                line-height: 1.25;
            }

            .card-title i {
                color: var(--bh-green);
            }

            .info-list {
                display: grid;
                gap: 2px;
            }

            .info-item {
                display: grid;
                grid-template-columns: 46px minmax(0, 1fr);
                gap: 14px;
                align-items: center;
                padding: 16px 0;
                border-bottom: 1px solid var(--bh-ceramic);
            }

            .info-item:last-child {
                border-bottom: none;
            }

            .info-icon,
            .activity-icon {
                display: inline-flex;
                align-items: center;
                justify-content: center;
                width: 46px;
                height: 46px;
                border-radius: 12px;
                background: #f1f8f5;
                color: var(--bh-green);
                font-size: 1rem;
            }

            .info-label {
                margin-bottom: 3px;
                color: var(--bh-text);
                font-weight: 800;
            }

            .info-value {
                color: var(--bh-soft-text);
                overflow-wrap: anywhere;
            }

            .form-grid {
                display: grid;
                gap: 16px;
            }

            .form-group {
                display: grid;
                gap: 8px;
            }

            .form-label {
                color: var(--bh-text);
                font-weight: 800;
            }

            .form-control {
                min-height: 48px;
                padding: 12px 14px;
                border: 1px solid var(--bh-border);
                border-radius: 8px;
                background: var(--bh-white);
                color: var(--bh-text);
                font-size: 1rem;
                outline: none;
                transition: border-color .2s ease, box-shadow .2s ease, background .2s ease;
            }

            .form-control:focus {
                border-color: var(--bh-green);
                box-shadow: 0 0 0 3px rgba(0, 98, 65, .14);
            }

            .form-control:disabled {
                background: #f7f6f3;
                color: var(--bh-muted);
                cursor: not-allowed;
            }

            .help-text {
                color: var(--bh-muted);
                font-size: .88rem;
            }

            .action-row {
                display: flex;
                flex-wrap: wrap;
                gap: 10px;
                margin-top: 4px;
            }

            .btn {
                display: inline-flex;
                align-items: center;
                justify-content: center;
                gap: 8px;
                min-height: 44px;
                padding: 10px 18px;
                border-radius: 999px;
                font-weight: 800;
                text-decoration: none;
                transition: transform .2s ease, box-shadow .2s ease, background .2s ease, color .2s ease;
            }

            .btn-primary {
                border: 1px solid var(--bh-green);
                background: var(--bh-green);
                color: var(--bh-white);
            }

            .btn-primary:hover {
                background: var(--bh-accent);
                border-color: var(--bh-accent);
                color: var(--bh-white);
                box-shadow: 0 10px 20px rgba(0, 98, 65, .2);
                transform: translateY(-1px);
            }

            .btn-outline-primary {
                border: 1px solid var(--bh-green);
                background: transparent;
                color: var(--bh-green);
            }

            .btn-outline-primary:hover {
                background: #f1f8f5;
                color: var(--bh-green);
            }

            .btn-outline-danger {
                border: 1px solid var(--bh-danger);
                background: transparent;
                color: var(--bh-danger);
            }

            .btn-outline-danger:hover {
                background: var(--bh-danger-soft);
                color: var(--bh-danger);
            }

            .btn:disabled {
                opacity: .5;
                pointer-events: none;
            }

            .security-item,
            .activity-item {
                display: grid;
                grid-template-columns: 46px minmax(0, 1fr) auto;
                gap: 14px;
                align-items: center;
                padding: 16px 0;
                border-bottom: 1px solid var(--bh-ceramic);
            }

            .security-item:last-child,
            .activity-item:last-child {
                border-bottom: none;
            }

            .activity-item {
                grid-template-columns: 46px minmax(0, 1fr);
            }

            .activity-title {
                color: var(--bh-text);
                font-weight: 800;
            }

            .activity-time {
                margin-top: 3px;
                color: var(--bh-muted);
                font-size: .9rem;
            }

            @media (max-width: 980px) {
                .profile-hero,
                .content-grid {
                    grid-template-columns: 1fr;
                }

                .stat-panel {
                    min-width: 0;
                }
            }

            @media (max-width: 680px) {
                .topbar-inner,
                .profile-shell {
                    width: min(100% - 24px, 1180px);
                }

                .topbar-inner {
                    min-height: auto;
                    padding: 14px 0;
                    align-items: flex-start;
                }

                .brand span {
                    font-size: 1.08rem;
                }

                .home-link span {
                    display: none;
                }

                .profile-hero,
                .profile-card {
                    padding: 24px;
                }

                .profile-main {
                    grid-template-columns: 1fr;
                    text-align: center;
                    justify-items: center;
                }

                .profile-avatar {
                    width: 104px;
                    height: 104px;
                    font-size: 2.8rem;
                }

                .stat-panel {
                    grid-template-columns: 1fr;
                }

                .security-item {
                    grid-template-columns: 46px minmax(0, 1fr);
                }

                .security-item .btn {
                    grid-column: 1 / -1;
                    width: 100%;
                }

                .action-row .btn {
                    width: 100%;
                }
            }
        </style>
    </head>
    <body>
        <header class="topbar">
            <div class="topbar-inner">
                <a class="brand" href="${pageContext.request.contextPath}/homepage">
                    <span class="brand-mark"><i class="fas fa-users-cog"></i></span>
                    <span>BetterHR</span>
                </a>

                <nav class="topnav" aria-label="Điều hướng tài khoản">
                    <a class="home-link" href="${pageContext.request.contextPath}/homepage">
                        <i class="fas fa-home"></i>
                        <span>Trang chủ</span>
                    </a>
                    <div class="profile-dropdown">
                        <button type="button" class="nav-avatar" onclick="toggleProfileDropdown()" title="Menu tài khoản">
                            <i class="fas fa-user"></i>
                        </button>
                        <div class="profile-dropdown-menu" id="profileDropdown">
                            <a href="${pageContext.request.contextPath}/profilepage" class="profile-dropdown-item">
                                <i class="fas fa-user"></i>
                                Hồ sơ cá nhân
                            </a>
                            <a href="${pageContext.request.contextPath}/Admin/AdminHome.jsp" class="profile-dropdown-item">
                                <i class="fas fa-table-columns"></i>
                                Bảng điều khiển
                            </a>
                            <a href="${pageContext.request.contextPath}/Views/ChangePassword.jsp" class="profile-dropdown-item">
                                <i class="fas fa-key"></i>
                                Đổi mật khẩu
                            </a>
                            <a href="javascript:void(0)" onclick="handleLogout()" class="profile-dropdown-item logout">
                                <i class="fas fa-right-from-bracket"></i>
                                Đăng xuất
                            </a>
                        </div>
                    </div>
                </nav>
            </div>
        </header>

        <main class="profile-shell">
            <c:if test="${not empty successMessage}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="fas fa-check-circle me-2"></i>${successMessage}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="fas fa-exclamation-circle me-2"></i>${errorMessage}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <section class="profile-hero">
                <div class="profile-main">
                    <div class="profile-avatar">
                        <i class="fas fa-user"></i>
                    </div>
                    <div>
                        <div class="eyebrow">Hồ sơ nhân sự</div>
                        <h1 class="profile-name">${userProfile.fullName != null ? userProfile.fullName : 'Người dùng'}</h1>
                        <div class="profile-role">
                            <i class="fas fa-briefcase"></i>
                            <c:choose>
                                <c:when test="${userProfile.role == 'Admin'}">Quản trị viên</c:when>
                                <c:when test="${userProfile.role == 'HR Manager'}">Quản lý nhân sự</c:when>
                                <c:when test="${userProfile.role == 'HR Staff'}">Nhân viên nhân sự</c:when>
                                <c:when test="${userProfile.role == 'Employee'}">Nhân viên</c:when>
                                <c:when test="${userProfile.role == 'Department Manager'}">Quản lý phòng ban</c:when>
                                <c:when test="${not empty userProfile.role}">${userProfile.role}</c:when>
                                <c:otherwise>Nhân viên</c:otherwise>
                            </c:choose>
                        </div>
                        <p class="profile-summary">
                            Quản lý thông tin cá nhân, liên hệ và bảo mật tài khoản BetterHR của bạn.
                        </p>
                    </div>
                </div>

                <div class="stat-panel" aria-label="Thống kê tài khoản">
                    <div class="stat-card">
                        <span class="stat-number">${userStats.daysSinceJoin != null ? userStats.daysSinceJoin : '0'}</span>
                        <span class="stat-label">Ngày hoạt động</span>
                    </div>
                    <div class="stat-card">
                        <span class="stat-number">${userStats.loginCount != null ? userStats.loginCount : '0'}</span>
                        <span class="stat-label">Số lần đăng nhập</span>
                    </div>
                </div>
            </section>

            <div class="content-grid">
                <div class="left-stack">
                    <section class="profile-card">
                        <h2 class="card-title">
                            <i class="fas fa-address-card"></i>
                            Thông tin cá nhân
                        </h2>

                        <div class="info-list">
                            <div class="info-item">
                                <div class="info-icon"><i class="fas fa-envelope"></i></div>
                                <div>
                                    <div class="info-label">Địa chỉ email</div>
                                    <div class="info-value">${userProfile.email != null ? userProfile.email : 'Chưa cập nhật'}</div>
                                </div>
                            </div>

                            <div class="info-item">
                                <div class="info-icon"><i class="fas fa-phone"></i></div>
                                <div>
                                    <div class="info-label">Số điện thoại</div>
                                    <div class="info-value">${userProfile.phone != null ? userProfile.phone : 'Chưa cập nhật'}</div>
                                </div>
                            </div>

                            <div class="info-item">
                                <div class="info-icon"><i class="fas fa-at"></i></div>
                                <div>
                                    <div class="info-label">Tên đăng nhập</div>
                                    <div class="info-value">${userProfile.username != null ? userProfile.username : 'Chưa cập nhật'}</div>
                                </div>
                            </div>

                            <div class="info-item">
                                <div class="info-icon"><i class="fas fa-building"></i></div>
                                <div>
                                    <div class="info-label">Phòng ban</div>
                                    <div class="info-value">${userProfile.department != null ? userProfile.department : 'Chưa phân công'}</div>
                                </div>
                            </div>

                            <div class="info-item">
                                <div class="info-icon"><i class="fas fa-calendar-day"></i></div>
                                <div>
                                    <div class="info-label">Ngày tham gia</div>
                                    <div class="info-value">${userProfile.joinDate != null ? userProfile.joinDate : 'Chưa có dữ liệu'}</div>
                                </div>
                            </div>

                            <div class="info-item">
                                <div class="info-icon"><i class="fas fa-clock"></i></div>
                                <div>
                                    <div class="info-label">Lần đăng nhập gần nhất</div>
                                    <div class="info-value">${userProfile.lastLogin != null ? userProfile.lastLogin : 'Chưa có dữ liệu'}</div>
                                </div>
                            </div>
                        </div>
                    </section>

                    <section class="profile-card">
                        <h2 class="card-title">
                            <i class="fas fa-clock-rotate-left"></i>
                            Hoạt động gần đây
                        </h2>

                        <c:choose>
                            <c:when test="${not empty userActivities}">
                                <c:forEach var="activity" items="${userActivities}">
                                    <div class="activity-item">
                                        <div class="activity-icon">
                                            <c:choose>
                                                <c:when test="${activity.activity == 'LOGIN'}">
                                                    <i class="fas fa-right-to-bracket"></i>
                                                </c:when>
                                                <c:when test="${activity.activity == 'PROFILE_UPDATE'}">
                                                    <i class="fas fa-pen"></i>
                                                </c:when>
                                                <c:when test="${activity.activity == 'PASSWORD_CHANGE'}">
                                                    <i class="fas fa-key"></i>
                                                </c:when>
                                                <c:otherwise>
                                                    <i class="fas fa-circle-info"></i>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div>
                                            <div class="activity-title">${activity.description}</div>
                                            <div class="activity-time">${activity.activityDate}</div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <div class="activity-item">
                                    <div class="activity-icon"><i class="fas fa-circle-info"></i></div>
                                    <div>
                                        <div class="activity-title">Chưa có hoạt động gần đây</div>
                                        <div class="activity-time">Hoạt động của bạn sẽ hiển thị tại đây</div>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </section>
                </div>

                <div class="right-stack">
                    <section class="profile-card">
                        <h2 class="card-title">
                            <i class="fas fa-sliders"></i>
                            Cài đặt tài khoản
                        </h2>

                        <form id="profileForm" action="${pageContext.request.contextPath}/profilepage" method="POST">
                            <input type="hidden" name="action" value="update">

                            <div class="form-grid">
                                <div class="form-group">
                                    <label class="form-label" for="fullName">Họ và tên</label>
                                    <input type="text" class="form-control" id="fullName" name="fullName" value="${userProfile.fullName != null ? userProfile.fullName : ''}" disabled required>
                                </div>

                                <div class="form-group">
                                    <label class="form-label" for="email">Địa chỉ email</label>
                                    <input type="email" class="form-control" id="email" name="email" value="${userProfile.email != null ? userProfile.email : ''}" disabled required>
                                </div>

                                <div class="form-group">
                                    <label class="form-label" for="phone">Số điện thoại</label>
                                    <input type="tel" class="form-control" id="phone" name="phone" value="${userProfile.phone != null ? userProfile.phone : ''}" disabled>
                                </div>

                                <div class="form-group">
                                    <label class="form-label" for="department">Phòng ban</label>
                                    <select class="form-control" id="department" name="department" disabled>
                                        <option value="Information Technology" ${userProfile.department == 'Information Technology' ? 'selected' : ''}>Công nghệ thông tin</option>
                                        <option value="Human Resources" ${userProfile.department == 'Human Resources' ? 'selected' : ''}>Nhân sự</option>
                                        <option value="Finance" ${userProfile.department == 'Finance' ? 'selected' : ''}>Tài chính</option>
                                        <option value="Marketing" ${userProfile.department == 'Marketing' ? 'selected' : ''}>Tiếp thị</option>
                                        <option value="Operations" ${userProfile.department == 'Operations' ? 'selected' : ''}>Vận hành</option>
                                    </select>
                                    <span class="help-text">Phòng ban không thể thay đổi tại trang hồ sơ.</span>
                                </div>

                                <div class="action-row">
                                    <button type="submit" class="btn btn-primary" id="saveProfileBtn" disabled>
                                        <i class="fas fa-check"></i>
                                        Lưu thay đổi
                                    </button>
                                    <button type="button" class="btn btn-outline-primary" id="editProfileBtn" onclick="toggleEditMode()">
                                        <i class="fas fa-pen"></i>
                                        Chỉnh sửa
                                    </button>
                                </div>
                            </div>
                        </form>
                    </section>

                    <section class="profile-card">
                        <h2 class="card-title">
                            <i class="fas fa-shield-halved"></i>
                            Bảo mật
                        </h2>

                        <div class="security-item">
                            <div class="info-icon"><i class="fas fa-key"></i></div>
                            <div>
                                <div class="info-label">Mật khẩu</div>
                                <div class="info-value">Cập nhật mật khẩu định kỳ để bảo vệ tài khoản.</div>
                            </div>
                            <a href="${pageContext.request.contextPath}/Views/ChangePassword.jsp" class="btn btn-outline-primary btn-sm">
                                Đổi mật khẩu
                            </a>
                        </div>

                        <div class="security-item">
                            <div class="info-icon"><i class="fas fa-list-check"></i></div>
                            <div>
                                <div class="info-label">Lịch sử đăng nhập</div>
                                <div class="info-value">Theo dõi các hoạt động gần đây của tài khoản.</div>
                            </div>
                            <button type="button" class="btn btn-outline-primary btn-sm">
                                Xem
                            </button>
                        </div>
                    </section>
                </div>
            </div>
        </main>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            function toggleProfileDropdown() {
                const dropdown = document.getElementById('profileDropdown');
                if (dropdown) {
                    dropdown.classList.toggle('show');
                }
            }

            document.addEventListener('click', function(event) {
                const dropdown = document.getElementById('profileDropdown');
                const avatar = document.querySelector('.nav-avatar');

                if (dropdown && avatar && !avatar.contains(event.target) && !dropdown.contains(event.target)) {
                    dropdown.classList.remove('show');
                }
            });

            function handleLogout() {
                if (confirm('Bạn có chắc chắn muốn đăng xuất không?')) {
                    window.location.href = '${pageContext.request.contextPath}/LogoutController';
                }
            }

            function toggleEditMode() {
                const form = document.getElementById('profileForm');
                const inputs = form.querySelectorAll('input, select, textarea');
                const editBtn = document.getElementById('editProfileBtn');
                const saveBtn = document.getElementById('saveProfileBtn');
                const isEditMode = !document.getElementById('fullName').disabled;

                if (isEditMode) {
                    inputs.forEach(input => {
                        if (input.name !== 'action') {
                            input.disabled = true;
                        }
                    });
                    saveBtn.disabled = true;
                    editBtn.innerHTML = '<i class="fas fa-pen"></i>Chỉnh sửa';
                    editBtn.classList.remove('btn-outline-danger');
                    editBtn.classList.add('btn-outline-primary');
                } else {
                    inputs.forEach(input => {
                        if (input.name !== 'department' && input.name !== 'action') {
                            input.disabled = false;
                        }
                    });
                    saveBtn.disabled = false;
                    editBtn.innerHTML = '<i class="fas fa-xmark"></i>Hủy';
                    editBtn.classList.remove('btn-outline-primary');
                    editBtn.classList.add('btn-outline-danger');
                }
            }

            setTimeout(function() {
                document.querySelectorAll('.alert').forEach(alert => {
                    if (alert.classList.contains('show')) {
                        alert.classList.remove('show');
                        setTimeout(() => alert.remove(), 300);
                    }
                });
            }, 5000);
        </script>
    </body>
</html>
