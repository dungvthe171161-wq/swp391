# Tính năng Admin: Gán quyền cho vai trò

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Admin cấu hình permission động cho từng role.

## Route, controller và JSP liên quan
- `/admin`, `/admin/users`, `/admin/role/*`, `/admin/role-permissions/api`, `/departments`.
- Controller: `AdminController`, `UserController`, `RoleServlet`, `RolePermissionServlet`, `DepartmentController`.
- JSP: `AdminHome.jsp`, `Users.jsp`, `RolePermissionManager.jsp` và các trang Admin liên quan.

## Hiện trạng code
- `RolePermissionServlet` xử lý `/admin/role-permissions/api`.
- `ModulePermissionFilter` yêu cầu `MANAGE_ROLE_PERMISSIONS`.
- Permission seed hiện chưa bao phủ đầy đủ các quyền mới như `APPROVE_PAYROLL`.

## Quy tắc nghiệp vụ chuẩn
- Chỉ Admin có quyền quản lý phân quyền mới được thay đổi role-permission.
- Thay đổi permission phải có audit và không làm mất quyền truy cập tối thiểu của Admin.
- Permission phải đồng bộ với controller/filter thật.

## Code còn lệch spec hoặc cần bổ sung
- Bổ sung permission còn thiếu cho payroll, tuyển dụng và create employee.
- Kiểm tra seed `RolePermission` sau khi thêm permission mới.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

