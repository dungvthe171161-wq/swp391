<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.hrm.controller.RecruitmentController.PendingCandidateProfile"%>
<%@page import="com.hrm.model.entity.Recruitment"%>
<%
    Recruitment recruitment = (Recruitment) request.getAttribute("recruitment");
    PendingCandidateProfile draft = (PendingCandidateProfile) request.getAttribute("profileDraft");
    Integer ttlMinutes = (Integer) request.getAttribute("ttlMinutes");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Xác nhận email - BetterHR</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        :root {
            --green: #006241;
            --accent: #00754A;
            --house: #1E3932;
            --canvas: #f2f0eb;
            --card: #ffffff;
            --muted: rgba(0,0,0,0.58);
            --border: rgba(0,0,0,0.14);
            --red: #c82014;
            --mint: #d4e9e2;
        }
        * { box-sizing: border-box; }
        body {
            min-height: 100vh;
            margin: 0;
            display: grid;
            place-items: center;
            padding: 24px;
            background: var(--canvas);
            color: rgba(0,0,0,0.87);
            font-family: Inter, "Helvetica Neue", Arial, sans-serif;
        }
        .verify-card {
            width: min(560px, 100%);
            padding: 34px;
            border: 1px solid var(--border);
            border-radius: 12px;
            background: var(--card);
            box-shadow: 0 0 0.5px rgba(0,0,0,0.14), 0 12px 32px rgba(0,0,0,0.10);
        }
        .verify-icon {
            width: 64px;
            height: 64px;
            display: grid;
            place-items: center;
            margin-bottom: 18px;
            border-radius: 14px;
            background: var(--mint);
            color: var(--green);
            font-size: 26px;
        }
        h1 {
            margin: 0 0 8px;
            color: var(--green);
            font-size: 30px;
            line-height: 1.15;
        }
        p { margin: 0 0 18px; color: var(--muted); line-height: 1.6; }
        .job-box {
            margin: 18px 0;
            padding: 14px 16px;
            border-left: 4px solid var(--accent);
            border-radius: 8px;
            background: #edebe9;
            font-weight: 800;
        }
        .alert {
            margin-bottom: 16px;
            padding: 12px 14px;
            border-radius: 8px;
            font-weight: 800;
        }
        .alert.error {
            border: 1px solid rgba(200,32,20,0.22);
            background: rgba(200,32,20,0.08);
            color: var(--red);
        }
        .alert.success {
            border: 1px solid rgba(0,98,65,0.22);
            background: var(--mint);
            color: var(--green);
        }
        label {
            display: block;
            margin-bottom: 8px;
            font-weight: 800;
        }
        input {
            width: 100%;
            min-height: 54px;
            padding: 0 16px;
            border: 1px solid var(--border);
            border-radius: 8px;
            font-size: 22px;
            font-weight: 800;
            letter-spacing: 6px;
            text-align: center;
            outline: 0;
        }
        input:focus {
            border-color: var(--accent);
            box-shadow: 0 0 0 3px rgba(0,117,74,0.14);
        }
        .actions {
            display: grid;
            gap: 10px;
            margin-top: 18px;
        }
        .btn {
            min-height: 46px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            border: 1px solid var(--accent);
            border-radius: 999px;
            background: var(--accent);
            color: #ffffff;
            font-weight: 800;
            text-decoration: none;
            cursor: pointer;
        }
        .btn.secondary {
            background: #ffffff;
            color: var(--green);
            border-color: var(--border);
        }
        .btn:hover { background: var(--green); color: #ffffff; }
        form { margin: 0; }
    </style>
</head>
<body>
    <main class="verify-card">
        <div class="verify-icon"><i class="fas fa-envelope-circle-check"></i></div>
        <h1>Xác nhận email</h1>
        <p>
            Chúng tôi đã gửi mã xác nhận đến
            <strong><%= draft != null ? draft.getEmail() : "" %></strong>.
            Mã có hiệu lực trong <%= ttlMinutes != null ? ttlMinutes : 10 %> phút.
        </p>

        <% if (recruitment != null) { %>
        <div class="job-box">
            <i class="fas fa-briefcase"></i> <%= recruitment.getTitle() %>
        </div>
        <% } %>

        <% if (request.getAttribute("success") != null) { %>
        <div class="alert success"><%= request.getAttribute("success") %></div>
        <% } %>
        <% if (request.getAttribute("error") != null) { %>
        <div class="alert error"><%= request.getAttribute("error") %></div>
        <% } %>

        <form action="${pageContext.request.contextPath}/RecruitmentController" method="POST">
            <input type="hidden" name="action" value="verifyCandidateProfileEmail">
            <label for="verificationCode">Mã xác nhận</label>
            <input id="verificationCode" name="verificationCode" inputmode="numeric" maxlength="6" required autofocus>
            <div class="actions">
                <button class="btn" type="submit"><i class="fas fa-check"></i> Xác nhận & Lưu hồ sơ</button>
            </div>
        </form>

        <form action="${pageContext.request.contextPath}/RecruitmentController" method="POST">
            <input type="hidden" name="action" value="resendCandidateProfileCode">
            <div class="actions">
                <button class="btn secondary" type="submit"><i class="fas fa-rotate-right"></i> Gửi lại mã</button>
                <a class="btn secondary" href="${pageContext.request.contextPath}/RecruitmentController"><i class="fas fa-arrow-left"></i> Về danh sách việc làm</a>
            </div>
        </form>
    </main>

    <script>
        document.getElementById('verificationCode').addEventListener('input', function(e) {
            e.target.value = e.target.value.replace(/\D/g, '').substring(0, 6);
        });
    </script>
</body>
</html>
