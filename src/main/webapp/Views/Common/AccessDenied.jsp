<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Access Denied</title>
        <style>
            body {
                margin: 0;
                padding: 0;
                font-family: "Segoe UI", Arial, sans-serif;
                background: #f8fafc;
                color: #1e293b;
                display: flex;
                align-items: center;
                justify-content: center;
                min-height: 100vh;
            }
            .card {
                background: #ffffff;
                border-radius: 18px;
                padding: 48px 56px;
                box-shadow: 0 24px 60px rgba(15, 23, 42, 0.12);
                text-align: center;
                max-width: 460px;
            }
            .card h1 {
                margin: 0 0 16px;
                font-size: 28px;
            }
            .card p {
                margin: 0 0 28px;
                line-height: 1.6;
                color: #475569;
            }
            .card .back-btn {
                display: inline-block;
                padding: 12px 24px;
                border-radius: 999px;
                border: none;
                cursor: pointer;
                background: linear-gradient(135deg, #2563eb, #3b82f6);
                color: #ffffff;
                font-weight: 600;
                transition: transform 0.2s ease, box-shadow 0.2s ease;
            }
            .card .back-btn:hover {
                transform: translateY(-2px);
                box-shadow: 0 14px 28px rgba(37, 99, 235, 0.28);
                outline: none;
            }
            .card .back-btn:focus-visible {
                outline: 3px solid rgba(59, 130, 246, 0.5);
                outline-offset: 2px;
            }
        </style>
    </head>
    <body>
        <div class="card">
            <h1>Access Denied</h1>
            <p>
                <c:out value="${requestScope.errorMessage != null ? requestScope.errorMessage : 'You do not have permission to access this page.'}"/>
            </p>
            <button type="button" class="back-btn" onclick="handleBack()">Back to previous page</button>
        </div>
        <script>
            function handleBack() {
                if (window.history.length > 1) {
                    window.history.back();
                    return;
                }
                window.location.href = '${pageContext.request.contextPath}/homepage';
            }
        </script>
    </body>
</html>




