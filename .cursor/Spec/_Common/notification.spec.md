# Đặc tả dùng chung: Thông báo hệ thống

Trạng thái: Đã rà soát theo code ngày 2026-07-18.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Mọi actor có chuông thông báo hoặc nhận sự kiện nghiệp vụ theo `SystemUser.UserID`.

## Route, controller và JSP liên quan
- `NotificationService`, `NotificationDAO`, `NotificationController`.
- `AppNotificationFilter`, `HrStaffNotificationFilter` và JSP `_NotificationBell.jspf`.
- `NotificationRedirectUtil`: điều hướng theo `TargetUrl` hoặc entity.
- `TaskDeadlineReminderListener` và `EmailTemplates.taskDeadlineReminder` cho email nhắc deadline task.

## Hiện trạng code
- Schema Notification đã có trường mở rộng cho actor, entity, URL đích, priority và hạn dùng.
- Filter đã nạp số lượng chưa đọc cho một số layout.
- `NotificationService` đã có event cho application mới, leave request/decision, task assigned, task status update, payroll pending/decision, recruitment pending/decision, contract pending/decision, application status change và một số thao tác Admin user/role/permission.
- HR Staff/HR Manager/Guest/Dept/Employee controller đã gọi notification ở các luồng chính như nộp hồ sơ, phỏng vấn, gửi offer, phản hồi offer, tạo task, employee cập nhật task, duyệt leave, submit/duyệt payroll và submit/duyệt contract.
- `TaskDeadlineReminderListener` gửi email nhắc deadline task qua `EmailTemplates.taskDeadlineReminder`; đây là email reminder, chưa phải bản ghi `Notification`.

## Quy tắc nghiệp vụ chuẩn
- Notification phải gắn đúng người nhận, đúng entity và URL có thể mở được.
- Đánh dấu đã đọc chỉ được thao tác trên notification của user hiện tại.
- Sự kiện quan trọng phải tạo notification cùng transaction với thay đổi trạng thái nếu có thể.
- Cần phân biệt rõ notification trong app, email nghiệp vụ và reminder chạy nền để tránh ghi thiếu hoặc gửi trùng.

## Code còn lệch spec hoặc cần bổ sung
- Chưa phải mọi workflow đều phát notification đầy đủ: cập nhật/reassign task từ Dept Manager, thay đổi work schedule, offer hết hạn và một số thao tác Admin/department vẫn cần chốt event.
- Email/notification đang gọi gần thao tác cập nhật trạng thái nhưng chưa có outbox/idempotency/transaction chung.
- Cần chuẩn hóa danh sách event bắt buộc cho tuyển dụng, payroll, contract, leave, task, work schedule và Admin security.
- Cần thống nhất redirect notification theo từng actor và chống tạo notification trùng.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Tạo task gửi notification cho employee và employee cập nhật task gửi notification cho Dept Manager.
- Submit/duyệt payroll, submit/duyệt contract và phản hồi offer tạo notification đúng actor.
- Listener nhắc deadline task chỉ gửi email đúng điều kiện và không gửi trùng trong cùng ngày.
- Notification redirect mở đúng route đích và user khác không đánh dấu đọc được notification không thuộc mình.

