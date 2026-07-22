# Tính năng Employee: Xem lương, hợp đồng và ký hợp đồng

Trạng thái: Đã rà soát theo code ngày 2026-07-22.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Employee xem phiếu lương, xem hợp đồng mới nhất, tải văn bản hợp đồng và ký hợp đồng của chính mình khi hợp đồng đủ điều kiện.

## Route, controller và JSP liên quan
- `GET /employee/payroll`, `GET /employee/contract`, `POST /employee/contract`, `GET /employee/contract/document`.
- Controller chính: `EmployeePortalController`.
- DAO/entity: `PayrollDAO`, `ContractDAO`, `ContractDocumentDAO`, `Contract`, `ContractDocument`.
- JSP: `Views/Employee/Payroll.jsp`, `Views/Employee/Contract.jsp`.

## Hiện trạng code
- `/employee/payroll` hiển thị payroll theo employee hiện tại; nếu có `payrollId`, controller chỉ set chi tiết khi `employeeId` của payroll khớp employee hiện tại.
- `/employee/contract` hiển thị hợp đồng mới nhất từ `ContractDAO.getContractByEmployeeId` và văn bản mới nhất từ `ContractDocumentDAO.getLatestByContractId`.
- `/employee/contract/document` tải inline văn bản hợp đồng theo `contractId` sau khi kiểm tra `Contract.EmployeeID == employeeId`.
- `POST /employee/contract` cho ký hợp đồng khi trạng thái là `Pending_Signature` hoặc `Approved`, yêu cầu `agreeDocument`, văn bản hợp đồng đọc được và `signatureData` dạng PNG base64.
- Chữ ký được lưu vào `/Upload/signatures/contract_{contractId}_user_{userId}_{timestamp}.png`; code lưu `SignatureHash`, `SignIp`, `SignUserAgent`, `ContractContentHash`, cập nhật hợp đồng sang `Active` và expire hợp đồng `Active` cũ của employee trong transaction DAO.

## Quy tắc nghiệp vụ chuẩn
- Employee không được xem payroll, hợp đồng hoặc document của employee khác.
- Employee không được tự sửa dữ liệu payroll hoặc nội dung hợp đồng; chỉ được ký khi hợp đồng đã được duyệt và còn chờ chữ ký.
- Ký hợp đồng phải có xác nhận đã đọc văn bản, chữ ký hợp lệ, hash nội dung hợp đồng và thông tin audit tối thiểu.
- Document hợp đồng và chữ ký là dữ liệu cá nhân, không được phục vụ như static public nếu chưa kiểm tra quyền.

## Code còn lệch spec hoặc cần bổ sung
- `ModulePermissionFilter` hiện bảo vệ `/employee/*` bằng `VIEW_EMPLOYEE_DETAIL`, chưa tách quyền riêng cho payroll cá nhân, contract cá nhân và ký hợp đồng.
- File chữ ký nằm dưới `/Upload/signatures/*`; `SessionSecurityFilter` đang bỏ qua `/Upload/*` như static resource nên chữ ký có nguy cơ bị truy cập trực tiếp nếu đoán được URL.
- `GET /employee/contract/document` đã có ownership guard nhưng chưa có header bảo mật/cache, audit lượt tải và chính sách retention rõ ràng.
- Chưa có CSRF token cho form ký hợp đồng.
- Cần test employee chưa có payroll/contract/document và test sửa `contractId`, `payrollId` của người khác.

## Kiểm thử tối thiểu
- Employee A không xem được payroll, contract document hoặc ký hợp đồng của Employee B bằng cách sửa request.
- Contract ở `Pending_Approval`, `Rejected`, `Expired` hoặc thiếu document không ký được.
- Signature quá ngắn, quá lớn hoặc không phải PNG base64 bị từ chối.
- Ký thành công chuyển contract hiện tại sang `Active`, lưu thông tin chữ ký và expire contract `Active` cũ của cùng employee.
