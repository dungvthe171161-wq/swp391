# Tính năng: Đăng nhập hệ thống (Login)
Trạng thái: Đã phê duyệt
Tác nhân: Guest, SystemUser
Độ ưu tiên: Cao
Mã nguồn liên quan: `LoginController`, `DAO`, `HomepageController`, `Views/Login.jsp`

## Mục tiêu
Cho phép người dùng đã có tài khoản thực hiện đăng nhập vào hệ thống BetterHR (HRMS) bằng tên đăng nhập hoặc email và mật khẩu. Sau khi đăng nhập thành công, hệ thống sẽ khởi tạo phiên làm việc `HttpSession`, lưu đối tượng `systemUser`, rồi điều hướng người dùng về trang chủ `/homepage`.

## Các Route và giao diện
- `GET /login`: Hiển thị trang đăng nhập `/Views/Login.jsp`.
- `GET /login?success=registered`: Hiển thị thông báo tạo tài khoản thành công.
- `GET /login?success=registered_mail_failed`: Hiển thị thông báo đã tạo tài khoản thành công nhưng hệ thống chưa gửi được email xác nhận.
- `POST /login`: Xử lý dữ liệu từ biểu mẫu đăng nhập.
- Biểu mẫu hiện tại gửi lên các tham số: `user`, `pass`, `rememberMe`.
- Tham số `user` chấp nhận cả tên đăng nhập (username) hoặc địa chỉ email.

## Luồng chính
1. Khách truy cập (Guest) vào tuyến đường `/login`.
2. Hệ thống kiểm tra và đọc thông tin cookie (nếu có), sau đó thiết lập các thuộc tính tương ứng từ cookie vào request.
3. Người dùng nhập tên đăng nhập (hoặc email) và mật khẩu.
4. Lớp điều khiển `LoginController` lấy thông tin tài khoản qua hàm `DAO.getInstance().getAccountByUsernameOrEmail(login)`.
5. Hệ thống thực hiện kiểm tra mật khẩu qua hàm `DAO.getInstance().checkPassword(password, user.getPasswordHash())`.
6. Nếu mật khẩu trùng khớp và hợp lệ, hệ thống tiến hành hủy phiên làm việc (session) cũ (nếu có).
7. Hệ thống khởi tạo phiên làm việc mới an toàn và lưu trữ thuộc tính `systemUser`.
8. Nếu người dùng chọn `rememberMe` (tự động nhớ đăng nhập), hệ thống sẽ lưu thông tin cookie `username` trong vòng 24 giờ; nếu không chọn, cookie này sẽ bị xóa bỏ.
9. Hệ thống chuyển hướng (redirect) người dùng về trang chủ `/homepage`.
10. `HomepageController` xử lý và hiển thị trang chủ kèm theo các bảng điều khiển (dashboard) tương ứng mà vai trò của người dùng được phân quyền.

## Luồng lỗi
- Tên đăng nhập/email hoặc mật khẩu bị rỗng/sai định dạng: Chuyển tiếp (forward) lại trang `/Views/Login.jsp` và thiết lập thông điệp cảnh báo `mess` bằng tiếng Việt.
- Nhập sai tên đăng nhập/email hoặc mật khẩu: Forward lại trang `/Views/Login.jsp` và thiết lập thông điệp lỗi `mess` bằng tiếng Việt.
- Tài khoản được tạo chỉ dùng đăng nhập qua Google (không có mật khẩu cục bộ): Forward lại trang đăng nhập và hiển thị thông báo yêu cầu người dùng tiếp tục đăng nhập bằng Google.
- Lỗi kết nối cơ sở dữ liệu (DB): Forward lại trang đăng nhập và hiển thị thông báo lỗi bằng tiếng Việt.
- Phiên làm việc (session) cũ vẫn tồn tại: Thực hiện hủy phiên làm việc (`invalidate`) trước khi tạo phiên mới để hạn chế rủi ro bị tấn công cố định phiên (session fixation).

## Bảo mật và ràng buộc kỹ thuật
- Mã nguồn dự án sử dụng thư viện `jakarta.servlet.*`, tuyệt đối không dùng thư viện cũ `javax.servlet.*`.
- Mật khẩu trong cơ sở dữ liệu được lưu trữ tại cột `SystemUser.PasswordHash`. Mã nguồn hiện tại đang thực hiện so sánh dưới dạng văn bản thuần (plain text) theo thiết kế hiện tại của dự án, chưa áp dụng các thuật toán băm mật khẩu như BCrypt hay SHA.
- Cookie tự động nhớ đăng nhập (remember-me) hiện tại mới chỉ lưu trữ tên đăng nhập `username`, không lưu trữ mật khẩu để đảm bảo an toàn.

## Hiện trạng code
- Đã có sẵn các tuyến đường xử lý `GET /login` và `POST /login`.
- Đã hỗ trợ khởi tạo session đăng nhập và chuyển hướng về trang chủ `/homepage`.
- Đã cấu hình cookie remember-me chỉ lưu trữ trường `username`.
- Đã tích hợp logic kiểm tra tài khoản đang hoạt động (active) hoặc bị khóa (locked) trong controller đăng nhập.
- Đã xây dựng bộ đếm số lần đăng nhập thất bại và tự động khóa tài khoản tạm thời dựa vào các trường `FailedLoginAttempt` và `LockedUntil`.
- Đã cấu hình đầy đủ các thông điệp thông báo thành công cho các tham số `registered`, `registered_mail_failed`, `password_changed`.
- Chưa tích hợp ghi nhật ký hệ thống (audit log) cho hoạt động đăng nhập.

## Tiêu chí nghiệm thu
- [ ] Đăng nhập với thông tin chính xác khởi tạo thành công session mới chứa đối tượng `systemUser`.
- [ ] Đăng nhập thành công chuyển hướng chính xác về `/homepage`.
- [ ] Gửi thiếu tên đăng nhập hoặc mật khẩu hiển thị thông điệp thông báo lỗi tương ứng trên trang đăng nhập.
- [ ] Nhập sai thông tin tên đăng nhập hoặc mật khẩu không khởi tạo session mới.
- [ ] Hệ thống bắt buộc phải kiểm tra mật khẩu thông qua hàm `DAO.checkPassword`.

## Các phần việc còn thiếu
- [ ] Về lâu dài, nếu cần tính năng nhớ đăng nhập thực sự an toàn, cần thay thế cookie lưu username bằng cơ chế token remember-me an toàn.
- [ ] Bổ sung ghi nhật ký hoạt động (audit log) cho các lượt đăng nhập thành công và thất bại.
