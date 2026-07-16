# Tính năng Admin: Quản lý vị trí GPS văn phòng

Trạng thái: Đã rà soát theo code ngày 2026-07-14.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Admin cấu hình các vùng GPS được dùng để xác thực chấm công.
- Code hiện còn cho phép HR Manager và HR Staff; quyền chính thức cần được chốt bằng permission thay vì chỉ dựa vào role.

## Route, controller và JSP liên quan
- `GET /admin/office-location`, `POST /admin/office-location`.
- Controller: `OfficeLocationController`; DAO: `OfficeLocationDAO`; helper: `GeoUtil`.
- Entity: `OfficeLocation`; JSP: `Admin/office-location.jsp`.

## Hiện trạng code
- GET tải toàn bộ vị trí văn phòng.
- POST hỗ trợ `create`, `update`, `deactivate`.
- Server validate tên, latitude, longitude và bán kính dương.
- Guard hiện cho phép role Admin, HR Manager hoặc HR Staff.

## Quy tắc nghiệp vụ chuẩn
- Chỉ user có `VIEW_OFFICE_LOCATION` được xem và `MANAGE_OFFICE_LOCATION` được thay đổi.
- `LocationCode` phải duy nhất; tên, tọa độ và bán kính phải hợp lệ.
- Không xóa vật lý vị trí đã được dùng trong attendance; chỉ deactivate.
- Cần quy định cách chọn vị trí khi nhiều vùng GPS chồng lấn và hành vi khi không còn vị trí active.
- Mọi thay đổi tọa độ, bán kính và trạng thái phải được audit với old/new value.
- POST phải có CSRF token.

## Code còn lệch spec hoặc cần bổ sung
- Guard trong controller và `ModulePermissionFilter` chưa thống nhất: filter yêu cầu quyền xem nhưng controller dùng allowlist role.
- Chưa thấy kiểm tra trùng `LocationCode`, CSRF và audit.
- Chưa có giới hạn hợp lý cho `radiusMeters` và chính sách vị trí mặc định.
- Cần làm rõ HR Staff/HR Manager có thực sự được quyền cấu hình GPS hay không.

## Kiểm thử tối thiểu
- Kiểm tra riêng quyền xem và quyền quản lý cho từng role.
- Từ chối tọa độ ngoài biên, bán kính không hợp lệ, mã trùng và CSRF sai.
- Deactivate không làm mất lịch sử attendance.
- Attendance xác định đúng vùng khi có hai vị trí gần hoặc chồng lấn.
