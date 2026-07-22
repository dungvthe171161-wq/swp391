# Feature Tree 03 - Kế hoạch triển khai Cổng tự phục vụ của Nhân viên

- **Trạng thái:** Bản nháp
- **Phạm vi chuẩn:** Feature Tree 03
- **Phụ thuộc:** Plan 01, 06, 07, 08 và domain Task của Plan 04
- **Role:** Employee

## 1. Mục tiêu

Cung cấp một Employee Portal an toàn về ownership cho dashboard, hồ sơ cá nhân, lịch làm việc, chấm công, nghỉ phép, task được giao, hợp đồng, payroll/payslip và notification.

## 2. Ranh giới phạm vi

Plan này chịu trách nhiệm:

- Route, trang, kiểm tra ownership và cách trình bày tích hợp nhiều module cho Employee.
- Chỉnh sửa các trường hồ sơ cá nhân được phép.
- Tổng hợp dữ liệu domain vào dashboard Employee.

Plan này sử dụng thay vì triển khai lại:

- Quy tắc Task từ Plan 04.
- Quy tắc duyệt/ký Contract từ Plan 06.
- Công thức và lifecycle Payroll từ Plan 07.
- Quy tắc Schedule, Attendance và Leave từ Plan 08.
- Hạ tầng Notification từ Plan 10.

## 3. Code hiện tại cần audit và tái sử dụng

- `src/main/java/com/hrm/controller/employee/EmployeePortalController.java`
- `src/main/java/com/hrm/controller/employee/ViewTask.java`
- `ProfilepageController` cũ và `Views/Profilepage.jsp`
- `EmployeeDAO`, `AttendanceDAO`, `WorkScheduleDAO`, `MailRequestDAO`, `TaskDAO`
- `ContractDAO`, `ContractDocumentDAO`, `PayrollDAO`, `OfficeLocationDAO`
- `Views/Employee/EmployeeHome.jsp`, `EmployeeProfile.jsp`, `Attendance.jsp`
- `Views/Employee/Schedule.jsp`, `Leaves.jsp`, `Tasks.jsp`, `ViewTask.jsp`
- `Views/Employee/Contract.jsp`, `Payroll.jsp`
- Các include sidebar, topbar và styles dùng chung cho Employee

## 4. Các route lúc chạy hiện có

```text
/employee                     -> dashboard
/employee/profile             -> hồ sơ của chính mình
/employee/attendance          -> chấm công và check-in/out của chính mình
/employee/schedule            -> lịch làm việc của chính mình
/employee/leaves              -> đơn nghỉ của chính mình
/employee/tasks               -> task được giao cho chính mình
/employee/contract            -> hợp đồng và chữ ký của chính mình
/employee/contract/document   -> tải tài liệu đã được phân quyền
/employee/payroll             -> payslip của chính mình
```

## 5. Các bất biến ownership

- `SystemUser` đã xác thực được ánh xạ tới đúng một ngữ cảnh Employee trước mọi truy vấn domain.
- EmployeeID được suy ra từ session/account mapping, không tin giá trị từ request parameter.
- Employee có thể sửa trường liên hệ/avatar cá nhân nhưng không được sửa department, position, employment status, salary hoặc role.
- Mọi truy vấn Task, Leave, Schedule, Attendance, Contract, Document và Payroll đều phải kèm điều kiện ownership Employee.
- Lỗi của một widget không được làm lộ dữ liệu của nhân viên khác.
- Hành động phía Employee gọi domain service và không triển khai lặp lại business transition.

## 6. Các task triển khai

### Task 1 - Hợp nhất route và ngữ cảnh Employee

- [ ] Kiểm kê `/employee/*`, `/employee/view-task`, route profile cũ và route Task cũ.
- [ ] Chọn `EmployeePortalController` làm điểm vào chuẩn của portal.
- [ ] Triển khai một method dùng lại để ánh xạ người dùng đã xác thực sang ngữ cảnh Employee active.
- [ ] Trả về nhất quán chuyển hướng đăng nhập, access denied hoặc trạng thái tài khoản chưa liên kết.
- [ ] Thêm redirect tương thích cho route cũ vẫn còn được liên kết.
- [ ] Thêm test cho thiếu session, sai role, Employee inactive và thiếu Employee mapping.

### Task 2 - Xây dashboard tổng hợp có khả năng chịu lỗi

- [ ] Định nghĩa widget cho Attendance, lịch làm việc tiếp theo, Task đang mở, số ngày phép, Contract mới nhất, Payroll mới nhất và Notification.
- [ ] Dùng truy vấn DAO/service chuyên biệt thay vì load collection không giới hạn.
- [ ] Định nghĩa trạng thái rỗng cho từng widget.
- [ ] Giới hạn mọi truy vấn widget theo EmployeeID đã xác định.
- [ ] Quyết định widget lỗi sẽ hiển thị trạng thái không khả dụng hay làm lỗi toàn trang; ghi log an toàn.
- [ ] Test nhân viên mới chưa có dữ liệu liên quan và nhân viên có dữ liệu đầy đủ.

### Task 3 - Xem và cập nhật hồ sơ cá nhân

- [ ] Kiểm kê trường Employee được phép chỉnh sửa và trường do HR quản lý.
- [ ] Chỉ cho phép trường cá nhân đã duyệt như phone, địa chỉ/thông tin liên hệ và avatar.
- [ ] Từ chối hoặc bỏ qua department, position, salary, role và employment-status gửi từ browser.
- [ ] Kiểm tra phone, ngày, độ dài địa chỉ và ràng buộc avatar ở backend.
- [ ] Lưu avatar upload bằng đường dẫn an toàn được cấu hình và tên file do server sinh.
- [ ] Giải quyết phần trùng với `ProfilepageController` để chỉ còn một luồng cập nhật hồ sơ Employee chuẩn.
- [ ] Thêm test mass-assignment và cập nhật trường không được phép.

### Task 4 - Tích hợp Schedule và Attendance

- [ ] Chỉ render Schedule được gán cho Employee hiện tại.
- [ ] Dùng OfficeLocation active và quy tắc Schedule từ Plan 08.
- [ ] Gửi GPS check-in/check-out qua Attendance service của Plan 08.
- [ ] Hiển thị trạng thái hôm nay, tổng hợp tháng và các bản ghi gần đây.
- [ ] Hiển thị thông báo có thể xử lý cho trường hợp không có lịch, ngoài bán kính, thao tác trùng và tọa độ không hợp lệ.
- [ ] Thêm test UI/controller; giữ test nghiệp vụ khoảng cách/thời gian trong Plan 08.

### Task 5 - Tích hợp tự phục vụ Leave

- [ ] Hiển thị số ngày phép và lịch sử Leave của Employee hiện tại.
- [ ] Kiểm tra loại nghỉ, chi tiết, khoảng ngày, lý do và thông tin bàn giao bắt buộc.
- [ ] Gửi yêu cầu tạo qua Leave service của Plan 08.
- [ ] Không nhận requester, approver hoặc status do Employee gửi lên.
- [ ] Hiển thị Pending/Approved/Rejected và lý do từ chối.
- [ ] Quyết định có cho phép hủy đơn hay không và chuyển qua state service nếu nằm trong phạm vi.
- [ ] Test ownership và hành vi Post/Redirect/Get.

### Task 6 - Xem và cập nhật Task được giao

- [ ] Loại bỏ hoặc redirect hành vi Employee không an toàn của `/viewTask` cũ.
- [ ] Kiểm tra ownership assignment trước khi list, xem chi tiết hoặc cập nhật.
- [ ] Hiển thị Task, hạn hoàn thành, manager, trạng thái theo assignment và hành động khả dụng.
- [ ] Chỉ cho phép transition được định nghĩa bởi workflow Task của Plan 04.
- [ ] Ngăn một assignee thay đổi trạng thái của assignee khác.
- [ ] Hiển thị kết quả manager từ chối/duyệt khi đã triển khai.
- [ ] Thêm test xung đột route, ownership, transition không hợp lệ và double-submit.

### Task 7 - Tích hợp Contract và Document

- [ ] Chỉ load Contract hiện tại và lịch sử được phép của Employee.
- [ ] Kiểm tra ownership trước khi stream Document.
- [ ] Chuyển validation chữ ký và state transition cho Plan 06.
- [ ] Ngăn ký Contract bị từ chối, hết hạn, đã active hoặc thuộc Employee khác.
- [ ] Hiển thị an toàn thời gian ký và trạng thái Contract.
- [ ] Thêm test ownership ở cấp controller; giữ test hashing/transaction trong Plan 06.

### Task 8 - Tích hợp Payroll và payslip

- [ ] Chỉ load Payroll thuộc Employee hiện tại.
- [ ] Quyết định trạng thái lifecycle được hiển thị; thông thường chỉ Approved và Paid.
- [ ] Hiển thị kỳ lương, snapshot lương cơ bản, phụ cấp, khấu trừ, thuế, bảo hiểm và lương thực nhận.
- [ ] Không tính lại Payroll trong Employee controller hoặc JSP.
- [ ] Chỉ cung cấp in/tải xuống khi không thể bỏ qua kiểm tra ownership.
- [ ] Thêm test PayrollID của Employee khác và bản ghi Draft/Pending bị ẩn.

### Task 9 - Điều hướng portal, Notification và accessibility

- [ ] Dùng sidebar, topbar và styles Employee dùng chung cho mọi section.
- [ ] Giữ active navigation nhất quán.
- [ ] Inject dữ liệu notification bell qua Plan 10 thay vì truy vấn trong từng JSP.
- [ ] Thêm trạng thái rỗng, thành công và lỗi rõ ràng, không phụ thuộc loading phía client.
- [ ] Bảo đảm form có label, tổng hợp validation backend và hành động dùng được bằng bàn phím.
- [ ] Kiểm tra nội dung tiếng Việt UTF-8 và layout responsive.

### Task 10 - Xác minh

- [ ] Thêm test route và ownership cho Employee Portal.
- [ ] Chạy các integration suite liên quan của Plan 04/06/07/08.
- [ ] Chạy `mvn test`, `mvn -q compile` và `mvn -q package`.
- [ ] Kiểm thử thủ công mọi route Employee với một nhân viên đầy đủ dữ liệu và một nhân viên mới.
- [ ] Thử cho Employee A truy cập Task, Leave, Contract Document và Payroll của Employee B.
- [ ] Xác nhận không hành động Employee nào nhận ID hoặc status mang quyền từ browser.

## 7. Tiêu chí hoàn thành

- `/employee/*` là portal chuẩn và không còn route xung đột thiếu an toàn.
- Mọi trang và hành động đều suy ra ownership Employee từ ngữ cảnh xác thực.
- Chỉnh sửa hồ sơ không thể thay đổi trường do HR quản lý.
- Schedule, Attendance, Leave, Task, Contract và Payroll chuyển nghiệp vụ cho plan sở hữu tương ứng.
- Employee A không thể xem hoặc cập nhật dữ liệu của Employee B.
- Test, compile, package, responsive UI và kiểm tra portal thủ công đều đạt.

