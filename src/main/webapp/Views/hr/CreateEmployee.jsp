<%-- 
    Document   : CreateEmployee
    Created on : Oct 22, 2025, 10:24:49 PM
    Author     : admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.hrm.model.entity.Guest" %>
<%@page import="com.hrm.model.entity.Department" %>
<%@page import="java.util.List" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Tạo nhân viên - BetterHR</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/hr-dashboard.css">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <style>
            /* HR Dashboard Layout */
            .hr-dashboard-container {
                min-height: 100vh;
                background-color: #f8f9fa;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            }
            
            .hr-header {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
            
            .search-bar {
                position: relative;
                display: flex;
                align-items: center;
            }
            
            .search-bar i {
                position: absolute;
                left: 12px;
                color: #6b7280;
            }
            
            .search-bar input {
                padding: 8px 12px 8px 35px;
                border: none;
                border-radius: 20px;
                background: rgba(255,255,255,0.2);
                color: white;
                width: 200px;
            }
            
            .search-bar input::placeholder {
                color: #d1d5db;
            }
            
            .user-profile {
                display: flex;
                align-items: center;
                gap: 8px;
                padding: 8px 12px;
                background: rgba(255,255,255,0.2);
                border-radius: 20px;
                cursor: pointer;
            }
            
            .profile-img {
                width: 24px;
                height: 24px;
                border-radius: 50%;
            }
            
            .nav-btn {
                display: flex;
                align-items: center;
                gap: 8px;
                padding: 8px 16px;
                background: rgba(255,255,255,0.2);
                border: 1px solid rgba(255,255,255,0.3);
                border-radius: 20px;
                color: white;
                text-decoration: none;
                font-weight: 500;
                transition: all 0.3s ease;
            }
            
            .nav-btn:hover {
                background: rgba(255,255,255,0.3);
                transform: translateY(-1px);
            }
            
            .hr-main-content {
                display: flex;
                max-width: 1400px;
                margin: 0 auto;
                padding: 2rem;
                gap: 2rem;
            }
            
            .hr-sidebar {
                width: 280px;
                background: white;
                border-radius: 12px;
                box-shadow: 0 4px 20px rgba(0,0,0,0.1);
                padding: 1.5rem;
                height: fit-content;
            }
            
            .nav-section h3 {
                color: #64748b;
                font-size: 0.875rem;
                font-weight: 600;
                text-transform: uppercase;
                letter-spacing: 0.05em;
                margin-bottom: 1rem;
                padding-bottom: 0.5rem;
                border-bottom: 1px solid #e2e8f0;
            }
            
            .nav-item {
                display: flex;
                align-items: center;
                gap: 0.75rem;
                padding: 0.75rem 1rem;
                border-radius: 8px;
                text-decoration: none;
                color: #64748b;
                transition: all 0.3s ease;
                font-weight: 500;
                margin-bottom: 0.5rem;
            }
            
            .nav-item:hover {
                background: #f1f5f9;
                color: #2563eb;
                transform: translateX(4px);
            }
            
            .nav-item.active {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
            }
            
            .nav-item i {
                font-size: 1.1rem;
                width: 20px;
                text-align: center;
            }
            
            .hr-content-area {
                flex: 1;
                background: white;
                border-radius: 12px;
                box-shadow: 0 4px 20px rgba(0,0,0,0.1);
                padding: 2rem;
                min-height: 600px;
            }
            
            /* Form Styles */
            .create-employee-container {
                max-width: 100%;
                margin: 0;
                padding: 0;
                background: transparent;
                min-height: auto;
            }
            
            
            .form-container {
                background: transparent;
                padding: 0;
                border-radius: 0;
                box-shadow: none;
                margin-bottom: 0;
            }
            
            .form-section {
                margin-bottom: 30px;
            }
            
            .section-title {
                color: #2c3e50;
                font-size: 20px;
                font-weight: 600;
                margin-bottom: 15px;
                padding-bottom: 10px;
                border-bottom: 2px solid #3498db;
            }
            
            .form-row {
                display: flex;
                gap: 20px;
                margin-bottom: 20px;
            }
            
            .form-group {
                flex: 1;
            }
            
            .form-group label {
                display: block;
                margin-bottom: 5px;
                color: #2c3e50;
                font-weight: 500;
            }
            
            .form-group input,
            .form-group select,
            .form-group textarea {
                width: 100%;
                padding: 12px;
                border: 2px solid #e1e8ed;
                border-radius: 6px;
                font-size: 14px;
                transition: border-color 0.3s ease;
            }
            
            .form-group input:focus,
            .form-group select:focus,
            .form-group textarea:focus {
                outline: none;
                border-color: #3498db;
            }
            
            .required {
                color: #e74c3c;
            }
            
            .guest-list {
                background: #f8f9fa;
                border-radius: 6px;
                padding: 15px;
                margin-bottom: 20px;
            }
            
            .guest-item {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 10px;
                background: white;
                border-radius: 4px;
                margin-bottom: 10px;
                border: 1px solid #e1e8ed;
                cursor: pointer;
                transition: all 0.3s ease;
            }
            
            .guest-item:hover {
                border-color: #3498db;
                box-shadow: 0 2px 4px rgba(52, 152, 219, 0.1);
            }
            
            .guest-item.selected {
                border-color: #3498db;
                background: #ebf3fd;
            }
            
            .guest-info {
                flex: 1;
            }
            
            .guest-name {
                font-weight: 600;
                color: #2c3e50;
                margin-bottom: 5px;
            }
            
            .guest-details {
                color: #7f8c8d;
                font-size: 14px;
            }
            
            .guest-status {
                padding: 4px 8px;
                border-radius: 12px;
                font-size: 12px;
                font-weight: 500;
                text-transform: uppercase;
            }
            
            .status-applied {
                background: #fff3cd;
                color: #856404;
            }
            
            .status-interview {
                background: #d1ecf1;
                color: #0c5460;
            }
            
            .status-rejected {
                background: #f8d7da;
                color: #721c24;
            }
            
            .btn {
                padding: 12px 24px;
                border: none;
                border-radius: 6px;
                font-size: 14px;
                font-weight: 500;
                cursor: pointer;
                transition: all 0.3s ease;
                text-decoration: none;
                display: inline-block;
                text-align: center;
            }
            
            .btn-primary {
                background: #3498db;
                color: white;
            }
            
            .btn-primary:hover {
                background: #2980b9;
            }
            
            .btn-success {
                background: #27ae60;
                color: white;
            }
            
            .btn-success:hover {
                background: #229954;
            }
            
            .btn-secondary {
                background: #95a5a6;
                color: white;
            }
            
            .btn-secondary:hover {
                background: #7f8c8d;
            }
            
            .alert {
                padding: 15px;
                border-radius: 6px;
                margin-bottom: 20px;
                font-weight: 500;
            }
            
            .alert-success {
                background: #d4edda;
                color: #155724;
                border: 1px solid #c3e6cb;
            }
            
            .alert-error {
                background: #f8d7da;
                color: #721c24;
                border: 1px solid #f5c6cb;
            }
            
            .form-actions {
                display: flex;
                gap: 15px;
                justify-content: flex-end;
                margin-top: 30px;
                padding-top: 20px;
                border-top: 1px solid #e1e8ed;
            }
            
            .hidden {
                display: none;
            }
            
            .loading {
                opacity: 0.6;
                pointer-events: none;
            }
            
            @media (max-width: 768px) {
                .form-row {
                    flex-direction: column;
                    gap: 0;
                }
                
                .form-actions {
                    flex-direction: column;
                }
            }
        </style>
        <link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;500;600;700;800&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/hr-theme.css?v=hr-manager-shell-20260627-4">
    </head>
    <body>
        <!-- HR Dashboard Container -->
        <div class="hr-dashboard-container hr-manager-page-shell">
            <!-- Header -->
            <header class="hr-header">
                <div class="header-content">
                    <div class="logo-section">
                        <i class="fas fa-users"></i>
                        <h1>Tạo nhân viên</h1>
                    </div>
                    <div class="header-actions">
                        <div class="search-bar">
                            <i class="fas fa-search"></i>
                            <input type="text" placeholder="Tìm nhân viên...">
                        </div>
                        <div class="user-profile">
                            <img src="${pageContext.request.contextPath}/image/logo/1.png" alt="Hồ sơ" class="profile-img">
                            <span>Quản lý HR</span>
                            <i class="fas fa-chevron-down"></i>
                        </div>
                        <a href="${pageContext.request.contextPath}/HrHomeController" class="nav-btn">
                            <i class="fas fa-tachometer-alt"></i>
                            HR Home
                        </a>
                        <a href="${pageContext.request.contextPath}/homepage" class="nav-btn">
                            <i class="fas fa-home"></i>
                            Trang chủ
                        </a>
                    </div>
                </div>
            </header>

            <!-- Main Content -->
            <div class="hr-main-content">
                <c:set var="hrSidebarActive" value="create-employee" />
                <%@ include file="_HrManagerSidebar.jspf" %>

                <!-- Content Area -->
                <main class="hr-content-area">
                    <div class="create-employee-container">
            
            <!-- Success/Error Messages -->
            <% if (request.getAttribute("success") != null) { %>
                <div class="alert alert-success">
                    <strong>Success!</strong> <%= request.getAttribute("success") %>
                </div>
            <% } %>
            
            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error">
                    <strong>Error!</strong> <%= request.getAttribute("error") %>
                </div>
            <% } %>
            
            <form action="${pageContext.request.contextPath}/hr/create-employee" method="POST" id="createEmployeeForm">
                <input type="hidden" name="action" value="create">
                <input type="hidden" name="guestId" id="selectedGuestId">
                
                <div class="form-container">
                    <!-- Guest Selection Section -->
                    <div class="form-section">
                        <h3 class="section-title">1. Chọn ứng viên guest</h3>
                        <div class="guest-list">
                            <% 
                                List<Guest> guests = (List<Guest>) request.getAttribute("guests");
                                if (guests != null && !guests.isEmpty()) {
                                    for (Guest guest : guests) {
                            %>
                                <div class="guest-item" data-guest-id="<%= guest.getGuestId() %>" 
                                     data-guest-name="<%= guest.getFullName() %>"
                                     data-guest-email="<%= guest.getEmail() %>"
                                     data-guest-phone="<%= guest.getPhone() %>">
                                    <div class="guest-info">
                                        <div class="guest-name"><%= guest.getFullName() %></div>
                                        <div class="guest-details">
                                            Email: <%= guest.getEmail() %> | 
                                            Số điện thoại: <%= guest.getPhone() %> |
                                            Ngày ứng tuyển: <%= guest.getAppliedDate() != null ? guest.getAppliedDate().toString() : "N/A" %>
                                        </div>
                                    </div>
                                    <div class="guest-status status-<%= guest.getStatus().toLowerCase() %>">
                                        <%= guest.getStatus() %>
                                    </div>
                                </div>
                            <% 
                                    }
                                } else {
                            %>
                                <div style="text-align: center; padding: 20px; color: #7f8c8d;">
                                    Chưa có guest phù hợp để chuyển thành nhân viên.
                                </div>
                            <% } %>
                        </div>
                    </div>
                    
                    <!-- Employee Information Section -->
                    <div class="form-section">
                        <h3 class="section-title">2. Thông tin nhân viên</h3>
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label for="fullName">Họ và tên <span class="required">*</span></label>
                                <input type="text" id="fullName" name="fullName" required readonly 
                                       style="background-color: #f8f9fa; cursor: not-allowed;">
                            </div>
                        </div>
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label for="email">Email <span class="required">*</span></label>
                                <input type="email" id="email" name="email" required readonly 
                                       style="background-color: #f8f9fa; cursor: not-allowed;">
                            </div>
                            <div class="form-group">
                                <label for="phone">Số điện thoại</label>
                                <input type="tel" id="phone" name="phone" readonly 
                                       style="background-color: #f8f9fa; cursor: not-allowed;">
                            </div>
                        </div>
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label for="gender">Giới tính</label>
                                <select id="gender" name="gender" required>
                                    <option value="">Chọn giới tính</option>
                                    <option value="Male">Nam</option>
                                    <option value="Female">Nữ</option>
                                    <option value="Other">Khác</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="dob">Ngày sinh</label>
                                <input type="date" id="dob" name="dob">
                            </div>
                        </div>
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label for="address">Địa chỉ</label>
                                <textarea id="address" name="address" rows="3"></textarea>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Employment Details Section -->
                    <div class="form-section">
                        <h3 class="section-title">3. Thông tin công việc</h3>
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label for="departmentId">Phòng ban <span class="required">*</span></label>
                                <select id="departmentId" name="departmentId" required>
                                    <option value="">Chọn phòng ban</option>
                                    <% 
                                        List<Department> departments = (List<Department>) request.getAttribute("departments");
                                        if (departments != null) {
                                            for (Department dept : departments) {
                                    %>
                                        <option value="<%= dept.getDepartmentId() %>"><%= dept.getDeptName() %></option>
                                    <% 
                                            }
                                        }
                                    %>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="position">Vị trí <span class="required">*</span></label>
                                <select id="position" name="position" required disabled>
                                    <option value="">Vui lòng chọn phòng ban trước</option>
                                </select>
                            </div>
                        </div>
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label for="hireDate">Ngày bắt đầu <span class="required">*</span></label>
                                <input type="date" id="hireDate" name="hireDate" required>
                            </div>
                            <div class="form-group">
                                <label for="endDate">Ngày kết thúc</label>
                                <input type="date" id="endDate" name="endDate">
                            </div>
                        </div>
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label for="status">Trạng thái làm việc <span class="required">*</span></label>
                                <select id="status" name="status" required>
                                    <option value="">Chọn trạng thái</option>
                                    <option value="Active">Chính thức</option>
                                    <option value="Intern">Thực tập</option>
                                    <option value="Probation">Thử việc</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    
                    <!-- System Account Section -->
                    <div class="form-section">
                        <h3 class="section-title">4. Tài khoản hệ thống</h3>
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label for="username">Username <span class="required">*</span></label>
                                <input type="text" id="username" name="username" required>
                            </div>
                            <div class="form-group">
                                <label for="password">Password <span class="required">*</span></label>
                                <input type="password" id="password" name="password" required>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Form Actions -->
                    <div class="form-actions">
                       
                        <button type="submit" class="btn btn-success" id="createEmployeeBtn">Tạo nhân viên</button>
                    </div>
                </div>
            </form>
                    </div>
                </main>
            </div>
        </div>
        
        <script>
            // Department-Position mapping
            // Note: Human Resources is excluded as this page is for creating regular employees only
            const departmentPositions = {
                'Finance': [
                    { value: 'Finance Manager', text: 'Finance Manager' },
                    { value: 'Accountant', text: 'Accountant' },
                    { value: 'Financial Analyst', text: 'Financial Analyst' },
                    { value: 'Budget Coordinator', text: 'Budget Coordinator' }
                ],
                'IT Department': [
                    { value: 'IT Manager', text: 'IT Manager' },
                    { value: 'Developer', text: 'Developer' },
                    { value: 'System Administrator', text: 'System Administrator' },
                    { value: 'Database Administrator', text: 'Database Administrator' },
                    { value: 'Frontend Developer', text: 'Frontend Developer' },
                    { value: 'Backend Developer', text: 'Backend Developer' },
                    { value: 'Full Stack Developer', text: 'Full Stack Developer' },
                    { value: 'DevOps Engineer', text: 'DevOps Engineer' }
                ],
                'Marketing': [
                    { value: 'Marketing Manager', text: 'Marketing Manager' },
                    { value: 'Designer', text: 'Designer' },
                    { value: 'Content Writer', text: 'Content Writer' },
                    { value: 'Social Media Specialist', text: 'Social Media Specialist' },
                    { value: 'Marketing Analyst', text: 'Marketing Analyst' },
                    { value: 'Brand Manager', text: 'Brand Manager' }
                ],
                'Operations': [
                    { value: 'Operations Manager', text: 'Operations Manager' },
                    { value: 'Operator', text: 'Operator' },
                    { value: 'Project Manager', text: 'Project Manager' },
                    { value: 'Quality Assurance', text: 'Quality Assurance' },
                    { value: 'Process Coordinator', text: 'Process Coordinator' }
                ]
            };

            // Guest selection functionality
            document.addEventListener('DOMContentLoaded', function() {
                const guestItems = document.querySelectorAll('.guest-item');
                const selectedGuestIdInput = document.getElementById('selectedGuestId');
                const fullNameInput = document.getElementById('fullName');
                const emailInput = document.getElementById('email');
                const phoneInput = document.getElementById('phone');
                const createEmployeeBtn = document.getElementById('createEmployeeBtn');
                const departmentSelect = document.getElementById('departmentId');
                const positionSelect = document.getElementById('position');
                
                // Handle department change
                departmentSelect.addEventListener('change', function() {
                    const selectedDeptName = this.options[this.selectedIndex].text;
                    
                    // Clear position options
                    positionSelect.innerHTML = '';
                    
                    if (selectedDeptName && selectedDeptName !== 'Chọn phòng ban') {
                        // Enable position select
                        positionSelect.disabled = false;
                        
                        // Add default option
                        const defaultOption = document.createElement('option');
                        defaultOption.value = '';
                        defaultOption.textContent = 'Select Position';
                        positionSelect.appendChild(defaultOption);
                        
                        // Add positions for selected department
                        if (departmentPositions[selectedDeptName]) {
                            departmentPositions[selectedDeptName].forEach(pos => {
                                const option = document.createElement('option');
                                option.value = pos.value;
                                option.textContent = pos.text;
                                positionSelect.appendChild(option);
                            });
                        }
                    } else {
                        // Disable position select
                        positionSelect.disabled = true;
                        const defaultOption = document.createElement('option');
                        defaultOption.value = '';
                        defaultOption.textContent = 'Vui lòng chọn phòng ban trước';
                        positionSelect.appendChild(defaultOption);
                    }
                });
                
                // Handle guest selection
                guestItems.forEach(item => {
                    item.addEventListener('click', function() {
                        // Remove previous selection
                        guestItems.forEach(gi => gi.classList.remove('selected'));
                        
                        // Add selection to clicked item
                        this.classList.add('selected');
                        
                        // Set hidden input value
                        selectedGuestIdInput.value = this.dataset.guestId;
                        
                        // Pre-fill form with guest data (keep readonly)
                        fullNameInput.value = this.dataset.guestName;
                        emailInput.value = this.dataset.guestEmail;
                        phoneInput.value = this.dataset.guestPhone;
                        
                        // Ensure fields remain readonly
                        fullNameInput.setAttribute('readonly', 'readonly');
                        emailInput.setAttribute('readonly', 'readonly');
                        phoneInput.setAttribute('readonly', 'readonly');
                        
                        // Enable create button
                        createEmployeeBtn.disabled = false;
                        createEmployeeBtn.textContent = 'Tạo nhân viên';
                    });
                });
                
                // Form validation
                const form = document.getElementById('createEmployeeForm');
                const hireDateInput = document.getElementById('hireDate');
                const endDateInput = document.getElementById('endDate');
                
                form.addEventListener('submit', function(e) {
                    if (!selectedGuestIdInput.value) {
                        e.preventDefault();
                        alert('Please select a guest candidate first.');
                        return false;
                    }
                    
                    if (!positionSelect.value) {
                        e.preventDefault();
                        alert('Please select a position.');
                        return false;
                    }
                    
                    // Validate end date is after or equal to start date
                    if (hireDateInput.value && endDateInput.value) {
                        const startDate = new Date(hireDateInput.value);
                        const endDate = new Date(endDateInput.value);
                        if (endDate < startDate) {
                            e.preventDefault();
                            alert('End date must be after or equal to start date.');
                            return false;
                        }
                    }
                    
                    // Show loading state
                    createEmployeeBtn.disabled = true;
                    createEmployeeBtn.textContent = 'Đang tạo nhân viên...';
                    form.classList.add('loading');
                });
                
                // Set default hire date to today
                const today = new Date().toISOString().split('T')[0];
                document.getElementById('hireDate').value = today;
            });
        </script>
    </body>
</html>
