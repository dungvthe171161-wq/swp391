<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tạo tài khoản - BetterHR</title>
    <link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
</head>
<body>
    <main class="auth-shell">
        <section class="auth-brand-panel" aria-label="BetterHR">
            <div class="auth-brand-content">
                <h1>BetterHR</h1>
                <p>Tạo tài khoản để bắt đầu hành trình nhân sự chuyên nghiệp</p>
            </div>
        </section>

        <section class="auth-form-panel">
            <div class="auth-card wide">
                <a class="auth-back-link" href="${pageContext.request.contextPath}/login">
                    <i class="fas fa-arrow-left"></i>
                    <span>Quay lại đăng nhập</span>
                </a>

                <div class="auth-header">
                    <span class="auth-mini-brand">BetterHR</span>
                    <h2>Tạo tài khoản mới</h2>
                    <p>Điền thông tin để tạo tài khoản BetterHR.</p>
                </div>

                <form class="auth-form" action="${pageContext.request.contextPath}/register" method="post">
                    <div class="auth-field">
                        <input type="text" name="username" value="${username}" class="auth-input" placeholder="Tên đăng nhập" maxlength="100" required>
                    </div>

                    <div class="auth-field">
                        <input type="email" name="email" value="${email}" class="auth-input" placeholder="Email" maxlength="150" required>
                    </div>

                    <div class="auth-field">
                        <input type="password" name="password" class="auth-input" placeholder="Mật khẩu" minlength="6" maxlength="100" required>
                    </div>

                    <div class="auth-field">
                        <input type="password" name="confirmPassword" class="auth-input" placeholder="Xác nhận mật khẩu" minlength="6" maxlength="100" required>
                    </div>

                    <c:if test="${not empty mess}">
                        <div class="auth-message error">
                            <i class="fas fa-exclamation-circle"></i>
                            <span>${mess}</span>
                        </div>
                    </c:if>

                    <button type="submit" class="auth-button">Tạo tài khoản</button>
                </form>

                <p class="auth-footnote">Đã có tài khoản? <a class="auth-link" href="${pageContext.request.contextPath}/login">Đăng nhập</a></p>
            </div>
        </section>
    </main>
</body>
</html>
