# Spec triển khai Guest Phase 2

Trạng thái: Đã rà soát theo code ngày 2026-07-13.
Ngôn ngữ: tiếng Việt có dấu.

## Actor và phạm vi
- Guest portal phase 2: profile, application, interview, offer và chuyển đổi sang employee.

## Route, controller và JSP liên quan
- `/guest/profile`, `/guest/applications`, `/guest/dashboard`, `/guest/offer/respond`.
- `/hrstaff/interviews/schedule`, `/hrstaff/offers/manage`, `/hr/create-employee`.
- Controller: `GuestPortalController`, `InterviewScheduleController`, `OfferManagementController`, `CreateEmployeeController`.

## Hiện trạng code
- `CandidateProfile`: lưu hồ sơ ứng tuyển, xác nhận email bằng mã trong session, upload CV PDF/DOC/DOCX tối đa 10MB.
- `Application`: tạo liên kết `Recruitment`, `Guest`, `CandidateProfile`; chặn ứng tuyển trùng một `Recruitment`.
- Interview: HR đặt/đổi/hủy lịch, cập nhật Pass/Fail; gửi email và notification cho Guest; dashboard Guest hiển thị lịch thật.
- Offer: HR tạo/lưu nháp/gửi offer; gửi mail + notification; Guest accept/reject trên `/guest/applications`; accept cập nhật `Application` sang `Hired`.
- Employee conversion: `/hr/create-employee` chỉ hiện ứng viên `Application = Hired` và `Offer = Accepted`; server-side chặn nếu chưa có offer Accepted hợp lệ; sau tạo Employee set `Guest.Status = Converted`, giữ lịch sử `Guest`/`Application`.

## Quy tắc nghiệp vụ chuẩn
- Không cần thêm bảng mới cho phase này; dùng `CandidateProfile`, `Application`, `Interview`, `Offer`, `Notification`.
- `ViewCV` và link HR nên ưu tiên `applicationId`.

## Code còn lệch spec hoặc cần bổ sung
- Workflow service transaction trung tâm cho toàn bộ bước tuyển dụng.
- Tự động tạo Employee/onboarding/contract ngay khi Guest accepted.
- Loại bỏ hoàn toàn luồng CV/action legacy theo `guestId` trên `ViewCV`.
- Cần test lại trên Tomcat/MySQL thật sau deploy để xác nhận mail config và upload/static path.

## Kiểm thử tối thiểu
- `mvn -q compile` pass.
- Guest accept offer → HR tạo employee → `Guest.Status = Converted`, lịch sử application còn nguyên.
- Upload CV và xác thực email profile hoạt động đúng giới hạn 10MB.
