# Tính năng Auth: Điều hướng trang chủ theo vai trò

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- User sau login hoặc user truy cập homepage public.

## Route, controller và JSP liên quan
- `/login`, `/logout`, `/register`, `/homepage`, `/ForgotPassword`, `/Recovery`, `/changepass`, `/changepassRE`.
- `/auth/google`, `/auth/google/callback`, `/loginByGmail`.
- Controller: `LoginController`, `LogoutController`, `RegisterController`, `GoogleAuthController`, `HomepageController` và controller đổi/quên mật khẩu.

## Hiện trạng code
- `HomepageController` và `RoleRedirectUtil` quyết định route.
- Dashboard map theo `RoleID` 1-6.
- Guest đi `/guest/dashboard`, Employee đi `/employee`.

## Quy tắc nghiệp vụ chuẩn
- Role không hợp lệ hoặc chưa login đi về homepage/login phù hợp.
- Không link trực tiếp JSP dashboard nếu controller cần nạp dữ liệu.
- Điều hướng phải thống nhất giữa login local và Google login.

## Code còn lệch spec hoặc cần bổ sung
- Cần cập nhật spec và code cùng lúc nếu đổi dashboard actor.
- Cần test tất cả 6 role seed.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

