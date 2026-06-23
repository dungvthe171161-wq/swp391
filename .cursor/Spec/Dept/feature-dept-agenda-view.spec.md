# Feature: Agenda View

Status: Approved
Actor: Dept Manager
Priority: Medium
Related Code: `DeptController`, `Views/DeptManager/agenda.jsp`

## Route

* `GET /dept?action=agenda`

## Luong chinh

1. Dept Manager truy cap `/dept?action=agenda`.
2. He thong kiem tra session `systemUser`.
3. `RoleAuthorizationFilter` kiem tra role Dept Manager.
4. `ModulePermissionFilter` kiem tra permission truy cap module phong ban.
5. Controller lay danh sach su kien lien quan den phong ban:

   * Task sap den han
   * Task qua han
   * Cuoc hop
   * Lich cong tac
   * Cac su kien noi bo
6. He thong sap xep du lieu theo thoi gian.
7. He thong hien thi Agenda View.
8. Dept Manager theo doi cac hoat dong sap dien ra cua phong ban.
9. Dept Manager co the loc du lieu theo ngay hoac khoang thoi gian.

## Alternative Flow

### AF-01: Khong co du lieu

1. Khong tim thay su kien nao trong khoang thoi gian duoc chon.
2. He thong hien thi thong bao:

   * `Khong co du lieu lich lam viec.`

### AF-02: Khong dung quyen

1. User khong phai Dept Manager hoac khong co permission.
2. He thong hien thi trang Access Denied.

### AF-03: Chua dang nhap

1. Session `systemUser` khong ton tai.
2. He thong redirect den `/login`.

## Business Rules

### BR-17

Dept Manager chi duoc xem du lieu thuoc phong ban minh phu trach.

### BR-47

Agenda View chi hien thi cac su kien lien quan den phong ban hien tai.

### BR-48

Du lieu phai duoc sap xep theo thoi gian tang dan.

### BR-49

Khong hien thi su kien cua phong ban khac.

## Hien trang code

* Da co `DeptController`.
* Da co giao dien `agenda.jsp`.
* Da co session `systemUser`.
* Can bo sung logic tong hop lich neu chua ton tai.

## Technical Notes

* Co the hien thi dang:

  * Calendar View
  * Daily View
  * Weekly View
  * Monthly View
* Ho tro filter theo:

  * Ngay
  * Tuan
  * Thang
* Chi load du lieu thuoc department hien tai.
* Du lieu co the duoc tong hop tu Task, Meeting va cac module lien quan.

## Acceptance Criteria

* [ ] Dept Manager truy cap duoc Agenda View.
* [ ] Chi hien thi du lieu thuoc phong ban hien tai.
* [ ] Hien thi dung cac task va su kien lien quan.
* [ ] Ho tro filter theo ngay.
* [ ] Du lieu duoc sap xep theo thoi gian.
* [ ] User chua login bi redirect Login.
* [ ] User khong dung role/permission bi chan truy cap.
* [ ] Hien thi thong bao khi khong co du lieu.
