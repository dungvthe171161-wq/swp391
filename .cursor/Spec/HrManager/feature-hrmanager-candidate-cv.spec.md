# Feature: HR Manager xem CV va ung vien
Status: Partial
Actor: HR Manager
Priority: Medium
Related Code: `ViewCV`, `Views/hr/ViewCV.jsp`, `GuestDAO`

## Route
- `GET /viewCV`
- `POST /viewCV`

## Luong chinh
1. HR Manager mo `/viewCV`.
2. Controller lay thong tin candidate/CV theo id.
3. Forward den `/Views/hr/ViewCV.jsp`.
4. HR Manager xem thong tin de ra quyet dinh tuyen dung.

## Hien trang code
- `RoleAuthorizationFilter` cho role 2 va role 4 vao `/viewCV`.
- JSP hien tai nam trong `/Views/hr/ViewCV.jsp`.
- Trang lien quan HR Manager phai dung tieng Viet va BetterHR theme.

## Acceptance Criteria
- [ ] HR Manager xem duoc CV hop le.
- [ ] Id candidate khong hop le hien thong bao loi.
- [ ] Chua login bi chuyen ve login.

## Missing Work
- [ ] Dinh nghia ro action approve/reject candidate neu HR Manager la nguoi quyet dinh.
- [ ] Tach view CV rieng cho HR Staff/HR Manager neu UI khac nhau.
- [ ] Chuan hoa message email gui ung vien neu approve/reject.
