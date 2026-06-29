# Tính năng: Đăng ký tài khoản cục bộ (Local Register)
Trạng thái: Đã phê duyệt
Tác nhân: Khách (Guest)
Độ ưu tiên: Cao
Mã nguồn liên quan: `RegisterController`, `DAO`, `SystemUser`, `EmailSender`, `Views/Register.jsp`, `LoginController`, `SessionSecurityFilter`

## Mục tiêu
Cho phép người dùng tạo tài khoản cục bộ (local account) trên hệ thống bằng tên đăng nhập, email và mật khẩu. Sau khi tài khoản được khởi tạo thành công, hệ thống sẽ thực hiện gửi một thư điện tử (email) chào mừng BetterHR nếu cấu hình SMTP đã được thiết lập chính xác.

## Các Route
- `GET /register`: Hiển thị biểu mẫu (form) đăng ký tài khoản.
- `POST /register`: Xác thực thông tin đầu vào và tiến hành tạo tài khoản.
- Trang JSP: `/Views/Register.jsp`.

## Tuyến đường công khai (Public route)
Bộ lọc bảo mật `SessionSecurityFilter` phải cho phép khách chưa đăng nhập (guest) truy cập vào:
- `/register`
- `/Views/Register.jsp`

## Luồng chính
1. Khách truy cập nhấp chọn nút `Tạo tài khoản mới` từ trang đăng nhập hoặc trang chủ.
2. Trình duyệt mở tuyến đường `GET /register`.
3. Người dùng nhập các thông tin: `username`, `email`, `password`, `confirmPassword`.
4. Lớp điều khiển `RegisterController` tiến hành xác thực dữ liệu bắt buộc, kiểm tra định dạng tên đăng nhập/email, độ dài mật khẩu và độ trùng khớp của mật khẩu xác nhận.
5. Hệ thống thực hiện kiểm tra trùng lặp tên đăng nhập (username) và địa chỉ email trong bảng `SystemUser`.
6. Nếu các thông tin hợp lệ, controller tiến hành tạo mới đối tượng tài khoản `SystemUser` cục bộ.
7. Hệ thống thực hiện gửi email thông báo đăng ký thành công qua lớp `EmailSender`.
8. Nếu gửi email thành công, hệ thống chuyển hướng người dùng về `/login?success=registered`.
9. Nếu tạo tài khoản thành công nhưng gửi email thất bại, hệ thống chuyển hướng người dùng về `/login?success=registered_mail_failed`.

## Quy tắc nghiệp vụ (Business rule) cho tài khoản mới
- Tài khoản đăng ký cục bộ mặc định phải được khởi tạo với vai trò là `Guest`.
- Tài khoản đăng ký cục bộ không được liên kết trực tiếp với mã nhân viên `EmployeeID` ngay khi tạo.
- Trường `EmployeeID` bắt buộc phải để giá trị `NULL` cho đến khi ứng viên trúng tuyển và được HR Manager phê duyệt chuyển thành nhân viên chính thức trong luồng xử lý tại tuyến đường `/hr/create-employee`.
- Khi ứng viên được chuyển thành nhân viên chính thức, hệ thống mới tiến hành cập nhật vai trò của tài khoản từ `Guest` sang `Employee` và thực hiện gán mã nhân viên `EmployeeID` tương ứng.
- Mật khẩu hiện tại được lưu trữ và kiểm tra theo logic trường `SystemUser.PasswordHash` của dự án, chưa yêu cầu tích hợp cơ chế băm mật khẩu trong đặc tả này.

## Nội dung email đăng ký thành công
- Tiêu đề thư (Subject) nên đặt là: `Đăng ký thành công BetterHR`.
- Nội dung email cần hiển thị đầy đủ: Lời chào mừng, tên đăng nhập, địa chỉ email đăng ký, và thông báo tài khoản đã sẵn sàng đăng nhập vào hệ thống BetterHR.
- Cấu hình SMTP phải được đọc từ biến môi trường hoặc tập tin cấu hình cục bộ được thiết lập ignore (mail.local.properties), tuyệt đối không viết cứng mã bí mật (app password) trong mã nguồn Java.

## Xử lý lỗi (Error handling)
- Các trường tên đăng nhập/email/mật khẩu bị bỏ trống: Forward quay lại trang `/Views/Register.jsp` kèm theo thông điệp lỗi `mess` tương ứng.
- Tên đăng nhập sai định dạng: Forward quay lại biểu mẫu đăng ký.
- Địa chỉ email sai định dạng hoặc vượt quá độ dài quy định: Forward quay lại biểu mẫu đăng ký.
- Mật khẩu quá ngắn hoặc quá dài: Forward quay lại biểu mẫu đăng ký.
- Mật khẩu xác nhận không trùng khớp: Forward quay lại biểu mẫu đăng ký.
- Tên đăng nhập hoặc địa chỉ email đã tồn tại trên hệ thống: Forward quay lại biểu mẫu đăng ký.
- Lỗi trong quá trình tạo tài khoản người dùng: Forward quay lại biểu mẫu đăng ký.
- Gửi email thất bại sau khi đã tạo tài khoản thành công: Hệ thống không thực hiện hoàn tác (rollback) tài khoản người dùng đã tạo; thay vào đó, hiển thị thông báo tạo tài khoản thành công kèm theo cảnh báo lỗi gửi email trên trang đăng nhập.

## Tiêu chí nghiệm thu
- [ ] Khách truy cập (Guest) mở được trang `/register` khi chưa đăng nhập.
- [ ] Nhập tên đăng nhập/email hợp lệ và không trùng lặp khởi tạo thành công tài khoản `SystemUser`.
- [ ] Trùng lặp tên đăng nhập hoặc email bị hệ thống từ chối đăng ký.
- [ ] Tài khoản mới tạo bắt buộc có vai trò mặc định là `Guest`.
- [ ] Tài khoản mới tạo không được gán mã `EmployeeID` nếu chưa được phê duyệt chuyển thành nhân viên.
- [ ] Tạo tài khoản thành công thực hiện gửi email chào mừng BetterHR nếu cấu hình SMTP chính xác.
- [ ] Lỗi gửi email không làm ảnh hưởng hoặc làm mất tài khoản người dùng đã được tạo thành công.
- [ ] Sau khi hoàn thành đăng ký, người dùng được đưa về trang đăng nhập và nhìn thấy thông điệp thông báo phù hợp.

## Các phần việc còn thiếu
- [ ] Khắc phục các lỗi hiển thị ký tự (mojibake) trong các thông điệp hoặc mẫu email (email template) bên trong `RegisterController` nếu có.
- [ ] Cập nhật logic trong `RegisterController` để gọi hàm `DAO.getRoleIdByName("Guest")` lấy vai trò Guest thay vì vai trò mặc định `Employee` như trước đây.
- [ ] Tích hợp thêm cơ chế giới hạn tần suất đăng ký (rate limit) hoặc mã captcha nếu tính năng đăng ký công khai bị tấn công spam.
- [ ] Bổ sung ghi nhật ký hệ thống (audit log) cho hoạt động đăng ký tài khoản công khai.
