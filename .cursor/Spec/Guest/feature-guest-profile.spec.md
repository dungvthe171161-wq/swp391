# Tính năng Guest: Cập nhật hồ sơ cá nhân và hồ sơ ứng tuyển

Trạng thái: Đã cập nhật theo code ngày 2026-07-13.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Guest cập nhật thông tin cá nhân, hồ sơ ứng tuyển và CV.

## Route, controller và JSP liên quan
- `/guest`, `/guest/dashboard`, `/guest/applications`, `/guest/profile`, `/guest/notification/read`, `/guest/offer/respond`.
- `/RecruitmentController` cho trang tuyển dụng và nộp hồ sơ.
- Controller: `GuestPortalController`, `RecruitmentController`; JSP: `Views/Guest/*`.

## Hiện trạng code
- `/guest/profile` lưu `Guest` cơ bản và `CandidateProfile`.
- Candidate profile yêu cầu mã xác thực email 6 số, hết hạn sau 10 phút.
- CV chấp nhận pdf/doc/docx tối đa 10MB; avatar tối đa 5MB.
- CV được đổi tên UUID và lưu tại src/main/webapp/Upload/cvs thông qua UploadPathUtil.
- Database chỉ lưu tên file ở CandidateProfile.CVFilePath.
- Chi tiết dùng chung tuân theo _Common/upload-cv.spec.md.

## Quy tắc nghiệp vụ chuẩn
- Thông tin ứng tuyển phải validate họ tên, email, số điện thoại, ngày sinh và CV.
- Email ứng tuyển cần xác thực khi thay đổi.
- CV phải lưu an toàn và không ghi đè nhầm hồ sơ khác.

## Code còn lệch spec hoặc cần bổ sung
- Cần rate limit gửi mã xác thực.
- File CV cũ và file của draft OTP chưa hoàn tất chưa được dọn tự động.
- Cần kiểm tra MIME/magic bytes thay vì chỉ extension.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

