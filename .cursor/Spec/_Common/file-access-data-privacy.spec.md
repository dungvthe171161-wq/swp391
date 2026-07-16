# Đặc tả dùng chung: Truy cập file và bảo vệ dữ liệu cá nhân

Trạng thái: Bổ sung chuẩn mục tiêu theo đối chiếu code ngày 2026-07-14.
Ngôn ngữ: tiếng Việt có dấu. Spec này bổ sung cho `upload-cv.spec.md` và các luồng tài liệu hợp đồng.

## Actor và phạm vi
- Guest, Employee, HR Staff, HR Manager, Admin và mọi người dùng truy cập CV, hợp đồng hoặc file chứa dữ liệu cá nhân.

## Route, controller và JSP liên quan
- `/Upload/cvs/*`, `CvFileServlet`, `UploadPathUtil`, `RecruitmentController`, `ViewCV`.
- `CreateContractController`, `ContractListController`, `ContractDocumentDAO` và các trang xem hợp đồng.
- `SessionSecurityFilter` hiện phân loại `/Upload/*` như static resource.

## Hiện trạng code
- CV được phục vụ inline theo tên file qua `CvFileServlet`.
- `SessionSecurityFilter` bỏ qua xác thực cho toàn bộ đường dẫn `/Upload/*`.
- Upload có kiểm tra riêng ở một số controller nhưng quyền đọc file chưa có ownership/permission tập trung.

## Quy tắc nghiệp vụ chuẩn
- CV và hợp đồng là dữ liệu riêng tư, không được coi là static resource công khai.
- Guest chỉ xem file thuộc hồ sơ/application của mình; Employee chỉ xem hợp đồng của mình.
- HR Staff/HR Manager/Admin chỉ xem khi có permission và đúng phạm vi nghiệp vụ.
- Server phải resolve đường dẫn an toàn, chống `..`, encoded traversal, symlink escape và MIME giả.
- Tên lưu trữ phải khó đoán; tên gốc chỉ dùng khi tạo `Content-Disposition` sau khi sanitize.
- Phải đặt `X-Content-Type-Options: nosniff`, cache policy phù hợp và allowlist loại file.
- Có chính sách retention, xóa, audit truy cập và không ghi nội dung/file path nhạy cảm vào log.

## Code còn lệch spec hoặc cần bổ sung
- Cần bỏ `/Upload/*` khỏi danh sách static public và thêm authorization tại endpoint tải file.
- `CvFileServlet` cần tra cứu metadata/ownership thay vì chỉ nhận tên file.
- Cần thống nhất nơi lưu file ngoài web root và chính sách cho contract document.
- Chưa có quy tắc antivirus/content scanning và retention.

## Kiểm thử tối thiểu
- Anonymous, user sai ownership và user thiếu permission không tải được file.
- Kiểm tra path traversal thường, URL encoded, tên file lạ, MIME giả và file không tồn tại.
- User hợp lệ xem đúng file nhưng không suy đoán được file khác.
- Header bảo mật, cache và tên download phải đúng.
