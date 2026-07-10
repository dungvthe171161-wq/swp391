# Feature: HR Manager duyet payroll
Status: Partial
Actor: HR Manager
Priority: High
Related Code: `PayrollApprovalController`, `PayrollDAO`, `Views/hr/PayrollManagement.jsp`

## Route
- `GET /hr/payroll-approval`
- `GET /hr/payroll-approval?ajax=true&payrollId=...`
- `POST /hr/payroll-approval`
- `POST /hr/payroll-approval/batch-approve`
- `POST /hr/payroll-approval/batch-reject`
- JSP lien quan: `/Views/hr/PayrollManagement.jsp`.

## Luong chinh
1. HR Manager vao `/hr/payroll-approval`.
2. `PayrollApprovalController` lay danh sach payroll theo filter `status`, `employeeId`, `payPeriod`, `page`, `pageSize`.
3. HR Manager xem chi tiet bang AJAX neu can.
4. HR Manager approve hoac reject tung payroll hoac batch.
5. Neu approve, status chuyen sang `Approved` va cap nhat `ApprovedBy`, `ApprovedDate` neu DAO ho tro.
6. Neu reject, status chuyen theo rule code hien co de HR Staff sua lai.

## UI contract
- Text hien thi tieng Viet, status ky thuat giu `Pending`, `Approved`, `Rejected`, `Paid`.
- Link tu HR Home vao danh sach cho duyet: `/hr/payroll-approval?status=Pending`.
- Form action/hidden input phai giu dung route controller.

## Hien trang code
- `PayrollApprovalController` dang dung `REQUIRED_PERMISSION = "VIEW_USERS"`, can review vi ten permission khong dung nghiep vu payroll.

## Acceptance Criteria
- [ ] HR Staff khong duoc approve payroll cuoi cung.
- [ ] HR Manager duyet payroll hop le thanh cong.
- [ ] Payroll khong ton tai hoac khong dung status bi tu choi.

## Missing Work
- [ ] Dinh nghia permission rieng nhu `APPROVE_PAYROLL`.
- [ ] Doi permission hien tai tu `VIEW_USERS` sang permission payroll phu hop sau khi chot matrix.
- [ ] Them ly do reject payroll.
- [ ] Them audit log cho phe duyet payroll.
