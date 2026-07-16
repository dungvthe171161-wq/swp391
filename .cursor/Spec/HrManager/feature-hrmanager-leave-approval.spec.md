# Tính năng HR Manager: Quản lý đơn nghỉ phép

Trạng thái: Đã rà soát theo code ngày 2026-07-14.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- HR Manager xem và xử lý đơn nghỉ phép theo phạm vi toàn công ty hoặc theo cơ chế escalation được phê duyệt.
- Ranh giới trách nhiệm với Dept Manager phải được chốt trước khi mở quyền ghi.

## Route, controller và JSP liên quan
- `GET /hr/leaves`, `POST /hr/leaves`.
- Controller: `LeaveApprovalController`; DAO: `MailRequestDAO`.
- JSP: `Views/hr/LeaveRequests.jsp`.
- Luồng liên quan: `/dept/leaves` và `/employee/leaves` trong Employee Portal.

## Hiện trạng code
- HR Manager có controller và màn hình riêng để xem/xử lý leave request.
- Trạng thái dùng chung gồm `Pending`, `Approved`, `Rejected`.
- Code chưa thể hiện rõ mô hình duyệt một cấp, hai cấp hay HR chỉ xử lý escalation.

## Quy tắc nghiệp vụ chuẩn
- Phải xác định rõ phạm vi dữ liệu HR Manager được xem và loại đơn được quyền quyết định.
- Nếu duyệt hai cấp, trạng thái và người duyệt ở từng cấp phải được lưu riêng; không ghi đè lịch sử Dept Manager.
- Không cho người dùng tự duyệt đơn của mình.
- Phải kiểm tra số dư phép, ngày trùng và chính sách loại nghỉ trước khi phê duyệt cuối.
- Quyết định cuối phải atomic với cập nhật số dư phép, notification và audit.
- POST phải có CSRF token và chống xử lý lặp.

## Code còn lệch spec hoặc cần bổ sung
- Chưa có permission riêng và chưa chốt ranh giới Dept Manager/HR Manager.
- Chưa thấy kiểm tra trạng thái cũ trong thao tác update, audit đầy đủ hoặc lý do từ chối bắt buộc.
- Cần xác nhận nguồn dữ liệu `MailRequest` có phải source of truth lâu dài cho leave hay không.
- Cần quy tắc transaction khi cập nhật số dư phép.

## Kiểm thử tối thiểu
- HR Manager chỉ xem/xử lý đúng phạm vi và không tự duyệt đơn của mình.
- Kiểm tra workflow một cấp/hai cấp theo quyết định nghiệp vụ cuối cùng.
- Hai người duyệt đồng thời chỉ có một quyết định hợp lệ.
- Số dư phép, trạng thái, notification và audit phải nhất quán khi có lỗi giữa chừng.
