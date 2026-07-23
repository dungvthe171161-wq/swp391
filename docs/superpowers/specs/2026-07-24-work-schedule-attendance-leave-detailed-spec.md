# Detailed Spec - Feature Tree 08 Work Schedule, Attendance And Leave

Phạm vi: đặc tả chi tiết cho domain WorkSchedule, GPS Attendance và Leave. File này bổ sung chi tiết cho `docs/superpowers/plans/feature-tree/08-work-schedule-attendance-leave.md`.

## 1. Actors

- Employee: xem lịch cá nhân, check-in/check-out GPS, gửi leave request, xem trạng thái leave.
- Department Manager: xem/gán/sửa/xóa lịch trong phòng ban, xem và quyết định leave trong phòng ban theo mô hình phê duyệt đã chốt.
- HR Manager: xem và quyết định leave nếu mô hình phê duyệt cho phép.
- Admin/HR authorized user: cấu hình OfficeLocation theo phạm vi Feature Tree 09.
- Payroll process: đọc Attendance và Leave Approved theo kỳ.

## 2. Data Model

### WorkSchedule

- `ScheduleID`.
- `ScheduleCode`.
- `ScheduleName`.
- `StartTime`.
- `EndTime`.
- `BreakMinutes`.
- `WorkingHours`.
- `GraceLateMinutes`.
- `GraceEarlyLeaveMinutes`.
- `IsActive`.
- `CreatedBy`.
- `CreatedAt`.
- `UpdatedAt`.

### EmployeeWorkSchedule

- `AssignmentID`.
- `EmployeeID`.
- `ScheduleID`.
- `WorkDate`.
- `Note`.
- `AssignedBy`.
- `CreatedAt`.
- `UpdatedAt`.

Rule: một Employee có tối đa một schedule assignment cho mỗi `WorkDate`, trừ khi dự án chính thức thiết kế split shift.

### OfficeLocation

- `OfficeLocationID`.
- `LocationCode`.
- `LocationName`.
- `Address`.
- `Latitude`.
- `Longitude`.
- `RadiusMeters`.
- `IsActive`.

### Attendance

- `AttendanceID`.
- `EmployeeID`.
- `Date`.
- `CheckIn`.
- `CheckOut`.
- `WorkingHours`.
- `OvertimeHours`.
- `ScheduleID`.
- `CheckInLatitude`.
- `CheckInLongitude`.
- `CheckInDistanceMeters`.
- `CheckInAccuracy`.
- `CheckInOfficeLocationID`.
- `CheckInMethod`.
- `CheckOutLatitude`.
- `CheckOutLongitude`.
- `CheckOutDistanceMeters`.
- `CheckOutAccuracy`.
- `CheckOutOfficeLocationID`.
- `CheckOutMethod`.

### MailRequest Leave

- `RequestID`.
- `EmployeeID`.
- `RequestType = 'Leave'`.
- `LeaveType`.
- `StartDate`.
- `EndDate`.
- `Reason`.
- `Status`.
- `ApprovedBy`.

## 3. WorkSchedule Master Management

### Mục đích

Quản lý danh sách ca làm việc chuẩn có thể được gán cho Employee.

### Actor

- Admin hoặc role được dự án cấp quyền quản lý schedule master.

### Operations

- Create WorkSchedule.
- Update WorkSchedule.
- Deactivate WorkSchedule.
- View active WorkSchedule.

### Business rules

- `ScheduleCode` nếu có phải unique.
- `StartTime` phải trước `EndTime`, trừ khi dự án định nghĩa ca qua ngày.
- `WorkingHours` phải lớn hơn 0.
- `BreakMinutes`, `GraceLateMinutes`, `GraceEarlyLeaveMinutes` không âm.
- WorkSchedule inactive không được gán mới cho Employee.

### Current implementation note

DAO hiện có method create/update/deactivate, nhưng cần route/controller/view hoặc ghi rõ actor quản lý trong Feature Tree 09 nếu không nằm trong FT08 UI.

## 4. View Own Schedule

### Mục đích

Employee xem lịch cá nhân theo tháng.

### Actor

- Employee.

### Route

- GET `/employee/schedule`

### Business rules

- EmployeeID lấy từ session, không lấy từ browser.
- Chỉ trả về `EmployeeWorkSchedule` thuộc Employee hiện tại.
- Nếu chưa có lịch, hiển thị empty state.

## 5. View Department Schedule

### Mục đích

Department Manager xem lịch nhân viên trong phòng ban của mình.

### Actor

- Department Manager.

### Route

- GET `/dept/schedules`

### Business rules

- Manager phải có department scope hợp lệ.
- Chỉ xem nhân viên thuộc phòng ban của manager.
- Cần permission xem lịch, dự kiến `VIEW_WORK_SCHEDULE`.
- Không lọc dữ liệu khác phòng ban ở client; backend phải scope query.

## 6. Assign Schedule To Employee

### Mục đích

Department Manager gán một WorkSchedule active cho Employee trong phòng ban vào một ngày cụ thể.

### Actor

- Department Manager.

### Route

- POST `/dept/schedules`

### Request fields

- `action=assign`.
- `employeeId`.
- `scheduleId`.
- `workDate`.
- `note`.

### Business rules

- Manager cần permission `MANAGE_WORK_SCHEDULE`.
- Employee phải thuộc phòng ban của manager.
- WorkSchedule phải active.
- Mỗi Employee chỉ có một assignment trong một ngày.
- Nếu trùng Employee + WorkDate, dự án phải chọn rõ một trong hai hành vi:
  - cập nhật assignment hiện có; hoặc
  - báo lỗi conflict.

### Postconditions

- `EmployeeWorkSchedule` được tạo mới hoặc cập nhật theo policy đã chọn.
- `AssignedBy` lưu EmployeeID của manager hoặc actor được ủy quyền.

## 7. Update Schedule Assignment

### Mục đích

Department Manager sửa schedule/date/note của assignment đã tạo trong phòng ban.

### Business rules

- Assignment phải thuộc Employee trong phòng ban manager.
- WorkSchedule mới phải active.
- Không được sửa assignment đã có Attendance nếu chưa có policy correction.
- Nếu đổi `WorkDate`, vẫn phải bảo toàn unique Employee + WorkDate.

## 8. Delete Schedule Assignment

### Mục đích

Department Manager xóa assignment sai hoặc chưa cần dùng.

### Business rules

- Assignment phải thuộc phòng ban manager.
- Không được xóa assignment trong quá khứ hoặc assignment đã có Attendance nếu chưa có policy correction.
- Nếu cần giữ audit/history, nên deactivate/cancel thay vì hard delete.

## 9. GPS Attendance Page

### Mục đích

Employee xem trạng thái attendance hôm nay, office location, lịch hôm nay, summary tháng và lịch sử gần đây.

### Route

- GET `/employee/attendance`

### Data displayed

- Today attendance.
- Today status.
- Today schedule.
- OfficeLocation active.
- Allowed radius.
- Monthly summary.
- Recent attendance history.

## 10. GPS Check-in

### Mục đích

Employee ghi nhận vào ca bằng GPS.

### Route

- POST `/employee/attendance`

### Request fields

- `action=checkIn`.
- `latitude`.
- `longitude`.
- `accuracy`.

### Business rules

- EmployeeID lấy từ session.
- Latitude phải trong [-90, 90].
- Longitude phải trong [-180, 180].
- Accuracy phải tồn tại, hữu hạn và không âm.
- Dự án phải định nghĩa ngưỡng accuracy tối đa được chấp nhận.
- Phải có OfficeLocation active hợp lệ.
- Distance phải do backend tính.
- Check-in hợp lệ khi chưa có CheckIn trong ngày.
- Dự án phải quyết định rõ: không có EmployeeWorkSchedule trong ngày thì cho phép hay từ chối.
- Dự án phải quyết định rõ: Employee đang có Leave Approved trong ngày thì có được check-in hay không.

### Postconditions

- Attendance được tạo hoặc cập nhật check-in.
- Lưu GPS raw fields, distance, accuracy, OfficeLocationID và method `GPS`.

## 11. GPS Check-out

### Mục đích

Employee ghi nhận ra ca bằng GPS.

### Business rules

- Phải có CheckIn trong ngày.
- Chưa có CheckOut.
- GPS validation giống check-in.
- WorkingHours và OvertimeHours phải được tính theo policy đã tài liệu hóa.
- Nếu tính theo WorkSchedule, phải dùng `StartTime`, `EndTime`, `WorkingHours`, grace và overtime rule của WorkSchedule hiệu lực.

### Postconditions

- Attendance được cập nhật CheckOut, WorkingHours, OvertimeHours và GPS checkout fields.

## 12. Attendance History And Summary

### Mục đích

Employee và các quy trình downstream xem tổng hợp Attendance.

### Required metrics

- Worked days.
- Total working hours.
- Total overtime hours.
- Late count.
- Early leave count.
- Recent records.

### Business rules

- Summary phải scope theo Employee.
- Late/early nên dựa trên WorkSchedule hiệu lực thay vì mốc cố định, trừ khi dự án quy định giờ làm cố định.

## 13. OfficeLocation Dependency

### Mục đích

OfficeLocation định nghĩa vị trí hợp lệ để check-in/check-out GPS.

### Business rules

- OfficeLocation phải active khi dùng.
- Radius phải lớn hơn 0.
- Latitude/longitude phải hợp lệ.
- Dự án phải định nghĩa có một hay nhiều OfficeLocation active.
- Nếu nhiều OfficeLocation active, backend chọn nearest office và kiểm radius với office gần nhất.
- Quản trị OfficeLocation thuộc Feature Tree 09, nhưng rule sử dụng thuộc Feature Tree 08.

## 14. Submit Leave Request

### Mục đích

Employee gửi yêu cầu nghỉ phép.

### Route

- POST `/employee/leaves`

### Request fields

- `leaveType`.
- `leaveDetail`.
- `startDate`.
- `endDate`.
- `handoverTo`.
- `handoverWork`.
- `reason`.

### Business rules

- Requester lấy từ session.
- Status ban đầu là `Pending`.
- Employee không được gửi `ApprovedBy` hoặc `Status`.
- LeaveType hợp lệ: Annual, Sick, Maternity, Unpaid, Other hoặc danh sách được cấu hình.
- LeaveDetail hợp lệ: FullDay, Morning, Afternoon hoặc danh sách được cấu hình.
- StartDate không ở quá khứ.
- EndDate không trước StartDate.
- Pending/Approved leave không được overlap.
- Paid leave phải kiểm tra balance.
- Handover email/work/reason là bắt buộc nếu policy yêu cầu.
- Notification cho approver và email handover chỉ nên gửi sau khi request tạo thành công.

## 15. View Leave Request Status

### Mục đích

Employee xem các leave request của chính mình.

### Business rules

- Chỉ hiển thị leave có `EmployeeID` của Employee hiện tại.
- Hiển thị trạng thái `Pending`, `Approved`, `Rejected`.
- Nếu có `ApprovedBy`, hiển thị người xử lý nếu dữ liệu hỗ trợ.
- Nếu có reject reason trong tương lai, hiển thị reason.

## 16. Department Manager Approve/Reject Leave

### Mục đích

Department Manager quyết định leave request của nhân viên thuộc phòng ban.

### Business rules

- Chỉ xử lý leave của Employee thuộc phòng ban manager.
- Chỉ request `Pending` được quyết định.
- Decision hợp lệ: `Approved`, `Rejected`.
- `ApprovedBy` phải là approver đang đăng nhập.
- Notification kết quả phải gửi về Employee theo policy Notification.
- Cần xác định rõ đây là quyết định cuối hay bước review đầu tiên.

## 17. HR Manager Approve/Reject Leave

### Mục đích

HR Manager quyết định leave request theo mô hình phê duyệt đã chọn.

### Business rules

- Chỉ request `Pending` hoặc trạng thái chờ HR được xử lý, tùy approval model.
- Nếu Model A: HR Manager có thể là approver cuối thay thế Department Manager theo routing rule rõ ràng.
- Nếu Model B: HR Manager chỉ xử lý sau khi Department Manager review và state phải thể hiện bước trung gian.
- Không giữ hai route cùng là quyết định cuối nếu không có routing rule.

## 18. Leave Balance

### Mục đích

Đảm bảo Employee không gửi vượt số dư nghỉ hưởng lương.

### Business rules

- Dự án phải định nghĩa đơn vị balance là ngày hay session.
- FullDay = 2 sessions nếu dùng session.
- Morning/Afternoon = 1 session.
- Unpaid leave không trừ paid balance.
- Pending và Approved paid leave đều nên giữ chỗ balance.
- Dự án phải định nghĩa default balance, carry-over và năm áp dụng.
- Reject/cancel phải trả lại balance nếu balance được trừ hoặc giữ chỗ.

## 19. Schedule - Attendance - Leave Relationship

### Required decisions

- Không có schedule hôm nay: được check-in hay từ chối.
- WorkSchedule inactive: không được assign mới.
- Approved Leave trong ngày: được check-in hay từ chối.
- Attendance phải lưu `ScheduleID` nếu có schedule hiệu lực.
- WorkingHours/OvertimeHours tính theo WorkSchedule hay mốc cố định.
- Late/EarlyLeave tính theo ca nào.
- Payroll chỉ đọc Attendance và Leave Approved trong kỳ đã chuẩn hóa.

## 20. Approval Model

Dự án phải chọn đúng một mô hình:

- Model A: Department Manager hoặc HR Manager là approver cuối theo routing rule rõ ràng.
- Model B: Department Manager review trước, HR Manager quyết định cuối.

Nếu chọn Model B, database/state tối thiểu cần phân biệt:

- `Pending_Manager`.
- `Pending_HR`.
- `Approved`.
- `Rejected`.

## 21. Traceability

- Main plan: `docs/superpowers/plans/feature-tree/08-work-schedule-attendance-leave.md`.
- UI dependencies: Feature Tree 03 và Feature Tree 04.
- OfficeLocation dependency: Feature Tree 09.
- Payroll dependency: Feature Tree 07.
- Notification dependency: Feature Tree 10.
