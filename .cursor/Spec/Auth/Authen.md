# Đặc tả module: Xác thực

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Người dùng chưa đăng nhập và mọi actor đăng nhập vào HRMS.

## Route, controller và JSP liên quan
- `/login`, `/logout`, `/register`, `/homepage`, `/ForgotPassword`, `/Recovery`, `/changepass`, `/changepassRE`.
- `/auth/google`, `/auth/google/callback`, `/loginByGmail`.
- Controller: `LoginController`, `LogoutController`, `RegisterController`, `GoogleAuthController`, `HomepageController` và controller đổi/quên mật khẩu.

## Hiện trạng code
- Đăng nhập local đọc `SystemUser` và so sánh mật khẩu bằng `DAO.checkPassword`.
- Role redirect hiện dùng `RoleRedirectUtil`.
- Google Login đã có backend OAuth2.
- Mật khẩu hiện lưu/so sánh dạng plain text trong cột `PasswordHash`.

## Quy tắc nghiệp vụ chuẩn
- Đăng nhập thành công phải set session `systemUser` và redirect đúng dashboard.
- Đăng xuất phải invalidate session.
- Đăng ký local mặc định role Guest, `EmployeeID = NULL`.
- Mật khẩu thiết kế chuẩn phải hash, không lưu plain text.

## Code còn lệch spec hoặc cần bổ sung
- Cần nâng cấp hashing mật khẩu và migration tương thích.
- Cần rate limit login/quên mật khẩu.
- Cần đảm bảo Google config lấy từ môi trường khi deploy.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

