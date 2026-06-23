# Feature: Admin Dashboard
Status: Approved
Actor: Admin
Priority: High
Related Code: `AdminController`, `DashboardDAO`, `EmployeeDAO`, `DepartmentDAO`, `SystemUserDAO`, `Admin/AdminHome.jsp`

## Route
- `GET /admin`
- `GET /admin?action=dashboard`
- `GET /admin?action=dashboard-data`

## Luong dashboard HTML
1. Admin truy cap `/admin` hoac `/admin?action=dashboard`.
2. Filter kiem tra session va permission `MANAGE_SYSTEM`.
3. `AdminController` lay thong ke tong quan tu cac DAO.
4. He thong set attribute cho request.
5. Forward den `Admin/AdminHome.jsp`.

## Luong dashboard JSON
1. Admin goi `/admin?action=dashboard-data`.
2. Controller lay du lieu thong ke.
3. Response tra `application/json` va `UTF-8`.

## Du lieu hien thi
- Tong nhan vien.
- Tong phong ban.
- Tong user.
- Thong ke hoat dong, phong ban, permission, status distribution neu DAO tra ve.

## UI contract
- Dashboard admin dung sidebar/topbar BetterHR dong bo voi cac trang Admin khac.
- Card thong ke co nhan tieng Viet va mau theo BetterHR palette.
- Chart phong ban va trang thai nhan vien phai co kich thuoc on dinh, khong tran layout.
- Neu chart chua co du lieu that, UI phai hien empty state tieng Viet thay vi loi JS.

## Hien trang code
- Da co dashboard HTML va JSON.
- JSON trong `AdminController` con duoc build thu cong bang `StringBuilder`.
- Mot so exception con dung `printStackTrace`.

## Acceptance Criteria
- [ ] Admin co quyen xem duoc dashboard.
- [ ] User khong co quyen bi chan truoc khi vao controller.
- [ ] JSON dashboard set dung content type va encoding.
- [ ] Loi database khong lam lo stack trace ra UI.
- [ ] Dashboard khong con UI cu de len UI moi.
- [ ] Tat ca nhan/label hien tieng Viet.
