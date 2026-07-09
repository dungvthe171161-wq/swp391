# Tính năng Guest: Xem hồ sơ đã ứng tuyển

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Guest xem danh sách application của chính mình.

## Route, controller và JSP liên quan
- `/guest`, `/guest/dashboard`, `/guest/applications`, `/guest/profile`, `/guest/notification/read`, `/guest/offer/respond`.
- `/RecruitmentController` cho trang tuyển dụng và nộp hồ sơ.
- Controller: `GuestPortalController`, `RecruitmentController`; JSP: `Views/Guest/*`.

## Hiện trạng code
- `/guest/applications` dùng `ApplicationDAO.findByUserId`.
- Danh sách có interview sắp tới và offer pending từ DAO liên quan.
- Offer response thực hiện POST `/guest/offer/respond`.

## Quy tắc nghiệp vụ chuẩn
- Chỉ xem application theo user hiện tại.
- Status hiển thị phải dùng enum database hiện có.
- Action offer chỉ hiện khi offer thuộc user hiện tại và còn hợp lệ.

## Code còn lệch spec hoặc cần bổ sung
- Cần test nhiều application cùng Guest.
- Cần xử lý trạng thái offer accepted không nhảy thẳng Hired nếu áp dụng thiết kế mới.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

