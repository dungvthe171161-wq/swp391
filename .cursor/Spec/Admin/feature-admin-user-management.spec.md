# Tính năng Admin: Quản lý người dùng

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Admin tạo, cập nhật, khóa/mở khóa, xóa và reset mật khẩu user.

## Route, controller và JSP liên quan
- `/admin`, `/admin/users`, `/admin/role/*`, `/admin/role-permissions/api`, `/departments`.
- Controller: `AdminController`, `UserController`, `RoleServlet`, `RolePermissionServlet`, `DepartmentController`.
- JSP: `AdminHome.jsp`, `Users.jsp`, `RolePermissionManager.jsp` và các trang Admin liên quan.

## Hiện trạng code
- `UserController` xử lý `/admin/users` và các action JSON.
- Permission hiện dùng `VIEW_USERS` cho nhiều thao tác.
- Reset password trả mật khẩu tạm thời trong JSON.

## Quy tắc nghiệp vụ chuẩn
- Tách permission xem, tạo, sửa, xóa, reset nếu triển khai phân quyền chi tiết.
- Không trả mật khẩu plain text trong response.
- Mọi thay đổi user phải validate và audit.

## Code còn lệch spec hoặc cần bổ sung
- Cần đổi cơ chế reset password.
- Cần xem lại permission cho action ghi dữ liệu.
- Cần đảm bảo mật khẩu được hash khi code bảo mật được nâng cấp.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

