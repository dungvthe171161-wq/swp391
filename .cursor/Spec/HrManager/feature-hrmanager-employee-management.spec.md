# Tính năng HR Manager: Quản lý nhân viên

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- HR Manager xem danh sách nhân viên và tạo employee từ ứng viên đã tuyển.

## Route, controller và JSP liên quan
- `/HrHomeController`, `/viewRecruitment`, `/viewCV`, `/hr/employee-list`, `/hr/create-employee`.
- `/hr/approve-reject-contracts`, `/hr/payroll-approval`, `/hr/leaves` và các route `/hr/*`.
- Controller: `HrHomeController`, `ViewRecruitment`, `ViewCV`, `EmployeeListController`, `CreateEmployeeController`, `ApproveRejectContractController`, `PayrollApprovalController`.

## Hiện trạng code
- `EmployeeListController` dùng `/hr/employee-list` và `VIEW_EMPLOYEES`.
- `CreateEmployeeController` dùng `/hr/create-employee` và `VIEW_EMPLOYEES`.
- Create employee hiện insert employee, tạo/promote user, rồi xóa Guest.

## Quy tắc nghiệp vụ chuẩn
- Tạo employee nên yêu cầu `CREATE_EMPLOYEE`.
- Không xóa Guest nếu cần giữ lịch sử tuyển dụng.
- Phải chạy transaction để tránh employee/user/application cập nhật dở dang.

## Code còn lệch spec hoặc cần bổ sung
- Sửa permission create employee.
- Giữ Guest và liên kết application hired employee nếu có migration.
- Hash mật khẩu user mới.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

