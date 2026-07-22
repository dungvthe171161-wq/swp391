# Đặc tả module Dept: Quản lý công việc

Trạng thái: Đã rà soát theo code ngày 2026-07-18.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Dept Manager quản lý task và yêu cầu nghỉ phép trong phạm vi phòng ban.

## Route, controller và JSP liên quan
- `/dept`, `/dept/*`, `/taskManager`, `/postTask`, `/viewTask`, `/dept/leaves`.
- Route chuẩn đề xuất nếu refactor: `/dept/tasks`, `/dept/tasks/create`, `/dept/tasks/detail`.
- Controller: `DeptController`, `TaskManager`, `PostTask`, `ViewTask`, `DeptLeaveController`.
- JSP: `Views/DeptManager/*`.

## Hiện trạng code
- Dept Manager dashboard dùng `/dept?action=dashboard`.
- Chi tiết/cập nhật task dùng `/viewTask` qua servlet `ViewTask`.
- Danh sách/tạo task vẫn dùng `/taskManager`, `/postTask`.
- Một số controller đã dùng scope phòng ban qua `DeptManagerScope`.
- Dept và Employee không dùng cùng mapping: Dept dùng `/viewTask`, Employee dùng `/employee/view-task`.
- Tạo task qua `PostTask` đã gửi notification trong app và email cho employee được giao.
- Employee cập nhật trạng thái task qua `/employee/tasks` đã gửi notification cho Dept Manager.
- Duyệt leave qua `DeptLeaveController` đã gửi notification cho employee.

## Quy tắc nghiệp vụ chuẩn
- Dept Manager chỉ thấy dữ liệu phòng ban mình quản lý.
- Tạo task phải gán cho employee thuộc phạm vi phòng ban.
- Cập nhật task không được vượt phạm vi hoặc sửa dữ liệu actor khác.

## Ma trận route, quyền và dữ liệu
| Nhóm chức năng | Route/controller hiện tại | Route chuẩn nên dùng | Permission chuẩn cần có | Bảng dữ liệu chính |
|---|---|---|---|---|
| Dashboard phòng ban | `/dept`, `DeptController` | `/dept` | `VIEW_DEPARTMENTS` hiện tại; dashboard riêng nếu bổ sung seed/migration | `Department`, `Employee`, `Task`, `LeaveRequest` |
| Danh sách task | `/taskManager`, `TaskManager` | `/dept/tasks` | permission xem task riêng sau khi bổ sung seed/migration | `Task`, `Employee` |
| Tạo task | `/postTask`, `PostTask` | `/dept/tasks/create` | permission tạo task riêng sau khi bổ sung seed/migration | `Task`, `Employee` |
| Chi tiết/cập nhật task | `/viewTask`, `ViewTask` | `/dept/tasks/detail` | permission cập nhật task riêng sau khi bổ sung seed/migration | `Task` |
| Duyệt nghỉ phép phòng ban | `/dept/leaves`, `DeptLeaveController` | `/dept/leaves` | permission duyệt leave phòng ban riêng sau khi bổ sung seed/migration | `LeaveRequest`, `Employee` |

## Code còn lệch spec hoặc cần bổ sung
- `/taskManager`, `/postTask` và `/viewTask` vẫn là route legacy; chưa chuẩn hóa hết về `/dept/tasks/*`.
- Create/update task vẫn dùng `VIEW_DEPARTMENTS`, chưa có permission riêng.
- Kiểm tra toàn bộ controller task đã dùng scope phòng ban nhất quán.
- Cần audit đầy đủ cho tạo/cập nhật task và duyệt leave.
- Cần bổ sung hoặc chốt rule notification cho Dept Manager cập nhật/reassign task trong `ViewTask`; notification tạo task, employee cập nhật task và leave decision đã có.
- Cần đưa email nhắc deadline task từ `TaskDeadlineReminderListener` vào test/monitoring nếu giữ luồng reminder.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Dept Manager không truy cập task/leave phòng ban khác bằng cách sửa ID trên URL.
- Employee không truy cập được route `/dept/*` và `/viewTask` của phòng ban khác.
