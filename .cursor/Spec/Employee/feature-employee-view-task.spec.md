# Tính năng Employee: Xem và cập nhật task được giao

Trạng thái: Đã rà soát theo code ngày 2026-07-13.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Employee xem task được giao và cập nhật trạng thái cho task của mình.

## Route, controller và JSP liên quan
- `/employee`, `/employee/tasks`, `/employee/tasks/detail`.
- Controller: `EmployeePortalController`, `EmployeeViewTask`.
- JSP: `Views/Employee/Tasks.jsp`, `Views/Employee/ViewTask.jsp`.

## Hiện trạng code
- Danh sách task dùng `/employee/tasks` trong `EmployeePortalController`.
- Chi tiết/cập nhật task dùng `/employee/tasks/detail` qua servlet `EmployeeViewTask`.
- `EmployeeViewTask` kiểm tra ownership theo `systemUser.EmployeeID`.
- Servlet legacy `employee.ViewTask` mapping `/viewTask` có thể còn nhưng không phải route chính.

## Quy tắc nghiệp vụ chuẩn
- Employee chỉ thao tác task được giao cho mình.
- Status phải nằm trong enum task hiện có.
- Cập nhật task nên tạo notification cho Dept Manager nếu nghiệp vụ yêu cầu.

## Code còn lệch spec hoặc cần bổ sung
- Cần test sửa URL `taskId` của employee khác trên `/employee/tasks/detail`.
- Cần loại bỏ hoàn toàn link/JSP còn trỏ route legacy `/viewTask` nếu còn.
- Cần đảm bảo notification khi employee hoàn thành/từ chối task.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Employee A không xem/cập nhật task của Employee B.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.
