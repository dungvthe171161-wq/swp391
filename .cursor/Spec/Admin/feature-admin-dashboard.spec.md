# Tính năng: Bảng điều khiển Admin (Admin Dashboard)
Trạng thái: Đã phê duyệt
Tác nhân: Admin
Độ ưu tiên: Cao
Mã nguồn liên quan: `AdminController`, `DashboardDAO`, `EmployeeDAO`, `DepartmentDAO`, `SystemUserDAO`, `Admin/AdminHome.jsp`

## Các Route
- `GET /admin`
- `GET /admin?action=dashboard`
- `GET /admin?action=dashboard-data`

## Luồng Dashboard HTML
1. Admin truy cập `/admin` hoặc `/admin?action=dashboard`.
2. Bộ lọc (Filter) kiểm tra session và quyền (permission) `MANAGE_SYSTEM`.
3. `AdminController` lấy số liệu thống kê tổng quan từ các DAO.
4. Hệ thống thiết lập thuộc tính (set attribute) cho request.
5. Chuyển tiếp (forward) đến `Admin/AdminHome.jsp`.

## Luồng Dashboard JSON
1. Admin gọi `/admin?action=dashboard-data`.
2. Controller lấy dữ liệu thống kê.
3. Phản hồi (Response) trả về định dạng `application/json` và mã hóa `UTF-8`.

## Dữ liệu hiển thị
- Tổng số nhân viên.
- Tổng số phòng ban.
- Tổng số người dùng (user).
- Thống kê hoạt động, phòng ban, quyền hạn, phân phối trạng thái (status distribution) nếu DAO trả về.

## Giao ước giao diện (UI contract)
- Bảng điều khiển admin sử dụng thanh bên (sidebar)/thanh điều hướng trên (topbar) BetterHR đồng bộ với các trang Admin khác.
- Thẻ (Card) thống kê có nhãn bằng tiếng Việt và màu sắc theo bảng màu (palette) BetterHR.
- Biểu đồ (Chart) phòng ban và trạng thái nhân viên phải có kích thước ổn định, không làm tràn bố cục (layout).
- Nếu biểu đồ chưa có dữ liệu thực tế, giao diện (UI) phải hiển thị trạng thái trống (empty state) bằng tiếng Việt thay vì báo lỗi JS.

## Hiện trạng code
- Đã có dashboard dạng HTML và JSON.
- JSON trong `AdminController` vẫn đang được xây dựng thủ công bằng `StringBuilder`.
- Một số ngoại lệ (exception) vẫn dùng `printStackTrace`.

## Tiêu chí nghiệm thu
- [ ] Admin có quyền xem được bảng điều khiển (dashboard).
- [ ] Người dùng không có quyền sẽ bị chặn trước khi vào controller.
- [ ] JSON dashboard thiết lập đúng loại nội dung (content type) và mã hóa (encoding).
- [ ] Lỗi cơ sở dữ liệu không làm lộ vết ngăn xếp (stack trace) ra giao diện người dùng.
- [ ] Bảng điều khiển không bị chồng lấn giao diện cũ lên giao diện mới.
- [ ] Tất cả nhãn/nhãn hiển thị (label) phải là tiếng Việt.
