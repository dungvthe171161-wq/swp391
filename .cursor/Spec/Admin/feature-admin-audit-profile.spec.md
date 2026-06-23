# Feature: Audit log va profile admin
Status: Partial
Actor: Admin
Priority: Medium
Related Code: `AdminController`, `SystemLogDAO`, `Admin/AuditLog.jsp`, `Admin/Profile.jsp`

## Route
- `GET /admin?action=audit-log`
- `GET /admin?action=profile`

## Luong audit log
1. Admin vao `/admin?action=audit-log`.
2. Controller lay danh sach system log.
3. Forward den `Admin/AuditLog.jsp`.

## Luong profile
1. Admin vao `/admin?action=profile`.
2. Controller lay thong tin user hien tai tu session.
3. Forward den `Admin/Profile.jsp`.

## Hien trang code
- Da co forward den AuditLog va Profile trong `AdminController`.
- Can kiem tra them muc do filter/permission rieng neu muon tach `VIEW_AUDIT_LOG`.

## Acceptance Criteria
- [ ] Admin xem duoc audit log.
- [ ] Admin xem duoc profile cua chinh minh.
- [ ] User chua login bi redirect ve login.

## Missing Work
- [ ] Them permission rieng cho audit log neu can bao mat cao.
- [ ] Ghi log day du cho cac hanh dong admin quan trong.
