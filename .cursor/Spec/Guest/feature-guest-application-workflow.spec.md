# Tính năng Guest: Luồng ứng tuyển

Trạng thái: Đã rà soát theo code ngày 2026-07-13.
Ngôn ngữ: tiếng Việt có dấu.

## Actor và phạm vi
- Guest và HR Staff/HR Manager tham gia luồng ứng tuyển từ nộp hồ sơ đến tuyển thành công.

## Route, controller và JSP liên quan
- Nộp hồ sơ: `RecruitmentController`, `/guest/profile`.
- HR xem ứng viên: `/candidates`, `/viewCV`.
- Phỏng vấn: `/hrstaff/interviews/schedule`.
- Offer: `/hrstaff/offers/manage`, `/guest/applications`, `/guest/offer/respond`.
- Tạo nhân viên: `/hr/create-employee`, `CreateEmployeeController`.

## Hiện trạng code
1. Guest nộp hồ sơ vào `Recruitment`.
2. Hệ thống tạo `Application` gắn `Guest`, `CandidateProfile`, `Recruitment`.
3. HR xem danh sách ứng viên tại `/candidates`.
4. HR đặt lịch phỏng vấn tại `/hrstaff/interviews/schedule`.
5. HR có thể đổi lịch, hủy lịch, cập nhật kết quả Pass/Fail.
6. Pass chuyển `Application.Status` sang `Offered`; Fail chuyển sang `Rejected`.
7. HR tạo/gửi offer tại `/hrstaff/offers/manage`; khi gửi, `CurrentStep = Offer`.
8. Guest xem offer tại `/guest/applications`.
9. Guest accept/reject offer.
10. Accept chuyển `Application.Status` sang `Hired`; Reject chuyển sang `Rejected`.
11. HR tạo Employee tại `/hr/create-employee`; `Guest.Status` chuyển `Converted`, không xóa `Guest`.

## Quy tắc nghiệp vụ chuẩn
- `Application` là nguồn sự thật cho từng lần ứng tuyển.
- Trạng thái `Application.Status`: `Applied`, `Screening`, `Interview`, `Offered`, `Hired`, `Rejected`, `Withdrawn`.
- Trạng thái `Application.CurrentStep`: `Applied`, `Screening`, `Interview`, `Offer`, `Hired`, `Rejected`, `Withdrawn`.
- Không cập nhật `Guest.Status` thay cho workflow mới trên `Application`.

## Code còn lệch spec hoặc cần bổ sung
- Chưa có workflow service trung tâm để validate chuyển trạng thái và gom transaction.
- Accept offer nhảy thẳng `Hired`, chưa có bước chờ onboarding riêng.
- `ViewCV` fallback legacy vẫn cập nhật `Guest.Status` khi POST theo `guestId`.
- Phase sau: tự động tạo Employee/onboarding/contract từ `Application` khi đủ điều kiện.

## Kiểm thử tối thiểu
- Một Guest có nhiều `Application` vẫn theo dõi đúng timeline từng lần ứng tuyển.
- Nộp trùng cùng một `Recruitment` bị chặn hoặc xử lý theo rule đã định.
- Sau accept offer, HR chỉ tạo Employee khi `Application = Hired` và `Offer = Accepted`.
