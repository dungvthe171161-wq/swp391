# Tính năng: Khách (Guest) xem trang chủ công khai (Public Homepage)
Trạng thái: Đã phê duyệt
Tác nhân: Ứng viên tự do (Guest Candidate)
Độ ưu tiên: Trung bình
Mã nguồn liên quan: `HomepageController`, `Views/Homepage.jsp`, `Views/Login.jsp`, `Views/Register.jsp`

## Các Route
- `GET /homepage`

## Luồng chính
1. Khách truy cập vào tuyến đường `/homepage`.
2. Nếu người dùng chưa đăng nhập, `HomepageController` thiết lập quyền truy cập công khai/khách (public/Guest).
3. Controller chuyển tiếp (forward) yêu cầu đến trang `/Views/Homepage.jsp`.
4. Người dùng xem các nội dung công khai, danh sách việc làm/liên kết công khai và các nút điều hướng đến trang đăng nhập/đăng ký.
5. Nếu người dùng đã đăng nhập với vai trò Guest, trang chủ vẫn được hiển thị bình thường như một trang công khai.

## Các nút điều hướng trên trang chủ
- Nhấp vào Logo `BetterHR` chuyển hướng về trang chủ `/homepage`.
- Nút `Đăng nhập` chuyển hướng đến trang `/login`.
- Nút `Đăng ký` chuyển hướng đến trang `/register`, tuyệt đối không trỏ nhầm về `/login`.
- Đường liên kết việc làm chuyển hướng đến `RecruitmentController`.
- Nếu đã đăng nhập vai trò Guest, trong Giai đoạn 1 menu vai trò hiển thị chữ `Guest` và trỏ về `/homepage`.
- Khi cổng thông tin Guest dashboard được xây dựng, menu vai trò Guest sẽ trỏ về `/guest/dashboard`.

## Nội dung giao diện (UI content)
- Giao diện thiết kế theo đúng chủ đề BetterHR theme.
- Toàn bộ nội dung chữ (text) hiển thị bằng tiếng Việt có dấu.
- Logo giữ nguyên tên thương hiệu `BetterHR`.
- Tuyệt đối không hiển thị các menu hoặc phân hệ quản lý nội bộ của bộ phận HR/Admin nếu người dùng chưa đăng nhập tài khoản có quyền hạn tương ứng.

## Tiêu chí nghiệm thu
- [ ] Khách truy cập vào `/homepage` bình thường và không bị hệ thống bắt buộc phải đăng nhập.
- [ ] Trang chủ hiển thị giao diện công khai theo đúng chủ đề BetterHR theme.
- [ ] Nút đăng ký mở chính xác trang `/register`.
- [ ] Liên kết việc làm mở chính xác danh sách tin tuyển dụng.
- [ ] Người dùng Guest chưa đăng nhập không nhìn thấy các liên kết hay bảng điều khiển nội bộ.
- [ ] Người dùng Guest đã đăng nhập không bị hệ thống tự động chuyển hướng sang trang chủ dành cho nhân viên (Employee Dashboard).

## Các phần việc còn thiếu
- [ ] Khi triển khai tuyến đường `/guest/dashboard`, cần cập nhật lại giá trị biến `guestUrl` bên trong lớp điều khiển `HomepageController`.
