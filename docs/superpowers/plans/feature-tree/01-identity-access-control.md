# Feature Tree 01 - Kế hoạch triển khai Định danh và Kiểm soát truy cập

- **Trạng thái:** Bản nháp
- **Phạm vi chuẩn:** Feature Tree 01
- **Phụ thuộc:** Không có
- **Được sử dụng bởi:** Plan 02-10

## 1. Mục tiêu

Cung cấp một luồng định danh thống nhất và an toàn cho tài khoản cục bộ và Google OAuth, sau đó thực thi nhất quán session, role, quyền module, quyền theo role, quyền ghi đè theo người dùng và phạm vi phòng ban trên toàn hệ thống.

## 2. Phạm vi và quyền sở hữu

Plan này chịu trách nhiệm:

- Đăng ký cục bộ, đăng nhập, đăng xuất, xác minh email, khôi phục, đặt lại và đổi mật khẩu.
- Ủy quyền Google OAuth, callback, đối chiếu tài khoản và xử lý `LoginProvider`.
- Tạo, xoay vòng, hết hạn và vô hiệu hóa session.
- Phân quyền lúc chạy thông qua filter role và permission.
- Sự kiện audit cho xác thực và các hành động nhạy cảm liên quan đến bảo mật tài khoản.

Plan này không chịu trách nhiệm:

- Màn hình Admin để gán role và permission; phần này thuộc Plan 09.
- Quy tắc phân quyền riêng của từng module nghiệp vụ ngoài việc định nghĩa và thực thi hợp đồng permission dùng chung.
- Cơ chế gửi email; phần này thuộc Plan 10.

## 3. Code hiện tại cần audit và tái sử dụng

### Controller và filter

- `src/main/java/com/hrm/controller/LoginController.java`
- `src/main/java/com/hrm/controller/RegisterController.java`
- `src/main/java/com/hrm/controller/GoogleAuthController.java`
- `src/main/java/com/hrm/controller/LogoutController.java`
- `src/main/java/com/hrm/controller/ForgotPassController.java`
- `src/main/java/com/hrm/controller/RecoveryController.java`
- `src/main/java/com/hrm/controller/ChangePassController.java`
- `src/main/java/com/hrm/controller/ChangePassREController.java`
- `src/main/java/com/hrm/filter/SessionSecurityFilter.java`
- `src/main/java/com/hrm/filter/RoleAuthorizationFilter.java`
- `src/main/java/com/hrm/filter/ModulePermissionFilter.java`
- `src/main/java/com/hrm/filter/AdminAuthorizationFilter.java`

### Dữ liệu và tiện ích

- `SystemUserDAO`, `GuestDAO`, `RoleDAO`, `PermissionDAO`
- `RolePermissionDAO`, `UserPermissionDAO`
- `RoleRedirectUtil`, `PermissionUtil`
- `SystemUser`, `Guest`, `Role`, `Permission`, `RolePermission`, `UserPermission`

### Giao diện và cấu hình

- `Views/Login.jsp`, `Views/Register.jsp`, `Views/ForgotPassword.jsp`
- `Views/Recovery.jsp`, `Views/ChangePassword.jsp`, `Views/ChangePasswordRE.jsp`
- `META-INF/google.example.properties`, `META-INF/db.example.properties`

## 4. Luồng code lúc chạy

```text
JSP đăng nhập/đăng ký/OAuth/khôi phục
  -> Controller xác thực kiểm tra dữ liệu HTTP
  -> Service xác thực áp dụng chính sách định danh
  -> SystemUserDAO/GuestDAO đọc hoặc cập nhật dữ liệu định danh
  -> Session được xoay vòng và chứa ngữ cảnh người dùng chuẩn
  -> RoleRedirectUtil chọn trang đích
  -> Các filter bảo mật kiểm soát mọi request được bảo vệ
```

## 5. Các bất biến bảo mật

- Mật khẩu chỉ được lưu dưới dạng hash BCrypt.
- Thông báo đăng nhập thất bại không tiết lộ username hoặc email có tồn tại hay không.
- Đăng nhập thành công phải xoay vòng session ID và xóa dữ liệu khôi phục tạm thời.
- Đăng xuất phải vô hiệu hóa session phía server và xóa cookie xác thực.
- Mã đặt lại/khôi phục có TTL ngắn, giới hạn số lần thử và chỉ được sử dụng một lần.
- Giá trị `state` của Google OAuth được kiểm tra và không thể tái sử dụng.
- Tài khoản bị vô hiệu hóa hoặc khóa không thể tiếp tục dùng session cũ để truy cập route được bảo vệ.
- Quyền ghi đè theo người dùng tuân theo một quy tắc ưu tiên được tài liệu hóa so với quyền theo role.
- Filter trả về 401/chuyển hướng cho truy cập chưa xác thực và 403/AccessDenied cho người dùng đã xác thực nhưng không có quyền.
- Secret production được đọc từ biến môi trường hoặc file cấu hình không commit.

## 6. Các task triển khai

### Task 1 - Thiết lập baseline cho xác thực và phân quyền

- [ ] Kiểm kê mọi route công khai và được bảo vệ từ annotation servlet và `web.xml`.
- [ ] Lập ma trận role-route-permission cho Guest, Employee, HR Staff, HR Manager, Department Manager và Admin.
- [ ] Ghi nhận các tên session attribute hiện tại và chọn một hợp đồng người dùng đã xác thực duy nhất.
- [ ] Thêm test thất bại cho sai mật khẩu, tài khoản bị khóa, session hết hạn, thiếu role và thiếu quyền module.
- [ ] Thêm test chuyển hướng cho từng role thông qua `RoleRedirectUtil`.

### Task 2 - Chuẩn hóa mô hình định danh

- [ ] Quyết định các trường dùng để xác định một principal giữa `SystemUser` và `Guest`.
- [ ] Quy định rõ chính sách phân biệt chữ hoa/chữ thường khi đối chiếu username/email.
- [ ] Bổ sung unique index cho định danh bằng migration idempotent nếu còn thiếu.
- [ ] Định nghĩa các giá trị `LoginProvider` hợp lệ và quy tắc liên kết tài khoản LOCAL/GOOGLE.
- [ ] Thêm DAO method sử dụng `PreparedStatement` và trả về kết quả rõ ràng cho không tìm thấy/vô hiệu hóa/bị khóa.
- [ ] Loại bỏ SQL xác thực hoặc quy tắc nghiệp vụ khỏi JSP và controller.

### Task 3 - Hoàn thiện đăng ký cục bộ

- [ ] Kiểm tra username, email, độ mạnh mật khẩu, xác nhận mật khẩu và định danh trùng ở backend.
- [ ] Hash mật khẩu trước mọi thao tác insert.
- [ ] Quy định tài khoản mới được kích hoạt ngay hay cần xác minh email.
- [ ] Tạo dữ liệu Guest liên quan trong cùng transaction khi đăng ký cho ứng viên.
- [ ] Ngăn double-submit tạo tài khoản trùng.
- [ ] Thêm test cho thành công, lỗi validation, trùng dữ liệu và lỗi database.

### Task 4 - Gia cố đăng nhập, session và đăng xuất

- [ ] Chuyển quyết định xác thực vào service chuyên trách thay vì lặp lại trong controller.
- [ ] Xoay vòng session ID sau khi đăng nhập thành công.
- [ ] Chỉ lưu user, role và các mã phạm vi cần thiết theo hợp đồng chuẩn trong session.
- [ ] Kiểm tra lại trạng thái active/locked cho request được bảo vệ hoặc vô hiệu hóa session cũ một cách an toàn.
- [ ] Chỉ giữ URL quay lại đã được kiểm tra và thuộc nội bộ hệ thống.
- [ ] Vô hiệu hóa session và cookie xác thực khi đăng xuất.
- [ ] Ghi đăng nhập thành công, sự kiện đăng nhập thất bại theo chính sách và đăng xuất vào `SystemLog` mà không ghi mật khẩu hoặc token.

### Task 5 - Hoàn thiện Google OAuth

- [ ] Sinh và kiểm tra OAuth `state` trong session.
- [ ] Trao đổi authorization code qua endpoint và credential lấy từ cấu hình.
- [ ] Kiểm tra dữ liệu định danh Google trả về trước khi tìm hoặc tạo tài khoản.
- [ ] Quy định hành vi an toàn khi đã có tài khoản local dùng cùng email đã xác minh.
- [ ] Chặn liên kết tự động khi thiếu email hoặc email chưa được xác minh.
- [ ] Xử lý từ chối, lỗi callback, state hết hạn và provider timeout bằng thông báo an toàn cho người dùng.
- [ ] Thêm test liên kết tài khoản và kiểm tra callback mà không gọi provider thật.

### Task 6 - Hợp nhất quên, khôi phục, đặt lại và đổi mật khẩu

- [ ] Thay các luồng reset trùng lặp bằng một chính sách mật khẩu và một mô hình trạng thái khôi phục.
- [ ] Lưu trạng thái khôi phục phía server hoặc dưới dạng token database đã hash và có thời hạn; không lưu token plaintext có thể tái sử dụng.
- [ ] Áp dụng giới hạn số lần thử và thời gian chờ gửi lại.
- [ ] Yêu cầu mật khẩu hiện tại khi người dùng đã đăng nhập muốn đổi mật khẩu.
- [ ] Vô hiệu hóa trạng thái khôi phục ngay sau khi thành công.
- [ ] Vô hiệu hóa các session khác sau khi reset mật khẩu nếu hệ thống hỗ trợ.
- [ ] Gửi email thông qua template và lớp sender của Plan 10.

### Task 7 - Hợp nhất phân quyền lúc chạy

- [ ] Tài liệu hóa thứ tự chính xác của `SessionSecurityFilter`, `RoleAuthorizationFilter`, `ModulePermissionFilter` và phân quyền Admin.
- [ ] Loại bỏ các kiểm tra phân quyền chồng chéo hoặc mâu thuẫn.
- [ ] Bảo đảm controller vẫn kiểm tra ownership tài nguyên sau kiểm tra quyền ở cấp route.
- [ ] Áp dụng phạm vi phòng ban qua `DeptManagerScope` cho tài nguyên Department Manager.
- [ ] Trả về hành vi access-denied nhất quán cho JSP và JSON endpoint.
- [ ] Thêm test cho quyền theo role, user allow override, user deny override và truy cập khác phòng ban.

### Task 8 - Cấu hình, logging và an toàn vận hành

- [ ] Loại bỏ mọi secret database, Google, SMTP hoặc redirect đang bị hardcode được phát hiện thêm.
- [ ] Kiểm tra cấu hình bắt buộc khi khởi động mà không in giá trị secret.
- [ ] Thay `System.out` trong xác thực bằng SLF4J logging.
- [ ] Thêm log an toàn theo correlation cho lỗi OAuth và khôi phục.
- [ ] Bảo đảm log không chứa mật khẩu, authorization code, access token, reset token hoặc đầy đủ mã khôi phục.

### Task 9 - Thống nhất giao diện và thông báo

- [ ] Giữ ổn định form action và tên parameter, trừ khi có redirect tương thích.
- [ ] Dùng ngôn ngữ thiết kế HRMS dùng chung cho mọi trang xác thực.
- [ ] Thêm thông báo rõ ràng cho session hết hạn, tài khoản bị khóa, trạng thái khôi phục không hợp lệ và access denied.
- [ ] Chỉ giữ trang người dùng yêu cầu ban đầu khi URL quay lại thuộc nội bộ và người dùng có quyền.
- [ ] Kiểm tra nội dung tiếng Việt được lưu và hiển thị bằng UTF-8.

### Task 10 - Xác minh

- [ ] Chạy unit test xác thực và permission.
- [ ] Chạy `mvn test`.
- [ ] Chạy `mvn -q compile` và `mvn -q package`.
- [ ] Kiểm thử thủ công các luồng đăng ký/đăng nhập/đăng xuất/đổi/đặt lại mật khẩu cục bộ.
- [ ] Kiểm thử thủ công Google OAuth thành công, bị từ chối và state không hợp lệ.
- [ ] Kiểm thử thủ công toàn bộ ma trận role-route.
- [ ] Xác nhận không có production secret hoặc mật khẩu plaintext được thêm vào Git.

## 7. Tiêu chí hoàn thành

- Toàn hệ thống sử dụng một hợp đồng session cho người dùng đã xác thực.
- Xác thực local và Google vượt qua cả test thành công lẫn test lỗi.
- Khôi phục mật khẩu có thời hạn, giới hạn tần suất và chỉ dùng một lần.
- Mọi route được bảo vệ đều có xác thực cùng kiểm tra role/module/resource.
- Các hành động xác thực và bảo mật tài khoản tạo audit record an toàn.
- Maven test, compile, package và kiểm tra role thủ công đều đạt.

