# Module Spec: HR Staff Operations
Status: Approved
Actor: HR Staff
Priority: High
Related Code: `com.hrm.controller.hrstaff.*`, `Views/HrStaff/*`

## Pham vi
HR Staff van hanh nghiep vu nhan su hang ngay: dashboard, dang tin tuyen dung, xem ung vien, tao hop dong, quan ly payroll, phu cap va khau tru.

## Feature files
- `feature-hrstaff-dashboard.spec.md`
- `feature-hrstaff-recruitment-post.spec.md`
- `feature-hrstaff-candidate-management.spec.md`
- `feature-hrstaff-contract-management.spec.md`
- `feature-hrstaff-payroll-management.spec.md`
- `feature-hrstaff-payroll-adjustment-api.spec.md`

## Route chinh
- `/hrstaff`
- `/postRecruitments`
- `/detailRecruitmentCreate`
- `/candidates`
- `/viewCV`
- `/hrstaff/contracts`
- `/hrstaff/contracts/create`
- `/hrstaff/payroll`
- `/hrstaff/payroll/calculate`
- `/hrstaff/payroll/allowance`
- `/hrstaff/payroll/deduction`
- `/api/payroll`
- `/api/allowance/*`
- `/api/deduction/*`

## Phan quyen hien co
- `RoleAuthorizationFilter` cho role 4 vao `/hrstaff`, `/Views/HrStaff`, payroll, contract va mot so recruitment route.
- Mot so controller payroll dung `PermissionUtil.ensureRolePermission` hoac `ensureRolePermissionJson`.

## Canh bao code
- Khong nen ghi spec cho phep `printStackTrace`/`System.err` trong production; neu code con co thi day la missing work.
- Duyet payroll cuoi cung khong thuoc HR Staff, nam o module HR Manager/HR.
