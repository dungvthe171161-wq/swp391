# Cross-cutting Spec: Permission Matrix
Status: Approved
Priority: High
Related Code: `RoleAuthorizationFilter`, `ModulePermissionFilter`, `AdminAuthorizationFilter`, `PermissionUtil`

## Muc tieu
Chuan hoa bang phan quyen cho toan bo HRMS de moi actor chi truy cap dung route va permission cua minh.

## Role hien co theo code
| Role ID | Actor | Khu vuc chinh |
| --- | --- | --- |
| 1 | Admin | `/admin`, `/admin/*`, `/departments`, `/Admin/*` |
| 2 | HR Manager | `/HrHomeController`, `/hr/*`, `/Views/hr/*`, `/viewRecruitment`, `/viewCV`, `/detailWaitingRecruitment` |
| 3 | Dept Manager | `/dept`, `/taskManager`, `/postTask`, `/viewTask` |
| 4 | HR Staff | `/hrstaff`, `/Views/HrStaff/*`, `/postRecruitments`, `/candidates`, `/viewCV`, payroll, contracts |
| 5 | Employee | `/Views/Employee/*`, `/employee/*`, employee home/profile/payroll/contract/leave |

## Permission hien co theo filter
| Route | Permission | Filter |
| --- | --- | --- |
| `/admin` | `MANAGE_SYSTEM` | `ModulePermissionFilter` |
| `/admin/users` | `VIEW_USERS` | `ModulePermissionFilter` |
| `/admin/role/*` | `VIEW_ROLES` | `ModulePermissionFilter` |
| `/admin/role-permissions/api` | `MANAGE_ROLE_PERMISSIONS` | `ModulePermissionFilter` |
| `/dept`, `/taskManager`, `/postTask`, `/viewTask` | `VIEW_DEPARTMENTS` | `ModulePermissionFilter` |
| `/employee`, `/employee/*` | `VIEW_EMPLOYEE_DETAIL` | `ModulePermissionFilter` |
| `/viewRecruitment` | `VIEW_RECRUITMENT` | `ViewRecruitment` + filters |
| `/HrHomeController` | Role 2 | `RoleAuthorizationFilter` |
| `/hr/employee-list`, `/hr/create-employee` | `VIEW_EMPLOYEES` hien tai | `PermissionUtil` trong controller |
| `/hr/approve-reject-contracts` | `VIEW_CONTRACTS` hien tai | `PermissionUtil` trong controller |
| `/hr/payroll-approval` | `VIEW_USERS` hien tai | `PermissionUtil` trong controller |

## Rule dac biet
- RoleID 1 Admin la super admin trong code permission hien tai va khong duoc bi chan khoi chuc nang role permission chi vi thieu seed `MANAGE_ROLE_PERMISSIONS`.
- User khac Admin van phai qua permission tu `RolePermission`/`UserPermission`.

## Permission seed trong data.sql
`src/data/data.sql` hien seed cac permission:

- `VIEW_EMPLOYEES`
- `VIEW_EMPLOYEE_DETAIL`
- `VIEW_DEPARTMENTS`
- `VIEW_CONTRACTS`
- `VIEW_RECRUITMENT`
- `VIEW_PAYROLLS`
- `VIEW_ALL_PAYROLLS`
- `VIEW_USERS`
- `VIEW_ROLES`
- `MANAGE_ROLE_PERMISSIONS`
- `VIEW_LEAVES`
- `VIEW_REPORTS`
- `VIEW_AUDIT_LOG`
- `MANAGE_SYSTEM`

## Missing Work
- [ ] Dua `/departments` vao filter Admin/permission.
- [ ] Tach `/viewTask` cua Employee ra route rieng de khong bi permission `VIEW_DEPARTMENTS`.
- [ ] Dinh nghia permission rieng cho payroll approval va contract approval neu muon tach khoi permission view.
- [ ] Xem lai `/hr/payroll-approval` dang dung `VIEW_USERS`; nen doi sang `VIEW_PAYROLLS` hoac `APPROVE_PAYROLL`.
- [ ] Xem lai create/update/delete employee dang dung `VIEW_EMPLOYEES`; nen them `MANAGE_EMPLOYEES` neu can.
- [ ] Viet test cho cac route nhay cam: admin, payroll, employee data, task.

## Acceptance Criteria
- [ ] Moi route nhay cam co role/permission ro rang.
- [ ] API request bi tu choi tra JSON 403.
- [ ] HTML request bi tu choi redirect login hoac AccessDenied dung logic filter.
- [ ] Khong co route admin/HR/payroll nao bi bo ngoai filter.
