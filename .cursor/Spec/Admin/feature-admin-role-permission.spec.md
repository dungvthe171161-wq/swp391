# Tính năng: Gán quyền hạn (permission) cho vai trò (role)
Trạng thái: Đã phê duyệt
Tác nhân: Admin
Độ ưu tiên: Cao
Mã nguồn liên quan: `AdminController`, `RolePermissionServlet`, `RolePermissionDAO`, `PermissionDAO`, `Admin/RolePermissionManager.jsp`

## Các Route
- Giao diện (UI): `GET /admin?action=role-permissions`
- API: `GET /admin/role-permissions/api`
- API: `POST /admin/role-permissions/api`

## Luồng giao diện (UI Flow)
1. Admin truy cập `/admin?action=role-permissions`.
2. Controller kiểm tra thông tin phiên làm việc (session).
3. Lấy danh sách vai trò (role) và bảng tổng hợp quyền hạn (permission summary).
4. Nhóm các quyền (group permission) theo danh mục (category), các danh mục có giá trị `null` hoặc rỗng sẽ được gom vào nhóm `Khác`.
5. Chuyển tiếp (forward) đến `Admin/RolePermissionManager.jsp`.

## Luồng API GET
1. Phía máy khách (Frontend) gọi `/admin/role-permissions/api`.
2. Nếu có tham số `roleId`, API sẽ trả về các quyền hạn và trạng thái đã gán (granted) của vai trò đó.
3. Phản hồi (Response) trả về dữ liệu dạng JSON mã hóa `UTF-8`.

## Luồng API POST
1. Frontend gửi dữ liệu JSON chứa `roleId`, `granted`, và danh sách `permissionIds` hoặc `permissionId`.
2. Servlet thực hiện phân tích cú pháp (parse) nội dung body bằng `request.getReader()`.
3. Nếu `granted = true` thì tiến hành gán quyền (permission).
4. Nếu `granted = false` thì thực hiện thu hồi/gỡ quyền (permission).
5. Trả về trạng thái phản hồi dạng JSON.

## Hiện trạng code
- API đã có sẵn trong hệ thống.
- Phần body gửi lên bị rỗng/sai định dạng sẽ trả về thông báo lỗi JSON (error message) kèm mã trạng thái HTTP 400, không trả về đối tượng rỗng.
- Bộ lọc quyền (Permission filter) yêu cầu quyền `MANAGE_ROLE_PERMISSIONS`.
- Vai trò Admin có RoleID = 1 mặc định được phép quản lý quyền hạn của vai trò (role permissions) theo logic `PermissionUtil`/bộ lọc hiện tại; các người dùng khác vẫn bắt buộc phải có quyền `MANAGE_ROLE_PERMISSIONS`.

## Giao ước giao diện (UI contract)
- Trang quản lý phân quyền sử dụng bố cục (layout) Admin BetterHR đồng bộ, tránh việc hiển thị đè giao diện cũ lên giao diện mới.
- Hộp chọn (Checkbox) phân quyền vai trò phải có trạng thái rõ ràng và tương tác nhấp chọn bình thường.
- Các nút chức năng `Lưu trạng thái`, `Bật tất cả`, `Tắt tất cả`, `Về Admin` hiển thị hoàn toàn bằng tiếng Việt.
- Thông báo Toast hiển thị lỗi/thành công cần được gom nhóm hợp lý, không hiển thị quá nhiều thông báo trùng lặp khi người dùng không đủ quyền.

## Tiêu chí nghiệm thu
- [ ] API GET trả về định dạng JSON UTF-8.
- [ ] Các quyền hạn được phân nhóm theo danh mục rõ ràng.
- [ ] Gửi request POST với body thiếu `roleId` hoặc danh sách quyền hạn sẽ bị trả về mã lỗi HTTP 400.
- [ ] Người dùng không có quyền `MANAGE_ROLE_PERMISSIONS` bị hệ thống chặn.
- [ ] Tài khoản Admin có RoleID = 1 mở và lưu được trang phân quyền ngay cả khi dữ liệu seed ban đầu bị thiếu quyền hạn của vai trò.
- [ ] Hộp chọn (Checkbox) trên giao diện có thể thay đổi trạng thái (toggle) nếu người dùng có quyền hợp lệ.

## Các phần việc còn thiếu
- [ ] Bổ sung các bài kiểm thử (test) cho việc cấp/thu hồi nhiều quyền cùng một lúc.
- [ ] Ghi nhật ký hệ thống (audit log) khi thay đổi cấu hình quyền hạn của một vai trò.
