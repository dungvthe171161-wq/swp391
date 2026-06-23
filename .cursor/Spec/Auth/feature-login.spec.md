# Feature: Dang nhap he thong
Status: Approved
Actor: Guest, SystemUser
Priority: High
Related Code: `LoginController`, `DAO`, `HomepageController`, `Views/Login.jsp`

## Muc tieu
Cho phep nguoi dung da co tai khoan dang nhap vao HRMS bang username/email va mat khau, tao `HttpSession`, luu `systemUser`, sau do dieu huong ve `/homepage`.

## Route va giao dien
- `GET /login`: hien thi `/Views/Login.jsp`.
- `GET /login?success=registered`: hien thong bao tao tai khoan thanh cong.
- `GET /login?success=registered_mail_failed`: hien thong bao account da tao nhung email xac nhan chua gui duoc.
- `POST /login`: xu ly form dang nhap.
- Form hien tai gui parameter `user`, `pass`, `rememberMe`.
- Parameter `user` co the la username hoac email.

## Luong chinh
1. Guest truy cap `/login`.
2. He thong doc cookie neu co va set attribute tu cookie vao request.
3. Guest nhap username va password.
4. `LoginController` lay tai khoan bang `DAO.getInstance().getAccountByUsernameOrEmail(login)`.
5. He thong kiem tra mat khau bang `DAO.getInstance().checkPassword(password, user.getPasswordHash())`.
6. Neu hop le, he thong huy session cu neu co.
7. He thong tao session moi va luu `systemUser`.
8. Neu tick `rememberMe`, he thong luu cookie `username` trong 24 gio; neu khong tick thi xoa cookie nay.
9. He thong redirect ve `/homepage`.
10. `HomepageController` hien thi trang chu va cac dashboard duoc cap quyen theo role.

## Luong loi
- Username/email hoac password rong/sai dinh dang: forward lai `/Views/Login.jsp` va set `mess` tieng Viet.
- Sai username/email hoac password: forward lai `/Views/Login.jsp` va set `mess` tieng Viet.
- Account Google-only khong co password: forward lai login va yeu cau tiep tuc voi Google.
- DB khong ket noi duoc: forward lai login va hien thong bao tieng Viet.
- Session cu ton tai: invalidate truoc khi tao session moi de giam rui ro session fixation.

## Bao mat va rang buoc ky thuat
- Code dung `jakarta.servlet.*`, khong dung `javax.servlet.*`.
- Mat khau trong database nam o `SystemUser.PasswordHash`; code hien tai dang so sanh plain text theo quyet dinh hien tai cua du an, khong dung BCrypt/SHA.
- Cookie remember-me hien tai chi luu username, khong luu password.

## Hien trang code
- Da co `GET /login` va `POST /login`.
- Da co session login va redirect ve `/homepage`.
- Da co remember-me cookie chi luu `username`.
- Da co check active/locked account trong controller login.
- Da co failed-login counter va lock tam thoi theo `FailedLoginAttempt`/`LockedUntil`.
- Da co success message cho `registered`, `registered_mail_failed`, `password_changed`.
- Chua co ghi audit log dang nhap.

## Acceptance Criteria
- [ ] Dang nhap dung tao session moi chua `systemUser`.
- [ ] Dang nhap dung redirect ve `/homepage`.
- [ ] Thieu username/password hien thi loi tren trang login.
- [ ] Sai username/password khong tao session.
- [ ] He thong kiem tra password bang `DAO.checkPassword`.

## Missing Work
- [ ] Sau nay neu can remember-me that, thay cookie username bang token remember-me an toan.
- [ ] Them audit log dang nhap thanh cong/that bai neu can.
