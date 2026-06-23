# Feature: Employee xem payslip
Status: Planned
Actor: Employee
Priority: High
Source: `G1_SE2014_SRS Document.docx.pdf`, `src/data/data.sql`
Related Code: `PayrollDAO`, `Payroll`, `Views/Employee/ViewPayroll.jsp`

## Tinh chat spec
Day la target design/gia thiet de gen code sau. File `feature-employee-payroll-contract.spec.md` chi la ban tom tat hien trang cu.

## Muc tieu
Employee xem payslip online cua chinh minh. Payslip lay tu payroll do HR tao/tinh va HR Manager approve theo workflow payroll.

## Route de xuat
- `GET /employee/payslips`: danh sach ky luong.
- `GET /employee/payslips/detail?id={payrollId}`: chi tiet payslip.

## Controller/DAO/JSP de xuat
| Thanh phan | De xuat |
| --- | --- |
| Controller | `com.hrm.controller.employee.EmployeePayslipController` |
| DAO | `PayrollDAO` |
| JSP | `/Views/Employee/ViewPayroll.jsp` hoac JSP moi |
| Bang | `Payroll`, `PayrollAudit`, `Employee` |

## Luong chinh
1. Employee vao `/employee/payslips`.
2. Controller lay `EmployeeID` tu session `systemUser`.
3. DAO lay payroll theo `EmployeeID`.
4. Employee chon mot ky luong.
5. Controller kiem tra payroll do thuoc Employee hien tai.
6. JSP hien base salary, allowance, bonus, deduction, net salary, pay period, status.

## Rule hien thi
Mac dinh Employee chi nen xem payroll da cong bo:
- `Approved`
- `Paid`

Khong hien `Draft` cho Employee neu chua co quyet dinh khac.

## Luong loi
- Chua dang nhap: redirect `/login`.
- User khong co `EmployeeID`: hien loi khong co ho so nhan vien.
- Payroll khong thuoc Employee hien tai: AccessDenied/403.
- Payroll chua duoc cong bo: khong hien chi tiet.

## Acceptance Criteria
- [ ] Employee xem duoc payslip cua chinh minh.
- [ ] Employee khong xem duoc payslip cua employee khac.
- [ ] Payroll `Draft` khong hien cho Employee theo mac dinh.
- [ ] Chi tiet payslip hien dung cac thanh phan luong.
- [ ] Loi khong lo stack trace.

## Missing Work
- [ ] Tao Employee payslip controller.
- [ ] Chuan hoa JSP payslip theo BetterHR theme va tieng Viet.
- [ ] Them test chong IDOR payroll.
