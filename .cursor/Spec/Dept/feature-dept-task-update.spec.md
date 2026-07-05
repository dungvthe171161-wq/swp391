# Tính năng Dept: Cập nhật công việc

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Dept Manager xem/cập nhật chi tiết task của phòng ban.

## Route, controller và JSP liên quan
- `/dept`, `/dept/*`, `/taskManager`, `/postTask`, `/viewTask`, `/dept/leaves`.
- Controller: `DeptController`, `TaskManager`, `PostTask`, `ViewTask`, `DeptLeaveController`.
- JSP: `Views/DeptManager/*`.

## Hiện trạng code
- `ViewTask` Dept đang dùng `/viewTask`.
- Route này bị Employee servlet dùng trùng.
- Filter đang coi `/viewTask` là route Dept với `VIEW_DEPARTMENTS`.

## Quy tắc nghiệp vụ chuẩn
- Cập nhật phải kiểm tra task thuộc scope manager.
- Không cập nhật task đã đóng nếu nghiệp vụ không cho phép.
- Mọi thay đổi trạng thái nên có audit/notification nếu cần.

## Code còn lệch spec hoặc cần bổ sung
- Cần sửa trùng servlet name/mapping.
- Cần route riêng `/dept/tasks/detail` hoặc tương đương.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

