# HRMS Route Testing Guide

## Đường dẫn đã được cập nhật và chuẩn hóa

### 1. Profile Management
- **URL:** `/HRMS/ProfileManagementController`
- **File:** `ProfileManagementController.java`
- **Chức năng:** Hiển thị danh sách nhân viên từ database trong HR Dashboard
- **Dữ liệu:** Tất cả nhân viên với thông tin phòng ban

### 2. Employment Status
- **URL:** `/HRMS/EmploymentStatusController`
- **File:** `EmploymentStatusController.java`
- **JSP:** `EmploymentStatus.jsp`
- **Chức năng:** Quản lý trạng thái việc làm của nhân viên (loại trừ admin)
- **Dữ liệu:** Nhân viên có thể cập nhật trạng thái (Active, Probation, Intern, Resigned)

### 3. Task Management
- **URL:** `/HRMS/TaskManagementController`
- **File:** `TaskManagementController.java`
- **JSP:** `TaskManagement.jsp`
- **Chức năng:** Quản lý công việc, giao việc cho nhân viên
- **Dữ liệu:** Tạo task mới, xem danh sách tasks

### 4. Database Connection Test
- **URL:** `/HRMS/DBConnectionTest`
- **File:** `DBConnectionTest.java`
- **Chức năng:** Kiểm tra kết nối database và dữ liệu

## Cách test:

### Bước 1: Kiểm tra Database
1. Truy cập: `http://localhost:8080/HRMS/DBConnectionTest`
2. Đảm bảo hiển thị "Database Connection Successful!"
3. Kiểm tra có dữ liệu nhân viên trong database

### Bước 2: Test các trang HR
1. **Profile Management:** `http://localhost:8080/HRMS/ProfileManagementController`
2. **Employment Status:** `http://localhost:8080/HRMS/EmploymentStatusController`
3. **Task Management:** `http://localhost:8080/HRMS/TaskManagementController`

### Bước 3: Test từ HR Dashboard
1. Truy cập: `http://localhost:8080/HRMS/Views/hr/HrHome.jsp`
2. Click vào "Employment Status" hoặc "Task Management" trong sidebar
3. Các link sẽ mở trong tab mới

## Cấu trúc đường dẫn chuẩn:

```
/HRMS/
├── ProfileManagementController     → Hiển thị HR Dashboard với dữ liệu nhân viên
├── EmploymentStatusController      → Trang quản lý trạng thái việc làm
├── TaskManagementController        → Trang quản lý công việc
├── DBConnectionTest               → Test kết nối database
└── Views/hr/
    ├── HrHome.jsp                 → Dashboard chính
    ├── EmploymentStatus.jsp       → Trang trạng thái việc làm
    └── TaskManagement.jsp         → Trang quản lý công việc
```

## Lưu ý quan trọng:

1. **Database:** Đảm bảo database `hrm_db` đã được tạo và có dữ liệu từ `data.sql`
2. **DBConnection:** Kiểm tra thông tin kết nối trong `DBConnection.java`
3. **Server:** Đảm bảo Tomcat server đang chạy trên port 8080
4. **Context Path:** Ứng dụng phải được deploy với context path `/HRMS`

## Troubleshooting:

### Nếu trang không load:
1. Kiểm tra console browser (F12)
2. Kiểm tra server logs
3. Đảm bảo servlet được compile và deploy
4. Kiểm tra database connection

### Nếu không có dữ liệu:
1. Chạy script `data.sql` để tạo dữ liệu mẫu
2. Kiểm tra bảng Employee có dữ liệu không
3. Kiểm tra DBConnection có đúng thông tin không

## Test Routes HTML:
Truy cập `http://localhost:8080/HRMS/test-routes.html` để có giao diện test tất cả các routes.



