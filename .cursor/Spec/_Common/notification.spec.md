# Đặc tả dùng chung: Thông báo hệ thống

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Mọi actor có chuông thông báo hoặc nhận sự kiện nghiệp vụ theo `SystemUser.UserID`.

## Route, controller và JSP liên quan
- `NotificationService`, `NotificationDAO`, `NotificationController`.
- `AppNotificationFilter`, `HrStaffNotificationFilter` và JSP `_NotificationBell.jspf`.
- `NotificationRedirectUtil`: điều hướng theo `TargetUrl` hoặc entity.

## Hiện trạng code
- Schema Notification đã có trường mở rộng cho actor, entity, URL đích, priority và hạn dùng.
- Filter đã nạp số lượng chưa đọc cho một số layout.
- Một số sự kiện tuyển dụng, phỏng vấn và nghỉ phép đã tạo notification.

## Quy tắc nghiệp vụ chuẩn
- Notification phải gắn đúng người nhận, đúng entity và URL có thể mở được.
- Đánh dấu đã đọc chỉ được thao tác trên notification của user hiện tại.
- Sự kiện quan trọng phải tạo notification cùng transaction với thay đổi trạng thái nếu có thể.

## Code còn lệch spec hoặc cần bổ sung
- Chưa phải mọi workflow đều phát notification đầy đủ.
- Cần chuẩn hóa danh sách event bắt buộc cho tuyển dụng, payroll, contract, leave và task.
- Cần test redirect notification theo từng actor.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

