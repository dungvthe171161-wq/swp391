# Feature: Employee quan ly Mail Request
Status: Planned
Actor: Employee
Priority: High
Source: `G1_SE2014_SRS Document.docx.pdf`, `src/data/data.sql`
Related Code: `MailRequestDAO`, `MailRequest`, `Views/Employee/LeaveManagement.jsp`

## Tinh chat spec
Day la target design/gia thiet de gen code sau. File `feature-employee-leave-management.spec.md` chi la hien trang/legacy neu can doi chieu.

## Muc tieu
Cho phep Employee gui va theo doi cac request noi bo theo SRS:
- Submit Mail Request.
- View Mail Request Response.
- Nhan thong bao khi request duoc approve/reject.

Bang source la `MailRequest`, khong phai bang `Leave` rieng, theo `_Common/database-impact.spec.md`.

## Route de xuat
- `GET /employee/requests`: danh sach request cua nhan vien dang dang nhap.
- `GET /employee/requests/create`: hien form tao request.
- `POST /employee/requests/create`: tao request moi.
- `GET /employee/requests/detail?id={requestId}`: xem chi tiet request.
- `POST /employee/requests/cancel`: huy request neu con `Pending` neu business rule cho phep.

## Controller/DAO/JSP de xuat
| Thanh phan | De xuat |
| --- | --- |
| Controller | `com.hrm.controller.employee.EmployeeRequestController` |
| DAO | `MailRequestDAO` |
| Entity | `MailRequest` |
| JSP list | `/Views/Employee/RequestList.jsp` |
| JSP create | `/Views/Employee/CreateRequest.jsp` |
| JSP detail | `/Views/Employee/RequestDetail.jsp` |
| Bang | `MailRequest` |

## Request type
Gia tri DB hop le:
- `Leave`
- `Resignation`
- `Petition`

Voi request nghi phep, `LeaveType` co the la:
- `Annual`
- `Sick`
- `Maternity`
- `Unpaid`
- `Other`

## Luong danh sach
1. Employee vao `/employee/requests`.
2. Controller lay `systemUser` tu session.
3. Controller xac dinh `EmployeeID` cua user hien tai.
4. DAO lay request theo `EmployeeID`.
5. He thong forward den list JSP.
6. Employee thay status `Pending`, `Approved`, `Rejected`.

## Luong tao request
1. Employee vao `/employee/requests/create`.
2. Employee chon `RequestType`.
3. Neu `RequestType = Leave`, Employee chon `LeaveType`, `StartDate`, `EndDate`, `Reason`.
4. Neu `RequestType = Resignation` hoac `Petition`, Employee nhap `Reason`, ngay ap dung neu can.
5. Controller validate input.
6. Controller tao `MailRequest` voi `Status = Pending`, `ApprovedBy = NULL`.
7. Thanh cong redirect ve `/employee/requests?success=created`.
8. He thong co the gui notification/email cho Dept Manager/HR neu notification service da co.

## Luong xem detail/response
1. Employee mo `/employee/requests/detail?id={requestId}`.
2. Controller lay request theo id.
3. Controller kiem tra request thuoc `EmployeeID` hien tai.
4. JSP hien thong tin request, status va nguoi duyet neu co.
5. Neu request bi reject, UI hien ly do neu schema sau nay co cot ly do.

## Luong loi
- Chua dang nhap: redirect `/login`.
- User khong co `EmployeeID`: hien loi khong co ho so nhan vien.
- `requestId` khong ton tai: hien loi than thien.
- Request khong thuoc employee hien tai: tra AccessDenied/403.
- `StartDate` sau `EndDate`: validation error.
- `Reason` rong: validation error.
- `LeaveType` khong dung enum DB: validation error.

## Hien trang code
- Da co `MailRequestDAO.insert`, `MailRequestDAO.getByEmployee`, `MailRequestDAO.updateStatus`.
- Da co `MailRequest` entity nhung chua thay field `LeaveType` trong entity/DAO insert hien tai.
- Da co `Views/Employee/LeaveManagement.jsp` nhung la form tinh, chua thay servlet xu ly Employee request.
- Chua co request list/detail JSP theo Employee.

## Acceptance Criteria
- [ ] Employee xem duoc danh sach request cua chinh minh.
- [ ] Employee tao request hop le va request moi co `Status = Pending`.
- [ ] Employee khong xem duoc request cua employee khac.
- [ ] Request nghi phep validate ngay bat dau/ket thuc.
- [ ] Request type va status ghi DB dung enum trong `data.sql`.
- [ ] Detail page hien duoc ket qua approve/reject.
- [ ] Loi validation khong tao ban ghi moi.

## Missing Work
- [ ] Cap nhat `MailRequest` entity/DAO neu can ho tro `LeaveType`.
- [ ] Tao Employee request controller va JSP list/create/detail.
- [ ] Tao spec Dept Manager/HR Staff approve request neu chua du.
- [ ] Them notification/email sau khi submit va sau khi approve/reject.
- [ ] Them audit log cho create/cancel request neu can.
