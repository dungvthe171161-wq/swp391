<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập - BetterHR</title>
    <link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
</head>
<body>
    <main class="auth-shell">
        <section class="auth-brand-panel" aria-label="BetterHR">
            <div class="auth-brand-content">
                <h1>BetterHR</h1>
                <p>Giải pháp quản trị nhân sự lấy con người làm trung tâm</p>
            </div>
        </section>

        <section class="auth-form-panel">
            <div class="auth-card">
                <a href="${pageContext.request.contextPath}/homepage" class="auth-home-link">
                    <i class="fas fa-arrow-left"></i>
                    <span>Về trang chủ</span>
                </a>

                <div class="auth-header">
                    <span class="auth-mini-brand">BetterHR</span>
                    <h2>Chào mừng bạn quay trở lại</h2>
                    <p>Vui lòng đăng nhập vào tài khoản của bạn</p>
                </div>

                <form class="auth-form" action="${pageContext.request.contextPath}/login" method="post">
                    <div class="auth-field">
                        <input type="text" name="user" value="${username}" class="auth-input" placeholder="Email hoặc tên đăng nhập" required>
                    </div>

                    <div class="auth-field">
                        <input type="password" name="pass" class="auth-input" placeholder="Mật khẩu" required>
                    </div>

                    <div class="auth-row">
                        <label class="auth-check" for="rememberMe">
                            <input type="checkbox" name="rememberMe" value="1" id="rememberMe" ${username != null ? "checked" : ""}>
                            <span>Ghi nhớ đăng nhập</span>
                        </label>
                        <a class="auth-link" href="${pageContext.request.contextPath}/Views/ForgotPassword.jsp">Quên mật khẩu?</a>
                    </div>

                    <c:if test="${not empty mess}">
                        <div class="auth-message error">
                            <i class="fas fa-exclamation-circle"></i>
                            <span>${mess}</span>
                        </div>
                    </c:if>

                    <c:if test="${not empty successMess}">
                        <div class="auth-message success">
                            <i class="fas fa-circle-check"></i>
                            <span>${successMess}</span>
                        </div>
                    </c:if>

                    <button type="submit" class="auth-button">Đăng nhập</button>
                </form>

                <div class="auth-divider"><span>Hoặc</span></div>

                <a href="${pageContext.request.contextPath}/auth/google" class="google-button">
                    <span class="google-icon">G</span>
                    <span>Tiếp tục với Google</span>
                </a>

                <p class="auth-footnote">Chưa có tài khoản? <a class="auth-link" href="${pageContext.request.contextPath}/register">Tạo tài khoản mới</a></p>
            </div>
        </section>
    </main>
</body>
</html>
