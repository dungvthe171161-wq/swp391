# Đặc Tả Tính Năng: Tự Động Gửi Email & Thông Báo Nhắc Nhở Hạn Chót

- **Mã Tính Năng**: `feature-task-deadline-reminder`
- **Cấp Độ Đặc Tả**: Standard Spec (Theo Mục 16.3)
- **Trạng Thái**: Approved
- **Tác Giả**: Đội Ngũ Lập Trình SE | **Người Kiểm Duyệt**: Trưởng Nhóm Chất Lượng Code
- **Mã Nguồn Áp Dụng**: `com.hrm.listener.TaskDeadlineReminderListener`
- **Quy Tắc Hệ Thống**: AGENTS.md, Tự động hóa chạy ngầm (Scheduled Service)

---

## 1. Bối Cảnh Nghiệp Vụ (Business Context)
Nhân viên thường xử lý nhiều công việc cùng lúc dẫn đến nguy cơ quên hoặc trễ hạn chót (due date). Hệ thống cần tự động phát hiện và gửi cảnh báo nhắc nhở trước khi hết hạn để đảm bảo tiến độ công việc chung.

---

## 2. Kịch Bản Sử Dụng (User Stories)
* **Kịch Bản 1 (Chạy Ngầm Định Kỳ)**:
  * *Với tư cách là* Nhân viên có công việc sắp hết hạn trong vòng 12 giờ tới, *tôi muốn* nhận được email và thông báo trên hệ thống *để* tôi tập trung hoàn thành công việc đúng giờ.

---

## 3. Tiêu Chí Nghiệm Thu (Acceptance Criteria - Cú Pháp EARS)
- `TRONG KHI` bộ quét chạy ngầm `TaskDeadlineReminderListener` hoạt động, `HỆ THỐNG PHẢI` thực thi quét định kỳ 15 phút một lần để quét cơ sở dữ liệu.
- `KHI` phát hiện bản ghi phân công trong `assign_list` thỏa mãn đồng thời các điều kiện:
  1. Hạn chót công việc sắp tới trong vòng 12 giờ (`due_date - NOW() <= INTERVAL '12 hours'`).
  2. Trạng thái công việc chưa hoàn thành (`status NOT IN ('Completed', 'Approved')`).
  3. Chưa gửi nhắc nhở trước đó (`reminder_sent_at IS NULL`).
  `HỆ THỐNG PHẢI` kích hoạt gửi thư nhắc nhở qua cấu hình JavaMail, đồng thời tạo thông báo đẩy trên hệ thống cho nhân viên tiếp nhận công việc.
- `KHI` quá trình gửi mail nhắc nhở thành công, `HỆ THỐNG PHẢI` cập nhật cột `reminder_sent_at = NOW()` để tránh việc gửi nhắc nhở trùng lặp ở chu kỳ tiếp theo.

---

## 4. Đặc Tả Giao Tiếp (API Contract)
* **Cơ Chế Kích Hoạt**: Tự động chạy ngầm, không qua Endpoint HTTP trực tiếp từ Client.
* **Cấu Hình Mail Server**: Đọc thông số máy chủ gửi thư từ cấu hình file `mail.properties`.

---

## 5. Ràng Buộc Kỹ Thuật (Technical Constraints)
* **Performance**: Bộ quét chạy ngầm phải thực thi trong một luồng riêng (worker thread) sử dụng `ScheduledExecutorService` để tránh làm nghẽn luồng xử lý Web Servlet chính.
* **Bảo Mật**: Tuyệt đối không lưu trữ hay ghi vết mật khẩu tài khoản gửi mail thô vào file cấu hình commit lên Git.

---

## 6. Ngoài Phạm Vi (Out of Scope)
* Chưa hỗ trợ tùy chỉnh cấu hình khoảng thời gian nhắc nhở (ví dụ: nhắc trước 24h hoặc 48h theo ý muốn của nhân viên). Mặc định hệ thống luôn là nhắc nhở trước 12 giờ.
