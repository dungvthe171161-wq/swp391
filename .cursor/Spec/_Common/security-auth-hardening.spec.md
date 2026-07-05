# Đặc tả dùng chung: Bảo mật xác thực

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Actor Auth, Admin và mọi user đăng nhập; spec này chốt chuẩn bảo mật cho login, session, đổi mật khẩu và reset password.

## Route, controller và JSP liên quan
- `LoginController`, `RegisterController`, `ForgotPassController`, `RecoveryController`, `ChangePassController`, `ChangePassREController`.
- `DAO` và `SystemUserDAO` đọc/ghi `SystemUser.PasswordHash`.
- `SessionSecurityFilter` và `RoleAuthorizationFilter` xử lý session và route.

## Hiện trạng code
- `DAO.hashPassword` hiện trả lại plain text; `DAO.checkPassword` so sánh plain text.
- Admin reset password trả mật khẩu tạm thời trong JSON response.
- Google Login có backend OAuth2 và tạo user theo `LoginProvider`.
- Session đăng nhập dùng `systemUser`.

## Quy tắc nghiệp vụ chuẩn
- Mật khẩu phải được hash bằng thuật toán mạnh như BCrypt trước khi ghi database.
- Reset password không được trả mật khẩu plain text qua response; nên dùng link hoặc token một lần.
- Luồng quên mật khẩu cần giới hạn số lần thử mã và thời hạn mã.
- Đăng xuất phải invalidate session hiện tại.

## Code còn lệch spec hoặc cần bổ sung
- Code hiện chưa hash mật khẩu dù cột tên là `PasswordHash`.
- Cần migration hoặc cơ chế tương thích khi chuyển từ plain text sang hash.
- Cần bổ sung rate limit và audit cho reset password, đổi mật khẩu, login thất bại.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

