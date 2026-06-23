# Feature: HR Staff quan ly bang luong
Status: Approved
Actor: HR Staff
Priority: High
Related Code: `PayrollManagementController`, `PayrollCalculateController`, `PayrollDAO`, `Views/HrStaff/PayrollManagement.jsp`

## Route
- `GET /hrstaff/payroll`
- `POST /hrstaff/payroll/delete`
- `POST /hrstaff/payroll/submit`
- `POST /hrstaff/payroll/batch-delete`
- `POST /hrstaff/payroll/batch-submit`
- `POST /hrstaff/payroll/generate-all`
- `GET /hrstaff/payroll/details`
- `GET /api/payroll`
- `GET /hrstaff/payroll/calculate`

## Luong danh sach payroll
1. HR Staff vao `/hrstaff/payroll`.
2. Controller kiem tra role/permission payroll.
3. Lay danh sach payroll, allowance, deduction, tax/insurance rate neu can.
4. Forward den `/Views/HrStaff/PayrollManagement.jsp`.

## Luong tinh payroll
1. HR Staff goi `/hrstaff/payroll/calculate`.
2. Controller tinh ngay cong thuc te tu attendance.
3. Tra JSON.

## Luong generate all
1. HR Staff goi `/hrstaff/payroll/generate-all`.
2. Controller goi DAO/stored procedure tao payroll hang loat.
3. Redirect ve payroll kem thong bao.

## Luong submit/delete/batch
1. HR Staff submit mot payroll hoac nhieu payroll.
2. Controller validate id list khong rong.
3. Xu ly va redirect ve tab payroll.

## Acceptance Criteria
- [ ] HR Staff xem duoc payroll management.
- [ ] `/hrstaff/payroll/calculate` tra JSON UTF-8.
- [ ] Batch submit/delete voi danh sach rong bi tu choi.
- [ ] Generate all khong lam crash khi procedure loi.

## Missing Work
- [ ] Chuan hoa status payroll: Draft, Pending, Approved, Paid.
- [ ] Duyet payroll cuoi cung de rieng cho HR Manager.
- [ ] Them audit log cho submit/delete/generate.
