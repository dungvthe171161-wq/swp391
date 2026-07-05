# Tinh nang Guest: Lich phong van

Trang thai: Da cap nhat theo code ngay 2026-07-02.

## Pham vi
- HR/HR Staff quan ly lich phong van cua tung Application.
- Guest xem lich phong van sap toi trong dashboard va nhan notification/email khi lich thay doi.

## Da trien khai
- Route HR: `/hrstaff/interviews/schedule`.
- HR chon Application va tao lich phong van voi vong phong van, thoi gian, dia diem/link meeting, nguoi phong van, ghi chu.
- HR sua/doi lich bang `interviewId`; lich cap nhat sang `Rescheduled`.
- HR huy lich; `Interview.Status = Cancelled`.
- HR cap nhat ket qua Pass/Fail:
  - Pass: `Interview.Status = Completed`, `Interview.Result = Passed`, Application sang `Offered`.
  - Fail: `Interview.Status = Completed`, `Interview.Result = Failed`, Application sang `Rejected`.
- Moi thao tac dat lich/doi lich/huy/ket qua deu co co che gui email cho CandidateProfile.Email va tao notification cho Guest neu Guest co UserID.
- Guest dashboard lay lich that tu `InterviewDAO.findUpcomingByUserId`, khong con phu thuoc card tinh.

## Database
- Khong can tao bang moi cho phase nay.
- Tiep tuc dung bang `Interview`, `Application`, `CandidateProfile`, `Guest`, `Notification`.

## Con lai / nen lam tiep
- Nen gom tao/sua interview + update Application + notification vao workflow service co transaction.
- Nen them audit/log cho nguoi thuc hien thay doi lich.
- Nen test UI tren Tomcat voi data MySQL that sau deploy.
