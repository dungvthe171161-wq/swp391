# Feature Tree 09 - Kế hoạch triển khai Quản trị hệ thống

- **Trạng thái:** Bản nháp
- **Phạm vi chuẩn:** Feature Tree 09
- **Phụ thuộc:** Plan 01 và 10
- **Cung cấp master data cho:** Plan 04, 05, 07 và 08

## 1. Mục tiêu

Cung cấp chức năng quản trị có bảo vệ bằng permission cho User, Role, RolePermission, quyền ghi đè UserPermission, Department, OfficeLocation, AuditLog và dữ liệu Chatbot FAQ/review, đồng thời giữ toàn vẹn tham chiếu và ghi nhận mọi thay đổi nhạy cảm.

## 2. Ranh giới phạm vi

Plan này chịu trách nhiệm màn hình và mutation quản trị. Plan 01 chịu trách nhiệm ngữ nghĩa xác thực và thực thi permission lúc chạy. Plan 08 chịu trách nhiệm cách OfficeLocation được dùng cho GPS Attendance. Plan 10 chịu trách nhiệm Chatbot runtime và gửi Notification; plan này chỉ quản lý cấu hình FAQ và UI review.

## 3. Code hiện tại cần audit và tái sử dụng

### Controller

- `admin/AdminController`
- `admin/UserController`
- `admin/RoleServlet`
- `admin/RolePermissionServlet`
- `admin/DepartmentController`
- `admin/OfficeLocationController`
- `admin/ChatbotFaqController`
- `AdminAuthorizationFilter`, `ModulePermissionFilter`

### DAO và model

- `SystemUserDAO`, `RoleDAO`, `PermissionDAO`, `RolePermissionDAO`, `UserPermissionDAO`
- `DepartmentDAO`, `OfficeLocationDAO`, `SystemLogDAO`
- `ChatbotFaqDAO`, `ChatbotReviewDAO`, `ChatbotFeedbackDAO`
- Các entity và permission DTO tương ứng

### Giao diện

- `Admin/AdminHome.jsp`, `Users.jsp`, `Roles.jsp`, `RolePermissionManager.jsp`
- `Admin/Departments.jsp`, `office-location.jsp`, `AuditLog.jsp`
- `Views/AI/AI_Faq_Management.jsp`
- CSS và JavaScript Admin dùng chung

## 4. Luồng code lúc chạy

```text
Request Admin
  -> Xác thực + quyền Admin/module của Plan 01
  -> Controller Admin chuyên trách kiểm tra input và trạng thái hiện tại mong đợi
  -> Admin service áp dụng master-data rule và quy tắc an toàn
  -> DAO transaction cập nhật record mục tiêu và SystemLog
  -> commit
  -> Plan 10 gửi Notification tùy chọn cho Admin/User
  -> Post/Redirect/Get hoặc JSON result
```

## 5. Các bất biến quản trị

- `/admin/*` và Admin API yêu cầu permission rõ ràng, không chỉ dựa vào việc có link trên trang.
- Admin không thể vô tình xóa khả năng quản trị viên cuối cùng của hệ thống.
- Thứ tự ưu tiên RolePermission và UserPermission phải khớp thực thi của Plan 01.
- Xóa master data đang được tham chiếu phải bị chặn hoặc chuyển thành thao tác deactivate đã tài liệu hóa.
- Audit record chỉ được append đối với người dùng và Admin thông thường.
- Credential reset và secret được sinh không được ghi log hoặc trả về qua API danh sách.
- Filter và phân trang bảng Admin chạy ở backend và giữ điều kiện phân quyền.

## 6. Các task triển khai

### Task 1 - Kiểm kê route, action và permission bắt buộc

- [ ] Kiểm kê `/admin`, `/admin/users`, `/admin/role/*`, RolePermission API, `/departments`, OfficeLocation, FAQ và Audit route.
- [ ] Ánh xạ mỗi action GET/POST/API tới permission rõ ràng từ Plan 01.
- [ ] Xác định action switch quá rộng trong `AdminController` cần chuyển sang controller/service chuyên trách.
- [ ] Ghi nhận form action và hợp đồng JavaScript API hiện tại trước khi refactor.
- [ ] Thêm baseline test cho chưa xác thực, không phải Admin, Admin thiếu permission và Admin được phép đầy đủ.

### Task 2 - Tạo pattern dùng chung cho command/service Admin

- [ ] Định nghĩa typed result cho validation, not-found, duplicate, stale-state, dependency-conflict và permission.
- [ ] Chuyển mutation nhiều bảng và nhạy cảm với audit từ controller sang service chuyên trách.
- [ ] Thêm DAO method nhận Connection khi cập nhật mục tiêu và `SystemLog` phải commit cùng nhau.
- [ ] Dùng Post/Redirect/Get cho mutation JSP và HTTP status có cấu trúc cho JSON API.
- [ ] Thay `System.out` trong controller bằng SLF4J.

### Task 3 - Quản trị lifecycle User

- [ ] Định nghĩa account status hợp lệ và quy tắc lock/unlock/deactivate.
- [ ] Kiểm tra username, email, role, Employee/Guest liên kết và định danh trùng.
- [ ] Tạo User với mật khẩu BCrypt hoặc luồng activation/reset dùng một lần.
- [ ] Ngăn form Admin đặt trực tiếp security metadata nội bộ.
- [ ] Triển khai search, filter status/role, sort và pagination an toàn.
- [ ] Làm cập nhật lock/unlock và đổi role có điều kiện theo trạng thái mong đợi.
- [ ] Ngăn tự khóa và loại bỏ Admin khả dụng cuối cùng theo rule đã tài liệu hóa.
- [ ] Triển khai reset mật khẩu mà không làm lộ mật khẩu plaintext có thể tái sử dụng.
- [ ] Gửi Notification cho User bị ảnh hưởng qua Plan 10 khi phù hợp.
- [ ] Thêm test duplicate, tự thao tác, Admin cuối, stale-state và sai permission.

### Task 4 - Lifecycle Role

- [ ] Định nghĩa role hệ thống bất biến và role tùy chỉnh có thể sửa.
- [ ] Kiểm tra tên Role duy nhất, description và active state.
- [ ] Chặn xóa Role đang được User tham chiếu hoặc triển khai workflow gán lại/deactivate trong transaction.
- [ ] Ngăn sửa invariant mà Plan 01 yêu cầu.
- [ ] Thêm danh sách/tìm kiếm Role và action theo trạng thái.
- [ ] Audit tạo, cập nhật, activate/deactivate và thao tác sửa role được bảo vệ bị từ chối.
- [ ] Thêm test role đang tham chiếu, tên trùng, role được bảo vệ và concurrency.

### Task 5 - Ánh xạ RolePermission

- [ ] Load catalog Permission chuẩn và nhóm theo module/action.
- [ ] Định nghĩa thiếu mapping có nghĩa là deny hay không và cách biểu diễn allow/deny rõ ràng.
- [ ] Cập nhật tập Permission của Role trong một transaction.
- [ ] Từ chối PermissionID không tồn tại, inactive hoặc trùng.
- [ ] Bảo vệ RolePermission API bằng CSRF/authorization phù hợp với dự án.
- [ ] Trả version hoặc trạng thái hiện tại mong đợi để phát hiện chỉnh sửa đồng thời cũ khi phù hợp.
- [ ] Audit Permission được thêm/xóa dưới dạng diff.
- [ ] Thêm test replace permission, stale edit, ID không hợp lệ và role được bảo vệ.

### Task 6 - Quyền ghi đè UserPermission

- [ ] Tài liệu hóa thứ tự ưu tiên allow/deny override cùng Plan 01.
- [ ] Cung cấp view/API Admin cho quyền hiệu lực từ Role và override hiện tại.
- [ ] Ngăn User-Permission override trùng.
- [ ] Định nghĩa hành vi reset về mặc định của Role.
- [ ] Cảnh báo hoặc chặn override làm mất quyền truy cập chức năng khôi phục quan trọng của chính Admin.
- [ ] Audit giá trị override cũ và mới.
- [ ] Thêm test ma trận permission hiệu lực gồm Role allow/deny và User allow/deny.

### Task 7 - Master data Department

- [ ] Kiểm tra tên/code Department, description, manager assignment và active state.
- [ ] Thực thi identifier Department duy nhất.
- [ ] Xác minh manager được gán là Employee đủ điều kiện và xử lý mapping vòng lặp/không nhất quán.
- [ ] Chặn hard delete khi Employee, Recruitment, Task hoặc Schedule tham chiếu Department.
- [ ] Ưu tiên deactivate với quy trình gán lại đã tài liệu hóa.
- [ ] Giữ runtime scope của Department Manager tương thích Plan 04.
- [ ] Audit create, update, đổi manager, deactivate và delete bị từ chối.
- [ ] Thêm test referential integrity và manager scope.

### Task 8 - Master data OfficeLocation

- [ ] Kiểm tra name/address, latitude, longitude, radius và active state.
- [ ] Định nghĩa chính sách một/nhiều OfficeLocation active và quan hệ Employee/Department nếu cần.
- [ ] Từ chối tọa độ không hợp lệ, radius không dương và record active xung đột theo policy.
- [ ] Chặn delete khi lịch sử Attendance tham chiếu OfficeLocation; dùng deactivate.
- [ ] Giữ giá trị location lịch sử dùng làm bằng chứng Attendance.
- [ ] Audit thay đổi location và radius vì ảnh hưởng điều kiện chấm công.
- [ ] Thêm test tọa độ, radius, active policy và location đang được tham chiếu.

### Task 9 - Quản trị AuditLog

- [ ] Định nghĩa trường bắt buộc: actor, action, affected type/ID, timestamp, outcome và chi tiết an toàn.
- [ ] Bảo đảm hành động authentication, User, Role, Permission, Department, Location, Recruitment, Contract, Payroll và Leave có thể ghi audit event.
- [ ] Giữ quyền Admin thông thường ở chế độ chỉ đọc; không có endpoint edit/delete audit event.
- [ ] Thêm filter backend cho actor, action, entity, outcome và khoảng ngày.
- [ ] Che secret, token, dữ liệu mật khẩu, chữ ký, đường dẫn CV và dữ liệu cá nhân quá mức.
- [ ] Định nghĩa retention/export policy nếu dự án yêu cầu.
- [ ] Thêm test transaction ghi audit và permission xem audit.

### Task 10 - Quản trị Chatbot FAQ và review Feedback

- [ ] Tạo/cập nhật câu hỏi, câu trả lời, category/intent, role scope, keyword, thứ tự và active state của FAQ.
- [ ] Kiểm tra độ dài text và ngăn markup không an toàn render không escape.
- [ ] Cung cấp activate/deactivate thay vì delete phá hủy khi lịch sử tham chiếu FAQ.
- [ ] Hiển thị truy vấn fallback và Feedback tiêu cực gần đây để review.
- [ ] Cho phép chuyển khoảng trống đã review thành FAQ mới/cập nhật bằng hành động Admin rõ ràng.
- [ ] Giữ phần trả lời Chatbot runtime, History và Gemini trong Plan 10.
- [ ] Audit thay đổi publish FAQ.
- [ ] Thêm test permission, validation, FAQ inactive và output escaping.

### Task 11 - UI nhất quán và API an toàn

- [ ] Dùng layout, navigation, pagination và filter Admin dùng chung.
- [ ] Giữ hợp đồng form/JavaScript hiện có hoặc cung cấp thay đổi tương thích trong cùng task.
- [ ] Thêm xác nhận cho action phá hủy/deactivate nhưng không coi confirm là authorization.
- [ ] Trả JSON đúng content type và status cho validation, conflict, unauthorized, forbidden và server error.
- [ ] Escape giá trị do người dùng kiểm soát trong JSP và JavaScript.
- [ ] Kiểm tra nội dung tiếng Việt UTF-8 và bảng responsive.

### Task 12 - Xác minh

- [ ] Chạy test User, Role, effective-permission, Department, OfficeLocation, Audit, FAQ, controller và API.
- [ ] Chạy `mvn test`, `mvn -q compile` và `mvn -q package`.
- [ ] Kiểm thử thủ công từng trang Admin với quyền đầy đủ và Admin bị giới hạn quyền.
- [ ] Thử tự khóa, xóa Admin cuối, sửa role được bảo vệ, xóa master data đang tham chiếu và cập nhật tọa độ không hợp lệ.
- [ ] Xác nhận mỗi mutation nhạy cảm thành công tạo đúng một audit record an toàn.
- [ ] Xác nhận không có mật khẩu, reset token, secret OAuth/SMTP/Gemini, chữ ký hoặc nội dung CV xuất hiện trong log/API result.

## 7. Tiêu chí hoàn thành

- Route và API Admin được bảo vệ bằng permission rõ ràng của Plan 01.
- Action User, Role, Permission, Department, Location, Audit và FAQ dùng service chuyên trách có validation.
- Hành vi permission hiệu lực khớp với thực thi runtime.
- Master data đang được tham chiếu không bị xóa phá hủy.
- Thay đổi nhạy cảm được audit trong transaction mà không làm lộ secret.
- Test, compile, package, Admin giới hạn quyền và kiểm tra quản trị thủ công đầy đủ đều đạt.

