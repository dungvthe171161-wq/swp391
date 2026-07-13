# Tính năng HR Manager: Quản lý nhân viên

Trạng thái: Đã rà soát theo code ngày 2026-07-13.
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
- Create employee insert employee, tạo/promote user, rồi set `Guest.Status = Converted` (không xóa `Guest`).
- Mật khẩu user mới có thể là temp password plain text.

## Quy tắc nghiệp vụ chuẩn
- Tạo employee nên yêu cầu `CREATE_EMPLOYEE`.
- Không xóa Guest; giữ lịch sử tuyển dụng và liên kết application.
- Phải chạy transaction để tránh employee/user/application cập nhật dở dang.

## Code còn lệch spec hoặc cần bổ sung
- Create employee vẫn dùng `VIEW_EMPLOYEES`, chưa dùng `CREATE_EMPLOYEE`.
- Temp password plain text; chưa hash mật khẩu user mới.
- Thiếu transaction/audit đầy đủ khi tạo employee.
- Cần liên kết application hired với employee mới nếu có migration.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Chỉ tạo employee khi `Application = Hired` và `Offer = Accepted`.
- Sau tạo employee, `Guest.Status = Converted` và lịch sử application còn nguyên.
