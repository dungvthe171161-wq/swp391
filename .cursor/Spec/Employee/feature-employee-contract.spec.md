# Feature: Employee xem hop dong
Status: Planned
Actor: Employee
Priority: Medium
Source: existing `.cursor/Spec`, `src/data/data.sql`
Related Code: `ContractDAO`, `Contract`, `Views/Employee/ViewContract.jsp`

## Tinh chat spec
Day la target design/gia thiet de gen code sau. File `feature-employee-payroll-contract.spec.md` chi la ban tom tat hien trang cu.

## Muc tieu
Cho phep Employee xem hop dong lao dong cua chinh minh. Chuc nang nay bo sung cho luong payslip vi payslip phu thuoc contract/base salary.

## Route de xuat
- `GET /employee/contracts`: xem hop dong hien tai/lich su hop dong.
- `GET /employee/contracts/detail?id={contractId}`: xem chi tiet mot hop dong neu can.

## Controller/DAO/JSP de xuat
| Thanh phan | De xuat |
| --- | --- |
| Controller | `com.hrm.controller.employee.EmployeeContractController` |
| DAO | `ContractDAO` |
| Entity | `Contract` |
| JSP | `/Views/Employee/ViewContract.jsp` |
| Bang | `Contract`, `Employee` |

## Luong chinh
1. Employee vao `/employee/contracts`.
2. Controller lay `systemUser` tu session va xac dinh `EmployeeID`.
3. DAO lay contract theo employee hien tai.
4. JSP hien contract type, start date, end date, base salary, allowance, status.
5. Employee chi xem, khong sua/xoa/duyet.

## Luong loi
- Chua dang nhap: redirect `/login`.
- User khong co `EmployeeID`: hien loi khong co hop dong nhan vien.
- Contract id khong ton tai: hien loi than thien.
- Contract khong thuoc employee hien tai: AccessDenied/403.

## Hien trang code
- Da co `ContractDAO.getContractByEmployeeId(int employeeId)`.
- Da co `/Views/Employee/ViewContract.jsp`.
- Chua thay Employee contract servlet.
- `RoleAuthorizationFilter` co `/ViewContract`, nhung route nen chuyen sang `/employee/contracts`.

## Acceptance Criteria
- [ ] Employee xem duoc hop dong cua chinh minh.
- [ ] Employee khong xem duoc hop dong cua employee khac.
- [ ] Contract status hien thi dung theo enum DB.
- [ ] Employee khong co quyen sua/xoa/approve/reject contract.

## Missing Work
- [ ] Tao Employee contract controller.
- [ ] Chuan hoa JSP contract theo BetterHR theme va tieng Viet.
- [ ] Them test chong IDOR contract.
