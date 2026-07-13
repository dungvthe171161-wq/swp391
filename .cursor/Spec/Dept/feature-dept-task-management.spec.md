# Đặc tả module Dept: Quản lý công việc

Trạng thái: Đã rà soát theo code ngày 2026-07-13.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Dept Manager quản lý task và yêu cầu nghỉ phép trong phạm vi phòng ban.

## Route, controller và JSP liên quan
- `/dept`, `/dept/*`, `/dept/tasks/detail`, `/dept/leaves`.
- Route legacy: `/taskManager`, `/postTask`, `/viewTask`.
- Controller: `DeptController`, `TaskManager`, `PostTask`, `DeptViewTask`, `DeptLeaveController`.
- JSP: `Views/DeptManager/*`.

## Hiện trạng code
- Dept Manager dashboard dùng `/dept?action=dashboard`.
- Chi tiết/cập nhật task dùng `/dept/tasks/detail` qua servlet `DeptViewTask`.
- Danh sách/tạo task legacy vẫn dùng `/taskManager`, `/postTask`.
- Một số controller đã dùng scope phòng ban qua `DeptManagerScope`.
- Xung đột route `/viewTask` với Employee đã được xử lý bằng route riêng theo actor.

## Quy tắc nghiệp vụ chuẩn
- Dept Manager chỉ thấy dữ liệu phòng ban mình quản lý.
- Tạo task phải gán cho employee thuộc phạm vi phòng ban.
- Cập nhật task không được vượt phạm vi hoặc sửa dữ liệu actor khác.

## Ma trận route, quyền và dữ liệu
| Nhóm chức năng | Route/controller hiện tại | Route chuẩn nên dùng | Permission chuẩn cần có | Bảng dữ liệu chính |
|---|---|---|---|---|
| Dashboard phòng ban | `/dept`, `DeptController` | `/dept` | `VIEW_DEPARTMENT_DASHBOARD` hoặc `VIEW_DEPARTMENTS` | `Department`, `Employee`, `Task`, `LeaveRequest` |
| Danh sách task | `/taskManager`, `TaskManager` | `/dept/tasks` | `VIEW_DEPARTMENT_TASKS` | `Task`, `Employee` |
| Tạo task | `/postTask`, `PostTask` | `/dept/tasks/create` | `CREATE_DEPARTMENT_TASK` | `Task`, `Employee` |
| Chi tiết/cập nhật task | `/dept/tasks/detail`, `DeptViewTask` | `/dept/tasks/detail` | `UPDATE_DEPARTMENT_TASK` | `Task` |
| Duyệt nghỉ phép phòng ban | `/dept/leaves`, `DeptLeaveController` | `/dept/leaves` | `APPROVE_DEPARTMENT_LEAVE` | `LeaveRequest`, `Employee` |

## Code còn lệch spec hoặc cần bổ sung
- `/taskManager` và `/postTask` vẫn là route legacy; chưa chuẩn hóa hết về `/dept/tasks/*`.
- Create/update task vẫn dùng `VIEW_DEPARTMENTS`, chưa có permission riêng.
- Kiểm tra toàn bộ controller task đã dùng scope phòng ban nhất quán.
- Cần audit/notification đầy đủ cho tạo/cập nhật task và duyệt leave.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Dept Manager không truy cập task/leave phòng ban khác bằng cách sửa ID trên URL.
- Employee không truy cập được route `/dept/*` và `/dept/tasks/detail` của phòng ban khác.
