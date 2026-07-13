# Đặc tả module HR Staff: Vận hành nhân sự

Trạng thái: Đã rà soát theo code ngày 2026-07-13.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- HR Staff xử lý tuyển dụng, ứng viên, hợp đồng và payroll theo quyền.

## Route, controller và JSP liên quan
- `/hrstaff`, `/postRecruitments`, `/candidates`, `/viewCV`, `/hrstaff/interviews/schedule`.
- `/hrstaff/contracts`, `/hrstaff/contracts/create`, `/hrstaff/payroll`, `/hrstaff/payroll/*`, `/api/payroll`, `/api/allowance/*`, `/api/deduction/*`.
- Controller: `HrStaffHomeController`, `PostRecruitmentController`, `ViewCandidateController`, `InterviewScheduleController`, `ContractListController`, `CreateContractController`, `PayrollManagementController`.

## Hiện trạng code
- HR Staff dashboard dùng `/hrstaff`.
- Recruitment post và candidate hiện dùng permission `VIEW_RECRUITMENT`.
- Payroll HR Staff dùng permission `VIEW_PAYROLLS`.
- Interview schedule cập nhật application sang `Interview` và gửi email/notification.

## Quy tắc nghiệp vụ chuẩn
- HR Staff được tạo/sửa tin tuyển dụng, quản lý ứng viên, lên lịch phỏng vấn, quản lý payroll/contract theo quyền được cấp.
- Thao tác ghi dữ liệu nên có permission riêng, không chỉ dùng quyền xem.
- Workflow tuyển dụng phải thao tác theo `ApplicationID`.

## Ma trận route, quyền và dữ liệu cần bổ sung
| Nhóm chức năng | Route/controller | Permission chuẩn cần có | Bảng dữ liệu chính | Ghi chú |
|---|---|---|---|---|
| Dashboard HR Staff | `/hrstaff`, `HrStaffHomeController` | `VIEW_HRSTAFF_DASHBOARD` | Recruitment, Application, Payroll, Contract | Hiển thị số liệu vận hành. |
| Quản lý tin tuyển dụng | `/postRecruitments`, `PostRecruitmentController` | `VIEW_RECRUITMENT`, `CREATE_RECRUITMENT`, `UPDATE_RECRUITMENT`, `DELETE_RECRUITMENT` | `Recruitment` | Action ghi không dùng chung `VIEW_RECRUITMENT`. |
| Quản lý ứng viên | `/candidates`, `ViewCandidateController` | `MANAGE_APPLICANTS` hoặc `VIEW_APPLICATIONS` | `Application`, `Guest`, `CandidateProfile` | Dữ liệu phải theo `ApplicationID`. |
| Xem CV | `/viewCV`, `ViewCV` | `VIEW_APPLICATION_CV` | `Application`, `CandidateProfile` | Ưu tiên `applicationId`; `guestId` chỉ fallback legacy. |
| Lên lịch phỏng vấn | `/hrstaff/interviews/schedule`, `InterviewScheduleController` | `SCHEDULE_INTERVIEW` | `Interview`, `Application`, `Notification` | Cần transaction và chống tạo lịch trùng. |
| Quản lý hợp đồng | `/hrstaff/contracts`, `/hrstaff/contracts/create` | `VIEW_CONTRACTS`, `CREATE_CONTRACT`, `UPDATE_CONTRACT` | `Contract`, `Employee` | HR Staff tạo/push duyệt, HR Manager phê duyệt. |
| Quản lý payroll | `/hrstaff/payroll*`, `/api/payroll` | `VIEW_PAYROLLS`, `CREATE_PAYROLL`, `UPDATE_PAYROLL`, `SUBMIT_PAYROLL`, `DELETE_PAYROLL` | `Payroll`, allowance, deduction | Batch action phải kiểm tra quyền từng action. |
| Phụ cấp/khấu trừ | `/api/allowance/*`, `/api/deduction/*` | `MANAGE_PAYROLL_ADJUSTMENTS` | Allowance, Deduction hoặc bảng tương ứng | JSON lỗi phải thống nhất. |

## Workflow tuyển dụng cần bổ sung
- Application mới bắt đầu ở `Applied`; mọi action HR Staff phải thao tác trên `ApplicationID`.
- Screening/interview/reject/offer phải có state transition rõ theo enum database hiện có hoặc kèm migration nếu thêm enum mới.
- Lên lịch phỏng vấn phải kiểm tra application thuộc trạng thái hợp lệ, chưa có lịch trùng nếu nghiệp vụ chỉ cho một vòng.
- Reject ứng viên phải có lý do và notification cho Guest.
- Nếu tạo offer, cần phân biệt rõ `Offer.Status` với `Application.Status`; không cập nhật `Guest.Status` cho workflow mới.

## Workflow payroll và contract cần bổ sung
- Payroll HR Staff được tạo/tính/sửa khi còn `Draft` hoặc trạng thái cho phép; submit chuyển sang `Pending`.
- Payroll đã gửi duyệt không được xóa/sửa nếu không có quyền hoặc rule rollback.
- Contract HR Staff tạo ở `Draft` hoặc `Pending_Approval`, không tự duyệt nếu nghiệp vụ yêu cầu HR Manager.
- Allowance/deduction phải validate số tiền, kỳ lương và employee; không cho số âm nếu không có rule riêng.

## Notification và audit bắt buộc
- Nộp application, bắt đầu screening, lên lịch phỏng vấn, reject, gửi offer phải phát notification đúng actor.
- Tạo/sửa recruitment, đổi trạng thái application, tạo interview, tạo contract, submit payroll phải ghi audit.
- Email phỏng vấn hoặc offer chỉ gửi sau khi dữ liệu chính đã ghi thành công; lỗi email phải được log hoặc đưa vào retry, không làm trạng thái bị lệch.

## Checklist nghiệm thu riêng cho HR Staff
- HR Staff thiếu quyền ghi không tạo/sửa/xóa recruitment, payroll hoặc contract được.
- `/candidates` và `/viewCV` hiển thị đúng application khi một Guest có nhiều application.
- Tạo interview trùng cho cùng application bị chặn nếu spec chỉ cho một lịch.
- Submit payroll chuyển đúng trạng thái và HR Manager nhìn thấy bản ghi chờ duyệt.
- API allowance/deduction trả JSON lỗi tiếng Việt khi dữ liệu không hợp lệ.

## Code còn lệch spec hoặc cần bổ sung
- Recruitment/candidate/interview/offer còn dùng chung `VIEW_RECRUITMENT`; cần tách `MANAGE_APPLICANTS`, `SCHEDULE_INTERVIEW`, `CREATE_RECRUITMENT`.
- Payroll HR Staff dùng `VIEW_PAYROLLS`; contract dùng `VIEW_CONTRACTS`; chưa tách quyền action ghi/duyệt.
- `ViewCV` fallback `guestId` và POST legacy cập nhật `Guest.Status` vẫn còn.
- Interview schedule chưa có transaction chung và guard chống lịch trùng theo application.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.
