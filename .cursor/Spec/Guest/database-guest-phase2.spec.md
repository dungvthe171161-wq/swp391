# Đặc tả Cơ sở dữ liệu: Guest Application Flow
Trạng thái: Đã triển khai luồng CandidateProfile
Tác nhân: Guest Candidate, HR, HR Staff, Admin
Độ ưu tiên: Cao
Tập tin liên quan: `src/data/data.sql`, `src/data/migrations/2026-06-22_guest_phase2_workflow.sql`, `src/data/migrations/2026-06-28_candidate_profile_apply_flow.sql`

## Mục tiêu
Tách dữ liệu hồ sơ ứng viên dùng lại khỏi dữ liệu từng lần ứng tuyển.

Thiết kế hiện tại:
- `Guest` giữ vai trò tài khoản/ứng viên để tương thích hệ thống cũ.
- `CandidateProfile` lưu hồ sơ ứng tuyển dùng lại một lần.
- `Application` lưu từng lần ứng tuyển vào một tin tuyển dụng.
- `Interview`, `Offer`, `Notification` tiếp tục phục vụ các bước xử lý sau khi đã có application.

## CandidateProfile
Lưu hồ sơ ứng viên dùng lại cho nhiều lần ứng tuyển.

Các cột chính:
- `CandidateProfileID`
- `GuestID`
- `FullName`
- `Phone`
- `Email`
- `DateOfBirth`
- `Address`
- `DesiredPosition`
- `ExpectedSalary`
- `WorkExperience`
- `CVFilePath`
- `EmailVerified`
- `EmailVerifiedAt`
- `CreatedDate`
- `UpdatedDate`

Ràng buộc:
- `GuestID` tham chiếu `Guest.GuestID`.
- `GuestID` là duy nhất để mỗi Guest chỉ có một hồ sơ ứng tuyển dùng lại.
- Không tạo bảng OTP/email verification riêng cho luồng này.

## Email verification
Mã xác nhận email được lưu trong `HttpSession`, không lưu database.

Session keys của luồng ứng tuyển:
- `candidateProfileDraft`
- `candidateProfileVerifyCode`
- `candidateProfileVerifyExpiresAt`
- `candidateProfileVerifyRecruitmentId`

Session keys của trang Guest Profile:
- `guestCandidateProfileDraft`
- `guestCandidateProfileVerifyCode`
- `guestCandidateProfileVerifyExpiresAt`

Mã xác nhận hết hạn sau 10 phút. Chỉ khi mã hợp lệ mới insert/update `CandidateProfile`.

## Application
Lưu mỗi lần Guest ứng tuyển vào một tin tuyển dụng cụ thể.

Các cột chính:
- `ApplicationID`
- `GuestID`
- `RecruitmentID`
- `CandidateProfileID`
- `AppliedDate`
- `Status`
- `CurrentStep`
- `CV`
- `CoverLetter`
- `Source`
- `Note`
- `CreatedDate`
- `UpdatedDate`

Ràng buộc:
- `GuestID` tham chiếu `Guest.GuestID`.
- `RecruitmentID` tham chiếu `Recruitment.RecruitmentID`.
- `CandidateProfileID` tham chiếu `CandidateProfile.CandidateProfileID`.
- Unique `GuestID + RecruitmentID` để chặn ứng tuyển trùng.

Quy tắc dữ liệu:
- Thông tin cá nhân không lặp lại trong `Application`.
- `Application.CV` giữ đường dẫn CV tại thời điểm ứng tuyển để tương thích màn HR/ViewCV.
- Nguồn hồ sơ đầy đủ lấy từ `CandidateProfile`.
- Khi Guest cập nhật `CandidateProfile`, các lần ứng tuyển tiếp theo dùng dữ liệu mới nhất.

## Recruitment
Luồng này không yêu cầu thêm mới các bảng/cột:
- Department.
- Benefit.
- Deadline.

Trang ứng tuyển chỉ hiển thị dữ liệu tuyển dụng đang có sẵn từ bảng/model `Recruitment`.

## Interview
Lưu lịch phỏng vấn theo từng `Application`.

Các cột chính:
- `InterviewID`
- `ApplicationID`
- `RoundNo`
- `ScheduledAt`
- `Location`
- `MeetingLink`
- `InterviewerEmployeeID`
- `Status`
- `Result`
- `Note`
- `CreatedDate`
- `UpdatedDate`

## Offer
Lưu thông tin thư mời nhận việc cho application đã đạt yêu cầu.

Các cột chính:
- `OfferID`
- `ApplicationID`
- `Position`
- `OfferedSalary`
- `StartDate`
- `ExpiredAt`
- `Status`
- `SentAt`
- `RespondedAt`
- `Note`
- `CreatedDate`
- `UpdatedDate`

## Notification
Lưu thông báo dùng chung theo tài khoản `SystemUser.UserID`.

Các cột chính:
- `NotificationID`
- `UserID`
- `ApplicationID`
- `Title`
- `Message`
- `Type`
- `IsRead`
- `CreatedDate`
- `ReadDate`

## Backfill và tương thích dữ liệu cũ
- Không drop bảng/cột cũ.
- Không drop `Guest.RecruitmentID`.
- Dữ liệu cũ có thể được đọc fallback khi application/profile mới chưa đầy đủ.
- HR ViewCV ưu tiên CV từ `Application.CV`, fallback sang `CandidateProfile.CVFilePath`, sau đó tới dữ liệu Guest cũ nếu cần.

## Acceptance Criteria
- [x] Có bảng `CandidateProfile`.
- [x] Có migration tạo/cập nhật `CandidateProfile`.
- [x] `Application` có `CandidateProfileID`.
- [x] `Application` có `Note`.
- [x] `Application` có unique `GuestID + RecruitmentID`.
- [x] Không cần bảng OTP/email verification mới.
- [x] Không thêm department/benefit/deadline cho luồng này.
- [x] HR vẫn xem được CV từ application tạo bởi luồng mới.
