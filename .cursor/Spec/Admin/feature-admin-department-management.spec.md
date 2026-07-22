# Tính năng Admin: Quản lý phòng ban

Trạng thái: Đã rà soát theo code ngày 2026-07-22.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Admin quản lý phòng ban và dữ liệu liên quan tới Dept Manager.

## Route, controller và JSP liên quan
- `/departments`.
- Controller: `DepartmentController`.
- Filter/guard: `RoleAuthorizationFilter`, `ModulePermissionFilter`, `PermissionUtil` trong `DepartmentController`.
- JSP: `Admin/Departments.jsp`; các JSP legacy có thể được gọi trong nhánh edit/permissions nếu còn tồn tại.

## Hiện trạng code
- `DepartmentController` mapping `/departments`.
- `/departments` đã được thêm vào `RoleAuthorizationFilter` cho Admin, `ModulePermissionFilter` với `VIEW_DEPARTMENTS`, và `DepartmentController.ensureAccess` kiểm tra `ROLE_ADMIN` + `VIEW_DEPARTMENTS`.
- List/search/sort/paging phòng ban submit và redirect về `/departments?action=departments`.
- CRUD phòng ban qua `DepartmentController` hiện vẫn dùng chung quyền `VIEW_DEPARTMENTS`, chưa áp dụng permission action riêng.
- `Admin/Departments.jsp` có hằng JS `DEPARTMENT_API_URL = /admin/department`, nhưng source chưa thấy servlet tương ứng; form chính vẫn submit về `/departments`.
- Nhánh `department-permissions-save` trong `DepartmentController` hiện chỉ in danh sách permission ra console, chưa lưu quan hệ quyền thật.

## Quy tắc nghiệp vụ chuẩn
- Chỉ Admin hoặc role được cấp quyền quản lý phòng ban mới được truy cập route quản lý phòng ban.
- Tạo/sửa/xóa phòng ban nên dùng permission riêng: `CREATE_DEPARTMENT`, `EDIT_DEPARTMENT`, `DELETE_DEPARTMENT`.
- Không xóa phòng ban đang có employee/task nếu chưa có chính sách xử lý.
- Dữ liệu phòng ban phải giữ toàn vẹn khóa ngoại và ghi audit cho thao tác ghi.

## Code còn lệch spec hoặc cần bổ sung
- Department CRUD thiếu permission tách theo thao tác `CREATE_DEPARTMENT`, `EDIT_DEPARTMENT`, `DELETE_DEPARTMENT`.
- Cần thêm audit cho tạo/sửa/xóa department.
- Rà lại hoặc bỏ hằng JS `/admin/department` nếu không dùng endpoint đó.
- Nhánh `department-permissions-save` chưa có lưu dữ liệu thật và còn `System.out`.
- Reset password và quản lý user/role/permission còn thiếu audit đầy đủ.

## Kiểm thử tối thiểu
- User không đăng nhập bị redirect/chặn khi truy cập `/departments`.
- User không phải Admin không truy cập được `/departments`.
- Admin mở danh sách, tìm kiếm, phân trang, tạo/sửa/xóa department qua `/departments?action=departments`.
- Xóa department đang có employee bị chặn.
