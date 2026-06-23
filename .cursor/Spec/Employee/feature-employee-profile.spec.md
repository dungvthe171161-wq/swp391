# Feature: Ho so ca nhan nhan vien
Status: Approved
Actor: Employee, Authenticated User
Priority: Medium
Related Code: `ProfilepageController`, `Views/Profilepage.jsp`, `Views/Employee/EmployeeProfile.jsp`

## Route
- `/profilepage`
- JSP lien quan: `/Views/Profilepage.jsp`, `/Views/Employee/EmployeeProfile.jsp`

## Luong chinh
1. User da dang nhap mo trang profile.
2. Controller lay thong tin tu session va DAO.
3. Forward den JSP profile.
4. Nguoi dung xem thong tin ca nhan va thong tin tai khoan.

## Acceptance Criteria
- [ ] Chua dang nhap khong xem duoc profile.
- [ ] Profile hien thi dung thong tin user hien tai.
- [ ] Khong hien thi thong tin user khac.

## Missing Work
- [ ] Neu cho phep cap nhat profile, can spec rieng cho validation va fields duoc sua.
