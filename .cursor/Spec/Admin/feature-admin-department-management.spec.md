# Tính năng Admin: Quản lý phòng ban

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Admin quản lý phòng ban và dữ liệu liên quan tới Dept Manager.

## Route, controller và JSP liên quan
- `/admin`, `/admin/users`, `/admin/role/*`, `/admin/role-permissions/api`, `/departments`.
- Controller: `AdminController`, `UserController`, `RoleServlet`, `RolePermissionServlet`, `DepartmentController`.
- JSP: `AdminHome.jsp`, `Users.jsp`, `RolePermissionManager.jsp` và các trang Admin liên quan.

## Hiện trạng code
- `DepartmentController` mapping `/departments`.
- Code hiện chưa có kiểm tra session/role/permission ngay trong controller.
- `/departments` không nằm trong `ModulePermissionFilter`.

## Quy tắc nghiệp vụ chuẩn
- Chỉ Admin hoặc role được cấp quyền quản lý phòng ban mới được truy cập.
- Không xóa phòng ban đang có employee/task nếu chưa có chính sách xử lý.
- Dữ liệu phòng ban phải giữ toàn vẹn khóa ngoại.

## Code còn lệch spec hoặc cần bổ sung
- Bắt buộc bổ sung filter hoặc kiểm tra quyền trong `DepartmentController`.
- Cần thêm audit cho tạo/sửa/xóa department.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

