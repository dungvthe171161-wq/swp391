<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết tin tuyển dụng - BetterHR</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/hr-theme.css?v=hr-staff-shell-20260630-1">
    <style>
        .recruitment-detail-page { max-width:1180px; margin:0 auto; }
        .recruitment-detail-header { display:flex; justify-content:space-between; align-items:flex-start; gap:24px; margin-bottom:24px; }
        .recruitment-detail-eyebrow { display:block; margin-bottom:7px; color:#00754a; font-size:13px; font-weight:800; text-transform:uppercase; }
        .recruitment-detail-title-row { display:flex; align-items:center; gap:12px; flex-wrap:wrap; }
        .recruitment-detail-title { margin:0; color:#00482f; font-size:30px; line-height:1.25; font-weight:800; }
        .recruitment-detail-subtitle { margin:8px 0 0; color:#66736d; font-size:14px; }
        .recruitment-detail-status { display:inline-flex; align-items:center; gap:7px; border-radius:999px; padding:7px 11px; font-size:11px; font-weight:850; text-transform:uppercase; }
        .recruitment-detail-status::before { content:''; width:6px; height:6px; border-radius:50%; background:currentColor; }
        .status-New { background:#eef1f0; color:#4d5b55; }
        .status-Waiting { background:#e8f0ff; color:#2458b8; }
        .status-Rejected { background:#fff0ee; color:#b23a30; }
        .status-Applied { background:#e3f7ec; color:#087a4d; }
        .status-Deleted { background:#f1f2f2; color:#747d78; }
        .recruitment-detail-back, .recruitment-detail-cancel, .recruitment-detail-save { min-height:42px; display:inline-flex; align-items:center; justify-content:center; gap:9px; border-radius:7px; padding:0 17px; font-size:14px; font-weight:800; text-decoration:none; cursor:pointer; white-space:nowrap; }
        .recruitment-detail-back, .recruitment-detail-cancel { border:1px solid #cfd8d3; background:#fff; color:#145c43; }
        .recruitment-detail-back:hover, .recruitment-detail-cancel:hover { border-color:#00754a; color:#00754a; background:#f4faf7; }
        .recruitment-detail-panel { border:1px solid #dce3df; border-radius:8px; background:#fff; box-shadow:0 8px 24px rgba(28,47,39,.06); overflow:hidden; }
        .recruitment-detail-panel-heading { display:flex; align-items:center; justify-content:space-between; gap:16px; padding:20px 26px; border-bottom:1px solid #e5ebe7; background:#f8faf8; }
        .recruitment-detail-panel-heading h2 { margin:0; color:#173f31; font-size:19px; font-weight:800; }
        .recruitment-detail-panel-heading span { color:#6b756f; font-size:13px; }
        .recruitment-detail-body { padding:26px; }
        .recruitment-detail-alert { display:flex; align-items:flex-start; gap:10px; margin-bottom:20px; border:1px solid #efc7c2; border-radius:7px; padding:12px 14px; background:#fff6f5; color:#a62f25; font-size:14px; font-weight:700; }
        .recruitment-detail-readonly { margin-bottom:20px; border:1px solid #d6ded9; border-radius:7px; padding:12px 14px; background:#f5f7f5; color:#5c6962; font-size:14px; }
        .recruitment-detail-grid { display:grid; grid-template-columns:minmax(0,1fr) 180px 250px; gap:20px; }
        .recruitment-detail-field { min-width:0; }
        .recruitment-detail-full { grid-column:1 / -1; }
        .recruitment-detail-field label { display:block; margin-bottom:8px; color:#304c40; font-size:14px; font-weight:800; }
        .recruitment-required { color:#c24135; }
        .recruitment-detail-field input, .recruitment-detail-field textarea { width:100%; border:1px solid #ccd6d0; border-radius:7px; background:#fff; color:#1d2d26; font:inherit; font-size:15px; padding:12px 14px; transition:border-color .16s ease, box-shadow .16s ease; }
        .recruitment-detail-field input { min-height:46px; }
        .recruitment-detail-field textarea { resize:vertical; line-height:1.55; }
        .recruitment-detail-field input::placeholder, .recruitment-detail-field textarea::placeholder { color:#8b9690; }
        .recruitment-detail-field input:focus, .recruitment-detail-field textarea:focus { outline:none; border-color:#00754a; box-shadow:0 0 0 3px rgba(0,117,74,.12); }
        .recruitment-detail-fieldset { min-width:0; margin:0; padding:0; border:0; }
        .recruitment-detail-fieldset:disabled { opacity:.72; }
        .recruitment-detail-suffix { display:grid; grid-template-columns:minmax(0,1fr) auto; }
        .recruitment-detail-suffix input { border-radius:7px 0 0 7px; }
        .recruitment-detail-suffix span { display:flex; align-items:center; border:1px solid #ccd6d0; border-left:0; border-radius:0 7px 7px 0; padding:0 14px; background:#f4f6f4; color:#59665f; font-size:13px; font-weight:800; }
        .recruitment-detail-actions { display:flex; justify-content:flex-end; gap:12px; margin-top:26px; padding-top:22px; border-top:1px solid #e5ebe7; }
        .recruitment-detail-cancel { color:#53635b; }
        .recruitment-detail-save { border:1px solid #00754a; background:#00754a; color:#fff; }
        .recruitment-detail-save:hover { border-color:#005f3c; background:#005f3c; }
        @media (max-width:900px) { .recruitment-detail-grid { grid-template-columns:1fr 1fr; } .recruitment-detail-location { grid-column:1 / -1; } }
        @media (max-width:640px) { .recruitment-detail-header { flex-direction:column; } .recruitment-detail-back { width:100%; } .recruitment-detail-panel-heading, .recruitment-detail-body { padding:20px; } .recruitment-detail-grid { grid-template-columns:1fr; } .recruitment-detail-field, .recruitment-detail-location { grid-column:1; } .recruitment-detail-actions { flex-direction:column-reverse; } .recruitment-detail-cancel, .recruitment-detail-save { width:100%; } }
    </style>
</head>
<body class="hr-staff-page-shell">
<%
    request.setAttribute("hrStaffSidebarActive", "recruitment");
    request.setAttribute("hrStaffPageTitle", "Chi tiết tin tuyển dụng");
    request.setAttribute("hrStaffSearchPlaceholder", "Tìm kiếm tin tuyển dụng...");
    request.setAttribute("hrStaffProfileSubtitle", "Quản trị tuyển dụng");
%>
<div class="staff-shell">
    <%@ include file="../HrStaff/_HrStaffSidebar.jspf" %>
    <main class="staff-main">
        <%@ include file="../HrStaff/_HrStaffTopbar.jspf" %>
        <section class="staff-content">
            <div class="recruitment-detail-page">
                <header class="recruitment-detail-header">
                    <div>
                        <span class="recruitment-detail-eyebrow">Tuyển dụng</span>
                        <div class="recruitment-detail-title-row">
                            <h1 class="recruitment-detail-title">Chi tiết tin tuyển dụng</h1>
                            <span class="recruitment-detail-status status-${rec.status}">
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
                        <p class="recruitment-detail-subtitle">Mã tin #${rec.recruitmentId} · Đăng ngày ${rec.postedDateDisplay}</p>
                    </div>
                    <a href="${pageContext.request.contextPath}/postRecruitments" class="recruitment-detail-back">
                        <i class="fa-solid fa-arrow-left" aria-hidden="true"></i>
                        Quay lại danh sách
                    </a>
                </header>

                <section class="recruitment-detail-panel">
                    <div class="recruitment-detail-panel-heading">
                        <h2>Thông tin vị trí</h2>
                        <span>Cập nhật lần lượt các thông tin cần thay đổi</span>
                    </div>
                    <div class="recruitment-detail-body">
                        <c:if test="${not empty mess}">
                            <div class="recruitment-detail-alert" role="alert">
                                <i class="fa-solid fa-circle-exclamation" aria-hidden="true"></i>
                                <span>${mess}</span>
                            </div>
                        </c:if>
                        <c:if test="${rec.status eq 'Deleted'}">
                            <div class="recruitment-detail-readonly">
                                Tin tuyển dụng đã xóa và chỉ có thể xem thông tin.
                            </div>
                        </c:if>
                        <form action="${pageContext.request.contextPath}/detailRecruitment" method="post">
                            <input type="hidden" name="id" value="${rec.recruitmentId}">
                            <input type="hidden" name="status" value="${rec.status}">
                            <fieldset class="recruitment-detail-fieldset" ${rec.status eq 'Deleted' ? 'disabled' : ''}>
                                <div class="recruitment-detail-grid">
                                    <div class="recruitment-detail-field recruitment-detail-full">
                                        <label for="titleInput">Tên vị trí <span class="recruitment-required">*</span></label>
                                        <input type="text" id="titleInput" name="Title" value="${fn:escapeXml(rec.title)}" placeholder="Tên vị trí tuyển dụng" maxlength="200" required>
                                    </div>
                                    <div class="recruitment-detail-field recruitment-detail-full">
                                        <label for="descriptionInput">Mô tả công việc <span class="recruitment-required">*</span></label>
                                        <textarea id="descriptionInput" name="Description" placeholder="Mô tả trách nhiệm và công việc chính" maxlength="1000" rows="6" required><c:out value="${rec.description}"/></textarea>
                                    </div>
                                    <div class="recruitment-detail-field recruitment-detail-full">
                                        <label for="requirementInput">Yêu cầu ứng viên <span class="recruitment-required">*</span></label>
                                        <textarea id="requirementInput" name="Requirement" placeholder="Kinh nghiệm, kỹ năng và trình độ cần thiết" maxlength="200" rows="3" required><c:out value="${rec.requirement}"/></textarea>
                                    </div>
                                    <div class="recruitment-detail-field recruitment-detail-location">
                                        <label for="locationInput">Địa điểm làm việc <span class="recruitment-required">*</span></label>
                                        <input type="text" id="locationInput" name="Location" value="${fn:escapeXml(rec.location)}" placeholder="Địa điểm làm việc" maxlength="200" required>
                                    </div>
                                    <div class="recruitment-detail-field">
                                        <label for="applicantInput">Số lượng tuyển <span class="recruitment-required">*</span></label>
                                        <input type="number" id="applicantInput" name="Applicant" value="${rec.applicant}" placeholder="1" min="1" step="1" required>
                                    </div>
                                    <div class="recruitment-detail-field">
                                        <label for="salaryInput">Mức lương <span class="recruitment-required">*</span></label>
                                        <div class="recruitment-detail-suffix">
                                            <input type="number" id="salaryInput" name="Salary" value="${rec.salaryInputValue}" placeholder="15000000" min="1" step="1000" required>
                                            <span>VNĐ</span>
                                        </div>
                                    </div>
                                </div>
                            </fieldset>
                            <div class="recruitment-detail-actions">
                                <a href="${pageContext.request.contextPath}/postRecruitments" class="recruitment-detail-cancel">Hủy</a>
                                <c:if test="${rec.status ne 'Deleted'}">
                                    <button type="submit" class="recruitment-detail-save">
                                        <i class="fa-regular fa-floppy-disk" aria-hidden="true"></i>
                                        Lưu thay đổi
                                    </button>
                                </c:if>
                            </div>
                        </form>
                    </div>
                </section>
            </div>
        </section>
    </main>
</div>
</body>
</html>