# Feature: Quan ly nguoi dung
Status: Approved
Actor: Admin
Priority: High
Related Code: `UserController`, `SystemUserDAO`, `RoleDAO`, `EmployeeDAO`, `Admin/Users.jsp`

## Route
- `GET /admin/users`
- `GET /admin/users?action=getUser`
- `GET /admin/users?action=checkUsername`
- `GET /admin/users?action=toggleStatus`
- `GET /admin/users?action=delete`
- `POST /admin/users?action=save`
- `POST /admin/users?action=update`
- `POST /admin/users?action=resetPassword`
- `POST /admin/users?action=toggleStatus`
- `POST /admin/users?action=delete`

## Luong danh sach
1. Admin vao `/admin/users`.
2. Filter yeu cau permission `VIEW_USERS`.
3. Controller doc filter `role`, `status`, `department`, `username`.
4. Lay danh sach user co phan trang, page size mac dinh 10.
5. Forward den `Admin/Users.jsp`.

## Luong tao user
1. Admin submit form voi `username`, `password`, `roleId`, `employeeId`, `isActive`.
2. Controller validate username/password/role/employee.
3. Tao user moi.
4. Goi `DAO.changePassword(username, password)` de cap nhat `SystemUser.PasswordHash` theo logic password hien tai cua du an.
5. Tra JSON/redirect theo action hien tai.

## Luong cap nhat user
1. Admin submit thong tin user.
2. Controller validate du lieu.
3. Cap nhat role, employee, status va thong tin lien quan.

## Luong reset password
1. Admin goi `resetPassword`.
2. Controller sinh mat khau tam bang `SecureRandom`.
3. Goi `DAO.changePassword`.
4. Tra mat khau tam trong response JSON theo code hien tai.

## Hien trang code
- Da co CRUD user co filter va phan trang.
- Da co reset password.
- Mat khau khong dung SHA-256 trong controller; `DAO.changePassword` cap nhat `SystemUser.PasswordHash` theo logic plain text hien tai.
- Tra plaintext temp password sau reset la rui ro can can nhac.

## Acceptance Criteria
- [ ] User list co phan trang 10 ban ghi/trang.
- [ ] Tao user thieu username/password/role/employee bi tu choi.
- [ ] Username trung bi tu choi.
- [ ] Reset password cap nhat `SystemUser.PasswordHash` trong DB.

## Missing Work
- [ ] Khong tra temp password plaintext neu chuyen sang gui email an toan.
- [ ] Ghi audit log cho create/update/delete/reset password.
- [ ] Chuan hoa response JSON va HTTP status.
