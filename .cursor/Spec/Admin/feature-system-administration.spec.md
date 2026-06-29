# Đặc tả Module: Quản trị hệ thống (System Administration)
Trạng thái: Đã phê duyệt
Tác nhân: Admin
Độ ưu tiên: Cao
Mã nguồn liên quan: `AdminController`, `UserController`, `RoleServlet`, `RolePermissionServlet`, `DepartmentController`, `AdminAuthorizationFilter`, `ModulePermissionFilter`

## Phạm vi
Admin quản lý nền tảng hệ thống gồm bảng điều khiển (dashboard), người dùng (user), vai trò (role), quyền hạn (permission), phòng ban (department), nhật ký hệ thống (audit log) và thông tin cá nhân (profile) của Admin.

## Các tập tin đặc tả tính năng (Feature files)
- `feature-admin-dashboard.spec.md`
- `feature-admin-user-management.spec.md`
- `feature-admin-role-management.spec.md`
- `feature-admin-role-permission.spec.md`
- `feature-admin-department-management.spec.md`
- `feature-admin-audit-profile.spec.md`

## Nguyên tắc truy cập
- Admin bắt buộc phải có thông tin phiên đăng nhập `systemUser` trong session.
- Các tuyến đường (route) `/admin` và `/admin/*` đang được bảo vệ bởi bộ lọc quyền `ModulePermissionFilter`.
- Route `/admin` đang được bảo vệ bổ sung bởi `AdminAuthorizationFilter`.
- Route `/departments` hiện tại được xử lý bởi `DepartmentController`, nhưng chưa được đưa vào `ModulePermissionFilter` và `AdminAuthorizationFilter`; đặc tả này đánh dấu đây là phần việc còn thiếu (missing work) cần được khắc phục.

## Vai trò và quyền hạn (Role & Permission) liên quan
- `MANAGE_SYSTEM`: cho phép truy cập bảng điều khiển quản trị (admin dashboard).
- `VIEW_USERS`: cho phép quản lý người dùng (user).
- `VIEW_ROLES`: cho phép quản lý vai trò (role).
- `MANAGE_ROLE_PERMISSIONS`: cho phép quản lý và gán quyền hạn cho vai trò (role permission).

## Các điểm cần khớp với mã nguồn hiện tại
- Mật khẩu của người dùng trong code được cập nhật thông qua hàm `DAO.changePassword`, không dùng mã hóa SHA-256 trực tiếp trong controller.
- `DAO.changePassword` hiện tại đang ghi trực tiếp giá trị mật khẩu mới vào cột `SystemUser.PasswordHash` dưới dạng văn bản thuần (plain text) theo logic hiện tại của dự án; nếu sau này thay đổi thuật toán băm (hash) thì phải đồng thời cập nhật cả đặc tả và mã nguồn.
- `RoleServlet` xử lý các đường dẫn dạng `/admin/role/*`.
- `RolePermissionServlet` xử lý API tại `/admin/role-permissions/api`.
- `UserController` xử lý đường dẫn `/admin/users`.
- `DepartmentController` xử lý đường dẫn `/departments`.

## Giao ước giao diện (UI contract)
- Tất cả các trang quản trị phải áp dụng chủ đề BetterHR theo đặc tả giao diện chung tại `_Common/ui-language-theme.spec.md`.
- Thanh bên (Sidebar) quản trị phải hiển thị rõ thương hiệu `BetterHR` cùng dòng chữ mô tả phụ bằng tiếng Việt (ví dụ: `Cổng quản trị`).
- Menu đang hoạt động (active menu) phải được đánh dấu nổi bật rõ ràng, không bị mất màu nền hoặc mờ chữ.
- Thanh công cụ phía trên (Topbar) chứa chức năng tìm kiếm, thông báo và thông tin cá nhân có thể thiết kế giao diện tĩnh (UI placeholder) nếu chưa có backend riêng, tránh việc tự bổ sung logic không cần thiết.
- Toàn bộ nội dung chữ (text) hiển thị trong phân hệ Admin phải là tiếng Việt, ngoại trừ logo `BetterHR` và các thông số kỹ thuật đặc thù.

## Các phần việc còn thiếu cấp module
- [ ] Đưa đường dẫn `/departments` vào trong bộ lọc bảo vệ quyền hạn của admin (permission filter).
- [ ] Chuẩn hóa việc ghi nhật ký hoạt động (logging), loại bỏ toàn bộ các câu lệnh `System.out.println` và hàm `printStackTrace` trong các controller khi chạy môi trường production.
- [ ] Chuẩn hóa mã lỗi và cấu trúc thông điệp lỗi JSON cho các API của Admin.
- [ ] Bổ sung mã kiểm thử (test) cho bộ lọc quyền hạn (permission filter) và API quản lý quyền hạn của vai trò (role-permission).
