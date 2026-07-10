<%--
    Document   : Recruitment
    Created on : Oct 27, 2025, 1:45:23 PM
    Author     : admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.hrm.model.entity.Recruitment"%>
<%@page import="java.util.List"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tuyển dụng - BetterHR</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        :root {
            --bh-green: #006241;
            --bh-accent: #00754A;
            --bh-house: #1E3932;
            --bh-canvas: #f2f0eb;
            --bh-ceramic: #edebe9;
            --bh-text: rgba(0, 0, 0, 0.87);
            --bh-muted: rgba(0, 0, 0, 0.58);
            --bh-border: rgba(0, 0, 0, 0.14);
            --bh-gold: #cba258;
            --bh-red: #c82014;
        }

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            font-family: Inter, "Helvetica Neue", Arial, sans-serif;
            line-height: 1.6;
            color: var(--bh-text);
            background: var(--bh-canvas);
        }

        .container {
            max-width: 1180px;
            margin: 0 auto;
            padding: 0 20px;
        }

        .header {
            background: var(--bh-house);
            color: #ffffff;
            padding: 22px 0;
            box-shadow: 0 1px 3px rgba(0,0,0,0.10), 0 2px 2px rgba(0,0,0,0.06), 0 0 2px rgba(0,0,0,0.07);
        }

        .header-content {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 24px;
        }

        .btn-homepage {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            padding: 9px 18px;
            border: 1px solid rgba(255, 255, 255, 0.35);
            border-radius: 50px;
            background: rgba(255, 255, 255, 0.12);
            color: #ffffff;
            text-decoration: none;
            font-weight: 700;
            font-size: 14px;
            transition: transform 0.2s ease, background 0.2s ease, color 0.2s ease;
            white-space: nowrap;
        }

        .btn-homepage:hover {
            background: #ffffff;
            color: var(--bh-green);
            transform: translateY(-2px);
            text-decoration: none;
        }

        .btn-homepage:active,
        .apply-btn:active {
            transform: scale(0.95);
        }

        .header-text {
            flex: 1;
            text-align: center;
        }

        .header h1 {
            font-size: 30px;
            line-height: 1.25;
            font-weight: 800;
            margin-bottom: 6px;
        }

        .header p {
            color: rgba(255, 255, 255, 0.72);
            font-size: 15px;
        }

        .hero-section {
            background: var(--bh-ceramic);
            padding: 56px 0 44px;
            border-bottom: 1px solid rgba(0, 0, 0, 0.08);
        }

        .hero-content {
            text-align: center;
        }

        .hero-content h2,
        .section-title h2 {
            color: var(--bh-green);
            font-weight: 800;
            letter-spacing: 0;
        }

        .hero-content h2 {
            font-size: 34px;
            margin-bottom: 12px;
        }

        .hero-content p,
        .section-title p {
            color: var(--bh-muted);
            font-size: 16px;
        }

        .hero-content p {
            max-width: 700px;
            margin: 0 auto;
        }

        .jobs-section {
            padding: 56px 0;
        }

        .section-title {
            text-align: center;
            margin-bottom: 34px;
        }

        .section-title h2 {
            font-size: 32px;
            margin-bottom: 8px;
        }

        .jobs-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(330px, 1fr));
            gap: 24px;
        }

        .job-card {
            background: #ffffff;
            border: 1px solid var(--bh-border);
            border-radius: 12px;
            padding: 28px;
            box-shadow: 0 0 0.5px rgba(0,0,0,0.14), 0 8px 24px rgba(0,0,0,0.08);
            transition: transform 0.25s ease, box-shadow 0.25s ease;
        }

        .job-card:hover {
            transform: translateY(-4px);
            box-shadow: 0 0 0.5px rgba(0,0,0,0.14), 0 12px 30px rgba(0,0,0,0.12);
        }

        .job-header {
            margin-bottom: 18px;
        }

        .job-title {
            color: var(--bh-text);
            font-size: 22px;
            line-height: 1.35;
            font-weight: 800;
            margin-bottom: 10px;
        }

        .job-location,
        .job-date {
            color: var(--bh-muted);
            font-size: 14px;
        }

        .job-location i,
        .job-date i {
            color: rgba(0, 0, 0, 0.42);
            margin-right: 6px;
        }

        .job-salary {
            color: var(--bh-accent);
            font-weight: 800;
            font-size: 16px;
            margin-top: 8px;
        }

        .job-applicant {
            color: var(--bh-green);
            font-weight: 700;
            font-size: 15px;
            margin-top: 8px;
        }

        .job-status {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            margin-top: 10px;
            padding: 5px 12px;
            border-radius: 50px;
            font-size: 13px;
            font-weight: 800;
            background: #d4e9e2;
            color: var(--bh-green);
        }

        .status-waiting {
            background: rgba(203, 162, 88, 0.18);
            color: #80601f;
        }

        .status-deleted,
        .status-close {
            background: rgba(200, 32, 20, 0.08);
            color: var(--bh-red);
        }

        .job-description {
            color: var(--bh-muted);
            margin-bottom: 18px;
        }

        .job-requirements {
            margin-bottom: 18px;
        }

        .job-requirements h4 {
            color: var(--bh-text);
            font-size: 15px;
            font-weight: 800;
            margin-bottom: 8px;
        }

        .job-requirements ul {
            list-style: none;
            padding-left: 0;
        }

        .job-requirements li {
            position: relative;
            padding-left: 18px;
            margin-bottom: 6px;
            color: var(--bh-muted);
        }

        .job-requirements li::before {
            content: "•";
            position: absolute;
            left: 0;
            color: var(--bh-accent);
            font-weight: 900;
        }

        .job-footer {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 16px;
            margin-top: 22px;
            padding-top: 18px;
            border-top: 1px solid rgba(0, 0, 0, 0.12);
        }

        .apply-btn {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            border: 1px solid var(--bh-accent);
            border-radius: 50px;
            background: var(--bh-accent);
            color: #ffffff;
            padding: 10px 18px;
            text-decoration: none;
            font-size: 14px;
            font-weight: 800;
            cursor: pointer;
            white-space: nowrap;
            transition: transform 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
        }

        .apply-btn:hover {
            background: var(--bh-green);
            box-shadow: 0 6px 18px rgba(0, 98, 65, 0.24);
            color: #ffffff;
            text-decoration: none;
        }

        .apply-btn.disabled {
            border-color: rgba(0, 0, 0, 0.25);
            background: rgba(0, 0, 0, 0.42);
            cursor: not-allowed;
            opacity: 0.78;
        }

        .company-info {
            background: #ffffff;
            padding: 48px 0;
            border-top: 1px solid rgba(0, 0, 0, 0.08);
            border-bottom: 1px solid rgba(0, 0, 0, 0.08);
        }

        .company-stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
            gap: 22px;
            text-align: center;
        }

        .stat-item {
            padding: 18px;
            border-radius: 12px;
            background: var(--bh-canvas);
        }

        .stat-item h3 {
            color: var(--bh-green);
            font-size: 34px;
            line-height: 1;
            margin-bottom: 8px;
        }

        .stat-item p {
            color: var(--bh-muted);
            font-weight: 700;
        }

        .contact-section {
            background: var(--bh-house);
            color: #ffffff;
            padding: 48px 0;
            text-align: center;
        }

        .contact-section h2 {
            font-size: 28px;
            margin-bottom: 8px;
        }

        .contact-section > .container > p {
            color: rgba(255, 255, 255, 0.72);
        }

        .contact-info {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
            gap: 20px;
            margin-top: 28px;
        }

        .contact-item {
            padding: 20px;
            border-radius: 12px;
            background: rgba(255, 255, 255, 0.08);
            border: 1px solid rgba(255, 255, 255, 0.12);
        }

        .contact-item i {
            color: var(--bh-gold);
            font-size: 26px;
            margin-bottom: 10px;
        }

        .contact-item h4 {
            margin-bottom: 4px;
        }

        .contact-item p {
            color: rgba(255, 255, 255, 0.72);
        }

        .alert {
            padding: 14px 16px;
            margin-bottom: 22px;
            border-radius: 8px;
            font-weight: 700;
        }

        .alert-success {
            background: #d4e9e2;
            color: var(--bh-green);
            border: 1px solid rgba(0, 98, 65, 0.22);
        }

        .alert-error {
            background: rgba(200, 32, 20, 0.08);
            color: var(--bh-red);
            border: 1px solid rgba(200, 32, 20, 0.22);
        }

        @media (max-width: 768px) {
            .header-content,
            .job-footer {
                align-items: stretch;
                flex-direction: column;
            }

            .header-text {
                text-align: left;
            }

            .hero-content h2,
            .section-title h2 {
                font-size: 28px;
            }

            .jobs-grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
    <div class="header">
        <div class="container">
            <div class="header-content">
                <a href="${pageContext.request.contextPath}/homepage" class="btn-homepage" title="Quay về trang chủ">
                    <i class="fas fa-home"></i>
                    <span>Trang chủ</span>
                </a>
                <div class="header-text">
                    <h1><i class="fas fa-briefcase"></i> Tuyển dụng</h1>
                    <p>Khám phá các cơ hội nghề nghiệp tại BetterHR</p>
                </div>
            </div>
        </div>
    </div>

    <div class="hero-section">
        <div class="container">
            <div class="hero-content">
                <h2>Mở ra cơ hội phát triển sự nghiệp</h2>
                <p>Chúng tôi tìm kiếm những nhân sự giàu năng lực để cùng xây dựng môi trường làm việc chuyên nghiệp, tử tế và hiệu quả.</p>
            </div>
        </div>
    </div>

    <div class="jobs-section">
        <div class="container">
            <div class="section-title">
                <h2>Cơ hội nghề nghiệp</h2>
                <p>Các vị trí đang tuyển dụng</p>
            </div>

            <% if (request.getAttribute("success") != null) { %>
                <div class="alert alert-success">
                    <i class="fas fa-check-circle"></i> <%= request.getAttribute("success") %>
                </div>
            <% } %>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error">
                    <i class="fas fa-exclamation-circle"></i> <%= request.getAttribute("error") %>
                </div>
            <% } %>

            <div class="jobs-grid">
                <%
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    List<Recruitment> recruitments = (List<Recruitment>) request.getAttribute("recruitments");
                    if (recruitments != null && !recruitments.isEmpty()) {
                        for (Recruitment recruitment : recruitments) {
                %>
                <div class="job-card">
                    <div class="job-header">
                        <h3 class="job-title"><%= recruitment.getTitle() %></h3>
                        <div class="job-location">
                            <i class="fas fa-map-marker-alt"></i><%= recruitment.getLocation() %>
                        </div>
                        <% if (recruitment.getSalary() != null) { %>
                        <div class="job-salary">
                            <i class="fas fa-money-bill-wave"></i> <%= String.format("%,.0f", recruitment.getSalary()) %> VNĐ
                        </div>
                        <% } %>
                        <div class="job-applicant">
                            <i class="fas fa-users"></i> Số lượng tuyển: <%= recruitment.getApplicant() %> người
                        </div>
                        <% if (recruitment.getStatus() != null && "Applied".equals(recruitment.getStatus())) { %>
                        <div class="job-status status-applied">
                            <i class="fas fa-info-circle"></i> Đang tuyển
                        </div>
                        <% } %>
                    </div>

                    <div class="job-description">
                        <%= recruitment.getDescription() %>
                    </div>

                    <% if (recruitment.getRequirement() != null && !recruitment.getRequirement().trim().isEmpty()) { %>
                    <div class="job-requirements">
                        <h4>Yêu cầu:</h4>
                        <ul>
                            <%
                                String[] requirements = recruitment.getRequirement().split("\n");
                                for (String req : requirements) {
                                    if (!req.trim().isEmpty()) {
                            %>
                            <li><%= req.trim() %></li>
                            <%
                                    }
                                }
                            %>
                        </ul>
                    </div>
                    <% } %>

                    <div class="job-footer">
                        <div class="job-date">
                            <i class="fas fa-calendar"></i>
                            <% if (recruitment.getPostedDate() != null) { %>
                                <%= recruitment.getPostedDate().format(formatter) %>
                            <% } %>
                        </div>

                        <%
                            String status = recruitment.getStatus();
                            boolean canApply = status != null && status.equals("Applied");
                        %>
                        <% if (canApply) { %>
                        <a href="${pageContext.request.contextPath}/RecruitmentController?action=apply&recruitmentId=<%= recruitment.getRecruitmentId() %>"
                           class="apply-btn">
                            <i class="fas fa-paper-plane"></i> Ứng tuyển ngay
                        </a>
                        <% } else { %>
                        <span class="apply-btn disabled">
                            <i class="fas fa-lock"></i> Đã đóng
                        </span>
                        <% } %>
                    </div>
                </div>
                <%
                        }
                    } else {
                %>
                <div class="job-card" style="grid-column: 1 / -1; text-align: center;">
                    <h3 class="job-title">Hiện chưa có tin tuyển dụng</h3>
                    <p class="job-description">Vui lòng quay lại sau để xem các cơ hội mới.</p>
                </div>
                <%
                    }
                %>
            </div>
        </div>
    </div>

    <div class="company-info">
        <div class="container">
            <div class="section-title">
                <h2>Vì sao chọn BetterHR?</h2>
                <p>Môi trường làm việc chuyên nghiệp, rõ ràng và nhiều cơ hội phát triển</p>
            </div>

            <div class="company-stats">
                <div class="stat-item">
                    <h3>500+</h3>
                    <p>Nhân sự</p>
                </div>
                <div class="stat-item">
                    <h3>50+</h3>
                    <p>Dự án</p>
                </div>
                <div class="stat-item">
                    <h3>15+</h3>
                    <p>Năm kinh nghiệm</p>
                </div>
                <div class="stat-item">
                    <h3>98%</h3>
                    <p>Hài lòng</p>
                </div>
            </div>
        </div>
    </div>

    <div class="contact-section">
        <div class="container">
            <h2>Kết nối với chúng tôi</h2>
            <p>BetterHR không thu bất kỳ khoản phí nào từ ứng viên trong quá trình tuyển dụng</p>

            <div class="contact-info">
                <div class="contact-item">
                    <i class="fas fa-envelope"></i>
                    <h4>Email</h4>
                    <p>ducnvhe180815@fpt.edu.vn</p>
                </div>
                <div class="contact-item">
                    <i class="fas fa-phone"></i>
                    <h4>Điện thoại</h4>
                    <p>0818886875</p>
                </div>
                <div class="contact-item">
                    <i class="fas fa-map-marker-alt"></i>
                    <h4>Địa chỉ</h4>
                    <p>FPT Hà Nội</p>
                </div>
            </div>
        </div>
    </div>

    <script>
        const observerOptions = {
            threshold: 0.1,
            rootMargin: '0px 0px -50px 0px'
        };

        const observer = new IntersectionObserver(function(entries) {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.style.opacity = '1';
                    entry.target.style.transform = 'translateY(0)';
                }
            });
        }, observerOptions);

        document.querySelectorAll('.job-card').forEach(card => {
            card.style.opacity = '0';
            card.style.transform = 'translateY(20px)';
            card.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
            observer.observe(card);
        });
    </script>
</body>
</html>
