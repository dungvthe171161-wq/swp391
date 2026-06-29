<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Tin tuyển dụng - BetterHR</title>
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css"/>
        <style>
            :root {
                --primary:#5b5bd6;
                --secondary:#8b5bd6;
                --bg:#f3f4f6;
                --card:#ffffff;
                --border:#e5e7eb;
                --text:#000000;
                --muted:#6b7280;
                --success:#10b981;
                --warning:#f59e0b;
                --danger:#ef4444;
                --accent:#2563eb;
            }

            * { box-sizing: border-box; }

            body {
                margin: 0;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background: var(--bg);
                color: var(--text);
            }

            .topbar {
                height: 64px;
                display: flex;
                align-items: center;
                justify-content: space-between;
                padding: 0 20px;
                background: linear-gradient(100deg, var(--primary), var(--secondary));
                color: #fff;
                box-shadow: 0 2px 6px rgba(0,0,0,0.08);
            }

            .brand {
                display: flex;
                align-items: center;
                gap: 10px;
                font-weight: 700;
                font-size: 22px;
            }

            .brand .logo {
                width: 30px;
                height: 30px;
                border-radius: 10px;
                background: #fff;
                color: var(--primary);
                display: flex;
                align-items: center;
                justify-content: center;
                font-weight: 800;
            }

            .top-actions {
                display: flex;
                align-items: center;
                gap: 12px;
            }

            .btn {
                display: inline-flex;
                align-items: center;
                gap: 6px;
                padding: 10px 16px;
                border-radius: 10px;
                border: none;
                cursor: pointer;
                text-decoration: none;
                font-weight: 600;
                transition: all 0.2s ease;
            }

            .btn.primary { background: #fff; color: var(--primary); }
            .btn.secondary { 
                background: rgba(255,255,255,0.18); 
                color: #fff; 
            }
            
            .btn.secondary:hover {
                background: rgba(255,255,255,0.25);
            }
            .btn.success { background: var(--success); color: #fff; }
            .btn.warning { background: var(--warning); color: #fff; }
            .btn.danger { background: var(--danger); color: #fff; }

            .btn:hover {
                transform: translateY(-1px);
                box-shadow: 0 6px 16px rgba(37,99,235,0.2);
            }

            .layout {
                display: grid;
                grid-template-columns: 280px 1fr;
                gap: 20px;
                padding: 24px;
                max-width: 1400px;
                margin: 0 auto;
            }

            .sidebar {
                background: var(--card);
                border: 1px solid var(--border);
                border-radius: 14px;
                padding: 20px 18px;
                position: sticky;
                top: 24px;
                height: fit-content;
            }

            .nav-group + .nav-group {
                margin-top: 20px;
                padding-top: 18px;
                border-top: 1px solid var(--border);
            }

            .nav-title {
                font-size: 12px;
                text-transform: uppercase;
                letter-spacing: .5px;
                color: var(--muted);
                font-weight: 600;
                margin-bottom: 10px;
            }

            .side-link, .side-link:visited {
                display: flex;
                align-items: center;
                gap: 10px;
                padding: 12px 12px;
                border-radius: 10px;
                color: var(--text) !important;
                text-decoration: none;
                transition: all 0.2s;
                font-weight: 500;
                font-size: 15px;
                white-space: nowrap;
            }

            .side-link:hover {
                background: #eef2ff;
                color: var(--primary);
            }

            .side-link.active {
                background: linear-gradient(120deg, var(--primary), var(--secondary));
                color: #fff;
                box-shadow: 0 6px 18px rgba(91, 91, 214, 0.25);
            }

            .content {
                display: flex;
                flex-direction: column;
                gap: 20px;
            }

            .card {
                background: var(--card);
                border: 1px solid var(--border);
                border-radius: 14px;
                padding: 22px;
                box-shadow: 0 1px 3px rgba(0,0,0,0.08);
            }

            .page-header {
                display: flex;
                justify-content: space-between;
                align-items: flex-start;
                gap: 16px;
                flex-wrap: wrap;
            }

            .page-title {
                margin: 0;
                font-size: 28px;
                font-weight: 700;
            }

            .muted { color: var(--muted); font-size: 14px; }

            .filters {
                display: grid;
                grid-template-columns: repeat(12, 1fr);
                gap: 16px;
                align-items: end;
            }

            .filter-field {
                display: flex;
                flex-direction: column;
                gap: 8px;
            }

            .filter-field label {
                font-size: 12px;
                font-weight: 600;
                color: var(--muted);
                text-transform: uppercase;
                letter-spacing: 0.4px;
                margin-bottom: 0;
            }

            .filter-field input,
            .filter-field select {
                width: 100%;
                border: 1px solid var(--border);
                border-radius: 10px;
                padding: 10px 12px;
                font-size: 14px;
                transition: all 0.2s;
                background: #fff;
                font-family: inherit;
                color: var(--text);
            }

            .filter-field select {
                cursor: pointer;
                appearance: none;
                background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%236b7280' d='M6 9L1 4h10z'/%3E%3C/svg%3E");
                background-repeat: no-repeat;
                background-position: right 12px center;
                padding-right: 35px;
            }

            .filter-field input[type="date"] {
                position: relative;
                padding-right: 35px;
            }

            .filter-field input[type="date"]::-webkit-calendar-picker-indicator {
                position: absolute;
                right: 8px;
                cursor: pointer;
                opacity: 0.6;
            }

            .filter-field input:focus,
            .filter-field select:focus {
                outline: none;
                border-color: var(--primary);
                box-shadow: 0 0 0 3px rgba(91,91,214,0.12);
            }

            .filter-actions {
                display: flex;
                gap: 10px;
                align-items: center;
                justify-content: flex-end;
                margin-top: 0;
            }

            .filter-field.filter-actions {
                grid-column: span 12;
                flex-direction: row;
                justify-content: flex-end;
                margin-top: 8px;
                align-items: center;
                gap: 10px;
            }

            .filter-field.filter-actions .btn {
                white-space: nowrap;
                flex-shrink: 0;
                margin: 0;
            }

            .message {
                padding: 16px;
                border-radius: 12px;
                margin-top: 12px;
                font-weight: 600;
            }

            .message.success { background: #dcfce7; color: #166534; border: 1px solid #bbf7d0; }
            .message.error { background: #fee2e2; color: #b91c1c; border: 1px solid #fecaca; }

            .recruitment-list {
                display: flex;
                flex-direction: column;
                gap: 16px;
            }

            .recruitment-card {
                background: var(--card);
                border: 1px solid var(--border);
                border-radius: 14px;
                padding: 20px;
                display: grid;
                grid-template-columns: 1fr auto;
                gap: 20px;
                transition: transform 0.2s ease, box-shadow 0.2s ease;
            }

            .recruitment-card:hover {
                transform: translateY(-3px);
                box-shadow: 0 12px 24px rgba(15, 23, 42, 0.12);
            }

            .status-pill {
                display: inline-flex;
                align-items: center;
                gap: 6px;
                padding: 6px 12px;
                border-radius: 999px;
                font-size: 12px;
                font-weight: 700;
                text-transform: uppercase;
            }

            .status-New { background: #e0e7ff; color: #3730a3; }
            .status-Waiting { background: #dbeafe; color: #1d4ed8; }
            .status-Rejected { background: #fee2e2; color: #b91c1c; }
            .status-Applied { background: #dcfce7; color: #15803d; }
            .status-Deleted { background: #f3f4f6; color: #4b5563; }

            .metrics {
                display: flex;
                gap: 16px;
                flex-wrap: wrap;
                font-size: 14px;
                color: var(--muted);
            }

            .card-actions {
                display: flex;
                gap: 10px;
                align-items: center;
            }

            .empty-state {
                padding: 48px 20px;
                text-align: center;
                color: var(--muted);
            }

            .pagination {
                display: flex;
                gap: 10px;
                align-items: center;
                justify-content: center;
                padding-top: 20px;
            }

            .page-item {
                display: inline-flex;
                align-items: center;
                justify-content: center;
                min-width: 36px;
                height: 36px;
                padding: 0 12px;
                border-radius: 8px;
                border: 1px solid var(--border);
                color: var(--text);
                text-decoration: none;
                font-weight: 600;
                transition: all 0.2s;
            }

            .page-item:hover {
                border-color: var(--primary);
                color: var(--primary);
            }

            .page-item.active {
                background: var(--primary);
                border-color: var(--primary);
                color: #fff;
                cursor: default;
            }

            .page-item.disabled {
                opacity: 0.4;
                pointer-events: none;
            }

            @media (max-width: 1024px) {
                .layout { grid-template-columns: 1fr; }
                .sidebar { position: relative; top: auto; }
                .recruitment-card { grid-template-columns: 1fr; }
            }

            @media (max-width: 768px) {
                .topbar {
                    flex-direction: column;
                    gap: 12px;
                    height: auto;
                    padding: 16px;
                }
                .filters {
                    grid-template-columns: 1fr;
                }
                .filter-field {
                    grid-column: span 1 !important;
                }
                .filter-field.filter-actions {
                    grid-column: span 1 !important;
                    flex-direction: column;
                    align-items: stretch;
                }
                .filter-actions {
                    justify-content: stretch;
                }
                .filter-actions .btn {
                    width: 100%;
                    justify-content: center;
                }
            }
        </style>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/hr-theme.css?v=hr-staff-shell-20260627-1">
    </head>
    <body class="hr-staff-page-shell">
        <%
            request.setAttribute("hrStaffSidebarActive", "recruitment");
            request.setAttribute("hrStaffPageTitle", "Tin tuyển dụng");
            request.setAttribute("hrStaffSearchPlaceholder", "Tìm kiếm tin tuyển dụng...");
            request.setAttribute("hrStaffProfileSubtitle", "Quản trị tuyển dụng");
        %>
        <div class="staff-shell">
            <%@ include file="_HrStaffSidebar.jspf" %>
            <main class="staff-main">
                <%@ include file="_HrStaffTopbar.jspf" %>
                <section class="staff-content">
        <div class="layout">
            <main class="content">
                <section class="card">
                    <div class="page-header">
                        <div>
                            <h1 class="page-title hr-staff-recruitment-page-title">Danh sách tin tuyển dụng</h1>
                            <p class="muted">Tạo, gửi duyệt và theo dõi trạng thái các tin tuyển dụng.</p>
                        </div>
                        <a class="btn success hr-staff-create-recruitment-action" href="${pageContext.request.contextPath}/detailRecruitmentCreate">
                            <i class="fa-solid fa-plus"></i>
                            Tạo tin tuyển dụng
                        </a>
                    </div>
                    <c:if test="${not empty mess}">
                        <div class="message success">${mess}</div>
                    </c:if>
                </section>

                <section class="card">
                    <c:choose>
                        <c:when test="${empty recruitment}">
                            <div class="empty-state">
                                <div style="font-size:48px;">📭</div>
                                <p>Chưa có tin tuyển dụng nào.</p>
                                <a class="btn success" href="${pageContext.request.contextPath}/detailRecruitmentCreate">Tạo tin tuyển dụng</a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="recruitment-list">
                                <c:forEach var="rec" items="${recruitment}">
                                    <div class="recruitment-card">
                                        <div>
                                            <h3 style="margin:0 0 10px 0;">${rec.title}</h3>
                                            <div class="metrics">
                                                <span>🗓️ Ngày đăng: <strong>${rec.postedDate}</strong></span>
                                                <span>👥 Số lượng tuyển: <strong>${rec.applicant}</strong></span>
                                                <span>
                                                    <span class="status-pill status-${rec.status}">
                                                        <c:choose>
                                                            <c:when test="${rec.status eq 'New'}">Mới</c:when>
                                                            <c:when test="${rec.status eq 'Waiting'}">Chờ duyệt</c:when>
                                                            <c:when test="${rec.status eq 'Rejected'}">Bị từ chối</c:when>
                                                            <c:when test="${rec.status eq 'Applied'}">Đang tuyển</c:when>
                                                            <c:when test="${rec.status eq 'Deleted'}">Đã xóa</c:when>
                                                            <c:otherwise>${rec.status}</c:otherwise>
                                                        </c:choose>
                                                    </span>
                                                </span>
                                            </div>
                                        </div>
                                        <div class="card-actions">
                                            <a class="btn secondary" href="${pageContext.request.contextPath}/detailRecruitment?id=${rec.recruitmentId}">👁️ Xem</a>
                                            <c:if test="${rec.status ne 'Deleted' and rec.status ne 'Waiting' and rec.status ne 'Applied'}">
                                                <a class="btn success" href="${pageContext.request.contextPath}/postRecruitments?action=send&id=${rec.recruitmentId}" onclick="return confirm('Bạn có chắc muốn gửi tin tuyển dụng này để duyệt không?');">📨 Gửi duyệt</a>
                                                <a class="btn danger" href="${pageContext.request.contextPath}/postRecruitments?action=delete&id=${rec.recruitmentId}" onclick="return confirm('Bạn có chắc muốn xóa tin tuyển dụng này không?');">🗑️ Xóa</a>
                                            </c:if>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>

                    <c:if test="${totalPages > 1}">
                        <div class="pagination">
                            <c:choose>
                                <c:when test="${currentPage > 1}">
                                    <a class="page-item" href="${pageContext.request.contextPath}/postRecruitments?page=${currentPage - 1}">«</a>
                                </c:when>
                                <c:otherwise>
                                    <span class="page-item disabled">«</span>
                                </c:otherwise>
                            </c:choose>

                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <c:choose>
                                    <c:when test="${currentPage == i}">
                                        <span class="page-item active">${i}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <a class="page-item" href="${pageContext.request.contextPath}/postRecruitments?page=${i}">${i}</a>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>

                            <c:choose>
                                <c:when test="${currentPage < totalPages}">
                                    <a class="page-item" href="${pageContext.request.contextPath}/postRecruitments?page=${currentPage + 1}">»</a>
                                </c:when>
                                <c:otherwise>
                                    <span class="page-item disabled">»</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:if>
                </section>
            </main>
        </div>
                </section>
            </main>
        </div>
    </body>
</html>

