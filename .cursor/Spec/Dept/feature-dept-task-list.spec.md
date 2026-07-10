# Tính năng: Danh sách công việc (task) phòng ban
Trạng thái: Đã phê duyệt
Tác nhân: Trưởng phòng (Dept Manager)
Độ ưu tiên: Cao
Mã nguồn liên quan: `TaskManager`, `Views/DeptManager/taskManager.jsp`

## Các Route
- `GET /taskManager`
- `POST /taskManager`

## Luồng GET
1. Trưởng phòng (Dept Manager) truy cập `/taskManager`.
2. Hệ thống kiểm tra phiên làm việc (session).
3. Controller lấy danh sách công việc (task).
4. Chuyển tiếp (forward) đến `/Views/DeptManager/taskManager.jsp`.

## Luồng POST
1. Nếu có yêu cầu gửi dữ liệu dạng `POST` tới tuyến đường `/taskManager`.
2. Controller thực hiện chuyển hướng (redirect) về lại `GET /taskManager`.

## Hiện trạng code
- Đã xây dựng lớp `TaskManager`.
- Yêu cầu `POST /taskManager` đã được cấu hình chuyển hướng về `/taskManager`.

## Tiêu chí nghiệm thu
- [ ] Trưởng phòng (Dept Manager) xem được danh sách các công việc.
- [ ] Người dùng chưa đăng nhập bị chuyển hướng về trang đăng nhập.
- [ ] Phương thức `POST` không xử lý trực tiếp dữ liệu mà được chuyển hướng về phương thức `GET`.

## Các phần việc còn thiếu
- [ ] Giới hạn danh sách công việc chỉ hiển thị các công việc thuộc phòng ban do Trưởng phòng (Dept Manager) đó quản lý.
- [ ] Bổ sung các chức năng lọc, tìm kiếm, và phân trang (filter/search/pagination) nếu giao diện (UI) yêu cầu.
