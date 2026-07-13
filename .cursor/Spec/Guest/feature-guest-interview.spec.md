# Tính năng Guest: Lịch phỏng vấn

Trạng thái: Đã rà soát theo code ngày 2026-07-13.
Ngôn ngữ: tiếng Việt có dấu.

## Actor và phạm vi
- HR/HR Staff quản lý lịch phỏng vấn của từng `Application`.
- Guest xem lịch phỏng vấn sắp tới trong dashboard và nhận notification/email khi lịch thay đổi.

## Route, controller và JSP liên quan
- HR route: `/hrstaff/interviews/schedule`.
- Guest dashboard: `/guest/dashboard`.
- Controller/DAO: `InterviewScheduleController`, `InterviewDAO`, `GuestPortalController`.

## Hiện trạng code
- HR chọn `Application` và tạo lịch phỏng vấn với vòng phỏng vấn, thời gian, địa điểm/link meeting, người phỏng vấn, ghi chú.
- HR sửa/đổi lịch bằng `interviewId`; lịch cập nhật sang `Rescheduled`.
- HR hủy lịch: `Interview.Status = Cancelled`.
- HR cập nhật kết quả Pass/Fail:
  - Pass: `Interview.Status = Completed`, `Interview.Result = Passed`, Application sang `Offered`.
  - Fail: `Interview.Status = Completed`, `Interview.Result = Failed`, Application sang `Rejected`.
- Mỗi thao tác đặt lịch/đổi lịch/hủy/kết quả đều có cơ chế gửi email cho `CandidateProfile.Email` và tạo notification cho Guest nếu Guest có `UserID`.
- Guest dashboard lấy lịch thật từ `InterviewDAO.findUpcomingByUserId`, không còn phụ thuộc card tĩnh.

## Quy tắc nghiệp vụ chuẩn
- Interview phải tham chiếu `ApplicationID`.
- Guest chỉ xem lịch thuộc application của chính mình.
- Đổi/hủy lịch phải thông báo cho Guest kịp thời.

## Code còn lệch spec hoặc cần bổ sung
- Nên gom tạo/sửa interview + cập nhật `Application` + notification vào workflow service có transaction.
- Nên thêm audit/log cho người thực hiện thay đổi lịch.
- Nên test UI trên Tomcat với data MySQL thật sau deploy.

## Kiểm thử tối thiểu
- Guest A không xem được lịch phỏng vấn của Guest B.
- Pass/Fail cập nhật đúng `Interview` và `Application.Status`.
- Đổi/hủy lịch gửi email và notification cho Guest.
