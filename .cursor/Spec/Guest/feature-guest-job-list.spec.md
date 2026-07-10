# Tính năng: Guest xem danh sách và chi tiết việc làm
Trạng thái: Đã cập nhật theo luồng CandidateProfile
Tác nhân: Guest Candidate
Độ ưu tiên: Cao
Mã nguồn liên quan: `RecruitmentController`, `RecruitmentDAO`, `Views/Recruitment.jsp`

## Route hiện tại
- `GET /RecruitmentController`
- `GET /RecruitmentController?action=apply&recruitmentId={recruitmentId}`

## Luồng chính
1. Guest truy cập trang danh sách việc làm.
2. Controller lấy các tin tuyển dụng đang hiển thị công khai.
3. Giao diện hiển thị thông tin việc làm hiện có: tên vị trí, địa điểm, mức lương, số lượng, mô tả, yêu cầu và các trường recruitment đang có.
4. Guest bấm `Ứng tuyển ngay`.
5. Nếu chưa đăng nhập, hệ thống yêu cầu đăng nhập/đăng ký và lưu đường dẫn quay lại job.
6. Sau khi đăng nhập, hệ thống quay lại đúng tin tuyển dụng.
7. Nếu đã đăng nhập và chưa có `CandidateProfile`, hệ thống hiển thị form hồ sơ ứng tuyển.
8. Nếu đã đăng nhập và đã có `CandidateProfile`, hệ thống chuyển thẳng sang màn xác nhận ứng tuyển.

## Ghi chú dữ liệu tuyển dụng
Luồng này không thêm mới database cho:
- Department.
- Benefit.
- Deadline.

Nếu các thông tin này đã tồn tại trong model/database hiện tại thì giao diện có thể hiển thị. Nếu chưa tồn tại, không tạo schema mới chỉ để phục vụ luồng ứng tuyển này.

## Acceptance Criteria
- [x] Guest chưa đăng nhập vẫn xem được danh sách việc làm.
- [x] Nút ứng tuyển trỏ đúng `action=apply&recruitmentId={id}`.
- [x] Guest chưa đăng nhập được yêu cầu đăng nhập và quay lại đúng job sau login.
- [x] Guest chưa có profile thấy form hồ sơ.
- [x] Guest đã có profile thấy màn xác nhận ứng tuyển.
- [x] Lỗi hệ thống không lộ stack trace ra giao diện.
