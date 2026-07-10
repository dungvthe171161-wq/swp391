# Cross-cutting Spec: Business Status Workflow
Status: Approved
Priority: High
Schema Source: `src/data/data.sql`

## Muc tieu
Chuan hoa status cho cac nghiep vu co vong doi ro rang. Phan "DB enum hien tai" phai khop voi `src/data/data.sql`; phan "De xuat" chi dung khi refactor schema.

## Employee status
| DB enum hien tai | Y nghia |
| --- | --- |
| `Active` | Dang lam viec |
| `Resigned` | Da nghi viec |
| `Probation` | Thu viec |
| `Intern` | Thuc tap |

## Contract status
| DB enum hien tai | Y nghia |
| --- | --- |
| `Draft` | Ban nhap |
| `Pending_Approval` | Cho duyet |
| `Approved` | Da duyet |
| `Rejected` | Bi tu choi |
| `Active` | Dang hieu luc |
| `Expired` | Het han |

## MailRequest status
| DB enum hien tai | Y nghia |
| --- | --- |
| `Pending` | Cho duyet |
| `Approved` | Da duyet |
| `Rejected` | Bi tu choi |

## Task status
| DB enum hien tai | Y nghia |
| --- | --- |
| `Waiting` | Cho xu ly |
| `In Progress` | Dang lam |
| `Completed` | Hoan thanh |
| `Rejected` | Bi tu choi |

## Recruitment status
| DB enum hien tai | Y nghia |
| --- | --- |
| `Waiting` | Cho xu ly/cho duyet |
| `New` | Tin moi |
| `Rejected` | Bi tu choi |
| `Applied` | Da co ung tuyen/da apply |
| `Deleted` | Da xoa mem |

## Guest status
| DB enum hien tai | Y nghia |
| --- | --- |
| `Processing` | Dang xu ly |
| `Hired` | Da tuyen |
| `Rejected` | Bi tu choi |

## Payroll status
| DB enum hien tai | Y nghia | Rule |
| --- | --- | --- |
| `Draft` | HR Staff dang tao/tinh | Cho phep sua/xoa |
| `Pending` | Da submit cho duyet | Khoa allowance/deduction |
| `Approved` | HR Manager da duyet | Khoa sua |
| `Rejected` | Bi tu choi, cho HR Staff sua/gui lai | Cho submit lai |
| `Paid` | Da chi tra | Khoa sua |

## PayrollAudit status
| DB hien tai | Y nghia |
| --- | --- |
| `Status VARCHAR(20) DEFAULT 'Draft'` | Khong bi rang buoc ENUM trong SQL, nhung nen dong bo voi `Payroll.Status` |

## De xuat refactor sau nay
- Recruitment nen tach ve `Draft`, `Pending`, `Approved`, `Rejected`, `Closed` neu can workflow duyet ro rang.
- Guest/Candidate nen mo rong `New`, `Reviewing`, `Interview`, `Passed`, `Rejected` neu can pipeline tuyen dung.
- Task nen doi `Waiting` thanh `NotStarted` neu muon ten status de hieu hon, nhung phai migrate DB va code.
- Contract nen dung `Pending_Approval` dung voi DB, khong ghi `Pending` trong code/spec neu chua migrate.

## Acceptance Criteria
- [ ] Spec va code dung dung enum hien co trong `data.sql`.
- [ ] UI co the hien thi tieng Viet, nhung gia tri ghi DB phai dung enum.
- [ ] Neu them status moi, phai update `data.sql`, DAO, controller, JSP va spec.
