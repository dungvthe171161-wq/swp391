<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Danh sách ứng viên - BetterHR</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/hr-theme.css?v=hr-staff-shell-20260630-1">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/chatbot.css">
    <style>
        .candidate-page { max-width:1440px; margin:0 auto; }
        .candidate-page-header { display:flex; align-items:flex-start; justify-content:space-between; gap:24px; margin-bottom:22px; }
        .candidate-eyebrow { display:block; margin-bottom:7px; color:#00754a; font-size:13px; font-weight:800; text-transform:uppercase; }
        .candidate-page-title { margin:0; color:#00482f; font-size:30px; line-height:1.25; font-weight:800; }
        .candidate-page-subtitle { margin:8px 0 0; color:#66736d; font-size:15px; }
        .candidate-filters { display:grid; grid-template-columns:minmax(240px,1.4fr) 210px 170px 170px auto; gap:14px; align-items:end; margin-bottom:22px; border:1px solid #dce3df; border-radius:8px; padding:18px; background:#fff; }
        .candidate-filter-field label { display:block; margin-bottom:7px; color:#526159; font-size:12px; font-weight:800; text-transform:uppercase; }
        .candidate-filter-field input, .candidate-filter-field select { width:100%; min-height:42px; border:1px solid #ccd6d0; border-radius:7px; background:#fff; color:#24372f; padding:9px 12px; font:inherit; font-size:14px; }
        .candidate-filter-field input:focus, .candidate-filter-field select:focus { outline:none; border-color:#00754a; box-shadow:0 0 0 3px rgba(0,117,74,.1); }
        .candidate-filter-actions { display:flex; gap:8px; }
        .candidate-button { min-height:40px; display:inline-flex; align-items:center; justify-content:center; gap:8px; border:1px solid transparent; border-radius:7px; padding:0 14px; font-size:13px; font-weight:800; text-decoration:none; cursor:pointer; white-space:nowrap; }
        .candidate-filter-submit { background:#173f31; color:#fff; }
        .candidate-filter-submit:hover { background:#0d3024; }
        .candidate-filter-clear { border-color:#ccd6d0; background:#fff; color:#4d5e55; }
        .candidate-filter-clear:hover { border-color:#00754a; color:#00754a; background:#f4faf7; }
        .candidate-table-panel { border:1px solid #dce3df; border-radius:8px; background:#fff; overflow:hidden; }
        .candidate-list-bar { min-height:56px; display:flex; align-items:center; justify-content:space-between; gap:16px; padding:0 18px; border-bottom:1px solid #e5ebe7; background:#f8faf8; }
        .candidate-list-bar h2 { margin:0; color:#173f31; font-size:18px; font-weight:800; }
        .candidate-list-count { color:#6a766f; font-size:13px; font-weight:700; }
        .candidate-table-scroll { width:100%; overflow-x:auto; }
        .candidate-table { width:100%; min-width:1080px; table-layout:fixed; border-collapse:separate; border-spacing:0; }
        .candidate-table th { position:sticky; top:0; z-index:1; padding:13px 14px; border-bottom:1px solid #dce3df; background:#fbfaf7; color:#5d6a63; font-size:11px; font-weight:850; text-align:left; text-transform:uppercase; }
        .candidate-table td { height:72px; padding:12px 14px; border-bottom:1px solid #e6ebe8; color:#22352d; font-size:13px; vertical-align:middle; }
        .candidate-table tbody tr:last-child td { border-bottom:0; }
        .candidate-table tbody tr:hover td { background:#f7fbf8; }
        .candidate-col-person { width:26%; }
        .candidate-col-phone { width:12%; }
        .candidate-col-date { width:14%; }
        .candidate-col-job { width:16%; }
        .candidate-col-status { width:14%; }
        .candidate-col-cv { width:7%; text-align:center !important; }
        .candidate-col-action { width:11%; text-align:right !important; }
        .candidate-person { min-width:0; }
        .candidate-person-main { display:flex; align-items:center; gap:11px; min-width:0; }
        .candidate-avatar { width:36px; height:36px; flex:0 0 36px; display:flex; align-items:center; justify-content:center; border-radius:50%; background:#dff3e9; color:#006241; font-size:13px; font-weight:850; text-transform:uppercase; }
        .candidate-person-copy { min-width:0; }
        .candidate-name { display:block; color:#123f2f; font-size:14px; font-weight:850; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }
        .candidate-email { display:block; margin-top:3px; color:#6a766f; font-size:12px; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }
        .candidate-id { display:inline-block; margin-top:4px; color:#8a948f; font-size:11px; font-weight:750; }
        .candidate-phone, .candidate-job { white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }
        .candidate-date { color:#44554d; line-height:1.35; white-space:nowrap; }
        .candidate-status { display:inline-flex; align-items:center; gap:6px; border-radius:999px; padding:7px 10px; font-size:11px; font-weight:850; white-space:nowrap; }
        .candidate-status::before { content:''; width:6px; height:6px; border-radius:50%; background:currentColor; }
        .status-applied { background:#eaf2ff; color:#2458b8; }
        .status-screening { background:#f3efff; color:#7047b7; }
        .status-interview { background:#e7f8f8; color:#087783; }
        .status-offered { background:#fff2e7; color:#a95116; }
        .status-hired { background:#e3f7ec; color:#087a4d; }
        .status-rejected { background:#fff0ee; color:#b23a30; }
        .status-withdrawn, .status-processing, .status-default { background:#f0f2f1; color:#59645f; }
        .candidate-cv-link { width:36px; height:36px; display:inline-flex; align-items:center; justify-content:center; border:1px solid #bdd2c8; border-radius:7px; background:#fff; color:#00754a; text-decoration:none; }
        .candidate-cv-link:hover { border-color:#00754a; background:#eff9f4; }
        .candidate-action-button { min-width:112px; min-height:38px; display:inline-flex; align-items:center; justify-content:center; gap:7px; border-radius:7px; padding:0 12px; background:#006241; color:#fff; font-size:12px; font-weight:850; text-decoration:none; white-space:nowrap; }
        .candidate-action-button:hover { background:#004c32; color:#fff; }
        .candidate-empty { padding:52px 20px; text-align:center; color:#6a766f; }
        .candidate-empty i { margin-bottom:12px; color:#7b9187; font-size:30px; }
        .candidate-message { display:flex; align-items:flex-start; gap:9px; margin:16px 18px; border-radius:7px; padding:12px 14px; font-size:13px; font-weight:700; }
        .candidate-message.error { background:#fff0ee; color:#a62f25; border:1px solid #efc7c2; }
        .candidate-message.success { background:#effaf5; color:#136342; border:1px solid #b9decf; }
        .candidate-message.warning { background:#fff8e8; color:#8c620c; border:1px solid #ecd49e; }
        .candidate-table-footer { min-height:62px; display:flex; align-items:center; justify-content:space-between; gap:16px; padding:10px 18px; border-top:1px solid #e5ebe7; background:#fbfcfb; }
        .candidate-page-summary { color:#6a766f; font-size:13px; font-weight:700; }
        .candidate-pagination { display:flex; align-items:center; gap:7px; }
        .candidate-page-item { min-width:36px; height:36px; display:inline-flex; align-items:center; justify-content:center; border:1px solid #ccd6d0; border-radius:7px; background:#fff; color:#34473e; font-size:13px; font-weight:800; text-decoration:none; }
        .candidate-page-item:hover { border-color:#00754a; color:#00754a; }
        .candidate-page-item.active { border-color:#00754a; background:#00754a; color:#fff; }
        .candidate-page-item.disabled { opacity:.4; pointer-events:none; }
        @media (max-width:1180px) { .candidate-filters { grid-template-columns:1fr 1fr; } .candidate-filter-actions { justify-content:flex-end; } }
        @media (max-width:900px) { .candidate-col-phone, .candidate-phone-cell { display:none; } .candidate-table { min-width:880px; } }
        @media (max-width:760px) { .candidate-page-header { flex-direction:column; } .candidate-filters { grid-template-columns:1fr; } .candidate-filter-actions { justify-content:stretch; } .candidate-filter-actions .candidate-button { flex:1; } .candidate-table-footer { align-items:flex-start; flex-direction:column; } }
    </style>
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
            <div class="candidate-page">
                <header class="candidate-page-header">
                    <div>
                        <span class="candidate-eyebrow">Tuyển dụng</span>
                        <h1 class="candidate-page-title">Ứng viên</h1>
                        <p class="candidate-page-subtitle">Theo dõi hồ sơ trong toàn bộ quy trình tuyển dụng</p>
                    </div>
                </header>

                <form class="candidate-filters" action="${pageContext.request.contextPath}/candidates" method="get">
                    <div class="candidate-filter-field">
                        <label for="searchByName">Tên ứng viên</label>
                        <input type="search" id="searchByName" name="searchByName" placeholder="Tìm theo tên ứng viên" value="${fn:escapeXml(param.searchByName)}">
                    </div>
                    <div class="candidate-filter-field">
                        <label for="filterStatus">Trạng thái</label>
                        <select id="filterStatus" name="filterStatus">
                            <option value="">Tất cả trạng thái</option>
                            <option value="Applied" ${param.filterStatus eq 'Applied' ? 'selected' : ''}>Đã nộp</option>
                            <option value="Screening" ${param.filterStatus eq 'Screening' ? 'selected' : ''}>Sàng lọc</option>
                            <option value="Interview" ${param.filterStatus eq 'Interview' ? 'selected' : ''}>Phỏng vấn</option>
                            <option value="Offered" ${param.filterStatus eq 'Offered' ? 'selected' : ''}>Đã qua phỏng vấn</option>
                            <option value="Hired" ${param.filterStatus eq 'Hired' ? 'selected' : ''}>Đã nhận offer</option>
                            <option value="Rejected" ${param.filterStatus eq 'Rejected' ? 'selected' : ''}>Từ chối</option>
                        </select>
                    </div>
                    <div class="candidate-filter-field">
                        <label for="startDate">Từ ngày</label>
                        <input type="date" id="startDate" name="startDate" value="${fn:escapeXml(param.startDate)}">
                    </div>
                    <div class="candidate-filter-field">
                        <label for="endDate">Đến ngày</label>
                        <input type="date" id="endDate" name="endDate" value="${fn:escapeXml(param.endDate)}">
                    </div>
                    <div class="candidate-filter-actions">
                        <button type="submit" class="candidate-button candidate-filter-submit"><i class="fa-solid fa-filter" aria-hidden="true"></i> Lọc</button>
                        <a href="${pageContext.request.contextPath}/candidates" class="candidate-button candidate-filter-clear"><i class="fa-solid fa-rotate-left" aria-hidden="true"></i> Xóa lọc</a>
                    </div>
                </form>

                <section class="candidate-table-panel">
                    <div class="candidate-list-bar">
                        <h2>Danh sách hồ sơ</h2>
                        <span class="candidate-list-count">Trang ${currentPage} · ${applications != null ? fn:length(applications) : 0} hồ sơ</span>
                    </div>
                    <div class="candidate-table-scroll">
                        <c:choose>
                            <c:when test="${empty applications}">
                                <div class="candidate-empty"><i class="fa-regular fa-folder-open" aria-hidden="true"></i><div>Không có ứng viên phù hợp với bộ lọc hiện tại.</div></div>
                            </c:when>
                            <c:otherwise>
                                <table class="candidate-table">
                                    <thead>
                                        <tr>
                                            <th class="candidate-col-person">Ứng viên</th>
                                            <th class="candidate-col-phone">Điện thoại</th>
                                            <th class="candidate-col-date">Ngày ứng tuyển</th>
                                            <th class="candidate-col-job">Vị trí</th>
                                            <th class="candidate-col-status">Trạng thái</th>
                                            <th class="candidate-col-cv">CV</th>
                                            <th class="candidate-col-action">Thao tác</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="app" items="${applications}">
                                            <c:set var="candidateName" value="${not empty app.candidateProfile.fullName ? app.candidateProfile.fullName : app.guest.fullName}"/>
                                            <c:set var="candidateEmail" value="${not empty app.candidateProfile.email ? app.candidateProfile.email : app.guest.email}"/>
                                            <c:set var="candidatePhone" value="${not empty app.candidateProfile.phone ? app.candidateProfile.phone : app.guest.phone}"/>
                                            <c:set var="statusKey" value="${fn:toLowerCase(app.application.status)}"/>
                                            <c:set var="appliedDateText" value="${fn:replace(app.application.appliedDate, 'T', ' ')}"/>
                                            <tr>
                                                <td>
                                                    <div class="candidate-person">
                                                        <div class="candidate-person-main">
                                                            <span class="candidate-avatar"><c:out value="${fn:substring(candidateName, 0, 1)}"/></span>
                                                            <div class="candidate-person-copy">
                                                                <span class="candidate-name" title="${fn:escapeXml(candidateName)}"><c:out value="${candidateName}"/></span>
                                                                <span class="candidate-email" title="${fn:escapeXml(candidateEmail)}"><c:out value="${candidateEmail}"/></span>
                                                                <span class="candidate-id">#${app.application.applicationId}</span>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td class="candidate-phone-cell"><span class="candidate-phone"><c:out value="${candidatePhone}"/></span></td>
                                                <td>
                                                    <div class="candidate-date">
                                                        <c:choose>
                                                            <c:when test="${fn:length(appliedDateText) >= 16}">
                                                                ${fn:substring(appliedDateText, 8, 10)}/${fn:substring(appliedDateText, 5, 7)}/${fn:substring(appliedDateText, 0, 4)}<br>
                                                                ${fn:substring(appliedDateText, 11, 16)}
                                                            </c:when>
                                                            <c:otherwise><c:out value="${appliedDateText}"/></c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </td>
                                                <td><div class="candidate-job" title="${fn:escapeXml(app.jobTitle)}"><c:out value="${app.jobTitle}"/></div></td>
                                                <td>
                                                    <span class="candidate-status status-${not empty statusKey ? statusKey : 'default'}">
                                                        <c:choose>
                                                            <c:when test="${statusKey eq 'applied'}">Đã nộp</c:when>
                                                            <c:when test="${statusKey eq 'screening'}">Sàng lọc CV</c:when>
                                                            <c:when test="${statusKey eq 'interview' and app.application.currentStep eq 'Offer'}">Đã qua phỏng vấn</c:when>
                                                            <c:when test="${statusKey eq 'interview'}">Phỏng vấn</c:when>
                                                            <c:when test="${statusKey eq 'offered'}">Đã qua phỏng vấn</c:when>
                                                            <c:when test="${statusKey eq 'hired'}">Đã nhận offer</c:when>
                                                            <c:when test="${statusKey eq 'rejected'}">Từ chối</c:when>
                                                            <c:otherwise><c:out value="${app.application.status}"/></c:otherwise>
                                                        </c:choose>
                                                    </span>
                                                </td>
                                                <td class="candidate-col-cv">
                                                    <a class="candidate-cv-link" href="${pageContext.request.contextPath}/viewCV?applicationId=${app.application.applicationId}" title="Xem CV" aria-label="Xem CV của ${fn:escapeXml(candidateName)}"><i class="fa-regular fa-file-pdf" aria-hidden="true"></i></a>
                                                </td>
                                                <td class="candidate-col-action">
                                                    <c:choose>
                                                        <c:when test="${statusKey eq 'applied' or statusKey eq 'screening'}">
                                                            <a class="candidate-action-button" href="${pageContext.request.contextPath}/hrstaff/interviews/schedule?applicationId=${app.application.applicationId}"><i class="fa-regular fa-calendar-plus"></i> Đặt lịch</a>
                                                        </c:when>
                                                        <c:when test="${statusKey eq 'interview' and app.application.currentStep eq 'Offer'}">
                                                            <a class="candidate-action-button" href="${pageContext.request.contextPath}/hrstaff/offers/manage?applicationId=${app.application.applicationId}"><i class="fa-regular fa-file-lines"></i> Soạn offer</a>
                                                        </c:when>
                                                        <c:when test="${statusKey eq 'interview'}">
                                                            <a class="candidate-action-button" href="${pageContext.request.contextPath}/hrstaff/interviews/schedule?applicationId=${app.application.applicationId}"><i class="fa-regular fa-calendar"></i> Xem lịch</a>
                                                        </c:when>
                                                        <c:when test="${statusKey eq 'offered'}">
                                                            <a class="candidate-action-button" href="${pageContext.request.contextPath}/hrstaff/offers/manage?applicationId=${app.application.applicationId}"><i class="fa-regular fa-file-lines"></i> Xem offer</a>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <a class="candidate-action-button" href="${pageContext.request.contextPath}/viewCV?applicationId=${app.application.applicationId}"><i class="fa-regular fa-eye"></i> Chi tiết</a>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <c:if test="${not empty mess}"><div class="candidate-message error"><i class="fa-solid fa-circle-exclamation"></i><span>${mess}</span></div></c:if>
                    <c:if test="${not empty success}"><div class="candidate-message success"><i class="fa-solid fa-circle-check"></i><span>${success}</span></div></c:if>
                    <c:if test="${not empty warning}"><div class="candidate-message warning"><i class="fa-solid fa-triangle-exclamation"></i><span>${warning}</span></div></c:if>

                    <div class="candidate-table-footer">
                        <span class="candidate-page-summary">Hiển thị tối đa 10 hồ sơ trên mỗi trang</span>
                        <c:if test="${totalPages > 1}">
                            <nav class="candidate-pagination" aria-label="Phân trang ứng viên">
                                <c:choose>
                                    <c:when test="${currentPage > 1}">
                                        <c:url var="previousPageUrl" value="/candidates"><c:param name="page" value="${currentPage - 1}"/><c:param name="searchByName" value="${param.searchByName}"/><c:param name="filterStatus" value="${param.filterStatus}"/><c:param name="startDate" value="${param.startDate}"/><c:param name="endDate" value="${param.endDate}"/></c:url>
                                        <a class="candidate-page-item" href="${previousPageUrl}" aria-label="Trang trước"><i class="fa-solid fa-chevron-left"></i></a>
                                    </c:when>
                                    <c:otherwise><span class="candidate-page-item disabled"><i class="fa-solid fa-chevron-left"></i></span></c:otherwise>
                                </c:choose>
                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <c:choose>
                                        <c:when test="${currentPage == i}"><span class="candidate-page-item active">${i}</span></c:when>
                                        <c:otherwise>
                                            <c:url var="candidatePageUrl" value="/candidates"><c:param name="page" value="${i}"/><c:param name="searchByName" value="${param.searchByName}"/><c:param name="filterStatus" value="${param.filterStatus}"/><c:param name="startDate" value="${param.startDate}"/><c:param name="endDate" value="${param.endDate}"/></c:url>
                                            <a class="candidate-page-item" href="${candidatePageUrl}">${i}</a>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                                <c:choose>
                                    <c:when test="${currentPage < totalPages}">
                                        <c:url var="nextPageUrl" value="/candidates"><c:param name="page" value="${currentPage + 1}"/><c:param name="searchByName" value="${param.searchByName}"/><c:param name="filterStatus" value="${param.filterStatus}"/><c:param name="startDate" value="${param.startDate}"/><c:param name="endDate" value="${param.endDate}"/></c:url>
                                        <a class="candidate-page-item" href="${nextPageUrl}" aria-label="Trang sau"><i class="fa-solid fa-chevron-right"></i></a>
                                    </c:when>
                                    <c:otherwise><span class="candidate-page-item disabled"><i class="fa-solid fa-chevron-right"></i></span></c:otherwise>
                                </c:choose>
                            </nav>
                        </c:if>
                    </div>
                </section>
            </div>
        </section>
    </main>
</div>
<%@ include file="../AI/AI_Assistant_Widget.jspf" %>
<script charset="UTF-8" src="${pageContext.request.contextPath}/js/chatbot.js"></script>
</body>
</html>