# Feature: HR Manager quan ly nhan vien
Status: Partial
Actor: HR Manager
Priority: High
Related Code: `EmployeeListController`, `CreateEmployeeController`, `Views/hr/EmployeeList.jsp`, `Views/hr/CreateEmployee.jsp`

## Route
- `GET /hr/employee-list`
- `POST /hr/employee-list`
- `GET /hr/create-employee`
- `POST /hr/create-employee`

## Luong danh sach
1. HR Manager vao `/hr/employee-list`.
2. `EmployeeListController` kiem tra permission `VIEW_EMPLOYEES`.
3. Controller lay danh sach nhan vien, thong ke va filter/search neu co.
4. Forward den `/Views/hr/EmployeeList.jsp`.
5. HR Manager co the xem chi tiet, cap nhat trang thai, sua thong tin hoac xoa theo action POST hien co.

## Luong tao employee
1. HR Manager vao `/hr/create-employee`.
2. `CreateEmployeeController` kiem tra permission `VIEW_EMPLOYEES`.
3. Controller load guest co status `Hired`/`HIRED` va danh sach department hop le.
4. HR Manager nhap thong tin employee va account neu form yeu cau.
5. Controller validate guest, fullName, email, departmentId, status, username, password, hireDate.
6. Controller tao `Employee`.
7. Neu email da co account role `Guest`, controller cap nhat account do sang role `Employee` va gan `EmployeeID`.
8. Neu email chua co account, controller tao `SystemUser` moi role `Employee` va gan `EmployeeID`.
9. Thanh cong redirect ve `/hr/employee-list`.

## UI contract
- JSP lien quan phai theo BetterHR theme va hien thi tieng Viet.
- Nut quay lai HR Home tro ve `/HrHomeController`.
- Khong doi ten input/form action khi chi sua giao dien.

## Acceptance Criteria
- [ ] HR Manager xem duoc danh sach employee.
- [ ] Tao employee thieu thong tin bat buoc bi tu choi.
- [ ] Email/phone neu co rule unique phai duoc validate.
- [ ] Guest da duoc tuyen moi xuat hien trong nguon tao employee.
- [ ] Neu user da dang ky truoc bang email do, he thong khong tao duplicate account ma chuyen account Guest sang Employee.
- [ ] Tao employee thanh cong quay ve `/hr/employee-list`.

## Missing Work
- [ ] Xem lai permission `VIEW_EMPLOYEES` co du cho create/update/delete hay can them `MANAGE_EMPLOYEES`.
- [ ] Them audit log khi tao employee.
- [ ] Cap nhat `CreateEmployeeController`/DAO neu hien tai chua ho tro chuyen account Guest co san sang Employee.
