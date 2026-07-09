# Tính năng Employee: Xem lương và hợp đồng

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Employee xem bảng lương và hợp đồng của chính mình.

## Route, controller và JSP liên quan
- `/employee`, `/employee/*`.
- Controller chính: `EmployeePortalController`.
- JSP: `Views/Employee/EmployeeHome.jsp`, `Tasks.jsp`, `Leaves.jsp`, `Payroll.jsp`, `Contract.jsp`, `Attendance.jsp`, `EmployeeProfile.jsp`.

## Hiện trạng code
- `/employee/payroll` và `/employee/contract` do `EmployeePortalController` xử lý.
- JSP tương ứng là `Payroll.jsp` và `Contract.jsp`.
- Dữ liệu phải lấy theo employee hiện tại.

## Quy tắc nghiệp vụ chuẩn
- Employee không được xem payroll/contract của người khác.
- Chỉ hiển thị bản ghi phù hợp trạng thái công bố hoặc được phép xem.
- Không cho sửa payroll/contract từ cổng Employee.

## Code còn lệch spec hoặc cần bổ sung
- Cần test employee chưa có payroll hoặc contract.
- Cần kiểm tra trạng thái nào được phép hiển thị.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

