# BetterHR AI Assistant — Tài liệu đặc tả tổng thể (Master Specification)
Trạng thái: Bản thảo
Module: AI
Độ ưu tiên: Cao
Đặc tả liên quan: `_Common/permission-matrix.spec.md`, `_Common/notification.spec.md`, `_Common/status-workflow.spec.md`
Mã nguồn liên quan: `com.hrm.controller.*`, `com.hrm.dao.*`, `com.hrm.model.*`, `com.hrm.service.*`

---

## 1. Tổng quan

BetterHR AI Assistant là trợ lý trí tuệ nhân tạo tích hợp trực tiếp vào hệ thống quản lý nhân sự BetterHR.

**Lý do tồn tại:**
- HRMS hiện tại có nhiều module phân tán: tuyển dụng, hợp đồng, chấm công, lương, nghỉ phép, tác vụ (task), phân quyền. Người dùng mất nhiều thao tác để tìm thông tin hoặc thực hiện công việc.
- AI được thêm vào để giảm thời gian thao tác, hướng dẫn người dùng dùng chức năng, và trả lời nhanh các câu hỏi nghiệp vụ thay vì click qua nhiều màn hình.

**Tích hợp vào HRMS hiện tại:**
- AI không thay thế các controller, DAO hay service hiện có.
- AI gọi vào các service/DAO đã có thông qua một lớp dịch vụ AI (AI Service layer) trung gian.
- AI tuân thủ phân quyền theo Vai trò (Role) hiện tại: Guest, Employee, HR Staff, HR Manager, Dept Manager, Admin.
- AI không truy cập MySQL trực tiếp. Mọi truy vấn dữ liệu phải đi qua DAO.
- AI hiển thị kết quả bên trong giao diện BetterHR, không mở trang ngoài.

---

## 2. Tầm nhìn

**Mục tiêu dài hạn:**

BetterHR AI không chỉ là một chatbot trả lời câu hỏi. Nó là một trợ lý HR thông minh có khả năng:

- Hiểu người dùng đang cần gì dựa vào ngữ cảnh session (vai trò, trang hiện tại, lịch sử hành động).
- Tự động hóa các tác vụ nhân sự lặp đi lặp lại (tra cứu lương, xem lịch phỏng vấn, kiểm tra trạng thái hợp đồng).
- Gợi ý hành động tiếp theo phù hợp với vai trò và trạng thái nghiệp vụ.
- Trở thành điểm tiếp xúc trung tâm cho tất cả actor trong hệ thống.

**AI không thay thế con người:**
- Các quyết định nghiệp vụ quan trọng (duyệt lương, ký hợp đồng, tuyển dụng) vẫn do con người xác nhận.
- AI chỉ đề xuất, hiển thị, tổng kết và hướng dẫn; không tự ý thực hiện hành động có hậu quả lớn.

---

## 3. Mục tiêu

### Mục tiêu nghiệp vụ
- Giảm thời gian tìm kiếm thông tin nội bộ.
- Tăng tốc độ xử lý các task HR hằng ngày.
- Giảm tải cho HR Staff và HR Manager trên các câu hỏi lặp đi lặp lại.
- Hỗ trợ onboarding nhân viên mới bằng hướng dẫn tự động.

### Mục tiêu kỹ thuật
- AI Service layer không chứa business logic; mọi logic nằm trong service/DAO hiện có.
- AI phải tuân thủ toàn bộ security rule: session, role, permission.
- AI không tạo SQL, không gọi JDBC trực tiếp.
- Intent Router phải dễ dàng mở rộng để thêm intent mới mà không làm hỏng flow hiện có.
- Toàn bộ response của AI phải được ghi log để debug và audit.

### Mục tiêu trải nghiệm người dùng
- Trả lời bằng tiếng Việt, gọn gàng, đủ ý.
- Không để người dùng hỏi lại cùng một câu.
- Cung cấp hành động nhanh (quick action) thay vì chỉ trả lời văn bản.
- Có graceful fallback khi AI không hiểu: hướng dẫn người dùng click đúng nơi.

---

## 4. Phạm vi — Phiên bản 1 (Version 1)

### AI Version 1 có thể làm
- Trả lời các câu hỏi phổ biến liên quan đến: lương, nghỉ phép, hợp đồng, tuyển dụng, phòng ban, task, chấm công.
- Hiển thị thông tin từ hệ thống (đọc dữ liệu qua DAO hiện có).
- Gợi ý hành động tiếp theo (quick action) phù hợp với role và trạng thái.
- Hướng dẫn người dùng đến đúng route/màn hình cần thiết.
- Hiển thị dashboard summary đơn giản.
- Trả lời FAQ nội bộ về chính sách công ty.
- Nhận dạng role người dùng từ session và trả lời theo phạm vi phân quyền tương ứng.

### AI Version 1 KHÔNG làm
- Không tự động thực hiện hành động nghiệp vụ mà không có xác nhận rõ ràng của người dùng.
- Không tạo, sửa, xóa dữ liệu mà không qua form xác nhận hiện có.
- Không phân tích CV (dự kiến Version 2).
- Không tự động tạo báo cáo (dự kiến Version 2).
- Không hỗ trợ giọng nói (dự kiến Version 2).
- Không gửi email tự động (dự kiến Version 2).
- Không kết nối API ngoài (không gọi ChatGPT, OpenAI hay dịch vụ bên ngoài trong Version 1 trừ khi có quyết định riêng).
- Không lưu lịch sử hội thoại lâu dài qua các phiên đăng nhập khác nhau (context chỉ tồn tại trong session hiện tại).

---

## 5. Vai trò (Role) được hỗ trợ

### Guest (Ứng viên chưa có tài khoản / khách)
- Xem thông tin tuyển dụng công khai.
- Đặt câu hỏi về vị trí đang tuyển, quy trình ứng tuyển.
- Nhận hướng dẫn đăng ký tài khoản hoặc nộp đơn.
- AI không hiển thị dữ liệu nội bộ của BetterHR cho Guest.

### Employee (Nhân viên)
- Tra cứu thông tin lương, chấm công, nghỉ phép cá nhân.
- Xem trạng thái hợp đồng, task được giao.
- Hỏi về chính sách nội bộ (nghỉ phép, phúc lợi).
- Nhận thông báo và các cập nhật liên quan đến bản thân.

### HR Staff
- Tra cứu nhanh danh sách ứng viên theo trạng thái.
- Hỏi về trạng thái hợp đồng nhân viên.
- Kiểm tra nhanh trạng thái payroll.
- Nhận gợi ý khi còn thiếu dữ liệu khi tạo hợp đồng / tính lương.

### HR Manager
- Xem tổng quan dashboard nhân sự.
- Hỏi về các payroll/hợp đồng đang chờ duyệt.
- Nhận cảnh báo các mục có vấn đề.
- Tìm kiếm nhanh thông tin nhân viên/ứng viên.

### Department Manager (Dept Manager)
- Xem tổng quan task của phòng ban.
- Hỏi về trạng thái task, nhân viên trong phòng ban.
- Nhận nhắc nhở deadline task sắp đến.

### Admin
- Hỏi về trạng thái hệ thống, tài khoản, phân quyền.
- Xem tóm tắt số liệu toàn hệ thống.
- Tìm kiếm nhanh user, role, permission.
- AI cũng phải gợi ý đúng route Admin thay vì tự xử lý.

---

## 6. Các tính năng cốt lõi (Core Features)

### 6.1 AI Chat
Widget chat ẩn/hiện hiển thị trên giao diện BetterHR sau khi đăng nhập. Người dùng gõ câu hỏi bằng tiếng Việt, AI trả lời trong cùng khung chat. Có nút quick action để truyền lệnh nhanh mà không cần gõ tay.

### 6.2 AI Search
Thanh tìm kiếm thông minh. Người dùng gõ tên nhân viên, vị trí, số hợp đồng, AI trả về kết quả có liên quan thay vì danh sách phẳng. Kết quả có link trực tiếp đến màn hình chi tiết.

### 6.3 AI Assistant (Contextual)
Trợ lý hiểu người dùng đang ở màn hình nào (dựa vào URL hiện tại). Ví dụ: nếu người dùng đang ở màn hình tạo hợp đồng, AI có thể gợi ý các trường còn thiếu, hoặc giải thích các trường cần điền.

### 6.4 Quick Actions
Bộ nút hành động nhanh đề xuất bởi AI theo ngữ cảnh người dùng và role. Ví dụ với HR Staff: "Xem ứng viên mới", "Tính lương tháng này", "Tạo hợp đồng". Mỗi quick action hướng đến route hiện có, không xử lý logic bên trong AI.

### 6.5 Dashboard Summary
AI tổng hợp các chỉ số quan trọng từ database (qua DAO) và hiển thị tóm tắt trên dashboard. Ví dụ: số ứng viên chờ xử lý, số payroll chờ duyệt, số task quá hạn. Chỉ hiển thị số liệu phù hợp với role.

### 6.6 Navigation Assistant
Khi người dùng hỏi "Tôi muốn xem lương", AI không chỉ trả lời bằng văn bản mà còn hiển thị nút "Đi đến trang lương" để trỏ thẳng đến route đúng.

### 6.7 FAQ
Trả lời các câu hỏi phổ biến về chính sách công ty, hướng dẫn sử dụng hệ thống BetterHR. Dữ liệu FAQ có thể cấu hình từ file tĩnh hoặc database, không hardcode trong AI logic.

### 6.8 Context Memory (In-session)
AI ghi nhớ lịch sử hội thoại trong session hiện tại để trả lời mạch lạc. Ví dụ: nếu người dùng đã hỏi về nhân viên A, câu hỏi tiếp theo "họ làm ở phòng ban nào" AI hiểu là vẫn nói về nhân viên A. Context bị xóa khi session kết thúc.

### 6.9 Notification Assistant
AI có thể tóm tắt các thông báo chưa đọc cho người dùng. Chỉ đọc từ bảng `Notification` qua `NotificationDAO`, không tạo thông báo mới từ phía AI Version 1.

---

## 7. Kiến trúc tổng thể

```
Người dùng (Browser)
      |
      v
Khung chat (Chat Widget) / Thanh tìm kiếm AI (AI Search Bar) (JSP / JS)
      |
      v  [HTTP Request / AJAX]
ChatServlet (com.hrm.controller.ai.ChatServlet)
      |
      v
AI Service (com.hrm.service.ai.AIService)
      |
      v
Intent Router (com.hrm.service.ai.IntentRouter)
      |
      +----> PayrollService / PayrollDAO
      +----> LeaveService / LeaveDAO
      +----> RecruitmentService / GuestDAO
      +----> ContractDAO
      +----> NotificationDAO
      +----> EmployeeDAO
      +----> DepartmentDAO
      +----> TaskDAO
      +----> FAQ Handler (file tĩnh hoặc DB)
      |
      v
AI Response Builder (com.hrm.service.ai.AIResponseBuilder)
      |
      v  [JSON Response]
Chat Widget hiển thị kết quả
```

**Ghi chú kiến trúc:**
- `ChatServlet` là điểm vào duy nhất của AI. Nó kiểm tra session và role trước khi xử lý.
- `AIService` không gọi trực tiếp DAO. Nó gọi các Service hiện có; nếu Service chưa tồn tại thì gọi DAO.
- `IntentRouter` nhận đầu vào là intent đã được phân loại và role người dùng, trả về dữ liệu phù hợp.
- Mọi response của AI là JSON; Chat Widget tự render HTML phía browser.
- AI không bao giờ trả về raw data nhạy cảm (mật khẩu, token OAuth, secret key).

---

## 8. Thiết kế hội thoại (Conversation Design)

### 8.1 Chào hỏi (Greeting)
Khi người dùng mở chat lần đầu trong session, AI chủ động chào theo role.
Ví dụ:
- Employee: "Xin chào [Tên]! Tôi có thể giúp gì cho bạn hôm nay?"
- HR Staff: "Xin chào! Bạn có muốn xem ứng viên mới, kiểm tra lương hay có việc gì khác không?"
- Admin: "Xin chào Admin! Hệ thống hiện đang hoạt động bình thường. Cần tôi tra cứu gì không?"

### 8.2 Đặt câu hỏi tiếp theo (Follow-up)
Nếu câu hỏi của người dùng còn thiếu thông tin, AI hỏi lại một cách ngắn gọn.
Ví dụ: "Bạn muốn xem lương của tháng nào?"
Không hỏi quá 2 lần cho cùng một luồng thông tin còn thiếu.

### 8.3 Ngữ cảnh (Context)
AI ghi nhớ đối tượng đang được nói đến trong cuộc hội thoại hiện tại.
Nếu người dùng đã nói "nhân viên Nguyễn Văn A", các câu hỏi tiếp theo không cần nhắc lại tên.
Context được reset khi người dùng bắt đầu chủ đề mới rõ ràng.

### 8.4 Xác nhận hành động (Confirmation)
Với mỗi hành động có hậu quả nghiệp vụ (ví dụ: "Tạo hợp đồng cho nhân viên X"), AI không tự thực hiện.
AI hiển thị tóm tắt hành động và các nút: "Xác nhận" / "Hủy" hoặc hướng đến form hiện có.
Mục tiêu: người dùng luôn là người quyết định cuối cùng.

### 8.5 Kết thúc hội thoại (Ending)
Sau khi hoàn thành yêu cầu, AI hỏi thêm có cần gì nữa không.
Ví dụ: "Tôi đã lấy được thông tin bảng lương tháng 6. Bạn còn cần gì khác không?"
Nếu người dùng nói không hoặc đóng chat, AI không gửi thêm tin.

---

## 9. Phân loại Intent (Intent Categories)

### GREETING
Chào hỏi và bắt đầu cuộc hội thoại. AI phân loại bất kỳ câu hỏi mở đầu thành intent này.
Ví dụ: "Xin chào", "Hey", "Chào buổi sáng".

### PAYROLL
Các câu hỏi liên quan đến lương, phụ cấp, khấu trừ, tính lương, lịch sử lương.
Ví dụ: "Lương tháng này của tôi là bao nhiêu?", "Xem bảng lương tháng 5 của nhân viên A".

### ATTENDANCE
Các câu hỏi liên quan đến chấm công, giờ làm, vào/ra.
Ví dụ: "Tôi đã chấm công ngày hôm nay chưa?", "Tháng này tôi đi trễ mấy lần?".

### RECRUITMENT
Các câu hỏi liên quan đến tin tuyển dụng, số ứng viên, trạng thái tuyển dụng.
Ví dụ: "Hiện có bao nhiêu vị trí đang tuyển?", "Ứng viên nào đang ở trạng thái Reviewing?".

### LEAVE
Các câu hỏi liên quan đến xin nghỉ phép, số ngày phép còn lại, trạng thái đơn xin nghỉ.
Ví dụ: "Tôi còn bao nhiêu ngày phép?", "Đơn nghỉ phép của tôi được duyệt chưa?".

### NOTIFICATION
Hỏi về thông báo chưa đọc, yêu cầu AI tóm tắt thông báo.
Ví dụ: "Tôi có thông báo gì mới không?", "Tóm tắt tin nhắn chưa đọc cho tôi".

### CONTRACT
Các câu hỏi liên quan đến hợp đồng lao động, trạng thái hợp đồng, ngày hết hạn.
Ví dụ: "Hợp đồng của tôi còn hiệu lực đến khi nào?", "Hợp đồng nào đang chờ duyệt?".

### INTERVIEW
Các câu hỏi liên quan đến lịch phỏng vấn, kết quả phỏng vấn.
Ví dụ: "Ứng viên nào có lịch phỏng vấn tuần này?", "Kết quả phỏng vấn của ứng viên B thế nào?".

### EMPLOYEE
Các câu hỏi liên quan đến thông tin nhân viên: họ tên, phòng ban, vị trí, trạng thái.
Ví dụ: "Thông tin của nhân viên Nguyễn Văn A?", "Phòng ban Marketing hiện có bao nhiêu người?".

### DEPARTMENT
Các câu hỏi liên quan đến phòng ban, cấu trúc tổ chức, danh sách nhân viên theo phòng ban.
Ví dụ: "Phòng ban IT do ai quản lý?", "Danh sách phòng ban hiện có".

### TASK
Các câu hỏi liên quan đến task, tiến độ công việc, deadline, phân công.
Ví dụ: "Task nào của phòng ban tôi sắp hết hạn?", "Task X đang ở trạng thái gì?".

### NAVIGATION
Người dùng muốn đi đến một màn hình cụ thể nhưng không biết route.
Ví dụ: "Tôi muốn tạo hợp đồng mới", "Cho tôi xem danh sách nhân viên".
AI trả về quick action hoặc link trực tiếp đến route phù hợp.

### HELP
Người dùng cần hướng dẫn sử dụng hệ thống, không biết chức năng ở đâu.
Ví dụ: "Làm thế nào để chỉnh sửa thông tin cá nhân?", "Quy trình duyệt lương là gì?".

### UNKNOWN
Câu hỏi AI không thể phân loại hoặc nằm ngoài phạm vi Version 1.
AI báo người dùng rằng mình chưa hỗ trợ yêu cầu này và gợi ý họ liên hệ bộ phận hỗ trợ hoặc tìm trên menu hệ thống.

---

## 10. Nguyên tắc phát triển

- **Không bao giờ truy cập MySQL trực tiếp.** Mọi thao tác dữ liệu phải đi qua DAO tương ứng.
- **Không sinh SQL.** AI không tạo câu truy vấn SQL dưới bất kỳ hình thức nào.
- **Luôn dùng DAO.** AIService gọi DAO hoặc Service hiện có; không tạo DAO mới chỉ cho AI nếu đã có DAO cho module đó.
- **Luôn tôn trọng phân quyền.** IntentRouter phải kiểm tra role của người dùng trước khi trả về dữ liệu. Dữ liệu trả về phải phù hợp với phân quyền của role đó.
- **Luôn xác nhận trước khi thực hiện.** Với bất kỳ hành động có tác động đến database, AI phải hiển thị confirmation trước; không tự động execute.
- **Không bịa đặt dữ liệu.** Nếu DAO trả về null hoặc danh sách rỗng, AI thông báo không có dữ liệu, không tự sinh số liệu.
- **Business logic giữ nguyên trong module hiện có.** AI không chứa logic tính lương, tính ngày công, tính phép; nó chỉ gọi vào service đã có.
- **Mọi response là JSON.** ChatServlet luôn trả JSON, không trả HTML fragment từ servlet.
- **Log mọi request.** Mọi câu hỏi gửi đến ChatServlet và mọi intent được phân loại đều phải được ghi log để debug và audit.
- **Không lưu context lâu dài.** Context hội thoại chỉ sống trong session hiện tại. Không lưu vào database trong Version 1.
- **Dùng UTF-8.** Mọi response của AI đều phải được encode đúng UTF-8 để hiển thị tiếng Việt chính xác.

---

## 11. Mở rộng tương lai

### Voice Assistant
Hỗ trợ ra lệnh bằng giọng nói cho các actor chính (Employee, HR Staff). Cần tích hợp Web Speech API hoặc dịch vụ nhận dạng giọng nói bên thứ ba.

### CV Analysis
AI tự động phân tích CV ứng viên (PDF, DOCX) và trích xuất thông tin chính: kinh nghiệm, kỹ năng, học vấn. Kết quả dự kiến gắn vào profile ứng viên.

### AI Recommendation
AI đề xuất ứng viên phù hợp cho vị trí tuyển dụng dựa vào lịch sử công ty và yêu cầu công việc. AI đề xuất tăng/giam lương, phân công task dựa vào dữ liệu lịch sử.

### AI Report
AI tự tạo báo cáo nhân sự định kỳ (theo tháng, quý, năm) dưới dạng biểu đồ và văn bản tóm tắt. Xuất file PDF hoặc Excel.

### AI Email Assistant
AI soạn thảo email nhân sự mẫu: email mời ứng viên, email thông báo phỏng vấn, email xác nhận hợp đồng. Người dùng duyệt và gửi; AI không tự động gửi email.

### AI Interview Assistant
Hỗ trợ HR Staff/Manager trong quá trình phỏng vấn: đề xuất câu hỏi theo JD, ghi chú kết quả phỏng vấn bằng giọng nói, tóm tắt ý kiến sau phỏng vấn.

### Document Search
Tìm kiếm toàn văn trong các tài liệu nội bộ: nội quy, chính sách, biên bản họp. Kết quả trả về đoạn trích dẫn và link tài liệu gốc.

### OCR
Trích xuất văn bản từ hình ảnh: CMND/CCCD, bằng cấp, chứng chỉ, số hợp đồng scan. Dữ liệu sau OCR phải được người dùng xác nhận trước khi lưu vào database.

---

## Các phần việc còn thiếu
- [ ] Chưa có `ChatServlet`, `AIService`, `IntentRouter`, `AIResponseBuilder` trong codebase.
- [ ] Chưa có bảng database để lưu config FAQ và context (nếu Version 2 cần).
- [ ] Chưa định nghĩa JSON response schema cho AI.
- [ ] Chưa có Chat Widget (JSP + JS) trong giao diện BetterHR.
- [ ] Chưa có route `/ai/chat` trong `web.xml` hoặc filter bảo vệ route AI.
- [ ] Chưa cập nhật `permission-matrix.spec.md` với route AI.
- [ ] Chưa có spec chi tiết cho từng feature: AI_Chat_Spec, AI_Search_Spec, AI_Intent_Spec, AI_Prompt_Spec.

## Tiêu chí nghiệm thu
- [ ] AI chỉ trả lời trong phạm vi phân quyền của role đang đăng nhập.
- [ ] Mọi intent đều có graceful fallback nếu dữ liệu không tìm thấy.
- [ ] AI không tự thực hiện hành động nghiệp vụ mà không có xác nhận của người dùng.
- [ ] Toàn bộ response hiển thị đúng tiếng Việt, encode UTF-8.
- [ ] Không có truy cập MySQL trực tiếp từ bất kỳ class nào trong package `com.hrm.service.ai`.
- [ ] Log được ghi với mỗi request đến `ChatServlet`.
