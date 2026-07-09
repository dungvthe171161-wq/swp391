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
        <title>Quản lý phòng ban - HRMS</title>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/Admin_home.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/unified-layout.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/department.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/department-filters.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/pagination.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/user-menu.css">

        <style>
            /* Filter styles */
            .filter-group {
                display: flex;
                align-items: center;
                gap: 10px;
                margin-right: 15px;
            }

            .filter-select {
                padding: 6px 12px;
                border: 1px solid #ddd;
                border-radius: 4px;
                background-color: white;
                font-size: 13px;
                min-width: 140px;
                cursor: pointer;
            }

            .filter-select:hover {
                border-color: #5b6ef5;
            }

            /* User menu styles */
            .user-menu {
                position: relative;
                display: flex;
                align-items: center;
                gap: 8px;
                padding: 4px 8px;
                border-radius: 4px;
                cursor: pointer;
                transition: background-color 0.2s;
            }

            .user-menu:hover {
                background-color: rgba(0, 0, 0, 0.05);
            }

            .user-menu img {
                width: 32px;
                height: 32px;
                border-radius: 50%;
                object-fit: cover;
            }

            .username {
                font-size: 14px;
                font-weight: 500;
                color: #333;
            }

            .user-dropdown {
                position: absolute;
                top: 100%;
                right: 0;
                width: 200px;
                background: white;
                border-radius: 4px;
                box-shadow: 0 2px 8px rgba(0,0,0,0.15);
                padding: 8px 0;
                display: none;
                z-index: 1000;
                margin-top: 8px;
            }

            .user-menu:hover .user-dropdown {
                display: block;
            }

            .user-dropdown a {
                display: flex;
                align-items: center;
                padding: 8px 16px;
                color: #333;
                text-decoration: none;
                transition: background-color 0.2s;
            }

            .user-dropdown a:hover {
                background-color: #f5f5f5;
            }

            .user-dropdown a i {
                margin-right: 8px;
                width: 16px;
                text-align: center;
            }

            .user-dropdown a.logout {
                color: #dc3545;
                border-top: 1px solid #eee;
                margin-top: 4px;
                padding-top: 8px;
            }

            .user-dropdown a.logout:hover {
                background-color: #ffebee;
            }

            #toast-container {
                position: fixed;
                top: 20px;
                right: 20px;
                z-index: 9999;
            }

            .toast {
                display: flex;
                align-items: center;
                min-width: 260px;
                padding: 14px 18px;
                margin-bottom: 10px;
                border-radius: 8px;
                color: white;
                font-size: 14px;
                font-weight: 500;
                box-shadow: 0 4px 10px rgba(0,0,0,0.2);
                opacity: 0;
                transform: translateX(100%);
                animation: slideIn 0.4s forwards, fadeOut 0.5s 2.8s forwards;
            }

            .toast-success {
                background-color: #4CAF50;
            }
            .toast-error {
                background-color: #f44336;
            }

            @keyframes slideIn {
                from {
                    opacity: 0;
                    transform: translateX(100%);
                }
                to {
                    opacity: 1;
                    transform: translateX(0);
                }
            }

            @keyframes fadeOut {
                from {
                    opacity: 1;
                    transform: translateX(0);
                }
                to {
                    opacity: 0;
                    transform: translateX(100%);
                }
            }

            .top-bar .search-form {
                display: flex;
                gap: 8px;
                align-items: center;
                flex: 1;
                max-width: 500px;
                margin-right: 20px;
            }

            .top-bar .search-form input {
                flex: 1;
                min-width: 200px;
                padding: 6px 10px;
                border: 1px solid #ddd;
                border-radius: 4px;
                font-size: 13px;
                height: 32px;
            }

            .top-bar .search-form button,
            .top-bar .search-form a {
                padding: 6px 12px;
                background-color: #5b6ef5;
                color: white;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                text-decoration: none;
                display: inline-block;
                font-size: 13px;
                height: 32px;
                line-height: 1;
                white-space: nowrap;
            }

            .top-bar .search-form button:hover {
                background-color: #4a5dd8;
            }

            .top-bar .search-form a {
                background-color: #6c757d;
            }

            .top-bar .search-form a:hover {
                background-color: #5a6268;
            }

            .top-bar {
                display: flex;
                align-items: center;
                justify-content: space-between;
                padding: 12px 20px;
                min-height: 60px;
            }

            .top-bar-actions {
                display: flex;
                align-items: center;
                gap: 15px;
            }


            .top-bar select {
                padding: 6px 10px;
                border: 1px solid #ddd;
                border-radius: 4px;
                font-size: 13px;
                height: 32px;
                background-color: white;
                cursor: pointer;
            }

            .top-bar select:hover {
                border-color: #5b6ef5;
            }

            .user-menu {
                display: flex;
                align-items: center;
                gap: 8px;
                cursor: pointer;
                position: relative;
            }

            .user-menu img {
                width: 32px;
                height: 32px;
                border-radius: 50%;
            }

            .user-menu span {
                font-size: 14px;
                font-weight: 500;
            }

            .user-dropdown {
                position: absolute;
                top: 100%;
                right: 0;
                background-color: white;
                border: 1px solid #ddd;
                border-radius: 4px;
                box-shadow: 0 2px 8px rgba(0,0,0,0.1);
                min-width: 150px;
                z-index: 1000;
                display: none;
                margin-top: 8px;
            }

            .user-dropdown a {
                display: block;
                padding: 10px 16px;
                color: #333;
                text-decoration: none;
                font-size: 14px;
                border-bottom: 1px solid #f0f0f0;
                transition: background-color 0.2s;
            }

            .user-dropdown a:last-child {
                border-bottom: none;
            }

            .user-dropdown a:hover {
                background-color: #f5f5f5;
            }

            .user-dropdown a.logout {
                color: #f44336;
            }

            .user-dropdown a.logout:hover {
                background-color: #ffebee;
            }

            .sort-header {
                cursor: pointer;
                user-select: none;
                display: flex;
                align-items: center;
                gap: 6px;
                padding: 4px 0;
                transition: color 0.2s;
            }

            .sort-header:hover {
                color: #5b6ef5;
            }

            .sort-icon {
                display: inline-block;
                width: 12px;
                height: 12px;
                font-size: 10px;
                opacity: 0.5;
            }

            .sort-header.active .sort-icon {
                opacity: 1;
                color: #5b6ef5;
            }

            .sort-header .sort-icon.asc::before {
                content: "▼";
            }

            .sort-header .sort-icon.desc::before {
                content: "▼";
            }

            /* Cập nhật bộ lọc để đồng bộ với trang vai trò */
            .filter-controls {
                display: flex;
                justify-content: flex-start;
                align-items: flex-end;
                margin-bottom: 20px;
                gap: 20px;
                flex-wrap: wrap;
                padding: 15px;
                background-color: #f8f9fa;
                border: 1px solid #e9ecef;
                border-radius: 6px;
            }
            .filter-group {
                display: flex;
                flex-direction: column;
                align-items: flex-start;
                gap: 5px;
            }
            .filter-group input[type="text"],
            .filter-group select {
                padding: 8px 12px;
                border: 1px solid #ddd;
                border-radius: 4px;
                font-size: 14px;
                min-width: 150px;
                box-sizing: border-box;
                height: 38px;
            }
            .filter-buttons {
                align-self: flex-end;
                display: flex;
                gap: 10px;
            }
            .btn-primary, .btn-secondary {
                padding: 8px 15px;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                font-size: 14px;
                line-height: 1.2;
                height: 38px;
                box-sizing: border-box;
                white-space: nowrap;
            }
            .btn-primary {
                background-color: #007bff;
                color: white;
            }
            .btn-secondary {
                background-color: #6c757d;
                color: white;
            }
            .departments-table th {
                cursor: pointer;
                user-select: none;
            }
            .sort-arrow {
                margin-left: 5px;
                font-size: 0.8em;
                vertical-align: middle;
            }
            .alert {
                padding: 10px;
                margin-bottom: 15px;
                border-radius: 4px;
                font-weight: bold;
            }
            .alert.error {
                background-color: #f8d7da;
                color: #721c24;
                border: 1px solid #f5c6cb;
            }
            .alert.success {
                background-color: #d4edda;
                color: #155724;
                border: 1px solid #c3e6cb;
            }
            .modal {
                display: none;
                position: fixed;
                z-index: 1000;
                left: 0;
                top: 0;
                width: 100%;
                height: 100%;
                overflow: auto;
                background-color: rgba(0,0,0,0.4);
                justify-content: center;
                align-items: center;
            }
            .modal-content {
                background-color: #fefefe;
                padding: 20px;
                border: 1px solid #888;
                width: 90%;
                max-width: 500px;
                border-radius: 8px;
            }
            .modal-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                border-bottom: 1px solid #eee;
                padding-bottom: 10px;
                margin-bottom: 20px;
            }
            .close-btn {
                color: #aaa;
                font-size: 28px;
                font-weight: bold;
                cursor: pointer;
                background: none;
                border: none;
            }
            .form-group label {
                display: block;
                margin-bottom: 5px;
                font-weight: bold;
            }
            .form-group input[type="text"],
            .form-group select {
                width: 100%;
                padding: 10px;
                border: 1px solid #ccc;
                border-radius: 4px;
                box-sizing: border-box;
                margin-bottom: 10px;
            }
            .form-actions {
                margin-top: 20px;
                text-align: right;
            }
            .btn-cancel {
                background-color: #f4f4f4;
                color: #333;
                padding: 10px 15px;
                border: 1px solid #ddd;
                border-radius: 4px;
                cursor: pointer;
                margin-right: 10px;
            }
            .btn-submit {
                background-color: #28a745;
                color: white;
                padding: 10px 15px;
                border: none;
                border-radius: 4px;
                cursor: pointer;
            }
            .btn-danger {
                background-color: #dc3545;
                color: white;
                padding: 10px 15px;
                border: none;
                border-radius: 4px;
                cursor: pointer;
            }

        </style>

        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script>
        const contextPath = '${pageContext.request.contextPath}';
        </script>
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

            <main class="main-content">
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

                <!-- Main Content -->
                <section class="dashboard-content">
                    <div class="page-header">
                        <h1 class="page-title">Quản lý phòng ban</h1>
                        <button class="btn-primary" onclick="showDepartmentModal()">+ Thêm phòng ban mới</button>
                    </div>

                    <c:if test="${not empty errorMessage}">
                        <div class="alert error">${errorMessage}</div>
                    </c:if>

                    <c:if test="${not empty successMessage}">
                        <div class="alert success">${successMessage}</div>
                    </c:if>

                    <div class="filter-section">
                        <div class="filter-controls">
                            <div class="filter-group">
                                <label for="searchQueryInput">Tìm kiếm</label>
                                <input type="text" id="searchQueryInput" name="search"
                                       placeholder="Tên phòng ban hoặc ID..."
                                       value="${searchKeyword}" class="filter-input">
                            </div>
                            <div class="filter-buttons">
                                <button type="button" onclick="applyFilters()" class="btn-primary">Áp dụng lọc</button>
                                <button type="button" onclick="clearAllFilters()" class="btn-secondary">Xóa tất cả</button>
                            </div>
                        </div>
                    </div>


                    <div class="table-section">
                        <table class="departments-table">
                            <thead>
                                <tr>
                                    <th onclick="sortTable('DepartmentID')">Mã phòng ban
                                        <c:if test="${sortBy == 'DepartmentID' || sortBy == ''}">
                                            <span class="sort-arrow">${sortOrder == 'ASC' ? '▲' : '▼'}</span>
                                        </c:if>
                                    </th>
                                    <th onclick="sortTable('DeptName')">Tên phòng ban
                                        <c:if test="${sortBy == 'DeptName'}">
                                            <span class="sort-arrow">${sortOrder == 'ASC' ? '▲' : '▼'}</span>
                                        </c:if>
                                    </th>
                                    <th>Quản lý</th>
                                    <th onclick="sortTable('EmployeeCount')">Số nhân viên
                                        <c:if test="${sortBy == 'EmployeeCount'}">
                                            <span class="sort-arrow">${sortOrder == 'ASC' ? '▲' : '▼'}</span>
                                        </c:if>
                                    </th>
                                    <th>Trạng thái</th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody id="departmentsTableBody">
                                <c:choose>
                                    <c:when test="${not empty departmentList}">
                                        <c:forEach var="dept" items="${departmentList}">
                                            <tr>
                                                <td>${dept.departmentId}</td>
                                                <td>${dept.deptName}</td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${not empty dept.deptManagerId}">
                                                            Mã quản lý: ${dept.deptManagerId}
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span style="color: #999;">Chưa phân công</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>${dept.employeeCount != null ? dept.employeeCount : 0}</td>
                                                <td><span style="background: #d4edda; padding: 4px 8px; border-radius: 4px;">Hoạt động</span></td>
                                                <td>
                                                    <button class="btn-action edit" onclick="showDepartmentModal(${dept.departmentId}, '${dept.deptName}', ${dept.deptManagerId})">
                                                        <i class="fa fa-pencil"></i> Sửa
                                                    </button>
                                                    <button class="btn-action delete" onclick="deleteDepartment(${dept.departmentId}, '${dept.deptName}')">
                                                        <i class="fa fa-trash"></i> Xóa
                                                    </button>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td colspan="6" style="text-align: center;">Không tìm thấy phòng ban phù hợp với điều kiện lọc.</td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>

                    <div class="pagination-bar">
                        <div class="pagination-info">
                            <c:set var="total" value="${total != null ? total : 0}" />
                            <c:set var="page" value="${page != null ? page : 1}" />
                            <c:set var="pageSize" value="${pageSize != null ? pageSize : 10}" />
                            <c:set var="totalPages" value="${totalPages != null ? totalPages : 1}" />

                            <c:set var="start" value="${total > 0 ? (page - 1) * pageSize + 1 : 0}" />
                            <c:set var="end" value="${page * pageSize}" />
                            <c:if test="${end > total}">
                                <c:set var="end" value="${total}" />
                            </c:if>
                            <span>Hiển thị ${start} - ${end} / ${total}</span>

                            <div class="page-size-selector">
                                <label for="pageSizeSelect">Số dòng mỗi trang:</label>
                                <select id="pageSizeSelect" onchange="changePageSize(this.value)">
                                    <option value="5" <c:if test="${pageSize == 5}">selected</c:if>>5</option>
                                    <option value="10" <c:if test="${pageSize == 10}">selected</c:if>>10</option>
                                    <option value="20" <c:if test="${pageSize == 20}">selected</c:if>>20</option>
                                    <option value="50" <c:if test="${pageSize == 50}">selected</c:if>>50</option>
                                    </select>
                                </div>
                            </div>

                            <div class="pagination-controls">
                            <c:url var="baseUrl" value="/admin?action=departments">
                                <c:param name="pageSize" value="${pageSize}" />
                                <c:param name="search" value="${searchKeyword}" />
                                <c:param name="sortBy" value="${sortBy}" />
                                <c:param name="sortOrder" value="${sortOrder}" />
                            </c:url>

                            <c:choose>
                                <c:when test="${page > 1}">
                                    <c:url var="prevUrl" value="${baseUrl}">
                                        <c:param name="page" value="${page - 1}" />
                                    </c:url>
                                    <a href="${prevUrl}" class="btn-pagination">&larr; Trước</a>
                                </c:when>
                                <c:otherwise>
                                    <span class="disabled">&larr; Trước</span>
                                </c:otherwise>
                            </c:choose>

                            <c:set var="range" value="2" />
                            <c:set var="start_page" value="${page - range > 1 ? page - range : 1}" />
                            <c:set var="end_page" value="${page + range < totalPages ? page + range : totalPages}" />

                            <c:if test="${start_page > 1}">
                                <c:url var="firstPageUrl" value="${baseUrl}"><c:param name="page" value="1" /></c:url>
                                <a href="${firstPageUrl}" class="btn-pagination">1</a>
                                <c:if test="${start_page > 2}">
                                    <span class="ellipsis">...</span>
                                </c:if>
                            </c:if>

                            <c:forEach begin="${start_page}" end="${end_page}" var="i">
                                <c:choose>
                                    <c:when test="${i == page}">
                                        <span class="active btn-pagination">${i}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <c:url var="pageUrl" value="${baseUrl}"><c:param name="page" value="${i}" /></c:url>
                                        <a href="${pageUrl}" class="btn-pagination">${i}</a>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>

                            <c:if test="${end_page < totalPages}">
                                <c:if test="${end_page < totalPages - 1}">
                                    <span class="ellipsis">...</span>
                                </c:if>
                                <c:url var="lastPageUrl" value="${baseUrl}"><c:param name="page" value="${totalPages}" /></c:url>
                                <a href="${lastPageUrl}" class="btn-pagination">${totalPages}</a>
                            </c:if>

                            <c:choose>
                                <c:when test="${page < totalPages}">
                                    <c:url var="nextUrl" value="${baseUrl}">
                                        <c:param name="page" value="${page + 1}" />
                                    </c:url>
                                    <a href="${nextUrl}" class="btn-pagination">Sau &rarr;</a>
                                </c:when>
                                <c:otherwise>
                                    <span class="disabled">Sau &rarr;</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </section>
            </main>
        </div>

        <!-- Modal thêm/sửa phòng ban -->
        <div id="departmentModal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h2 id="modalTitle">Thêm phòng ban mới</h2>
                    <button class="close-btn" onclick="closeDepartmentModal()">&times;</button>
                </div>
                <form id="departmentForm">
                    <input type="hidden" id="departmentId" name="departmentId">
                    <div class="form-group">
                        <label for="departmentName">Tên phòng ban *</label>
                        <input type="text" id="departmentName" name="departmentName" required placeholder="VD: HR, Tài chính, IT">
                    </div>

                    <div class="form-group">
                        <label for="departmentManager">Quản lý</label>
                        <select id="departmentManager" name="departmentManager">
                            <option value="">-- Chọn quản lý --</option>
                            <c:forEach var="manager" items="${managers}">
                                <option value="${manager.employeeId}">${manager.fullName}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-actions">
                        <button type="button" class="btn-cancel" onclick="closeDepartmentModal()">Hủy</button>
                        <button type="submit" class="btn-submit">Lưu phòng ban</button>
                    </div>
                </form>
            </div>
        </div>

        <!-- Modal xóa phòng ban -->
        <div id="deleteModal" class="modal">
            <div class="modal-content" style="max-width: 400px;">
                <div class="modal-header">
                    <h2>Xác nhận xóa</h2>
                    <button class="close-btn" onclick="closeDeleteModal()">&times;</button>
                </div>
                <p>Bạn có chắc muốn xóa phòng ban: <strong id="deleteDepartmentName"></strong> (ID: <strong id="deleteDepartmentId"></strong>)?</p>
                <form id="deleteForm">
                    <input type="hidden" id="confirmDeleteDepartmentId" name="departmentId">
                    <div class="form-actions">
                        <button type="button" class="btn-cancel" onclick="closeDeleteModal()">Hủy</button>
                        <button type="submit" class="btn-submit" style="background-color: #dc3545;">Xóa</button>
                    </div>
                </form>
            </div>
        </div>

        <div id="toast-container"></div>

        <script>
        const DEPARTMENT_LIST_URL = '${pageContext.request.contextPath}/admin?action=departments';
        const DEPARTMENT_API_URL = '${pageContext.request.contextPath}/admin/department';

        function applyFilters() {
            const urlParams = new URLSearchParams(window.location.search);
            const search = document.getElementById('searchQueryInput').value.trim();
            const pageSizeSelect = document.getElementById('pageSizeSelect');
            const currentPageSize = pageSizeSelect ? pageSizeSelect.value : '10';
            const currentSortBy = '${sortBy}';
            const currentSortOrder = '${sortOrder}';

            urlParams.set('page', '1');
            urlParams.set('pageSize', currentPageSize);

            if (search !== '') {
                urlParams.set('search', search);
            } else {
                urlParams.delete('search');
            }

            if (currentSortBy && currentSortOrder) {
                urlParams.set('sortBy', currentSortBy);
                urlParams.set('sortOrder', currentSortOrder);
            }

            window.location.href = DEPARTMENT_LIST_URL + '&' + urlParams.toString();
        }

        function clearAllFilters() {
            window.location.href = DEPARTMENT_LIST_URL;
        }

        function changePageSize(newSize) {
            const urlParams = new URLSearchParams(window.location.search);
            urlParams.set('pageSize', newSize);
            urlParams.set('page', '1');

            if (urlParams.get('search') === '')
                urlParams.delete('search');

            window.location.href = DEPARTMENT_LIST_URL + '&' + urlParams.toString();
        }

        function sortTable(column) {
            const urlParams = new URLSearchParams(window.location.search);
            const currentSortBy = urlParams.get('sortBy') || 'DepartmentID';
            const currentSortOrder = urlParams.get('sortOrder') || 'ASC';
            let newSortOrder = 'ASC';

            if (currentSortBy === column) {
                newSortOrder = (currentSortOrder === 'ASC') ? 'DESC' : 'ASC';
            }

            urlParams.set('page', '1');
            urlParams.set('sortBy', column);
            urlParams.set('sortOrder', newSortOrder);

            if (urlParams.get('search') === '')
                urlParams.delete('search');

            window.location.href = DEPARTMENT_LIST_URL + '&' + urlParams.toString();
        }

        function showDepartmentModal(departmentId = null, departmentName = '', departmentManager = '') {
            const modal = document.getElementById('departmentModal');
            document.getElementById('departmentId').value = departmentId || '';
            document.getElementById('departmentName').value = departmentName;
            document.getElementById('departmentManager').value = departmentManager || '';
            document.getElementById('modalTitle').textContent = departmentId ? 'Sửa phòng ban' : 'Thêm phòng ban mới';
            modal.style.display = 'flex';
        }

        function closeDepartmentModal() {
            document.getElementById('departmentModal').style.display = 'none';
        }

        function deleteDepartment(departmentId, departmentName) {
            document.getElementById('deleteDepartmentId').textContent = departmentId;
            document.getElementById('deleteDepartmentName').textContent = departmentName;
            document.getElementById('confirmDeleteDepartmentId').value = departmentId;
            document.getElementById('deleteModal').style.display = 'flex';
        }

        function closeDeleteModal() {
            document.getElementById('deleteModal').style.display = 'none';
        }

        window.onclick = function (event) {
            const departmentModal = document.getElementById('departmentModal');
            const deleteModal = document.getElementById('deleteModal');

            if (event.target === departmentModal) {
                departmentModal.style.display = 'none';
            }
            if (event.target === deleteModal) {
                deleteModal.style.display = 'none';
            }
        };

        document.getElementById('departmentForm').addEventListener('submit', function (e) {
            e.preventDefault();

            const departmentId = document.getElementById('departmentId').value;
            const departmentName = document.getElementById('departmentName').value;
            const departmentManager = document.getElementById('departmentManager').value;

            if (departmentName.trim() === '') {
                alert('Vui lòng nhập tên phòng ban.');
                return;
            }

            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '${pageContext.request.contextPath}/departments';

            // Add action parameter
            const actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = 'department-save';
            form.appendChild(actionInput);

            const idInput = document.createElement('input');
            idInput.type = 'hidden';
            idInput.name = 'deptId';
            idInput.value = departmentId;
            form.appendChild(idInput);

            const nameInput = document.createElement('input');
            nameInput.type = 'hidden';
            nameInput.name = 'deptName';
            nameInput.value = departmentName.trim();
            form.appendChild(nameInput);

            const managerInput = document.createElement('input');
            managerInput.type = 'hidden';
            managerInput.name = 'deptManagerId';
            managerInput.value = departmentManager;
            form.appendChild(managerInput);

            document.body.appendChild(form);
            form.submit();
        });

        document.getElementById('deleteForm').addEventListener('submit', function (e) {
            e.preventDefault();

            const departmentId = document.getElementById('confirmDeleteDepartmentId').value;

            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '${pageContext.request.contextPath}/departments';

            const actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = 'department-delete';
            form.appendChild(actionInput);

            const idInput = document.createElement('input');
            idInput.type = 'hidden';
            idInput.name = 'deptId';
            idInput.value = departmentId;
            form.appendChild(idInput);

            document.body.appendChild(form);
            form.submit();
        });

        function showToast(message, type = "success") {
            const container = document.getElementById("toast-container");
            const toast = document.createElement("div");
            toast.className = "toast " + (type === "success" ? "toast-success" : "toast-error");
            toast.innerHTML = message;
            container.appendChild(toast);
            setTimeout(() => toast.remove(), 3500);
        }
        </script>
        <script>
            // JSTL session messages
            <c:if test="${not empty successMessage}">
            showToast("Thành công: ${successMessage}", "success");
            </c:if>
            <c:if test="${not empty errorMessage}">
            showToast("Lỗi: ${errorMessage}", "error");
            </c:if>
        </script>

        <script>
            // User menu toggle function
            function toggleUserMenu() {
                const userMenu = document.querySelector('.user-menu');
                userMenu.classList.toggle('active');
            }

            // Close user menu when clicking outside
            document.addEventListener('click', function (event) {
                if (!event.target.closest('.user-menu')) {
                    const userMenu = document.querySelector('.user-menu');
                    if (userMenu && userMenu.classList.contains('active')) {
                        userMenu.classList.remove('active');
                    }
                }
            });
        </script>
    </body>
</html>
