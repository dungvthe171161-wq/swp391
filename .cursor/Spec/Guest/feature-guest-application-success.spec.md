# Tính năng Guest: Xem trang ứng tuyển thành công

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Guest nhận xác nhận sau khi nộp hồ sơ.

## Route, controller và JSP liên quan
- `/guest`, `/guest/dashboard`, `/guest/applications`, `/guest/profile`, `/guest/notification/read`, `/guest/offer/respond`.
- `/RecruitmentController` cho trang tuyển dụng và nộp hồ sơ.
- Controller: `GuestPortalController`, `RecruitmentController`; JSP: `Views/Guest/*`.

## Hiện trạng code
- Sau khi tạo application, controller redirect/forward đến trang thành công tùy luồng.
- Notification cũng được tạo cho ứng viên.
- Thông tin cần hiển thị gồm job đã nộp và bước tiếp theo.

## Quy tắc nghiệp vụ chuẩn
- Không hiển thị dữ liệu của application khác.
- Trang thành công chỉ là xác nhận, không tạo thêm application.
- Có link về `/guest/applications` hoặc dashboard.

## Code còn lệch spec hoặc cần bổ sung
- Cần test refresh trang thành công không nộp lại form.
- Cần kiểm tra thông báo tiếng Việt có dấu.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

