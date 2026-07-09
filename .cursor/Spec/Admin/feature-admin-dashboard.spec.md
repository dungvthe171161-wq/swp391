# Tính năng Admin: Bảng điều khiển

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Admin xem tổng quan hệ thống sau khi đăng nhập.

## Route, controller và JSP liên quan
- `/admin`, `/admin/users`, `/admin/role/*`, `/admin/role-permissions/api`, `/departments`.
- Controller: `AdminController`, `UserController`, `RoleServlet`, `RolePermissionServlet`, `DepartmentController`.
- JSP: `AdminHome.jsp`, `Users.jsp`, `RolePermissionManager.jsp` và các trang Admin liên quan.

## Hiện trạng code
- Dashboard dùng `/admin?action=dashboard` và JSP Admin.
- Thông tin hiển thị phụ thuộc dữ liệu controller nạp.
- Route `/admin` được filter kiểm tra `MANAGE_SYSTEM`.

## Quy tắc nghiệp vụ chuẩn
- Dashboard chỉ hiển thị dữ liệu tổng hợp, không thực hiện thay đổi dữ liệu.
- Link thao tác phải trỏ về controller, không trỏ thẳng JSP nếu cần dữ liệu.

## Code còn lệch spec hoặc cần bổ sung
- Cần kiểm tra các card thống kê có dữ liệu thật hay placeholder.
- Cần audit nếu dashboard có action nhanh thay đổi dữ liệu.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

