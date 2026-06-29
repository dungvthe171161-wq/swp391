<%-- 
    Document   : ApproveRejectContract
    Created on : Nov 7, 2025, 9:18:53 AM
    Author     : admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, java.sql.Date, java.text.SimpleDateFormat, java.math.BigDecimal, java.text.NumberFormat, java.util.Locale" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Duyệt hợp đồng - BetterHR</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/hr-dashboard.css">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
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

            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background-color: var(--light-color);
                color: var(--text-color);
                margin: 0;
                padding: 0;
            }

            .hr-dashboard-container {
                min-height: 100vh;
                background-color: var(--light-color);
            }

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
            }

            .container {
                max-width: 1400px;
                margin: 2rem auto;
                padding: 0 2rem;
            }

            .page-header {
                background: white;
                border-radius: 12px;
                padding: 2rem;
                margin-bottom: 2rem;
                box-shadow: 0 2px 10px rgba(0,0,0,0.05);
            }

            .page-header h2 {
                margin: 0 0 0.5rem 0;
                color: var(--text-color);
                font-size: 1.8rem;
                font-weight: 700;
            }

            .page-header p {
                margin: 0;
                color: var(--text-muted);
                font-size: 1rem;
            }

            .alert {
                padding: 1rem 1.5rem;
                border-radius: 8px;
                margin-bottom: 2rem;
                display: flex;
                align-items: center;
                gap: 1rem;
                animation: slideIn 0.3s ease-out;
                box-shadow: 0 4px 12px rgba(0,0,0,0.1);
                position: relative;
                transition: opacity 0.3s ease-out, transform 0.3s ease-out;
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

            @keyframes slideOut {
                from {
                    opacity: 1;
                    transform: translateY(0);
                }
                to {
                    opacity: 0;
                    transform: translateY(-10px);
                }
            }

            .alert-success {
                background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
                color: #065f46;
                border: 2px solid #10b981;
            }

            .alert-error {
                background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
                color: #991b1b;
                border: 2px solid #ef4444;
            }

            .alert i {
                font-size: 1.5rem;
                flex-shrink: 0;
            }

            .alert span {
                flex: 1;
                font-weight: 500;
                line-height: 1.5;
            }

            .alert-close {
                background: none;
                border: none;
                color: inherit;
                font-size: 1.5rem;
                cursor: pointer;
                padding: 0;
                width: 24px;
                height: 24px;
                display: flex;
                align-items: center;
                justify-content: center;
                opacity: 0.7;
                transition: opacity 0.2s;
            }

            .alert-close:hover {
                opacity: 1;
            }

            .contract-card {
                background: white;
                border-radius: 12px;
                padding: 2rem;
                margin-bottom: 1.5rem;
                box-shadow: 0 2px 10px rgba(0,0,0,0.05);
                border-left: 4px solid var(--warning-color);
                transition: all 0.3s ease;
            }

            .contract-card:hover {
                box-shadow: 0 8px 25px rgba(0,0,0,0.15);
                transform: translateY(-2px);
            }

            .contract-header {
                display: flex;
                justify-content: space-between;
                align-items: flex-start;
                margin-bottom: 1.5rem;
                padding-bottom: 1rem;
                border-bottom: 1px solid #e5e7eb;
            }

            .contract-title {
                flex: 1;
            }

            .contract-title h3 {
                margin: 0 0 0.5rem 0;
                color: var(--text-color);
                font-size: 1.3rem;
                font-weight: 600;
            }

            .contract-title .employee-name {
                color: var(--primary-color);
                font-size: 1.1rem;
                font-weight: 600;
                margin-bottom: 0.25rem;
            }

            .contract-id {
                color: var(--text-muted);
                font-size: 0.9rem;
            }

            .status-badge {
                display: inline-block;
                padding: 0.5rem 1rem;
                border-radius: 20px;
                font-size: 0.85rem;
                font-weight: 600;
                text-transform: uppercase;
                background: #dbeafe;
                color: #1e40af;
            }

            .contract-details {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
                gap: 1.5rem;
                margin-bottom: 1.5rem;
            }

            .detail-item {
                display: flex;
                flex-direction: column;
            }

            .detail-label {
                font-size: 0.85rem;
                color: var(--text-muted);
                margin-bottom: 0.25rem;
                font-weight: 500;
            }

            .detail-value {
                font-size: 1rem;
                color: var(--text-color);
                font-weight: 600;
            }

            .changes-section {
                background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
                border-left: 4px solid var(--primary-color);
                padding: 1.5rem;
                border-radius: 8px;
                margin-bottom: 1.5rem;
                box-shadow: 0 2px 8px rgba(102, 126, 234, 0.1);
            }

            .changes-section h4 {
                margin: 0 0 1rem 0;
                color: var(--primary-color);
                font-size: 1.1rem;
                font-weight: 700;
                display: flex;
                align-items: center;
                gap: 0.5rem;
            }

            .changes-list {
                list-style: none;
                padding: 0;
                margin: 0;
            }

            .changes-list li {
                padding: 0.5rem 0;
                color: var(--text-color);
                font-size: 0.95rem;
            }

            .changes-list li:before {
                content: "▸ ";
                color: var(--primary-color);
                font-weight: bold;
                margin-right: 0.5rem;
            }

            .changes-list li.salary-change-highlight:before {
                display: none;
            }

            .changes-list li {
                line-height: 1.6;
                padding: 0.75rem 0;
                border-bottom: 1px solid rgba(102, 126, 234, 0.1);
            }

            .changes-list li:last-child {
                border-bottom: none;
            }

            .changes-list li strong {
                color: var(--primary-color);
                font-weight: 600;
            }

            /* Highlight salary changes - make them more prominent */
            .changes-list li.salary-change-highlight {
                background: linear-gradient(135deg, #fff5e6 0%, #ffe8cc 100%) !important;
                padding: 1rem !important;
                border-radius: 6px;
                margin: 0.75rem 0;
                border-left: 4px solid #f59e0b !important;
                border-bottom: none !important;
                font-weight: 600;
                color: #92400e;
                box-shadow: 0 2px 8px rgba(245, 158, 11, 0.2);
                font-size: 1.05rem;
                line-height: 1.8;
            }


            .contract-actions {
                display: flex;
                gap: 1rem;
                justify-content: flex-end;
                padding-top: 1rem;
                border-top: 1px solid #e5e7eb;
            }

            .btn {
                padding: 0.75rem 1.5rem;
                border-radius: 8px;
                border: none;
                font-size: 1rem;
                font-weight: 600;
                cursor: pointer;
                transition: all 0.3s ease;
                display: inline-flex;
                align-items: center;
                gap: 0.5rem;
                text-decoration: none;
            }

            .btn-approve {
                background: var(--success-color);
                color: white;
            }

            .btn-approve:hover {
                background: #059669;
                transform: translateY(-2px);
                box-shadow: 0 4px 15px rgba(16, 185, 129, 0.3);
            }

            .btn-reject {
                background: var(--danger-color);
                color: white;
            }

            .btn-reject:hover {
                background: #dc2626;
                transform: translateY(-2px);
                box-shadow: 0 4px 15px rgba(239, 68, 68, 0.3);
            }

            .empty-state {
                text-align: center;
                padding: 4rem 2rem;
                background: white;
                border-radius: 12px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.05);
            }

            .empty-state i {
                font-size: 4rem;
                color: #cbd5e1;
                margin-bottom: 1rem;
            }

            .empty-state h3 {
                color: var(--text-muted);
                margin-bottom: 0.5rem;
                font-size: 1.5rem;
            }

            .empty-state p {
                color: #94a3b8;
                font-size: 1rem;
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
                overflow-y: auto;
            }

            .modal.active {
                display: flex;
                align-items: center;
                justify-content: center;
                padding: 2rem;
            }

            .modal-content {
                background: white;
                border-radius: 12px;
                padding: 2rem;
                max-width: 500px;
                width: 100%;
                box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            }

            .modal-header {
                margin-bottom: 1.5rem;
            }

            .modal-header h3 {
                margin: 0;
                color: var(--text-color);
                font-size: 1.5rem;
                font-weight: 600;
            }

            .modal-body {
                margin-bottom: 1.5rem;
            }

            .form-group {
                margin-bottom: 1.5rem;
            }

            .form-group label {
                display: block;
                margin-bottom: 0.5rem;
                color: var(--text-color);
                font-weight: 500;
            }

            .form-group textarea {
                width: 100%;
                padding: 0.75rem;
                border: 1px solid #e5e7eb;
                border-radius: 8px;
                font-size: 1rem;
                font-family: inherit;
                resize: vertical;
                min-height: 100px;
            }

            .form-group textarea:focus {
                outline: none;
                border-color: var(--primary-color);
                box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
            }

            .modal-footer {
                display: flex;
                gap: 1rem;
                justify-content: flex-end;
            }

            .btn-cancel {
                background: #e5e7eb;
                color: var(--text-color);
            }

            .btn-cancel:hover {
                background: #d1d5db;
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
                        <i class="fas fa-file-contract"></i>
                        <h1>Duyệt hợp đồng</h1>
                    </div>
                    <div class="header-actions">
                        <a href="${pageContext.request.contextPath}/HrHomeController" class="btn-homepage">
                            <i class="fas fa-arrow-left"></i>
                            <span>Quay lại HR Home</span>
                        </a>
                    </div>
                </div>
            </header>

            <!-- Main Content -->
            <main class="hr-main-content">
                <c:set var="hrSidebarActive" value="contracts" />
                <%@ include file="_HrManagerSidebar.jspf" %>

                <div class="hr-content-area">
            <div class="container">
                <!-- Page Header -->
                <div class="page-header">
                    <h2><i class="fas fa-clipboard-check"></i> Hợp đồng chờ duyệt</h2>
                    <p>Rà soát, phê duyệt hoặc từ chối các hợp đồng đang chờ xử lý</p>
                </div>

                <!-- Success/Error Messages -->
                <c:if test="${not empty success}">
                    <div class="alert alert-success" id="successAlert">
                        <i class="fas fa-check-circle"></i>
                        <span>${success}</span>
                        <button type="button" class="alert-close" onclick="closeAlert('successAlert')" aria-label="Close">
                            <i class="fas fa-times"></i>
                        </button>
                    </div>
                </c:if>

                <c:if test="${not empty error}">
                    <div class="alert alert-error" id="errorAlert">
                        <i class="fas fa-exclamation-circle"></i>
                        <span>${error}</span>
                        <button type="button" class="alert-close" onclick="closeAlert('errorAlert')" aria-label="Close">
                            <i class="fas fa-times"></i>
                        </button>
                    </div>
                </c:if>

                <!-- Contracts List -->
                <c:choose>
                    <c:when test="${not empty contracts && contracts.size() > 0}">
                        <c:forEach var="contract" items="${contracts}" varStatus="loop">
                            <%
                                // Get current contract from loop
                                java.util.Map<String, Object> contractMap = (java.util.Map<String, Object>) pageContext.getAttribute("contract");
                                java.sql.Date startDate = null;
                                java.sql.Date endDate = null;
                                BigDecimal baseSalary = null;
                                BigDecimal allowance = null;
                                
                                if (contractMap != null) {
                                    startDate = (java.sql.Date) contractMap.get("startDate");
                                    endDate = (java.sql.Date) contractMap.get("endDate");
                                    baseSalary = (BigDecimal) contractMap.get("baseSalary");
                                    allowance = (BigDecimal) contractMap.get("allowance");
                                }
                                
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                            %>
                            <div class="contract-card">
                                <div class="contract-header">
                                    <div class="contract-title">
                                        <div class="employee-name">
                                            <i class="fas fa-user"></i> ${contract.employeeName}
                                        </div>
                                        <div class="contract-id">Contract ID: #${contract.contractId}</div>
                                    </div>
                                    <span class="status-badge">Pending Approval</span>
                                </div>

                                <!-- Changes Section -->
                                <c:if test="${not empty contract.changes}">
                                    <div class="changes-section">
                                        <h4><i class="fas fa-info-circle"></i> Changes:</h4>
                                        <ul class="changes-list">
                                            <c:forEach var="change" items="${contract.changes}">
                                                <li class="${fn:contains(change, 'Salary Changed') ? 'salary-change-highlight' : ''}">
                                                    ${change}
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </div>
                                </c:if>

                                <!-- Contract Details -->
                                <div class="contract-details">
                                    <div class="detail-item">
                                        <span class="detail-label">Contract Type</span>
                                        <span class="detail-value">${contract.contractType}</span>
                                    </div>
                                    <div class="detail-item">
                                        <span class="detail-label">Start Date</span>
                                        <span class="detail-value">
                                            <%= startDate != null ? sdf.format(startDate) : "Not specified" %>
                                        </span>
                                    </div>
                                    <div class="detail-item">
                                        <span class="detail-label">End Date</span>
                                        <span class="detail-value">
                                            <%= endDate != null ? sdf.format(endDate) : "Not specified" %>
                                        </span>
                                    </div>
                                    <div class="detail-item">
                                        <span class="detail-label">Base Salary</span>
                                        <span class="detail-value">
                                            <%= baseSalary != null ? nf.format(baseSalary) + " VND" : "0 VND" %>
                                        </span>
                                    </div>
                                    <div class="detail-item">
                                        <span class="detail-label">Allowance</span>
                                        <span class="detail-value">
                                            <%= allowance != null ? nf.format(allowance) + " VND" : "0 VND" %>
                                        </span>
                                    </div>
                                    <c:if test="${not empty contract.note}">
                                        <div class="detail-item" style="grid-column: 1 / -1;">
                                            <span class="detail-label">Notes</span>
                                            <span class="detail-value">${contract.note}</span>
                                        </div>
                                    </c:if>
                                </div>

                                <!-- Actions -->
                                <div class="contract-actions">
                                    <button type="button" class="btn btn-approve" onclick="approveContract(${contract.contractId})">
                                        <i class="fas fa-check"></i>
                                        Duyệt
                                    </button>
                                    <button type="button" class="btn btn-reject" onclick="showRejectModal(${contract.contractId})">
                                        <i class="fas fa-times"></i>
                                        Từ chối
                                    </button>
                                </div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <i class="fas fa-check-circle"></i>
                            <h3>No Contracts Pending Approval</h3>
                            <p>All contracts have been processed or there are no contracts pending approval.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
                </div>
            </main>
        </div>

        <!-- Reject Modal -->
        <div id="rejectModal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h3><i class="fas fa-times-circle"></i> Từ chối hợp đồng</h3>
                </div>
                <form id="rejectForm" method="POST" action="${pageContext.request.contextPath}/hr/approve-reject-contracts">
                    <input type="hidden" name="action" value="reject">
                    <input type="hidden" name="contractId" id="rejectContractId">
                    <div class="modal-body">
                        <div class="form-group">
                            <label for="rejectionReason">Lý do từ chối <span style="color: var(--danger-color);">*</span></label>
                            <textarea id="rejectionReason" name="rejectionReason" required placeholder="Enter rejection reason..."></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-cancel" onclick="closeRejectModal()">Hủy</button>
                        <button type="submit" class="btn btn-reject">
                            <i class="fas fa-times"></i>
                            Xác nhận từ chối
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <script>
            // Auto-hide success/error messages after 1 second
            document.addEventListener('DOMContentLoaded', function() {
                const successAlert = document.getElementById('successAlert');
                const errorAlert = document.getElementById('errorAlert');
                
                // Auto-hide success alert after 1 second
                if (successAlert) {
                    setTimeout(function() {
                        fadeOut(successAlert);
                    }, 1000);
                }
                
                // Auto-hide error alert after 1 second
                if (errorAlert) {
                    setTimeout(function() {
                        fadeOut(errorAlert);
                    }, 1000);
                }
                
                // Scroll to top if there's an alert
                if (successAlert || errorAlert) {
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                }
            });

            function closeAlert(alertId) {
                const alert = document.getElementById(alertId);
                if (alert) {
                    fadeOut(alert);
                }
            }

            function fadeOut(element) {
                if (!element) return;
                element.style.transition = 'opacity 0.3s ease-out, transform 0.3s ease-out';
                element.style.opacity = '0';
                element.style.transform = 'translateY(-10px)';
                setTimeout(function() {
                    element.style.display = 'none';
                }, 300);
            }

            function approveContract(contractId) {
                if (confirm('Bạn có chắc muốn duyệt hợp đồng này?')) {
                    const form = document.createElement('form');
                    form.method = 'POST';
                    form.action = '${pageContext.request.contextPath}/hr/approve-reject-contracts';
                    
                    const actionInput = document.createElement('input');
                    actionInput.type = 'hidden';
                    actionInput.name = 'action';
                    actionInput.value = 'approve';
                    form.appendChild(actionInput);
                    
                    const contractIdInput = document.createElement('input');
                    contractIdInput.type = 'hidden';
                    contractIdInput.name = 'contractId';
                    contractIdInput.value = contractId;
                    form.appendChild(contractIdInput);
                    
                    document.body.appendChild(form);
                    form.submit();
                }
            }

            function showRejectModal(contractId) {
                document.getElementById('rejectContractId').value = contractId;
                document.getElementById('rejectModal').classList.add('active');
                document.getElementById('rejectionReason').focus();
            }

            function closeRejectModal() {
                document.getElementById('rejectModal').classList.remove('active');
                document.getElementById('rejectionReason').value = '';
            }

            // Close modal when clicking outside
            document.getElementById('rejectModal').addEventListener('click', function(e) {
                if (e.target === this) {
                    closeRejectModal();
                }
            });

            // Close modal with ESC key
            document.addEventListener('keydown', function(e) {
                if (e.key === 'Escape') {
                    closeRejectModal();
                }
            });
        </script>
    </body>
</html>
