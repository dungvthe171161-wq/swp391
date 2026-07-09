# Tính năng HR Staff: Quản lý ứng viên

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- HR Staff xem danh sách application, CV và xử lý ứng viên.

## Route, controller và JSP liên quan
- `/hrstaff`, `/postRecruitments`, `/candidates`, `/viewCV`, `/hrstaff/interviews/schedule`.
- `/hrstaff/contracts`, `/hrstaff/contracts/create`, `/hrstaff/payroll`, `/hrstaff/payroll/*`, `/api/payroll`, `/api/allowance/*`, `/api/deduction/*`.
- Controller: `HrStaffHomeController`, `PostRecruitmentController`, `ViewCandidateController`, `InterviewScheduleController`, `ContractListController`, `CreateContractController`, `PayrollManagementController`.

## Hiện trạng code
- `ViewCandidateController` mapping `/candidates` và dùng `VIEW_RECRUITMENT`.
- `ViewCV` dùng `/viewCV` và nhận `guestId`.
- Route `/candidates` được RoleAuthorizationFilter cho cả HR Manager và HR Staff.

## Quy tắc nghiệp vụ chuẩn
- Danh sách ứng viên phải dựa trên `Application`, không chỉ `Guest`.
- CV phải là CV của application đang xét.
- Chuyển trạng thái phải validate state machine.

## Code còn lệch spec hoặc cần bổ sung
- Cần chuyển xem CV sang `applicationId`.
- Cần permission quản lý applicant riêng.
- Cần bỏ cập nhật `Guest.Status` cho workflow mới.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

