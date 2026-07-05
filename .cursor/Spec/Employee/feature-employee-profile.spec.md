# Tính năng Employee: Hồ sơ cá nhân

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Employee xem và cập nhật thông tin cá nhân được phép.

## Route, controller và JSP liên quan
- `/employee`, `/employee/*`.
- Controller chính: `EmployeePortalController`.
- JSP: `Views/Employee/EmployeeHome.jsp`, `Tasks.jsp`, `Leaves.jsp`, `Payroll.jsp`, `Contract.jsp`, `Attendance.jsp`, `EmployeeProfile.jsp`.

## Hiện trạng code
- `/employee/profile` do `EmployeePortalController` hoặc controller profile chung xử lý tùy luồng.
- JSP `EmployeeProfile.jsp` hiển thị dữ liệu cá nhân.
- Một số thông tin nhạy cảm không nên tự sửa nếu thuộc HR quản lý.

## Quy tắc nghiệp vụ chuẩn
- Employee chỉ sửa trường được phép như số điện thoại, địa chỉ, ảnh nếu nghiệp vụ cho phép.
- Email/role/department/salary do Admin hoặc HR quản lý.
- Validation phải trả thông báo tiếng Việt.

## Code còn lệch spec hoặc cần bổ sung
- Cần xác định rõ trường nào Employee được sửa.
- Cần audit nếu sửa dữ liệu nhạy cảm.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

