# Tính năng: Quản lý người dùng (User management)
Trạng thái: Đã phê duyệt
Tác nhân: Admin
Độ ưu tiên: Cao
Mã nguồn liên quan: `UserController`, `SystemUserDAO`, `RoleDAO`, `EmployeeDAO`, `Admin/Users.jsp`

## Các Route
- `GET /admin/users`
- `GET /admin/users?action=getUser`
- `GET /admin/users?action=checkUsername`
- `GET /admin/users?action=toggleStatus`
- `GET /admin/users?action=delete`
- `POST /admin/users?action=save`
- `POST /admin/users?action=update`
- `POST /admin/users?action=resetPassword`
- `POST /admin/users?action=toggleStatus`
- `POST /admin/users?action=delete`

## Luồng danh sách
1. Admin truy cập tuyến đường `/admin/users`.
2. Bộ lọc (Filter) yêu cầu quyền `VIEW_USERS`.
3. Controller đọc các bộ lọc tìm kiếm `role`, `status`, `department`, `username`.
4. Lấy danh sách người dùng có phân trang, số bản ghi mặc định trên mỗi trang (page size) là 10.
5. Chuyển tiếp (forward) đến `Admin/Users.jsp`.

## Luồng tạo người dùng (User)
1. Admin gửi (submit) biểu mẫu với các trường `username`, `password`, `roleId`, `employeeId`, `isActive`.
2. Controller xác thực (validate) các trường username/password/role/employee.
3. Tạo người dùng mới trong hệ thống.
4. Gọi `DAO.changePassword(username, password)` để cập nhật mật khẩu vào `SystemUser.PasswordHash` theo logic mật khẩu hiện tại của dự án.
5. Trả về kết quả dạng JSON hoặc thực hiện chuyển hướng (redirect) tùy theo hành động (action) hiện tại.

## Luồng cập nhật người dùng
1. Admin submit thông tin cần cập nhật của người dùng.
2. Controller tiến hành xác thực dữ liệu đầu vào.
3. Cập nhật các thông tin về vai trò (role), thông tin nhân viên (employee), trạng thái hoạt động (status) và các trường liên quan khác.

## Luồng đặt lại mật khẩu (Reset password)
1. Admin gọi hành động `resetPassword`.
2. Controller sinh mật khẩu tạm thời ngẫu nhiên bằng `SecureRandom`.
3. Gọi phương thức `DAO.changePassword`.
4. Trả về mật khẩu tạm thời dạng văn bản thuần (plaintext) trong phản hồi JSON theo code hiện tại.

## Hiện trạng code
- Đã có đầy đủ chức năng CRUD người dùng kèm theo bộ lọc và phân trang.
- Đã hỗ trợ chức năng đặt lại mật khẩu (reset password).
- Mật khẩu không mã hóa SHA-256 trong controller; `DAO.changePassword` thực hiện cập nhật thẳng vào cột `SystemUser.PasswordHash` theo logic lưu trữ plain text hiện tại của dự án.
- Việc trả về mật khẩu tạm dạng văn bản thuần sau khi reset là một rủi ro bảo mật cần được xem xét lại.

## Tiêu chí nghiệm thu
- [ ] Danh sách người dùng hiển thị đúng phân trang 10 bản ghi/trang.
- [ ] Tạo tài khoản mới nhưng thiếu thông tin username/password/role/employee sẽ bị hệ thống từ chối.
- [ ] Tên đăng nhập (username) trùng lặp sẽ bị hệ thống từ chối.
- [ ] Đặt lại mật khẩu cập nhật chính xác giá trị vào cột `SystemUser.PasswordHash` trong cơ sở dữ liệu.

## Các phần việc còn thiếu
- [ ] Thay đổi luồng để không trả về mật khẩu tạm dạng văn bản thuần (plaintext) hiển thị trên màn hình, thay vào đó nên gửi email an toàn cho người dùng.
- [ ] Ghi nhật ký hệ thống (audit log) cho các thao tác tạo mới, cập nhật, xóa và đặt lại mật khẩu.
- [ ] Chuẩn hóa định dạng JSON phản hồi và mã trạng thái HTTP (HTTP status code).
