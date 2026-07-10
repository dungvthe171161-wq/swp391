<%-- 
    Document   : ViewRecruitment (Final Merged Version)
    Created on : Nov 2, 2025, 10:55:04 PM
    Author     : DELL
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý tin tuyển dụng chờ duyệt - BetterHR</title> <%-- THAY ĐỔI TIÊU ĐỀ --%>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
        <%-- Toàn bộ phần CSS giữ nguyên --%>
        <style>
            :root {
                --primary-color: #667eea;
                --secondary-color: #764ba2;
                --accent-color: #3b82f6;
                --success-color: #10b981;
                --warning-color: #f59e0b;
                --danger-color: #ef4444;
                --dark-color: #1f2937;
                --light-color: #f8f9fa;
                --text-color: #374151;
                --text-muted: #6b7280;
            }

            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                line-height: 1.6;
                color: var(--text-color);
                background-color: var(--light-color);
            }

            /* HR Dashboard Container */
            .hr-dashboard-container {
                min-height: 100vh;
                background-color: var(--light-color);
            }

            /* Header */
            .hr-header {
                background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
                color: white;
                padding: 1rem 0;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            }

            .header-content {
                max-width: 1400px;
                margin: 0 auto;
                padding: 0 2rem;
                display: flex;
                justify-content: space-between;
                align-items: center;
            }

            .logo-section {
                display: flex;
                align-items: center;
                gap: 1rem;
            }

            .logo-section i {
                font-size: 2rem;
            }

            .logo-section h1 {
                font-size: 1.8rem;
                font-weight: 600;
                margin: 0;
            }

            .header-actions {
                display: flex;
                align-items: center;
                gap: 1rem;
            }

            .search-box {
                display: flex;
                align-items: center;
                background: rgba(255,255,255,0.2);
                border-radius: 25px;
                padding: 0.5rem 1rem;
                gap: 0.5rem;
            }

            .search-box input {
                background: transparent;
                border: none;
                color: white;
                outline: none;
                font-size: 0.9rem;
                width: 200px;
            }

            .search-box input::placeholder {
                color: rgba(255, 255, 255, 0.7);
            }

            .notification-bell {
                position: relative;
                background: rgba(255,255,255,0.2);
                border-radius: 50%;
                width: 40px;
                height: 40px;
                display: flex;
                align-items: center;
                justify-content: center;
                cursor: pointer;
                transition: all 0.3s ease;
            }

            .notification-bell:hover {
                background: rgba(255,255,255,0.3);
            }

            .notification-count {
                position: absolute;
                top: -5px;
                right: -5px;
                background: var(--danger-color);
                color: white;
                border-radius: 50%;
                width: 20px;
                height: 20px;
                font-size: 0.7rem;
                display: flex;
                align-items: center;
                justify-content: center;
                font-weight: 600;
            }

            .user-profile {
                display: flex;
                align-items: center;
                gap: 0.5rem;
                background: rgba(255,255,255,0.2);
                padding: 0.5rem 1rem;
                border-radius: 25px;
                cursor: pointer;
                transition: all 0.3s ease;
            }

            .user-profile:hover {
                background: rgba(255,255,255,0.3);
            }

            .user-profile img {
                width: 32px;
                height: 32px;
                border-radius: 50%;
            }

            .btn-homepage {
                background: rgba(255,255,255,0.2);
                color: white;
                padding: 0.5rem 1rem;
                border-radius: 25px;
                text-decoration: none;
                display: flex;
                align-items: center;
                gap: 0.5rem;
                transition: all 0.3s ease;
                border: 1px solid rgba(255,255,255,0.3);
            }

            .btn-homepage:hover {
                background: rgba(255,255,255,0.3);
                transform: translateY(-2px);
                box-shadow: 0 4px 15px rgba(0,0,0,0.2);
                color: white;
            }

            /* Main Content Layout */
            .hr-main-content {
                display: flex;
                max-width: 1400px;
                margin: 0 auto;
                padding: 2rem;
                gap: 2rem;
            }

            /* Sidebar */
            .hr-sidebar {
                width: 280px;
                background: white;
                border-radius: 12px;
                box-shadow: 0 4px 20px rgba(0,0,0,0.1);
                padding: 1.5rem;
                height: fit-content;
            }

            .nav-section {
                margin-bottom: 2rem;
            }

            .nav-section h3 {
                font-size: 0.8rem;
                font-weight: 600;
                color: var(--text-muted);
                text-transform: uppercase;
                letter-spacing: 0.5px;
                margin-bottom: 1rem;
            }

            .nav-item {
                display: flex;
                align-items: center;
                gap: 0.75rem;
                padding: 0.75rem 1rem;
                border-radius: 8px;
                text-decoration: none;
                color: var(--text-muted);
                transition: all 0.3s ease;
                font-weight: 500;
                margin-bottom: 0.5rem;
            }

            .nav-item:hover {
                background: #f1f5f9;
                color: var(--primary-color);
                transform: translateX(4px);
                text-decoration: none;
            }

            .nav-item.active {
                background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
                color: white;
                box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
            }

            .nav-item i {
                font-size: 1.1rem;
                width: 20px;
                text-align: center;
            }

            /* Content Area */
            .hr-content-area {
                flex: 1;
                background: white;
                border-radius: 12px;
                box-shadow: 0 4px 20px rgba(0,0,0,0.1);
                padding: 2rem;
                min-height: 600px;
            }

            /* Custom styles for recruitment list */
            .recruitment-list-item {
                border-left: 4px solid var(--primary-color);
                transition: all 0.3s ease;
            }
            .recruitment-list-item:hover {
                transform: translateY(-3px);
                box-shadow: 0 8px 25px rgba(0,0,0,0.1);
                border-left-color: var(--accent-color);
            }
            .recruitment-title {
                color: var(--dark-color);
                font-weight: 600;
            }
            .recruitment-meta {
                font-size: 0.9rem;
                color: var(--text-muted);
            }
            .action-btn-custom {
                text-decoration: none;
                padding: 0.4rem 0.8rem;
                border-radius: 5px;
                font-size: 0.9rem;
                transition: all 0.2s ease;
                display: inline-block;
            }
            .action-btn-custom:hover {
                text-decoration: none;
                transform: translateY(-1px);
            }
            
            /* Đảm bảo các nút Clear và View hiển thị rõ */
            .btn-secondary {
                color: #fff !important;
                background-color: #6c757d !important;
                border-color: #6c757d !important;
                display: inline-block !important;
                visibility: visible !important;
            }
            .btn-secondary:hover {
                color: #fff !important;
                background-color: #5c636a !important;
                border-color: #565e64 !important;
            }
            
            .btn-outline-primary {
                color: #0d6efd !important;
                border-color: #0d6efd !important;
                background-color: transparent !important;
                display: inline-block !important;
                visibility: visible !important;
            }
            .btn-outline-primary:hover {
                color: #fff !important;
                background-color: #0d6efd !important;
                border-color: #0d6efd !important;
            }
            
            .btn-outline-success {
                color: #198754 !important;
                border-color: #198754 !important;
                background-color: transparent !important;
                display: inline-block !important;
                visibility: visible !important;
            }
            .btn-outline-success:hover {
                color: #fff !important;
                background-color: #198754 !important;
                border-color: #198754 !important;
            }
            
            .btn-outline-danger {
                color: #dc3545 !important;
                border-color: #dc3545 !important;
                background-color: transparent !important;
                display: inline-block !important;
                visibility: visible !important;
            }
            .btn-outline-danger:hover {
                color: #fff !important;
                background-color: #dc3545 !important;
                border-color: #dc3545 !important;
            }
            
            /* Đảm bảo card có background và border rõ ràng */
            .card {
                background-color: #fff !important;
                border: 1px solid #dee2e6 !important;
                box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075) !important;
            }
            
            .card-body {
                background-color: #fff !important;
                color: #212529 !important;
            }
            
            /* Đảm bảo text trong card hiển thị rõ */
            .card-title {
                color: #212529 !important;
            }
            
            .card-text {
                color: #6c757d !important;
            }
            
            /* Đảm bảo form và các input hiển thị rõ */
            .form-label {
                color: #212529 !important;
                font-weight: 500 !important;
            }
            
            .form-control {
                background-color: #fff !important;
                border: 1px solid #ced4da !important;
                color: #212529 !important;
            }
            
            .btn-primary {
                color: #fff !important;
                background-color: #0d6efd !important;
                border-color: #0d6efd !important;
                display: inline-block !important;
                visibility: visible !important;
            }
            
            .btn-primary:hover {
                color: #fff !important;
                background-color: #0b5ed7 !important;
                border-color: #0a58ca !important;
            }
            
            /* Thêm style cho trạng thái Applied - làm mờ */
            .status-applied {
                opacity: 0.5 !important;
            }
            
            /* Đảm bảo các nút trong status-applied vẫn hiển thị */
            .status-applied .action-btn-custom,
            .status-applied .btn {
                opacity: 1 !important;
            }

            /* Responsive Design */
            @media (max-width: 768px) {
                .hr-main-content {
                    flex-direction: column;
                    padding: 1rem;
                }

                .hr-sidebar {
                    width: 100%;
                    margin-bottom: 1rem;
                }

                .header-content {
                    flex-direction: column;
                    gap: 1rem;
                    padding: 0 1rem;
                }

                .header-actions {
                    flex-wrap: wrap;
                    justify-content: center;
                }

                .search-box input {
                    width: 150px;
                }
            }
        </style>
        <link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;500;600;700;800&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/hr-theme.css?v=hr-manager-shell-20260627-4">
    </head>
    <body>
        <div class="hr-dashboard-container hr-manager-page-shell">
            <!-- Header -->
            <header class="hr-header">
                <div class="header-content">
                    <div class="logo-section">
                        <i class="fas fa-eye"></i>
                        <h1>Tin tuyển dụng chờ duyệt</h1>
                    </div>
                    <div class="header-actions">
                        <div class="search-box">
                            <i class="fas fa-search"></i>
                            <input type="text" placeholder="Tìm tin tuyển dụng...">
                        </div>
                        <div class="notification-bell">
                            <i class="fas fa-bell"></i>
                            <span class="notification-count">3</span>
                        </div>
                        <div class="user-profile">
                            <img src="https://i.pravatar.cc/40" alt="Người dùng HR">
                            <span>Quản lý HR</span>
                            <i class="fas fa-chevron-down"></i>
                        </div>
                        <a href="${pageContext.request.contextPath}/HrHomeController" class="btn-homepage" title="Quay lại HR Home">
                            <i class="fas fa-home"></i>
                            <span>HR Home</span>
                        </a>
                    </div>
                </div>
            </header>

            <!-- Main Content -->
            <main class="hr-main-content">
                <c:set var="hrSidebarActive" value="recruitment" />
                <%@ include file="_HrManagerSidebar.jspf" %>

                <!-- Content Area -->
                <div class="hr-content-area">
                    <!-- Tiêu đề trang -->
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <%-- THAY ĐỔI TIÊU ĐỀ --%>
                        <h1 class="h2">Tin tuyển dụng chờ duyệt</h1>
                    </div>

                    <!-- FORM TÌM KIẾM VÀ LỌC -->
                    <div class="card mb-4">
                        <div class="card-body">
                            <form action="${pageContext.request.contextPath}/viewRecruitment" method="GET" class="row g-3 align-items-end">
                                <!-- Cột tìm kiếm theo Title -->
                                <div class="col-md-4">
                                    <label class="form-label">Tìm theo tiêu đề</label>
                                    <input type="text" class="form-control" name="searchByTitle" placeholder="Enter title..." value="${param.searchByTitle}">
                                </div>
                                <!-- Cột ngày bắt đầu -->
                                <div class="col-md-3">
                                    <label class="form-label">Start Date</label>
                                    <input type="date" class="form-control" id="startDate" name="startDate" value="${param.startDate}">
                                </div>

                                <!-- Cột ngày kết thúc -->
                                <div class="col-md-3">
                                    <label class="form-label">End Date</label>
                                    <input type="date" class="form-control" id="endDate" name="endDate" value="${param.endDate}">
                                </div>

                                <!-- Cột nút Apply -->
                                <div class="col-md-auto d-flex gap-2">
                                    <button type="submit" class="btn btn-primary">
                                        <i class="fas fa-filter me-1"></i> Apply Filter
                                    </button>
                                    <!-- Nút Clear dùng thẻ a -->
                                    <a href="${pageContext.request.contextPath}/viewRecruitment" class="btn btn-secondary">
                                        <i class="fas fa-times me-1"></i> Clear
                                    </a>
                                </div>
                            </form>
                        </div>
                    </div>
                    <!-- KẾT THÚC FORM -->

                    <!-- DANH SÁCH TUYỂN DỤNG -->
                    <c:if test="${not empty recruitment}">
                        <c:forEach var="rec" items="${recruitment}">
                            <!-- Xác định class cho card dựa trên status -->
                            <c:set var="cardClass" value="card mb-3 recruitment-list-item" />
                            <c:if test="${rec.status eq 'Applied'}">
                                <c:set var="cardClass" value="${cardClass} status-applied" />
                            </c:if>
                            
                            <div class="${cardClass}">
                                <div class="card-body">
                                    <div class="row align-items-center">
                                        <div class="col-md-8">
                                            <h5 class="card-title recruitment-title">${rec.title}</h5>
                                            <p class="card-text recruitment-meta">
                                                <%-- SỬA LỖI HIỂN THỊ NGÀY --%>
                                                <i class="far fa-calendar-alt me-1"></i> Posted on: <span class="badge bg-info text-dark">${rec.postedDate}</span>
                                                <span class="mx-2">|</span>
                                                <i class="fas fa-info-circle me-1"></i> Status: <span class="badge bg-info text-dark">${rec.status}</span>
                                                <span class="mx-2">|</span>
                                                <i class="fas fa-users me-1"></i> Applicants: ${rec.applicant}
                                            </p>
                                        </div>
                                        <div class="col-md-4 text-end">
                                            <a href="${pageContext.request.contextPath}/detailWaitingRecruitment?id=${rec.recruitmentId}" class="action-btn-custom btn btn-sm btn-outline-primary">
                                                <i class="fas fa-eye me-1"></i> View
                                            </a>
                                            
                                            <!-- Chỉ hiển thị nút Apply và Reject nếu status không phải là Applied -->
                                            <c:if test="${rec.status ne 'Applied'}">
                                                <a href="${pageContext.request.contextPath}/viewRecruitment?action=apply&id=${rec.recruitmentId}" 
                                                   class="action-btn-custom btn btn-sm btn-outline-success ms-2"
                                                   onclick="return confirm('Bạn có chắc muốn duyệt tin tuyển dụng này? Thao tác này không thể hoàn tác.');">
                                                    <i class="fas fa-paper-plane me-1"></i> Apply
                                                </a>
                                                <a href="${pageContext.request.contextPath}/viewRecruitment?action=reject&id=${rec.recruitmentId}" 
                                                   class="action-btn-custom btn btn-sm btn-outline-danger ms-2"
                                                   onclick="return confirm('Bạn có chắc muốn từ chối tin tuyển dụng này? Thao tác này không thể hoàn tác.');">
                                                    <i class="fas fa-times me-1"></i> Từ chối
                                                </a>
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:if>
                    
                    <c:if test="${not empty mess}">
                        <div class="error-message">
                            <i class="fas fa-exclamation-circle"></i>
                            <span>${mess}</span>
                        </div>
                    </c:if>

                    <c:if test="${empty recruitment}">
                        <div class="alert alert-warning text-center" role="alert">
                            <i class="fas fa-exclamation-triangle me-2"></i> No recruitment posts found.
                        </div>
                    </c:if>

                    <!-- PHÂN TRANG -->

                    <c:if test="${totalPages > 1}">
                        <nav aria-label="Page navigation" class="mt-4">
                            <ul class="pagination justify-content-center">
                                <c:if test="${currentPage > 1}">
                                    <li class="page-item">
                                        <a class="page-link" href="${pageContext.request.contextPath}/viewRecruitment?page=${currentPage - 1}&searchByTitle=${param.searchByTitle}&startDate=${param.startDate}&endDate=${param.endDate}" aria-label="Previous">
                                            <span aria-hidden="true">&laquo;</span>
                                        </a>
                                    </li>
                                </c:if>
                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <c:choose>
                                        <c:when test="${currentPage == i}">
                                            <li class="page-item active" aria-current="page">
                                                <span class="page-link">${i}</span>
                                            </li>
                                        </c:when>
                                        <c:otherwise>
                                            <li class="page-item">
                                                <a class="page-link" href="${pageContext.request.contextPath}/viewRecruitment?page=${i}&searchByTitle=${param.searchByTitle}&startDate=${param.startDate}&endDate=${param.endDate}">${i}</a>
                                            </li>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>

                                <c:if test="${currentPage < totalPages}">
                                    <li class="page-item">
                                        <a class="page-link" href="${pageContext.request.contextPath}/viewRecruitment?page=${currentPage + 1}&searchByTitle=${param.searchByTitle}&startDate=${param.startDate}&endDate=${param.endDate}" aria-label="Next">
                                            <span aria-hidden="true">&raquo;</span>
                                        </a>
                                    </li>
                                </c:if>

                            </ul>
                        </nav>
                    </c:if>
                </div>
            </main>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
                                                   function performSearch() {
                                                       const searchInput = document.getElementById('searchInput');
                                                       if (searchInput) {
                                                           alert('Chức năng tìm kiếm cho "' + searchInput.value + '" chưa được triển khai.');
                                                       }
                                                   }
        </script>
    </body>
</html>
