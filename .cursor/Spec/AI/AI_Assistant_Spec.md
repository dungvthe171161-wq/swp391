# Đặc tả module AI: Trợ lý BetterHR

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Trợ lý AI nội bộ hỗ trợ người dùng BetterHR nếu module được triển khai.

## Route, controller và JSP liên quan
- Spec hiện chỉ là định hướng; cần đối chiếu route thật trước khi triển khai.
- Route `/ai/chat` được nhắc trong README cũ nhưng chưa được đưa vào filter bảo vệ.
- Nếu có ChatServlet hoặc API AI, phải đặt dưới route đã bảo vệ session.

## Hiện trạng code
- Chưa xác nhận đầy đủ controller AI trong code hiện tại.
- Chưa thấy route AI được bảo vệ trong các filter chính.
- Không nên coi AI Assistant là đã hoàn tất nếu thiếu route, service và permission.

## Quy tắc nghiệp vụ chuẩn
- AI chỉ được truy cập bởi user đã đăng nhập.
- Không gửi dữ liệu nhạy cảm ra ngoài nếu chưa có chính sách bảo mật.
- Phải có permission hoặc rule rõ actor nào dùng được AI.

## Code còn lệch spec hoặc cần bổ sung
- Bổ sung filter cho `/ai/chat` hoặc route AI thật.
- Bổ sung logging, giới hạn dữ liệu và kiểm soát prompt nếu triển khai.
- Cần test bảo mật trước khi bật cho production.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

