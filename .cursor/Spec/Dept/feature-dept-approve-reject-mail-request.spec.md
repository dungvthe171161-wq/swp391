# Feature: Approve / Reject Mail Request

Status: Approved
Actor: Dept Manager
Priority: High
Related Code: `MailRequestController`, `DAO`

## Route

* `POST /mailRequest?action=approve`
* `POST /mailRequest?action=reject`

## Luong chinh - Approve Request

1. Dept Manager truy cap danh sach Mail Request.
2. He thong kiem tra session `systemUser`.
3. `RoleAuthorizationFilter` kiem tra role Dept Manager.
4. `ModulePermissionFilter` kiem tra permission xu ly request.
5. Dept Manager chon request can xu ly.
6. He thong hien thi chi tiet request.
7. He thong kiem tra request ton tai.
8. He thong kiem tra request thuoc phong ban cua Dept Manager.
9. He thong kiem tra request dang o trang thai `Pending`.
10. Dept Manager chon `Approve`.
11. He thong cap nhat trang thai request thanh `Approved`.
12. He thong luu nguoi phe duyet va thoi gian phe duyet.
13. He thong gui thong bao cho nhan vien.
14. Redirect ve danh sach request.

## Luong chinh - Reject Request

1. Dept Manager chon request can xu ly.
2. He thong hien thi thong tin request.
3. Dept Manager chon `Reject`.
4. Dept Manager nhap ly do tu choi.
5. He thong validate du lieu.
6. He thong cap nhat trang thai request thanh `Rejected`.
7. He thong luu ly do tu choi.
8. He thong luu nguoi xu ly va thoi gian xu ly.
9. He thong gui thong bao cho nhan vien.
10. Redirect ve danh sach request.

## Alternative Flow

### AF-01: Request da duoc xu ly

1. Request da o trang thai `Approved` hoac `Rejected`.
2. He thong khong cho phep xu ly lai.
3. Hien thi thong bao:

   * `Request da duoc xu ly truoc do.`

### AF-02: Request khong thuoc phong ban

1. Request thuoc department khac.
2. He thong tu choi truy cap.
3. Hien thi trang Access Denied.

### AF-03: Dept Manager xu ly request cua chinh minh

1. Nguoi tao request la chinh Dept Manager dang dang nhap.
2. He thong khong cho phep tu phe duyet.
3. Hien thi thong bao:

   * `Khong duoc xu ly request cua chinh minh.`

### AF-04: Reject khong nhap ly do

1. Dept Manager submit Reject ma khong nhap comment.
2. He thong hien thi loi validation.
3. Khong cap nhat du lieu.

### AF-05: Request khong ton tai

1. He thong khong tim thay request theo ID.
2. Hien thi thong bao:

   * `Request khong ton tai.`

### AF-06: Chua dang nhap

1. Session `systemUser` khong ton tai.
2. Redirect den `/login`.

## Business Rules

### BR-08

Dept Manager chi duoc xu ly request cua nhan vien thuoc phong ban minh phu trach.

### BR-09

Chi nguoi co quyen quan ly phong ban moi duoc phe duyet hoac tu choi request.

### BR-19

Reject request bat buoc phai co ly do hoac comment.

### BR-23

Chi duoc xu ly request dang o trang thai `Pending`.

### BR-24

Dept Manager khong duoc phe duyet request do chinh minh tao.

## Hien trang code

* Da co `MailRequestController`.
* Da co DAO thao tac voi Mail Request.
* Da co session `systemUser`.
* Can bo sung logic Approve/Reject neu chua ton tai.

## Technical Notes

* Reject bat buoc co `comment`.
* Chi xu ly request co trang thai `Pending`.
* Luu `approvedBy`.
* Luu `approvedAt` hoac `processedAt`.
* Luu `rejectReason` khi request bi tu choi.
* Chi load request thuoc department hien tai.

## Acceptance Criteria

* [ ] Dept Manager approve request thanh cong.
* [ ] Dept Manager reject request thanh cong.
* [ ] Reject request bat buoc nhap ly do.
* [ ] Khong the xu ly request da duoc xu ly truoc do.
* [ ] Khong the xu ly request cua phong ban khac.
* [ ] Khong the phe duyet request cua chinh minh.
* [ ] He thong luu thong tin nguoi phe duyet.
* [ ] He thong luu thoi gian xu ly.
* [ ] User chua login bi redirect Login.
* [ ] User khong dung role/permission bi chan truy cap.
