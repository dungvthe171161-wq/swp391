<%-- 
    Document   : PayrollManagement
    Created on : Nov 5, 2025
    Author     : admin
    Description: Payroll Management for HR Staff - Manage Allowances, Deductions, and Payroll
    FIXED: Enhanced JavaScript error handling and form submission flows
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, java.math.BigDecimal, com.hrm.model.entity.Employee" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%!
    private String payrollStatusLabel(String status) {
        if ("Draft".equals(status)) return "Bản nháp";
        if ("Pending".equals(status)) return "Chờ phê duyệt";
        if ("Approved".equals(status)) return "Đã duyệt";
        if ("Rejected".equals(status)) return "Bị từ chối";
        if ("Paid".equals(status)) return "Đã thanh toán";
        return status != null ? status : "Không có";
    }
%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý lương - Nhân viên nhân sự</title>
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css"/>
        <link rel="stylesheet" href="<%=request.getContextPath()%>/Admin/css/pagination.css"/>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <style>
            :root {
                --primary:#5b5bd6;
                --secondary:#8b5bd6;
                --bg:#f3f4f6;
                --card:#ffffff;
                --border:#e5e7eb;
                --text:#111827;
                --muted:#6b7280;
                --success:#10b981;
                --error:#ef4444;
                --warning:#f59e0b;
            }

            body {
                background: var(--bg);
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                margin: 0;
                padding: 0;
            }

            .topbar {
                height:64px;
                display:flex;
                align-items:center;
                justify-content:space-between;
                padding:0 20px;
                background: linear-gradient(90deg, var(--primary), var(--secondary));
                color:#fff;
                box-shadow: 0 2px 6px rgba(0,0,0,0.08);
            }

            .brand {
                display:flex;
                align-items:center;
                gap:10px;
                font-weight:700;
                font-size:22px;
            }

            .brand .logo {
                width:28px;
                height:28px;
                background:#fff;
                color:var(--primary);
                border-radius:8px;
                display:flex;
                align-items:center;
                justify-content:center;
                font-weight:800;
            }

            .top-actions {
                display:flex;
                align-items:center;
                gap:12px;
            }

            .search {
                display:flex;
                align-items:center;
                gap:8px;
                background:rgba(255,255,255,0.2);
                padding:6px 10px;
                border-radius:999px;
            }

            .search input {
                border:none;
                outline:none;
                background:transparent;
                color:#fff;
                width:280px;
            }

            .btn {
                display:inline-block;
                padding:10px 14px;
                border-radius:8px;
                text-decoration:none;
                color:#fff;
                background:#2563eb;
                border: none;
                cursor: pointer;
                font-size: 14px;
                transition: all 0.2s;
            }

            .btn:hover {
                background: #1d4ed8;
                transform: translateY(-1px);
            }

            .btn.secondary {
                background: var(--success);
            }

            .btn.secondary:hover {
                background: #059669;
            }

            .container {
                width:100%;
                max-width:none;
                margin:0;
                padding: 20px;
                display:grid;
                grid-template-columns: 280px 1fr;
                gap:20px;
                align-items:start;
            }

            .sidebar {
                background: var(--card);
                border:1px solid var(--border);
                border-radius:12px;
                padding:14px;
                position:sticky;
                top:20px;
                height: fit-content;
            }

            .side-group {
                padding:12px 8px;
                border-top:1px solid var(--border);
            }

            .side-group:first-of-type {
                border-top:none;
            }

            .side-title {
                font-weight:700;
                color: var(--muted);
                font-size:13px;
                letter-spacing:.5px;
                text-transform:uppercase;
                margin-bottom:10px;
            }

            .side-link {
                display:flex;
                align-items:center;
                gap:10px;
                padding:12px 10px;
                margin-bottom:8px;
                border-radius:10px;
                color:#111;
                text-decoration:none;
                background:#eef2ff;
                transition: all 0.2s;
            }

            .side-link:hover {
                background: #dbeafe;
            }

            .side-link.neutral {
                background:#fff;
                border:1px solid var(--border);
            }

            .side-link.active {
                background: var(--primary);
                color: #fff;
            }

            .content {
                display:flex;
                flex-direction:column;
                gap:16px;
            }

            .hero {
                background: linear-gradient(90deg, var(--primary), var(--secondary));
                color:#fff;
                border-radius:14px;
                padding:22px;
            }

            .hero h2 {
                margin:0 0 6px 0;
            }

            .hero .muted {
                color: rgba(255,255,255,0.9);
            }

            .card {
                border:1px solid var(--border);
                border-radius:12px;
                background:var(--card);
                padding:20px;
            }

            .muted {
                color: var(--muted);
                font-size:14px;
            }

            /* Tabs */
            .tabs {
                display:flex;
                gap:8px;
                border-bottom:2px solid var(--border);
                margin-bottom:20px;
            }

            .tab-btn {
                padding:12px 24px;
                background:none;
                border:none;
                border-bottom:3px solid transparent;
                cursor:pointer;
                font-size:14px;
                font-weight:600;
                color:var(--muted);
                transition:all 0.2s;
                text-decoration:none;
                display:inline-block;
            }

            .tab-btn:hover {
                color:var(--primary);
                text-decoration:none;
            }

            .tab-btn.active {
                color:var(--primary);
                border-bottom-color:var(--primary);
            }
            
            .tab-btn.active:hover {
                color:var(--primary);
            }

            .tab-content {
                display:none;
            }

            .tab-content.active {
                display:block;
            }

            /* Forms */
            .form-group {
                margin-bottom:20px;
            }

            .form-group label {
                display:block;
                margin-bottom:8px;
                font-weight:600;
                color:var(--text);
            }

            .form-group input,
            .form-group select,
            .form-group textarea {
                width:100%;
                padding:10px 12px;
                border:1px solid var(--border);
                border-radius:8px;
                font-size:14px;
                transition:border-color 0.2s;
            }

            .form-group input:focus,
            .form-group select:focus,
            .form-group textarea:focus {
                outline:none;
                border-color:var(--primary);
            }

            .form-row {
                display:grid;
                grid-template-columns:1fr 1fr;
                gap:16px;
            }

            .required {
                color:var(--error);
            }

            /* Tables */
            table {
                width:100%;
                border-collapse:collapse;
                margin-top:16px;
            }

            th, td {
                padding:12px;
                border-bottom:1px solid var(--border);
                text-align:left;
            }

            th {
                background:#f9fafb;
                font-weight:600;
                color:var(--text);
            }

            tr:hover {
                background:#f9fafb;
            }

            /* Status badges */
            .status-badge {
                padding:4px 12px;
                border-radius:12px;
                font-size:12px;
                font-weight:600;
                display:inline-block;
            }

            .status-Draft {
                background:#e5e7eb;
                color:#374151;
            }

            .status-Pending {
                background:#fef3c7;
                color:#92400e;
            }

            .status-Approved {
                background:#d1fae5;
                color:#065f46;
            }

            .status-Rejected {
                background:#fee2e2;
                color:#991b1b;
            }

            .status-Paid {
                background:#dbeafe;
                color:#1e40af;
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
                display:flex;
                gap:6px;
                align-items:center;
                justify-content:flex-start;
            }

            .btn-small {
                padding:6px 12px;
                font-size:12px;
                display:inline-flex;
                align-items:center;
                gap:4px;
                font-weight:500;
                white-space:nowrap;
                box-shadow:0 1px 2px rgba(0,0,0,0.05);
            }

            .btn-small:hover {
                box-shadow:0 2px 4px rgba(0,0,0,0.1);
                transform:translateY(-1px);
            }

            .btn-small i {
                font-size:11px;
            }

            .btn-danger {
                background:var(--error);
            }

            .btn-danger:hover {
                background:#dc2626;
            }

            .btn-warning {
                background:var(--warning);
            }

            .btn-warning:hover {
                background:#d97706;
            }

            /* Summary cards */
            .summary-grid {
                display:grid;
                grid-template-columns:repeat(4, 1fr);
                gap:16px;
                margin:20px 0;
            }

            .summary-card {
                background:var(--card);
                border:1px solid var(--border);
                border-radius:8px;
                padding:16px;
                border-left:4px solid var(--primary);
            }

            .summary-card h4 {
                margin:0 0 8px 0;
                font-size:12px;
                color:var(--muted);
                text-transform:uppercase;
            }

            .summary-card .value {
                font-size:24px;
                font-weight:700;
                color:var(--text);
            }

            .empty {
                color:var(--muted);
                padding:40px;
                text-align:center;
            }

            /* Modal */
            .modal {
                display:none;
                position:fixed;
                top:0;
                left:0;
                width:100%;
                height:100%;
                background:rgba(0,0,0,0.5);
                z-index:1000;
                align-items:center;
                justify-content:center;
            }

            .modal.active {
                display:flex;
            }

            .modal-content {
                background:var(--card);
                border-radius:12px;
                padding:24px;
                max-width:600px;
                width:90%;
                max-height:90vh;
                overflow-y:auto;
            }

            .modal-header {
                display:flex;
                justify-content:space-between;
                align-items:center;
                margin-bottom:20px;
            }

            .modal-header h3 {
                margin:0;
            }

            .close-btn {
                background:none;
                border:none;
                font-size:24px;
                cursor:pointer;
                color:var(--muted);
            }

            .close-btn:hover {
                color:var(--text);
            }

            @media (max-width: 1024px) {
                .container {
                    grid-template-columns: 1fr;
                }
                .sidebar {
                    position:relative;
                    top:auto;
                }
                .form-row {
                    grid-template-columns: 1fr;
                }
                .summary-grid {
                    grid-template-columns: repeat(2, 1fr);
                }
            }
            
            /* Pagination styles */
            .pagination-bar {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 1rem;
                background-color: #fff;
                border-radius: 4px;
                margin-top: 1rem;
                box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
            }
            
            .pagination-info {
                color: #666;
                font-size: 14px;
                display: flex;
                align-items: center;
                gap: 20px;
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
                color: #0066cc;
                text-decoration: none;
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
            
            .pagination-controls span.ellipsis {
                border: none;
                padding: 0 4px;
            }
            
            .page-size-selector {
                display: inline-flex;
                align-items: center;
                gap: 8px;
            }
            
            .page-size-selector label {
                font-size: 14px;
                color: #666;
            }
            
            .page-size-selector select {
                padding: 4px 8px;
                border: 1px solid #ddd;
                border-radius: 4px;
                font-size: 14px;
            }
            
            /* Sortable table headers */
            table thead th[style*="cursor:pointer"] {
                user-select: none;
                transition: background-color 0.2s;
            }
            
            table thead th[style*="cursor:pointer"]:hover {
                background-color: #f8f9fa;
            }
        </style>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/hr-theme.css?v=hr-staff-shell-20260627-1">
    </head>
    <body class="hr-staff-page-shell">
        <%
            request.setAttribute("hrStaffSidebarActive", "payroll");
            request.setAttribute("hrStaffPageTitle", "Lương & phụ cấp");
            request.setAttribute("hrStaffSearchPlaceholder", "Tìm kiếm nhân viên, bảng lương...");
            request.setAttribute("hrStaffProfileSubtitle", "Quản trị bảng lương");
        %>
        <div class="staff-shell">
            <%@ include file="_HrStaffSidebar.jspf" %>
            <main class="staff-main">
                <%@ include file="_HrStaffTopbar.jspf" %>
                <section class="staff-content">
        <!-- Thanh điều hướng nhanh -->
        <div class="topbar">
            <div class="brand">
                <div class="logo">HR</div>
                <div>Bảng lương nhân sự</div>
            </div>
            <div class="top-actions">
                <div class="search">
                    <span>🔍</span>
                    <input type="text" placeholder="Tìm nhân viên, bảng lương..."/>
                </div>
                <a class="btn secondary" href="<%=request.getContextPath()%>/homepage">Trang chủ</a>
            </div>
        </div>

        <div class="container">
            <!-- Sidebar -->
            <aside class="sidebar">
                <div class="side-group">
                    <div class="side-title">Chính</div>
                    <a class="side-link" href="<%=request.getContextPath()%>/hrstaff">🏠 Trang nhân sự</a>
                </div>
                <div class="side-group">
                    <div class="side-title">Yêu cầu & phê duyệt</div>
                    <a class="side-link neutral" href="#">✅ Yêu cầu & đề xuất</a>
                </div>
                <div class="side-group">
                    <div class="side-title">Lương & hợp đồng</div>
                    <a class="side-link active" href="<%=request.getContextPath()%>/hrstaff/payroll">💰 Lương & phụ cấp</a>
                    <a class="side-link neutral" href="<%=request.getContextPath()%>/hrstaff/contracts/create">📝 Tạo hợp đồng</a>
                    <a class="side-link neutral" href="<%=request.getContextPath()%>/hrstaff/contracts">📄 Danh sách hợp đồng</a>
                </div>
                <div class="side-group">
                    <div class="side-title">Tuyển dụng</div>
                    <a class="side-link neutral" href="${pageContext.request.contextPath}/postRecruitments">📢 Tin tuyển dụng</a>
                    <a class="side-link neutral" href="${pageContext.request.contextPath}/candidates">👀 Ứng viên</a>
                    <a class="side-link neutral" href="#">➕ Tạo nhân viên</a>
                </div>
            </aside>

            <!-- Content Area -->
            <main class="content">
                <section class="hero">
                    <h2>Quản lý lương</h2>
                    <div class="muted">Quản lý phụ cấp, khấu trừ và tính lương nhân viên</div>
                </section>

                <%-- Display success/error messages from session --%>
                <%
                    String successMessage = (String) request.getSession().getAttribute("success");
                    String errorMessage = (String) request.getSession().getAttribute("error");
                    if (successMessage != null) {
                        request.getSession().removeAttribute("success");
                %>
                <div style="background:#d4edda; border:1px solid #c3e6cb; border-radius:6px; padding:12px; margin-bottom:20px; color:#155724;">
                    <strong>✓ Thành công:</strong> <%= successMessage %>
                </div>
                <%
                    }
                    if (errorMessage != null) {
                        request.getSession().removeAttribute("error");
                %>
                <div style="background:#f8d7da; border:1px solid #f5c6cb; border-radius:6px; padding:12px; margin-bottom:20px; color:#721c24;">
                    <strong>✗ Lỗi:</strong> <%= errorMessage %>
                </div>
                <%
                    }
                %>

                <!-- Tabs -->
                <div class="card">
                    <div class="tabs">
                        <%
                            String currentTab = (String) request.getAttribute("currentTab");
                            if (currentTab == null) currentTab = "allowance";
                        %>
                        <c:url var="allowanceUrl" value="/hrstaff/payroll">
                            <c:param name="tab" value="allowance"/>
                            <c:param name="allowancePage" value="1"/>
                            <c:param name="allowancePageSize" value="${allowancePageSize != null ? allowancePageSize : 10}"/>
                        </c:url>
                        <a href="${allowanceUrl}" 
                           class="tab-btn <%= "allowance".equals(currentTab) ? "active" : "" %>">💰 Phụ cấp</a>
                        <c:url var="deductionUrl" value="/hrstaff/payroll">
                            <c:param name="tab" value="deduction"/>
                            <c:param name="deductionPage" value="1"/>
                            <c:param name="deductionPageSize" value="${deductionPageSize != null ? deductionPageSize : 10}"/>
                        </c:url>
                        <a href="${deductionUrl}" 
                           class="tab-btn <%= "deduction".equals(currentTab) ? "active" : "" %>">➖ Khấu trừ</a>
                        <a href="<%=request.getContextPath()%>/hrstaff/payroll?tab=attendance" 
                           class="tab-btn <%= "attendance".equals(currentTab) ? "active" : "" %>">📅 Chấm công</a>
                        <a href="<%=request.getContextPath()%>/hrstaff/payroll?tab=payroll" 
                           class="tab-btn <%= "payroll".equals(currentTab) ? "active" : "" %>">📊 Bảng lương</a>
                    </div>

                    <!-- Allowance Tab -->
                    <div id="allowance" class="tab-content <%= "allowance".equals(currentTab) ? "active" : "" %>">
                        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:20px;">
                            <h3 style="margin:0;">Phụ cấp nhân viên</h3>
                            <button class="btn" onclick="openAllowanceModal()">+ Thêm phụ cấp</button>
                        </div>

                        <!-- Filter -->
                        <div class="form-row" style="margin-bottom:20px;">
                            <div class="form-group">
                                <label>Nhân viên</label>
                                <select id="allowanceEmployeeFilter" onchange="filterAllowances()">
                                    <option value="">Tất cả nhân viên</option>
                                    <%
                                        List<Employee> employees = (List<Employee>) request.getAttribute("employees");
                                        String employeeFilter = (String) request.getAttribute("employeeFilter");
                                        if (employees != null) {
                                            for (Employee emp : employees) {
                                                String selected = (employeeFilter != null && employeeFilter.equals(String.valueOf(emp.getEmployeeId()))) ? "selected" : "";
                                    %>
                                    <option value="<%= emp.getEmployeeId() %>" <%= selected %>><%= emp.getFullName() %> (ID: <%= emp.getEmployeeId() %>)</option>
                                    <%
                                            }
                                        }
                                    %>
                                </select>
                            </div>
                            <div class="form-group">
                                <label>Tháng</label>
                                <input type="month" id="allowanceMonthFilter" onchange="filterAllowances()" value="<%= request.getAttribute("allowanceMonth") != null ? request.getAttribute("allowanceMonth") : "" %>"/>
                            </div>
                        </div>

                        <!-- Allowance List -->
                        <table id="allowanceTable">
                            <thead>
                                <tr>
                                    <th>Nhân viên</th>
                                    <th>Loại phụ cấp</th>
                                    <th>Số tiền</th>
                                    <th>Tháng</th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody id="allowanceTableBody">
                                <%
                                    List<Map<String, Object>> allowances = (List<Map<String, Object>>) request.getAttribute("allowances");
                                    if (allowances == null || allowances.isEmpty()) {
                                %>
                                <tr>
                                    <td colspan="5" class="empty">Chưa có phụ cấp. Nhấn "Thêm phụ cấp" để tạo mới.</td>
                                </tr>
                                <%
                                    } else {
                                        for (Map<String, Object> allowance : allowances) {
                                %>
                                <tr>
                                    <td><%= allowance.get("employeeName") %></td>
                                    <td><%= allowance.get("allowanceName") %></td>
                                    <td><%= String.format("%,d VNĐ", ((java.math.BigDecimal) allowance.get("amount")).intValue()) %></td>
                                    <td><%= allowance.get("month") %></td>
                                    <td>
                                        <div class="action-btns">
                                            <button class="btn btn-small" onclick="editAllowance(<%= allowance.get("id") %>)" title="Chỉnh sửa phụ cấp">
                                                <i class="fas fa-edit"></i> Sửa
                                            </button>
                                            <button class="btn btn-small btn-danger" onclick="deleteAllowance(<%= allowance.get("id") %>)" title="Xóa phụ cấp">
                                                <i class="fas fa-trash"></i> Xóa
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                                <%
                                        }
                                    }
                                %>
                            </tbody>
                        </table>
                        
                        <%-- Pagination Bar for Allowance tab --%>
                        <c:if test="${currentTab == 'allowance'}">
                            <div class="pagination-bar">
                                <div class="pagination-info">
                                    <c:set var="total" value="${totalAllowances != null ? totalAllowances : 0}" />
                                    <c:set var="page" value="${allowancePage != null ? allowancePage : 1}" />
                                    <c:set var="pageSize" value="${allowancePageSize != null ? allowancePageSize : 10}" />
                                    <c:set var="totalPages" value="${allowanceTotalPages != null ? allowanceTotalPages : 1}" />
                                    
                                    <%-- Calculate display range --%>
                                    <c:set var="start" value="${total > 0 ? (page - 1) * pageSize + 1 : 0}" />
                                    <c:set var="end" value="${page * pageSize}" />
                                    <c:if test="${end > total}">
                                        <c:set var="end" value="${total}" />
                                    </c:if>
                                    <span>Hiển thị ${start} - ${end} trong tổng số ${total}</span>
                                    
                                    <div class="page-size-selector" style="margin-left: 20px; display: inline-block;">
                                        <label for="allowancePageSizeSelect" style="margin-right: 8px;">Số dòng mỗi trang:</label>
                                        <select id="allowancePageSizeSelect" onchange="changeAllowancePageSize(this.value)" style="padding: 4px 8px; border: 1px solid #ddd; border-radius: 4px;">
                                            <option value="5" <c:if test="${pageSize == 5}">selected</c:if>>5</option>
                                            <option value="10" <c:if test="${pageSize == 10}">selected</c:if>>10</option>
                                            <option value="20" <c:if test="${pageSize == 20}">selected</c:if>>20</option>
                                            <option value="50" <c:if test="${pageSize == 50}">selected</c:if>>50</option>
                                        </select>
                                    </div>
                                </div>

                                <div class="pagination-controls">
                                    <%-- Base URL --%>
                                    <c:url var="baseUrl" value="/hrstaff/payroll">
                                        <c:param name="tab" value="allowance" />
                                        <c:param name="allowancePageSize" value="${pageSize}" />
                                        <c:param name="employeeFilter" value="${employeeFilter != null ? employeeFilter : ''}" />
                                        <c:param name="allowanceMonth" value="${allowanceMonth != null ? allowanceMonth : ''}" />
                                    </c:url>

                                    <%-- Previous button --%>
                                    <c:choose>
                                        <c:when test="${page > 1}">
                                            <c:url var="prevUrl" value="${baseUrl}">
                                                <c:param name="allowancePage" value="${page - 1}" />
                                            </c:url>
                                            <a href="${prevUrl}" class="btn-pagination">← Trước</a>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="disabled">← Trước</span>
                                        </c:otherwise>
                                    </c:choose>

                                    <%-- Page numbers --%>
                                    <c:set var="range" value="2" />
                                    <c:set var="start_page" value="${page - range > 1 ? page - range : 1}" />
                                    <c:set var="end_page" value="${page + range < totalPages ? page + range : totalPages}" />

                                    <c:if test="${start_page > 1}">
                                        <c:url var="firstPageUrl" value="${baseUrl}">
                                            <c:param name="allowancePage" value="1" />
                                        </c:url>
                                        <a href="${firstPageUrl}" class="btn-pagination">1</a>
                                        <c:if test="${start_page > 2}">
                                            <span class="ellipsis">...</span>
                                        </c:if>
                                    </c:if>

                                    <c:forEach begin="${start_page}" end="${end_page}" var="i">
                                        <c:choose>
                                            <c:when test="${i == page}">
                                                <span class="active btn-pagination">${i}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <c:url var="pageUrl" value="${baseUrl}">
                                                    <c:param name="allowancePage" value="${i}" />
                                                </c:url>
                                                <a href="${pageUrl}" class="btn-pagination">${i}</a>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>

                                    <c:if test="${end_page < totalPages}">
                                        <c:if test="${end_page < totalPages - 1}">
                                            <span class="ellipsis">...</span>
                                        </c:if>
                                        <c:url var="lastPageUrl" value="${baseUrl}">
                                            <c:param name="allowancePage" value="${totalPages}" />
                                        </c:url>
                                        <a href="${lastPageUrl}" class="btn-pagination">${totalPages}</a>
                                    </c:if>

                                    <%-- Next button --%>
                                    <c:choose>
                                        <c:when test="${page < totalPages}">
                                            <c:url var="nextUrl" value="${baseUrl}">
                                                <c:param name="allowancePage" value="${page + 1}" />
                                            </c:url>
                                            <a href="${nextUrl}" class="btn-pagination">Sau →</a>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="disabled">Sau →</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </c:if>
                    </div>

                    <!-- Deduction Tab -->
                    <div id="deduction" class="tab-content <%= "deduction".equals(currentTab) ? "active" : "" %>">
                        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:20px;">
                            <h3 style="margin:0;">Khấu trừ nhân viên</h3>
                            <button class="btn" onclick="openDeductionModal()">+ Thêm khấu trừ</button>
                        </div>

                        <!-- Filter -->
                        <div class="form-row" style="margin-bottom:20px;">
                            <div class="form-group">
                                <label>Nhân viên</label>
                                <select id="deductionEmployeeFilter" onchange="filterDeductions()">
                                    <option value="">Tất cả nhân viên</option>
                                    <%
                                        if (employees != null) {
                                            for (Employee emp : employees) {
                                                String selected = (employeeFilter != null && employeeFilter.equals(String.valueOf(emp.getEmployeeId()))) ? "selected" : "";
                                    %>
                                    <option value="<%= emp.getEmployeeId() %>" <%= selected %>><%= emp.getFullName() %> (ID: <%= emp.getEmployeeId() %>)</option>
                                    <%
                                            }
                                        }
                                    %>
                                </select>
                            </div>
                            <div class="form-group">
                                <label>Tháng</label>
                                <input type="month" id="deductionMonthFilter" onchange="filterDeductions()" value="<%= request.getAttribute("deductionMonth") != null ? request.getAttribute("deductionMonth") : "" %>"/>
                            </div>
                        </div>

                        <!-- Deduction List -->
                        <table id="deductionTable">
                            <thead>
                                <tr>
                                    <th>Nhân viên</th>
                                    <th>Loại khấu trừ</th>
                                    <th>Số tiền</th>
                                    <th>Tháng</th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody id="deductionTableBody">
                                <%
                                    List<Map<String, Object>> deductions = (List<Map<String, Object>>) request.getAttribute("deductions");
                                    if (deductions == null || deductions.isEmpty()) {
                                %>
                                <tr>
                                    <td colspan="5" class="empty">Chưa có khoản khấu trừ. Nhấn "Thêm khấu trừ" để tạo mới.</td>
                                </tr>
                                <%
                                    } else {
                                        for (Map<String, Object> deduction : deductions) {
                                %>
                                <tr>
                                    <td><%= deduction.get("employeeName") %></td>
                                    <td><%= deduction.get("deductionName") %></td>
                                    <td><%= String.format("%,d VNĐ", ((java.math.BigDecimal) deduction.get("amount")).intValue()) %></td>
                                    <td><%= deduction.get("month") %></td>
                                    <td>
                                        <div class="action-btns">
                                            <button class="btn btn-small" onclick="editDeduction(<%= deduction.get("id") %>)">Sửa</button>
                                            <button class="btn btn-small btn-danger" onclick="deleteDeduction(<%= deduction.get("id") %>)">Xóa</button>
                                        </div>
                                    </td>
                                </tr>
                                <%
                                        }
                                    }
                                %>
                            </tbody>
                        </table>
                        
                        <%-- Pagination Bar for Deduction tab --%>
                        <c:if test="${currentTab == 'deduction'}">
                            <div class="pagination-bar">
                                <div class="pagination-info">
                                    <c:set var="total" value="${totalDeductions != null ? totalDeductions : 0}" />
                                    <c:set var="page" value="${deductionPage != null ? deductionPage : 1}" />
                                    <c:set var="pageSize" value="${deductionPageSize != null ? deductionPageSize : 10}" />
                                    <c:set var="totalPages" value="${deductionTotalPages != null ? deductionTotalPages : 1}" />
                                    
                                    <%-- Calculate display range --%>
                                    <c:set var="start" value="${total > 0 ? (page - 1) * pageSize + 1 : 0}" />
                                    <c:set var="end" value="${page * pageSize}" />
                                    <c:if test="${end > total}">
                                        <c:set var="end" value="${total}" />
                                    </c:if>
                                    <span>Hiển thị ${start} - ${end} trong tổng số ${total}</span>
                                    
                                    <div class="page-size-selector" style="margin-left: 20px; display: inline-block;">
                                        <label for="deductionPageSizeSelect" style="margin-right: 8px;">Số dòng mỗi trang:</label>
                                        <select id="deductionPageSizeSelect" onchange="changeDeductionPageSize(this.value)" style="padding: 4px 8px; border: 1px solid #ddd; border-radius: 4px;">
                                            <option value="5" <c:if test="${pageSize == 5}">selected</c:if>>5</option>
                                            <option value="10" <c:if test="${pageSize == 10}">selected</c:if>>10</option>
                                            <option value="20" <c:if test="${pageSize == 20}">selected</c:if>>20</option>
                                            <option value="50" <c:if test="${pageSize == 50}">selected</c:if>>50</option>
                                        </select>
                                    </div>
                                </div>

                                <div class="pagination-controls">
                                    <%-- Base URL --%>
                                    <c:url var="baseUrl" value="/hrstaff/payroll">
                                        <c:param name="tab" value="deduction" />
                                        <c:param name="deductionPageSize" value="${pageSize}" />
                                        <c:param name="employeeFilter" value="${employeeFilter != null ? employeeFilter : ''}" />
                                        <c:param name="deductionMonth" value="${deductionMonth != null ? deductionMonth : ''}" />
                                    </c:url>

                                    <%-- Previous button --%>
                                    <c:choose>
                                        <c:when test="${page > 1}">
                                            <c:url var="prevUrl" value="${baseUrl}">
                                                <c:param name="deductionPage" value="${page - 1}" />
                                            </c:url>
                                            <a href="${prevUrl}" class="btn-pagination">← Trước</a>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="disabled">← Trước</span>
                                        </c:otherwise>
                                    </c:choose>

                                    <%-- Page numbers --%>
                                    <c:set var="range" value="2" />
                                    <c:set var="start_page" value="${page - range > 1 ? page - range : 1}" />
                                    <c:set var="end_page" value="${page + range < totalPages ? page + range : totalPages}" />

                                    <c:if test="${start_page > 1}">
                                        <c:url var="firstPageUrl" value="${baseUrl}">
                                            <c:param name="deductionPage" value="1" />
                                        </c:url>
                                        <a href="${firstPageUrl}" class="btn-pagination">1</a>
                                        <c:if test="${start_page > 2}">
                                            <span class="ellipsis">...</span>
                                        </c:if>
                                    </c:if>

                                    <c:forEach begin="${start_page}" end="${end_page}" var="i">
                                        <c:choose>
                                            <c:when test="${i == page}">
                                                <span class="active btn-pagination">${i}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <c:url var="pageUrl" value="${baseUrl}">
                                                    <c:param name="deductionPage" value="${i}" />
                                                </c:url>
                                                <a href="${pageUrl}" class="btn-pagination">${i}</a>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>

                                    <c:if test="${end_page < totalPages}">
                                        <c:if test="${end_page < totalPages - 1}">
                                            <span class="ellipsis">...</span>
                                        </c:if>
                                        <c:url var="lastPageUrl" value="${baseUrl}">
                                            <c:param name="deductionPage" value="${totalPages}" />
                                        </c:url>
                                        <a href="${lastPageUrl}" class="btn-pagination">${totalPages}</a>
                                    </c:if>

                                    <%-- Next button --%>
                                    <c:choose>
                                        <c:when test="${page < totalPages}">
                                            <c:url var="nextUrl" value="${baseUrl}">
                                                <c:param name="deductionPage" value="${page + 1}" />
                                            </c:url>
                                            <a href="${nextUrl}" class="btn-pagination">Sau →</a>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="disabled">Sau →</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </c:if>
                    </div>

                    <!-- Attendance Tab -->
                    <div id="attendance" class="tab-content <%= "attendance".equals(currentTab) ? "active" : "" %>">
                        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:20px;">
                            <h3 style="margin:0;">Thống kê chấm công</h3>
                            <div class="muted">Xem dữ liệu chấm công dùng để tính lương</div>
                        </div>

                        <!-- Filter -->
                        <div class="form-row" style="margin-bottom:20px;">
                            <div class="form-group">
                                <label>Nhân viên</label>
                                <select id="attendanceEmployeeFilter" onchange="filterAttendance()">
                                    <option value="">Chọn nhân viên</option>
                                    <%
                                        String attendanceEmployeeFilter = request.getParameter("employeeFilter");
                                        if (employees != null) {
                                            for (Employee emp : employees) {
                                                String selected = (attendanceEmployeeFilter != null && attendanceEmployeeFilter.equals(String.valueOf(emp.getEmployeeId()))) ? "selected" : "";
                                    %>
                                    <option value="<%= emp.getEmployeeId() %>" <%= selected %>><%= emp.getFullName() %> (ID: <%= emp.getEmployeeId() %>)</option>
                                    <%
                                            }
                                        }
                                    %>
                                </select>
                            </div>
                            <div class="form-group">
                                <label>Tháng</label>
                                <input type="month" id="attendanceMonthFilter" onchange="filterAttendance()" value="<%= request.getAttribute("attendanceMonth") != null ? request.getAttribute("attendanceMonth") : "" %>"/>
                            </div>
                        </div>

                        <!-- Attendance Statistics -->
                        <div id="attendanceStats">
                            <div class="summary-grid" style="grid-template-columns: repeat(3, 1fr); margin-bottom:20px;">
                                <div class="summary-card">
                                    <h4>Ngày công thực tế</h4>
                                    <div class="value" id="statWorkDays">0 ngày</div>
                                    <div class="muted" style="margin-top:4px;" id="statPaidLeaveInfo">0 ngày nghỉ có lương</div>
                                </div>
                                <div class="summary-card" style="border-left-color:var(--error);">
                                    <h4>Nghỉ không lương</h4>
                                    <div class="value" id="statUnpaidLeave" style="color:var(--error);">0 ngày</div>
                                    <div class="muted" style="margin-top:4px;" id="statUnpaidLeaveAmount">0 VNĐ</div>
                                </div>
                                <div class="summary-card" style="border-left-color:var(--warning);">
                                    <h4>Đi muộn</h4>
                                    <div class="value" id="statLateCount" style="color:var(--warning);">0 lần</div>
                                    <div class="muted" style="margin-top:4px;" id="statLatePenalty">0 VNĐ</div>
                                </div>
                            </div>
                            <div class="summary-grid" style="grid-template-columns: repeat(2, 1fr); margin-bottom:20px;">
                                <div class="summary-card" style="border-left-color:var(--warning);">
                                    <h4>Về sớm</h4>
                                    <div class="value" id="statEarlyLeave">0 lần</div>
                                </div>
                                <div class="summary-card" style="border-left-color:var(--success);">
                                    <h4>Giờ tăng ca</h4>
                                    <div class="value" id="statOvertimeHours" style="color:var(--success);">0.0 giờ</div>
                                    <div class="muted" style="margin-top:4px;" id="statOvertimeAmount">0 VNĐ</div>
                                </div>
                            </div>
                            <div style="background:#f0f9ff; border:1px solid #bae6fd; border-radius:8px; padding:16px; margin-top:20px;">
                                <h4 style="margin:0 0 12px 0; color:#0369a1;">💡 Thao tác nhanh</h4>
                                <div style="display:flex; gap:8px; flex-wrap:wrap;">
                                    <button class="btn btn-small" onclick="applyAttendanceDeductions()">Tạo khấu trừ từ chấm công</button>
                                    <button class="btn btn-small secondary" onclick="applyAttendanceAllowances()">Tạo phụ cấp từ chấm công</button>
                                </div>
                                <div class="muted" style="margin-top:8px; font-size:12px;">
                                    Hệ thống sẽ tự tạo khoản khấu trừ cho nghỉ không lương/đi muộn và phụ cấp cho giờ tăng ca.
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Payroll Tab -->
                    <div id="payroll" class="tab-content <%= "payroll".equals(currentTab) ? "active" : "" %>">
                        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:20px;">
                            <h3 style="margin:0;">Tính lương</h3>
                            <div style="display:flex; gap:8px;">
                                <button class="btn btn-small" onclick="generatePayrollForAll()" style="background:var(--warning);" title="Tạo bảng lương cho tất cả nhân viên đang hoạt động bằng thủ tục lưu trữ">
                                    ⚡ Tạo tất cả
                                </button>
                                <button class="btn" onclick="openPayrollModal()">+ Tạo bảng lương</button>
                            </div>
                        </div>
                        
                        <div style="background:#f0f9ff; border:1px solid #bae6fd; border-radius:8px; padding:16px; margin-bottom:20px;">
                            <h4 style="margin:0 0 8px 0; color:#0369a1;">💡 Cách tính lương</h4>
                            <div style="font-size:13px; color:var(--muted);">
                                <strong>Tính tự động:</strong> Hệ thống tính lương bằng thủ tục <code>sp_GeneratePayrollImproved</code>:
                                <ul style="margin:8px 0 0 20px; padding:0;">
                                    <li><strong>Ngày công thực tế:</strong> Tính từ dữ liệu chấm công (WorkingHours / 8)</li>
                                    <li><strong>Ngày nghỉ có lương:</strong> Tính từ đơn nghỉ đã duyệt (phép năm, ốm, thai sản)</li>
                                    <li><strong>Lương cơ bản thực nhận:</strong> (Ngày công thực tế + ngày nghỉ có lương) × (lương cơ bản / 26)</li>
                                    <li><strong>Lương tăng ca:</strong> Giờ tăng ca × (lương cơ bản / 208) × 1.5</li>
                                    <li><strong>Bảo hiểm:</strong> BHXH (8%), BHYT (1.5%), BHTN (1%) từ lương cơ bản</li>
                                    <li><strong>Thuế TNCN:</strong> Tính từ thu nhập chịu thuế sau giảm trừ</li>
                                    <li><strong>Lương thực nhận:</strong> Lương cơ bản thực nhận + lương tăng ca + phụ cấp - tổng khấu trừ</li>
                                </ul>
                            </div>
                        </div>

                        <!-- Filter -->
                        <div class="form-row" style="margin-bottom:20px;">
                            <div class="form-group">
                                <label>Nhân viên</label>
                                <select id="payrollEmployeeFilter" onchange="filterPayrolls()">
                                    <option value="">Tất cả nhân viên</option>
                                    <%
                                        if (employees != null) {
                                            for (Employee emp : employees) {
                                                String selected = (employeeFilter != null && employeeFilter.equals(String.valueOf(emp.getEmployeeId()))) ? "selected" : "";
                                    %>
                                    <option value="<%= emp.getEmployeeId() %>" <%= selected %>><%= emp.getFullName() %> (ID: <%= emp.getEmployeeId() %>)</option>
                                    <%
                                            }
                                        }
                                    %>
                                </select>
                            </div>
                            <div class="form-group">
                                <label>Trạng thái</label>
                                <select id="payrollStatusFilter" onchange="filterPayrolls()">
                                    <option value="">Tất cả trạng thái</option>
                                    <%
                                        String statusFilter = (String) request.getAttribute("statusFilter");
                                        String[] statuses = {"Draft", "Pending", "Approved", "Rejected", "Paid"};
                                        for (String status : statuses) {
                                            String selected = (statusFilter != null && statusFilter.equals(status)) ? "selected" : "";
                                    %>
                                    <option value="<%= status %>" <%= selected %>><%= payrollStatusLabel(status) %></option>
                                    <%
                                        }
                                    %>
                                </select>
                            </div>
                        </div>

                        <!-- Batch Actions Bar -->
                        <div id="batchActionsBar">
                            <span id="selectedCount" style="font-weight:600; color:#0369a1;">0 đã chọn</span>
                            <button type="button" class="btn btn-small secondary" onclick="submitSelectedPayrolls()" id="batchSubmitBtn" style="display:none;">
                                <i class="fas fa-paper-plane"></i> Gửi duyệt mục đã chọn
                            </button>
                            <button type="button" class="btn btn-small btn-danger" onclick="deleteSelectedPayrolls()" id="batchDeleteBtn" style="display:none;">
                                <i class="fas fa-trash"></i> Xóa mục đã chọn
                            </button>
                            <button type="button" class="btn btn-small" onclick="clearSelection()" style="background:#6b7280; color:white;">
                                <i class="fas fa-times"></i> Bỏ chọn
                            </button>
                        </div>

                        <!-- Payroll List -->
                        <table id="payrollTable">
                            <thead>
                                <tr>
                                    <%
                                        String sortBy = (String) request.getAttribute("sortBy");
                                        String sortOrder = (String) request.getAttribute("sortOrder");
                                        if (sortBy == null) sortBy = "PayPeriod";
                                        if (sortOrder == null) sortOrder = "DESC";
                                    %>
                                    <th style="width:40px; text-align:center;">
                                        <input type="checkbox" id="selectAllCheckbox" onchange="toggleSelectAll(this)" title="Chọn tất cả">
                                    </th>
                                    <th style="cursor:pointer;" onclick="sortPayrollTable('FullName')">
                                        Nhân viên<%
                                            if ("FullName".equals(sortBy)) {
                                                out.print(sortOrder.equals("ASC") ? " ▲" : " ▼");
                                            }
                                        %>
                                    </th>
                                    <th style="cursor:pointer;" onclick="sortPayrollTable('PayPeriod')">
                                        Kỳ lương<%
                                            if ("PayPeriod".equals(sortBy)) {
                                                out.print(sortOrder.equals("ASC") ? " ▲" : " ▼");
                                            }
                                        %>
                                    </th>
                                    <th style="cursor:pointer;" onclick="sortPayrollTable('BaseSalary')">
                                        Lương cơ bản thực nhận<%
                                            if ("BaseSalary".equals(sortBy)) {
                                                out.print(sortOrder.equals("ASC") ? " ▲" : " ▼");
                                            }
                                        %>
                                    </th>
                                    <th style="cursor:pointer;" onclick="sortPayrollTable('Bonus')">
                                        Lương tăng ca<%
                                            if ("Bonus".equals(sortBy)) {
                                                out.print(sortOrder.equals("ASC") ? " ▲" : " ▼");
                                            }
                                        %>
                                    </th>
                                    <th style="cursor:pointer;" onclick="sortPayrollTable('Allowance')">
                                        Phụ cấp<%
                                            if ("Allowance".equals(sortBy)) {
                                                out.print(sortOrder.equals("ASC") ? " ▲" : " ▼");
                                            }
                                        %>
                                    </th>
                                    <th style="cursor:pointer;" onclick="sortPayrollTable('Deduction')">
                                        Khấu trừ<%
                                            if ("Deduction".equals(sortBy)) {
                                                out.print(sortOrder.equals("ASC") ? " ▲" : " ▼");
                                            }
                                        %>
                                    </th>
                                    <th style="cursor:pointer;" onclick="sortPayrollTable('NetSalary')">
                                        Lương thực nhận<%
                                            if ("NetSalary".equals(sortBy)) {
                                                out.print(sortOrder.equals("ASC") ? " ▲" : " ▼");
                                            }
                                        %>
                                    </th>
                                    <th style="cursor:pointer;" onclick="sortPayrollTable('Status')">
                                        Trạng thái<%
                                            if ("Status".equals(sortBy)) {
                                                out.print(sortOrder.equals("ASC") ? " ▲" : " ▼");
                                            }
                                        %>
                                    </th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody id="payrollTableBody">
                                <%
                                    List<Map<String, Object>> payrolls = (List<Map<String, Object>>) request.getAttribute("payrolls");
                                    if (payrolls == null || payrolls.isEmpty()) {
                                %>
                                <tr>
                                    <td colspan="10" class="empty">Chưa có bảng lương. Nhấn "Tạo bảng lương" để tạo mới.</td>
                                </tr>
                                <%
                                    } else {
                                        for (Map<String, Object> payroll : payrolls) {
                                            String status = (String) payroll.get("status");
                                %>
                                <tr data-payroll-id="<%= payroll.get("payrollId") %>" data-status="<%= status %>">
                                    <td style="text-align:center;">
                                        <%
                                            // Only show checkbox for Draft and Rejected status
                                            if ("Draft".equals(status) || "Rejected".equals(status)) {
                                        %>
                                        <input type="checkbox" class="payroll-checkbox" 
                                               data-payroll-id="<%= payroll.get("payrollId") %>" 
                                               data-status="<%= status %>"
                                               onchange="updateBatchActions()">
                                        <%
                                            }
                                        %>
                                    </td>
                                    <td><%= payroll.get("employeeName") %></td>
                                    <td><%= payroll.get("payPeriod") %></td>
                                    <td><%= String.format("%,d VNĐ", ((java.math.BigDecimal) payroll.get("baseSalary")).intValue()) %></td>
                                    <td><%= String.format("%,d VNĐ", ((java.math.BigDecimal) payroll.get("bonus")).intValue()) %></td>
                                    <td><%= String.format("%,d VNĐ", ((java.math.BigDecimal) payroll.get("allowance")).intValue()) %></td>
                                    <td><%= String.format("%,d VNĐ", ((java.math.BigDecimal) payroll.get("deduction")).intValue()) %></td>
                                    <td><strong><%= String.format("%,d VNĐ", ((java.math.BigDecimal) payroll.get("netSalary")).intValue()) %></strong></td>
                                    <td>
                                        <span class="status-badge status-<%= status %>"><%= payrollStatusLabel(status) %></span>
                                    </td>
                                    <td>
                                        <div class="action-btns">
                                            <%
                                                if ("Draft".equals(status)) {
                                            %>
                                            <button class="btn btn-small" onclick="editPayroll(<%= payroll.get("payrollId") %>)">Sửa</button>
                                            <button class="btn btn-small secondary" onclick="submitPayroll(<%= payroll.get("payrollId") %>)">Gửi duyệt</button>
                                            <button class="btn btn-small btn-danger" onclick="deletePayroll(<%= payroll.get("payrollId") %>)">Xóa</button>
                                            <%
                                                } else if ("Rejected".equals(status)) {
                                            %>
                                            <button class="btn btn-small" onclick="editPayroll(<%= payroll.get("payrollId") %>)" title="Sửa bảng lương bị từ chối">
                                                <i class="fas fa-edit"></i> Sửa
                                            </button>
                                            <button class="btn btn-small btn-success" onclick="resubmitPayroll(<%= payroll.get("payrollId") %>)" title="Gửi lại sau khi chỉnh sửa">
                                                <i class="fas fa-paper-plane"></i> Gửi lại
                                            </button>
                                            <button class="btn btn-small" onclick="viewPayrollDetails(<%= payroll.get("payrollId") %>)" title="Xem chi tiết và ghi chú từ chối">
                                                <i class="fas fa-eye"></i> Xem
                                            </button>
                                            <%
                                                } else {
                                            %>
                                            <button class="btn btn-small" onclick="viewPayrollDetails(<%= payroll.get("payrollId") %>)">Xem chi tiết</button>
                                            <%
                                                }
                                            %>
                                        </div>
                                    </td>
                                </tr>
                                <%
                                        }
                                    }
                                %>
                            </tbody>
                        </table>
                        
                        <%-- Pagination Bar (only show for payroll tab) --%>
                        <c:if test="${currentTab == 'payroll'}">
                            <div class="pagination-bar">
                                <div class="pagination-info">
                                    <c:set var="total" value="${total != null ? total : 0}" />
                                    <c:set var="page" value="${page != null ? page : 1}" />
                                    <c:set var="pageSize" value="${pageSize != null ? pageSize : 10}" />
                                    <c:set var="totalPages" value="${totalPages != null ? totalPages : 1}" />
                                    
                                    <%-- Calculate display range --%>
                                    <c:set var="start" value="${total > 0 ? (page - 1) * pageSize + 1 : 0}" />
                                    <c:set var="end" value="${page * pageSize}" />
                                    <c:if test="${end > total}">
                                        <c:set var="end" value="${total}" />
                                    </c:if>
                                    <span>Hiển thị ${start} - ${end} trong tổng số ${total}</span>
                                    
                                    <div class="page-size-selector" style="margin-left: 20px; display: inline-block;">
                                        <label for="pageSizeSelect" style="margin-right: 8px;">Số dòng mỗi trang:</label>
                                        <select id="pageSizeSelect" onchange="changePayrollPageSize(this.value)" style="padding: 4px 8px; border: 1px solid #ddd; border-radius: 4px;">
                                            <option value="5" <c:if test="${pageSize == 5}">selected</c:if>>5</option>
                                            <option value="10" <c:if test="${pageSize == 10}">selected</c:if>>10</option>
                                            <option value="20" <c:if test="${pageSize == 20}">selected</c:if>>20</option>
                                            <option value="50" <c:if test="${pageSize == 50}">selected</c:if>>50</option>
                                        </select>
                                    </div>
                                </div>

                                <div class="pagination-controls">
                                    <%-- Base URL --%>
                                    <c:url var="baseUrl" value="/hrstaff/payroll">
                                        <c:param name="tab" value="payroll" />
                                        <c:param name="pageSize" value="${pageSize}" />
                                        <c:param name="employeeFilter" value="${employeeFilter != null ? employeeFilter : ''}" />
                                        <c:param name="statusFilter" value="${statusFilter != null ? statusFilter : ''}" />
                                        <c:param name="sortBy" value="${sortBy != null ? sortBy : 'PayPeriod'}" />
                                        <c:param name="sortOrder" value="${sortOrder != null ? sortOrder : 'DESC'}" />
                                    </c:url>

                                    <%-- Previous button --%>
                                    <c:choose>
                                        <c:when test="${page > 1}">
                                            <c:url var="prevUrl" value="${baseUrl}">
                                                <c:param name="page" value="${page - 1}" />
                                            </c:url>
                                            <a href="${prevUrl}" class="btn-pagination">← Trước</a>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="disabled">← Trước</span>
                                        </c:otherwise>
                                    </c:choose>

                                    <%-- Page numbers --%>
                                    <c:set var="range" value="2" />
                                    <c:set var="start_page" value="${page - range > 1 ? page - range : 1}" />
                                    <c:set var="end_page" value="${page + range < totalPages ? page + range : totalPages}" />

                                    <c:if test="${start_page > 1}">
                                        <c:url var="firstPageUrl" value="${baseUrl}">
                                            <c:param name="page" value="1" />
                                        </c:url>
                                        <a href="${firstPageUrl}" class="btn-pagination">1</a>
                                        <c:if test="${start_page > 2}">
                                            <span class="ellipsis">...</span>
                                        </c:if>
                                    </c:if>

                                    <c:forEach begin="${start_page}" end="${end_page}" var="i">
                                        <c:choose>
                                            <c:when test="${i == page}">
                                                <span class="active btn-pagination">${i}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <c:url var="pageUrl" value="${baseUrl}">
                                                    <c:param name="page" value="${i}" />
                                                </c:url>
                                                <a href="${pageUrl}" class="btn-pagination">${i}</a>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>

                                    <c:if test="${end_page < totalPages}">
                                        <c:if test="${end_page < totalPages - 1}">
                                            <span class="ellipsis">...</span>
                                        </c:if>
                                        <c:url var="lastPageUrl" value="${baseUrl}">
                                            <c:param name="page" value="${totalPages}" />
                                        </c:url>
                                        <a href="${lastPageUrl}" class="btn-pagination">${totalPages}</a>
                                    </c:if>

                                    <%-- Next button --%>
                                    <c:choose>
                                        <c:when test="${page < totalPages}">
                                            <c:url var="nextUrl" value="${baseUrl}">
                                                <c:param name="page" value="${page + 1}" />
                                            </c:url>
                                            <a href="${nextUrl}" class="btn-pagination">Sau →</a>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="disabled">Sau →</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </c:if>
                    </div>
                </div>
            </main>
        </div>
                </section>
            </main>
        </div>

        <!-- Modal phụ cấp -->
        <div id="allowanceModal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h3 id="allowanceModalTitle">Thêm phụ cấp</h3>
                    <button class="close-btn" onclick="closeAllowanceModal()">&times;</button>
                </div>
                <form id="allowanceForm" method="POST" action="<%=request.getContextPath()%>/hrstaff/payroll/allowance">
                    <input type="hidden" id="allowanceId" name="allowanceId"/>
                    <div class="form-group">
                        <label>Nhân viên <span class="required">*</span></label>
                        <select id="allowanceEmployee" name="employeeId" required>
                            <option value="">Chọn nhân viên</option>
                            <%
                                if (employees != null) {
                                    for (Employee emp : employees) {
                            %>
                            <option value="<%= emp.getEmployeeId() %>"><%= emp.getFullName() %> (ID: <%= emp.getEmployeeId() %>)</option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Loại phụ cấp <span class="required">*</span></label>
                        <select id="allowanceType" name="allowanceTypeId" required>
                            <option value="">Chọn loại phụ cấp</option>
                            <%
                                List<Map<String, Object>> allowanceTypes = (List<Map<String, Object>>) request.getAttribute("allowanceTypes");
                                if (allowanceTypes != null) {
                                    for (Map<String, Object> type : allowanceTypes) {
                            %>
                            <option value="<%= type.get("allowanceTypeId") %>"><%= type.get("allowanceName") %></option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label>Số tiền (VNĐ) <span class="required">*</span></label>
                            <input type="number" id="allowanceAmount" name="amount" min="0" step="1000" required/>
                        </div>
                        <div class="form-group">
                            <label>Tháng <span class="required">*</span></label>
                            <input type="month" id="allowanceMonth" name="month" required/>
                        </div>
                    </div>
                    <div style="display:flex; gap:8px; justify-content:flex-end; margin-top:20px;">
                        <button type="button" class="btn btn-small" onclick="closeAllowanceModal()">Hủy</button>
                        <button type="submit" class="btn btn-small">Lưu</button>
                    </div>
                </form>
            </div>
        </div>

        <!-- Modal khấu trừ -->
        <div id="deductionModal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h3 id="deductionModalTitle">Thêm khấu trừ</h3>
                    <button class="close-btn" onclick="closeDeductionModal()">&times;</button>
                </div>
                <form id="deductionForm" method="POST" action="<%=request.getContextPath()%>/hrstaff/payroll/deduction">
                    <input type="hidden" id="deductionId" name="deductionId"/>
                    <div class="form-group">
                        <label>Nhân viên <span class="required">*</span></label>
                        <select id="deductionEmployee" name="employeeId" required>
                            <option value="">Chọn nhân viên</option>
                            <%
                                if (employees != null) {
                                    for (Employee emp : employees) {
                            %>
                            <option value="<%= emp.getEmployeeId() %>"><%= emp.getFullName() %> (ID: <%= emp.getEmployeeId() %>)</option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Loại khấu trừ <span class="required">*</span></label>
                        <select id="deductionType" name="deductionTypeId" required>
                            <option value="">Chọn loại khấu trừ</option>
                            <%
                                List<Map<String, Object>> deductionTypes = (List<Map<String, Object>>) request.getAttribute("deductionTypes");
                                if (deductionTypes != null) {
                                    for (Map<String, Object> type : deductionTypes) {
                            %>
                            <option value="<%= type.get("deductionTypeId") %>"><%= type.get("deductionName") %></option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label>Số tiền (VNĐ) <span class="required">*</span></label>
                            <input type="number" id="deductionAmount" name="amount" min="0" step="1000" required/>
                        </div>
                        <div class="form-group">
                            <label>Tháng <span class="required">*</span></label>
                            <input type="month" id="deductionMonth" name="month" required/>
                        </div>
                    </div>
                    <div style="display:flex; gap:8px; justify-content:flex-end; margin-top:20px;">
                        <button type="button" class="btn btn-small" onclick="closeDeductionModal()">Hủy</button>
                        <button type="submit" class="btn btn-small">Lưu</button>
                    </div>
                </form>
            </div>
        </div>

        <!-- Modal chi tiết bảng lương -->
        <div id="payrollDetailsModal" class="modal">
            <div class="modal-content" style="max-width:900px;">
                <div class="modal-header">
                    <h3>Chi tiết bảng lương</h3>
                    <button class="close-btn" onclick="closePayrollDetailsModal()">&times;</button>
                </div>
                <div id="payrollDetailsContent">
                    <!-- Nội dung được tải động -->
                </div>
            </div>
        </div>

        <!-- Modal bảng lương -->
        <div id="payrollModal" class="modal">
            <div class="modal-content" style="max-width:800px;">
                <div class="modal-header">
                    <h3>Tạo/Sửa bảng lương</h3>
                    <button class="close-btn" onclick="closePayrollModal()">&times;</button>
                </div>
                <form id="payrollForm" method="POST" action="<%=request.getContextPath()%>/hrstaff/payroll">
                    <input type="hidden" id="payrollId" name="payrollId"/>
                    <div class="form-row">
                        <div class="form-group">
                            <label>Nhân viên <span class="required">*</span></label>
                            <select id="payrollEmployee" name="employeeId" required onchange="loadEmployeePayrollData()">
                                <option value="">Chọn nhân viên</option>
                                <%
                                    if (employees != null) {
                                        for (Employee emp : employees) {
                                %>
                                <option value="<%= emp.getEmployeeId() %>"><%= emp.getFullName() %> (ID: <%= emp.getEmployeeId() %>)</option>
                                <%
                                        }
                                    }
                                %>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Kỳ lương (tháng) <span class="required">*</span></label>
                            <input type="month" id="payrollPeriod" name="payPeriod" required onchange="loadEmployeePayrollData()"/>
                        </div>
                    </div>

                    <!-- Attendance Info Section -->
                    <div id="attendanceInfoSection" style="display:none; background:#f0f9ff; border:1px solid #bae6fd; border-radius:8px; padding:16px; margin-bottom:20px;">
                        <div style="margin-bottom:12px;">
                            <h4 style="margin:0; color:#0369a1;">📅 Tóm tắt chấm công</h4>
                            <div class="muted" style="font-size:12px; margin-top:4px;">Dữ liệu chấm công được tự động tính và áp dụng vào bảng lương</div>
                        </div>
                        <div class="summary-grid" style="grid-template-columns: repeat(4, 1fr); gap:12px; margin:0;">
                            <div style="background:white; padding:12px; border-radius:6px;">
                                <div style="font-size:11px; color:var(--muted); margin-bottom:4px;">Ngày công thực tế</div>
                                <div style="font-weight:600; font-size:16px;" id="attWorkDays">0 ngày</div>
                                <div style="font-size:10px; color:var(--muted); margin-top:2px;" id="attPaidLeaveInfo">0 ngày nghỉ có lương</div>
                            </div>
                            <div style="background:white; padding:12px; border-radius:6px; border-left:3px solid var(--error);">
                                <div style="font-size:11px; color:var(--muted); margin-bottom:4px;">Nghỉ không lương</div>
                                <div style="font-weight:600; font-size:16px; color:var(--error);" id="attUnpaidLeave">0 ngày</div>
                                <div style="font-size:11px; color:var(--muted);" id="attUnpaidLeaveAmt">0 VNĐ</div>
                            </div>
                            <div style="background:white; padding:12px; border-radius:6px; border-left:3px solid var(--warning);">
                                <div style="font-size:11px; color:var(--muted); margin-bottom:4px;">Đi muộn</div>
                                <div style="font-weight:600; font-size:16px; color:var(--warning);" id="attLateCount">0 lần</div>
                                <div style="font-size:11px; color:var(--muted);" id="attLatePenalty">0 VNĐ</div>
                            </div>
                            <div style="background:white; padding:12px; border-radius:6px; border-left:3px solid var(--success);">
                                <div style="font-size:11px; color:var(--muted); margin-bottom:4px;">Tăng ca</div>
                                <div style="font-weight:600; font-size:16px; color:var(--success);" id="attOvertimeHours">0.0h</div>
                                <div style="font-size:11px; color:var(--muted);" id="attOvertimeAmt">0 VNĐ</div>
                            </div>
                        </div>
                    </div>

                    <!-- Insurance & Tax Info Section -->
                    <div id="insuranceTaxSection" style="display:none; background:#fef3c7; border:1px solid #fcd34d; border-radius:8px; padding:16px; margin-bottom:20px;">
                        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
                            <h4 style="margin:0; color:#92400e;">💰 Tính bảo hiểm & thuế</h4>
                        </div>
                        <div id="insuranceWarning" style="display:none; background:#fee2e2; border:1px solid #fca5a5; border-radius:6px; padding:12px; margin-bottom:12px; color:#991b1b; font-size:13px;">
                            <strong>⚠️ Lưu ý:</strong> Nhân viên này chưa có <strong>lương cơ bản</strong> (chưa có hợp đồng hoặc hợp đồng chưa có lương cơ bản). 
                            Vui lòng tạo/cập nhật hợp đồng trước khi tính bảo hiểm và thuế.
                            <br/><br/>
                            <strong>Hướng dẫn:</strong>
                            <ul style="margin:8px 0 0 20px; padding:0;">
                                <li>Kiểm tra xem nhân viên đã có hợp đồng chưa</li>
                                <li>Đảm bảo hợp đồng có lương cơ bản > 0</li>
                                <li>Đảm bảo ngày bắt đầu hợp đồng <= ngày cuối tháng tính lương</li>
                                <li>Đảm bảo ngày kết thúc hợp đồng >= ngày đầu tháng tính lương hoặc để trống</li>
                            </ul>
                        </div>
                        <div id="baseSalaryWarning" style="display:none; background:#fef3c7; border:1px solid #fcd34d; border-radius:6px; padding:12px; margin-bottom:12px; color:#92400e; font-size:13px;">
                            <strong>ℹ️ Thông tin:</strong> Lương cơ bản từ hợp đồng: <strong id="displayBaseSalary">0 VNĐ</strong>
                        </div>
                        <div class="summary-grid" style="grid-template-columns: repeat(4, 1fr); gap:12px; margin:0;">
                            <div style="background:white; padding:12px; border-radius:6px; border-left:3px solid #3b82f6;">
                                <div style="font-size:11px; color:var(--muted); margin-bottom:4px;">BHXH (8%)</div>
                                <div style="font-weight:600; font-size:16px; color:#3b82f6;" id="insBHXH">0 VNĐ</div>
                            </div>
                            <div style="background:white; padding:12px; border-radius:6px; border-left:3px solid #10b981;">
                                <div style="font-size:11px; color:var(--muted); margin-bottom:4px;">BHYT (1.5%)</div>
                                <div style="font-weight:600; font-size:16px; color:#10b981;" id="insBHYT">0 VNĐ</div>
                            </div>
                            <div style="background:white; padding:12px; border-radius:6px; border-left:3px solid #8b5cf6;">
                                <div style="font-size:11px; color:var(--muted); margin-bottom:4px;">BHTN (1%)</div>
                                <div style="font-weight:600; font-size:16px; color:#8b5cf6;" id="insBHTN">0 VNĐ</div>
                            </div>
                            <div style="background:white; padding:12px; border-radius:6px; border-left:3px solid var(--error);">
                                <div style="font-size:11px; color:var(--muted); margin-bottom:4px;">Thuế TNCN</div>
                                <div style="font-weight:600; font-size:16px; color:var(--error);" id="insTax">0 VNĐ</div>
                            </div>
                        </div>
                        <div style="margin-top:12px; padding-top:12px; border-top:1px solid #fcd34d; display:flex; justify-content:space-between; font-size:12px;">
                            <div>
                                <span style="color:var(--muted);">Thu nhập chịu thuế:</span>
                                <strong id="insTaxableIncome">0 VNĐ</strong>
                            </div>
                            <div>
                                <span style="color:var(--muted);">Người phụ thuộc:</span>
                                <strong id="insDependents">0</strong>
                            </div>
                            <div>
                                <span style="color:var(--muted);">Tổng bảo hiểm:</span>
                                <strong id="insTotal">0 VNĐ</strong>
                            </div>
                        </div>
                    </div>

                    <!-- Summary Cards -->
                    <div class="summary-grid" id="payrollSummary" style="display:none; grid-template-columns: repeat(5, 1fr);">
                        <div class="summary-card">
                            <h4>Lương cơ bản</h4>
                            <div class="value" id="summaryBaseSalary" style="font-size:18px;">0 VNĐ</div>
                            <div class="muted" style="margin-top:4px; font-size:11px;">Theo hợp đồng</div>
                        </div>
                        <div class="summary-card">
                            <h4>Lương cơ bản thực nhận</h4>
                            <div class="value" id="summaryActualBaseSalary" style="font-size:18px;">0 VNĐ</div>
                            <div class="muted" style="margin-top:4px; font-size:11px;" id="summaryActualBaseSalaryDesc">Theo chấm công</div>
                        </div>
                        <div class="summary-card">
                            <h4>Lương tăng ca</h4>
                            <div class="value" id="summaryOTSalary" style="font-size:18px; color:var(--success);">0 VNĐ</div>
                            <div class="muted" style="margin-top:4px; font-size:11px;" id="summaryOTHours">0 giờ</div>
                        </div>
                        <div class="summary-card">
                            <h4>Tổng phụ cấp</h4>
                            <div class="value" id="summaryAllowance" style="font-size:18px;">0 VNĐ</div>
                        </div>
                        <div class="summary-card">
                            <h4>Tổng khấu trừ</h4>
                            <div class="value" id="summaryDeduction" style="font-size:18px; color:var(--error);">0 VNĐ</div>
                            <div class="muted" style="margin-top:4px; font-size:11px;" id="deductionBreakdown">Bảo hiểm & thuế</div>
                        </div>
                    </div>
                    
                    <div class="summary-card" id="summaryNetSalaryCard" style="display:none; margin-top:16px; border-left:4px solid var(--success);">
                        <h4>Lương thực nhận</h4>
                        <div class="value" id="summaryNetSalary" style="color:var(--success); font-size:28px;">0 VNĐ</div>
                        <div class="muted" style="margin-top:4px; font-size:12px;">Lương cơ bản thực nhận + lương tăng ca + phụ cấp - tổng khấu trừ</div>
                    </div>

                    <!-- Manual Edit Section -->
                    <div id="manualEditSection" style="display:none; background:#fff3cd; border:1px solid #ffc107; border-radius:8px; padding:16px; margin-top:20px;">
                        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
                            <h4 style="margin:0; color:#856404;">✏️ Điều chỉnh thủ công</h4>
                            <button type="button" class="btn btn-small" onclick="toggleManualEdit()" id="toggleEditBtn" style="font-size:12px; padding:6px 12px;">
                                <i class="fas fa-edit"></i> Sửa giá trị
                            </button>
                        </div>
                        <div id="manualEditWarning" style="display:none; background:#fff3cd; border:1px solid #ffc107; border-radius:6px; padding:12px; margin-bottom:12px; color:#856404; font-size:13px;">
                            <strong>⚠️ Cảnh báo:</strong> Bạn đang chỉnh sửa giá trị đã tính tự động. Hãy đảm bảo công thức lương thực nhận chính xác:<br/>
                            <strong>Lương thực nhận = Lương cơ bản thực nhận + lương tăng ca + phụ cấp - tổng khấu trừ</strong>
                        </div>
                        
                        <div class="form-row" style="margin-bottom:12px;">
                            <div class="form-group" style="flex:1;">
                                <label>Lương cơ bản thực nhận</label>
                                <input type="number" id="payrollActualBaseSalary" name="actualBaseSalary" step="0.01" min="0" 
                                    readonly style="background:#f5f5f5; cursor:not-allowed;" 
                                    onchange="validateNetSalary()" onblur="validateNetSalary()"/>
                                <small id="actualBaseSalaryWarning" style="display:none; color:var(--error); font-size:11px;"></small>
                            </div>
                            <div class="form-group" style="flex:1;">
                                <label>Lương tăng ca</label>
                                <input type="number" id="payrollOTSalary" name="otSalary" step="0.01" min="0" 
                                    readonly style="background:#f5f5f5; cursor:not-allowed;" 
                                    onchange="validateNetSalary()" onblur="validateNetSalary()"/>
                                <small id="otSalaryWarning" style="display:none; color:var(--error); font-size:11px;"></small>
                            </div>
                        </div>
                        
                        <div class="form-row" style="margin-bottom:12px;">
                            <div class="form-group" style="flex:1;">
                                <label>Tổng phụ cấp</label>
                                <input type="number" id="payrollAllowance" name="allowance" step="0.01" min="0" 
                                    readonly style="background:#f5f5f5; cursor:not-allowed;" 
                                    onchange="validateNetSalary()" onblur="validateNetSalary()"/>
                                <small id="allowanceWarning" style="display:none; color:var(--error); font-size:11px;"></small>
                            </div>
                            <div class="form-group" style="flex:1;">
                                <label>Tổng khấu trừ</label>
                                <input type="number" id="payrollDeduction" name="deduction" step="0.01" min="0" 
                                    readonly style="background:#f5f5f5; cursor:not-allowed;" 
                                    onchange="validateNetSalary()" onblur="validateNetSalary()"/>
                                <small id="deductionWarning" style="display:none; color:var(--error); font-size:11px;"></small>
                            </div>
                        </div>
                        
                        <div class="form-row" style="margin-bottom:12px;">
                            <div class="form-group" style="flex:1;">
                                <label>Lương thực nhận <span style="color:var(--error);">*</span> <span style="color:var(--muted); font-size:11px; font-weight:normal;">(Tự động tính)</span></label>
                                <input type="number" id="payrollNetSalary" name="netSalary" step="0.01" min="0" required
                                    readonly style="background:#f5f5f5; cursor:not-allowed; font-weight:bold; color:#059669;" 
                                    onchange="updateNetSalaryAuto()" onblur="updateNetSalaryAuto()"/>
                                <small id="netSalaryWarning" style="display:none; color:var(--error); font-size:11px; font-weight:bold;"></small>
                                <small id="netSalaryFormula" style="display:block; color:var(--muted); font-size:11px; margin-top:4px;">
                                    Công thức: Lương cơ bản thực nhận + lương tăng ca + phụ cấp - tổng khấu trừ (tự động tính, không sửa trực tiếp)
                                </small>
                            </div>
                        </div>
                        
                        <div style="display:flex; gap:8px; margin-top:12px;">
                            <button type="button" class="btn btn-small" onclick="recalculatePayroll()" style="background:#0369a1; color:white;">
                                <i class="fas fa-calculator"></i> Tính lại từ dữ liệu gốc
                            </button>
                            <button type="button" class="btn btn-small" onclick="compareWithCalculated()" style="background:#059669; color:white;">
                                <i class="fas fa-balance-scale"></i> So sánh với số tự động
                            </button>
                        </div>
                    </div>
                    
                    <!-- Comparison Section (hidden by default) -->
                    <div id="comparisonSection" style="display:none; background:#e0f2fe; border:1px solid #0ea5e9; border-radius:8px; padding:16px; margin-top:12px;">
                        <h4 style="margin:0 0 12px 0; color:#0c4a6e;">📊 So sánh: thủ công và tự động</h4>
                        <table style="width:100%; border-collapse:collapse; font-size:13px;">
                            <thead>
                                <tr style="background:#bae6fd; border-bottom:2px solid #0ea5e9;">
                                    <th style="padding:8px; text-align:left;">Trường</th>
                                    <th style="padding:8px; text-align:right;">Tự động tính</th>
                                    <th style="padding:8px; text-align:right;">Giá trị thủ công</th>
                                    <th style="padding:8px; text-align:right;">Chênh lệch</th>
                                </tr>
                            </thead>
                            <tbody id="comparisonTableBody">
                                <!-- Will be populated by JavaScript -->
                            </tbody>
                        </table>
                        <div style="margin-top:12px; display:flex; gap:8px;">
                            <button type="button" class="btn btn-small" onclick="useCalculatedValues()" style="background:#059669; color:white;">
                                <i class="fas fa-check"></i> Dùng giá trị tự động
                            </button>
                            <button type="button" class="btn btn-small" onclick="hideComparison()" style="background:#6b7280; color:white;">
                                Đóng so sánh
                            </button>
                        </div>
                    </div>

                    <input type="hidden" id="payrollBaseSalary" name="baseSalary"/>
                    <input type="hidden" id="calculatedActualBaseSalary"/>
                    <input type="hidden" id="calculatedOTSalary"/>
                    <input type="hidden" id="calculatedAllowance"/>
                    <input type="hidden" id="calculatedDeduction"/>
                    <input type="hidden" id="calculatedNetSalary"/>

                    <div style="display:flex; gap:8px; justify-content:flex-end; margin-top:20px;">
                        <button type="button" class="btn btn-small" onclick="closePayrollModal()">Hủy</button>
                        <button type="submit" class="btn btn-small" name="action" value="save" onclick="return validatePayrollForm()">Lưu bản nháp</button>
                        <button type="submit" class="btn btn-small secondary" name="action" value="submit" onclick="return validatePayrollForm()">Gửi phê duyệt</button>
                    </div>
                </form>
            </div>
        </div>

        <script>
            function getPayrollStatusLabel(status) {
                switch (status) {
                    case 'Draft':
                        return 'Bản nháp';
                    case 'Pending':
                        return 'Chờ phê duyệt';
                    case 'Approved':
                        return 'Đã duyệt';
                    case 'Rejected':
                        return 'Bị từ chối';
                    case 'Paid':
                        return 'Đã thanh toán';
                    default:
                        return status || 'Không có';
                }
            }

            function openAllowanceModal(resetForm = true) {
                const modal = document.getElementById('allowanceModal');
                if (modal) {
                    modal.classList.add('active');
                    if (resetForm) {
                        document.getElementById('allowanceModalTitle').textContent = 'Thêm phụ cấp';
                        document.getElementById('allowanceForm').reset();
                        document.getElementById('allowanceId').value = '';
                    }
                }
            }

            function closeAllowanceModal() {
                const modal = document.getElementById('allowanceModal');
                if (modal) {
                    modal.classList.remove('active');
                }
            }

            function openDeductionModal(resetForm = true) {
                const modal = document.getElementById('deductionModal');
                if (modal) {
                    modal.classList.add('active');
                    if (resetForm) {
                        document.getElementById('deductionModalTitle').textContent = 'Thêm khấu trừ';
                        document.getElementById('deductionForm').reset();
                        document.getElementById('deductionId').value = '';
                    }
                }
            }

            function closeDeductionModal() {
                const modal = document.getElementById('deductionModal');
                if (modal) {
                    modal.classList.remove('active');
                }
            }

            function openPayrollModal(resetForm = true) {
                const modal = document.getElementById('payrollModal');
                if (modal) {
                    modal.classList.add('active');
                    
                    if (resetForm) {
                        document.getElementById('payrollForm').reset();
                        document.getElementById('payrollId').value = '';
                        document.getElementById('payrollSummary').style.display = 'none';
                        document.getElementById('attendanceInfoSection').style.display = 'none';
                        document.getElementById('insuranceTaxSection').style.display = 'none';
                        
                        const payrollPeriod = document.getElementById('payrollPeriod');
                        if (payrollPeriod && !payrollPeriod.value) {
                            const now = new Date();
                            const year = now.getFullYear();
                            const month = String(now.getMonth() + 1).padStart(2, '0');
                            payrollPeriod.value = year + '-' + month;
                        }
                    }
                }
            }

            function closePayrollModal() {
                const modal = document.getElementById('payrollModal');
                if (modal) {
                    modal.classList.remove('active');
                }
            }

            function editAllowance(id) {
                if (!id) {
                    console.error('Invalid allowance ID');
                    return;
                }
                fetch('<%=request.getContextPath()%>/api/allowance/' + id)
                        .then(r => {
                            if (!r.ok) {
                                throw new Error('Không tải được dữ liệu phụ cấp: ' + r.status);
                            }
                            return r.json();
                        })
                        .then(data => {
                            if (!data || !data.id) {
                                throw new Error('Dữ liệu phụ cấp không hợp lệ');
                            }
                            // Set form values with the fetched data
                            document.getElementById('allowanceId').value = data.id || '';
                            document.getElementById('allowanceEmployee').value = data.employeeId || '';
                            document.getElementById('allowanceType').value = data.allowanceTypeId || '';
                            document.getElementById('allowanceAmount').value = data.amount || '';
                            document.getElementById('allowanceMonth').value = data.month || '';
                            document.getElementById('allowanceModalTitle').textContent = 'Sửa phụ cấp';
                            // Open modal without resetting form
                            openAllowanceModal(false);
                        })
                        .catch(err => {
                            console.error('Error loading allowance:', err);
                            alert('Lỗi khi tải dữ liệu phụ cấp: ' + err.message);
                        });
            }

            function editDeduction(id) {
                if (!id) {
                    console.error('Invalid deduction ID');
                    return;
                }
                fetch('<%=request.getContextPath()%>/api/deduction/' + id)
                        .then(r => {
                            if (!r.ok) {
                                throw new Error('Không tải được dữ liệu khấu trừ: ' + r.status);
                            }
                            return r.json();
                        })
                        .then(data => {
                            if (!data || !data.id) {
                                throw new Error('Dữ liệu khấu trừ không hợp lệ');
                            }
                            // Set form values with the fetched data
                            document.getElementById('deductionId').value = data.id || '';
                            document.getElementById('deductionEmployee').value = data.employeeId || '';
                            document.getElementById('deductionType').value = data.deductionTypeId || '';
                            document.getElementById('deductionAmount').value = data.amount || '';
                            document.getElementById('deductionMonth').value = data.month || '';
                            document.getElementById('deductionModalTitle').textContent = 'Sửa khấu trừ';
                            // Open modal without resetting form
                            openDeductionModal(false);
                        })
                        .catch(err => {
                            console.error('Error loading deduction:', err);
                            alert('Lỗi khi tải dữ liệu khấu trừ: ' + err.message);
                        });
            }

            function editPayroll(id) {
                if (!id) {
                    console.error('Invalid payroll ID');
                    return;
                }
                console.log('Loading payroll data for ID:', id);
                
                fetch('<%=request.getContextPath()%>/api/payroll?payrollId=' + id)
                    .then(response => {
                        if (!response.ok) {
                            throw new Error('Lỗi kết nối: ' + response.status);
                        }
                        return response.json();
                    })
                    .then(data => {
                        console.log('Payroll data loaded:', data);
                        
                        // Open modal first without resetting form
                        openPayrollModal(false);
                        
                        // Set form values
                        document.getElementById('payrollId').value = data.payrollId || '';
                        document.getElementById('payrollEmployee').value = data.employeeId || '';
                        document.getElementById('payrollPeriod').value = data.payPeriod || '';
                        
                        const actualBaseSalary = parseFloat(data.baseSalary) || 0;
                        const otSalary = parseFloat(data.bonus) || 0; 
                        const allowance = parseFloat(data.allowance) || 0;
                        const deduction = parseFloat(data.deduction) || 0;
                        const netSalary = parseFloat(data.netSalary) || 0;
                        
                        document.getElementById('payrollBaseSalary').value = actualBaseSalary;
                        document.getElementById('payrollActualBaseSalary').value = actualBaseSalary;
                        document.getElementById('payrollOTSalary').value = otSalary;
                        document.getElementById('payrollAllowance').value = allowance;
                        document.getElementById('payrollDeduction').value = deduction;
                        
                        // Auto-calculate Net Salary from components
                        if (typeof updateNetSalaryAuto === 'function') {
                            updateNetSalaryAuto();
                        } else {
                            // Fallback: calculate directly
                            const calculatedNet = actualBaseSalary + otSalary + allowance - deduction;
                            document.getElementById('payrollNetSalary').value = Math.max(0, calculatedNet).toFixed(2);
                        }
                        
                        // Calculate netSalary for summary display
                        const calculatedNet = actualBaseSalary + otSalary + allowance - deduction;
                        const netSalaryForSummary = Math.max(0, calculatedNet);
                        
                        updatePayrollSummary({
                            baseSalary: actualBaseSalary,
                            actualBaseSalary: actualBaseSalary,
                            otSalary: otSalary,
                            totalAllowance: allowance,
                            totalDeduction: deduction,
                            netSalary: netSalaryForSummary
                        });
                        
                        // Load employee-specific data after setting employee and period
                        loadEmployeePayrollData();
                    })
                    .catch(err => {
                        console.error('Error loading payroll:', err);
                            alert('Lỗi khi tải dữ liệu bảng lương: ' + err.message);
                    });
            }

            function deleteAllowance(id) {
                if (confirm('Bạn có chắc muốn xóa phụ cấp này không?')) {
                    window.location.href = '<%=request.getContextPath()%>/hrstaff/payroll/allowance/delete?allowanceId=' + id;
                }
            }

            function deleteDeduction(id) {
                if (confirm('Bạn có chắc muốn xóa khoản khấu trừ này không?')) {
                    window.location.href = '<%=request.getContextPath()%>/hrstaff/payroll/deduction/delete?deductionId=' + id;
                }
            }

            function deletePayroll(id) {
                if (confirm('Bạn có chắc muốn xóa bảng lương này không?')) {
                    const urlParams = new URLSearchParams(window.location.search);
                    let url = '<%=request.getContextPath()%>/hrstaff/payroll/delete?payrollId=' + id;
                    if (urlParams.get('page')) url += '&page=' + urlParams.get('page');
                    if (urlParams.get('pageSize')) url += '&pageSize=' + urlParams.get('pageSize');
                    if (urlParams.get('sortBy')) url += '&sortBy=' + urlParams.get('sortBy');
                    if (urlParams.get('sortOrder')) url += '&sortOrder=' + urlParams.get('sortOrder');
                    if (urlParams.get('employeeFilter')) url += '&employeeFilter=' + urlParams.get('employeeFilter');
                    if (urlParams.get('statusFilter')) url += '&statusFilter=' + urlParams.get('statusFilter');
                    window.location.href = url;
                }
            }

            function submitPayroll(id) {
                if (confirm('Bạn có chắc muốn gửi bảng lương này để phê duyệt không?')) {
                    const urlParams = new URLSearchParams(window.location.search);
                    let url = '<%=request.getContextPath()%>/hrstaff/payroll/submit?payrollId=' + id;
                    // Preserve pagination parameters
                    if (urlParams.get('page')) url += '&page=' + urlParams.get('page');
                    if (urlParams.get('pageSize')) url += '&pageSize=' + urlParams.get('pageSize');
                    if (urlParams.get('sortBy')) url += '&sortBy=' + urlParams.get('sortBy');
                    if (urlParams.get('sortOrder')) url += '&sortOrder=' + urlParams.get('sortOrder');
                    if (urlParams.get('employeeFilter')) url += '&employeeFilter=' + urlParams.get('employeeFilter');
                    if (urlParams.get('statusFilter')) url += '&statusFilter=' + urlParams.get('statusFilter');
                    window.location.href = url;
                }
            }

            function resubmitPayroll(id) {
                if (confirm('Bạn có chắc muốn gửi lại bảng lương bị từ chối này để phê duyệt không? Hãy đảm bảo đã xem và xử lý lý do từ chối.')) {
                    const urlParams = new URLSearchParams(window.location.search);
                    let url = '<%=request.getContextPath()%>/hrstaff/payroll/submit?payrollId=' + id;
                    // Preserve pagination parameters
                    if (urlParams.get('page')) url += '&page=' + urlParams.get('page');
                    if (urlParams.get('pageSize')) url += '&pageSize=' + urlParams.get('pageSize');
                    if (urlParams.get('sortBy')) url += '&sortBy=' + urlParams.get('sortBy');
                    if (urlParams.get('sortOrder')) url += '&sortOrder=' + urlParams.get('sortOrder');
                    if (urlParams.get('employeeFilter')) url += '&employeeFilter=' + urlParams.get('employeeFilter');
                    if (urlParams.get('statusFilter')) url += '&statusFilter=' + urlParams.get('statusFilter');
                    window.location.href = url;
                }
            }

            // ========== Batch Selection Functions ==========
            
            function toggleSelectAll(checkbox) {
                const checkboxes = document.querySelectorAll('.payroll-checkbox');
                checkboxes.forEach(cb => {
                    cb.checked = checkbox.checked;
                });
                updateBatchActions();
            }

            function updateBatchActions() {
                const checkboxes = document.querySelectorAll('.payroll-checkbox:checked');
                const selectedCount = checkboxes.length;
                const batchBar = document.getElementById('batchActionsBar');
                const selectedCountEl = document.getElementById('selectedCount');
                const batchSubmitBtn = document.getElementById('batchSubmitBtn');
                const batchDeleteBtn = document.getElementById('batchDeleteBtn');

                if (selectedCount > 0) {
                    batchBar.style.display = 'flex';
                    selectedCountEl.textContent = selectedCount + ' đã chọn';
                    
                    // Check which actions are available based on selected payrolls
                    let hasDraft = false;
                    let hasRejected = false;
                    let hasDeletable = false;
                    
                    checkboxes.forEach(cb => {
                        const status = cb.getAttribute('data-status');
                        if (status === 'Draft') {
                            hasDraft = true;
                            hasDeletable = true;
                        } else if (status === 'Rejected') {
                            hasRejected = true;
                        }
                    });
                    
                    // Show submit button if there are Draft or Rejected payrolls
                    batchSubmitBtn.style.display = (hasDraft || hasRejected) ? 'inline-block' : 'none';
                    
                    // Show delete button only for Draft payrolls
                    batchDeleteBtn.style.display = hasDeletable ? 'inline-block' : 'none';
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
            }

            function getSelectedPayrollIds() {
                const checkboxes = document.querySelectorAll('.payroll-checkbox:checked');
                return Array.from(checkboxes).map(cb => cb.getAttribute('data-payroll-id'));
            }

            function submitSelectedPayrolls() {
                const selectedIds = getSelectedPayrollIds();
                if (selectedIds.length === 0) {
                    alert('Vui lòng chọn ít nhất một bảng lương để gửi duyệt.');
                    return;
                }
                
                const count = selectedIds.length;
                if (confirm('Bạn có chắc chắn muốn gửi duyệt ' + count + ' bảng lương đã chọn không?')) {
                    const urlParams = new URLSearchParams(window.location.search);
                    let url = '<%=request.getContextPath()%>/hrstaff/payroll/batch-submit?payrollIds=' + selectedIds.join(',');
                    
                    // Preserve pagination parameters
                    if (urlParams.get('page')) url += '&page=' + urlParams.get('page');
                    if (urlParams.get('pageSize')) url += '&pageSize=' + urlParams.get('pageSize');
                    if (urlParams.get('sortBy')) url += '&sortBy=' + urlParams.get('sortBy');
                    if (urlParams.get('sortOrder')) url += '&sortOrder=' + urlParams.get('sortOrder');
                    if (urlParams.get('employeeFilter')) url += '&employeeFilter=' + urlParams.get('employeeFilter');
                    if (urlParams.get('statusFilter')) url += '&statusFilter=' + urlParams.get('statusFilter');
                    
                    window.location.href = url;
                }
            }

            function deleteSelectedPayrolls() {
                const selectedIds = getSelectedPayrollIds();
                if (selectedIds.length === 0) {
                    alert('Vui lòng chọn ít nhất một bảng lương để xóa.');
                    return;
                }
                
                const count = selectedIds.length;
                if (confirm('⚠️ CẢNH BÁO: Bạn có chắc chắn muốn xóa ' + count + ' payroll đã chọn không?\n\nHành động này không thể hoàn tác!')) {
                    const urlParams = new URLSearchParams(window.location.search);
                    let url = '<%=request.getContextPath()%>/hrstaff/payroll/batch-delete?payrollIds=' + selectedIds.join(',');
                    
                    // Preserve pagination parameters
                    if (urlParams.get('page')) url += '&page=' + urlParams.get('page');
                    if (urlParams.get('pageSize')) url += '&pageSize=' + urlParams.get('pageSize');
                    if (urlParams.get('sortBy')) url += '&sortBy=' + urlParams.get('sortBy');
                    if (urlParams.get('sortOrder')) url += '&sortOrder=' + urlParams.get('sortOrder');
                    if (urlParams.get('employeeFilter')) url += '&employeeFilter=' + urlParams.get('employeeFilter');
                    if (urlParams.get('statusFilter')) url += '&statusFilter=' + urlParams.get('statusFilter');
                    
                    window.location.href = url;
                }
            }

            function clearSelection() {
                const checkboxes = document.querySelectorAll('.payroll-checkbox');
                checkboxes.forEach(cb => cb.checked = false);
                document.getElementById('selectAllCheckbox').checked = false;
                updateBatchActions();
            }

            function viewPayrollDetails(id) {
                if (!id) {
                    console.error('Invalid payroll ID');
                    return;
                }
                
                const modal = document.getElementById('payrollDetailsModal');
                const content = document.getElementById('payrollDetailsContent');
                
                // Show loading state
                content.innerHTML = '<div style="text-align:center; padding:40px;"><div class="muted">Đang tải chi tiết bảng lương...</div></div>';
                modal.classList.add('active');
                
                fetch('<%=request.getContextPath()%>/api/payroll?payrollId=' + id)
                    .then(response => {
                        if (!response.ok) {
                            throw new Error('Lỗi kết nối: ' + response.status);
                        }
                        return response.json();
                    })
                    .then(data => {
                        console.log('Payroll details loaded:', data);
                        renderPayrollDetails(data);
                    })
                    .catch(err => {
                        console.error('Error loading payroll details:', err);
                        content.innerHTML = '<div style="background:#fee2e2; border:1px solid #fca5a5; border-radius:6px; padding:16px; color:#991b1b;">' +
                            '<strong>Lỗi:</strong> ' + err.message + '</div>';
                    });
            }
            
            function renderPayrollDetails(data) {
                const content = document.getElementById('payrollDetailsContent');
                const audit = data.audit || {};
                
                let html = '<div style="display:flex; flex-direction:column; gap:20px;">';
                
                // Basic Info Section
                html += '<div class="card" style="padding:16px;">';
                html += '<h4 style="margin:0 0 16px 0; color:var(--primary);">📋 Thông tin cơ bản</h4>';
                html += '<div class="form-row">';
                html += '<div class="form-group"><label>Nhân viên</label><div style="padding:8px 0; font-weight:600;">' + (data.employeeName || 'Không có') + '</div></div>';
                html += '<div class="form-group"><label>Kỳ lương</label><div style="padding:8px 0; font-weight:600;">' + (data.payPeriod || 'Không có') + '</div></div>';
                html += '</div>';
                html += '<div class="form-row">';
                html += '<div class="form-group"><label>Trạng thái</label><div style="padding:8px 0;"><span class="status-badge status-' + (data.status || 'Draft') + '">' + getPayrollStatusLabel(data.status || 'Draft') + '</span></div></div>';
                if (data.approvedDate) {
                    html += '<div class="form-group"><label>Ngày duyệt</label><div style="padding:8px 0;">' + data.approvedDate + '</div></div>';
                }
                html += '</div>';
                html += '</div>';
                
                // Salary Breakdown Section
                html += '<div class="card" style="padding:16px;">';
                html += '<h4 style="margin:0 0 16px 0; color:var(--primary);">💰 Chi tiết lương</h4>';
                html += '<div class="summary-grid" style="grid-template-columns: repeat(3, 1fr); gap:12px; margin:0;">';
                html += '<div style="background:#f0f9ff; padding:12px; border-radius:6px; border-left:3px solid #3b82f6;">';
                html += '<div style="font-size:11px; color:var(--muted); margin-bottom:4px;">Lương cơ bản</div>';
                html += '<div style="font-weight:600; font-size:16px;">' + formatCurrency(parseFloat(data.baseSalary) || 0) + '</div>';
                if (audit.actualBaseSalary) {
                    html += '<div style="font-size:10px; color:var(--muted); margin-top:2px;">(Thực nhận: ' + formatCurrency(parseFloat(audit.actualBaseSalary)) + ')</div>';
                }
                html += '</div>';
                html += '<div style="background:#f0fdf4; padding:12px; border-radius:6px; border-left:3px solid var(--success);">';
                html += '<div style="font-size:11px; color:var(--muted); margin-bottom:4px;">Lương tăng ca</div>';
                html += '<div style="font-weight:600; font-size:16px; color:var(--success);">' + formatCurrency(parseFloat(data.bonus) || 0) + '</div>';
                if (audit.overtimeHours) {
                    html += '<div style="font-size:10px; color:var(--muted); margin-top:2px;">(' + parseFloat(audit.overtimeHours).toFixed(1) + ' giờ)</div>';
                }
                html += '</div>';
                html += '<div style="background:#fef3c7; padding:12px; border-radius:6px; border-left:3px solid var(--warning);">';
                html += '<div style="font-size:11px; color:var(--muted); margin-bottom:4px;">Phụ cấp</div>';
                html += '<div style="font-weight:600; font-size:16px;">' + formatCurrency(parseFloat(data.allowance) || 0) + '</div>';
                html += '</div>';
                html += '</div>';
                html += '</div>';
                
                // Attendance Details (if available)
                if (audit.actualWorkingDays !== undefined || audit.paidLeaveDays !== undefined) {
                    html += '<div class="card" style="padding:16px;">';
                    html += '<h4 style="margin:0 0 16px 0; color:var(--primary);">📅 Chi tiết chấm công</h4>';
                    html += '<div class="summary-grid" style="grid-template-columns: repeat(4, 1fr); gap:12px; margin:0;">';
                    if (audit.actualWorkingDays !== undefined) {
                        html += '<div style="background:#f0f9ff; padding:12px; border-radius:6px;">';
                        html += '<div style="font-size:11px; color:var(--muted); margin-bottom:4px;">Ngày công thực tế</div>';
                        html += '<div style="font-weight:600; font-size:16px;">' + parseFloat(audit.actualWorkingDays).toFixed(1) + ' ngày</div>';
                        html += '</div>';
                    }
                    if (audit.paidLeaveDays !== undefined) {
                        html += '<div style="background:#f0fdf4; padding:12px; border-radius:6px;">';
                        html += '<div style="font-size:11px; color:var(--muted); margin-bottom:4px;">Ngày nghỉ có lương</div>';
                        html += '<div style="font-weight:600; font-size:16px; color:var(--success);">' + parseFloat(audit.paidLeaveDays).toFixed(1) + ' ngày</div>';
                        html += '</div>';
                    }
                    if (audit.unpaidLeaveDays !== undefined) {
                        html += '<div style="background:#fee2e2; padding:12px; border-radius:6px;">';
                        html += '<div style="font-size:11px; color:var(--muted); margin-bottom:4px;">Ngày nghỉ không lương</div>';
                        html += '<div style="font-weight:600; font-size:16px; color:var(--error);">' + parseFloat(audit.unpaidLeaveDays).toFixed(1) + ' ngày</div>';
                        html += '</div>';
                    }
                    if (audit.overtimeHours !== undefined) {
                        html += '<div style="background:#f0fdf4; padding:12px; border-radius:6px;">';
                        html += '<div style="font-size:11px; color:var(--muted); margin-bottom:4px;">Giờ tăng ca</div>';
                        html += '<div style="font-weight:600; font-size:16px; color:var(--success);">' + parseFloat(audit.overtimeHours).toFixed(1) + ' giờ</div>';
                        html += '</div>';
                    }
                    html += '</div>';
                    html += '</div>';
                }
                
                // Insurance & Tax Details (if available)
                if (audit.bhxh !== undefined || audit.personalTax !== undefined) {
                    html += '<div class="card" style="padding:16px;">';
                    html += '<h4 style="margin:0 0 16px 0; color:var(--primary);">💳 Bảo hiểm & thuế</h4>';
                    html += '<div class="summary-grid" style="grid-template-columns: repeat(4, 1fr); gap:12px; margin:0;">';
                    if (audit.bhxh !== undefined) {
                        html += '<div style="background:#f0f9ff; padding:12px; border-radius:6px; border-left:3px solid #3b82f6;">';
                        html += '<div style="font-size:11px; color:var(--muted); margin-bottom:4px;">BHXH (8%)</div>';
                        html += '<div style="font-weight:600; font-size:16px; color:#3b82f6;">' + formatCurrency(parseFloat(audit.bhxh) || 0) + '</div>';
                        html += '</div>';
                    }
                    if (audit.bhyt !== undefined) {
                        html += '<div style="background:#f0fdf4; padding:12px; border-radius:6px; border-left:3px solid #10b981;">';
                        html += '<div style="font-size:11px; color:var(--muted); margin-bottom:4px;">BHYT (1.5%)</div>';
                        html += '<div style="font-weight:600; font-size:16px; color:#10b981;">' + formatCurrency(parseFloat(audit.bhyt) || 0) + '</div>';
                        html += '</div>';
                    }
                    if (audit.bhtn !== undefined) {
                        html += '<div style="background:#f5f3ff; padding:12px; border-radius:6px; border-left:3px solid #8b5cf6;">';
                        html += '<div style="font-size:11px; color:var(--muted); margin-bottom:4px;">BHTN (1%)</div>';
                        html += '<div style="font-weight:600; font-size:16px; color:#8b5cf6;">' + formatCurrency(parseFloat(audit.bhtn) || 0) + '</div>';
                        html += '</div>';
                    }
                    if (audit.personalTax !== undefined) {
                        html += '<div style="background:#fee2e2; padding:12px; border-radius:6px; border-left:3px solid var(--error);">';
                        html += '<div style="font-size:11px; color:var(--muted); margin-bottom:4px;">Thuế TNCN</div>';
                        html += '<div style="font-weight:600; font-size:16px; color:var(--error);">' + formatCurrency(parseFloat(audit.personalTax) || 0) + '</div>';
                        html += '</div>';
                    }
                    html += '</div>';
                    if (audit.taxableIncome !== undefined) {
                        html += '<div style="margin-top:12px; padding-top:12px; border-top:1px solid var(--border); font-size:12px;">';
                        html += '<span style="color:var(--muted);">Thu nhập chịu thuế:</span> ';
                        html += '<strong>' + formatCurrency(parseFloat(audit.taxableIncome) || 0) + '</strong>';
                        html += '</div>';
                    }
                    html += '</div>';
                }
                
                // Deductions Section
                html += '<div class="card" style="padding:16px;">';
                html += '<h4 style="margin:0 0 16px 0; color:var(--primary);">➖ Khấu trừ</h4>';
                html += '<div style="display:flex; flex-direction:column; gap:8px;">';
                html += '<div style="display:flex; justify-content:space-between; padding:8px; background:#f9fafb; border-radius:6px;">';
                html += '<span>Tổng khấu trừ:</span>';
                html += '<strong style="color:var(--error);">' + formatCurrency(parseFloat(data.deduction) || 0) + '</strong>';
                html += '</div>';
                if (audit.totalDeduction !== undefined) {
                    html += '<div style="font-size:12px; color:var(--muted); padding-left:8px;">';
                    html += 'Bao gồm: Bảo hiểm (' + formatCurrency((parseFloat(audit.bhxh) || 0) + (parseFloat(audit.bhyt) || 0) + (parseFloat(audit.bhtn) || 0)) + ')';
                    if (audit.personalTax) {
                        html += ' + Thuế (' + formatCurrency(parseFloat(audit.personalTax)) + ')';
                    }
                    if (audit.otherDeduction) {
                        html += ' + Khác (' + formatCurrency(parseFloat(audit.otherDeduction)) + ')';
                    }
                    html += ')';
                    html += '</div>';
                }
                html += '</div>';
                html += '</div>';
                
                // Net Salary
                html += '<div class="card" style="padding:16px; border-left:4px solid var(--success);">';
                html += '<div style="display:flex; justify-content:space-between; align-items:center;">';
                html += '<h4 style="margin:0; color:var(--success);">💵 Lương thực nhận</h4>';
                html += '<div style="font-size:28px; font-weight:700; color:var(--success);">' + formatCurrency(parseFloat(data.netSalary) || 0) + '</div>';
                html += '</div>';
                html += '<div style="margin-top:8px; font-size:12px; color:var(--muted);">';
                html += 'Lương cơ bản + lương tăng ca + phụ cấp - tổng khấu trừ';
                html += '</div>';
                html += '</div>';
                
                // Notes (if available)
                if (audit.notes) {
                    // Check if payroll is rejected to highlight rejection note
                    const isRejected = (data.status === 'Rejected' || audit.status === 'Rejected');
                    const hasRejectionNote = audit.notes.includes('[REJECTED]');
                    
                    html += '<div class="card" style="padding:16px;' + (isRejected && hasRejectionNote ? ' border-left:4px solid var(--error); background:#fef2f2;' : '') + '">';
                    html += '<h4 style="margin:0 0 8px 0; color:' + (isRejected && hasRejectionNote ? 'var(--error)' : 'var(--primary)') + ';">';
                    html += isRejected && hasRejectionNote ? '⚠️ Ghi chú từ chối' : '📝 Ghi chú';
                    html += '</h4>';
                    html += '<div style="color:' + (isRejected && hasRejectionNote ? 'var(--text-color)' : 'var(--muted)') + '; white-space: pre-wrap; line-height: 1.6;">' + audit.notes.replace(/\n/g, '<br>') + '</div>';
                    if (isRejected && hasRejectionNote) {
                        html += '<div style="margin-top:12px; padding:8px; background:#fee2e2; border-radius:4px; font-size:12px; color:#991b1b;">';
                        html += '<strong>Vui lòng xem lại và chỉnh sửa bảng lương theo lý do từ chối ở trên.</strong>';
                        html += '</div>';
                    }
                    html += '</div>';
                }
                
                html += '</div>';
                
                content.innerHTML = html;
            }
            
            function closePayrollDetailsModal() {
                const modal = document.getElementById('payrollDetailsModal');
                if (modal) {
                    modal.classList.remove('active');
                }
            }
            
            // Generate payroll for all employees using stored procedure
            function generatePayrollForAll() {
                const month = prompt('Nhập kỳ lương (YYYY-MM):', new Date().toISOString().slice(0, 7));
                if (!month) return;
                
                if (confirm(`Tạo bảng lương cho TẤT CẢ nhân viên đang hoạt động trong kỳ ${month}?\n\nHệ thống sẽ dùng thủ tục sp_GeneratePayrollImproved để tự tính:\n- Ngày công thực tế và ngày nghỉ có lương\n- Lương cơ bản thực nhận\n- Lương tăng ca\n- Bảo hiểm (BHXH, BHYT, BHTN)\n- Thuế thu nhập cá nhân (TNCN)\n- Lương thực nhận\n\nThao tác này sẽ tạo/cập nhật bảng kiểm toán lương và bảng lương cho tất cả nhân viên đang hoạt động.`)) {
                    window.location.href = '<%=request.getContextPath()%>/hrstaff/payroll/generate-all?period=' + month;
                }
            }

            function filterAllowances() {
                const employeeId = document.getElementById('allowanceEmployeeFilter').value;
                const month = document.getElementById('allowanceMonthFilter').value;
                const urlParams = new URLSearchParams(window.location.search);
                
                // Clear existing filters and set new ones
                urlParams.delete('employeeFilter');
                urlParams.delete('allowanceMonth');
                urlParams.delete('allowancePage'); // Reset to page 1 when filter changes
                
                if (employeeId) {
                    urlParams.set('employeeFilter', employeeId);
                }
                if (month) {
                    urlParams.set('allowanceMonth', month);
                }
                
                // Preserve page size if exists
                const pageSize = urlParams.get('allowancePageSize');
                if (pageSize) {
                    urlParams.set('allowancePageSize', pageSize);
                }
                
                urlParams.set('tab', 'allowance');
                urlParams.set('allowancePage', '1'); // Always go to page 1 on filter change
                
                window.location.href = '<%=request.getContextPath()%>/hrstaff/payroll?' + urlParams.toString();
            }

            function filterDeductions() {
                const employeeId = document.getElementById('deductionEmployeeFilter').value;
                const month = document.getElementById('deductionMonthFilter').value;
                const urlParams = new URLSearchParams(window.location.search);
                
                // Clear existing filters and set new ones
                urlParams.delete('employeeFilter');
                urlParams.delete('deductionMonth');
                urlParams.delete('deductionPage'); // Reset to page 1 when filter changes
                
                if (employeeId) {
                    urlParams.set('employeeFilter', employeeId);
                }
                if (month) {
                    urlParams.set('deductionMonth', month);
                }
                
                // Preserve page size if exists
                const pageSize = urlParams.get('deductionPageSize');
                if (pageSize) {
                    urlParams.set('deductionPageSize', pageSize);
                }
                
                urlParams.set('tab', 'deduction');
                urlParams.set('deductionPage', '1'); // Always go to page 1 on filter change
                
                window.location.href = '<%=request.getContextPath()%>/hrstaff/payroll?' + urlParams.toString();
            }

            function filterPayrolls() {
                const employeeId = document.getElementById('payrollEmployeeFilter').value;
                const status = document.getElementById('payrollStatusFilter').value;
                const urlParams = new URLSearchParams(window.location.search);
                
                // Clear existing filters
                urlParams.delete('employeeFilter');
                urlParams.delete('statusFilter');
                
                // Set new filters
                if (employeeId) {
                    urlParams.set('employeeFilter', employeeId);
                }
                if (status) {
                    urlParams.set('statusFilter', status);
                }
                
                // Reset to page 1 when filtering
                urlParams.set('page', '1');
                urlParams.set('tab', 'payroll');
                
                // Preserve pagination settings
                if (!urlParams.get('pageSize')) {
                    urlParams.set('pageSize', '10');
                }
                if (!urlParams.get('sortBy')) {
                    urlParams.set('sortBy', 'PayPeriod');
                }
                if (!urlParams.get('sortOrder')) {
                    urlParams.set('sortOrder', 'DESC');
                }
                
                window.location.href = '<%=request.getContextPath()%>/hrstaff/payroll?' + urlParams.toString();
            }
            
            function changeAllowancePageSize(newSize) {
                const urlParams = new URLSearchParams(window.location.search);
                urlParams.set('tab', 'allowance');
                urlParams.set('allowancePageSize', newSize);
                urlParams.set('allowancePage', '1'); // Reset to first page
                
                // Preserve filters
                const employeeFilter = document.getElementById('allowanceEmployeeFilter')?.value;
                const monthFilter = document.getElementById('allowanceMonthFilter')?.value;
                if (employeeFilter) {
                    urlParams.set('employeeFilter', employeeFilter);
                }
                if (monthFilter) {
                    urlParams.set('allowanceMonth', monthFilter);
                }
                
                window.location.href = '<%=request.getContextPath()%>/hrstaff/payroll?' + urlParams.toString();
            }

            function changeDeductionPageSize(newSize) {
                const urlParams = new URLSearchParams(window.location.search);
                urlParams.set('tab', 'deduction');
                urlParams.set('deductionPageSize', newSize);
                urlParams.set('deductionPage', '1'); // Reset to first page
                
                // Preserve filters
                const employeeFilter = document.getElementById('deductionEmployeeFilter')?.value;
                const monthFilter = document.getElementById('deductionMonthFilter')?.value;
                if (employeeFilter) {
                    urlParams.set('employeeFilter', employeeFilter);
                }
                if (monthFilter) {
                    urlParams.set('deductionMonth', monthFilter);
                }
                
                window.location.href = '<%=request.getContextPath()%>/hrstaff/payroll?' + urlParams.toString();
            }

            function changePayrollPageSize(newSize) {
                const urlParams = new URLSearchParams(window.location.search);
                urlParams.set('pageSize', newSize);
                urlParams.set('page', '1'); // Reset to first page when changing page size
                urlParams.set('tab', 'payroll');
                // Preserve existing filters and sort (they are already in URL)
                window.location.href = '<%=request.getContextPath()%>/hrstaff/payroll?' + urlParams.toString();
            }
            
            function sortPayrollTable(column) {
                const urlParams = new URLSearchParams(window.location.search);
                const currentSortBy = urlParams.get('sortBy') || 'PayPeriod';
                const currentSortOrder = urlParams.get('sortOrder') || 'DESC';
                let newSortOrder = 'DESC';
                
                if (currentSortBy === column) {
                    newSortOrder = (currentSortOrder === 'ASC') ? 'DESC' : 'ASC';
                }
                
                urlParams.set('page', '1'); // Reset to first page when sorting
                urlParams.set('sortBy', column);
                urlParams.set('sortOrder', newSortOrder);
                urlParams.set('tab', 'payroll');
                // Preserve existing filters and pageSize (they are already in URL)
                
                window.location.href = '<%=request.getContextPath()%>/hrstaff/payroll?' + urlParams.toString();
            }
            
            function filterAttendance() {
                const employeeId = document.getElementById('attendanceEmployeeFilter').value;
                const month = document.getElementById('attendanceMonthFilter').value;
                
                // If both filters are empty, just reset stats to 0 and reload without params
                if (!employeeId && !month) {
                    resetAttendanceStats();
                    window.location.href = '<%=request.getContextPath()%>/hrstaff/payroll?tab=attendance';
                    return;
                }
                
                // Reload page with filters
                const params = new URLSearchParams();
                if (employeeId)
                    params.append('employeeFilter', employeeId);
                if (month)
                    params.append('attendanceMonth', month);
                params.append('tab', 'attendance');
                window.location.href = '<%=request.getContextPath()%>/hrstaff/payroll?' + params.toString();
            }

            // Reset attendance stats to default values (0)
            function resetAttendanceStats() {
                const statWorkDays = document.getElementById('statWorkDays');
                const statPaidLeaveInfo = document.getElementById('statPaidLeaveInfo');
                const statUnpaidLeave = document.getElementById('statUnpaidLeave');
                const statUnpaidLeaveAmount = document.getElementById('statUnpaidLeaveAmount');
                const statLateCount = document.getElementById('statLateCount');
                const statLatePenalty = document.getElementById('statLatePenalty');
                const statEarlyLeave = document.getElementById('statEarlyLeave');
                const statOvertimeHours = document.getElementById('statOvertimeHours');
                const statOvertimeAmount = document.getElementById('statOvertimeAmount');
                
                if (statWorkDays) statWorkDays.textContent = '0 ngày';
                if (statPaidLeaveInfo) statPaidLeaveInfo.textContent = '0 ngày nghỉ có lương';
                if (statUnpaidLeave) statUnpaidLeave.textContent = '0 ngày';
                if (statUnpaidLeaveAmount) statUnpaidLeaveAmount.textContent = '0 VNĐ';
                if (statLateCount) statLateCount.textContent = '0 lần';
                if (statLatePenalty) statLatePenalty.textContent = '0 VNĐ';
                if (statEarlyLeave) statEarlyLeave.textContent = '0 lần';
                if (statOvertimeHours) statOvertimeHours.textContent = '0.0 giờ';
                if (statOvertimeAmount) statOvertimeAmount.textContent = '0 VNĐ';
            }

            function loadAttendanceData() {
                console.log('loadAttendanceData called');
                const employeeId = document.getElementById('attendanceEmployeeFilter')?.value;
                const monthInput = document.getElementById('attendanceMonthFilter');
                
                if (!monthInput) {
                    console.error('attendanceMonthFilter element not found');
                    return;
                }
                
                let month = monthInput.value || '';

                // If no employee or month selected, reset to default (0) and return
                if (!employeeId || !month || month === '-' || month === '') {
                    console.log('No employee or month selected, resetting to default values');
                    resetAttendanceStats();
                    return;
                }

                // Validate and fix month format
                if (!month.match(/^\d{4}-\d{2}$/)) {
                    console.warn('Invalid month format:', month, '- fixing...');
                    // Set default month
                    const now = new Date();
                    const year = now.getFullYear();
                    const monthNum = String(now.getMonth() + 1).padStart(2, '0');
                    month = year + '-' + monthNum; // Use string concatenation instead of template literal
                    
                    // Validate before setting
                    if (month && month.match(/^\d{4}-\d{2}$/)) {
                        monthInput.value = month;
                        console.log('Fixed month to:', month);
                    } else {
                        console.error('Failed to generate valid month format. year:', year, 'monthNum:', monthNum, 'result:', month);
                        resetAttendanceStats();
                        return;
                    }
                }
                
                console.log('Loading attendance data for employee:', employeeId, 'month:', month);
                const url = '<%=request.getContextPath()%>/hrstaff/payroll/calculate?employeeId=' + employeeId + '&month=' + month;
                console.log('Fetching from:', url);
                
                fetch(url)
                        .then(response => {
                            console.log('Response status:', response.status);
                            if (!response.ok)
                                throw new Error('Lỗi kết nối: ' + response.status);
                            return response.text();
                        })
                        .then(text => {
                            console.log('Response text:', text);
                            try {
                                const data = JSON.parse(text);
                                console.log('Parsed data:', data);
                                if (data && data.attendance) {
                                    const att = data.attendance;
                                    console.log('Attendance stats:', att);
                                    
                                    const statWorkDays = document.getElementById('statWorkDays');
                                    const statPaidLeaveInfo = document.getElementById('statPaidLeaveInfo');
                                    const statUnpaidLeave = document.getElementById('statUnpaidLeave');
                                    const statUnpaidLeaveAmount = document.getElementById('statUnpaidLeaveAmount');
                                    const statLateCount = document.getElementById('statLateCount');
                                    const statLatePenalty = document.getElementById('statLatePenalty');
                                    const statEarlyLeave = document.getElementById('statEarlyLeave');
                                    const statOvertimeHours = document.getElementById('statOvertimeHours');
                                    const statOvertimeAmount = document.getElementById('statOvertimeAmount');
                                    
                                    const actualWorkingDays = parseFloat(att.actualWorkingDays) || 0;
                                    const paidLeaveDays = parseFloat(att.paidLeaveDays) || 0;
                                    const unpaidLeaveDays = parseFloat(att.unpaidLeaveDays) || 0;
                                    
                                    if (statWorkDays) statWorkDays.textContent = actualWorkingDays.toFixed(1) + ' ngày';
                                    if (statPaidLeaveInfo) statPaidLeaveInfo.textContent = paidLeaveDays.toFixed(1) + ' ngày nghỉ có lương';
                                    if (statUnpaidLeave) statUnpaidLeave.textContent = unpaidLeaveDays.toFixed(1) + ' ngày';
                                    if (statUnpaidLeaveAmount) statUnpaidLeaveAmount.textContent = formatCurrency(parseFloat(att.calculatedUnpaidLeaveAmount) || 0);
                                    if (statLateCount) statLateCount.textContent = (att.lateCount || 0) + ' lần';
                                    if (statLatePenalty) statLatePenalty.textContent = formatCurrency(parseFloat(att.calculatedLatePenalty) || 0);
                                    if (statEarlyLeave) statEarlyLeave.textContent = (att.earlyLeaveCount || 0) + ' lần';
                                    if (statOvertimeHours) statOvertimeHours.textContent = parseFloat(att.totalOvertimeHours || 0).toFixed(1) + ' giờ';
                                    if (statOvertimeAmount) statOvertimeAmount.textContent = formatCurrency(parseFloat(att.calculatedOvertimeAmount) || 0);

                                    console.log('Attendance stats displayed');
                                } else {
                                    console.warn('No attendance data in response:', data);
                                    // If no data, reset to default (0)
                                    resetAttendanceStats();
                                }
                            } catch (e) {
                                console.error('Error parsing attendance data:', e);
                                console.error('Response text:', text);
                                alert('Lỗi khi tải dữ liệu chấm công. Vui lòng kiểm tra nhật ký trình duyệt để biết chi tiết.');
                            }
                        })
                        .catch(err => {
                            console.error('Fetch error:', err);
                            alert('Lỗi khi tải dữ liệu chấm công: ' + err.message);
                        });
            }

            function applyAttendanceDeductions() {
                alert('Tính năng: Tự tạo khoản khấu trừ từ dữ liệu chấm công');
            }

            function applyAttendanceAllowances() {
                alert('Tính năng: Tự tạo phụ cấp từ dữ liệu chấm công');
            }


            function loadEmployeePayrollData() {
                const employeeId = document.getElementById('payrollEmployee').value;
                const month = document.getElementById('payrollPeriod').value;

                if (!employeeId || !month) {
                    document.getElementById('payrollSummary').style.display = 'none';
                    document.getElementById('attendanceInfoSection').style.display = 'none';
                    document.getElementById('insuranceTaxSection').style.display = 'none';
                    return;
                }

                const url = '<%=request.getContextPath()%>/hrstaff/payroll/calculate?employeeId=' + employeeId + '&month=' + month;
                fetch(url)
                        .then(response => {
                            if (!response.ok)
                                throw new Error('Lỗi kết nối: ' + response.status);
                            return response.text();
                        })
                        .then(text => {
                            try {
                                const data = JSON.parse(text);
                                console.log('Payroll data received:', data); // Debug log
                                
                                if (data) {
                                    const baseSalary = parseFloat(data.baseSalary) || 0;
                                    const actualBaseSalary = parseFloat(data.actualBaseSalary) || 0;
                                    const otSalary = parseFloat(data.otSalary) || 0;
                                    const totalAllowance = parseFloat(data.totalAllowance) || 0;
                                    const totalDeduction = parseFloat(data.totalDeduction) || 0;
                                    const netSalary = parseFloat(data.netSalary) || 0;

                                    console.log('Parsed values:', {baseSalary, actualBaseSalary, otSalary, totalAllowance, totalDeduction, netSalary}); // Debug log

                                    // Store base salary (hidden)
                                    document.getElementById('payrollBaseSalary').value = baseSalary;
                                    
                                    // Store calculated values (for comparison)
                                    document.getElementById('calculatedActualBaseSalary').value = actualBaseSalary;
                                    document.getElementById('calculatedOTSalary').value = otSalary;
                                    document.getElementById('calculatedAllowance').value = totalAllowance;
                                    document.getElementById('calculatedDeduction').value = totalDeduction;
                                    document.getElementById('calculatedNetSalary').value = netSalary;
                                    
                                    // Set editable values (initially same as calculated)
                                    document.getElementById('payrollActualBaseSalary').value = actualBaseSalary;
                                    document.getElementById('payrollOTSalary').value = otSalary;
                                    document.getElementById('payrollAllowance').value = totalAllowance;
                                    document.getElementById('payrollDeduction').value = totalDeduction;
                                    
                                    // Auto-calculate Net Salary from components
                                    if (typeof updateNetSalaryAuto === 'function') {
                                        updateNetSalaryAuto();
                                    } else {
                                        // Fallback: calculate directly
                                        const calculatedNet = actualBaseSalary + otSalary + totalAllowance - totalDeduction;
                                        document.getElementById('payrollNetSalary').value = Math.max(0, calculatedNet).toFixed(2);
                                    }
                                    
                                    // Show manual edit section
                                    document.getElementById('manualEditSection').style.display = 'block';
                                    
                                    // Hide warnings initially
                                    if (typeof clearAllWarnings === 'function') {
                                        clearAllWarnings();
                                    }
                                    const manualEditWarningEl = document.getElementById('manualEditWarning');
                                    if (manualEditWarningEl) manualEditWarningEl.style.display = 'none';
                                    
                                    // Auto-validate after loading (now just ensures Net Salary is calculated)
                                    if (typeof validateNetSalary === 'function') {
                                        validateNetSalary();
                                    }

                                    // Check if baseSalary is 0 and show warning
                                    if (baseSalary === 0) {
                                        console.warn('PayrollCalculateController: BaseSalary is 0. Cannot calculate payroll properly.');
                                        alert('⚠️ Cảnh báo: Nhân viên này chưa có lương cơ bản từ hợp đồng.\n\n' +
                                            'Vui lòng kiểm tra:\n' +
                                            '1. Nhân viên đã có hợp đồng chưa?\n' +
                                            '2. Hợp đồng đã có lương cơ bản > 0 chưa?\n' +
                                            '3. Hợp đồng có hiệu lực trong tháng tính lương chưa?\n' +
                                            '(Ngày bắt đầu <= ngày cuối tháng VÀ ngày kết thúc >= ngày đầu tháng hoặc để trống)\n\n' +
                                            'Không thể tính lương chính xác nếu không có lương cơ bản.');
                                    }
                                    
                                    // Update Insurance & Tax info if available
                                    if (data.insurance) {
                                        console.log('Insurance data:', data.insurance); // Debug log
                                        updateInsuranceTaxInfo(data.insurance, baseSalary);
                                    } else {
                                        console.warn('No insurance data in response'); // Debug log
                                    }
                                    
                                    // Update Attendance info if available (this will store data in window.currentAttendanceData)
                                    if (data.attendance) {
                                        console.log('Calling updateAttendanceInfo with:', data.attendance);
                                        console.log('data.attendance.actualWorkingDays:', data.attendance.actualWorkingDays);
                                        console.log('data.attendance.paidLeaveDays:', data.attendance.paidLeaveDays);
                                        updateAttendanceInfo(data.attendance, baseSalary);
                                    } else {
                                        console.warn('data.attendance is null or undefined');
                                    }
                                    
                                    // Always show summary, even if values are 0
                                    updatePayrollSummary({
                                        baseSalary: baseSalary,
                                        actualBaseSalary: actualBaseSalary,
                                        otSalary: otSalary,
                                        totalAllowance: totalAllowance,
                                        totalDeduction: totalDeduction,
                                        netSalary: netSalary,
                                        attendance: data.attendance // Pass attendance data
                                    });
                                    
                                    console.log('Summary updated'); // Debug log
                                }
                            } catch (e) {
                                console.error('Error parsing payroll data:', e);
                                console.error('Response text:', text);
                                alert('Lỗi khi đọc dữ liệu bảng lương. Vui lòng kiểm tra nhật ký trình duyệt để biết chi tiết.');
                            }
                        })
                        .catch(err => {
                            console.error('Fetch error:', err);
                            alert('Lỗi khi tải dữ liệu bảng lương. Vui lòng thử lại.');
                        });
            }
            
            // Update Insurance & Tax info in Payroll modal
            function updateInsuranceTaxInfo(insurance, baseSalaryParam) {
                if (!insurance) {
                    console.warn('updateInsuranceTaxInfo: insurance is null or undefined');
                    return;
                }
                
                console.log('updateInsuranceTaxInfo called with:', insurance); // Debug log
                
                const bhxh = parseFloat(insurance.bhxh) || 0;
                const bhyt = parseFloat(insurance.bhyt) || 0;
                const bhtn = parseFloat(insurance.bhtn) || 0;
                const insuranceTotal = parseFloat(insurance.insuranceTotal) || 0;
                const tax = parseFloat(insurance.tax) || 0;
                const taxableIncome = parseFloat(insurance.taxableIncome) || 0;
                const dependents = insurance.dependents || 0;
                
                console.log('Parsed values:', {bhxh, bhyt, bhtn, insuranceTotal, tax, taxableIncome, dependents}); // Debug log
                
                // Check if baseSalary is 0 (no contract or no base salary)
                const baseSalary = baseSalaryParam !== undefined ? baseSalaryParam : 
                    (parseFloat(document.getElementById('payrollBaseSalary').value) || 0);
                const hasBaseSalary = baseSalary > 0;
                
                // Update display
                const bhxhEl = document.getElementById('insBHXH');
                const bhytEl = document.getElementById('insBHYT');
                const bhtnEl = document.getElementById('insBHTN');
                const taxEl = document.getElementById('insTax');
                const taxableIncomeEl = document.getElementById('insTaxableIncome');
                const dependentsEl = document.getElementById('insDependents');
                const totalEl = document.getElementById('insTotal');
                const warningEl = document.getElementById('insuranceWarning');
                const baseSalaryWarningEl = document.getElementById('baseSalaryWarning');
                const displayBaseSalaryEl = document.getElementById('displayBaseSalary');
                
                if (bhxhEl) {
                    bhxhEl.textContent = formatCurrency(bhxh);
                    console.log('Updated insBHXH:', formatCurrency(bhxh));
                } else {
                    console.error('Element insBHXH not found');
                }
                
                if (bhytEl) bhytEl.textContent = formatCurrency(bhyt);
                if (bhtnEl) bhtnEl.textContent = formatCurrency(bhtn);
                if (taxEl) taxEl.textContent = formatCurrency(tax);
                if (taxableIncomeEl) taxableIncomeEl.textContent = formatCurrency(taxableIncome);
                if (dependentsEl) dependentsEl.textContent = dependents;
                if (totalEl) totalEl.textContent = formatCurrency(insuranceTotal);
                
                // Show/hide warning if no base salary
                if (warningEl) {
                    if (!hasBaseSalary) {
                        warningEl.style.display = 'block';
                    } else {
                        warningEl.style.display = 'none';
                    }
                }
                
                // Show base salary info
                if (baseSalaryWarningEl && displayBaseSalaryEl) {
                    displayBaseSalaryEl.textContent = formatCurrency(baseSalary);
                    if (hasBaseSalary) {
                        baseSalaryWarningEl.style.display = 'block';
                        baseSalaryWarningEl.style.background = '#d1fae5';
                        baseSalaryWarningEl.style.borderColor = '#10b981';
                        baseSalaryWarningEl.style.color = '#065f46';
                    } else {
                        baseSalaryWarningEl.style.display = 'block';
                        baseSalaryWarningEl.style.background = '#fee2e2';
                        baseSalaryWarningEl.style.borderColor = '#fca5a5';
                        baseSalaryWarningEl.style.color = '#991b1b';
                    }
                }
                
                // Show insurance section (always show, even if values are 0)
                const section = document.getElementById('insuranceTaxSection');
                if (section) {
                    section.style.display = 'block';
                    console.log('Insurance section displayed');
                } else {
                    console.error('Element insuranceTaxSection not found');
                }
            }
            
            // Update Attendance info in Payroll modal
            function updateAttendanceInfo(attendance, baseSalary) {
                console.log('updateAttendanceInfo called with:', attendance);
                console.log('attendance.actualWorkingDays:', attendance?.actualWorkingDays, 'type:', typeof attendance?.actualWorkingDays);
                console.log('attendance.paidLeaveDays:', attendance?.paidLeaveDays, 'type:', typeof attendance?.paidLeaveDays);
                if (!attendance) {
                    console.warn('updateAttendanceInfo: attendance is null or undefined');
                    return;
                }
                
                // Safe parsing with fallback to 0 - handle null, undefined, and NaN
                const actualWorkingDays = (attendance.actualWorkingDays != null && !isNaN(parseFloat(attendance.actualWorkingDays))) 
                    ? parseFloat(attendance.actualWorkingDays) : 0;
                const paidLeaveDays = (attendance.paidLeaveDays != null && !isNaN(parseFloat(attendance.paidLeaveDays))) 
                    ? parseFloat(attendance.paidLeaveDays) : 0;
                const unpaidLeaveDays = (attendance.unpaidLeaveDays != null && !isNaN(parseFloat(attendance.unpaidLeaveDays))) 
                    ? parseFloat(attendance.unpaidLeaveDays) : 0;
                const workDays = attendance.workDays || 0;
                const lateCount = attendance.lateCount || 0;
                const earlyLeaveCount = attendance.earlyLeaveCount || 0;
                const totalOvertimeHours = (attendance.totalOvertimeHours != null && !isNaN(parseFloat(attendance.totalOvertimeHours))) 
                    ? parseFloat(attendance.totalOvertimeHours) : 0;
                const unpaidLeaveAmount = (attendance.calculatedUnpaidLeaveAmount != null && !isNaN(parseFloat(attendance.calculatedUnpaidLeaveAmount))) 
                    ? parseFloat(attendance.calculatedUnpaidLeaveAmount) : 0;
                const latePenalty = (attendance.calculatedLatePenalty != null && !isNaN(parseFloat(attendance.calculatedLatePenalty))) 
                    ? parseFloat(attendance.calculatedLatePenalty) : 0;
                const overtimeAmount = (attendance.calculatedOvertimeAmount != null && !isNaN(parseFloat(attendance.calculatedOvertimeAmount))) 
                    ? parseFloat(attendance.calculatedOvertimeAmount) : 0;
                
                console.log('Parsed values:', {
                    actualWorkingDays, paidLeaveDays, unpaidLeaveDays, 
                    totalOvertimeHours, unpaidLeaveAmount, latePenalty, overtimeAmount,
                    'raw actualWorkingDays': attendance.actualWorkingDays,
                    'raw paidLeaveDays': attendance.paidLeaveDays
                });
                
                // Update display
                const workDaysEl = document.getElementById('attWorkDays');
                const paidLeaveInfoEl = document.getElementById('attPaidLeaveInfo');
                const unpaidLeaveEl = document.getElementById('attUnpaidLeave');
                const unpaidLeaveAmtEl = document.getElementById('attUnpaidLeaveAmt');
                const lateCountEl = document.getElementById('attLateCount');
                const latePenaltyEl = document.getElementById('attLatePenalty');
                const earlyLeaveEl = document.getElementById('attEarlyLeave');
                const overtimeHoursEl = document.getElementById('attOvertimeHours');
                const overtimeAmtEl = document.getElementById('attOvertimeAmt');
                const otHoursEl = document.getElementById('summaryOTHours');
                
                // Update work days display with actual working days and paid leave days
                try {
                    if (workDaysEl) {
                        // Ensure values are numbers - handle all edge cases
                        let workDaysNum = 0;
                        try {
                            if (actualWorkingDays != null && actualWorkingDays !== undefined) {
                                workDaysNum = parseFloat(actualWorkingDays);
                                if (isNaN(workDaysNum)) workDaysNum = 0;
                            }
                        } catch (e) {
                            console.error('Error parsing actualWorkingDays:', e);
                            workDaysNum = 0;
                        }
                        
                        let paidLeaveNum = 0;
                        try {
                            if (paidLeaveDays != null && paidLeaveDays !== undefined) {
                                paidLeaveNum = parseFloat(paidLeaveDays);
                                if (isNaN(paidLeaveNum)) paidLeaveNum = 0;
                            }
                        } catch (e) {
                            console.error('Error parsing paidLeaveDays:', e);
                            paidLeaveNum = 0;
                        }
                        
                        const workDaysText = workDaysNum.toFixed(1) + ' ngày';
                        const paidLeaveText = paidLeaveNum > 0 ? ' (' + paidLeaveNum.toFixed(1) + ' paid leave)' : '';
                        workDaysEl.textContent = workDaysText + paidLeaveText;
                        console.log('Updated workDaysEl:', workDaysEl.textContent, 'actualWorkingDays:', workDaysNum, 'paidLeaveDays:', paidLeaveNum);
                    } else {
                        console.warn('workDaysEl not found');
                    }
                } catch (e) {
                    console.error('Error updating workDaysEl:', e);
                }
                
                // Update paid leave info display
                try {
                    if (paidLeaveInfoEl) {
                        let paidLeaveNum = 0;
                        try {
                            if (paidLeaveDays != null && paidLeaveDays !== undefined) {
                                paidLeaveNum = parseFloat(paidLeaveDays);
                                if (isNaN(paidLeaveNum)) paidLeaveNum = 0;
                            }
                        } catch (e) {
                            console.error('Error parsing paidLeaveDays for display:', e);
                            paidLeaveNum = 0;
                        }
                        paidLeaveInfoEl.textContent = paidLeaveNum.toFixed(1) + ' ngày nghỉ có lương';
                        console.log('Updated paidLeaveInfoEl:', paidLeaveInfoEl.textContent, 'paidLeaveDays:', paidLeaveNum);
                    } else {
                        console.warn('paidLeaveInfoEl not found');
                    }
                } catch (e) {
                    console.error('Error updating paidLeaveInfoEl:', e);
                }
                if (unpaidLeaveEl) unpaidLeaveEl.textContent = `${unpaidLeaveDays.toFixed(1)} ngày`;
                if (unpaidLeaveAmtEl) unpaidLeaveAmtEl.textContent = formatCurrency(unpaidLeaveAmount);
                if (lateCountEl) lateCountEl.textContent = `${lateCount} lần`;
                if (latePenaltyEl) latePenaltyEl.textContent = formatCurrency(latePenalty);
                if (earlyLeaveEl) earlyLeaveEl.textContent = `${earlyLeaveCount} lần`;
                if (overtimeHoursEl) overtimeHoursEl.textContent = `${totalOvertimeHours.toFixed(1)}h`;
                if (overtimeAmtEl) overtimeAmtEl.textContent = formatCurrency(overtimeAmount);
                if (otHoursEl) otHoursEl.textContent = `${totalOvertimeHours.toFixed(1)} giờ`;
                
                // Update actual base salary description
                const actualBaseSalaryDescEl = document.getElementById('summaryActualBaseSalaryDesc');
                if (actualBaseSalaryDescEl) {
                    actualBaseSalaryDescEl.textContent = `${actualWorkingDays.toFixed(1)} công + ${paidLeaveDays.toFixed(1)} ngày nghỉ có lương`;
                }
                
                // Show attendance section if there's any data
                const section = document.getElementById('attendanceInfoSection');
                if (section) {
                    section.style.display = 'block'; // Always show to display calculation details
                }
                
                // Store attendance data for later use
                window.currentAttendanceData = {
                    actualWorkingDays: actualWorkingDays,
                    paidLeaveDays: paidLeaveDays,
                    unpaidLeaveDays: unpaidLeaveDays,
                    unpaidLeaveAmount: unpaidLeaveAmount,
                    latePenalty: latePenalty,
                    overtimeAmount: overtimeAmount,
                    totalOvertimeHours: totalOvertimeHours,
                    month: document.getElementById('payrollPeriod').value,
                    employeeId: document.getElementById('payrollEmployee').value
                };
            }
            
            function updatePayrollSummary(data) {
                if (!data) {
                    console.warn('updatePayrollSummary: data is null or undefined');
                    return;
                }
                
                const baseSalary = data.baseSalary || 0;
                const actualBaseSalary = data.actualBaseSalary || 0;
                const otSalary = data.otSalary || 0;
                const totalAllowance = data.totalAllowance || 0;
                const totalDeduction = data.totalDeduction || 0;
                const netSalary = data.netSalary || 0;
                const attendance = data.attendance || window.currentAttendanceData;
                
                console.log('updatePayrollSummary called with:', {baseSalary, actualBaseSalary, otSalary, totalAllowance, totalDeduction, netSalary}); // Debug log
                
                const baseSalaryEl = document.getElementById('summaryBaseSalary');
                const actualBaseSalaryEl = document.getElementById('summaryActualBaseSalary');
                const otSalaryEl = document.getElementById('summaryOTSalary');
                const allowanceEl = document.getElementById('summaryAllowance');
                const deductionEl = document.getElementById('summaryDeduction');
                const netSalaryEl = document.getElementById('summaryNetSalary');
                const summaryEl = document.getElementById('payrollSummary');
                const netSalaryCardEl = document.getElementById('summaryNetSalaryCard');
                const otHoursEl = document.getElementById('summaryOTHours');
                
                if (baseSalaryEl) {
                    baseSalaryEl.textContent = formatCurrency(baseSalary);
                }
                
                if (actualBaseSalaryEl) {
                    actualBaseSalaryEl.textContent = formatCurrency(actualBaseSalary);
                }
                
                if (otSalaryEl) {
                    otSalaryEl.textContent = formatCurrency(otSalary);
                }
                
                if (allowanceEl) {
                    allowanceEl.textContent = formatCurrency(totalAllowance);
                }
                
                // Update OT hours in summary
                if (otHoursEl) {
                    let otHours = 0;
                    if (attendance && attendance.totalOvertimeHours !== undefined) {
                        otHours = parseFloat(attendance.totalOvertimeHours) || 0;
                    } else if (window.currentAttendanceData && window.currentAttendanceData.totalOvertimeHours !== undefined) {
                        otHours = parseFloat(window.currentAttendanceData.totalOvertimeHours) || 0;
                    }
                    otHoursEl.textContent = otHours.toFixed(1) + ' giờ';
                }
                
                if (deductionEl) {
                    deductionEl.textContent = formatCurrency(totalDeduction);
                }
                
                if (netSalaryEl) {
                    netSalaryEl.textContent = formatCurrency(netSalary);
                }
                
                if (summaryEl) {
                    summaryEl.style.display = 'grid';
                }
                
                if (netSalaryCardEl) {
                    netSalaryCardEl.style.display = 'block';
                }
            }

            function formatCurrency(amount) {
                return new Intl.NumberFormat('vi-VN', {
                    style: 'currency',
                    currency: 'VND'
                }).format(amount);
            }

            document.addEventListener('DOMContentLoaded', function () {
                const now = new Date();
                const year = now.getFullYear();
                const month = String(now.getMonth() + 1).padStart(2, '0');
                // Use string concatenation to avoid JSP template literal parsing issues
                const currentMonth = year + '-' + month;
                
                console.log('DOMContentLoaded: year:', year, 'month:', month, 'currentMonth:', currentMonth);

                        // Set default month for all month inputs (but preserve URL parameter values)
                        ['allowanceMonthFilter', 'deductionMonthFilter', 'attendanceMonthFilter', 'allowanceMonth', 'deductionMonth', 'payrollPeriod'].forEach(id => {
                            const elem = document.getElementById(id);
                            if (elem) {
                                const currentValue = elem.value || '';
                                // Only set default if empty or invalid, AND not attendanceMonthFilter (which should preserve URL value)
                                if (id === 'attendanceMonthFilter') {
                                    // For attendanceMonthFilter, only set default if truly empty (not from URL)
                                    // The value should already be set from JSP if it exists in URL
                                    if (!currentValue || currentValue === '-' || currentValue === '') {
                                        if (currentMonth && currentMonth.match(/^\d{4}-\d{2}$/)) {
                                            elem.value = currentMonth;
                                            console.log('Set default month for', id, 'to:', currentMonth);
                                        }
                                    } else {
                                        console.log('Preserving attendanceMonthFilter value from URL:', currentValue);
                                    }
                                } else {
                                    // For other inputs, set default if empty or invalid
                                    if (currentMonth && currentMonth.match(/^\d{4}-\d{2}$/)) {
                                        if (!currentValue || currentValue === '-' || currentValue === '' || !currentValue.match(/^\d{4}-\d{2}$/)) {
                                            elem.value = currentMonth;
                                            console.log('Set default month for', id, 'to:', currentMonth);
                                        }
                                    } else {
                                        console.error('Invalid currentMonth format:', currentMonth);
                                    }
                                }
                            }
                        });

                        // Tab is now handled by backend via URL parameter and JSP rendering
                        // Load attendance data if on attendance tab and filters are set
                        const urlParams = new URLSearchParams(window.location.search);
                        const currentTab = urlParams.get('tab');
                        if (currentTab === 'attendance') {
                            // Wait a bit for DOM to be fully ready
                            setTimeout(() => {
                                const employeeId = document.getElementById('attendanceEmployeeFilter')?.value;
                                const monthInput = document.getElementById('attendanceMonthFilter');
                                
                                // Ensure month input has valid value
                                if (monthInput) {
                                    let month = monthInput.value || '';
                                    
                                    // Get month from URL parameter if input is empty
                                    const urlMonth = urlParams.get('attendanceMonth');
                                    if ((!month || month === '-' || month === '') && urlMonth) {
                                        month = urlMonth;
                                        monthInput.value = month;
                                        console.log('Set month from URL parameter:', month);
                                    }
                                    
                                    // Fix invalid month format only if still invalid after checking URL
                                    if (!month || month === '-' || month === '' || !month.match(/^\d{4}-\d{2}$/)) {
                                        const now = new Date();
                                        const year = now.getFullYear();
                                        const monthNum = String(now.getMonth() + 1).padStart(2, '0');
                                        // Use string concatenation to avoid JSP template literal parsing issues
                                        month = year + '-' + monthNum;
                                        
                                        // Validate before setting
                                        if (month && month.match(/^\d{4}-\d{2}$/)) {
                                            monthInput.value = month;
                                            console.log('Fixed month format to:', month);
                                        } else {
                                            console.error('Failed to generate valid month format. year:', year, 'monthNum:', monthNum, 'result:', month);
                                            return;
                                        }
                                    }
                                    
                                    // Load data if employee is selected, otherwise reset to 0
                                    if (employeeId && month && month.match(/^\d{4}-\d{2}$/)) {
                                        console.log('Loading attendance data for employee:', employeeId, 'month:', month);
                                        loadAttendanceData();
                                    } else {
                                        console.log('No employee or month selected, resetting to default values');
                                        resetAttendanceStats();
                                    }
                                } else {
                                    console.error('attendanceMonthFilter element not found');
                                }
                            }, 300);
                        }

                    document.addEventListener('click', function (event) {
                        ['allowanceModal', 'deductionModal', 'payrollModal', 'payrollDetailsModal'].forEach(modalId => {
                            const modal = document.getElementById(modalId);
                            if (modal && event.target === modal) {
                                modal.classList.remove('active');
                            }
                        });
                    });
            });

            // ========== Manual Edit Functions ==========
            
            let isEditing = false;
            
            function toggleManualEdit() {
                isEditing = !isEditing;
                // Net Salary is always readonly, only allow editing other fields
                const inputs = ['payrollActualBaseSalary', 'payrollOTSalary', 'payrollAllowance', 'payrollDeduction'];
                const btn = document.getElementById('toggleEditBtn');
                
                inputs.forEach(id => {
                    const input = document.getElementById(id);
                    if (input) {
                        if (isEditing) {
                            input.removeAttribute('readonly');
                            input.style.background = 'white';
                            input.style.cursor = 'text';
                            // Add event listeners for auto-calculation
                            input.addEventListener('input', updateNetSalaryAuto);
                            input.addEventListener('change', updateNetSalaryAuto);
                            if (btn) btn.innerHTML = '<i class="fas fa-lock"></i> Khóa giá trị';
                            document.getElementById('manualEditWarning').style.display = 'block';
                        } else {
                            input.setAttribute('readonly', 'readonly');
                            input.style.background = '#f5f5f5';
                            input.style.cursor = 'not-allowed';
                            // Remove event listeners
                            input.removeEventListener('input', updateNetSalaryAuto);
                            input.removeEventListener('change', updateNetSalaryAuto);
                            if (btn) btn.innerHTML = '<i class="fas fa-edit"></i> Sửa giá trị';
                            document.getElementById('manualEditWarning').style.display = 'none';
                        }
                    }
                });
                
                // Ensure Net Salary is always readonly
                const netSalaryInput = document.getElementById('payrollNetSalary');
                if (netSalaryInput) {
                    netSalaryInput.setAttribute('readonly', 'readonly');
                    netSalaryInput.style.background = '#f5f5f5';
                    netSalaryInput.style.cursor = 'not-allowed';
                }
                
                // Auto-calculate Net Salary when toggling
                updateNetSalaryAuto();
            }
            
            // Auto-calculate Net Salary when any component changes
            function updateNetSalaryAuto() {
                const actualBaseSalary = parseFloat(document.getElementById('payrollActualBaseSalary').value) || 0;
                const otSalary = parseFloat(document.getElementById('payrollOTSalary').value) || 0;
                const allowance = parseFloat(document.getElementById('payrollAllowance').value) || 0;
                const deduction = parseFloat(document.getElementById('payrollDeduction').value) || 0;
                
                // Calculate Net Salary: ActualBaseSalary + OTSalary + Allowance - Deduction
                const netSalary = Math.max(0, actualBaseSalary + otSalary + allowance - deduction);
                
                const netSalaryInput = document.getElementById('payrollNetSalary');
                if (netSalaryInput) {
                    netSalaryInput.value = netSalary.toFixed(2);
                }
                
                // Update summary if exists
                if (typeof updatePayrollSummary === 'function') {
                    updatePayrollSummary({
                        baseSalary: parseFloat(document.getElementById('payrollBaseSalary').value) || 0,
                        actualBaseSalary: actualBaseSalary,
                        otSalary: otSalary,
                        totalAllowance: allowance,
                        totalDeduction: deduction,
                        netSalary: netSalary
                    });
                }
            }
            
            function validateNetSalary() {
                // Net Salary is now always calculated automatically, no validation needed
                // Just hide any warnings
                const warningEl = document.getElementById('netSalaryWarning');
                const formulaEl = document.getElementById('netSalaryFormula');
                
                if (warningEl) warningEl.style.display = 'none';
                if (formulaEl) {
                    formulaEl.style.color = 'var(--muted)';
                    formulaEl.style.fontWeight = 'normal';
                }
                
                // Always return true since Net Salary is auto-calculated
                return true;
            }
            
            function recalculatePayroll() {
                const employeeId = document.getElementById('payrollEmployee').value;
                const month = document.getElementById('payrollPeriod').value;
                
                if (!employeeId || !month) {
                    alert('Vui lòng chọn nhân viên và kỳ lương trước.');
                    return;
                }
                
                if (confirm('Tính lại toàn bộ giá trị từ dữ liệu gốc? Thao tác này sẽ ghi đè các thay đổi thủ công.')) {
                    loadEmployeePayrollData();
                    if (isEditing) {
                        toggleManualEdit(); // Lock fields after recalculate
                    }
                }
            }
            
            function compareWithCalculated() {
                const actualBaseSalary = parseFloat(document.getElementById('payrollActualBaseSalary').value) || 0;
                const otSalary = parseFloat(document.getElementById('payrollOTSalary').value) || 0;
                const allowance = parseFloat(document.getElementById('payrollAllowance').value) || 0;
                const deduction = parseFloat(document.getElementById('payrollDeduction').value) || 0;
                const netSalary = parseFloat(document.getElementById('payrollNetSalary').value) || 0;
                
                const calcActualBase = parseFloat(document.getElementById('calculatedActualBaseSalary').value) || 0;
                const calcOT = parseFloat(document.getElementById('calculatedOTSalary').value) || 0;
                const calcAllowance = parseFloat(document.getElementById('calculatedAllowance').value) || 0;
                const calcDeduction = parseFloat(document.getElementById('calculatedDeduction').value) || 0;
                const calcNet = parseFloat(document.getElementById('calculatedNetSalary').value) || 0;
                
                const tbody = document.getElementById('comparisonTableBody');
                if (!tbody) return;
                
                const fields = [
                    { name: 'Lương cơ bản thực nhận', manual: actualBaseSalary, calculated: calcActualBase },
                    { name: 'Lương tăng ca', manual: otSalary, calculated: calcOT },
                    { name: 'Tổng phụ cấp', manual: allowance, calculated: calcAllowance },
                    { name: 'Tổng khấu trừ', manual: deduction, calculated: calcDeduction },
                    { name: 'Lương thực nhận', manual: netSalary, calculated: calcNet }
                ];
                
                tbody.innerHTML = '';
                fields.forEach(field => {
                    const diff = field.manual - field.calculated;
                    const hasDiff = Math.abs(diff) > 0.01;
                    const row = document.createElement('tr');
                    row.style.background = hasDiff ? '#fff3cd' : 'white';
                    row.innerHTML = '<td style="padding:8px;">' + field.name + '</td>' +
                        '<td style="padding:8px; text-align:right;">' + formatCurrency(field.calculated) + '</td>' +
                        '<td style="padding:8px; text-align:right;' + (hasDiff ? ' font-weight:bold; color:var(--error);' : '') + '">' + formatCurrency(field.manual) + '</td>' +
                        '<td style="padding:8px; text-align:right;' + (hasDiff ? ' font-weight:bold; color:var(--error);' : '') + '">' + formatCurrency(diff) + '</td>';
                    tbody.appendChild(row);
                });
                
                document.getElementById('comparisonSection').style.display = 'block';
            }
            
            function useCalculatedValues() {
                const calcActualBase = document.getElementById('calculatedActualBaseSalary').value;
                const calcOT = document.getElementById('calculatedOTSalary').value;
                const calcAllowance = document.getElementById('calculatedAllowance').value;
                const calcDeduction = document.getElementById('calculatedDeduction').value;
                const calcNet = document.getElementById('calculatedNetSalary').value;
                
                document.getElementById('payrollActualBaseSalary').value = calcActualBase;
                document.getElementById('payrollOTSalary').value = calcOT;
                document.getElementById('payrollAllowance').value = calcAllowance;
                document.getElementById('payrollDeduction').value = calcDeduction;
                document.getElementById('payrollNetSalary').value = calcNet;
                
                // Update summary
                updatePayrollSummary({
                    baseSalary: parseFloat(document.getElementById('payrollBaseSalary').value) || 0,
                    actualBaseSalary: parseFloat(calcActualBase) || 0,
                    otSalary: parseFloat(calcOT) || 0,
                    totalAllowance: parseFloat(calcAllowance) || 0,
                    totalDeduction: parseFloat(calcDeduction) || 0,
                    netSalary: parseFloat(calcNet) || 0
                });
                
                validateNetSalary();
                hideComparison();
            }
            
            function hideComparison() {
                document.getElementById('comparisonSection').style.display = 'none';
            }
            
            function toggleOverrideReason() {
                const checkbox = document.getElementById('manualOverride');
                const reasonGroup = document.getElementById('overrideReasonGroup');
                if (checkbox && reasonGroup) {
                    reasonGroup.style.display = checkbox.checked ? 'block' : 'none';
                    if (!checkbox.checked) {
                        document.getElementById('overrideReason').value = '';
                    }
                }
            }
            
            function clearAllWarnings() {
                ['actualBaseSalaryWarning', 'otSalaryWarning', 'allowanceWarning', 'deductionWarning', 'netSalaryWarning'].forEach(id => {
                    const el = document.getElementById(id);
                    if (el) el.style.display = 'none';
                });
            }
            
            function validatePayrollForm() {
                // Net Salary is now always calculated automatically, no validation needed
                // Just ensure Net Salary is calculated correctly
                if (typeof updateNetSalaryAuto === 'function') {
                    updateNetSalaryAuto();
                }
                
                // Basic validation: ensure required fields are filled
                const employeeId = document.getElementById('payrollEmployee').value;
                const payPeriod = document.getElementById('payrollPeriod').value;
                
                if (!employeeId || !payPeriod) {
                    alert('⚠️ Vui lòng chọn nhân viên và kỳ lương.');
                    return false;
                }
                
                return true;
            }
            
            function isManualEdited() {
                if (!isEditing) return false;
                
                const actualBaseSalary = parseFloat(document.getElementById('payrollActualBaseSalary').value) || 0;
                const otSalary = parseFloat(document.getElementById('payrollOTSalary').value) || 0;
                const allowance = parseFloat(document.getElementById('payrollAllowance').value) || 0;
                const deduction = parseFloat(document.getElementById('payrollDeduction').value) || 0;
                const netSalary = parseFloat(document.getElementById('payrollNetSalary').value) || 0;
                
                const calcActualBase = parseFloat(document.getElementById('calculatedActualBaseSalary').value) || 0;
                const calcOT = parseFloat(document.getElementById('calculatedOTSalary').value) || 0;
                const calcAllowance = parseFloat(document.getElementById('calculatedAllowance').value) || 0;
                const calcDeduction = parseFloat(document.getElementById('calculatedDeduction').value) || 0;
                const calcNet = parseFloat(document.getElementById('calculatedNetSalary').value) || 0;
                
                const tolerance = 0.01;
                return Math.abs(actualBaseSalary - calcActualBase) > tolerance ||
                       Math.abs(otSalary - calcOT) > tolerance ||
                       Math.abs(allowance - calcAllowance) > tolerance ||
                       Math.abs(deduction - calcDeduction) > tolerance ||
                       Math.abs(netSalary - calcNet) > tolerance;
            }
        </script>
    </body>
</html>
