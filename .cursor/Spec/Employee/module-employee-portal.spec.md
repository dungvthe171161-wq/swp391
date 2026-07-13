# Đặc tả module Employee: Cổng nhân viên

Trạng thái: Đã rà soát theo code ngày 2026-07-13.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Employee xem và thao tác dữ liệu cá nhân trong BetterHR.

## Route, controller và JSP liên quan
- `/employee`, `/employee/*`, `/employee/tasks/detail`.
- Controller chính: `EmployeePortalController`, `EmployeeViewTask`.
- JSP: `Views/Employee/EmployeeHome.jsp`, `Tasks.jsp`, `Leaves.jsp`, `Payroll.jsp`, `Contract.jsp`, `Attendance.jsp`, `EmployeeProfile.jsp`.

## Hiện trạng code
- Employee portal gom route trong `EmployeePortalController`.
- Các tab chính gồm dashboard, task, leave, payroll, contract, attendance và profile.
- Task detail dùng `EmployeeViewTask` tại `/employee/tasks/detail` với ownership guard theo `systemUser.EmployeeID`.
- Task list/update list dùng DAO lấy task theo employee để giới hạn phạm vi.

## Quy tắc nghiệp vụ chuẩn
- Employee chỉ xem dữ liệu của chính mình.
- Tạo leave phải validate ngày, số buổi nghỉ phép có lương và overlap.
- Payroll/contract chỉ đọc, không tự sửa dữ liệu đã duyệt.

## Ma trận route, quyền và dữ liệu
| Nhóm chức năng | Route/controller | Permission chuẩn cần có | Bảng dữ liệu chính | Ghi chú |
|---|---|---|---|---|
| Dashboard cá nhân | `/employee`, `EmployeePortalController` | `VIEW_DASHBOARD` hoặc `VIEW_EMPLOYEE_DETAIL` | `Employee`, `Task`, `LeaveRequest`, `Payroll`, `Contract` | Dữ liệu theo employee hiện tại. |
| Task được giao | `/employee/tasks`, `/employee/tasks/detail` | `VIEW_EMPLOYEE_TASKS` hoặc `VIEW_EMPLOYEE_DETAIL` | `Task` | Dùng `EmployeeViewTask` cho chi tiết. |
| Nghỉ phép | `/employee/leaves` | `VIEW_LEAVES`, `CREATE_LEAVE` | `LeaveRequest`, `Employee` | Tạo mới và xem lịch sử của chính mình. |
| Payroll | `/employee/payroll` | `VIEW_PAYROLLS` hoặc quyền xem payroll cá nhân | `Payroll` | Chỉ đọc bản ghi của chính mình. |
| Hợp đồng | `/employee/contract` | `VIEW_CONTRACTS` | `Contract` | Chỉ đọc hợp đồng của chính mình. |
| Chấm công | `/employee/attendance` | `VIEW_ATTENDANCE` hoặc quyền xem attendance cá nhân | `Attendance` | Cần làm rõ chỉ xem hay có check-in/check-out. |
| Hồ sơ cá nhân | `/employee/profile` | `VIEW_EMPLOYEE_DETAIL`, `UPDATE_OWN_PROFILE` nếu cho sửa | `Employee`, `SystemUser` | Chỉ sửa trường được phép. |

## Code còn lệch spec hoặc cần bổ sung
- Cần test tất cả route `/employee/*` với user khác role.
- Cần đảm bảo notification leave gửi đúng Dept Manager.
- Cần audit khi employee sửa hồ sơ cá nhân nếu được phép.

## Kiểm thử tối thiểu
- Employee A không xem được dữ liệu Employee B bằng cách sửa URL hoặc request body.
- Employee không truy cập được route Dept `/dept/*` và `/dept/tasks/detail`.
- Tạo leave overlap hoặc vượt số phép bị chặn.
