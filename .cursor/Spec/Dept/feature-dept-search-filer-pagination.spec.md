# Feature: Tim kiem, loc va phan trang task
Status: Approved
Actor: Dept Manager
Priority: Medium
Related Code: `TaskManager`, `Views/DeptManager/taskManager.jsp`

## Route
- `GET /taskManager`

## Luong chinh
1. Dept Manager truy cap `/taskManager`.
2. He thong kiem tra session `systemUser`.
3. Filter kiem tra role/permission.
4. Dept Manager nhap dieu kien tim kiem.
5. Controller xu ly search va filter.
6. He thong hien thi danh sach task.
7. Neu co nhieu du lieu thi phan trang.

## Hien trang code
- Da co search theo `searchByTitle`.
- Da co filter theo `filterStatus`.
- Da co filter theo `startDate` va `endDate`.
- Da co pagination voi `pageSize = 5`.

## Acceptance Criteria
- [ ] Tim kiem task theo title.
- [ ] Loc task theo status.
- [ ] Loc task theo khoang thoi gian.
- [ ] Ho tro pagination.
- [ ] Hien thi dung tong so trang.