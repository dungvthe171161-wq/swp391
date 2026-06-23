# Feature: Nhan vien quan ly nghi phep
Status: Planned
Actor: Employee
Priority: Medium
Related Code: `Views/Employee/LeaveManagement.jsp`, `LeaveDAO`, `Leave`

## Luu y pham vi
File nay la ban cu theo `LeaveDAO`/`Leave`. Theo `_Common/database-impact.spec.md`, luong request/nghi phep moi nen dung `MailRequest`; target design nam trong `feature-employee-request-management.spec.md`.

## Muc tieu
Cho phep nhan vien xem va tao yeu cau nghi phep neu module nghi phep duoc kich hoat.

## Route/JSP hien co
- `/Views/Employee/LeaveManagement.jsp`
- Pattern filter co `/LeaveManagement`.

## Luong de xuat
1. Employee mo trang nghi phep.
2. He thong hien thi lich su leave cua employee hien tai.
3. Employee tao yeu cau nghi phep moi.
4. He thong validate ngay bat dau, ngay ket thuc, ly do.
5. Luu request voi trang thai cho duyet.

## Hien trang code
- Co entity/DAO `Leave`, `LeaveDAO` va JSP.
- Can kiem tra controller xu ly nghiep vu nghi phep neu bat dau implement.

## Acceptance Criteria
- [ ] Employee chi xem leave cua minh.
- [ ] Tao leave khong duoc trung ngay khong hop le.
- [ ] Leave moi co trang thai cho duyet.

## Missing Work
- [ ] Chot dung tiep `Leave` hay migrate ve `MailRequest`.
- [ ] Neu theo target moi, khong gen controller rieng tu file nay; gen theo `feature-employee-request-management.spec.md`.
