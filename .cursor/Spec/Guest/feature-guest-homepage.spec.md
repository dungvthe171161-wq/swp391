# Tính năng Guest: Xem trang chủ công khai

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Ứng viên xem thông tin BetterHR và tuyển dụng trước hoặc sau khi đăng nhập.

## Route, controller và JSP liên quan
- `/guest`, `/guest/dashboard`, `/guest/applications`, `/guest/profile`, `/guest/notification/read`, `/guest/offer/respond`.
- `/RecruitmentController` cho trang tuyển dụng và nộp hồ sơ.
- Controller: `GuestPortalController`, `RecruitmentController`; JSP: `Views/Guest/*`.

## Hiện trạng code
- Homepage public do `HomepageController` và các JSP public xử lý.
- Guest đăng nhập được redirect về `/guest/dashboard`.
- Danh sách tuyển dụng có thể đi qua `RecruitmentController`.

## Quy tắc nghiệp vụ chuẩn
- Trang public không yêu cầu session nếu chỉ xem thông tin chung.
- Nút nộp hồ sơ phải dẫn đến luồng ứng tuyển hợp lệ.
- Text hiển thị phải là tiếng Việt có dấu.

## Code còn lệch spec hoặc cần bổ sung
- Cần kiểm tra route public nào bị filter quá chặt.
- Cần đảm bảo link ứng tuyển không bỏ qua validate đăng nhập/hồ sơ.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

