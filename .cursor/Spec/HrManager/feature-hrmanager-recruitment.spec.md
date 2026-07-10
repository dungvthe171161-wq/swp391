# Module Spec: HR Manager Operations
Status: Approved
Actor: HR Manager
Priority: High
Related Code: `com.hrm.controller.hr.*`, `com.hrm.controller.hrManager.*`, `Views/hr/*`

## Pham vi
HR Manager quan ly cac nghiep vu cap phe duyet va quan tri nhan su: recruitment review, candidate/CV review, tao employee, duyet hop dong, duyet payroll.

## Feature files
- `feature-hrmanager-home.spec.md`
- `feature-hrmanager-recruitment-review.spec.md`
- `feature-hrmanager-candidate-cv.spec.md`
- `feature-hrmanager-employee-management.spec.md`
- `feature-hrmanager-contract-approval.spec.md`
- `feature-hrmanager-payroll-approval.spec.md`

## Route chinh
- `/HrHomeController`
- `/viewRecruitment`
- `/detailWaitingRecruitment`
- `/viewCV`
- `/hr/create-employee`
- `/hr/*`
- Payroll approval routes trong `PayrollApprovalController`
- Contract approval routes trong `ApproveRejectContractController`

## Phan quyen hien co
- `RoleAuthorizationFilter` cho role 2 vao `/HrHomeController`, `/hr/`, `/Views/hr/`, `/viewRecruitment`, `/viewCV`, `/detailWaitingRecruitment`.
- `ViewRecruitment` dung permission `VIEW_RECRUITMENT`.

## Canh bao code
- Mot so route HR Staff va HR Manager cung lien quan recruitment, can tach ro: HR Staff tao tin, HR Manager xem/duyet/xem tin cho duyet.
- HR Home la man hinh dieu huong trung tam cua HR Manager; link tu homepage nen vao `/HrHomeController`, khong vao JSP truc tiep.
