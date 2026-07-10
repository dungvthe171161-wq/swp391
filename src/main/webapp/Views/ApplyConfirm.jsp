<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.hrm.model.entity.CandidateProfile"%>
<%@page import="com.hrm.model.entity.Recruitment"%>
<%
    Recruitment recruitment = (Recruitment) request.getAttribute("recruitment");
    CandidateProfile profile = (CandidateProfile) request.getAttribute("candidateProfile");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Xác nhận ứng tuyển - BetterHR</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        :root {
            --green: #006241;
            --accent: #00754A;
            --house: #1E3932;
            --canvas: #f2f0eb;
            --ceramic: #edebe9;
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
            background: var(--canvas);
            color: rgba(0,0,0,0.87);
            font-family: Inter, "Helvetica Neue", Arial, sans-serif;
            line-height: 1.6;
        }
        .header {
            padding: 42px 20px;
            background: var(--house);
            color: #ffffff;
            text-align: center;
        }
        .header h1 {
            margin: 0 0 8px;
            font-size: clamp(30px, 4vw, 44px);
            line-height: 1.15;
        }
        .header p { margin: 0; color: rgba(255,255,255,0.74); }
        .container {
            width: min(880px, calc(100% - 32px));
            margin: 0 auto;
        }
        .confirm-card {
            margin: 32px 0 56px;
            padding: 34px;
            border: 1px solid var(--border);
            border-radius: 12px;
            background: var(--card);
            box-shadow: 0 0 0.5px rgba(0,0,0,0.14), 0 10px 28px rgba(0,0,0,0.08);
        }
        .alert {
            margin-bottom: 18px;
            padding: 12px 14px;
            border-radius: 8px;
            font-weight: 800;
        }
        .alert.error {
            border: 1px solid rgba(200,32,20,0.22);
            background: rgba(200,32,20,0.08);
            color: var(--red);
        }
        .summary-grid {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 18px;
            margin: 22px 0;
        }
        .summary-box {
            padding: 18px;
            border: 1px solid var(--border);
            border-radius: 10px;
            background: #ffffff;
        }
        .summary-box h2 {
            margin: 0 0 14px;
            color: var(--green);
            font-size: 20px;
        }
        .meta {
            display: grid;
            gap: 10px;
        }
        .meta p {
            display: flex;
            justify-content: space-between;
            gap: 16px;
            margin: 0;
            padding-bottom: 9px;
            border-bottom: 1px solid #ebe7df;
        }
        .meta p:last-child { border-bottom: 0; padding-bottom: 0; }
        .meta strong { color: var(--green); }
        .note {
            margin: 18px 0;
            padding: 16px;
            border-left: 4px solid var(--accent);
            border-radius: 8px;
            background: var(--mint);
            color: var(--green);
            font-weight: 800;
        }
        textarea {
            width: 100%;
            min-height: 90px;
            padding: 12px 14px;
            border: 1px solid var(--border);
            border-radius: 8px;
            resize: vertical;
            outline: 0;
        }
        textarea:focus {
            border-color: var(--accent);
            box-shadow: 0 0 0 3px rgba(0,117,74,0.14);
        }
        .actions {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
            margin-top: 18px;
        }
        .btn {
            min-height: 46px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            padding: 0 18px;
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
        @media (max-width: 760px) {
            .confirm-card { padding: 22px; }
            .summary-grid { grid-template-columns: 1fr; }
            .actions { display: grid; }
        }
    </style>
</head>
<body>
    <header class="header">
        <div class="container">
            <h1><i class="fas fa-circle-check"></i> Xác nhận ứng tuyển</h1>
            <p>Bạn sắp ứng tuyển bằng hồ sơ đã lưu trong BetterHR.</p>
        </div>
    </header>

    <main class="container">
        <section class="confirm-card">
            <% if (request.getAttribute("error") != null) { %>
            <div class="alert error"><%= request.getAttribute("error") %></div>
            <% } %>

            <div class="note">
                <i class="fas fa-shield-heart"></i> Hệ thống sẽ dùng hồ sơ ứng tuyển mới nhất của bạn. Bạn không cần nhập lại thông tin cá nhân hay CV.
            </div>

            <div class="summary-grid">
                <div class="summary-box">
                    <h2>Việc làm</h2>
                    <div class="meta">
                        <p><strong>Tên công việc</strong><span><%= recruitment != null ? recruitment.getTitle() : "" %></span></p>
                        <p><strong>Địa điểm</strong><span><%= recruitment != null && recruitment.getLocation() != null ? recruitment.getLocation() : "N/A" %></span></p>
                        <p><strong>Mức lương</strong><span><%= recruitment != null && recruitment.getSalary() != null ? String.format("%,.0f VND", recruitment.getSalary()) : "Thỏa thuận" %></span></p>
                    </div>
                </div>

                <div class="summary-box">
                    <h2>Hồ sơ ứng viên</h2>
                    <div class="meta">
                        <p><strong>Họ tên</strong><span><%= profile != null ? profile.getFullName() : "" %></span></p>
                        <p><strong>Email</strong><span><%= profile != null ? profile.getEmail() : "" %></span></p>
                        <p><strong>SĐT</strong><span><%= profile != null ? profile.getPhone() : "" %></span></p>
                        <p><strong>CV</strong><span><%= profile != null ? profile.getCvFilePath() : "" %></span></p>
                    </div>
                </div>
            </div>

            <form action="${pageContext.request.contextPath}/RecruitmentController" method="POST">
                <input type="hidden" name="action" value="confirmApplication">
                <% if (recruitment != null) { %>
                <input type="hidden" name="recruitmentId" value="<%= recruitment.getRecruitmentId() %>">
                <% } %>
                <label for="note"><strong>Ghi chú cho HR</strong> <span style="color: var(--muted); font-weight: 500;">(nếu có)</span></label>
                <textarea id="note" name="note" placeholder="Bạn có thể để lại lời nhắn ngắn cho HR..."></textarea>
                <div class="actions">
                    <button class="btn" type="submit"><i class="fas fa-paper-plane"></i> Xác nhận ứng tuyển</button>
                    <a class="btn secondary" href="${pageContext.request.contextPath}/guest/profile"><i class="fas fa-user-pen"></i> Cập nhật hồ sơ</a>
                    <a class="btn secondary" href="${pageContext.request.contextPath}/RecruitmentController"><i class="fas fa-arrow-left"></i> Quay lại</a>
                </div>
            </form>
        </section>
    </main>
</body>
</html>
