# Tính năng HR Staff: Bảng điều khiển

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- HR Staff xem tổng quan công việc vận hành.

## Route, controller và JSP liên quan
- `/hrstaff`, `/postRecruitments`, `/candidates`, `/viewCV`, `/hrstaff/interviews/schedule`.
- `/hrstaff/contracts`, `/hrstaff/contracts/create`, `/hrstaff/payroll`, `/hrstaff/payroll/*`, `/api/payroll`, `/api/allowance/*`, `/api/deduction/*`.
- Controller: `HrStaffHomeController`, `PostRecruitmentController`, `ViewCandidateController`, `InterviewScheduleController`, `ContractListController`, `CreateContractController`, `PayrollManagementController`.

## Hiện trạng code
- `HrStaffHomeController` mapping `/hrstaff`.
- Topbar HR Staff có thể nạp notification qua `HrStaffNotificationFilter`.
- Dashboard cần link qua controller, không link thẳng JSP nếu cần data.

## Quy tắc nghiệp vụ chuẩn
- Chỉ HR Staff/Admin được vào dashboard.
- Số liệu phải lấy từ DAO thật hoặc ghi rõ nếu là placeholder.
- UI dùng tiếng Việt có dấu.

## Code còn lệch spec hoặc cần bổ sung
- Cần kiểm tra các card dashboard đã phản ánh dữ liệu thật.
- Cần test route với HR Manager và Employee.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

