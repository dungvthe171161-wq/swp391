# Tính năng Auth: Đăng ký tài khoản cục bộ

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Ứng viên hoặc người dùng mới tạo tài khoản local.

## Route, controller và JSP liên quan
- `/login`, `/logout`, `/register`, `/homepage`, `/ForgotPassword`, `/Recovery`, `/changepass`, `/changepassRE`.
- `/auth/google`, `/auth/google/callback`, `/loginByGmail`.
- Controller: `LoginController`, `LogoutController`, `RegisterController`, `GoogleAuthController`, `HomepageController` và controller đổi/quên mật khẩu.

## Hiện trạng code
- `RegisterController` tạo `SystemUser` local.
- Role mặc định theo spec là Guest.
- Có gửi email BetterHR sau khi tạo account trong code hiện tại.

## Quy tắc nghiệp vụ chuẩn
- Username/email phải unique.
- Password phải đạt policy và được hash ở thiết kế chuẩn.
- User mới không được gắn `EmployeeID` nếu chưa là employee.

## Code còn lệch spec hoặc cần bổ sung
- Code hiện còn phụ thuộc cơ chế mật khẩu plain text.
- Cần test tạo Guest profile sau đăng ký.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

