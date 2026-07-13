# Tính năng Dept: Cập nhật công việc

Trạng thái: Đã rà soát theo code ngày 2026-07-13.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Dept Manager xem/cập nhật chi tiết task của phòng ban.

## Route, controller và JSP liên quan
- `/dept/tasks/detail`, servlet `DeptViewTask`.
- Route legacy tham chiếu: `/viewTask`, `dept.ViewTask` (không còn là route chính).
- JSP: `Views/DeptManager/viewTask.jsp` hoặc trang detail tương ứng.

## Hiện trạng code
- `DeptViewTask` xử lý GET/POST tại `/dept/tasks/detail`.
- Kiểm tra scope phòng ban qua `DeptManagerScope` và ownership task theo manager.
- Filter yêu cầu permission `VIEW_DEPARTMENTS` cho route Dept.

## Quy tắc nghiệp vụ chuẩn
- Cập nhật phải kiểm tra task thuộc scope manager.
- Không cập nhật task đã đóng nếu nghiệp vụ không cho phép.
- Mọi thay đổi trạng thái nên có audit/notification nếu cần.

## Code còn lệch spec hoặc cần bổ sung
- Cập nhật task vẫn dùng `VIEW_DEPARTMENTS`, chưa có `UPDATE_DEPARTMENT_TASK`.
- JSP/link legacy có thể còn trỏ `/viewTask`.
- Cần audit khi Dept Manager thay đổi trạng thái task.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Sửa `taskId` ngoài phòng ban trả 403 hoặc redirect phù hợp.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.
