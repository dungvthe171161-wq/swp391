# Đặc tả dùng chung: Trạng thái nghiệp vụ

Trạng thái: Đã rà soát theo code ngày 2026-07-13.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Tất cả actor có thao tác làm thay đổi trạng thái tuyển dụng, hợp đồng, payroll, nghỉ phép và task.

## Route, controller và JSP liên quan
- `src/data/data.sql`: định nghĩa enum chính.
- `ApplicationDAO`, `InterviewDAO`, `OfferDAO`: đọc và ghi trạng thái tuyển dụng.
- Các controller HR Staff, HR Manager, Guest và Employee sử dụng trạng thái để hiển thị hành động.

## Hiện trạng code
- `Application.Status`: `Applied`, `Screening`, `Interview`, `Offered`, `Rejected`, `Withdrawn`, `Hired`.
- `Application.CurrentStep`: `Applied`, `Screening`, `Interview`, `Offer`, `Hired`, `Rejected`, `Withdrawn`.
- `Interview.Status`: `Scheduled`, `Completed`, `Cancelled`, `NoShow`, `Rescheduled`; `Interview.Result`: `Pending`, `Passed`, `Failed`.
- `Offer.Status`: `Draft`, `Sent`, `Accepted`, `Rejected`, `Expired`, `Cancelled`.
- Task dùng `Waiting`, `In Progress`, `Completed`, `Rejected`; nghỉ phép dùng `Pending`, `Approved`, `Rejected`.

## Quy tắc nghiệp vụ chuẩn
- Spec phải dùng đúng enum hiện có nếu chưa có migration.
- Nếu muốn trạng thái chi tiết hơn, phải sửa database, DAO, controller, JSP và test cùng lúc.
- Khi gửi offer: `Application.Status = Offered`, `Application.CurrentStep = Offer`.
- Không cập nhật `Guest.Status` thay cho `Application.Status` trong workflow ứng tuyển mới.

## Code còn lệch spec hoặc cần bổ sung
- Spec cũ có `InterviewScheduled`, `OfferAccepted`, `OfferDeclined`; code hiện chưa hỗ trợ các enum đó.
- `OfferDAO.respondOffer` hiện chuyển application sang `Hired` ngay khi ứng viên accept offer.
- `ViewCV` fallback POST legacy vẫn cập nhật `Guest.Status`, dễ lệch với `Application.Status`.
- Chưa có workflow service transaction trung tâm cho chuyển trạng thái tuyển dụng.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

