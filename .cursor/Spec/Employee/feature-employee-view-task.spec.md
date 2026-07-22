# Tính năng Employee: Xem và cập nhật task được giao

Trạng thái: Đã rà soát theo code ngày 2026-07-18.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Employee xem task được giao và cập nhật trạng thái cho task của mình.

## Route, controller và JSP liên quan
- `/employee`, `/employee/tasks`, `/employee/view-task`.
- Controller: `EmployeePortalController`, `EmployeeViewTask`.
- JSP: `Views/Employee/Tasks.jsp`, `Views/Employee/ViewTask.jsp`.

## Hiện trạng code
- Danh sách task dùng `/employee/tasks` trong `EmployeePortalController`.
- Danh sách và cập nhật trạng thái nhanh dùng `/employee/tasks` trong `EmployeePortalController`.
- Chi tiết task dùng `/employee/view-task` qua servlet `EmployeeViewTask`.
- `EmployeeViewTask` kiểm tra ownership theo `systemUser.EmployeeID`.
- Servlet `EmployeeViewTask` mapping `/employee/view-task` và kiểm tra ownership theo `systemUser.EmployeeID`.
- Khi employee cập nhật trạng thái task thành công qua `/employee/tasks`, `EmployeePortalController` gửi notification cho Dept Manager bằng `notifyTaskStatusUpdatedForManagers`.

## Quy tắc nghiệp vụ chuẩn
- Employee chỉ thao tác task được giao cho mình.
- Status phải nằm trong enum task hiện có.
- Cập nhật task phải tạo notification cho Dept Manager đúng phòng ban nếu nghiệp vụ giữ luồng hiện tại.

## Code còn lệch spec hoặc cần bổ sung
- Cần test sửa URL `taskId` của employee khác trên `/employee/tasks` và `/employee/view-task`.
- Cần chuẩn hóa route detail sang `/employee/tasks/detail` nếu đổi route code.
- Cần test notification khi employee hoàn thành/từ chối/cập nhật task, bao gồm trường hợp manager không còn active user.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Employee A không xem/cập nhật task của Employee B.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.
