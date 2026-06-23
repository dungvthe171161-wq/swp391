# Feature: HR Staff quan ly ung vien
Status: Approved
Actor: HR Staff
Priority: High
Related Code: `ViewCandidateController`, `ViewCV`, `GuestDAO`, `Views/HrStaff/ViewCandidate.jsp`, `Views/hr/ViewCV.jsp`

## Route
- `GET /candidates`
- `POST /candidates`
- `GET /viewCV`
- `POST /viewCV`

## Luong danh sach ung vien
1. HR Staff vao `/candidates`.
2. Controller lay danh sach guest/candidate.
3. Phan trang mac dinh theo code hien tai cua `ViewCandidateController`.
4. Forward den `/Views/HrStaff/ViewCandidate.jsp`.

## Luong cap nhat trang thai
1. HR Staff thao tac tren candidate.
2. `ViewCandidateController` xu ly action/status.
3. Redirect ve `/candidates` kem query success/error.

## Luong xem CV
1. HR Staff mo `/viewCV` voi id ung vien.
2. Controller lay thong tin CV.
3. Forward den JSP xem CV.

## Acceptance Criteria
- [ ] HR Staff xem duoc danh sach ung vien.
- [ ] Chua login bi redirect login.
- [ ] Cap nhat status redirect dung ve danh sach.
- [ ] Khong xem duoc CV neu thieu id hop le.

## Missing Work
- [ ] Dinh nghia status ung vien chuan: New, Reviewing, Interview, Passed, Rejected.
- [ ] Them luong chuyen candidate Passed thanh Employee neu can.
