<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý phỏng vấn - BetterHR</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/hr-theme.css?v=interview-workflow-20260702">
    <style>
        body { margin: 0; font-family: "Segoe UI", Arial, sans-serif; background: #f3f4f6; color: #111827; }
        .page { max-width: 1120px; margin: 32px auto; padding: 0 20px; }
        .card { background: #fff; border: 1px solid #e5e7eb; border-radius: 14px; box-shadow: 0 8px 24px rgba(15,23,42,.08); overflow: hidden; margin-bottom: 18px; }
        .header { padding: 24px 28px; background: #006241; color: #fff; }
        .header h1 { margin: 0; font-size: 26px; }
        .header p { margin: 8px 0 0; opacity: .86; }
        .body { padding: 24px 28px; }
        .summary { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 12px; margin-bottom: 20px; }
        .summary div { padding: 14px; background: #f9fafb; border: 1px solid #e5e7eb; border-radius: 12px; }
        .summary span { display: block; color: #6b7280; font-size: 12px; font-weight: 700; text-transform: uppercase; margin-bottom: 4px; }
        .summary strong { color: #111827; }
        .alert { margin-bottom: 16px; padding: 13px 15px; border-radius: 10px; font-weight: 700; }
        .alert.error { background: #fee2e2; color: #991b1b; }
        .alert.success { background: #dcfce7; color: #166534; }
        .alert.warning { background: #fef3c7; color: #92400e; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 12px; border-bottom: 1px solid #e5e7eb; text-align: left; font-size: 14px; vertical-align: top; }
        th { background: #f9fafb; color: #374151; text-transform: uppercase; font-size: 12px; }
        .form-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 16px; }
        .field { display: flex; flex-direction: column; gap: 8px; }
        .field.full { grid-column: 1 / -1; }
        label { font-weight: 700; color: #374151; }
        input, select, textarea { border: 1px solid #d1d5db; border-radius: 10px; padding: 11px 12px; font: inherit; }
        textarea { min-height: 92px; resize: vertical; }
        .actions { display: flex; gap: 10px; flex-wrap: wrap; justify-content: flex-end; margin-top: 20px; }
        .btn { display: inline-flex; align-items: center; justify-content: center; border: 0; border-radius: 999px; padding: 10px 16px; font-weight: 800; text-decoration: none; cursor: pointer; }
        .btn.primary { background: #00754a; color: #fff; }
        .btn.secondary { background: #e5e7eb; color: #111827; }
        .btn.danger { background: #dc2626; color: #fff; }
        .btn.link { background: transparent; color: #006241; padding: 0; }
        .section-title { margin: 0 0 14px; font-size: 20px; }
        @media (max-width: 820px) { .summary, .form-grid { grid-template-columns: 1fr; } }
    </style>
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/chatbot.css">
</head>
<body>
<main class="page">
    <section class="card">
        <header class="header">
            <h1>Quản lý phỏng vấn</h1>
            <p>Đặt lịch, đổi lịch, hủy lịch và cập nhật kết quả phỏng vấn theo từng Application.</p>
        </header>
        <div class="body">
            <c:if test="${not empty error}"><div class="alert error">${error}</div></c:if>
            <c:if test="${param.interviewScheduled eq '1'}"><div class="alert success">Đã đặt lịch phỏng vấn.</div></c:if>
            <c:if test="${param.interviewUpdated eq '1'}"><div class="alert success">Đã cập nhật lịch phỏng vấn.</div></c:if>
            <c:if test="${param.interviewResult eq '1'}"><div class="alert success">Đã cập nhật kết quả phỏng vấn.</div></c:if>
            <c:if test="${param.interviewCancelled eq '1'}"><div class="alert success">Đã hủy lịch phỏng vấn.</div></c:if>
            <c:if test="${param.mail eq 'failed'}"><div class="alert warning">Thao tác đã lưu nhưng email chưa gửi được. Vui lòng kiểm tra cấu hình mail.</div></c:if>

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
                <div><span>Vị trí</span><strong>${applicationView.jobTitle}</strong></div>
                <div><span>Application</span><strong>#${applicationView.application.applicationId}</strong></div>
            </div>
        </div>
    </section>

    <section class="card">
        <div class="body">
            <h2 class="section-title">Các lịch phỏng vấn</h2>
            <c:choose>
                <c:when test="${empty interviews}">
                    <p>Chưa có lịch phỏng vấn nào cho hồ sơ này.</p>
                </c:when>
                <c:otherwise>
                    <table>
                        <thead>
                        <tr>
                            <th>Thời gian</th>
                            <th>Địa điểm/Link</th>
                            <th>Trạng thái</th>
                            <th>Kết quả</th>
                            <th>Thao tác</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="item" items="${interviews}">
                            <tr>
                                <td>${item.scheduledAt}</td>
                                <td>
                                    <c:if test="${not empty item.location}">${item.location}<br></c:if>
                                    <c:if test="${not empty item.meetingLink}">
                                        <a href="${item.meetingLink}" target="_blank" rel="noopener">${item.meetingLink}</a>
                                    </c:if>
                                </td>
                                <td>${item.status}</td>
                                <td>${item.result}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${item.status eq 'Scheduled' or item.status eq 'Rescheduled'}">
                                            <a class="btn link" href="${pageContext.request.contextPath}/hrstaff/interviews/schedule?applicationId=${applicationView.application.applicationId}&interviewId=${item.interviewId}">Sửa</a>
                                        </c:when>
                                        <c:when test="${item.status eq 'Completed' and item.result eq 'Passed'}">
                                            <a class="btn link" href="${pageContext.request.contextPath}/hrstaff/offers/manage?applicationId=${applicationView.application.applicationId}">Soạn offer</a>
                                        </c:when>
                                        <c:otherwise>Đã xử lý</c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </section>

    <section class="card">
        <div class="body">
            <h2 class="section-title">
                <c:choose>
                    <c:when test="${selectedInterview != null}">Sửa/đổi lịch phỏng vấn</c:when>
                    <c:otherwise>Đặt lịch phỏng vấn mới</c:otherwise>
                </c:choose>
            </h2>
            <form method="post" action="${pageContext.request.contextPath}/hrstaff/interviews/schedule">
                <input type="hidden" name="action" value="saveSchedule">
                <input type="hidden" name="applicationId" value="${applicationView.application.applicationId}">
                <input type="hidden" name="interviewId" value="${selectedInterview.interviewId}">
                <div class="form-grid">

                    <div class="field">
                        <label for="scheduledAt">Thời gian</label>
                        <input type="datetime-local" id="scheduledAt" name="scheduledAt"
                               value="${selectedInterview != null ? selectedInterview.scheduledAtInputValue : param.scheduledAt}" required>
                    </div>
                    <div class="field">
                        <label for="location">Địa điểm</label>
                        <input type="text" id="location" name="location"
                               value="${selectedInterview != null ? selectedInterview.location : param.location}" placeholder="VD: Phòng họp 3A">
                    </div>
                    <div class="field">
                        <label for="meetingLink">Link meeting</label>
                        <input type="url" id="meetingLink" name="meetingLink"
                               value="${selectedInterview != null ? selectedInterview.meetingLink : param.meetingLink}" placeholder="https://...">
                    </div>
                    <div class="field full">
                        <label for="interviewerEmployeeId">Người phỏng vấn</label>
                        <select id="interviewerEmployeeId" name="interviewerEmployeeId">
                            <option value="">Chưa chỉ định</option>
                            <c:forEach var="employee" items="${employees}">
                                <option value="${employee.employeeId}"
                                        <c:if test="${selectedInterview != null && selectedInterview.interviewerEmployeeId == employee.employeeId}">selected</c:if>>
                                    ${employee.fullName} - ${employee.position}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="field full">
                        <label for="note">Ghi chú</label>
                        <textarea id="note" name="note" placeholder="Ghi chú cho ứng viên hoặc người phỏng vấn">${selectedInterview != null ? selectedInterview.note : param.note}</textarea>
                    </div>
                </div>
                <div class="actions">
                    <a class="btn secondary" href="${pageContext.request.contextPath}/candidates">Quay lại danh sách</a>
                    <a class="btn secondary" href="${pageContext.request.contextPath}/hrstaff/offers/manage?applicationId=${applicationView.application.applicationId}">Tạo/Gửi offer</a>
                    <button class="btn primary" type="submit">
                        <c:choose>
                            <c:when test="${selectedInterview != null}">Lưu thay đổi lịch</c:when>
                            <c:otherwise>Lưu lịch & gửi thông báo</c:otherwise>
                        </c:choose>
                    </button>
                </div>
            </form>
        </div>
    </section>

    <c:if test="${selectedInterview != null and (selectedInterview.status eq 'Scheduled' or selectedInterview.status eq 'Rescheduled')}">
        <section class="card">
            <div class="body">
                <h2 class="section-title">Kết quả hoặc hủy lịch</h2>
                <form method="post" action="${pageContext.request.contextPath}/hrstaff/interviews/schedule">
                    <input type="hidden" name="action" value="updateResult">
                    <input type="hidden" name="interviewId" value="${selectedInterview.interviewId}">
                    <div class="form-grid">
                        <div class="field">
                            <label for="result">Kết quả</label>
                            <select id="result" name="result" required>
                                <option value="">Chọn kết quả</option>
                                <option value="Passed">Pass</option>
                                <option value="Failed">Fail</option>
                            </select>
                        </div>
                        <div class="field full">
                            <label for="resultNote">Ghi chú kết quả</label>
                            <textarea id="resultNote" name="resultNote" placeholder="Nhận xét sau phỏng vấn"></textarea>
                        </div>
                    </div>
                    <div class="actions">
                        <button class="btn primary" type="submit">Cập nhật kết quả</button>
                    </div>
                </form>

                <form method="post" action="${pageContext.request.contextPath}/hrstaff/interviews/schedule">
                    <input type="hidden" name="action" value="cancelInterview">
                    <input type="hidden" name="interviewId" value="${selectedInterview.interviewId}">
                    <div class="field full">
                        <label for="cancelNote">Lý do hủy lịch</label>
                        <textarea id="cancelNote" name="cancelNote" placeholder="Lý do hủy hoặc hướng xử lý tiếp theo"></textarea>
                    </div>
                    <div class="actions">
                        <button class="btn danger" type="submit">Hủy lịch phỏng vấn</button>
                    </div>
                </form>
            </div>
        </section>
    </c:if>
</main>
    <%@ include file="../AI/AI_Assistant_Widget.jspf" %>
    <script charset="UTF-8" src="${pageContext.request.contextPath}/js/chatbot.js"></script>
</body>
</html>
