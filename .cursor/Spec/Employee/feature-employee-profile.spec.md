# Tính năng Employee: Hồ sơ cá nhân

Trạng thái: Đã rà soát theo code ngày 2026-07-22.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Employee xem thông tin cá nhân trong Employee portal và có thể đi qua profile chung nếu dùng `/profilepage`.

## Route, controller và JSP liên quan
- `/employee/profile`: `EmployeePortalController`, JSP `Views/Employee/EmployeeProfile.jsp`.
- `/profilepage`: `ProfilepageController`, JSP `Views/Profilepage.jsp`.
- Bảng liên quan: `Employee`, `SystemUser`, `Department`, `Role`, `SystemLog`.

## Hiện trạng code
- `/employee/profile` chỉ forward `EmployeeProfile.jsp` sau khi `prepareEmployeeContext` xác định `systemUser.EmployeeID`.
- `/profilepage` là profile chung cho user đã đăng nhập, có action update profile và change password.
- `ProfilepageController` update `Employee.FullName`, `Employee.Email`, `Employee.Phone`; department/bio hiện chưa được ghi.
- Đổi mật khẩu trong `/profilepage` vẫn ghi plain text vào `SystemUser.PasswordHash`.

## Quy tắc nghiệp vụ chuẩn
- Employee chỉ sửa trường được phép như số điện thoại, địa chỉ, ảnh nếu nghiệp vụ cho phép.
- Email/role/department/salary do Admin hoặc HR quản lý nếu chưa có rule tự cập nhật.
- Validation phải trả thông báo tiếng Việt.
- Đổi mật khẩu phải tuân theo spec `_Common/security-auth-hardening.spec.md`.

## Code còn lệch spec hoặc cần bổ sung
- Cần xác định rõ trường nào Employee được sửa trong `/employee/profile` và `/profilepage`.
- Cần audit nếu sửa dữ liệu nhạy cảm.
- `/profilepage` chưa có CSRF và vẫn dùng plain password.
- Redirect chưa đăng nhập của `ProfilepageController` còn trỏ thẳng `/Views/Login.jsp`.

## Kiểm thử tối thiểu
- Employee chưa có `EmployeeID` không xem được `/employee/profile`.
- User chưa đăng nhập không xem được `/profilepage`.
- Update profile không cho sửa role/department/salary ngoài nghiệp vụ được phép.
- Change password sai current password bị chặn.
