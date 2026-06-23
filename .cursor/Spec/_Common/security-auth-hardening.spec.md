# Cross-cutting Spec: Auth Security Hardening
Status: Approved
Priority: High
Related Code: `LoginController`, `RegisterController`, `LogoutController`, `DAO`, `SessionSecurityFilter`, `ForgotPassController`, `RecoveryController`, `ChangePassREController`, `EmailSender`

## Muc tieu
Lam ro cac yeu cau bao mat cho dang nhap, session, cookie, reset password va cau hinh SMTP.

## Hien trang dang dung
- Password nam trong `SystemUser.PasswordHash`.
- Theo quyet dinh hien tai cua du an, `DAO.checkPassword` va `DAO.changePassword` dang xu ly plain text, chua dung BCrypt/SHA.
- Khong con `PasswordMigrationNotAuto`.
- Login thanh cong invalidate session cu truoc khi tao session moi.
- Session login luu `systemUser`.
- Remember-me hien tai chi luu cookie `username`, khong luu password.
- Login co check `IsActive`.
- Login co check `LockedUntil`.
- Login sai goi `recordFailedLogin`, khoa tam thoi sau 5 lan sai trong 15 phut.
- Quen mat khau dung `SystemUser.Email`, PIN session `pinCode`, flag `recoveryVerified`.
- Route recovery public o filter nhung controller van chan bang session recovery.
- Email SMTP lay tu bien moi truong hoac file local ignored, khong hardcode secret trong Java code.

## Route public can co
- `/login`
- `/register`
- `/auth/google`
- `/auth/google/callback`
- `/loginByGmail`
- `/homepage`
- `/ForgotPassword`
- `/Recovery`
- `/changepassRE`
- `/Views/ForgotPassword.jsp`
- `/Views/Register.jsp`

## Reset password security
1. User nhap email tai `/ForgotPassword`.
2. Chi email ton tai trong `SystemUser.Email` moi duoc gui PIN.
3. PIN luu trong session `pinCode`.
4. Session recovery timeout 600 giay.
5. PIN dung set `recoveryVerified = true`.
6. `/changepassRE` phai yeu cau ca `recoveryEmail` va `recoveryVerified`.
7. Doi mat khau thanh cong phai xoa `recoveryEmail`, `pinCode`, `recoveryVerified`.

## Email config security
- File mau: `src/main/resources/META-INF/mail.example.properties`.
- File that local: `src/main/resources/META-INF/mail.local.properties`.
- `mail.local.properties` phai nam trong `.gitignore`.
- Gmail phai dung App Password.
- Khong commit Gmail password/app password.

## Hardening de xuat sau nay
1. Chuyen `PasswordHash` sang BCrypt hoac Argon2.
2. Viet migration rieng cho password seed/data mau neu can.
3. Them bang remember-token neu muon remember-me that.
4. Them rate limit gui PIN theo email/IP.
5. Luu reset token/PIN vao DB voi expiry/revoked thay vi chi dung session neu can bao mat cao hon.
6. Them audit log cho `LOGIN_SUCCESS`, `LOGIN_FAILED`, `RESET_PASSWORD`, `CHANGE_PASSWORD`.
7. Them rate limit/captcha cho public register neu bi spam.

## Acceptance Criteria
- [ ] Cookie remember-me khong chua password.
- [ ] Logout invalidate session.
- [ ] Login sai khong tao session.
- [ ] Inactive account khong tao session.
- [ ] Locked account khong tao session.
- [ ] PIN sai khong vao duoc `/changepassRE`.
- [ ] Mo truc tiep `/changepassRE` khi chua verify PIN bi redirect ve `/ForgotPassword`.
- [ ] SMTP secret khong nam trong Java source.
- [ ] File mail config local khong bi commit.
- [ ] Register public validate duplicate username/email, tao role `Guest` va khong tu gan `EmployeeID`.

## Missing Work
- [ ] Chua co BCrypt/Argon2, day la hardening tuong lai neu project doi yeu cau.
- [ ] Chua co bang reset token rieng.
- [ ] Chua co rate limit gui PIN.
- [ ] Chua co audit log day du cho auth events.
