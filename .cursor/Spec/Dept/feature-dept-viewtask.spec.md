# Feature: View Task Assignees

Status: Approved
Actor: Dept Manager
Priority: Medium
Related Code: `TaskManager`, `DAO`, `Views/DeptManager/taskManager.jsp`

## Route
- `GET /taskManager?action=viewAssignees&id={taskId}`

## Luong chinh
1. Dept Manager chon task.
2. Controller nhan `taskId`.
3. He thong lay danh sach employee duoc assign.
4. He thong lay thong tin tung employee.
5. Tra ve danh sach nhan vien duoc giao task.

## Hien trang code
- Da co `getEmployeeIdsByTaskId`.
- Da co `getEmp`.
- Tra du lieu dang `text/html`.

## Acceptance Criteria
- [ ] Hien thi dung danh sach nhan vien duoc giao task.
- [ ] Neu khong co employee thi hien thi thong bao.
- [ ] TaskId khong hop le thi khong gay loi he thong.