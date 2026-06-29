# Feature: Guest cập nhật hồ sơ cá nhân và hồ sơ ứng tuyển
Status: Implemented for candidate profile section
Actor: Guest Candidate
Priority: High
Related Code: `GuestPortalController`, `CandidateProfileDAO`, `Views/Guest/Profile.jsp`, `Views/Guest/ProfileEmailVerify.jsp`, `css/guest.css`

## Route hiện tại
- `GET /guest/profile`
- `POST /guest/profile`
- `POST /guest/profile?action=verifyCandidateProfileEmail`
- `POST /guest/profile?action=resendCandidateProfileCode`

## Mục tiêu
Trang Guest Profile quản lý hai nhóm thông tin:
- Thông tin cá nhân/tài khoản Guest hiện có.
- `Hồ sơ ứng tuyển` dùng lại cho các lần apply sau.

## Hồ sơ ứng tuyển cho phép chỉnh sửa
- Họ và tên.
- Số điện thoại.
- Email ứng tuyển.
- Ngày sinh.
- Địa chỉ.
- Vị trí mong muốn.
- Mức lương mong muốn.
- Kinh nghiệm làm việc.
- CV.

## Quy tắc lưu
- Nếu chưa có `CandidateProfile`, tạo mới sau khi xác nhận email.
- Nếu đã có `CandidateProfile` và email không đổi/đã xác nhận, cập nhật trực tiếp.
- Nếu email thay đổi hoặc profile chưa xác nhận email, hệ thống gửi mã xác nhận.
- Mã xác nhận lưu trong `HttpSession`, không lưu database.
- Sau khi xác nhận mã hợp lệ, insert/update `CandidateProfile`.
- Các lần ứng tuyển sau sử dụng thông tin mới nhất trong `CandidateProfile`.

## Validation
- Họ tên bắt buộc.
- Email đúng định dạng.
- Phone đúng định dạng cơ bản.
- DateOfBirth không được lớn hơn ngày hiện tại.
- CV chỉ chấp nhận PDF, DOC, DOCX.
- CV tối đa 10MB.
- Nếu không upload CV mới khi cập nhật, giữ CV cũ.

## Acceptance Criteria
- [x] Guest đã login xem được profile của mình.
- [x] Guest cập nhật được thông tin hồ sơ ứng tuyển.
- [x] Guest không cập nhật được profile của người khác.
- [x] Đổi email hồ sơ ứng tuyển cần xác nhận email.
- [x] Không tạo database riêng cho mã xác nhận email.
- [x] Thông tin `CandidateProfile` tự động được dùng cho các lần apply sau.
