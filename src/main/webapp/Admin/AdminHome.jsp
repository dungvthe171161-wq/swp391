<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>BetterHR - Bảng điều khiển quản trị</title>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;500;600;700;800&display=swap" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/admin-dashboard-redesign.css?v=20260618c">
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    </head>
    <body class="admin-dashboard-page">
        <div class="dashboard-container betterhr-admin-shell">
            <aside class="sidebar betterhr-sidebar">
                <div class="sidebar-header">
                    <a class="brand-lockup" href="${pageContext.request.contextPath}/admin?action=dashboard">
                        <span class="brand-mark material-symbols-outlined">admin_panel_settings</span>
                        <span>
                            <strong>BetterHR</strong>
                            <small>Cổng quản trị</small>
                        </span>
                    </a>
                </div>

                <nav class="sidebar-nav" aria-label="Điều hướng quản trị">
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
                </nav>
            </aside>

            <main class="main-content betterhr-main">
                <header class="top-bar betterhr-topbar">
                    <div class="search-box">
                        <span class="material-symbols-outlined search-icon">search</span>
                        <input class="search-input" type="text" placeholder="Tìm kiếm...">
                    </div>

                    <div class="top-bar-actions">
                        <button class="icon-button notification-btn" type="button" aria-label="Thông báo">
                            <span class="material-symbols-outlined">notifications</span>
                        </button>
                        <a class="icon-button" href="${pageContext.request.contextPath}/admin?action=profile" aria-label="Cài đặt">
                            <span class="material-symbols-outlined">settings</span>
                        </a>

                        <div class="user-menu" onclick="toggleUserMenu()">
                            <div class="user-info">
                                <div class="admin-user-copy">
                                    <span>Quản trị viên</span>
                                    <small>Quản trị viên</small>
                                </div>
                                <img src="${pageContext.request.contextPath}/Admin/images/admin-user-avatar.png" alt="Quản trị viên">
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

                <section class="dashboard-content betterhr-dashboard-content">
                    <div class="dashboard-heading">
                        <div>
                            <p class="eyebrow">Quản trị</p>
                            <h1 class="page-title">Tổng quan hệ thống</h1>
                            <p class="page-subtitle">Dữ liệu quản trị được cập nhật hôm nay.</p>
                        </div>
                        <a class="export-button" href="${pageContext.request.contextPath}/admin?action=audit-log">
                            <span class="material-symbols-outlined">download</span>
                            Xuất báo cáo
                        </a>
                    </div>

                    <div class="stats-grid betterhr-stats-grid">
                        <article class="stat-card">
                            <div class="stat-card-top">
                                <div class="stat-icon green"><span class="material-symbols-outlined">groups</span></div>
                                <span class="stat-change up">+2.5%</span>
                            </div>
                            <div class="stat-info">
                                <h3>Tổng nhân viên</h3>
                                <p class="stat-value">${totalEmployees}</p>
                            </div>
                        </article>

                        <article class="stat-card">
                            <div class="stat-card-top">
                                <div class="stat-icon mint"><span class="material-symbols-outlined">how_to_reg</span></div>
                                <span class="stat-change up">+1.2%</span>
                            </div>
                            <div class="stat-info">
                                <h3>Nhân viên đang làm việc</h3>
                                <p class="stat-value">${activeEmployees}</p>
                            </div>
                        </article>

                        <article class="stat-card">
                            <div class="stat-card-top">
                                <div class="stat-icon gold"><span class="material-symbols-outlined">domain</span></div>
                                <span class="stat-change neutral">0%</span>
                            </div>
                            <div class="stat-info">
                                <h3>Phòng ban</h3>
                                <p class="stat-value">${totalDepartments}</p>
                            </div>
                        </article>

                        <article class="stat-card">
                            <div class="stat-card-top">
                                <div class="stat-icon slate"><span class="material-symbols-outlined">manage_accounts</span></div>
                                <span class="stat-change up">+5%</span>
                            </div>
                            <div class="stat-info">
                                <h3>Người dùng hệ thống</h3>
                                <p class="stat-value">${totalUser}</p>
                            </div>
                        </article>
                    </div>

                    <div class="charts-section betterhr-charts">
                        <article class="chart-card chart-card-wide">
                            <div class="chart-header">
                                <div>
                                    <h3>Phân bổ nhân viên theo phòng ban</h3>
                                    <div class="chart-info">Theo phòng ban</div>
                                </div>
                                <button class="chart-menu-button" type="button" aria-label="Tùy chọn biểu đồ">
                                    <span class="material-symbols-outlined">more_vert</span>
                                </button>
                            </div>
                            <div class="chart-canvas-wrap">
                                <canvas id="employeeChart" height="190"></canvas>
                            </div>
                        </article>

                        <article class="chart-card status-chart-card">
                            <div class="chart-header">
                                <div>
                                    <h3>Trạng thái nhân viên</h3>
                                    <div class="chart-info">Chính thức và thử việc</div>
                                </div>
                            </div>
                            <div class="chart-canvas-wrap donut-wrap">
                                <canvas id="statusChart" height="190"></canvas>
                            </div>
                        </article>

                        <article class="chart-card chart-card-full">
                            <div class="chart-header">
                                <div>
                                    <h3>Hoạt động hệ thống</h3>
                                    <div class="chart-info">7 ngày qua</div>
                                </div>
                                <select id="activityChartRange" class="chart-range-selector">
                                    <option value="7">7 ngày qua</option>
                                    <option value="14">14 ngày qua</option>
                                    <option value="30">30 ngày qua</option>
                                </select>
                            </div>
                            <div class="chart-container">
                                <canvas id="activityChart" height="190"></canvas>
                            </div>
                            <div class="chart-summary">
                                <div class="summary-item">
                                    <span class="summary-label">Tổng hoạt động</span>
                                    <span id="totalActivities" class="summary-value">0</span>
                                </div>
                                <div class="summary-item">
                                    <span class="summary-label">Trung bình mỗi ngày</span>
                                    <span id="avgActivities" class="summary-value">0</span>
                                </div>
                                <div class="summary-item">
                                    <span class="summary-label">Ngày cao nhất</span>
                                    <span id="peakDay" class="summary-value">-</span>
                                </div>
                            </div>
                        </article>
                    </div>

                    <div class="activity-section betterhr-activity-grid">
                        <article class="activity-card">
                            <div class="section-card-header">
                                <h3>Hoạt động gần đây</h3>
                                <span class="section-chip">Audit</span>
                            </div>
                            <div class="activity-list">
                                <c:choose>
                                    <c:when test="${not empty recentActivity}">
                                        <c:forEach var="activity" items="${recentActivity}">
                                            <div class="activity-item">
                                                <div class="activity-icon success">
                                                    <span class="material-symbols-outlined">history</span>
                                                </div>
                                                <div class="activity-details">
                                                    <span class="activity-time">${activity.timestamp.month} ${activity.timestamp.dayOfMonth}, ${String.format('%02d', activity.timestamp.hour)}:${String.format('%02d', activity.timestamp.minute)}</span>
                                                    <span class="activity-text">${activity.action} - ${activity.objectType}: ${activity.newValue}</span>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="activity-item">
                                            <div class="activity-icon info">
                                                <span class="material-symbols-outlined">inbox</span>
                                            </div>
                                            <span class="activity-text">Chưa có hoạt động gần đây</span>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </article>

                        <article class="referrals-card">
                            <div class="section-card-header">
                                <h3>Thao tác nhanh</h3>
                                <span class="section-chip">Admin</span>
                            </div>
                            <div class="referrals-list">
                                <a href="${pageContext.request.contextPath}/admin?action=departments" class="referral-item">
                                    <span class="referral-name">Quản lý phòng ban</span>
                                    <span class="material-symbols-outlined referral-count">arrow_forward</span>
                                </a>
                                <a href="${pageContext.request.contextPath}/admin/users" class="referral-item">
                                    <span class="referral-name">Quản lý người dùng</span>
                                    <span class="material-symbols-outlined referral-count">arrow_forward</span>
                                </a>
                                <a href="${pageContext.request.contextPath}/admin?action=role-permissions" class="referral-item">
                                    <span class="referral-name">Phân quyền</span>
                                    <span class="material-symbols-outlined referral-count">arrow_forward</span>
                                </a>
                                <a href="${pageContext.request.contextPath}/admin?action=audit-log" class="referral-item">
                                    <span class="referral-name">Xem nhật ký hệ thống</span>
                                    <span class="material-symbols-outlined referral-count">arrow_forward</span>
                                </a>
                            </div>
                        </article>
                    </div>
                </section>

                <footer class="betterhr-footer">
                    <p>© 2024 Hệ thống BetterHR. Mọi quyền được bảo lưu.</p>
                    <div>
                        <a href="${pageContext.request.contextPath}/homepage">Trang chủ</a>
                        <a href="${pageContext.request.contextPath}/admin?action=audit-log">Trạng thái hệ thống</a>
                    </div>
                </footer>
            </main>
        </div>

        <script>
            function toggleUserMenu() {
                const userMenu = document.querySelector('.user-menu');
                if (userMenu) {
                    userMenu.classList.toggle('active');
                }
            }

            document.addEventListener('click', function (event) {
                if (!event.target.closest('.user-menu')) {
                    const userMenu = document.querySelector('.user-menu');
                    if (userMenu && userMenu.classList.contains('active')) {
                        userMenu.classList.remove('active');
                    }
                }
            });

            window.contextPath = '${pageContext.request.contextPath}';
            window.adminBaseUrl = '${pageContext.request.contextPath}/admin';
            window.dashboardData = {
                employeeDistribution: <c:out value="${employeeDistributionJson}" escapeXml="false" />,
                employeeStatus: <c:out value="${employeeStatusJson}" escapeXml="false" />,
                activityData: <c:out value="${activityDataJson}" escapeXml="false" />
            };
        </script>

        <script src="${pageContext.request.contextPath}/Admin/js/dashboard.js?v=20260618c"></script>
    </body>
</html>
