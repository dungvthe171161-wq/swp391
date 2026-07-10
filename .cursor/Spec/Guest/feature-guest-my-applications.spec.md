# Tính năng: Guest xem danh sách hồ sơ đã ứng tuyển
Trạng thái: Cập nhật theo schema Application/CandidateProfile
Tác nhân: Guest Candidate
Độ ưu tiên: Cao
Mã nguồn liên quan: `GuestPortalController`, `ApplicationDAO`, `CandidateProfileDAO`, `RecruitmentDAO`, `InterviewDAO`, `OfferDAO`

## Route mục tiêu
- `GET /guest/applications`

## Nguồn dữ liệu chính
Danh sách hồ sơ đã ứng tuyển lấy từ `Application`:
- `Application.GuestID -> Guest.GuestID`.
- `Application.RecruitmentID -> Recruitment.RecruitmentID`.
- `Application.CandidateProfileID -> CandidateProfile.CandidateProfileID`.
- Lọc theo tài khoản đang đăng nhập: `Guest.UserID = session.systemUser.UserID`.

Không dùng `Guest.Status` hoặc `Guest.RecruitmentID` làm nguồn chính cho hồ sơ mới. Chỉ dùng fallback cho dữ liệu cũ nếu cần.

## Trường hiển thị
- Tên công việc.
- Địa điểm.
- Mức lương.
- Ngày ứng tuyển.
- Trạng thái application.
- CV đã nộp, ưu tiên `Application.CV`, fallback `CandidateProfile.CVFilePath`.

## Tiến trình hồ sơ
- `Applied` -> Đã nộp hồ sơ.
- `Screening` -> Đang sàng lọc.
- `Interview` -> Phỏng vấn.
- `Offered` -> Thư mời nhận việc.
- `Hired` -> Nhận việc thành công.
- `Rejected` -> Từ chối.
- `Withdrawn` -> Đã rút hồ sơ.

## Acceptance Criteria
- [ ] Guest đã đăng nhập xem được danh sách application của mình.
- [ ] Guest không xem được application của người khác.
- [ ] Danh sách lấy dữ liệu chính từ `Application`.
- [ ] Thông tin ứng viên/CV join từ `CandidateProfile` khi cần.
- [ ] Empty state thân thiện khi chưa ứng tuyển job nào.
