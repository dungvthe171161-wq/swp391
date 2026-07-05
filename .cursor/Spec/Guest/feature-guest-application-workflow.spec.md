# Tinh nang Guest: Luong ung tuyen

Trang thai: Da cap nhat theo code ngay 2026-07-02.

## Luong hien tai
1. Guest nop ho so vao Recruitment.
2. He thong tao `Application` gan `Guest`, `CandidateProfile`, `Recruitment`.
3. HR xem danh sach ung vien tai `/candidates`.
4. HR dat lich phong van tai `/hrstaff/interviews/schedule`.
5. HR co the doi lich, huy lich, cap nhat ket qua Pass/Fail.
6. Pass chuyen Application sang `Offered`; Fail chuyen sang `Rejected`.
7. HR tao/gui offer tai `/hrstaff/offers/manage`.
8. Guest xem offer tai `/guest/applications`.
9. Guest accept/reject offer.
10. Accept chuyen Application sang `Hired`; Reject chuyen sang `Rejected`.

## Trang thai Application dang dung
- `Applied`
- `Screening`
- `Interview`
- `Offered`
- `Hired`
- `Rejected`
- `Withdrawn`

## Database
- Khong can them database cho Interview + Offer phase hien tai.
- Da tan dung: `Application`, `CandidateProfile`, `Interview`, `Offer`, `Notification`.

## Con lai / nen lam tiep
- Nen tao workflow service trung tam de validate chuyen trang thai va gom transaction.
- Nen bo dan cac luong cu dua tren `Guest.Status` khi quan ly tuyen dung.
- Phase sau: khi `Hired`, HR co the tao Employee/onboarding/contract tu Application.
