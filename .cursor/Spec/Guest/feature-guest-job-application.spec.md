# Tính năng: Guest nộp hồ sơ ứng tuyển
Trạng thái: Đã triển khai luồng CandidateProfile
Tác nhân: Guest Candidate
Độ ưu tiên: Cao
Mã nguồn liên quan: `RecruitmentController`, `CandidateProfileDAO`, `ApplicationDAO`, `EmailSender`, `Views/ApplyForm.jsp`, `Views/ApplyVerifyEmail.jsp`, `Views/ApplyConfirm.jsp`, `Views/Success.jsp`

## Route hiện tại
- `GET /RecruitmentController?action=apply&recruitmentId={recruitmentId}`
- `POST /RecruitmentController?action=saveCandidateProfile`
- `POST /RecruitmentController?action=verifyCandidateProfileEmail`
- `POST /RecruitmentController?action=resendCandidateProfileCode`
- `POST /RecruitmentController?action=confirmApplication`

## Mục tiêu
Guest chỉ nhập hồ sơ ứng tuyển một lần. Sau khi đã có `CandidateProfile` đã xác nhận email, các lần ứng tuyển sau chỉ cần xác nhận ứng tuyển, không nhập lại họ tên/email/số điện thoại/ngày sinh/địa chỉ/kinh nghiệm/CV.

## Luồng khi chưa đăng nhập
1. Guest bấm `Ứng tuyển ngay`.
2. Hệ thống yêu cầu đăng nhập hoặc đăng ký.
3. URL công việc hiện tại được lưu vào `redirectAfterLogin`.
4. Sau khi đăng nhập local hoặc Google thành công, hệ thống quay lại đúng job đang xem.

## Luồng khi đã đăng nhập
1. Controller lấy `systemUser` trong session.
2. Controller lấy hoặc tạo/liên kết `Guest` theo tài khoản đăng nhập.
3. Controller kiểm tra `Recruitment` hợp lệ.
4. Controller kiểm tra đã tồn tại `Application` theo `GuestID + RecruitmentID` chưa.
5. Nếu đã ứng tuyển, hiển thị thông báo `Bạn đã ứng tuyển công việc này.` và không tạo thêm application.
6. Nếu chưa có `CandidateProfile` hoặc profile chưa xác nhận email, hiển thị form hồ sơ.
7. Nếu đã có `CandidateProfile` hợp lệ, chuyển thẳng sang màn xác nhận ứng tuyển.

## Form hồ sơ ứng tuyển
Form chỉ dùng khi Guest chưa có hồ sơ ứng tuyển hợp lệ.

Thông tin cá nhân:
- Họ và tên.
- Số điện thoại.
- Email.
- Ngày sinh.
- Địa chỉ/nơi ở hiện tại.

Thông tin nghề nghiệp:
- Vị trí mong muốn.
- Mức lương mong muốn.
- Kinh nghiệm làm việc.

CV:
- Upload file bắt buộc khi tạo hồ sơ lần đầu.
- Chỉ chấp nhận `.pdf`, `.doc`, `.docx`.
- Tối đa 10MB.
- Lưu đường dẫn file vào `CandidateProfile.CVFilePath`.

## Xác nhận email trước khi lưu profile
1. Sau khi Guest bấm `Lưu hồ sơ & Tiếp tục`, controller validate dữ liệu và lưu bản nháp vào session.
2. Hệ thống gửi mã xác nhận 6 chữ số tới email trong hồ sơ.
3. Mã xác nhận và thời điểm hết hạn được lưu trong `HttpSession`.
4. Không lưu mã xác nhận vào database.
5. Khi Guest nhập mã đúng và còn hạn, hệ thống insert/update `CandidateProfile`.
6. Sau khi lưu thành công, hệ thống chuyển sang màn xác nhận ứng tuyển.

## Màn xác nhận ứng tuyển
Hiển thị Guest sắp ứng tuyển vào:
- Tên công việc.
- Phòng ban nếu dữ liệu recruitment hiện có hỗ trợ.
- Địa điểm.
- Mức lương.

Nút chính: `Xác nhận ứng tuyển`.

## Tạo Application
Khi Guest xác nhận:
- Tạo record trong `Application`.
- Gán `GuestID`.
- Gán `RecruitmentID`.
- Gán `CandidateProfileID`.
- Gán `CV` từ `CandidateProfile.CVFilePath`.
- `Status = Applied`.
- `CurrentStep = Applied`.
- `Source = Portal`.
- `AppliedDate = NOW()`.

`Application` không lặp lại toàn bộ thông tin cá nhân. Khi cần xem thông tin ứng viên, hệ thống join sang `CandidateProfile`.

## Validation
- Họ tên bắt buộc.
- Email đúng định dạng.
- Số điện thoại đúng định dạng cơ bản.
- Ngày sinh không lớn hơn ngày hiện tại.
- CV đúng định dạng PDF/DOC/DOCX.
- CV tối đa 10MB.
- `recruitmentId` phải tồn tại.
- Không cho ứng tuyển trùng `GuestID + RecruitmentID`.

## Acceptance Criteria
- [x] Guest chưa đăng nhập được yêu cầu đăng nhập và quay lại đúng job sau login.
- [x] Guest chưa có profile thấy form hồ sơ ứng tuyển.
- [x] Guest có profile đã xác nhận email không phải nhập lại thông tin.
- [x] Lưu profile yêu cầu xác nhận email bằng mã session.
- [x] Không tạo bảng OTP/email verification.
- [x] Xác nhận ứng tuyển tạo `Application` liên kết `CandidateProfile`.
- [x] Ứng tuyển thành công hiển thị trang thành công.
- [x] Nếu đã ứng tuyển công việc đó, hiển thị `Bạn đã ứng tuyển công việc này.`.
