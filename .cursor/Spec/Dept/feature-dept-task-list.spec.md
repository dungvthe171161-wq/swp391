# Tính năng Dept: Danh sách công việc

Trạng thái: Đã rà soát theo code ngày 2026-07-16.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Dept Manager xem danh sách task của phòng ban.

## Route, controller và JSP liên quan
- `/dept`, `/dept/*`, `/taskManager`, `/viewTask`, `/dept/leaves`.
- Controller: `DeptController`, `TaskManager`, `PostTask`, `ViewTask`, `DeptLeaveController`.
- JSP: `Views/DeptManager/*`.

## Hiện trạng code
- Danh sách task legacy đi qua `/taskManager`.
- Chi tiết task dùng `/viewTask` qua `ViewTask`.
- Permission hiện dùng `VIEW_DEPARTMENTS`.
- Dữ liệu phải gắn với manager/department qua `DeptManagerScope`.

## Quy tắc nghiệp vụ chuẩn
- Chỉ hiển thị task thuộc phòng ban được quản lý.
- Bộ lọc trạng thái phải dùng enum task hiện có.
- Không cho xem task ngoài scope bằng cách sửa URL.

## Code còn lệch spec hoặc cần bổ sung
- Cần chuẩn hóa danh sách/chi tiết task sang `/dept/tasks` và `/dept/tasks/detail` nếu đổi route code.
- Cần test manager của phòng ban khác.
- Cần permission riêng permission xem task riêng sau khi bổ sung seed/migration.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.
