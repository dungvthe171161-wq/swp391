<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${sessionScope.systemUser == null || sessionScope.systemUser.roleId != 1}">
    <c:redirect url="/homepage"/>
</c:if>
<c:if test="${sessionScope.systemUser.roleId == 1 && empty audiences}">
    <c:redirect url="/admin/chatbot-faqs"/>
</c:if>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý FAQ chatbot - BetterHR</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/admin-dashboard-redesign.css?v=20260618c">
    <style>
        :root {
            --faq-ink: #17231f;
            --faq-muted: #65716c;
            --faq-border: #dce4e0;
            --faq-surface: #ffffff;
            --faq-soft: #f3f7f5;
            --faq-primary: #006b49;
            --faq-primary-dark: #174336;
            --faq-warning: #fff4d6;
            --faq-danger: #b42318;
        }

        body.chatbot-faq-admin-page {
            letter-spacing: 0;
        }

        .chatbot-faq-admin-page button,
        .chatbot-faq-admin-page input,
        .chatbot-faq-admin-page select,
        .chatbot-faq-admin-page textarea {
            font: inherit;
            letter-spacing: 0;
        }

        .faq-dashboard-content {
            padding: 32px 40px 48px;
        }

        .faq-admin-heading.dashboard-heading {
            align-items: flex-start;
            gap: 24px;
            margin-bottom: 24px;
        }

        .faq-heading-copy {
            max-width: 780px;
        }

        .faq-fallback-note {
            max-width: 430px;
            padding: 12px 14px;
            border-left: 4px solid #d59a00;
            background: var(--faq-warning);
            color: #2f2510;
            font-size: 13px;
            line-height: 1.45;
        }

        .faq-alert {
            margin-bottom: 16px;
            padding: 12px 14px;
            border-radius: 6px;
            border: 1px solid;
            font-weight: 700;
        }

        .faq-alert--success {
            color: #145c3e;
            background: #e8f7f0;
            border-color: #a9dbc5;
        }

        .faq-alert--error {
            color: #8a1c14;
            background: #fff0ee;
            border-color: #efb7b0;
        }

        .faq-band {
            padding: 24px 0;
            border-top: 1px solid var(--faq-border);
        }

        .faq-band__title {
            margin-bottom: 16px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 12px;
        }

        .faq-band__title h2 {
            margin: 0;
            color: var(--faq-ink);
            font-size: 20px;
        }

        .faq-count,
        .faq-help {
            color: var(--faq-muted);
            font-size: 13px;
        }

        .faq-grid {
            display: grid;
            grid-template-columns: repeat(12, minmax(0, 1fr));
            gap: 14px;
        }

        .faq-field {
            grid-column: span 4;
            display: grid;
            gap: 6px;
            min-width: 0;
        }

        .faq-field--wide {
            grid-column: span 8;
        }

        .faq-field--full {
            grid-column: 1 / -1;
        }

        .faq-field--small {
            grid-column: span 2;
        }

        .faq-field label {
            font-size: 13px;
            font-weight: 700;
            color: #405049;
        }

        .faq-field input,
        .faq-field select,
        .faq-field textarea,
        .faq-filter input,
        .faq-filter select,
        .faq-table input,
        .faq-table select,
        .faq-table textarea {
            width: 100%;
            border: 1px solid var(--faq-border);
            border-radius: 6px;
            background: var(--faq-surface);
            color: var(--faq-ink);
            outline: none;
        }

        .faq-field input,
        .faq-field select,
        .faq-filter input,
        .faq-filter select,
        .faq-table input,
        .faq-table select {
            min-height: 42px;
            padding: 0 10px;
        }

        .faq-field textarea,
        .faq-table textarea {
            min-height: 84px;
            padding: 10px;
            resize: vertical;
            line-height: 1.45;
        }

        .faq-field input:focus,
        .faq-field select:focus,
        .faq-field textarea:focus,
        .faq-filter input:focus,
        .faq-filter select:focus,
        .faq-table input:focus,
        .faq-table select:focus,
        .faq-table textarea:focus {
            border-color: var(--faq-primary);
            box-shadow: 0 0 0 3px rgba(0, 107, 73, .10);
        }

        .faq-check {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            min-height: 42px;
            font-weight: 700;
        }

        .faq-check input {
            width: 18px;
            height: 18px;
            accent-color: var(--faq-primary);
        }

        .faq-actions {
            grid-column: 1 / -1;
            display: flex;
            gap: 10px;
            align-items: center;
        }

        .faq-button {
            min-height: 40px;
            padding: 0 14px;
            border: 1px solid transparent;
            border-radius: 6px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 7px;
            background: var(--faq-primary);
            color: #fff;
            font-weight: 800;
            cursor: pointer;
            text-decoration: none;
            white-space: nowrap;
        }

        .faq-button--secondary {
            color: var(--faq-primary-dark);
            background: #fff;
            border-color: var(--faq-border);
        }

        .faq-button--danger {
            color: var(--faq-danger);
            background: #fff;
            border-color: #efb7b0;
        }

        .faq-button .material-symbols-outlined {
            font-size: 19px;
        }

        .faq-filter {
            display: grid;
            grid-template-columns: minmax(260px, 1fr) 180px 170px auto auto;
            gap: 10px;
            margin-bottom: 16px;
            align-items: center;
        }

        .faq-table-wrap {
            width: 100%;
            overflow-x: auto;
            border: 1px solid var(--faq-border);
            border-radius: 8px;
            background: #fff;
            box-shadow: 0 10px 22px rgba(0, 0, 0, .035);
        }

        .faq-table {
            width: 100%;
            min-width: 1120px;
            border-collapse: collapse;
            table-layout: fixed;
        }

        .faq-table th,
        .faq-table td {
            padding: 10px 12px;
            border-bottom: 1px solid var(--faq-border);
            text-align: left;
            vertical-align: top;
        }

        .faq-table th {
            background: var(--faq-soft);
            color: #405049;
            font-size: 12px;
            text-transform: uppercase;
            letter-spacing: .02em;
        }

        .faq-table input,
        .faq-table select,
        .faq-table textarea {
            width: 100%;
            border: 1px solid var(--faq-border);
            border-radius: 6px;
            background: var(--faq-surface);
            color: var(--faq-ink);
            outline: none;
            font-size: 13px;
        }

        .faq-table input,
        .faq-table select {
            min-height: 36px;
            padding: 0 9px;
        }

        .faq-table textarea {
            min-width: 0;
            min-height: 68px;
            max-height: 92px;
            padding: 8px 9px;
            resize: vertical;
            line-height: 1.45;
        }

        .faq-table .faq-col-order {
            width: 72px;
        }

        .faq-table .faq-row-actions {
            display: flex;
            flex-direction: column;
            gap: 7px;
        }

        .faq-table .faq-row-actions .faq-button {
            min-height: 34px;
            padding: 0 10px;
            font-size: 13px;
        }

        .faq-table th:first-child,
        .faq-table td:first-child {
            width: 38px;
            text-align: center;
            font-weight: 800;
            color: var(--faq-primary-dark);
        }

        .faq-table th:nth-child(2),
        .faq-table td:nth-child(2) {
            width: 130px;
        }

        .faq-table th:nth-child(3),
        .faq-table td:nth-child(3) {
            width: 120px;
        }

        .faq-table th:nth-child(4),
        .faq-table td:nth-child(4) {
            width: 170px;
        }

        .faq-table th:nth-child(5),
        .faq-table td:nth-child(5) {
            width: 180px;
        }

        .faq-table th:nth-child(6),
        .faq-table td:nth-child(6) {
            width: 180px;
        }

        .faq-table th:nth-child(7),
        .faq-table td:nth-child(7) {
            width: 76px;
        }

        .faq-table th:nth-child(8),
        .faq-table td:nth-child(8) {
            width: 96px;
        }

        .faq-table th:last-child,
        .faq-table td:last-child {
            width: 92px;
            position: sticky;
            right: 0;
            z-index: 2;
            background: #fff;
            box-shadow: -10px 0 18px rgba(255, 255, 255, .92);
        }

        .faq-table th:last-child {
            z-index: 3;
            background: var(--faq-soft);
        }

        .faq-table tbody tr:hover td {
            background: #fbfdfc;
        }

        .faq-table tbody tr:hover td:last-child {
            background: #fff;
        }

        .faq-status {
            display: inline-flex;
            margin-top: 6px;
            padding: 4px 8px;
            border-radius: 999px;
            font-size: 12px;
            font-weight: 800;
        }

        .faq-status--active {
            color: #145c3e;
            background: #dff4ea;
        }

        .faq-status--inactive {
            color: #725b21;
            background: #f4ecd2;
        }

        .faq-table td:nth-child(8) .faq-check {
            min-height: 28px;
            font-size: 13px;
        }

        .faq-table td:nth-child(8) .faq-status {
            margin-top: 4px;
        }
        .faq-review-table {
            width: 100%;
            min-width: 980px;
            border-collapse: collapse;
        }

        .faq-review-table th,
        .faq-review-table td {
            padding: 11px 12px;
            border-bottom: 1px solid var(--faq-border);
            text-align: left;
            vertical-align: top;
        }

        .faq-review-table th {
            background: var(--faq-soft);
            font-size: 12px;
            text-transform: uppercase;
        }

        .faq-review-question {
            max-width: 360px;
            font-weight: 700;
        }

        .faq-review-reply {
            max-width: 430px;
            color: var(--faq-muted);
        }

        .faq-review-meta {
            font-size: 12px;
            color: var(--faq-muted);
            white-space: nowrap;
        }

        @media (max-width: 980px) {
            .faq-admin-heading.dashboard-heading {
                flex-direction: column;
                align-items: flex-start;
            }

            .faq-fallback-note {
                max-width: none;
            }

            .faq-field,
            .faq-field--wide {
                grid-column: span 6;
            }

            .faq-field--small {
                grid-column: span 3;
            }

            .faq-filter {
                grid-template-columns: 1fr 1fr;
            }
        }

        @media (max-width: 620px) {
            .faq-dashboard-content {
                padding-top: 24px;
            }

            .faq-grid {
                grid-template-columns: 1fr;
            }

            .faq-field,
            .faq-field--wide,
            .faq-field--small,
            .faq-field--full {
                grid-column: 1;
            }

            .faq-filter {
                grid-template-columns: 1fr;
            }

            .faq-filter .faq-button,
            .faq-actions .faq-button {
                width: 100%;
            }

            .faq-actions {
                align-items: stretch;
                flex-direction: column;
            }
        }
    </style>
</head>
<body class="admin-dashboard-page chatbot-faq-admin-page">
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
            <a href="${pageContext.request.contextPath}/admin/office-location"
               class="nav-item ${activePage == 'office-location' ? 'active' : ''}">
                <span class="material-symbols-outlined">location_on</span>
                <span>Cấu hình GPS</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin/chatbot-faqs"
               class="nav-item ${activePage == 'chatbot-faqs' ? 'active' : ''}">
                <span class="material-symbols-outlined">smart_toy</span>
                <span>FAQ chatbot</span>
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
                <input class="search-input" type="text" placeholder="Tìm trong quản trị...">
            </div>

            <div class="top-bar-actions">
                <%@ include file="../_NotificationBell.jspf" %>
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

        <section class="dashboard-content faq-dashboard-content">
            <div class="dashboard-heading faq-admin-heading">
                <div class="faq-heading-copy">
                    <p class="eyebrow">Quản trị</p>
                    <h1 class="page-title">Quản lý FAQ chatbot</h1>
                    <p class="page-subtitle">Quản trị nội dung FAQ, ưu tiên câu trả lời theo intent và vai trò người dùng.</p>
                </div>
                <div class="faq-fallback-note">
                    Không cần API key: câu hỏi ngoài FAQ sẽ trả lời an toàn rằng chưa có thông tin phù hợp và hướng người dùng chọn gợi ý hoặc liên hệ HR.
                </div>
            </div>

            <c:if test="${not empty successMessage}">
                <div class="faq-alert faq-alert--success"><c:out value="${successMessage}"/></div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div class="faq-alert faq-alert--error"><c:out value="${errorMessage}"/></div>
            </c:if>

            <section class="faq-band" aria-labelledby="create-faq-title">
                <div class="faq-band__title"><h2 id="create-faq-title">Thêm FAQ mới</h2></div>
                <form method="post" action="${pageContext.request.contextPath}/admin/chatbot-faqs" class="faq-grid">
                    <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>">
                    <input type="hidden" name="action" value="create">
                    <div class="faq-field">
                        <label for="new-intent">Intent</label>
                        <input id="new-intent" name="intent" maxlength="80" pattern="[a-z0-9_]{2,80}" placeholder="leave_request" required>
                        <span class="faq-help">Chữ thường không dấu, số và dấu gạch dưới.</span>
                    </div>
                    <div class="faq-field">
                        <label for="new-audience">Vai trò nhận câu trả lời</label>
                        <select id="new-audience" name="audienceRole" required>
                            <c:forEach var="audience" items="${audiences}"><option value="${audience}">${audience}</option></c:forEach>
                        </select>
                    </div>
                    <div class="faq-field faq-field--small">
                        <label for="new-order">Thứ tự</label>
                        <input id="new-order" type="number" name="sortOrder" min="0" max="100000" value="100" required>
                    </div>
                    <div class="faq-field faq-field--small">
                        <label>Trạng thái</label>
                        <label class="faq-check"><input type="checkbox" name="isActive" checked> Đang bật</label>
                    </div>
                    <div class="faq-field faq-field--full">
                        <label for="new-question">Câu hỏi mẫu</label>
                        <input id="new-question" name="question" maxlength="500" required>
                    </div>
                    <div class="faq-field faq-field--wide">
                        <label for="new-answer">Câu trả lời</label>
                        <textarea id="new-answer" name="answer" maxlength="4000" required></textarea>
                    </div>
                    <div class="faq-field">
                        <label for="new-suggestions">Gợi ý tiếp theo</label>
                        <textarea id="new-suggestions" name="suggestions" maxlength="1000" placeholder="Xem nhiệm vụ|Liên hệ HR"></textarea>
                        <span class="faq-help">Ngăn cách từng gợi ý bằng dấu |.</span>
                    </div>
                    <div class="faq-actions">
                        <button class="faq-button" type="submit"><span class="material-symbols-outlined" aria-hidden="true">add</span>Thêm FAQ</button>
                        <button class="faq-button faq-button--secondary" type="reset">Đặt lại</button>
                    </div>
                </form>
            </section>

            <section class="faq-band" aria-labelledby="faq-list-title">
                <div class="faq-band__title">
                    <h2 id="faq-list-title">Danh sách FAQ</h2>
                    <span class="faq-count"><c:out value="${faqs.size()}"/> bản ghi</span>
                </div>
                <form class="faq-filter" method="get" action="${pageContext.request.contextPath}/admin/chatbot-faqs">
                    <input type="search" name="search" value="<c:out value='${search}'/>" placeholder="Tìm intent, câu hỏi hoặc câu trả lời">
                    <select name="audience" aria-label="Lọc theo vai trò">
                        <option value="">Tất cả vai trò</option>
                        <c:forEach var="audience" items="${audiences}">
                            <option value="${audience}" ${audienceFilter == audience ? 'selected' : ''}>${audience}</option>
                        </c:forEach>
                    </select>
                    <select name="active" aria-label="Lọc theo trạng thái">
                        <option value="" ${empty activeFilter ? 'selected' : ''}>Tất cả trạng thái</option>
                        <option value="active" ${activeFilter == 'active' ? 'selected' : ''}>Đang bật</option>
                        <option value="inactive" ${activeFilter == 'inactive' ? 'selected' : ''}>Đã ẩn</option>
                    </select>
                    <button class="faq-button" type="submit"><span class="material-symbols-outlined" aria-hidden="true">search</span>Lọc</button>
                    <a class="faq-button faq-button--secondary" href="${pageContext.request.contextPath}/admin/chatbot-faqs">Xóa lọc</a>
                </form>

                <div class="faq-table-wrap">
                    <table class="faq-table">
                        <thead>
                        <tr><th>ID</th><th>Intent</th><th>Vai trò</th><th>Câu hỏi</th><th>Câu trả lời</th><th>Gợi ý</th><th>Thứ tự</th><th>Trạng thái</th><th>Hành động</th></tr>
                        </thead>
                        <tbody>
                        <c:forEach var="faq" items="${faqs}">
                            <tr>
                                <td>${faq.faqId}</td>
                                <td><input class="faq-col-intent" form="faq-form-${faq.faqId}" name="intent" maxlength="80" pattern="[a-z0-9_]{2,80}" value="<c:out value='${faq.intent}'/>" required></td>
                                <td>
                                    <select class="faq-col-audience" form="faq-form-${faq.faqId}" name="audienceRole" required>
                                        <c:forEach var="audience" items="${audiences}">
                                            <option value="${audience}" ${faq.audienceRole == audience ? 'selected' : ''}>${audience}</option>
                                        </c:forEach>
                                    </select>
                                </td>
                                <td><textarea form="faq-form-${faq.faqId}" name="question" maxlength="500" required><c:out value="${faq.question}"/></textarea></td>
                                <td><textarea form="faq-form-${faq.faqId}" name="answer" maxlength="4000" required><c:out value="${faq.answer}"/></textarea></td>
                                <td><textarea form="faq-form-${faq.faqId}" name="suggestions" maxlength="1000"><c:out value="${faq.suggestions}"/></textarea></td>
                                <td><input class="faq-col-order" form="faq-form-${faq.faqId}" type="number" name="sortOrder" min="0" max="100000" value="${faq.sortOrder}" required></td>
                                <td>
                                    <label class="faq-check"><input form="faq-form-${faq.faqId}" type="checkbox" name="isActive" ${faq.active ? 'checked' : ''}> Bật</label>
                                    <span class="faq-status ${faq.active ? 'faq-status--active' : 'faq-status--inactive'}">${faq.active ? 'Đang dùng' : 'Đã ẩn'}</span>
                                </td>
                                <td>
                                    <form id="faq-form-${faq.faqId}" class="faq-row-actions" method="post" action="${pageContext.request.contextPath}/admin/chatbot-faqs">
                                        <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>">
                                        <input type="hidden" name="faqId" value="${faq.faqId}">
                                        <button class="faq-button" type="submit" name="action" value="update"><span class="material-symbols-outlined" aria-hidden="true">save</span>Lưu</button>
                                        <c:choose>
                                            <c:when test="${faq.active}"><button class="faq-button faq-button--danger" type="submit" name="action" value="deactivate"><span class="material-symbols-outlined" aria-hidden="true">visibility_off</span>Ẩn</button></c:when>
                                            <c:otherwise><button class="faq-button faq-button--secondary" type="submit" name="action" value="activate"><span class="material-symbols-outlined" aria-hidden="true">visibility</span>Bật</button></c:otherwise>
                                        </c:choose>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty faqs}"><tr><td colspan="9" class="faq-empty">Chưa có FAQ phù hợp với bộ lọc.</td></tr></c:if>
                        </tbody>
                    </table>
                </div>
            </section>

            <section class="faq-band" aria-labelledby="review-title">
                <div class="faq-band__title">
                    <div><h2 id="review-title">Câu hỏi cần bổ sung FAQ</h2><span class="faq-help">Hiển thị fallback hoặc câu trả lời bị đánh giá không hữu ích gần đây.</span></div>
                    <span class="faq-count"><c:out value="${reviewItems.size()}"/> mục cần xem</span>
                </div>
                <div class="faq-table-wrap">
                    <table class="faq-review-table">
                        <thead><tr><th>Thời gian</th><th>Vai trò / trang</th><th>Câu hỏi</th><th>Câu trả lời bot</th><th>Kết quả</th></tr></thead>
                        <tbody>
                        <c:forEach var="review" items="${reviewItems}">
                            <tr>
                                <td class="faq-review-meta"><c:out value="${review.createdAt}"/></td>
                                <td class="faq-review-meta"><c:out value="${empty review.roleName ? 'Public' : review.roleName}"/><br><c:out value="${review.pagePath}"/></td>
                                <td class="faq-review-question"><c:out value="${review.userQuestion}"/></td>
                                <td class="faq-review-reply"><c:out value="${review.botReply}"/></td>
                                <td>
                                    <c:if test="${review.fallback}"><span class="faq-status faq-status--inactive">Fallback</span></c:if>
                                    <c:if test="${review.rating == 'NotUseful'}"><span class="faq-status faq-status--inactive">Không hữu ích</span></c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty reviewItems}"><tr><td colspan="5" class="faq-empty">Chưa có fallback hoặc phản hồi không hữu ích.</td></tr></c:if>
                        </tbody>
                    </table>
                </div>
            </section>
        </section>
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
        const userMenu = document.querySelector('.user-menu');
        if (userMenu && !userMenu.contains(event.target)) {
            userMenu.classList.remove('active');
        }
    });

    document.addEventListener('keydown', function (event) {
        if (event.key === 'Escape') {
            const userMenu = document.querySelector('.user-menu');
            if (userMenu) {
                userMenu.classList.remove('active');
            }
        }
    });
</script>
</body>
</html>