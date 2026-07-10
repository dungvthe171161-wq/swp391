# Tính năng: Khách (Guest) xem danh sách thông báo (Notification View) Giai đoạn 2
Trạng thái: Bản thảo
Tác nhân: Ứng viên tự do (Guest Candidate)
Độ ưu tiên: Trung bình
Dữ liệu liên quan: `Notification`, `SystemUser`, `Application`
Đặc tả chung liên quan: `../_Common/notification.spec.md`

## Mục tiêu
Hiển thị danh sách các thông báo (notification) liên quan đến Guest ngay trong cổng thông tin Guest portal.

Bảng `Notification` là phân hệ dùng chung cho tất cả các tác nhân trong hệ thống, không được thiết kế dành riêng cho phân hệ Guest. Đặc tả này chỉ tập trung mô tả chi tiết cách Guest xem các thông báo của chính mình.

## Các sự kiện kích hoạt tạo thông báo
- Hồ sơ ứng tuyển (application) được khởi tạo thành công trên hệ thống.
- Hồ sơ ứng tuyển được cập nhật thay đổi trạng thái.
- Lịch phỏng vấn (interview) được khởi tạo, thay đổi lịch hẹn, hoặc bị hủy bỏ.
- Thư mời nhận việc (offer) được gửi đi từ phía công ty.
- Thư mời nhận việc sắp hết hạn phản hồi.
- Thư mời nhận việc được bộ phận HR cập nhật kết quả.

## Các tính năng hiển thị cho Guest
- Hiển thị các thông báo mới nhất trên bảng điều khiển (dashboard).
- Xem danh sách đầy đủ tất cả các thông báo.
- Đánh dấu thông báo là đã đọc (mark as read).

## Quy tắc xử lý dữ liệu (Data rule)
- Thực hiện lọc dữ liệu theo điều kiện `Notification.UserID = session.systemUser.UserID`.
- Nếu bản ghi thông báo có liên kết mã `ApplicationID`, chỉ hiển thị thông báo đó khi hồ sơ ứng tuyển đó thuộc về tài khoản Guest đang đăng nhập hiện tại.
- Người dùng Guest tuyệt đối không được phép xem các thông báo của các tác nhân khác.
- Sử dụng chung logic truy vấn dữ liệu từ lớp `NotificationDAO`.

## Tiêu chí nghiệm thu
- [ ] Thông báo được gán liên kết chính xác với mã tài khoản người dùng `SystemUser.UserID`.
- [ ] Bản ghi thông báo có thể liên kết với mã `ApplicationID` nếu nội dung thông báo đó trực tiếp liên quan đến một hồ sơ ứng tuyển cụ thể.
- [ ] Người dùng Guest chỉ đọc được các thông báo được gửi riêng cho chính mình.
- [ ] Các thông báo mới phải hiển thị rõ ràng trạng thái chưa đọc để người dùng nhận diện.
- [ ] Hành động đánh dấu đã đọc (mark read) của Guest chỉ được phép cập nhật trạng thái đối với thông báo của chính Guest đó.
