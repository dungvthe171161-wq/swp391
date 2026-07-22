# Đặc tả dùng chung: Ma trận phân quyền

Trạng thái: Đã rà soát theo code ngày 2026-07-22.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Tất cả actor đăng nhập vào BetterHR; spec này mô tả lớp filter, role và permission đang chi phối truy cập.

## Route, controller và JSP liên quan
- `RoleRedirectUtil`: điều hướng dashboard theo `RoleID` 1-6.
- `RoleAuthorizationFilter`: bảo vệ các route `/hr/`, `/hrstaff`, `/dept`, `/employee`, `/guest`, `/departments` và một số route legacy.
- `ModulePermissionFilter`: bảo vệ `/admin`, `/departments`, `/dept`, `/taskManager`, `/postTask`, `/viewTask`, `/employee` và `/employee/*`.
- `PermissionUtil`: kiểm tra `systemUser` và permission động theo `RolePermission`/`UserPermission`.

## Hiện trạng code
- Role seed hiện có: Admin, HR Manager, Dept Manager, HR Staff, Employee, Guest.
- Admin trong `PermissionUtil.hasPermission` được bypass permission động khi `RoleID = 1`.
- Nhiều rule trong `ModulePermissionFilter` chỉ kiểm tra permission vì `requiredRoleId = null`.
- `/candidates` và `/viewCV` đang được `RoleAuthorizationFilter` cho cả HR Manager và HR Staff truy cập.
- `/departments` đã được bảo vệ bằng `RoleAuthorizationFilter` cho Admin, `ModulePermissionFilter` với `VIEW_DEPARTMENTS`, và guard trong `DepartmentController`.

## Quy tắc nghiệp vụ chuẩn
- Mọi route quản trị phải có cả role phù hợp và permission phù hợp.
- Permission phải phản ánh đúng thao tác: xem, tạo, sửa, xóa, phê duyệt không dùng chung một mã quyền nếu nghiệp vụ khác nhau.
- Controller quan trọng phải kiểm tra lại quyền, không chỉ dựa vào ẩn nút trên JSP.
- Tên permission phải giữ đúng theo seed/code, ví dụ department hiện có `CREATE_DEPARTMENT`, `EDIT_DEPARTMENT`, `DELETE_DEPARTMENT`.

## Code còn lệch spec hoặc cần bổ sung
- Department CRUD vẫn dùng chung `VIEW_DEPARTMENTS`, chưa tách `CREATE_DEPARTMENT`, `EDIT_DEPARTMENT`, `DELETE_DEPARTMENT` và chưa có audit đầy đủ.
- Tách quyền `CREATE_EMPLOYEE`, `APPROVE_PAYROLL`, `APPROVE_CONTRACT`, `MANAGE_APPLICANTS`, `SCHEDULE_INTERVIEW`, `CREATE_RECRUITMENT`, `EDIT_RECRUITMENT` nếu code triển khai workflow đầy đủ.
- Sửa các controller đang dùng quyền quá rộng: create employee dùng `VIEW_EMPLOYEES`, approve payroll dùng `VIEW_USERS`, approve contract dùng `VIEW_CONTRACTS`.
- Dept create/update task vẫn dùng `VIEW_DEPARTMENTS`, chưa có permission riêng.
- Employee schedule/payroll/contract cá nhân chưa dùng permission riêng như `VIEW_OWN_WORK_SCHEDULE`.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.
