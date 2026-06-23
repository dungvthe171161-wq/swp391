# Feature: Cap nhat task phong ban
Status: Approved
Actor: Dept Manager
Priority: High
Related Code: `com.hrm.controller.dept.ViewTask`, `DAO`, `Views/DeptManager/viewTask.jsp`

## Route
- `GET /viewTask?id={taskId}`
- `POST /viewTask`

## Luong GET
1. Dept Manager mo chi tiet task bang id.
2. Controller kiem tra session.
3. Lay task theo id.
4. Forward den `/Views/DeptManager/viewTask.jsp`.

## Luong POST
1. Dept Manager submit `taskId`, `title`, `description`, `startDate`, `dueDate`.
2. Controller parse `taskId`.
3. Goi `DAO.updateTask`.
4. Neu thanh cong, goi `DAO.deleteTaskAssignments(taskId)` de reset assignment cu.
5. Redirect ve `/viewTask?id={taskId}&mess=Task updated successfully`.
6. Neu that bai, redirect ve `/viewTask?id={taskId}&error=Failed to update task`.

## Hien trang code
- Da co luong update va reset assignment.
- Chua thay xu ly HTTP 400 rieng cho `NumberFormatException`.

## Acceptance Criteria
- [ ] Update thanh cong redirect kem `mess`.
- [ ] Update that bai redirect kem `error`.
- [ ] Chua login bi redirect ve `/Views/Login.jsp`.

## Missing Work
- [ ] Bat loi id sai va tra loi than thien.
- [ ] Kiem tra task thuoc phong ban truoc khi cho sua.
