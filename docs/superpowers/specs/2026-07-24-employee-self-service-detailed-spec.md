# Detailed Spec - Feature Tree 03 Employee Self Service

Phạm vi: đặc tả chi tiết cho các màn hình và hành động Employee Portal. File này bổ sung chi tiết cho `docs/superpowers/plans/feature-tree/03-employee-self-service.md`; không thay thế các plan sở hữu nghiệp vụ như Schedule/Attendance/Leave, Task, Contract, Payroll và Notification.

## 1. Actors

- Employee: người dùng đã đăng nhập, có `SystemUser.EmployeeID`.
- Admin: có thể truy cập để kiểm tra/hỗ trợ theo chính sách role hiện có.

## 2. Preconditions chung

- Người dùng đã đăng nhập và session có `systemUser`.
- `systemUser.employeeId` tồn tại và ánh xạ được sang một bản ghi `Employee`.
- Employee portal không nhận `EmployeeID` từ browser cho các truy vấn dữ liệu cá nhân.

## 3. Authorization chung

- Route chuẩn: `/employee` và `/employee/*`.
- Role được phép: Employee hoặc Admin theo filter hiện có.
- Permission hiện có cho `/employee/*`: `VIEW_EMPLOYEE_DETAIL`.
- Mọi dữ liệu cá nhân phải được scope theo `employeeId` lấy từ session.

## 4. Employee Dashboard

### Mục đích

Employee xem nhanh trạng thái cá nhân trong ngày và các shortcut tới các chức năng tự phục vụ.

### Route

- GET `/employee`

### Dữ liệu hiển thị

- Attendance hôm nay.
- Trạng thái check-in/check-out hôm nay.
- Tổng hợp attendance tháng.
- Attendance gần đây.
- Task đang mở.
- Leave đang pending.
- Contract hiện tại.
- Payroll gần nhất.
- Shortcut tới Leave, Payroll, Task, Contract, Attendance.

### Normal flow

1. Employee mở `/employee`.
2. Hệ thống xác thực session và xác định Employee từ `systemUser.employeeId`.
3. Hệ thống load dữ liệu dashboard theo Employee hiện tại.
4. Hệ thống render Employee dashboard.

### Empty state

- Nếu chưa có attendance, task, contract hoặc payroll, màn hình phải hiển thị trạng thái rỗng thay vì lỗi.

### Business rules

- Không truy vấn dashboard bằng EmployeeID do browser gửi.
- Mỗi widget phải lỗi độc lập hoặc có trạng thái rỗng an toàn.

## 5. View Profile

### Mục đích

Employee xem hồ sơ cá nhân của chính mình.

### Route

- GET `/employee/profile`

### Trường hiển thị tối thiểu

- Mã nhân viên.
- Họ tên.
- Email.
- Số điện thoại.
- Giới tính.
- Ngày sinh.
- Địa chỉ.
- Phòng ban.
- Chức vụ.
- Ngày vào làm.
- Trạng thái nhân viên.

### Business rules

- Chỉ hiển thị hồ sơ của Employee đang đăng nhập.
- Không hỗ trợ xem hồ sơ Employee khác bằng query string hoặc form parameter.

## 6. Update Profile

### Mục đích

Employee cập nhật các trường hồ sơ cá nhân được phép.

### Route chuẩn mong muốn

- POST `/employee/profile`

### Trường được phép chỉnh sửa

- Email cá nhân nếu chính sách HR cho phép.
- Số điện thoại.
- Địa chỉ.

### Trường không được phép chỉnh sửa

- EmployeeID.
- DepartmentID.
- Position.
- Salary.
- HireDate.
- Status.
- Role.
- SystemUser linkage.

### Validation

- Email đúng định dạng và không vượt độ dài database.
- Phone đúng định dạng dự án chấp nhận.
- Address được trim và giới hạn độ dài.

### Current implementation note

Code hiện tại có route legacy `/profilepage?action=update`. Khi triển khai chuẩn, cần hợp nhất về `/employee/profile` hoặc ghi rõ route legacy là ngoài Employee Portal.

## 7. Employee Work Schedule

### Mục đích

Employee xem lịch làm việc cá nhân theo tháng.

### Route

- GET `/employee/schedule`

### Request parameters

- `month` optional.
- `year` optional.

### Dữ liệu

- WorkDate.
- ScheduleCode.
- ScheduleName.
- StartTime.
- EndTime.
- Note.

### Business rules

- Chỉ render assignment thuộc Employee hiện tại.
- Nếu chưa có lịch, hiển thị empty state.
- Quy tắc assignment và schedule active thuộc Feature Tree 08.

## 8. Employee Attendance

### Mục đích

Employee xem attendance, lấy GPS và thực hiện check-in/check-out.

### Routes

- GET `/employee/attendance`
- POST `/employee/attendance`

### POST parameters

- `action`: `checkIn` hoặc `checkOut`.
- `latitude`.
- `longitude`.
- `accuracy`.

### Business rules

- Browser có thể hỗ trợ lấy GPS, nhưng server phải tự validate tọa độ, accuracy, OfficeLocation và radius.
- Không tin distance do JavaScript tính.
- Check-out chỉ hợp lệ khi đã check-in và chưa check-out.
- Quy tắc schedule, accuracy, working hours và overtime thuộc Feature Tree 08.

## 9. Employee Leave

### Mục đích

Employee tạo leave request và xem trạng thái leave request của chính mình.

### Routes

- GET `/employee/leaves`
- POST `/employee/leaves`

### POST parameters

- `leaveType`.
- `leaveDetail`.
- `startDate`.
- `endDate`.
- `handoverTo`.
- `handoverWork`.
- `reason`.

### Business rules

- Requester luôn lấy từ Employee context, không lấy từ browser.
- Employee không được gửi status hoặc approver.
- Leave mới có trạng thái ban đầu `Pending`.
- Validation balance, overlap, session và approval thuộc Feature Tree 08.

## 10. Employee Tasks

### Mục đích

Employee xem task được giao và cập nhật trạng thái assignment của chính mình.

### Routes

- GET `/employee/tasks`
- POST `/employee/tasks`

### POST parameters

- `taskId`.
- `status`.
- `feedback`.

### Business rules

- Task detail/update phải kiểm tra assignment ownership bằng `taskId + employeeId`.
- Không dùng route legacy không kiểm ownership.
- Task lifecycle và manager review thuộc Feature Tree 04.

## 11. Employee Contract

### Mục đích

Employee xem hợp đồng, tải document và ký hợp đồng thuộc chính mình.

### Routes

- GET `/employee/contract`
- GET `/employee/contract/document?contractId=...`
- POST `/employee/contract`

### POST parameters

- `contractId`.
- `agreeDocument`.
- `signatureData`.

### Business rules

- Contract phải thuộc Employee hiện tại.
- Chỉ contract ở trạng thái signable mới được ký.
- Document tải về phải được kiểm tra ownership.
- Quy tắc lifecycle contract thuộc Feature Tree 06.

## 12. Employee Payroll

### Mục đích

Employee xem payroll/payslip của chính mình.

### Routes

- GET `/employee/payroll`
- GET `/employee/payroll?payrollId=...`

### Business rules

- Chỉ load payroll thuộc Employee hiện tại.
- Employee-visible payroll nên giới hạn ở trạng thái được phép theo Feature Tree 07, dự kiến `Approved` hoặc `Paid`.
- Không tính lại payroll trong Employee controller hoặc JSP.

## 13. Employee Notifications

### Mục đích

Employee xem notification, mark one as read, mark all as read và đi tới target route an toàn.

### UI

- Notification bell dùng chung.

### Business rules

- Employee chỉ thấy notification có `UserID` của chính mình.
- Mark-read phải kiểm ownership notification.
- Notification event catalog thuộc Feature Tree 10.

## 14. Non-functional Requirements

- Mọi trang Employee phải có empty state rõ ràng.
- Lỗi quyền phải trả Access Denied hoặc redirect an toàn.
- Không hiển thị dữ liệu Employee khác.
- Không nhận các trường có quyền cao từ browser như EmployeeID, status, approver, salary hoặc role.

## 15. Traceability

- Main plan: `docs/superpowers/plans/feature-tree/03-employee-self-service.md`.
- Dependencies: Feature Tree 01, 04, 06, 07, 08, 10.
