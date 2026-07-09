# Thiết kế hệ thống thông báo BetterHR

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu.
Phạm vi: Notification dùng chung cho Admin, HR Manager, HR Staff, Dept Manager, Employee và Guest.

## Hiện trạng code
- Database `Notification` đã có các trường mở rộng: `ActorUserID`, `EntityType`, `EntityID`, `TargetUrl`, `Priority`, `ExpiresAt`.
- Code đã có `NotificationService`, `NotificationDAO`, `NotificationController`, `NotificationRedirectUtil`.
- `AppNotificationFilter` và `HrStaffNotificationFilter` nạp dữ liệu chuông thông báo cho một số layout.
- JSP dùng chung `_NotificationBell.jspf` đã tồn tại.
- Một số workflow đã tạo notification: ứng tuyển, phỏng vấn, leave và một số sự kiện HR.

## Thiết kế chuẩn
- Notification phải gắn `UserID` người nhận, entity nghiệp vụ và URL điều hướng an toàn.
- Đánh dấu đã đọc chỉ được thực hiện bởi chính người nhận.
- Event quan trọng nên tạo notification trong cùng transaction với thay đổi trạng thái.
- Notification phải hiển thị tiếng Việt có dấu, thời gian rõ ràng và phân biệt đã đọc/chưa đọc.
- Filter/topbar chỉ nạp dữ liệu cần thiết, tránh query nặng trên mọi request.

## Code còn lệch thiết kế
- Chưa phải mọi workflow đều phát notification đầy đủ.
- Còn song song notification legacy và service mới ở một số chỗ.
- Cần test redirect theo từng actor vì một URL có thể hợp lệ với actor này nhưng không hợp lệ với actor khác.
- Cần chuẩn hóa danh sách event bắt buộc cho payroll, contract, task, leave, recruitment và account.

## Kiểm thử tối thiểu
- User chỉ thấy notification của chính mình.
- Đọc một notification và đọc tất cả hoạt động đúng.
- Click notification mở đúng route hoặc fallback an toàn.
- Notification count cập nhật sau khi đọc.
- Maven compile pass sau mọi thay đổi service/filter/JSP.