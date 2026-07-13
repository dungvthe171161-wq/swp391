# Tính năng HR Manager: Phê duyệt hợp đồng

Trạng thái: Đã rà soát theo code ngày 2026-07-13.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- HR Manager duyệt hoặc từ chối hợp đồng do HR Staff tạo.

## Route, controller và JSP liên quan
- `/HrHomeController`, `/viewRecruitment`, `/viewCV`, `/hr/employee-list`, `/hr/create-employee`.
- `/hr/approve-reject-contracts`, `/hr/payroll-approval`, `/hr/leaves` và các route `/hr/*`.
- Controller: `HrHomeController`, `ViewRecruitment`, `ViewCV`, `EmployeeListController`, `CreateEmployeeController`, `ApproveRejectContractController`, `PayrollApprovalController`.

## Hiện trạng code
- `ApproveRejectContractController` mapping `/hr/approve-reject-contracts`.
- Permission hiện dùng `VIEW_CONTRACTS`.
- Contract enum có `Pending_Approval`, `Approved`, `Rejected`, `Active`, `Expired`.

## Quy tắc nghiệp vụ chuẩn
- Duyệt hợp đồng cần permission phê duyệt riêng nếu phân quyền chi tiết.
- Reject phải có lý do nếu nghiệp vụ yêu cầu.
- Thay đổi trạng thái phải audit và notification cho HR Staff/Employee nếu cần.

## Code còn lệch spec hoặc cần bổ sung
- Tách `APPROVE_CONTRACT` khỏi quyền xem.
- Cần test trạng thái hợp đồng không thể duyệt.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

