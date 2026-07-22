# Đặc Tả Tính Năng: Nhân Viên Xem & Cập Nhật Tiến Độ Công Việc

- **Mã Tính Năng**: `feature-employee-view-task`
- **Cấp Độ Đặc Tả**: Standard Spec (Theo Mục 16.3)
- **Trạng Thái**: Approved
- **Tác Giả**: Đội Ngũ Lập Trình SE | **Người Kiểm Duyệt**: Trưởng Nhóm Chất Lượng Code
- **Mã Nguồn Áp Dụng**: `com.hrm.controller.employee.ViewTask`, `com.hrm.dao.TaskDAO`
- **Quy Tắc Hệ Thống**: AGENTS.md, Ràng buộc quyền sở hữu (Ownership Guard)

---

## 1. Bối Cảnh Nghiệp Vụ (Business Context)
Nhân viên cần một cổng thông tin tập trung để xem danh sách công việc được phân công, cập nhật tiến độ từ khi bắt đầu cho đến khi hoàn thành hoặc báo cáo vướng mắc.

---

## 2. Kịch Bản Sử Dụng (User Stories)
* **Kịch Bản 1 (Happy Path - Cập Nhật Trạng Thái)**:
  * *Với tư cách là* Nhân viên (Employee), *tôi muốn* xem chi tiết công việc được giao và chuyển trạng thái từ `Waiting` sang `In Progress` khi bắt đầu thực hiện.
* **Kịch Bản 2 (Happy Path - Nộp Báo Cáo Hoàn Thành)**:
  * *Với tư cách là* Nhân viên, *tôi muốn* nộp báo cáo hoàn thành công việc kèm ghi chú và đường dẫn bằng chứng (evidence) để Trưởng phòng đánh giá.
* **Kịch Bản 3 (Bảo Mật - Ngăn Chặn Xem Trộm)**:
  * *Với tư cách là* Nhân viên, *tôi muốn* hệ thống ngăn chặn nhân viên khác xem hoặc thay đổi thông tin công việc của tôi thông qua việc thay đổi ID trên URL.

---

## 3. Tiêu Chí Nghiệm Thu (Acceptance Criteria - Cú Pháp EARS)
- `KHI` Nhân viên truy cập đường dẫn `/employee/tasks/detail?id={taskId}`, `HỆ THỐNG PHẢI` xác minh điều kiện bảo mật: Mã `employee_id` của nhân viên đăng nhập phải khớp với bản ghi trong `assign_list`.
- `KHI` phát hiện mã nhân viên đăng nhập không khớp với `employee_id` được giao trong `assign_list`, `HỆ THỐNG PHẢI` từ chối truy cập và trả về mã lỗi HTTP 403 Forbidden.
- `KHI` Nhân viên chuyển trạng thái từ `Waiting` sang `In Progress`, `HỆ THỐNG PHẢI` cập nhật cột `status` trong `assign_list` và ghi nhật ký thay đổi vào bảng `SystemLog`.
- `KHI` Nhân viên gửi báo cáo hoàn thành công việc (`status = 'Completed'`), `HỆ THỐNG PHẢI` kiểm tra và yêu cầu ghi chú hoàn thành (`completion_notes`) không được để trống, đồng thời gửi thông báo hệ thống đến Trưởng phòng quản lý phòng ban.

---

## 4. Đặc Tả Giao Tiếp (API Contract)
* **Endpoint**: `POST /employee/tasks/update`
* **Request Payload**:
```json
{
  "taskId": "Integer (Bắt buộc)",
  "status": "String (Bắt buộc, CHECK IN ['In Progress', 'Completed'])",
  "notes": "String (Bắt buộc khi trạng thái là Completed)",
  "evidenceUrl": "String (Tùy chọn, đường dẫn chứng minh công việc)"
}
```
* **Phản hồi Kỳ Vọng**:
  - `HTTP 200 OK` JSON `{ "success": true, "message": "MSG-TASK-06" }` hoặc `HTTP 302` Redirect về trang danh sách task cá nhân.
  - `HTTP 403` (Forbidden nếu vi phạm quyền sở hữu).
  - `HTTP 400` (Bad Request nếu thiếu thông tin ghi chú khi nộp hoàn thành).

---

## 5. Ràng Buộc Kỹ Thuật (Technical Constraints)
* **Ownership Guard**: Kiểm tra quyền truy cập chặt chẽ tại tầng Controller (Servlet) trước khi thực hiện bất kỳ câu truy vấn UPDATE nào xuống database.

---

## 6. Ngoài Phạm Vi (Out of Scope)
* Việc tự động lưu bản nháp ghi chú hoàn thành (auto-save draft) khi đang viết báo cáo chưa được hỗ trợ.
