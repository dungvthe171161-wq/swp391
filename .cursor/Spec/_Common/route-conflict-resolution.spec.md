# Đặc tả dùng chung: Xử lý xung đột route

Trạng thái: Đã rà soát theo code ngày 2026-07-16.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Dept Manager và Employee cùng có chức năng xem task; spec này mô tả cách tách route theo actor.

## Route, controller và JSP liên quan
- Dept chi tiết task: servlet `ViewTask` trong package `com.hrm.controller.dept`, route `/viewTask`.
- Employee chi tiết task: servlet name `EmployeeViewTask`, class `com.hrm.controller.employee.ViewTask`, route `/employee/view-task`.
- Danh sách/tạo task Dept: `TaskManager` (`/taskManager`), `PostTask` (`/postTask`).
- Employee danh sách task: `EmployeePortalController` (`/employee/tasks`).
- `ModulePermissionFilter`: bảo vệ `/taskManager`, `/postTask`, `/viewTask`, `/employee` và `/employee/*`.

## Hiện trạng code
- Không còn trùng mapping giữa Dept và Employee: Dept dùng `/viewTask`, Employee dùng `/employee/view-task`.
- `EmployeeViewTask` kiểm tra ownership theo `systemUser.EmployeeID`.
- Dept `ViewTask` dùng `DeptManagerScope`; route vẫn nằm ngoài `/dept/*`.
- `ModulePermissionFilter` vẫn có rule riêng cho `/viewTask`, `/taskManager` và `/postTask`.

## Quy tắc nghiệp vụ chuẩn
- Route Dept phải nằm dưới `/dept/*`, ví dụ `/dept/tasks`, `/dept/tasks/create`, `/dept/tasks/detail`.
- Route Employee phải nằm dưới `/employee/*`, ví dụ `/employee/tasks` và `/employee/tasks/detail`.
- Không dùng chung servlet name cho hai controller khác actor.

## Code còn lệch spec hoặc cần bổ sung
- `/taskManager`, `/postTask` và `/viewTask` vẫn là route legacy; chưa chuẩn hóa hết về `/dept/tasks/*`.
- Employee detail hiện là `/employee/view-task`, chưa chuẩn hóa thành `/employee/tasks/detail`.
- Cần cập nhật JSP link khi đổi sang route chuẩn.
- Cần kiểm tra lại permission vì create/update task vẫn dùng chung `VIEW_DEPARTMENTS`.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.
