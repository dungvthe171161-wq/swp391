# Tính năng Admin: Nhật ký và hồ sơ cá nhân

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Admin xem thông tin cá nhân và nhật ký hệ thống.

## Route, controller và JSP liên quan
- `/admin`, `/admin/users`, `/admin/role/*`, `/admin/role-permissions/api`, `/departments`.
- Controller: `AdminController`, `UserController`, `RoleServlet`, `RolePermissionServlet`, `DepartmentController`.
- JSP: `AdminHome.jsp`, `Users.jsp`, `RolePermissionManager.jsp` và các trang Admin liên quan.

## Hiện trạng code
- Profile dùng controller/profile chung nếu có.
- Audit log chưa có service thống nhất trong code.
- Một số hành động quản trị chưa ghi audit.

## Quy tắc nghiệp vụ chuẩn
- Admin được xem audit theo quyền riêng.
- Profile chỉ cho sửa dữ liệu của chính user hoặc theo quyền quản trị.
- Audit không hiển thị dữ liệu bí mật.

## Code còn lệch spec hoặc cần bổ sung
- Cần triển khai audit service và màn hình audit nếu đây là yêu cầu bắt buộc.
- Cần kiểm tra phân quyền profile Admin với user thường.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

