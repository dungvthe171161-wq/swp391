# Đặc tả dùng chung: Ngôn ngữ giao diện và chủ đề BetterHR

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Tất cả JSP, fragment, message validation và notification hiển thị cho người dùng.

## Route, controller và JSP liên quan
- JSP trong `src/main/webapp/Views`, `Admin` và fragment topbar/sidebar.
- CSS dùng chung như `hr-theme.css` và style nội bộ từng actor.
- Thông báo từ controller đặt vào request/session/query string.

## Hiện trạng code
- Nhiều UI đã dùng tiếng Việt có dấu.
- Một số tên kỹ thuật, permission, route và enum vẫn giữ tiếng Anh theo code.
- Terminal có thể hiển thị mojibake, nhưng file phải lưu UTF-8.

## Quy tắc nghiệp vụ chuẩn
- Text người dùng nhìn thấy phải là tiếng Việt có dấu, ngắn gọn và nhất quán.
- Không dịch tên class, route, permission, enum và brand `BetterHR`.
- Không đổi name/value form khi chỉ sửa câu chữ hoặc giao diện.

## Code còn lệch spec hoặc cần bổ sung
- Cần rà toàn bộ JSP nếu còn text không dấu.
- Cần đảm bảo response `Content-Type` có UTF-8 ở các servlet trả JSON/HTML.
- Cần kiểm tra trình duyệt thật vì terminal không phản ánh chính xác encoding.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

