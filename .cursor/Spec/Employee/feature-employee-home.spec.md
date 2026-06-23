# Feature: Trang chu nhan vien
Status: Partial
Actor: Employee
Priority: Medium
Related Code: `HomepageController`, `Views/Employee/EmployeeHome.jsp`, `RoleAuthorizationFilter`

## Route
- Redirect hien tai tu `/homepage` den `/Views/Employee/EmployeeHome.jsp` voi role Employee.

## Luong chinh
1. Employee da dang nhap truy cap `/homepage`.
2. `HomepageController` nhan role Employee.
3. Redirect den `/Views/Employee/EmployeeHome.jsp`.
4. Filter cho phep role 5 vao `/Views/Employee/`.

## Hien trang code
- Dang redirect truc tiep den JSP.
- Chua co controller home rieng cho Employee.

## Acceptance Criteria
- [ ] Employee login thanh cong vao duoc trang chu nhan vien.
- [ ] Role khac khong duoc vao truc tiep khu vuc Employee neu filter ap dung.

## Missing Work
- [ ] Tao controller Employee Home neu muon dung MVC chuan hon.
