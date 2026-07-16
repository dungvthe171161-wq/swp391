# Tính năng HR Staff: Quản lý offer

Trạng thái: Đã rà soát theo code ngày 2026-07-14.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- HR Staff tạo draft, cập nhật và gửi offer cho ứng viên đã vượt qua các bước tuyển dụng cần thiết.

## Route, controller và JSP liên quan
- `GET /hrstaff/offers/manage`, `POST /hrstaff/offers/manage`.
- Controller: `OfferManagementController`; DAO: `OfferDAO`, `ApplicationDAO` và DAO liên quan.
- JSP: `Views/HrStaff/ManageOffer.jsp`; phía Guest phản hồi offer trong Guest Portal.

## Hiện trạng code
- Controller hỗ trợ lưu draft và gửi offer.
- Trạng thái Offer gồm `Draft`, `Sent`, `Accepted`, `Rejected`, `Expired`, `Cancelled`.
- Khi gửi, code cập nhật Application sang `Status = Offered`, `CurrentStep = Offer`.
- `OfferDAO.respondOffer` hiện có hành vi chuyển application sang `Hired` ngay khi ứng viên accept.

## Quy tắc nghiệp vụ chuẩn
- Chỉ application hợp lệ mới được tạo offer; phải chốt một offer hiệu lực cho mỗi application tại một thời điểm.
- Draft phải validate dữ liệu tối thiểu trước khi gửi: chức danh, lương, ngày bắt đầu, hạn phản hồi và điều khoản.
- Offer đã `Accepted`, `Rejected`, `Expired` hoặc `Cancelled` không được sửa như draft.
- Gửi offer, đổi trạng thái Application và tạo notification phải nhất quán trong transaction/outbox.
- Hạn phản hồi phải ở tương lai; việc hết hạn phải có cơ chế xác định rõ và idempotent.
- Accept offer không mặc nhiên đồng nghĩa đã hoàn tất onboarding nếu nghiệp vụ còn bước ký hợp đồng.

## Code còn lệch spec hoặc cần bổ sung
- Quyền quản lý offer đang dùng `VIEW_RECRUITMENT`, chưa có `MANAGE_OFFER`.
- Cần chốt việc `Accepted -> Hired` hay chuyển sang bước Contract/Onboarding.
- Chưa thấy quy tắc đầy đủ cho nhiều offer, expiration job, CSRF và audit.
- Cần tránh gửi email/notification trùng khi client submit lại.

## Kiểm thử tối thiểu
- Không tạo/gửi offer cho application sai trạng thái hoặc thiếu trường bắt buộc.
- Không sửa offer đã kết thúc và không tồn tại hai offer `Sent` cho cùng application.
- Gửi lại cùng request không tạo bản ghi/email trùng.
- Kiểm tra đồng bộ chính xác Offer, Application, notification và audit.
