# Đặc tả dùng chung: Xử lý xung đột route

Trạng thái: Đã rà soát theo code ngày 2026-07-13.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Dept Manager và Employee cùng có chức năng xem task; spec này mô tả cách tách route theo actor.

## Route, controller và JSP liên quan
- Dept chi tiết task: servlet `DeptViewTask`, route `/dept/tasks/detail`.
- Employee chi tiết task: servlet `EmployeeViewTask`, route `/employee/tasks/detail`.
- Danh sách/tạo task Dept legacy: `TaskManager` (`/taskManager`), `PostTask` (`/postTask`).
- Employee danh sách task: `EmployeePortalController` (`/employee/tasks`).
- Servlet legacy `dept.ViewTask` và `employee.ViewTask` có thể còn mapping `/viewTask` nhưng không phải route chính.

## Hiện trạng code
- Xung đột servlet name/mapping `/viewTask` giữa Dept và Employee đã được xử lý bằng route riêng theo actor.
- `EmployeeViewTask` kiểm tra ownership theo `systemUser.EmployeeID`.
- `DeptViewTask` kiểm tra scope phòng ban qua `DeptManagerScope`.
- `ModulePermissionFilter` vẫn có rule legacy cho `/viewTask`, `/taskManager`, `/postTask`.

## Quy tắc nghiệp vụ chuẩn
- Route Dept phải nằm dưới `/dept/*`, ví dụ `/dept/tasks`, `/dept/tasks/create`, `/dept/tasks/detail`.
- Route Employee phải nằm dưới `/employee/*`, ví dụ `/employee/tasks` và `/employee/tasks/detail`.
- Không dùng chung servlet name cho hai controller khác actor.

## Code còn lệch spec hoặc cần bổ sung
- `/taskManager` và `/postTask` vẫn là route legacy; chưa chuẩn hóa hết về `/dept/tasks/*`.
- Cần cập nhật JSP link còn trỏ tới route legacy nếu có.
- Cần kiểm tra lại permission vì create/update task vẫn dùng chung `VIEW_DEPARTMENTS`.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.
