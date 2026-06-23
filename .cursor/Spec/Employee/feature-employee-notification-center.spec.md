# Feature: Employee notification center
Status: Planned
Actor: Employee
Priority: Medium
Source: `G1_SE2014_SRS Document.docx.pdf`
Related Code: `SystemLogDAO`, `MailRequestDAO`, `PayrollDAO`, `TaskDAO`, `EmailSender`

## Tinh chat spec
Day la target design/gia thiet de gen code sau. Neu chua co bang `Notification`, implementation phai chot fallback hoac them schema truoc.

## Muc tieu
Cho phep Employee xem thong bao lien quan den request, task, payroll va tai khoan. SRS co yeu cau Request Notification, Email Notification Service va Auto-Notification for Requests.

## Route de xuat
- `GET /employee/notifications`: danh sach thong bao.
- `POST /employee/notifications/mark-read`: danh dau da doc.
- `POST /employee/notifications/mark-all-read`: danh dau tat ca da doc.

## Controller/DAO/JSP de xuat
| Thanh phan | De xuat |
| --- | --- |
| Controller | `com.hrm.controller.employee.EmployeeNotificationController` |
| DAO | `SystemLogDAO` tam thoi hoac `NotificationDAO` neu them bang moi |
| JSP | `/Views/Employee/Notifications.jsp` |
| Bang | `SystemLog` tam thoi; nen co `Notification` neu can notification dung nghia |

## Nguon thong bao
Thong bao co the sinh ra khi:
- MailRequest duoc approve/reject.
- Task moi duoc giao.
- Task completion bi reject/duoc chap nhan.
- Payroll cua Employee duoc approve/paid va co payslip moi.
- Contract moi active/approved.
- Password/account thay doi.

## Luong danh sach
1. Employee vao `/employee/notifications`.
2. Controller lay `systemUser` tu session.
3. Controller xac dinh `EmployeeID`/`UserID`.
4. DAO lay thong bao gan voi user hien tai.
5. JSP hien loai thong bao, noi dung, thoi gian, trang thai da doc/chua doc.

## Luong mark read
1. Employee bam vao notification hoac bam danh dau da doc.
2. Controller kiem tra notification thuoc user hien tai.
3. He thong cap nhat read status.
4. Redirect/tra JSON thanh cong.

## Luu y schema
`SystemLog` hien co phu hop audit log hon la notification. Neu can notification dung nghia, nen them bang moi sau:
- `NotificationID`
- `UserID`
- `EmployeeID`
- `Type`
- `Title`
- `Message`
- `ObjectType`
- `ObjectID`
- `IsRead`
- `CreatedAt`

Neu chua them bang moi, UI co the hien mot so event tu `SystemLog` nhung khong du de mark read ca nhan.

## Luong loi
- Chua dang nhap: redirect `/login`.
- Notification khong thuoc user hien tai: AccessDenied/403.
- Notification khong ton tai: hien loi than thien.

## Hien trang code
- Da co `SystemLogDAO`.
- Da co `EmailSender`.
- Chua thay bang/controller/JSP notification rieng.
- `Homepage.jsp` va mot so dashboard co UI notification/topbar nhung chua co backend notification ro rang.

## Out of Scope
- Khong lam realtime websocket trong spec nay.
- Khong push notification mobile.
- Khong thay email service hien co, chi bo sung notification trong app.

## Acceptance Criteria
- [ ] Employee xem duoc thong bao cua chinh minh.
- [ ] Employee khong xem/mark read thong bao cua user khac.
- [ ] Thong bao request approve/reject lien ket duoc ve request detail.
- [ ] Thong bao payslip lien ket duoc ve payslip detail neu co payroll id.
- [ ] Neu chua co bang Notification, spec implement phai ghi ro fallback va han che.

## Missing Work
- [ ] Chot co them bang `Notification` hay dung fallback `SystemLog`.
- [ ] Tao controller/JSP notification.
- [ ] Them hook tao notification trong request, task, payroll, contract flows.
