# Cross-cutting Spec: Error Handling Standard
Status: Approved
Priority: High
Related Code: All Controllers, `PermissionUtil`, `Views/Common/AccessDenied.jsp`

## Muc tieu
Thong nhat cach tra loi loi cho HTML page va JSON API trong HRMS.

## HTML response
| Truong hop | Xu ly |
| --- | --- |
| Chua dang nhap | Redirect `/login` hoac `/Views/Login.jsp` theo controller hien tai |
| Khong du quyen | Forward `/Views/Common/AccessDenied.jsp` hoac redirect login theo filter |
| Validation error | Forward lai JSP kem attribute loi |
| Loi server | Hien thong bao than thien, khong lo stack trace |

## JSON response
| Truong hop | HTTP status | Format de xuat |
| --- | --- | --- |
| Validation error | 400 | `{ "status": "error", "message": "...", "code": "VALIDATION_ERROR" }` |
| Chua dang nhap | 401 | `{ "status": "error", "message": "Vui long dang nhap", "code": "UNAUTHORIZED" }` |
| Thieu quyen | 403 | `{ "status": "error", "message": "...", "code": "FORBIDDEN" }` |
| Khong tim thay | 404 | `{ "status": "error", "message": "...", "code": "NOT_FOUND" }` |
| Loi server | 500 | `{ "status": "error", "message": "Co loi he thong", "code": "SERVER_ERROR" }` |

## Logging
- Khong dung `System.out.println` hoac `printStackTrace` trong production.
- Dung `java.util.logging.Logger` hoac logging framework thong nhat.
- Log exception chi tiet o server, UI/API chi hien message an toan.

## Acceptance Criteria
- [ ] API luon set `application/json` va `UTF-8`.
- [ ] HTML loi khong hien stack trace.
- [ ] Loi validation tra dung 400 voi API.
- [ ] Loi permission tra dung 403 voi API.

## Missing Work
- [ ] Chuan hoa cac controller con redirect/login JSP lan lon.
- [ ] Thay cac `printStackTrace` bang logger.
