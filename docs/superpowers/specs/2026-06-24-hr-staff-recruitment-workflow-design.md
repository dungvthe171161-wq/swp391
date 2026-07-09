# Thiết kế workflow tuyển dụng cho HR Staff

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu.
Phạm vi: HR Staff, HR Manager, Guest và dữ liệu `CandidateProfile`, `Application`, `Interview`, `Offer`.

## Hiện trạng code
- `RecruitmentController.confirmApplication` tạo `Application` với `Status = Applied`, `CurrentStep = Applied`, lấy CV từ `CandidateProfile` và tạo notification cho ứng viên/HR Staff.
- `ViewCandidateController` và `PostRecruitmentController` hiện dùng permission `VIEW_RECRUITMENT`.
- `InterviewScheduleController` tạo `Interview`, cập nhật `Application` sang `Interview`, gửi email và notification.
- `ViewCV` hiện nhận `guestId`, không nhận `applicationId`; POST cập nhật `Guest.Status` thành `Hired` hoặc `Rejected`.
- `OfferDAO.respondOffer` cho phép `Accepted` hoặc `Rejected`; nếu accept thì cập nhật `Application.Status = Hired` và `CurrentStep = Hired`.
- `Offer` đang unique theo `ApplicationID`, nên code/schema hiện không hỗ trợ nhiều offer cho một application.

## Trạng thái được code hỗ trợ
- `Application.Status`: `Applied`, `Screening`, `Interview`, `Offered`, `Rejected`, `Withdrawn`, `Hired`.
- `Application.CurrentStep`: `Applied`, `Screening`, `Interview`, `Offer`, `Hired`, `Rejected`, `Withdrawn`.
- `Interview.Status`: `Scheduled`, `Completed`, `Cancelled`, `NoShow`, `Rescheduled`.
- `Interview.Result`: `Pending`, `Passed`, `Failed`.
- `Offer.Status`: `Draft`, `Sent`, `Accepted`, `Rejected`, `Expired`, `Cancelled`.

## Thiết kế chuẩn cần hướng tới
- `Application` là nguồn sự thật của từng lần ứng tuyển.
- HR xem CV theo `ApplicationID`, không theo `guestId`.
- Mọi chuyển trạng thái tuyển dụng đi qua service hoặc DAO có validate state machine.
- Tạo interview, cập nhật application, gửi notification phải chạy trong transaction hoặc có cơ chế bù lỗi rõ ràng.
- Accept offer không nên tự xóa Guest; nếu cần bước tạo Employee riêng thì phải bổ sung enum/migration/service tương ứng.
- Permission ghi dữ liệu cần tách khỏi quyền xem: quản lý applicant, tạo interview, gửi offer, tạo employee.

## Code còn lệch thiết kế
- Chưa có workflow service trung tâm.
- Chưa có enum `OfferAccepted`, `OfferDeclined` hoặc `InterviewScheduled`; spec không được mô tả các trạng thái này như đã tồn tại.
- `ViewCV` và một số luồng cũ vẫn dựa vào `Guest.Status`.
- `CreateEmployeeController` hiện dùng `VIEW_EMPLOYEES` và xóa `Guest` sau khi tạo employee.
- Chưa có guard rõ ràng để ngăn tạo nhiều interview nếu nghiệp vụ chỉ cho một vòng.

## Kiểm thử tối thiểu
- Nộp hồ sơ tạo đúng `Application`.
- HR Staff tạo lịch phỏng vấn cho đúng application và ứng viên nhận notification/email.
- Guest chỉ xem application, interview và offer của chính mình.
- Offer accept/reject chỉ tác động offer thuộc user hiện tại.
- Maven compile phải pass; deploy servlet phải được kiểm tra riêng vì compile không bắt lỗi mapping trùng.