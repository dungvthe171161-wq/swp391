# Đặc tả dùng chung: Truy cập file và bảo vệ dữ liệu cá nhân

Trạng thái: Đã rà soát theo code ngày 2026-07-22.
Ngôn ngữ: tiếng Việt có dấu. Spec này bổ sung cho `upload-cv.spec.md`, document hợp đồng và chữ ký điện tử.

## Actor và phạm vi
- Guest, Employee, HR Staff, HR Manager, Admin và mọi người dùng truy cập CV, hợp đồng, chữ ký hoặc file chứa dữ liệu cá nhân.

## Route, controller và JSP liên quan
- `/Upload/cvs/*`, `CvFileServlet`, `UploadPathUtil`, `RecruitmentController`, `ViewCV`.
- `/employee/contract/document`, `EmployeePortalController`, `ContractDocumentDAO`, `ContractDocument`.
- `/Upload/signatures/*`, `ContractDAO.EmployeeSignaturePath`, `Views/Employee/Contract.jsp`.
- `CreateContractController`, `ContractListController` và các trang xem hợp đồng.
- `SessionSecurityFilter` hiện phân loại `/Upload/*` như static resource.

## Hiện trạng code
- CV được phục vụ inline theo tên file qua `CvFileServlet`.
- `ViewCV` ưu tiên `applicationId`; `guestId` chỉ còn fallback legacy.
- Contract document của Employee được phục vụ qua `/employee/contract/document` và có ownership guard `Contract.EmployeeID == employeeId`.
- Chữ ký hợp đồng được lưu thành PNG dưới `/Upload/signatures/*` và được JSP hiển thị lại bằng đường dẫn này.
- `SessionSecurityFilter` bỏ qua xác thực cho toàn bộ đường dẫn `/Upload/*`.

## Quy tắc nghiệp vụ chuẩn
- CV, hợp đồng và chữ ký là dữ liệu riêng tư, không được coi là static resource công khai.
- Guest chỉ xem file thuộc hồ sơ/application của mình; Employee chỉ xem hợp đồng và chữ ký của mình.
- HR Staff/HR Manager/Admin chỉ xem khi có permission và đúng phạm vi nghiệp vụ.
- Server phải resolve đường dẫn an toàn, chống `..`, encoded traversal, symlink escape và MIME giả.
- Tên lưu trữ phải khó đoán; tên gốc chỉ dùng khi tạo `Content-Disposition` sau khi sanitize.
- Phải đặt `X-Content-Type-Options: nosniff`, cache policy phù hợp và allowlist loại file.
- Có chính sách retention, xóa, audit truy cập và không ghi nội dung/file path nhạy cảm vào log.

## Code còn lệch spec hoặc cần bổ sung
- Cần bỏ `/Upload/*` khỏi danh sách static public hoặc tách CV/chữ ký sang endpoint có authorization.
- `CvFileServlet` cần tra cứu metadata/ownership thay vì chỉ nhận tên file.
- `/employee/contract/document` đã có ownership guard nhưng chưa có header bảo mật/cache và audit lượt tải.
- Chữ ký `/Upload/signatures/*` hiện có nguy cơ public nếu đoán được URL.
- Cần thống nhất nơi lưu file ngoài web root, antivirus/content scanning và retention.

## Kiểm thử tối thiểu
- Anonymous, user sai ownership và user thiếu permission không tải được CV, contract document hoặc chữ ký.
- Kiểm tra path traversal thường, URL encoded, tên file lạ, MIME giả và file không tồn tại.
- Employee hợp lệ tải đúng document của mình nhưng không suy đoán được document/chữ ký của người khác.
- Header bảo mật, cache và tên download phải đúng.
