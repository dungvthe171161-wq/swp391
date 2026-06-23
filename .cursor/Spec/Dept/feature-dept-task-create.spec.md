# Feature: Tao task phong ban
Status: Approved
Actor: Dept Manager
Priority: High
Related Code: `PostTask`, `TaskDAO`, `DAO`, `Views/DeptManager/postTask.jsp`

## Route
- `GET /postTask`
- `POST /postTask`

## Luong GET
1. Dept Manager vao `/postTask`.
2. He thong kiem tra session.
3. Controller lay du lieu can hien thi form giao viec.
4. Forward den `Views/DeptManager/postTask.jsp`.

## Luong POST
1. Dept Manager submit title, description, start date, due date va danh sach employee neu form co.
2. Controller validate du lieu bat buoc.
3. Goi DAO tao task.
4. Thanh cong redirect `/taskManager?mess=Task created successfully`.
5. That bai redirect `/postTask?error=Failed to create task`.

## Hien trang code
- Da co `PostTask`.
- Da co redirect thanh cong/that bai.

## Acceptance Criteria
- [ ] Chua login bi redirect ve `/Views/Login.jsp`.
- [ ] Tao task thanh cong quay ve task manager.
- [ ] Tao task loi quay lai postTask kem error.

## Missing Work
- [ ] Kiem tra employee duoc assign phai thuoc phong ban cua Dept Manager.
- [ ] Chuan hoa message sang tieng Viet.
