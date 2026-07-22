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
- Ràng buộc số lần đăng nhập sai (Lockout Policy):
        + Khi người dùng nhập sai mật khẩu liên tiếp từ lần thứ 7 (tức là đã sai > 6 lần), hệ thống hiển thị cảnh báo dạng pop-up thông báo tài khoản có nguy cơ bị khóa nếu tiếp tục nhập sai.
        + Khi người dùng nhập sai mật khẩu đủ 8 lần liên tiếp, hệ thống tự động khóa tài khoản bằng cách cập nhật is_locked = TRUE và locked_at = NOW() (hoặc IsActive = false).
        + Mỗi lần đăng nhập thành công, hệ thống phải tự động reset bộ đếm số lần sai liên tiếp failed_attempts về 0.
        + Chỉ duy nhất tài khoản vai trò Admin mới có quyền mở khóa cho tài khoản này bằng cách truy cập màn hình admin gửi POST lên /admin/users/unlock.

## Code còn lệch spec hoặc cần bổ sung
- Cần bổ sung cột theo dõi số lần đăng nhập sai liên tiếp `failed_attempts` (mặc định 0), `is_locked` (mặc định FALSE) và `locked_at` vào bảng `SystemUser`.
- Cần lập trình bộ đếm số lần đăng nhập sai tại `LoginController`, trả về JSON cờ hiển thị pop-up cảnh báo sau lần thứ 6 sai và khóa cứng ở lần thứ 8 sai.
- Cần lập trình API mở khóa `/admin/users/unlock` chỉ cho phép Admin truy cập để reset bộ đếm và mở khóa tài khoản.
- Cần test user inactive và user Google-only.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

