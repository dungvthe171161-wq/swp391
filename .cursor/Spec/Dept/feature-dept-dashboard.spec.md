# Tính năng Dept: Bảng điều khiển phòng ban

Trạng thái: Đã rà soát theo code ngày 2026-07-16.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Dept Manager xem tổng quan phòng ban.

## Route, controller và JSP liên quan
- `/dept`, `/dept/*`, `/taskManager`, `/postTask`, `/viewTask`, `/dept/leaves`.
- Controller: `DeptController`, `TaskManager`, `PostTask`, `ViewTask`, `DeptLeaveController`.
- JSP: `Views/DeptManager/*`.

## Hiện trạng code
- `DeptController` xử lý `/dept`.
- Dashboard phải nạp số liệu theo phòng ban của manager.
- Route được filter theo role Dept/Admin và permission `VIEW_DEPARTMENTS`.

## Quy tắc nghiệp vụ chuẩn
- Không hiển thị dữ liệu phòng ban khác.
- Link thao tác task/leave phải dùng controller.
- Thông báo UI phải là tiếng Việt có dấu.

## Code còn lệch spec hoặc cần bổ sung
- Cần kiểm tra số liệu dashboard đã scope đúng phòng ban chưa.
- Cần chuẩn hóa route legacy trên dashboard.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

