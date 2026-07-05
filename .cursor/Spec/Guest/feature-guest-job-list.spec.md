# Tính năng Guest: Xem danh sách và chi tiết việc làm

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Guest hoặc ứng viên public xem các vị trí tuyển dụng đang mở.

## Route, controller và JSP liên quan
- `/guest`, `/guest/dashboard`, `/guest/applications`, `/guest/profile`, `/guest/notification/read`, `/guest/offer/respond`.
- `/RecruitmentController` cho trang tuyển dụng và nộp hồ sơ.
- Controller: `GuestPortalController`, `RecruitmentController`; JSP: `Views/Guest/*`.

## Hiện trạng code
- Danh sách việc làm lấy từ `RecruitmentDAO` hoặc `RecruitmentController`.
- Dashboard Guest có recommended recruitments.
- Status tuyển dụng legacy có `Waiting`, `New`, `Rejected`, `Applied`, `Deleted`.

## Quy tắc nghiệp vụ chuẩn
- Chỉ hiển thị job đang mở theo trạng thái được phép.
- Chi tiết job phải có vị trí, mô tả, yêu cầu và hành động ứng tuyển.
- Không hiển thị job đã xóa hoặc không còn hiệu lực.

## Code còn lệch spec hoặc cần bổ sung
- Cần xác định rõ enum nào là nguồn trạng thái recruitment đang mở.
- Cần test job hết hạn hoặc bị xóa.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

