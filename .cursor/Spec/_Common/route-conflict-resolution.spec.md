# Đặc tả dùng chung: Xử lý xung đột route

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Dept Manager và Employee cùng có chức năng xem task; spec này chốt cách xử lý route bị trùng.

## Route, controller và JSP liên quan
- `com.hrm.controller.dept.ViewTask`: `@WebServlet(name = "ViewTask", urlPatterns = {"/viewTask"})`.
- `com.hrm.controller.employee.ViewTask`: cũng khai báo name `ViewTask` và mapping `/viewTask`.
- `ModulePermissionFilter` và `RoleAuthorizationFilter` đang coi `/viewTask` là route của Dept Manager.

## Hiện trạng code
- Build Java vẫn compile, nhưng servlet container có thể lỗi khi deploy vì trùng name/mapping.
- Employee portal mới đã có `/employee/tasks` trong `EmployeePortalController`.
- Dept legacy task vẫn dùng `/taskManager`, `/postTask`, `/viewTask`.

## Quy tắc nghiệp vụ chuẩn
- Route Dept phải nằm dưới `/dept/*`, ví dụ `/dept/tasks`, `/dept/tasks/create`, `/dept/tasks/detail`.
- Route Employee phải nằm dưới `/employee/*`, ví dụ `/employee/tasks` và `/employee/tasks/detail`.
- Không dùng chung servlet name cho hai controller khác actor.

## Code còn lệch spec hoặc cần bổ sung
- Cần đổi mapping legacy `/viewTask` trước khi test task theo actor.
- Cần cập nhật JSP link và filter sau khi tách route.
- Cần kiểm tra lại permission vì `/viewTask` hiện yêu cầu `VIEW_DEPARTMENTS`.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

