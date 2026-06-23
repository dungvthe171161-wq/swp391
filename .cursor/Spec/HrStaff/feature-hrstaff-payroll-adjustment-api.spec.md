# Feature: HR Staff quan ly phu cap va khau tru
Status: Approved
Actor: HR Staff
Priority: High
Related Code: `PayrollAllowanceController`, `PayrollDeductionController`, `PayrollAllowanceApiController`, `PayrollDeductionApiController`

## Route HTML
- `/hrstaff/payroll/allowance`
- `/hrstaff/payroll/allowance/delete`
- `/hrstaff/payroll/deduction`
- `/hrstaff/payroll/deduction/delete`

## Route API
- `/api/allowance/*`
- `/api/deduction/*`

## Luong phu cap
1. HR Staff them/sua phu cap cua employee trong ky payroll.
2. Controller kiem tra payroll status.
3. Neu status bi khoa, redirect ve payroll tab allowance kem error.
4. Neu hop le, luu va redirect ve tab allowance.

## Luong khau tru
1. HR Staff them/sua khau tru cua employee.
2. Controller kiem tra payroll status.
3. Neu hop le, luu va redirect ve tab deduction.

## Luong API
1. Frontend goi `/api/allowance/*` hoac `/api/deduction/*`.
2. Controller tra JSON.
3. Neu thieu quyen payroll, tra JSON 403.

## Acceptance Criteria
- [ ] Khong cho sua/xoa adjustment khi payroll da khoa theo rule code.
- [ ] API tra `application/json` va UTF-8.
- [ ] Thieu id khi xoa thi redirect/tra loi loi ro rang.
- [ ] Role khac khong goi duoc API payroll adjustment.

## Missing Work
- [ ] Chuan hoa tat ca loi API ve JSON status code thong nhat.
- [ ] Them audit log cho adjustment.
