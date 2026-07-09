# Đặc tả module Employee: Cổng nhân viên

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Employee xem và thao tác dữ liệu cá nhân trong BetterHR.

## Route, controller và JSP liên quan
- `/employee`, `/employee/*`.
- Controller chính: `EmployeePortalController`.
- JSP: `Views/Employee/EmployeeHome.jsp`, `Tasks.jsp`, `Leaves.jsp`, `Payroll.jsp`, `Contract.jsp`, `Attendance.jsp`, `EmployeeProfile.jsp`.

## Hiện trạng code
- Employee portal mới gom route trong `EmployeePortalController`.
- Các tab chính gồm dashboard, task, leave, payroll, contract, attendance và profile.
- Task update dùng DAO lấy task theo employee để giới hạn phạm vi.
- Servlet legacy `employee.ViewTask` vẫn mapping `/viewTask` và bị trùng với Dept.

## Quy tắc nghiệp vụ chuẩn
- Employee chỉ xem dữ liệu của chính mình.
- Tạo leave phải validate ngày, số buổi nghỉ phép có lương và overlap.
- Payroll/contract chỉ đọc, không tự sửa dữ liệu đã duyệt.

## Ma trận route, quyền và dữ liệu cần bổ sung
| Nhóm chức năng | Route/controller | Permission chuẩn cần có | Bảng dữ liệu chính | Ghi chú |
|---|---|---|---|---|
| Dashboard cá nhân | `/employee`, `EmployeePortalController` | `VIEW_DASHBOARD` hoặc `VIEW_EMPLOYEE_DETAIL` | `Employee`, `Task`, `LeaveRequest`, `Payroll`, `Contract` | Dữ liệu theo employee hiện tại. |
| Task được giao | `/employee/tasks` | `VIEW_EMPLOYEE_TASKS` hoặc `VIEW_EMPLOYEE_DETAIL` | `Task` | Không dùng route legacy `/viewTask`. |
| Nghỉ phép | `/employee/leaves` | `VIEW_LEAVES`, `CREATE_LEAVE` | `LeaveRequest`, `Employee` | Tạo mới và xem lịch sử của chính mình. |
| Payroll | `/employee/payroll` | `VIEW_PAYROLLS` hoặc quyền xem payroll cá nhân | `Payroll` | Chỉ đọc bản ghi của chính mình. |
| Hợp đồng | `/employee/contract` | `VIEW_CONTRACTS` | `Contract` | Chỉ đọc hợp đồng của chính mình. |
| Chấm công | `/employee/attendance` | `VIEW_ATTENDANCE` hoặc quyền xem attendance cá nhân | `Attendance` | Cần làm rõ chỉ xem hay có check-in/check-out. |
| Hồ sơ cá nhân | `/employee/profile` | `VIEW_EMPLOYEE_DETAIL`, `UPDATE_OWN_PROFILE` nếu cho sửa | `Employee`, `SystemUser` | Chỉ sửa trường được phép. |

## Ownership rule bắt buộc
- Mọi route `/employee/*` phải lấy `EmployeeID` từ `systemUser`, không tin `employeeId` từ request nếu có.
- Employee không được xem payroll, contract, task, leave, attendance hoặc profile của người khác bằng cách sửa ID.
- Nếu `systemUser.EmployeeID` null, controller phải redirect hoặc hiển thị lỗi cấu hình tài khoản.
- Các action POST phải kiểm tra ownership lại ở DAO/service, không chỉ kiểm tra ở JSP.

## Workflow Employee cần bổ sung
- Leave: Employee được tạo đơn `Pending`; chỉ được sửa/hủy khi đơn còn `Pending` nếu nghiệp vụ cho phép.
- Leave có lương phải kiểm tra số buổi còn lại, ngày bắt đầu/kết thúc và overlap với đơn đang mở.
- Task: Employee chỉ cập nhật trạng thái task được giao; trạng thái phải nằm trong enum task hiện có.
- Payroll/contract: Employee chỉ xem bản ghi đã được phép công bố, không chỉnh sửa.
- Attendance: cần chốt rõ spec là chỉ xem dữ liệu hay cho phép check-in/check-out.

## Notification và audit bắt buộc
- Tạo leave gửi notification cho Dept Manager hoặc người duyệt đúng phòng ban.
- Leave được duyệt/từ chối gửi notification cho Employee.
- Employee cập nhật task quan trọng gửi notification cho Dept Manager.
- Sửa hồ sơ cá nhân nếu được phép phải ghi audit tối thiểu các trường thay đổi.

## Checklist nghiệm thu riêng cho Employee
- Employee A không xem được dữ liệu Employee B bằng cách sửa URL hoặc request body.
- Employee không truy cập được route Dept `/viewTask` legacy sau khi route được tách.
- Tạo leave overlap hoặc vượt số phép bị chặn.
- Payroll/contract không hiển thị dữ liệu chưa được phép công bố.
- Tài khoản Employee chưa gắn `EmployeeID` được xử lý rõ, không lỗi trắng trang.

## Code còn lệch spec hoặc cần bổ sung
- Không dùng route legacy `/viewTask` cho employee; dùng `/employee/tasks`.
- Cần test tất cả route `/employee/*` với user khác role.
- Cần đảm bảo notification leave gửi đúng Dept Manager.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.
