# Tính năng: Lịch phỏng vấn của Khách (Guest Interview Schedule) Giai đoạn 2
Trạng thái: Bản thảo
Tác nhân: Ứng viên tự do (Guest Candidate), HR, HR Staff
Độ ưu tiên: Trung bình
Dữ liệu liên quan: `Interview`, `Application`, `Employee`

## Mục tiêu
Quản lý thông tin lịch hẹn và kết quả các vòng phỏng vấn của ứng viên gắn liền với từng hồ sơ ứng tuyển (application) cụ thể.

## Luồng công việc mục tiêu (Target workflow)
1. Bộ phận HR/HR Staff khởi tạo lịch phỏng vấn cho một hồ sơ ứng tuyển cụ thể.
2. Hệ thống tiến hành tạo mới bản ghi trong bảng `Interview`.
3. Người dùng Guest nhìn thấy lịch hẹn phỏng vấn sắp tới hiển thị trên bảng điều khiển (dashboard) cá nhân của mình.
4. Sau khi buổi phỏng vấn kết thúc, HR tiến hành cập nhật kết quả phỏng vấn vào các trường `Result` và `Status`.
5. Nếu ứng viên vượt qua vòng phỏng vấn (passed), hồ sơ ứng tuyển (application) có thể được chuyển sang bước gửi thư mời nhận việc (offer).

## Các trường thông tin hiển thị cho Guest
- Tên công việc ứng tuyển (Job title).
- Vòng phỏng vấn (Round number).
- Thời gian phỏng vấn.
- Địa điểm phỏng vấn hoặc đường dẫn phòng họp trực tuyến (meeting link).
- Trạng thái phỏng vấn hiện tại.

## Tiêu chí nghiệm thu
- [ ] Một hồ sơ ứng tuyển (application) có thể được thiết lập nhiều vòng phỏng vấn (interview round) khác nhau.
- [ ] Người dùng Guest chỉ nhìn thấy thông tin lịch phỏng vấn thuộc về hồ sơ ứng tuyển của chính mình.
- [ ] Bộ phận HR/HR Staff có quyền cập nhật thông tin lịch hẹn phỏng vấn và kết quả phỏng vấn.
- [ ] Các cuộc phỏng vấn bị hủy bỏ hoặc thay đổi lịch phỏng vấn phải được hiển thị thông báo rõ ràng trên giao diện.
