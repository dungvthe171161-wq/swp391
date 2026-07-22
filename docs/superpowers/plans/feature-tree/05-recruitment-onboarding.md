# Feature Tree 05 - Kế hoạch triển khai Tuyển dụng và Tiếp nhận nhân viên

- **Trạng thái:** Bản nháp
- **Phạm vi chuẩn:** Feature Tree 05
- **Phụ thuộc:** Plan 01, 02, dữ liệu Department/Role của Plan 09 và Plan 10
- **Đầu ra:** Bản ghi Employee/SystemUser được Plan 03, 06, 07 và 08 sử dụng

## 1. Mục tiêu

Cung cấp một workflow tuyển dụng có transaction xuyên suốt từ vacancy draft, phê duyệt, sàng lọc ứng viên, phỏng vấn, Offer, phản hồi của ứng viên đến chuyển ứng viên đã chấp nhận thành Employee và SystemUser.

## 2. Nguồn plan cũ

- `docs/superpowers/plans/2026-06-24-hr-staff-recruitment-workflow.md`
- `docs/superpowers/plans/2026-06-30-interview-email-notification-flow.md`
- `docs/superpowers/plans/2026-07-02-interview-result-offer-flow.md`
- `docs/superpowers/specs/2026-06-24-hr-staff-recruitment-workflow-design.md`

Hai plan sau ghi nhận phần Interview/Offer đã hoàn thành, trong khi plan tổng quát ngày 24/6 vẫn chưa tick. Plan chuẩn này phải audit code kết quả thay vì chỉ tin trạng thái checkbox.

## 3. Ranh giới phạm vi

Plan 02 chịu trách nhiệm form công khai/ứng viên, trang theo dõi và cách hiển thị phản hồi an toàn về ownership. Plan này chịu trách nhiệm state transition có thẩm quyền và workflow HR. Plan 10 chịu trách nhiệm hạ tầng gửi; plan này phát domain event sau commit.

## 4. Code hiện tại cần audit và tái sử dụng

### HR Staff và HR Manager

- `PostRecruitmentController`, `DetailRecruitmentCreate`
- `ViewCandidateController`, `InterviewScheduleController`, `OfferManagementController`
- `hrManager/ViewRecruitment`, `hrManager/DetailWaitingRecruitment`
- `hr/ViewCV`, `hr/CreateEmployeeController`, các controller chi tiết Recruitment

### Candidate và thành phần dùng chung

- `RecruitmentController`, `GuestPortalController`, `CvFileServlet`
- `RecruitmentDAO`, `ApplicationDAO`, `CandidateProfileDAO`, `InterviewDAO`, `OfferDAO`
- `EmployeeDAO`, `SystemUserDAO`, `GuestDAO`, `DepartmentDAO`
- `RecruitmentWorkflowRules`, `EmailSender`, `NotificationService`

## 5. Luồng code chuẩn lúc chạy

```text
HR Staff tạo vacancy draft
  -> HR Manager duyệt/từ chối
  -> vacancy được duyệt xuất hiện công khai
  -> ứng viên gửi Application qua Plan 02
  -> HR Staff sàng lọc Application và quản lý Interview
  -> Interview thành công cho phép tạo/gửi Offer
  -> ứng viên chấp nhận/từ chối qua Plan 02
  -> HR Manager/HR được phép chuyển Offer Accepted thành Employee/SystemUser
  -> transaction commit
  -> Plan 10 gửi Notification/email
```

## 6. Hợp đồng trạng thái bắt buộc

Trước khi triển khai, phải đối chiếu các trạng thái logic dưới đây với chính xác giá trị enum/string MySQL trong schema live và migration.

```text
Recruitment: Draft/New -> Waiting Approval -> Published/Applied hoặc Rejected -> Closed
Application: Applied -> Screening -> Interview -> Offered -> Hired
             Applied/Screening/Interview/Offered -> Rejected
             trạng thái chưa kết thúc được phép -> Withdrawn (chỉ khi còn trong phạm vi)
Interview: Scheduled -> Rescheduled -> Completed(Passed/Failed) hoặc Cancelled
Offer: Draft -> Sent -> Accepted/Rejected/Expired/Cancelled
```

Không controller nào được tự tạo cách viết trạng thái mới hoặc cập nhật trạng thái ngoài transition policy.

## 7. Các bất biến transaction

- State transition dùng conditional update với trạng thái hiện tại mong đợi.
- Các thao tác ghi liên quan dùng chung một JDBC `Connection` và commit hoặc rollback cùng nhau.
- Application status là nguồn sự thật chính của tiến trình tuyển dụng; Interview và Offer cung cấp chi tiết từng giai đoạn.
- Gửi email và Notification xảy ra sau commit, không quyết định thành công của database.
- Chấp nhận Offer và chuyển ứng viên có tính idempotent.
- Chuyển Candidate thành Employee không để lại cập nhật Employee, account, Guest hoặc Application một phần.

## 8. Các task triển khai

### Task 1 - Audit schema, code và kết quả plan cũ

- [ ] So sánh giá trị schema live/bootstrap của Recruitment, Application, Interview, Offer, Guest, Employee và SystemUser.
- [ ] Kiểm kê mọi thao tác cập nhật status trực tiếp trong controller và DAO.
- [ ] Ghi nhận bước nào của plan cũ đã triển khai, một phần hoặc còn thiếu.
- [ ] Xác định route công khai, route HR Staff, route HR Manager và route tương thích.
- [ ] Thêm report/migration chỉ đọc cho Application trùng, Offer trùng, Interview mồ côi và giá trị trạng thái không hợp lệ.
- [ ] Thiết lập baseline workflow test trước khi refactor.

### Task 2 - Định nghĩa enum trạng thái và transition policy

- [ ] Tạo constant/enum tương thích database cho Recruitment, Application, Interview và Offer.
- [ ] Mã hóa transition hợp lệ trong `RecruitmentWorkflowRules` hoặc policy class chuyên trách.
- [ ] Định nghĩa role nào được thực hiện từng transition.
- [ ] Định nghĩa trạng thái kết thúc và hành vi khi thao tác lặp.
- [ ] Định nghĩa cách suy ra hoặc đồng bộ `Application.CurrentStep`.
- [ ] Viết test cho mọi transition được phép và bị cấm.
- [ ] Loại bỏ việc chuẩn hóa string rải rác sau khi caller chuyển sang dùng policy.

### Task 3 - Ổn định schema và constraint

- [ ] Thêm migration idempotent cho các trạng thái và cột workflow bắt buộc.
- [ ] Thực thi một Application cho mỗi cặp Guest-Recruitment.
- [ ] Định nghĩa một Application có thể có nhiều vòng Interview hay không và tối đa một vòng active.
- [ ] Định nghĩa một Offer active/hiện tại cho mỗi Application và ngăn nhiều Offer Sent.
- [ ] Thêm index cho filter hàng đợi HR và truy vấn theo dõi của ứng viên.
- [ ] Đồng bộ `src/data/data.sql` với migration.
- [ ] Test migration trên bản sao database có dữ liệu cũ.

### Task 4 - Làm DAO hỗ trợ transaction

- [ ] Thêm DAO overload nhận Connection cho mọi thao tác ghi trong cùng workflow action.
- [ ] Thêm `SELECT ... FOR UPDATE` hoặc conditional update tại điểm dễ xung đột đồng thời.
- [ ] Chỉ chuyển trạng thái Application khi trạng thái hiện tại mong đợi khớp.
- [ ] Chỉ cập nhật schedule/result/cancel Interview khi đúng trạng thái và ownership.
- [ ] Chỉ cập nhật draft/send/respond/expire/cancel Offer khi đúng trạng thái và hạn thời gian.
- [ ] Trả về kết quả số dòng ảnh hưởng rõ ràng thay vì boolean chung khi cần chẩn đoán conflict.
- [ ] Thêm DAO contract test bằng cách kiểm thử đã cấu hình trong dự án.

### Task 5 - Triển khai `RecruitmentWorkflowService`

- [ ] Tạo service có thể inject DAO và quản lý transaction boundary.
- [ ] Triển khai submit/approve/reject/publish/close vacancy.
- [ ] Triển khai screening và reject Application.
- [ ] Triển khai schedule/reschedule/cancel/result Interview.
- [ ] Triển khai save draft/send/respond/expire/cancel Offer.
- [ ] Triển khai chuyển Candidate bằng transaction idempotent riêng.
- [ ] Trả về typed result cho thành công, lỗi validation, không có quyền, không tìm thấy, trạng thái cũ và conflict.
- [ ] Xếp hàng lệnh Notification/email để chỉ chạy sau commit.

### Task 6 - Tạo và phê duyệt vacancy

- [ ] Kiểm tra title, description, requirement, location, salary, dữ liệu department/position và ngày publish.
- [ ] Giới hạn hành động tạo/sửa/xóa của HR Staff ở trạng thái trước publish được phép.
- [ ] Yêu cầu quyền HR Manager khi approve/reject.
- [ ] Lưu lý do từ chối và metadata approver.
- [ ] Bảo đảm chỉ vacancy đã duyệt/publish xuất hiện qua Plan 02.
- [ ] Thêm test phê duyệt trái phép, phê duyệt trạng thái cũ, sửa sau publish và ngày không hợp lệ.

### Task 7 - Hàng đợi Application, screening và truy cập CV

- [ ] Dùng record lấy Application làm trung tâm, join CandidateProfile và Recruitment.
- [ ] Giữ tìm kiếm, filter trạng thái, vacancy và ngày với phân trang backend nếu cần.
- [ ] Yêu cầu quyền HR khi xem danh sách/chi tiết Candidate và lấy CV được bảo vệ.
- [ ] Chỉ hiển thị action khi policy/state cho phép nhưng vẫn kiểm tra lại ở backend.
- [ ] Chuyển reject và screening qua workflow service.
- [ ] Thêm test quyền/ownership CV và action Application không hợp lệ.

### Task 8 - Workflow Interview

- [ ] Kiểm tra Application đủ điều kiện, vòng Interview, thời gian, địa điểm/meeting link, interviewer và note.
- [ ] Ngăn Interview active trùng cho cùng vòng trừ khi quy tắc cho phép rõ ràng.
- [ ] Hỗ trợ schedule, reschedule, cancel, pass và fail qua service transition.
- [ ] Lưu actor và timestamp cần cho audit.
- [ ] Cập nhật giai đoạn Application nhất quán với kết quả Interview.
- [ ] Phát message cho Candidate/Interviewer sau commit.
- [ ] Thêm test thời gian quá khứ, sai trạng thái, kết quả lặp, cancel sau complete và cập nhật đồng thời.

### Task 9 - Workflow Offer và phản hồi ứng viên

- [ ] Chỉ cho phép tạo Offer draft với Application/kết quả Interview đủ điều kiện.
- [ ] Kiểm tra position, offered salary, start date, expiry và note.
- [ ] Chỉ cho chỉnh sửa khi Draft.
- [ ] Gửi bằng transition Draft-to-Sent có điều kiện.
- [ ] Chỉ cho Candidate sở hữu accept/reject khi Offer đang Sent và chưa hết hạn.
- [ ] Triển khai expiry/cancellation với ảnh hưởng trạng thái Application được định nghĩa rõ.
- [ ] Ngăn gọi trực tiếp `OfferDAO.respondOffer` để bỏ qua service policy.
- [ ] Thêm test ownership, biên hết hạn, phản hồi lặp và concurrency.

### Task 10 - Chuyển Candidate thành Employee trong transaction

- [ ] Lock Application Hired và Offer Accepted trước khi chuyển.
- [ ] Kiểm tra Candidate đã được chuyển chưa và trả về Employee hiện có một cách an toàn.
- [ ] Ánh xạ rõ ràng trường CandidateProfile đã duyệt sang trường Employee.
- [ ] Kiểm tra unique email/account của Employee trước insert.
- [ ] Tạo hoặc liên kết SystemUser với đúng role Employee.
- [ ] Cập nhật trạng thái chuyển đổi Guest bằng giá trị schema live hỗ trợ, hoặc migrate enum trước.
- [ ] Cập nhật metadata chuyển đổi Application nếu schema có hỗ trợ.
- [ ] Commit thay đổi Employee, account, Guest và Application cùng nhau.
- [ ] Thêm rollback test gây lỗi sau từng ranh giới ghi.

### Task 11 - Controller, route và UI

- [ ] Giới hạn controller ở ngữ cảnh xác thực, permission, parse HTTP và render kết quả.
- [ ] Giữ hoặc redirect route cũ đến khi mọi liên kết JSP đã được chuyển.
- [ ] Thay target chi tiết Recruitment công khai còn thiếu thông qua Plan 02.
- [ ] Render nút action từ kết quả policy/state dùng chung.
- [ ] Dùng Post/Redirect/Get và flash message cho mọi mutation.
- [ ] Giữ quy ước shell dùng chung của HR Staff và HR Manager.
- [ ] Thêm test controller cho role, permission, validation, transition cũ và not-found.

### Task 12 - Event, audit và job vận hành

- [ ] Định nghĩa domain event cho quyết định vacancy, reject Application, thay đổi/kết quả Interview, gửi/phản hồi/hết hạn Offer và chuyển Employee.
- [ ] Gửi Notification và email template qua Plan 10.
- [ ] Ghi actor, timestamp, action, trạng thái cũ/mới và entity reference vào audit data.
- [ ] Làm xử lý expiry có tính idempotent và được bảo vệ nếu triển khai dưới dạng endpoint hoặc scheduled task.
- [ ] Ghi log lỗi bằng entity ID nhưng không chứa nội dung CV hoặc dữ liệu Candidate nhạy cảm.

### Task 13 - Xác minh

- [ ] Chạy test transition policy, DAO, service transaction, permission controller và trigger Notification.
- [ ] Chạy `mvn test`, `mvn -q compile` và `mvn -q package`.
- [ ] Thực hiện thủ công từ Draft vacancy đến chuyển Employee.
- [ ] Kiểm thử thủ công reject ở các giai đoạn vacancy, Application, Interview và Offer.
- [ ] Chạy kiểm tra concurrent/double-submit cho Application, kết quả Interview, phản hồi Offer và chuyển Employee.
- [ ] Xác minh rollback không để lại dữ liệu Employee/SystemUser/Guest/Application một phần.

## 9. Tiêu chí hoàn thành

- Một transition policy được tài liệu hóa kiểm soát mọi trạng thái tuyển dụng.
- Controller không còn điều phối ghi workflow nhiều bảng.
- Hành động Interview, Offer và chuyển Employee có điều kiện, transaction và idempotent.
- View công khai/ứng viên dùng Plan 02, còn transition có thẩm quyền nằm trong plan này.
- Gửi Notification/email xảy ra sau commit thông qua Plan 10.
- Happy path, các nhánh reject, concurrency, rollback, Maven và kiểm tra thủ công đều đạt.

