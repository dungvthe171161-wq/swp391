# Tính năng: Guest xem trang ứng tuyển thành công
Trạng thái: Đã cập nhật theo luồng CandidateProfile
Tác nhân: Guest Candidate
Độ ưu tiên: Trung bình
Mã nguồn liên quan: `Views/Success.jsp`, `RecruitmentController`

## Route/JSP hiện tại
- `/Views/Success.jsp`

## Luồng chính
1. Guest bấm `Xác nhận ứng tuyển` trên màn `ApplyConfirm.jsp`.
2. Controller tạo `Application` liên kết `Guest`, `Recruitment`, `CandidateProfile`.
3. Controller chuyển tới trang thành công.
4. Trang hiển thị thông báo ứng tuyển thành công.
5. Guest có thể quay lại danh sách việc làm hoặc trang chủ.

## Nội dung bắt buộc
- Thông báo ứng tuyển thành công.
- Không yêu cầu Guest nhập lại thông tin hồ sơ.
- Link quay lại danh sách việc làm.
- Link quay lại trang chủ.
- Không hiển thị thông tin lỗi kỹ thuật/stack trace.

## Acceptance Criteria
- [x] Trang thành công chỉ hiển thị sau khi tạo `Application` thành công.
- [x] Nếu application bị trùng, không hiển thị thành công mà hiển thị `Bạn đã ứng tuyển công việc này.`.
- [x] Nội dung hiển thị bằng tiếng Việt.
- [x] Có link quay lại danh sách việc làm.
