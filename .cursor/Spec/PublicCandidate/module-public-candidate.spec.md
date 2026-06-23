# Module Spec: Public Candidate
Status: Approved
Actor: Guest Candidate
Priority: Medium
Related Code: `RecruitmentController`, `RecruitmentDAO`, `GuestDAO`, `Views/Recruitment.jsp`, `Views/ApplyForm.jsp`, `Views/Success.jsp`

## Pham vi
Ung vien ben ngoai he thong xem danh sach viec lam public. Khi bam ung tuyen, he thong yeu cau dang nhap truoc; sau khi da co session thi moi nop don ung tuyen.

## Feature files
- `feature-public-job-list.spec.md`
- `feature-public-job-application.spec.md`
- `feature-public-application-success.spec.md`

## Route chinh
- `/RecruitmentController`
- `/Views/Recruitment.jsp`
- `/Views/ApplyForm.jsp`
- `/Views/Success.jsp`

## Nguyen tac
- Guest duoc phep xem job dang tuyen.
- Guest chua dang nhap khong duoc nop application; bam ung tuyen phai redirect login.
- User da dang nhap duoc nop application theo route apply.
- Du lieu nop vao phai validate frontend va backend.
- Upload CV phai duoc gioi han loai file/kich thuoc.
