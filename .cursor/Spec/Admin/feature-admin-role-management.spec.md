# Feature: Quan ly vai tro
Status: Approved
Actor: Admin
Priority: High
Related Code: `RoleServlet`, `RoleDAO`, `Admin/Roles.jsp`

## Route
- `GET /admin/role/*`
- `POST /admin/role/*`
- `PUT /admin/role/*`
- `DELETE /admin/role/*`

## Luong danh sach
1. Admin vao man hinh role.
2. Filter yeu cau permission `VIEW_ROLES`.
3. `RoleServlet` lay danh sach role.
4. Phan trang voi `DEFAULT_PAGE_SIZE = 10`.
5. Forward den `Admin/Roles.jsp` hoac tra JSON theo request API.

## Luong CRUD
1. Admin tao role bang POST.
2. Admin xem chi tiet role bang GET co id.
3. Admin cap nhat role bang PUT.
4. Admin xoa role bang DELETE neu role khong bi rang buoc.

## Hien trang code
- Da co `RoleServlet` route `/admin/role/*`.
- Da co JSON content type cho API.
- Permission filter nhan dien API role khi path co id hoac method khac GET.

## Acceptance Criteria
- [ ] Role list dung page size 10.
- [ ] Tao/sua/xoa role tra JSON ro rang.
- [ ] User thieu `VIEW_ROLES` bi chan.
- [ ] Loi validation khong tao role rong ten.

## Missing Work
- [ ] Dinh nghia rule khong cho xoa role dang co user.
- [ ] Them audit log khi thay doi role.
