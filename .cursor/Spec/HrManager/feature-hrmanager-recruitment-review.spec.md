# Feature: HR Manager xem va xu ly recruitment
Status: Approved
Actor: HR Manager
Priority: High
Related Code: `ViewRecruitment`, `DetailWaitingRecruitment`, `RecruitmentDAO`, `Views/hr/ViewRecruitment.jsp`, `Views/hr/DetailWaitingRecruitment.jsp`

## Route
- `GET /viewRecruitment`
- `POST /viewRecruitment`
- `GET /detailWaitingRecruitment`
- `POST /detailWaitingRecruitment`

## Luong danh sach
1. HR Manager vao `/viewRecruitment`.
2. Controller kiem tra permission `VIEW_RECRUITMENT`.
3. Lay danh sach recruitment.
4. Forward den `/Views/hr/ViewRecruitment.jsp`.
5. JSP hien thi tieng Viet va dung BetterHR theme.

## Luong chi tiet/phe duyet
1. HR Manager mo `/detailWaitingRecruitment?id=...`.
2. Controller lay recruitment theo id.
3. Forward den `/Views/hr/DetailWaitingRecruitment.jsp`.
4. Neu submit action, controller cap nhat recruitment theo logic hien co.

## Acceptance Criteria
- [ ] HR Manager co permission xem duoc recruitment.
- [ ] Thieu permission bi dua den AccessDenied.
- [ ] Id sai khong lam crash servlet.
- [ ] Nut quay lai/HR Home tro ve `/HrHomeController`.
- [ ] Khong loi font tieng Viet tren danh sach va chi tiet recruitment.

## Missing Work
- [ ] Chuan hoa status recruitment: Pending, Approved, Rejected, Closed.
- [ ] Ghi ly do tu choi neu reject.
- [ ] Chuan hoa logging thay vi `System.out`/`printStackTrace`.
