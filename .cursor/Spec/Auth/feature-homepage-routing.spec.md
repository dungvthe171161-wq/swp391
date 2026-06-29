# Tính năng: Định tuyến trang chủ (Homepage) hiển thị quyền theo vai trò (role)
Trạng thái: Đã phê duyệt
Tác nhân: Guest, Admin, HR Manager, HR Staff, Dept Manager, Employee
Độ ưu tiên: Cao
Mã nguồn liên quan: `HomepageController`, `Views/Homepage.jsp`

## Mục tiêu
Sử dụng đường dẫn `/homepage` làm cổng truy cập trung tâm của hệ thống. Sau tất cả các luồng đăng nhập (đăng nhập cục bộ hoặc Google Login) hoặc đổi mật khẩu thành công, người dùng sẽ được đưa về login/homepage theo luồng xác thực (auth flow), và trang chủ sẽ hiển thị các khu vực/bảng điều khiển (dashboard) mà vai trò hiện tại được cấp quyền truy cập.

## Các Route
- `GET /homepage`

## Luồng cho khách truy cập (Guest flow)
1. Khách truy cập vào `/homepage`.
2. Nếu chưa tồn tại đối tượng `systemUser` trong session, `HomepageController` sẽ thiết lập đối tượng quyền truy cập `dashboardAccess` mặc định dành cho khách (guest).
3. Hệ thống chuyển tiếp (forward) yêu cầu đến `/Views/Homepage.jsp`.
4. Khách xem trang công khai (public) và danh sách các liên kết công khai.
5. Nút `Đăng ký` trên trang chủ trỏ chính xác tới tuyến đường `/register`, không trỏ nhầm về `/login`.

## Luồng người dùng đã đăng nhập (Logged-in user flow)
1. Người dùng truy cập vào `/homepage`.
2. `HomepageController` tiến hành lấy đối tượng `systemUser` từ session.
3. Controller lấy vai trò (role) hiện tại và khởi tạo đối tượng quyền truy cập `dashboardAccess`.
4. Hệ thống forward yêu cầu đến `/Views/Homepage.jsp`, không tự động chuyển hướng bắt buộc theo vai trò (auto redirect).
5. Trang JSP hiển thị các nút chức năng/bảng điều khiển (dashboard) tương ứng:
   - Admin: Khu vực Admin, phân hệ HR/HR Staff nếu được cấp quyền, khu vực Employee, Guest.
   - HR Manager: Khu vực HR Manager, Employee, Guest.
   - HR Staff: Khu vực HR Staff, Employee, Guest.
   - Dept Manager: Khu vực Dept Manager/Employee/Guest nếu logic phân quyền cho phép.
   - Employee: Khu vực Employee và Guest.
   - Guest (vai trò): Chỉ hiển thị khu vực Guest/public.

## Hiện trạng code
- Đã có sẵn tuyến đường (route) `/homepage`.
- Đã thực hiện forward về `/Views/Homepage.jsp`.
- Đã sử dụng đối tượng `dashboardAccess` để render menu tương ứng với từng vai trò.
- Đăng nhập cục bộ (local login) và đăng nhập Google đều chuyển hướng về `/homepage` sau khi khởi tạo thành công session.

## Tiêu chí nghiệm thu
- [ ] Khách truy cập (Guest) vào `/homepage` nhìn thấy giao diện trang công khai.
- [ ] Người dùng đã đăng nhập truy cập vào `/homepage` vẫn ở lại trang chủ, không bị hệ thống tự động chuyển hướng bắt buộc sang trang khác.
- [ ] Trang chủ hiển thị đúng dashboard/menu tương ứng với vai trò hiện tại của người dùng.
- [ ] Vai trò không hợp lệ hoặc thiếu thông tin vai trò sẽ tự động được đưa về quyền hạn của khách/công khai (Guest/public).
- [ ] Các nút bấm/đường liên kết dashboard trên trang chủ trỏ đúng tuyến đường trong code hiện tại.
- [ ] Nút `Đăng ký` trên trang chủ mở đúng đường dẫn `/register`.

## Các phần việc còn thiếu
- [ ] Chuẩn hóa việc ánh xạ mã vai trò (role ID) và tên vai trò (role name) trong tài liệu hệ thống để tránh nhầm lẫn.
- [ ] Bổ sung mã kiểm thử (test) cho đối tượng quyền truy cập `dashboardAccess` đối với từng vai trò.
