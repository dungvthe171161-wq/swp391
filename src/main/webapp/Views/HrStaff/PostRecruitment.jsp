<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tin tuyển dụng - BetterHR</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/hr-theme.css?v=hr-staff-shell-20260630-1">
    <style>
        .recruitment-page { max-width:1400px; margin:0 auto; }
        .recruitment-page-header { display:flex; justify-content:space-between; align-items:flex-start; gap:24px; margin-bottom:22px; }
        .recruitment-eyebrow { display:block; margin-bottom:7px; color:#00754a; font-size:13px; font-weight:800; text-transform:uppercase; }
        .recruitment-title { margin:0; color:#00482f; font-size:30px; line-height:1.25; font-weight:800; }
        .recruitment-subtitle { margin:8px 0 0; color:#66736d; font-size:15px; }
        .recruitment-primary-action, .recruitment-button { min-height:40px; display:inline-flex; align-items:center; justify-content:center; gap:8px; border-radius:7px; padding:0 15px; border:1px solid transparent; font-size:14px; font-weight:800; text-decoration:none; cursor:pointer; white-space:nowrap; }
        .recruitment-primary-action { min-height:44px; padding:0 18px; background:#00754a; color:#fff; }
        .recruitment-primary-action:hover, .recruitment-button-primary:hover { background:#005f3c; color:#fff; }
        .recruitment-alert { display:flex; align-items:center; gap:10px; margin-bottom:18px; border:1px solid #b9decf; border-radius:7px; padding:12px 14px; background:#effaf5; color:#136342; font-size:14px; font-weight:700; }
        .recruitment-filters { display:grid; grid-template-columns:minmax(240px,1.5fr) 210px 170px 170px auto; gap:14px; align-items:end; margin-bottom:24px; border:1px solid #dce3df; border-radius:8px; padding:18px; background:#fff; }
        .recruitment-filter-field label { display:block; margin-bottom:7px; color:#526159; font-size:12px; font-weight:800; text-transform:uppercase; }
        .recruitment-filter-field input, .recruitment-filter-field select { width:100%; min-height:42px; border:1px solid #ccd6d0; border-radius:7px; background:#fff; color:#24372f; padding:9px 12px; font:inherit; font-size:14px; }
        .recruitment-filter-field input:focus, .recruitment-filter-field select:focus { outline:none; border-color:#00754a; box-shadow:0 0 0 3px rgba(0,117,74,.1); }
        .recruitment-filter-actions { display:flex; gap:8px; }
        .recruitment-filter-submit { background:#173f31; color:#fff; }
        .recruitment-filter-submit:hover { background:#0d3024; color:#fff; }
        .recruitment-filter-clear { border-color:#ccd6d0; background:#fff; color:#4d5e55; }
        .recruitment-filter-clear:hover { border-color:#00754a; color:#00754a; background:#f4faf7; }
        .recruitment-list-heading { display:flex; align-items:center; justify-content:space-between; gap:16px; margin-bottom:12px; }
        .recruitment-list-heading h2 { margin:0; color:#173f31; font-size:19px; font-weight:800; }
        .recruitment-count { color:#6a766f; font-size:14px; font-weight:700; }
        .recruitment-list { display:flex; flex-direction:column; gap:10px; }
        .recruitment-item { display:grid; grid-template-columns:minmax(0,1fr) auto; gap:24px; align-items:center; border:1px solid #dce3df; border-radius:8px; padding:18px 20px; background:#fff; transition:border-color .16s ease, box-shadow .16s ease; }
        .recruitment-item:hover { border-color:#a9c5b8; box-shadow:0 7px 20px rgba(24,54,42,.07); }
        .recruitment-item-top { display:flex; align-items:center; gap:12px; flex-wrap:wrap; margin-bottom:13px; }
        .recruitment-item-title { margin:0; color:#00482f; font-size:18px; line-height:1.35; font-weight:850; }
        .recruitment-status { display:inline-flex; align-items:center; gap:6px; border-radius:999px; padding:6px 10px; font-size:11px; font-weight:850; text-transform:uppercase; }
        .recruitment-status::before { content:''; width:6px; height:6px; border-radius:50%; background:currentColor; }
        .status-New { background:#eef1f0; color:#4d5b55; }
        .status-Waiting { background:#e8f0ff; color:#2458b8; }
        .status-Rejected { background:#fff0ee; color:#b23a30; }
        .status-Applied { background:#e3f7ec; color:#087a4d; }
        .status-Deleted { background:#f1f2f2; color:#747d78; }
        .recruitment-meta { display:grid; grid-template-columns:repeat(4,minmax(135px,auto)); gap:12px 24px; }
        .recruitment-meta-item { display:grid; grid-template-columns:18px auto; column-gap:8px; align-items:start; min-width:0; }
        .recruitment-meta-item i { margin-top:2px; color:#6c8076; font-size:14px; text-align:center; }
        .recruitment-meta-label { display:block; color:#7a857f; font-size:11px; font-weight:750; text-transform:uppercase; }
        .recruitment-meta-value { display:block; margin-top:2px; color:#34473e; font-size:13px; font-weight:750; overflow-wrap:anywhere; }
        .recruitment-actions { display:flex; align-items:center; justify-content:flex-end; gap:8px; flex-wrap:wrap; max-width:320px; }
        .recruitment-button-detail { border-color:#b8cac1; background:#fff; color:#145c43; }
        .recruitment-button-detail:hover { border-color:#00754a; background:#f3faf6; color:#00754a; }
        .recruitment-button-primary { background:#00754a; color:#fff; }
        .recruitment-button-danger { border-color:#efc7c2; background:#fff; color:#ad362c; }
        .recruitment-button-danger:hover { background:#fff4f2; border-color:#d77a71; color:#922a22; }
        .recruitment-empty { border:1px dashed #becdc5; border-radius:8px; padding:52px 24px; text-align:center; background:#fbfcfb; }
        .recruitment-empty i { color:#759086; font-size:32px; }
        .recruitment-empty h3 { margin:14px 0 5px; color:#24473a; font-size:18px; }
        .recruitment-empty p { margin:0 0 18px; color:#738078; font-size:14px; }
        .recruitment-pagination { display:flex; justify-content:center; align-items:center; gap:7px; margin-top:22px; }
        .recruitment-page-item { min-width:38px; height:38px; display:inline-flex; align-items:center; justify-content:center; border:1px solid #ccd6d0; border-radius:7px; background:#fff; color:#34473e; font-size:14px; font-weight:800; text-decoration:none; }
        .recruitment-page-item:hover { border-color:#00754a; color:#00754a; }
        .recruitment-page-item.active { border-color:#00754a; background:#00754a; color:#fff; }
        .recruitment-page-item.disabled { opacity:.4; pointer-events:none; }
        @media (max-width:1180px) { .recruitment-filters { grid-template-columns:1fr 1fr; } .recruitment-filter-actions { justify-content:flex-end; } .recruitment-meta { grid-template-columns:repeat(2,minmax(150px,1fr)); } }
        @media (max-width:780px) { .recruitment-page-header { flex-direction:column; } .recruitment-primary-action { width:100%; } .recruitment-filters { grid-template-columns:1fr; } .recruitment-filter-actions { justify-content:stretch; } .recruitment-filter-actions .recruitment-button { flex:1; } .recruitment-item { grid-template-columns:1fr; } .recruitment-actions { max-width:none; justify-content:flex-start; } }
        @media (max-width:520px) { .recruitment-meta { grid-template-columns:1fr; } .recruitment-actions .recruitment-button { width:100%; } .recruitment-list-heading { align-items:flex-start; flex-direction:column; } }
    </style>
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
            <div class="recruitment-page">
                <header class="recruitment-page-header">
                    <div>
                        <span class="recruitment-eyebrow">Tuyển dụng</span>
                        <h1 class="recruitment-title">Tin tuyển dụng</h1>
                        <p class="recruitment-subtitle">Quản lý nhu cầu tuyển dụng và trạng thái phê duyệt</p>
                    </div>
                    <a class="recruitment-primary-action" href="${pageContext.request.contextPath}/detailRecruitmentCreate">
                        <i class="fa-solid fa-plus" aria-hidden="true"></i>
                        Tạo tin tuyển dụng
                    </a>
                </header>

                <c:if test="${not empty mess}">
                    <div class="recruitment-alert" role="status">
                        <i class="fa-solid fa-circle-check" aria-hidden="true"></i>
                        <span>${mess}</span>
                    </div>
                </c:if>

                <form class="recruitment-filters" method="get" action="${pageContext.request.contextPath}/postRecruitments">
                    <div class="recruitment-filter-field">
                        <label for="searchByTitle">Tên vị trí</label>
                        <input id="searchByTitle" type="search" name="searchByTitle" value="${fn:escapeXml(searchByTitle)}" placeholder="Tìm theo tên vị trí">
                    </div>
                    <div class="recruitment-filter-field">
                        <label for="filterStatus">Trạng thái</label>
                        <select id="filterStatus" name="filterStatus">
                            <option value="">Tất cả trạng thái</option>
                            <option value="New" ${filterStatus eq 'New' ? 'selected' : ''}>Mới</option>
                            <option value="Waiting" ${filterStatus eq 'Waiting' ? 'selected' : ''}>Chờ duyệt</option>
                            <option value="Applied" ${filterStatus eq 'Applied' ? 'selected' : ''}>Đang tuyển</option>
                            <option value="Rejected" ${filterStatus eq 'Rejected' ? 'selected' : ''}>Bị từ chối</option>
                            <option value="Deleted" ${filterStatus eq 'Deleted' ? 'selected' : ''}>Đã xóa</option>
                        </select>
                    </div>
                    <div class="recruitment-filter-field">
                        <label for="startDate">Từ ngày</label>
                        <input id="startDate" type="date" name="startDate" value="${fn:escapeXml(startDate)}">
                    </div>
                    <div class="recruitment-filter-field">
                        <label for="endDate">Đến ngày</label>
                        <input id="endDate" type="date" name="endDate" value="${fn:escapeXml(endDate)}">
                    </div>
                    <div class="recruitment-filter-actions">
                        <button type="submit" class="recruitment-button recruitment-filter-submit">
                            <i class="fa-solid fa-filter" aria-hidden="true"></i> Lọc
                        </button>
                        <a class="recruitment-button recruitment-filter-clear" href="${pageContext.request.contextPath}/postRecruitments" title="Xóa bộ lọc">
                            <i class="fa-solid fa-rotate-left" aria-hidden="true"></i>
                            <span>Xóa lọc</span>
                        </a>
                    </div>
                </form>

                <div class="recruitment-list-heading">
                    <h2>Danh sách vị trí</h2>
                    <span class="recruitment-count">${totalRecruitment} tin tuyển dụng</span>
                </div>

                <c:choose>
                    <c:when test="${empty recruitment}">
                        <div class="recruitment-empty">
                            <i class="fa-regular fa-folder-open" aria-hidden="true"></i>
                            <h3>Không có tin tuyển dụng phù hợp</h3>
                            <p>Thử thay đổi bộ lọc hoặc tạo một tin tuyển dụng mới.</p>
                            <a class="recruitment-primary-action" href="${pageContext.request.contextPath}/detailRecruitmentCreate">Tạo tin tuyển dụng</a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="recruitment-list">
                            <c:forEach var="rec" items="${recruitment}">
                                <article class="recruitment-item">
                                    <div>
                                        <div class="recruitment-item-top">
                                            <h3 class="recruitment-item-title">${rec.title}</h3>
                                            <span class="recruitment-status status-${rec.status}">
                                                <c:choose>
                                                    <c:when test="${rec.status eq 'New'}">Mới</c:when>
                                                    <c:when test="${rec.status eq 'Waiting'}">Chờ duyệt</c:when>
                                                    <c:when test="${rec.status eq 'Rejected'}">Bị từ chối</c:when>
                                                    <c:when test="${rec.status eq 'Applied'}">Đang tuyển</c:when>
                                                    <c:when test="${rec.status eq 'Deleted'}">Đã xóa</c:when>
                                                    <c:otherwise>${rec.status}</c:otherwise>
                                                </c:choose>
                                            </span>
                                        </div>
                                        <div class="recruitment-meta">
                                            <div class="recruitment-meta-item">
                                                <i class="fa-regular fa-calendar" aria-hidden="true"></i>
                                                <div><span class="recruitment-meta-label">Ngày đăng</span><span class="recruitment-meta-value">${rec.postedDateDisplay}</span></div>
                                            </div>
                                            <div class="recruitment-meta-item">
                                                <i class="fa-solid fa-users" aria-hidden="true"></i>
                                                <div><span class="recruitment-meta-label">Số lượng</span><span class="recruitment-meta-value">${rec.applicant} người</span></div>
                                            </div>
                                            <div class="recruitment-meta-item">
                                                <i class="fa-solid fa-location-dot" aria-hidden="true"></i>
                                                <div><span class="recruitment-meta-label">Địa điểm</span><span class="recruitment-meta-value">${rec.location}</span></div>
                                            </div>
                                            <div class="recruitment-meta-item">
                                                <i class="fa-solid fa-money-bill-wave" aria-hidden="true"></i>
                                                <div><span class="recruitment-meta-label">Mức lương</span><span class="recruitment-meta-value"><fmt:formatNumber value="${rec.salary}" maxFractionDigits="0"/> VNĐ</span></div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="recruitment-actions">
                                        <a class="recruitment-button recruitment-button-detail" href="${pageContext.request.contextPath}/detailRecruitment?id=${rec.recruitmentId}">
                                            <i class="fa-regular fa-eye" aria-hidden="true"></i> Chi tiết
                                        </a>
                                        <c:if test="${rec.status ne 'Deleted' and rec.status ne 'Waiting' and rec.status ne 'Applied'}">
                                            <a class="recruitment-button recruitment-button-primary" href="${pageContext.request.contextPath}/postRecruitments?action=send&id=${rec.recruitmentId}" onclick="return confirm('Bạn có chắc muốn gửi tin tuyển dụng này để duyệt không?');">
                                                <i class="fa-regular fa-paper-plane" aria-hidden="true"></i> Gửi duyệt
                                            </a>
                                            <a class="recruitment-button recruitment-button-danger" href="${pageContext.request.contextPath}/postRecruitments?action=delete&id=${rec.recruitmentId}" onclick="return confirm('Bạn có chắc muốn xóa tin tuyển dụng này không?');">
                                                <i class="fa-regular fa-trash-can" aria-hidden="true"></i> Xóa
                                            </a>
                                        </c:if>
                                    </div>
                                </article>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>

                <c:if test="${totalPages > 1}">
                    <nav class="recruitment-pagination" aria-label="Phân trang tin tuyển dụng">
                        <c:choose>
                            <c:when test="${currentPage > 1}">
                                <c:url var="previousPageUrl" value="/postRecruitments">
                                    <c:param name="page" value="${currentPage - 1}"/><c:param name="searchByTitle" value="${searchByTitle}"/><c:param name="filterStatus" value="${filterStatus}"/><c:param name="startDate" value="${startDate}"/><c:param name="endDate" value="${endDate}"/>
                                </c:url>
                                <a class="recruitment-page-item" href="${previousPageUrl}" aria-label="Trang trước"><i class="fa-solid fa-chevron-left"></i></a>
                            </c:when>
                            <c:otherwise><span class="recruitment-page-item disabled"><i class="fa-solid fa-chevron-left"></i></span></c:otherwise>
                        </c:choose>
                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <c:choose>
                                <c:when test="${currentPage == i}"><span class="recruitment-page-item active">${i}</span></c:when>
                                <c:otherwise>
                                    <c:url var="pageUrl" value="/postRecruitments">
                                        <c:param name="page" value="${i}"/><c:param name="searchByTitle" value="${searchByTitle}"/><c:param name="filterStatus" value="${filterStatus}"/><c:param name="startDate" value="${startDate}"/><c:param name="endDate" value="${endDate}"/>
                                    </c:url>
                                    <a class="recruitment-page-item" href="${pageUrl}">${i}</a>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                        <c:choose>
                            <c:when test="${currentPage < totalPages}">
                                <c:url var="nextPageUrl" value="/postRecruitments">
                                    <c:param name="page" value="${currentPage + 1}"/><c:param name="searchByTitle" value="${searchByTitle}"/><c:param name="filterStatus" value="${filterStatus}"/><c:param name="startDate" value="${startDate}"/><c:param name="endDate" value="${endDate}"/>
                                </c:url>
                                <a class="recruitment-page-item" href="${nextPageUrl}" aria-label="Trang sau"><i class="fa-solid fa-chevron-right"></i></a>
                            </c:when>
                            <c:otherwise><span class="recruitment-page-item disabled"><i class="fa-solid fa-chevron-right"></i></span></c:otherwise>
                        </c:choose>
                    </nav>
                </c:if>
            </div>
        </section>
    </main>
</div>
</body>
</html>