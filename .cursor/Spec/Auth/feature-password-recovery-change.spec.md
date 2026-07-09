# Tính năng Auth: Khôi phục và đổi mật khẩu

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- User quên mật khẩu hoặc đổi mật khẩu khi đã đăng nhập.

## Route, controller và JSP liên quan
- `/login`, `/logout`, `/register`, `/homepage`, `/ForgotPassword`, `/Recovery`, `/changepass`, `/changepassRE`.
- `/auth/google`, `/auth/google/callback`, `/loginByGmail`.
- Controller: `LoginController`, `LogoutController`, `RegisterController`, `GoogleAuthController`, `HomepageController` và controller đổi/quên mật khẩu.

## Hiện trạng code
- Luồng quên mật khẩu dùng controller `ForgotPassController`, `RecoveryController`, `ChangePassREController`.
- Đổi mật khẩu khi đăng nhập dùng `ChangePassController` hoặc profile controller.
- Mật khẩu mới hiện ghi trực tiếp vào `PasswordHash`.

## Quy tắc nghiệp vụ chuẩn
- Mã xác nhận phải có thời hạn và giới hạn số lần thử.
- Mật khẩu mới phải đạt policy và được hash ở thiết kế chuẩn.
- Đổi mật khẩu thành công nên yêu cầu đăng nhập lại nếu cần.

## Code còn lệch spec hoặc cần bổ sung
- Cần bổ sung rate limit và audit.
- Cần chuyển sang hash mật khẩu.
- Cần tránh trả hoặc log mật khẩu plain text.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

