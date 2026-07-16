# Tính năng HR Staff: Lập lịch phỏng vấn

Trạng thái: Đã rà soát theo code ngày 2026-07-14.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- HR Staff lập lịch hoặc đổi lịch phỏng vấn cho application hợp lệ trong quy trình tuyển dụng.

## Route, controller và JSP liên quan
- `GET /hrstaff/interviews/schedule`, `POST /hrstaff/interviews/schedule`.
- Controller: `InterviewScheduleController`; DAO: `ApplicationDAO`, `InterviewDAO` và DAO liên quan.
- JSP: `Views/HrStaff/ScheduleInterview.jsp`.
- Phía Guest xem lịch qua các route trong `GuestPortalController`.

## Hiện trạng code
- Controller yêu cầu quyền `VIEW_RECRUITMENT`, tải application và xử lý dữ liệu lịch phỏng vấn.
- Hệ thống có các trạng thái Interview: `Scheduled`, `Completed`, `Cancelled`, `NoShow`, `Rescheduled`; Result: `Pending`, `Passed`, `Failed`.
- Luồng có gửi email/notification theo thiết kế hiện có của module tuyển dụng.

## Quy tắc nghiệp vụ chuẩn
- Chỉ application ở bước hợp lệ mới được lập lịch; ngày giờ không được nằm trong quá khứ.
- Phải validate ứng viên, người phỏng vấn, thời gian, timezone và địa điểm hoặc meeting URL.
- Không tạo lịch xung đột cho cùng ứng viên hoặc người phỏng vấn.
- Reschedule phải giữ lịch sử lịch cũ và cập nhật trạng thái nhất quán.
- Khi lập lịch thành công, `Application.Status = Interview` và `CurrentStep = Interview` trong cùng transaction.
- Email/notification chỉ gửi sau khi dữ liệu được commit; retry không được tạo lịch trùng.

## Code còn lệch spec hoặc cần bổ sung
- Quyền tạo lịch đang dùng chung `VIEW_RECRUITMENT`, chưa có `SCHEDULE_INTERVIEW`.
- Cần xác nhận transaction giữa Interview, Application, notification và email outbox.
- Chưa có quy tắc chuẩn về timezone, xung đột lịch và idempotency.
- Cần CSRF, audit và lý do khi cancel/reschedule.

## Kiểm thử tối thiểu
- Không lập lịch cho application sai trạng thái, ngày quá khứ hoặc application không tồn tại.
- Phát hiện xung đột ứng viên/người phỏng vấn và submit lặp.
- Application và Interview phải rollback cùng nhau khi ghi dữ liệu thất bại.
- Email lỗi không được làm mất lịch đã lưu và phải có trạng thái retry rõ ràng.
