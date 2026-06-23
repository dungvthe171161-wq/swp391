# Feature: Quan ly phong ban
Status: Partial
Actor: Admin
Priority: High
Related Code: `DepartmentController`, `DepartmentDAO`, `Admin/Departments.jsp`

## Route
- `GET /departments`
- `GET /departments?action=departments`
- `GET /departments?action=edit`
- `GET /departments?action=permissions`
- `POST /departments?action=department-save`
- `POST /departments?action=department-delete`
- `POST /departments?action=department-permissions-save`

## Luong danh sach
1. Admin vao `/departments`.
2. Controller doc search/filter/page.
3. Truy van department bang PreparedStatement.
4. Forward den `Admin/Departments.jsp`.

## Luong them/sua/xoa
1. Admin mo form them hoac edit.
2. Submit `department-save`.
3. Controller validate va luu.
4. Xoa department bang `department-delete` neu hop le.
5. Redirect ve `/departments?action=departments`.

## Hien trang code
- Controller `/departments` da co.
- Co bug redirect trong save permissions ve `/department?action=departments` singular, trong khi route dung la `/departments`.
- Route `/departments` chua nam trong `ModulePermissionFilter`/`AdminAuthorizationFilter`.

## Acceptance Criteria
- [ ] Admin xem duoc danh sach department.
- [ ] Them/sua/xoa department redirect ve dung `/departments?action=departments`.
- [ ] User khong phai Admin khong vao duoc `/departments`.

## Missing Work
- [ ] Them `/departments` vao filter bao ve.
- [ ] Sua redirect singular `/department` thanh `/departments`.
- [ ] Them audit log cho thay doi department.
