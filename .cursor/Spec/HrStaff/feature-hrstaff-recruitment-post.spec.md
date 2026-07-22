# Tính năng HR Staff: Quản lý tin tuyển dụng

Trạng thái: Đã rà soát theo code ngày 2026-07-22.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- HR Staff tạo, xem danh sách và chỉnh sửa tin tuyển dụng.

## Route, controller và JSP liên quan
- `/hrstaff`, `/postRecruitments`, `/detailRecruitmentCreate`, `/detailRecruitment`.
- Route liên quan tuyển dụng khác: `/candidates`, `/viewCV`, `/hrstaff/interviews/schedule`.
- Controller: `HrStaffHomeController`, `PostRecruitmentController`, `DetailRecruitmentCreate`, `DetailRecruitment`, `ViewCandidateController`, `InterviewScheduleController`.
- JSP: `Views/HrStaff/PostRecruitment.jsp`, `Views/HrStaff/CreateNewRecruitment.jsp`, `Views/hr/DetailRecruitment.jsp`.

## Hiện trạng code
- `PostRecruitmentController` mapping `/postRecruitments`, kiểm tra role HR Staff và permission `VIEW_RECRUITMENT`.
- `DetailRecruitmentCreate` mapping `/detailRecruitmentCreate`, dùng `VIEW_RECRUITMENT`, forward form tạo tin và gọi `DAO.createRecruitment`.
- `DetailRecruitment` mapping `/detailRecruitment`, nằm package `com.hrm.controller.hr` nhưng guard hiện yêu cầu role HR Staff và permission `VIEW_RECRUITMENT`; dùng để xem/sửa tin từ `PostRecruitment.jsp`.
- Route detail vẫn là route legacy ngoài namespace `/hrstaff/*`.

## Quy tắc nghiệp vụ chuẩn
- Tạo/sửa tin tuyển dụng nên dùng permission ghi dữ liệu riêng: `CREATE_RECRUITMENT`, `EDIT_RECRUITMENT` hoặc permission tương ứng trong code.
- Recruitment phải validate vị trí, phòng ban, số lượng, mô tả, yêu cầu, lương và hạn tuyển nếu có.
- Không hiển thị tin đã xóa/đóng cho ứng viên public nếu nghiệp vụ không cho phép.
- Route HR Staff nên được chuẩn hóa dưới `/hrstaff/recruitments/*` khi refactor route.

## Code còn lệch spec hoặc cần bổ sung
- Tạo/sửa recruitment vẫn dùng chung `VIEW_RECRUITMENT`, chưa tách `CREATE_RECRUITMENT` và `EDIT_RECRUITMENT`.
- `/detailRecruitmentCreate` và `/detailRecruitment` vẫn là route legacy ngoài `/hrstaff/*`.
- `DetailRecruitment` dùng DAO chung, chưa có transaction/audit và còn `printStackTrace` khi lỗi.
- Cần test trạng thái recruitment đang mở/đóng và validate dữ liệu dài/rỗng nhất quán giữa create và edit.

## Kiểm thử tối thiểu
- HR Staff thiếu permission ghi không tạo/sửa được recruitment.
- GET `/postRecruitments`, `/detailRecruitmentCreate`, `/detailRecruitment?id=...` hiển thị đúng dữ liệu.
- POST create/edit với lương hoặc số lượng <= 0 bị chặn.
- User không phải HR Staff/Admin không truy cập được route legacy tuyển dụng.
