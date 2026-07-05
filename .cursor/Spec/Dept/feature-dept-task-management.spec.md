# Đặc tả module Dept: Quản lý công việc

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Dept Manager quản lý task và yêu cầu nghỉ phép trong phạm vi phòng ban.

## Route, controller và JSP liên quan
- `/dept`, `/dept/*`, `/taskManager`, `/postTask`, `/viewTask`, `/dept/leaves`.
- Controller: `DeptController`, `TaskManager`, `PostTask`, `ViewTask`, `DeptLeaveController`.
- JSP: `Views/DeptManager/*`.

## Hiện trạng code
- Dept Manager dashboard dùng `/dept?action=dashboard`.
- Task legacy vẫn dùng `/taskManager`, `/postTask`, `/viewTask`.
- Một số controller đã dùng scope phòng ban qua `DeptManagerScope`.
- `/viewTask` bị trùng với Employee legacy servlet.

## Quy tắc nghiệp vụ chuẩn
- Dept Manager chỉ thấy dữ liệu phòng ban mình quản lý.
- Tạo task phải gán cho employee thuộc phạm vi phòng ban.
- Cập nhật task không được vượt phạm vi hoặc sửa dữ liệu actor khác.

## Ma trận route, quyền và dữ liệu cần bổ sung
| Nhóm chức năng | Route/controller hiện tại | Route chuẩn nên dùng | Permission chuẩn cần có | Bảng dữ liệu chính |
|---|---|---|---|---|
| Dashboard phòng ban | `/dept`, `DeptController` | `/dept` | `VIEW_DEPARTMENT_DASHBOARD` hoặc `VIEW_DEPARTMENTS` | `Department`, `Employee`, `Task`, `LeaveRequest` |
| Danh sách task | `/taskManager`, `TaskManager` | `/dept/tasks` | `VIEW_DEPARTMENT_TASKS` | `Task`, `Employee` |
| Tạo task | `/postTask`, `PostTask` | `/dept/tasks/create` | `CREATE_DEPARTMENT_TASK` | `Task`, `Employee` |
| Chi tiết/cập nhật task | `/viewTask`, `ViewTask` | `/dept/tasks/detail` hoặc `/dept/tasks/{id}` | `UPDATE_DEPARTMENT_TASK` | `Task` |
| Duyệt nghỉ phép phòng ban | `/dept/leaves`, `DeptLeaveController` | `/dept/leaves` | `APPROVE_DEPARTMENT_LEAVE` | `LeaveRequest`, `Employee` |

## Rule scope phòng ban bắt buộc
- Mọi truy vấn danh sách task, employee và leave phải lọc theo phòng ban mà Dept Manager đang quản lý.
- Khi tạo task, assignee phải thuộc phòng ban của Dept Manager; nếu không, request trả lỗi quyền hoặc validation.
- Khi cập nhật task, task phải thuộc employee trong phòng ban của Dept Manager.
- Dept Manager không được xem đơn nghỉ hoặc task của phòng ban khác dù biết ID.
- Nếu manager chưa được gắn phòng ban, dashboard phải hiển thị trạng thái rỗng hoặc lỗi cấu hình rõ ràng.

## Workflow task và leave cần bổ sung
- Task status hợp lệ theo code hiện tại: `Waiting`, `In Progress`, `Completed`, `Rejected`.
- Dept Manager tạo task ở trạng thái khởi tạo hợp lệ, sau đó Employee cập nhật tiến độ nếu được phép.
- Dept Manager chỉ được reject/đóng task theo rule nghiệp vụ đã định; mọi thay đổi trạng thái phải có thời điểm cập nhật.
- Leave phòng ban: chỉ đơn `Pending` mới được duyệt/từ chối; từ chối nên bắt buộc lý do.

## Notification và audit bắt buộc
- Tạo task gửi notification cho Employee được giao.
- Employee cập nhật task gửi notification cho Dept Manager nếu task hoàn thành hoặc bị từ chối.
- Duyệt/từ chối leave gửi notification cho Employee.
- Tạo/cập nhật task và duyệt/từ chối leave phải ghi audit tối thiểu gồm manager, employee, entity ID, trạng thái cũ/mới và lý do nếu có.

## Checklist nghiệm thu riêng cho Dept Manager
- Dept Manager không truy cập được task/leave của phòng ban khác bằng cách sửa ID trên URL.
- Employee không truy cập được route Dept legacy hoặc route chuẩn `/dept/*`.
- Route `/viewTask` được tách khỏi Employee trước khi nghiệm thu deploy servlet.
- Tạo task cho employee ngoài phòng ban bị chặn.
- Duyệt leave chỉ áp dụng cho đơn `Pending` trong phòng ban.

## Code còn lệch spec hoặc cần bổ sung
- Tách route `/viewTask` để không trùng Employee.
- Chuẩn hóa route Dept về `/dept/tasks/*`.
- Kiểm tra toàn bộ controller task đã dùng scope phòng ban nhất quán.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.
