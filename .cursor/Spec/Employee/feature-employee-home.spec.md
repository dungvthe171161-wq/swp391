# Tính năng Employee: Trang tổng quan

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Employee xem tổng quan task, nghỉ phép, payroll, contract và thông báo.

## Route, controller và JSP liên quan
- `/employee`, `/employee/*`.
- Controller chính: `EmployeePortalController`.
- JSP: `Views/Employee/EmployeeHome.jsp`, `Tasks.jsp`, `Leaves.jsp`, `Payroll.jsp`, `Contract.jsp`, `Attendance.jsp`, `EmployeeProfile.jsp`.

## Hiện trạng code
- `/employee` redirect hoặc hiển thị dashboard trong `EmployeePortalController`.
- Dữ liệu dashboard lấy theo `systemUser.EmployeeID`.
- Topbar Employee có thể hiển thị notification.

## Quy tắc nghiệp vụ chuẩn
- Chỉ hiển thị số liệu của employee hiện tại.
- Không link trực tiếp JSP nếu controller cần nạp dữ liệu.
- UI dùng tiếng Việt có dấu.

## Code còn lệch spec hoặc cần bổ sung
- Cần test Employee không có `EmployeeID`.
- Cần kiểm tra dữ liệu dashboard khi không có task/payroll/contract.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

