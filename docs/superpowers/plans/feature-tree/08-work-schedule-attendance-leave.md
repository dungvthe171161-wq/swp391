# Feature Tree 08 - Kế hoạch triển khai Lịch làm việc, Chấm công và Nghỉ phép

- **Trạng thái:** Bản nháp
- **Phạm vi chuẩn:** Feature Tree 08
- **Phụ thuộc:** Plan 01, dữ liệu OfficeLocation/Department của Plan 09 và Plan 10
- **Được sử dụng bởi:** Plan 03, 04 và 07

## 1. Mục tiêu

Cung cấp một mô hình domain thống nhất cho WorkSchedule active, assignment theo Employee/ngày, GPS check-in/check-out, tổng hợp Attendance, validation số dư/overlap Leave, Notification bàn giao, phê duyệt và kết quả Attendance/Leave sẵn sàng cho Payroll.

## 2. Ranh giới phạm vi

Plan này chịu trách nhiệm rule, trạng thái, service, DAO và database constraint của Schedule, Attendance và Leave. Plan 04 chịu trách nhiệm giao diện Department Manager và điều phối đã scope. Plan 03 chịu trách nhiệm giao diện Employee. Plan 09 chịu trách nhiệm UI master data OfficeLocation. Plan 07 sử dụng kết quả cuối theo kỳ.

## 3. Code và dữ liệu hiện tại cần audit

### Controller và tiện ích

- Các phần Schedule/Attendance/Leave trong `EmployeePortalController`
- `dept/DeptWorkScheduleController`
- `dept/DeptLeaveController`
- `hr/LeaveApprovalController`
- `admin/OfficeLocationController`
- `GeoUtil`, `DeptManagerScope`, `EmailSender`

### DAO và model

- `WorkScheduleDAO`, `AttendanceDAO`, `MailRequestDAO`, `OfficeLocationDAO`, `EmployeeDAO`
- `WorkSchedule`, `EmployeeWorkSchedule`, `Attendance`, `MailRequest`, `OfficeLocation`

### Migration

- `src/data/migrations/2026-07-09_work_schedule_gps_attendance_final.sql`
- `src/data/migrations/2026-07-15_gps_attendance_accuracy.sql`

## 4. Luồng lúc chạy

```text
Admin -> cấu hình OfficeLocation
Department Manager -> gán Schedule cho Employee đã scope
Employee -> xem Schedule -> GPS check-in/check-out
Employee -> gửi Leave -> validation/balance/overlap -> hàng đợi phê duyệt
Approver được phép -> quyết định có điều kiện
Payroll -> đọc Attendance theo kỳ và Leave Approved
```

## 5. Các bất biến domain

- Một Employee có tối đa một WorkSchedule assignment có hiệu lực mỗi ngày, trừ khi hệ thống thiết kế split shift rõ ràng.
- Schedule và OfficeLocation phải active khi được sử dụng.
- Tọa độ GPS, accuracy, distance và OfficeLocation được kiểm tra ở backend.
- Server time và business timezone đã cấu hình quyết định ngày/giờ Attendance.
- Check-out yêu cầu đã check-in và chỉ được thực hiện một lần.
- Leave Pending và Approved không được overlap yêu cầu Leave active khác.
- Leave balance được kiểm tra nhất quán theo đơn vị session/ngày của dự án.
- Một mô hình phê duyệt rõ ràng kiểm soát quyết định Leave cuối.
- Payroll đọc kết quả Attendance bất biến/đã chốt và Leave Approved trong kỳ.

## 6. Quyết định phê duyệt bắt buộc

Trước khi triển khai, phải chọn và tài liệu hóa đúng một mô hình:

```text
Mô hình A: Department Manager HOẶC HR Manager được phép đưa ra một quyết định cuối.
Mô hình B: Department Manager review bước đầu, sau đó HR Manager đưa ra quyết định cuối.
```

Code hiện tại có hai route độc lập chuyển Pending sang Approved/Rejected. Không triển khai UI tuần tự nếu chưa thêm state và metadata reviewer riêng; đồng thời không giữ hai route cùng là quyết định cuối nếu chưa có routing rule rõ ràng.

## 7. Các task triển khai

### Task 1 - Audit schema, quy tắc thời gian và đường xử lý trùng

- [ ] So sánh schema WorkSchedule, EmployeeWorkSchedule, Attendance, OfficeLocation và MailRequest với DAO/migration.
- [ ] Kiểm kê mọi thao tác đọc/ghi Schedule, Attendance và Leave trong controller, DAO, JSP và Payroll.
- [ ] Định nghĩa business timezone của ứng dụng và biên ngày Payroll.
- [ ] Ghi nhận giờ làm việc là cố định hay được suy ra từ Schedule.
- [ ] Xác định method Attendance không dùng GPS cũ và quyết định giữ, giới hạn hay xóa.
- [ ] Thiết lập regression test cho Schedule, GPS, Leave và input Payroll hiện tại.

### Task 2 - Chuẩn hóa schema Schedule và constraint assignment

- [ ] Định nghĩa trường WorkSchedule, active state, giờ bắt đầu/kết thúc, giờ nghỉ, grace period và overtime rule.
- [ ] Thực thi quy tắc duy nhất Employee-Date assignment đã chọn.
- [ ] Thêm index cho truy vấn Employee/ngày và Department/tháng.
- [ ] Định nghĩa việc gán lại ảnh hưởng tới Attendance hiện có như thế nào.
- [ ] Thêm metadata assigned-by, timestamp và note khi cần.
- [ ] Đồng bộ bootstrap schema và migration.
- [ ] Thêm migration test cho assignment trùng.

### Task 3 - Tạo `WorkScheduleService`

- [ ] Chuyển validation assignment khỏi `DeptWorkScheduleController` và DAO.
- [ ] Kiểm tra Schedule active, ngày hợp lệ, Employee đã scope và quy tắc duplicate/overlap.
- [ ] Triển khai assign, update và delete/deactivate bằng thao tác có điều kiện.
- [ ] Ngăn đổi Schedule sau khi đã có Attendance, trừ khi có rule correction được cấp quyền.
- [ ] Trả về typed result cho validation, permission, conflict và not-found.
- [ ] Thêm service test cho khác phòng ban, trùng ngày, Schedule inactive và đã có Attendance.

### Task 4 - Chính sách OfficeLocation và GPS

- [ ] Kiểm tra latitude, longitude, radius, name/address và active state trong thao tác Admin Plan 09.
- [ ] Định nghĩa được phép có một hay nhiều OfficeLocation active và cách gán cho Employee.
- [ ] Dùng `GeoUtil` với đơn vị và hành vi biên được tài liệu hóa.
- [ ] Định nghĩa accuracy thiết bị tối đa được chấp nhận và distance cộng accuracy có ảnh hưởng đến kết quả hay không.
- [ ] Lưu tọa độ check-in/out, distance đã tính, accuracy, OfficeLocationID và method.
- [ ] Không tin distance do JavaScript tính.
- [ ] Thêm test biên latitude/longitude, mép radius, accuracy kém và location inactive.

### Task 5 - Tạo `AttendanceService`

- [ ] Xác định Employee, server time/date hiện tại, Schedule có hiệu lực và OfficeLocation được phép.
- [ ] Kiểm tra state transition check-in/check-out.
- [ ] Tính working/overtime hour từ Schedule có hiệu lực và rounding rule đã tài liệu hóa.
- [ ] Làm insert check-in và update check-out có điều kiện/idempotent.
- [ ] Trả kết quả rõ ràng cho không có Schedule, ngoài radius, accuracy kém, đã check-in, chưa check-in và đã check-out.
- [ ] Giữ chi tiết GPS thô riêng tư với role không liên quan.
- [ ] Thêm test double-click đồng thời và biên nửa đêm/timezone.

### Task 6 - Quyết định phạm vi Attendance correction

- [ ] Xác nhận correction/request approval Attendance có thuộc phạm vi dự án hay không.
- [ ] Nếu ngoài phạm vi, tài liệu hóa giới hạn và làm record bất biến với người dùng thông thường.
- [ ] Nếu trong phạm vi, định nghĩa state CorrectionRequested/Approved/Rejected, giá trị gốc, giá trị yêu cầu, lý do, reviewer và audit.
- [ ] Không cho HR hoặc manager ghi đè Attendance mà không giữ bằng chứng gốc.
- [ ] Thêm test ownership correction, permission reviewer và payroll cutoff nếu triển khai.

### Task 7 - Chuẩn hóa đơn vị Leave, balance và overlap rule

- [ ] Định nghĩa đơn vị full-day, morning, afternoon, paid và unpaid leave.
- [ ] Tập trung danh sách Leave type/detail hợp lệ thay vì lặp constant trong controller.
- [ ] Định nghĩa năm tính phép, carryover, maternity/sick, weekend, holiday và cách tính half-day.
- [ ] Kiểm tra start/end date, lý do, trường bàn giao bắt buộc và Leave balance.
- [ ] Kiểm tra overlap với request Pending/Approved trong cùng transaction.
- [ ] Thêm database index/constraint hỗ trợ truy vấn overlap và lịch sử Employee.
- [ ] Thêm test full-day/half-day, biên năm, balance và overlap.

### Task 8 - Tạo `LeaveRequestService`

- [ ] Chuyển quy tắc tạo/quyết định Leave khỏi controller Employee/Dept/HR.
- [ ] Tạo request Pending với requester suy ra từ ngữ cảnh Employee đã xác thực.
- [ ] Xác định hàng đợi approver từ mô hình phê duyệt đã chốt.
- [ ] Triển khai approve/reject có điều kiện với actor, timestamp và lý do bắt buộc.
- [ ] Định nghĩa quy tắc cancel và trả lại balance nếu có hỗ trợ cancel.
- [ ] Trả typed result và dùng Post/Redirect/Get trong portal controller.
- [ ] Chỉ xếp hàng email bàn giao và Notification Plan 10 sau commit.

### Task 9 - Triển khai mô hình phê duyệt đã chọn

- [ ] Với Mô hình A, thêm routing/permission rule rõ ràng xác định approver cuối duy nhất cho từng request.
- [ ] Với Mô hình B, migrate state sang Pending_Manager, Pending_HR, Approved và Rejected với metadata reviewer riêng.
- [ ] Cập nhật đồng thời `DeptLeaveController`, `LeaveApprovalController`, `MailRequestDAO`, hàng đợi JSP, route Notification và dashboard count.
- [ ] Xóa hoặc redirect đường quyết định cũ gây xung đột.
- [ ] Thêm test manager/HR quyết định đồng thời và xử lý stale-state.

### Task 10 - Read model cho Payroll

- [ ] Định nghĩa tổng Attendance theo kỳ, vắng mặt, đi muộn/về sớm, overtime và Leave có/không lương đã duyệt mà Plan 07 cần.
- [ ] Chỉ đọc record trong biên PayPeriod đã chuẩn hóa.
- [ ] Chỉ dùng Leave Approved và tránh đếm trùng đơn vị overlap.
- [ ] Định nghĩa cutoff/tính lại khi correction hoặc approval đến muộn.
- [ ] Thêm fixture đối chiếu record Attendance/Leave hằng ngày với input Payroll.

### Task 11 - Tích hợp UI và thông báo

- [ ] Giữ trang Employee của Plan 03 làm view/action trên các service này.
- [ ] Giữ trang Department của Plan 04 làm view/action đã scope trên các service này.
- [ ] Giới hạn trang OfficeLocation của Plan 09 ở quản trị master data.
- [ ] Hiển thị nhất quán thông báo Schedule, GPS, balance, overlap, approval và conflict.
- [ ] Hiển thị đúng giai đoạn phê duyệt khi chọn Mô hình B.
- [ ] Kiểm tra layout responsive và nội dung tiếng Việt UTF-8.

### Task 12 - Xác minh

- [ ] Chạy test Schedule, GPS, state Attendance, biên thời gian, Leave balance/overlap, concurrency approval và Payroll read model.
- [ ] Chạy `mvn test`, `mvn -q compile` và `mvn -q package`.
- [ ] Cấu hình thủ công OfficeLocation, gán Schedule, check-in/out, gửi Leave, approve/reject và kiểm tra input Payroll.
- [ ] Thử quyết định Schedule và Leave khác phòng ban.
- [ ] Test GPS ngay bên trong, đúng mép và bên ngoài radius cùng accuracy kém.
- [ ] Xác minh các approver cạnh tranh không thể tạo hai quyết định cuối mâu thuẫn.

## 8. Tiêu chí hoàn thành

- Schedule assignment, GPS Attendance và Leave dùng service tập trung và state rule rõ ràng.
- Server time, timezone, distance, accuracy và ownership được kiểm tra nhất quán.
- Đúng một mô hình phê duyệt Leave đã tài liệu hóa được triển khai.
- Payroll nhận input Attendance theo kỳ và Leave Approved đã đối chiếu.
- Test khác phòng ban, concurrency, boundary, Maven và luồng thủ công đầy đủ đều đạt.

