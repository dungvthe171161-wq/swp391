# Đặc tả dùng chung: Kế hoạch kiểm thử

Trạng thái: Đã rà soát theo code ngày 2026-07-22.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Toàn bộ HRMS sau khi sửa code hoặc spec nghiệp vụ.

## Route, controller và JSP liên quan
- Maven build: `mvn -q compile`.
- Unit/integration test dưới `src/test` nếu có.
- Manual test theo route actor và dữ liệu seed.

## Hiện trạng code
- Route task hiện tại: Dept dùng `/taskManager`, `/postTask`, `/viewTask`; Employee dùng `/employee/tasks` và `/employee/view-task`.
- `/departments` đã có role filter, module permission filter và guard controller-level.
- Employee có `/employee/schedule`, `/employee/contract/document`, `POST /employee/contract` và profile chung `/profilepage` cần có smoke test riêng.
- Đã có test tự động cho một phần notification service và email template, nhưng workflow tích hợp theo actor còn thiếu.
- Test tự động hiện chưa bao phủ đầy đủ workflow tuyển dụng, phân quyền, notification và route legacy.

## Quy tắc nghiệp vụ chuẩn
- Mỗi actor phải có smoke test đăng nhập, mở dashboard, truy cập route bị cấm.
- Workflow có thay đổi trạng thái phải test cả thành công và thất bại.
- Spec thay đổi permission phải test role không đủ quyền.
- File cá nhân phải test cả ownership hợp lệ và truy cập trái phép.

## Code còn lệch spec hoặc cần bổ sung
- Cần test ownership guard trên `/employee/tasks` và `/employee/view-task` khi sửa `taskId` của employee khác.
- Cần test `/employee/schedule` với tháng/năm sai và employee chưa có lịch.
- Cần test `/employee/contract/document` và `POST /employee/contract` với `contractId` của employee khác.
- Cần test `/profilepage` update profile/change password, gồm CSRF sau khi hardening.
- Cần test database cho enum Application/Offer/Interview.
- Cần test bảo mật password sau khi chuyển sang hash.
- Cần test route Dept hiện tại `/taskManager`, `/postTask`, `/viewTask`; nếu chuẩn hóa route thì bổ sung test cho `/dept/tasks/*`.
- Cần test notification matrix: task assigned, employee task status update, leave request/decision, payroll pending/decision, contract pending/decision và offer response.
- Cần test `TaskDeadlineReminderListener`/email reminder theo deadline task và chống gửi trùng.
- Cần test user không phải Admin không truy cập được `/departments`.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.
