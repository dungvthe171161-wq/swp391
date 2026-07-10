# Tính năng: Luồng công việc ứng tuyển của Guest
Trạng thái: Đã triển khai phần nộp hồ sơ
Tác nhân: Guest Candidate, HR, HR Staff
Độ ưu tiên: Cao
Dữ liệu liên quan: `Guest`, `CandidateProfile`, `Application`, `Recruitment`, `Interview`, `Offer`, `Notification`
Mã nguồn liên quan: `RecruitmentController`, `GuestPortalController`, `ApplicationDAO`, `CandidateProfileDAO`, `ViewCV`

## Mục tiêu
Mỗi lượt ứng tuyển được lưu trong `Application`, còn hồ sơ cá nhân/CV dùng lại được lưu trong `CandidateProfile`.

## Luồng chính
1. Guest xem danh sách hoặc chi tiết việc làm.
2. Guest bấm `Ứng tuyển ngay`.
3. Nếu chưa đăng nhập, hệ thống yêu cầu đăng nhập/dăng ký và quay lại đúng job sau khi login.
4. Nếu đã đăng nhập, hệ thống kiểm tra trùng application theo `GuestID + RecruitmentID`.
5. Nếu chưa có `CandidateProfile`, Guest hoàn thiện hồ sơ và xác nhận email.
6. Nếu đã có `CandidateProfile`, hệ thống bỏ qua form hồ sơ.
7. Hệ thống hiển thị màn xác nhận ứng tuyển.
8. Guest xác nhận, hệ thống tạo `Application`.
9. HR/HR Staff xử lý application qua các bước sàng lọc, phỏng vấn, offer, tuyển dụng.

## Luồng lần đầu
- Guest nhập hồ sơ ứng tuyển.
- Guest upload CV.
- Hệ thống gửi mã xác nhận email.
- Guest nhập mã.
- Hệ thống lưu `CandidateProfile`.
- Guest xác nhận ứng tuyển.
- Hệ thống tạo `Application`.

## Luồng các lần sau
- Guest bấm ứng tuyển.
- Nếu cần login thì login trước.
- Hệ thống thấy đã có `CandidateProfile`.
- Chỉ hiển thị màn xác nhận ứng tuyển.
- Guest xác nhận.
- Hệ thống tạo `Application` mới cho job mới.

## Quy tắc ngăn ứng tuyển trùng
- `Application` có unique `GuestID + RecruitmentID`.
- Controller kiểm tra trước khi hiển thị form/confirm.
- Nếu phát hiện trùng, hiển thị đúng thông báo `Bạn đã ứng tuyển công việc này.`.
- Không tạo thêm record mới.

## Ánh xạ trạng thái
- `Applied` -> Đã nộp hồ sơ.
- `Screening` -> Đang sàng lọc.
- `Interview` -> Phỏng vấn.
- `Offered` -> Đã gửi thư mời nhận việc.
- `Rejected` -> Từ chối.
- `Withdrawn` -> Đã rút hồ sơ.
- `Hired` -> Đã tuyển dụng.

## HR xem CV
Màn HR xem CV phải lấy CV theo thứ tự ưu tiên:
1. `Application.CV`.
2. `CandidateProfile.CVFilePath`.
3. Dữ liệu Guest cũ nếu application/profile mới chưa có CV.

Thông tin tuyển dụng trên màn HR lấy theo `Application.RecruitmentID`; nếu dữ liệu cũ chưa có application thì fallback theo `Guest.RecruitmentID`.

## Acceptance Criteria
- [x] Mỗi lượt ứng tuyển hợp lệ tạo một `Application`.
- [x] Không tạo thêm bản ghi thông tin cá nhân trong `Guest` khi ứng tuyển.
- [x] `Application` liên kết `CandidateProfile`.
- [x] Guest chỉ cần nhập profile một lần.
- [x] Guest không được ứng tuyển trùng cùng một job.
- [x] HR xem được CV của application tạo bởi luồng mới.
- [ ] Các bước Interview/Offer/Notification tiếp tục xử lý theo application trong spec riêng.
