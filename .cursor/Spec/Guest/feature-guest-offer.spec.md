# Tính năng Guest: Offer tuyển dụng

Trạng thái: Đã rà soát theo code ngày 2026-07-13.
Ngôn ngữ: tiếng Việt có dấu.

## Actor và phạm vi
- HR/HR Staff tạo và gửi offer cho ứng viên sau phỏng vấn.
- Guest nhận email + notification và phản hồi offer trong cổng ứng viên.

## Route, controller và JSP liên quan
- HR route: `/hrstaff/offers/manage`.
- Guest route: `/guest/applications`, POST `/guest/offer/respond`.
- Controller/DAO: `OfferManagementController`, `OfferDAO`, `GuestPortalController`.
- JSP: `Views/Guest/Applications.jsp`, `Views/HrStaff/ManageOffer.jsp`.

## Hiện trạng code
- HR mở form offer từ danh sách ứng viên hoặc trang lịch phỏng vấn.
- HR tạo/cập nhật bản nháp offer với vị trí, lương đề xuất, ngày bắt đầu dự kiến, hạn phản hồi và ghi chú.
- HR gửi offer:
  - `Offer.Status = Sent`.
  - `Application.Status = Offered`.
  - `Application.CurrentStep = Offer` (không dùng `Offered` cho `CurrentStep`).
  - Gửi email tới `CandidateProfile.Email`, fallback `Guest.Email`.
  - Tạo notification cho Guest nếu Guest có `UserID`.
- Guest thấy block `pendingOffers` và có nút Chấp nhận/Từ chối.
- Guest POST `/guest/offer/respond`:
  - Accepted: `Offer.Status = Accepted`, `Application.Status = Hired`, `CurrentStep = Hired`.
  - Rejected: `Offer.Status = Rejected`, `Application.Status = Rejected`, `CurrentStep = Rejected`.
- `OfferDAO.respondOffer` chỉ cho phép phản hồi offer thuộc đúng Guest và còn hạn.
- Sau khi Guest accept/reject, hệ thống gửi notification và email cho HR Staff + HR Manager.
- Màn hình HR tạo nhân viên chỉ hiện ứng viên có `Application = Hired` và `Offer = Accepted`.

## Quy tắc nghiệp vụ chuẩn
- Offer phải gắn `ApplicationID`; Guest chỉ phản hồi offer của chính mình.
- Phân biệt rõ `Application.Status` (`Offered`) và `Application.CurrentStep` (`Offer`).
- Accept offer không xóa `Guest`; HR tạo Employee sau và set `Guest.Status = Converted`.
- Nếu cần bước chờ onboarding, phải thêm enum/migration thay vì nhảy thẳng `Hired`.

## Code còn lệch spec hoặc cần bổ sung
- Chưa tự động tạo Employee/onboarding/contract; HR tạo Employee thủ công sau khi offer được chấp nhận.
- Accept offer nhảy thẳng `Application.Status = Hired`, chưa có trạng thái chờ tạo Employee riêng.
- `Offer` unique theo `ApplicationID`, chưa hỗ trợ nhiều offer/application.
- Nên thêm workflow service transaction cho send offer và respond offer.

## Kiểm thử tối thiểu
- Guest chỉ phản hồi offer thuộc user hiện tại và còn hạn.
- Sau gửi offer, kiểm tra `Application.Status = Offered` và `CurrentStep = Offer`.
- Accept/reject cập nhật đúng `Offer`, `Application` và gửi notification cho HR.
