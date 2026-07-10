# Tính năng: Cập nhật công việc (task) phòng ban
Trạng thái: Đã phê duyệt
Tác nhân: Trưởng phòng (Dept Manager)
Độ ưu tiên: Cao
Mã nguồn liên quan: `com.hrm.controller.dept.ViewTask`, `DAO`, `Views/DeptManager/viewTask.jsp`

## Các Route
- `GET /viewTask?id={taskId}`
- `POST /viewTask`

## Luồng GET
1. Trưởng phòng (Dept Manager) mở trang chi tiết công việc bằng mã định danh công việc (id).
2. Controller kiểm tra phiên làm việc (session) của người dùng.
3. Tiến hành lấy thông tin công việc theo id.
4. Chuyển tiếp (forward) đến `/Views/DeptManager/viewTask.jsp`.

## Luồng POST
1. Trưởng phòng (Dept Manager) gửi (submit) các thông tin gồm: `taskId`, `title`, `description`, `startDate`, `dueDate`.
2. Controller thực hiện phân tích cú pháp (parse) mã `taskId`.
3. Gọi hàm `DAO.updateTask` để cập nhật dữ liệu.
4. Nếu cập nhật thành công, gọi tiếp hàm `DAO.deleteTaskAssignments(taskId)` để xóa bỏ và thiết lập lại các phân công công việc cũ (reset assignment).
5. Chuyển hướng (redirect) về tuyến đường `/viewTask?id={taskId}&mess=Task updated successfully`.
6. Nếu cập nhật thất bại, chuyển hướng (redirect) về tuyến đường `/viewTask?id={taskId}&error=Failed to update task`.

## Hiện trạng code
- Đã xây dựng hoàn thiện luồng cập nhật thông tin công việc và đặt lại phân công nhân sự.
- Chưa tích hợp xử lý riêng mã lỗi HTTP 400 khi xảy ra ngoại lệ chuyển đổi kiểu số `NumberFormatException` đối với ID công việc.

## Tiêu chí nghiệm thu
- [ ] Cập nhật công việc thành công chuyển hướng người dùng kèm theo thông điệp thông báo `mess`.
- [ ] Cập nhật công việc thất bại chuyển hướng người dùng kèm theo thông điệp thông báo lỗi `error`.
- [ ] Người dùng chưa đăng nhập bị chuyển hướng về trang đăng nhập `/Views/Login.jsp`.

## Các phần việc còn thiếu
- [ ] Bắt các lỗi khi truyền sai ID công việc và trả về thông báo giao diện thân thiện với người dùng.
- [ ] Kiểm tra xác thực đảm bảo công việc phải thuộc phòng ban quản lý của Trưởng phòng đó trước khi cho phép chỉnh sửa.
