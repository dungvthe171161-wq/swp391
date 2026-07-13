# Tính năng Admin: Quản lý phòng ban

Trạng thái: Đã rà soát theo code ngày 2026-07-13.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Admin quản lý phòng ban và dữ liệu liên quan tới Dept Manager.

## Route, controller và JSP liên quan
- `/admin`, `/admin/users`, `/admin/role/*`, `/admin/role-permissions/api`, `/departments`.
- Controller: `AdminController`, `UserController`, `RoleServlet`, `RolePermissionServlet`, `DepartmentController`.
- JSP: `AdminHome.jsp`, `Users.jsp`, `Departments.jsp`, `RolePermissionManager.jsp` và các trang Admin liên quan.

## Hiện trạng code
- `DepartmentController` mapping `/departments`.
- `/departments` được bảo vệ bởi `RoleAuthorizationFilter`, `ModulePermissionFilter` và guard trong `DepartmentController`.
- CRUD phòng ban hiện dùng chung permission `VIEW_DEPARTMENTS` cho mọi thao tác.

## Quy tắc nghiệp vụ chuẩn
- Chỉ Admin hoặc role được cấp quyền quản lý phòng ban mới được truy cập.
- Tạo/sửa/xóa phòng ban nên dùng permission riêng: `CREATE_DEPARTMENT`, `UPDATE_DEPARTMENT`, `DELETE_DEPARTMENT`.
- Không xóa phòng ban đang có employee/task nếu chưa có chính sách xử lý.
- Dữ liệu phòng ban phải giữ toàn vẹn khóa ngoại.

## Code còn lệch spec hoặc cần bổ sung
- Department CRUD vẫn dùng chung `VIEW_DEPARTMENTS`; thiếu permission tách theo thao tác.
- Cần thêm audit cho tạo/sửa/xóa department.
- Reset password và quản lý user/role/permission còn thiếu audit đầy đủ.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- User không có quyền không truy cập được `/departments`.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
