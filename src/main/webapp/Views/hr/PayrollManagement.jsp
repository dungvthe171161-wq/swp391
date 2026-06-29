<%-- 
    Document   : PayrollManagement
    Created on : Nov 5, 2025
    Author     : admin
    Description: Payroll Approval Management for HR Manager - Review and approve/reject payrolls submitted by HR Staff
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, java.math.BigDecimal, java.text.NumberFormat, java.util.Locale, java.text.SimpleDateFormat" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Duyệt bảng lương - BetterHR</title>
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css"/>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
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
                --border-color: #e5e7eb;
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

            /* Payroll specific styles */
            .status-tabs {
                display: flex;
                gap: 8px;
                border-bottom: 2px solid var(--border-color);
                margin-bottom: 20px;
                flex-wrap: wrap;
            }

            .status-tab-btn {
                padding: 12px 24px;
                background: none;
                border: none;
                border-bottom: 3px solid transparent;
                cursor: pointer;
                font-size: 14px;
                font-weight: 600;
                color: var(--text-muted);
                transition: all 0.2s;
                display: flex;
                align-items: center;
                gap: 8px;
            }

            .status-tab-btn:hover {
                color: var(--primary-color);
                background: rgba(102, 126, 234, 0.05);
            }

            .status-tab-btn.active {
                color: var(--primary-color);
                border-bottom-color: var(--primary-color);
                background: rgba(102, 126, 234, 0.1);
            }

            .status-tab-btn i {
                font-size: 16px;
            }

            /* Filters */
            .filters-section {
                background: #f8f9fa;
                border-radius: 12px;
                padding: 20px;
                margin-bottom: 24px;
                border: 1px solid var(--border-color);
            }

            .filters-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 16px;
            }

            .filters-header h3 {
                margin: 0;
                font-size: 1.1rem;
                font-weight: 600;
                color: var(--text-color);
                display: flex;
                align-items: center;
                gap: 8px;
            }

            .filters-header h3 i {
                color: var(--primary-color);
            }

            .filters {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
                gap: 16px;
                margin-bottom: 16px;
            }

            .form-group {
                margin-bottom: 0;
            }

            .form-group label {
                display: block;
                margin-bottom: 8px;
                font-weight: 600;
                color: var(--text-color);
                font-size: 14px;
            }

            .form-group select,
            .form-group input {
                width: 100%;
                padding: 10px 12px;
                border: 1px solid var(--border-color);
                border-radius: 8px;
                font-size: 14px;
                transition: all 0.2s;
                background: white;
            }

            .form-group select:focus,
            .form-group input:focus {
                outline: none;
                border-color: var(--primary-color);
                box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
            }

            .filter-actions {
                display: flex;
                gap: 12px;
                align-items: flex-end;
            }

            .btn-secondary {
                background: var(--text-muted);
                color: white;
            }

            .btn-secondary:hover {
                background: #4b5563;
            }

            .btn-clear {
                background: var(--warning-color);
                color: white;
                display: flex;
                align-items: center;
                gap: 6px;
            }

            .btn-clear:hover {
                background: #d97706;
            }

            .btn i {
                margin-right: 4px;
            }

            /* Tables */
            .table-container {
                overflow-x: auto;
                border-radius: 8px;
                border: 1px solid var(--border-color);
            }

            table {
                width: 100%;
                border-collapse: collapse;
                background: white;
            }

            th, td {
                padding: 14px 16px;
                border-bottom: 1px solid var(--border-color);
                text-align: left;
            }

            th {
                background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
                font-weight: 600;
                color: var(--text-color);
                font-size: 13px;
                text-transform: uppercase;
                letter-spacing: 0.5px;
                position: sticky;
                top: 0;
                z-index: 10;
            }

            tbody tr {
                transition: all 0.2s ease;
            }

            tbody tr:hover {
                background: #f8f9fa;
            }

            tbody tr:last-child td {
                border-bottom: none;
            }

            /* Status badges */
            .status-badge {
                padding: 4px 12px;
                border-radius: 12px;
                font-size: 12px;
                font-weight: 600;
                display: inline-block;
            }

            .status-Draft {
                background: #e5e7eb;
                color: #374151;
            }

            .status-Pending {
                background: #fef3c7;
                color: #92400e;
            }

            .status-Approved {
                background: #d1fae5;
                color: #065f46;
            }

            .status-Rejected {
                background: #fee2e2;
                color: #991b1b;
            }

            .status-Paid {
                background: #dbeafe;
                color: #1e40af;
            }

            /* Batch Actions Bar */
            #batchActionsBar {
                display: none;
                margin-bottom: 15px;
                padding: 12px;
                background: #f0f9ff;
                border: 1px solid #bae6fd;
                border-radius: 8px;
                align-items: center;
                gap: 12px;
            }

            #batchActionsBar.show {
                display: flex;
            }

            #selectedCount {
                font-weight: 600;
                color: #0369a1;
                margin-right: auto;
            }

            .payroll-checkbox {
                cursor: pointer;
                width: 18px;
                height: 18px;
            }

            /* Action buttons */
            .action-btns {
                display: flex;
                gap: 8px;
            }

            .btn {
                display: inline-block;
                padding: 10px 14px;
                border-radius: 8px;
                text-decoration: none;
                color: #fff;
                background: #2563eb;
                border: none;
                cursor: pointer;
                font-size: 14px;
                transition: all 0.2s;
            }

            .btn:hover {
                background: #1d4ed8;
                transform: translateY(-1px);
            }

            .btn-success {
                background: var(--success-color);
            }

            .btn-success:hover {
                background: #059669;
            }

            .btn-danger {
                background: var(--danger-color);
            }

            .btn-danger:hover {
                background: #dc2626;
            }

            .btn-small {
                padding: 6px 12px;
                font-size: 12px;
            }

            /* Messages */
            .message {
                padding: 12px 16px;
                border-radius: 8px;
                margin-bottom: 20px;
            }

            .message-success {
                background: #d4edda;
                border: 1px solid #c3e6cb;
                color: #155724;
            }

            .message-error {
                background: #f8d7da;
                border: 1px solid #f5c6cb;
                color: #721c24;
            }

            /* Pagination */
            .pagination-bar {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 1rem;
                background-color: #fff;
                border-radius: 4px;
                margin-top: 1rem;
            }

            .pagination-controls {
                display: flex;
                gap: 5px;
                align-items: center;
            }

            .pagination-controls a,
            .pagination-controls span {
                display: inline-flex;
                align-items: center;
                justify-content: center;
                min-width: 32px;
                height: 32px;
                padding: 0 8px;
                border: 1px solid #dee2e6;
                border-radius: 4px;
                font-size: 14px;
                color: #0066cc;
                text-decoration: none;
                transition: all 0.2s ease;
            }

            .pagination-controls a:hover {
                background-color: #f8f9fa;
                border-color: #0066cc;
            }

            .pagination-controls span.active {
                background-color: #0066cc;
                border-color: #0066cc;
                color: #fff;
            }

            .pagination-controls span.disabled {
                color: #ccc;
                border-color: #eee;
                cursor: not-allowed;
                pointer-events: none;
            }

            .empty {
                color: var(--text-muted);
                padding: 60px 20px;
                text-align: center;
            }

            .empty-state {
                display: flex;
                flex-direction: column;
                align-items: center;
                justify-content: center;
                padding: 60px 20px;
            }

            .empty-state i {
                font-size: 4rem;
                color: var(--text-muted);
                margin-bottom: 16px;
                opacity: 0.5;
            }

            .empty-state h3 {
                color: var(--text-color);
                margin-bottom: 8px;
            }

            .empty-state p {
                color: var(--text-muted);
            }

            /* Results summary */
            .results-summary {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 12px 16px;
                background: #f8f9fa;
                border-radius: 8px;
                margin-bottom: 16px;
                font-size: 14px;
            }

            .results-count {
                color: var(--text-color);
                font-weight: 600;
            }

            .results-count span {
                color: var(--primary-color);
            }

            /* Modal */
            .modal {
                display: none;
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: rgba(0,0,0,0.5);
                z-index: 1000;
                align-items: center;
                justify-content: center;
            }

            .modal.active {
                display: flex;
            }

            .modal-content {
                background: white;
                border-radius: 12px;
                padding: 0;
                max-width: 700px;
                width: 90%;
                max-height: 90vh;
                overflow-y: auto;
                box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            }

            .modal-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 24px;
                border-bottom: 1px solid var(--border-color);
                background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
                color: white;
                border-radius: 12px 12px 0 0;
            }

            .modal-header h3 {
                margin: 0;
                display: flex;
                align-items: center;
                gap: 10px;
                font-size: 1.5rem;
            }

            .modal-header h3 i {
                font-size: 1.3rem;
            }

            .close-btn {
                background: rgba(255,255,255,0.2);
                border: none;
                font-size: 24px;
                cursor: pointer;
                color: white;
                width: 36px;
                height: 36px;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                transition: all 0.2s;
            }

            .close-btn:hover {
                background: rgba(255,255,255,0.3);
                transform: rotate(90deg);
            }

            .modal-body {
                padding: 24px;
            }

            .detail-section {
                margin-bottom: 24px;
            }

            .detail-section-title {
                font-size: 1rem;
                font-weight: 600;
                color: var(--text-color);
                margin-bottom: 16px;
                padding-bottom: 8px;
                border-bottom: 2px solid var(--primary-color);
                display: flex;
                align-items: center;
                gap: 8px;
            }

            .detail-section-title i {
                color: var(--primary-color);
            }

            .detail-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
                gap: 16px;
            }

            .detail-item {
                background: #f8f9fa;
                padding: 16px;
                border-radius: 8px;
                border-left: 4px solid var(--primary-color);
            }

            .detail-label {
                font-size: 0.85rem;
                font-weight: 600;
                color: var(--text-muted);
                text-transform: uppercase;
                letter-spacing: 0.5px;
                margin-bottom: 8px;
            }

            .detail-value {
                font-size: 1.1rem;
                font-weight: 600;
                color: var(--text-color);
            }

            .detail-value.currency {
                color: var(--primary-color);
                font-size: 1.3rem;
            }

            .detail-value.status {
                display: inline-block;
                padding: 6px 12px;
                border-radius: 20px;
                font-size: 0.9rem;
            }

            .detail-row {
                display: flex;
                justify-content: space-between;
                padding: 12px 0;
                border-bottom: 1px solid var(--border-color);
            }

            .detail-row:last-child {
                border-bottom: none;
            }

            .currency {
                font-weight: 600;
                color: var(--primary-color);
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

                .filters {
                    grid-template-columns: 1fr;
                }
            }
        </style>
        <link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;500;600;700;800&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/hr-theme.css?v=hr-manager-shell-20260627-4">
    </head>
    <body>
        <div class="hr-dashboard-container hr-manager-page-shell">
            <!-- Header -->
            <header class="hr-header">
                <div class="header-content">
                    <div class="logo-section">
                        <i class="fas fa-money-bill-wave"></i>
                        <h1>Duyệt bảng lương</h1>
                    </div>
                    <div class="header-actions">
                        <div class="search-box">
                            <i class="fas fa-search"></i>
                            <input type="text" placeholder="Tìm bảng lương...">
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
                        <a href="${pageContext.request.contextPath}/homepage" class="btn-homepage" title="Về trang chủ">
                            <i class="fas fa-home"></i>
                            <span>Trang chủ</span>
                        </a>
                    </div>
                </div>
            </header>

            <!-- Main Content -->
            <main class="hr-main-content">
                <c:set var="hrSidebarActive" value="payroll" />
                <%@ include file="_HrManagerSidebar.jspf" %>

                <!-- Content Area -->
                <div class="hr-content-area" id="payrollApprovalContent">
                    <!-- Page Title -->
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <h1 class="h2">Duyệt bảng lương</h1>
                    </div>

                    <%-- Display success/error messages --%>
                    <c:if test="${not empty success}">
                        <div class="message message-success">
                            <strong>✓ Success:</strong> ${success}
                        </div>
                    </c:if>
                    <c:if test="${not empty error}">
                        <div class="message message-error">
                            <strong>✗ Error:</strong> ${error}
                        </div>
                    </c:if>

                    <div class="card payroll-approval-card" style="border: 1px solid var(--border-color); border-radius: 12px; padding: 24px; margin-bottom: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.05);">
                        <!-- Status Tabs -->
                        <div class="status-tabs">
                            <c:url var="pendingUrl" value="/hr/payroll-approval">
                                <c:param name="status" value="Pending"/>
                                <c:if test="${not empty employeeFilter}">
                                    <c:param name="employeeFilter" value="${employeeFilter}"/>
                                </c:if>
                                <c:if test="${not empty payPeriodFilter}">
                                    <c:param name="payPeriod" value="${payPeriodFilter}"/>
                                </c:if>
                            </c:url>
                            <c:url var="approvedUrl" value="/hr/payroll-approval">
                                <c:param name="status" value="Approved"/>
                                <c:if test="${not empty employeeFilter}">
                                    <c:param name="employeeFilter" value="${employeeFilter}"/>
                                </c:if>
                                <c:if test="${not empty payPeriodFilter}">
                                    <c:param name="payPeriod" value="${payPeriodFilter}"/>
                                </c:if>
                            </c:url>
                            <c:url var="rejectedUrl" value="/hr/payroll-approval">
                                <c:param name="status" value="Rejected"/>
                                <c:if test="${not empty employeeFilter}">
                                    <c:param name="employeeFilter" value="${employeeFilter}"/>
                                </c:if>
                                <c:if test="${not empty payPeriodFilter}">
                                    <c:param name="payPeriod" value="${payPeriodFilter}"/>
                                </c:if>
                            </c:url>
                            <c:url var="paidUrl" value="/hr/payroll-approval">
                                <c:param name="status" value="Paid"/>
                                <c:if test="${not empty employeeFilter}">
                                    <c:param name="employeeFilter" value="${employeeFilter}"/>
                                </c:if>
                                <c:if test="${not empty payPeriodFilter}">
                                    <c:param name="payPeriod" value="${payPeriodFilter}"/>
                                </c:if>
                            </c:url>
                            <c:url var="allUrl" value="/hr/payroll-approval">
                                <c:if test="${not empty employeeFilter}">
                                    <c:param name="employeeFilter" value="${employeeFilter}"/>
                                </c:if>
                                <c:if test="${not empty payPeriodFilter}">
                                    <c:param name="payPeriod" value="${payPeriodFilter}"/>
                                </c:if>
                            </c:url>

                            <a class="status-tab-btn ${statusFilter == 'Pending' ? 'active' : ''}" href="${pendingUrl}">
                                <i class="fas fa-hourglass-half"></i>
                                Pending Approval
                                <span class="badge bg-warning text-dark">${pendingCount}</span>
                            </a>
                            <a class="status-tab-btn ${statusFilter == 'Approved' ? 'active' : ''}" href="${approvedUrl}">
                                <i class="fas fa-check-circle"></i>
                                Đã duyệt
                                <span class="badge bg-success">${approvedCount}</span>
                            </a>
                            <a class="status-tab-btn ${statusFilter == 'Rejected' ? 'active' : ''}" href="${rejectedUrl}">
                                <i class="fas fa-times-circle"></i>
                                Từ chối
                                <span class="badge bg-danger">${rejectedCount}</span>
                            </a>
                            <a class="status-tab-btn ${statusFilter == 'Paid' ? 'active' : ''}" href="${paidUrl}">
                                <i class="fas fa-money-bill-wave"></i>
                                Paid
                                <span class="badge bg-primary">${paidCount}</span>
                            </a>
                            <a class="status-tab-btn ${empty statusFilter ? 'active' : ''}" href="${allUrl}">
                                <i class="fas fa-list"></i> All
                            </a>
                        </div>

                        <!-- Results Summary -->
                        <c:if test="${not empty payrolls}">
                            <div class="results-summary">
                                <div class="results-count">
                                    Showing <span>${(currentPage - 1) * pageSize + 1}</span> to 
                                    <span>${currentPage * pageSize > totalCount ? totalCount : currentPage * pageSize}</span> 
                                    of <span>${totalCount}</span> payrolls
                                </div>
                            </div>
                        </c:if>

                        <!-- Filters Section -->
                        <div class="filters-section">
                            <div class="filters-header">
                                <h3><i class="fas fa-filter"></i> Filters</h3>
                                <c:if test="${not empty employeeFilter || not empty payPeriodFilter}">
                                    <c:url var="clearFiltersUrl" value="/hr/payroll-approval">
                                        <c:if test="${not empty statusFilter}">
                                            <c:param name="status" value="${statusFilter}"/>
                                        </c:if>
                                    </c:url>
                                    <a class="btn btn-clear btn-small" href="${clearFiltersUrl}">
                                        <i class="fas fa-times"></i> Xóa bộ lọc
                                    </a>
                                </c:if>
                            </div>
                            <form method="GET" action="<%=request.getContextPath()%>/hr/payroll-approval" id="filterForm">
                                <input type="hidden" name="status" value="${statusFilter}"/>
                                <input type="hidden" name="page" id="pageField" value="${currentPage}"/>
                                <input type="hidden" name="pageSize" id="pageSizeField" value="${pageSize}"/>
                                <div class="filters">
                                    <div class="form-group">
                                        <label><i class="fas fa-user"></i> Nhân viên</label>
                                        <select name="employeeFilter" id="employeeFilter">
                                            <option value="">Tất cả nhân viên</option>
                                            <c:forEach var="employee" items="${employees}">
                                                <option value="${employee.employeeId}" 
                                                        ${employeeFilter == employee.employeeId.toString() ? 'selected' : ''}>
                                                    ${employee.fullName} (ID: ${employee.employeeId})
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label><i class="fas fa-calendar-alt"></i> Kỳ lương</label>
                                        <input type="month" name="payPeriod" id="payPeriod" value="${payPeriodFilter}"/>
                                    </div>
                                    <div class="form-group filter-actions">
                                        <label>&nbsp;</label>
                                        <div style="display: flex; gap: 8px;">
                                            <button type="submit" class="btn">
                                                <i class="fas fa-search"></i> Áp dụng
                                            </button>
                                            <c:if test="${not empty employeeFilter || not empty payPeriodFilter}">
                                                <c:url var="resetFiltersUrl" value="/hr/payroll-approval">
                                                    <c:if test="${not empty statusFilter}">
                                                        <c:param name="status" value="${statusFilter}"/>
                                                    </c:if>
                                                </c:url>
                                                <a class="btn btn-secondary btn-small" href="${resetFiltersUrl}">
                                                    <i class="fas fa-redo"></i> Xóa
                                                </a>
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </div>

                        <!-- Batch Actions Bar -->
                        <div id="batchActionsBar" style="display: none;">
                            <span id="selectedCount" style="font-weight:600; color:#0369a1;">0 mục đã chọn</span>
                            <button type="button" class="btn btn-small btn-success" onclick="approveSelectedPayrolls()" id="batchApproveBtn" style="display:none;">
                                <i class="fas fa-check"></i> Duyệt mục đã chọn
                            </button>
                            <button type="button" class="btn btn-small btn-danger" onclick="rejectSelectedPayrolls()" id="batchRejectBtn" style="display:none;">
                                <i class="fas fa-times"></i> Từ chối mục đã chọn
                            </button>
                            <button type="button" class="btn btn-small" onclick="clearSelection()" style="background:#6b7280; color:white;">
                                <i class="fas fa-times"></i> Bỏ chọn
                            </button>
                        </div>

                        <!-- Payroll Table -->
                        <div class="table-container">
                            <table>
                                <thead>
                                    <tr>
                                        <th style="width:40px; text-align:center;">
                                            <input type="checkbox" id="selectAllCheckbox" onchange="toggleSelectAll(this)" title="Chọn tất cả">
                                        </th>
                                        <th><i class="fas fa-user"></i> Nhân viên</th>
                                        <th><i class="fas fa-calendar"></i> Kỳ lương</th>
                                        <th><i class="fas fa-money-bill"></i> Lương cơ bản</th>
                                        <th><i class="fas fa-plus-circle"></i> Phụ cấp</th>
                                        <th><i class="fas fa-gift"></i> Thưởng</th>
                                        <th><i class="fas fa-minus-circle"></i> Khấu trừ</th>
                                        <th><i class="fas fa-wallet"></i> Thực nhận</th>
                                        <th><i class="fas fa-info-circle"></i> Trạng thái</th>
                                        <th><i class="fas fa-cog"></i> Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${empty payrolls}">
                                            <tr>
                                                <td colspan="10">
                                                    <div class="empty-state">
                                                        <i class="fas fa-inbox"></i>
                                                        <h3>Không tìm thấy bảng lương</h3>
                                                        <p>Không có bảng lương phù hợp với bộ lọc hiện tại.</p>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach var="payroll" items="${payrolls}">
                                                <tr data-payroll-id="${payroll.payrollId}"
                                                    data-employee-name="${payroll.employeeName}"
                                                    data-pay-period="${payroll.payPeriod}"
                                                    data-base-salary="${payroll.baseSalary}"
                                                    data-allowance="${payroll.allowance}"
                                                    data-bonus="${payroll.bonus}"
                                                    data-deduction="${payroll.deduction}"
                                                    data-net-salary="${payroll.netSalary}"
                                                    data-status="${payroll.status}"
                                                    data-approved-date="${payroll.approvedDate}">
                                                    <td style="text-align:center;">
                                                        <c:if test="${payroll.status == 'Pending'}">
                                                            <input type="checkbox" class="payroll-checkbox" 
                                                                   data-payroll-id="${payroll.payrollId}"
                                                                   data-payroll-status="${payroll.status}"
                                                                   onchange="updateBatchActions()">
                                                        </c:if>
                                                    </td>
                                                    <td>${payroll.employeeName}</td>
                                                    <td>${payroll.payPeriod}</td>
                                                    <td class="currency">
                                                        <fmt:formatNumber value="${payroll.baseSalary}" type="number" groupingUsed="true"/> VNĐ
                                                    </td>
                                                    <td class="currency">
                                                        <fmt:formatNumber value="${payroll.allowance}" type="number" groupingUsed="true"/> VNĐ
                                                    </td>
                                                    <td class="currency">
                                                        <fmt:formatNumber value="${payroll.bonus}" type="number" groupingUsed="true"/> VNĐ
                                                    </td>
                                                    <td class="currency">
                                                        <fmt:formatNumber value="${payroll.deduction}" type="number" groupingUsed="true"/> VNĐ
                                                    </td>
                                                    <td class="currency" style="font-weight:700; color:var(--primary-color);">
                                                        <fmt:formatNumber value="${payroll.netSalary}" type="number" groupingUsed="true"/> VNĐ
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${payroll.status == 'Pending'}">
                                                                <span class="status-badge status-Pending">Chờ duyệt</span>
                                                            </c:when>
                                                            <c:when test="${payroll.status == 'Approved'}">
                                                                <span class="status-badge status-Approved">Đã duyệt</span>
                                                            </c:when>
                                                            <c:when test="${payroll.status == 'Rejected'}">
                                                                <span class="status-badge status-Rejected">Từ chối</span>
                                                            </c:when>
                                                            <c:when test="${payroll.status == 'Draft'}">
                                                                <span class="status-badge status-Draft">Nháp</span>
                                                            </c:when>
                                                            <c:when test="${payroll.status == 'Paid'}">
                                                                <span class="status-badge status-Paid">Đã chi trả</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="status-badge status-Draft">${payroll.status}</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <div class="action-btns">
                                                            <button class="btn btn-small view-payroll-btn" 
                                                                    data-payroll-id="${payroll.payrollId}"
                                                                    data-employee-name="${payroll.employeeName}"
                                                                    data-pay-period="${payroll.payPeriod}"
                                                                    data-base-salary="${payroll.baseSalary}"
                                                                    data-allowance="${payroll.allowance}"
                                                                    data-bonus="${payroll.bonus}"
                                                                    data-deduction="${payroll.deduction}"
                                                                    data-net-salary="${payroll.netSalary}"
                                                                    data-status="${payroll.status}"
                                                                    data-approved-date="${payroll.approvedDate}"
                                                                    title="Xem chi tiết">
                                                                <i class="fas fa-eye"></i> Xem
                                                            </button>
                                                            <c:if test="${payroll.status == 'Pending'}">
                                                                <button class="btn btn-small btn-success approve-payroll-btn" 
                                                                        data-payroll-id="${payroll.payrollId}"
                                                                        title="Duyệt bảng lương">
                                                                    <i class="fas fa-check"></i> Duyệt
                                                                </button>
                                                                <button class="btn btn-small btn-danger reject-payroll-btn" 
                                                                        data-payroll-id="${payroll.payrollId}"
                                                                        title="Từ chối bảng lương">
                                                                    <i class="fas fa-times"></i> Từ chối
                                                                </button>
                                                            </c:if>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </table>
                        </div>

                        <!-- Pagination -->
                        <div class="pagination-bar">
                            <c:set var="page" value="${currentPage != null ? currentPage : 1}" />
                            <c:set var="size" value="${pageSize != null ? pageSize : 10}" />
                            <c:set var="total" value="${totalCount != null ? totalCount : 0}" />
                            <c:set var="totalPagesCalc" value="${totalPages != null ? totalPages : 1}" />
                            <c:set var="start" value="${total > 0 ? (page - 1) * size + 1 : 0}" />
                            <c:set var="end" value="${page * size}" />
                            <c:if test="${end > total}">
                                <c:set var="end" value="${total}" />
                            </c:if>

                            <div class="pagination-info">
                                <span>Hiển thị ${start} - ${end} trên ${total} bảng lương</span>
                                <div class="page-size-selector">
                                    <label for="pageSizeSelect">Số dòng mỗi trang:</label>
                                    <select id="pageSizeSelect" onchange="changePayrollPageSize(this.value)">
                                        <option value="5" <c:if test="${size == 5}">selected</c:if>>5</option>
                                        <option value="10" <c:if test="${size == 10}">selected</c:if>>10</option>
                                        <option value="20" <c:if test="${size == 20}">selected</c:if>>20</option>
                                        <option value="50" <c:if test="${size == 50}">selected</c:if>>50</option>
                                    </select>
                                </div>
                            </div>

                            <div class="pagination-controls">
                                <c:choose>
                                    <c:when test="${page > 1}">
                                        <c:url var="prevUrl" value="/hr/payroll-approval">
                                            <c:param name="page" value="${page - 1}" />
                                            <c:param name="pageSize" value="${size}" />
                                            <c:if test="${not empty statusFilter}">
                                                <c:param name="status" value="${statusFilter}" />
                                            </c:if>
                                            <c:if test="${not empty employeeFilter}">
                                                <c:param name="employeeFilter" value="${employeeFilter}" />
                                            </c:if>
                                            <c:if test="${not empty payPeriodFilter}">
                                                <c:param name="payPeriod" value="${payPeriodFilter}" />
                                            </c:if>
                                        </c:url>
                                        <a href="${prevUrl}" class="btn-pagination">← Trước</a>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="disabled">← Trước</span>
                                    </c:otherwise>
                                </c:choose>

                                <c:set var="range" value="2" />
                                <c:set var="startPage" value="${page - range > 1 ? page - range : 1}" />
                                <c:set var="endPage" value="${page + range < totalPagesCalc ? page + range : totalPagesCalc}" />

                                <c:if test="${startPage > 1}">
                                    <c:url var="firstPageUrl" value="/hr/payroll-approval">
                                        <c:param name="page" value="1" />
                                        <c:param name="pageSize" value="${size}" />
                                        <c:if test="${not empty statusFilter}">
                                            <c:param name="status" value="${statusFilter}" />
                                        </c:if>
                                        <c:if test="${not empty employeeFilter}">
                                            <c:param name="employeeFilter" value="${employeeFilter}" />
                                        </c:if>
                                        <c:if test="${not empty payPeriodFilter}">
                                            <c:param name="payPeriod" value="${payPeriodFilter}" />
                                        </c:if>
                                    </c:url>
                                    <a href="${firstPageUrl}" class="btn-pagination">1</a>
                                    <c:if test="${startPage > 2}">
                                        <span class="ellipsis">...</span>
                                    </c:if>
                                </c:if>

                                <c:forEach begin="${startPage}" end="${endPage}" var="i">
                                    <c:choose>
                                        <c:when test="${i == page}">
                                            <span class="active btn-pagination">${i}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <c:url var="pageUrl" value="/hr/payroll-approval">
                                                <c:param name="page" value="${i}" />
                                                <c:param name="pageSize" value="${size}" />
                                                <c:if test="${not empty statusFilter}">
                                                    <c:param name="status" value="${statusFilter}" />
                                                </c:if>
                                                <c:if test="${not empty employeeFilter}">
                                                    <c:param name="employeeFilter" value="${employeeFilter}" />
                                                </c:if>
                                                <c:if test="${not empty payPeriodFilter}">
                                                    <c:param name="payPeriod" value="${payPeriodFilter}" />
                                                </c:if>
                                            </c:url>
                                            <a href="${pageUrl}" class="btn-pagination">${i}</a>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>

                                <c:if test="${endPage < totalPagesCalc}">
                                    <c:if test="${endPage < totalPagesCalc - 1}">
                                        <span class="ellipsis">...</span>
                                    </c:if>
                                    <c:url var="lastPageUrl" value="/hr/payroll-approval">
                                        <c:param name="page" value="${totalPagesCalc}" />
                                        <c:param name="pageSize" value="${size}" />
                                        <c:if test="${not empty statusFilter}">
                                            <c:param name="status" value="${statusFilter}" />
                                        </c:if>
                                        <c:if test="${not empty employeeFilter}">
                                            <c:param name="employeeFilter" value="${employeeFilter}" />
                                        </c:if>
                                        <c:if test="${not empty payPeriodFilter}">
                                            <c:param name="payPeriod" value="${payPeriodFilter}" />
                                        </c:if>
                                    </c:url>
                                    <a href="${lastPageUrl}" class="btn-pagination">${totalPagesCalc}</a>
                                </c:if>

                                <c:choose>
                                    <c:when test="${page < totalPagesCalc}">
                                        <c:url var="nextUrl" value="/hr/payroll-approval">
                                            <c:param name="page" value="${page + 1}" />
                                            <c:param name="pageSize" value="${size}" />
                                            <c:if test="${not empty statusFilter}">
                                                <c:param name="status" value="${statusFilter}" />
                                            </c:if>
                                            <c:if test="${not empty employeeFilter}">
                                                <c:param name="employeeFilter" value="${employeeFilter}" />
                                            </c:if>
                                            <c:if test="${not empty payPeriodFilter}">
                                                <c:param name="payPeriod" value="${payPeriodFilter}" />
                                            </c:if>
                                        </c:url>
                                        <a href="${nextUrl}" class="btn-pagination">Sau →</a>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="disabled">Sau →</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>
            </main>
        </div>

        <!-- Payroll Details Modal -->
        <div id="payrollDetailModal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h3><i class="fas fa-file-invoice-dollar"></i> Chi tiết bảng lương</h3>
                    <button class="close-btn" onclick="closePayrollModal()" title="Đóng">&times;</button>
                </div>
                <div class="modal-body" id="payrollDetailContent">
                    <!-- Content will be loaded dynamically -->
                </div>
            </div>
        </div>

        <!-- Reject Payroll Modal -->
        <div id="rejectPayrollModal" class="modal">
            <div class="modal-content" style="max-width: 600px;">
                <div class="modal-header">
                    <h3><i class="fas fa-times-circle"></i> Từ chối bảng lương</h3>
                    <button class="close-btn" onclick="closeRejectModal()" title="Đóng">&times;</button>
                </div>
                <div class="modal-body">
                    <form id="rejectPayrollForm">
                        <input type="hidden" id="rejectPayrollId" name="payrollId" />
                        <input type="hidden" id="batchRejectPayrollIds" name="payrollIds" />
                        <input type="hidden" id="rejectAction" name="action" value="reject" />
                        
                        <div class="form-group" style="margin-bottom: 20px;">
                            <label for="rejectNote" style="display: block; margin-bottom: 8px; font-weight: 600; color: var(--text-color);">
                                Lý do từ chối <span style="color: var(--error);">*</span>
                            </label>
                            <textarea 
                                id="rejectNote" 
                                name="rejectNote" 
                                rows="5" 
                                style="width: 100%; padding: 12px; border: 1px solid var(--border-color); border-radius: 8px; font-family: inherit; font-size: 14px; resize: vertical;"
                                placeholder="Vui lòng nhập lý do từ chối bảng lương..."
                                required></textarea>
                            <small style="color: var(--muted-color); margin-top: 4px; display: block;">
                                Ghi chú này sẽ hiển thị cho HR Staff để đối chiếu khi chỉnh sửa bảng lương.
                            </small>
                        </div>
                        
                        <div class="form-actions" style="display: flex; gap: 12px; justify-content: flex-end; margin-top: 24px;">
                            <button type="button" class="btn btn-secondary" onclick="closeRejectModal()" style="padding: 10px 20px;">
                                Hủy
                            </button>
                            <button type="submit" class="btn btn-danger" style="padding: 10px 20px;">
                                <i class="fas fa-times"></i> Xác nhận từ chối
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            const appContext = '${pageContext.request.contextPath}';
            let statusFilterField;
            let employeeFilterField;
            let payPeriodFilterField;
            let pageField;
            let pageSizeField;

            const getCurrentFilters = () => {
                return {
                    status: statusFilterField && statusFilterField.value ? statusFilterField.value.trim() : '',
                    employee: employeeFilterField && employeeFilterField.value ? employeeFilterField.value.trim() : '',
                    payPeriod: payPeriodFilterField && payPeriodFilterField.value ? payPeriodFilterField.value : '',
                    page: pageField && pageField.value ? pageField.value.trim() : '',
                    pageSize: pageSizeField && pageSizeField.value ? pageSizeField.value.trim() : ''
                };
            };

            document.addEventListener('DOMContentLoaded', function() {
                const filterForm = document.getElementById('filterForm');
                statusFilterField = filterForm ? filterForm.querySelector('input[name="status"]') : null;
                employeeFilterField = document.getElementById('employeeFilter');
                payPeriodFilterField = document.getElementById('payPeriod');
                pageField = document.getElementById('pageField');
                pageSizeField = document.getElementById('pageSizeField');

                if (filterForm) {
                    filterForm.addEventListener('submit', function() {
                        if (pageField) {
                            pageField.value = '1';
                        }
                    });
                }
                
                // Event listeners for action buttons
                document.querySelectorAll('.view-payroll-btn').forEach(btn => {
                    btn.addEventListener('click', function() {
                        viewPayrollDetails(this);
                    });
                });
                
                document.querySelectorAll('.approve-payroll-btn').forEach(btn => {
                    btn.addEventListener('click', function() {
                        const payrollId = this.getAttribute('data-payroll-id');
                        approvePayroll(payrollId);
                    });
                });
                
                document.querySelectorAll('.reject-payroll-btn').forEach(btn => {
                    btn.addEventListener('click', function() {
                        const payrollId = this.getAttribute('data-payroll-id');
                        rejectPayroll(payrollId);
                    });
                });

                // Handle reject form submission
                const rejectForm = document.getElementById('rejectPayrollForm');
                if (rejectForm) {
                    rejectForm.addEventListener('submit', function(e) {
                        e.preventDefault();
                        
                        const rejectNote = document.getElementById('rejectNote').value.trim();
                        if (!rejectNote) {
                            alert('Please provide a rejection reason.');
                            return;
                        }
                        
                        const singlePayrollId = document.getElementById('rejectPayrollId').value;
                        const batchPayrollIds = document.getElementById('batchRejectPayrollIds').value;
                        
                        // Determine if this is batch reject or single reject
                        const isBatchReject = batchPayrollIds && batchPayrollIds.trim() !== '';
                        
                        const form = document.createElement('form');
                        form.method = 'POST';
                        form.action = appContext + (isBatchReject ? '/hr/payroll-approval/batch-reject' : '/hr/payroll-approval');

                        const { status, employee, payPeriod, page, pageSize } = getCurrentFilters();
                        
                        const actionInput = document.createElement('input');
                        actionInput.type = 'hidden';
                        actionInput.name = 'action';
                        actionInput.value = 'reject';
                        form.appendChild(actionInput);
                        
                        if (isBatchReject) {
                            // Batch reject: add multiple payroll IDs
                            const payrollIds = batchPayrollIds.split(',');
                            payrollIds.forEach(id => {
                                const input = document.createElement('input');
                                input.type = 'hidden';
                                input.name = 'payrollIds';
                                input.value = id.trim();
                                form.appendChild(input);
                            });
                        } else {
                            // Single reject: add single payroll ID
                            const payrollIdInput = document.createElement('input');
                            payrollIdInput.type = 'hidden';
                            payrollIdInput.name = 'payrollId';
                            payrollIdInput.value = singlePayrollId;
                            form.appendChild(payrollIdInput);
                        }
                        
                        const rejectNoteInput = document.createElement('input');
                        rejectNoteInput.type = 'hidden';
                        rejectNoteInput.name = 'rejectNote';
                        rejectNoteInput.value = rejectNote;
                        form.appendChild(rejectNoteInput);
                        
                        if (status) {
                            const statusInput = document.createElement('input');
                            statusInput.type = 'hidden';
                            statusInput.name = 'status';
                            statusInput.value = status;
                            form.appendChild(statusInput);
                        }
                        if (employee) {
                            const empInput = document.createElement('input');
                            empInput.type = 'hidden';
                            empInput.name = 'employeeFilter';
                            empInput.value = employee;
                            form.appendChild(empInput);
                        }
                        if (payPeriod) {
                            const periodInput = document.createElement('input');
                            periodInput.type = 'hidden';
                            periodInput.name = 'payPeriod';
                            periodInput.value = payPeriod;
                            form.appendChild(periodInput);
                        }
                        if (page) {
                            const pageInput = document.createElement('input');
                            pageInput.type = 'hidden';
                            pageInput.name = 'page';
                            pageInput.value = page;
                            form.appendChild(pageInput);
                        }
                        if (pageSize) {
                            const sizeInput = document.createElement('input');
                            sizeInput.type = 'hidden';
                            sizeInput.name = 'pageSize';
                            sizeInput.value = pageSize;
                            form.appendChild(sizeInput);
                        }
                        
                        document.body.appendChild(form);
                        form.submit();
                    });
                }

                // Close reject modal when clicking outside
                const rejectModal = document.getElementById('rejectPayrollModal');
                if (rejectModal) {
                    rejectModal.addEventListener('click', function(e) {
                        if (e.target === rejectModal) {
                            closeRejectModal();
                        }
                    });
                }
            });

            window.viewPayrollDetails = function(trigger) {
                if (!trigger) {
                    console.error('Missing trigger element for payroll details');
                    return;
                }
                const payrollId = trigger.getAttribute('data-payroll-id');
                if (!payrollId) {
                    console.error('Invalid payroll ID');
                    return;
                }

                const modal = document.getElementById('payrollDetailModal');
                const content = document.getElementById('payrollDetailContent');
                content.innerHTML = '<div style="text-align:center; padding:32px; color: var(--muted-color);">'
                    + '<i class="fas fa-spinner fa-spin" style="font-size:32px; margin-bottom:12px;"></i>'
                    + '<div>Đang tải chi tiết bảng lương...</div>'
                    + '</div>';
                modal.classList.add('active');

                const detailsUrl = appContext + '/hr/payroll-approval?ajax=true&payrollId=' + encodeURIComponent(payrollId);
                fetch(detailsUrl, {
                    credentials: 'same-origin',
                    headers: {
                        'Accept': 'application/json'
                    }
                })
                    .then(response => {
                        if (!response.ok) {
                            throw new Error(`Server returned ${response.status}`);
                        }
                        return response.json();
                    })
                    .then(data => {
                        if (data.error) {
                            throw new Error(data.error);
                        }
                        renderPayrollDetails(data);
                    })
                    .catch(err => {
                        console.error('Error loading payroll details:', err);
                        content.innerHTML = '<div style="background:#fee2e2; border:1px solid #fca5a5; border-radius:8px; padding:20px; color:#991b1b;">'
                            + '<div style="font-weight:600; margin-bottom:8px;">Unable to load payroll details</div>'
                            + `<div>${err.message}</div>`
                            + '</div>';
                    });
            }

            const formatCurrency = (amount) => {
                const value = Number(amount || 0);
                return new Intl.NumberFormat('vi-VN', {
                    style: 'currency',
                    currency: 'VND'
                }).format(value);
            };

            const formatNumber = (value) => {
                if (value === null || value === undefined || value === '') {
                    return '0';
                }
                const num = Number(value);
                if (Number.isNaN(num)) {
                    return value;
                }
                return Number.isInteger(num) ? num.toString() : num.toFixed(2);
            };

            const formatDateTime = (value) => {
                if (!value) {
                    return '';
                }
                const date = new Date(value);
                if (Number.isNaN(date.getTime())) {
                    return value;
                }
                return date.toLocaleString('vi-VN', {
                    hour12: false
                });
            };

            const getStatusClass = (value) => ({
                'Pending': 'status-Pending',
                'Approved': 'status-Approved',
                'Rejected': 'status-Rejected',
                'Draft': 'status-Draft',
                'Paid': 'status-Paid'
            })[value] || 'status-Draft';

            window.renderPayrollDetails = function(data) {
                const content = document.getElementById('payrollDetailContent');
                const audit = data.audit || {};

                // Priority: Use audit data if available, otherwise use payroll data
                const employeeName = data.employeeName || 'N/A';
                const payPeriod = data.payPeriod || 'N/A';
                const status = (audit.status || data.status) || 'Draft';
                const approvedDate = data.approvedDate || '';
                
                // Use audit values if available, otherwise fallback to payroll values
                const baseSalary = audit.baseSalary !== undefined && audit.baseSalary !== null ? audit.baseSalary : (data.baseSalary || 0);
                const allowance = audit.allowance !== undefined && audit.allowance !== null ? audit.allowance : (data.allowance || 0);
                const otSalary = audit.otSalary !== undefined && audit.otSalary !== null ? audit.otSalary : (data.bonus || 0);
                const deduction = audit.totalDeduction !== undefined && audit.totalDeduction !== null ? audit.totalDeduction : (data.deduction || 0);
                const netSalary = audit.netSalary !== undefined && audit.netSalary !== null ? audit.netSalary : (data.netSalary || 0);

                const parts = [];
                parts.push('<div class="detail-section">');
                parts.push('  <div class="detail-section-title"><i class="fas fa-user"></i> Thông tin nhân viên</div>');
                parts.push('  <div class="detail-grid">');
                parts.push('    <div class="detail-item">');
                parts.push('      <div class="detail-label">Tên nhân viên</div>');
                parts.push('      <div class="detail-value">' + employeeName + '</div>');
                parts.push('    </div>');
                parts.push('    <div class="detail-item">');
                parts.push('      <div class="detail-label">Pay Period</div>');
                parts.push('      <div class="detail-value">' + payPeriod + '</div>');
                parts.push('    </div>');
                parts.push('    <div class="detail-item">');
                parts.push('      <div class="detail-label">Status</div>');
                parts.push('      <div class="detail-value"><span class="status-badge ' + getStatusClass(status) + '">' + status + '</span></div>');
                parts.push('    </div>');
                if (approvedDate) {
                    parts.push('    <div class="detail-item">');
                    parts.push('      <div class="detail-label">Ngày duyệt</div>');
                    parts.push('      <div class="detail-value">' + approvedDate + '</div>');
                    parts.push('    </div>');
                }
                if (audit.calculatedAt) {
                    parts.push('    <div class="detail-item">');
                    parts.push('      <div class="detail-label">Calculated At</div>');
                    parts.push('      <div class="detail-value">' + formatDateTime(audit.calculatedAt) + '</div>');
                    parts.push('    </div>');
                }
                if (audit.calculatedByUsername || audit.calculatedBy) {
                    parts.push('    <div class="detail-item">');
                    parts.push('      <div class="detail-label">Calculated By</div>');
                    const calcBy = audit.calculatedByUsername || ('User ID: ' + audit.calculatedBy);
                    parts.push('      <div class="detail-value">' + calcBy + '</div>');
                    parts.push('    </div>');
                }
                parts.push('  </div>');
                parts.push('</div>');

                // Display PayrollAudit information if available
                if (audit && Object.keys(audit).length > 0) {
                    // Attendance Information Section
                    parts.push('<div class="detail-section">');
                    parts.push('  <div class="detail-section-title"><i class="fas fa-calendar-check"></i> Attendance Information</div>');
                    parts.push('  <div class="detail-grid" style="grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));">');
                    
                    if (audit.actualWorkingDays !== undefined && audit.actualWorkingDays !== null) {
                        parts.push('    <div class="detail-item">');
                        parts.push('      <div class="detail-label">Actual Working Days</div>');
                        parts.push('      <div class="detail-value">' + formatNumber(audit.actualWorkingDays) + ' days</div>');
                        parts.push('    </div>');
                    }
                    
                    if (audit.paidLeaveDays !== undefined && audit.paidLeaveDays !== null) {
                        parts.push('    <div class="detail-item">');
                        parts.push('      <div class="detail-label">Paid Leave Days</div>');
                        parts.push('      <div class="detail-value" style="color: var(--success-color);">' + formatNumber(audit.paidLeaveDays) + ' days</div>');
                        parts.push('    </div>');
                    }
                    
                    if (audit.unpaidLeaveDays !== undefined && audit.unpaidLeaveDays !== null) {
                        parts.push('    <div class="detail-item">');
                        parts.push('      <div class="detail-label">Unpaid Leave Days</div>');
                        parts.push('      <div class="detail-value" style="color: var(--error);">' + formatNumber(audit.unpaidLeaveDays) + ' days</div>');
                        parts.push('    </div>');
                    }
                    
                    if (audit.overtimeHours !== undefined && audit.overtimeHours !== null) {
                        parts.push('    <div class="detail-item">');
                        parts.push('      <div class="detail-label">Overtime Hours</div>');
                        parts.push('      <div class="detail-value" style="color: var(--success-color);">' + formatNumber(audit.overtimeHours) + ' hours</div>');
                        parts.push('    </div>');
                    }
                    
                    parts.push('  </div>');
                    parts.push('</div>');

                    // Salary Breakdown Section (from PayrollAudit)
                    parts.push('<div class="detail-section">');
                    parts.push('  <div class="detail-section-title"><i class="fas fa-calculator"></i> Salary Breakdown (from PayrollAudit)</div>');
                    parts.push('  <div class="detail-grid" style="grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));">');
                    
                    if (audit.baseSalary !== undefined && audit.baseSalary !== null) {
                        parts.push('    <div class="detail-item">');
                        parts.push('      <div class="detail-label">Base Salary</div>');
                        parts.push('      <div class="detail-value currency">' + formatCurrency(audit.baseSalary) + '</div>');
                        parts.push('    </div>');
                    }
                    
                    if (audit.actualBaseSalary !== undefined && audit.actualBaseSalary !== null) {
                        parts.push('    <div class="detail-item">');
                        parts.push('      <div class="detail-label">Actual Base Salary</div>');
                        parts.push('      <div class="detail-value currency" style="color: var(--primary-color);">' + formatCurrency(audit.actualBaseSalary) + '</div>');
                        parts.push('    </div>');
                    }
                    
                    if (audit.otSalary !== undefined && audit.otSalary !== null) {
                        parts.push('    <div class="detail-item">');
                        parts.push('      <div class="detail-label">OT Salary</div>');
                        parts.push('      <div class="detail-value currency" style="color: var(--success-color);">+ ' + formatCurrency(audit.otSalary) + '</div>');
                        parts.push('    </div>');
                    }
                    
                    if (audit.allowance !== undefined && audit.allowance !== null) {
                        parts.push('    <div class="detail-item">');
                        parts.push('      <div class="detail-label">Allowance</div>');
                        parts.push('      <div class="detail-value currency" style="color: var(--success-color);">+ ' + formatCurrency(audit.allowance) + '</div>');
                        parts.push('    </div>');
                    }
                    
                    parts.push('  </div>');
                    parts.push('</div>');

                    // Insurance & Tax Section
                    parts.push('<div class="detail-section">');
                    parts.push('  <div class="detail-section-title"><i class="fas fa-shield-alt"></i> Insurance & Tax</div>');
                    parts.push('  <div class="detail-grid" style="grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));">');
                    
                    if (audit.bhxh !== undefined && audit.bhxh !== null) {
                        parts.push('    <div class="detail-item">');
                        parts.push('      <div class="detail-label">BHXH (8%)</div>');
                        parts.push('      <div class="detail-value currency" style="color: #3b82f6;">' + formatCurrency(audit.bhxh) + '</div>');
                        parts.push('    </div>');
                    }
                    
                    if (audit.bhyt !== undefined && audit.bhyt !== null) {
                        parts.push('    <div class="detail-item">');
                        parts.push('      <div class="detail-label">BHYT (1.5%)</div>');
                        parts.push('      <div class="detail-value currency" style="color: #10b981;">' + formatCurrency(audit.bhyt) + '</div>');
                        parts.push('    </div>');
                    }
                    
                    if (audit.bhtn !== undefined && audit.bhtn !== null) {
                        parts.push('    <div class="detail-item">');
                        parts.push('      <div class="detail-label">BHTN (1%)</div>');
                        parts.push('      <div class="detail-value currency" style="color: #8b5cf6;">' + formatCurrency(audit.bhtn) + '</div>');
                        parts.push('    </div>');
                    }
                    
                    if (audit.taxableIncome !== undefined && audit.taxableIncome !== null) {
                        parts.push('    <div class="detail-item">');
                        parts.push('      <div class="detail-label">Taxable Income</div>');
                        parts.push('      <div class="detail-value currency">' + formatCurrency(audit.taxableIncome) + '</div>');
                        parts.push('    </div>');
                    }
                    
                    if (audit.personalTax !== undefined && audit.personalTax !== null) {
                        parts.push('    <div class="detail-item">');
                        parts.push('      <div class="detail-label">Personal Tax (TNCN)</div>');
                        parts.push('      <div class="detail-value currency" style="color: var(--error);">- ' + formatCurrency(audit.personalTax) + '</div>');
                        parts.push('    </div>');
                    }
                    
                    parts.push('  </div>');
                    parts.push('</div>');

                    // Deductions Section
                    parts.push('<div class="detail-section">');
                    parts.push('  <div class="detail-section-title"><i class="fas fa-minus-circle"></i> Deductions</div>');
                    parts.push('  <div class="detail-grid" style="grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));">');
                    
                    if (audit.absentPenalty !== undefined && audit.absentPenalty !== null) {
                        parts.push('    <div class="detail-item">');
                        parts.push('      <div class="detail-label">Absent Penalty</div>');
                        parts.push('      <div class="detail-value currency" style="color: var(--error);">- ' + formatCurrency(audit.absentPenalty) + '</div>');
                        parts.push('    </div>');
                    }
                    
                    if (audit.otherDeduction !== undefined && audit.otherDeduction !== null) {
                        parts.push('    <div class="detail-item">');
                        parts.push('      <div class="detail-label">Other Deduction</div>');
                        parts.push('      <div class="detail-value currency" style="color: var(--error);">- ' + formatCurrency(audit.otherDeduction) + '</div>');
                        parts.push('    </div>');
                    }
                    
                    if (audit.totalDeduction !== undefined && audit.totalDeduction !== null) {
                        parts.push('    <div class="detail-item" style="border-left: 4px solid var(--error);">');
                        parts.push('      <div class="detail-label">Total Deduction</div>');
                        parts.push('      <div class="detail-value currency" style="color: var(--error); font-size: 1.2rem; font-weight: 700;">- ' + formatCurrency(audit.totalDeduction) + '</div>');
                        parts.push('    </div>');
                    }
                    
                    parts.push('  </div>');
                    parts.push('</div>');

                    // Net Salary Section
                    parts.push('<div class="detail-section">');
                    parts.push('  <div class="detail-section-title"><i class="fas fa-wallet"></i> Net Salary (from PayrollAudit)</div>');
                    parts.push('  <div style="background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%); padding: 24px; border-radius: 12px; text-align: center;">');
                    parts.push('    <div class="detail-label" style="color: rgba(255,255,255,0.9); margin-bottom: 12px;">Total Net Salary</div>');
                    parts.push('    <div class="detail-value currency" style="color: white; font-size: 2rem; margin: 0;">' + formatCurrency(audit.netSalary || netSalary) + '</div>');
                    parts.push('  </div>');
                    parts.push('</div>');

                    // Notes Section
                    if (audit.notes) {
                        parts.push('<div class="detail-section">');
                        parts.push('  <div class="detail-section-title"><i class="fas fa-sticky-note"></i> Notes</div>');
                        parts.push('  <div style="background: #f8f9fa; border: 1px solid var(--border-color); border-radius: 8px; padding: 16px; color: var(--text-color);">');
                        parts.push('    <div style="white-space: pre-wrap; line-height: 1.6;">' + audit.notes + '</div>');
                        parts.push('  </div>');
                        parts.push('</div>');
                    }
                } else {
                    // Fallback: Show basic payroll info if no audit data
                    parts.push('<div class="detail-section">');
                    parts.push('  <div class="detail-section-title"><i class="fas fa-calculator"></i> Salary Breakdown</div>');
                    parts.push('  <div class="detail-grid">');
                    parts.push('    <div class="detail-item">');
                    parts.push('      <div class="detail-label">Base Salary</div>');
                    parts.push('      <div class="detail-value currency">' + formatCurrency(baseSalary) + '</div>');
                    parts.push('    </div>');
                    parts.push('    <div class="detail-item">');
                    parts.push('      <div class="detail-label">Allowance</div>');
                    parts.push('      <div class="detail-value currency" style="color: var(--success-color);">+ ' + formatCurrency(allowance) + '</div>');
                    parts.push('    </div>');
                    parts.push('    <div class="detail-item">');
                    parts.push('      <div class="detail-label">OT Salary</div>');
                    parts.push('      <div class="detail-value currency" style="color: var(--success-color);">+ ' + formatCurrency(otSalary) + '</div>');
                    parts.push('    </div>');
                    parts.push('    <div class="detail-item">');
                    parts.push('      <div class="detail-label">Deduction</div>');
                    parts.push('      <div class="detail-value currency" style="color: var(--danger-color);">- ' + formatCurrency(deduction) + '</div>');
                    parts.push('    </div>');
                    parts.push('  </div>');
                    parts.push('</div>');

                    parts.push('<div class="detail-section">');
                    parts.push('  <div class="detail-section-title"><i class="fas fa-wallet"></i> Net Salary</div>');
                    parts.push('  <div style="background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%); padding: 24px; border-radius: 12px; text-align: center;">');
                    parts.push('    <div class="detail-label" style="color: rgba(255,255,255,0.9); margin-bottom: 12px;">Total Net Salary</div>');
                    parts.push('    <div class="detail-value currency" style="color: white; font-size: 2rem; margin: 0;">' + formatCurrency(netSalary) + '</div>');
                    parts.push('  </div>');
                    parts.push('</div>');

                    parts.push('<div class="detail-section">');
                    parts.push('  <div class="detail-section-title"><i class="fas fa-clipboard-list"></i> Payroll Audit</div>');
                    parts.push('  <div style="padding:16px; border:1px dashed var(--border-color); border-radius:12px; color: var(--muted-color);">');
                    parts.push('    No audit details available for this payroll.');
                    parts.push('  </div>');
                    parts.push('</div>');
                }

                content.innerHTML = parts.join('');
            };

            window.closePayrollModal = function() {
                document.getElementById('payrollDetailModal').classList.remove('active');
            };

            window.approvePayroll = function(payrollId) {
                if (confirm('Bạn có chắc muốn duyệt bảng lương này?')) {
                    const form = document.createElement('form');
                    form.method = 'POST';
                    form.action = appContext + '/hr/payroll-approval';

                    const { status, employee, payPeriod, page, pageSize } = getCurrentFilters();
                    
                    const actionInput = document.createElement('input');
                    actionInput.type = 'hidden';
                    actionInput.name = 'action';
                    actionInput.value = 'approve';
                    form.appendChild(actionInput);
                    
                    const payrollIdInput = document.createElement('input');
                    payrollIdInput.type = 'hidden';
                    payrollIdInput.name = 'payrollId';
                    payrollIdInput.value = payrollId;
                    form.appendChild(payrollIdInput);
                    
                    if (status) {
                        const statusInput = document.createElement('input');
                        statusInput.type = 'hidden';
                        statusInput.name = 'status';
                        statusInput.value = status;
                        form.appendChild(statusInput);
                    }
                    if (employee) {
                        const empInput = document.createElement('input');
                        empInput.type = 'hidden';
                        empInput.name = 'employeeFilter';
                        empInput.value = employee;
                        form.appendChild(empInput);
                    }
                    if (payPeriod) {
                        const periodInput = document.createElement('input');
                        periodInput.type = 'hidden';
                        periodInput.name = 'payPeriod';
                        periodInput.value = payPeriod;
                        form.appendChild(periodInput);
                    }
                    if (page) {
                        const pageInput = document.createElement('input');
                        pageInput.type = 'hidden';
                        pageInput.name = 'page';
                        pageInput.value = page;
                        form.appendChild(pageInput);
                    }
                    if (pageSize) {
                        const sizeInput = document.createElement('input');
                        sizeInput.type = 'hidden';
                        sizeInput.name = 'pageSize';
                        sizeInput.value = pageSize;
                        form.appendChild(sizeInput);
                    }
                    
                    document.body.appendChild(form);
                    form.submit();
                }
            };

            window.rejectPayroll = function(payrollId) {
                const modal = document.getElementById('rejectPayrollModal');
                const payrollIdInput = document.getElementById('rejectPayrollId');
                const batchPayrollIdsInput = document.getElementById('batchRejectPayrollIds');
                const rejectNoteTextarea = document.getElementById('rejectNote');
                
                payrollIdInput.value = payrollId;
                if (batchPayrollIdsInput) {
                    batchPayrollIdsInput.value = ''; // Clear batch IDs for single reject
                }
                rejectNoteTextarea.value = ''; // Clear previous note
                rejectNoteTextarea.placeholder = 'Please provide a reason for rejecting this payroll...';
                modal.classList.add('active');
            };

            window.closeRejectModal = function() {
                const modal = document.getElementById('rejectPayrollModal');
                const form = document.getElementById('rejectPayrollForm');
                const batchPayrollIdsInput = document.getElementById('batchRejectPayrollIds');
                modal.classList.remove('active');
                form.reset();
                if (batchPayrollIdsInput) {
                    batchPayrollIdsInput.value = '';
                }
            };

            // ========== Batch Selection Functions ==========
            
            window.toggleSelectAll = function(checkbox) {
                const checkboxes = document.querySelectorAll('.payroll-checkbox');
                checkboxes.forEach(cb => {
                    cb.checked = checkbox.checked;
                });
                updateBatchActions();
            };

            window.updateBatchActions = function() {
                const checkboxes = document.querySelectorAll('.payroll-checkbox:checked');
                const selectedCount = checkboxes.length;
                const batchBar = document.getElementById('batchActionsBar');
                const selectedCountEl = document.getElementById('selectedCount');
                const batchApproveBtn = document.getElementById('batchApproveBtn');
                const batchRejectBtn = document.getElementById('batchRejectBtn');

                if (selectedCount > 0) {
                    batchBar.style.display = 'flex';
                    selectedCountEl.textContent = selectedCount + ' selected';
                    
                    // Check which actions are available based on selected payrolls
                    let hasPending = false;
                    
                    checkboxes.forEach(cb => {
                        const status = cb.getAttribute('data-payroll-status');
                        if (status === 'Pending') {
                            hasPending = true;
                        }
                    });
                    
                    // Show buttons only if there are Pending payrolls
                    batchApproveBtn.style.display = hasPending ? 'inline-block' : 'none';
                    batchRejectBtn.style.display = hasPending ? 'inline-block' : 'none';
                } else {
                    batchBar.style.display = 'none';
                }
                
                // Update select all checkbox state
                const allCheckboxes = document.querySelectorAll('.payroll-checkbox');
                const selectAllCheckbox = document.getElementById('selectAllCheckbox');
                if (allCheckboxes.length > 0) {
                    const allChecked = Array.from(allCheckboxes).every(cb => cb.checked);
                    selectAllCheckbox.checked = allChecked;
                }
            };

            window.getSelectedPayrollIds = function() {
                const checkboxes = document.querySelectorAll('.payroll-checkbox:checked');
                return Array.from(checkboxes).map(cb => cb.getAttribute('data-payroll-id'));
            };

            window.approveSelectedPayrolls = function() {
                const selectedIds = getSelectedPayrollIds();
                
                if (selectedIds.length === 0) {
                    alert('Please select at least one payroll to approve.');
                    return;
                }
                
                const count = selectedIds.length;
                if (confirm('Bạn có chắc muốn duyệt ' + count + ' bảng lương đã chọn?')) {
                    const form = document.createElement('form');
                    form.method = 'POST';
                    form.action = appContext + '/hr/payroll-approval/batch-approve';

                    const { status, employee, payPeriod, page, pageSize } = getCurrentFilters();
                    
                    // Add payroll IDs
                    selectedIds.forEach(id => {
                        const input = document.createElement('input');
                        input.type = 'hidden';
                        input.name = 'payrollIds';
                        input.value = id;
                        form.appendChild(input);
                    });
                    
                    // Preserve filters
                    if (status) {
                        const statusInput = document.createElement('input');
                        statusInput.type = 'hidden';
                        statusInput.name = 'status';
                        statusInput.value = status;
                        form.appendChild(statusInput);
                    }
                    if (employee) {
                        const empInput = document.createElement('input');
                        empInput.type = 'hidden';
                        empInput.name = 'employeeFilter';
                        empInput.value = employee;
                        form.appendChild(empInput);
                    }
                    if (payPeriod) {
                        const periodInput = document.createElement('input');
                        periodInput.type = 'hidden';
                        periodInput.name = 'payPeriod';
                        periodInput.value = payPeriod;
                        form.appendChild(periodInput);
                    }
                    if (page) {
                        const pageInput = document.createElement('input');
                        pageInput.type = 'hidden';
                        pageInput.name = 'page';
                        pageInput.value = page;
                        form.appendChild(pageInput);
                    }
                    if (pageSize) {
                        const sizeInput = document.createElement('input');
                        sizeInput.type = 'hidden';
                        sizeInput.name = 'pageSize';
                        sizeInput.value = pageSize;
                        form.appendChild(sizeInput);
                    }
                    
                    document.body.appendChild(form);
                    form.submit();
                }
            };

            window.rejectSelectedPayrolls = function() {
                const selectedIds = getSelectedPayrollIds();
                
                if (selectedIds.length === 0) {
                    alert('Please select at least one payroll to reject.');
                    return;
                }
                
                const count = selectedIds.length;
                if (confirm('Bạn có chắc muốn từ chối ' + count + ' bảng lương đã chọn?\n\nBạn sẽ cần nhập lý do từ chối áp dụng cho toàn bộ các mục đã chọn.')) {
                    // Store selected IDs in the hidden input for batch reject modal
                    const batchPayrollIdsInput = document.getElementById('batchRejectPayrollIds');
                    if (batchPayrollIdsInput) {
                        batchPayrollIdsInput.value = selectedIds.join(',');
                    }
                    
                    // Open reject modal with batch mode
                    const modal = document.getElementById('rejectPayrollModal');
                    const payrollIdInput = document.getElementById('rejectPayrollId');
                    const rejectNoteTextarea = document.getElementById('rejectNote');
                    
                    payrollIdInput.value = ''; // Clear single payroll ID
                    rejectNoteTextarea.value = ''; // Clear previous note
                    rejectNoteTextarea.placeholder = 'Enter rejection reason for ' + count + ' selected payroll(s)...';
                    modal.classList.add('active');
                }
            };

            window.clearSelection = function() {
                const checkboxes = document.querySelectorAll('.payroll-checkbox');
                checkboxes.forEach(cb => cb.checked = false);
                document.getElementById('selectAllCheckbox').checked = false;
                updateBatchActions();
            };

            window.formatCurrency = function(amount) {
                return new Intl.NumberFormat('en-US').format(amount);
            };

            window.changePayrollPageSize = function(newSize) {
                const { status, employee, payPeriod } = getCurrentFilters();
                const params = new URLSearchParams();
                if (status) {
                    params.append('status', status);
                }
                if (employee) {
                    params.append('employeeFilter', employee);
                }
                if (payPeriod) {
                    params.append('payPeriod', payPeriod);
                }
                if (newSize) {
                    params.append('pageSize', newSize);
                }
                params.append('page', '1');
                window.location.href = appContext + '/hr/payroll-approval' + (params.toString() ? ('?' + params.toString()) : '');
            };

            // Close modal when clicking outside
            document.getElementById('payrollDetailModal').addEventListener('click', function(e) {
                if (e.target === this) {
                    closePayrollModal();
                }
            });

            // Close modal with ESC key
            document.addEventListener('keydown', function(e) {
                if (e.key === 'Escape') {
                    const modal = document.getElementById('payrollDetailModal');
                    if (modal && modal.classList.contains('active')) {
                        closePayrollModal();
                    }
                }
            });
        </script>
    </body>
</html>
