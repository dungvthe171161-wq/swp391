<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
                <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;500;600;700;800&display=swap" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet"><title>Phân quyền</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/Admin_home.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/user-menu.css">
        <style>
            body {
                background: #f4f6fb;
            }
            .permission-wrapper {
                min-height: 100vh;
                display: flex;
                flex-direction: column;
            }
            .role-permission-header {
                padding: 24px 32px 18px;
                background: linear-gradient(135deg, #3578ff, #5a98ff);
                color: #fff;
                box-shadow: 0 8px 24px rgba(53, 131, 255, 0.25);
            }
            .role-permission-header h1 {
                margin: 0;
                font-size: 28px;
                font-weight: 600;
            }
            .role-permission-header p {
                margin: 10px 0 0;
                opacity: 0.9;
            }
            .matrix-wrapper {
                padding: 32px;
                flex: 1;
            }
            .matrix-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                gap: 16px;
                margin-bottom: 20px;
            }
            .back-link {
                padding: 10px 16px;
                border-radius: 999px;
                background: linear-gradient(135deg, rgba(79, 123, 255, 0.22), rgba(109, 144, 255, 0.38));
                color: #fff;
                font-weight: 600;
                text-decoration: none;
                display: inline-flex;
                align-items: center;
                gap: 6px;
                transition: background 0.2s ease, transform 0.2s ease;
                box-shadow: 0 6px 18px rgba(79, 123, 255, 0.25);
            }
            .back-link:hover {
                transform: translateX(-2px);
                background: linear-gradient(135deg, rgba(79, 123, 255, 0.38), rgba(109, 144, 255, 0.48));
            }
            .matrix-actions {
                display: flex;
                justify-content: flex-end;
                gap: 12px;
                margin-bottom: 18px;
            }
            .matrix-action-btn {
                padding: 10px 18px;
                border-radius: 12px;
                border: none;
                font-weight: 600;
                cursor: pointer;
                background: linear-gradient(135deg, #4f7bff, #6d90ff);
                color: #fff;
                box-shadow: 0 6px 16px rgba(79, 123, 255, 0.25);
                transition: transform 0.2s ease, box-shadow 0.2s ease;
            }
            .matrix-action-btn:hover {
                transform: translateY(-1px);
                box-shadow: 0 10px 20px rgba(79, 123, 255, 0.3);
            }
            .matrix-action-btn.danger {
                background: linear-gradient(135deg, #f46a6a, #f38a8a);
                box-shadow: 0 6px 16px rgba(244, 106, 106, 0.25);
            }
            .matrix-action-btn.danger:hover {
                box-shadow: 0 10px 20px rgba(244, 106, 106, 0.35);
            }
            .matrix-container {
                background: #fff;
                border-radius: 20px;
                box-shadow: 0 12px 34px rgba(44, 68, 148, 0.12);
                overflow: hidden;
            }
            .matrix-scroll {
                overflow-x: auto;
            }
            .permission-matrix {
                width: 100%;
                min-width: 720px;
                border-collapse: separate;
                border-spacing: 0;
            }
            .permission-matrix th,
            .permission-matrix td {
                padding: 16px 18px;
                font-size: 14px;
                border-bottom: 1px solid #e6ecff;
                background: #fff;
            }
            .permission-matrix thead th {
                background: linear-gradient(135deg, #4f7bff, #6c92ff);
                color: #fff;
                font-weight: 600;
                letter-spacing: 0.5px;
                text-transform: uppercase;
                font-size: 13px;
                position: sticky;
                top: 0;
                z-index: 2;
            }
            .permission-matrix .sticky-col {
                position: sticky;
                left: 0;
                z-index: 1;
                background: #fff;
                box-shadow: 6px 0 16px rgba(58, 82, 139, 0.08);
            }
            .permission-matrix thead .sticky-col {
                background: linear-gradient(135deg, #4f7bff, #6c92ff);
                box-shadow: 6px 0 18px rgba(25, 52, 128, 0.18);
            }
            .category-info {
                display: flex;
                flex-direction: column;
                gap: 6px;
            }
            .category-info-title {
                font-size: 15px;
                font-weight: 600;
                color: #273a70;
            }
            .category-info-desc {
                color: #64749b;
                font-size: 13px;
            }
            .category-info-meta {
                font-size: 12px;
                color: #8a97bc;
            }
            .category-actions {
                display: flex;
                gap: 8px;
                margin-top: 8px;
            }
            .category-btn {
                padding: 6px 10px;
                border-radius: 8px;
                border: none;
                font-size: 12px;
                font-weight: 600;
                cursor: pointer;
                background: #e6edff;
                color: #3a55a5;
                transition: background 0.2s ease, transform 0.2s ease;
            }
            .category-btn:hover {
                background: #d0dcff;
                transform: translateY(-1px);
            }
            .category-btn.danger {
                background: #fde4e4;
                color: #c03c3c;
            }
            .category-btn.danger:hover {
                background: #facdcd;
            }
            .matrix-role-header {
                text-align: center;
                font-size: 14px;
            }
            .matrix-cell {
                text-align: center;
                position: relative;
            }
            .matrix-cell.locked-role .matrix-toggle {
                cursor: not-allowed;
                opacity: 0.6;
            }
            .matrix-cell.locked-role .matrix-toggle:hover {
                transform: none;
                border-color: #d4dcff;
            }
            .matrix-cell.granted {
                background: rgba(52, 199, 89, 0.08);
            }
            .matrix-cell.pending {
                opacity: 0.55;
            }
            .matrix-toggle {
                display: inline-flex;
                align-items: center;
                justify-content: center;
                width: 38px;
                height: 38px;
                background: #f5f7ff;
                border-radius: 10px;
                border: 2px solid #d4dcff;
                transition: background 0.2s ease, border-color 0.2s ease, transform 0.2s ease;
                cursor: pointer;
            }
            .matrix-toggle:hover {
                transform: translateY(-1px);
                border-color: #b9c5ff;
            }
            .matrix-checkbox {
                display: none;
            }
            .matrix-slider {
                position: relative;
                width: 22px;
                height: 22px;
                border-radius: 6px;
                border: 2px solid #b7c2ef;
                background: #fff;
                display: inline-flex;
                align-items: center;
                justify-content: center;
                font-size: 14px;
                line-height: 1;
                color: #fff;
                transition: background 0.2s ease, border-color 0.2s ease, color 0.2s ease;
            }
            .matrix-slider::after {
                content: '✓';
                opacity: 0;
                transform: scale(0.6);
                transition: opacity 0.2s ease, transform 0.2s ease;
            }
            .matrix-cell.granted .matrix-toggle {
                background: rgba(52, 199, 89, 0.15);
                border-color: #34c759;
            }
            .matrix-cell.granted .matrix-slider {
                background: #34c759;
                border-color: #27a44c;
            }
            .matrix-cell.granted .matrix-slider::after {
                opacity: 1;
                transform: scale(1);
            }
            .empty-state {
                padding: 60px 32px;
                text-align: center;
                color: #607199;
            }
            .toast-container {
                position: fixed;
                top: 24px;
                right: 24px;
                display: flex;
                flex-direction: column;
                gap: 12px;
                z-index: 9999;
            }
            .toast {
                min-width: 320px;
                padding: 16px 20px;
                border-radius: 14px;
                background: #ffffff;
                color: #1f2d5a;
                box-shadow: 0 12px 30px rgba(37, 56, 118, 0.18);
                display: flex;
                align-items: center;
                justify-content: space-between;
                gap: 16px;
                font-size: 14px;
                font-weight: 600;
                animation: fade-in 0.25s ease;
                border: 1px solid rgba(37, 56, 118, 0.12);
            }
            .toast-message {
                color: inherit;
                white-space: normal;
                flex: 1;
            }
            .toast.success {
                border-color: rgba(33, 193, 122, 0.45);
                color: #1d6f4a;
                background: #ecfdf3;
            }
            .toast.error {
                border-color: rgba(240, 97, 97, 0.55);
                color: #aa2c2c;
                background: #fff1f0;
            }
            .toast.info {
                border-color: rgba(69, 102, 255, 0.45);
                color: #263a96;
                background: #eef3ff;
            }
            .toast.warning {
                border-color: rgba(255, 194, 58, 0.55);
                color: #8f6500;
                background: #fff7e6;
            }
            .toast button {
                background: transparent;
                border: none;
                color: inherit;
                cursor: pointer;
                font-size: 16px;
                opacity: 0.6;
                transition: opacity 0.2s ease;
                margin-left: 12px;
            }
            @keyframes fade-in {
                from { opacity: 0; transform: translateY(8px); }
                to { opacity: 1; transform: translateY(0); }
            }
            @media (max-width: 1100px) {
                .matrix-wrapper {
                    padding: 20px;
                }
                .matrix-container {
                    border-radius: 16px;
                }
            }
        </style>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/admin-shared-theme.css?v=20260618d">
            <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/admin-permissions.css?v=20260618c">
    </head>
    <body class="admin-page role-permission-page">
        <div class="dashboard-container permission-admin-shell">
            <aside class="sidebar">
                <div class="sidebar-header">
                    <div class="logo">
                        <img src="https://cdn-icons-png.flaticon.com/512/847/847969.png" alt="BetterHR" width="32">
                        <span>BetterHR</span>
                    </div>
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
                       class="nav-item active">
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

            <main class="main-content">
                <header class="top-bar permission-topbar">
                    <div class="search-box">
                        <span class="material-symbols-outlined search-icon">search</span>
                        <input class="search-input" type="text" placeholder="Tìm kiếm...">
                    </div>
                    <div class="top-bar-actions">
                        <button class="icon-button" type="button" aria-label="Thông báo">
                            <span class="material-symbols-outlined">notifications</span>
                        </button>
                        <a class="icon-button active" href="${pageContext.request.contextPath}/admin?action=role-permissions" aria-label="Phân quyền">
                            <span class="material-symbols-outlined">settings</span>
                        </a>
                        <a class="icon-button" href="${pageContext.request.contextPath}/admin?action=profile" aria-label="Trợ giúp">
                            <span class="material-symbols-outlined">help</span>
                        </a>
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

                <section class="permission-wrapper">
                    <c:choose>
                        <c:when test="${empty roles}">
                            <div class="empty-state">
                                <span class="material-symbols-outlined">admin_panel_settings</span>
                                <h2>Chưa có vai trò</h2>
                                <p>Vui lòng tạo vai trò trước khi cấu hình phân quyền.</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="matrix-wrapper">
                                <div class="permission-hero">
                                    <div class="permission-hero-copy">
                                        <span class="permission-eyebrow">Quản trị</span>
                                        <h1>Quản lý phân quyền</h1>
                                        <p>Cấu hình nhóm chức năng cho từng vai trò trong hệ thống BetterHR.</p>
                                    </div>
                                    <div class="matrix-actions">
                                        <a href="${pageContext.request.contextPath}/admin?action=dashboard" class="matrix-action-btn secondary back-link">
                                            <span class="material-symbols-outlined">arrow_back</span>
                                            Về Admin
                                        </a>
                                        <button type="button" class="matrix-action-btn" id="saveStateBtn">Lưu trạng thái</button>
                                        <button type="button" class="matrix-action-btn" id="grantAllBtn">Bật tất cả</button>
                                        <button type="button" class="matrix-action-btn danger" id="revokeAllBtn">Tắt tất cả</button>
                                    </div>
                                </div>

                                <div class="matrix-container">
                                    <div class="matrix-scroll">
                                        <table class="permission-matrix">
                                            <thead>
                                                <tr>
                                                    <th class="sticky-col">
                                                        <div class="category-info matrix-head-info">
                                                            <span class="category-info-title">Nhóm chức năng</span>
                                                            <span class="category-info-desc">Phân quyền theo từng vai trò</span>
                                                        </div>
                                                    </th>
                                                    <c:forEach var="role" items="${roles}">
                                                        <c:set var="normalizedRoleName" value="${fn:toUpperCase(role.roleName)}"/>
                                                        <c:set var="displayRoleName" value="${role.roleName}"/>
                                                        <c:if test="${normalizedRoleName eq 'HR MANAGER'}">
                                                            <c:set var="displayRoleName" value="HR"/>
                                                        </c:if>
                                                        <c:if test="${fn:toLowerCase(role.roleName) ne 'admin'}">
                                                            <th class="matrix-role-header"
                                                                data-role-id="${role.roleId}"
                                                                data-permissions="${fn:escapeXml(rolePermissionCsv[role.roleId])}">
                                                                ${displayRoleName}
                                                            </th>
                                                        </c:if>
                                                    </c:forEach>
                                                    <th class="row-actions-header">Thao tác</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="entry" items="${groupedPermissions}">
                                                    <c:set var="permissionIds" value=""/>
                                                    <c:set var="categoryDesc" value=""/>
                                                    <c:set var="totalPermissions" value="0"/>
                                                    <c:forEach var="permission" items="${entry.value}" varStatus="status">
                                                        <c:set var="permissionIds"
                                                               value="${permissionIds}${status.index > 0 ? ',' : ''}${permission.permissionId}"/>
                                                        <c:if test="${status.first}">
                                                            <c:set var="categoryDesc"
                                                                   value="${empty permission.description ? '' : permission.description}"/>
                                                        </c:if>
                                                        <c:set var="totalPermissions" value="${status.count}"/>
                                                    </c:forEach>
                                                    <c:if test="${empty categoryDesc}">
                                                        <c:set var="categoryDesc" value="Feature ${entry.key}"/>
                                                    </c:if>
                                                    <c:if test="${entry.key ne 'Department' and entry.key ne 'Role' and entry.key ne 'System' and entry.key ne 'User'}">
                                                        <tr class="category-row" data-category="${entry.key}" data-permission-ids="${permissionIds}">
                                                            <td class="category-info sticky-col">
                                                                <span class="category-info-title">${entry.key}</span>
                                                                <span class="category-info-desc">${categoryDesc}</span>
                                                                <span class="category-info-meta">${totalPermissions} quyền chi tiết</span>
                                                            </td>
                                                            <c:forEach var="role" items="${roles}">
                                                                <c:set var="isAdminRole" value="${fn:toLowerCase(role.roleName) eq 'admin'}"/>
                                                                <c:if test="${not isAdminRole}">
                                                                    <td class="matrix-cell">
                                                                        <label class="matrix-toggle" aria-label="Vai trò ${role.roleName} - ${entry.key}">
                                                                            <input type="checkbox"
                                                                                   class="matrix-checkbox"
                                                                                   data-role-id="${role.roleId}"
                                                                                   data-permission-ids="${permissionIds}">
                                                                            <span class="matrix-slider"></span>
                                                                        </label>
                                                                    </td>
                                                                </c:if>
                                                            </c:forEach>
                                                            <td class="row-actions-cell">
                                                                <div class="category-actions">
                                                                    <button type="button"
                                                                            class="category-btn apply-all"
                                                                            data-category="${entry.key}"
                                                                            data-permission-ids="${permissionIds}">
                                                                        Bật
                                                                    </button>
                                                                    <button type="button"
                                                                            class="category-btn danger remove-all"
                                                                            data-category="${entry.key}"
                                                                            data-permission-ids="${permissionIds}">
                                                                        Tắt
                                                                    </button>
                                                                </div>
                                                            </td>
                                                        </tr>
                                                    </c:if>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </section>
            </main>
        </div>

        <div class="toast-container" id="toastContainer"></div>

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

            (function () {
                const contextPath = '${pageContext.request.contextPath}';
                const toastContainer = document.getElementById('toastContainer');
                const roleHeaders = Array.from(document.querySelectorAll('.matrix-role-header'));
                const checkboxes = Array.from(document.querySelectorAll('.matrix-checkbox'));
                const categoryRows = Array.from(document.querySelectorAll('.category-row'));
                const categoryApplyButtons = Array.from(document.querySelectorAll('.category-btn.apply-all'));
                const categoryRemoveButtons = Array.from(document.querySelectorAll('.category-btn.remove-all'));
                const saveStateBtn = document.getElementById('saveStateBtn');
                const grantAllBtn = document.getElementById('grantAllBtn');
                const revokeAllBtn = document.getElementById('revokeAllBtn');
                const roleAssignments = new Map();
                let lockChange = false;

                function parseIds(value) {
                    if (!value) {
                        return [];
                    }
                    return value.split(',')
                            .map(part => parseInt(part.trim(), 10))
                            .filter(id => !Number.isNaN(id) && id > 0);
                }

                roleHeaders.forEach(header => {
                    const roleId = parseInt(header.dataset.roleId, 10);
                    const assignedIds = new Set(parseIds(header.dataset.permissions));
                    roleAssignments.set(roleId, assignedIds);
                });

                function updateRoleHeader(roleId) {
                    roleHeaders.forEach(header => {
                        const headerRoleId = parseInt(header.dataset.roleId, 10);
                        if (headerRoleId === roleId) {
                            const assigned = roleAssignments.get(roleId) || new Set();
                            const csv = Array.from(assigned)
                                    .sort((a, b) => a - b)
                                    .join(',');
                            header.dataset.permissions = csv;
                        }
                    });
                }

                function showToast(message, type = 'info') {
                const displayMessage = (message && String(message).trim().length > 0)
                        ? message
                        : (type === 'error' ? 'Có lỗi xảy ra. Vui lòng thử lại.' : 'Thao tác đã hoàn tất.');
                    if (!toastContainer) {
                        return;
                    }
                const toast = document.createElement('div');
                toast.className = `toast ${type}`;

                const messageEl = document.createElement('span');
                messageEl.className = 'toast-message';
                messageEl.textContent = displayMessage;

                const closeBtn = document.createElement('button');
                closeBtn.type = 'button';
                closeBtn.setAttribute('aria-label', 'Đóng thông báo');
                closeBtn.innerHTML = '&times;';
                closeBtn.addEventListener('click', () => toast.remove());

                toast.appendChild(messageEl);
                toast.appendChild(closeBtn);
                toastContainer.appendChild(toast);
                    setTimeout(() => toast.remove(), 4000);
                }

                function syncMatrix() {
                    lockChange = true;
                    checkboxes.forEach(cb => {
                        const roleId = parseInt(cb.dataset.roleId, 10);
                        const ids = parseIds(cb.dataset.permissionIds);
                        const assigned = roleAssignments.get(roleId) || new Set();
                        const granted = ids.length > 0 && ids.every(id => assigned.has(id));
                        cb.checked = granted;
                        const cell = cb.closest('.matrix-cell');
                        if (cell) {
                            cell.classList.toggle('granted', granted);
                            cell.classList.remove('pending');
                        }
                    });
                    lockChange = false;
                }

                async function updateMatrix(roleId, permissionIds, granted, checkbox, options = {}) {
                    const { silentSuccess = false } = options;
                    const cell = checkbox.closest('.matrix-cell');
                    if (cell) {
                        cell.classList.add('pending');
                    }
                    try {
                        const response = await fetch(`${contextPath}/admin/role-permissions/api`, {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json',
                                'Accept': 'application/json'
                            },
                            body: JSON.stringify({ roleId, permissionIds, granted })
                        });
                        const data = await response.json();
                        if (!response.ok || data.status !== 'success') {
                            throw new Error(data.message || 'Cập nhật thất bại.');
                        }
                        let set = roleAssignments.get(roleId);
                        if (!set) {
                            set = new Set();
                            roleAssignments.set(roleId, set);
                        }
                        permissionIds.forEach(id => {
                            if (granted) {
                                set.add(id);
                            } else {
                                set.delete(id);
                            }
                        });
                        updateRoleHeader(roleId);
                        syncMatrix();
                        if (!silentSuccess) {
                            showToast(data.message || 'Cập nhật thành công.', 'success');
                        }
                        return true;
                    } catch (error) {
                        checkbox.checked = !granted;
                            showToast(error.message || 'Cập nhật phân quyền thất bại.', 'error');
                        return false;
                    } finally {
                        if (cell) {
                            cell.classList.remove('pending');
                        }
                    }
                }

                async function toggleRowForAllRoles(permissionIdsAttr, granted, options = {}) {
                    const { silent = false, sourceButton } = options;
                    const ids = parseIds(permissionIdsAttr);
                    if (ids.length === 0) {
                        if (!silent) {
                            showToast('Không tìm thấy quyền chi tiết cho nhóm này.', 'error');
                        }
                        return { updated: 0, total: 0 };
                    }
                    const relatedCheckboxes = checkboxes.filter(cb => cb.dataset.permissionIds === permissionIdsAttr);
                    let updatedCount = 0;
                    if (sourceButton) {
                        sourceButton.disabled = true;
                    }
                    try {
                        for (const cb of relatedCheckboxes) {
                            if (cb.disabled) {
                                continue;
                            }
                            const roleId = parseInt(cb.dataset.roleId, 10);
                            if (!roleId) {
                                continue;
                            }
                            if (cb.checked === granted) {
                                const cell = cb.closest('.matrix-cell');
                                if (cell) {
                                    cell.classList.toggle('granted', granted);
                                }
                                continue;
                            }
                            cb.checked = granted;
                            const success = await updateMatrix(roleId, ids, granted, cb, { silentSuccess: true });
                            if (success) {
                                updatedCount++;
                            }
                        }
                    } finally {
                        if (sourceButton) {
                            sourceButton.disabled = false;
                        }
                        syncMatrix();
                    }

                    if (!silent) {
                        if (updatedCount === 0) {
                            showToast(granted ? 'Tất cả vai trò đã được bật.' : 'Tất cả vai trò đã được tắt.', 'info');
                        } else {
                        showToast(granted ? 'Đã bật nhóm này cho tất cả vai trò.' : 'Đã tắt nhóm này cho tất cả vai trò.', granted ? 'success' : 'warning');
                        }
                    }

                    return { updated: updatedCount, total: relatedCheckboxes.length };
                }

                async function toggleAllRows(granted) {
                    let totalUpdated = 0;
                    for (const row of categoryRows) {
                        const permissionIdsAttr = row.dataset.permissionIds || '';
                        const result = await toggleRowForAllRoles(permissionIdsAttr, granted, { silent: true });
                        totalUpdated += result.updated;
                    }
                    syncMatrix();
                    if (totalUpdated === 0) {
                        showToast(granted ? 'Tất cả quyền đã được bật.' : 'Tất cả quyền đã được tắt.', 'info');
                    } else {
                        showToast(granted ? 'Đã bật toàn bộ quyền cho tất cả vai trò.' : 'Đã tắt toàn bộ quyền cho tất cả vai trò.', granted ? 'success' : 'warning');
                    }
                }

                if (saveStateBtn) {
                    saveStateBtn.addEventListener('click', () => {
                        const totalGranted = Array.from(roleAssignments.values())
                                .reduce((sum, set) => sum + set.size, 0);
                        showToast(`Đã lưu trạng thái hiện tại (${totalGranted} quyền đang bật).`, 'success');
                    });
                }

                categoryApplyButtons.forEach(btn => {
                    btn.addEventListener('click', async () => {
                        if (lockChange) {
                            showToast('Đang xử lý thao tác khác. Vui lòng chờ.', 'info');
                            return;
                        }
                        await toggleRowForAllRoles(btn.dataset.permissionIds || '', true, { sourceButton: btn });
                    });
                });

                categoryRemoveButtons.forEach(btn => {
                    btn.addEventListener('click', async () => {
                        if (lockChange) {
                            showToast('Đang xử lý thao tác khác. Vui lòng chờ.', 'info');
                            return;
                        }
                        await toggleRowForAllRoles(btn.dataset.permissionIds || '', false, { sourceButton: btn });
                    });
                });

                if (grantAllBtn) {
                    grantAllBtn.addEventListener('click', async () => {
                        if (lockChange) {
                            showToast('Đang xử lý thao tác khác. Vui lòng chờ.', 'info');
                            return;
                        }
                        grantAllBtn.disabled = true;
                        if (revokeAllBtn) {
                            revokeAllBtn.disabled = true;
                        }
                        try {
                            await toggleAllRows(true);
                        } finally {
                            grantAllBtn.disabled = false;
                            if (revokeAllBtn) {
                                revokeAllBtn.disabled = false;
                            }
                        }
                    });
                }

                if (revokeAllBtn) {
                    revokeAllBtn.addEventListener('click', async () => {
                        if (lockChange) {
                            showToast('Đang xử lý thao tác khác. Vui lòng chờ.', 'info');
                            return;
                        }
                        revokeAllBtn.disabled = true;
                        if (grantAllBtn) {
                            grantAllBtn.disabled = true;
                        }
                        try {
                            await toggleAllRows(false);
                        } finally {
                            revokeAllBtn.disabled = false;
                            if (grantAllBtn) {
                                grantAllBtn.disabled = false;
                            }
                        }
                    });
                }

                checkboxes.forEach(cb => {
                    if (cb.disabled) {
                        return;
                    }
                    cb.addEventListener('change', () => {
                        if (lockChange) {
                            return;
                        }
                        const roleId = parseInt(cb.dataset.roleId, 10);
                        const permissionIds = parseIds(cb.dataset.permissionIds);
                        if (!roleId || permissionIds.length === 0) {
                            showToast('Không xác định được quyền cần cập nhật.', 'error');
                            cb.checked = !cb.checked;
                            return;
                        }
                        updateMatrix(roleId, permissionIds, cb.checked, cb, { silentSuccess: true });
                    });
                });

                syncMatrix();
            })();
        </script>
    </body>
</html>
