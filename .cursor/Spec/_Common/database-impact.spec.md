# Đặc tả dùng chung: Ảnh hưởng cơ sở dữ liệu

Trạng thái: Đã rà soát theo code ngày 2026-07-22.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Tất cả module đọc/ghi MySQL; spec này giúp đối chiếu bảng nào bị tác động khi sửa code.

## Route, controller và JSP liên quan
- `src/data/data.sql`: schema chính và dữ liệu seed.
- DAO trong `src/main/java/com/hrm/dao`: lớp truy cập dữ liệu.
- Migration trong `src/data/migrations`: notification, payroll, work schedule/GPS attendance và task assignment.

## Hiện trạng code
- Auth/Admin dùng `SystemUser`, `Role`, `Permission`, `RolePermission`, `SystemLog`/audit nếu có.
- Recruitment dùng `Recruitment`, `Guest`, `CandidateProfile`, `Application`, `Interview`, `Offer`, `Notification`.
- Employee dùng `Employee`, `Task`, `MailRequest`, `Payroll`, `Contract`, `ContractDocument`, `Attendance`, `WorkSchedule`, `EmployeeWorkSchedule`.
- Contract signing cập nhật `Contract.Status`, `SignedAt`, `SignedBy`, `EmployeeSignaturePath`, `SignatureHash`, `SignIp`, `SignUserAgent`, `ContractContentHash` và expire contract `Active` cũ của cùng employee.
- Notification schema đã có `ActorUserID`, `EntityType`, `EntityID`, `TargetUrl`, `Priority`, `ExpiresAt`.
- `CreateEmployeeController` sau khi tạo employee set `Guest.Status = Converted`, không xóa bản ghi `Guest`.

## Quy tắc nghiệp vụ chuẩn
- Mọi thay đổi enum phải đi kèm migration và sửa DAO/controller/JSP.
- Không xóa lịch sử tuyển dụng khi chuyển ứng viên thành nhân viên.
- Không ghi trạng thái workflow mới vào bảng legacy nếu đã có bảng chuyên trách.
- Luồng phối hợp nhiều bảng phải có transaction và idempotency guard.

## Code còn lệch spec hoặc cần bổ sung
- `Offer` đang có unique theo `ApplicationID`, nên chưa hỗ trợ nhiều offer cho một application.
- Một số thao tác phối hợp nhiều DAO chưa chạy trong transaction chung.
- `CreateEmployeeController` chưa dùng transaction đầy đủ và chưa ghi audit.
- File/chữ ký hợp đồng cần chính sách retention và bảo vệ truy cập thống nhất với dữ liệu database.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.
