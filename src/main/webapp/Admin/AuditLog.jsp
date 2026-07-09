<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
                <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;500;600;700;800&display=swap" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet"><title>Nhật ký hệ thống - HRMS</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/Admin_home.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/unified-layout.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/user-menu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/pagination.css">

<style>
            .log-table {
                width: 100%;
                border-collapse: collapse;
                margin-top: 20px;
                background-color: #fff;
                box-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
            }
            .log-table th, .log-table td {
                padding: 12px 15px;
                text-align: left;
                border-bottom: 1px solid #ddd;
                vertical-align: top;
            }
            .log-table th {
                background-color: #f4f7f6;
                color: #333;
                font-weight: 600;
                cursor: pointer;
            }
            .log-table tr:hover {
                background-color: #f9f9f9;
            }
            .log-container {
                padding: 20px;
                background-color: #fff;
                border-radius: 8px;
            }
            .log-detail-btn {
                background: none;
                border: none;
                color: #007bff;
                cursor: pointer;
                padding: 0;
                font-size: 14px;
            }

            /* Style cho modal */
            .modal {
                display: none;
                position: fixed;
                z-index: 1000;
                left: 0; top: 0;
                width: 100%; height: 100%;
                overflow: auto;
                background-color: rgba(0,0,0,0.4);
            }
            .modal-content {
                background-color: #fefefe;
                margin: 10% auto;
                padding: 20px;
                border: 1px solid #888;
                width: 80%;
                max-width: 800px;
                border-radius: 8px;
            }
            .modal-content pre {
                background-color: #eee;
                padding: 10px;
                border: 1px solid #ddd;
                max-height: 200px;
                overflow-y: auto;
                white-space: pre-wrap;
                word-break: break-all;
            }
            .close {
                color: #aaa;
                float: right;
                font-size: 28px;
                font-weight: bold;
                cursor: pointer;
            }

            .filter-controls {
                display: flex;
                justify-content: flex-start;
                align-items: flex-end;
                margin-bottom: 20px;
                gap: 20px;
                flex-wrap: wrap;
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
                box-sizing: border-box; /* [cite: 36] */
                height: 38px; /* Thiết lập chiều cao cố định để căn chỉnh tốt hơn */
            }

            .filter-buttons {
                align-self: flex-end; /* [cite: 38] */
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
            }

            .btn-primary {
                background-color: #007bff;
                color: white;
            }
            .btn-secondary {
                background-color: #6c757d;
                color: white;
            }

            .log-table th {
                cursor: pointer;
                user-select: none;
            }
            .sort-arrow {
                margin-left: 5px;
                font-size: 0.8em;
                vertical-align: middle;
            }

            .pagination-info {
                display: flex;
                align-items: center;
                gap: 15px;
                font-size: 14px;
                color: #666;
            }
            .page-size-selector {
                display: flex;
                align-items: center;
                gap: 8px;
            }
            .page-size-selector select {
                padding: 6px 10px;
                border: 1px solid #ddd;
                border-radius: 4px;
                font-size: 13px;
                background-color: white;
                cursor: pointer;
            }
        </style>
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
                        <h1 class="page-title">Nhật ký hệ thống</h1>
                    </div>

                    <c:if test="${not empty errorMessage}">
                        <div class="alert error">
                            <strong>Lỗi:</strong> <c:out value="${errorMessage}"/>
                        </div>
                    </c:if>

                    <div class="filter-section">
                        <div class="filter-controls">
                            <div class="filter-group">
                                <label for="searchQueryInput">Tìm kiếm</label>
                                <input type="text" id="searchQueryInput" name="search"
                                       placeholder="LogID, người dùng, thao tác, đối tượng..."
                                       value="${searchQuery}" class="filter-input">
                            </div>

                            <div class="filter-group">
                                <label for="filterAction">Loại thao tác</label>
                                <select id="filterAction" name="filterAction" class="filter-select">
                                    <option value="all">Tất cả thao tác</option>
                                    <c:forEach var="action" items="${distinctActions}">
                                        <option value="${action}" <c:if test="${filterAction == action}">selected</c:if>>
                                            ${action}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="filter-group">
                                <label for="filterObjectType">Loại đối tượng</label>
                                <select id="filterObjectType" name="filterObjectType" class="filter-select">
                                    <option value="all">Tất cả đối tượng</option>
                                    <c:forEach var="objectType" items="${distinctObjectTypes}">
                                        <option value="${objectType}" <c:if test="${filterObjectType == objectType}">selected</c:if>>
                                            ${objectType}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="filter-buttons">
                                <button type="button" onclick="applyFilters()" class="btn-primary">Áp dụng lọc</button>
                                <button type="button" onclick="clearAllFilters()" class="btn-secondary">Xóa tất cả</button>
                            </div>
                        </div>
                    </div>

                    <div class="table-section">
                        <table class="log-table">
                            <thead>
                                <tr>
                                    <th onclick="sortTable('LogID')">Log ID
                                        <c:if test="${sortBy == 'LogID'}">
                                            <span class="sort-arrow">${sortOrder == 'ASC' ? '▲' : '▼'}</span>
                                        </c:if>
                                    </th>
                                    <th onclick="sortTable('Timestamp')">Timestamp
                                        <c:if test="${sortBy == 'Timestamp' || sortBy == ''}">
                                            <span class="sort-arrow">${sortOrder == 'ASC' ? '▲' : '▼'}</span>
                                        </c:if>
                                    </th>
                                    <th onclick="sortTable('Username')">Tên người dùng
                                        <c:if test="${sortBy == 'Username'}">
                                            <span class="sort-arrow">${sortOrder == 'ASC' ? '▲' : '▼'}</span>
                                        </c:if>
                                    </th>
                                    <th onclick="sortTable('Action')">Thao tác
                                        <c:if test="${sortBy == 'Action'}">
                                            <span class="sort-arrow">${sortOrder == 'ASC' ? '▲' : '▼'}</span>
                                        </c:if>
                                    </th>
                                    <th onclick="sortTable('ObjectType')">Loại đối tượng
                                        <c:if test="${sortBy == 'ObjectType'}">
                                            <span class="sort-arrow">${sortOrder == 'ASC' ? '▲' : '▼'}</span>
                                        </c:if>
                                    </th>
                                    <th>Chi tiết</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${not empty systemLogs}">
                                        <c:forEach var="log" items="${systemLogs}">
                                            <tr>
                                                <td>${log.logId}</td>
                                                <td class="timestamp-cell">
                                                    <fmt:parseDate value="${log.timestamp}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedTimestamp" type="both" />
                                                    <fmt:formatDate value="${parsedTimestamp}" pattern="yyyy-MM-dd HH:mm:ss" />
                                                </td>
                                                <td>${log.userName != null && log.userName != '' ? log.userName : 'Không có'} (${log.userId})</td>
                                                <td class="action-cell">${log.action}</td>
                                                <td>${log.objectType}</td>
                                                <td>
                                                    <button class="log-detail-btn"
                                                            onclick="showLogDetails('${log.logId}',
                                                                                   '${fn:escapeXml(log.oldValue)}',
                                                                                   '${fn:escapeXml(log.newValue)}',
                                                                                   '${fn:escapeXml(log.action)}',
                                                                                   '${fn:escapeXml(log.objectType)}')">
                                                        Xem
                                                    </button>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td colspan="6" style="text-align: center;">Không tìm thấy bản ghi nhật ký phù hợp với điều kiện lọc.</td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>

                    <div class="pagination-bar">
                            <div class="pagination-info">
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
                                        <option value="15" <c:if test="${pageSize == 15}">selected</c:if>>15</option>
                                        <option value="20" <c:if test="${pageSize == 20}">selected</c:if>>20</option>
                                        <option value="50" <c:if test="${pageSize == 50}">selected</c:if>>50</option>
                                    </select>
                                </div>
                            </div>

                            <div class="pagination-controls">

                                <c:url var="baseUrl" value="/admin">
                                    <c:param name="action" value="audit-log" />
                                    <c:param name="pageSize" value="${pageSize}" />
                                    <%-- Thêm các tham số search/filter/sort để đảm bảo liên kết giữ trạng thái --%>
                                    <c:param name="search" value="${searchQuery}" />
                                    <c:param name="filterAction" value="${filterAction eq 'all' ? null : filterAction}" />
                                    <c:param name="filterObjectType" value="${filterObjectType eq 'all' ? null : filterObjectType}" />
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

        <div id="logDetailsModal" class="modal">
             <div class="modal-content">
                <span onclick="document.getElementById('logDetailsModal').style.display='none'" class="close">&times;</span>
                <h3>Chi tiết nhật ký - ID: <span id="modalLogId"></span></h3>
                <p><strong>Thao tác:</strong> <span id="modalAction"></span> trên <span id="modalObjectType"></span></p>
                <hr>
                <h4>Giá trị cũ:</h4>
                <pre id="modalOldValue"></pre>
                <h4>Giá trị mới:</h4>
                <pre id="modalNewValue"></pre>
            </div>
        </div>

        <script>
            function applyFilters() {
                const urlParams = new URLSearchParams();

                const search = document.getElementById('searchQueryInput').value.trim();
                const action = document.getElementById('filterAction').value;
                const objectType = document.getElementById('filterObjectType').value;

                const currentPageSize = document.getElementById('pageSizeSelect').value || '10';
                const currentSortBy = '${sortBy}';
                const currentSortOrder = '${sortOrder}';

                urlParams.set('action', 'audit-log');
                urlParams.set('page', '1');
                urlParams.set('pageSize', currentPageSize);

                if (search !== '') urlParams.set('search', search);
                if (action !== 'all') urlParams.set('filterAction', action);
                if (objectType !== 'all') urlParams.set('filterObjectType', objectType);

                if (currentSortBy && currentSortOrder) {
                    urlParams.set('sortBy', currentSortBy);
                    urlParams.set('sortOrder', currentSortOrder);
                }

                window.location.href = '${pageContext.request.contextPath}/admin?' + urlParams.toString();
            }

            function clearAllFilters() {
                window.location.href = '${pageContext.request.contextPath}/admin?action=audit-log';
            }

            function changePageSize(newSize) {
                const urlParams = new URLSearchParams();

                urlParams.set('action', 'audit-log');
                urlParams.set('pageSize', newSize);
                urlParams.set('page', '1');

                const searchInput = document.getElementById('searchQueryInput');
                const filterActionSelect = document.getElementById('filterAction');
                const filterObjectTypeSelect = document.getElementById('filterObjectType');

                const search = searchInput ? searchInput.value.trim() : '';
                const filterAction = filterActionSelect ? filterActionSelect.value : '';
                const filterObjectType = filterObjectTypeSelect ? filterObjectTypeSelect.value : '';

                const currentUrlParams = new URLSearchParams(window.location.search);
                const sortBy = currentUrlParams.get('sortBy') || 'Timestamp';
                const sortOrder = currentUrlParams.get('sortOrder') || 'DESC';

                if (search && search !== '') {
                    urlParams.set('search', search);
                }
                if (filterAction && filterAction !== 'all' && filterAction !== '') {
                    urlParams.set('filterAction', filterAction);
                }
                if (filterObjectType && filterObjectType !== 'all' && filterObjectType !== '') {
                    urlParams.set('filterObjectType', filterObjectType);
                }

                if (sortBy && sortBy !== '') {
                    urlParams.set('sortBy', sortBy);
                }
                if (sortOrder && sortOrder !== '') {
                    urlParams.set('sortOrder', sortOrder);
                }

                window.location.href = '${pageContext.request.contextPath}/admin?' + urlParams.toString();
            }

            function sortTable(column) {
                const urlParams = new URLSearchParams(window.location.search);
                const currentSortBy = urlParams.get('sortBy') || 'Timestamp';
                const currentSortOrder = urlParams.get('sortOrder') || 'DESC';
                let newSortOrder = 'ASC';

                if (currentSortBy === column) {
                    newSortOrder = (currentSortOrder === 'ASC') ? 'DESC' : 'ASC';
                }

                urlParams.set('action', 'audit-log');
                urlParams.set('page', '1');
                urlParams.set('sortBy', column);
                urlParams.set('sortOrder', newSortOrder);

                if (urlParams.get('search') === '') urlParams.delete('search');
                if (urlParams.get('filterAction') === 'all') urlParams.delete('filterAction');
                if (urlParams.get('filterObjectType') === 'all') urlParams.delete('filterObjectType');

                window.location.href = '${pageContext.request.contextPath}/admin?' + urlParams.toString();
            }

            function showLogDetails(logId, oldValue, newValue, action, objectType) {
                document.getElementById('modalLogId').innerText = logId;
                document.getElementById('modalAction').innerText = action;
                document.getElementById('modalObjectType').innerText = objectType;

                const formatValue = (value) => {
                    if (!value || value.trim() === 'null' || value.trim() === '') {
                        return 'Không có hoặc trống';
                    }
                    let decodedValue = new DOMParser().parseFromString(value, 'text/html').documentElement.textContent;
                    return decodedValue.replace(/\\n/g, '\n').replace(/\\r/g, '\r');
                };

                document.getElementById('modalOldValue').innerText = formatValue(oldValue);
                document.getElementById('modalNewValue').innerText = formatValue(newValue);
                document.getElementById('logDetailsModal').style.display = 'block';
            }
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
