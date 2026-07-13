# Đặc tả module AI: Trợ lý BetterHR (Chatbot)

Trạng thái: Đã rà soát theo code ngày 2026-07-13.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Người dùng đã đăng nhập BetterHR (mọi role được hỗ trợ theo dữ liệu role-aware).
- Admin quản lý FAQ chatbot.

## Route, controller và JSP liên quan
- Chat API: `/api/chatbot/message`, `ChatbotController`.
- Feedback API: `/api/chatbot/feedback`, `ChatbotFeedbackController`.
- Admin FAQ: `/admin/chatbot-faqs`, `ChatbotFaqController`.
- Widget JSP: `Views/AI/AI_Assistant_Widget.jspf`.
- Service: `ChatbotService`, `AiChatService`, `ChatbotRoleDataService`, `ChatbotContentSafety`, `ChatbotRateLimiter`.
- Filter: `SessionSecurityFilter` bảo vệ `/api/chatbot/message` và `/api/chatbot/feedback`.

## Hiện trạng code
- `ChatbotController` nhận POST JSON, giới hạn độ dài message, rate limit theo session.
- Trả lời dựa trên FAQ, dữ liệu theo role (`ChatbotRoleDataService`) và fallback AI provider (`AiChatService`).
- Lưu lịch sử hội thoại qua `ChatbotHistoryDAO`.
- Admin quản lý FAQ tại `/admin/chatbot-faqs`; route được `ModulePermissionFilter` cho qua trực tiếp (không qua rule `/admin` chung).
- Chatbot không hiển thị dữ liệu nhạy cảm như số lương chi tiết trong khung chat.

## Quy tắc nghiệp vụ chuẩn
- AI/chatbot chỉ dùng cho user đã đăng nhập (session `systemUser`).
- Dữ liệu trả lời phải tuân permission và scope của role hiện tại.
- Không gửi dữ liệu nhạy cảm ra ngoài nếu chưa có chính sách bảo mật.
- Admin FAQ cần permission quản trị phù hợp.

## Code còn lệch spec hoặc cần bổ sung
- Chưa có permission riêng cho sử dụng chatbot theo role (hiện dựa session + role data).
- Cần bổ sung logging/audit đầy đủ cho feedback và thay đổi FAQ.
- Cần test bảo mật và giới hạn prompt trước khi bật AI provider production.
- Ma trận notification/event chưa đồng bộ với module chatbot.

## Kiểm thử tối thiểu
- User chưa đăng nhập không gọi được `/api/chatbot/message`.
- Mỗi role nhận câu trả lời phù hợp phạm vi dữ liệu (không lộ payroll người khác).
- Admin cập nhật FAQ thành công tại `/admin/chatbot-faqs`.
