# Đặc tả dùng chung: Tải lên CV

Trạng thái: Đã rà soát theo code ngày 2026-07-13.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Guest/PublicCandidate nộp hồ sơ và HR xem CV ứng viên.

## Route, controller và JSP liên quan
- `RecruitmentController`: nhận hồ sơ ứng tuyển public/guest.
- `GuestPortalController`: lưu `CandidateProfile` và CV ở `/guest/profile`.
- `ViewCV`: HR xem CV qua route `/viewCV`.

## Hiện trạng code
- Guest profile chấp nhận CV `pdf`, `doc`, `docx` tối đa 10MB.
- File được lưu dưới thư mục upload và tên file được sinh UUID ở một số luồng.
- `ViewCV` GET/POST ưu tiên tham số `applicationId`; nếu không có thì fallback `guestId` (legacy).

## Quy tắc nghiệp vụ chuẩn
- CV của lần ứng tuyển phải gắn với `Application`, không chỉ với `Guest`.
- HR xem CV phải xem đúng CV của application đang xét.
- File upload phải kiểm tra extension, kích thước và đường dẫn lưu an toàn.

## Code còn lệch spec hoặc cần bổ sung
- Fallback `guestId` trên `ViewCV` vẫn còn; cần loại bỏ khi toàn bộ JSP/link đã chuyển sang `applicationId`.
- Fallback POST legacy vẫn cập nhật `Guest.Status` thay vì chỉ `Application`.
- Cần thống nhất nơi lưu CV giữa `CandidateProfile` và `Application`.
- Cần test nhiều application của cùng một Guest.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.
