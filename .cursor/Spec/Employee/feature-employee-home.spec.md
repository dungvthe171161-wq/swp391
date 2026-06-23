# Feature: Trang chu nhan vien
Status: Partial
Actor: Employee
Priority: Medium
Related Code: `HomepageController`, `Views/Employee/EmployeeHome.jsp`, `RoleAuthorizationFilter`


## Luu y pham vi
File nay mo ta hien trang Employee Home trong code hien co. Khi gen code moi, uu tien target design trong `feature-employee-dashboard-summary.spec.md`.


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

- [ ] Khi implement moi, doi sang route `/employee/home` neu theo module Employee Self Service.

