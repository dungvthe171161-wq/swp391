# Module Spec: Guest Candidate
Status: Approved
Actor: Guest Candidate
Priority: Medium
Related Code: `RecruitmentController`, `RecruitmentDAO`, `GuestDAO`, `Views/Homepage.jsp`, `Views/Recruitment.jsp`, `Views/ApplyForm.jsp`, `Views/Success.jsp`

## Pham vi
Guest Candidate la nguoi dung chua phai nhan vien trong he thong BetterHR. Actor nay co the xem homepage public, xem danh sach viec lam, dang ky/dang nhap tai khoan va nop don ung tuyen sau khi da co session.

Trong database, tai khoan moi tu register local hoac Google Login phai la role `Guest` va `EmployeeID = NULL`. Chi khi HR Manager tao/chuyen thanh nhan vien thi account moi duoc cap role `Employee` va gan `EmployeeID`.

## Feature files
- `feature-guest-homepage.spec.md`
- `feature-guest-job-list.spec.md`
- `feature-guest-job-application.spec.md`
- `feature-guest-application-success.spec.md`

## Route chinh
- `GET /homepage`
- `GET /RecruitmentController`
- `GET /RecruitmentController?action=apply&id={recruitmentId}` hoac route apply theo code hien tai
- `POST /RecruitmentController`
- `/Views/Homepage.jsp`
- `/Views/Recruitment.jsp`
- `/Views/ApplyForm.jsp`
- `/Views/Success.jsp`

## Nguyen tac
- Guest duoc phep xem homepage public va job dang tuyen.
- Guest chua dang nhap khong duoc nop application; bam ung tuyen phai redirect login.
- User da dang nhap duoc nop application theo route apply.
- Du lieu nop don phai validate frontend va backend.
- Upload CV phai duoc gioi han loai file/kich thuoc theo `_Common/upload-cv.spec.md`.
- UI hien thi tieng Viet, giu logo BetterHR.

## Chuyen doi sang Employee
1. Guest dang ky local hoac Google Login tao `SystemUser` role `Guest`, `EmployeeID = NULL`.
2. Guest nop don ung tuyen va duoc HR xu ly theo bang `Guest`.
3. Khi HR Manager tao employee bang email da co account Guest, he thong cap nhat account do sang role `Employee` va gan `EmployeeID`.
4. Neu email chua co account, HR Manager tao employee thi he thong tao `SystemUser` moi role `Employee` va gan `EmployeeID`.

## Acceptance Criteria
- [ ] Guest xem duoc homepage public.
- [ ] Guest xem duoc danh sach job dang tuyen.
- [ ] Guest chua login bam ung tuyen bi yeu cau dang nhap.
- [ ] User da login mo duoc form apply.
- [ ] Submit application hop le tao record ung vien/application.
- [ ] Submit thanh cong sang success page.
- [ ] Account Guest khong co `EmployeeID` truoc khi HR Manager chuyen thanh nhan vien.
