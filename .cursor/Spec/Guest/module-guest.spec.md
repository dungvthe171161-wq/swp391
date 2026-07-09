# Đặc tả module Guest: Cổng ứng viên

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Guest là ứng viên có tài khoản, xem tuyển dụng, nộp hồ sơ và theo dõi application.

## Route, controller và JSP liên quan
- `/guest`, `/guest/dashboard`, `/guest/applications`, `/guest/profile`, `/guest/notification/read`, `/guest/offer/respond`.
- `/RecruitmentController` cho trang tuyển dụng và nộp hồ sơ.
- Controller: `GuestPortalController`, `RecruitmentController`; JSP: `Views/Guest/*`.

## Hiện trạng code
- Guest phải đăng nhập để vào portal `/guest/*`.
- Profile dùng `Guest` và `CandidateProfile`, có xác thực email bằng mã 6 số.
- Danh sách application lấy qua `ApplicationDAO.findByUserId`.
- Offer response gọi `OfferDAO.respondOffer`; accept hiện chuyển application sang `Hired`.

## Quy tắc nghiệp vụ chuẩn
- Guest chỉ xem application, interview, offer và notification của chính mình.
- CV của từng lần ứng tuyển phải gắn đúng `Application`.
- Không tự chuyển thành Employee nếu chưa có thao tác HR chuẩn.

## Ma trận route, quyền và dữ liệu cần bổ sung
| Nhóm chức năng | Route/controller | Yêu cầu truy cập | Bảng dữ liệu chính | Ghi chú |
|---|---|---|---|---|
| Dashboard ứng viên | `/guest/dashboard`, `GuestPortalController` | Đăng nhập role Guest hoặc Admin được hỗ trợ nếu cần | `Guest`, `CandidateProfile`, `Application`, `Interview`, `Offer`, `Notification` | Dữ liệu theo user hiện tại. |
| Hồ sơ ứng viên | `/guest/profile` | Guest đã đăng nhập | `Guest`, `CandidateProfile` | Email profile cần xác thực bằng mã. |
| Danh sách application | `/guest/applications` | Guest đã đăng nhập | `Application`, `Recruitment`, `Interview`, `Offer` | Không xem application của user khác. |
| Đọc notification | `/guest/notification/read` | Guest đã đăng nhập | `Notification` | Chỉ đánh dấu notification của chính mình. |
| Phản hồi offer | `/guest/offer/respond` | Guest đã đăng nhập và offer thuộc user | `Offer`, `Application` | Chỉ phản hồi offer còn hợp lệ. |
| Xem việc làm/nộp hồ sơ | `/RecruitmentController` | Public hoặc Guest tùy màn hình | `Recruitment`, `Application`, `CandidateProfile` | Cần ghi rõ route nào public, route nào bắt buộc login. |

## Ranh giới Guest public và Guest đã đăng nhập
- Trang xem danh sách/chi tiết job có thể public nếu chỉ đọc dữ liệu tuyển dụng đang mở.
- Nộp hồ sơ cần xác định rõ: hoặc bắt buộc đăng nhập Guest, hoặc tạo hồ sơ public có cơ chế liên kết user sau.
- Portal `/guest/*` luôn yêu cầu session `systemUser` và role Guest hoặc rule admin-test rõ ràng.
- Nếu user đã là Employee thì không nên đi vào portal Guest trừ khi có rule chuyển vai trò rõ.

## Source of truth và workflow cần bổ sung
- `Application` là nguồn sự thật cho từng lần ứng tuyển; `Guest` chỉ là hồ sơ ứng viên dùng chung.
- Một Guest có thể có nhiều application, nhưng không được nộp trùng cùng một recruitment nếu chưa có rule cho phép nộp lại.
- CV khi nộp job phải được lưu hoặc tham chiếu theo `Application`, không chỉ lấy CV mới nhất của Guest.
- Interview và Offer phải tham chiếu `ApplicationID`; UI Guest hiển thị timeline theo từng application.
- Nếu cần bước “đã nhận offer nhưng chờ HR tạo employee”, phải thêm enum/migration và không nhảy thẳng `Hired`.

## Notification và audit bắt buộc
- Nộp hồ sơ thành công tạo notification xác nhận cho Guest và notification xử lý cho HR Staff.
- Lên lịch phỏng vấn, đổi lịch, reject, gửi offer, offer hết hạn hoặc offer được phản hồi phải tạo notification cho Guest.
- Guest đọc notification hoặc phản hồi offer chỉ tác động dữ liệu của chính user hiện tại.
- Phản hồi offer cần audit hoặc event log gồm `offerId`, `applicationId`, trạng thái cũ/mới và thời điểm.

## Checklist nghiệm thu riêng cho Guest
- Guest A không xem được application, interview, offer, notification của Guest B.
- Nộp trùng cùng một recruitment bị chặn hoặc được xử lý theo rule nộp lại đã ghi rõ.
- Offer không thuộc user hiện tại hoặc đã hết hạn không phản hồi được.
- Một Guest có nhiều application vẫn xem đúng CV/timeline/offer theo từng application.
- Accept offer không xóa `Guest` và không làm mất lịch sử tuyển dụng nếu áp dụng workflow mới.

## Code còn lệch spec hoặc cần bổ sung
- Code hiện chuyển offer accepted thành `Hired` ngay, chưa có trạng thái chờ tạo Employee riêng.
- `CreateEmployeeController` hiện xóa `Guest`, trái với yêu cầu giữ lịch sử.
- Cần thống nhất source of truth giữa `Guest`, `CandidateProfile` và `Application`.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.
