# Tính năng: Thư mời nhận việc của Khách (Guest Offer) Giai đoạn 2
Trạng thái: Bản thảo
Tác nhân: Ứng viên tự do (Guest Candidate), HR, HR Staff
Độ ưu tiên: Trung bình
Dữ liệu liên quan: `Offer`, `Application`

## Mục tiêu
Quản lý quy trình gửi và phản hồi thư mời nhận việc (offer) sau khi ứng viên vượt qua các vòng phỏng vấn thành công.

## Luồng công việc mục tiêu (Target workflow)
1. Bộ phận HR khởi tạo thông tin thư mời nhận việc (offer) cho hồ sơ ứng tuyển đã đạt yêu cầu.
2. Thư mời nhận việc ban đầu được đặt trạng thái là `Draft` (Bản thảo).
3. Khi thực hiện gửi thư mời tới ứng viên, trạng thái được cập nhật thành `Sent` (Đã gửi).
4. Ứng viên Guest xem chi tiết nội dung thư mời nhận việc trên portal và đưa ra quyết định: chấp nhận (accept) hoặc từ chối (reject).
5. Nếu ứng viên chọn chấp nhận (`Accepted`), trạng thái hồ sơ ứng tuyển (application) sẽ tự động được cập nhật thành `Hired` (Đã tuyển dụng).
6. Bộ phận HR tiến hành tạo thông tin nhân viên mới (Employee) từ tài khoản Guest sau khi trạng thái thư mời nhận việc chuyển thành `Accepted`.
7. Hệ thống tự động khởi tạo thông báo (notification) tương ứng gửi cho các bên khi thư mời nhận việc được gửi đi hoặc khi ứng viên phản hồi.

## Quy tắc chuyển đổi sang tài khoản Nhân viên (Employee)
- Giai đoạn 1: Bộ phận HR có thể thực hiện tạo thông tin Nhân viên khi trạng thái hồ sơ ứng tuyển chuyển thành `Guest.Status = Hired`.
- Giai đoạn 2: Chỉ cho phép hệ thống tạo thông tin Nhân viên khi trạng thái thư mời nhận việc chính thức chuyển thành `Offer.Status = Accepted`.

## Tiêu chí nghiệm thu
- [ ] Chỉ cho phép tạo mới thư mời nhận việc đối với các hồ sơ ứng tuyển hợp lệ.
- [ ] Người dùng Guest chỉ xem được thư mời nhận việc gửi riêng cho tài khoản của chính mình.
- [ ] Bắt buộc phải có trạng thái thư mời nhận việc là `Accepted` mới kích hoạt cho phép HR thực hiện chuyển tài khoản ứng viên thành Nhân viên chính thức.
- [ ] Thư mời nhận việc đã quá hạn phản hồi (expired offer) sẽ bị hệ thống khóa và không cho phép ứng viên chọn chấp nhận.
- [ ] Nếu ứng viên chọn từ chối thư mời nhận việc, hệ thống không cho phép chuyển tài khoản đó thành Nhân viên.
- [ ] Các thông báo (notification) phải được tự động khởi tạo khi thư mời nhận việc được gửi đi, được chấp nhận hoặc bị từ chối.
