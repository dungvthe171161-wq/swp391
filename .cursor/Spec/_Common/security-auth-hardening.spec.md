# Đặc tả dùng chung: Bảo mật xác thực

Trạng thái: Đã rà soát theo code ngày 2026-07-22.
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
- Mật khẩu được lưu trữ dưới dạng thô (Plaintext) theo nghiệp vụ hiện tại của dự án.
- Mật khẩu hợp lệ bắt buộc có độ dài từ **10 đến 36 ký tự**, chứa cả chữ cái và chữ số, kiểm tra khớp với biểu thức chính quy Regex `^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{10,36}$` ở cả Javascript (Frontend) và Controller (Backend).
- Nhập sai mật khẩu liên tiếp **từ lần thứ 7** (tức là đã sai > 6 lần): Hiển thị cảnh báo dạng pop-up thông báo tài khoản có nguy cơ bị khóa nếu tiếp tục nhập sai.
- Nhập sai mật khẩu liên tiếp **đủ 8 lần**: Hệ thống tự động khóa tài khoản (chuyển trạng thái `is_locked = TRUE` và `locked_at = NOW()`).
- Chỉ duy nhất người dùng có vai trò **Admin** mới có quyền mở khóa tài khoản này thông qua nút bấm "Mở khóa" trên giao diện Admin gửi request POST lên `/admin/users/unlock`.
- Reset password không được trả mật khẩu plain text qua response; nên dùng link hoặc token một lần.
- Luồng quên mật khẩu cần giới hạn số lần thử mã và thời hạn mã.
- Đăng xuất phải invalidate session hiện tại.
- 
## Code còn lệch spec hoặc cần bổ sung
- Giữ nguyên việc lưu trữ mật khẩu dạng thô (Plaintext) tại cột `PasswordHash`.
- Cần bổ sung các cột `failed_attempts` (mặc định 0), `is_locked` (mặc định FALSE) và `locked_at` vào bảng `SystemUser`.
- Lập trình kiểm tra độ mạnh mật khẩu tại `RegisterController` và `UserController` bằng Regex `^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{10,36}$`.
- Cấu hình logic đếm số lần nhập sai liên tiếp tại `LoginController` và tự động cập nhật trạng thái khóa tài khoản.
- Lập trình API mở khóa `/admin/users/unlock` chỉ cho phép Admin truy cập để reset bộ đếm và mở khóa tài khoản.
- Cần bổ sung rate limit và audit cho reset password, đổi mật khẩu, login thất bại.
## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

