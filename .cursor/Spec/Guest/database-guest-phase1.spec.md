# Đặc tả dữ liệu Guest giai đoạn 1

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Dữ liệu hồ sơ ứng viên ban đầu trước khi workflow Application đầy đủ ổn định.

## Route, controller và JSP liên quan
- `/guest`, `/guest/dashboard`, `/guest/applications`, `/guest/profile`, `/guest/notification/read`, `/guest/offer/respond`.
- `/RecruitmentController` cho trang tuyển dụng và nộp hồ sơ.
- Controller: `GuestPortalController`, `RecruitmentController`; JSP: `Views/Guest/*`.

## Hiện trạng code
- `Guest` lưu hồ sơ cơ bản và trạng thái legacy.
- `RecruitmentController` vẫn có luồng ứng tuyển public/guest.
- Một số code cũ còn dựa vào `Guest.Status`.

## Quy tắc nghiệp vụ chuẩn
- Giai đoạn 1 chỉ dùng `Guest` cho hồ sơ cá nhân, không thay thế workflow application mới.
- Không xóa dữ liệu Guest khi còn application liên quan.
- Email và user liên kết phải tránh tạo hồ sơ trùng.

## Code còn lệch spec hoặc cần bổ sung
- Cần giảm phụ thuộc vào `Guest.Status` cho tuyển dụng.
- Cần migration làm sạch Guest trùng nếu có.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

