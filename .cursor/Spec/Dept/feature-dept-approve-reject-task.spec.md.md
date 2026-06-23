# Feature: Approve / Reject Task Completion

Status: Approved
Actor: Dept Manager
Priority: High
Related Code: `TaskManager`, `DAO`

## Route

* `GET /taskManager?action=approve&id={taskId}`
* `POST /taskManager?action=approve&id={taskId}`
* `GET /taskManager?action=reject&id={taskId}`
* `POST /taskManager?action=reject&id={taskId}`

## Luong chinh - Approve Task

1. Dept Manager truy cap chuc nang Approve Task.
2. He thong kiem tra session `systemUser`.
3. `RoleAuthorizationFilter` kiem tra role Dept Manager.
4. `ModulePermissionFilter` kiem tra permission duoc phep quan ly task.
5. Controller lay thong tin task theo `taskId`.
6. He thong kiem tra task ton tai.
7. He thong kiem tra task thuoc phong ban cua Dept Manager.
8. He thong cap nhat trang thai task thanh `Approved`.
9. He thong luu thong tin nguoi phe duyet va thoi gian phe duyet.
10. He thong gui thong bao den nhan vien duoc giao task.
11. Redirect ve trang danh sach task.

## Luong chinh - Reject Task

1. Dept Manager truy cap chuc nang Reject Task.
2. He thong kiem tra session `systemUser`.
3. Controller lay thong tin task theo `taskId`.
4. He thong kiem tra task ton tai.
5. He thong kiem tra task thuoc phong ban cua Dept Manager.
6. Dept Manager nhap ly do tu choi.
7. He thong validate ly do khong duoc de trong.
8. He thong cap nhat trang thai task thanh `Rejected`.
9. He thong luu ly do tu choi va thoi gian xu ly.
10. He thong gui thong bao cho nhan vien.
11. Redirect ve trang danh sach task.

## Alternative Flow

### AF-01: Task khong ton tai

1. He thong khong tim thay task theo `taskId`.
2. Hien thi thong bao:

   * `Task khong ton tai.`

### AF-02: Task khong thuoc phong ban

1. Task thuoc department khac.
2. He thong tu choi truy cap.
3. Hien thi trang Access Denied.

### AF-03: Tu phe duyet task cua chinh minh

1. Dept Manager la nguoi thuc hien task.
2. He thong khong cho phe duyet.
3. Hien thi thong bao:

   * `Khong duoc phe duyet task cua chinh minh.`

### AF-04: Reject khong nhap ly do

1. Dept Manager submit Reject ma khong nhap reason.
2. He thong hien thi loi validation.
3. Khong cap nhat du lieu.

### AF-05: Chua dang nhap

1. Session `systemUser` khong ton tai.
2. Redirect den `/login`.

## Business Rules

### BR-07

Dept Manager chi duoc thao tac voi task thuoc phong ban cua minh.

### BR-09

Moi thao tac phe duyet phai duoc thuc hien boi nguoi co quyen quan ly phong ban.

### BR-18

Task chi duoc phe duyet khi dang o trang thai cho xac nhan hoan thanh (`Pending Approval`).

### BR-19

Reject task bat buoc phai co ly do tu choi.

### BR-20

Dept Manager khong duoc phe duyet task do chinh minh thuc hien.

## Hien trang code

* Da co servlet `TaskManager`.
* Da co DAO cap nhat thong tin task.
* Da co session `systemUser`.
* Can bo sung xu ly Approve/Reject neu chua ton tai.

## Technical Notes

* Reject bat buoc co `reason`.
* Luu `approvedAt` hoac `reviewedAt`.
* Luu `approvedBy`.
* Chi xu ly task thuoc department hien tai.
* Chi cho phep phe duyet task o trang thai `Pending Approval`.

## Acceptance Criteria

* [ ] Dept Manager approve task thanh cong.
* [ ] Dept Manager reject task thanh cong.
* [ ] Reject task bat buoc nhap ly do.
* [ ] Khong the approve/reject task cua phong ban khac.
* [ ] Khong the approve task cua chinh minh.
* [ ] He thong luu thoi gian phe duyet.
* [ ] User chua login bi redirect Login.
* [ ] User khong dung role/permission bi chan truy cap.
