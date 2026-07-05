# Đặc tả dùng chung: Kế hoạch kiểm thử

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Toàn bộ HRMS sau khi sửa code hoặc spec nghiệp vụ.

## Route, controller và JSP liên quan
- Maven build: `mvn -q compile`.
- Unit/integration test dưới `src/test` nếu có.
- Manual test theo route actor và dữ liệu seed.

## Hiện trạng code
- Maven compile hiện pass với warning Jansi/native access.
- Một số lỗi deploy servlet như trùng mapping có thể không bị compile phát hiện.
- Test tự động hiện chưa bao phủ đầy đủ workflow tuyển dụng và phân quyền.

## Quy tắc nghiệp vụ chuẩn
- Mỗi actor phải có smoke test đăng nhập, mở dashboard, truy cập route bị cấm.
- Workflow có thay đổi trạng thái phải test cả thành công và thất bại.
- Spec thay đổi permission phải test role không đủ quyền.

## Code còn lệch spec hoặc cần bổ sung
- Cần test container/deploy để bắt lỗi trùng `/viewTask`.
- Cần test database cho enum Application/Offer/Interview.
- Cần test bảo mật password sau khi chuyển sang hash.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

