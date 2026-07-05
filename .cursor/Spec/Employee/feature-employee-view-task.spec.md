# Tính năng Employee: Xem và cập nhật task được giao

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Employee xem task được giao và cập nhật trạng thái cho task của mình.

## Route, controller và JSP liên quan
- `/employee`, `/employee/*`.
- Controller chính: `EmployeePortalController`.
- JSP: `Views/Employee/EmployeeHome.jsp`, `Tasks.jsp`, `Leaves.jsp`, `Payroll.jsp`, `Contract.jsp`, `Attendance.jsp`, `EmployeeProfile.jsp`.

## Hiện trạng code
- Route chuẩn là `/employee/tasks` trong `EmployeePortalController`.
- DAO dùng truy vấn task theo employee để tránh xem task người khác.
- Route legacy `/viewTask` không nên dùng cho Employee.

## Quy tắc nghiệp vụ chuẩn
- Employee chỉ thao tác task được giao cho mình.
- Status phải nằm trong enum task hiện có.
- Cập nhật task nên tạo notification cho Dept Manager nếu nghiệp vụ yêu cầu.

## Code còn lệch spec hoặc cần bổ sung
- Xóa hoặc đổi mapping servlet legacy `employee.ViewTask`.
- Cần test sửa URL taskId của employee khác.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

