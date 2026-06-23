# Feature: Employee xem va cap nhat task duoc giao
Status: Planned
Actor: Employee
Priority: High
Source: `G1_SE2014_SRS Document.docx.pdf`, existing route-conflict spec
Related Code: `com.hrm.controller.employee.ViewTask`, `DAO`, `Views/Employee/ViewTask.jsp`, `Task`, `assignList`

## Tinh chat spec
Day la target design/gia thiet de gen code sau. File `feature-employee-view-task.spec.md` chi mo ta hien trang `/viewTask` dang bi xung dot.

## Muc tieu
Cho phep Employee xem danh sach task duoc giao, xem chi tiet task va cap nhat status/tien do task theo SRS. Task approval cuoi cung thuoc Dept Manager/manager, khong thuoc Employee.

## Van de hien tai
Code hien co co hai servlet cung mapping `/viewTask`:
- `com.hrm.controller.dept.ViewTask`
- `com.hrm.controller.employee.ViewTask`

Filter hien tai gan `/viewTask` voi Dept Manager va permission `VIEW_DEPARTMENTS`, nen Employee khong co route sach de xem task cua minh.

## Route de xuat
- `GET /employee/tasks`: danh sach task duoc assign cho Employee dang dang nhap.
- `GET /employee/tasks/detail?id={taskId}`: chi tiet task.
- `POST /employee/tasks/update-status`: cap nhat status/tien do task.

## Controller/DAO/JSP de xuat
| Thanh phan | De xuat |
| --- | --- |
| Controller | `com.hrm.controller.employee.EmployeeTaskController` |
| DAO | `TaskDAO` hoac `DAO` hien co neu chua tach |
| JSP list | `/Views/Employee/TaskList.jsp` |
| JSP detail | `/Views/Employee/ViewTask.jsp` |
| Bang | `Task`, `assignList`, `Employee` |

## Luong danh sach
1. Employee vao `/employee/tasks`.
2. Controller lay `systemUser` tu session.
3. Controller xac dinh `EmployeeID`.
4. DAO lay cac task trong `assignList` co `EmpId = EmployeeID`.
5. JSP hien title, due date, status, assigned by.

## Luong chi tiet
1. Employee mo `/employee/tasks/detail?id={taskId}`.
2. Controller lay task theo id.
3. Controller kiem tra task co assign cho Employee hien tai.
4. JSP hien title, description, assigned by, start date, due date, status.

## Luong update status
1. Employee submit status moi tu task detail/list.
2. Status hop le theo DB: `Waiting`, `In Progress`, `Completed`, `Rejected`.
3. Employee chi duoc update task duoc assign cho minh.
4. Neu Employee chuyen sang `Completed`, Dept Manager co the review/approve theo spec Dept task approval.
5. He thong ghi audit log neu co.

## Rule status de xuat
- `Waiting` -> `In Progress`.
- `In Progress` -> `Completed`.
- `In Progress` -> `Rejected` neu Employee tu choi/khong the lam va co ly do.
- Khong cho Employee cap nhat task khong duoc assign.
- Khong cho Employee sua title, description, assignee, due date.

## Hien trang code
- Co `com.hrm.controller.employee.ViewTask` nhung chi xem chi tiet theo `/viewTask?action=view&id=...`.
- Chua thay list route rieng cho Employee.
- Chua thay update status route rieng cho Employee.
- JSP `EmployeeHome.jsp` co link `/viewTask`, hien dang sai routing/permission.

## Acceptance Criteria
- [ ] Employee xem duoc danh sach task duoc giao.
- [ ] Employee khong xem duoc task khong assign cho minh.
- [ ] Employee cap nhat duoc status task cua minh theo enum DB.
- [ ] Employee khong sua duoc noi dung task hoac assignment.
- [ ] Route Employee task khong dung `/viewTask`.
- [ ] Filter cho phep role Employee vao `/employee/tasks`.

## Missing Work
- [ ] Tach route `/viewTask` theo `_Common/route-conflict-resolution.spec.md`.
- [ ] Tao list/update status controller.
- [ ] Cap nhat link trong `EmployeeHome.jsp`.
- [ ] Them Dept Manager task approval spec/flow.
- [ ] Them test chong truy cap task cua employee khac.
