# Đặc tả dữ liệu Guest giai đoạn 2: Application, Interview và Offer

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Dữ liệu tuyển dụng chuẩn cho Guest có nhiều lần ứng tuyển.

## Route, controller và JSP liên quan
- `/guest`, `/guest/dashboard`, `/guest/applications`, `/guest/profile`, `/guest/notification/read`, `/guest/offer/respond`.
- `/RecruitmentController` cho trang tuyển dụng và nộp hồ sơ.
- Controller: `GuestPortalController`, `RecruitmentController`; JSP: `Views/Guest/*`.

## Hiện trạng code
- Database đã có `CandidateProfile`, `Application`, `Interview`, `Offer`.
- `Application.Status` hiện không có `OfferAccepted` hoặc `OfferDeclined`.
- `Offer` hiện có unique trên `ApplicationID`, nên mỗi application chỉ có một offer theo schema hiện tại.

## Quy tắc nghiệp vụ chuẩn
- `Application` là nguồn sự thật cho từng lần ứng tuyển.
- Interview và Offer phải tham chiếu `ApplicationID`.
- Nếu cần nhiều offer cho một application, phải bỏ unique và bổ sung rule active offer.

## Code còn lệch spec hoặc cần bổ sung
- Spec cũ nói nhiều offer và trạng thái chi tiết nhưng code/schema chưa hỗ trợ.
- Cần migration nếu muốn workflow phase 2 đầy đủ.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

