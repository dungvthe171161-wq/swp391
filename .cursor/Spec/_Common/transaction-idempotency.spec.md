# Đặc tả dùng chung: Transaction và tính idempotent

Trạng thái: Bổ sung chuẩn mục tiêu theo đối chiếu code ngày 2026-07-14.
Ngôn ngữ: tiếng Việt có dấu. Spec này áp dụng cho workflow ghi nhiều bảng hoặc phát sinh tác dụng phụ ngoài database.

## Actor và phạm vi
- Tất cả module có thao tác nhiều bước: tuyển dụng, offer, interview, hợp đồng, payroll, leave, employee/user và notification.

## Route, controller và JSP liên quan
- Các controller HR Staff/HR Manager, `CreateEmployeeController`, `GuestPortalController`, `EmployeePortalController`.
- DAO liên quan tới `Application`, `Interview`, `Offer`, `Contract`, `ContractDocument`, `Payroll`, `SystemUser`, `Guest`, `Notification`.

## Hiện trạng code
- Nhiều controller gọi tuần tự nhiều DAO; mỗi DAO có thể tự mở connection và commit riêng.
- Một số luồng có thao tác bù thủ công, ví dụ xóa bản ghi vừa tạo khi lưu tài liệu thất bại.
- Email/notification thường được gọi gần thao tác cập nhật trạng thái nhưng chưa có outbox/idempotency chung.

## Quy tắc nghiệp vụ chuẩn
- Các cập nhật tạo thành một quyết định nghiệp vụ phải dùng cùng transaction database.
- Lỗi giữa chừng phải rollback toàn bộ dữ liệu liên quan; không để trạng thái nửa hoàn tất.
- Chuyển trạng thái phải kiểm tra trạng thái cũ trong câu UPDATE hoặc dùng optimistic locking.
- Request retry/double-submit phải idempotent; create quan trọng nên có business key hoặc idempotency key.
- Email/notification chỉ phát sau commit; nên dùng outbox có trạng thái retry và khóa chống gửi trùng.
- Compensation chỉ dùng khi không thể transaction và phải được audit rõ.

## Code còn lệch spec hoặc cần bổ sung
- Cần service layer nhận chung `Connection` cho workflow nhiều DAO.
- Cần transaction cho Application+Interview, Offer+Application, Contract+Document, Payroll+adjustment và Employee+User+Guest conversion.
- Chưa có idempotency key/outbox dùng chung.
- Cần chuẩn hóa unique constraint và kiểm tra concurrency ở database.

## Kiểm thử tối thiểu
- Tiêm lỗi ở từng bước và xác nhận không còn dữ liệu nửa hoàn tất.
- Gửi cùng request hai lần chỉ tạo một kết quả nghiệp vụ và một notification/email.
- Hai user cập nhật cùng bản ghi chỉ một chuyển trạng thái hợp lệ thành công.
- Retry worker/outbox không gửi trùng sau restart.
