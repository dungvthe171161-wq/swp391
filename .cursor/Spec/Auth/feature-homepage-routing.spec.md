# Feature: Homepage hien thi quyen theo role
Status: Approved
Actor: Guest, Admin, HR Manager, HR Staff, Dept Manager, Employee
Priority: High
Related Code: `HomepageController`, `Views/Homepage.jsp`

## Muc tieu
Dung `/homepage` lam cua vao trung tam. Sau moi luong dang nhap local/Google hoac doi mat khau thanh cong, user quay ve login/homepage theo luong auth, va homepage hien thi cac khu vuc/dashboard ma role hien tai duoc cap quyen.

## Route
- `GET /homepage`

## Luong guest
1. Guest truy cap `/homepage`.
2. Neu chua co `systemUser` trong session, `HomepageController` set `dashboardAccess` mac dinh cho guest.
3. He thong forward den `/Views/Homepage.jsp`.
4. Guest xem trang public va danh sach link public.
5. Nut `Dang ky` tren homepage tro toi `/register`, khong tro nham ve `/login`.

## Luong da dang nhap
1. User truy cap `/homepage`.
2. `HomepageController` lay `systemUser` tu session.
3. Controller lay role hien tai va tao object `dashboardAccess`.
4. He thong forward den `/Views/Homepage.jsp`, khong auto redirect bat buoc theo role.
5. JSP hien thi cac nut/dashboard tuong ung:
   - Admin: khu vuc Admin, HR/HR Staff neu duoc cap, Employee, Guest.
   - HR Manager: khu vuc HR Manager, Employee, Guest.
   - HR Staff: khu vuc HR Staff, Employee, Guest.
   - Dept Manager: khu vuc Dept Manager/Employee/Guest neu code cap quyen.
   - Employee: khu vuc Employee va Guest.
   - Guest role: chi khu vuc Guest/public.

## Hien trang code
- Da co route `/homepage`.
- Da forward ve `/Views/Homepage.jsp`.
- Da dung `dashboardAccess` de render menu theo role.
- Login local va Google deu redirect ve `/homepage` sau khi tao session.

## Acceptance Criteria
- [ ] Guest vao `/homepage` thay trang public.
- [ ] User da dang nhap vao `/homepage` van o homepage, khong bi redirect bat buoc.
- [ ] Homepage hien dashboard/menu theo role hien tai.
- [ ] Role khong hop le hoac thieu role fallback ve quyen Guest/public.
- [ ] Nut/link dashboard tren homepage tro dung route code hien tai.
- [ ] Nut `Dang ky` tren homepage mo dung `/register`.

## Missing Work
- [ ] Chuan hoa mapping role ID/role name trong tai lieu he thong de tranh nham lan.
- [ ] Them test cho `dashboardAccess` theo tung role.
