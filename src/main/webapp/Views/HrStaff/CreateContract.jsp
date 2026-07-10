<%-- 
    Document   : CreateContract
    Created on : Nov 4, 2025, 11:01:15 PM
    Author     : admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, com.hrm.model.entity.Employee" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Tạo hợp đồng - Nhân viên nhân sự</title>
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
                transition: background 0.2s;
            }

            .btn:hover {
                background: #1d4ed8;
            }

            .btn.secondary {
                background: var(--success);
            }

            .btn.secondary:hover {
                background: #059669;
            }

            .container {
                max-width: 900px;
                margin: 30px auto;
                padding: 0 20px;
            }

            .card {
                background: var(--card);
                border: 1px solid var(--border);
                border-radius: 12px;
                padding: 24px;
                box-shadow: 0 1px 3px rgba(0,0,0,0.1);
            }

            .card-header {
                margin-bottom: 24px;
                padding-bottom: 16px;
                border-bottom: 2px solid var(--border);
            }

            .card-header h2 {
                margin: 0;
                color: var(--text);
                font-size: 24px;
            }

            .card-header .subtitle {
                color: var(--muted);
                font-size: 14px;
                margin-top: 4px;
            }

            .alert {
                padding: 12px 16px;
                border-radius: 8px;
                margin-bottom: 20px;
                font-size: 14px;
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

            .form-group input:focus,
            .form-group select:focus,
            .form-group textarea:focus {
                outline: none;
                border-color: var(--primary);
                box-shadow: 0 0 0 3px rgba(91, 91, 214, 0.1);
            }

            .form-group textarea {
                resize: vertical;
                min-height: 80px;
            }

            .form-group .help-text {
                font-size: 12px;
                color: var(--muted);
                margin-top: 4px;
            }

            .form-row {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 16px;
            }

            @media (max-width: 768px) {
                .form-row {
                    grid-template-columns: 1fr;
                }
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
                color: #fff;
            }

            .btn-cancel:hover {
                background: #4b5563;
            }
        </style>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/hr-theme.css?v=hr-staff-shell-20260627-1">
    </head>
    <body class="hr-staff-page-shell">
        <%
            request.setAttribute("hrStaffSidebarActive", "create-contract");
            request.setAttribute("hrStaffPageTitle", "Tạo hợp đồng");
            request.setAttribute("hrStaffSearchPlaceholder", "Tìm kiếm nhân viên, hợp đồng...");
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
                <div>Tạo hợp đồng</div>
            </div>
            <div class="top-actions">
                <a class="btn secondary" href="<%=request.getContextPath()%>/hrstaff">← Quay lại</a>
            </div>
        </div>

        <div class="container">
            <div class="card">
                <div class="card-header">
                    <h2>📝 Tạo hợp đồng mới</h2>
               
                </div>

                <!-- Error/Success Messages -->
                <% if (request.getAttribute("error") != null) { %>
                <div class="alert error">
                    <%= request.getAttribute("error") %>
                </div>
                <% } %>
                <% if (request.getAttribute("success") != null) { %>
                <div class="alert success">
                    <%= request.getAttribute("success") %>
                </div>
                <% } %>

                <!-- Form -->
                <form method="POST" action="<%=request.getContextPath()%>/hrstaff/contracts/create">
                    <!-- Chọn nhân viên -->
                    <div class="form-group">
                        <label for="employeeId">
                            Nhân viên <span class="required">*</span>
                        </label>
                        <select id="employeeId" name="employeeId" required>
                            <option value="">-- Chọn nhân viên --</option>
                            <%
                                List<Employee> employees = (List<Employee>) request.getAttribute("employees");
                                if (employees != null) {
                                    for (Employee emp : employees) {
                            %>
                            <option value="<%= emp.getEmployeeId() %>">
                                <%= emp.getFullName() %> 
                                (<%= emp.getEmployeeId() %>) 
                                <% if (emp.getDepartmentName() != null) { %>
                                    - <%= emp.getDepartmentName() %>
                                <% } %>
                            </option>
                            <%
                                    }
                                }
                            %>
                        </select>
                        <div class="help-text">Chọn nhân viên cần tạo hợp đồng</div>
                    </div>

                    <!-- Loại hợp đồng -->
                    <div class="form-group">
                        <label for="contractType">
                            Loại hợp đồng <span class="required">*</span>
                        </label>
                        <select id="contractType" name="contractType" required>
                            <option value="">-- Chọn loại hợp đồng --</option>
                            <option value="Full-time">Toàn thời gian</option>
                            <option value="Part-time">Bán thời gian</option>
                            <option value="Probation">Thử việc</option>
                            <option value="Intern">Thực tập</option>
                            <option value="Contract">Theo hợp đồng</option>
                        </select>
                        <div class="help-text">Loại hợp đồng lao động áp dụng</div>
                    </div>

                    <!-- Thời hạn hợp đồng -->
                    <div class="form-row">
                        <div class="form-group">
                            <label for="startDate">
                                Ngày bắt đầu <span class="required">*</span>
                            </label>
                            <input type="date" id="startDate" name="startDate" required/>
                            <div class="help-text">Ngày hợp đồng bắt đầu có hiệu lực</div>
                        </div>

                        <div class="form-group">
                            <label for="endDate">
                                Ngày kết thúc
                            </label>
                            <input type="date" id="endDate" name="endDate"/>
                            <div class="help-text">Để trống nếu hợp đồng chưa có ngày hết hạn</div>
                        </div>
                    </div>

                    <!-- Thông tin lương -->
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
                                required
                                placeholder="Ví dụ: 15000000"
                            />
                            <div class="help-text">Lương cơ bản (VNĐ)</div>
                        </div>

                        <div class="form-group">
                            <label for="allowance">
                                Phụ cấp
                            </label>
                            <input 
                                type="number" 
                                id="allowance" 
                                name="allowance" 
                                min="0" 
                                step="1000"
                                placeholder="Ví dụ: 2000000"
                            />
                            <div class="help-text">Phụ cấp (VNĐ), mặc định là 0</div>
                        </div>
                    </div>

                    <!-- Trạng thái -->
                    <div class="form-group">
                        <label for="status">
                            Trạng thái <span class="required">*</span>
                        </label>
                        <select id="status" name="status" required>
                            <option value="Draft">Bản nháp</option>
                            <option value="Pending_Approval">Chờ phê duyệt</option>
                        </select>
                        <div class="help-text">Trạng thái hợp đồng sau khi tạo</div>
                    </div>

                    <!-- Ghi chú -->
                    <div class="form-group">
                        <label for="note">
                            Ghi chú
                        </label>
                        <textarea 
                            id="note" 
                            name="note" 
                            rows="4"
                            maxlength="1000"
                            placeholder="Nhập ghi chú hợp đồng nếu có..."
                        ></textarea>
                        <div class="help-text">Ghi chú bổ sung cho hợp đồng (không bắt buộc)</div>
                    </div>

                    <!-- Nút thao tác -->
                    <div class="form-actions">
                        <a href="<%=request.getContextPath()%>/hrstaff" class="btn btn-cancel">
                            Hủy
                        </a>
                        <button type="submit" class="btn secondary">
                            ✓ Tạo hợp đồng
                        </button>
                    </div>
                </form>
            </div>
        </div>

                </section>
            </main>
        </div>

        <script>
            // Giới hạn ngày bắt đầu từ hôm nay
            const today = new Date().toISOString().split('T')[0];
            document.getElementById('startDate').setAttribute('min', today);
            
            // Kiểm tra ngày kết thúc phải sau ngày bắt đầu
            const startDateInput = document.getElementById('startDate');
            const endDateInput = document.getElementById('endDate');
            
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
        </script>
    </body>
</html>
