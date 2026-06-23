# Feature: Nhan vien xem chi tiet task
Status: Partial
Actor: Employee
Priority: High
Related Code: `com.hrm.controller.employee.ViewTask`, `Views/Employee/ViewTask.jsp`

## Luu y pham vi
File nay mo ta hien trang route `/viewTask` dang bi xung dot voi Dept Manager. Khi gen code moi, uu tien target design trong `feature-employee-task-workflow.spec.md` voi route `/employee/tasks`.


## Route hien co
- `GET /viewTask?action=view&id={taskId}`
- `POST /viewTask` goi lai `doGet`

## Luong chinh
1. Employee truy cap `/viewTask?action=view&id={taskId}`.
2. Controller kiem tra session `systemUser`.
3. Neu thieu id, forward den `/Views/Employee/ViewTask.jsp` kem error.
4. Parse `taskId`.
5. Lay task bang `DAO.getTaskById(taskId)`.
6. Lay nguoi giao viec bang `DAO.getEmp(task.getAssignedBy())`.
7. Lay danh sach employee duoc assign bang `DAO.getEmployeeIdsByTaskId(taskId)`.
8. Set `task`, `assignedByEmployee`, `assignedEmployees`.
9. Forward den `/Views/Employee/ViewTask.jsp`.

## Hien trang code
- Co controller Employee ViewTask nhung dang trung route `/viewTask` voi Dept ViewTask.
- `RoleAuthorizationFilter` hien cho `/viewTask` role 3 Dept Manager, khong cho role 5 Employee.
- `ModulePermissionFilter` cung gan `/viewTask` voi permission `VIEW_DEPARTMENTS`, khong phu hop Employee.

## Acceptance Criteria
- [ ] Employee xem duoc task duoc giao cho minh.
- [ ] Employee khong xem duoc task cua nguoi khac.
- [ ] Id sai hien thi loi than thien.

## Missing Work
- [ ] Tach route Employee thanh `/employee/tasks` hoac `/employee/view-task`.
- [ ] Cap nhat filter cho role 5 Employee.
- [ ] Them check task co assigned cho employee hien tai truoc khi hien thi.
- [ ] Bo trung mapping `/viewTask`.
