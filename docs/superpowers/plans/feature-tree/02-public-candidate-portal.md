# Feature Tree 02 - Kế hoạch triển khai Cổng công khai và Ứng viên

- **Trạng thái:** Bản nháp
- **Phạm vi chuẩn:** Feature Tree 02
- **Phụ thuộc:** Plan 01 và nền tảng Plan 10
- **Tích hợp với:** State machine tuyển dụng của Plan 05

## 1. Mục tiêu

Cung cấp hành trình đầy đủ từ khách truy cập công khai đến ứng viên: xem vacancy đã duyệt, xác thực, duy trì hồ sơ ứng viên dùng lại, upload CV được bảo vệ, gửi tối đa một đơn cho mỗi vacancy, theo dõi tiến trình, xem lịch phỏng vấn và phản hồi offer.

## 2. Nguồn plan cũ

- `docs/superpowers/plans/2026-06-28-candidate-profile-apply-flow.md`
- Các phần dành cho Guest trong `2026-06-30-interview-email-notification-flow.md`
- Các phần dành cho Guest trong `2026-07-02-interview-result-offer-flow.md`

Cần giữ lại luồng Candidate Profile đã triển khai, sau đó audit theo ranh giới chuẩn và quy tắc bảo mật trong plan này.

## 3. Ranh giới phạm vi

Plan này chịu trách nhiệm cho trang dành cho ứng viên, validation, ownership và cách trình bày.

Plan 05 chịu trách nhiệm:

- Chuyển trạng thái Recruitment, Application, Interview và Offer.
- Hành động theo workflow của HR Staff và HR Manager.
- Chuyển ứng viên thành nhân viên trong transaction.

Plan 10 chịu trách nhiệm hạ tầng gửi email và notification.

## 4. Code hiện tại cần audit và tái sử dụng

- `HomepageController`, `RecruitmentController`, `GuestPortalController`, `CvFileServlet`
- `RecruitmentDAO`, `CandidateProfileDAO`, `ApplicationDAO`, `InterviewDAO`, `OfferDAO`, `GuestDAO`
- `Homepage.jsp`, `Recruitment.jsp`, `ApplyForm.jsp`, `ApplyVerifyEmail.jsp`, `ApplyConfirm.jsp`, `Success.jsp`
- `Views/Guest/Dashboard.jsp`, `Applications.jsp`, `Profile.jsp`, `ProfileEmailVerify.jsp`
- `src/data/migrations/2026-06-28_candidate_profile_apply_flow.sql`

## 5. Luồng code lúc chạy

```text
Khách công khai -> HomepageController -> truy vấn Recruitment đã publish -> danh sách/chi tiết việc làm
Ứng viên -> RecruitmentController -> validation hồ sơ ứng viên/xác minh email
         -> CandidateProfileDAO/ApplicationDAO -> xác nhận -> Application đã gửi
Ứng viên -> GuestPortalController -> Application/Interview/Offer của chính mình -> hành động phản hồi
         -> workflow service của Plan 05 -> database -> notification/email của Plan 10
```

## 6. Các bất biến nghiệp vụ

- Người dùng công khai chỉ thấy vacancy đã được duyệt, publish và hợp lệ theo ngày hiện tại.
- Một Guest có tối đa một `CandidateProfile` dùng lại.
- Một Guest chỉ có thể tạo tối đa một Application cho một Recruitment.
- Thay đổi email trong hồ sơ ứng viên phải xác minh trước khi lưu.
- File CV được kiểm tra theo ràng buộc nội dung và chỉ được tải qua endpoint có phân quyền.
- Ứng viên chỉ xem được Application, Interview, Offer và Notification của chính mình.
- Chỉ nhận phản hồi Offer khi Offer đang ở trạng thái Sent và chưa hết hạn.
- Code phía ứng viên không trực tiếp thực hiện chuyển trạng thái workflow của HR.

## 7. Các task triển khai

### Task 1 - Audit các điểm vào tuyển dụng công khai

- [ ] Kiểm kê `/homepage`, `/RecruitmentController`, action công khai và liên kết JSP.
- [ ] Xác định và triển khai JSP hoặc route chi tiết Recruitment còn thiếu được ghi nhận trong audit SRS.
- [ ] Định nghĩa một truy vấn DAO cho vacancy đã publish với điều kiện phê duyệt, trạng thái và ngày.
- [ ] Loại bỏ logic listing trùng lặp trong controller hoặc JSP scriptlet.
- [ ] Thêm test chứng minh vacancy chưa publish, bị từ chối, hết hạn và chưa đến ngày không được hiển thị.

### Task 2 - Ổn định định danh ứng viên và quay lại luồng ứng tuyển

- [ ] Yêu cầu xác thực trước khi tạo hồ sơ hoặc gửi Application.
- [ ] Chỉ lưu URL đích nội bộ đã được kiểm tra khi chuyển sang trang đăng nhập.
- [ ] Đưa Guest trở lại vacancy đã chọn sau đăng nhập local hoặc Google.
- [ ] Từ chối URL quay lại trỏ ra ngoài ứng dụng hoặc tới module không được phép.
- [ ] Test session hết hạn giữa luồng ứng tuyển.

### Task 3 - Hoàn thiện Candidate Profile dùng lại

- [ ] Audit trường bắt buộc và validation ở cả form ứng tuyển lẫn form hồ sơ Guest.
- [ ] Kiểm tra họ tên, số điện thoại, email, ngày sinh, địa chỉ, vị trí mong muốn, lương mong đợi và kinh nghiệm ở backend.
- [ ] Chỉ giữ trạng thái verified khi email không thay đổi.
- [ ] Dùng mã xác minh có thời hạn 10 phút, thời gian chờ gửi lại, giới hạn số lần thử và chỉ dùng một lần.
- [ ] Xóa dữ liệu draft và xác minh trong session sau khi thành công, hết hạn, đăng xuất hoặc hủy.
- [ ] Tách cập nhật hồ sơ Guest cơ bản khỏi dữ liệu nghề nghiệp của Candidate Profile.
- [ ] Thêm test DAO và controller cho lần tạo đầu, cập nhật, email không đổi, email thay đổi và mã hết hạn.

### Task 4 - Gia cố upload và truy xuất CV

- [ ] Chỉ cho phép PDF/DOC/DOCX theo thỏa thuận và giới hạn dung lượng tối đa.
- [ ] Sinh tên file phía server và không dùng tên file người dùng gửi làm đường dẫn.
- [ ] Kiểm tra đường dẫn upload chuẩn hóa luôn nằm trong thư mục upload được cấu hình.
- [ ] Quy định hành vi thay thế và dọn dẹp khi ứng viên upload CV mới.
- [ ] Giữ tương thích `Application.CV` và ưu tiên `CandidateProfile.CVFilePath` theo đặc tả.
- [ ] Yêu cầu ownership ứng viên hoặc quyền HR phù hợp trong `CvFileServlet`/route xem CV.
- [ ] Trả về 404/403 an toàn mà không làm lộ đường dẫn vật lý.
- [ ] Test path traversal, file quá lớn, phần mở rộng kép, file bị thiếu và truy cập trái phép.

### Task 5 - Bảo đảm gửi Application có tính nguyên tử và idempotent

- [ ] Kiểm tra lại trạng thái publish của vacancy và Application trùng ngay trước insert.
- [ ] Thực thi `UNIQUE(GuestID, RecruitmentID)` bằng migration idempotent.
- [ ] Tạo Application với trạng thái/current step/source ban đầu chuẩn từ Plan 05.
- [ ] Xử lý duplicate-key race thành thông báo đã ứng tuyển thân thiện.
- [ ] Không tạo Application dở dang nếu lưu hồ sơ thất bại.
- [ ] Thêm test controller/service cho thành công, trùng, vacancy đóng và double-submit đồng thời.

### Task 6 - Theo dõi Application của ứng viên

- [ ] Load Application theo Guest đã xác thực, không dùng GuestID do request gửi lên.
- [ ] Hiển thị nhãn trạng thái từ một mapping dùng chung thuộc Plan 05.
- [ ] Hiển thị tiêu đề vacancy, ngày ứng tuyển, bước hiện tại, trạng thái và hành động tiếp theo phù hợp.
- [ ] Quyết định withdrawal có nằm trong phạm vi hay không; nếu có, chỉ cho phép từ trạng thái được tài liệu hóa thông qua Plan 05.
- [ ] Thêm trạng thái giao diện cho rỗng, đang xử lý, bị từ chối, đã tuyển và đã rút đơn.
- [ ] Test sửa URL và parameter để truy cập dữ liệu ứng viên khác.

### Task 7 - Hiển thị phỏng vấn và Offer

- [ ] Chỉ load Interview sắp tới/liên quan của ứng viên đã xác thực.
- [ ] Hiển thị vòng, ngày giờ, địa điểm, meeting link, thông tin người phỏng vấn và trạng thái một cách an toàn.
- [ ] Hiển thị Offer với vị trí, mức lương, ngày bắt đầu, hạn phản hồi, ghi chú và trạng thái hiện tại.
- [ ] Gửi accept/reject qua workflow service kiểm tra ownership của Plan 05.
- [ ] Ngăn phản hồi lặp hoặc phản hồi Offer hết hạn.
- [ ] Sau refresh hiển thị kết quả ổn định thay vì gửi lại hành động.
- [ ] Thêm test sai owner, Offer hết hạn, đã phản hồi và phản hồi hợp lệ.

### Task 8 - Notification và thông báo

- [ ] Dùng Plan 10 cho xác minh, Application, Interview và Offer message.
- [ ] Hiển thị notification chưa đọc/gần đây của ứng viên một cách nhất quán.
- [ ] Kiểm tra redirect của notification trước khi điều hướng.
- [ ] Dùng Post/Redirect/Get cho kết quả cập nhật hồ sơ, ứng tuyển và phản hồi Offer.
- [ ] Chuẩn hóa thông báo thành công và lỗi tiếng Việt bằng UTF-8.

### Task 9 - Tích hợp giao diện

- [ ] Giữ điều hướng nhất quán giữa trang công khai, trang ứng tuyển và Guest Portal.
- [ ] Giữ tên field của form mà controller hiện tại cần hoặc bổ sung xử lý tương thích.
- [ ] Cung cấp label dễ tiếp cận và phần tổng hợp lỗi validation cho form hồ sơ và CV.
- [ ] Tránh hiển thị numeric ID nội bộ khi không cần thiết.
- [ ] Kiểm tra giao diện mobile cho danh sách/chi tiết vacancy, Application card, dữ liệu Interview và hành động Offer.

### Task 10 - Xác minh

- [ ] Chạy test hồ sơ ứng viên, Application, xem Interview, phản hồi Offer và truy cập file.
- [ ] Chạy `mvn test`, `mvn -q compile` và `mvn -q package`.
- [ ] Kiểm thử thủ công luồng quay lại sau đăng nhập local và Google.
- [ ] Kiểm thử thủ công lần ứng tuyển đầu, ứng tuyển lặp, cập nhật hồ sơ, thay CV và phản hồi Offer.
- [ ] Xác minh ứng viên A không truy cập được bất kỳ tài nguyên nào của ứng viên B.

## 8. Tiêu chí hoàn thành

- Luồng đầy đủ từ trang công khai đến gửi Application hoạt động mà không thiếu route hoặc JSP.
- Candidate Profile có thể dùng lại và xác minh email an toàn.
- Lưu trữ/truy xuất CV an toàn về đường dẫn và được kiểm tra ownership.
- Gửi Application là duy nhất và an toàn trước race condition.
- Theo dõi Application, hiển thị Interview và phản hồi Offer tuân theo trạng thái của Plan 05.
- Test, compile, package và hành trình ứng viên thủ công đều đạt.

