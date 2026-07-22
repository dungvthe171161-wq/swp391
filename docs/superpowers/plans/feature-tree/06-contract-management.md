# Feature Tree 06 - Kế hoạch triển khai Quản lý hợp đồng

- **Trạng thái:** Bản nháp
- **Phạm vi chuẩn:** Feature Tree 06
- **Phụ thuộc:** Plan 01, quy trình tạo Employee của Plan 05 và Plan 10
- **Được sử dụng bởi:** Plan 03 và 07

## 1. Mục tiêu

Cung cấp lifecycle Contract nhất quán từ HR Staff tạo draft và document, HR Manager approve/reject, Employee xem/ký có kiểm tra ownership, lưu metadata xác minh chữ ký, hết hạn Contract cũ và kích hoạt Contract mới.

## 2. Code và dữ liệu hiện tại cần audit

### Controller và giao diện

- `hrstaff/CreateContractController`
- `hrstaff/ContractListController`
- `hr/ApproveRejectContractController`
- Các action Contract trong `employee/EmployeePortalController`
- `Views/HrStaff/CreateContract.jsp`, `ContractList.jsp`
- `Views/hr/ApproveRejectContract.jsp`
- `Views/Employee/Contract.jsp`

### Model và DAO

- `Contract`, `ContractDocument`
- `ContractDAO`, `ContractDocumentDAO`, `EmployeeDAO`

### Migration

- `src/data/2026-07-07-contract-document.sql`
- `src/data/2026-07-07-contract-document-file.sql`
- `src/data/2026-07-07-contract-employee-signature.sql`
- `src/data/2026-07-13-contract-signature-metadata.sql`

Audit SRS ghi nhận các cột metadata chữ ký mà `ContractDAO` sử dụng có thể chưa tồn tại trong bảng live. Vì vậy, xác minh schema là bước đầu tiên có tính chặn.

## 3. Ranh giới phạm vi

Plan này chịu trách nhiệm trạng thái Contract và ContractDocument, validation, transaction, ownership, ký và audit. Plan 03 chịu trách nhiệm điều hướng phía Employee. Plan 07 sử dụng lương Contract có hiệu lực nhưng không được đoán từ kỳ không hợp lệ. Plan 10 chịu trách nhiệm gửi Notification và email.

## 4. Lifecycle chuẩn cần đối chiếu với database

```text
Draft -> Pending_Approval -> Rejected
                          -> Pending_Signature -> Active -> Expired/Terminated
```

Nếu giữ `Approved` cũ làm alias, phải định nghĩa nó được migrate sang hay được xem tương đương `Pending_Signature`; không để hai trạng thái phê duyệt mơ hồ.

## 5. Các bất biến Contract

- Chuyển trạng thái Contract có điều kiện theo trạng thái hiện tại mong đợi.
- HR Staff không thể tự approve nếu không có quyền HR Manager rõ ràng.
- Chỉ Employee sở hữu mới được xem/tải/ký Contract được bảo vệ.
- Dữ liệu chữ ký được kiểm tra, giới hạn dung lượng, decode an toàn và hash.
- Kích hoạt Contract mới và hết hạn Contract Active cũ xảy ra trong cùng một transaction.
- Mức lương Contract dùng cho Payroll phải có hiệu lực trong kỳ Payroll được chọn.
- DAO không âm thầm fallback sang schema thiếu cột sau khi migration chuẩn được yêu cầu.

## 6. Các task triển khai

### Task 1 - Xác minh và chuẩn hóa schema Contract

- [ ] So sánh cột `Contract` và `ContractDocument` live với mọi migration repository và `data.sql`.
- [ ] Áp dụng hoặc thay migration để Status, Notes, document storage, SignedAt, SignedBy, signature hash, content hash và metadata khớp với code.
- [ ] Định nghĩa chính xác giá trị status và migrate dữ liệu cũ theo cách idempotent.
- [ ] Thêm index cho EmployeeID, Status, khoảng ngày và hàng đợi approve/sign.
- [ ] Thêm foreign key và nullability rule bảo toàn dữ liệu cũ an toàn.
- [ ] Loại bỏ SQL fallback trong `ContractDAO` sau khi chứng minh migration chuẩn hoạt động.
- [ ] Test migration trên bản sao database có Contract cũ.

### Task 2 - Định nghĩa validation và transition policy của Contract

- [ ] Tạo constant/enum trạng thái Contract tương thích database.
- [ ] Mã hóa transition hợp lệ và role được phép.
- [ ] Kiểm tra start date, end date, base salary, allowance, contract type và notes.
- [ ] Định nghĩa quy tắc overlap và Contract tương lai có được tồn tại cùng Contract Active hay không.
- [ ] Định nghĩa quy tắc edit/delete cho từng trạng thái.
- [ ] Định nghĩa hành vi gửi lại sau khi bị reject.
- [ ] Viết trước mọi test transition được phép/bị cấm.

### Task 3 - Ổn định ContractDAO và ContractDocumentDAO

- [ ] Thêm overload thao tác ghi nhận Connection cho hoạt động transaction.
- [ ] Thêm truy vấn có kiểm tra ownership và trạng thái cho action Employee.
- [ ] Thêm cập nhật Draft và submit Pending_Approval có điều kiện.
- [ ] Thêm approve/reject có điều kiện kèm actor, thời gian và metadata lý do.
- [ ] Thêm `SELECT ... FOR UPDATE` hoặc locking tương đương cho kích hoạt bằng chữ ký.
- [ ] Thêm lookup Contract có hiệu lực theo Employee và kỳ Payroll cho Plan 07.
- [ ] Trả về conflict/not-found rõ ràng thay vì dựa trên nội dung exception.
- [ ] Dùng `PreparedStatement`, try-with-resources và SLF4J logging.

### Task 4 - Tạo `ContractService` có transaction

- [ ] Tạo service có thể inject, chịu trách nhiệm lifecycle Contract.
- [ ] Triển khai tạo draft với nội dung Document/metadata file.
- [ ] Triển khai update và submit để duyệt.
- [ ] Triển khai approve và reject.
- [ ] Triển khai Employee ký và activate.
- [ ] Triển khai expire/terminate rõ ràng nếu được yêu cầu.
- [ ] Thu thập audit và event Plan 10, chỉ gửi message sau commit.
- [ ] Trả về typed result cho validation, permission, not-found, stale-state và conflict.

### Task 5 - Luồng HR Staff tạo/sửa/danh sách

- [ ] Chỉ load Employee đủ điều kiện và dữ liệu việc làm cần thiết.
- [ ] Kiểm tra toàn bộ form ở backend; bỏ qua status hoặc trường approver do browser gửi.
- [ ] Lưu Contract và ContractDocument nhất quán.
- [ ] Định nghĩa hành vi an toàn cho file Contract upload so với nội dung được sinh.
- [ ] Chỉ cho edit/delete ở trạng thái được tài liệu hóa.
- [ ] Submit sang Pending_Approval qua service.
- [ ] Thêm filter, phân trang và action theo trạng thái vào trang danh sách.
- [ ] Thêm test create, update, double-submit, ngày không hợp lệ và lỗi database.

### Task 6 - HR Manager approve/reject

- [ ] Yêu cầu role HR Manager cùng quyền approve Contract.
- [ ] Load đầy đủ Contract, Employee và Document mà không lộ đường dẫn không an toàn.
- [ ] Chỉ approve từ Pending_Approval và chuyển sang trạng thái chờ ký chuẩn.
- [ ] Chỉ reject từ Pending_Approval và bắt buộc có lý do.
- [ ] Ghi actor, timestamp, trạng thái cũ/mới và lý do vào audit data.
- [ ] Dùng conditional update để ngăn quyết định hai lần.
- [ ] Gửi Notification cho HR Staff và Employee sau commit khi phù hợp.
- [ ] Thêm test sai role, stale-state, approve lặp và lý do reject.

### Task 7 - Employee xem và tải Document được bảo vệ

- [ ] Suy ra EmployeeID từ tài khoản đã xác thực qua ngữ cảnh Plan 03.
- [ ] Lấy Contract/Document với điều kiện EmployeeID ownership ngay trong DAO.
- [ ] Stream file với content type và disposition an toàn mà không lộ đường dẫn filesystem.
- [ ] Từ chối path traversal, file thiếu, metadata không khớp và ContractID của Employee khác.
- [ ] Định nghĩa Employee có được xem Contract Expired trong lịch sử hay không.
- [ ] Thêm test ownership và bảo mật file.

### Task 8 - Employee ký và kích hoạt trong transaction

- [ ] Chỉ nhận PNG/định dạng đã thỏa thuận và giới hạn dung lượng sau decode.
- [ ] Kiểm tra Base64 và từ chối dữ liệu chữ ký lỗi hoặc rỗng.
- [ ] Tính signature hash và content hash của Contract/Document bằng thuật toán đã tài liệu hóa.
- [ ] Lock Contract mục tiêu rồi kiểm tra lại ownership Employee và trạng thái Pending_Signature.
- [ ] Expire mọi Contract Active trước đó của Employee.
- [ ] Activate Contract đã ký và lưu SignedAt, SignedBy, hash cùng metadata.
- [ ] Commit việc expire và activate cùng nhau hoặc rollback cả hai.
- [ ] Làm thao tác lặp có tính idempotent mà không ghi đè bằng chứng chữ ký.
- [ ] Thêm test nội dung bị sửa, sai owner, sai trạng thái, ký hai lần và rollback.

### Task 9 - Tích hợp Payroll

- [ ] Cung cấp một truy vấn service/DAO cho Contract có hiệu lực trong kỳ Payroll được yêu cầu.
- [ ] Định nghĩa hành vi khi đổi Contract giữa kỳ.
- [ ] Từ chối hoặc cảnh báo tính Payroll khi không có Contract hợp lệ; không âm thầm dùng mức lương mới nhất không liên quan.
- [ ] Bảo đảm giá trị lương dùng `BigDecimal` và scale tiền tệ nhất quán.
- [ ] Thêm test biên ngày hiệu lực cho Plan 07.

### Task 10 - Notification, audit và cleanup

- [ ] Định nghĩa event cho submitted, approved, rejected, pending signature, signed/active, expired và terminated.
- [ ] Gửi Notification/email qua Plan 10 sau commit.
- [ ] Thay `System.out`/`System.err` và log fallback lỗi schema bằng SLF4J.
- [ ] Không ghi log byte chữ ký, nội dung Document, mức lương vượt quá nhu cầu audit hoặc đường dẫn filesystem.
- [ ] Cập nhật số liệu dashboard/hàng đợi sau khi chuẩn hóa status.

### Task 11 - Xác minh

- [ ] Chạy test migration, transition, DAO, service transaction, permission controller, ownership, hashing và file.
- [ ] Chạy `mvn test`, `mvn -q compile` và `mvn -q package`.
- [ ] Thực hiện thủ công create -> submit -> approve -> Employee sign -> active.
- [ ] Kiểm thử thủ công reject/gửi lại và Contract thứ hai thay Contract Active.
- [ ] Xác minh Employee khác không thể xem, tải hoặc ký Contract.
- [ ] Xác minh truy vấn Contract có hiệu lực cho Payroll ở biên kỳ.

## 7. Tiêu chí hoàn thành

- Schema live, migration, model và DAO thống nhất về trường và giá trị trạng thái Contract.
- Không còn SQL fallback schema trong đường DAO chuẩn.
- Create, approve/reject, sign, expire Contract cũ và activate tuân theo một service policy.
- Ký và kích hoạt được kiểm tra ownership, hash, transaction và idempotent.
- Payroll nhận Contract hợp lệ theo kỳ thay vì fallback mức lương không an toàn.
- Test, compile, package, bảo mật file và lifecycle thủ công đầy đủ đều đạt.

