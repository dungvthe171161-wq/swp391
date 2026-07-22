# Đặc tả dùng chung: Hồ sơ người dùng

Trạng thái: Đã rà soát theo code ngày 2026-07-22.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Mọi user đã đăng nhập có `systemUser`; Employee có thêm view profile trong cổng `/employee/profile`.

## Route, controller và JSP liên quan
- `GET /profilepage`, `POST /profilepage`.
- Controller: `ProfilepageController`.
- JSP: `Views/Profilepage.jsp`; Employee portal còn có `Views/Employee/EmployeeProfile.jsp` tại `/employee/profile`.
- DAO/bảng: `SystemUser`, `Employee`, `Department`, `Role`, `SystemLog`, `DAO`.

## Hiện trạng code
- `ProfilepageController` lấy user từ session attribute `systemUser`; nếu chưa đăng nhập thì redirect về `/Views/Login.jsp`.
- Không có `action` thì controller tải `UserProfile`, `UserActivity`, `UserStats` và forward tới `Views/Profilepage.jsp`.
- `action=update` cập nhật `Employee.FullName`, `Employee.Email`, `Employee.Phone` theo `SystemUser.EmployeeID`; tham số `department` và `bio` hiện chưa được ghi.
- `action=changePassword` kiểm tra mật khẩu hiện tại bằng `DAO.checkPassword`, sau đó ghi mật khẩu mới trực tiếp vào `SystemUser.PasswordHash`.
- Controller ghi `SystemLog` cho `PROFILE_UPDATE` và `PASSWORD_CHANGE`.

## Quy tắc nghiệp vụ chuẩn
- User chỉ được xem/sửa hồ sơ của chính mình.
- Các trường role, department, salary, status và dữ liệu nhạy cảm phải do Admin/HR quản lý, không cho tự sửa qua profile chung.
- Email/phone cần validate định dạng và kiểm tra trùng nếu dùng làm định danh liên hệ.
- Đổi mật khẩu phải dùng hash mạnh, kiểm tra độ mạnh mật khẩu, rate limit và CSRF token.
- Điều hướng khi chưa đăng nhập nên thống nhất về `/login` thay vì link thẳng JSP.

## Code còn lệch spec hoặc cần bổ sung
- Mật khẩu vẫn plain text; `ProfilepageController.updatePassword` ghi trực tiếp `newPassword` vào cột `PasswordHash`.
- Chưa có CSRF token cho update profile và change password.
- Chưa validate định dạng/trùng email, phone ở `ProfilepageController`.
- Redirect chưa đăng nhập còn đi thẳng `/Views/Login.jsp`, chưa thống nhất route `/login`.
- `UserStats.loginCount` đang dùng tổng số log theo action `LOGIN`, chưa chắc là số lần đăng nhập của riêng user hiện tại.

## Kiểm thử tối thiểu
- User chưa đăng nhập không mở được `/profilepage`.
- User A không thể cập nhật hồ sơ User B bằng cách sửa request.
- Update profile chỉ ghi các trường được phép và ghi `SystemLog`.
- Đổi mật khẩu sai current password bị chặn; đổi thành công đăng nhập được bằng mật khẩu mới sau khi hardening tương thích hash.
