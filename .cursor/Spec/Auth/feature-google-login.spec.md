# Tính năng Auth: Đăng nhập bằng Google

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- User đăng nhập hoặc đăng ký bằng tài khoản Google.

## Route, controller và JSP liên quan
- `/login`, `/logout`, `/register`, `/homepage`, `/ForgotPassword`, `/Recovery`, `/changepass`, `/changepassRE`.
- `/auth/google`, `/auth/google/callback`, `/loginByGmail`.
- Controller: `LoginController`, `LogoutController`, `RegisterController`, `GoogleAuthController`, `HomepageController` và controller đổi/quên mật khẩu.

## Hiện trạng code
- `GoogleAuthController` xử lý `/auth/google`, callback và `/loginByGmail`.
- Code lưu `GoogleID`, `AvatarUrl`, `LoginProvider`.
- Dashboard vẫn đi qua role redirect.

## Quy tắc nghiệp vụ chuẩn
- Google account phải liên kết bằng email hoặc GoogleID an toàn.
- Không tạo trùng user nếu email đã tồn tại.
- Config client id/secret không hard-code khi deploy.

## Code còn lệch spec hoặc cần bổ sung
- Cần kiểm tra cấu hình local/env.
- Cần chính sách khi email Google trùng user local.
- Cần test user Google chưa có role hoặc bị inactive.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

