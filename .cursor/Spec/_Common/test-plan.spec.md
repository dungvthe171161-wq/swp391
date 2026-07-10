# Cross-cutting Spec: Test Plan
Status: Approved
Priority: Medium

## Muc tieu
Dinh nghia bo test toi thieu cho cac module HRMS theo spec.

## Auth tests
- Login dung tao session `systemUser`.
- Login sai khong tao session.
- Register thanh cong tao `SystemUser` va redirect `/login?success=registered` neu gui mail thanh cong.
- Register tao account thanh cong nhung mail fail redirect `/login?success=registered_mail_failed`.
- Register duplicate username/email bi tu choi.
- Register public tao role `Guest` va khong tu gan `EmployeeID` neu chua qua luong employee.
- Logout invalidate session.
- Remember-me chi luu username, khong luu password.
- Forgot password chi gui PIN khi email ton tai trong `SystemUser.Email`.
- PIN sai hoac het session khong duoc vao form tao mat khau moi.
- PIN dung set `recoveryVerified` va redirect sang `/changepassRE`.
- Doi mat khau khoi phuc thanh cong cap nhat `SystemUser.PasswordHash` va xoa `recoveryEmail`, `pinCode`, `recoveryVerified`.
- Mo truc tiep `/changepassRE` khi chua verify PIN bi redirect ve `/ForgotPassword`.

## Permission tests
- User khong phai Admin khong vao `/admin`.
- `/departments` phai bi chan neu chua duoc cap quyen sau khi sua filter.
- HR Staff khong duoc approve payroll cuoi cung.
- Employee khong vao duoc route Dept Manager.

## Admin tests
- Tao user validate username/password/role/employee.
- Role permission API POST body sai tra 400.
- RoleID 1 Admin mo/luu trang phan quyen duoc.
- Trang phan quyen checkbox bam duoc khi co quyen va khong spam toast trung nhau.
- Admin dashboard khong bi UI cu de len UI moi.
- Role list phan trang 10 ban ghi.

## Dept tests
- Dept Manager tao task thanh cong.
- Update task thanh cong reset assignment cu.
- User khong dung phong ban khong sua task neu rule da implement.

## Employee tests
- Employee chi xem task duoc giao.
- Employee khong xem payroll/contract cua nguoi khac.

## HR Staff tests
- Generate payroll khong crash khi procedure loi.
- Batch submit/delete payroll list rong bi tu choi.
- Khong sua allowance/deduction khi payroll `Pending/Approved/Paid`.

## HR Manager tests
- `/HrHomeController` load HR Home cho role HR Manager.
- HR Home co quick access dung route: recruitment, create employee, employee list, contract approval, payroll approval.
- HR Home khong bi duplicate title va khong loi font tieng Viet.
- Approve/reject recruitment dung status.
- Approve/reject contract dung role.
- Approve/reject payroll dung role.

## UI/Language tests
- Cac JSP da sua khong con mojibake/ky tu replacement.
- Text hien thi tieng Viet, tru logo `BetterHR` va gia tri ky thuat backend.
- Form UI update khong doi `action`, `method`, `name`, `value`.

## Public Candidate tests
- Guest xem job list.
- Guest chua login bam ung tuyen redirect `/login?error=login_required`.
- User da login bam ung tuyen mo form apply.
- Apply job validate email/phone/name.
- Upload CV sai dinh dang bi tu choi.

## Acceptance Criteria
- [ ] Moi bug quan trong trong spec co test tuong ung.
- [ ] Test uu tien route, permission, validation va status workflow.
