# Chỉ mục đặc tả HRMS

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: toàn bộ spec phải viết bằng tiếng Việt có dấu; tên class, route, bảng và permission được giữ nguyên theo code.

## Nguyên tắc đọc spec
- Spec trong `.cursor/Spec` là nguồn mô tả nghiệp vụ theo từng actor: `Auth`, `Admin`, `Dept`, `Employee`, `HrStaff`, `HrManager`, `Guest`, `PublicCandidate` và `AI`.
- Mỗi spec phải nêu rõ route/controller/JSP liên quan, hiện trạng code, quy tắc nghiệp vụ chuẩn, phần code còn lệch spec và kiểm thử tối thiểu.
- Khi code hiện tại chưa đạt thiết kế mong muốn, spec phải ghi vào mục “Code còn lệch spec hoặc cần bổ sung”; không được viết như thể chức năng đã hoàn thành.
- Chỉ sửa UI thì không đổi route, `form action`, tên input, enum hoặc attribute controller.
- Session đăng nhập dùng attribute `systemUser`; dashboard sau đăng nhập đi qua `RoleRedirectUtil`.

## Actor và route dashboard
- `Admin` có `RoleID = 1`, dashboard `/admin?action=dashboard`.
- `HR Manager` có `RoleID = 2`, dashboard `/HrHomeController`.
- `Dept Manager` có `RoleID = 3`, dashboard `/dept?action=dashboard`.
- `HR Staff` có `RoleID = 4`, dashboard `/hrstaff`.
- `Employee` có `RoleID = 5`, dashboard `/employee`.
- `Guest` có `RoleID = 6`, dashboard `/guest/dashboard`.

## Các spec dùng chung
- `_Common/permission-matrix.spec.md`: ma trận route, role, permission và filter.
- `_Common/status-workflow.spec.md`: enum nghiệp vụ hiện có trong database và workflow chuẩn.
- `_Common/security-auth-hardening.spec.md`: bảo mật đăng nhập, mật khẩu, session và reset password.
- `_Common/route-conflict-resolution.spec.md`: xung đột route `/viewTask`.
- `_Common/database-impact.spec.md`: bảng đọc/ghi theo module.
- `_Common/notification.spec.md`: thông báo dùng chung theo `SystemUser.UserID`.
- `_Common/ui-language-theme.spec.md`: chuẩn tiếng Việt và giao diện BetterHR.

## Kết quả đối chiếu code quan trọng
- Code compile thành công bằng Maven; warning hiện tại không chặn build.
- `/departments` đang là servlet quản lý phòng ban nhưng chưa nằm trong `ModulePermissionFilter` và chưa có kiểm tra quyền trong controller.
- `Dept/ViewTask` và `Employee/ViewTask` cùng khai báo servlet name `ViewTask` và cùng mapping `/viewTask`; cần tách route trước khi coi workflow task là ổn định.
- Workflow tuyển dụng hiện dùng enum database `Applied`, `Screening`, `Interview`, `Offered`, `Rejected`, `Withdrawn`, `Hired`; chưa có các trạng thái chi tiết như `OfferDraft` hoặc `OfferAccepted`.
- `ViewCV` đang nhận `guestId` và cập nhật `Guest.Status`; spec tuyển dụng mới phải yêu cầu thao tác theo `ApplicationID`.
- `CreateEmployeeController` hiện dùng quyền `VIEW_EMPLOYEES`, tạo hoặc promote tài khoản rồi xóa `Guest`; thiết kế chuẩn phải dùng `CREATE_EMPLOYEE` và giữ lịch sử ứng tuyển.
- Mật khẩu hiện đang so sánh/lưu dạng plain text trong `PasswordHash`; mọi spec bảo mật phải ghi rõ đây là hiện trạng cần hardening, không được mô tả như đã hash.
- `PayrollApprovalController` hiện dùng quyền `VIEW_USERS`; thiết kế chuẩn cần tách quyền phê duyệt payroll riêng.