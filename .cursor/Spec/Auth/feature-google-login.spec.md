# Feature: Dang nhap bang Google
Status: Approved
Actor: Guest, SystemUser
Priority: High
Related Code: `GoogleAuthController`, `LoginController`, `DAO`, `DBConnection`, `SessionSecurityFilter`, `constant.Iconstant`, `Views/Login.jsp`

## Muc tieu
Cho phep user dang nhap bang Google OAuth2 doc lap voi luong username/password. Sau khi xac thuc thanh cong, he thong tao session `systemUser` va redirect ve `/homepage` de homepage hien thi quyen theo role hien tai.

## Route
- `GET /auth/google`: tao state va redirect sang Google OAuth2.
- `GET /auth/google/callback`: nhan authorization code, lay token/profile va tao session.
- `GET /loginByGmail`: route legacy cung tro ve xu ly callback theo servlet hien tai.

## Public filter
`SessionSecurityFilter` phai cho phep:
- `/auth/google`
- `/auth/google/callback`
- `/loginByGmail`

## Luong chinh
1. Guest click `Tiep tuc voi Google` tren `/login`.
2. `GoogleAuthController` kiem tra `GOOGLE_CLIENT_ID` va `GOOGLE_CLIENT_SECRET`.
3. Neu thieu config, redirect `/login?error=google_config`.
4. Neu du config, controller tao `googleOAuthState`, luu `googleRedirectUri` vao session, redirect sang Google voi scope `openid email profile`.
5. Google callback ve `/auth/google/callback` kem `code` va `state`.
6. Controller kiem tra state. Sai state redirect `/login?error=google_state`.
7. Controller doi code lay token.
8. Controller validate `id_token` bang Google token info va kiem tra audience khop `GOOGLE_CLIENT_ID`.
9. Controller lay profile gom `GoogleID/sub`, `Email`, `Name`, `AvatarUrl`.
10. Neu profile thieu GoogleID hoac Email, redirect `/login?error=google_profile`.
11. Neu database khong ket noi duoc, redirect `/login?error=db`.
12. He thong resolve user theo thu tu:
    - Tim `SystemUser` theo `GoogleID`.
    - Neu chua co, tim theo `Email`.
    - Neu email da ton tai, update `GoogleID`, `AvatarUrl`, reset failed login/lock, cap nhat `LastLogin`.
    - Neu email chua ton tai, tao `SystemUser` moi voi `LoginProvider='GOOGLE'`, `PasswordHash=NULL`, `RoleID=Guest`, `EmployeeID=NULL`, `IsActive=TRUE`.
13. Neu user inactive, redirect `/login?error=inactive`.
14. Neu user dang bi lock, redirect `/login?error=locked`.
15. Controller invalidate session cu, tao session moi, set `systemUser`.
16. Redirect ve `/homepage`.

## Gia tri mac dinh khi tao Google user moi
- `Username`: tao tu email/name, tranh trung.
- `Email`: email Google da lower-case.
- `PasswordHash`: `NULL`.
- `GoogleID`: profile `sub`.
- `AvatarUrl`: profile `picture`.
- `LoginProvider`: `GOOGLE`.
- `RoleID`: role `Guest` qua `DAO.getOrCreateRoleIdByName("Guest")`.
- `EmployeeID`: `NULL`.
- `FailedLoginAttempt`: `0`.
- `LockedUntil`: `NULL`.
- `LastLogin`: `NOW()`.
- `IsActive`: `TRUE`.
- `CreatedDate`: `NOW()`.

## Bao mat
- Client ID/secret doc tu env/system property hoac `META-INF/google.properties`, khong hardcode trong Java.
- OAuth state bat buoc khop de tranh CSRF.
- `id_token` phai validate audience truoc khi tin profile.
- Khong luu access token vao client/session.
- Duplicate email duoc xu ly bang lien ket tai khoan qua `Email`.
- Duplicate GoogleID duoc xu ly bang lookup `GoogleID`.
- Tai khoan inactive/locked khong duoc tao session.

## Hien trang code
- Da co nut Google tren `Views/Login.jsp`.
- Da co `GoogleAuthController` cho `/auth/google`, `/auth/google/callback`, `/loginByGmail`.
- Da co DAO: `getAccountByGoogleId`, `getAccountByEmail`, `updateGoogleAccount`, `createGoogleUser`.
- Da redirect ve `/homepage` sau khi Google login thanh cong.

## Acceptance Criteria
- [ ] Thieu Google config hien `/login?error=google_config`.
- [ ] Click Google login redirect sang Google dung client ID va callback URL.
- [ ] Callback sai state bi tu choi.
- [ ] Token sai audience bi tu choi.
- [ ] GoogleID ton tai dang nhap vao dung user.
- [ ] Email ton tai nhung chua co GoogleID duoc lien ket Google.
- [ ] Email moi tao user moi role `Guest`.
- [ ] User inactive/locked khong duoc dang nhap.
- [ ] Dang nhap thanh cong tao session `systemUser` va ve `/homepage`.

## Missing Work
- [ ] Them test tich hop cho OAuth callback bang mock Google response.
- [ ] Chuan hoa log loi OAuth va DB theo logging chung cua project.
