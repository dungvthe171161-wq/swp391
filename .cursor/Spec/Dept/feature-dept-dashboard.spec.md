# Feature: Dashboard phong ban
Status: Approved
Actor: Dept Manager
Priority: Medium
Related Code: `DeptController`, `Views/DeptManager/deptHome.jsp`

## Route
- `GET /dept`
- `GET /dept?action=dashboard`

## Luong chinh
1. Dept Manager truy cap `/dept`.
2. He thong kiem tra session `systemUser`.
3. Filter kiem tra role/permission.
4. Controller lay du lieu can thiet cho phong ban.
5. Forward den `/Views/DeptManager/deptHome.jsp`.

## Hien trang code
- Da co `DeptController`.
- Da co redirect ve login khi chua co session.

## Acceptance Criteria
- [ ] Dept Manager vao duoc dashboard phong ban.
- [ ] User chua login bi chuyen ve login.
- [ ] User khong dung role/permission bi filter chan.
