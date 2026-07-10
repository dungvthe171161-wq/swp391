# Feature: HR Manager duyet hop dong
Status: Partial
Actor: HR Manager
Priority: High
Related Code: `ApproveRejectContractController`, `ContractDAO`, `Views/hr/ApproveRejectContract.jsp`

## Route
- `GET /hr/approve-reject-contracts`
- `POST /hr/approve-reject-contracts`
- JSP: `/Views/hr/ApproveRejectContract.jsp`.

## Luong chinh
1. HR Manager vao `/hr/approve-reject-contracts`.
2. `ApproveRejectContractController` kiem tra permission `VIEW_CONTRACTS`.
3. Controller lay danh sach contract cho duyet.
4. HR Manager approve hoac reject.
5. Controller cap nhat status contract.
6. Redirect ve danh sach kem thong bao.

## UI contract
- JSP phai theo BetterHR theme va hien thi tieng Viet.
- Link tu HR Home vao `/hr/approve-reject-contracts`.
- Gia tri status gui backend giu dung theo code/data hien tai.

## Acceptance Criteria
- [ ] Chi HR Manager co quyen moi duyet hop dong.
- [ ] Reject phai co ly do neu business rule yeu cau.
- [ ] Khong duyet contract khong ton tai.

## Missing Work
- [ ] Xem lai permission `VIEW_CONTRACTS` co du cho approve/reject hay can them `APPROVE_CONTRACTS`.
- [ ] Them audit log approve/reject.
