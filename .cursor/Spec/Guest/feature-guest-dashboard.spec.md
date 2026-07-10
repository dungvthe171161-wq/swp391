# Tính năng: Bảng điều khiển của Khách (Guest Dashboard)
Trạng thái: Bản thảo
Tác nhân: Ứng viên tự do (Guest Candidate)
Độ ưu tiên: Trung bình
Mã nguồn liên quan: `GuestPortalController`, `ApplicationDAO`, `InterviewDAO`, `OfferDAO`, `NotificationDAO`

## Tuyến đường mục tiêu (Target Route)
- `GET /guest/dashboard`

## Mục tiêu
Xây dựng một trang tổng quan riêng biệt dành cho Guest sau khi đăng nhập thành công. Trang này hoàn toàn độc lập, không dùng chung giao diện với phân hệ Admin, HR hay Employee.

## Danh sách Menu trên thanh bên (Sidebar)
- Tổng quan
- Việc làm tuyển dụng
- Hồ sơ đã nộp
- Lịch phỏng vấn
- Thư mời nhận việc
- Thông báo hệ thống
- Hồ sơ cá nhân
- Đổi mật khẩu
- Đăng xuất

## Nguồn dữ liệu Giai đoạn 1 (Phase 1)
Vì mã nguồn của Giai đoạn 1 chưa sử dụng bảng `Application`, các số liệu trên bảng điều khiển Giai đoạn 1 sẽ được tổng hợp tạm thời từ bảng `Guest`:
- Tổng số hồ sơ đã nộp: Đếm số lượng bản ghi trong bảng `Guest` theo mã tài khoản `UserID` hoặc địa chỉ email.
- Đang xử lý: Bản ghi có trạng thái `Guest.Status = 'Processing'`.
- Đã duyệt/tuyển dụng: Bản ghi có trạng thái `Guest.Status = 'Hired'`.
- Bị từ chối: Bản ghi có trạng thái `Guest.Status = 'Rejected'`.

## Nguồn dữ liệu Giai đoạn 2 (Phase 2)
Khi chạy script migration dữ liệu và chuyển đổi code sang luồng công việc mới của Giai đoạn 2, bảng điều khiển sẽ lấy số liệu trực tiếp từ:
- Tổng số hồ sơ ứng tuyển (application).
- Số lượng hồ sơ ứng tuyển đang trong quá trình xử lý.
- Danh sách lịch phỏng vấn sắp tới.
- Thư mời nhận việc đang chờ phản hồi từ ứng viên.
- Các thông báo mới nhất.

Các hàm truy vấn dữ liệu Giai đoạn 2:
- `ApplicationDAO.countByUserId(systemUser.UserID)`.
- `ApplicationDAO.countActiveByUserId(systemUser.UserID)`.
- `InterviewDAO.findUpcomingByUserId(systemUser.UserID, limit)`.
- `OfferDAO.findPendingByUserId(systemUser.UserID)`.
- `NotificationDAO.findByUserId(systemUser.UserID, limit)`.
- `NotificationDAO.countUnreadByUserId(systemUser.UserID)`.

## Tiêu chí nghiệm thu
- [ ] Chỉ cho phép tài khoản Guest đã đăng nhập thành công truy cập bảng điều khiển.
- [ ] Admin được cấp quyền truy cập để phục vụ việc kiểm tra hoặc xem trước giao diện nếu hệ thống hỗ trợ cơ chế giả danh (impersonate) hoặc preview.
- [ ] Người dùng Guest tuyệt đối không xem được dữ liệu của Guest khác.
- [ ] Các chỉ số hiển thị bình thường và không gặp lỗi khi Guest chưa nộp bất kỳ hồ sơ ứng tuyển nào.
- [ ] Giao diện người dùng sử dụng đúng chủ đề BetterHR theme và hiển thị bằng tiếng Việt có dấu.
- [ ] Khi chuyển sang Giai đoạn 2, bảng điều khiển bắt buộc phải lấy dữ liệu từ các bảng mới, không tổng hợp theo cột `Guest.Status` cũ nữa.
