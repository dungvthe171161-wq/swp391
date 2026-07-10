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
        <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet"><title>Quản lý vai trò - HRMS</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/Admin_home.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/unified-layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/roles.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/pagination.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/user-menu.css">

    <style>
        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }
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
        .roles-table th {
            cursor: pointer;
            user-select: none;
        }
        .sort-arrow {
            margin-left: 5px;
            font-size: 0.8em;
            vertical-align: middle;
        }

        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0; top: 0;
            width: 100%; height: 100%;
            overflow: auto;
            background-color: rgba(0,0,0,0.4);
            justify-content: center;
            align-items: center;
        }
        .modal-content {
            background-color: #fefefe;
            margin: auto;
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
        .form-group input[type="text"] {
            width: 100%;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
            box-sizing: border-box;
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
    </style>

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script>
        const contextPath = '${pageContext.request.contextPath}';
    </script>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/admin-shared-theme.css?v=20260618d">
    </head>
<body class="admin-page">
    <div class="dashboard-container">
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

            <section class="dashboard-content">
                <div class="page-header">
                    <h1 class="page-title">Quản lý vai trò</h1>
                    <button class="btn-primary" onclick="showRoleModal()">+ Thêm vai trò mới</button>
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
                                   placeholder="Tên vai trò hoặc ID..."
                                   value="${searchQuery}" class="filter-input">
                        </div>
                        <div class="filter-buttons">
                            <button type="button" onclick="applyFilters()" class="btn-primary">Áp dụng lọc</button>
                            <button type="button" onclick="clearAllFilters()" class="btn-secondary">Xóa tất cả</button>
                        </div>
                    </div>
                </div>
                <div class="table-section">
                    <table class="roles-table">
                        <thead>
                            <tr>
                                <th onclick="sortTable('RoleID')">Mã vai trò
                                    <c:if test="${sortBy == 'RoleID' || sortBy == ''}">
                                        <span class="sort-arrow">${sortOrder == 'ASC' ? '▲' : '▼'}</span>
                                    </c:if>
                                </th>
                                <th onclick="sortTable('RoleName')">Tên vai trò
                                    <c:if test="${sortBy == 'RoleName'}">
                                        <span class="sort-arrow">${sortOrder == 'ASC' ? '▲' : '▼'}</span>
                                    </c:if>
                                </th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody id="rolesTableBody">
                            <c:choose>
                                <c:when test="${not empty roles}">
                                    <c:forEach var="role" items="${roles}">
                                        <tr>
                                            <td>${role.roleId}</td>
                                            <td>${fn:escapeXml(role.roleName)}</td>
                                            <td>
                                                <button class="btn-action edit" onclick="showRoleModal(${role.roleId}, '${fn:escapeXml(role.roleName)}')">
                                                    <i class="fa fa-pencil"></i> Sửa
                                                </button>
                                                <button class="btn-action delete" onclick="deleteRole(${role.roleId}, '${fn:escapeXml(role.roleName)}')">
                                                    <i class="fa fa-trash"></i> Xóa
                                                </button>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="3" style="text-align: center;">Không tìm thấy vai trò phù hợp với điều kiện lọc.</td>
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

                        <%-- Tính toán hiển thị --%>
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

                        <%-- Base URL trỏ về RoleServlet endpoint LIST --%>
                        <c:url var="baseUrl" value="/admin/role/list">
                            <c:param name="pageSize" value="${pageSize}" />
                            <c:param name="search" value="${searchQuery}" />
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

    <div id="roleModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h2 id="modalTitle">Thêm vai trò mới</h2>
                <button class="close-btn" onclick="closeRoleModal()">&times;</button>
            </div>
            <form id="roleForm">
                <input type="hidden" id="roleId" name="roleId">
                <div class="form-group">
                    <label for="roleName">Tên vai trò *</label>
                    <input type="text" id="roleName" name="roleName" required placeholder="Ví dụ: Quản lý, HR, Tài chính">
                </div>

                <div class="form-actions">
                    <button type="button" class="btn-cancel" onclick="closeRoleModal()">Hủy</button>
                    <button type="submit" class="btn-submit">Lưu vai trò</button>
                </div>
            </form>
        </div>
    </div>

    <div id="deleteModal" class="modal">
        <div class="modal-content" style="max-width: 400px;">
            <div class="modal-header">
                <h2>Xác nhận xóa</h2>
                <button class="close-btn" onclick="closeDeleteModal()">&times;</button>
            </div>
            <p>Bạn có chắc muốn xóa vai trò: <strong id="deleteRoleName"></strong> (ID: <strong id="deleteRoleId"></strong>)?</p>
            <form id="deleteForm">
                <input type="hidden" id="confirmDeleteRoleId" name="roleId">
                <div class="form-actions">
                    <button type="button" class="btn-cancel" onclick="closeDeleteModal()">Hủy</button>
                    <button type="submit" class="btn-submit" style="background-color: #dc3545;">Xóa</button>
                </div>
            </form>
        </div>
    </div>

    <script>
        const ROLE_LIST_URL = '${pageContext.request.contextPath}/admin/role/list';
        const ROLE_API_URL = '${pageContext.request.contextPath}/admin/role';

        // Allow Enter key to trigger search from filter input
        document.addEventListener('DOMContentLoaded', function() {
            const filterSearchInput = document.getElementById('searchQueryInput');

            if (filterSearchInput) {
                filterSearchInput.addEventListener('keypress', function(e) {
                    if (e.key === 'Enter') {
                        e.preventDefault();
                        applyFilters();
                    }
                });
            }
        });

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

            window.location.href = ROLE_LIST_URL + '?' + urlParams.toString();
        }

        function clearAllFilters() {
            window.location.href = ROLE_LIST_URL;
        }

        function changePageSize(newSize) {
            const urlParams = new URLSearchParams(window.location.search);

            urlParams.set('pageSize', newSize);
            urlParams.set('page', '1');

            if (urlParams.get('search') === '') urlParams.delete('search');

            window.location.href = ROLE_LIST_URL + '?' + urlParams.toString();
        }

        function sortTable(column) {
            const urlParams = new URLSearchParams(window.location.search);
            const currentSortBy = urlParams.get('sortBy') || 'RoleID';
            const currentSortOrder = urlParams.get('sortOrder') || 'ASC';
            let newSortOrder = 'ASC';

            if (currentSortBy === column) {
                newSortOrder = (currentSortOrder === 'ASC') ? 'DESC' : 'ASC';
            }

            urlParams.set('page', '1');
            urlParams.set('sortBy', column);
            urlParams.set('sortOrder', newSortOrder);

            if (urlParams.get('search') === '') urlParams.delete('search');

            window.location.href = ROLE_LIST_URL + '?' + urlParams.toString();
        }

        function showRoleModal(roleId = null, roleName = '') {
            const modal = document.getElementById('roleModal');
            document.getElementById('roleId').value = roleId || '';
            document.getElementById('roleName').value = roleName;
            document.getElementById('modalTitle').textContent = roleId ? 'Sửa vai trò' : 'Thêm vai trò mới';
            modal.style.display = 'flex';
        }

        function closeRoleModal() {
            document.getElementById('roleModal').style.display = 'none';
        }

        function deleteRole(roleId, roleName) {
            document.getElementById('deleteRoleId').textContent = roleId;
            document.getElementById('deleteRoleName').textContent = roleName;
            document.getElementById('confirmDeleteRoleId').value = roleId;
            document.getElementById('deleteModal').style.display = 'flex';
        }

        function closeDeleteModal() {
            document.getElementById('deleteModal').style.display = 'none';
        }

        window.onclick = function (event) {
            const roleModal = document.getElementById('roleModal');
            const deleteModal = document.getElementById('deleteModal');

            if (event.target === roleModal) {
                roleModal.style.display = 'none';
            }
            if (event.target === deleteModal) {
                deleteModal.style.display = 'none';
            }
        };


        document.getElementById('roleForm').addEventListener('submit', function(e) {
            e.preventDefault();

            const roleId = document.getElementById('roleId').value;
            const roleName = document.getElementById('roleName').value;

            if (roleName.trim() === "") {
                alert('Vui lòng nhập tên vai trò.');
                return;
            }

            const isEdit = roleId !== '';
            const method = isEdit ? 'PUT' : 'POST';
            const url = isEdit ? (ROLE_API_URL + '/' + roleId) : ROLE_API_URL;
            console.log('Đang gửi fetch:', method, url);

            fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ roleName: roleName })
            })
            .then(response => {
                console.log('Đã nhận phản hồi từ server. Trạng thái:', response.status);

                if (!response.ok) {
                    console.log('Response không OK (lỗi 4xx/5xx). Đang đọc JSON lỗi...');

                    return response.json().then(err => {
                        console.log('Đã đọc JSON lỗi:', err);
                        throw new Error(err.message || response.statusText);
                    });
                }

                console.log('Response OK (2xx). Đang đọc JSON thành công...');
                return response.json();
            })
            .then(data => {
                console.log('Fetch thành công, data:', data);
                closeRoleModal();

                window.location.href = ROLE_LIST_URL + window.location.search;
            })
            .catch(error => {
                console.error('--- Đã vào khối catch ---');
                console.error('Thông báo lỗi:', error.message);
                alert('Lỗi: ' + error.message);
            });

        });

        document.getElementById('deleteForm').addEventListener('submit', function(e) {
            e.preventDefault();

            const roleId = document.getElementById('confirmDeleteRoleId').value;
            const url = ROLE_API_URL + '/' + roleId;

            fetch(url, {
                method: 'DELETE'
            })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => { throw new Error(err.message || response.statusText); });
                }
                return response.json();
            })
            .then(data => {
                closeDeleteModal();
                alert(data.message || 'Đã xóa vai trò thành công!');
                window.location.href = ROLE_LIST_URL + window.location.search;
            })
            .catch(error => {
                alert('Lỗi: ' + error.message);
            });
        });

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
