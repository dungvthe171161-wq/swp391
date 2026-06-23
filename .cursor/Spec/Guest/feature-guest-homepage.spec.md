# Feature: Guest xem homepage public
Status: Approved
Actor: Guest Candidate
Priority: Medium
Related Code: `HomepageController`, `Views/Homepage.jsp`, `Views/Login.jsp`, `Views/Register.jsp`

## Route
- `GET /homepage`

## Luong chinh
1. Guest truy cap `/homepage`.
2. `HomepageController` khong tim thay session `systemUser`.
3. Controller tao `dashboardAccess` theo quyen Guest/public.
4. Forward den `/Views/Homepage.jsp`.
5. Guest xem noi dung public, job/link public va nut dieu huong dang nhap/dang ky.

## Dieu huong
- Link logo `BetterHR` tro ve `/homepage`.
- Nut `Dang nhap` tro ve `/login`.
- Nut `Dang ky` tro ve `/register`, khong tro nham ve `/login`.
- Link viec lam tro den route job list hien co.

## Acceptance Criteria
- [ ] Guest vao `/homepage` khong bi bat dang nhap.
- [ ] Homepage hien giao dien public dung BetterHR theme.
- [ ] Nut dang ky mo dung `/register`.
- [ ] Guest chi thay khu vuc public, khong thay dashboard noi bo neu chua login.

## Missing Work
- [ ] Them test cho trang homepage public neu co test UI/integration.
