# Feature: HR Staff quan ly hop dong
Status: Approved
Actor: HR Staff
Priority: High
Related Code: `ContractListController`, `CreateContractController`, `ContractDAO`, `Views/HrStaff/ContractList.jsp`, `Views/HrStaff/CreateContract.jsp`

## Route
- `GET /hrstaff/contracts`
- `POST /hrstaff/contracts`
- `GET /hrstaff/contracts/create`
- `POST /hrstaff/contracts/create`

## Luong danh sach
1. HR Staff vao `/hrstaff/contracts`.
2. Controller lay danh sach contract.
3. Ap dung search/filter/pagination neu request co.
4. Forward den `/Views/HrStaff/ContractList.jsp`.

## Luong tao hop dong
1. HR Staff vao `/hrstaff/contracts/create`.
2. Controller load du lieu employee va form.
3. HR Staff nhap thong tin hop dong.
4. Controller validate employee, loai hop dong, ngay bat dau, ngay ket thuc, luong.
5. Luu contract.
6. Redirect ve `/hrstaff/contracts`.

## Luong xoa/cap nhat neu co
1. HR Staff thao tac tren danh sach contract.
2. Controller xu ly action theo parameter.
3. Redirect ve `/hrstaff/contracts` kem success/error.

## Acceptance Criteria
- [ ] HR Staff tao hop dong hop le thanh cong.
- [ ] Ngay ket thuc khong duoc truoc ngay bat dau.
- [ ] Employee khong hop le bi tu choi.
- [ ] Role khac khong vao duoc route HR Staff contract.

## Missing Work
- [ ] Duyet hop dong thuoc HR Manager/HR, khong gop vao HR Staff neu can workflow phe duyet.
- [ ] Them audit log tao/sua/xoa contract.
