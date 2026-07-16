# Đặc tả dùng chung: Cấu hình và quản lý secret

Trạng thái: Bổ sung chuẩn mục tiêu theo đối chiếu code ngày 2026-07-14.
Ngôn ngữ: tiếng Việt có dấu. Spec này áp dụng cho môi trường development, test và production.

## Actor và phạm vi
- Developer, người triển khai hệ thống và các component đọc cấu hình database, mail, Google OAuth và AI provider.

## Route, controller và JSP liên quan
- `DBConnection`, `AiProviderConfig`, `GoogleAuthController`, các service gửi mail.
- `META-INF/db.properties`, `db.example.properties`, `mail.example.properties`, `google.example.properties`.
- Biến môi trường tương ứng với database, mail, OAuth và AI key.

## Hiện trạng code
- Dự án hỗ trợ một phần cấu hình từ environment/property file.
- `db.properties` là file local nhạy cảm và hiện có nguy cơ chứa credential/merge conflict.
- File example có thể chứa chuỗi trông giống credential thay vì placeholder rỗng.

## Quy tắc nghiệp vụ chuẩn
- Không commit password, API key, client secret, token hoặc credential thật vào Git kể cả file example.
- Thứ tự ưu tiên cấu hình phải rõ: environment/secret store, file local không commit, rồi giá trị mặc định an toàn.
- Production phải fail fast khi thiếu cấu hình bắt buộc; không fallback sang credential mặc định.
- Log và error response không được chứa secret, connection string có password hoặc token OAuth.
- Cấu hình phải tách theo môi trường và có quy trình rotate/revoke khi nghi ngờ lộ secret.
- File example chỉ chứa tên key, mô tả và placeholder vô hại.

## Code còn lệch spec hoặc cần bổ sung
- Cần giải quyết conflict `db.properties`, bảo đảm file thật không được track và rotate credential nếu đã lộ.
- Cần làm sạch `mail.example.properties` và rà lịch sử Git.
- Cần validation cấu hình tập trung và thông báo thiếu config không lộ dữ liệu nhạy cảm.
- Cần tài liệu deployment cho từng biến môi trường.

## Kiểm thử tối thiểu
- Build/test không phụ thuộc secret thật.
- Production khởi động thất bại rõ ràng khi thiếu config bắt buộc nhưng không in secret.
- Scanner secret không phát hiện credential trong source và file example.
- Kiểm tra thứ tự ưu tiên environment so với file local.
