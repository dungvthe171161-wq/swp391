# Tính năng Dept: Danh sách nhân viên phòng ban

Trạng thái: Hiện trạng đọc dữ liệu cơ bản, đã rà soát theo code ngày 2026-07-14; chưa phải chức năng quản lý nhân viên hoàn chỉnh.
Ngôn ngữ: tiếng Việt có dấu. Spec này không mặc định các chức năng tương lai đã được triển khai.

## Actor và phạm vi
- Dept Manager xem nhân viên thuộc đúng phòng ban mình quản lý.

## Route, controller và JSP liên quan
- `GET /dept?action=employees`.
- Controller: `DeptController`; scope: `DeptManagerScope`; DAO: `EmployeeDAO`.
- JSP: `Views/DeptManager/employees.jsp`.

## Hiện trạng code
- Controller dùng `employeeDAO.getByDepartmentId` và forward danh sách sang JSP.
- Màn hình dùng chung dữ liệu/thuộc tính với dashboard Dept.
- Chưa có controller riêng cho tìm kiếm, phân trang, export hoặc cập nhật nhân viên.

## Quy tắc nghiệp vụ chuẩn
- Manager chỉ xem nhân viên trong phòng ban của mình; không nhận `departmentId` tùy ý từ client.
- Dữ liệu nhạy cảm như lương, tài khoản, giấy tờ và thông tin liên hệ phải ẩn nếu không có permission riêng.
- Mặc định chỉ hiển thị nhân viên active; nếu có lịch sử/inactive phải có bộ lọc và quyền rõ.
- Chức năng này là read-only; cập nhật hồ sơ/chuyển phòng ban thuộc HR Manager hoặc actor được cấp quyền.
- Tìm kiếm, sort và pagination phải được validate và ổn định.

## Code còn lệch spec hoặc cần bổ sung
- Cần xác định tập trường được phép hiển thị và che dữ liệu cá nhân.
- Chưa có pagination/search phía server và permission riêng `VIEW_DEPARTMENT_EMPLOYEES`.
- Cần làm rõ hành vi với manager chưa gắn Employee/Department.

## Kiểm thử tối thiểu
- Không xem được nhân viên phòng ban khác bằng sửa query hoặc ID.
- Trường nhạy cảm không xuất hiện trong HTML khi thiếu quyền.
- Danh sách rỗng, manager chưa gắn phòng ban và employee inactive hiển thị đúng.
- Nếu bổ sung pagination/search, kiểm tra input quá dài và ký tự đặc biệt.
