# Tính năng: Nhật ký hệ thống (Audit log) và thông tin cá nhân (Profile) Admin
Trạng thái: Chưa hoàn thiện (Partial)
Tác nhân: Admin
Độ ưu tiên: Trung bình
Mã nguồn liên quan: `AdminController`, `SystemLogDAO`, `Admin/AuditLog.jsp`, `Admin/Profile.jsp`

## Các Route
- `GET /admin?action=audit-log`
- `GET /admin?action=profile`

## Luồng nhật ký hệ thống (Audit log)
1. Admin truy cập `/admin?action=audit-log`.
2. Controller lấy danh sách nhật ký hệ thống (system log).
3. Chuyển tiếp (forward) đến `Admin/AuditLog.jsp`.

## Luồng thông tin cá nhân (Profile)
1. Admin truy cập `/admin?action=profile`.
2. Controller lấy thông tin người dùng hiện tại từ session.
3. Chuyển tiếp (forward) đến `Admin/Profile.jsp`.

## Hiện trạng code
- Đã có forward đến AuditLog và Profile trong `AdminController`.
- Cần kiểm tra thêm mức độ filter/permission riêng nếu muốn tách quyền `VIEW_AUDIT_LOG`.

## Tiêu chí nghiệm thu
- [ ] Admin xem được nhật ký hệ thống.
- [ ] Admin xem được thông tin cá nhân (profile) của chính mình.
- [ ] Người dùng chưa đăng nhập (login) bị chuyển hướng (redirect) về trang đăng nhập.

## Các phần việc còn thiếu
- [ ] Thêm quyền (permission) riêng cho nhật ký hệ thống nếu cần tính bảo mật cao.
- [ ] Ghi nhật ký (log) đầy đủ cho các hành động quan trọng của admin.
