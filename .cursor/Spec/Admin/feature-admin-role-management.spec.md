# Tính năng: Quản lý vai trò (Role management)
Trạng thái: Đã phê duyệt
Tác nhân: Admin
Độ ưu tiên: Cao
Mã nguồn liên quan: `RoleServlet`, `RoleDAO`, `Admin/Roles.jsp`

## Các Route
- `GET /admin/role/*`
- `POST /admin/role/*`
- `PUT /admin/role/*`
- `DELETE /admin/role/*`

## Luồng danh sách
1. Admin truy cập màn hình quản lý vai trò (role).
2. Bộ lọc (Filter) yêu cầu quyền `VIEW_ROLES`.
3. `RoleServlet` lấy danh sách vai trò (role).
4. Phân trang với kích thước trang mặc định `DEFAULT_PAGE_SIZE = 10`.
5. Chuyển tiếp (forward) đến `Admin/Roles.jsp` hoặc trả về dữ liệu dạng JSON tùy theo yêu cầu của API.

## Luồng CRUD (Thêm, Đọc, Sửa, Xóa)
1. Admin tạo vai trò (role) mới bằng phương thức `POST`.
2. Admin xem chi tiết vai trò bằng phương thức `GET` có chứa tham số ID.
3. Admin cập nhật vai trò bằng phương thức `PUT`.
4. Admin xóa vai trò bằng phương thức `DELETE` nếu vai trò đó không có ràng buộc dữ liệu.

## Hiện trạng code
- Đã có `RoleServlet` xử lý tuyến đường `/admin/role/*`.
- Đã thiết lập loại nội dung JSON (JSON content type) cho API.
- Bộ lọc quyền (Permission filter) nhận diện API vai trò khi đường dẫn (path) có ID hoặc sử dụng phương thức (method) khác `GET`.

## Tiêu chí nghiệm thu
- [ ] Danh sách vai trò hiển thị đúng số lượng 10 bản ghi trên mỗi trang (page size = 10).
- [ ] Các hành động tạo/sửa/xóa vai trò trả về kết quả JSON rõ ràng.
- [ ] Người dùng thiếu quyền `VIEW_ROLES` sẽ bị chặn truy cập.
- [ ] Lỗi xác thực (validation error) không cho phép tạo vai trò có tên rỗng.

## Các phần việc còn thiếu
- [ ] Định nghĩa quy tắc không cho phép xóa vai trò (role) đang có người dùng gán vào.
- [ ] Thêm nhật ký hoạt động (audit log) khi có thay đổi liên quan đến vai trò.
