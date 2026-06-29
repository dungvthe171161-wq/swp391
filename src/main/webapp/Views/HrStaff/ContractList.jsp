<%-- 
    Document   : ContractList
    Created on : Nov 5, 2025, 12:20:42 AM
    Author     : admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, java.sql.Date, com.hrm.model.entity.Contract, com.hrm.model.entity.Employee" %>
<%!
    private String contractStatusLabel(String status) {
        if ("Draft".equals(status)) return "Bản nháp";
        if ("Pending_Approval".equals(status)) return "Chờ phê duyệt";
        if ("Active".equals(status)) return "Đang hiệu lực";
        if ("Rejected".equals(status)) return "Bị từ chối";
        if ("Expired".equals(status)) return "Hết hạn";
        if ("All".equals(status)) return "Tất cả";
        return status != null ? status : "Không có";
    }

    private String contractTypeLabel(String type) {
        if ("Full-time".equals(type)) return "Toàn thời gian";
        if ("Part-time".equals(type)) return "Bán thời gian";
        if ("Probation".equals(type)) return "Thử việc";
        if ("Intern".equals(type)) return "Thực tập";
        if ("Contract".equals(type)) return "Theo hợp đồng";
        if ("All".equals(type)) return "Tất cả";
        return type != null ? type : "Không có";
    }
%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Danh sách hợp đồng - Nhân viên nhân sự</title>
        <link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css"/>
        <style>
            :root {
                --primary: #5b5bd6;
                --secondary: #8b5bd6;
                --bg: #f3f4f6;
                --card: #ffffff;
                --border: #e5e7eb;
                --text: #111827;
                --muted: #6b7280;
                --success: #10b981;
                --error: #ef4444;
                --warning: #f59e0b;
            }

            body {
                background: var(--bg);
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                margin: 0;
                padding: 0;
            }

            .topbar {
                height: 64px;
                display: flex;
                align-items: center;
                justify-content: space-between;
                padding: 0 20px;
                background: linear-gradient(90deg, var(--primary), var(--secondary));
                color: #fff;
                box-shadow: 0 2px 6px rgba(0,0,0,0.08);
            }

            .brand {
                display: flex;
                align-items: center;
                gap: 10px;
                font-weight: 700;
                font-size: 22px;
            }

            .brand .logo {
                width: 28px;
                height: 28px;
                background: #fff;
                color: var(--primary);
                border-radius: 8px;
                display: flex;
                align-items: center;
                justify-content: center;
                font-weight: 800;
            }

            .top-actions {
                display: flex;
                align-items: center;
                gap: 12px;
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

            .btn.secondary {
                background: var(--success);
            }

            .btn.secondary:hover {
                background: #059669;
            }

            .btn-small {
                padding: 6px 12px;
                font-size: 12px;
            }

            .container {
                max-width: 1400px;
                margin: 30px auto;
                padding: 0 20px;
            }

            .card {
                background: var(--card);
                border: 1px solid var(--border);
                border-radius: 12px;
                padding: 24px;
                box-shadow: 0 1px 3px rgba(0,0,0,0.1);
                margin-bottom: 20px;
            }

            .card-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 24px;
                padding-bottom: 16px;
                border-bottom: 2px solid var(--border);
            }

            .card-header h2 {
                margin: 0;
                color: var(--text);
                font-size: 24px;
            }

            .alert {
                padding: 12px 16px;
                border-radius: 8px;
                margin-bottom: 20px;
                font-size: 14px;
                transition: opacity 0.3s ease-out, transform 0.3s ease-out;
                animation: slideIn 0.3s ease-out;
            }

            .alert.error {
                background: #fee2e2;
                color: #991b1b;
                border: 1px solid #fecaca;
            }

            .alert.success {
                background: #d1fae5;
                color: #065f46;
                border: 1px solid #a7f3d0;
            }

            @keyframes slideIn {
                from {
                    opacity: 0;
                    transform: translateY(-10px);
                }
                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }

            .filters {
                display: grid;
                grid-template-columns: 2fr 1fr 1fr auto;
                gap: 12px;
                margin-bottom: 20px;
            }

            .filter-group {
                display: flex;
                flex-direction: column;
            }

            .filter-group label {
                font-size: 12px;
                color: var(--muted);
                margin-bottom: 4px;
                font-weight: 500;
            }

            .filter-group input,
            .filter-group select {
                padding: 8px 12px;
                border: 1px solid var(--border);
                border-radius: 6px;
                font-size: 14px;
            }

            .filter-group input:focus,
            .filter-group select:focus {
                outline: none;
                border-color: var(--primary);
            }

            .table-container {
                overflow-x: auto;
            }

            table {
                width: 100%;
                border-collapse: collapse;
            }

            thead {
                background: #f9fafb;
            }

            th, td {
                padding: 12px;
                text-align: left;
                border-bottom: 1px solid var(--border);
            }

            th {
                font-weight: 600;
                color: var(--text);
                font-size: 13px;
                text-transform: uppercase;
                letter-spacing: 0.5px;
            }

            th.sortable {
                cursor: pointer;
                user-select: none;
                position: relative;
                padding-right: 20px;
            }

            th.sortable:hover {
                background: #f3f4f6;
            }

            th.sortable::after {
                content: '';
                position: absolute;
                right: 8px;
                top: 50%;
                transform: translateY(-50%);
                width: 0;
                height: 0;
                border-left: 4px solid transparent;
                border-right: 4px solid transparent;
            }

            th.sortable.sort-asc::after {
                border-bottom: 6px solid var(--text);
                border-top: none;
            }

            th.sortable.sort-desc::after {
                border-top: 6px solid var(--text);
                border-bottom: none;
            }

            th.sortable.sort-none::after {
                border-top: 4px solid var(--muted);
                border-bottom: 4px solid var(--muted);
                opacity: 0.3;
            }

            td {
                font-size: 14px;
                color: var(--text);
            }

            tbody tr:hover {
                background: #f9fafb;
            }

            .status-badge {
                display: inline-block;
                padding: 4px 10px;
                border-radius: 12px;
                font-size: 11px;
                font-weight: 600;
                text-transform: uppercase;
            }

            .status-Draft {
                background: #fef3c7;
                color: #92400e;
            }

            .status-Pending_Approval {
                background: #dbeafe;
                color: #1e40af;
            }

            .status-Approved {
                background: #d1fae5;
                color: #065f46;
            }

            .status-Rejected {
                background: #fee2e2;
                color: #991b1b;
            }

            .status-Active {
                background: #d1fae5;
                color: #065f46;
            }

            .status-Expired {
                background: #f3f4f6;
                color: #4b5563;
            }

            .actions {
                display: flex;
                gap: 8px;
            }

            .btn-edit {
                background: var(--warning);
                padding: 6px 12px;
                font-size: 12px;
            }

            .btn-edit:hover {
                background: #d97706;
            }

            .btn-delete {
                background: var(--error);
                padding: 6px 12px;
                font-size: 12px;
            }

            .btn-delete:hover {
                background: #dc2626;
            }

            .empty-state {
                text-align: center;
                padding: 40px 20px;
                color: var(--muted);
            }

            .empty-state-icon {
                font-size: 48px;
                margin-bottom: 16px;
                opacity: 0.5;
            }

            /* Edit Modal */
            .modal {
                display: none;
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: rgba(0,0,0,0.5);
                z-index: 1000;
                overflow-y: auto;
            }

            .modal.active {
                display: flex;
                align-items: center;
                justify-content: center;
                padding: 20px;
            }

            .modal-content {
                background: var(--card);
                border-radius: 12px;
                padding: 24px;
                max-width: 700px;
                width: 100%;
                max-height: 90vh;
                overflow-y: auto;
            }

            .modal-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 24px;
                padding-bottom: 16px;
                border-bottom: 2px solid var(--border);
            }

            .modal-header h3 {
                margin: 0;
                font-size: 20px;
            }

            .close-btn {
                background: none;
                border: none;
                font-size: 24px;
                cursor: pointer;
                color: var(--muted);
                padding: 0;
                width: 32px;
                height: 32px;
                display: flex;
                align-items: center;
                justify-content: center;
                border-radius: 6px;
            }

            .close-btn:hover {
                background: var(--bg);
            }

            .form-group {
                margin-bottom: 20px;
            }

            .form-group label {
                display: block;
                margin-bottom: 6px;
                color: var(--text);
                font-weight: 500;
                font-size: 14px;
            }

            .form-group label .required {
                color: var(--error);
                margin-left: 2px;
            }

            .form-group input,
            .form-group select,
            .form-group textarea {
                width: 100%;
                padding: 10px 12px;
                border: 1px solid var(--border);
                border-radius: 8px;
                font-size: 14px;
                transition: border-color 0.2s;
                box-sizing: border-box;
            }

            .form-group textarea {
                resize: none;
                min-height: 80px;
                max-height: 120px;
                overflow-y: auto;
            }

            .form-group input:focus,
            .form-group select:focus,
            .form-group textarea:focus {
                outline: none;
                border-color: var(--primary);
                box-shadow: 0 0 0 3px rgba(91, 91, 214, 0.1);
            }

            .form-row {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 16px;
            }

            .form-actions {
                display: flex;
                gap: 12px;
                justify-content: flex-end;
                margin-top: 24px;
                padding-top: 20px;
                border-top: 1px solid var(--border);
            }

            .btn-cancel {
                background: #6b7280;
            }

            .btn-cancel:hover {
                background: #4b5563;
            }

            @media (max-width: 768px) {
                .filters {
                    grid-template-columns: 1fr;
                }
                .form-row {
                    grid-template-columns: 1fr;
                }
                .table-container {
                    overflow-x: scroll;
                }
            }
        </style>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/hr-theme.css?v=hr-staff-shell-20260627-1">
    </head>
    <body class="hr-staff-page-shell">
        <%
            request.setAttribute("hrStaffSidebarActive", "contracts");
            request.setAttribute("hrStaffPageTitle", "Danh sách hợp đồng");
            request.setAttribute("hrStaffSearchPlaceholder", "Tìm kiếm hợp đồng, nhân viên...");
            request.setAttribute("hrStaffProfileSubtitle", "Quản trị hợp đồng");
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
                <div>Danh sách hợp đồng</div>
            </div>
            <div class="top-actions">
                <a class="btn secondary" href="<%=request.getContextPath()%>/hrstaff/contracts/create">+ Tạo mới</a>
                <a class="btn" href="<%=request.getContextPath()%>/hrstaff">← Quay lại</a>
            </div>
        </div>

        <div class="container">
            <!-- Thông báo -->
            <% if (request.getParameter("success") != null) { %>
            <div class="alert success" id="successAlert">
                Cập nhật hợp đồng thành công!
            </div>
            <% } %>
            <% if (request.getAttribute("error") != null) { %>
            <div class="alert error" id="errorAlert">
                <%= request.getAttribute("error") %>
            </div>
            <% } %>
            <% if (request.getAttribute("success") != null) { %>
            <div class="alert success" id="successAlertAttr">
                <%= request.getAttribute("success") %>
            </div>
            <% } %>
            <% if (request.getParameter("deleteSuccess") != null) { %>
            <div class="alert success" id="deleteSuccessAlert">
                Xóa hợp đồng thành công!
            </div>
            <% } %>

            <!-- Bộ lọc -->
            <div class="card">
                <form method="GET" action="<%=request.getContextPath()%>/hrstaff/contracts">
                    <div class="filters">
                        <div class="filter-group">
                            <label>Tìm kiếm</label>
                            <input 
                                type="text" 
                                name="keyword" 
                                placeholder="Tên nhân viên..."
                                value="<%= request.getAttribute("keyword") != null ? request.getAttribute("keyword") : "" %>"
                            />
                        </div>
                        <div class="filter-group">
                            <label>Trạng thái</label>
                            <select name="status">
                                <option value="All" <%= (request.getAttribute("statusFilter") == null || request.getAttribute("statusFilter").equals("All")) ? "selected" : "" %>>Tất cả</option>
                                <option value="Draft" <%= "Draft".equals(request.getAttribute("statusFilter")) ? "selected" : "" %>>Bản nháp</option>
                                <option value="Pending_Approval" <%= "Pending_Approval".equals(request.getAttribute("statusFilter")) ? "selected" : "" %>>Chờ phê duyệt</option>
                             
                                <option value="Active" <%= "Active".equals(request.getAttribute("statusFilter")) ? "selected" : "" %>>Đang hiệu lực</option>
                                <option value="Rejected" <%= "Rejected".equals(request.getAttribute("statusFilter")) ? "selected" : "" %>>Bị từ chối</option>
                                <option value="Expired" <%= "Expired".equals(request.getAttribute("statusFilter")) ? "selected" : "" %>>Hết hạn</option>
                            </select>
                        </div>
                        <div class="filter-group">
                            <label>Loại hợp đồng</label>
                            <select name="contractType">
                                <option value="All" <%= (request.getAttribute("contractTypeFilter") == null || request.getAttribute("contractTypeFilter").equals("All")) ? "selected" : "" %>>Tất cả</option>
                                <option value="Full-time" <%= "Full-time".equals(request.getAttribute("contractTypeFilter")) ? "selected" : "" %>>Toàn thời gian</option>
                                <option value="Part-time" <%= "Part-time".equals(request.getAttribute("contractTypeFilter")) ? "selected" : "" %>>Bán thời gian</option>
                                <option value="Probation" <%= "Probation".equals(request.getAttribute("contractTypeFilter")) ? "selected" : "" %>>Thử việc</option>
                                <option value="Intern" <%= "Intern".equals(request.getAttribute("contractTypeFilter")) ? "selected" : "" %>>Thực tập</option>
                          
                            </select>
                        </div>
                        <div class="filter-group" style="justify-content: flex-end;">
                            <label style="opacity: 0;">Thao tác</label>
                            <div style="display: flex; gap: 8px;">
                                <button type="submit" class="btn btn-small">🔍 Tìm kiếm</button>
                                <a href="<%=request.getContextPath()%>/hrstaff/contracts" class="btn btn-small btn-cancel">Xóa lọc</a>
                            </div>
                        </div>
                    </div>
                    <%-- Hidden inputs to preserve parameters when submitting filter form --%>
                    <input type="hidden" name="sortBy" value="<%= request.getAttribute("sortBy") != null ? request.getAttribute("sortBy") : "" %>" />
                    <input type="hidden" name="page" value="1" />
                </form>
            </div>

            <!-- Bảng hợp đồng -->
            <div class="card">
                <div class="card-header">
                    <h2>📄 Danh sách hợp đồng</h2>
                </div>

                <div class="table-container">
                    <%
                        List<Map<String, Object>> contracts = (List<Map<String, Object>>) request.getAttribute("contracts");
                        if (contracts == null || contracts.isEmpty()) {
                    %>
                    <div class="empty-state">
                        <div class="empty-state-icon">📋</div>
                        <h3>Chưa có hợp đồng</h3>
                        <p>Không tìm thấy hợp đồng trong hệ thống hoặc không có kết quả phù hợp.</p>
                        <a href="<%=request.getContextPath()%>/hrstaff/contracts/create" class="btn secondary" style="margin-top: 16px;">Tạo hợp đồng mới</a>
                    </div>
                    <%
                        } else {
                    %>
                    <table>
                        <thead>
                            <tr>
                                <%
                                    Object sortByObj = request.getAttribute("sortBy");
                                    String sortClass = "sort-desc";
                                    if (sortByObj != null) {
                                        String sort = sortByObj.toString();
                                        if ("contractIdAsc".equals(sort)) {
                                            sortClass = "sort-asc";
                                        } else if ("contractIdDesc".equals(sort)) {
                                            sortClass = "sort-desc";
                                        }
                                    }
                                %>
                                <th class="sortable <%= sortClass %>" onclick="toggleSort()">ID</th>
                                <th>Nhân viên</th>
                                <th>Loại hợp đồng</th>
                                <th>Ngày bắt đầu</th>
                                <th>Ngày kết thúc</th>
                                <th>Lương cơ bản</th>
                                <th>Phụ cấp</th>
                                <th>Ghi chú</th>
                                <th>Trạng thái</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                for (Map<String, Object> contract : contracts) {
                                    int contractId = (Integer) contract.get("contractId");
                                    String employeeName = (String) contract.get("employeeName");
                                    String contractType = (String) contract.get("contractType");
                                    Date startDate = (Date) contract.get("startDate");
                                    Date endDate = (Date) contract.get("endDate");
                                    java.math.BigDecimal baseSalary = (java.math.BigDecimal) contract.get("baseSalary");
                                    java.math.BigDecimal allowance = (java.math.BigDecimal) contract.get("allowance");
                                    String status = (String) contract.get("status");
                                    String note = (String) contract.get("note");
                                    
                                    // Format status
                                    if (status == null) status = "Draft";
                                    
                                    // Check if editable
                                    boolean canEdit = "Draft".equals(status) || "Pending_Approval".equals(status) || "Active".equals(status);
                                    
                                    // Check if deletable (only Draft, Rejected, or Expired)
                                    boolean canDelete = "Draft".equals(status) || "Rejected".equals(status) || "Expired".equals(status);
                            %>
                            <tr>
                                <td><strong>#<%= contractId %></strong></td>
                                <td><%= employeeName != null ? employeeName : "Không có" %></td>
                                <td><%= contractTypeLabel(contractType) %></td>
                                <td><%= startDate != null ? startDate.toString() : "Không có" %></td>
                                <td><%= endDate != null ? endDate.toString() : "Chưa xác định" %></td>
                                <td><%= baseSalary != null ? String.format("%,d VNĐ", baseSalary.intValue()) : "Không có" %></td>
                                <td><%= allowance != null ? String.format("%,d VNĐ", allowance.intValue()) : "0 VNĐ" %></td>
                                <td style="max-width: 200px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;" title="<%= note != null && !note.trim().isEmpty() ? note : "Không có ghi chú" %>">
                                    <%= note != null && !note.trim().isEmpty() ? (note.length() > 30 ? note.substring(0, 30) + "..." : note) : "<span style='color: var(--muted);'>-</span>" %>
                                </td>
                                <td>
                                    <span class="status-badge status-<%= status %>">
                                        <%= contractStatusLabel(status) %>
                                    </span>
                                </td>
                                <td>
                                    <div class="actions">
                                        <% if (canEdit) { %>
                                        <button 
                                            class="btn btn-edit btn-small" 
                                            onclick="openEditModal(<%= contractId %>)"
                                        >
                                            ✏️ Sửa
                                        </button>
                                        <% } else { %>
                                        <span style="color: var(--muted); font-size: 12px;">Không thể sửa</span>
                                        <% } %>
                                        <% if (canDelete) { %>
                                        <button 
                                            class="btn btn-delete btn-small" 
                                            onclick="confirmDelete(<%= contractId %>, '<%= employeeName != null ? employeeName : "Không có" %>')"
                                        >
                                            🗑️ Xóa
                                        </button>
                                        <% } %>
                                    </div>
                                </td>
                            </tr>
                            <%
                                }
                            %>
                        </tbody>
                    </table>
                    <%
                        }
                    %>
                </div>
                
                <!-- Pagination -->
                <%
                    Integer currentPage = (Integer) request.getAttribute("currentPage");
                    Integer totalPages = (Integer) request.getAttribute("totalPages");
                    Integer totalContracts = (Integer) request.getAttribute("totalContracts");
                    String keywordParam = (String) request.getAttribute("keyword");
                    String statusParam = (String) request.getAttribute("statusFilter");
                    String contractTypeParam = (String) request.getAttribute("contractTypeFilter");
                    String sortByParam = (String) request.getAttribute("sortBy");
                    
                    if (currentPage == null) currentPage = 1;
                    if (totalPages == null) totalPages = 1;
                    if (totalContracts == null) totalContracts = 0;
                    
                    if (totalPages > 1) {
                %>
                <div style="margin-top: 24px; padding-top: 20px; border-top: 1px solid var(--border); display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 12px;">
                    <div style="color: var(--muted); font-size: 14px;">
                        Hiển thị <%= (currentPage - 1) * 5 + 1 %> - <%= Math.min(currentPage * 5, totalContracts) %> trong tổng số <%= totalContracts %> hợp đồng
                    </div>
                    <div style="display: flex; gap: 8px; align-items: center;">
                        <%-- Previous button --%>
                        <% if (currentPage > 1) { %>
                            <%
                                int prevPage = currentPage - 1;
                                String prevUrl = request.getContextPath() + "/hrstaff/contracts?page=" + prevPage;
                                if (keywordParam != null && !keywordParam.isEmpty()) {
                                    prevUrl += "&keyword=" + java.net.URLEncoder.encode(keywordParam, "UTF-8");
                                }
                                if (statusParam != null && !statusParam.isEmpty()) {
                                    prevUrl += "&status=" + java.net.URLEncoder.encode(statusParam, "UTF-8");
                                }
                                if (contractTypeParam != null && !contractTypeParam.isEmpty()) {
                                    prevUrl += "&contractType=" + java.net.URLEncoder.encode(contractTypeParam, "UTF-8");
                                }
                                if (sortByParam != null && !sortByParam.isEmpty()) {
                                    prevUrl += "&sortBy=" + java.net.URLEncoder.encode(sortByParam, "UTF-8");
                                }
                            %>
                            <a href="<%= prevUrl %>" class="btn btn-small">← Trước</a>
                        <% } else { %>
                            <span class="btn btn-small" style="opacity: 0.5; cursor: not-allowed; background: #e5e7eb; color: #9ca3af;">← Trước</span>
                        <% } %>
                        
                        <%-- Page numbers --%>
                        <%
                            int startPage = Math.max(1, currentPage - 2);
                            int endPage = Math.min(totalPages, currentPage + 2);
                            
                            if (startPage > 1) {
                                String firstUrl = request.getContextPath() + "/hrstaff/contracts?page=1";
                                if (keywordParam != null && !keywordParam.isEmpty()) {
                                    firstUrl += "&keyword=" + java.net.URLEncoder.encode(keywordParam, "UTF-8");
                                }
                                if (statusParam != null && !statusParam.isEmpty()) {
                                    firstUrl += "&status=" + java.net.URLEncoder.encode(statusParam, "UTF-8");
                                }
                                if (contractTypeParam != null && !contractTypeParam.isEmpty()) {
                                    firstUrl += "&contractType=" + java.net.URLEncoder.encode(contractTypeParam, "UTF-8");
                                }
                                if (sortByParam != null && !sortByParam.isEmpty()) {
                                    firstUrl += "&sortBy=" + java.net.URLEncoder.encode(sortByParam, "UTF-8");
                                }
                        %>
                            <a href="<%= firstUrl %>" class="btn btn-small" style="<%= currentPage == 1 ? "background: var(--primary); color: white;" : "" %>">1</a>
                            <% if (startPage > 2) { %>
                                <span style="color: var(--muted);">...</span>
                            <% } %>
                        <% } %>
                        
                        <% for (int i = startPage; i <= endPage; i++) {
                            String pageUrl = request.getContextPath() + "/hrstaff/contracts?page=" + i;
                            if (keywordParam != null && !keywordParam.isEmpty()) {
                                pageUrl += "&keyword=" + java.net.URLEncoder.encode(keywordParam, "UTF-8");
                            }
                            if (statusParam != null && !statusParam.isEmpty()) {
                                pageUrl += "&status=" + java.net.URLEncoder.encode(statusParam, "UTF-8");
                            }
                            if (contractTypeParam != null && !contractTypeParam.isEmpty()) {
                                pageUrl += "&contractType=" + java.net.URLEncoder.encode(contractTypeParam, "UTF-8");
                            }
                            if (sortByParam != null && !sortByParam.isEmpty()) {
                                pageUrl += "&sortBy=" + java.net.URLEncoder.encode(sortByParam, "UTF-8");
                            }
                        %>
                            <a href="<%= pageUrl %>" class="btn btn-small" style="<%= currentPage == i ? "background: var(--primary); color: white;" : "" %>"><%= i %></a>
                        <% } %>
                        
                        <% if (endPage < totalPages) {
                            String lastUrl = request.getContextPath() + "/hrstaff/contracts?page=" + totalPages;
                            if (keywordParam != null && !keywordParam.isEmpty()) {
                                lastUrl += "&keyword=" + java.net.URLEncoder.encode(keywordParam, "UTF-8");
                            }
                            if (statusParam != null && !statusParam.isEmpty()) {
                                lastUrl += "&status=" + java.net.URLEncoder.encode(statusParam, "UTF-8");
                            }
                            if (contractTypeParam != null && !contractTypeParam.isEmpty()) {
                                lastUrl += "&contractType=" + java.net.URLEncoder.encode(contractTypeParam, "UTF-8");
                            }
                            if (sortByParam != null && !sortByParam.isEmpty()) {
                                lastUrl += "&sortBy=" + java.net.URLEncoder.encode(sortByParam, "UTF-8");
                            }
                        %>
                            <% if (endPage < totalPages - 1) { %>
                                <span style="color: var(--muted);">...</span>
                            <% } %>
                            <a href="<%= lastUrl %>" class="btn btn-small" style="<%= currentPage == totalPages ? "background: var(--primary); color: white;" : "" %>"><%= totalPages %></a>
                        <% } %>
                        
                        <%-- Next button --%>
                        <% if (currentPage < totalPages) { %>
                            <%
                                int nextPage = currentPage + 1;
                                String nextUrl = request.getContextPath() + "/hrstaff/contracts?page=" + nextPage;
                                if (keywordParam != null && !keywordParam.isEmpty()) {
                                    nextUrl += "&keyword=" + java.net.URLEncoder.encode(keywordParam, "UTF-8");
                                }
                                if (statusParam != null && !statusParam.isEmpty()) {
                                    nextUrl += "&status=" + java.net.URLEncoder.encode(statusParam, "UTF-8");
                                }
                                if (contractTypeParam != null && !contractTypeParam.isEmpty()) {
                                    nextUrl += "&contractType=" + java.net.URLEncoder.encode(contractTypeParam, "UTF-8");
                                }
                                if (sortByParam != null && !sortByParam.isEmpty()) {
                                    nextUrl += "&sortBy=" + java.net.URLEncoder.encode(sortByParam, "UTF-8");
                                }
                            %>
                            <a href="<%= nextUrl %>" class="btn btn-small">Sau →</a>
                        <% } else { %>
                            <span class="btn btn-small" style="opacity: 0.5; cursor: not-allowed; background: #e5e7eb; color: #9ca3af;">Sau →</span>
                        <% } %>
                    </div>
                </div>
                <% } %>
            </div>
        </div>

        <!-- Modal chỉnh sửa -->
        <div id="editModal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h3>✏️ Chỉnh sửa hợp đồng</h3>
                    <button class="close-btn" onclick="closeEditModal()">&times;</button>
                </div>

                <%
                    Contract editingContract = (Contract) request.getAttribute("editingContract");
                    List<Employee> employees = (List<Employee>) request.getAttribute("employees");
                    if (editingContract != null) {
                        // Find current employee
                        Employee currentEmployee = null;
                        if (employees != null) {
                            for (Employee emp : employees) {
                                if (emp.getEmployeeId() == editingContract.getEmployeeId()) {
                                    currentEmployee = emp;
                                    break;
                                }
                            }
                        }
                %>
                <form method="POST" action="<%=request.getContextPath()%>/hrstaff/contracts">
                    <input type="hidden" name="contractId" value="<%= editingContract.getContractId() %>">
                    <input type="hidden" name="employeeId" value="<%= editingContract.getEmployeeId() %>">
                    
                    <div class="form-group">
                        <label for="employeeDisplay">
                            Nhân viên <span class="required">*</span>
                        </label>
                        <input 
                            type="text" 
                            id="employeeDisplay" 
                            value="<%= currentEmployee != null ? currentEmployee.getFullName() + " (ID: " + currentEmployee.getEmployeeId() + (currentEmployee.getDepartmentName() != null ? " - " + currentEmployee.getDepartmentName() : "") + ")" : "ID nhân viên: " + editingContract.getEmployeeId() %>"
                            readonly
                            style="background-color: #f3f4f6; cursor: not-allowed;"
                        />
                        <div style="font-size: 12px; color: var(--muted); margin-top: 4px;">
                            ⚠️ Không thể đổi nhân viên khi chỉnh sửa hợp đồng
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="contractType">
                            Loại hợp đồng <span class="required">*</span>
                        </label>
                        <select id="contractType" name="contractType" required>
                            <option value="">-- Chọn loại hợp đồng --</option>
                            <option value="Full-time" <%= "Full-time".equals(editingContract.getContractType()) ? "selected" : "" %>>Toàn thời gian</option>
                            <option value="Part-time" <%= "Part-time".equals(editingContract.getContractType()) ? "selected" : "" %>>Bán thời gian</option>
                            <option value="Probation" <%= "Probation".equals(editingContract.getContractType()) ? "selected" : "" %>>Thử việc</option>
                            <option value="Intern" <%= "Intern".equals(editingContract.getContractType()) ? "selected" : "" %>>Thực tập</option>
                            <option value="Contract" <%= "Contract".equals(editingContract.getContractType()) ? "selected" : "" %>>Theo hợp đồng</option>
                        </select>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label for="startDate">
                                Ngày bắt đầu <span class="required">*</span>
                            </label>
                            <input 
                                type="date" 
                                id="startDate" 
                                name="startDate" 
                                value="<%= editingContract.getStartDate() != null ? editingContract.getStartDate().toString() : "" %>"
                                required
                            />
                        </div>

                        <div class="form-group">
                            <label for="endDate">Ngày kết thúc</label>
                            <input 
                                type="date" 
                                id="endDate" 
                                name="endDate"
                                value="<%= editingContract.getEndDate() != null ? editingContract.getEndDate().toString() : "" %>"
                            />
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label for="baseSalary">
                                Lương cơ bản <span class="required">*</span>
                            </label>
                            <input 
                                type="number" 
                                id="baseSalary" 
                                name="baseSalary" 
                                min="0" 
                                step="1000" 
                                value="<%= editingContract.getBaseSalary() != null ? editingContract.getBaseSalary().intValue() : "" %>"
                                required
                            />
                            <div style="font-size: 12px; color: var(--muted); margin-top: 4px;">
                                ⚠️ Khi đổi lương cơ bản, trạng thái hợp đồng sẽ tự chuyển sang "Chờ phê duyệt"
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="allowance">Phụ cấp</label>
                            <input 
                                type="number" 
                                id="allowance" 
                                name="allowance" 
                                min="0" 
                                step="1000"
                                value="<%= editingContract.getAllowance() != null ? editingContract.getAllowance().intValue() : "0" %>"
                            />
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="note">Ghi chú</label>
                        <textarea 
                            id="note" 
                            name="note" 
                            rows="4"
                            maxlength="1000"
                            placeholder="Nhập ghi chú hợp đồng nếu có..."
                        ><%= editingContract.getNote() != null ? editingContract.getNote() : "" %></textarea>
                        <div style="font-size: 12px; color: var(--muted); margin-top: 4px;">
                            Ghi chú bổ sung cho hợp đồng (không bắt buộc)
                        </div>
                    </div>

                    <div class="form-actions">
                        <button type="button" class="btn btn-cancel" onclick="closeEditModal()">
                            Hủy
                        </button>
                        <button type="submit" class="btn secondary">
                            ✓ Lưu thay đổi
                        </button>
                    </div>
                </form>
                <%
                    }
                %>
            </div>
        </div>

                </section>
            </main>
        </div>

        <script>
            <%
                if (editingContract != null) {
            %>
            // Mở modal khi đang chỉnh sửa
            document.addEventListener('DOMContentLoaded', function() {
                document.getElementById('editModal').classList.add('active');
            });
            <%
                }
            %>

            function openEditModal(contractId) {
                window.location.href = '<%=request.getContextPath()%>/hrstaff/contracts?editId=' + contractId;
            }

            function closeEditModal() {
                document.getElementById('editModal').classList.remove('active');
                window.location.href = '<%=request.getContextPath()%>/hrstaff/contracts';
            }

            function confirmDelete(contractId, employeeName) {
                if (confirm('Bạn có chắc muốn xóa hợp đồng này không?\n\nMã hợp đồng: ' + contractId + '\nNhân viên: ' + employeeName + '\n\nHành động này không thể hoàn tác!')) {
                    window.location.href = '<%=request.getContextPath()%>/hrstaff/contracts?deleteId=' + contractId;
                }
            }

            function toggleSort() {
                const currentSort = '<%= request.getAttribute("sortBy") != null ? request.getAttribute("sortBy") : "" %>';
                const keyword = '<%= request.getAttribute("keyword") != null ? request.getAttribute("keyword") : "" %>';
                const status = '<%= request.getAttribute("statusFilter") != null ? request.getAttribute("statusFilter") : "" %>';
                const contractType = '<%= request.getAttribute("contractTypeFilter") != null ? request.getAttribute("contractTypeFilter") : "" %>';
                const currentPage = '<%= request.getAttribute("currentPage") != null ? request.getAttribute("currentPage") : "1" %>';
                
                let newSort;
                if (currentSort === 'contractIdAsc' || currentSort === '') {
                    newSort = 'contractIdDesc';
                } else {
                    newSort = 'contractIdAsc';
                }
                
                // Build URL with all parameters
                let url = '<%=request.getContextPath()%>/hrstaff/contracts?sortBy=' + newSort + '&page=' + currentPage;
                if (keyword) {
                    url += '&keyword=' + encodeURIComponent(keyword);
                }
                if (status && status !== 'All') {
                    url += '&status=' + encodeURIComponent(status);
                }
                if (contractType && contractType !== 'All') {
                    url += '&contractType=' + encodeURIComponent(contractType);
                }
                
                window.location.href = url;
            }


            // Close modal on outside click
            document.getElementById('editModal')?.addEventListener('click', function(e) {
                if (e.target === this) {
                    closeEditModal();
                }
            });

            // Date validation
            const startDateInput = document.getElementById('startDate');
            const endDateInput = document.getElementById('endDate');
            
            if (startDateInput && endDateInput) {
                startDateInput.addEventListener('change', function() {
                    if (this.value) {
                        endDateInput.setAttribute('min', this.value);
                    }
                });
                
                endDateInput.addEventListener('change', function() {
                    if (startDateInput.value && this.value && this.value < startDateInput.value) {
                        alert('Ngày kết thúc phải sau ngày bắt đầu!');
                        this.value = '';
                    }
                });
            }

            // Auto-hide alerts after 1 second
            document.addEventListener('DOMContentLoaded', function() {
                const alerts = document.querySelectorAll('.alert');
                alerts.forEach(function(alert) {
                    setTimeout(function() {
                        fadeOut(alert);
                    }, 1000);
                });
            });

            function fadeOut(element) {
                if (!element) return;
                element.style.transition = 'opacity 0.3s ease-out, transform 0.3s ease-out';
                element.style.opacity = '0';
                element.style.transform = 'translateY(-10px)';
                setTimeout(function() {
                    element.style.display = 'none';
                }, 300);
            }
        </script>
    </body>
</html>
