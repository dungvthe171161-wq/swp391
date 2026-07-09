# Đặc tả dùng chung: Nhật ký kiểm toán

Trạng thái: Đã rà soát theo code ngày 2026-07-02.
Ngôn ngữ: tiếng Việt có dấu. Spec này mô tả đúng hiện trạng code; phần chưa đúng được ghi rõ ở mục cần sửa trong code.

## Actor và phạm vi
- Admin, HR Manager, HR Staff và các thao tác thay đổi dữ liệu nhạy cảm.

## Route, controller và JSP liên quan
- Các controller quản lý user, role, tuyển dụng, hợp đồng, payroll và employee.
- Database có thể dùng bảng audit hiện có hoặc bổ sung bảng chuyên trách nếu thiếu.
- Log nghiệp vụ phải gắn `UserID`, entity và thời điểm.

## Hiện trạng code
- Code hiện chưa có audit service thống nhất cho mọi workflow.
- Một số thao tác chỉ ghi log console hoặc không ghi.
- Spec cũ yêu cầu audit nhưng chưa được triển khai đầy đủ.

## Quy tắc nghiệp vụ chuẩn
- Tạo/sửa/xóa user, role, permission, department phải có audit.
- Duyệt hợp đồng, duyệt payroll, reject/hire ứng viên phải có audit.
- Audit không được chứa mật khẩu plain text hoặc dữ liệu bí mật.

## Code còn lệch spec hoặc cần bổ sung
- Cần tạo service audit dùng chung.
- Cần đưa audit vào transaction của workflow quan trọng.
- Cần màn hình hoặc truy vấn cho Admin xem audit.

## Kiểm thử tối thiểu
- Chạy `mvn -q compile` sau khi thay đổi code liên quan.
- Kiểm tra đăng nhập đúng actor và truy cập đúng route chính.
- Kiểm tra trường hợp không có quyền phải bị chặn bằng redirect hoặc JSON lỗi phù hợp.

