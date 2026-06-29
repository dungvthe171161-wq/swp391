# Module Spec: Guest Candidate Portal
Status: Implemented for candidate profile apply flow
Actor: Guest Candidate
Priority: High
Related Code: `RecruitmentController`, `GuestPortalController`, `LoginController`, `GoogleAuthController`, `SessionSecurityFilter`, `CandidateProfileDAO`, `ApplicationDAO`, `Views/Recruitment.jsp`, `Views/ApplyForm.jsp`, `Views/ApplyVerifyEmail.jsp`, `Views/ApplyConfirm.jsp`, `Views/Success.jsp`, `Views/Guest/Profile.jsp`, `Views/Guest/ProfileEmailVerify.jsp`, `ViewCV`

## Muc tieu
Guest Candidate la tai khoan ung vien trong HRMS. Guest co the xem viec lam, xem chi tiet tuyen dung, tao ho so ung tuyen dung mot lan, xac nhan email, ung tuyen vao nhieu tin tuyen dung ma khong phai nhap lai thong tin.

Luồng mới hướng tới trải nghiệm giống website tuyển dụng: lần đầu hoàn thiện hồ sơ, các lần sau chỉ bấm ứng tuyển và xác nhận.

## Trang thai code hien tai
- Register local va Google Login tao/cap nhat `SystemUser` role `Guest`.
- Danh sach va chi tiet viec lam dung `GET /RecruitmentController`.
- Nut ung tuyen dung `GET /RecruitmentController?action=apply&recruitmentId={id}`.
- Neu chua dang nhap, he thong luu `redirectAfterLogin` de sau khi login quay lai dung tin tuyen dung dang xem.
- Luong apply moi dung `CandidateProfile` lam ho so ung vien dung lai.
- `Application` luu moi lan ung tuyen, lien ket `GuestID`, `RecruitmentID`, `CandidateProfileID`, va giu `CV` de tuong thich man HR xem CV.
- Xac nhan email ho so ung tuyen dung `HttpSession`, khong tao bang OTP/email-verification rieng.
- Trang Guest Profile co menu/section `Hồ sơ ứng tuyển` de cap nhat ho so ung vien.

## Quyet dinh thiet ke

### CandidateProfile
`CandidateProfile` la ho so ung vien dung lai cho moi lan ung tuyen:
- Moi `GuestID` co toi da 1 `CandidateProfile`.
- Luu thong tin ca nhan, thong tin nghe nghiep, CV va trang thai xac nhan email.
- Khi Guest cap nhat profile, cac lan ung tuyen tiep theo dung thong tin moi nhat.

### Application
`Application` chi dai dien cho mot lan ung tuyen vao mot `Recruitment`:
- Khong lap lai toan bo thong tin ca nhan.
- Lien ket den `CandidateProfileID`.
- Co unique `GuestID + RecruitmentID` de chan nop trung.
- Status mac dinh khi tao moi: `Applied`.
- CurrentStep mac dinh: `Applied`.
- Source mac dinh: `Portal`.

### Email verification
- Khong tao database moi cho ma xac nhan.
- Ma xac nhan email va ban nhap tam duoc luu trong session.
- Ma het han sau 10 phut.
- Chi luu `CandidateProfile` sau khi ma email hop le.

### Recruitment fields
Khong them schema rieng cho department, benefit, deadline trong luong nay. Man hinh ung tuyen chi hien thi nhung thong tin da co san tu `Recruitment`.

## Database hien tai can co
- Bang `CandidateProfile`.
- Bang `Application` co them `CandidateProfileID` va `Note`.
- Bang `Application` giu cot `CV` de tuong thich code HR/ViewCV cu va de hien thi CV nhanh.
- Bang `Guest` van duoc giu de tuong thich, khong drop `Guest.RecruitmentID`.

## Route chinh
- `GET /RecruitmentController`
- `GET /RecruitmentController?action=apply&recruitmentId={recruitmentId}`
- `POST /RecruitmentController?action=saveCandidateProfile`
- `POST /RecruitmentController?action=verifyCandidateProfileEmail`
- `POST /RecruitmentController?action=resendCandidateProfileCode`
- `POST /RecruitmentController?action=confirmApplication`
- `GET /guest/profile`
- `POST /guest/profile`
- `POST /guest/profile?action=verifyCandidateProfileEmail`
- `POST /guest/profile?action=resendCandidateProfileCode`

## Luong ung tuyen
1. Guest xem danh sach/chi tiet viec lam.
2. Guest bam `Ứng tuyển ngay`.
3. Neu chua dang nhap, hien yeu cau dang nhap/dang ky va sau login quay lai dung tin tuyen dung.
4. Neu da dang nhap, controller kiem tra da ung tuyen tin nay chua.
5. Neu da ung tuyen, hien thong bao `Bạn đã ứng tuyển công việc này.` va khong tao record moi.
6. Neu chua co `CandidateProfile` da xac nhan email, hien form ho so ung tuyen.
7. Guest nhap ho so, upload CV PDF/DOC/DOCX toi da 10MB, he thong gui ma xac nhan email.
8. Guest nhap ma xac nhan hop le, he thong luu `CandidateProfile`.
9. He thong hien man xac nhan ung tuyen voi thong tin cong viec.
10. Guest bam xac nhan, he thong tao `Application`.
11. He thong hien trang thanh cong.

## Chuc nang Guest Profile
Trang `Guest Profile` co section `Hồ sơ ứng tuyển`, cho phep sua:
- Ho va ten.
- So dien thoai.
- Email ung tuyen.
- Ngay sinh.
- Dia chi.
- Vi tri mong muon.
- Muc luong mong muon.
- Kinh nghiem lam viec.
- CV.

Neu email moi hoac profile chua xac nhan email, he thong gui ma xac nhan va chi luu sau khi ma hop le. Neu email khong doi va da xac nhan, luu truc tiep.

## Nguyen tac phan quyen
- Guest chua login duoc xem homepage public va job list.
- Guest chua login bam ung tuyen phai duoc yeu cau dang nhap/dang ky.
- Guest da login chi ung tuyen bang profile cua minh.
- Guest chi xem/cap nhat profile cua chinh minh.
- HR/HR Staff xem CV tu `Application.CV`, fallback sang `CandidateProfile.CVFilePath` va du lieu cu neu can.

## Acceptance Criteria
- [x] Guest xem duoc danh sach viec lam.
- [x] Guest chua login bam ung tuyen duoc yeu cau dang nhap va sau login quay lai dung job.
- [x] Guest lan dau ung tuyen phai tao `CandidateProfile`.
- [x] Luu `CandidateProfile` yeu cau xac nhan email bang ma session.
- [x] CV chi chap nhan PDF, DOC, DOCX va toi da 10MB.
- [x] Guest da co profile thi khong nhap lai thong tin khi ung tuyen lan sau.
- [x] Man xac nhan ung tuyen hien thong tin cong viec truoc khi tao application.
- [x] Xac nhan ung tuyen tao record trong `Application`.
- [x] Khong cho ung tuyen trung cung `GuestID + RecruitmentID`.
- [x] Guest Profile cap nhat duoc `Hồ sơ ứng tuyển`.
- [x] HR view CV lay duoc CV tu luong ung tuyen moi.
