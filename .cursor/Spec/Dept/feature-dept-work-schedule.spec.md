# Tính năng Dept: Quản lý lịch làm việc

Trạng thái: Đã rà soát theo code ngày 2026-07-14.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Dept Manager xem và phân lịch làm việc cho nhân viên thuộc phòng ban mình quản lý.

## Route, controller và JSP liên quan
- `GET /dept/schedules`, `POST /dept/schedules`.
- Controller: `DeptWorkScheduleController`; DAO: `WorkScheduleDAO`, `EmployeeDAO`.
- Scope: `DeptManagerScope`; JSP: `Views/DeptManager/schedules.jsp`.
- Permission hiện dùng: `VIEW_WORK_SCHEDULE`, `MANAGE_WORK_SCHEDULE`.

## Hiện trạng code
- GET đọc lịch theo tháng/năm, danh sách nhân viên trong phòng ban và các mẫu lịch đang hoạt động.
- POST hỗ trợ `action=assign`, `action=update`, `action=delete`.
- Assign kiểm tra employee thuộc phòng ban; update/delete dùng DAO có scope theo `DepartmentID`.
- Lỗi parse hoặc lỗi thao tác hiện được gom thành thông báo thất bại chung.

## Quy tắc nghiệp vụ chuẩn
- Chỉ phân lịch cho nhân viên thuộc phòng ban và chỉ dùng mẫu lịch đang hoạt động.
- Một nhân viên không được có các phân công xung đột trong cùng ngày.
- Không sửa/xóa tùy ý lịch đã phát sinh chấm công hoặc đã khóa kỳ; nếu cho phép phải có luồng điều chỉnh và audit.
- `workDate`, `scheduleId`, tháng và năm phải được validate phía server.
- Mọi thay đổi lịch phải audit và thông báo cho nhân viên bị ảnh hưởng.
- POST phải có CSRF token và chống submit lặp.

## Code còn lệch spec hoặc cần bổ sung
- Chưa thấy CSRF, audit và notification khi lịch thay đổi.
- Cần quy tắc rõ cho lịch trùng, lịch quá khứ và lịch đã có attendance.
- `getAllActiveSchedules` cần được kiểm tra lại tại thời điểm ghi, không chỉ khi hiển thị.
- Thông báo lỗi cần phân biệt validation, thiếu quyền, xung đột và lỗi hệ thống.

## Kiểm thử tối thiểu
- User chỉ có quyền xem không thể assign/update/delete.
- Không thể dùng `employeeId` hoặc `assignmentId` thuộc phòng ban khác.
- Lịch trùng, ngày sai, schedule không tồn tại/inactive và CSRF sai phải thất bại an toàn.
- Kiểm tra biên chuyển tháng/năm và dữ liệu hiển thị đúng timezone hệ thống.
