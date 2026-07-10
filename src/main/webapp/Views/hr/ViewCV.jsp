<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Xem CV ứng viên</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-9ndCyUaIbzAi2FUVXJi0CjmCapSmO7SnpJef0486qhLnuZ2cdeRhO02iuK6FUUVM" crossorigin="anonymous">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" integrity="sha512-iecdLmaskl7CVkqkXNQ/ZH/XLlvWZOJyj7Yy7tcenmpD1ypASozpmT/E0iPtmFIB46ZmdtAc9eNBvH0H/ZpiBw==" crossorigin="anonymous" referrerpolicy="no-referrer" />
        <style>
            body {
                background-color: #f8f9fa;
                font-family: "Hanken Grotesk", "Inter", "Segoe UI", Arial, sans-serif;
            }
            .cv-container {
                margin-top: 30px;
                margin-bottom: 30px;
                padding: 25px;
                background-color: white;
                border-radius: 8px;
                box-shadow: 0 2px 5px rgba(0,0,0,0.05);
            }
            .cv-header {
                padding-bottom: 20px;
                margin-bottom: 20px;
                border-bottom: 1px solid #dee2e6;
            }
            .cv-image-container {
                background-color: #e9ecef;
                display: flex;
                align-items: center;
                justify-content: center;
                min-height: 600px;
                border-radius: 5px;
                overflow: hidden;
            }
            .cv-image {
                max-width: 100%;
                max-height: 80vh;
                object-fit: contain;
            }
            .cv-info-container {
                display: flex;
                flex-direction: column;
                height: 100%;
            }
            .info-section p {
                margin-bottom: 10px;
                text-align: left;
            }
            .info-section strong {
                color: #495057;
            }
            .action-buttons {
                margin-top: 20px;
                margin-bottom: 20px;
            }
            .action-buttons form {
                display: block;
                margin-bottom: 10px;
            }
            .recruitment-info {
                background-color: #f8f9fa;
                padding: 15px;
                border-radius: 5px;
                margin-top: auto;
                text-align: left;
            }
            .recruitment-info p {
                margin-bottom: 5px;
            }
            .recruitment-info strong {
                color: #343a40;
            }
            .error-message {
                background-color: #f8d7da;
                color: #721c24;
                border: 1px solid #f5c6cb;
                padding: 10px;
                border-radius: 4px;
                margin-top: 15px;
                text-align: center;
            }
        </style>
        <link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;500;600;700;800&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/hr-theme.css">
    </head>
    <body>
        <div class="container">
            <div class="cv-container">
                <!-- Phần tiêu đề -->
                <div class="cv-header">
                    <h1 class="text-center">CV của ${g.fullName}</h1>
                </div>

                <div class="row g-0">
                    <!-- Phần bên trái: ảnh CV -->
                    <div class="col-md-8">
                        <div class="cv-image-container">
                            <c:choose>
                                <c:when test="${not empty g.cv}">
                                    <%-- Lấy phần mở rộng của file và chuyển về chữ thường để so sánh --%>
                                    <c:set var="fileExtension" value="${fn:toLowerCase(fn:substringAfter(g.cv, '.'))}" />

                                    <c:choose>
                                        <%-- Nếu là file ảnh thì hiển thị bằng thẻ img --%>
                                        <c:when test="${fileExtension eq 'jpg' or fileExtension eq 'jpeg' or fileExtension eq 'png' or fileExtension eq 'gif'}">
                                            <img src="${pageContext.request.contextPath}/Upload/cvs/${g.cv}" alt="CV Image" class="cv-image">
                                        </c:when>
                                        <%-- Nếu là file PDF thì hiển thị bằng iframe để xem trực tiếp --%>
                                        <c:when test="${fileExtension eq 'pdf'}">
                                            <iframe src="${pageContext.request.contextPath}/Upload/cvs/${g.cv}" 
                                                    class="cv-image" 
                                                    style="width: 100%; height: 100%; min-height: 600px; border: none;">
                                                Trình duyệt của bạn không hỗ trợ hiển thị PDF.
                                                <a href="${pageContext.request.contextPath}/Upload/cvs/${g.cv}" target="_blank" class="btn btn-primary">
                                                    <i class="fas fa-download"></i> Tải xuống CV
                                                </a>
                                            </iframe>
                                        </c:when>
                                        <%-- Nếu là file khác thì hiển thị liên kết để tải về --%>
                                        <c:otherwise>
                                            <div class="text-center mt-5">
                                                <i class="fas fa-file fa-5x text-primary"></i>
                                                <p class="mt-2">CV là một tài liệu.</p>
                                                <a href="${pageContext.request.contextPath}/Upload/cvs/${g.cv}" target="_blank" class="btn btn-primary">
                                                    <i class="fas fa-download"></i> Xem/Tải xuống CV
                                                </a>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </c:when>
                                <c:otherwise>
                                    <p class="text-center mt-5">Ứng viên này chưa tải lên CV.</p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <!-- Phần bên phải: thông tin và nút -->
                    <div class="col-md-4 ps-4">
                        <div class="cv-info-container">
                            <div class="info-section">
                                <p><strong>Họ và tên:</strong> ${g.fullName}</p>
                                <p><strong>Email:</strong> ${g.email}</p>
                                <p><strong>Điện thoại:</strong> ${g.phone}</p>
                                <p><strong>Trạng thái:</strong> ${not empty reviewStatus ? reviewStatus : g.status}</p>
                                <p><strong>Ngày ứng tuyển:</strong> ${g.appliedDate}</p>
                            </div>

                            <div class="action-buttons">
                                <c:if test="${canReviewCandidate}">
                                    <form action="${pageContext.request.contextPath}/viewCV" method="post">
                                        <input name="action" value="apply" type="hidden">
                                        <input name="guestId" value="${g.guestId}" type="hidden">
                                        <button type="submit" class="btn btn-success w-100" ${canAcceptCandidate ? '' : 'disabled'}>Accept</button>
                                    </form>
                                    <form action="${pageContext.request.contextPath}/viewCV" method="post">
                                        <input name="action" value="reject" type="hidden">
                                        <input name="guestId" value="${g.guestId}" type="hidden">
                                        <button type="submit" class="btn btn-danger w-100">Reject</button>
                                    </form>
                                    <c:if test="${not canAcceptCandidate}">
                                        <div class="text-danger small text-center">Tin tuyển dụng này đã hết chỉ tiêu.</div>
                                    </c:if>
                                </c:if>
                            </div>

                            <div class="recruitment-info">
                                <h5>Yêu cầu tuyển dụng: ${not empty r ? r.title : 'Chưa xác định'}</h5>
                                <p><strong>Vị trí:</strong> ${not empty r ? r.location : 'Chưa xác định'}</p>
                                <p><strong>Lương:</strong> ${not empty r ? r.salary : 'Chưa xác định'}</p>
                                <p><strong>Số lượng còn lại:</strong> ${not empty r ? r.applicant : 'Chưa xác định'}</p>
                                <p><strong>Yêu cầu:</strong> ${not empty r ? r.requirement : 'Chưa xác định'}</p>
                            </div>

                            <c:if test="${not empty mess}">
                                <div class="error-message">
                                    <span>${mess}</span>
                                </div>
                            </c:if>

                            <a href="${pageContext.request.contextPath}/candidates" class="btn btn-secondary w-100 mt-3">Quay lại</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
