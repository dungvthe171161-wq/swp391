# Cross-cutting Spec: Database Impact Map
Status: Approved
Priority: Medium
Schema Source: `src/data/data.sql`

## Muc tieu
Lap ban do bang du lieu bi doc/ghi theo module de khi implement khong tac dong sai bang. Spec nay dung ten bang va status theo `src/data/data.sql`.

## Bang chinh trong data.sql
| Nhom | Bang |
| --- | --- |
| Core | `Role`, `Department`, `Employee`, `SystemUser`, `SystemLog` |
| HR | `Contract`, `Recruitment`, `Guest`, `MailRequest` |
| Task | `Task`, `assignList` |
| Payroll | `Payroll`, `PayrollAudit`, `Attendance`, `AllowanceType`, `EmployeeAllowance`, `DeductionType`, `EmployeeDeduction`, `InsuranceRate`, `TaxRate`, `Dependent` |
| Permission | `Permission`, `RolePermission`, `UserPermission` |

## Auth
| Feature | Read | Write |
| --- | --- | --- |
| Register local | `SystemUser`, `Role` | `SystemUser` voi `RoleID=Guest`, `EmployeeID=NULL`; email qua SMTP neu cau hinh |
| Login | `SystemUser`, `Role`, `RolePermission`, `UserPermission` neu check permission | HTTP session, cookie, `SystemUser.LastLogin` neu implement |
| Logout | HTTP session, cookie | HTTP session, cookie |
| Change password | `SystemUser` | `SystemUser.PasswordHash` |
| Forgot password | `SystemUser.Email` | recovery state/session: `recoveryEmail`, `pinCode`, `recoveryVerified` |

## Admin
| Feature | Read | Write |
| --- | --- | --- |
| Dashboard | `Employee`, `Department`, `SystemUser`, `SystemLog`, payroll/recruitment stats neu DAO dung | none |
| User management | `SystemUser`, `Role`, `Employee`, `Department` | `SystemUser` |
| Role management | `Role` | `Role` |
| Role permission | `Role`, `Permission`, `RolePermission` | `RolePermission` |
| User permission override | `SystemUser`, `Permission`, `UserPermission` | `UserPermission` |
| Department | `Department`, `Employee` | `Department` |
| Audit log | `SystemLog` | none |

## Dept Manager
| Feature | Read | Write |
| --- | --- | --- |
| Dashboard | `Department`, `Employee`, `Task`, `assignList` | none |
| Create task | `Employee`, `Department` | `Task`, `assignList` |
| Task list | `Task`, `assignList`, `Employee` | none |
| Update task | `Task`, `assignList` | `Task`, `assignList` |

## Employee
| Feature | Read | Write |
| --- | --- | --- |
| View task | `Task`, `assignList`, `Employee` | none |
| Profile | `Employee`, `SystemUser`, `Department`, `Role` | `Employee`/`SystemUser` neu cho sua |
| Payroll view | `Payroll`, `PayrollAudit` neu can chi tiet | none |
| Contract view | `Contract` | none |
| Leave/mail request | `MailRequest` | `MailRequest` |

## HR Staff
| Feature | Read | Write |
| --- | --- | --- |
| Recruitment post | `Recruitment` | `Recruitment` |
| Candidate | `Guest`, `Recruitment` | `Guest.Status` |
| Contract | `Contract`, `Employee` | `Contract` |
| Payroll list/details | `Payroll`, `PayrollAudit`, `Employee`, `Contract`, `Attendance`, `EmployeeAllowance`, `EmployeeDeduction` | none |
| Payroll generate | `Employee`, `Contract`, `Attendance`, `MailRequest`, `EmployeeAllowance`, `EmployeeDeduction`, `InsuranceRate`, `TaxRate`, `Dependent` | `Payroll`, `PayrollAudit`, `EmployeeDeduction` |
| Payroll submit/delete | `Payroll` | `Payroll.Status`, `Payroll` |
| Allowance/Deduction | `EmployeeAllowance`, `EmployeeDeduction`, `AllowanceType`, `DeductionType`, `Payroll` | `EmployeeAllowance`, `EmployeeDeduction` |

## HR Manager
| Feature | Read | Write |
| --- | --- | --- |
| HR Home | `Employee`, `Department`, `Payroll` neu section payroll, `Contract` neu hien pending contract count | none neu chi dashboard |
| Recruitment review | `Recruitment` | `Recruitment.Status` |
| CV review | `Guest`, `Recruitment` | `Guest.Status` neu co |
| Employee management | `Employee`, `Department`, `Role`, `SystemUser` | `Employee`, `SystemUser` |
| Contract approval | `Contract`, `Employee` | `Contract.Status` |
| Payroll approval | `Payroll`, `PayrollAudit`, `Employee` | `Payroll.Status`, `Payroll.ApprovedBy`, `Payroll.ApprovedDate` |

## Guest Candidate
| Feature | Read | Write |
| --- | --- | --- |
| Job list | `Recruitment` | none |
| Apply job | `Recruitment` | `Guest`, uploaded CV path in `Guest.CV` |

## Luu y quan trong
- SQL dung ten bang PascalCase, vi du `SystemUser`, khong phai `system_user`.
- Bang giao task la `assignList`, khong phai `task_assignment`.
- Bang nghi phep/yeu cau la `MailRequest`, khong phai `Leave`.
- Ung vien public nam trong `Guest`, khong co bang `Candidate` rieng.
- Payroll generate ghi ca `Payroll` va `PayrollAudit`.

## Acceptance Criteria
- [ ] Moi feature spec chinh tham chieu dung ten bang trong `data.sql`.
- [ ] Mutating feature phai co validation va audit log neu nhay cam.
- [ ] Neu doi schema, phai cap nhat lai spec nay cung luc.
