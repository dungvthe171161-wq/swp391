# Đặc tả module HR Manager: Quản lý nhân sự cấp quản lý

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- HR Manager xem tuyển dụng, quản lý employee và phê duyệt nghiệp vụ nhân sự.

## Route, controller và JSP liên quan
- `/HrHomeController`, `/viewRecruitment`, `/viewCV`, `/hr/employee-list`, `/hr/create-employee`.
- `/hr/approve-reject-contracts`, `/hr/payroll-approval`, `/hr/leaves` và các route `/hr/*`.
- Controller: `HrHomeController`, `ViewRecruitment`, `ViewCV`, `EmployeeListController`, `CreateEmployeeController`, `ApproveRejectContractController`, `PayrollApprovalController`.

## Hiện trạng code
- HR Manager dashboard dùng `/HrHomeController`.
- `ViewRecruitment` dùng permission `VIEW_RECRUITMENT`.
- `CreateEmployeeController` dùng permission `VIEW_EMPLOYEES` và xóa `Guest` sau khi tạo employee.
- `PayrollApprovalController` dùng permission `VIEW_USERS` dù thao tác là duyệt payroll.
- `ApproveRejectContractController` dùng `VIEW_CONTRACTS`.

## Quy tắc nghiệp vụ chuẩn
- HR Manager xem/tổng duyệt dữ liệu nhân sự theo quyền được cấp.
- Phê duyệt payroll, hợp đồng và tạo employee phải dùng permission đúng nghiệp vụ.
- Tạo employee từ ứng viên phải giữ lịch sử application/interview/offer.

## Ma trận route, quyền và dữ liệu cần bổ sung
| Nhóm chức năng | Route/controller | Permission chuẩn cần có | Bảng dữ liệu chính | Ghi chú |
|---|---|---|---|---|
| Dashboard HR Manager | `/HrHomeController`, `HrHomeController` | `VIEW_HR_DASHBOARD` hoặc permission dashboard tương đương | Employee, payroll, contract, recruitment | Không truy cập trực tiếp JSP nếu cần nạp số liệu. |
| Xem tuyển dụng | `/viewRecruitment`, `ViewRecruitment` | `VIEW_RECRUITMENT` | `Recruitment`, `Application` | Chỉ xem hoặc review, không trộn với action của HR Staff nếu chưa phân quyền. |
| Xem CV ứng viên | `/viewCV`, `ViewCV` | `VIEW_RECRUITMENT` hoặc `VIEW_APPLICATION_CV` | `Application`, `Guest`, `CandidateProfile` | Chuẩn mới phải nhận `ApplicationID`, không chỉ `guestId`. |
| Xem nhân viên | `/hr/employee-list`, `EmployeeListController` | `VIEW_EMPLOYEES` | `Employee`, `Department`, `Position` | Chỉ xem danh sách và chi tiết. |
| Tạo nhân viên từ ứng viên | `/hr/create-employee`, `CreateEmployeeController` | `CREATE_EMPLOYEE` | `Employee`, `SystemUser`, `Application`, `Guest` | Phải transaction và không xóa lịch sử Guest/Application. |
| Duyệt hợp đồng | `/hr/approve-reject-contracts`, `ApproveRejectContractController` | `APPROVE_CONTRACT` | `Contract` | Không dùng `VIEW_CONTRACTS` cho action approve/reject. |
| Duyệt payroll | Route trong `PayrollApprovalController` | `APPROVE_PAYROLL`, `VIEW_PAYROLLS` | `Payroll`, `PayrollDetail` nếu có | Không dùng `VIEW_USERS` cho payroll. |
| Duyệt nghỉ phép cấp HR | `/hr/leaves`, `LeaveApprovalController` | `APPROVE_LEAVE` hoặc quyền duyệt leave tương ứng | `LeaveRequest`, `Employee` | Cần ghi rõ quan hệ với Dept Manager duyệt leave. |

## Workflow phê duyệt cần bổ sung
- Payroll: chỉ payroll `Pending` mới được duyệt hoặc từ chối; duyệt chuyển sang `Approved`, từ chối chuyển sang `Rejected` và bắt buộc có lý do nếu nghiệp vụ yêu cầu.
- Contract: chỉ hợp đồng `Pending_Approval` mới được duyệt/từ chối; duyệt chuyển sang `Approved` hoặc `Active` theo rule code; từ chối phải lưu lý do.
- Create Employee: chỉ application đủ điều kiện tuyển thành công mới được tạo employee; thao tác phải khóa application/user liên quan để tránh tạo trùng.
- Recruitment review: HR Manager được xem và đánh giá, nhưng action nào thuộc HR Staff phải tách permission rõ.

## Notification và audit bắt buộc
- Duyệt/từ chối payroll tạo notification cho HR Staff phụ trách và employee nếu payroll đã được công bố.
- Duyệt/từ chối hợp đồng tạo notification cho HR Staff và employee liên quan.
- Tạo employee từ ứng viên tạo notification cho user được chuyển role và HR Staff phụ trách tuyển dụng.
- Mọi approve/reject/create employee phải ghi audit gồm trạng thái cũ, trạng thái mới, người thao tác, lý do và entity ID.

## Checklist nghiệm thu riêng cho HR Manager
- HR Manager vào được `/HrHomeController` nhưng HR Staff/Employee không vào được route `/hr/*` nếu không được cấp.
- User có `VIEW_PAYROLLS` nhưng không có `APPROVE_PAYROLL` không duyệt được payroll.
- User có `VIEW_CONTRACTS` nhưng không có `APPROVE_CONTRACT` không duyệt được hợp đồng.
- Tạo employee không xóa `Guest` và không làm mất `Application`, `Interview`, `Offer`.
- CV hiển thị đúng theo application đang review khi một Guest có nhiều application.

## Code còn lệch spec hoặc cần bổ sung
- Đổi create employee sang `CREATE_EMPLOYEE` và không xóa `Guest` nếu theo workflow mới.
- Đổi approve payroll sang quyền phê duyệt riêng, không dùng `VIEW_USERS`.
- Tách quyền duyệt hợp đồng khỏi `VIEW_CONTRACTS` nếu cần phân quyền chuẩn.
- Sửa `ViewCV` theo `ApplicationID`.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.
