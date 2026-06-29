# Spec triển khai: Guest CandidateProfile Application Flow
Trạng thái: Đã triển khai phần apply/profile, các phần Interview/Offer/Notification tiếp tục theo spec riêng
Tác nhân: Guest Candidate, HR, HR Staff, Admin
Độ ưu tiên: Cao

## Mục tiêu triển khai
Thay luồng Guest nhập hồ sơ mỗi lần ứng tuyển bằng luồng:

Xem việc làm -> Ứng tuyển -> Đăng nhập nếu cần -> Kiểm tra `CandidateProfile` -> Tạo/cập nhật profile và xác nhận email nếu cần -> Xác nhận ứng tuyển -> Tạo `Application` -> Thành công.

## Database

### CandidateProfile
Lưu hồ sơ ứng viên dùng lại:
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

Quy tắc:
- Một `GuestID` chỉ có một `CandidateProfile`.
- CV lưu đường dẫn file.
- Email phải xác nhận trước khi profile được lưu/chấp nhận.

### Application
Lưu từng lần ứng tuyển:
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

Quy tắc:
- Unique `GuestID + RecruitmentID`.
- `Status = Applied` khi tạo mới.
- `CurrentStep = Applied` khi tạo mới.
- `Source = Portal`.
- `CV` lấy từ `CandidateProfile.CVFilePath`.

## Không triển khai trong luồng này
- Không tạo database OTP/email verification.
- Không thêm mới department, benefit, deadline cho recruitment.
- Không drop các cột/bảng cũ của Guest để tránh phá dữ liệu hiện có.

## Session email verification

Luồng apply dùng:
- `candidateProfileDraft`
- `candidateProfileVerifyCode`
- `candidateProfileVerifyExpiresAt`
- `candidateProfileVerifyRecruitmentId`

Luồng profile dùng:
- `guestCandidateProfileDraft`
- `guestCandidateProfileVerifyCode`
- `guestCandidateProfileVerifyExpiresAt`

Quy tắc:
- Mã 6 chữ số.
- Hết hạn sau 10 phút.
- Có nút gửi lại mã.
- Lưu profile chỉ khi mã đúng và còn hạn.

## Controller

### RecruitmentController
Các action cần có:
- `apply`
- `saveCandidateProfile`
- `verifyCandidateProfileEmail`
- `resendCandidateProfileCode`
- `confirmApplication`

Nhiệm vụ:
- Bắt login khi Guest ứng tuyển.
- Lưu redirect để quay lại đúng job sau login.
- Validate recruitment.
- Check duplicate application.
- Điều hướng sang form profile hoặc confirm.
- Validate và upload CV.
- Gửi mã xác nhận email.
- Lưu `CandidateProfile`.
- Tạo `Application`.

### GuestPortalController
Nhiệm vụ:
- Load `CandidateProfile` vào trang profile.
- Cập nhật section `Hồ sơ ứng tuyển`.
- Xác nhận email bằng session khi email thay đổi hoặc profile chưa verified.
- Giữ CV cũ nếu Guest không upload CV mới.

### LoginController / GoogleAuthController / SessionSecurityFilter
Nhiệm vụ:
- Bảo toàn `redirectAfterLogin`.
- Sau login quay lại đúng job Guest đang ứng tuyển.

### ViewCV
Nhiệm vụ:
- HR xem CV từ application mới.
- Ưu tiên `Application.CV`.
- Fallback `CandidateProfile.CVFilePath`.
- Fallback dữ liệu Guest cũ khi cần.

## JSP/UI

### ApplyForm.jsp
Form hồ sơ ứng tuyển dùng một lần:
- Họ tên.
- Số điện thoại.
- Email.
- Ngày sinh.
- Địa chỉ.
- Vị trí mong muốn.
- Mức lương mong muốn.
- Kinh nghiệm.
- CV.

### ApplyVerifyEmail.jsp
Màn nhập mã xác nhận:
- Hiển thị email nhận mã.
- Ô nhập mã.
- Nút xác nhận và lưu hồ sơ.
- Nút gửi lại mã.
- Nút quay lại form hồ sơ.

### ApplyConfirm.jsp
Màn xác nhận ứng tuyển:
- Tên công việc.
- Phòng ban nếu có dữ liệu.
- Địa điểm.
- Mức lương.
- Nút `Xác nhận ứng tuyển`.

### Guest/Profile.jsp
Thêm section `Hồ sơ ứng tuyển`:
- Cho phép chỉnh sửa profile và CV.
- Nếu cần xác nhận email, chuyển sang `ProfileEmailVerify.jsp`.

## Validation
- Họ tên bắt buộc.
- Email đúng định dạng.
- Phone đúng định dạng.
- Ngày sinh không lớn hơn ngày hiện tại.
- CV chỉ nhận PDF, DOC, DOCX.
- CV tối đa 10MB.
- Không cho application trùng `GuestID + RecruitmentID`.

## Thông báo bắt buộc
- Ứng tuyển thành công.
- `Bạn đã ứng tuyển công việc này.`
- Không thể lưu hồ sơ ứng tuyển khi lỗi hệ thống/validation.
- Mã xác nhận sai hoặc đã hết hạn.

## Acceptance Criteria
- [x] Guest chưa login bấm ứng tuyển được yêu cầu login và quay lại đúng job.
- [x] Guest chưa có profile phải nhập form và xác nhận email.
- [x] Guest đã có profile được chuyển thẳng tới confirm.
- [x] Lưu profile dùng mã xác nhận session, không tạo bảng OTP.
- [x] CV validate PDF/DOC/DOCX và 10MB.
- [x] Confirm tạo `Application` liên kết `Recruitment`, `Guest`, `CandidateProfile`.
- [x] Không cho ứng tuyển trùng.
- [x] Guest Profile chỉnh sửa được hồ sơ ứng tuyển.
- [x] HR xem được CV từ application mới.
- [ ] Guest My Applications hoàn thiện UI danh sách application nếu chưa triển khai.
- [ ] Interview/Offer/Notification hoàn thiện theo các spec tương ứng.
