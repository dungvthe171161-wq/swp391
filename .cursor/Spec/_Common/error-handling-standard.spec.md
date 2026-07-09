# Đặc tả dùng chung: Chuẩn xử lý lỗi

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Tất cả controller JSP và JSON API trong HRMS.

## Route, controller và JSP liên quan
- Controller servlet trả forward/redirect cho HTML và JSON cho API.
- `PermissionUtil` có helper cho forbidden HTML và JSON.
- Một số controller hiện vẫn dùng `printStackTrace` hoặc `System.out`.

## Hiện trạng code
- HTML lỗi thường forward về JSP hoặc redirect kèm query string.
- API admin, payroll và notification có nhánh JSON.
- Validation form được xử lý rải rác trong controller.

## Quy tắc nghiệp vụ chuẩn
- Lỗi quyền trả 403 hoặc redirect login tùy loại request.
- Lỗi validation phải giữ lại dữ liệu người dùng đã nhập nếu hợp lý.
- Lỗi hệ thống phải log ở server, không lộ stack trace cho người dùng.

## Code còn lệch spec hoặc cần bổ sung
- Cần thay `printStackTrace` bằng logger.
- Cần chuẩn hóa format JSON lỗi: `success`, `message`, `errors` nếu có.
- Cần tách lỗi validation, lỗi quyền và lỗi hệ thống rõ hơn.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

