# Thiết kế cổng Trưởng phòng

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu.
Phạm vi: Dept Manager quản lý dashboard, task và nghỉ phép trong phòng ban.

## Hiện trạng code
- Dashboard Dept Manager dùng `/dept?action=dashboard`.
- Task legacy dùng `/taskManager`, `/postTask`, `/viewTask`.
- `DeptLeaveController` dùng `/dept/leaves`.
- `ModulePermissionFilter` yêu cầu `VIEW_DEPARTMENTS` cho `/dept`, `/taskManager`, `/postTask`, `/viewTask`.
- `RoleAuthorizationFilter` cho Admin và Dept Manager truy cập các route Dept.
- `Dept/ViewTask` và `Employee/ViewTask` đang trùng servlet name `ViewTask` và mapping `/viewTask`.

## Thiết kế chuẩn
- Route Dept nên nằm dưới `/dept/*`: `/dept/tasks`, `/dept/tasks/create`, `/dept/tasks/detail`, `/dept/leaves`.
- Mọi dữ liệu task/leave phải scope theo phòng ban mà manager được quản lý.
- Tạo task nên có permission ghi dữ liệu riêng, không dùng chung `VIEW_DEPARTMENTS`.
- Chi tiết task của Dept và task của Employee phải dùng route khác nhau.
- UI Dept Manager phải dùng tiếng Việt có dấu và link qua controller.

## Code còn lệch thiết kế
- `/viewTask` bị trùng với Employee, cần sửa trước khi coi module task ổn định.
- Route legacy chưa thống nhất dưới `/dept/*`.
- Permission task đang dùng quyền xem phòng ban, chưa tách quyền tạo/sửa task.

## Kiểm thử tối thiểu
- Dept Manager vào được dashboard và chỉ thấy dữ liệu phòng ban của mình.
- Dept Manager không tạo được task cho employee ngoài phòng ban.
- Employee không vào được route Dept.
- Deploy servlet không còn lỗi trùng mapping `/viewTask` sau khi sửa code.