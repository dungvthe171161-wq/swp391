# Đặc tả dùng chung: Tải lên và hiển thị CV

Trạng thái: Đã cập nhật theo code ngày 2026-07-16.
Ngôn ngữ: tiếng Việt có dấu. Spec này là nguồn mô tả chung cho các luồng Guest/PublicCandidate tải CV và HR xem CV.

## Actor và phạm vi
- Guest cập nhật CV trong hồ sơ ứng viên.
- Guest/PublicCandidate đã đăng nhập tải CV khi nộp hồ sơ vào một tin tuyển dụng.
- HR Staff/HR Manager xem hoặc tải CV của application đang xử lý.

## Route, controller và JSP liên quan
- `Views/ApplyForm.jsp`: form ứng tuyển, field file `cvFile`, POST multipart đến `/RecruitmentController` với action `saveCandidateProfile`.
- `Views/Guest/Profile.jsp`: form hồ sơ ứng viên, field file `candidateCvFile`, POST multipart đến `/guest/profile` với action `saveCandidateProfile`.
- `RecruitmentController.saveCvFile`: nhận CV từ form ứng tuyển.
- `GuestPortalController.saveCandidateCvFile`: nhận CV từ hồ sơ Guest.
- `UploadPathUtil`: xác định thư mục lưu và kiểm tra tên file khi đọc.
- `CvFileServlet`: phục vụ file tại `/Upload/cvs/*`.
- `CandidateProfileDAO`: lưu tên file vào `CandidateProfile.CVFilePath`.
- `RecruitmentController.confirmApplication`: sao chép tên CV vào `Application.CV` khi tạo application.
- `ViewCV` và `Views/hr/ViewCV.jsp`: HR tải thông tin application và hiển thị CV.

## Hiện trạng code
1. Người dùng chọn file từ form hồ sơ hoặc form ứng tuyển.
2. Controller nhận multipart part tương ứng (`candidateCvFile` hoặc `cvFile`).
3. Nếu không chọn file mới và đã có CV, hệ thống giữ nguyên tên file cũ.
4. File mới được kiểm tra dung lượng tối đa 10MB và extension `pdf`, `doc` hoặc `docx`.
5. Hệ thống sinh tên UUID, giữ nguyên extension và ghi file vào thư mục CV dùng chung.
6. Nếu email hồ sơ đã xác minh và không đổi, `CandidateProfile` được lưu ngay. Nếu chưa xác minh hoặc email thay đổi, draft và OTP 6 số được lưu trong session; OTP hết hạn sau 10 phút và profile chỉ được ghi database sau khi xác minh thành công.
7. Khi người dùng xác nhận nộp hồ sơ, hệ thống tạo `Application` với `Status=Applied`, `CurrentStep=Applied` và `Application.CV=CandidateProfile.CVFilePath`.
8. HR mở `/viewCV?applicationId=...`; JSP tạo URL `/Upload/cvs/{fileName}`.
9. `CvFileServlet` kiểm tra tên file, từ chối path traversal/file ngoài định dạng cho phép và stream file về trình duyệt với `Content-Disposition: inline`.

## Lưu trữ
- Thư mục mặc định trong môi trường project: `src/main/webapp/Upload/cvs`.
- Có thể ghi đè bằng JVM property `hrms.cv.upload.dir` hoặc environment variable `HRMS_CV_UPLOAD_DIR`.
- Nếu không tìm thấy project root, hệ thống fallback sang thư mục deploy `/Upload/cvs`; cuối cùng là `${user.home}/hrms/Upload/cvs`.
- Database chỉ lưu tên file, không lưu binary/BLOB của CV.
- `CandidateProfile.CVFilePath` là CV hồ sơ hiện tại.
- `Application.CV` là tên CV được gắn vào lần ứng tuyển khi application được tạo.
- File upload runtime phải được Git bỏ qua vì có thể chứa dữ liệu cá nhân.

## Validation và bảo mật
- Request phải dùng `multipart/form-data`.
- Dung lượng tối đa: 10MB.
- Extension cho phép: `pdf`, `doc`, `docx`.
- Tên file lưu phải là UUID để tránh trùng và không dùng tên do người dùng cung cấp.
- Khi đọc file, tên nhận từ URL không được chứa path khác với basename và file phải nằm trong thư mục CV đã resolve.
- Route CV đi qua filter đăng nhập hiện tại; chỉ actor đã đăng nhập và có luồng nghiệp vụ phù hợp mới được mở trang HR.

## Quy tắc nghiệp vụ chuẩn
- Không tạo application nếu recruitment đã đóng, hồ sơ chưa sẵn sàng hoặc Guest đã nộp trùng recruitment.
- CV của application phải lấy từ `CandidateProfile` tại thời điểm xác nhận nộp hồ sơ.
- HR phải truy cập bằng `applicationId` để xem đúng CV của lần ứng tuyển đang xét.
- Thay CV trong profile không được tự động thay `Application.CV` của application đã tạo trước đó.
- Không chọn file mới khi cập nhật profile phải giữ nguyên CV hiện tại.

## Code còn lệch spec hoặc cần bổ sung
- File được ghi trước khi OTP hoàn tất; gửi OTP thất bại hoặc người dùng không xác minh có thể để lại file không được database tham chiếu.
- Khi thay CV, file cũ chưa được xóa tự động.
- Validation hiện dựa trên extension; chưa kiểm tra MIME/magic bytes của nội dung file.
- Fallback `guestId` trên `ViewCV` vẫn còn cho luồng legacy và POST legacy vẫn có thể cập nhật `Guest.Status`.
- Cần cơ chế dọn file mồ côi dựa trên tham chiếu từ `CandidateProfile` và `Application`.

## Kiểm thử tối thiểu
- Upload `pdf`, `doc`, `docx` hợp lệ dưới 10MB từ cả hai form.
- Từ chối extension không hợp lệ và file trên 10MB.
- Không chọn file mới phải giữ CV cũ.
- Xác minh OTP đúng/sai/hết hạn và kiểm tra thời điểm database được cập nhật.
- Sau upload, file phải xuất hiện trong `src/main/webapp/Upload/cvs` và còn tồn tại sau `mvn clean`.
- `/Upload/cvs/{fileName}` trả đúng content type; path traversal và file không tồn tại trả 404.
- `Application.CV` phải khớp `CandidateProfile.CVFilePath` tại thời điểm tạo application.
- `/viewCV?applicationId=...` phải hiển thị đúng CV khi một Guest có nhiều application.
- Chạy `mvn -q package` sau khi thay đổi code liên quan.
