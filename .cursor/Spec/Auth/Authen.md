# Module Spec: Authentication
Status: Approved
Actors: Guest, SystemUser

## Pham vi
Module Auth bao gom dang nhap, dang ky, dang xuat, Google Login, homepage routing, quen mat khau, xac minh PIN va doi mat khau.

## Feature files
- `feature-login.spec.md`
- `feature-register.spec.md`
- `feature-logout.spec.md`
- `feature-homepage-routing.spec.md`
- `feature-password-recovery-change.spec.md`
- `feature-google-login.spec.md`

## Nguyen tac dung voi code hien tai
- Tai khoan dang nhap bang username parameter `user` va password parameter `pass`.
- Login local chap nhan username hoac email.
- Register local dung route `/register`, tao tai khoan `SystemUser` va gui email BetterHR neu SMTP dung.
- Session dang nhap luu attribute `systemUser`.
- Password trong code hien tai duoc kiem tra bang `DAO.checkPassword` va luu/cap nhat tai `SystemUser.PasswordHash` theo logic plain text hien tai cua du an.
- Luong quen mat khau dung `SystemUser.Email`, PIN session `pinCode`, va flag `recoveryVerified` truoc khi cho vao `/changepassRE`.
- `/homepage` la cua vao trung tam hien thi cac dashboard duoc cap quyen theo role, khong auto redirect bat buoc theo role.
- Code dung Jakarta Servlet, khong dung `javax.servlet`.

## Diem can canh bao
- Remember-me hien tai chi luu cookie `username`, khong luu password.
- Google Login da co servlet OAuth2 va callback.
- Login da co `FailedLoginAttempt`, `LockedUntil`, `LastLogin` theo `SystemUser`.
- Mail SMTP phai dung bien moi truong hoac file local ignored, khong hardcode secret trong Java code.
- Role mac dinh cho user dang ky moi la `Guest`, `EmployeeID = NULL`; chi khi HR Manager tao/chuyen thanh nhan vien moi doi sang role `Employee` va gan `EmployeeID`.
