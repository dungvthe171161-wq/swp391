# Feature: Employee dashboard summary
Status: Planned
Actor: Employee
Priority: Medium
Source: existing `.cursor/Spec`, `G1_SE2014_SRS Document.docx.pdf`
Related Code: `HomepageController`, `Views/Employee/EmployeeHome.jsp`, `MailRequestDAO`, `PayrollDAO`, `ContractDAO`, `AttendanceDAO`

## Muc tieu
Bien Employee Home thanh man hinh tong quan co du lieu that thay vi chi la menu tinh. Dashboard giup Employee nhanh chong thay task can lam, request dang cho duyet, payslip moi, attendance hom nay va thong bao gan day.

## Route de xuat
- `GET /employee/home`

## Controller/DAO/JSP de xuat
| Thanh phan | De xuat |
| --- | --- |
| Controller | `com.hrm.controller.employee.EmployeeHomeController` |
| JSP | `/Views/Employee/EmployeeHome.jsp` |
| DAO | `MailRequestDAO`, `PayrollDAO`, `ContractDAO`, `AttendanceDAO`, `TaskDAO` hoac `DAO` hien co |
| Bang | `Employee`, `Task`, `assignList`, `MailRequest`, `Payroll`, `Contract`, `Attendance` |

## Widget de xuat
- Task dang lam/cho xu ly.
- Request `Pending`.
- Payslip moi nhat da duoc cong bo.
- Contract hien tai.
- Attendance hom nay.
- Notification chua doc neu notification center duoc implement.

## Luong chinh
1. Employee dang nhap vao `/employee/home`.
2. Controller lay `systemUser` tu session.
3. Controller xac dinh `EmployeeID`.
4. Controller lay cac summary count/list ngan theo EmployeeID.
5. Forward sang `/Views/Employee/EmployeeHome.jsp`.
6. JSP hien cac card va quick links den module con.

## Quick links
- Ho so: `/employee/profile`
- Request: `/employee/requests`
- Payslip: `/employee/payslips`
- Hop dong: `/employee/contracts`
- Task: `/employee/tasks`
- Cham cong: `/employee/attendance`
- Thong bao: `/employee/notifications`

## Luong loi
- Chua dang nhap: redirect `/login`.
- User khong co `EmployeeID`: hien empty state huong dan lien he HR.
- DAO loi: hien empty state an toan, log loi server.

## Hien trang code
- `HomepageController` dang set `employeeUrl` tro truc tiep `/Views/Employee/EmployeeHome.jsp`.
- `EmployeeHome.jsp` hien tai chu yeu la UI/menu, co link `/viewTask` chua dung route Employee de xuat.
- Chua co `EmployeeHomeController` rieng.

## Out of Scope
- Khong tinh toan payroll trong dashboard.
- Khong approve request/task trong dashboard.
- Khong them chart nang neu chua co data that.

## Acceptance Criteria
- [ ] Employee vao `/employee/home` thay dashboard co du lieu cua chinh minh.
- [ ] Dashboard khong doc/hien du lieu cua employee khac.
- [ ] Cac quick link tro dung route `/employee/*`.
- [ ] Neu chua co data, hien empty state tieng Viet.
- [ ] Khong link truc tiep JSP neu controller can nap du lieu.
- [ ] UI theo BetterHR theme va tieng Viet.

## Missing Work
- [ ] Tao Employee home controller.
- [ ] Cap nhat `HomepageController.employeeUrl` sang `/employee/home`.
- [ ] Cap nhat link `/viewTask` trong EmployeeHome sang `/employee/tasks`.
- [ ] Them DAO summary method neu can.
