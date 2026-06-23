# Module Spec: Employee Self Service
Status: Planned
Actor: Employee
Priority: High
Source: `G1_SE2014_SRS Document.docx.pdf`, existing `.cursor/Spec`, `src/data/data.sql`
Related Code: `HomepageController`, `ProfilepageController`, `com.hrm.controller.employee.*`, `Views/Employee/*`, `Profilepage.jsp`

## Pham vi
Employee Self Service la khu vuc nhan vien tu phuc vu trong BetterHR. Theo SRS, Employee dung he thong de cap nhat/thinh cau cap nhat thong tin ca nhan, gui request nghi phep/don tu, xem ket qua request, xem payslip, xem task duoc giao va cap nhat tien do/status task.

## Tinh chat spec
Spec nay la gia thiet/target design de sau nay gen code, khong phai cam ket rang code hien tai da co day du. Khi implement, doc nhom "canonical" lam nguon chinh; cac file "hien trang/legacy" chi dung de doi chieu code cu va tranh spec-code drift.

## Feature canonical de implement sau
- `feature-employee-profile-update-request.spec.md`
- `feature-employee-request-management.spec.md`
- `feature-employee-payslip.spec.md`
- `feature-employee-contract.spec.md`
- `feature-employee-task-workflow.spec.md`
- `feature-employee-attendance.spec.md`
- `feature-employee-notification-center.spec.md`
- `feature-employee-dashboard-summary.spec.md`

## File hien trang/legacy can doi chieu
- `feature-employee-home.spec.md`: hien trang EmployeeHome; target moi la `feature-employee-dashboard-summary.spec.md`.
- `feature-employee-profile.spec.md`: hien trang profile view; update request co spec rieng.
- `feature-employee-leave-management.spec.md`: ban cu theo `LeaveDAO`; target moi la `feature-employee-request-management.spec.md` dung `MailRequest`.
- `feature-employee-payroll-contract.spec.md`: ban tom tat cu; target moi tach thanh payslip va contract.
- `feature-employee-view-task.spec.md`: hien trang `/viewTask`; target moi la `feature-employee-task-workflow.spec.md`.

## Nguyen tac theo SRS
- Employee chi duoc xem va thao tac tren du lieu cua chinh minh.
- Employee co the gui MailRequest gom `Leave`, `Resignation`, `Petition`.
- Employee duoc xem response/ket qua approve/reject cua request.
- Employee duoc xem payslip do HR cung cap sau khi payroll da qua workflow phu hop.
- Employee duoc xem task duoc giao va cap nhat status/tien do task.
- Dept Manager la actor duyet request va task completion cua nhan vien phong ban.

## Route de xuat
Route de xuat dung prefix `/employee` de tranh xung dot voi Dept Manager:

| Feature | Route de xuat | JSP de xuat/hien co |
| --- | --- | --- |
| Employee home | `GET /employee/home` | `/Views/Employee/EmployeeHome.jsp` |
| Profile | `GET /employee/profile` | `/Views/Profilepage.jsp` hoac `/Views/Employee/EmployeeProfile.jsp` |
| Profile update request | `GET/POST /employee/profile-update-request` | JSP moi hoac form trong profile |
| Request list | `GET /employee/requests` | JSP moi |
| Create request | `GET/POST /employee/requests/create` | JSP moi hoac `LeaveManagement.jsp` sau khi refactor |
| Request detail | `GET /employee/requests/detail?id={requestId}` | JSP moi |
| Payslip list/detail | `GET /employee/payslips`, `GET /employee/payslips/detail?id={payrollId}` | `/Views/Employee/ViewPayroll.jsp` sau khi co controller |
| Contract view | `GET /employee/contracts` | `/Views/Employee/ViewContract.jsp` sau khi co controller |
| Task list/detail | `GET /employee/tasks`, `GET /employee/tasks/detail?id={taskId}` | `/Views/Employee/ViewTask.jsp` sau khi tach route |
| Update task status | `POST /employee/tasks/update-status` | task detail/list |
| Attendance | `GET /employee/attendance`, `POST /employee/attendance/check-in`, `POST /employee/attendance/check-out` | JSP moi |
| Notifications | `GET /employee/notifications` | JSP moi |

## Hien trang code
- Da co `HomepageController` nhung `employeeUrl` dang tro truc tiep `/Views/Employee/EmployeeHome.jsp`.
- Da co `ProfilepageController` route `/profilepage`, co xem profile va update truc tiep `Employee`.
- Da co `Views/Employee/EmployeeHome.jsp`, `EmployeeProfile.jsp`, `LeaveManagement.jsp`, `ViewPayroll.jsp`, `ViewContract.jsp`, `ViewTask.jsp`.
- Trong `com.hrm.controller.employee` moi thay `ViewTask.java`.
- Employee `ViewTask` va Dept `ViewTask` dang trung `@WebServlet("/viewTask")`.
- `RoleAuthorizationFilter` co pattern `/employee`, `/employee/`, `/ViewPayroll`, `/ViewContract`, `/EmployeeHome`, `/LeaveManagement`.
- `ModulePermissionFilter` dang gan `/employee` voi `VIEW_EMPLOYEE_DETAIL`.
- Chua thay servlet rieng cho request list/create/detail, payslip, contract, employee home.

## Database impact
| Feature | Read | Write |
| --- | --- | --- |
| Profile view | `SystemUser`, `Employee`, `Department`, `Role` | none |
| Profile update request | `SystemUser`, `Employee` | `MailRequest` hoac bang rieng neu sau nay tach profile change request |
| Request management | `MailRequest`, `Employee` | `MailRequest` |
| Payslip | `Payroll`, `PayrollAudit`, `Employee` | none |
| Contract | `Contract`, `Employee` | none |
| Task workflow | `Task`, `assignList`, `Employee` | `Task.Status` neu cho Employee update status |
| Attendance | `Attendance`, `Employee` | `Attendance` neu kich hoat check-in/check-out |
| Notification | `SystemLog` hoac `Notification` neu them bang moi | `Notification.IsRead` neu co bang notification |

## Trang thai nghiep vu
- `MailRequest.Status`: `Pending`, `Approved`, `Rejected`.
- `MailRequest.RequestType`: `Leave`, `Resignation`, `Petition`.
- `Task.Status`: `Waiting`, `In Progress`, `Completed`, `Rejected`.
- `Payroll.Status`: `Draft`, `Pending`, `Approved`, `Rejected`, `Paid`; Employee nen chi xem ky luong da duoc cong bo theo rule project.
- `Contract.Status`: `Draft`, `Pending_Approval`, `Approved`, `Rejected`, `Active`, `Expired`; Employee nen chi xem contract cua minh.

## Phan quyen
- Employee role ID hien tai: `5`.
- Employee route nen dung prefix `/employee/*`.
- Employee khong duoc phu thuoc permission `VIEW_DEPARTMENTS` de xem task.
- Employee khong duoc truyen `employeeId` tu request body/query de xem du lieu nguoi khac; employeeId phai suy ra tu session `systemUser.EmployeeID`.

## Out of Scope
- Khong them schema moi neu chua co yeu cau migrate.
- Khong doi password hashing trong module nay.
- Khong gop flow duyet request/task vao Employee; approval thuoc Dept Manager/HR theo spec rieng.
- Khong cho Employee tu sua department, role, status, base salary, contract, payroll.

## Acceptance Criteria
- [ ] Employee vao khu vuc `/employee/*` phai co session `systemUser`.
- [ ] Employee chi xem duoc profile, request, payslip, contract, task cua chinh minh.
- [ ] Moi feature co route/controller/JSP ro rang, khong link truc tiep vao JSP neu can nap data.
- [ ] Khong con route Employee task dung chung `/viewTask` voi Dept Manager khi implement moi.
- [ ] UI hien thi tieng Viet, giu brand `BetterHR`.
- [ ] Loi validation hien message than thien, khong lo stack trace.

## Missing Work
- [ ] Tao Employee home controller thay vi link truc tiep JSP.
- [ ] Tach route Employee task sang `/employee/tasks`.
- [ ] Tao request controller/JSP cho list/create/detail.
- [ ] Tao payslip controller/JSP read-only theo employee session.
- [ ] Tao contract controller/JSP read-only theo employee session.
- [ ] Tao attendance controller/JSP neu kich hoat cham cong cho Employee.
- [ ] Tao notification center neu can thong bao trong app.
- [ ] Cap nhat permission/filter khi route moi duoc implement.
- [ ] Them test chong truy cap du lieu employee khac.
