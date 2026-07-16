# Đặc tả dùng chung: CSRF và phương thức HTTP

Trạng thái: Bổ sung chuẩn mục tiêu theo đối chiếu code ngày 2026-07-14.
Ngôn ngữ: tiếng Việt có dấu. Spec này áp dụng cho mọi thao tác làm thay đổi dữ liệu; hiện trạng chưa đạt phải được ghi rõ khi triển khai.

## Actor và phạm vi
- Tất cả user, form HTML và JSON API dùng session cookie trong HRMS.

## Route, controller và JSP liên quan
- Mọi servlet có `doPost`, cùng các route create/update/delete/approve/reject/submit/read.
- Các luồng cần ưu tiên: recruitment, contract, payroll, leave, task, user/role/department, notification và office location.
- `ChatbotFaqController` và `AI_Faq_Management.jsp` hiện là mẫu có CSRF token cục bộ.

## Hiện trạng code
- CSRF mới xuất hiện rõ ở luồng quản lý chatbot FAQ.
- Một số thao tác mutation còn được thực hiện trong GET hoặc qua query string, gồm xóa và chuyển trạng thái.
- Chưa có filter/helper CSRF dùng chung cho toàn ứng dụng.

## Quy tắc nghiệp vụ chuẩn
- GET, HEAD và OPTIONS chỉ đọc dữ liệu, không tạo tác dụng phụ nghiệp vụ.
- Create/update/delete/approve/reject/submit/read-all phải dùng POST hoặc phương thức mutation phù hợp.
- Request dùng session cookie phải có CSRF token gắn với session; form dùng hidden field, JSON dùng header chuẩn.
- Token thiếu, sai hoặc hết hạn trả 403; không thực hiện một phần nghiệp vụ.
- Sau POST HTML phải redirect theo Post/Redirect/Get; submit lại không được tạo tác dụng phụ trùng.
- Cookie session phải có `HttpOnly`, `Secure` khi chạy HTTPS và `SameSite` phù hợp; SameSite không thay thế CSRF token.

## Code còn lệch spec hoặc cần bổ sung
- Cần helper/filter CSRF dùng chung và cơ chế sinh/rotate token thống nhất.
- Cần chuyển các mutation trong `doGet` sang POST, đặc biệt recruitment, contract, payroll adjustment và delete.
- Cần chuẩn hóa phản hồi CSRF cho HTML và JSON.
- Cần cập nhật toàn bộ JSP/form và JavaScript gọi API mutation.

## Kiểm thử tối thiểu
- GET không làm thay đổi database dù có query action mutation.
- POST thiếu/sai token trả 403; token đúng chỉ dùng trong session tương ứng.
- Kiểm tra submit lặp, back/refresh và nhiều tab không tạo bản ghi hoặc notification trùng.
- Kiểm tra API JSON và form HTML đều được bảo vệ.
