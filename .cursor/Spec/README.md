# Chỉ mục Spec của HRMS
Trạng thái: Đã phê duyệt
Cập nhật: 2026-06-18

## Mục tiêu
Thư mục `.cursor/Spec` lưu các spec theo actor và module, viết theo luồng code hiện tại của HRMS. Mỗi spec cần nêu rõ actor, route, controller/JSP liên quan, luồng chính, hiện trạng code và missing work.

## Thư mục Tác nhân (Actor)
- `Auth`: đăng nhập, đăng ký, đăng xuất, homepage routing, quên mật khẩu/PIN, đổi mật khẩu, Google Login.
- `Admin`: dashboard, user, role, permission, department, audit/profile.
- `Dept`: dashboard phòng ban, tạo task, danh sách task, cập nhật task.
- `Employee`: home, view task, profile, payroll/contract, leave.
- `HrStaff`: recruitment post, candidate, contract, payroll, allowance/deduction.
- `HrManager`: recruitment review, CV, employee, contract approval, payroll approval.
- `Guest`: actor Guest Candidate xem homepage public, xem job và nộp đơn ứng tuyển.
- `PublicCandidate`: spec cũ của ứng viên public, giữ để đối chiếu; spec chính nên đọc trong `Guest`.
- `AI`: trợ lý AI tích hợp cho toàn bộ BetterHR; chi tiết trong `AI/AI_Assistant_Spec.md`.

## Đặc tả dùng chung (Cross-cutting Spec)
- `_Common/permission-matrix.spec.md`: bảng role, route, permission và filter.
- `_Common/security-auth-hardening.spec.md`: bảo mật login, session, cookie, remember-me.
- `_Common/error-handling-standard.spec.md`: chuẩn lỗi HTML và JSON API.
- `_Common/status-workflow.spec.md`: status nghiệp vụ cho recruitment, candidate, payroll, contract, task, leave.
- `_Common/audit-log.spec.md`: thao tác cần ghi log và dữ liệu log cần có.
- `_Common/upload-cv.spec.md`: rule upload CV cho ứng viên public.
- `_Common/route-conflict-resolution.spec.md`: xử lý xung đột route `/viewTask`.
- `_Common/database-impact.spec.md`: bảng đọc/ghi theo module.
- `_Common/notification.spec.md`: thông báo dùng chung cho mọi actor theo `SystemUser.UserID`.
- `_Common/test-plan.spec.md`: bộ test tối thiểu theo module.
- `_Common/ui-language-theme.spec.md`: chuẩn giao diện, màu sắc, font chữ và tiếng Việt trên JSP.

## Nguyên tắc cập nhật spec
- Spec phải ưu tiên đúng với route/controller hiện có.
- Nếu code hiện tại có lỗi thiết kế, ghi vào `Missing Work`, không viết như đã hoàn thành.
- Password auth hiện tại dùng `SystemUser.PasswordHash` theo logic plain text của dự án; nếu đổi sang hash/BCrypt thì phải cập nhật code và spec cùng lúc.
- Session login dùng attribute `systemUser`.
- JSP và nội dung UI của dự án nên hiển thị tiếng Việt, giữ tên logo BetterHR.
- Khi chỉ sửa UI, không đổi route, form action, input name/value hoặc attribute controller.

## Các cảnh báo cần xử lý trong code
- `/departments` chưa được đưa vào filter bảo vệ admin.
- Employee `ViewTask` và Dept `ViewTask` đang trùng mapping `/viewTask`.
- Auth password recovery dùng PIN session, cần rate limit nếu hardening sau này.
- Google Login đã có backend OAuth2, cần đảm bảo config Google local/env đúng.
- Register local đã gửi email BetterHR sau khi tạo account; role mặc định phải là `Guest`, `EmployeeID = NULL`.
- Guest Candidate là actor public chính; Phase 2 dùng `Application`, `Interview`, `Offer` cho workflow ứng tuyển.
- Account Guest chỉ được chuyển sang Employee khi offer được accepted/hired theo spec Guest Phase 2.
- `Notification` là module chung cho mọi actor, Guest chỉ là một nơi hiển thị notification.
- HR Manager Home dùng `/HrHomeController`; không link trực tiếp JSP nếu cần nạp data.
- Một số controller còn `printStackTrace`/`System.out`, nên thay bằng logger.
- Route `/ai/chat` chưa được thêm vào filter; phải đảm bảo chỉ user đã đăng nhập mới gọi được ChatServlet.
