# Tính năng HR Manager: Xem và xử lý tuyển dụng

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- HR Manager xem danh sách recruitment/application và trạng thái tuyển dụng.

## Route, controller và JSP liên quan
- `/HrHomeController`, `/viewRecruitment`, `/viewCV`, `/hr/employee-list`, `/hr/create-employee`.
- `/hr/approve-reject-contracts`, `/hr/payroll-approval`, `/hr/leaves` và các route `/hr/*`.
- Controller: `HrHomeController`, `ViewRecruitment`, `ViewCV`, `EmployeeListController`, `CreateEmployeeController`, `ApproveRejectContractController`, `PayrollApprovalController`.

## Hiện trạng code
- `ViewRecruitment` mapping `/viewRecruitment` và dùng `VIEW_RECRUITMENT`.
- Route cũng nằm trong pattern cho HR Manager/Admin.
- Một số route candidate/CV dùng chung với HR Staff.

## Quy tắc nghiệp vụ chuẩn
- HR Manager chỉ thực hiện hành động được phân quyền.
- Xem application phải theo `ApplicationID` khi có nhiều lần ứng tuyển.
- Không cập nhật `Guest.Status` thay cho application.

## Code còn lệch spec hoặc cần bổ sung
- Cần tách rõ quyền HR Manager và HR Staff trên `/candidates`/`/viewCV`.
- Cần chuyển CV sang application-based.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

