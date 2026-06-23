# Cross-cutting Spec: Route Conflict Resolution
Status: Approved
Priority: High
Related Code: `com.hrm.controller.dept.ViewTask`, `com.hrm.controller.employee.ViewTask`, `RoleAuthorizationFilter`, `ModulePermissionFilter`

## Van de hien tai
Code hien co hai servlet cung khai bao route `/viewTask`:
- `com.hrm.controller.dept.ViewTask`
- `com.hrm.controller.employee.ViewTask`

Dong thoi filter hien tai gan `/viewTask` cho Dept Manager role 3 va permission `VIEW_DEPARTMENTS`, nen Employee role 5 khong phu hop voi route nay.

## Huong xu ly de xuat
| Actor | Route moi | Controller |
| --- | --- | --- |
| Dept Manager | `/dept/tasks`, `/dept/task-detail` | `com.hrm.controller.dept.*` |
| Employee | `/employee/tasks`, `/employee/task-detail` | `com.hrm.controller.employee.*` |

## Migration de xuat
1. Doi mapping Dept task list/detail sang prefix `/dept`.
2. Doi mapping Employee task sang prefix `/employee`.
3. Cap nhat JSP link trong DeptManager va Employee.
4. Cap nhat `RoleAuthorizationFilter`.
5. Cap nhat `ModulePermissionFilter`.
6. Giu redirect tam thoi neu can de tranh link cu hong.

## Acceptance Criteria
- [ ] Khong con hai servlet trung `@WebServlet("/viewTask")`.
- [ ] Dept Manager chi vao route `/dept/*` cho task.
- [ ] Employee chi vao route `/employee/*` cho task cua minh.
- [ ] Employee khong can permission `VIEW_DEPARTMENTS` de xem task duoc giao.
