# Tính năng: Tạo công việc (task) phòng ban
Trạng thái: Đã phê duyệt
Tác nhân: Trưởng phòng (Dept Manager)
Độ ưu tiên: Cao
Mã nguồn liên quan: `PostTask`, `TaskDAO`, `DAO`, `Views/DeptManager/postTask.jsp`

## Các Route
- `GET /postTask`
- `POST /postTask`

## Luồng GET
1. Trưởng phòng (Dept Manager) truy cập `/postTask`.
2. Hệ thống kiểm tra phiên làm việc (session).
3. Controller lấy dữ liệu cần hiển thị trên biểu mẫu (form) giao việc.
4. Chuyển tiếp (forward) đến `Views/DeptManager/postTask.jsp`.

## Luồng POST
1. Dept Manager gửi (submit) tiêu đề (title), mô tả (description), ngày bắt đầu (start date), ngày hết hạn (due date) và danh sách nhân viên (employee) nếu có trên form.
2. Controller xác thực (validate) các trường dữ liệu bắt buộc.
3. Gọi lớp DAO để tạo công việc (task) mới.
4. Thành công: chuyển hướng (redirect) về `/taskManager?mess=Task created successfully`.
5. Thất bại: chuyển hướng (redirect) về `/postTask?error=Failed to create task`.

## Hiện trạng code
- Đã có lớp xử lý `PostTask`.
- Đã hỗ trợ cơ chế chuyển hướng (redirect) khi thành công hoặc thất bại.

## Tiêu chí nghiệm thu
- [ ] Người dùng chưa đăng nhập bị chuyển hướng về trang `/Views/Login.jsp`.
- [ ] Tạo công việc thành công đưa người dùng quay lại màn hình quản lý công việc (task manager).
- [ ] Tạo công việc gặp lỗi đưa người dùng quay lại màn hình tạo công việc `postTask` kèm theo thông báo lỗi `error`.

## Các phần việc còn thiếu
- [ ] Bổ sung kiểm tra đảm bảo nhân viên được giao việc (assigned employee) phải thuộc phòng ban do Trưởng phòng (Dept Manager) đó quản lý.
- [ ] Chuẩn hóa các thông báo (message) phản hồi sang tiếng Việt có dấu.
