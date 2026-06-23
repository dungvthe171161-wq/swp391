# Module Spec: System Administration
Status: Approved
Actor: Admin
Priority: High
Related Code: `AdminController`, `UserController`, `RoleServlet`, `RolePermissionServlet`, `DepartmentController`, `AdminAuthorizationFilter`, `ModulePermissionFilter`

## Pham vi
Admin quan ly nen tang he thong gom dashboard, user, role, permission, department, audit log va profile admin.

## Feature files
- `feature-admin-dashboard.spec.md`
- `feature-admin-user-management.spec.md`
- `feature-admin-role-management.spec.md`
- `feature-admin-role-permission.spec.md`
- `feature-admin-department-management.spec.md`
- `feature-admin-audit-profile.spec.md`

## Nguyen tac truy cap
- Admin phai co session `systemUser`.
- Route `/admin` va `/admin/*` dang duoc bao ve boi `ModulePermissionFilter`.
- Route `/admin` dang duoc bao ve them boi `AdminAuthorizationFilter`.
- Route `/departments` hien co `DepartmentController`, nhung chua nam trong `ModulePermissionFilter`/`AdminAuthorizationFilter`; spec nay danh dau day la missing work can sua.

## Role va permission lien quan
- `MANAGE_SYSTEM`: truy cap admin dashboard.
- `VIEW_USERS`: quan ly user.
- `VIEW_ROLES`: quan ly role.
- `MANAGE_ROLE_PERMISSIONS`: quan ly gan permission cho role.

## Diem can dung voi code hien tai
- Password user trong code duoc cap nhat qua `DAO.changePassword`, khong phai SHA-256 trong controller.
- `DAO.changePassword` hien dang ghi gia tri moi vao `SystemUser.PasswordHash` theo logic plain text hien tai cua du an; neu sau nay doi hash thi phai cap nhat spec va code cung luc.
- `RoleServlet` dung `/admin/role/*`.
- `RolePermissionServlet` dung `/admin/role-permissions/api`.
- `UserController` dung `/admin/users`.
- `DepartmentController` dung `/departments`.

## UI contract
- Tat ca trang Admin dung BetterHR theme theo `_Common/ui-language-theme.spec.md`.
- Sidebar Admin giu brand `BetterHR` va subtitle tieng Viet nhu `Cong quan tri`.
- Menu active phai hien ro, khong mat mau/chu.
- Topbar search/notification/profile co the la UI neu chua co backend rieng, khong tu y them logic.
- Tat ca text hien thi trong Admin phai la tieng Viet, tru logo `BetterHR` va gia tri ky thuat.

## Missing Work cap module
- [ ] Them `/departments` vao filter bao ve admin/permission.
- [ ] Chuan hoa logging, bo `System.out.println` va `printStackTrace` trong controller production.
- [ ] Chuan hoa JSON error status code cho admin API.
- [ ] Them test cho permission filter va API role-permission.
