# Tính năng HR Manager: Trang tổng quan

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- HR Manager xem tổng quan tuyển dụng, nhân sự, hợp đồng và payroll.

## Route, controller và JSP liên quan
- `/HrHomeController`, `/viewRecruitment`, `/viewCV`, `/hr/employee-list`, `/hr/create-employee`.
- `/hr/approve-reject-contracts`, `/hr/payroll-approval`, `/hr/leaves` và các route `/hr/*`.
- Controller: `HrHomeController`, `ViewRecruitment`, `ViewCV`, `EmployeeListController`, `CreateEmployeeController`, `ApproveRejectContractController`, `PayrollApprovalController`.

## Hiện trạng code
- `HrHomeController` mapping `/HrHomeController`.
- Route `/hr/` được RoleAuthorizationFilter cho Admin và HR Manager.
- Dashboard phải nạp dữ liệu qua controller.

## Quy tắc nghiệp vụ chuẩn
- Không link thẳng JSP nếu cần dữ liệu dashboard.
- Số liệu phải đúng quyền HR Manager.
- UI phải tiếng Việt có dấu.

## Code còn lệch spec hoặc cần bổ sung
- Cần kiểm tra card dashboard có dữ liệu thật.
- Cần test HR Staff không vào được route HR Manager nếu không được phép.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

