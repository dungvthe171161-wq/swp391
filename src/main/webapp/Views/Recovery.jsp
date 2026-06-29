<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BetterHR - Xác minh mã PIN</title>
    <link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
</head>
<body>
    <main class="auth-shell">
        <section class="auth-brand-panel" aria-label="BetterHR">
            <div class="auth-brand-content">
                <h1>BetterHR</h1>
                <p>Xác minh mã bảo mật để bảo vệ tài khoản của bạn</p>
            </div>
        </section>

        <section class="auth-form-panel">
            <div class="auth-card">
                <a href="${pageContext.request.contextPath}/ForgotPassword" class="auth-back-link">
                    <i class="fas fa-arrow-left"></i>
                    <span>Quay lại nhập email</span>
                </a>

                <div class="auth-header">
                    <span class="auth-mini-brand">BetterHR</span>
                    <h2>Xác minh mã PIN</h2>
                    <p>Nhập mã gồm 6 chữ số đã được gửi tới email của bạn.</p>
                </div>

                <form class="auth-form" action="${pageContext.request.contextPath}/Recovery" method="post">
                    <div class="auth-field">
                        <input type="text" name="pin" value="" autofocus placeholder="Nhập mã PIN" class="auth-input" inputmode="numeric" maxlength="6" required>
                    </div>

                    <c:if test="${not empty pinExpireMessage}">
                        <div class="auth-message info">
                            <i class="fas fa-clock"></i>
                            <span>${pinExpireMessage}</span>
                        </div>
                    </c:if>

                    <c:if test="${not empty mess}">
                        <div class="auth-message error">
                            <i class="fas fa-exclamation-circle"></i>
                            <span>${mess}</span>
                        </div>
                    </c:if>

                    <button type="submit" class="auth-button">Xác nhận</button>
                </form>
            </div>
        </section>
    </main>
</body>
</html>
