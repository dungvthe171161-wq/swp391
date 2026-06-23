# HRMS Spec Index
Status: Approved
Updated: 2026-06-18

## Muc tieu
Thu muc `.cursor/Spec` luu cac spec theo actor va module, viet theo luong code hien tai cua HRMS. Moi spec can neu ro actor, route, controller/JSP lien quan, luong chinh, hien trang code va missing work.

## Actor folders
- `Auth`: dang nhap, dang ky, dang xuat, homepage routing, quen mat khau/PIN, doi mat khau, Google Login.
- `Admin`: dashboard, user, role, permission, department, audit/profile.
- `Dept`: dashboard phong ban, tao task, danh sach task, cap nhat task.
- `Employee`: home, view task, profile, payroll/contract, leave.
- `HrStaff`: recruitment post, candidate, contract, payroll, allowance/deduction.
- `HrManager`: recruitment review, CV, employee, contract approval, payroll approval.
- `Guest`: actor Guest Candidate xem homepage public, xem job va nop don ung tuyen.
- `PublicCandidate`: spec cu cua ung vien public, giu de doi chieu; spec chinh nen doc trong `Guest`.

## Cross-cutting specs
- `_Common/permission-matrix.spec.md`: bang role, route, permission va filter.
- `_Common/security-auth-hardening.spec.md`: bao mat login, session, cookie, remember-me.
- `_Common/error-handling-standard.spec.md`: chuan loi HTML va JSON API.
- `_Common/status-workflow.spec.md`: status nghiep vu cho recruitment, candidate, payroll, contract, task, leave.
- `_Common/audit-log.spec.md`: thao tac can ghi log va du lieu log can co.
- `_Common/upload-cv.spec.md`: rule upload CV cho ung vien public.
- `_Common/route-conflict-resolution.spec.md`: xu ly xung dot route `/viewTask`.
- `_Common/database-impact.spec.md`: bang doc/ghi theo module.
- `_Common/test-plan.spec.md`: bo test toi thieu theo module.
- `_Common/ui-language-theme.spec.md`: chuan giao dien, mau sac, font chu va tieng Viet tren JSP.

## Nguyen tac cap nhat spec
- Spec phai uu tien dung voi route/controller hien co.
- Neu code hien tai co loi thiet ke, ghi vao `Missing Work`, khong viet nhu da hoan thanh.
- Password auth hien tai dung `SystemUser.PasswordHash` theo logic plain text cua du an; neu doi sang hash/BCrypt thi phai cap nhat code va spec cung luc.
- Session login dung attribute `systemUser`.
- JSP va noi dung UI cua du an nen hien thi tieng Viet, giu ten logo BetterHR.
- Khi chi sua UI, khong doi route, form action, input name/value hoac attribute controller.

## Cac canh bao can xu ly trong code
- `/departments` chua duoc dua vao filter bao ve admin.
- Employee `ViewTask` va Dept `ViewTask` dang trung mapping `/viewTask`.
- Auth password recovery dung PIN session, can rate limit neu hardening sau nay.
- Google Login da co backend OAuth2, can dam bao config Google local/env dung.
- Register local da gui email BetterHR sau khi tao account; role mac dinh phai la `Guest`, `EmployeeID = NULL`.
- Guest Candidate la actor public chinh; account Guest chi duoc chuyen sang Employee khi HR Manager tao/chuyen nhan vien.
- HR Manager Home dung `/HrHomeController`; khong link truc tiep JSP neu can nap data.
- Mot so controller con `printStackTrace`/`System.out`, nen thay bang logger.
