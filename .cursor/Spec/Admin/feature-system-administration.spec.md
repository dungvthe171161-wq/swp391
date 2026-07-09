# Đặc tả module: Quản trị hệ thống

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Admin quản trị toàn bộ cấu hình hệ thống, user, role, permission và phòng ban.

## Route, controller và JSP liên quan
- `/admin`, `/admin/users`, `/admin/role/*`, `/admin/role-permissions/api`, `/departments`.
- Controller: `AdminController`, `UserController`, `RoleServlet`, `RolePermissionServlet`, `DepartmentController`.
- JSP: `AdminHome.jsp`, `Users.jsp`, `RolePermissionManager.jsp` và các trang Admin liên quan.

## Hiện trạng code
- Admin dashboard đi qua `/admin?action=dashboard`.
- User management có API tạo/sửa/xóa/khóa/reset password.
- Role và role-permission dùng permission động.
- `/departments` đang thiếu bảo vệ trong filter và controller.

## Quy tắc nghiệp vụ chuẩn
- Chỉ Admin có role và permission phù hợp mới được quản trị hệ thống.
- Reset password không được lộ mật khẩu plain text trong response ở thiết kế chuẩn.
- Quản lý role/permission phải có kiểm tra quyền riêng và audit.

## Ma trận route, quyền và dữ liệu cần bổ sung
| Nhóm chức năng | Route/controller | Permission chuẩn cần có | Bảng dữ liệu chính | Ghi chú |
|---|---|---|---|---|
| Dashboard quản trị | `/admin`, `AdminController` | `MANAGE_SYSTEM` hoặc `VIEW_ADMIN_DASHBOARD` | Tổng hợp từ nhiều bảng | Không link thẳng JSP nếu cần nạp dữ liệu. |
| Quản lý người dùng | `/admin/users`, `UserController` | `VIEW_USERS`, `CREATE_USER`, `UPDATE_USER`, `DELETE_USER`, `RESET_USER_PASSWORD` | `SystemUser`, `Role`, `Employee` | Action ghi dữ liệu không dùng chung quyền xem. |
| Quản lý vai trò | `/admin/role/*`, `RoleServlet` | `VIEW_ROLES`, `CREATE_ROLE`, `UPDATE_ROLE`, `DELETE_ROLE` | `Role` | Không xóa role đang được user sử dụng nếu chưa có rule chuyển dữ liệu. |
| Gán quyền vai trò | `/admin/role-permissions/api`, `RolePermissionServlet` | `MANAGE_ROLE_PERMISSIONS` | `Permission`, `RolePermission` | Phải có audit trước/sau khi thay đổi quyền. |
| Quản lý phòng ban | `/departments`, `DepartmentController` | `VIEW_DEPARTMENTS`, `CREATE_DEPARTMENT`, `UPDATE_DEPARTMENT`, `DELETE_DEPARTMENT` | `Department`, `Employee` | Route hiện cần bổ sung filter và controller-level auth. |

## Notification và audit bắt buộc
- Tạo, sửa, khóa/mở khóa, xóa user phải ghi audit gồm `actorUserId`, `targetUserId`, action, dữ liệu trước/sau và thời điểm.
- Reset password phải ghi audit nhưng không lưu mật khẩu tạm hoặc mật khẩu mới.
- Thêm/xóa permission của role phải ghi audit theo từng permission thay đổi.
- Tạo/sửa/xóa department phải ghi audit; nếu department có employee hoặc task liên quan thì phải ghi rõ lý do không cho xóa.
- Notification cho Admin chỉ cần phát khi có lỗi hệ thống, thao tác bảo mật nhạy cảm hoặc yêu cầu duyệt/cảnh báo; không spam notification cho mọi thao tác CRUD thường.

## Checklist nghiệm thu riêng cho Admin
- User không phải Admin không truy cập được `/admin`, `/admin/users`, `/admin/role/*`, `/admin/role-permissions/api`, `/departments`.
- Admin thiếu permission cụ thể bị chặn đúng ở action tương ứng, đặc biệt là API JSON.
- Reset password không trả mật khẩu plain text trong response sau khi code được hardening.
- Thay đổi role-permission có audit và không làm mất quyền quản trị tối thiểu của Admin.
- `/departments` được bảo vệ ở cả filter và controller, không chỉ ẩn link trên UI.

## Code còn lệch spec hoặc cần bổ sung
- Bổ sung filter cho `/departments`.
- Thay reset password trả mật khẩu tạm thời bằng token hoặc kênh an toàn.
- Bổ sung audit cho thay đổi user, role, permission và department.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.
