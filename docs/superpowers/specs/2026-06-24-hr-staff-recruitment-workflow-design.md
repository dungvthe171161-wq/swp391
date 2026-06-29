# HR Staff Recruitment Workflow Design

**Ngày chốt:** 2026-06-24  
**Phạm vi:** Ổn định lõi dữ liệu tuyển dụng và triển khai workflow dành cho HR Staff  
**Công nghệ:** Java 17, Jakarta Servlet/JSP, JDBC DAO, MySQL 8.0

## 1. Mục tiêu

Xây dựng một workflow tuyển dụng nhất quán, có transaction và phân quyền rõ ràng cho HR Staff:

1. Xem danh sách Application.
2. Xem hồ sơ ứng viên và đúng CV của từng Application.
3. Chuyển Application từ Applied sang Screening.
4. Tạo một lịch Interview.
5. Cập nhật kết quả Interview.
6. Tạo Offer dạng Draft và gửi Offer.
7. Gửi Notification sau các bước có ảnh hưởng đến ứng viên.
8. Chuyển ứng viên đã chấp nhận Offer thành Employee bằng một thao tác xác nhận riêng.

Workflow mới phải giữ toàn bộ lịch sử tuyển dụng và không được xóa Guest khi tạo Employee.

## 2. Quyết định nghiệp vụ đã chốt

### 2.1 Guest và Application

- `Guest` là hồ sơ ứng viên dùng chung, không đại diện cho một lần ứng tuyển.
- Mỗi tài khoản ứng viên có tối đa một Guest.
- Một Guest có thể có nhiều Application.
- Mỗi Application thuộc đúng một Recruitment.
- Một Guest không được có hai Application cho cùng một Recruitment.
- CV, cover letter và nguồn ứng tuyển của từng lần ứng tuyển thuộc Application.
- Thông tin cá nhân dùng chung như họ tên, email, điện thoại, ngày sinh và địa chỉ thuộc Guest.

### 2.2 Interview

- Mỗi Application chỉ có một Interview trong phạm vi hiện tại.
- Không triển khai nhiều vòng phỏng vấn.
- Kết quả `Failed` chỉ là kết quả đánh giá, không tự động Reject Application.
- HR Staff phải bấm `Confirm Reject` để đưa Application sang `Rejected`.
- Interview `Passed` không tự động tạo hoặc gửi Offer.

### 2.3 Offer

- Offer được tách thành hai bước: `Draft` và `Sent`.
- Offer Draft chưa hiển thị cho ứng viên và không gửi Notification.
- HR Staff phải kiểm tra và bấm `Send Offer`.
- Khi ứng viên chấp nhận Offer, Application chuyển sang `OfferAccepted`.
- `OfferAccepted` chưa phải `Hired`.
- HR Staff phải có quyền `CREATE_EMPLOYEE` và hoàn tất thao tác Create Employee.
- Khi ứng viên từ chối Offer, Application chuyển sang `OfferDeclined`.
- Khi Offer hết hạn, Application chuyển sang `OfferExpired`.
- Từ `OfferDeclined` hoặc `OfferExpired`, HR Staff có thể tạo Offer mới.
- Mỗi lần tạo lại Offer phải giữ được lịch sử Offer cũ.

### 2.4 Reject

- HR Staff được Reject trực tiếp từ `Applied` hoặc `Screening`.
- HR Staff được Reject sau khi Interview đã có kết quả.
- Mọi thao tác Reject đều bắt buộc nhập lý do.
- Reject phải tạo Notification cho ứng viên.
- Reject là quyết định thủ công; không có bước nào tự động Reject Application.

### 2.5 Chuyển thành Employee

- Không xóa Guest.
- Không xóa hoặc làm mất Application, Interview, Offer và Notification.
- Không tự động tạo Employee khi ứng viên Accept Offer.
- Chỉ Application ở trạng thái `OfferAccepted` mới được phép chuyển thành Employee.
- Người thao tác phải có quyền `CREATE_EMPLOYEE`.
- Sau khi tạo Employee thành công:
  - SystemUser được liên kết với Employee.
  - Role tài khoản được chuyển từ Guest sang Employee.
  - Application chuyển sang `Hired`.
  - Guest vẫn tồn tại để lưu lịch sử ứng viên.

## 3. State machine

### 3.1 Application status

Các trạng thái chuẩn:

| Status | Ý nghĩa |
|---|---|
| `Applied` | Ứng viên vừa gửi hồ sơ |
| `Screening` | HR Staff đang sàng lọc |
| `InterviewScheduled` | Đã tạo lịch phỏng vấn |
| `InterviewCompleted` | Đã ghi nhận kết quả phỏng vấn |
| `OfferDraft` | Đã tạo Offer nháp |
| `OfferSent` | Offer đã gửi cho ứng viên |
| `OfferAccepted` | Ứng viên đã chấp nhận Offer, chờ tạo Employee |
| `OfferDeclined` | Ứng viên từ chối Offer |
| `OfferExpired` | Offer hết hạn mà chưa được chấp nhận |
| `Rejected` | HR Staff xác nhận loại |
| `Withdrawn` | Ứng viên rút hồ sơ |
| `Hired` | Đã tạo Employee thành công |

### 3.2 Chuyển trạng thái hợp lệ

| Từ trạng thái | Hành động | Sang trạng thái |
|---|---|---|
| `Applied` | Start Screening | `Screening` |
| `Applied` | Confirm Reject | `Rejected` |
| `Screening` | Schedule Interview | `InterviewScheduled` |
| `Screening` | Confirm Reject | `Rejected` |
| `InterviewScheduled` | Record Interview Result | `InterviewCompleted` |
| `InterviewScheduled` | Reschedule Interview | `InterviewScheduled` |
| `InterviewScheduled` | Cancel Interview và Confirm Reject | `Rejected` |
| `InterviewCompleted` với kết quả `Passed` | Create Offer Draft | `OfferDraft` |
| `InterviewCompleted` | Confirm Reject | `Rejected` |
| `OfferDraft` | Update Draft | `OfferDraft` |
| `OfferDraft` | Send Offer | `OfferSent` |
| `OfferDraft` | Cancel Draft | `InterviewCompleted` |
| `OfferSent` | Candidate Accept | `OfferAccepted` |
| `OfferSent` | Candidate Decline | `OfferDeclined` |
| `OfferSent` | Expire Offer | `OfferExpired` |
| `OfferSent` | Cancel Offer | `InterviewCompleted` |
| `OfferDeclined` | Create New Offer Draft | `OfferDraft` |
| `OfferExpired` | Create New Offer Draft | `OfferDraft` |
| `OfferAccepted` | Create Employee | `Hired` |

Không controller, DAO hoặc JSP nào được tự đặt trạng thái ngoài bảng chuyển trạng thái này.

### 3.3 Trạng thái riêng của Interview

`Interview.Status`:

- `Scheduled`
- `Rescheduled`
- `Completed`
- `Cancelled`
- `NoShow`

`Interview.Result`:

- `Pending`
- `Passed`
- `Failed`

`Interview.Result = Failed` không đồng nghĩa với `Application.Status = Rejected`.

### 3.4 Trạng thái riêng của Offer

`Offer.Status`:

- `Draft`
- `Sent`
- `Accepted`
- `Declined`
- `Expired`
- `Cancelled`

Không dùng `Rejected` cho Offer vì cần phân biệt ứng viên từ chối Offer với HR Staff loại Application.

## 4. Mô hình dữ liệu

### 4.1 Guest

Guest phải có:

- `GuestID`
- `UserID`
- Thông tin hồ sơ cá nhân
- Các mốc thời gian tạo/cập nhật

Ràng buộc:

- `UserID` nullable để hỗ trợ dữ liệu cũ hoặc ứng viên chưa có tài khoản.
- Khi `UserID` khác null, phải unique.
- Không sử dụng `Guest.Status` làm nguồn trạng thái tuyển dụng.
- Không sử dụng `Guest.RecruitmentID` làm quan hệ ứng tuyển mới.
- Hai cột cũ có thể được giữ tạm trong giai đoạn migration nhưng code mới không được ghi vào chúng.

### 4.2 Application

Application là nguồn sự thật của workflow:

- `ApplicationID`
- `GuestID`
- `RecruitmentID`
- `Status`
- `CV`
- `CoverLetter`
- `Source`
- `AppliedDate`
- `RejectedReason`
- `CreatedDate`
- `UpdatedDate`

Ràng buộc:

- Unique `(GuestID, RecruitmentID)`.
- `GuestID` không được cascade delete trong nghiệp vụ chuyển thành Employee.
- Không cần `CurrentStep`; `Status` là nguồn sự thật duy nhất.

### 4.3 Interview

Do chỉ có một vòng phỏng vấn:

- Unique `ApplicationID`.
- Không cần dùng `RoundNo` trong code mới.
- Có thể giữ cột `RoundNo` với giá trị mặc định `1` để migration tương thích, nhưng UI và service không cho tạo vòng thứ hai.

Các trường chính:

- `InterviewID`
- `ApplicationID`
- `ScheduledAt`
- `Location`
- `MeetingLink`
- `InterviewerEmployeeID`
- `Status`
- `Result`
- `Note`

### 4.4 Offer

Để giữ lịch sử khi gửi lại Offer:

- Không đặt unique tuyệt đối trên `ApplicationID`.
- Một Application có thể có nhiều Offer theo thời gian.
- Tại một thời điểm chỉ có tối đa một Offer ở trạng thái `Draft` hoặc `Sent`.

Các trường chính:

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
- `CreatedByUserID`
- `CreatedDate`
- `UpdatedDate`

### 4.5 Liên kết khi tuyển thành công

Thêm liên kết có thể truy vết từ Application sang Employee:

- `Application.HiredEmployeeID`, nullable.
- `Application.HiredAt`, nullable.

Khi Application là `Hired`, hai trường trên phải có giá trị.

## 5. Xử lý Guest trùng UserID

Migration phải chạy trước khi thêm unique constraint:

1. Tìm các nhóm Guest có cùng `UserID`.
2. Chọn Guest chính theo ưu tiên:
   - Guest có hồ sơ cá nhân đầy đủ hơn.
   - Nếu bằng nhau, chọn Guest có `UpdatedDate` mới hơn.
   - Nếu vẫn bằng nhau, chọn `GuestID` nhỏ hơn.
3. Chuyển tất cả Application từ Guest phụ sang Guest chính.
4. Nếu việc chuyển tạo trùng `(GuestID, RecruitmentID)`, giữ Application có dữ liệu workflow mới hơn và không xóa lịch sử phụ trước khi sao lưu/audit.
5. Gộp các trường hồ sơ còn thiếu vào Guest chính.
6. Không xóa Guest phụ ngay trong migration đầu tiên; đánh dấu đã merge hoặc lưu mapping merge.
7. Sau khi xác nhận dữ liệu, thêm unique constraint cho `Guest.UserID`.

Service tạo hoặc lấy Guest phải ưu tiên tìm bằng `UserID`, sau đó mới fallback bằng email cho dữ liệu legacy.

## 6. Kiến trúc ứng dụng

### 6.1 Controller

Controller chỉ:

- Đọc và validate input HTTP.
- Kiểm tra session.
- Kiểm tra permission.
- Gọi service.
- Chuyển kết quả thành forward, redirect hoặc HTTP response.

Controller không trực tiếp phối hợp nhiều DAO và không tự quản lý transaction.

### 6.2 RecruitmentWorkflowService

Tạo một service trung tâm cho workflow:

- `submitApplication`
- `startScreening`
- `scheduleInterview`
- `rescheduleInterview`
- `recordInterviewResult`
- `confirmReject`
- `createOfferDraft`
- `updateOfferDraft`
- `sendOffer`
- `acceptOffer`
- `declineOffer`
- `expireOffer`
- `cancelOffer`
- `createEmployeeFromAcceptedOffer`

Service chịu trách nhiệm:

- Khóa/đọc Application hiện tại.
- Kiểm tra transition hợp lệ.
- Thực hiện toàn bộ thay đổi trong một transaction.
- Tạo Notification trong cùng transaction.
- Ghi audit log cho thao tác quan trọng.

### 6.3 DAO

DAO phải hỗ trợ nhận `Connection` từ service đối với thao tác nằm trong workflow.

Ví dụ:

- `ApplicationDAO.updateStatus(Connection, ...)`
- `InterviewDAO.insert(Connection, ...)`
- `OfferDAO.insert(Connection, ...)`
- `NotificationDAO.insert(Connection, ...)`
- `EmployeeDAO.insert(Connection, ...)`

DAO không tự commit hoặc rollback khi được truyền Connection.

## 7. Transaction

Mỗi workflow sau phải atomic:

### 7.1 Start Screening

1. Xác nhận Application đang là `Applied`.
2. Cập nhật Application thành `Screening`.
3. Tạo Notification.
4. Ghi audit log.
5. Commit.

### 7.2 Schedule Interview

1. Xác nhận Application đang là `Screening`.
2. Xác nhận Application chưa có Interview.
3. Tạo Interview `Scheduled/Pending`.
4. Cập nhật Application thành `InterviewScheduled`.
5. Tạo Notification chứa thời gian và địa điểm/link.
6. Ghi audit log.
7. Commit.

### 7.3 Record Interview Result

1. Xác nhận Application đang là `InterviewScheduled`.
2. Cập nhật Interview thành `Completed` và kết quả `Passed` hoặc `Failed`.
3. Cập nhật Application thành `InterviewCompleted`.
4. Ghi audit log.
5. Commit.

Không gửi Notification kết quả nội bộ tại bước này. Notification chỉ được gửi khi HR tạo Offer hoặc Confirm Reject.

### 7.4 Confirm Reject

1. Kiểm tra trạng thái hiện tại cho phép Reject.
2. Validate lý do không rỗng.
3. Cập nhật Application thành `Rejected`.
4. Lưu `RejectedReason`.
5. Hủy Interview hoặc Offer đang mở nếu có.
6. Tạo Notification.
7. Ghi audit log.
8. Commit.

### 7.5 Create Offer Draft

1. Xác nhận Application là `InterviewCompleted` và Interview có kết quả `Passed`, hoặc Application là `OfferDeclined`/`OfferExpired` từ một Interview đã `Passed`.
2. Xác nhận không có Offer Draft/Sent khác đang hoạt động.
3. Tạo Offer `Draft`.
4. Cập nhật Application thành `OfferDraft`.
5. Ghi audit log.
6. Commit.

Không tạo Notification.

### 7.6 Send Offer

1. Xác nhận Application là `OfferDraft`.
2. Validate đầy đủ Position, Salary, StartDate và ExpiredAt.
3. Cập nhật Offer thành `Sent`, đặt `SentAt`.
4. Cập nhật Application thành `OfferSent`.
5. Tạo Notification.
6. Ghi audit log.
7. Commit.

Email chỉ được gửi sau khi transaction commit thành công.

### 7.7 Candidate Responds to Offer

Accept:

1. Xác nhận Offer là `Sent` và chưa hết hạn.
2. Cập nhật Offer thành `Accepted`.
3. Cập nhật Application thành `OfferAccepted`.
4. Tạo Notification cho ứng viên xác nhận hệ thống đã nhận phản hồi.
5. Tạo Notification hoặc dashboard event cho HR Staff.
6. Ghi audit log.
7. Commit.

Decline:

1. Xác nhận Offer là `Sent`.
2. Cập nhật Offer thành `Declined`.
3. Cập nhật Application thành `OfferDeclined`.
4. Tạo Notification xác nhận phản hồi.
5. Ghi audit log.
6. Commit.

### 7.8 Expire Offer

Job hoặc request kiểm tra hết hạn phải:

1. Tìm Offer `Sent` có `ExpiredAt < NOW()`.
2. Cập nhật Offer thành `Expired`.
3. Cập nhật Application thành `OfferExpired`.
4. Tạo Notification.
5. Ghi audit log.
6. Commit theo từng Offer hoặc từng batch có kiểm soát.

### 7.9 Create Employee

1. Xác nhận Application là `OfferAccepted`.
2. Kiểm tra quyền `CREATE_EMPLOYEE`.
3. Khóa Application, Guest và SystemUser liên quan.
4. Validate email chưa thuộc Employee khác.
5. Tạo Employee.
6. Liên kết SystemUser với Employee.
7. Chuyển role sang Employee.
8. Cập nhật Application thành `Hired`.
9. Lưu `HiredEmployeeID` và `HiredAt`.
10. Giữ nguyên Guest.
11. Tạo Notification.
12. Ghi audit log.
13. Commit.

Nếu bất kỳ bước nào thất bại, rollback toàn bộ; không được xóa Employee thủ công để mô phỏng rollback.

## 8. Phân quyền

### 8.1 MANAGE_APPLICANTS

Cho phép:

- Xem danh sách Application.
- Xem Guest profile và Application CV.
- Start Screening.
- Schedule/Reschedule Interview.
- Record Interview Result.
- Confirm Reject.
- Create/Update/Send/Cancel Offer.

Không cho phép tạo Employee.

### 8.2 CREATE_EMPLOYEE

Cho phép:

- Mở form Create Employee từ Application `OfferAccepted`.
- Hoàn tất chuyển ứng viên thành Employee.

### 8.3 Quy tắc kiểm tra

- Permission phải được kiểm tra tại controller.
- Service phải kiểm tra lại điều kiện nghiệp vụ và trạng thái.
- Không chỉ dựa vào việc ẩn nút trên JSP.
- Danh sách Application của HR Staff dùng `MANAGE_APPLICANTS`, không dùng `VIEW_RECRUITMENT`.
- Create Employee dùng `CREATE_EMPLOYEE`, không dùng `VIEW_EMPLOYEES`.

## 9. HR Staff UI

### 9.1 Danh sách Application

Mỗi dòng hiển thị:

- Application ID.
- Họ tên ứng viên.
- Vị trí ứng tuyển.
- Ngày ứng tuyển.
- Application status.
- Interview status/result nếu có.
- Offer status nếu có.
- Nút xem chi tiết.

Bộ lọc:

- Tên/email ứng viên.
- Recruitment.
- Application status.
- Khoảng ngày ứng tuyển.

### 9.2 Chi tiết Application

Trang chi tiết gồm:

- Hồ sơ Guest.
- CV của chính Application.
- Cover letter.
- Thông tin Recruitment.
- Timeline trạng thái.
- Interview.
- Offer.
- Notification/audit summary phù hợp.
- Các action hợp lệ theo trạng thái và permission.

### 9.3 Action theo trạng thái

- `Applied`: Start Screening, Confirm Reject.
- `Screening`: Schedule Interview, Confirm Reject.
- `InterviewScheduled`: Reschedule, Record Result, Confirm Reject.
- `InterviewCompleted` với kết quả `Passed`: Create Offer Draft hoặc Confirm Reject.
- `InterviewCompleted` với kết quả `Failed`: cập nhật lại kết quả nếu nhập nhầm hoặc Confirm Reject; không được tạo Offer.
- `OfferDraft`: Edit Draft, Send Offer, Cancel Draft.
- `OfferSent`: View Offer, Cancel Offer.
- `OfferAccepted`: Create Employee nếu có `CREATE_EMPLOYEE`.
- `OfferDeclined`: Create New Offer Draft.
- `OfferExpired`: Create New Offer Draft.
- `Rejected`, `Withdrawn`, `Hired`: chỉ xem lịch sử.

## 10. Notification

Tạo Notification sau:

- Application được gửi.
- Screening bắt đầu.
- Interview được tạo hoặc đổi lịch.
- HR xác nhận Reject.
- Offer được gửi.
- Offer được Accept, Decline hoặc Expire.
- Employee được tạo thành công.

Không tạo Notification khi:

- HR ghi chú nội bộ.
- HR lưu hoặc sửa Offer Draft.
- HR ghi kết quả Interview nhưng chưa đưa ra quyết định tiếp theo.

Notification phải được insert trong cùng transaction với thay đổi trạng thái.

Email không nằm trong transaction database. Email chỉ được gửi sau commit; thất bại email không rollback trạng thái đã commit và phải được log hoặc đưa vào hàng đợi gửi lại.

## 11. Xử lý lỗi và cạnh tranh dữ liệu

- Service phải đọc Application bằng cơ chế khóa phù hợp khi chuyển trạng thái.
- Hai request đồng thời không được thực hiện cùng một transition hai lần.
- Transition từ trạng thái cũ phải dùng điều kiện trạng thái hiện tại trong câu UPDATE.
- Nếu trạng thái đã thay đổi, trả lỗi conflict thay vì ghi đè.
- Không cho gửi Offer đã hết hạn.
- Không cho Accept/Decline Offer không thuộc tài khoản ứng viên hiện tại.
- Không cho tạo Interview thứ hai.
- Không cho tạo Employee lần thứ hai từ cùng Application.
- Validation lỗi không được làm thay đổi dữ liệu.

## 12. Audit log

Ghi audit cho:

- Start Screening.
- Schedule/Reschedule Interview.
- Record Interview Result.
- Confirm Reject.
- Create/Send/Cancel Offer.
- Accept/Decline/Expire Offer.
- Create Employee.

Audit cần chứa:

- User thực hiện.
- Application ID.
- Loại hành động.
- Trạng thái cũ.
- Trạng thái mới.
- Thời điểm.
- Lý do hoặc ghi chú khi phù hợp.

## 13. Migration và tương thích dữ liệu cũ

Thứ tự migration:

1. Sao lưu và báo cáo Guest trùng UserID.
2. Merge quan hệ Guest/Application.
3. Thêm unique constraint cho Guest.UserID.
4. Bổ sung trạng thái Application mới.
5. Thêm `RejectedReason`, `HiredEmployeeID`, `HiredAt`.
6. Bỏ unique tuyệt đối của Offer.ApplicationID và thêm ràng buộc nghiệp vụ cho Offer đang hoạt động.
7. Đảm bảo Interview chỉ có một bản ghi cho mỗi Application.
8. Backfill Application từ Guest legacy nếu chưa có.
9. Chuyển code đọc trạng thái và CV sang Application.
10. Ngừng ghi `Guest.Status`, `Guest.RecruitmentID` và `Guest.CV` cho workflow mới.

Không xóa cột legacy trong cùng đợt triển khai đầu tiên. Chỉ xóa sau khi code mới ổn định và dữ liệu đã được kiểm chứng.

## 14. Tiêu chí nghiệm thu

Workflow được xem là hoàn thành khi:

1. HR Staff có `MANAGE_APPLICANTS` xem được danh sách Application.
2. CV hiển thị là CV thuộc Application đang xem.
3. Không thể chuyển trạng thái sai state machine.
4. Chỉ tạo được một Interview cho mỗi Application.
5. Interview Failed không tự Reject.
6. Confirm Reject bắt buộc có lý do và tạo Notification.
7. Offer Draft không hiển thị cho ứng viên.
8. Send Offer đồng bộ Offer/Application và tạo Notification trong một transaction.
9. Candidate Accept chuyển Application sang `OfferAccepted`, chưa tạo Employee.
10. Candidate Decline chuyển sang `OfferDeclined`.
11. Offer hết hạn chuyển sang `OfferExpired`.
12. Có thể tạo Offer mới sau Declined/Expired mà vẫn giữ lịch sử Offer cũ.
13. Chỉ người có `CREATE_EMPLOYEE` mới tạo Employee.
14. Create Employee giữ nguyên Guest và toàn bộ lịch sử tuyển dụng.
15. Nếu Create Employee thất bại giữa chừng, không có Employee/User/Application bị cập nhật dở dang.
16. Guest.UserID không còn dữ liệu trùng.
17. Mỗi bước cần thông báo đều có Notification.
18. Dự án compile thành công và các test workflow đều pass.

## 15. Ngoài phạm vi phiên bản đầu

- Nhiều vòng Interview.
- Hội đồng phỏng vấn nhiều người.
- Chấm điểm theo bộ tiêu chí.
- Offer approval nhiều cấp.
- Chữ ký điện tử.
- Tự động tạo Employee ngay khi Accept Offer.
- Xóa hoàn toàn các cột Guest legacy.

## 16. Thứ tự triển khai khuyến nghị

1. Migration và ổn định Guest/Application.
2. State transition policy và transaction-capable DAO.
3. RecruitmentWorkflowService.
4. Permission `MANAGE_APPLICANTS` và `CREATE_EMPLOYEE`.
5. HR Staff Application list/detail.
6. Screening và Reject.
7. Interview một vòng.
8. Offer Draft/Send/Response/Expiry.
9. Create Employee từ `OfferAccepted`.
10. Notification, email-after-commit và audit hoàn chỉnh.
11. Integration tests và regression test.
