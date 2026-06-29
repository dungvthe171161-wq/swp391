# Đặc tả Phân hệ: Quản lý công việc của Trưởng phòng (Dept Manager Task Management)
Trạng thái: Đã phê duyệt
Tác nhân: Trưởng phòng (Dept Manager)
Độ ưu tiên: Cao
Mã nguồn liên quan: `DeptController`, `PostTask`, `TaskManager`, `com.hrm.controller.dept.ViewTask`, `Views/DeptManager/*`

## Phạm vi
Trưởng phòng (Dept Manager) thực hiện quản lý bảng điều khiển (dashboard) phòng ban, tạo công việc (task) mới, xem danh sách công việc và cập nhật tiến độ công việc.

## Các tập tin đặc tả tính năng (Feature files)
- `feature-dept-dashboard.spec.md`
- `feature-dept-task-create.spec.md`
- `feature-dept-task-list.spec.md`
- `feature-dept-task-update.spec.md`

## Các Route hiện có
- `/dept`
- `/postTask`
- `/taskManager`
- `/viewTask`

## Phân quyền hiện có
- Bộ lọc `RoleAuthorizationFilter` cấp quyền cho vai trò số 3 (role 3 - Dept Manager) truy cập các đường dẫn: `/dept`, `/taskManager`, `/viewTask`, `/postTask`.
- Bộ lọc `ModulePermissionFilter` yêu cầu quyền hạn `VIEW_DEPARTMENTS` đối với các tuyến đường (route) này.

## Các cảnh báo trong mã nguồn
- Cả hai lớp `com.hrm.controller.dept.ViewTask` và `com.hrm.controller.employee.ViewTask` hiện tại đều đang khai báo nhãn đường dẫn trùng nhau là `@WebServlet("/viewTask")`. Đây là một xung đột ánh xạ (mapping conflict) cần được xử lý khi thực hiện dọn dẹp và tối ưu mã nguồn (clean code).
- Tài liệu đặc tả phòng ban (Dept spec) này chỉ tập trung mô tả luồng hoạt động của Trưởng phòng (Dept Manager), không bao gồm luồng xem công việc của Nhân viên (Employee view task).
