# Tính năng Guest: Nộp hồ sơ ứng tuyển

Trạng thái: Đã cập nhật theo code ngày 2026-07-13.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Guest nộp hồ sơ vào một recruitment.

## Route, controller và JSP liên quan
- `/guest`, `/guest/dashboard`, `/guest/applications`, `/guest/profile`, `/guest/notification/read`, `/guest/offer/respond`.
- `/RecruitmentController` cho trang tuyển dụng và nộp hồ sơ.
- Controller: `GuestPortalController`, `RecruitmentController`; JSP: `Views/Guest/*`.

## Hiện trạng code
- `RecruitmentController.confirmApplication` tạo `Application` status/currentStep `Applied`.
- Có tạo notification cho ứng viên và HR Staff.
- CV lấy từ CandidateProfile.CVFilePath và được sao chép vào Application.CV tại thời điểm tạo application.
- Upload và hiển thị CV tuân theo _Common/upload-cv.spec.md.

## Quy tắc nghiệp vụ chuẩn
- Một Guest không được nộp trùng cùng một recruitment nếu đã có application active.
- Application mới phải có GuestID, RecruitmentID, CV/source và ngày nộp.
- Thay CV hồ sơ sau khi đã nộp không tự động thay CV của application cũ.
- Nộp thành công phải có trang/notification xác nhận.

## Code còn lệch spec hoặc cần bổ sung
- Cần đảm bảo unique `(GuestID, RecruitmentID)` ở database hoặc service.
- Cần test ứng viên nộp lại cùng job.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

