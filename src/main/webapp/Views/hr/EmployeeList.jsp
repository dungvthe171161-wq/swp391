<%-- 
    Document   : EmployeeList
    Created on : Oct 23, 2025, 5:30:24 PM
    Author     : admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Danh sách nhân viên - BetterHR</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
        
        <style>
            :root {
                --primary-color: #667eea;
                --secondary-color: #764ba2;
                --success-color: #10b981;
                --warning-color: #f59e0b;
                --danger-color: #ef4444;
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
                background-color: var(--light-color);
                color: var(--text-color);
            }
            
            /* Main Container */
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
            
            /* Statistics Cards */
            .stats-card {
                background: white;
                border-radius: 12px;
                padding: 1.5rem;
                box-shadow: 0 2px 10px rgba(0,0,0,0.05);
                border-left: 4px solid var(--primary-color);
                transition: all 0.3s ease;
            }
            
            .stats-card:hover {
                transform: translateY(-2px);
                box-shadow: 0 8px 25px rgba(0,0,0,0.15);
            }
            
            .stats-number {
                font-size: 2rem;
                font-weight: 700;
                color: var(--primary-color);
                margin-bottom: 0.5rem;
            }
            
            .stats-label {
                color: var(--text-muted);
                font-size: 0.9rem;
                font-weight: 500;
            }
            
            /* Status Badges */
            .status-badge {
                padding: 0.25rem 0.75rem;
                border-radius: 20px;
                font-size: 0.8rem;
                font-weight: 500;
                text-transform: uppercase;
            }
            
            .status-active {
                background: #d1fae5;
                color: #065f46;
            }
            
            .status-probation {
                background: #fef3c7;
                color: #92400e;
            }
            
            .status-intern {
                background: #dbeafe;
                color: #1e40af;
            }
            
            .status-resigned {
                background: #fee2e2;
                color: #991b1b;
            }
            
            .status-terminated {
                background: #f3f4f6;
                color: #374151;
            }
            
            /* Error Message */
            .error-message {
                background: #fee2e2;
                color: #991b1b;
                padding: 1rem;
                border-radius: 8px;
                margin-bottom: 2rem;
                border: 1px solid #fecaca;
            }
            
            /* No Data Message */
            .no-data {
                text-align: center;
                padding: 4rem 2rem;
                background: white;
                border-radius: 12px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.05);
            }
            
            .no-data i {
                font-size: 4rem;
                color: #cbd5e1;
                margin-bottom: 1rem;
            }
            
            .no-data h3 {
                color: var(--text-muted);
                margin-bottom: 0.5rem;
            }
            
            .no-data p {
                color: #94a3b8;
            }
            
            /* Table Styling */
            .table-sm {
                font-size: 0.875rem;
            }
            
            .table-sm th {
                padding: 0.5rem;
                font-weight: 600;
                font-size: 0.8rem;
                text-transform: uppercase;
                letter-spacing: 0.5px;
            }
            
            .table-sm td {
                padding: 0.75rem 0.5rem;
                vertical-align: middle;
            }
            
            .table-sm .btn-group-sm .btn {
                padding: 0.25rem 0.5rem;
                font-size: 0.75rem;
                border-radius: 4px;
            }
            
            .table-sm .btn-group-sm .btn:hover {
                transform: translateY(-1px);
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            }
            
            .table-sm .fw-semibold {
                font-weight: 600;
                color: var(--text-color);
            }
            
            .table-sm .fw-medium {
                font-weight: 500;
                color: var(--text-color);
            }
            
            .table-sm small.text-muted {
                font-size: 0.75rem;
                color: var(--text-muted);
            }
            
            /* Filter Section Styling */
            .card {
                border: none;
                box-shadow: 0 2px 10px rgba(0,0,0,0.05);
                border-radius: 12px;
            }
            
            .card-header {
                background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
                color: white;
                border-radius: 12px 12px 0 0 !important;
                border: none;
            }
            
            .card-header h5 {
                margin: 0;
                font-weight: 600;
            }
            
            .form-label {
                font-weight: 600;
                color: var(--text-color);
                margin-bottom: 0.5rem;
            }
            
            .form-control, .form-select {
                border: 1px solid #e2e8f0;
                border-radius: 8px;
                padding: 0.5rem 0.75rem;
                transition: all 0.3s ease;
            }
            
            .form-control:focus, .form-select:focus {
                border-color: var(--primary-color);
                box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
            }
            
            /* Pagination Styling */
            .pagination {
                margin: 0;
            }
            
            .page-link {
                color: var(--primary-color);
                border: 1px solid #e2e8f0;
                padding: 0.5rem 0.75rem;
                margin: 0 2px;
                border-radius: 8px;
                transition: all 0.3s ease;
            }
            
            .page-link:hover {
                background-color: var(--primary-color);
                color: white;
                border-color: var(--primary-color);
                transform: translateY(-1px);
            }
            
            .page-item.active .page-link {
                background-color: var(--primary-color);
                border-color: var(--primary-color);
                color: white;
            }
            
            .page-item.disabled .page-link {
                color: #6c757d;
                background-color: #f8f9fa;
                border-color: #e2e8f0;
            }
            
            /* Badge Styling */
            .badge {
                font-size: 0.75rem;
                padding: 0.375rem 0.75rem;
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
                
                .pagination {
                    flex-wrap: wrap;
                }
                
                .page-link {
                    padding: 0.375rem 0.5rem;
                    font-size: 0.875rem;
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
                        <i class="fas fa-users-cog"></i>
                        <h1>Quản lý nhân viên</h1>
                    </div>
                    <div class="header-actions">
                        <div class="notification-bell">
                            <i class="fas fa-bell"></i>
                            <span class="notification-count">3</span>
                        </div>
                        <div class="user-profile">
                            <img src="https://i.pravatar.cc/40" alt="Người dùng HR">
                            <span>Quản lý HR</span>
                            <i class="fas fa-chevron-down"></i>
                        </div>
                        <a href="/homepage" class="btn-homepage" title="Về trang chủ">
                            <i class="fas fa-home"></i>
                            <span>Trang chủ</span>
                        </a>
                    </div>
                </div>
            </header>

            <!-- Main Content -->
            <main class="hr-main-content">
                <c:set var="hrSidebarActive" value="employee-list" />
                <%@ include file="_HrManagerSidebar.jspf" %>

                <!-- Content Area -->
                <div class="hr-content-area">
                    <!-- Page Header -->
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <div>
                            <h1 class="h2 mb-2">Danh sách nhân viên</h1>
                            <p class="text-muted">View and manage all employees in the system</p>
                        </div>
                        <a href="${pageContext.request.contextPath}/HrHomeController" class="btn btn-secondary">
                            <i class="fas fa-arrow-left me-2"></i>Quay lại HR Home
                        </a>
                    </div>

                    <!-- Filter Section -->
                    <div class="card mb-4">
                        <div class="card-header">
                            <h5 class="mb-0">
                                <i class="fas fa-filter me-2"></i>Tìm kiếm
                            </h5>
                        </div>
                        <div class="card-body">
                            <form method="GET" action="${pageContext.request.contextPath}/hr/employee-list" id="filterForm">
                                <input type="hidden" name="page" value="1" id="pageInput">
                                <div class="row g-3">
                                    <div class="col-md-8">
                                        <label for="search" class="form-label">Tìm kiếm</label>
                                        <input type="text" class="form-control" id="search" name="search" 
                                               placeholder="Tìm theo tên hoặc vị trí..."
                                               value="${searchKeyword}">
                                    </div>
                                </div>
                                <div class="row mt-3">
                                    <div class="col-12">
                                        <button type="submit" class="btn btn-primary me-2">
                                            <i class="fas fa-search me-1"></i>Tìm kiếm
                                        </button>
                                        <a href="${pageContext.request.contextPath}/hr/employee-list" class="btn btn-outline-secondary">
                                            <i class="fas fa-times me-1"></i>Clear
                                        </a>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>

                    <!-- Success Message -->
                    <c:if test="${not empty success}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="fas fa-check-circle me-2"></i>
                            ${success}
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>

                    <!-- Error Message -->
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="fas fa-exclamation-triangle me-2"></i>
                            ${error}
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>

                    <!-- Statistics Summary -->
                    <c:if test="${not empty allEmployeesForStats}">
                        <div class="row mb-4">
                            <div class="col-md-3">
                                <div class="stats-card">
                                    <div class="stats-number">${totalCount}</div>
                                    <div class="stats-label">Tổng nhân viên</div>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="stats-card">
                                    <div class="stats-number">
                                        <c:set var="activeCount" value="0" />
                                        <c:forEach var="emp" items="${allEmployeesForStats}">
                                            <c:if test="${emp.status == 'Active'}">
                                                <c:set var="activeCount" value="${activeCount + 1}" />
                                            </c:if>
                                        </c:forEach>
                                        ${activeCount}
                                    </div>
                                    <div class="stats-label">Nhân viên chính thức</div>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="stats-card">
                                    <div class="stats-number">
                                        <c:set var="probationCount" value="0" />
                                        <c:forEach var="emp" items="${allEmployeesForStats}">
                                            <c:if test="${emp.status == 'Probation'}">
                                                <c:set var="probationCount" value="${probationCount + 1}" />
                                            </c:if>
                                        </c:forEach>
                                        ${probationCount}
                                    </div>
                                    <div class="stats-label">Đang thử việc</div>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="stats-card">
                                    <div class="stats-number">
                                        <c:set var="internCount" value="0" />
                                        <c:forEach var="emp" items="${allEmployeesForStats}">
                                            <c:if test="${emp.status == 'Intern'}">
                                                <c:set var="internCount" value="${internCount + 1}" />
                                            </c:if>
                                        </c:forEach>
                                        ${internCount}
                                    </div>
                                    <div class="stats-label">Thực tập sinh</div>
                                </div>
                            </div>
                        </div>
                    </c:if>

                    <!-- Employee Table -->
                    <div class="card">
                        <div class="card-header d-flex justify-content-between align-items-center">
                            <h5 class="mb-0">
                                <i class="fas fa-users me-2"></i>Nhân viên
                                <c:if test="${not empty totalCount}">
                                    <span class="badge bg-primary ms-2">${totalCount} tổng</span>
                                </c:if>
                            </h5>
                            <div class="text-muted">
                                Hiển thị ${(currentPage - 1) * pageSize + 1} đến
                                ${currentPage * pageSize > totalCount ? totalCount : currentPage * pageSize} 
                                trên ${totalCount} bản ghi
                            </div>
                        </div>
                        <div class="card-body p-0">
                            <div class="table-responsive">
                                <c:choose>
                                    <c:when test="${not empty employees}">
                                        <table class="table table-striped table-hover table-sm mb-0">
                                            <thead class="table-dark">
                                                <tr>
                                                    <th style="width: 60px;">ID</th>
                                                    <th style="width: 200px;">Họ tên</th>
                                                    <th style="width: 120px;">Vị trí</th>
                                                    <th style="width: 120px;">Phòng ban</th>
                                                    <th style="width: 80px;">Trạng thái</th>
                                                    <th style="width: 100px;">Thao tác</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="employee" items="${employees}" varStatus="status">
                                                    <tr>
                                                        <td class="fw-bold">${(currentPage - 1) * pageSize + status.index + 1}</td>
                                                        <td>
                                                            <div class="d-flex align-items-center">
                                                                <div class="me-2" style="width: 28px; height: 28px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 50%; display: flex; align-items: center; justify-content: center; color: white; font-weight: 600; font-size: 0.8rem;">
                                                                    ${employee.fullName.charAt(0)}
                                                                </div>
                                                                <div>
                                                                    <div class="fw-semibold">${employee.fullName}</div>
                                                                    <small class="text-muted">${employee.email}</small>
                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div class="fw-medium">${employee.position}</div>
                                                            <small class="text-muted">${employee.phone}</small>
                                                        </td>
                                                        <td>
                                                            <div class="fw-medium">${employee.departmentName}</div>
                                                            <small class="text-muted">${employee.employmentPeriod}</small>
                                                        </td>
                                                        <td>
                                                            <span class="status-badge status-${employee.status.toLowerCase()}">${employee.status}</span>
                                                        </td>
                                                        <td>
                                                            <div class="btn-group btn-group-sm">
                                                                <button class="btn btn-outline-warning btn-sm" title="Sửa nhân viên"
                                                                        style="padding: 0.25rem 0.5rem;" 
                                                                        data-employee-id="${employee.employeeId}"
                                                                        data-employee-name="${fn:escapeXml(employee.fullName)}"
                                                                        data-employee-email="${fn:escapeXml(employee.email)}"
                                                                        data-employee-phone="${fn:escapeXml(employee.phone)}"
                                                                        data-employee-position="${fn:escapeXml(employee.position)}"
                                                                        data-employee-period="${fn:escapeXml(employee.employmentPeriod)}"
                                                                        data-employee-status="${fn:escapeXml(employee.status)}"
                                                                        data-employee-dept="${employee.departmentId}"
                                                                        onclick="showEditModalFromData(this)">
                                                                    <i class="fas fa-edit" style="font-size: 0.8rem;"></i>
                                                                </button>
                                                                <button class="btn btn-outline-success btn-sm" title="Cập nhật trạng thái"
                                                                        style="padding: 0.25rem 0.5rem;" 
                                                                        onclick="showStatusModal(${employee.employeeId}, '${employee.status}', '${employee.fullName}')">
                                                                    <i class="fas fa-user-check" style="font-size: 0.8rem;"></i>
                                                                </button>
                                                                <button class="btn btn-outline-danger btn-sm" title="Xóa nhân viên"
                                                                        style="padding: 0.25rem 0.5rem;" 
                                                                        onclick="confirmDeleteEmployee(${employee.employeeId}, '${fn:escapeXml(employee.fullName)}')">
                                                                    <i class="fas fa-trash" style="font-size: 0.8rem;"></i>
                                                                </button>
                                                            </div>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="no-data">
                                            <i class="fas fa-users-slash"></i>
                                            <h3>Không tìm thấy nhân viên</h3>
                                            <p>Không có nhân viên phù hợp với điều kiện tìm kiếm.</p>
                                            <a href="${pageContext.request.contextPath}/hr/create-employee" class="btn btn-primary mt-3">
                                                <i class="fas fa-user-plus me-2"></i>Tạo nhân viên đầu tiên
                                            </a>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>

                    <!-- Pagination -->
                    <c:if test="${totalPages > 1}">
                        <nav aria-label="Phân trang nhân viên" class="mt-4">
                            <ul class="pagination justify-content-center">
                                <!-- Previous button -->
                                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                    <a class="page-link" href="${pageContext.request.contextPath}/hr/employee-list?page=${currentPage - 1}&search=${searchKeyword}">
                                        <i class="fas fa-chevron-left"></i> Trước
                                    </a>
                                </li>
                                
                                <!-- Page numbers -->
                                <c:forEach begin="1" end="${totalPages}" var="pageNum">
                                    <c:choose>
                                        <c:when test="${pageNum == currentPage}">
                                            <li class="page-item active">
                                                <span class="page-link">${pageNum}</span>
                                            </li>
                                        </c:when>
                                        <c:when test="${pageNum <= 3 || pageNum > totalPages - 3 || (pageNum >= currentPage - 1 && pageNum <= currentPage + 1)}">
                                            <li class="page-item">
                                                <a class="page-link" href="${pageContext.request.contextPath}/hr/employee-list?page=${pageNum}&search=${searchKeyword}">
                                                    ${pageNum}
                                                </a>
                                            </li>
                                        </c:when>
                                        <c:when test="${pageNum == 4 && currentPage > 5}">
                                            <li class="page-item disabled">
                                                <span class="page-link">...</span>
                                            </li>
                                        </c:when>
                                        <c:when test="${pageNum == totalPages - 3 && currentPage < totalPages - 4}">
                                            <li class="page-item disabled">
                                                <span class="page-link">...</span>
                                            </li>
                                        </c:when>
                                    </c:choose>
                                </c:forEach>
                                
                                <!-- Next button -->
                                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                    <a class="page-link" href="${pageContext.request.contextPath}/hr/employee-list?page=${currentPage + 1}&search=${searchKeyword}">
                                        Sau <i class="fas fa-chevron-right"></i>
                                    </a>
                                </li>
                            </ul>
                        </nav>
                    </c:if>
                </div>
            </main>
        </div>

        <!-- Status Update Modal -->
        <div class="modal fade" id="statusModal" tabindex="-1" aria-labelledby="statusModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="statusModalLabel">Cập nhật trạng thái nhân viên</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                    </div>
                    <form id="statusUpdateForm" method="POST" action="${pageContext.request.contextPath}/hr/employee-list">
                        <div class="modal-body">
                            <input type="hidden" name="action" value="updateStatus">
                            <input type="hidden" name="employeeId" id="modalEmployeeId">
                            
                            <div class="mb-3">
                                <label for="employeeName" class="form-label">Tên nhân viên</label>
                                <input type="text" class="form-control" id="employeeName" readonly>
                            </div>
                            
                            <div class="mb-3">
                                <label for="currentStatus" class="form-label">Trạng thái hiện tại</label>
                                <input type="text" class="form-control" id="currentStatus" readonly>
                            </div>
                            
                            <div class="mb-3">
                                <label for="newStatus" class="form-label">Trạng thái mới</label>
                                <select class="form-select" name="status" id="newStatus" required>
                                    <option value="Active">Chính thức</option>
                                    <option value="Probation">Thử việc</option>
                                    <option value="Intern">Thực tập</option>
                                </select>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                            <button type="submit" class="btn btn-success">
                                <i class="fas fa-save me-2"></i>Cập nhật trạng thái
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Edit Employee Modal -->
        <div class="modal fade" id="editModal" tabindex="-1" aria-labelledby="editModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="editModalLabel">Sửa thông tin nhân viên</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                    </div>
                    <form id="editEmployeeForm" method="POST" action="${pageContext.request.contextPath}/hr/employee-list" onsubmit="return debugFormSubmit()">
                        <div class="modal-body">
                            <input type="hidden" name="action" value="editEmployee">
                            <input type="hidden" name="employeeId" id="editEmployeeId">
                            
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="editFullName" class="form-label">Họ và tên *</label>
                                    <input type="text" class="form-control" id="editFullName" name="fullName" required>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label for="editEmail" class="form-label">Email *</label>
                                    <input type="email" class="form-control" id="editEmail" name="email" required>
                                </div>
                            </div>
                            
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="editPhone" class="form-label">Số điện thoại</label>
                                    <input type="tel" class="form-control" id="editPhone" name="phone">
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label for="editPosition" class="form-label">Vị trí</label>
                                    <input type="text" class="form-control" id="editPosition" name="position" readonly style="background-color: #f8f9fa;">
                                </div>
                            </div>
                            
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="editStartDate" class="form-label">Ngày bắt đầu</label>
                                    <input type="date" class="form-control" id="editStartDate" name="startDate">
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label for="editEndDate" class="form-label">Ngày kết thúc</label>
                                    <input type="date" class="form-control" id="editEndDate" name="endDate">
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                            <button type="submit" class="btn btn-warning">
                                <i class="fas fa-save me-2"></i>Cập nhật nhân viên
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            // Update status function - no longer needed since we're using the same controller

            // Debug form submit function
            function debugFormSubmit() {
                const form = document.getElementById('editEmployeeForm');
                const formData = new FormData(form);
                
                console.log('Form submit debug:');
                for (let [key, value] of formData.entries()) {
                    console.log(key + ':', value);
                }
                
                return true; // Allow form to submit
            }

            // Edit employee modal functionality using data attributes
            function showEditModalFromData(button) {
                const employeeId = button.getAttribute('data-employee-id');
                const fullName = button.getAttribute('data-employee-name');
                const email = button.getAttribute('data-employee-email');
                const phone = button.getAttribute('data-employee-phone');
                const position = button.getAttribute('data-employee-position');
                const employmentPeriod = button.getAttribute('data-employee-period');
                const departmentId = button.getAttribute('data-employee-dept');
                
                console.log('showEditModalFromData called with:');
                console.log('employeeId:', employeeId);
                console.log('fullName:', fullName);
                console.log('email:', email);
                console.log('phone:', phone);
                console.log('position:', position);
                console.log('employmentPeriod:', employmentPeriod);
                console.log('departmentId:', departmentId);
                
                document.getElementById('editEmployeeId').value = employeeId;
                document.getElementById('editFullName').value = fullName;
                document.getElementById('editEmail').value = email;
                document.getElementById('editPhone').value = phone;
                document.getElementById('editPosition').value = position;
                
                // Parse employment period to extract start and end dates
                if (employmentPeriod && employmentPeriod.includes(' - ')) {
                    const dates = employmentPeriod.split(' - ');
                    document.getElementById('editStartDate').value = dates[0];
                    document.getElementById('editEndDate').value = dates[1];
                } else if (employmentPeriod) {
                    document.getElementById('editStartDate').value = employmentPeriod;
                    document.getElementById('editEndDate').value = '';
                }
                
                const modal = new bootstrap.Modal(document.getElementById('editModal'));
                modal.show();
            }

            // Edit employee modal functionality
            function showEditModal(employeeId, fullName, email, phone, position, employmentPeriod, status, departmentId) {
                console.log('showEditModal called with:');
                console.log('employeeId:', employeeId);
                console.log('fullName:', fullName);
                console.log('email:', email);
                console.log('phone:', phone);
                console.log('position:', position);
                console.log('employmentPeriod:', employmentPeriod);
                console.log('status:', status);
                console.log('departmentId:', departmentId);
                
                document.getElementById('editEmployeeId').value = employeeId;
                document.getElementById('editFullName').value = fullName;
                document.getElementById('editEmail').value = email;
                document.getElementById('editPhone').value = phone;
                document.getElementById('editPosition').value = position;
                document.getElementById('editEmploymentPeriod').value = employmentPeriod;
                document.getElementById('editStatus').value = status;
                
                const modal = new bootstrap.Modal(document.getElementById('editModal'));
                modal.show();
            }

            // Status update modal functionality
            function showStatusModal(employeeId, currentStatus, employeeName) {
                document.getElementById('modalEmployeeId').value = employeeId;
                document.getElementById('employeeName').value = employeeName;
                document.getElementById('currentStatus').value = currentStatus;
                document.getElementById('newStatus').value = currentStatus;
                
                const modal = new bootstrap.Modal(document.getElementById('statusModal'));
                modal.show();
            }
            
            // Delete employee confirmation
            function confirmDeleteEmployee(employeeId, employeeName) {
                if (confirm('Bạn có chắc muốn xóa nhân viên "' + employeeName + '"?\n\nThao tác này không thể hoàn tác và cũng sẽ xóa tài khoản hệ thống liên kết.')) {
                    // Create a form to submit delete request
                    const form = document.createElement('form');
                    form.method = 'POST';
                    form.action = '${pageContext.request.contextPath}/hr/employee-list';
                    
                    const actionInput = document.createElement('input');
                    actionInput.type = 'hidden';
                    actionInput.name = 'action';
                    actionInput.value = 'deleteEmployee';
                    form.appendChild(actionInput);
                    
                    const employeeIdInput = document.createElement('input');
                    employeeIdInput.type = 'hidden';
                    employeeIdInput.name = 'employeeId';
                    employeeIdInput.value = employeeId;
                    form.appendChild(employeeIdInput);
                    
                    document.body.appendChild(form);
                    form.submit();
                }
            }

            // Initialize page
            document.addEventListener('DOMContentLoaded', function() {
                console.log('Trang danh sách nhân viên đã tải xong');
            });
        </script>
    </body>
</html>
