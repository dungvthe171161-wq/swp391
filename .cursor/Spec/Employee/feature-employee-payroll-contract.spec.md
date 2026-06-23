# Feature: Nhan vien xem luong va hop dong
Status: Partial
Actor: Employee
Priority: Medium
Related Code: `Views/Employee/ViewPayroll.jsp`, `Views/Employee/ViewContract.jsp`, `RoleAuthorizationFilter`

## Luu y pham vi
File nay la ban tom tat hien trang payroll/contract. Khi gen code moi, uu tien:
- `feature-employee-payslip.spec.md` cho payroll/payslip.
- `feature-employee-contract.spec.md` cho contract.


## Route/JSP hien co
- `/Views/Employee/ViewPayroll.jsp`
- `/Views/Employee/ViewContract.jsp`
- Role filter co pattern `/ViewPayroll`, `/ViewContract`, `/Views/Employee/`.

## Luong xem bang luong
1. Employee mo man hinh bang luong.
2. He thong xac dinh employee tu session.
3. Chi lay payroll cua employee hien tai.
4. Hien thi bang luong.

## Luong xem hop dong
1. Employee mo man hinh hop dong.
2. He thong xac dinh employee tu session.
3. Chi lay contract cua employee hien tai.
4. Hien thi hop dong.

## Hien trang code
- Da co JSP employee payroll/contract.
- Can kiem tra controller rieng neu sau nay tach luong/hop dong qua servlet.

## Acceptance Criteria
- [ ] Employee chi xem duoc payroll cua minh.
- [ ] Employee chi xem duoc contract cua minh.
- [ ] Chua login bi chuyen ve login.

## Missing Work
- [ ] Tao/kiem tra controller backend cho ViewPayroll va ViewContract neu chua co.
- [ ] Them test chong truy cap du lieu employee khac.

- [ ] Khi implement moi, doi route sang `/employee/payslips` va `/employee/contracts`.

