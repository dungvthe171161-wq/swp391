// HR Dashboard JavaScript Functions

document.addEventListener('DOMContentLoaded', function() {
    initializeDashboard();
    initializeCharts();
});

// Initialize dashboard functionality
function initializeDashboard() {
    // Navigation handling
    const navItems = document.querySelectorAll('.nav-item');
    navItems.forEach(item => {
        item.addEventListener('click', function(e) {
            e.preventDefault();
            const sectionId = this.getAttribute('data-section');
            showSection(sectionId);
        });
    });

    // Tab handling
    const tabButtons = document.querySelectorAll('.tab-btn');
    tabButtons.forEach(button => {
        button.addEventListener('click', function() {
            const tabId = this.getAttribute('data-tab');
            const tabContainer = this.closest('.tab-content') || this.closest('.content-section');
            switchTab(tabContainer, tabId);
        });
    });

    // Status filter handling
    const filterButtons = document.querySelectorAll('.filter-btn');
    filterButtons.forEach(button => {
        button.addEventListener('click', function() {
            const status = this.getAttribute('data-status');
            filterByStatus(status);
        });
    });

    // Form submissions
    initializeFormHandlers();
    
    // Button actions
    initializeButtonActions();
}

// Show specific section
function showSection(sectionId) {
    // Hide all sections
    const sections = document.querySelectorAll('.content-section');
    sections.forEach(section => {
        section.classList.remove('active');
    });

    // Show selected section
    const targetSection = document.getElementById(sectionId);
    if (targetSection) {
        targetSection.classList.add('active');
    }

    // Update navigation
    const navItems = document.querySelectorAll('.nav-item');
    navItems.forEach(item => {
        item.classList.remove('active');
        if (item.getAttribute('data-section') === sectionId) {
            item.classList.add('active');
        }
    });

    // Scroll to top of content
    const contentArea = document.querySelector('.hr-content-area');
    if (contentArea) {
        contentArea.scrollTop = 0;
    }
}

// Switch between tabs
function switchTab(container, tabId) {
    // Remove active class from all tab buttons in this container
    const tabButtons = container.querySelectorAll('.tab-btn');
    tabButtons.forEach(button => {
        button.classList.remove('active');
        if (button.getAttribute('data-tab') === tabId) {
            button.classList.add('active');
        }
    });

    // Hide all tab panels in this container
    const tabPanels = container.querySelectorAll('.tab-panel');
    tabPanels.forEach(panel => {
        panel.classList.remove('active');
    });

    // Show selected tab panel
    const targetPanel = document.getElementById(tabId);
    if (targetPanel) {
        targetPanel.classList.add('active');
    }
}

// Filter employees by status
function filterByStatus(status) {
    const filterButtons = document.querySelectorAll('.filter-btn');
    filterButtons.forEach(button => {
        button.classList.remove('active');
        if (button.getAttribute('data-status') === status) {
            button.classList.add('active');
        }
    });

    const statusCards = document.querySelectorAll('.status-card');
    statusCards.forEach(card => {
        if (status === 'all') {
            card.style.display = 'flex';
        } else {
            const currentStatus = card.querySelector('.current-status');
            if (currentStatus && currentStatus.classList.contains(status)) {
                card.style.display = 'flex';
            } else {
                card.style.display = 'none';
            }
        }
    });
}

// Initialize form handlers
function initializeFormHandlers() {
    // Task assignment form
    const taskForm = document.querySelector('.task-assignment-form form');
    if (taskForm) {
        taskForm.addEventListener('submit', function(e) {
            e.preventDefault();
            handleTaskAssignment();
        });
    }

    // Salary calculation form
    const salaryForm = document.querySelector('.salary-form');
    if (salaryForm) {
        salaryForm.addEventListener('submit', function(e) {
            e.preventDefault();
            handleSalaryCalculation();
        });

        // Auto-calculate salary components
        const salaryInputs = salaryForm.querySelectorAll('input[type="number"]');
        salaryInputs.forEach(input => {
            input.addEventListener('input', calculateSalary);
        });
    }
}

// Initialize button actions
function initializeButtonActions() {
    // Approve/Reject buttons
    const approveButtons = document.querySelectorAll('.btn-approve');
    approveButtons.forEach(button => {
        button.addEventListener('click', function() {
            handleApproval(this, true);
        });
    });

    const rejectButtons = document.querySelectorAll('.btn-reject');
    rejectButtons.forEach(button => {
        button.addEventListener('click', function() {
            handleApproval(this, false);
        });
    });

    // Status update buttons
    const updateStatusButtons = document.querySelectorAll('.btn-update-status');
    updateStatusButtons.forEach(button => {
        button.addEventListener('click', function() {
            handleStatusUpdate(this);
        });
    });

    // View details buttons
    const viewDetailsButtons = document.querySelectorAll('.btn-view-details');
    viewDetailsButtons.forEach(button => {
        button.addEventListener('click', function() {
            handleViewDetails(this);
        });
    });
}

// Handle task assignment
function handleTaskAssignment() {
    const form = document.querySelector('.task-assignment-form form');
    const formData = new FormData(form);
    
    // Get form values
    const employee = form.querySelector('select').value;
    const title = form.querySelector('input[type="text"]').value;
    const description = form.querySelector('textarea').value;
    const deadline = form.querySelector('input[type="date"]').value;
    const priority = form.querySelectorAll('select')[1].value;

    // Validate form
    if (!employee || !title || !description || !deadline) {
        showNotification('Vui lòng điền đầy đủ thông tin', 'error');
        return;
    }

    // Simulate API call
    showNotification('Đã giao việc thành công!', 'success');
    form.reset();
    
    // Add to task list (simulation)
    addTaskToList({
        employee: employee,
        title: title,
        description: description,
        deadline: deadline,
        priority: priority,
        status: 'assigned'
    });
}

// Handle salary calculation
function handleSalaryCalculation() {
    const form = document.querySelector('.salary-form');
    const formData = new FormData(form);
    
    // Get salary components
    const basicSalary = parseFloat(form.querySelector('input[value="15000000"]').value) || 0;
    const lunchAllowance = parseFloat(form.querySelector('input[value="1000000"]').value) || 0;
    const transportAllowance = parseFloat(form.querySelector('input[value="500000"]').value) || 0;
    const bonus = parseFloat(form.querySelector('input[value="2000000"]').value) || 0;
    const personalTax = parseFloat(form.querySelector('input[value="500000"]').value) || 0;

    // Calculate totals
    const totalIncome = basicSalary + lunchAllowance + transportAllowance + bonus;
    const socialInsurance = basicSalary * 0.08;
    const healthInsurance = basicSalary * 0.015;
    const unemploymentInsurance = basicSalary * 0.01;
    const totalDeductions = socialInsurance + healthInsurance + unemploymentInsurance + personalTax;
    const netSalary = totalIncome - totalDeductions;

    // Update summary
    updateSalarySummary(totalIncome, totalDeductions, netSalary);
    
    showNotification('Đã tính toán và lưu lương thành công!', 'success');
}

// Calculate salary automatically
function calculateSalary() {
    const form = document.querySelector('.salary-form');
    if (!form) return;

    const basicSalary = parseFloat(form.querySelector('input[value="15000000"]').value) || 0;
    
    // Update insurance calculations
    const socialInsuranceInput = form.querySelector('input[value="1200000"]');
    const healthInsuranceInput = form.querySelector('input[value="225000"]');
    const unemploymentInsuranceInput = form.querySelector('input[value="150000"]');
    
    if (socialInsuranceInput) socialInsuranceInput.value = Math.round(basicSalary * 0.08);
    if (healthInsuranceInput) healthInsuranceInput.value = Math.round(basicSalary * 0.015);
    if (unemploymentInsuranceInput) unemploymentInsuranceInput.value = Math.round(basicSalary * 0.01);

    // Recalculate totals
    handleSalaryCalculation();
}

// Update salary summary
function updateSalarySummary(totalIncome, totalDeductions, netSalary) {
    const summaryItems = document.querySelectorAll('.summary-item');
    
    if (summaryItems[0]) {
        summaryItems[0].querySelector('.amount').textContent = formatCurrency(totalIncome);
    }
    if (summaryItems[1]) {
        summaryItems[1].querySelector('.amount').textContent = formatCurrency(totalDeductions);
    }
    if (summaryItems[2]) {
        summaryItems[2].querySelector('.amount').textContent = formatCurrency(netSalary);
    }
}

// Handle approval/rejection
function handleApproval(button, isApproved) {
    const card = button.closest('.request-card, .request-item');
    const action = isApproved ? 'phê duyệt' : 'từ chối';
    
    // Show confirmation
    if (confirm(`Bạn có chắc chắn muốn ${action} yêu cầu này?`)) {
        // Simulate API call
        showNotification(`Đã ${action} yêu cầu thành công!`, 'success');
        
        // Move card to appropriate section
        moveCardToSection(card, isApproved ? 'approved' : 'rejected');
    }
}

// Handle status update
function handleStatusUpdate(button) {
    const card = button.closest('.status-card');
    const select = card.querySelector('.status-select');
    const newStatus = select.value;
    
    if (confirm(`Bạn có chắc chắn muốn cập nhật trạng thái thành "${getStatusText(newStatus)}"?`)) {
        // Update status display
        const statusElement = card.querySelector('.current-status');
        statusElement.className = `current-status ${newStatus}`;
        statusElement.textContent = getStatusText(newStatus);
        
        showNotification('Đã cập nhật trạng thái thành công!', 'success');
    }
}

// Handle view details
function handleViewDetails(button) {
    const card = button.closest('.request-card, .request-item');
    const employeeName = card.querySelector('h4').textContent;
    
    // Show modal or navigate to details page
    showNotification(`Đang mở chi tiết của ${employeeName}...`, 'info');
}

// Move card to different section
function moveCardToSection(card, section) {
    card.style.opacity = '0.5';
    card.style.transform = 'scale(0.95)';
    
    setTimeout(() => {
        card.remove();
        showNotification('Đã chuyển yêu cầu sang danh sách phù hợp', 'info');
    }, 500);
}

// Get status text in Vietnamese
function getStatusText(status) {
    const statusMap = {
        'active': 'Đang hoạt động',
        'intern': 'Thực tập',
        'probation': 'Thử việc',
        'resigned': 'Đã thôi việc',
        'terminated': 'Chấm dứt hợp đồng'
    };
    return statusMap[status] || status;
}

// Add task to list (simulation)
function addTaskToList(task) {
    const taskList = document.querySelector('#task-progress');
    if (!taskList) return;

    const taskElement = document.createElement('div');
    taskElement.className = 'task-item';
    taskElement.innerHTML = `
        <div class="task-info">
            <h4>${task.title}</h4>
            <p><strong>Nhân viên:</strong> ${task.employee}</p>
            <p><strong>Hạn chót:</strong> ${formatDate(task.deadline)}</p>
            <p><strong>Độ ưu tiên:</strong> ${getPriorityText(task.priority)}</p>
        </div>
        <div class="task-status">
            <span class="status-badge assigned">Đã giao</span>
        </div>
    `;
    
    taskList.appendChild(taskElement);
}

// Get priority text in Vietnamese
function getPriorityText(priority) {
    const priorityMap = {
        'low': 'Thấp',
        'medium': 'Trung bình',
        'high': 'Cao',
        'urgent': 'Khẩn cấp'
    };
    return priorityMap[priority] || priority;
}

// Show job posting form
function showJobPostingForm() {
    const formHtml = `
        <div class="modal-overlay" id="jobPostingModal">
            <div class="modal-content">
                <div class="modal-header">
                    <h3>Tạo tin tuyển dụng mới</h3>
                    <button class="close-btn" onclick="closeModal('jobPostingModal')">&times;</button>
                </div>
                <form class="job-posting-form">
                    <div class="form-group">
                        <label>Vị trí tuyển dụng:</label>
                        <input type="text" class="form-input" placeholder="VD: Senior Java Developer" required>
                    </div>
                    <div class="form-group">
                        <label>Phòng ban:</label>
                        <select class="form-select" required>
                            <option value="">Chọn phòng ban</option>
                            <option value="it">IT Department</option>
                            <option value="marketing">Marketing Department</option>
                            <option value="hr">HR Department</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Mức lương:</label>
                        <input type="text" class="form-input" placeholder="VD: 20,000,000 - 30,000,000 VNĐ">
                    </div>
                    <div class="form-group">
                        <label>Kinh nghiệm yêu cầu:</label>
                        <input type="text" class="form-input" placeholder="VD: 3-5 năm">
                    </div>
                    <div class="form-group">
                        <label>Mô tả công việc:</label>
                        <textarea class="form-textarea" placeholder="Mô tả chi tiết về công việc..." required></textarea>
                    </div>
                    <div class="form-group">
                        <label>Yêu cầu ứng viên:</label>
                        <textarea class="form-textarea" placeholder="Các yêu cầu về kỹ năng, kinh nghiệm..." required></textarea>
                    </div>
                    <div class="form-actions">
                        <button type="button" class="btn-secondary" onclick="closeModal('jobPostingModal')">Hủy</button>
                        <button type="submit" class="btn-primary">Đăng tin</button>
                    </div>
                </form>
            </div>
        </div>
    `;
    
    document.body.insertAdjacentHTML('beforeend', formHtml);
    
    // Handle form submission
    const form = document.querySelector('.job-posting-form');
    form.addEventListener('submit', function(e) {
        e.preventDefault();
        handleJobPosting();
    });
}

// Handle job posting
function handleJobPosting() {
    showNotification('Đã tạo tin tuyển dụng thành công!', 'success');
    closeModal('jobPostingModal');
    
    // Refresh job postings list
    setTimeout(() => {
        location.reload();
    }, 1000);
}

// Close modal
function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.remove();
    }
}

// Initialize charts
function initializeCharts() {
    // Employee Overview Chart
    const employeeOverviewCtx = document.getElementById('employeeOverviewChart');
    if (employeeOverviewCtx) {
        new Chart(employeeOverviewCtx, {
            type: 'doughnut',
            data: {
                labels: ['Đang hoạt động', 'Thử việc', 'Thực tập', 'Đã nghỉ'],
                datasets: [{
                    data: [120, 12, 8, 16],
                    backgroundColor: ['#28a745', '#ffc107', '#17a2b8', '#dc3545']
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom'
                    }
                }
            }
        });
    }

    // Department Distribution Chart
    const departmentDistributionCtx = document.getElementById('departmentDistributionChart');
    if (departmentDistributionCtx) {
        new Chart(departmentDistributionCtx, {
            type: 'bar',
            data: {
                labels: ['IT', 'Marketing', 'HR', 'Sales', 'Finance'],
                datasets: [{
                    label: 'Số nhân viên',
                    data: [45, 28, 15, 32, 18],
                    backgroundColor: '#667eea'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    }

    // Recruitment Chart
    const recruitmentCtx = document.getElementById('recruitmentChart');
    if (recruitmentCtx) {
        new Chart(recruitmentCtx, {
            type: 'line',
            data: {
                labels: ['T1', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'T8', 'T9', 'T10', 'T11', 'T12'],
                datasets: [{
                    label: 'Ứng viên mới',
                    data: [12, 19, 8, 15, 22, 18, 25, 20, 16, 14, 21, 28],
                    borderColor: '#28a745',
                    backgroundColor: 'rgba(40, 167, 69, 0.1)',
                    tension: 0.4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    }

    // Turnover Chart
    const turnoverCtx = document.getElementById('turnoverChart');
    if (turnoverCtx) {
        new Chart(turnoverCtx, {
            type: 'pie',
            data: {
                labels: ['Tỷ lệ nghỉ việc thấp', 'Tỷ lệ nghỉ việc trung bình', 'Tỷ lệ nghỉ việc cao'],
                datasets: [{
                    data: [65, 25, 10],
                    backgroundColor: ['#28a745', '#ffc107', '#dc3545']
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom'
                    }
                }
            }
        });
    }
}

// Utility functions
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('vi-VN');
}

function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `
        <div class="notification-content">
            <i class="fas fa-${getNotificationIcon(type)}"></i>
            <span>${message}</span>
        </div>
    `;
    
    // Add styles
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: ${getNotificationColor(type)};
        color: white;
        padding: 1rem 1.5rem;
        border-radius: 8px;
        box-shadow: 0 4px 8px rgba(0,0,0,0.2);
        z-index: 10000;
        animation: slideIn 0.3s ease;
    `;
    
    document.body.appendChild(notification);
    
    // Auto remove after 3 seconds
    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => {
            notification.remove();
        }, 300);
    }, 3000);
}

function getNotificationIcon(type) {
    const icons = {
        'success': 'check-circle',
        'error': 'exclamation-circle',
        'warning': 'exclamation-triangle',
        'info': 'info-circle'
    };
    return icons[type] || 'info-circle';
}

function getNotificationColor(type) {
    const colors = {
        'success': '#28a745',
        'error': '#dc3545',
        'warning': '#ffc107',
        'info': '#17a2b8'
    };
    return colors[type] || '#17a2b8';
}

// Add CSS animations
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from {
            transform: translateX(100%);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    
    @keyframes slideOut {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(100%);
            opacity: 0;
        }
    }
    
    .modal-overlay {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0,0,0,0.5);
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 10000;
    }
    
    .modal-content {
        background: white;
        border-radius: 10px;
        padding: 2rem;
        max-width: 600px;
        width: 90%;
        max-height: 80vh;
        overflow-y: auto;
    }
    
    .modal-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 1.5rem;
        padding-bottom: 1rem;
        border-bottom: 1px solid #e9ecef;
    }
    
    .close-btn {
        background: none;
        border: none;
        font-size: 1.5rem;
        cursor: pointer;
        color: #666;
    }
    
    .form-actions {
        display: flex;
        gap: 1rem;
        justify-content: flex-end;
        margin-top: 2rem;
    }
`;
document.head.appendChild(style);
