# Feature Tree 07 - Kế hoạch triển khai Quản lý bảng lương

- **Trạng thái:** Bản nháp
- **Phạm vi chuẩn:** Feature Tree 07
- **Phụ thuộc:** Plan 01, 06, 08 và 10
- **Được sử dụng bởi:** Payslip Employee trong Plan 03

## 1. Mục tiêu

Cung cấp phép tính Payroll có thể tái hiện và audit từ lương Contract hợp lệ theo kỳ, Attendance, Leave đã duyệt, Allowance, Deduction, Insurance, Tax và Dependent; sau đó thực thi các trạng thái Draft, submit, approve/reject, thanh toán và payslip Employee.

## 2. Code và dữ liệu hiện tại cần audit

### Controller

- `hrstaff/PayrollManagementController`
- `hrstaff/PayrollCalculateController`
- `hrstaff/PayrollAllowanceController` và Allowance API
- `hrstaff/PayrollDeductionController` và Deduction API
- `hr/PayrollApprovalController`
- Phần Payroll Employee trong `EmployeePortalController`

### DAO và model

- `PayrollDAO`, `AllowanceTypeDAO`, `DeductionTypeDAO`
- `EmployeeAllowanceDAO`, `EmployeeDeductionDAO`
- `InsuranceRateDAO`, `TaxRateDAO`, `DependentDAO`
- `AttendanceDAO`, `MailRequestDAO`, `ContractDAO`, `EmployeeDAO`
- `Payroll` và các entity Employee/Allowance/Deduction

### Migration

- `src/data/migrations/2026-07-06_payroll_improvements.sql`
- `src/data/migrations/2026-07-07_payroll_deduplicate.sql`
- `src/data/migrations/2026-07-11_payroll_employee_deduction_unique.sql`

## 3. Ranh giới phạm vi

Plan 06 xác định Contract nào có hiệu lực trong một kỳ. Plan 08 chịu trách nhiệm dữ liệu đúng của Attendance và Leave đã duyệt. Plan này chịu trách nhiệm snapshot Payroll, công thức, lifecycle, phê duyệt, đánh dấu thanh toán và khả năng xem payslip. Plan 10 chịu trách nhiệm gửi Notification.

## 4. Lifecycle chuẩn

```text
Draft -> Pending -> Approved -> Paid
                 -> Rejected -> Draft sau khi được phép sửa và gửi lại
```

Code hiện tại có hiển thị/đếm `Paid`, nhưng audit SRS không tìm thấy hành động Approved-to-Paid chuẩn. Phải triển khai transition này hoặc loại bỏ rõ ràng khỏi hợp đồng trạng thái; không giữ một trạng thái không bao giờ đạt tới.

## 5. Các bất biến Payroll

- Một Employee có tối đa một Payroll cho mỗi PayPeriod đã chuẩn hóa.
- Cùng input snapshot và cùng rule phải tạo cùng kết quả tiền tệ.
- Mọi phép tính tiền dùng `BigDecimal` với scale và rounding được tài liệu hóa.
- Payroll Draft/Rejected được phép sửa; Payroll Pending/Approved/Paid không được sửa qua endpoint tính lương thông thường.
- Approve và Reject là transition có điều kiện từ Pending.
- Thanh toán là transition có điều kiện từ Approved kèm metadata người thanh toán và thời gian.
- Chỉ Employee sở hữu được xem payslip ở trạng thái được phép.
- Batch operation có tính idempotent và báo conflict theo từng record mà không làm hỏng record thành công.

## 6. Hợp đồng tính lương bắt buộc

Trước khi code, phải tài liệu hóa công thức và dấu của từng thành phần:

```text
Thu nhập gộp = lương cơ bản theo kỳ + phụ cấp + thưởng + khoản cộng hợp lệ
Khấu trừ Employee = khoản khấu trừ cấu hình + điều chỉnh nghỉ không lương/vắng mặt
Cơ sở và khoản đóng bảo hiểm = chính sách tỷ lệ áp dụng lên cơ sở lương hợp lệ
Thu nhập chịu thuế = thu nhập gộp - bảo hiểm/khấu trừ/giảm trừ người phụ thuộc hợp lệ
Thuế thu nhập cá nhân = biểu thuế lũy tiến dùng rule active trong kỳ
Lương thực nhận = thu nhập gộp - khấu trừ - bảo hiểm - thuế
```

Không chỉ dựa vào dàn ý này; phải ghi công thức nghiệp vụ chính xác của dự án vào test và implementation note trước khi thay đổi code tính production.

## 7. Các task triển khai

### Task 1 - Audit schema, kỳ lương và công thức hiện tại

- [ ] So sánh schema Payroll/PayrollAudit, Allowance, Deduction, Insurance, Tax, Dependent, Contract, Attendance và Leave với DAO/migration.
- [ ] Định nghĩa một định dạng và quy tắc validation `PayPeriod` thống nhất.
- [ ] Kiểm kê mọi phép tính lương trong controller, DAO, JSP và JavaScript.
- [ ] Ghi nhận công thức, rounding, fallback và khác biệt hiện tại.
- [ ] Thêm regression fixture cho các Employee đại diện trước khi refactor.
- [ ] Thêm report cho bản ghi Employee-PayPeriod trùng và status không hợp lệ.

### Task 2 - Thực thi tính duy nhất theo kỳ và schema trạng thái

- [ ] Áp dụng deduplication idempotent và migration `UNIQUE(EmployeeID, PayPeriod)`.
- [ ] Chuẩn hóa giá trị Draft, Pending, Approved, Rejected và Paid.
- [ ] Thêm cột metadata reject, approve và payment bắt buộc nếu còn thiếu.
- [ ] Định nghĩa tính duy nhất và quan hệ giữa PayrollAudit với Payroll.
- [ ] Thêm index cho PayPeriod, Status, Employee và hàng đợi approval.
- [ ] Đồng bộ bootstrap schema với migration.
- [ ] Xác minh migration trên dữ liệu cũ có duplicate và Rejected.

### Task 3 - Xây snapshot input Payroll bất biến

- [ ] Định nghĩa DTO `PayrollCalculationInput` chứa mọi input cùng phiên bản/ngày hiệu lực của rule.
- [ ] Load Contract có hiệu lực trong kỳ qua Plan 06; loại bỏ fallback Contract mới nhất khi không hợp lệ cho kỳ.
- [ ] Load Attendance đúng kỳ qua Plan 08.
- [ ] Chỉ load phiên/ngày Leave Approved liên quan đến kỳ.
- [ ] Load Allowance/Deduction active của Employee mà không áp dụng trùng.
- [ ] Load Insurance/Tax rate có hiệu lực trong kỳ và điều kiện Dependent.
- [ ] Lưu đủ chi tiết snapshot/audit để tái hiện kết quả về sau.
- [ ] Trả lỗi validation có thể xử lý khi thiếu Contract hoặc rate bắt buộc.

### Task 4 - Tách và test `PayrollCalculationService`

- [ ] Chuyển logic tính khỏi controller vào service có thể inject.
- [ ] Triển khai mọi công thức bằng `BigDecimal`, scale và rounding mode rõ ràng.
- [ ] Định nghĩa prorate khi đổi Contract giữa kỳ, nghỉ hưởng lương/không lương, vắng mặt, overtime và tháng không đầy đủ.
- [ ] Từ chối giá trị trung gian âm hoặc vô lý trừ khi business rule cho phép.
- [ ] Trả kết quả tính chi tiết dùng chung cho preview, persistence, audit và payslip.
- [ ] Thêm test biên cho không chấm công, đủ công, nghỉ có/không lương, overtime, nhiều Allowance/Deduction, trần Insurance, bậc Tax, Dependent và rounding.

### Task 5 - Làm PayrollDAO có transition điều kiện và transaction

- [ ] Thêm method create/update/audit nhận Connection.
- [ ] Thay `updateStatus` chung bằng các transition method có tên rõ ràng.
- [ ] Cập nhật SQL approve/reject với `WHERE Status = 'Pending'` và kiểm tra số dòng ảnh hưởng.
- [ ] Thêm `markPaid` với `WHERE Status = 'Approved'` cùng metadata actor/date/reference thanh toán.
- [ ] Cập nhật Payroll và PayrollAudit trong một transaction.
- [ ] Giới hạn delete ở Draft và định nghĩa Rejected có được delete hay không.
- [ ] Trả về duplicate, stale-state và not-found rõ ràng.
- [ ] Thêm test DAO cho conditional transition và rollback.

### Task 6 - HR Staff tính, preview và lưu Draft

- [ ] Kiểm tra Employee, PayPeriod và mọi input thủ công tùy chọn.
- [ ] Preview phép tính từ snapshot trước khi persistence.
- [ ] Chỉ create/update Payroll Draft/Rejected qua service.
- [ ] Ngăn browser đặt trực tiếp tổng đã tính hoặc lifecycle status.
- [ ] Hiển thị thiếu Contract/rate/Attendance mà không tạo Draft dở dang.
- [ ] Hiển thị giải thích chi tiết từng khoản cộng và trừ.
- [ ] Thêm test create/update, duplicate, kỳ không hợp lệ, thiếu Contract và submit lặp.

### Task 7 - Quản lý Allowance và Deduction

- [ ] Kiểm tra type, amount/rate, ngày hiệu lực, Employee ownership và active state.
- [ ] Thực thi unique rule cho Employee Deduction/Allowance active khi phù hợp.
- [ ] Bảo vệ JSON API bằng permission và trả đúng HTTP status.
- [ ] Ngăn thay đổi làm biến đổi hồi tố snapshot Payroll Approved/Paid.
- [ ] Định nghĩa delete thành deactivate khi cần giữ lịch sử.
- [ ] Thêm test validation API, permission, duplicate và ngày hiệu lực.

### Task 8 - Generate và submit theo batch

- [ ] Chọn Employee active đủ điều kiện có Contract hợp lệ theo kỳ.
- [ ] Làm generate-all idempotent bằng unique constraint Employee-PayPeriod.
- [ ] Quyết định transaction theo từng record hay all-or-nothing và tài liệu hóa.
- [ ] Trả số lượng created, updated, skipped, conflict và failed với chi tiết an toàn.
- [ ] Chỉ submit record Draft bằng conditional transition.
- [ ] Không submit lại hoặc tạo lại record Pending/Approved/Paid.
- [ ] Thêm test lỗi một phần, chạy lại, duplicate và batch lớn.

### Task 9 - HR Manager approve và reject

- [ ] Yêu cầu role HR Manager và quyền approve Payroll.
- [ ] Load breakdown phép tính và audit snapshot trước khi quyết định.
- [ ] Chỉ approve Payroll Pending qua service operation có điều kiện.
- [ ] Chỉ reject Payroll Pending và bắt buộc có note ý nghĩa.
- [ ] Ghi actor, timestamp, trạng thái cũ/mới và note.
- [ ] Áp dụng cùng quy tắc cho approve/reject hàng loạt mà không tin status browser gửi.
- [ ] Gửi Notification cho HR Staff/Employee sau commit khi cần.
- [ ] Thêm test sai role, stale-state, quyết định lặp và batch trộn trạng thái.

### Task 10 - Triển khai hoàn tất thanh toán

- [ ] Định nghĩa ai được đánh dấu Payroll Paid và payment reference bên ngoài nào bắt buộc.
- [ ] Thêm route service/controller cho Approved-to-Paid.
- [ ] Lưu paid actor, paid timestamp và reference/note.
- [ ] Làm transition có điều kiện và idempotent.
- [ ] Ngăn reject, tính lại, update hoặc delete sau Paid.
- [ ] Thêm audit và Notification Employee sau commit.
- [ ] Thêm test sai trạng thái, payment lặp, thiếu reference và permission.

### Task 11 - Payslip Employee

- [ ] Chỉ cung cấp Payroll Approved/Paid của Employee đã xác thực qua Plan 03.
- [ ] Hiển thị snapshot đã lưu thay vì tính lại bằng rule hiện tại.
- [ ] Hiển thị rõ base salary, khoản cộng, khấu trừ, Insurance, Tax và net salary.
- [ ] Kiểm tra ownership với mọi identifier chi tiết, in hoặc tải xuống.
- [ ] Quyết định Approved có hiển thị trước Paid hay không và tài liệu hóa.
- [ ] Thêm test khác Employee và trạng thái bị ẩn.

### Task 12 - Xác minh

- [ ] Chạy test công thức, rounding, ngày hiệu lực, transition DAO, service transaction, permission API, batch và payslip.
- [ ] Chạy `mvn test`, `mvn -q compile` và `mvn -q package`.
- [ ] Tính lại regression fixture và so sánh từng thành phần.
- [ ] Thực hiện thủ công calculate -> Draft -> Pending -> Approved -> Paid -> Employee payslip.
- [ ] Thực hiện thủ công reject -> sửa -> submit lại.
- [ ] Xác minh chạy lại generate-all không tạo duplicate.

## 8. Tiêu chí hoàn thành

- Input và rule Payroll được snapshot và có thể tái hiện.
- Input Contract, Attendance và Leave hợp lệ theo kỳ và đến từ plan sở hữu tương ứng.
- Phép tính tiền là thao tác `BigDecimal` xác định với rounding đã test.
- Lifecycle transition có điều kiện, transaction và audit đầy đủ đến Paid.
- Batch operation idempotent và payslip Employee an toàn về ownership.
- Test, compile, package, regression fixture và lifecycle thủ công đầy đủ đều đạt.

