# Tính năng HR Manager: Xem và xử lý tuyển dụng

Trạng thái: Đã rà soát theo code ngày 2026-07-22.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- HR Manager xem danh sách recruitment/application và xử lý tin tuyển dụng chờ review.

## Route, controller và JSP liên quan
- `/viewRecruitment`, `/detailWaitingRecruitment`, `/viewCV`.
- Controller: `ViewRecruitment`, `DetailWaitingRecruitment`, `ViewCV`.
- JSP: `Views/hr/ViewRecruitment.jsp`, `Views/hr/DetailWaitingRecruitment.jsp`, `Views/hr/ViewCV.jsp`.

## Hiện trạng code
- `ViewRecruitment` mapping `/viewRecruitment` và dùng `VIEW_RECRUITMENT`.
- `DetailWaitingRecruitment` mapping `/detailWaitingRecruitment`; GET đọc `id`, tải `Recruitment`; POST cập nhật recruitment rồi redirect lại cùng route.
- `RoleAuthorizationFilter` cho Admin/HR Manager vào `/detailWaitingRecruitment`; controller này chưa gọi `PermissionUtil`.
- Một số route candidate/CV dùng chung với HR Staff.
- `ViewCV` ưu tiên `applicationId`; `guestId` chỉ còn fallback legacy.

## Quy tắc nghiệp vụ chuẩn
- HR Manager chỉ thực hiện hành động được phân quyền.
- Xem application phải theo `ApplicationID` khi có nhiều lần ứng tuyển.
- Không cập nhật `Guest.Status` thay cho application trong workflow mới.
- Action sửa recruitment từ HR Manager phải có permission rõ và audit.

## Code còn lệch spec hoặc cần bổ sung
- `DetailWaitingRecruitment` thiếu controller-level guard `PermissionUtil`, audit và xử lý lỗi thống nhất.
- Cần tách rõ quyền HR Manager và HR Staff trên `/candidates`/`/viewCV`.
- `ViewCV` vẫn còn fallback `guestId` và POST legacy cập nhật `Guest.Status`.
- Validation create/edit recruitment chưa thống nhất giữa HR Staff và HR Manager.

## Kiểm thử tối thiểu
- HR Manager mở `/viewRecruitment`, `/detailWaitingRecruitment?id=...`, `/viewCV?applicationId=...` thành công.
- Employee/Guest không truy cập được các route HR Manager.
- Sửa recruitment với dữ liệu sai bị chặn và không mất dữ liệu đang nhập.
- CV hiển thị đúng application khi một Guest có nhiều application.
