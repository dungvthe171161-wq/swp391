# Tính năng: Khôi phục mật khẩu (Quên mật khẩu) và Đổi mật khẩu
Trạng thái: Đã phê duyệt
Tác nhân: Guest, Người dùng đã đăng nhập (Authenticated User)
Độ ưu tiên: Cao
Mã nguồn liên quan: `ForgotPassController`, `RecoveryController`, `ChangePassREController`, `ChangePassController`, `LoginController`, `SessionSecurityFilter`, `DAO`, `EmailSender`, `Views/ForgotPassword.jsp`, `Views/Recovery.jsp`, `Views/ChangePasswordRE.jsp`, `Views/ChangePassword.jsp`

## Mục tiêu
Hỗ trợ người dùng khôi phục lại mật khẩu bằng cách sử dụng địa chỉ email và xác thực qua mã PIN, sau đó cho phép khởi tạo mật khẩu mới. Đồng thời, hỗ trợ người dùng đã đăng nhập thực hiện đổi mật khẩu ngay trong tài khoản cá nhân.

## Các Route hiện có
- `GET /ForgotPassword`: Hiển thị biểu mẫu (form) nhập địa chỉ email để yêu cầu khôi phục mật khẩu.
- `POST /ForgotPassword`: Xác thực địa chỉ email nhập vào và gửi mã PIN khôi phục.
- `GET /Recovery`: Hiển thị biểu mẫu nhập mã PIN xác thực.
- `POST /Recovery`: Tiến hành xác minh mã PIN do người dùng cung cấp.
- `GET /changepassRE`: Hiển thị biểu mẫu tạo mật khẩu mới sau khi xác thực mã PIN thành công.
- `POST /changepassRE`: Cập nhật mật khẩu mới vào hệ thống.
- `GET/POST /changepass`: Thực hiện đổi mật khẩu khi người dùng đã đăng nhập vào hệ thống.
- `GET /login?success=password_changed`: Hiển thị thông báo đổi mật khẩu thành công trên trang đăng nhập.

## Các tuyến đường công khai (Public Route) trong bộ lọc bảo mật
Bộ lọc bảo mật `SessionSecurityFilter` phải cho phép truy cập công khai vào các tuyến đường sau khi người dùng chưa đăng nhập:
- `/ForgotPassword`
- `/Recovery`
- `/changepassRE`
- `/ForgotPassword.jsp`
- `/Views/ForgotPassword.jsp`

Lưu ý: Tuyến đường `/changepassRE` được cấu hình công khai ở mức bộ lọc (filter), nhưng lớp điều khiển (controller) vẫn phải kiểm soát chặt chẽ bằng cách kiểm tra thuộc tính session `recoveryEmail` và `recoveryVerified`.

## Luồng quên mật khẩu (Password recovery flow)
1. Khách truy cập (Guest) nhấp vào `Quên mật khẩu?` từ trang `/login` và hệ thống mở trang `/Views/ForgotPassword.jsp` hoặc `/ForgotPassword`.
2. Người dùng nhập địa chỉ email liên kết với tài khoản.
3. `ForgotPassController` xác thực (validate) địa chỉ email đảm bảo không được để trống.
4. Gọi hàm `DAO.checkEmailExist(email)` để kiểm tra sự tồn tại của email trong bảng người dùng hệ thống `SystemUser.Email`, không kiểm tra trong bảng nhân viên `Employee.Email`.
5. Nếu địa chỉ email không tồn tại trong hệ thống, chuyển tiếp (forward) quay lại trang `Views/ForgotPassword.jsp` kèm theo thông báo: `Email này không tồn tại trong hệ thống.`
6. Nếu email tồn tại, hệ thống tự động sinh một mã PIN ngẫu nhiên gồm 6 chữ số.
7. Gọi hàm `EmailSender.sendEmail(...)` để gửi mã PIN này tới địa chỉ email của người dùng thông qua giao thức SMTP.
8. Hệ thống lưu trữ các thuộc tính sau vào phiên làm việc (session):
   - `recoveryEmail = email`
   - `pinCode = pin`
   - Thời gian hết hạn của phiên làm việc (session timeout) là `600` giây (10 phút).
9. Hệ thống chuyển hướng (redirect) người dùng sang trang xác nhận `/Recovery`.

## Luồng xác minh mã PIN
1. Người dùng mở trang `/Recovery`.
2. `RecoveryController` forward yêu cầu đến `Views/Recovery.jsp`.
3. Người dùng nhập mã PIN đã nhận từ email.
4. Controller so sánh mã PIN trong tham số request `pin` với mã PIN lưu trong session `pinCode`.
5. Nếu mã PIN không chính xác hoặc session đã hết thời gian hiệu lực, forward quay lại trang `Views/Recovery.jsp` kèm theo thông báo: `Mã PIN không hợp lệ hoặc đã hết hạn.`
6. Nếu mã PIN trùng khớp, controller thiết lập thuộc tính session `recoveryVerified = true`.
7. Hệ thống chuyển hướng người dùng sang trang tạo mật khẩu mới `/changepassRE`.

## Luồng tạo mật khẩu mới sau khi xác thực mã PIN
1. Người dùng mở trang `/changepassRE`.
2. `ChangePassREController` chỉ cho phép truy cập biểu mẫu tạo mật khẩu mới khi session có đầy đủ:
   - `recoveryEmail`
   - `recoveryVerified = true`
3. Nếu thiếu thông tin session khôi phục hoặc chưa xác minh mã PIN thành công, chuyển hướng người dùng về trang `/ForgotPassword`.
4. Người dùng nhập mật khẩu mới `newPass` và xác nhận mật khẩu `confirmPass`.
5. Xác thực mật khẩu nhập vào theo các quy tắc:
   - Độ dài bắt buộc từ 8 đến 16 ký tự.
   - Chỉ cho phép chữ cái và chữ số theo biểu thức chính quy `[a-zA-Z0-9]+` (không chứa ký tự đặc biệt).
   - Xác nhận mật khẩu `confirmPass` phải trùng khớp hoàn toàn với mật khẩu mới `newPass`.
6. Controller lấy thông tin người dùng qua hàm `DAO.getAccountByEmail(recoveryEmail)`.
7. Nếu tìm thấy tài khoản tương ứng, gọi hàm `DAO.changePassword(user.getUsername(), newPass)` để lưu mật khẩu mới.
8. Nếu cập nhật thành công, hệ thống xóa bỏ các thuộc tính khôi phục khỏi session:
   - `recoveryEmail`
   - `pinCode`
   - `recoveryVerified`
9. Chuyển hướng người dùng về `/login?success=password_changed`.
10. `LoginController` hiển thị thông báo: `Đổi mật khẩu thành công. Bạn có thể đăng nhập bằng mật khẩu mới.`

## Luồng đổi mật khẩu khi đã đăng nhập
1. Người dùng đã đăng nhập (Authenticated user) truy cập vào `/changepass`.
2. Người dùng nhập mật khẩu hiện tại, mật khẩu mới và xác nhận mật khẩu mới.
3. `ChangePassController` thực hiện kiểm tra đối tượng người dùng đăng nhập trong session `systemUser`.
4. Hệ thống kiểm tra tính chính xác của mật khẩu hiện tại bằng hàm `DAO.checkPassword`.
5. Nếu mật khẩu hiện tại chính xác, gọi hàm `DAO.changePassword(username, newPass)` để cập nhật.
6. Hệ thống trả về giao diện hoặc chuyển hướng người dùng tương ứng với logic hiện có.

## Lớp gửi email (Email sender)
Lớp `EmailSender` tuyệt đối không được viết cứng địa chỉ Gmail hoặc mật khẩu ứng dụng (app password) bên trong mã nguồn Java.

Các nguồn cấu hình được ưu tiên đọc theo thứ tự:
1. Biến môi trường hệ thống (Environment variables):
   - `MAIL_USERNAME`
   - `MAIL_PASSWORD`
   - `MAIL_FROM`
   - `MAIL_FROM_NAME`
   - `MAIL_HOST`
   - `MAIL_PORT`
2. Các thuộc tính hệ thống Java (Java system properties) có cùng tên.
3. Tập tin cấu hình cục bộ được bỏ qua không đẩy lên git (ignored local file):
   - `src/main/resources/META-INF/mail.local.properties`
   - Hoặc tập tin `META-INF/mail.properties` khi hệ thống chạy deploy thực tế.

Tập tin mẫu cấu hình:
- `src/main/resources/META-INF/mail.example.properties`

Quy tắc cấu hình:
- Đối với dịch vụ Gmail bắt buộc phải cấu hình và sử dụng Mật khẩu ứng dụng (App Password).
- Tuyệt đối không commit tập tin cấu hình cục bộ `mail.local.properties` lên hệ thống quản lý mã nguồn.
- Nếu thiếu cấu hình gửi mail, luồng quên mật khẩu phải forward quay lại biểu mẫu nhập email kèm theo thông báo: `Không thể gửi email. Vui lòng thử lại sau.`

## Hiện trạng code
- Lớp điều khiển `ForgotPassController` đã có sẵn xử lý cho các phương thức GET/POST.
- Lớp điều khiển `RecoveryController` đã cấu hình gán thuộc tính `recoveryVerified` khi người dùng nhập đúng mã PIN.
- Lớp điều khiển `ChangePassREController` đã tích hợp chặn các truy cập trực tiếp nếu người dùng chưa xác minh mã PIN thành công.
- Bộ lọc `SessionSecurityFilter` đã cho phép truy cập công khai vào `/ForgotPassword`, `/Recovery`, `/changepassRE`.
- Hàm `DAO.checkEmailExist` đang thực hiện kiểm tra trong bảng `SystemUser.Email`.
- Mật khẩu hiện tại của người dùng được lưu trữ trực tiếp vào cột `SystemUser.PasswordHash` dạng văn bản thuần (plain text), chưa áp dụng băm hoặc BCrypt.
- Giao diện của các trang Quên mật khẩu, Xác minh mã PIN, Tạo mật khẩu mới sau khi xác minh mã PIN đã được thiết kế đồng bộ theo BetterHR và hiển thị bằng tiếng Việt.

## Tiêu chí nghiệm thu
- [ ] Khách truy cập mở được trang `/ForgotPassword` khi chưa đăng nhập.
- [ ] Gửi email rỗng hiển thị thông điệp báo lỗi và hệ thống không thực hiện gửi email.
- [ ] Gửi địa chỉ email không tồn tại trong bảng `SystemUser` hiển thị thông điệp báo lỗi.
- [ ] Nhập đúng email tồn tại hệ thống tiến hành gửi mã PIN và chuyển hướng sang trang `/Recovery`.
- [ ] Nhập sai mã PIN hoặc session hết hạn không thể truy cập vào biểu mẫu tạo mật khẩu mới.
- [ ] Nhập đúng mã PIN hệ thống chuyển hướng người dùng sang `/changepassRE`.
- [ ] Truy cập trực tiếp vào `/changepassRE` khi chưa có cờ xác nhận `recoveryVerified` sẽ bị chuyển hướng về `/ForgotPassword`.
- [ ] Nhập mật khẩu mới ngắn hơn 8 ký tự hoặc dài hơn 16 ký tự bị hệ thống từ chối.
- [ ] Nhập mật khẩu mới chứa ký tự đặc biệt bị hệ thống từ chối.
- [ ] Nhập xác nhận mật khẩu không trùng khớp với mật khẩu mới bị từ chối.
- [ ] Đổi mật khẩu thành công cập nhật chính xác giá trị vào cột `SystemUser.PasswordHash`.
- [ ] Đổi mật khẩu thành công thực hiện xóa các thuộc tính `recoveryEmail`, `pinCode`, `recoveryVerified` khỏi session.
- [ ] Đổi mật khẩu thành công chuyển hướng người dùng về `/login?success=password_changed`.

## Các phần việc còn thiếu
- [ ] Trong tương lai nếu dự án áp dụng băm mật khẩu, cần tiến hành cập nhật đồng thời hàm `DAO.changePassword` và đặc tả này.
- [ ] Có thể thiết kế bảng lưu trữ token khôi phục riêng trong cơ sở dữ liệu thay vì lưu trữ mã PIN tạm thời trong session để nâng cao tính bảo mật.
- [ ] Bổ sung cơ chế giới hạn tần suất gửi (rate limit) mã PIN để tránh việc bị gửi email spam liên tục.
