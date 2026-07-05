# Tính năng HR Manager: Xem CV ứng viên

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- HR Manager xem hồ sơ và CV ứng viên để review tuyển dụng.

## Route, controller và JSP liên quan
- `/HrHomeController`, `/viewRecruitment`, `/viewCV`, `/hr/employee-list`, `/hr/create-employee`.
- `/hr/approve-reject-contracts`, `/hr/payroll-approval`, `/hr/leaves` và các route `/hr/*`.
- Controller: `HrHomeController`, `ViewRecruitment`, `ViewCV`, `EmployeeListController`, `CreateEmployeeController`, `ApproveRejectContractController`, `PayrollApprovalController`.

## Hiện trạng code
- `ViewCV` mapping `/viewCV` và dùng `VIEW_RECRUITMENT`.
- GET hiện nhận `guestId` và enrich từ latest application/profile.
- POST cập nhật `Guest.Status` thành Hired/Rejected.

## Quy tắc nghiệp vụ chuẩn
- CV phải thuộc đúng application đang review.
- Trạng thái tuyển dụng phải cập nhật `Application`, không cập nhật `Guest` legacy.
- HR Manager phải có quyền review phù hợp.

## Code còn lệch spec hoặc cần bổ sung
- Sửa tham số từ `guestId` sang `applicationId` hoặc kiểm tra application rõ ràng.
- Bỏ cập nhật `Guest.Status` cho workflow mới.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

