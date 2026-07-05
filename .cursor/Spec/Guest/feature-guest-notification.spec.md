# Tính năng Guest: Xem thông báo

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Guest xem và đánh dấu đã đọc thông báo của mình.

## Route, controller và JSP liên quan
- `/guest`, `/guest/dashboard`, `/guest/applications`, `/guest/profile`, `/guest/notification/read`, `/guest/offer/respond`.
- `/RecruitmentController` cho trang tuyển dụng và nộp hồ sơ.
- Controller: `GuestPortalController`, `RecruitmentController`; JSP: `Views/Guest/*`.

## Hiện trạng code
- `/guest/notification/read` dùng `NotificationDAO` legacy.
- `AppNotificationFilter` và bell dùng notification service mới ở một số layout.
- Dashboard nạp notification gần nhất và số chưa đọc.

## Quy tắc nghiệp vụ chuẩn
- Chỉ đánh dấu notification của user hiện tại.
- Notification phải có nội dung tiếng Việt, thời gian và link nếu có.
- Đọc tất cả không ảnh hưởng notification của user khác.

## Code còn lệch spec hoặc cần bổ sung
- Cần thống nhất notification legacy và service mới.
- Cần test redirect từ chuông thông báo cho Guest.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

