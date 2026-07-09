<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BetterHR - Đổi mật khẩu</title>
    <link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
</head>
<body>
    <main class="auth-shell">
        <section class="auth-brand-panel" aria-label="BetterHR">
            <div class="auth-brand-content">
                <h1>BetterHR</h1>
                <p>Bảo vệ tài khoản để dữ liệu nhân sự luôn an toàn</p>
            </div>
        </section>

        <section class="auth-form-panel">
            <div class="auth-card">
                <a href="${pageContext.request.contextPath}/homepage" class="auth-back-link">
                    <i class="fas fa-arrow-left"></i>
                    <span>Về trang chủ</span>
                </a>

                <div class="auth-header">
                    <span class="auth-mini-brand">BetterHR</span>
                    <h2>Đổi mật khẩu</h2>
                    <p>Nhập mật khẩu hiện tại và mật khẩu mới của bạn.</p>
                </div>

                <form class="auth-form" action="${pageContext.request.contextPath}/changepass" method="post">
                    <div class="auth-field">
                        <input type="password" name="curPass" required placeholder="Mật khẩu hiện tại" class="auth-input">
                    </div>

                    <div class="auth-field">
                        <input type="password" name="newPass" required placeholder="Mật khẩu mới" class="auth-input">
                    </div>

                    <div class="auth-field">
                        <input type="password" name="confirmPass" required placeholder="Xác nhận mật khẩu mới" class="auth-input">
                    </div>

                    <c:if test="${not empty mess}">
                        <div class="auth-message error">
                            <i class="fas fa-circle-info"></i>
                            <span>${mess}</span>
                        </div>
                    </c:if>

                    <button type="submit" class="auth-button">Lưu thay đổi</button>
                </form>
            </div>
        </section>
    </main>
</body>
</html>
