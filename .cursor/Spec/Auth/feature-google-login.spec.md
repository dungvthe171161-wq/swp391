# Tính năng: Đăng nhập bằng tài khoản Google (Google Login)
Trạng thái: Đã phê duyệt
Tác nhân: Guest, SystemUser
Độ ưu tiên: Cao
Mã nguồn liên quan: `GoogleAuthController`, `LoginController`, `DAO`, `DBConnection`, `SessionSecurityFilter`, `constant.Iconstant`, `Views/Login.jsp`

## Mục tiêu
Cho phép người dùng thực hiện đăng nhập bằng Google OAuth2 độc lập với luồng đăng nhập truyền thống bằng tên đăng nhập/mật khẩu (username/password). Sau khi xác thực thành công qua Google, hệ thống sẽ tạo phiên làm việc (session) cho `systemUser` và chuyển hướng (redirect) về `/homepage` để trang chủ hiển thị các quyền hạn tương ứng theo vai trò (role) hiện tại.

## Các Route
- `GET /auth/google`: khởi tạo trạng thái (state) bảo mật và chuyển hướng người dùng sang trang Google OAuth2.
- `GET /auth/google/callback`: nhận mã ủy quyền (authorization code) từ Google, lấy thông tin token/profile người dùng và khởi tạo session.
- `GET /loginByGmail`: tuyến đường cũ (legacy route) vẫn được giữ lại để trỏ về xử lý callback theo servlet hiện tại.

## Bộ lọc công khai (Public filter)
Bộ lọc bảo mật `SessionSecurityFilter` bắt buộc phải cho phép truy cập công khai vào các tuyến đường sau:
- `/auth/google`
- `/auth/google/callback`
- `/loginByGmail`

## Luồng chính
1. Khách truy cập (Guest) nhấp vào nút `Tiếp tục với Google` trên trang `/login`.
2. `GoogleAuthController` kiểm tra cấu hình mã định danh client `GOOGLE_CLIENT_ID` và mã bí mật client `GOOGLE_CLIENT_SECRET`.
3. Nếu thiếu cấu hình trên hệ thống, chuyển hướng về `/login?error=google_config`.
4. Nếu cấu hình đầy đủ, controller sinh mã trạng thái bảo mật `googleOAuthState`, lưu đường dẫn chuyển hướng `googleRedirectUri` vào session và chuyển hướng người dùng sang Google với các phạm vi quyền (scope) gồm `openid email profile`.
5. Google thực hiện callback về `/auth/google/callback` kèm theo các tham số `code` và `state`.
6. Controller tiến hành xác thực tham số `state`. Nếu sai mã `state`, chuyển hướng về `/login?error=google_state` để tránh tấn công CSRF.
7. Controller dùng mã `code` gửi request đổi lấy mã thông báo (token).
8. Controller thực hiện xác thực mã `id_token` bằng dịch vụ Google token info và kiểm tra đối tượng sử dụng (audience) có khớp với `GOOGLE_CLIENT_ID` hay không.
9. Controller trích xuất thông tin hồ sơ (profile) của người dùng gồm: mã định danh Google `GoogleID/sub`, địa chỉ `Email`, tên hiển thị `Name`, và đường dẫn ảnh đại diện `AvatarUrl`.
10. Nếu thông tin hồ sơ thiếu trường GoogleID hoặc Email, chuyển hướng về `/login?error=google_profile`.
11. Nếu gặp lỗi kết nối cơ sở dữ liệu (database), chuyển hướng về `/login?error=db`.
12. Hệ thống tiến hành xác định và tìm kiếm tài khoản người dùng theo thứ tự ưu tiên:
    - Tìm kiếm bản ghi `SystemUser` theo giá trị `GoogleID`.
    - Nếu không tìm thấy, tiếp tục tìm kiếm theo địa chỉ `Email`.
    - Nếu email đã tồn tại trong hệ thống, cập nhật các trường thông tin `GoogleID`, `AvatarUrl`, đặt lại bộ đếm đăng nhập sai (failed login) và trạng thái khóa (lock status), đồng thời ghi nhận thời điểm đăng nhập cuối `LastLogin`.
    - Nếu email chưa tồn tại trong hệ thống, tạo mới tài khoản `SystemUser` với các thiết lập mặc định: `LoginProvider='GOOGLE'`, `PasswordHash=NULL`, `RoleID=Guest`, `EmployeeID=NULL`, `IsActive=TRUE`.
13. Nếu tài khoản người dùng đang ở trạng thái ngừng hoạt động (inactive), chuyển hướng về `/login?error=inactive`.
14. Nếu tài khoản người dùng đang bị khóa (locked), chuyển hướng về `/login?error=locked`.
15. Controller thực hiện hủy phiên làm việc (invalidate session) cũ, tạo phiên làm việc mới an toàn và thiết lập thuộc tính `systemUser`.
16. Chuyển hướng người dùng về trang chủ `/homepage`.

## Các giá trị mặc định khi tạo tài khoản Google mới
- `Username`: được sinh tự động từ email/tên người dùng, đảm bảo không bị trùng lặp trong hệ thống.
- `Email`: địa chỉ email nhận từ Google được chuyển hoàn toàn về chữ thường (lower-case).
- `PasswordHash`: đặt là `NULL`.
- `GoogleID`: lấy theo trường `sub` trong profile của Google.
- `AvatarUrl`: lấy theo trường `picture` trong profile của Google.
- `LoginProvider`: đặt giá trị là `GOOGLE`.
- `RoleID`: vai trò mặc định là `Guest` lấy qua hàm `DAO.getOrCreateRoleIdByName("Guest")`.
- `EmployeeID`: đặt là `NULL`.
- `FailedLoginAttempt`: đặt bằng `0`.
- `LockedUntil`: đặt là `NULL`.
- `LastLogin`: đặt bằng thời gian hiện tại `NOW()`.
- `IsActive`: đặt bằng `TRUE`.
- `CreatedDate`: đặt bằng thời gian hiện tại `NOW()`.

## Nguyên tắc bảo mật
- Client ID và Client Secret phải được đọc từ biến môi trường (env), cấu hình hệ thống (system property) hoặc từ tập tin thuộc tính `/META-INF/google.properties`, tuyệt đối không được viết cứng (hardcode) trong code Java.
- Mã trạng thái OAuth state bắt buộc phải khớp hoàn toàn giữa lúc gửi đi và lúc nhận về nhằm ngăn chặn tấn công giả mạo yêu cầu chéo trang (CSRF).
- Mã thông báo `id_token` bắt buộc phải được xác thực audience trước khi tin cậy và sử dụng thông tin profile.
- Không lưu trữ access token của Google ở phía Client hoặc trong Session của hệ thống BetterHR.
- Tránh trùng lặp địa chỉ email bằng cách tự động liên kết tài khoản (link account) thông qua trường `Email`.
- Tránh trùng lặp tài khoản Google bằng cách tra cứu theo `GoogleID`.
- Tuyệt đối không khởi tạo session cho các tài khoản đang bị khóa hoặc ngừng hoạt động.

## Hiện trạng code
- Đã hiển thị nút đăng nhập bằng Google trên trang `Views/Login.jsp`.
- Đã xây dựng `GoogleAuthController` xử lý các tuyến đường `/auth/google`, `/auth/google/callback`, `/loginByGmail`.
- Đã có đầy đủ các hàm trong lớp DAO: `getAccountByGoogleId`, `getAccountByEmail`, `updateGoogleAccount`, `createGoogleUser`.
- Đã chuyển hướng về trang chủ `/homepage` sau khi người dùng đăng nhập qua Google thành công.

## Tiêu chí nghiệm thu
- [ ] Giao diện hiển thị lỗi `/login?error=google_config` khi thiếu các cấu hình Google cần thiết.
- [ ] Nhấp chọn đăng nhập bằng Google chuyển hướng chính xác sang trang đăng nhập của Google với đúng client ID và URL callback.
- [ ] Các callback gửi sai mã `state` sẽ bị hệ thống từ chối và báo lỗi.
- [ ] Các token sai thông tin audience sẽ bị hệ thống từ chối xác thực.
- [ ] Tài khoản đã tồn tại `GoogleID` đăng nhập thành công vào đúng tài khoản đó.
- [ ] Tài khoản tồn tại địa chỉ `Email` trùng khớp nhưng chưa có `GoogleID` sẽ được tự động liên kết với Google.
- [ ] Địa chỉ email mới hoàn toàn sẽ kích hoạt tạo tài khoản mới với vai trò mặc định là `Guest`.
- [ ] Người dùng có tài khoản đang bị khóa hoặc ngừng hoạt động không thể thực hiện đăng nhập.
- [ ] Đăng nhập thành công tạo phiên làm việc `systemUser` và chuyển hướng về `/homepage`.

## Các phần việc còn thiếu
- [ ] Bổ sung mã kiểm thử tích hợp (integration test) cho luồng OAuth callback bằng cách giả lập (mock) phản hồi từ phía Google.
- [ ] Chuẩn hóa việc ghi log các lỗi liên quan đến OAuth và lỗi cơ sở dữ liệu theo định dạng ghi log chung của dự án.
