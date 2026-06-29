# Tính năng: Quản lý phòng ban
Trạng thái: Chưa hoàn thiện (Partial)
Tác nhân: Admin
Độ ưu tiên: Cao
Mã nguồn liên quan: `DepartmentController`, `DepartmentDAO`, `Admin/Departments.jsp`

## Các Route
- `GET /departments`
- `GET /departments?action=departments`
- `GET /departments?action=edit`
- `GET /departments?action=permissions`
- `POST /departments?action=department-save`
- `POST /departments?action=department-delete`
- `POST /departments?action=department-permissions-save`

## Luồng danh sách
1. Admin truy cập `/departments`.
2. Controller đọc dữ liệu tìm kiếm/bộ lọc/phân trang (search/filter/page).
3. Truy vấn phòng ban (department) bằng `PreparedStatement`.
4. Chuyển tiếp (forward) đến `Admin/Departments.jsp`.

## Luồng thêm/sửa/xóa
1. Admin mở biểu mẫu (form) thêm mới hoặc chỉnh sửa (edit).
2. Gửi (submit) hành động `department-save`.
3. Controller xác thực (validate) và lưu dữ liệu.
4. Xóa phòng ban bằng hành động `department-delete` nếu hợp lệ.
5. Chuyển hướng (redirect) về `/departments?action=departments`.

## Hiện trạng code
- Controller `/departments` đã có sẵn.
- Có lỗi (bug) chuyển hướng trong phần lưu quyền hạn (save permissions) về `/department?action=departments` dạng số ít, trong khi tuyến đường (route) đúng phải là `/departments`.
- Route `/departments` chưa được đưa vào `ModulePermissionFilter` hay `AdminAuthorizationFilter`.

## Tiêu chí nghiệm thu
- [ ] Admin xem được danh sách phòng ban (department).
- [ ] Thêm/sửa/xóa phòng ban chuyển hướng (redirect) về đúng `/departments?action=departments`.
- [ ] Người dùng không phải Admin không truy cập được vào `/departments`.

## Các phần việc còn thiếu
- [ ] Thêm `/departments` vào bộ lọc bảo vệ (filter).
- [ ] Sửa chuyển hướng dạng số ít `/department` thành `/departments`.
- [ ] Thêm nhật ký hoạt động (audit log) cho các thay đổi liên quan đến phòng ban.
