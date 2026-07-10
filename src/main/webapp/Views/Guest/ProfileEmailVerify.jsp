<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.hrm.controller.RecruitmentController.PendingCandidateProfile"%>
<%
    PendingCandidateProfile draft = (PendingCandidateProfile) request.getAttribute("profileDraft");
    Integer ttlMinutes = (Integer) request.getAttribute("ttlMinutes");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BetterHR | Xác nhận email hồ sơ</title>
    <link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;600;700;800&display=swap" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/guest.css" rel="stylesheet">
    <style>
        body {
            display: grid;
            place-items: center;
            padding: 24px;
        }
        .verify-wrap {
            width: min(540px, 100%);
        }
        .verify-code {
            width: 100%;
            min-height: 54px;
            padding: 0 14px;
            border: 1px solid var(--guest-line);
            border-radius: 8px;
            font-size: 22px;
            font-weight: 800;
            letter-spacing: 6px;
            text-align: center;
            outline: 0;
        }
        .verify-code:focus {
            border-color: var(--guest-secondary);
            box-shadow: 0 0 0 3px rgba(0, 108, 68, 0.16);
        }
        .verify-actions {
            display: grid;
            gap: 10px;
            margin-top: 16px;
        }
    </style>
</head>
<body>
    <main class="verify-wrap">
        <section class="candidate-card">
            <div class="candidate-card-header">
                <div>
                    <h2>Xác nhận email hồ sơ ứng tuyển</h2>
                    <p>Mã xác nhận đã được gửi đến ${profileDraft.email}. Mã hết hạn sau <%= ttlMinutes != null ? ttlMinutes : 10 %> phút.</p>
                </div>
            </div>
            <div class="candidate-card-body">
                <% if (request.getAttribute("success") != null) { %>
                    <div class="candidate-alert"><%= request.getAttribute("success") %></div>
                <% } %>
                <% if (request.getAttribute("error") != null) { %>
                    <div class="candidate-alert error"><%= request.getAttribute("error") %></div>
                <% } %>

                <form action="${pageContext.request.contextPath}/guest/profile" method="post">
                    <input type="hidden" name="action" value="verifyCandidateProfileEmail">
                    <label class="candidate-field" for="verificationCode">Mã xác nhận</label>
                    <input class="verify-code" id="verificationCode" name="verificationCode" inputmode="numeric" maxlength="6" required autofocus>
                    <div class="verify-actions">
                        <button class="candidate-btn" type="submit"><i class="fa-solid fa-check"></i> Xác nhận & Lưu hồ sơ</button>
                    </div>
                </form>

                <form action="${pageContext.request.contextPath}/guest/profile" method="post">
                    <input type="hidden" name="action" value="resendCandidateProfileCode">
                    <div class="verify-actions">
                        <button class="candidate-btn outline" type="submit"><i class="fa-solid fa-rotate-right"></i> Gửi lại mã</button>
                        <a class="candidate-btn outline" href="${pageContext.request.contextPath}/guest/profile"><i class="fa-solid fa-arrow-left"></i> Quay lại hồ sơ</a>
                    </div>
                </form>
            </div>
        </section>
    </main>
    <script>
        document.getElementById('verificationCode').addEventListener('input', function(e) {
            e.target.value = e.target.value.replace(/\D/g, '').substring(0, 6);
        });
    </script>
</body>
</html>
