# Common Spec: Notification
Status: Approved
Actor: All authenticated actors
Priority: Medium
Related Data: `Notification`, `SystemUser`, optional `Application`

## Muc tieu
`Notification` la module thong bao dung chung cho toan bo BetterHR, khong thuoc rieng Guest.

Moi thong bao gan voi mot account trong `SystemUser` thong qua `UserID`. Cac module khac co the tao notification khi co su kien can bao cho user.

## Database
Bang `Notification` gom:
- `NotificationID`
- `UserID`
- `ApplicationID`
- `Title`
- `Message`
- `Type`
- `IsRead`
- `CreatedDate`
- `ReadDate`

Rang buoc:
- `UserID` bat buoc, tham chieu `SystemUser.UserID`.
- `ApplicationID` optional, chi dung cho thong bao lien quan ung tuyen.
- Neu sau nay thong bao can gan voi Task, Payroll, Leave, Contract thi them cot tham chieu rieng hoac dung bang lien ket moi, khong nhan nhet nhieu object id vao `ApplicationID`.

## Type
Type hien tai:
- `Application`
- `Interview`
- `Offer`
- `System`

Mo rong sau nay:
- `Task`
- `Payroll`
- `Leave`
- `Contract`
- `Security`

## Actor su dung
- Guest: ho so ung tuyen, lich phong van, offer.
- Employee: task, cham cong, luong, nghi phep.
- Dept Manager: task nhom, lich nhom, yeu cau nghi phep.
- HR Staff: ung vien, hop dong, bang luong.
- HR Manager: phe duyet hop dong, payroll, nhan su.
- Admin: bao mat, phan quyen, system log can chu y.

## DAO chung
Nen tao `NotificationDAO`, khong dat logic notification trong DAO cua tung actor.

Method toi thieu:
- `create(Notification notification)`
- `findByUserId(int userId, int limit)`
- `countUnreadByUserId(int userId)`
- `markRead(int notificationId, int userId)`
- `markAllRead(int userId)`

## Security rule
- User chi doc notification co `Notification.UserID = session.systemUser.userID`.
- Admin co the xem/tim notification cua user khac neu co man hinh quan tri rieng.
- Moi update read state phai loc theo `UserID` de tranh user danh dau notification cua nguoi khac.

## UI rule
- Moi actor co the co icon chuong/thong bao tren dashboard rieng.
- Thong bao phai hien tieng Viet.
- Notification unread phai co trang thai ro rang.
- Neu chua co notification, hien empty state than thien.

## Acceptance Criteria
- [ ] Notification gan voi `SystemUser.UserID`.
- [ ] Notification co the dung cho tat ca actor.
- [ ] Notification lien quan ung tuyen co the gan them `ApplicationID`.
- [ ] User khong doc/cap nhat duoc notification cua nguoi khac.
- [ ] DAO notification dung chung, khong copy logic theo tung actor.
