# Tính năng Guest: Bảng điều khiển ứng viên

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Guest xem tổng quan hồ sơ, application, interview, offer và notification.

## Route, controller và JSP liên quan
- `/guest`, `/guest/dashboard`, `/guest/applications`, `/guest/profile`, `/guest/notification/read`, `/guest/offer/respond`.
- `/RecruitmentController` cho trang tuyển dụng và nộp hồ sơ.
- Controller: `GuestPortalController`, `RecruitmentController`; JSP: `Views/Guest/*`.

## Hiện trạng code
- `GuestPortalController.loadBaseData` nạp profile, applications, interviews, offers, notifications.
- Dashboard dùng `Views/Guest/Dashboard.jsp`.
- Có số lượng application active, hired, rejected và offer pending.

## Quy tắc nghiệp vụ chuẩn
- Dashboard chỉ hiển thị dữ liệu của user hiện tại.
- Không cho xem application của Guest khác qua sửa URL.
- Notification phải mở đúng route đích.

## Code còn lệch spec hoặc cần bổ sung
- Cần test user chưa có Guest profile.
- Cần test user có nhiều application.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

