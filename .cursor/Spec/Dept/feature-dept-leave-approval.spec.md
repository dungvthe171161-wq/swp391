# Tính năng Dept: Duyệt đơn nghỉ phép

Trạng thái: Đã rà soát theo code ngày 2026-07-14.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Dept Manager xem và xử lý đơn nghỉ phép của nhân viên thuộc đúng phòng ban mình quản lý.

## Route, controller và JSP liên quan
- `GET /dept/leaves`, `POST /dept/leaves`.
- Controller: `DeptLeaveController`; scope: `DeptManagerScope`.
- DAO: `MailRequestDAO`, `EmployeeDAO`; service: `NotificationService`, `NotificationRecipientService`.
- JSP: `Views/DeptManager/leaves.jsp`.

## Hiện trạng code
- GET mặc định lọc trạng thái `Pending`, đồng thời hỗ trợ lọc theo tham số `status`.
- POST nhận `requestId` và `decision`; chỉ chấp nhận `Approved` hoặc `Rejected`.
- `updateLeaveStatusByDepartment` giới hạn cập nhật theo `DepartmentID` và ghi người duyệt.
- Khi xử lý thành công, hệ thống gửi notification cho tài khoản đang hoạt động của nhân viên.

## Quy tắc nghiệp vụ chuẩn
- Manager chỉ được xem và xử lý đơn của nhân viên trong phòng ban của mình.
- Chỉ đơn `Pending` mới được chuyển sang `Approved` hoặc `Rejected`; quyết định đã kết thúc phải bất biến nếu không có luồng mở lại được phê duyệt.
- Không cho manager duyệt đơn của chính mình nếu chưa có chính sách thay thế người duyệt.
- Quyết định phải ghi người duyệt, thời điểm, trạng thái cũ/mới và gửi thông báo cho nhân viên.
- POST phải dùng CSRF token và áp dụng Post/Redirect/Get.

## Code còn lệch spec hoặc cần bổ sung
- Chưa thấy CSRF token và permission riêng như `APPROVE_DEPARTMENT_LEAVE`.
- Cần xác nhận DAO chỉ cập nhật từ `Pending` để chống duyệt lặp hoặc tranh chấp đồng thời.
- Chưa có audit log bắt buộc và lý do từ chối.
- Tham số `status` cần allowlist thay vì nhận giá trị tùy ý.

## Kiểm thử tối thiểu
- Dept Manager xem được đơn đúng phòng ban và không xem/duyệt được đơn phòng ban khác.
- Chỉ `Pending -> Approved/Rejected` thành công; submit lặp không tạo thêm tác dụng phụ.
- Thiếu/sai CSRF, thiếu permission hoặc manager chưa gắn phòng ban phải bị chặn.
- Notification chỉ gửi cho đúng nhân viên sau khi transaction cập nhật thành công.
