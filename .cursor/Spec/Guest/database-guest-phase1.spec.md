# Đặc tả Cơ sở dữ liệu: Khách truy cập (Guest) Giai đoạn 1
Trạng thái: Đã phê duyệt
Tác nhân: Ứng viên tự do (Guest Candidate), HR, Admin
Độ ưu tiên: Cao
Tập tin liên quan: `src/data/migrations/2026-06-22_guest_phase1_profile.sql`

## Lược đồ (Schema) hiện tại đã kiểm tra
Cơ sở dữ liệu (Database): `hrm_db`

Bảng `Guest` hiện có:
- `GuestID`
- `FullName`
- `Email`
- `Phone`
- `CV`
- `Status`
- `RecruitmentID`
- `AppliedDate`

Bảng `Recruitment` hiện có:
- `RecruitmentID`
- `JobTitle`
- `JobDescription`
- `Requirement`
- `Location`
- `Salary`
- `Status`
- `PostedDate`
- `Applicant`

Các bảng không bắt buộc cho Giai đoạn 1 (Phase 1):
- `Application`
- `Interview`
- `Offer`
- `Notification`

## Cột cần thêm cho Giai đoạn 1
Thêm vào bảng `Guest`:
- `UserID INT NULL`
- `Avatar VARCHAR(500) NULL`
- `Gender VARCHAR(20) NULL`
- `DateOfBirth DATE NULL`
- `Address VARCHAR(255) NULL`
- `UpdatedDate DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP`

## Lý do thêm cột
- `UserID`: Liên kết bản ghi Guest với tài khoản đăng nhập, tránh chỉ dựa vào địa chỉ email.
- `Avatar`: Phục vụ trang thông tin cá nhân (profile) của Guest.
- `Gender`: Thông tin cá nhân cơ bản (giới tính).
- `DateOfBirth`: Thông tin cá nhân cơ bản (ngày sinh).
- `Address`: Thông tin liên hệ (địa chỉ).
- `UpdatedDate`: Theo dõi thời điểm cập nhật cuối cùng.

## Không thực hiện trong Giai đoạn 1
Giai đoạn 1 không phụ thuộc vào các bảng `Application`, `Interview`, `Offer`, `Notification`. Các bảng này thuộc Giai đoạn 2 và đã có đặc tả/tập tin migration riêng để lập trình luồng công việc (workflow) mới.

Ghi chú cập nhật:
- Giai đoạn 2 đã có đặc tả và script SQL riêng tại `database-guest-phase2.spec.md`.
- Code Giai đoạn 1 vẫn hoàn toàn độc lập và không phụ thuộc vào 4 bảng của Giai đoạn 2.
- Khi viết mã cho Giai đoạn 2, không được xóa các cột của Giai đoạn 1 trong bảng `Guest` để tránh phá vỡ các luồng xử lý cũ.

## Mã nguồn cần cập nhật khi bắt đầu triển khai (implement)
- `Guest.java`: Thêm các trường/getter/setter cho `userId`, `avatar`, `gender`, `dateOfBirth`, `address`, `updatedDate`.
- `GuestDAO`: Ánh xạ (map) các cột mới trong các hàm get/list/search.
- `GuestDAO.insert`: Khi người dùng đã đăng nhập, thiết lập giá trị `UserID` để liên kết hồ sơ với tài khoản đăng nhập.
- `GuestDAO.update`: Cho phép người dùng cập nhật thông tin cá nhân mà không làm mất liên kết `RecruitmentID` cũ.
- Controller Guest portal mới: Ưu tiên truy vấn theo `UserID`, tự động dự phòng (fallback) theo email đối với các dữ liệu cũ.

## Quy tắc chuyển đổi dữ liệu (Migration rule)
- Chỉ thêm các cột cho phép nhận giá trị null (nullable) để tránh phá vỡ các dữ liệu cũ.
- Không thực hiện xóa cột (drop column).
- Không thay đổi kiểu liệt kê `Guest.Status` trong Giai đoạn 1.
- Không xóa cột `Guest.RecruitmentID` trong Giai đoạn 1.
- Tạo chỉ mục (index) cho các trường `UserID`, `Email`, `RecruitmentID + Status` để hỗ trợ tối ưu truy vấn trên cổng thông tin (portal).

## Tiêu chí nghiệm thu
- [ ] Script migration chạy bình thường trên cơ sở dữ liệu hiện tại.
- [ ] Luồng nộp hồ sơ (apply) hiện có vẫn thực hiện thêm (insert) dữ liệu được vào bảng `Guest`.
- [ ] Các dữ liệu cũ trong bảng `Guest` không bị mất mát hoặc ảnh hưởng.
- [ ] Cổng thông tin khách (Guest portal) có đầy đủ các cột cần thiết để xây dựng trang profile và danh sách hồ sơ đã nộp của Giai đoạn 1.
