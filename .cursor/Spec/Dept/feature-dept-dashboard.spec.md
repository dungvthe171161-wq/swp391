# Tính năng: Bảng điều khiển phòng ban (Department Dashboard)
Trạng thái: Đã phê duyệt
Tác nhân: Trưởng phòng (Dept Manager)
Độ ưu tiên: Trung bình
Mã nguồn liên quan: `DeptController`, `Views/DeptManager/deptHome.jsp`

## Các Route
- `GET /dept`
- `GET /dept?action=dashboard`

## Luồng chính
1. Trưởng phòng (Dept Manager) truy cập `/dept`.
2. Hệ thống kiểm tra phiên làm việc (session) `systemUser`.
3. Bộ lọc (Filter) kiểm tra vai trò/quyền hạn (role/permission).
4. Controller lấy dữ liệu cần thiết cho phòng ban.
5. Chuyển tiếp (forward) đến `/Views/DeptManager/deptHome.jsp`.

## Hiện trạng code
- Đã xây dựng `DeptController`.
- Đã có cơ chế chuyển hướng (redirect) về trang đăng nhập khi chưa có session.

## Tiêu chí nghiệm thu
- [ ] Trưởng phòng (Dept Manager) truy cập thành công bảng điều khiển phòng ban.
- [ ] Người dùng chưa đăng nhập bị hệ thống chuyển về trang login.
- [ ] Người dùng không đúng vai trò/quyền hạn bị bộ lọc (filter) chặn lại.
