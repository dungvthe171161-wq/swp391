# Feature: Danh sach task phong ban
Status: Approved
Actor: Dept Manager
Priority: High
Related Code: `TaskManager`, `Views/DeptManager/taskManager.jsp`

## Route
- `GET /taskManager`
- `POST /taskManager`

## Luong GET
1. Dept Manager vao `/taskManager`.
2. He thong kiem tra session.
3. Controller lay danh sach task.
4. Forward den `/Views/DeptManager/taskManager.jsp`.

## Luong POST
1. Neu co request POST den `/taskManager`.
2. Controller redirect ve `GET /taskManager`.

## Hien trang code
- Da co `TaskManager`.
- `POST /taskManager` redirect ve `/taskManager`.

## Acceptance Criteria
- [ ] Dept Manager xem duoc danh sach task.
- [ ] Chua login bi redirect ve login.
- [ ] POST khong xu ly truc tiep ma redirect ve GET.

## Missing Work
- [ ] Gioi han danh sach task theo phong ban cua Dept Manager.
- [ ] Them filter/search/pagination neu UI can.
