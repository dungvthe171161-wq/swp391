# Đặc tả module HR Manager: Quản lý nhân sự cấp quản lý

Trạng thái: Đã rà soát theo code ngày 2026-07-22.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- HR Manager xem tuyển dụng, quản lý employee và phê duyệt nghiệp vụ nhân sự.

## Route, controller và JSP liên quan
- `/HrHomeController`, `/viewRecruitment`, `/detailWaitingRecruitment`, `/viewCV`, `/hr/employee-list`, `/hr/create-employee`.
- `/hr/approve-reject-contracts`, `/hr/payroll-approval`, `/hr/leaves` và các route `/hr/*`.
- Controller: `HrHomeController`, `ViewRecruitment`, `DetailWaitingRecruitment`, `ViewCV`, `EmployeeListController`, `CreateEmployeeController`, `ApproveRejectContractController`, `PayrollApprovalController`, `LeaveApprovalController`.
- JSP: `Views/hr/HrHome.jsp`, `ViewRecruitment.jsp`, `DetailWaitingRecruitment.jsp`, `ViewCV.jsp`, `EmployeeList.jsp`, `CreateEmployee.jsp`, `ApproveRejectContract.jsp`, `PayrollManagement.jsp`, `LeaveRequests.jsp`.

## Hiện trạng code
- HR Manager dashboard dùng `/HrHomeController`.
- `ViewRecruitment` mapping `/viewRecruitment` và dùng permission `VIEW_RECRUITMENT`.
- `DetailWaitingRecruitment` mapping `/detailWaitingRecruitment`, hiển thị/sửa recruitment đang chờ theo `id`; route được `RoleAuthorizationFilter` cho Admin/HR Manager nhưng controller chưa có guard `PermissionUtil` riêng.
- `CreateEmployeeController` dùng permission `VIEW_EMPLOYEES` và set `Guest.Status = Converted` sau khi tạo employee.
- `PayrollApprovalController` dùng permission `VIEW_USERS` dù thao tác là duyệt payroll.
- `ApproveRejectContractController` dùng `VIEW_CONTRACTS`.

## Quy tắc nghiệp vụ chuẩn
- HR Manager xem/tổng duyệt dữ liệu nhân sự theo quyền được cấp.
- Phê duyệt payroll, hợp đồng và tạo employee phải dùng permission đúng nghiệp vụ.
- Tạo employee từ ứng viên phải giữ lịch sử application/interview/offer.
- Route review recruitment của HR Manager nên dùng guard controller-level ngoài filter.

## Ma trận route, quyền và dữ liệu cần bổ sung
| Nhóm chức năng | Route/controller | Permission chuẩn cần có | Bảng dữ liệu chính | Ghi chú |
|---|---|---|---|---|
| Dashboard HR Manager | `/HrHomeController`, `HrHomeController` | `VIEW_HR_DASHBOARD` hoặc permission dashboard tương đương | Employee, payroll, contract, recruitment | Không truy cập trực tiếp JSP nếu cần nạp số liệu. |
| Xem tuyển dụng | `/viewRecruitment`, `ViewRecruitment` | `VIEW_RECRUITMENT` | `Recruitment`, `Application` | Xem danh sách tin tuyển dụng. |
| Xem/chỉnh tin chờ xử lý | `/detailWaitingRecruitment`, `DetailWaitingRecruitment` | `VIEW_RECRUITMENT`, `EDIT_RECRUITMENT` nếu cho sửa | `Recruitment` | Controller còn thiếu guard `PermissionUtil`; POST có thể sửa recruitment. |
| Xem CV ứng viên | `/viewCV`, `ViewCV` | `VIEW_RECRUITMENT` hoặc `VIEW_APPLICATION_CV` | `Application`, `Guest`, `CandidateProfile` | Ưu tiên `applicationId`; `guestId` chỉ fallback legacy. |
| Xem nhân viên | `/hr/employee-list`, `EmployeeListController` | `VIEW_EMPLOYEES` | `Employee`, `Department`, `Position` | Chỉ xem danh sách và chi tiết. |
| Tạo nhân viên từ ứng viên | `/hr/create-employee`, `CreateEmployeeController` | `CREATE_EMPLOYEE` | `Employee`, `SystemUser`, `Application`, `Guest` | Phải transaction và không xóa lịch sử Guest/Application. |
| Duyệt hợp đồng | `/hr/approve-reject-contracts`, `ApproveRejectContractController` | `APPROVE_CONTRACT` | `Contract` | Không dùng `VIEW_CONTRACTS` cho action approve/reject. |
| Duyệt payroll | Route trong `PayrollApprovalController` | `APPROVE_PAYROLL`, `VIEW_PAYROLLS` | `Payroll`, `PayrollDetail` nếu có | Không dùng `VIEW_USERS` cho payroll. |
| Duyệt nghỉ phép cấp HR | `/hr/leaves`, `LeaveApprovalController` | `APPROVE_LEAVE` hoặc quyền duyệt leave tương ứng | `MailRequest`, `Employee` | Cần ghi rõ quan hệ với Dept Manager duyệt leave. |

## Workflow phê duyệt cần bổ sung
- Payroll: chỉ payroll `Pending` mới được duyệt hoặc từ chối; duyệt chuyển sang `Approved`, từ chối chuyển sang `Rejected` và bắt buộc có lý do nếu nghiệp vụ yêu cầu.
- Contract: chỉ hợp đồng `Pending_Approval` mới được duyệt/từ chối; duyệt chuyển sang `Pending_Signature` hoặc `Approved` theo rule code; từ chối phải lưu lý do.
- Create Employee: chỉ application đủ điều kiện tuyển thành công mới được tạo employee; thao tác phải khóa application/user liên quan để tránh tạo trùng.
- Recruitment review: HR Manager được xem và đánh giá, nhưng action nào thuộc HR Staff phải tách permission rõ.

## Notification và audit bắt buộc
- Duyệt/từ chối payroll tạo notification cho HR Staff phụ trách và employee nếu payroll đã được công bố.
- Duyệt/từ chối hợp đồng tạo notification cho HR Staff và employee liên quan.
- Tạo employee từ ứng viên tạo notification cho user được chuyển role và HR Staff phụ trách tuyển dụng.
- Mọi approve/reject/create employee phải ghi audit gồm trạng thái cũ, trạng thái mới, người thao tác, lý do và entity ID.

## Code còn lệch spec hoặc cần bổ sung
- `DetailWaitingRecruitment` chưa có guard `PermissionUtil`, chưa có audit và còn `System.out`/`printStackTrace`.
- Đổi create employee sang `CREATE_EMPLOYEE`; thêm transaction và audit.
- Đổi approve payroll sang quyền `APPROVE_PAYROLL`, không dùng `VIEW_USERS`.
- Tách quyền duyệt hợp đồng sang `APPROVE_CONTRACT`, không chỉ `VIEW_CONTRACTS`.
- `ViewCV` fallback `guestId` và POST legacy cập nhật `Guest.Status` vẫn còn.

## Kiểm thử tối thiểu
- HR Manager vào được `/HrHomeController`, `/viewRecruitment`, `/detailWaitingRecruitment?id=...`; HR Staff/Employee không vào được route HR Manager nếu không được cấp.
- User có `VIEW_PAYROLLS` nhưng không có `APPROVE_PAYROLL` không duyệt được payroll sau khi hardening.
- User có `VIEW_CONTRACTS` nhưng không có `APPROVE_CONTRACT` không duyệt được hợp đồng sau khi hardening.
- Tạo employee không xóa `Guest` và không làm mất `Application`, `Interview`, `Offer`.
- CV hiển thị đúng theo application đang review khi một Guest có nhiều application.
