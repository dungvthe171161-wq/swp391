# Đặc tả Phân hệ: Xác thực (Authentication)
Trạng thái: Đã phê duyệt
Tác nhân: Guest, SystemUser

## Phạm vi
Phân hệ Xác thực (Auth) bao gồm các chức năng đăng nhập, đăng ký, đăng xuất, Google Login, định tuyến trang chủ (homepage routing), quên mật khẩu, xác minh mã PIN và đổi mật khẩu.

## Các tập tin đặc tả tính năng (Feature files)
- `feature-login.spec.md`
- `feature-register.spec.md`
- `feature-logout.spec.md`
- `feature-homepage-routing.spec.md`
- `feature-password-recovery-change.spec.md`
- `feature-google-login.spec.md`

## Nguyên tắc đúng với mã nguồn hiện tại
- Tài khoản đăng nhập bằng tham số tên đăng nhập `user` và tham số mật khẩu `pass`.
- Đăng nhập cục bộ (local login) chấp nhận tên đăng nhập (username) hoặc email.
- Đăng ký cục bộ (local register) sử dụng tuyến đường (route) `/register`, tiến hành tạo tài khoản `SystemUser` và gửi email BetterHR nếu cấu hình SMTP chính xác.
- Phiên đăng nhập (session) lưu trữ thuộc tính `systemUser`.
- Mật khẩu trong mã nguồn hiện tại được xác thực qua hàm `DAO.checkPassword` và lưu/cập nhật tại trường `SystemUser.PasswordHash` theo logic lưu văn bản thuần (plain text) hiện tại của dự án.
- Luồng quên mật khẩu sử dụng `SystemUser.Email`, mã PIN phiên làm việc `pinCode`, và cờ xác thực `recoveryVerified` trước khi cho phép truy cập vào `/changepassRE`.
- `/homepage` đóng vai trò là cổng truy cập trung tâm để hiển thị các bảng điều khiển (dashboard) được cấp quyền tương ứng với vai trò (role), hệ thống không tự động chuyển hướng bắt buộc theo vai trò.
- Mã nguồn dự án sử dụng Jakarta Servlet, không sử dụng `javax.servlet`.

## Các lưu ý/cảnh báo
- Chức năng tự động nhớ đăng nhập (Remember-me) hiện tại mới chỉ lưu trữ cookie tên đăng nhập `username`, không lưu trữ mật khẩu.
- Chức năng đăng nhập Google (Google Login) đã có sẵn servlet OAuth2 và callback tương ứng.
- Đăng nhập đã được tích hợp đếm số lần thất bại `FailedLoginAttempt`, khóa tài khoản đến thời điểm `LockedUntil`, và thời điểm đăng nhập cuối `LastLogin` theo `SystemUser`.
- Cấu hình SMTP gửi mail phải sử dụng biến môi trường (environment variable) hoặc tập tin cấu hình cục bộ được thiết lập ignore (bỏ qua không đẩy lên git), không được viết cứng (hardcode) mã bí mật trong code Java.
- Vai trò mặc định cho tài khoản đăng ký mới là `Guest` và trường `EmployeeID = NULL`; tài khoản này chỉ được chuyển sang vai trò `Employee` và gán mã nhân viên `EmployeeID` sau khi bộ phận HR Manager phê duyệt/tạo thông tin nhân viên mới.
