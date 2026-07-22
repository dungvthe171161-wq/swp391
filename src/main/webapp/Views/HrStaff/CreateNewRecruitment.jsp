<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tạo tin tuyển dụng - BetterHR</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/hr-theme.css?v=hr-staff-shell-20260630-1">
    <style>
        .job-create-page { max-width: 1180px; margin: 0 auto; }
        .job-create-header { display:flex; align-items:flex-start; justify-content:space-between; gap:24px; margin-bottom:24px; }
        .job-create-eyebrow { display:block; margin-bottom:7px; color:#00754a; font-size:13px; font-weight:800; text-transform:uppercase; }
        .job-create-title { margin:0; color:#00482f; font-size:30px; line-height:1.25; font-weight:800; }
        .job-create-subtitle { margin:8px 0 0; color:#66736d; font-size:15px; }
        .job-back-link, .job-submit-button, .job-cancel-link { min-height:42px; display:inline-flex; align-items:center; justify-content:center; gap:9px; border-radius:7px; padding:0 17px; font-size:14px; font-weight:800; text-decoration:none; cursor:pointer; }
        .job-back-link { border:1px solid #cfd8d3; color:#145c43; background:#fff; white-space:nowrap; }
        .job-back-link:hover, .job-cancel-link:hover { border-color:#00754a; color:#00754a; background:#f4faf7; }
        .job-form-panel { border:1px solid #dce3df; border-radius:8px; background:#fff; box-shadow:0 8px 24px rgba(28,47,39,.06); overflow:hidden; }
        .job-form-heading { padding:22px 26px; border-bottom:1px solid #e5ebe7; background:#f8faf8; }
        .job-form-heading h2 { margin:0; color:#173f31; font-size:19px; font-weight:800; }
        .job-form-heading p { margin:5px 0 0; color:#6b756f; font-size:14px; }
        .job-form-body { padding:26px; }
        .job-form-grid { display:grid; grid-template-columns:minmax(0,1fr) 180px 250px; gap:20px; }
        .job-field { min-width:0; }
        .job-field-full { grid-column:1 / -1; }
        .job-field label { display:block; margin-bottom:8px; color:#304c40; font-size:14px; font-weight:800; }
        .job-required { color:#c24135; }
        .job-field input, .job-field textarea { width:100%; border:1px solid #ccd6d0; border-radius:7px; background:#fff; color:#1d2d26; font:inherit; font-size:15px; padding:12px 14px; transition:border-color .16s ease, box-shadow .16s ease; }
        .job-field input { min-height:46px; }
        .job-field textarea { resize:vertical; line-height:1.55; }
        .job-field input::placeholder, .job-field textarea::placeholder { color:#8b9690; }
        .job-field input:focus, .job-field textarea:focus { outline:none; border-color:#00754a; box-shadow:0 0 0 3px rgba(0,117,74,.12); }
        .job-input-suffix { display:grid; grid-template-columns:minmax(0,1fr) auto; }
        .job-input-suffix input { border-radius:7px 0 0 7px; }
        .job-input-suffix span { display:flex; align-items:center; border:1px solid #ccd6d0; border-left:0; border-radius:0 7px 7px 0; padding:0 14px; background:#f4f6f4; color:#59665f; font-size:13px; font-weight:800; }
        .job-error { display:flex; align-items:flex-start; gap:10px; margin-bottom:20px; border:1px solid #efc7c2; border-radius:7px; padding:12px 14px; background:#fff6f5; color:#a62f25; font-size:14px; font-weight:700; }
        .job-form-actions { display:flex; justify-content:flex-end; gap:12px; margin-top:26px; padding-top:22px; border-top:1px solid #e5ebe7; }
        .job-cancel-link { border:1px solid #cfd8d3; color:#53635b; background:#fff; }
        .job-submit-button { border:1px solid #00754a; background:#00754a; color:#fff; }
        .job-submit-button:hover { background:#005f3c; border-color:#005f3c; }
        @media (max-width:900px) { .job-form-grid { grid-template-columns:1fr 1fr; } .job-field-location { grid-column:1 / -1; } }
        @media (max-width:640px) { .job-create-header { flex-direction:column; } .job-back-link { width:100%; } .job-form-body, .job-form-heading { padding:20px; } .job-form-grid { grid-template-columns:1fr; } .job-field, .job-field-location { grid-column:1; } .job-form-actions { flex-direction:column-reverse; } .job-cancel-link, .job-submit-button { width:100%; } }
    </style>
</head>
<body class="hr-staff-page-shell">
<%
    request.setAttribute("hrStaffSidebarActive", "recruitment");
    request.setAttribute("hrStaffPageTitle", "Tạo tin tuyển dụng");
    request.setAttribute("hrStaffSearchPlaceholder", "Tìm kiếm tin tuyển dụng...");
    request.setAttribute("hrStaffProfileSubtitle", "Quản trị tuyển dụng");
%>
<div class="staff-shell">
    <%@ include file="_HrStaffSidebar.jspf" %>
    <main class="staff-main">
        <%@ include file="_HrStaffTopbar.jspf" %>
        <section class="staff-content">
            <div class="job-create-page">
                <header class="job-create-header">
                    <div>
                        <span class="job-create-eyebrow">Tuyển dụng</span>
                        <h1 class="job-create-title">Tạo tin tuyển dụng</h1>
                        <p class="job-create-subtitle">Thông tin vị trí và nhu cầu tuyển dụng</p>
                    </div>
                    <a href="${pageContext.request.contextPath}/postRecruitments" class="job-back-link">
                        <i class="fa-solid fa-arrow-left" aria-hidden="true"></i>
                        Quay lại danh sách
                    </a>
                </header>

                <section class="job-form-panel">
                    <div class="job-form-heading">
                        <h2>Thông tin tuyển dụng</h2>
                        <p>Các trường có dấu <span class="job-required">*</span> là bắt buộc.</p>
                    </div>
                    <div class="job-form-body">
                        <c:if test="${not empty mess}">
                            <div class="job-error" role="alert">
                                <i class="fa-solid fa-circle-exclamation" aria-hidden="true"></i>
                                <span>${mess}</span>
                            </div>
                        </c:if>
                        <form action="${pageContext.request.contextPath}/detailRecruitmentCreate" method="post">
                            <input type="hidden" name="id" value="">
                            <div class="job-form-grid">
                                <div class="job-field job-field-full">
                                    <label for="titleInput">Tên vị trí <span class="job-required">*</span></label>
                                    <input type="text" id="titleInput" name="Title" value="${title}" placeholder="Ví dụ: Chuyên viên tuyển dụng" maxlength="200" required autofocus>
                                </div>
                                <div class="job-field job-field-full">
                                    <label for="descriptionInput">Mô tả công việc <span class="job-required">*</span></label>
                                    <textarea id="descriptionInput" name="Description" placeholder="Mô tả trách nhiệm, công việc chính và môi trường làm việc" maxlength="1000" rows="6" required>${description}</textarea>
                                </div>
                                <div class="job-field job-field-full">
                                    <label for="requirementInput">Yêu cầu ứng viên <span class="job-required">*</span></label>
                                    <textarea id="requirementInput" name="Requirement" placeholder="Kinh nghiệm, kỹ năng và trình độ cần thiết" maxlength="200" rows="3" required>${requirement}</textarea>
                                </div>
                                <div class="job-field job-field-location">
                                    <label for="locationInput">Địa điểm làm việc <span class="job-required">*</span></label>
                                    <input type="text" id="locationInput" name="Location" value="${location}" placeholder="Ví dụ: Hà Nội" maxlength="200" required>
                                </div>
                                <div class="job-field">
                                    <label for="applicantInput">Số lượng tuyển <span class="job-required">*</span></label>
                                    <input type="number" id="applicantInput" name="Applicant" value="${applicant}" placeholder="1" min="1" step="1" required>
                                </div>
                                <div class="job-field">
                                    <label for="salaryInput">Mức lương <span class="job-required">*</span></label>
                                    <div class="job-input-suffix">
                                        <input type="number" id="salaryInput" name="Salary" value="${salary}" placeholder="15000000" min="1" step="1000" required>
                                        <span>VNĐ</span>
                                    </div>
                                </div>
                            </div>
                            <div class="job-form-actions">
                                <a href="${pageContext.request.contextPath}/postRecruitments" class="job-cancel-link">Hủy</a>
                                <button type="submit" class="job-submit-button">
                                    <i class="fa-solid fa-plus" aria-hidden="true"></i>
                                    Tạo tin tuyển dụng
                                </button>
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