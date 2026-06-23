# Module Spec: Dept Manager Task Management
Status: Approved
Actor: Dept Manager
Priority: High
Related Code: `DeptController`, `PostTask`, `TaskManager`, `com.hrm.controller.dept.ViewTask`, `Views/DeptManager/*`

## Pham vi
Dept Manager quan ly dashboard phong ban, tao task, xem danh sach task va cap nhat task.

## Feature files
- `feature-dept-dashboard.spec.md`
- `feature-dept-task-create.spec.md`
- `feature-dept-task-list.spec.md`
- `feature-dept-task-update.spec.md`


- `feature-dept-search-filer-pagination.md`
- `feature-dept-updatetaskstatus.md`
- `feature-dept-viewtask.md`



## Route hien co
- `/dept`
- `/postTask`
- `/taskManager`
- `/viewTask`

## Phan quyen hien co
- `RoleAuthorizationFilter` cho role 3 truy cap `/dept`, `/taskManager`, `/viewTask`, `/postTask`.
- `ModulePermissionFilter` yeu cau permission `VIEW_DEPARTMENTS` cho cac route nay.

## Canh bao code
- `com.hrm.controller.dept.ViewTask` va `com.hrm.controller.employee.ViewTask` deu khai bao `@WebServlet("/viewTask")`. Day la xung dot mapping can xu ly khi clean code.
- Spec Dept nay chi noi ve luong Dept Manager, khong gom Employee view task.
