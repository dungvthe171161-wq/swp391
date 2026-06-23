# Feature: Monitor Task Status

Status: Approved
Actor: Dept Manager
Priority: High
Related Code: `TaskManager`, `DAO`, `Views/DeptManager/taskManager.jsp`

## Route

* `GET /taskManager?action=monitor`

## Luong chinh

1. Dept Manager truy cap `/taskManager?action=monitor`.
2. He thong kiem tra session `systemUser`.
3. `RoleAuthorizationFilter` kiem tra role Dept Manager.
4. `ModulePermissionFilter` kiem tra permission duoc phep xem task phong ban.
5. Controller lay danh sach task thuoc phong ban cua Dept Manager.
6. He thong hien thi danh sach task kem thong tin:

   * Task Title
   * Assignee
   * Start Date
   * End Date
   * Status
7. Dept Manager xem trang thai va tien do thuc hien cua task.
8. Dept Manager co the xem chi tiet task neu can.

## Alternative Flow

### AF-01: Chua dang nhap

1. Session `systemUser` khong ton tai.
2. He thong redirect den `/login`.

### AF-02: Khong dung quyen

1. User khong phai Dept Manager hoac khong co permission.
2. He thong hien thi trang Access Denied.

### AF-03: Khong co task

1. Phong ban khong co task nao.
2. He thong hien thi thong bao:

   * `Khong co task can theo doi.`

## Business Rules

### BR-07

Dept Manager chi duoc xem task thuoc phong ban cua minh.

### BR-17

Khong duoc xem task cua phong ban khac.

### BR-18

Trang thai task phai duoc hien thi theo du lieu hien tai trong he thong.

## Hien trang code

* Da co servlet `TaskManager`.
* Da co DAO lay danh sach task.
* Da co giao dien `taskManager.jsp`.
* Da co co che session `systemUser`.

## Technical Notes

* Chi load task thuoc department hien tai cua Dept Manager.
* Ho tro filter theo `status`.
* Khong load task cua phong ban khac.
* Du lieu task duoc lay tu DAO theo DepartmentID.

## Acceptance Criteria

* [ ] Chi hien thi task thuoc phong ban cua Dept Manager.
* [ ] Hien thi dung trang thai task.
* [ ] Co the xem danh sach task can theo doi.
* [ ] Ho tro filter theo status.
* [ ] User chua login bi redirect den Login.
* [ ] User khong dung role/permission bi chan truy cap.
* [ ] Hien thi thong bao khi khong co task.
