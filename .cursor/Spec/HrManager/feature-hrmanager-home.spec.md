# Feature: HR Manager Home
Status: Approved
Actor: HR Manager
Priority: High
Related Code: `HrHomeController`, `EmployeeDAO`, `DepartmentDAO`, `PayrollDAO`, `ContractDAO`, `Views/hr/HrHome.jsp`, `css/hr-theme.css`

## Muc tieu
Cung cap man hinh trung tam cho HR Manager sau khi dang nhap va chon khu vuc HR tren homepage. Trang nay gom dieu huong nhanh toi cac nghiep vu HR Manager va hien thi du lieu co ban duoc nap tu controller.

## Route
- `GET /HrHomeController`
- `POST /HrHomeController`
- JSP render target: `/Views/hr/HrHome.jsp`
- Khong dung link public truc tiep toi `/Views/hr/HrHome.jsp` trong luong binh thuong vi JSP can attribute tu controller.

## Phan quyen
- `RoleAuthorizationFilter` cho role 2 truy cap `/HrHomeController`, `/hr/*`, `/Views/hr/*`, `/viewRecruitment`, `/viewCV`, `/detailWaitingRecruitment`.
- Neu chua login hoac khong phai role HR Manager, request bi redirect ve `/login` theo filter hien co.

## Luong chinh
1. HR Manager dang nhap local/Google thanh cong.
2. He thong redirect ve `/homepage`.
3. HR Manager bam khu vuc HR/HR Manager tren homepage.
4. Browser mo `/HrHomeController`.
5. `HrHomeController` load `employees`, `departments`, `section`.
6. Neu request co `section=payroll-management` hoac `payrollStatus`, controller load payroll list va cac count theo status.
7. Controller forward sang `/Views/hr/HrHome.jsp`.
8. JSP hien thi HR Home va cac the truy cap nhanh.

## Truy cap nhanh tren HR Home
- `Tin tuyen dung` -> `/viewRecruitment`
- `Tao nhan vien` -> `/hr/create-employee`
- `Danh sach nhan vien` -> `/hr/employee-list`
- `Duyet hop dong` -> `/hr/approve-reject-contracts`
- `Duyet bang luong` -> `/hr/payroll-approval?status=Pending`

## UI contract
- Giao dien dung BetterHR theme va tieng Viet theo `_Common/ui-language-theme.spec.md`.
- Khong hien trung title/hero cho cung mot noi dung.
- Sidebar phai hien active item ro rang, chu khong bi mat mau.
- Card truy cap nhanh phai co kich thuoc on dinh, icon va label khong bi tran/chen len nhau.
- Topbar search/notification/user menu chi la UI neu chua co backend rieng; khong duoc tu y them logic xu ly.

## Hien trang code
- `HrHomeController` da ton tai va forward sang `/Views/hr/HrHome.jsp`.
- `RoleAuthorizationFilter` da co `/HrHomeController` cho role 2.
- Trang HR Home da co link den recruitment, create employee, employee list, contract approval, payroll approval.
- Mot so thong tin thong ke/contract count con duoc tinh trong JSP hoac mock UI, can tach dan ve controller/DAO neu muon dung production.

## Acceptance Criteria
- [ ] Role HR Manager mo `/HrHomeController` thay HR Home.
- [ ] User khong dung role bi chan theo filter.
- [ ] Cac card truy cap nhanh tro dung route code hien tai.
- [ ] Trang khong bi duplicate title/section va khong loi font tieng Viet.
- [ ] Khi vao payroll section, status tab giu dung query `payrollStatus`.

## Missing Work
- [ ] Chuyen cac thong ke con tinh bang scriptlet trong JSP ve controller/service.
- [ ] Thay `System.out.println`, `System.err`, `printStackTrace` trong `HrHomeController` bang logger.
- [ ] Them audit/system activity that neu can hien thi recent activity.
- [ ] Them test route `/HrHomeController` va cac quick access link.
