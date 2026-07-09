# Tính năng Admin: Quản lý vai trò

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Admin quản lý danh sách role trong hệ thống.

## Route, controller và JSP liên quan
- `/admin`, `/admin/users`, `/admin/role/*`, `/admin/role-permissions/api`, `/departments`.
- Controller: `AdminController`, `UserController`, `RoleServlet`, `RolePermissionServlet`, `DepartmentController`.
- JSP: `AdminHome.jsp`, `Users.jsp`, `RolePermissionManager.jsp` và các trang Admin liên quan.

## Hiện trạng code
- `RoleServlet` xử lý `/admin/role/*`.
- `ModulePermissionFilter` yêu cầu `VIEW_ROLES` cho route role.
- Role seed gồm 6 actor chính.

## Quy tắc nghiệp vụ chuẩn
- Role phải có tên rõ nghĩa và không phá vỡ `RoleRedirectUtil`.
- Không xóa role đang có user nếu chưa xử lý dữ liệu liên quan.
- Thay đổi role phải cập nhật permission tương ứng.

## Code còn lệch spec hoặc cần bổ sung
- Cần kiểm tra controller có phân biệt quyền xem và quyền sửa role chưa.
- Cần audit cho mọi thay đổi role.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

