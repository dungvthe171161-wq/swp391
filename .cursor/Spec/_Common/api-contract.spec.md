# Đặc tả dùng chung: Chuẩn JSON API

Trạng thái: Bổ sung chuẩn mục tiêu theo đối chiếu code ngày 2026-07-14.
Ngôn ngữ: tiếng Việt có dấu. Spec này áp dụng cho mọi endpoint trả JSON.

## Actor và phạm vi
- JavaScript client, user đã đăng nhập, anonymous client được phép và các controller JSON của HRMS.

## Route, controller và JSP liên quan
- `/api/chatbot/message`, `/api/chatbot/feedback`, `/api/payroll`, `/api/allowance/*`, `/api/deduction/*`.
- `/admin/role-permissions/api` và các nhánh JSON của quản lý user/role.
- `PermissionUtil`, `SessionSecurityFilter`, `RoleAuthorizationFilter`.

## Hiện trạng code
- Các endpoint dùng Gson hoặc ghi JSON trực tiếp và có format phản hồi khác nhau.
- Việc nhận diện JSON đôi khi dựa vào `Accept` hoặc `X-Requested-With`.
- 401/403 và validation error chưa được chuẩn hóa đồng nhất.

## Quy tắc nghiệp vụ chuẩn
- Response dùng UTF-8 và `Content-Type: application/json`.
- Success tối thiểu có `success`, `data` và metadata cần thiết; error có `success=false`, `code`, `message`, `errors` nếu validation theo field.
- Dùng đúng status: 400 malformed, 401 chưa đăng nhập, 403 thiếu quyền, 404 không tồn tại, 409 xung đột, 422 validation nghiệp vụ, 429 rate limit, 500 lỗi hệ thống.
- Không trả stack trace, SQL, file path hoặc secret.
- API danh sách phải chuẩn hóa pagination, filter, sort và giới hạn page size.
- API dùng session cookie phải có CSRF cho mutation và ownership/permission phía server.
- Thay đổi breaking phải có version hoặc kế hoạch tương thích rõ.

## Code còn lệch spec hoặc cần bổ sung
- Cần DTO/envelope và helper ghi JSON dùng chung.
- Cần tách rõ 401 và 403 trong các filter.
- Cần chuẩn hóa validation, pagination và error code ổn định cho frontend.
- Các API allowance/deduction/payroll cần được rà lại route-role mapping và CSRF.

## Kiểm thử tối thiểu
- Contract test cho schema success/error và Content-Type.
- Kiểm tra đầy đủ 400/401/403/404/409/422/429/500.
- API không lộ exception hoặc dữ liệu ngoài ownership.
- Pagination/filter/sort cho kết quả ổn định và có giới hạn đầu vào.
