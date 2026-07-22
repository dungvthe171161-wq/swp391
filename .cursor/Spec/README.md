# Chỉ mục đặc tả HRMS

Trạng thái: Đã rà soát theo code ngày 2026-07-22.
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
- `_Common/profile.spec.md`: hồ sơ user chung qua `/profilepage`.
- `_Common/route-conflict-resolution.spec.md`: tách route task Dept/Employee.
- `_Common/database-impact.spec.md`: bảng đọc/ghi theo module.
- `_Common/notification.spec.md`: thông báo dùng chung theo `SystemUser.UserID`.
- `_Common/file-access-data-privacy.spec.md`: bảo vệ CV, contract document và chữ ký.
- `_Common/ui-language-theme.spec.md`: chuẩn tiếng Việt và giao diện BetterHR.

## Kết quả đối chiếu code quan trọng
- `/departments` hiện có `DepartmentController`, đã được bảo vệ bởi `RoleAuthorizationFilter`, `ModulePermissionFilter` và guard `PermissionUtil` trong controller.
- Department CRUD vẫn cần tách permission action `CREATE_DEPARTMENT`, `EDIT_DEPARTMENT`, `DELETE_DEPARTMENT` và audit nếu muốn hardening đầy đủ.
- Chi tiết task Dept hiện dùng `/viewTask` qua servlet `ViewTask`; Employee có danh sách/cập nhật nhanh tại `/employee/tasks` và servlet detail `EmployeeViewTask` mapping `/employee/view-task`.
- Employee schedule hiện có `/employee/schedule`; Employee contract có `/employee/contract/document` và ký bằng `POST /employee/contract`.
- Workflow tuyển dụng dùng enum database `Applied`, `Screening`, `Interview`, `Offered`, `Rejected`, `Withdrawn`, `Hired`; `Application.CurrentStep` dùng `Offer` khi ở bước offer.
- `ViewCV` ưu tiên tham số `applicationId`; chỉ còn fallback `guestId` legacy.
- `CreateEmployeeController` dùng quyền `VIEW_EMPLOYEES`, tạo hoặc promote tài khoản rồi set `Guest.Status = Converted` (không xóa `Guest`).
- Mật khẩu hiện đang so sánh/lưu dạng plain text trong `PasswordHash`; mọi spec bảo mật phải ghi rõ đây là hiện trạng cần hardening, không được mô tả như đã hash.
- `PayrollApprovalController` hiện dùng quyền `VIEW_USERS`; thiết kế chuẩn cần tách quyền phê duyệt payroll riêng.
- `DBConnectionTest`, `SimpleHrController`, `HrHomeSimple.jsp` và `TestHrHome.jsp` là luồng diagnostic/legacy cần giới hạn hoặc loại khỏi production.
