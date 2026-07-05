# Tính năng HR Staff: Quản lý hợp đồng

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- HR Staff tạo và quản lý hợp đồng trước khi HR Manager phê duyệt.

## Route, controller và JSP liên quan
- `/hrstaff`, `/postRecruitments`, `/candidates`, `/viewCV`, `/hrstaff/interviews/schedule`.
- `/hrstaff/contracts`, `/hrstaff/contracts/create`, `/hrstaff/payroll`, `/hrstaff/payroll/*`, `/api/payroll`, `/api/allowance/*`, `/api/deduction/*`.
- Controller: `HrStaffHomeController`, `PostRecruitmentController`, `ViewCandidateController`, `InterviewScheduleController`, `ContractListController`, `CreateContractController`, `PayrollManagementController`.

## Hiện trạng code
- `ContractListController` và `CreateContractController` xử lý route `/hrstaff/contracts`.
- Contract status trong DB gồm `Draft`, `Pending_Approval`, `Approved`, `Rejected`, `Active`, `Expired`.
- HR Manager có controller duyệt riêng.

## Quy tắc nghiệp vụ chuẩn
- HR Staff tạo draft/pending approval, không tự duyệt nếu nghiệp vụ yêu cầu HR Manager.
- Hợp đồng phải gắn đúng employee và validate ngày, lương, loại hợp đồng.
- Thay đổi hợp đồng cần audit/notification nếu quan trọng.

## Code còn lệch spec hoặc cần bổ sung
- Cần kiểm tra permission tạo/sửa contract đã tách khỏi quyền xem chưa.
- Cần test trạng thái hợp đồng không hợp lệ.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

