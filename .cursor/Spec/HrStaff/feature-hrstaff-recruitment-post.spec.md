# Tính năng HR Staff: Quản lý tin tuyển dụng

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- HR Staff tạo và quản lý tin tuyển dụng.

## Route, controller và JSP liên quan
- `/hrstaff`, `/postRecruitments`, `/candidates`, `/viewCV`, `/hrstaff/interviews/schedule`.
- `/hrstaff/contracts`, `/hrstaff/contracts/create`, `/hrstaff/payroll`, `/hrstaff/payroll/*`, `/api/payroll`, `/api/allowance/*`, `/api/deduction/*`.
- Controller: `HrStaffHomeController`, `PostRecruitmentController`, `ViewCandidateController`, `InterviewScheduleController`, `ContractListController`, `CreateContractController`, `PayrollManagementController`.

## Hiện trạng code
- `PostRecruitmentController` mapping `/postRecruitments`.
- Controller kiểm tra role HR Staff và permission `VIEW_RECRUITMENT`.
- Một số route detail legacy dùng `/detailRecruitmentCreate`.

## Quy tắc nghiệp vụ chuẩn
- Tạo/sửa tin tuyển dụng nên dùng permission ghi dữ liệu riêng.
- Recruitment phải validate vị trí, phòng ban, số lượng, mô tả và hạn tuyển.
- Không hiển thị tin đã xóa cho ứng viên public.

## Code còn lệch spec hoặc cần bổ sung
- Cần thêm permission tạo/sửa recruitment nếu muốn phân quyền chuẩn.
- Cần test trạng thái recruitment đang mở/đóng.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

