# Tính năng HR Staff: Quản lý phụ cấp và khấu trừ

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- HR Staff quản lý allowance và deduction liên quan payroll.

## Route, controller và JSP liên quan
- `/hrstaff`, `/postRecruitments`, `/candidates`, `/viewCV`, `/hrstaff/interviews/schedule`.
- `/hrstaff/contracts`, `/hrstaff/contracts/create`, `/hrstaff/payroll`, `/hrstaff/payroll/*`, `/api/payroll`, `/api/allowance/*`, `/api/deduction/*`.
- Controller: `HrStaffHomeController`, `PostRecruitmentController`, `ViewCandidateController`, `InterviewScheduleController`, `ContractListController`, `CreateContractController`, `PayrollManagementController`.

## Hiện trạng code
- API `/api/allowance/*` và `/api/deduction/*` phục vụ điều chỉnh payroll.
- Có route UI `/hrstaff/payroll/allowance` và `/hrstaff/payroll/deduction`.
- Các API cần trả JSON.

## Quy tắc nghiệp vụ chuẩn
- Điều chỉnh payroll phải validate số tiền, kỳ lương và employee.
- Không cho điều chỉnh payroll đã khóa nếu nghiệp vụ cấm.
- JSON lỗi phải có thông báo tiếng Việt.

## Code còn lệch spec hoặc cần bổ sung
- Cần kiểm tra permission API allowance/deduction có thống nhất với payroll không.
- Cần test số âm, thiếu employee, payroll đã duyệt.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

