# Feature: Dang ky tai khoan local
Status: Approved
Actor: Guest
Priority: High
Related Code: `RegisterController`, `DAO`, `SystemUser`, `EmailSender`, `Views/Register.jsp`, `LoginController`, `SessionSecurityFilter`

## Muc tieu
Cho phep nguoi dung tao tai khoan local bang username/email/password. Sau khi tao thanh cong, he thong gui email chao mung BetterHR neu SMTP da cau hinh dung.

## Route
- `GET /register`: hien thi form dang ky.
- `POST /register`: validate va tao tai khoan.
- JSP: `/Views/Register.jsp`.

## Public route
`SessionSecurityFilter` phai cho phep guest truy cap:
- `/register`
- `/Views/Register.jsp`

## Luong chinh
1. Guest bam `Tao tai khoan moi` tu trang login/homepage.
2. Browser mo `GET /register`.
3. Guest nhap `username`, `email`, `password`, `confirmPassword`.
4. `RegisterController` validate du lieu bat buoc, dinh dang username/email, do dai password va confirm password.
5. He thong kiem tra trung username va trung email trong `SystemUser`.
6. Neu hop le, controller tao `SystemUser` local.
7. He thong gui email dang ky thanh cong bang `EmailSender`.
8. Neu gui email thanh cong, redirect `/login?success=registered`.
9. Neu tao account thanh cong nhung gui email fail, redirect `/login?success=registered_mail_failed`.

## Business rule tai khoan moi
- Tai khoan dang ky local phai tao voi role `Guest`.
- Tai khoan dang ky local khong tao `EmployeeID` ngay.
- `EmployeeID` phai de `NULL` cho toi khi ung vien duoc tuyen/dang duoc HR Manager chuyen thanh nhan vien trong luong `/hr/create-employee`.
- Khi duoc chuyen thanh nhan vien, he thong moi cap nhat account tu role `Guest` sang role `Employee` va gan `EmployeeID`.
- Password hien tai luu/kiem tra theo logic `SystemUser.PasswordHash` cua du an, chua bat buoc hash migration trong spec nay.

## Email dang ky thanh cong
- Subject nen la: `Dang ky thanh cong BetterHR`.
- Noi dung email can co: loi chao, username, email, thong bao co the dang nhap vao BetterHR.
- SMTP config lay tu bien moi truong/file local ignored, khong hardcode app password trong Java source.

## Error handling
- Username/email/password rong: forward lai `/Views/Register.jsp` kem `mess`.
- Username sai dinh dang: forward lai form.
- Email sai dinh dang hoac qua dai: forward lai form.
- Password qua ngan/qua dai: forward lai form.
- Confirm password khong khop: forward lai form.
- Username/email da ton tai: forward lai form.
- Tao user fail: forward lai form.
- Gui mail fail sau khi tao user thanh cong: khong rollback user; hien thong bao thanh cong kem canh bao tren login.

## Acceptance Criteria
- [ ] Guest mo duoc `/register` khi chua login.
- [ ] Dang ky username/email hop le tao `SystemUser`.
- [ ] Duplicate username/email bi tu choi.
- [ ] Account moi co role `Guest`.
- [ ] Account moi khong gan `EmployeeID` neu chua qua luong tuyen dung/tao employee.
- [ ] Tao account thanh cong gui email BetterHR neu SMTP dung.
- [ ] Mail fail khong lam mat account da tao.
- [ ] Sau dang ky, user ve login va thay thong bao phu hop.

## Missing Work
- [ ] Sua cac message/email template trong `RegisterController` neu con mojibake.
- [ ] Cap nhat `RegisterController` de dung `DAO.getRoleIdByName("Guest")` thay vi role `Employee`.
- [ ] Them rate limit/captcha neu public register bi spam.
- [ ] Them audit log cho tao account public neu can.
