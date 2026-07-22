# Tính năng Employee: Xem lịch làm việc

Trạng thái: Đã rà soát theo code ngày 2026-07-22.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Employee xem ca làm việc hôm nay và lịch làm việc theo tháng của chính mình.

## Route, controller và JSP liên quan
- `GET /employee/schedule`.
- Controller: `EmployeePortalController`, method `showSchedule`.
- DAO/entity: `WorkScheduleDAO`, `WorkSchedule`, `EmployeeWorkSchedule`.
- JSP: `Views/Employee/Schedule.jsp`; sidebar Employee link tới `/employee/schedule`.

## Hiện trạng code
- `EmployeePortalController` xử lý `case "/schedule"` dưới route `/employee/*`.
- `prepareEmployeeContext` lấy `systemUser.EmployeeID`; nếu không có employee hợp lệ thì redirect về `/login` hoặc `/homepage`.
- `showSchedule` đọc `month`, `year`; nếu tham số sai thì fallback về `YearMonth.now()`.
- Dữ liệu hiển thị gồm `todaySchedule` từ `WorkScheduleDAO.getByEmployeeAndDate` và `monthlySchedules` từ `WorkScheduleDAO.getByEmployeeMonth`.
- Employee chỉ có quyền xem, không có POST tạo/sửa/xóa lịch ở cổng Employee.

## Quy tắc nghiệp vụ chuẩn
- Employee chỉ được xem lịch của chính `systemUser.EmployeeID`.
- Lịch làm việc do Dept Manager/Admin phân công ở luồng quản lý lịch, Employee không tự chỉnh sửa.
- Bộ lọc tháng/năm phải an toàn với dữ liệu sai; UI cần hiển thị trạng thái trống khi chưa có lịch.
- Nếu dùng permission riêng, route này nên kiểm tra `VIEW_OWN_WORK_SCHEDULE` thay vì dùng chung quyền xem chi tiết employee.

## Code còn lệch spec hoặc cần bổ sung
- `ModulePermissionFilter` hiện bảo vệ `/employee/*` bằng `VIEW_EMPLOYEE_DETAIL`, chưa dùng permission riêng `VIEW_OWN_WORK_SCHEDULE`.
- Chưa có test tự động cho `/employee/schedule`, fallback tháng/năm sai và trường hợp employee chưa có lịch.
- Chưa có audit/notification riêng khi lịch của employee thay đổi; phần này thuộc luồng Dept/Admin phân công lịch.

## Kiểm thử tối thiểu
- Employee A mở `/employee/schedule` chỉ thấy lịch của Employee A.
- Sửa `month`, `year` thành dữ liệu sai không làm lỗi 500.
- User không phải Employee/Admin không truy cập được `/employee/schedule`.
- Khi chưa có lịch trong tháng, JSP hiển thị trạng thái trống hợp lý.
