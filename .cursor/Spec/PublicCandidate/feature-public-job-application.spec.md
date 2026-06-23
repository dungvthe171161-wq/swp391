# Feature: Ung vien nop don ung tuyen
Status: Approved
Actor: Logged-in Candidate
Priority: High
Related Code: `RecruitmentController`, `GuestDAO`, `Views/ApplyForm.jsp`

## Route
- `GET /RecruitmentController?action=apply&id={recruitmentId}` hoac route apply theo code hien tai.
- `POST /RecruitmentController`

## Luong GET
1. User da dang nhap bam `Ung tuyen ngay` o danh sach job.
2. Controller kiem tra session `systemUser`.
3. Controller lay chi tiet recruitment theo id.
4. Forward den `/Views/ApplyForm.jsp`.

## Luong chua dang nhap
1. Guest chua dang nhap bam `Ung tuyen ngay`.
2. He thong redirect sang `/login?error=login_required`.
3. Sau khi dang nhap thanh cong, user quay ve `/homepage` theo luong auth hien tai; neu can quay lai job cu thi can bo sung returnUrl sau nay.

## Luong POST
1. User da dang nhap nhap ho ten, email, phone va cac thong tin apply.
2. User upload CV neu form yeu cau.
3. Controller validate du lieu.
4. Luu candidate/application qua DAO.
5. Thanh cong redirect `/Views/Success.jsp`.

## Validation
- Ho ten bat buoc.
- Email dung format.
- Phone dung format co ban.
- Recruitment id phai ton tai.
- CV neu upload phai dung dinh dang cho phep.

## Acceptance Criteria
- [ ] Ung vien submit du lieu hop le thanh cong.
- [ ] Chua login khong mo duoc form apply.
- [ ] Thieu email/ho ten/phone hien loi.
- [ ] Recruitment id khong ton tai khong tao application.
- [ ] Submit thanh cong sang success page.

## Missing Work
- [ ] Gioi han kich thuoc upload CV.
- [ ] Chong nop trung email cho cung mot job neu business rule yeu cau.
