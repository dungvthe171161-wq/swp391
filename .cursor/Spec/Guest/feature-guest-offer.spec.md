# Tinh nang Guest: Offer tuyen dung

Trang thai: Da cap nhat theo code ngay 2026-07-02.

## Pham vi
- HR/HR Staff tao va gui offer cho ung vien sau phong van.
- Guest nhan email + notification va phan hoi offer trong cong ung vien.

## Da trien khai
- HR route: `/hrstaff/offers/manage`.
- HR mo form offer tu danh sach ung vien hoac trang lich phong van.
- HR tao/cap nhat ban nhap offer voi:
  - Vi tri offer.
  - Luong de xuat.
  - Ngay bat dau du kien.
  - Han phan hoi.
  - Ghi chu.
- HR gui offer:
  - `Offer.Status = Sent`.
  - `Application.Status = Offered`.
  - `Application.CurrentStep = Offered`.
  - Gui email toi `CandidateProfile.Email`, fallback `Guest.Email`.
  - Tao notification cho Guest neu Guest co `UserID`.
- Guest route: `/guest/applications`.
- Guest thay block `pendingOffers` va co nut Chap nhan/Tu choi.
- Guest POST `/guest/offer/respond`:
  - Accepted: `Offer.Status = Accepted`, `Application.Status = Hired`, `CurrentStep = Hired`.
  - Rejected: `Offer.Status = Rejected`, `Application.Status = Rejected`, `CurrentStep = Rejected`.
- `OfferDAO.respondOffer` chi cho phep phan hoi offer thuoc dung Guest va con han.
- Sau khi Guest accept/reject, he thong gui notification va email cho HR Staff + HR Manager.
- Man hinh HR tao nhan vien chi hien ung vien co `Application = Hired` va `Offer = Accepted`.

## Database
- Khong can tao bang moi.
- Tiep tuc dung bang `Offer`, `Application`, `CandidateProfile`, `Guest`, `Notification`.

## Con lai / nen lam tiep
- Chua tu dong tao Employee/onboarding/contract; HR dang tao Employee thu cong sau khi offer duoc chap nhan.
- Nen them workflow service transaction cho send offer va respond offer neu mo rong tiep.
