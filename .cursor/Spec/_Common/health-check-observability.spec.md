# Đặc tả dùng chung: Health check và khả năng quan sát

Trạng thái: Bổ sung chuẩn mục tiêu theo đối chiếu code ngày 2026-07-14.
Ngôn ngữ: tiếng Việt có dấu. Spec này áp dụng cho endpoint chẩn đoán, logging và giám sát vận hành.

## Actor và phạm vi
- Người vận hành, hệ thống giám sát và Admin được cấp quyền chẩn đoán.

## Route, controller và JSP liên quan
- `DBConnectionTest`, `DBConnection` và các component kết nối database, mail, OAuth, AI.
- Logger trong controller, DAO và service; `SystemLog` chỉ dùng cho audit nghiệp vụ, không thay thế application log.

## Hiện trạng code
- Có servlet `DBConnectionTest` để kiểm tra kết nối database.
- Logging chưa đồng nhất; code còn `System.out`, `printStackTrace` và nhiều loại logger.
- Chưa có contract health/readiness/liveness hoặc correlation ID dùng chung.

## Quy tắc nghiệp vụ chuẩn
- Endpoint chẩn đoán chi tiết chỉ bật ở development hoặc yêu cầu Admin/permission riêng.
- Health response không được lộ DB URL, username, password, SQL exception, file path hoặc secret.
- Liveness chỉ phản ánh process; readiness kiểm tra dependency bắt buộc với timeout ngắn.
- Log phải có timestamp, level, component và correlation/request ID; dữ liệu cá nhân phải được mask.
- Audit log và application log có mục đích, retention và quyền truy cập riêng.
- Không dùng endpoint health để thực hiện mutation hoặc tự sửa dữ liệu.

## Code còn lệch spec hoặc cần bổ sung
- Cần giới hạn hoặc loại bỏ `DBConnectionTest` khỏi production.
- Cần chuẩn hóa logging và thay `System.out`/`printStackTrace`.
- Cần correlation ID, timeout dependency và chính sách retention.
- Trang homepage không được dùng audit log làm nguồn tin công khai.

## Kiểm thử tối thiểu
- Anonymous production không truy cập được chẩn đoán chi tiết.
- Dependency lỗi trả trạng thái phù hợp nhưng không lộ thông tin nhạy cảm.
- Correlation ID đi xuyên qua log của một request.
- Log không chứa password, token, CV, nội dung hợp đồng hoặc dữ liệu cá nhân không cần thiết.
