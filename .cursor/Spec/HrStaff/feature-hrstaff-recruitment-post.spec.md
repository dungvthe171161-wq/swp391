# Feature: HR Staff quan ly tin tuyen dung
Status: Approved
Actor: HR Staff
Priority: High
Related Code: `PostRecruitmentController`, `DetailRecruitmentCreate`, `DetailRecruitment`, `Views/HrStaff/PostRecruitment.jsp`, `Views/HrStaff/CreateNewRecruitment.jsp`

## Route
- `GET /postRecruitments`
- `POST /postRecruitments`
- `GET /detailRecruitmentCreate`
- `POST /detailRecruitmentCreate`
- `GET /detailRecruitment`
- `POST /detailRecruitment`

## Luong danh sach
1. HR Staff vao `/postRecruitments`.
2. Controller lay danh sach recruitment post.
3. Forward den `/Views/HrStaff/PostRecruitment.jsp`.

## Luong tao tin
1. HR Staff vao `/detailRecruitmentCreate`.
2. Nhap tieu de, vi tri, dia diem, luong, so luong, mo ta, yeu cau.
3. Submit form.
4. Controller validate du lieu.
5. Luu recruitment va redirect ve `/postRecruitments`.

## Luong sua/xoa neu co
1. HR Staff mo `/detailRecruitment?id=...`.
2. Controller lay chi tiet recruitment.
3. Submit thay doi.
4. Redirect ve `/postRecruitments` kem mess/error.

## Hien trang code
- Da co controller post/create/detail recruitment cho HR Staff/HR package.
- Can dong bo quyen giua role 4 va permission recruitment neu muon dung permission dong.

## Acceptance Criteria
- [ ] HR Staff tao duoc tin tuyen dung hop le.
- [ ] Du lieu thieu bat buoc bi bao loi.
- [ ] Sau khi tao/sua thanh cong quay ve danh sach recruitment.

## Missing Work
- [ ] Neu tin can HR Manager duyet, them status `Pending/Approved/Rejected`.
- [ ] Them audit log cho tao/sua/xoa recruitment.
