# Đặc tả dùng chung: Phát thông báo và trạng thái đã đọc

Trạng thái: Đã rà soát theo code ngày 2026-07-14.
Ngôn ngữ: tiếng Việt có dấu. Spec này bổ sung chi tiết thao tác đọc cho `notification.spec.md`.

## Actor và phạm vi
- Mọi user đã đăng nhập nhận notification trong ứng dụng.

## Route, controller và JSP liên quan
- `POST /notifications/read`, `POST /notifications/read-all`.
- Controller: `NotificationController`; service: `NotificationService`.
- Filter/JSP fragment: `AppNotificationFilter`, `HrStaffNotificationFilter`, `Views/_NotificationBell.jspf`.

## Hiện trạng code
- Hệ thống có endpoint đánh dấu một hoặc toàn bộ notification đã đọc.
- Notification được gắn với `SystemUser.UserID` và được nạp vào UI qua filter/service.
- Contract về ownership, CSRF, redirect và idempotency chưa được mô tả đầy đủ trong spec riêng.

## Quy tắc nghiệp vụ chuẩn
- Chỉ user hiện tại được đánh dấu notification thuộc chính mình; không tin `userId` từ client.
- `read` và `read-all` phải dùng POST, CSRF và idempotent.
- Đánh dấu lại notification đã đọc vẫn trả thành công mà không tạo audit/notification mới.
- Redirect sau thao tác chỉ được về path nội bộ nằm trong allowlist; không cho open redirect.
- Số unread phải nhất quán sau cập nhật và không được âm.
- Tạo notification nghiệp vụ cần business key để hạn chế bản ghi trùng.

## Code còn lệch spec hoặc cần bổ sung
- Cần xác nhận controller/service luôn update theo cả `NotificationID` và current `UserID`.
- Cần CSRF dùng chung và chuẩn phản hồi HTML/JSON.
- Chưa có chính sách retention, cleanup và chống notification trùng.
- Cần thống nhất một filter nạp notification để tránh truy vấn lặp theo role.

## Kiểm thử tối thiểu
- Không thể đánh dấu notification của user khác bằng cách sửa ID.
- Read/read-all lặp lại an toàn và unread count chính xác.
- CSRF sai, session hết hạn và redirect ngoài hệ thống phải bị chặn.
- Hai tab cập nhật đồng thời không làm sai trạng thái.
