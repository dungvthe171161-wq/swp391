<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.hrm.model.entity.CandidateProfile"%>
<%@page import="com.hrm.model.entity.Recruitment"%>
<%@page import="com.hrm.model.entity.SystemUser"%>
<%
    Recruitment recruitment = (Recruitment) request.getAttribute("recruitment");
    CandidateProfile profile = (CandidateProfile) request.getAttribute("candidateProfile");
    SystemUser currentUser = (SystemUser) request.getAttribute("currentUser");

    String fullName = request.getParameter("fullName") != null ? request.getParameter("fullName")
            : profile != null ? profile.getFullName()
            : currentUser != null ? currentUser.getUsername() : "";
    String email = request.getParameter("email") != null ? request.getParameter("email")
            : profile != null ? profile.getEmail()
            : currentUser != null ? currentUser.getEmail() : "";
    String phone = request.getParameter("phone") != null ? request.getParameter("phone")
            : profile != null ? profile.getPhone() : "";
    String dateOfBirth = request.getParameter("dateOfBirth") != null ? request.getParameter("dateOfBirth")
            : profile != null && profile.getDateOfBirth() != null ? profile.getDateOfBirth().toString() : "";
    String address = request.getParameter("address") != null ? request.getParameter("address")
            : profile != null ? profile.getAddress() : "";
    String desiredPosition = request.getParameter("desiredPosition") != null ? request.getParameter("desiredPosition")
            : profile != null ? profile.getDesiredPosition() : "";
    String expectedSalary = request.getParameter("expectedSalary") != null ? request.getParameter("expectedSalary")
            : profile != null && profile.getExpectedSalary() != null ? profile.getExpectedSalary().toPlainString() : "";
    String workExperience = request.getParameter("workExperience") != null ? request.getParameter("workExperience")
            : profile != null ? profile.getWorkExperience() : "";
    String currentCv = profile != null ? profile.getCvFilePath() : null;
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hồ sơ ứng tuyển - BetterHR</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        :root {
            --bh-green: #006241;
            --bh-accent: #00754A;
            --bh-house: #1E3932;
            --bh-canvas: #f2f0eb;
            --bh-ceramic: #edebe9;
            --bh-white: #ffffff;
            --bh-text: rgba(0, 0, 0, 0.87);
            --bh-muted: rgba(0, 0, 0, 0.58);
            --bh-border: rgba(0, 0, 0, 0.14);
            --bh-red: #c82014;
            --bh-mint: #d4e9e2;
        }

        * { box-sizing: border-box; margin: 0; padding: 0; }

        body {
            min-height: 100vh;
            font-family: Inter, "Helvetica Neue", Arial, sans-serif;
            color: var(--bh-text);
            background: var(--bh-canvas);
            line-height: 1.6;
        }

        .header {
            background: var(--bh-house);
            color: var(--bh-white);
            padding: 42px 20px 46px;
            text-align: center;
        }

        .header h1 {
            margin-bottom: 8px;
            font-size: clamp(30px, 4vw, 46px);
            line-height: 1.16;
            font-weight: 800;
            letter-spacing: 0;
        }

        .header p { color: rgba(255,255,255,0.72); font-size: 17px; }

        .container {
            width: min(980px, calc(100% - 32px));
            margin: 0 auto;
        }

        .navigation-buttons {
            display: flex;
            align-items: center;
            padding: 24px 0 0;
        }

        .back-btn,
        .submit-btn {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
            border-radius: 50px;
            font-weight: 800;
            text-decoration: none;
            cursor: pointer;
            transition: transform 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
        }

        .back-btn {
            min-height: 44px;
            padding: 9px 18px;
            border: 1px solid var(--bh-border);
            background: var(--bh-white);
            color: var(--bh-green);
            box-shadow: 0 0 0.5px rgba(0,0,0,0.14), 0 1px 1px rgba(0,0,0,0.18);
        }

        .form-section {
            margin: 32px auto 56px;
            padding: 38px;
            border: 1px solid var(--bh-border);
            border-radius: 12px;
            background: var(--bh-white);
            box-shadow: 0 0 0.5px rgba(0,0,0,0.14), 0 8px 24px rgba(0,0,0,0.08);
        }

        .job-info,
        .form-note {
            margin-bottom: 24px;
            padding: 22px;
            border-left: 4px solid var(--bh-accent);
            border-radius: 8px;
            background: var(--bh-ceramic);
        }

        .job-info h3 { margin-bottom: 10px; font-size: 22px; font-weight: 800; }
        .job-info p { margin-bottom: 6px; color: var(--bh-muted); }
        .job-info i, .form-note i { color: var(--bh-green); margin-right: 6px; }

        .form-note { background: var(--bh-mint); }
        .form-note h4 { color: var(--bh-green); font-size: 16px; font-weight: 800; }
        .form-note p { margin-top: 6px; color: rgba(0,0,0,0.70); font-size: 15px; }

        .section-heading {
            margin: 28px 0 16px;
            color: var(--bh-green);
            font-size: 19px;
            font-weight: 800;
        }

        .alert {
            margin-bottom: 24px;
            padding: 14px 16px;
            border-radius: 8px;
            font-weight: 700;
        }

        .alert-error {
            border: 1px solid rgba(200,32,20,0.22);
            background: rgba(200,32,20,0.08);
            color: var(--bh-red);
        }

        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 18px;
        }

        .form-group { margin-bottom: 20px; }

        .form-group label {
            display: block;
            margin-bottom: 8px;
            color: var(--bh-text);
            font-size: 14px;
            font-weight: 800;
        }

        .form-group label.required::after { content: " *"; color: var(--bh-red); }

        .form-group input,
        .form-group textarea {
            width: 100%;
            min-height: 52px;
            padding: 13px 16px;
            border: 1px solid rgba(0,0,0,0.18);
            border-radius: 8px;
            background: var(--bh-white);
            color: var(--bh-text);
            font-size: 15px;
            outline: none;
        }

        .form-group textarea { min-height: 136px; resize: vertical; }
        .form-group input:focus,
        .form-group textarea:focus {
            border-color: var(--bh-accent);
            box-shadow: 0 0 0 3px rgba(0,117,74,0.14);
        }

        .form-text {
            display: block;
            margin-top: 7px;
            color: var(--bh-muted);
            font-size: 13px;
        }

        .current-cv {
            margin-top: 8px;
            color: var(--bh-green);
            font-size: 13px;
            font-weight: 800;
        }

        .submit-btn {
            width: 100%;
            min-height: 54px;
            border: 1px solid var(--bh-accent);
            background: var(--bh-accent);
            color: var(--bh-white);
            font-size: 16px;
        }

        .submit-btn:hover {
            background: var(--bh-green);
            box-shadow: 0 8px 22px rgba(0,98,65,0.24);
            transform: translateY(-2px);
        }

        @media (max-width: 768px) {
            .navigation-buttons { justify-content: stretch; }
            .back-btn { width: 100%; }
            .form-section { padding: 26px 18px; }
            .form-row { grid-template-columns: 1fr; gap: 0; }
        }
    </style>
</head>
<body>
    <div class="header">
        <div class="container">
            <h1><i class="fas fa-id-card"></i> Hồ sơ ứng tuyển</h1>
            <p>Hoàn thiện hồ sơ một lần, các lần sau bạn chỉ cần xác nhận ứng tuyển.</p>
        </div>
    </div>

    <div class="container">
        <div class="navigation-buttons">
            <a href="${pageContext.request.contextPath}/RecruitmentController" class="back-btn">
                <i class="fas fa-arrow-left"></i> Quay lại danh sách việc làm
            </a>
        </div>

        <div class="form-section">
            <% if (recruitment != null) { %>
            <div class="job-info">
                <h3><i class="fas fa-briefcase"></i> <%= recruitment.getTitle() %></h3>
                <p><i class="fas fa-map-marker-alt"></i> <strong>Địa điểm:</strong> <%= recruitment.getLocation() != null ? recruitment.getLocation() : "N/A" %></p>
                <% if (recruitment.getSalary() != null) { %>
                <p><i class="fas fa-money-bill-wave"></i> <strong>Mức lương:</strong> <%= String.format("%,.0f", recruitment.getSalary()) %> VND</p>
                <% } %>
            </div>
            <% } %>

            <div class="form-note">
                <h4><i class="fas fa-envelope-circle-check"></i> Xác nhận email</h4>
                <p>Sau khi bấm lưu, BetterHR sẽ gửi mã xác nhận đến email bạn nhập. Hồ sơ chỉ được lưu sau khi xác nhận đúng mã.</p>
            </div>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error">
                    <i class="fas fa-exclamation-circle"></i> <%= request.getAttribute("error") %>
                </div>
            <% } %>

            <form action="${pageContext.request.contextPath}/RecruitmentController" method="POST" enctype="multipart/form-data">
                <input type="hidden" name="action" value="saveCandidateProfile">
                <% if (recruitment != null) { %>
                <input type="hidden" name="recruitmentId" value="<%= recruitment.getRecruitmentId() %>">
                <% } %>

                <h2 class="section-heading">Thông tin cá nhân</h2>
                <div class="form-group">
                    <label for="fullName" class="required">Họ và tên</label>
                    <input type="text" id="fullName" name="fullName" required value="<%= fullName != null ? fullName : "" %>">
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="email" class="required">Email</label>
                        <input type="email" id="email" name="email" required value="<%= email != null ? email : "" %>">
                    </div>
                    <div class="form-group">
                        <label for="phone" class="required">Số điện thoại</label>
                        <input type="tel" id="phone" name="phone" required value="<%= phone != null ? phone : "" %>">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="dateOfBirth">Ngày sinh</label>
                        <input type="date" id="dateOfBirth" name="dateOfBirth" value="<%= dateOfBirth %>">
                    </div>
                    <div class="form-group">
                        <label for="address">Địa chỉ/Nơi ở hiện tại</label>
                        <input type="text" id="address" name="address" value="<%= address != null ? address : "" %>">
                    </div>
                </div>

                <h2 class="section-heading">Thông tin nghề nghiệp</h2>
                <div class="form-row">
                    <div class="form-group">
                        <label for="desiredPosition">Vị trí mong muốn</label>
                        <input type="text" id="desiredPosition" name="desiredPosition" value="<%= desiredPosition != null ? desiredPosition : "" %>">
                    </div>
                    <div class="form-group">
                        <label for="expectedSalary">Mức lương mong muốn</label>
                        <input type="number" min="0" step="100000" id="expectedSalary" name="expectedSalary" value="<%= expectedSalary != null ? expectedSalary : "" %>">
                    </div>
                </div>

                <div class="form-group">
                    <label for="workExperience">Kinh nghiệm làm việc</label>
                    <textarea id="workExperience" name="workExperience" placeholder="Công ty đã từng làm, thời gian làm việc, mô tả công việc, kỹ năng nổi bật..."><%= workExperience != null ? workExperience : "" %></textarea>
                </div>

                <h2 class="section-heading">CV</h2>
                <div class="form-group">
                    <label for="cvFile" class="<%= currentCv == null || currentCv.isBlank() ? "required" : "" %>">Upload CV</label>
                    <input type="file" id="cvFile" name="cvFile" accept=".pdf,.doc,.docx" <%= currentCv == null || currentCv.isBlank() ? "required" : "" %>>
                    <small class="form-text">Chấp nhận file PDF, DOC, DOCX. Dung lượng tối đa 10MB.</small>
                    <% if (currentCv != null && !currentCv.isBlank()) { %>
                    <div class="current-cv"><i class="fas fa-file-lines"></i> CV hiện tại: <%= currentCv %></div>
                    <% } %>
                </div>

                <button type="submit" class="submit-btn">
                    <i class="fas fa-floppy-disk"></i> Lưu hồ sơ & Tiếp tục
                </button>
            </form>
        </div>
    </div>

    <script>
        const cvInput = document.getElementById('cvFile');
        cvInput.addEventListener('change', function(e) {
            const file = e.target.files[0];
            if (!file) return;
            const allowedExtensions = ['pdf', 'doc', 'docx'];
            const extension = file.name.split('.').pop().toLowerCase();
            if (!allowedExtensions.includes(extension)) {
                alert('Chỉ chấp nhận file PDF, DOC hoặc DOCX');
                e.target.value = '';
                return;
            }
            if (file.size > 10 * 1024 * 1024) {
                alert('File CV tối đa 10MB');
                e.target.value = '';
            }
        });

        document.getElementById('phone').addEventListener('input', function(e) {
            e.target.value = e.target.value.replace(/\D/g, '').substring(0, 11);
        });
    </script>
</body>
</html>
