# Tính năng: Đăng xuất hệ thống (Logout)
Trạng thái: Đã phê duyệt
Tác nhân: Người dùng đã đăng nhập (Authenticated User)
Độ ưu tiên: Cao
Mã nguồn liên quan: `LogoutController`

## Mục tiêu
Cho phép người dùng đã đăng nhập thoát khỏi hệ thống bằng cách hủy phiên làm việc `HttpSession` và quay trở lại trang chủ công khai (public homepage).

## Các Route
- `GET /logout`
- `POST /logout`

## Luồng chính
1. Người dùng nhấp chọn đăng xuất.
2. Lớp điều khiển `LogoutController` lấy phiên làm việc hiện tại bằng lệnh `request.getSession(false)`.
3. Nếu tồn tại phiên làm việc, hệ thống thực hiện hủy phiên làm việc (invalidate session).
4. Hệ thống chuyển hướng (redirect) người dùng về trang chủ `/homepage`.

## Luồng lỗi
- Nếu không tồn tại phiên làm việc, hệ thống vẫn chuyển hướng người dùng về trang chủ `/homepage`.

## Hiện trạng code
- Đã có sẵn các tuyến đường xử lý `GET /logout` và `POST /logout`.
- Đã thực hiện hủy phiên làm việc (invalidate session) của người dùng.
- Chưa xử lý xóa (clear) các cookie tự động nhớ đăng nhập `username` và `password`.

## Tiêu chí nghiệm thu
- [ ] Sau khi đăng xuất, phiên làm việc (session) cũ không còn giá trị sử dụng.
- [ ] Sau khi đăng xuất, người dùng được chuyển hướng về trang chủ công khai `/homepage`.
- [ ] Truy cập đường dẫn `/logout` khi chưa thực hiện đăng nhập không gây ra lỗi hệ thống (server error).

## Các phần việc còn thiếu
- [ ] Thực hiện xóa cookie remember-me khi người dùng đăng xuất.
- [ ] Nếu bổ sung cơ chế token remember-me, phải thực hiện thu hồi (revoke) token tương ứng trong cơ sở dữ liệu.
