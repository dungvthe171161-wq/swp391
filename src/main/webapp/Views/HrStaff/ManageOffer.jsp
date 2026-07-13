<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý offer - BetterHR</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/hr-theme.css?v=offer-workflow-20260702">
    <style>
        body { margin: 0; font-family: "Segoe UI", Arial, sans-serif; background: #f3f4f6; color: #111827; }
        .page { max-width: 1040px; margin: 32px auto; padding: 0 20px; }
        .card { background: #fff; border: 1px solid #e5e7eb; border-radius: 14px; box-shadow: 0 8px 24px rgba(15,23,42,.08); overflow: hidden; margin-bottom: 18px; }
        .header { padding: 24px 28px; background: #006241; color: #fff; }
        .header h1 { margin: 0; font-size: 26px; }
        .header p { margin: 8px 0 0; opacity: .88; }
        .body { padding: 24px 28px; }
        .summary { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 12px; margin-bottom: 20px; }
        .summary div { padding: 14px; background: #f9fafb; border: 1px solid #e5e7eb; border-radius: 12px; }
        .summary span { display: block; color: #6b7280; font-size: 12px; font-weight: 800; text-transform: uppercase; margin-bottom: 4px; }
        .summary strong { color: #111827; overflow-wrap: anywhere; }
        .alert { margin-bottom: 16px; padding: 13px 15px; border-radius: 10px; font-weight: 700; }
        .alert.error { background: #fee2e2; color: #991b1b; }
        .alert.success { background: #dcfce7; color: #166534; }
        .alert.warning { background: #fef3c7; color: #92400e; }
        .form-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 16px; }
        .field { display: flex; flex-direction: column; gap: 8px; }
        .field.full { grid-column: 1 / -1; }
        label { font-weight: 700; color: #374151; }
        input, textarea { border: 1px solid #d1d5db; border-radius: 10px; padding: 11px 12px; font: inherit; }
        textarea { min-height: 110px; resize: vertical; }
        .status-line { display: flex; align-items: center; justify-content: space-between; gap: 12px; margin-bottom: 16px; padding: 13px 15px; border-radius: 12px; background: #f9fafb; border: 1px solid #e5e7eb; }
        .badge { display: inline-flex; align-items: center; border-radius: 999px; padding: 7px 12px; font-size: 13px; font-weight: 800; background: #e5e7eb; color: #374151; }
        .badge.sent { background: #dbeafe; color: #1d4ed8; }
        .badge.accepted { background: #dcfce7; color: #166534; }
        .badge.rejected { background: #fee2e2; color: #991b1b; }
        .actions { display: flex; gap: 10px; flex-wrap: wrap; justify-content: flex-end; margin-top: 20px; }
        .btn { display: inline-flex; align-items: center; justify-content: center; border: 0; border-radius: 999px; padding: 10px 16px; font-weight: 800; text-decoration: none; cursor: pointer; }
        .btn.primary { background: #00754a; color: #fff; }
        .btn.secondary { background: #e5e7eb; color: #111827; }
        .btn.gold { background: #b7791f; color: #fff; }
        .hint { color: #6b7280; font-size: 13px; margin: 6px 0 0; }
        @media (max-width: 820px) { .summary, .form-grid { grid-template-columns: 1fr; } .status-line { align-items: flex-start; flex-direction: column; } }
    </style>
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/chatbot.css">
</head>
<body>
<main class="page">
    <section class="card">
        <header class="header">
            <h1>Quản lý offer</h1>
            <p>Tạo offer, gửi email và thông báo cho ứng viên ngay trên hồ sơ tuyển dụng.</p>
        </header>
        <div class="body">
            <c:if test="${not empty error}"><div class="alert error">${error}</div></c:if>
            <c:if test="${param.saved eq '1'}"><div class="alert success">Đã lưu bản nháp offer.</div></c:if>
            <c:if test="${param.sent eq '1'}"><div class="alert success">Đã gửi offer và cập nhật hồ sơ sang Offered.</div></c:if>
            <c:if test="${param.mail eq 'failed'}"><div class="alert warning">Offer đã gửi nhưng email chưa gửi được. Vui lòng kiểm tra cấu hình mail.</div></c:if>

            <div class="summary">
                <div>
                    <span>Ứng viên</span>
                    <strong>
                        <c:choose>
                            <c:when test="${not empty applicationView.candidateProfile.fullName}">${applicationView.candidateProfile.fullName}</c:when>
                            <c:otherwise>${applicationView.guest.fullName}</c:otherwise>
                        </c:choose>
                    </strong>
                </div>
                <div>
                    <span>Email</span>
                    <strong>
                        <c:choose>
                            <c:when test="${not empty applicationView.candidateProfile.email}">${applicationView.candidateProfile.email}</c:when>
                            <c:otherwise>${applicationView.guest.email}</c:otherwise>
                        </c:choose>
                    </strong>
                </div>
                <div><span>Công việc</span><strong>${applicationView.jobTitle}</strong></div>
                <div><span>Application</span><strong>#${applicationView.application.applicationId}</strong></div>
            </div>

            <div class="status-line">
                <div>
                    <strong>Trạng thái offer</strong>
                    <p class="hint">Offer đã gửi hoặc đã có phản hồi sẽ không cho sửa lại nội dung.</p>
                </div>
                <c:choose>
                    <c:when test="${offer == null}">
                        <span class="badge">Chưa tạo</span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge ${offer.status == 'Sent' ? 'sent' : (offer.status == 'Accepted' ? 'accepted' : (offer.status == 'Rejected' ? 'rejected' : ''))}">
                            ${offer.statusLabel}
                        </span>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </section>

    <section class="card">
        <div class="body">
            <form method="post" action="${pageContext.request.contextPath}/hrstaff/offers/manage">
                <input type="hidden" name="applicationId" value="${applicationView.application.applicationId}">
                <div class="form-grid">
                    <div class="field">
                        <label for="position">Vị trí offer</label>
                        <input type="text" id="position" name="position"
                               value="${offer != null ? offer.position : (empty param.position ? applicationView.jobTitle : param.position)}" required>
                    </div>
                    <div class="field">
                        <label for="offeredSalary">Lương đề xuất</label>
                        <input type="number" id="offeredSalary" name="offeredSalary" min="0" step="100000"
                               value="${offer != null ? offer.offeredSalary : param.offeredSalary}" placeholder="VD: 15000000">
                    </div>
                    <div class="field">
                        <label for="startDate">Ngày bắt đầu dự kiến</label>
                        <input type="date" id="startDate" name="startDate"
                               value="${offer != null ? offer.startDateInputValue : param.startDate}">
                    </div>
                    <div class="field">
                        <label for="expiredAt">Hạn phản hồi offer</label>
                        <input type="datetime-local" id="expiredAt" name="expiredAt"
                               value="${offer != null ? offer.expiredAtInputValue : param.expiredAt}">
                    </div>
                    <div class="field full">
                        <label for="note">Ghi chú offer</label>
                        <textarea id="note" name="note" placeholder="Thông tin phúc lợi, onboarding, tài liệu cần chuẩn bị...">${offer != null ? offer.note : param.note}</textarea>
                    </div>
                </div>

                <div class="actions">
                    <a class="btn secondary" href="${pageContext.request.contextPath}/candidates">Quay lại danh sách</a>
                    <a class="btn secondary" href="${pageContext.request.contextPath}/hrstaff/interviews/schedule?applicationId=${applicationView.application.applicationId}">Lịch phỏng vấn</a>
                    <c:if test="${offer == null || offer.status == 'Draft'}">
                        <button class="btn secondary" type="submit" name="action" value="saveDraft">Lưu nháp</button>
                        <button class="btn primary" type="submit" name="action" value="sendOffer">Gửi offer</button>
                    </c:if>
                    <c:if test="${offer != null && offer.status != 'Draft'}">
                        <span class="btn gold">Offer đã khóa theo trạng thái</span>
                    </c:if>
                </div>
            </form>
        </div>
    </section>
</main>
    <%@ include file="../AI/AI_Assistant_Widget.jspf" %>
    <script charset="UTF-8" src="${pageContext.request.contextPath}/js/chatbot.js"></script>
</body>
</html>
