# Cross-cutting Spec: Audit Log
Status: Approved
Priority: High
Schema Source: `src/data/data.sql`
Related Code: `SystemLogDAO`, `SystemLog`, `Admin/AuditLog.jsp`

## Muc tieu
Ghi lai cac thao tac quan trong de Admin/HRMS co the truy vet khi co thay doi du lieu nhay cam.

## Bang hien co
Bang `SystemLog` trong `data.sql` gom:

| Column | Mo ta |
| --- | --- |
| `LogID` | Khoa chinh |
| `UserID` | User thuc hien |
| `Action` | Hanh dong, vi du `LOGIN`, `CREATE`, `UPDATE`, `APPROVE` |
| `ObjectType` | Loai doi tuong, vi du `SystemUser`, `Recruitment`, `Task` |
| `OldValue` | Gia tri cu |
| `NewValue` | Gia tri moi |
| `Timestamp` | Thoi diem ghi log |

## Su kien can ghi log
- Login thanh cong/that bai neu co policy.
- Logout neu can.
- Admin tao/sua/xoa user.
- Admin reset password.
- Admin thay doi role/permission.
- Admin thay doi department.
- HR Staff tao/sua/xoa recruitment.
- HR Staff tao/sua/xoa contract.
- HR Staff generate/submit/delete payroll.
- HR Staff them/sua/xoa allowance/deduction.
- HR Manager approve/reject recruitment.
- HR Manager approve/reject contract.
- HR Manager approve/reject payroll.

## Format action de xuat
| Action | Khi nao dung |
| --- | --- |
| `LOGIN` | Dang nhap |
| `LOGOUT` | Dang xuat |
| `CREATE` | Tao moi du lieu |
| `UPDATE` | Cap nhat du lieu |
| `DELETE` | Xoa du lieu |
| `APPROVE` | Duyet |
| `REJECT` | Tu choi |
| `RESET_PASSWORD` | Reset mat khau |
| `GRANT_PERMISSION` | Gan quyen |
| `REVOKE_PERMISSION` | Go quyen |

## Acceptance Criteria
- [ ] Thao tac nhay cam duoc ghi vao `SystemLog`.
- [ ] `UserID` phai la user dang thao tac neu co session.
- [ ] `ObjectType` phai dung ten entity/bang lien quan.
- [ ] Admin xem duoc audit log tai `/admin?action=audit-log`.

## Missing Work
- [ ] Neu can IP/user-agent, phai them cot moi vao `SystemLog` truoc khi ghi spec nhu implemented.
- [ ] Bo sung log vao cac controller dang mutate du lieu.
