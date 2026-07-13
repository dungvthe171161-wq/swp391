# Tính năng HR Staff: Quản lý ứng viên

Trạng thái: Đã rà soát theo code ngày 2026-07-13.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- HR Staff xem danh sách application, CV và xử lý ứng viên.

## Route, controller và JSP liên quan
- `/hrstaff`, `/postRecruitments`, `/candidates`, `/viewCV`, `/hrstaff/interviews/schedule`.
- Controller: `HrStaffHomeController`, `PostRecruitmentController`, `ViewCandidateController`, `InterviewScheduleController`, `ViewCV`.

## Hiện trạng code
- `ViewCandidateController` mapping `/candidates` và dùng `VIEW_RECRUITMENT`.
- `ViewCV` dùng `/viewCV`; ưu tiên `applicationId`, fallback `guestId` legacy.
- Route `/candidates` được `RoleAuthorizationFilter` cho cả HR Manager và HR Staff.

## Quy tắc nghiệp vụ chuẩn
- Danh sách ứng viên phải dựa trên `Application`, không chỉ `Guest`.
- CV phải là CV của application đang xét.
- Chuyển trạng thái phải validate state machine.

## Code còn lệch spec hoặc cần bổ sung
- Recruitment/candidate/interview/offer còn dùng chung `VIEW_RECRUITMENT`; cần tách `MANAGE_APPLICANTS`, `SCHEDULE_INTERVIEW`, `CREATE_RECRUITMENT`.
- `ViewCV` fallback `guestId` và POST legacy cập nhật `Guest.Status` vẫn còn.
- Interview schedule chưa có transaction chung và guard chống lịch trùng theo application.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- `/candidates` và `/viewCV?applicationId=...` hiển thị đúng khi một Guest có nhiều application.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.
