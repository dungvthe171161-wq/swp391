# Tính năng Dept: Tạo công việc

Trạng thái: Đã rà soát theo code ngày 2026-07-18.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Dept Manager giao task cho nhân viên thuộc phòng ban.

## Route, controller và JSP liên quan
- `/postTask`, controller `PostTask` (legacy).
- Route chuẩn mục tiêu: `/dept/tasks/create`.
- JSP: `Views/DeptManager/postTask.jsp`.

## Hiện trạng code
- `PostTask` xử lý `/postTask`.
- Filter yêu cầu permission `VIEW_DEPARTMENTS` dù đây là thao tác tạo.
- Task status hiện có `Waiting`, `In Progress`, `Completed`, `Rejected`.
- Sau khi tạo và gán task, `PostTask` gửi notification trong app cho employee được giao và gửi email qua `EmailTemplates.taskAssigned`.

## Quy tắc nghiệp vụ chuẩn
- Tạo task nên có permission riêng như permission tạo task riêng sau khi bổ sung seed/migration.
- Assignee phải thuộc phòng ban trong scope.
- Ngày hạn và nội dung task phải được validate.

## Code còn lệch spec hoặc cần bổ sung
- Cần tách permission tạo task khỏi quyền xem phòng ban.
- Cần chuẩn hóa route sang `/dept/tasks/create`.
- Cần audit/outbox hoặc cơ chế idempotency để tránh tạo task thành công nhưng notification/email gửi trùng hoặc thất bại âm thầm.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Tạo task cho employee ngoài phòng ban bị chặn.
- Tạo task cho employee trong phòng ban tạo notification đúng người nhận và email đúng nội dung tối thiểu.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.
