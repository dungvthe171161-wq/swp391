<%--
    Document   : Success
    Created on : Oct 27, 2025, 1:45:23 PM
    Author     : admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gửi hồ sơ thành công - BetterHR</title>
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
            --bh-mint: #d4e9e2;
            --bh-gold: #cba258;
        }

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 32px 16px;
            font-family: Inter, "Helvetica Neue", Arial, sans-serif;
            color: var(--bh-text);
            background: var(--bh-canvas);
            line-height: 1.6;
        }

        .success-container {
            width: min(620px, 100%);
            padding: 42px;
            border: 1px solid var(--bh-border);
            border-radius: 12px;
            background: var(--bh-white);
            box-shadow: 0 0 0.5px rgba(0,0,0,0.14), 0 8px 24px rgba(0,0,0,0.08);
            text-align: center;
            animation: slideUp 0.5s ease-out;
        }

        @keyframes slideUp {
            from {
                opacity: 0;
                transform: translateY(24px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .success-icon {
            width: 82px;
            height: 82px;
            margin: 0 auto 22px;
            display: grid;
            place-items: center;
            border-radius: 50%;
            background: var(--bh-mint);
            color: var(--bh-green);
            font-size: 42px;
        }

        .success-title {
            margin-bottom: 14px;
            color: var(--bh-green);
            font-size: clamp(28px, 4vw, 38px);
            line-height: 1.2;
            font-weight: 800;
        }

        .success-message {
            margin-bottom: 28px;
            color: var(--bh-muted);
            font-size: 17px;
        }

        .success-details {
            margin-bottom: 28px;
            padding: 22px;
            border-left: 4px solid var(--bh-accent);
            border-radius: 8px;
            background: var(--bh-ceramic);
            text-align: left;
        }

        .success-details h3 {
            margin-bottom: 12px;
            color: var(--bh-text);
            font-size: 19px;
            font-weight: 800;
        }

        .success-details p {
            margin-bottom: 8px;
            color: var(--bh-muted);
            font-size: 15px;
        }

        .success-details p:last-child {
            margin-bottom: 0;
        }

        .success-details i {
            color: var(--bh-green);
            margin-right: 6px;
        }

        .action-buttons {
            display: flex;
            justify-content: center;
            gap: 14px;
            flex-wrap: wrap;
        }

        .btn {
            min-height: 46px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 9px;
            padding: 10px 20px;
            border-radius: 50px;
            border: 1px solid transparent;
            font-size: 14px;
            font-weight: 800;
            text-decoration: none;
            cursor: pointer;
            transition: transform 0.2s ease, box-shadow 0.2s ease, background 0.2s ease, border-color 0.2s ease;
        }

        .btn-primary {
            background: var(--bh-accent);
            color: var(--bh-white);
            border-color: var(--bh-accent);
        }

        .btn-primary:hover {
            background: var(--bh-green);
            border-color: var(--bh-green);
            box-shadow: 0 8px 22px rgba(0, 98, 65, 0.24);
            transform: translateY(-2px);
        }

        .btn-secondary {
            background: var(--bh-white);
            color: var(--bh-green);
            border-color: var(--bh-border);
        }

        .btn-secondary:hover {
            background: var(--bh-ceramic);
            transform: translateY(-2px);
        }

        .btn:active {
            transform: scale(0.95);
        }

        .contact-info {
            margin-top: 30px;
            padding-top: 24px;
            border-top: 1px solid rgba(0, 0, 0, 0.12);
        }

        .contact-info h4 {
            margin-bottom: 8px;
            color: var(--bh-text);
            font-size: 17px;
            font-weight: 800;
        }

        .contact-info p {
            color: var(--bh-muted);
            font-size: 14px;
        }

        @media (max-width: 640px) {
            .success-container {
                padding: 30px 20px;
            }

            .action-buttons {
                flex-direction: column;
            }

            .btn {
                width: 100%;
            }
        }
    </style>
</head>
<body>
    <main class="success-container">
        <div class="success-icon">
            <i class="fas fa-check"></i>
        </div>

        <h1 class="success-title">Gửi hồ sơ thành công!</h1>

        <p class="success-message">
            Cảm ơn bạn đã quan tâm đến BetterHR. Hồ sơ của bạn đã được ghi nhận và sẽ được bộ phận nhân sự xem xét.
        </p>

        <div class="success-details">
            <h3><i class="fas fa-info-circle"></i> Các bước tiếp theo</h3>
            <p><i class="fas fa-clock"></i> Chúng tôi sẽ xem xét hồ sơ trong vòng 3-5 ngày làm việc.</p>
            <p><i class="fas fa-envelope"></i> Bạn sẽ nhận được email thông báo về bước tiếp theo.</p>
            <p><i class="fas fa-phone"></i> Nếu phù hợp, chúng tôi sẽ liên hệ để sắp xếp phỏng vấn.</p>
        </div>

        <div class="action-buttons">
            <a href="${pageContext.request.contextPath}/RecruitmentController" class="btn btn-primary">
                <i class="fas fa-briefcase"></i> Xem thêm việc làm
            </a>
            <a href="${pageContext.request.contextPath}/homepage" class="btn btn-secondary">
                <i class="fas fa-home"></i> Về trang chủ
            </a>
        </div>

        <div class="contact-info">
            <h4><i class="fas fa-headset"></i> Cần hỗ trợ?</h4>
            <p>Nếu có câu hỏi về hồ sơ ứng tuyển, vui lòng liên hệ:</p>
            <p><strong>Email:</strong> ducnvhe180815@fpt.edu.vn | <strong>Điện thoại:</strong> 0818886875</p>
        </div>
    </main>
</body>
</html>
