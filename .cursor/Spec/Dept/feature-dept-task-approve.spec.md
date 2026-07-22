# Đặc Tả Tính Năng: Trưởng Phòng Phê Duyệt & Từ Chối Hoàn Thành Công Việc

- **Mã Tính Năng**: `feature-dept-task-approve`
- **Cấp Độ Đặc Tả**: Standard Spec (Theo Mục 16.3)
- **Trạng Thất**: Approved
- **Tác Giả**: Đội Ngũ Lập Trình SE | **Người Kiểm Duyệt**: Trưởng Nhóm Chất Lượng Code
- **Mã Nguồn Áp Dụng**: `com.hrm.controller.dept.ViewTask` (API approve/reject), `com.hrm.dao.TaskDAO`
- **Quy Tắc Hệ Thống**: AGENTS.md, BR-1900 (Self-Approval Prevention Rule)

---

## 1. Bối Cảnh Nghiệp Vụ (Business Context)
Sau khi nhân viên nộp báo cáo hoàn thành công việc, Trưởng phòng cần kiểm tra, đánh giá chất lượng kết quả và ra quyết định phê duyệt hoặc yêu cầu chỉnh sửa lại (từ chối) để kiểm soát chất lượng công việc phòng ban.

---

## 2. Kịch Bản Sử Dụng (User Stories)
* **Kịch Bản 1 (Happy Path - Phê Duyệt)**:
  * *Với tư cách là* Trưởng phòng, *tôi muốn* xác nhận duyệt một công việc đã hoàn thành của nhân viên *để* công việc được đóng lại thành công.
* **Kịch Bản 2 (Happy Path - Từ Chối Báo Cáo)**:
  * *Với tư cách là* Trưởng phòng, khi thấy kết quả công việc chưa đạt yêu cầu, *tôi muốn* từ chối báo cáo hoàn thành và nhập lý do yêu cầu chỉnh sửa *để* nhân viên nắm được lý do và sửa lại.
* **Kịch Bản 3 (Ràng Buộc Bảo Mật - Chống Tự Phê Duyệt)**:
  * *Với tư cách là* Nhân viên kiêm Trưởng phòng, *tôi muốn* hệ thống ngăn chặn hành vi tôi tự phê duyệt hoặc tự từ chối báo cáo công việc do chính tôi thực hiện để đảm bảo tính khách quan.

---

## 3. Tiêu Chí Nghiệm Thu (Acceptance Criteria - Cú Pháp EARS)
- `KHI` Trưởng phòng chọn phê duyệt công việc tại đường dẫn `/dept/tasks/detail`, `HỆ THỐNG PHẢI` cập nhật trạng thái trong `assign_list` thành `Approved`, đồng thời ghi nhận mã người duyệt (`reviewed_by = manager_id`) và thời gian duyệt (`reviewed_at = NOW()`).
- `KHI` Trưởng phòng chọn từ chối công việc, `HỆ THỐNG PHẢI` yêu cầu nhập thông tin lý do từ chối (`rejection_reason`) không được để trống, cập nhật trạng thái thành `Rejected`, đồng thời gửi thông báo kèm lý do cho nhân viên tiếp nhận.
- `TRONG KHI` Trưởng phòng gửi yêu cầu duyệt/từ chối, `HỆ THỐNG PHẢI` thực thi Quy tắc chống tự phê duyệt (BR-1900): NẾU `manager.employee_id == assignee.employee_id`, `HỆ THỐNG PHẢI` từ chối thực thi và trả về lỗi HTTP 403 Forbidden.

---

## 4. Đặc Tả Giao Tiếp (API Contract)
* **Endpoint Phê Duyệt**: `POST /dept/tasks/approve`
  * Payload: `{ "taskId": Integer, "assigneeId": Integer }`
  * Phản hồi: `HTTP 200 OK` JSON `{ "success": true, "status": "Approved" }`
* **Endpoint Từ Chối**: `POST /dept/tasks/reject`
  * Payload: `{ "taskId": Integer, "assigneeId": Integer, "rejectionReason": "String (Bắt buộc)" }`
  * Phản hồi: `HTTP 200 OK` JSON `{ "success": true, "status": "Rejected" }`

---

## 5. Ràng Buộc Kỹ Thuật (Technical Constraints)
* **Quy Tắc BR-1900**: Logic kiểm tra tự phê duyệt phải được thiết lập cứng tại tầng Service/Controller trước khi gọi truy vấn xuống DAO.

---

## 6. Ngoài Phạm Vi (Out of Scope)
* Chưa hỗ trợ đánh giá bằng thang điểm số (ví dụ: chấm điểm 8/10 hoặc 9/10 cho task). Chức năng này sẽ được triển khai ở module Đánh Giá Hiệu Suất (Performance Appraisal).
