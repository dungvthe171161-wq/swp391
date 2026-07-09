# Tính năng HR Staff: Quản lý bảng lương

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- HR Staff tạo, tính, submit và quản lý payroll.

## Route, controller và JSP liên quan
- `/hrstaff`, `/postRecruitments`, `/candidates`, `/viewCV`, `/hrstaff/interviews/schedule`.
- `/hrstaff/contracts`, `/hrstaff/contracts/create`, `/hrstaff/payroll`, `/hrstaff/payroll/*`, `/api/payroll`, `/api/allowance/*`, `/api/deduction/*`.
- Controller: `HrStaffHomeController`, `PostRecruitmentController`, `ViewCandidateController`, `InterviewScheduleController`, `ContractListController`, `CreateContractController`, `PayrollManagementController`.

## Hiện trạng code
- `PayrollManagementController` mapping nhiều route `/hrstaff/payroll*` và `/api/payroll`.
- Permission hiện dùng `VIEW_PAYROLLS` cho nhiều thao tác.
- Có controller tính payroll và API allowance/deduction.

## Quy tắc nghiệp vụ chuẩn
- Tạo/sửa/xóa/submit payroll cần permission ghi dữ liệu riêng nếu phân quyền chi tiết.
- Payroll phải tính theo employee, tháng/kỳ lương và trạng thái hợp lệ.
- Không cho sửa payroll đã duyệt/trả nếu nghiệp vụ khóa.

## Code còn lệch spec hoặc cần bổ sung
- Cần tách quyền xem payroll và quản lý payroll.
- Cần test batch submit/delete/generate theo quyền.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

