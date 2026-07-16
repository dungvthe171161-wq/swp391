# Tính năng Dept: Lịch phòng ban

Trạng thái: Màn hình tổng hợp sơ khai, đã rà soát theo code ngày 2026-07-14; chưa có calendar service hoặc event contract hoàn chỉnh.
Ngôn ngữ: tiếng Việt có dấu. Spec này phân biệt rõ hiện trạng với thiết kế mục tiêu.

## Actor và phạm vi
- Dept Manager xem các mốc công việc, nghỉ phép và lịch làm việc của phòng ban.

## Route, controller và JSP liên quan
- `GET /dept?action=calendar`.
- Controller: `DeptController`; DAO hiện dùng: `EmployeeDAO`, `DeptDashboardDAO`, `MailRequestDAO`.
- JSP: `Views/DeptManager/calendar.jsp`.
- Luồng liên quan: `/dept/schedules`, `/dept/leaves` và task của Dept.

## Hiện trạng code
- `DeptController` forward trang calendar qua `loadSimpleDeptPage`.
- Dữ liệu dùng chung gồm employee, dashboard count và leave đã approved.
- Chưa có DTO/event source thống nhất cho task, work schedule và leave.

## Quy tắc nghiệp vụ chuẩn
- Calendar chỉ chứa event thuộc phòng ban và khoảng thời gian được yêu cầu.
- Phải chốt loại event: task deadline, leave approved, work schedule và sự kiện khác nếu có.
- Mỗi event có `id`, `type`, `title`, start/end, timezone, trạng thái và URL chi tiết an toàn.
- Màu/nhãn phải nhất quán; dữ liệu nghỉ phép không được lộ lý do y tế nhạy cảm nếu không cần.
- Calendar mặc định read-only; mutation phải đi qua route nghiệp vụ gốc và permission tương ứng.
- Truy vấn phải giới hạn khoảng ngày để tránh tải toàn bộ lịch sử.

## Code còn lệch spec hoặc cần bổ sung
- Chưa có calendar DTO/service và truy vấn theo date range.
- Chưa tích hợp đầy đủ task và work schedule.
- Cần xác định timezone chuẩn, event chồng lấn và quyền xem chi tiết.

## Kiểm thử tối thiểu
- Chỉ trả event của đúng phòng ban và đúng khoảng ngày.
- Kiểm tra event kéo qua tháng/năm, timezone và ngày nghỉ nhiều ngày.
- Không lộ lý do nghỉ hoặc thông tin cá nhân không cần thiết.
- Calendar không trực tiếp thay đổi trạng thái khi người dùng chỉ mở/kéo event.
