# Tính năng Auth: Đăng xuất

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- User đăng xuất khỏi HRMS.

## Route, controller và JSP liên quan
- `/login`, `/logout`, `/register`, `/homepage`, `/ForgotPassword`, `/Recovery`, `/changepass`, `/changepassRE`.
- `/auth/google`, `/auth/google/callback`, `/loginByGmail`.
- Controller: `LoginController`, `LogoutController`, `RegisterController`, `GoogleAuthController`, `HomepageController` và controller đổi/quên mật khẩu.

## Hiện trạng code
- `LogoutController` mapping `/logout`.
- Luồng chính invalidate session rồi redirect về login/homepage.
- Không thay đổi dữ liệu nghiệp vụ.

## Quy tắc nghiệp vụ chuẩn
- Đăng xuất phải hủy session hiện tại.
- Sau logout không được quay lại trang bảo vệ bằng nút Back nếu session đã hết.
- Thông báo sau logout nên ngắn gọn bằng tiếng Việt.

## Code còn lệch spec hoặc cần bổ sung
- Cần kiểm tra cache header nếu trình duyệt còn hiển thị trang cũ.
- Cần test logout trên mọi actor.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

