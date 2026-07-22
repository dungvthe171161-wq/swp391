# Đặc tả module Employee: Cổng nhân viên

Trạng thái: Đã rà soát theo code ngày 2026-07-22.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Employee xem và thao tác dữ liệu cá nhân trong BetterHR.

## Route, controller và JSP liên quan
- `/employee`, `/employee/*`, `/employee/tasks`, `/employee/view-task`, `/employee/schedule`, `/employee/contract/document`.
- Controller chính: `EmployeePortalController`, servlet detail `EmployeeViewTask`.
- JSP: `Views/Employee/EmployeeHome.jsp`, `Tasks.jsp`, `ViewTask.jsp`, `Leaves.jsp`, `Payroll.jsp`, `Contract.jsp`, `Attendance.jsp`, `Schedule.jsp`, `EmployeeProfile.jsp`.

## Hiện trạng code
- Employee portal gom dashboard, profile, attendance, schedule, leaves, payroll, contract và tasks trong `EmployeePortalController`.
- Task list/update nhanh dùng `/employee/tasks` trong `EmployeePortalController`.
- Task detail dùng `EmployeeViewTask` tại `/employee/view-task` với ownership guard theo `systemUser.EmployeeID`.
- Schedule dùng `/employee/schedule`, lấy `todaySchedule` và `monthlySchedules` theo employee hiện tại.
- Contract cho phép xem document qua `/employee/contract/document` và ký bằng `POST /employee/contract` khi contract ở `Pending_Signature` hoặc `Approved`.
- Tạo leave gửi notification cho Dept Manager; cập nhật task gửi notification cho Dept Manager.

## Quy tắc nghiệp vụ chuẩn
- Employee chỉ xem dữ liệu của chính mình.
- Tạo leave phải validate ngày, số buổi nghỉ phép có lương và overlap.
- Payroll chỉ đọc; contract chỉ được ký theo trạng thái hợp lệ, không được tự sửa nội dung hoặc dữ liệu đã duyệt.
- Schedule chỉ đọc ở cổng Employee; phân công/sửa lịch thuộc actor Dept Manager/Admin.

## Ma trận route, quyền và dữ liệu
| Nhóm chức năng | Route/controller | Permission chuẩn cần có | Bảng dữ liệu chính | Ghi chú |
|---|---|---|---|---|
| Dashboard cá nhân | `/employee`, `EmployeePortalController` | `VIEW_DASHBOARD` hoặc `VIEW_EMPLOYEE_DETAIL` | `Employee`, `Task`, `LeaveRequest`, `Payroll`, `Contract` | Dữ liệu theo employee hiện tại. |
| Task được giao | `/employee/tasks`, `/employee/view-task` | `VIEW_EMPLOYEE_DETAIL` hiện tại; task riêng nếu bổ sung seed/migration | `Task` | `EmployeePortalController` xử lý list/update nhanh; `EmployeeViewTask` xử lý chi tiết. |
| Nghỉ phép | `/employee/leaves` | `VIEW_LEAVES`, `CREATE_LEAVE` | `MailRequest`, `Employee` | Tạo mới và xem lịch sử của chính mình. |
| Payroll | `/employee/payroll` | `VIEW_PAYROLLS` hoặc quyền xem payroll cá nhân | `Payroll` | Chỉ đọc bản ghi của chính mình; có guard khi xem chi tiết theo `payrollId`. |
| Hợp đồng | `/employee/contract`, `/employee/contract/document` | `VIEW_CONTRACTS`, quyền ký hợp đồng cá nhân nếu tách riêng | `Contract`, `ContractDocument` | Xem document và ký khi status cho phép. |
| Chấm công | `/employee/attendance` | quyền attendance riêng nếu bổ sung seed/migration; hiện route dùng `VIEW_EMPLOYEE_DETAIL` | `Attendance`, `OfficeLocation`, `EmployeeWorkSchedule` | Có check-in/check-out GPS. |
| Lịch làm việc | `/employee/schedule` | `VIEW_OWN_WORK_SCHEDULE` hoặc quyền xem lịch cá nhân | `WorkSchedule`, `EmployeeWorkSchedule` | Chỉ đọc lịch hôm nay và lịch theo tháng. |
| Hồ sơ cá nhân | `/employee/profile`, `/profilepage` | `VIEW_EMPLOYEE_DETAIL`; quyền tự sửa profile riêng nếu bổ sung seed/migration | `Employee`, `SystemUser`, `SystemLog` | `/profilepage` là profile chung; `/employee/profile` là view trong portal Employee. |

## Code còn lệch spec hoặc cần bổ sung
- Cần test tất cả route `/employee/*` với user khác role.
- Cần tách permission riêng cho lịch cá nhân, payroll cá nhân, contract cá nhân và ký hợp đồng nếu muốn phân quyền chi tiết.
- Cần bảo vệ `/Upload/signatures/*` hoặc chuyển file chữ ký ra endpoint có ownership guard.
- Cần test notification leave và task gửi đúng Dept Manager, kể cả trường hợp employee chưa gắn department hoặc manager không có active user.
- Cần audit khi employee sửa hồ sơ cá nhân và ký hợp đồng.

## Kiểm thử tối thiểu
- Employee A không xem được dữ liệu Employee B bằng cách sửa URL hoặc request body.
- Employee không truy cập được route Dept `/dept/*` và `/viewTask`.
- Tạo leave overlap hoặc vượt số phép bị chặn.
- Mở `/employee/schedule`, `/employee/payroll`, `/employee/contract` với dữ liệu trống không lỗi 500.
- Sửa `payrollId`, `contractId` hoặc `taskId` sang bản ghi của employee khác phải bị chặn.
