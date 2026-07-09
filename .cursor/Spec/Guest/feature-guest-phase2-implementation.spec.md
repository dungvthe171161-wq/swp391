# Spec trien khai Guest Phase 2

Trang thai: Da cap nhat theo code ngay 2026-07-02.

## Da xong
- CandidateProfile cho Guest:
  - Luu ho so ung tuyen mot lan.
  - Xac nhan email bang ma trong session truoc khi luu profile.
  - Upload CV PDF/DOC/DOCX toi da 10MB.
- Application:
  - Tao Application lien ket Recruitment, Guest, CandidateProfile.
  - Chan ung tuyen trung mot Recruitment.
- Interview:
  - HR dat lich, doi lich, huy lich.
  - HR cap nhat Pass/Fail.
  - Gui email va notification cho Guest.
  - Guest dashboard hien lich phong van that.
- Offer:
  - HR tao/lueu nhap/gui offer.
  - Gui mail + notification cho Guest.
  - Guest accept/reject offer tren `/guest/applications`.
  - Accept cap nhat Application sang `Hired`.
  - Guest accept/reject se gui mail + notification nguoc lai cho HR Staff/HR Manager.
- Employee conversion:
  - Man hinh `/hr/create-employee` chi hien ung vien da pass luong offer: `Application = Hired` va `Offer = Accepted`.
  - Server-side cung chan tao Employee neu Guest chua co offer Accepted hop le.
  - Sau khi tao Employee, giu lai Guest/Application history va doi `Guest.Status = Converted`.

## Khong can them database o phase nay
- Da co va dang dung: `CandidateProfile`, `Application`, `Interview`, `Offer`, `Notification`.
- Khong can them Department/Benefit/Deadline vi thong tin nay da nam trong Recruitment hoac khong thuoc phase nay.

## Chua lam / phase tiep theo
- Workflow service transaction trung tam cho tat ca buoc tuyen dung.
- Tu dong tao Employee/onboarding/contract ngay khi Guest Accepted.
- Chuyen cac man hinh CV cu tu `GuestID` sang uu tien `ApplicationID/CandidateProfileID`.

## Verification
- `mvn -q compile`: pass ngay 2026-07-02.
- `mvn -q package`: pass ngay 2026-07-02.
- Can test lai tren Tomcat/MySQL that sau deploy de xac nhan mail config va upload/static path.
