# Feature: Quen mat khau va doi mat khau
Status: Approved
Actor: Guest, Authenticated User
Priority: High
Related Code: `ForgotPassController`, `RecoveryController`, `ChangePassREController`, `ChangePassController`, `LoginController`, `SessionSecurityFilter`, `DAO`, `EmailSender`, `Views/ForgotPassword.jsp`, `Views/Recovery.jsp`, `Views/ChangePasswordRE.jsp`, `Views/ChangePassword.jsp`

## Muc tieu
Ho tro nguoi dung khoi phuc mat khau bang email va ma PIN, sau do tao mat khau moi. Dong thoi ho tro user da dang nhap doi mat khau trong tai khoan.

## Route hien co
- `GET /ForgotPassword`: hien thi form nhap email khoi phuc.
- `POST /ForgotPassword`: kiem tra email va gui ma PIN.
- `GET /Recovery`: hien thi form nhap PIN.
- `POST /Recovery`: xac minh PIN.
- `GET /changepassRE`: hien thi form tao mat khau moi sau khi PIN dung.
- `POST /changepassRE`: cap nhat mat khau moi.
- `GET/POST /changepass`: doi mat khau khi user da dang nhap.
- `GET /login?success=password_changed`: hien thi thong bao doi mat khau thanh cong.

## Public route trong filter
`SessionSecurityFilter` phai cho phep cac route sau khi chua dang nhap:
- `/ForgotPassword`
- `/Recovery`
- `/changepassRE`
- `/ForgotPassword.jsp`
- `/Views/ForgotPassword.jsp`

Luu y: `/changepassRE` la public o muc filter, nhung controller van phai chan bang session `recoveryEmail` va `recoveryVerified`.

## Luong quen mat khau
1. Guest bam `Quen mat khau?` tu `/login` va mo `/Views/ForgotPassword.jsp` hoac `/ForgotPassword`.
2. Guest nhap email tai khoan.
3. `ForgotPassController` validate email khong rong.
4. `DAO.checkEmailExist(email)` kiem tra trong bang `SystemUser.Email`, khong kiem tra `Employee.Email`.
5. Neu email khong ton tai, forward lai `Views/ForgotPassword.jsp` voi message: `Email nay khong ton tai trong he thong.`
6. Neu email ton tai, he thong tao PIN 6 chu so.
7. `EmailSender.sendEmail(...)` gui PIN qua SMTP.
8. He thong luu vao session:
   - `recoveryEmail = email`
   - `pinCode = pin`
   - session timeout `600` giay
9. He thong redirect sang `/Recovery`.

## Luong xac minh PIN
1. Guest mo `/Recovery`.
2. `RecoveryController` forward den `Views/Recovery.jsp`.
3. Guest nhap PIN.
4. Controller so sanh request parameter `pin` voi session attribute `pinCode`.
5. Neu PIN sai hoac session het han, forward lai `Views/Recovery.jsp` voi message: `Ma PIN khong hop le hoac da het han.`
6. Neu PIN dung, controller set `recoveryVerified = true`.
7. He thong redirect sang `/changepassRE`.

## Luong tao mat khau moi sau PIN
1. Guest mo `/changepassRE`.
2. `ChangePassREController` chi cho vao form khi session co:
   - `recoveryEmail`
   - `recoveryVerified = true`
3. Neu thieu session recovery hoac chua xac minh PIN, redirect ve `/ForgotPassword`.
4. Guest nhap `newPass` va `confirmPass`.
5. Validate mat khau:
   - Bat buoc tu 8 den 16 ky tu.
   - Chi cho phep chu va so theo pattern `[a-zA-Z0-9]+`.
   - `confirmPass` phai trung `newPass`.
6. Controller lay user bang `DAO.getAccountByEmail(recoveryEmail)`.
7. Neu tim thay user, goi `DAO.changePassword(user.getUsername(), newPass)`.
8. Neu cap nhat thanh cong, xoa session:
   - `recoveryEmail`
   - `pinCode`
   - `recoveryVerified`
9. Redirect ve `/login?success=password_changed`.
10. `LoginController` hien message: `Doi mat khau thanh cong. Ban co the dang nhap bang mat khau moi.`

## Luong doi mat khau khi da dang nhap
1. Authenticated user vao `/changepass`.
2. Nhap mat khau hien tai, mat khau moi va xac nhan mat khau.
3. `ChangePassController` kiem tra session `systemUser`.
4. He thong kiem tra mat khau hien tai bang `DAO.checkPassword`.
5. Neu hop le, goi `DAO.changePassword(username, newPass)`.
6. He thong tra ve UI/redirect theo logic hien tai.

## Email sender
`EmailSender` khong hardcode Gmail/app password trong Java code.

Nguon cau hinh uu tien:
1. Bien moi truong:
   - `MAIL_USERNAME`
   - `MAIL_PASSWORD`
   - `MAIL_FROM`
   - `MAIL_FROM_NAME`
   - `MAIL_HOST`
   - `MAIL_PORT`
2. Java system properties cung ten.
3. File local ignored:
   - `src/main/resources/META-INF/mail.local.properties`
   - hoac `META-INF/mail.properties` khi deploy.

File mau:
- `src/main/resources/META-INF/mail.example.properties`

Quy tac:
- Gmail phai dung App Password.
- Khong commit `mail.local.properties`.
- Neu thieu config mail, forgot password phai forward lai form voi message: `Khong the gui email. Vui long thu lai sau.`

## Hien trang code
- `ForgotPassController` da co GET/POST.
- `RecoveryController` da set `recoveryVerified` khi PIN dung.
- `ChangePassREController` da chan truy cap truc tiep neu chua xac minh PIN.
- `SessionSecurityFilter` da public `/ForgotPassword`, `/Recovery`, `/changepassRE`.
- `DAO.checkEmailExist` dang check `SystemUser.Email`.
- Password hien tai duoc luu vao `SystemUser.PasswordHash` theo logic plain text cua du an hien tai, chua hash/BCrypt.
- UI Forgot/Recovery/ChangePasswordRE da theo mau BetterHR va hien thi tieng Viet.

## Acceptance Criteria
- [ ] Guest mo duoc `/ForgotPassword` khi chua dang nhap.
- [ ] Email rong hien loi va khong gui mail.
- [ ] Email khong ton tai trong `SystemUser` hien loi.
- [ ] Email ton tai thi gui PIN va redirect `/Recovery`.
- [ ] PIN sai hoac session het han khong vao duoc form tao mat khau moi.
- [ ] PIN dung redirect sang `/changepassRE`.
- [ ] Mo truc tiep `/changepassRE` khi chua co `recoveryVerified` bi redirect ve `/ForgotPassword`.
- [ ] Mat khau moi duoi 8 hoac tren 16 ky tu bi tu choi.
- [ ] Mat khau moi co ky tu dac biet bi tu choi.
- [ ] Xac nhan mat khau khong khop bi tu choi.
- [ ] Doi mat khau thanh cong cap nhat `SystemUser.PasswordHash`.
- [ ] Doi mat khau thanh cong xoa `recoveryEmail`, `pinCode`, `recoveryVerified`.
- [ ] Doi mat khau thanh cong redirect ve `/login?success=password_changed`.

## Missing Work
- [ ] Sau nay neu bat lai hash password, cap nhat `DAO.changePassword` va spec nay cung luc.
- [ ] Co the them bang/token recovery rieng thay vi luu PIN trong session neu can bao mat cao hon.
- [ ] Co the them rate limit gui PIN de tranh spam email.
