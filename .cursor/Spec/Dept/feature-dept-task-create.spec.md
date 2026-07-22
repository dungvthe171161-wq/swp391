# Đặc Tả Tính Năng: Trưởng Phòng Tạo & Phân Công Công Việc

- **Mã Tính Năng**: `feature-dept-task-create`
- **Cấp Độ Đặc Tả**: Standard Spec (Theo Mục 16.3)
- **Trạng Thái**: Approved
- **Tác Giả**: Đội Ngũ Lập Trình SE | **Người Kiểm Duyệt**: Trưởng Nhóm Chất Lượng Code
- **Mã Nguồn Áp Dụng**: `com.hrm.controller.dept.PostTask`, `com.hrm.dao.TaskDAO`
- **Quy Tắc Hệ Thống**: AGENTS.md, BR-1896 (Department Scope Control)

---

## 1. Bối Cảnh Nghiệp Vụ (Business Context)
Trưởng phòng cần giao các hạng mục công việc cụ thể cho nhân viên trong phòng ban của mình để quản lý tiến độ tập trung, tránh việc giao việc qua lời nói hoặc chat thủ công dễ gây thất lạc thông tin.

---

## 2. Kịch Bản Sử Dụng (User Stories)
* **Kịch Bản 1 (Happy Path - Trưởng Phòng Giao Việc)**:
  * *Với tư cách là* Trưởng phòng (Department Manager), *tôi muốn* nhập thông tin công việc (tiêu đề, mô tả, độ ưu tiên, ngày bắt đầu, ngày hết hạn) và chọn nhân viên trong phòng ban để phân công công việc.

---

## 3. Tiêu Chí Nghiệm Thu (Acceptance Criteria - Cú Pháp EARS)
- `KHI` Trưởng phòng gửi form tạo công việc hợp lệ tại đường dẫn `/dept/tasks/postTask` (hoặc servlet mapping tương đương), `HỆ THỐNG PHẢI` kiểm tra quyền hạn `VIEW_DEPARTMENTS` và `MANAGE_TASKS`.
- `KHI` thông tin hợp lệ và nhân viên được chọn thuộc cùng phòng ban quản lý (`employee.department_id == manager.department_id`), `HỆ THỐNG PHẢI` tạo 1 bản ghi mới trong bảng `tasks` (với `assigned_by` là ID của Trưởng phòng) đồng thời tạo các dòng tương ứng cho từng nhân viên trong bảng `assign_list` với trạng thái mặc định là `Waiting`.
- `KHI` Trưởng phòng giao việc cho nhân viên ngoài phòng ban mình quản lý, `HỆ THỐNG PHẢI` từ chối lưu dữ liệu, rollback transaction và trả về lỗi HTTP 400 Bad Request kèm mã thông báo lỗi `MSG-TASK-ERR-02`.
- `KHI` ngày hết hạn (`due_date`) được thiết lập nhỏ hơn hoặc bằng ngày bắt đầu (`start_date`), `HỆ THỐNG PHẢI` trả về lỗi và hiển thị thông báo "Ngày hết hạn phải sau ngày bắt đầu".

---

## 4. Đặc Tả Giao Tiếp (API Contract)
* **Endpoint**: `POST /dept/tasks/postTask`
* **Request Payload**:
```json
{
  "title": "String (Bắt buộc, tối đa 255 ký tự)",
  "description": "String (Tùy chọn)",
  "priority": "String (CHECK IN ['LOW', 'MEDIUM', 'HIGH', 'URGENT'])",
  "startDate": "Timestamp (YYYY-MM-DD HH:mm:ss, Bắt buộc)",
  "dueDate": "Timestamp (YYYY-MM-DD HH:mm:ss, Bắt buộc)",
  "assignees": "Array of Integers (Danh sách Employee ID, Bắt buộc)"
}
```
* **Phản hồi Kỳ Vọng**:
  - `HTTP 302` (Redirect về `/dept/tasks` kèm message thành công `MSG-TASK-01` trong session).
  - `HTTP 400` (Bad Request với JSON hoặc thông báo lỗi nếu validation thất bại).

---

## 5. Ràng Buộc Kỹ Thuật (Technical Constraints)
* **Transaction Isolation**: Quá trình chèn dữ liệu vào bảng `tasks` và chèn hàng loạt vào bảng `assign_list` phải được thực hiện trong cùng một Database Transaction nhằm đảm bảo tính toàn vẹn dữ liệu.
* **Audit Log**: Ghi nhận một bản ghi log nghiệp vụ vào bảng `SystemLog` ngay sau khi lưu task thành công.

---

## 6. Ngoài Phạm Vi (Out of Scope)
* Chưa hỗ trợ đính kèm tập tin tài liệu (file attachment) lúc tạo công việc (sẽ thực hiện ở Sprint tiếp theo).
