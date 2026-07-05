# Tính năng Employee: Quản lý đơn nghỉ phép

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Employee tạo và xem đơn nghỉ phép của mình.

## Route, controller và JSP liên quan
- `/employee`, `/employee/*`.
- Controller chính: `EmployeePortalController`.
- JSP: `Views/Employee/EmployeeHome.jsp`, `Tasks.jsp`, `Leaves.jsp`, `Payroll.jsp`, `Contract.jsp`, `Attendance.jsp`, `EmployeeProfile.jsp`.

## Hiện trạng code
- `EmployeePortalController` xử lý `/employee/leaves`.
- Code validate ngày và kiểm tra overlap.
- Có luồng gửi notification cho Dept Manager.

## Quy tắc nghiệp vụ chuẩn
- Ngày kết thúc không được trước ngày bắt đầu.
- Nghỉ có lương phải kiểm tra số buổi còn lại.
- Employee chỉ xem đơn của mình.

## Code còn lệch spec hoặc cần bổ sung
- Cần test đơn overlap, hết phép, ngày quá khứ nếu nghiệp vụ cấm.
- Cần test notification gửi đúng người duyệt.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

