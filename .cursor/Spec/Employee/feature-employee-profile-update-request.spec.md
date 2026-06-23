# Feature: Employee yeu cau cap nhat ho so
Status: Planned
Actor: Employee
Priority: Medium
Source: `G1_SE2014_SRS Document.docx.pdf`
Related Code: `ProfilepageController`, `EmployeeDAO`, `MailRequestDAO`, `Views/Profilepage.jsp`, `Views/Employee/EmployeeProfile.jsp`

## Tinh chat spec
Day la target design/gia thiet de gen code sau. Code hien tai dang co `/profilepage` va co update truc tiep mot so field; spec nay de xuat luong request update dung theo SRS.

## Muc tieu
Employee co the gui yeu cau cap nhat thong tin ca nhan thay vi tu sua cac field nhay cam. Request duoc tao o trang thai `Pending`; approval khong nam trong pham vi Employee.

## Route de xuat
- `GET /employee/profile`: xem ho so hien tai.
- `GET /employee/profile-update-request`: mo form yeu cau cap nhat.
- `POST /employee/profile-update-request`: gui yeu cau cap nhat.

## Field duoc phep yeu cau sua
- `FullName`
- `Phone`
- `Address`
- `DOB`
- `Gender`

## Field khong cho Employee tu sua
- `EmployeeID`
- `DepartmentID`
- `Status`
- `Position`
- `RoleID`
- `BaseSalary`
- `Contract`
- `Payroll`

## Luong chinh
1. Employee mo `/employee/profile`.
2. Controller lay `EmployeeID` tu session `systemUser`.
3. Employee mo form yeu cau cap nhat.
4. Employee nhap field can sua va ly do.
5. Controller validate field duoc phep sua va co thay doi thuc su.
6. He thong tao request `Pending`.
7. Employee xem request vua tao trong `/employee/requests`.

## Luu y luu tru
Neu chua them bang profile update rieng, co the luu tam bang `MailRequest` voi `RequestType = Petition`, `Reason` chua noi dung field cu/moi. Neu can du lieu co cau truc, can them bang rieng va cap nhat spec/schema.

## Acceptance Criteria
- [ ] Employee tao duoc yeu cau cap nhat ho so voi status `Pending`.
- [ ] EmployeeID phai lay tu session, khong lay tu request parameter.
- [ ] Field khong duoc phep sua bi tu choi.
- [ ] Request rong/khong co thay doi bi tu choi.
- [ ] Request hien trong danh sach request cua Employee.

## Missing Work
- [ ] Chot luu bang `MailRequest` hay bang profile update rieng.
- [ ] Refactor `/profilepage` neu muon bo update truc tiep.
