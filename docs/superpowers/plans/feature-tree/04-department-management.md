# Feature Tree 04 - Kế hoạch triển khai Quản lý phòng ban

- **Trạng thái:** Bản nháp
- **Phạm vi chuẩn:** Feature Tree 04
- **Phụ thuộc:** Plan 01, 08 và 10
- **Role:** Department Manager

## 1. Mục tiêu

Cung cấp portal giới hạn theo phòng ban cho Department Manager, bao gồm dashboard, danh bạ nhân viên, giao và duyệt Task, điều phối lịch làm việc, quyết định Leave, calendar, performance và report mà không cho phép truy cập dữ liệu khác phòng ban.

## 2. Nguồn plan cũ

- `docs/superpowers/plans/2026-06-28-dept-manager-portal.md`
- `docs/superpowers/specs/2026-06-28-dept-manager-portal-design.md`

Plan cũ chưa cập nhật checkbox dù nhiều file mục tiêu đã tồn tại. Hãy coi đó là đầu vào thiết kế, sau đó xác minh từng khả năng dựa trên code và database hiện tại trước khi đánh dấu plan chuẩn này hoàn thành.

## 3. Ranh giới phạm vi

Plan này chịu trách nhiệm:

- Route, shell, dashboard và view cấp phòng ban của Department Manager Portal.
- Tạo Task, gán, gán lại, duyệt kết quả và báo cáo phòng ban.
- Điều phối Schedule và quyết định Leave trong phạm vi phòng ban.

Plan 08 chịu trách nhiệm quy tắc domain WorkSchedule, Attendance và Leave. Plan 09 chịu trách nhiệm quản trị master data Department. Plan 10 chịu trách nhiệm gửi Notification.

## 4. Code hiện tại cần audit và tái sử dụng

- `DeptManagerScope`, `PermissionUtil`
- `DeptController`, `PostTask`, `TaskManager`, `ViewTask`
- `DeptWorkScheduleController`, `DeptLeaveController`
- `EmployeeDAO`, `DepartmentDAO`, `DeptDashboardDAO`, `TaskDAO`, `WorkScheduleDAO`, `MailRequestDAO`
- `Views/DeptManager/_DeptManagerSidebar.jspf`
- `Views/DeptManager/_DeptManagerTopbar.jspf`
- `Views/DeptManager/_DeptManagerStyles.jspf`
- Các JSP dashboard, employees, task, schedule, leave, calendar, performance và report
- `src/data/migrations/2026-07-07_department_task_assignment_flow.sql`
- Các migration deadline Task và `TaskDeadlineReminderListener`

## 5. Luồng code lúc chạy

```text
Request của Department Manager
  -> Xác thực và quyền module của Plan 01
  -> DeptManagerScope ánh xạ User -> Employee -> Department
  -> Controller/service phòng ban kiểm tra tài nguyên yêu cầu thuộc Department đó
  -> DAO thêm DepartmentID/ownership manager vào điều kiện SELECT và UPDATE
  -> Database transaction
  -> Plan 10 gửi Notification cho Employee bị ảnh hưởng
  -> Kết quả trên manager portal
```

## 6. Các bất biến phạm vi phòng ban

- DepartmentID được suy ra từ ngữ cảnh đã xác thực, không tin giá trị từ browser.
- Mọi truy vấn danh sách, chi tiết, mutation, export và JSON response đều áp dụng cùng một phạm vi phòng ban.
- EmployeeID gửi từ browser phải được load lại và kiểm tra thuộc phòng ban của manager trước khi sử dụng.
- Mỗi Task assignment có trạng thái riêng theo Employee; một assignee không thể ghi đè tiến độ của assignee khác.
- Quyết định Leave và Schedule dùng conditional update để ngăn duyệt hai lần hoặc ghi dữ liệu cũ.
- Report là kết quả tổng hợp của truy vấn đã scope, không phải dữ liệu toàn hệ thống được lọc ở client.

## 7. Các task triển khai

### Task 1 - Đối chiếu plan cũ với code hiện tại

- [ ] So sánh từng task cũ với controller, DAO, JSP, migration và route hiện tại.
- [ ] Ghi từng mục là đã triển khai, một phần, còn thiếu hoặc đã bị thay thế trong implementation note của plan này.
- [ ] Ghi nhận cấu trúc bảng Task và assignment hiện tại trước khi thiết kế transition.
- [ ] Kiểm kê `/dept`, `/postTask`, `/taskManager`, `/viewTask`, `/dept/schedules` và `/dept/leaves`.
- [ ] Thêm baseline test chứng minh hành vi scope hiện tại trước khi refactor.

### Task 2 - Dùng `DeptManagerScope` làm hợp đồng đầu vào duy nhất

- [ ] Ánh xạ `SystemUser` đã xác thực sang Employee và Department đúng một lần cho mỗi request.
- [ ] Trả về kết quả rõ ràng cho chưa xác thực, sai role, thiếu Employee và thiếu Department.
- [ ] Chỉ công khai các identifier bất biến và dữ liệu hiển thị cần thiết cho controller.
- [ ] Thay logic lookup Department riêng lẻ trong controller bằng scope dùng chung.
- [ ] Thêm test manager có scope hợp lệ, thiếu Employee mapping, thiếu Department, Employee inactive và sai role.

### Task 3 - Thực thi phạm vi phòng ban trong DAO

- [ ] Audit truy vấn Employee, Task, assignment, WorkSchedule, MailRequest, dashboard và report.
- [ ] Thêm điều kiện phòng ban cho cả lookup chi tiết lẫn lookup danh sách.
- [ ] Thêm method update/delete có điều kiện gồm DepartmentID mong đợi và trạng thái hiện tại.
- [ ] Kiểm tra lại mọi EmployeeID browser gửi bằng DAO đã scope.
- [ ] Thêm test đọc, cập nhật, xóa và gán khác phòng ban.
- [ ] Dùng `PreparedStatement` và try-with-resources cho mọi truy vấn được sửa.

### Task 4 - Ổn định shell dùng chung và dashboard

- [ ] Áp dụng nhất quán sidebar, topbar, styles, page title, active page và notification bell.
- [ ] Lấy dashboard count từ `DeptDashboardDAO` với DepartmentID và manager EmployeeID.
- [ ] Hiển thị số nhân viên, Task đang mở, Task quá hạn, Leave chờ duyệt và dữ liệu Schedule/Calendar sắp tới.
- [ ] Định nghĩa trạng thái rỗng/lỗi cho phòng ban mới.
- [ ] Bảo đảm aggregate dashboard không đếm bản ghi ngoài phòng ban.
- [ ] Thêm test DAO cho count và test controller cho trường hợp thiếu scope.

### Task 5 - Danh bạ nhân viên phòng ban

- [ ] Chỉ liệt kê Employee active/được phép trong phòng ban của manager.
- [ ] Thêm tìm kiếm và filter trạng thái mà không bỏ điều kiện Department.
- [ ] Định nghĩa trường Employee mà Department Manager được xem.
- [ ] Không hiển thị lương, dữ liệu xác thực hoặc trường HR riêng tư nếu không có quyền rõ ràng.
- [ ] Kiểm tra ownership cho link chi tiết Employee hoặc giữ trang ở dạng chỉ danh sách.
- [ ] Thêm test trạng thái rỗng, tìm kiếm, phân trang và khác phòng ban.

### Task 6 - Chuẩn hóa trạng thái Task và assignment

- [ ] Quyết định `Task.Status` có phải trạng thái tổng hợp còn assignment row giữ trạng thái riêng của Employee hay không.
- [ ] Thêm migration idempotent nếu thiếu status cấp assignment, thời gian nộp, review state hoặc review reason.
- [ ] Định nghĩa transition Draft/Assigned/In Progress/Submitted/Completed/Rejected/Cancelled bằng giá trị tương thích database thực tế.
- [ ] Định nghĩa cách tính lại trạng thái tổng hợp Task khi nhiều assignee có trạng thái khác nhau.
- [ ] Thêm unique và foreign-key rule để ngăn assignment trùng hoặc không hợp lệ.
- [ ] Viết transition test trước khi cập nhật controller.

### Task 7 - Bảo mật tạo và gán lại Task

- [ ] Kiểm tra title, description, start date, due date và ít nhất một assignee.
- [ ] Từ chối start date sau due date và field dài bất hợp lý.
- [ ] Tạo Task và assignment trong một transaction.
- [ ] Kiểm tra lại từng assignee được chọn theo DepartmentID của manager.
- [ ] Chỉ cho phép chỉnh sửa khi đúng ownership Task, trạng thái và phòng ban.
- [ ] Định nghĩa hành vi gán lại cho assignee bị xóa nhưng đã có tiến độ.
- [ ] Chỉ gửi Notification sau commit thông qua Plan 10.
- [ ] Thêm test double-submit, rollback assignment một phần và khác phòng ban.

### Task 8 - Employee cập nhật Task và manager review

- [ ] Giải quyết xung đột route giữa `/viewTask` cũ và `/employee/view-task`.
- [ ] Yêu cầu ownership assignment khi Employee xem chi tiết và nộp kết quả.
- [ ] Chỉ cho Department Manager review assignment đã scope ở trạng thái Submitted.
- [ ] Lưu actor, thời gian và lý do từ chối khi cần.
- [ ] Dùng conditional update để ngăn review lặp.
- [ ] Gửi Notification khi giao, gán lại, duyệt, từ chối và nhắc deadline.
- [ ] Thêm test transition không hợp lệ và assignee này cố cập nhật assignee khác.

### Task 9 - Tích hợp Schedule và Leave

- [ ] Giữ `/dept/schedules` làm UI cho manager nhưng chuyển quy tắc validation/persistence cho service Plan 08.
- [ ] Xác minh Employee được chọn thuộc phòng ban trước khi gán/cập nhật/xóa Schedule.
- [ ] Giữ `/dept/leaves` làm hàng đợi quyết định đã scope theo mô hình duyệt được chốt trong Plan 08.
- [ ] Dùng transition có điều kiện từ Pending sang kết quả và lưu metadata approver.
- [ ] Ngăn Department Manager và HR Manager đưa ra hai quyết định cuối mâu thuẫn.
- [ ] Thêm test Schedule và Leave khác phòng ban.

### Task 10 - Calendar, Performance và Report

- [ ] Thay giá trị placeholder/static bằng dữ liệu DAO/service đã scope.
- [ ] Định nghĩa đầu vào Calendar: Leave đã duyệt, WorkSchedule, ngày bắt đầu/hạn Task và deadline.
- [ ] Định nghĩa rõ metric Performance, tránh suy luận đánh giá Employee chỉ từ trạng thái Task chưa đầy đủ.
- [ ] Định nghĩa khoảng ngày, filter, tổng số và permission cho Report.
- [ ] Giữ mọi truy vấn Report/export được scope phòng ban ở backend.
- [ ] Thêm test aggregate với hai phòng ban để phát hiện rò rỉ.

### Task 11 - Xác minh

- [ ] Chạy test department scope, workflow Task, Schedule, Leave và dashboard.
- [ ] Chạy `mvn test`, `mvn -q compile` và `mvn -q package`.
- [ ] Kiểm thử thủ công từng route Department Manager.
- [ ] Thử sửa URL, form và API bằng EmployeeID, TaskID, LeaveID và Schedule ID của phòng ban khác.
- [ ] Xác minh Notification chỉ được tạo một lần và sau commit thành công.
- [ ] Xác minh tổng số Report khớp với truy vấn database đã scope trực tiếp.

## 8. Tiêu chí hoàn thành

- Mọi thao tác Department Manager đều dùng `DeptManagerScope` cùng kiểm tra ownership tài nguyên.
- Trạng thái Task và assignment hỗ trợ nhiều assignee mà không làm hỏng trạng thái dùng chung.
- Hành động Schedule và Leave chuyển nghiệp vụ cho Plan 08 và không xung đột giữa approver.
- Dashboard, Calendar, Performance và Report dùng dữ liệu thật đã scope.
- Test truy cập khác phòng ban, Maven và route thủ công đều đạt.

