# Feature: Dang xuat he thong
Status: Approved
Actor: Authenticated User
Priority: High
Related Code: `LogoutController`

## Muc tieu
Cho phep nguoi dung da dang nhap thoat khoi he thong bang cach huy `HttpSession` va quay ve trang public homepage.

## Route
- `GET /logout`
- `POST /logout`

## Luong chinh
1. Nguoi dung click dang xuat.
2. `LogoutController` lay session hien tai bang `request.getSession(false)`.
3. Neu session ton tai, he thong invalidate session.
4. He thong redirect ve `/homepage`.

## Luong loi
- Neu khong co session, he thong van redirect ve `/homepage`.

## Hien trang code
- Da co `GET /logout` va `POST /logout`.
- Da invalidate session.
- Chua clear cookie remember-me `username` va `password`.

## Acceptance Criteria
- [ ] Sau logout, session cu khong con su dung duoc.
- [ ] Sau logout, nguoi dung quay ve `/homepage`.
- [ ] Goi `/logout` khi chua dang nhap khong gay loi server.

## Missing Work
- [ ] Xoa cookie remember-me khi dang xuat.
- [ ] Neu them token remember-me, phai revoke token o database.
