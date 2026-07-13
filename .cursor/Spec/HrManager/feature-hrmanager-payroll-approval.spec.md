# Tính năng HR Manager: Phê duyệt bảng lương

Trạng thái: Đã rà soát theo code ngày 2026-07-13.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- HR Manager xem, duyệt, từ chối hoặc xử lý batch payroll.

## Route, controller và JSP liên quan
- `/HrHomeController`, `/viewRecruitment`, `/viewCV`, `/hr/employee-list`, `/hr/create-employee`.
- `/hr/approve-reject-contracts`, `/hr/payroll-approval`, `/hr/leaves` và các route `/hr/*`.
- Controller: `HrHomeController`, `ViewRecruitment`, `ViewCV`, `EmployeeListController`, `CreateEmployeeController`, `ApproveRejectContractController`, `PayrollApprovalController`.

## Hiện trạng code
- `PayrollApprovalController` mapping các route phê duyệt payroll.
- Permission hiện dùng `VIEW_USERS` cho cả xem chi tiết AJAX.
- Payroll enum có `Draft`, `Pending`, `Approved`, `Rejected`, `Paid`.

## Quy tắc nghiệp vụ chuẩn
- Duyệt payroll phải dùng quyền riêng như `APPROVE_PAYROLL`.
- Chỉ payroll ở trạng thái phù hợp mới được duyệt hoặc từ chối.
- Batch action phải kiểm tra quyền và trạng thái từng bản ghi.

## Code còn lệch spec hoặc cần bổ sung
- Đổi permission từ `VIEW_USERS` sang quyền payroll đúng.
- Cần seed permission và role-permission mới.
- Cần test AJAX details với user thiếu quyền.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

