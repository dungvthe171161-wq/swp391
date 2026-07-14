<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Danh sách ứng viên - Nhân viên nhân sự</title>
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css"/>
        <style>
            :root {
                --primary:#5b5bd6;
                --secondary:#8b5bd6;
                --bg:#f3f4f6;
                --card:#ffffff;
                --border:#e5e7eb;
                --text:#111827;
                --muted:#6b7280;
                --success:#10b981;
                --danger:#ef4444;
                --accent:#2563eb;
            }

            * {
                box-sizing: border-box;
            }

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
                background: linear-gradient(90deg, var(--primary), var(--secondary));
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
                background: rgba(255,255,255,0.15);
                color: #fff;
            }

            .btn:hover {
                background: rgba(255,255,255,0.25);
                transform: translateY(-1px);
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

            .side-link {
                display: flex;
                align-items: center;
                gap: 10px;
                padding: 12px 12px;
                border-radius: 10px;
                color: var(--text);
                text-decoration: none;
                transition: all 0.2s;
                font-weight: 500;
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

            .muted {
                color: var(--muted);
                font-size: 14px;
            }

            .card {
                background: var(--card);
                border: 1px solid var(--border);
                border-radius: 14px;
                padding: 20px;
                box-shadow: 0 1px 3px rgba(0,0,0,0.08);
            }

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

            .actions {
                display: flex;
                gap: 10px;
                align-items: center;
                justify-content: flex-end;
                margin-top: 0;
            }

            .filter-field.actions {
                grid-column: span 12;
                flex-direction: row;
                justify-content: flex-end;
                margin-top: 8px;
            }

            .btn-primary {
                background: var(--accent);
                color: #fff;
            }

            .btn-secondary {
                background: #e5e7eb;
                color: var(--text);
            }

            .btn-primary:hover {
                background: #1d4ed8;
            }

            .btn-secondary:hover {
                background: #d1d5db;
            }

            .table-card {
                padding: 0;
                overflow: hidden;
            }

            .table-header {
                padding: 20px;
                border-bottom: 1px solid var(--border);
                display: flex;
                justify-content: space-between;
                align-items: center;
                gap: 12px;
            }

            .table-wrapper {
                width: 100%;
                overflow-x: auto;
            }

            table {
                width: 100%;
                border-collapse: collapse;
                min-width: 720px;
            }

            body.hr-staff-page-shell .staff-content .candidate-table-card {
                overflow: hidden !important;
            }

            body.hr-staff-page-shell .staff-content .candidate-table-wrapper {
                overflow-x: auto !important;
                background: #fff !important;
            }

            body.hr-staff-page-shell .staff-content table.candidate-table {
                width: 100% !important;
                min-width: 1120px !important;
                table-layout: fixed !important;
                border-collapse: separate !important;
                border-spacing: 0 !important;
            }

            body.hr-staff-page-shell .staff-content .candidate-table th,
            body.hr-staff-page-shell .staff-content .candidate-table td {
                padding: 12px 14px !important;
                vertical-align: middle !important;
                line-height: 1.45 !important;
                white-space: normal !important;
                overflow-wrap: anywhere !important;
                word-break: normal !important;
            }

            body.hr-staff-page-shell .staff-content .candidate-table thead th {
                background: #fbf8f2 !important;
                color: #5f6c64 !important;
                font-size: 12px !important;
                font-weight: 900 !important;
                letter-spacing: 0.04em !important;
                text-transform: uppercase !important;
            }

            body.hr-staff-page-shell .staff-content .candidate-table tbody td {
                color: #1f2d2a !important;
                font-size: 13px !important;
            }

            .candidate-id,
            .candidate-phone,
            .candidate-cv,
            .candidate-status {
                white-space: nowrap !important;
            }

            .candidate-name {
                font-weight: 700 !important;
            }

            .candidate-email,
            .candidate-job {
                font-size: 13px !important;
                line-height: 1.4 !important;
            }

            .candidate-date {
                font-size: 13px !important;
                color: #4f5d58 !important;
            }

            .candidate-actions {
                min-width: 132px !important;
            }

            .candidate-action-stack {
                display: grid !important;
                grid-template-columns: 1fr !important;
                gap: 8px !important;
                width: 100% !important;
            }

            body.hr-staff-page-shell .staff-content .candidate-action-stack .btn {
                width: 100% !important;
                min-height: 34px !important;
                justify-content: center !important;
                padding: 0 10px !important;
                border-radius: 999px !important;
                font-size: 12px !important;
                font-weight: 900 !important;
                line-height: 1.1 !important;
                text-align: center !important;
                white-space: nowrap !important;
                transform: none !important;
                box-shadow: none !important;
            }

            thead {
                background: #f9fafb;
                text-transform: uppercase;
                font-size: 12px;
                letter-spacing: 0.5px;
            }

            th, td {
                padding: 14px 20px;
                text-align: left;
                border-bottom: 1px solid var(--border);
                font-size: 14px;
            }

            tbody tr:hover {
                background: #f8fafc;
            }

            .status {
                display: inline-flex;
                align-items: center;
                gap: 6px;
                padding: 6px 12px;
                border-radius: 999px;
                font-size: 12px;
                font-weight: 600;
                text-transform: uppercase;
            }

            .status {
                white-space: nowrap;
                text-transform: none;
                font-weight: 800;
            }
            .status-applied { background: #eff6ff; color: #1d4ed8; }
            .status-screening { background: #f5f3ff; color: #6d28d9; }
            .status-interview { background: #ecfeff; color: #0e7490; }
            .status-offered { background: #fff7ed; color: #c2410c; }
            .status-hired { background: #dcfce7; color: #166534; }
            .status-rejected { background: #fee2e2; color: #b91c1c; }
            .status-withdrawn { background: #f3f4f6; color: #4b5563; }
            .status-processing,
            .status-default { background: #f3f4f6; color: #374151; }

            .empty-state {
                padding: 40px 20px;
                text-align: center;
                color: var(--muted);
            }

            .message {
                padding: 16px;
                margin: 20px;
                border-radius: 12px;
                background: #fee2e2;
                color: #991b1b;
                font-weight: 600;
            }

            .message.success {
                background: #dcfce7;
                color: #166534;
            }

            .message.warning {
                background: #fef3c7;
                color: #92400e;
            }

            .pagination {
                display: flex;
                gap: 10px;
                align-items: center;
                justify-content: center;
                padding: 20px;
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
                .layout {
                    grid-template-columns: 1fr;
                }
                .sidebar {
                    position: relative;
                    top: auto;
                }
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
                .filter-field.actions {
                    grid-column: span 1 !important;
                    flex-direction: column;
                    align-items: stretch;
                }
                .actions {
                    justify-content: stretch;
                }
                .actions .btn {
                    width: 100%;
                    justify-content: center;
                }
                table {
                    min-width: 600px;
                }
            }
        </style>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/hr-theme.css?v=hr-staff-shell-20260630-1">
        <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/chatbot.css">
</head>
    <body class="hr-staff-page-shell">
        <%
            request.setAttribute("hrStaffSidebarActive", "candidates");
            request.setAttribute("hrStaffPageTitle", "Ứng viên");
            request.setAttribute("hrStaffSearchPlaceholder", "Tìm kiếm ứng viên...");
            request.setAttribute("hrStaffProfileSubtitle", "Quản trị hồ sơ ứng viên");
        %>
        <div class="staff-shell">
            <%@ include file="_HrStaffSidebar.jspf" %>
            <main class="staff-main">
                <%@ include file="_HrStaffTopbar.jspf" %>
                <section class="staff-content">
        <div class="topbar">
            <div class="brand">
                <div class="logo">HR</div>
                <div>Quản lý ứng viên</div>
            </div>
            <div class="top-actions">
                <a class="btn" href="<%=request.getContextPath()%>/hrstaff">🏠 Trang nhân sự</a>
                <a class="btn" href="<%=request.getContextPath()%>/homepage">🌐 Trang chủ</a>
            </div>
        </div>

        <div class="layout">
            <aside class="sidebar">
                <div class="nav-group">
                    <div class="nav-title">Chính</div>
                    <a class="side-link" href="<%=request.getContextPath()%>/hrstaff">🏠 Trang nhân sự</a>
                </div>
                <div class="nav-group">
                    <div class="nav-title">Lương & hợp đồng</div>
                    <a class="side-link" href="<%=request.getContextPath()%>/hrstaff/payroll">💰 Lương & phụ cấp</a>
                    <a class="side-link" href="<%=request.getContextPath()%>/hrstaff/contracts/create">📝 Tạo hợp đồng</a>
                    <a class="side-link" href="<%=request.getContextPath()%>/hrstaff/contracts">📄 Danh sách hợp đồng</a>
                </div>
                <div class="nav-group">
                    <div class="nav-title">Tuyển dụng</div>
                    <a class="side-link" href="${pageContext.request.contextPath}/postRecruitments">📢 Tin tuyển dụng</a>
                    <a class="side-link active" href="${pageContext.request.contextPath}/candidates">👀 Ứng viên</a>
                    
                </div>
            </aside>

            <main class="content">
                <section class="card">
                    <div class="page-header">
                        <div>
                            <h1 class="page-title">Danh sách ứng viên</h1>
                            <p class="muted">Theo dõi và quản lý toàn bộ hồ sơ ứng viên trong quy trình tuyển dụng.</p>
                        </div>
                    </div>
                </section>

                <section class="card">
                    <form action="${pageContext.request.contextPath}/candidates" method="GET">
                        <div class="filters">
                            <div class="filter-field" style="grid-column: span 4;">
                                <label for="searchByName">Tìm theo tên</label>
                                <input type="text" id="searchByName" name="searchByName" placeholder="Nhập tên ứng viên..." value="${fn:escapeXml(param.searchByName)}">
                            </div>
                            <div class="filter-field" style="grid-column: span 3;">
                                <label for="filterStatus">Trạng thái</label>
                                <select id="filterStatus" name="filterStatus">
                                    <option value="">Tất cả</option>
                                    <option value="Applied" <c:if test="${param.filterStatus eq 'Applied'}">selected</c:if>>Đã nộp</option>
                                    <option value="Screening" <c:if test="${param.filterStatus eq 'Screening'}">selected</c:if>>Sàng lọc</option>
                                    <option value="Interview" <c:if test="${param.filterStatus eq 'Interview'}">selected</c:if>>Phỏng vấn</option>
                                    <option value="Offered" <c:if test="${param.filterStatus eq 'Offered'}">selected</c:if>>Đã qua phỏng vấn</option>
                                    <option value="Hired" <c:if test="${param.filterStatus eq 'Hired'}">selected</c:if>>Đã nhận offer</option>
                                    <option value="Rejected" <c:if test="${param.filterStatus eq 'Rejected'}">selected</c:if>>Từ chối</option>
                                </select>
                            </div>
                            <div class="filter-field" style="grid-column: span 2;">
                                <label for="startDate">Từ ngày</label>
                                <input type="date" id="startDate" name="startDate" value="${param.startDate}">
                            </div>
                            <div class="filter-field" style="grid-column: span 2;">
                                <label for="endDate">Đến ngày</label>
                                <input type="date" id="endDate" name="endDate" value="${param.endDate}">
                            </div>
                            <div class="filter-field actions">
                                <button type="submit" class="btn btn-primary">🔍 Lọc</button>
                                <a href="${pageContext.request.contextPath}/candidates" class="btn btn-secondary">✖ Xóa lọc</a>
                            </div>
                        </div>
                    </form>
                </section>

                <section class="card table-card candidate-table-card">
                    <div class="table-header">
                        <h2 style="margin:0;">Danh sách ứng viên</h2>
                        <span class="muted">Hiển thị ${applications != null ? fn:length(applications) : 0} hồ sơ trên trang này</span>
                    </div>
                    <div class="table-wrapper candidate-table-wrapper">
                        <c:choose>
                            <c:when test="${empty applications}">
                                <div class="empty-state">
                                    <div style="font-size:42px;">🗃️</div>
                                    <p>Không có ứng viên phù hợp với bộ lọc hiện tại.</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <table class="candidate-table">
                                    <colgroup>
                                        <col style="width: 80px;">
                                        <col style="width: 150px;">
                                        <col style="width: 220px;">
                                        <col style="width: 115px;">
                                        <col style="width: 60px;">
                                        <col style="width: 125px;">
                                        <col style="width: 140px;">
                                        <col style="width: 140px;">
                                        <col style="width: 130px;">
                                    </colgroup>
                                    <thead>
                                        <tr>
                                            <th>Application</th>
                                            <th>Họ và tên</th>
                                            <th>Email</th>
                                            <th>Số điện thoại</th>
                                            <th>CV</th>
                                            <th>Ngày ứng tuyển</th>
                                            <th>Trạng thái</th>
                                            <th>Tin tuyển dụng</th>
                                            <th>Phỏng vấn</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="app" items="${applications}">
                                            <tr>
                                                <td class="candidate-id">#${app.application.applicationId}</td>
                                                <td class="candidate-name">
                                                    <c:choose>
                                                        <c:when test="${not empty app.candidateProfile.fullName}">${app.candidateProfile.fullName}</c:when>
                                                        <c:otherwise>${app.guest.fullName}</c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td class="candidate-email">
                                                    <c:choose>
                                                        <c:when test="${not empty app.candidateProfile.email}">${app.candidateProfile.email}</c:when>
                                                        <c:otherwise>${app.guest.email}</c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td class="candidate-phone">
                                                    <c:choose>
                                                        <c:when test="${not empty app.candidateProfile.phone}">${app.candidateProfile.phone}</c:when>
                                                        <c:otherwise>${app.guest.phone}</c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td class="candidate-cv"><a href="${pageContext.request.contextPath}/viewCV?applicationId=${app.application.applicationId}" style="color:var(--accent);font-weight:600;text-decoration:none;">Xem</a></td>
                                                <td class="candidate-date">${app.application.appliedDate}</td>
                                                <td class="candidate-status">
                                                    <c:set var="statusKey" value="${fn:toLowerCase(app.application.status)}"/>
                                                    <span class="status status-${statusKey != null ? statusKey : 'default'}">
                                                        <c:choose>
                                                            <c:when test="${statusKey eq 'applied'}">Đã nộp</c:when>
                                                            <c:when test="${statusKey eq 'screening'}">Sàng lọc CV</c:when>
                                                            <c:when test="${statusKey eq 'interview' and app.application.currentStep eq 'Offer'}">Đã qua phỏng vấn</c:when>
                                                            <c:when test="${statusKey eq 'interview'}">Phỏng vấn</c:when>
                                                            <c:when test="${statusKey eq 'offered'}">Đã qua phỏng vấn</c:when>
                                                            <c:when test="${statusKey eq 'hired'}">Đã nhận offer</c:when>
                                                            <c:when test="${statusKey eq 'rejected'}">Từ chối</c:when>
                                                            <c:otherwise>${app.application.status}</c:otherwise>
                                                        </c:choose>
                                                    </span>
                                                </td>
                                                <td class="candidate-job">${app.jobTitle}</td>
                                                <td class="candidate-actions">
                                                    <div class="candidate-action-stack">
                                                        <c:choose>
                                                            <c:when test="${statusKey eq 'applied' or statusKey eq 'screening'}">
                                                                <a class="btn btn-primary" href="${pageContext.request.contextPath}/hrstaff/interviews/schedule?applicationId=${app.application.applicationId}">Đặt lịch</a>
                                                            </c:when>
                                                            <c:when test="${statusKey eq 'interview' and app.application.currentStep eq 'Offer'}">
                                                                <a class="btn btn-primary" href="${pageContext.request.contextPath}/hrstaff/offers/manage?applicationId=${app.application.applicationId}">Soạn offer</a>
                                                            </c:when>
                                                            <c:when test="${statusKey eq 'interview'}">
                                                                <a class="btn btn-primary" href="${pageContext.request.contextPath}/hrstaff/interviews/schedule?applicationId=${app.application.applicationId}">Xem lịch</a>
                                                            </c:when>
                                                            <c:when test="${statusKey eq 'offered'}">
                                                                <a class="btn btn-primary" href="${pageContext.request.contextPath}/hrstaff/offers/manage?applicationId=${app.application.applicationId}">Xem offer</a>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <a class="btn btn-primary" href="${pageContext.request.contextPath}/viewCV?applicationId=${app.application.applicationId}">Xem chi tiết</a>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <c:if test="${not empty mess}">
                        <div class="message">${mess}</div>
                    </c:if>
                    <c:if test="${not empty success}">
                        <div class="message success">${success}</div>
                    </c:if>
                    <c:if test="${not empty warning}">
                        <div class="message warning">${warning}</div>
                    </c:if>

                    <c:if test="${totalPages > 1}">
                        <div class="pagination">
                            <c:choose>
                                <c:when test="${currentPage > 1}">
                                    <a class="page-item" href="${pageContext.request.contextPath}/candidates?page=${currentPage - 1}&searchByName=${param.searchByName}&filterStatus=${param.filterStatus}&startDate=${param.startDate}&endDate=${param.endDate}">«</a>
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
                                        <a class="page-item" href="${pageContext.request.contextPath}/candidates?page=${i}&searchByName=${param.searchByName}&filterStatus=${param.filterStatus}&startDate=${param.startDate}&endDate=${param.endDate}">${i}</a>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>

                            <c:choose>
                                <c:when test="${currentPage < totalPages}">
                                    <a class="page-item" href="${pageContext.request.contextPath}/candidates?page=${currentPage + 1}&searchByName=${param.searchByName}&filterStatus=${param.filterStatus}&startDate=${param.startDate}&endDate=${param.endDate}">»</a>
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
        <%@ include file="../AI/AI_Assistant_Widget.jspf" %>
    <script charset="UTF-8" src="${pageContext.request.contextPath}/js/chatbot.js"></script>
</body>
</html>

