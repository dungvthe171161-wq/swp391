# Tính năng legacy: Ứng viên public nộp hồ sơ

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Ứng viên public gửi hồ sơ ứng tuyển từ trang tuyển dụng.

## Route, controller và JSP liên quan
- `/homepage`, `/RecruitmentController` và các route public nộp hồ sơ nếu có.
- Spec này là legacy; spec chính cho ứng viên có tài khoản nằm trong thư mục `Guest`.
- JSP public và trang thành công ứng tuyển.

## Hiện trạng code
- PublicCandidate là tên spec cũ cho ứng viên public.
- Code hiện đã có actor Guest rõ hơn với role `Guest` và portal `/guest/*`.
- Luồng public vẫn có thể dùng `RecruitmentController`.

## Quy tắc nghiệp vụ chuẩn
- Ứng viên public được xem job đang mở.
- Nếu nộp hồ sơ cần tạo hoặc liên kết Guest/Application theo thiết kế mới.
- Không dùng spec PublicCandidate làm nguồn chính nếu mâu thuẫn với Guest.

## Code còn lệch spec hoặc cần bổ sung
- Cần hợp nhất dần spec PublicCandidate vào Guest.
- Cần xác định route public nào còn được dùng thật.
- Cần đảm bảo không tạo dữ liệu ứng viên trùng.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

