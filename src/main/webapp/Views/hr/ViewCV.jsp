<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Xem CV Ứng viên</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-9ndCyUaIbzAi2FUVXJi0CjmCapSmO7SnpJef0486qhLnuZ2cdeRhO02iuK6FUUVM" crossorigin="anonymous">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" integrity="sha512-iecdLmaskl7CVkqkXNQ/ZH/XLlvWZOJyj7Yy7tcenmpD1ypASozpmT/E0iPtmFIB46ZmdtAc9eNBvH0H/ZpiBw==" crossorigin="anonymous" referrerpolicy="no-referrer" />
        <style>
            body {
                background-color: #f8f9fa; /* Nền xám nhạt */
                font-family: Arial, sans-serif;
            }
            .cv-container {
                margin-top: 30px;
                margin-bottom: 30px; /* Thêm khoảng cách ở dưới cho cân đối */
                padding: 25px; /* Thêm padding để tạo khoảng cách với các cạnh bên trong */
                background-color: white;
                border-radius: 8px;
                box-shadow: 0 2px 5px rgba(0,0,0,0.05); /* Đổ bóng nhẹ để làm nổi bật khung */
            }
            .cv-header {
                padding-bottom: 20px;
                margin-bottom: 20px;
                border-bottom: 1px solid #dee2e6;
            }
            .cv-image-container {
                background-color: #e9ecef; /* Nền xám nhạt cho phần ảnh */
                display: flex;
                align-items: center;
                justify-content: center;
                min-height: 600px; /* Chiều cao tối thiểu */
                border-radius: 5px; /* Bo góc cho khung ảnh */
                overflow: hidden; /* Ảnh không bị tràn ra ngoài khung */
            }
            .cv-image {
                max-width: 100%;
                max-height: 80vh; /* Giới hạn chiều cao ảnh */
                object-fit: contain; /* Đảm bảo ảnh hiển thị đầy đủ, không bị méo */
            }
            .cv-info-container {
                display: flex;
                flex-direction: column;
                height: 100%; /* Giúp các phần tử con có thể căn chỉnh theo chiều cao */
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
                display: block; /* Hiển thị form dưới dạng khối, mỗi form trên một dòng */
                margin-bottom: 10px;
            }
            .recruitment-info {
                background-color: #f8f9fa;
                padding: 15px;
                border-radius: 5px;
                margin-top: auto; /* Đẩy phần này xuống dưới cùng của cột phải */
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

                <div class="row g-0"> <!-- g-0 để bỏ khoảng cách giữa 2 cột -->
                    <!-- Phần bên trái: Ảnh CV (2/3) -->
                    <div class="col-md-8">
                        <div class="cv-image-container">
                            <c:choose>
                                <c:when test="${not empty g.cv}">
                                    <%-- Lấy phần mở rộng của file và chuyển về chữ thường để so sánh --%>
                                    <c:set var="fileExtension" value="${fn:toLowerCase(fn:substringAfter(g.cv, '.'))}" />

                                    <c:choose>
                                        <%-- Nếu là file ảnh (jpg, png, gif, jpeg) thì hiển thị bằng thẻ img --%>
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
                                        <%-- Nếu là file khác (doc, docx) thì hiển thị một liên kết để tải về --%>
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

                    <!-- Phần bên phải: Thông tin và Nút (1/3) -->
                    <div class="col-md-4 ps-4"> <!-- ps-4: padding-start để tạo khoảng cách giữa cột ảnh và cột info -->
                        <div class="cv-info-container">
                            <div class="info-section">
                                <p><strong>Họ và tên:</strong> ${g.fullName}</p>
                                <p><strong>Email:</strong> ${g.email}</p>
                                <p><strong>Điện thoại:</strong> ${g.phone}</p>
                                <p><strong>Trạng thái:</strong> ${g.status}</p>
                                <p><strong>Ngày ứng tuyển:</strong> ${g.appliedDate}</p>
                            </div>

                            <div class="action-buttons">
                                <c:if test="${g.status eq 'Processing' and r.applicant > 0}">
                                    <form action="${pageContext.request.contextPath}/viewCV" method="post">
                                        <input name="action" value="apply" type="hidden">
                                        <input name="guestId" value="${g.guestId}" type="hidden">
                                        <button type="submit" class="btn btn-success w-100">Accept</button>
                                    </form>
                                    <form action="${pageContext.request.contextPath}/viewCV" method="post">
                                        <input name="action" value="reject" type="hidden">
                                        <input name="guestId" value="${g.guestId}" type="hidden">
                                        <button type="submit" class="btn btn-danger w-100">Reject</button>
                                    </form>
                                </c:if>
                            </div>

                            <div class="recruitment-info">
                                <h5>Yêu cầu tuyển dụng: ${r.title}</h5>
                                <p><strong>Vị trí:</strong> ${r.location}</p>
                                <p><strong>Lương:</strong> ${r.salary}</p>
                                <p><strong>Số lượng còn lại:</strong> ${r.applicant}</p>
                                <p><strong>Yêu cầu:</strong> ${r.requirement}</p>
                            </div>

                            <c:if test="${not empty mess}">
                                <div class="error-message">
                                    <span>${mess}</span>
                                </div>
                            </c:if>

                            <a href="${pageContext.request.contextPath}/candidates" class="btn btn-secondary w-100 mt-3">Quay lại</a>                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
