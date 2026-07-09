# Tính năng Employee: Chấm công

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Employee xem dữ liệu chấm công của chính mình.

## Route, controller và JSP liên quan
- `/employee`, `/employee/*`.
- Controller chính: `EmployeePortalController`.
- JSP: `Views/Employee/EmployeeHome.jsp`, `Tasks.jsp`, `Leaves.jsp`, `Payroll.jsp`, `Contract.jsp`, `Attendance.jsp`, `EmployeeProfile.jsp`.

## Hiện trạng code
- `/employee/attendance` hiển thị JSP `Attendance.jsp`.
- Dữ liệu attendance phụ thuộc bảng chấm công hiện có.
- Route nằm dưới `/employee/*` nên được filter theo Employee/Admin.

## Quy tắc nghiệp vụ chuẩn
- Employee chỉ xem attendance của mình.
- Không cho sửa công nếu chưa có quy trình duyệt.
- Bộ lọc tháng/năm phải validate.

## Code còn lệch spec hoặc cần bổ sung
- Cần bổ sung test khi không có dữ liệu attendance.
- Cần làm rõ actor nào được sửa/chốt công.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

