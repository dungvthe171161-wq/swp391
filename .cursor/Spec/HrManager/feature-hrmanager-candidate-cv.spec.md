# Tính năng HR Manager: Xem CV ứng viên

Trạng thái: Đã rà soát theo code ngày 2026-07-13.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- HR Manager xem hồ sơ và CV ứng viên để review tuyển dụng.

## Route, controller và JSP liên quan
- `/viewCV`, controller `ViewCV`.
- JSP liên quan: `Views/hr/ViewCV.jsp` hoặc trang CV tương ứng.

## Hiện trạng code
- `ViewCV` mapping `/viewCV` và dùng `VIEW_RECRUITMENT`.
- GET ưu tiên `applicationId`; fallback `guestId` legacy nếu không có `applicationId`.
- POST ưu tiên pplicationId để cập nhật Application; fallback legacy vẫn cập nhật Guest.Status.
- Views/hr/ViewCV.jsp mở file qua /Upload/cvs/{fileName}; CvFileServlet đọc file từ thư mục do UploadPathUtil xác định.
- Upload và phục vụ file tuân theo _Common/upload-cv.spec.md.

## Quy tắc nghiệp vụ chuẩn
- CV phải thuộc đúng application đang review.
- Trạng thái tuyển dụng phải cập nhật `Application`, không cập nhật `Guest` legacy.
- HR Manager phải có quyền review phù hợp.

## Code còn lệch spec hoặc cần bổ sung
- Fallback `guestId` trên GET/POST vẫn còn; cần loại bỏ khi toàn bộ link đã dùng `applicationId`.
- Fallback POST legacy vẫn cập nhật `Guest.Status`.
- Cần test một Guest có nhiều application.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- /viewCV?applicationId=... hiển thị đúng CV của application.
- File PDF hợp lệ được hiển thị inline; file không tồn tại hoặc path không an toàn trả 404.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.
