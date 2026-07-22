# Feature Tree 10 - Kế hoạch triển khai Dịch vụ Notification, Email và Chatbot

- **Trạng thái:** Bản nháp
- **Phạm vi chuẩn:** Feature Tree 10
- **Phụ thuộc:** Ngữ cảnh định danh của Plan 01
- **Được dùng chung bởi:** Plan 02-09

## 1. Mục tiêu

Cung cấp hạ tầng dùng lại và an toàn về permission cho Notification trong ứng dụng, Email vận hành và phản hồi Chatbot theo role, có fallback cục bộ, tùy chọn sinh nội dung bằng Gemini, Conversation History, Feedback, Content Safety và Rate Limiting.

## 2. Nguồn plan cũ

- `docs/superpowers/plans/2026-06-29-notification-system-phase1.md`
- `docs/superpowers/specs/2026-06-29-notification-system-design.md`
- Các phần Notification/Email trong plan Interview và Offer cũ

Phase 1 được đánh dấu đã triển khai, gồm nền tảng database/service. Plan chuẩn này mở rộng nền tảng đó thành hợp đồng event toàn ứng dụng và hoàn thiện quy tắc vận hành Email/Chatbot.

## 3. Ranh giới phạm vi

Plan này chịu trách nhiệm hạ tầng gửi, ánh xạ người nhận, trạng thái đã đọc, validation redirect, template, tích hợp AI/provider, Safety/History/Feedback Chatbot và test dùng chung.

Các domain plan chịu trách nhiệm thời điểm event xảy ra và business transaction sinh event. Plan 09 chịu trách nhiệm CRUD FAQ và UI review; plan này chịu trách nhiệm sử dụng FAQ active ở runtime.

## 4. Code và dữ liệu hiện tại cần audit

### Notification

- `NotificationController`, `AppNotificationFilter`, `HrStaffNotificationFilter`
- `NotificationService`, `NotificationRecipientService`, `NotificationDAO`
- `NotificationRedirectUtil`, `_NotificationBell.jspf`
- Notification model, migration và các test Notification hiện có

### Email

- `EmailSender`, `EmailTemplates`
- `META-INF/mail.example.properties`
- Các vị trí gọi Email cho verification, recovery, Leave handover, Interview và Offer

### Chatbot và AI

- `ChatbotController`, `ChatbotFeedbackController`, `admin/ChatbotFaqController`
- `ChatbotService`, `ChatbotRoleDataService`, `ChatbotRoleDataRepository`
- `ChatbotRateLimiter`, `ChatbotContentSafety`
- `AiChatService`, `AiChatProvider`, `GeminiGenerateContentProvider`, `AiProviderConfig`
- `ChatbotFaqDAO`, `ChatbotHistoryDAO`, `ChatbotFeedbackDAO`, `ChatbotReviewDAO`
- `chatbot.js`, `chatbot.css`, `AI_Assistant_Widget.jspf`
- Unit test Chatbot/AI và migration phase 2

## 5. Luồng dùng chung lúc chạy

```text
Domain transaction
  -> ghi business state
  -> commit
  -> phát domain event
  -> NotificationRecipientService ánh xạ User nhận
  -> NotificationService lưu Notification trong ứng dụng
  -> Email template/sender gửi Email vận hành khi cần
```

```text
Request Chatbot
  -> ngữ cảnh xác thực và role/page do server xác định
  -> validation kích thước + rate limit + content safety
  -> intent/FAQ/dữ liệu theo role cục bộ
  -> Gemini fallback tùy chọn khi được phép/đã cấu hình
  -> lưu History
  -> JSON response an toàn cùng identifier Feedback
```

## 6. Các bất biến dịch vụ dùng chung

- Business database commit không phụ thuộc SMTP hoặc AI provider có hoạt động hay không.
- Ownership Notification được thực thi trong cả truy vấn đọc và mark-read.
- Redirect target thuộc nội bộ, được allowlist/kiểm tra và phù hợp role của người dùng.
- Recipient được ánh xạ từ quan hệ database và permission, không dùng danh sách User do browser gửi.
- Delivery attempt lặp không tạo Notification hoặc Email trùng không kiểm soát.
- Role Chatbot được suy ra từ session đã xác thực, không tin JSON request.
- Chatbot không thể làm lộ dữ liệu mà người dùng không truy cập được qua ứng dụng thông thường.
- AI fallback là tùy chọn; FAQ/intent cục bộ vẫn hoạt động khi AI bị tắt hoặc lỗi.
- Secret và dữ liệu cá nhân trong prompt/User không được ghi vào log thông thường.

## 7. Các task triển khai

### Task 1 - Audit mọi call site Notification, Email và Chatbot

- [ ] Kiểm kê mọi lệnh gọi trực tiếp `NotificationDAO`, `NotificationService`, `EmailSender` và Chatbot provider.
- [ ] Ánh xạ mỗi call sang domain event, recipient, channel, template, redirect và kỳ vọng idempotency.
- [ ] Xác định controller gửi trước commit hoặc trộn lỗi delivery với thành công nghiệp vụ.
- [ ] Kiểm kê mọi filter/fragment inject Notification và hành vi chồng chéo.
- [ ] Thiết lập baseline test cho NotificationService và ChatbotService hiện tại.

### Task 2 - Định nghĩa catalog domain event của ứng dụng

- [ ] Tạo tên event ổn định cho Recruitment, Interview, Offer, chuyển Employee, Contract, Payroll, Leave, Task, Account và Administration.
- [ ] Định nghĩa trường bắt buộc: event ID/key, entity type/ID, actor, ngữ cảnh recipient, timestamp và parameter hiển thị an toàn.
- [ ] Định nghĩa channel policy: chỉ in-app, chỉ Email, cả hai hoặc chỉ Audit không gửi cho User.
- [ ] Gán mỗi event cho đúng một domain plan sở hữu để ngăn phát trùng.
- [ ] Định nghĩa cách tạo idempotency key cho HTTP request lặp, retry và scheduled job.
- [ ] Thêm test hợp đồng event.

### Task 3 - Chuẩn hóa schema và repository Notification

- [ ] Xác minh UserID, title, message, type, entity routing, redirect URL, read state và timestamp giữa migration/schema live.
- [ ] Thêm event/idempotency key tùy chọn cùng chiến lược unique/index khi cần.
- [ ] Thêm index cho UserID + read state + created time.
- [ ] Giữ khả năng create nhận Connection cho workflow cố ý lưu event/outbox record trong transaction.
- [ ] Làm thao tác find/count/mark-read được scope theo ownership.
- [ ] Giới hạn an toàn số lượng recent list.
- [ ] Đồng bộ bootstrap schema và migration.
- [ ] Thêm repository test cho duplicate key, ownership, thứ tự, limit và mark-all.

### Task 4 - Hợp nhất `NotificationService`

- [ ] Giữ một Notification builder dùng chung có validation bắt buộc và default an toàn.
- [ ] Thay Notification do controller tự dựng không kiểm soát bằng mapping event-to-notification có type.
- [ ] Quyết định convenience method hiện có được giữ làm adapter hay chuyển vào event handler.
- [ ] Loại trùng UserID khi tạo cho batch recipient.
- [ ] Trả kết quả persistence/delivery phù hợp logging mà không làm lộ nội dung message không cần thiết.
- [ ] Giữ business success độc lập với lỗi Notification sau commit không quan trọng.
- [ ] Thêm service test cho từng event mapping và ngăn duplicate.

### Task 5 - Gia cố ánh xạ recipient

- [ ] Ánh xạ Candidate, Employee, HR Staff, HR Manager, Department Manager, Admin, Interviewer và recipient liên kết Actor từ dữ liệu có thẩm quyền.
- [ ] Lọc User inactive/locked khi không cần gửi.
- [ ] Thực thi department scope cho event gửi tới manager.
- [ ] Tránh gửi cho chính actor trừ khi event policy yêu cầu rõ ràng.
- [ ] Loại trùng User đủ điều kiện qua nhiều Role/quan hệ.
- [ ] Thêm test thiếu mapping, nhiều role phù hợp, User inactive và ranh giới Department.

### Task 6 - Notification bell, hành động đọc và redirect

- [ ] Hợp nhất trách nhiệm `AppNotificationFilter` và `HrStaffNotificationFilter` hoặc tài liệu hóa phạm vi URL không chồng chéo.
- [ ] Inject unread count và recent Notification đúng một lần cho mỗi HTML request phù hợp.
- [ ] Mark một Notification read bằng NotificationID cùng UserID đã xác thực.
- [ ] Mark all read chỉ cho UserID đã xác thực.
- [ ] Kiểm tra redirect bằng `NotificationRedirectUtil` theo local path và quyền role/module.
- [ ] Dùng trang fallback an toàn cho entity cũ, đã xóa hoặc không được phép.
- [ ] Thêm test ownership, open-redirect, target không có quyền, entity đã xóa và mark-all.

### Task 7 - Quyết định độ tin cậy gửi sau commit

- [ ] Với best-effort đơn giản, định nghĩa dispatcher sau commit cùng policy log/retry khi lỗi.
- [ ] Nếu cần đảm bảo delivery, thêm thiết kế outbox table và worker/listener idempotent.
- [ ] Không gửi Email bên trong transaction chưa commit.
- [ ] Định nghĩa số lần retry, backoff, lỗi cuối và khả năng quan sát cho vận hành.
- [ ] Bảo đảm retry dùng event/idempotency key.
- [ ] Thêm test commit thành công nhưng delivery lỗi và retry không tạo trùng.

### Task 8 - Chuẩn hóa cấu hình và sender Email

- [ ] Load SMTP host, port, username, credential, sender, TLS, timeout và feature switch từ environment/cấu hình không commit.
- [ ] Kiểm tra cấu hình mà không ghi log credential.
- [ ] Định nghĩa sender interface có thể thay bằng fake trong test.
- [ ] Thiết lập UTF-8 rõ ràng cho subject/body.
- [ ] Định nghĩa validation recipient và cách dựng header an toàn.
- [ ] Thêm timeout và phân loại lỗi rõ ràng.
- [ ] Thay việc controller tự tạo sender trực tiếp bằng abstraction dùng chung.
- [ ] Thêm test cấu hình, fake sender, timeout, recipient không hợp lệ và UTF-8.

### Task 9 - Tập trung Email template vận hành

- [ ] Tạo typed template cho verification, recovery, Interview schedule/change/result, Offer, Leave handover/decision, Contract, Payroll, Task và Account status khi cần.
- [ ] Escape hoặc format an toàn giá trị do User kiểm soát trong template.
- [ ] Chỉ đưa vào dữ liệu cá nhân/nghiệp vụ tối thiểu cần thiết.
- [ ] Dùng application link đã kiểm tra thay vì đường dẫn filesystem hoặc route chỉ dùng nội bộ.
- [ ] Thêm snapshot/assertion test cho subject, field bắt buộc, field tùy chọn bị thiếu và hiển thị tiếng Việt.
- [ ] Giữ marketing/bulk mail ngoài plan Email vận hành này trừ khi bổ sung rõ ràng sau.

### Task 10 - Gia cố biên request Chatbot

- [ ] Chỉ nhận POST JSON cho message và hành vi GET đã tài liệu hóa nếu còn giữ.
- [ ] Thực thi content type, độ dài message tối đa, JSON hợp lệ và field bắt buộc.
- [ ] Suy ra authentication, role, UserID và Employee/Department scope từ session server.
- [ ] Xem page context browser gửi là gợi ý và chuẩn hóa về trang đã biết.
- [ ] Áp dụng rate limiting trước thao tác AI/provider tốn tài nguyên.
- [ ] Áp dụng content safety trước xử lý cục bộ hoặc bên ngoài.
- [ ] Trả JSON nhất quán gồm status, intent, reply, suggestion, conversation ID và message ID.
- [ ] Thêm test JSON lỗi, input quá dài, giả mạo role, chưa xác thực, rate-limit và safety.

### Task 11 - Intent cục bộ, FAQ và dữ liệu theo role

- [ ] Định nghĩa thứ tự xác định giữa greeting, navigation intent, FAQ active, dữ liệu theo role và fallback.
- [ ] Chỉ dùng FAQ active do Plan 09 quản trị.
- [ ] Giữ chuẩn hóa dấu tiếng Việt mà không làm sai nội dung lưu/hiển thị.
- [ ] Phân quyền mọi truy vấn role data bằng cùng rule permission/ownership như trang thông thường.
- [ ] Giới hạn số kết quả và che trường nhạy cảm khỏi phản hồi Chatbot.
- [ ] Trả suggestion an toàn phù hợp role và trang hiện tại.
- [ ] Thêm test thứ tự intent, FAQ activation, biến thể có/không dấu, ranh giới role và che dữ liệu.

### Task 12 - AI/Gemini fallback

- [ ] Giữ Gemini disabled khi thiếu cấu hình hoặc feature switch tắt.
- [ ] Chuyển call provider qua `AiChatProvider`/`AiChatService`, không gọi từ controller.
- [ ] Xây prompt từ system context đã duyệt và user context an toàn tối thiểu.
- [ ] Không gửi secret, mật khẩu, nội dung CV, dữ liệu chữ ký, record Employee không giới hạn hoặc dữ liệu cá nhân không cần thiết.
- [ ] Áp dụng timeout provider và giới hạn kích thước response.
- [ ] Kiểm tra lại safety/format output trước khi trả reply.
- [ ] Fallback sang phản hồi cục bộ khi provider timeout, lỗi, output không hợp lệ hoặc hết quota.
- [ ] Thêm test fake provider thành công, timeout, lỗi, output không an toàn, disabled và fallback.

### Task 13 - Conversation History và Feedback

- [ ] Định nghĩa ownership Conversation và quy tắc tiếp tục session.
- [ ] Lưu User message, intent được chọn, nguồn response, assistant reply, timestamp và provider metadata an toàn.
- [ ] Ngăn một User đọc hoặc gửi Feedback cho message của User khác.
- [ ] Chấp nhận Useful/NotUseful một lần cho mỗi cặp message/User hoặc định nghĩa update semantics.
- [ ] Lưu comment Feedback tùy chọn đã sanitize nếu hỗ trợ.
- [ ] Cung cấp hàng đợi negative/fallback review cho Plan 09 mà không làm lộ rộng dữ liệu Conversation riêng tư.
- [ ] Thêm test ownership, Feedback trùng, chưa xác thực và lỗi History.

### Task 14 - Observability, privacy và bảo trì

- [ ] Thay `System.out`/`System.err` còn lại trong các service này bằng SLF4J.
- [ ] Ghi log outcome Event/Provider, latency, ID an toàn và loại lỗi mà không ghi đầy đủ nội dung riêng tư mặc định.
- [ ] Thêm retention rule có thể cấu hình cho History/Notification nếu cần.
- [ ] Định nghĩa health indicator cho cấu hình SMTP và Gemini mà không gọi live hoặc làm lộ secret.
- [ ] Tài liệu hóa hành vi local fallback cho vận hành.
- [ ] Xác minh không commit production secret.

### Task 15 - Xác minh tích hợp liên plan

- [ ] Xác minh phát event từ luồng Candidate, Recruitment, Contract, Payroll, Schedule/Leave, Task, Account và Admin.
- [ ] Xác minh mỗi event có đúng recipient, một Notification, Email policy mong đợi và redirect có quyền.
- [ ] Chạy test Notification, Recipient, Redirect, EmailTemplate, Chatbot, RoleData, RateLimit, ContentSafety, History, Feedback và AI.
- [ ] Chạy `mvn test`, `mvn -q compile` và `mvn -q package`.
- [ ] Kiểm thử thủ công bell/read action ở nhiều role và Chatbot cục bộ khi Gemini tắt.
- [ ] Kiểm thử thủ công hành vi fake/provider-enabled fallback mà không dùng production credential.

## 8. Tiêu chí hoàn thành

- Domain plan phát event ổn định, có chủ sở hữu duy nhất sau business transaction thành công.
- Recipient, read state và redirect Notification an toàn về ownership/permission.
- Email vận hành dựa trên template, cấu hình được, test được, dùng UTF-8 và gửi sau commit.
- Intent/FAQ/role data cục bộ của Chatbot có tính xác định và an toàn về permission.
- Gemini là tùy chọn, giới hạn quyền riêng tư, có timeout và luôn có local fallback.
- History và Feedback được kiểm tra ownership và dùng được trong UI review Plan 09.
- Test liên plan, Maven và kiểm tra Notification/Email/Chatbot thủ công đều đạt.

