# Tính năng Auth: Đăng nhập

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- User đăng nhập bằng username/email và mật khẩu.

## Route, controller và JSP liên quan
- `/login`, `/logout`, `/register`, `/homepage`, `/ForgotPassword`, `/Recovery`, `/changepass`, `/changepassRE`.
- `/auth/google`, `/auth/google/callback`, `/loginByGmail`.
- Controller: `LoginController`, `LogoutController`, `RegisterController`, `GoogleAuthController`, `HomepageController` và controller đổi/quên mật khẩu.

## Hiện trạng code
- `LoginController` kiểm tra user active, password và redirect theo role.
- Thông báo lỗi dùng tiếng Việt.
- Nếu user không có password local, luồng login local bị chặn.

## Quy tắc nghiệp vụ chuẩn
- Không tiết lộ user tồn tại hay không qua thông báo quá chi tiết.
- Login thành công set `systemUser` và không giữ mật khẩu trong session.
- Route sau login phải dùng `RoleRedirectUtil`.

## Code còn lệch spec hoặc cần bổ sung
- Cần hash mật khẩu.
- Cần lock/rate limit sau nhiều lần sai.
- Cần test user inactive và user Google-only.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

