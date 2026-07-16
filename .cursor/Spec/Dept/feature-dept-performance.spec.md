# Tính năng Dept: Theo dõi hiệu suất

Trạng thái: Màn hình sơ khai, đã rà soát theo code ngày 2026-07-14; chưa có mô hình tính điểm hiệu suất được chốt.
Ngôn ngữ: tiếng Việt có dấu. Spec này không mô tả chỉ số hiệu suất như chức năng đã hoàn thành.

## Actor và phạm vi
- Dept Manager xem số liệu hiệu suất của nhân viên thuộc phòng ban mình quản lý.

## Route, controller và JSP liên quan
- `GET /dept?action=performance`.
- Controller: `DeptController`; dữ liệu liên quan có thể đến từ `TaskDAO`, attendance và leave sau khi nghiệp vụ được phê duyệt.
- JSP: `Views/DeptManager/performance.jsp`.

## Hiện trạng code
- Controller hiện chỉ chuẩn bị thuộc tính dùng chung và forward JSP.
- Chưa có service/DAO chuyên tính performance, kỳ đánh giá, trọng số hoặc bản ghi đánh giá.
- Màn hình chưa phải source of truth cho lương, kỷ luật hoặc quyết định nhân sự.

## Quy tắc nghiệp vụ chuẩn
- Phải định nghĩa công thức trước khi hiển thị điểm: nguồn dữ liệu, trọng số, kỳ tính và quy tắc làm tròn.
- Chỉ dùng dữ liệu có chất lượng và trạng thái cuối; không coi task mới tạo hoặc leave hợp lệ là điểm trừ tùy ý.
- Nhân viên phải có quyền biết tiêu chí và kỳ dữ liệu được dùng nếu điểm ảnh hưởng quyền lợi.
- Manager chỉ xem phòng ban của mình; chỉnh điểm thủ công phải có permission, lý do và audit.
- Không dùng performance tự động làm quyết định kỷ luật/lương nếu chưa có bước review của con người.
- Dữ liệu lịch sử phải bất biến theo kỳ sau khi khóa.

## Code còn lệch spec hoặc cần bổ sung
- Chưa có công thức, bảng kỳ đánh giá, DAO/service hoặc workflow review.
- Chưa có permission `VIEW_DEPARTMENT_PERFORMANCE`/`MANAGE_PERFORMANCE`.
- Cần chốt quyền riêng tư, audit và quy trình khiếu nại/chỉnh sửa dữ liệu.

## Kiểm thử tối thiểu
- Công thức cho kết quả xác định với cùng dữ liệu và xử lý đúng dữ liệu thiếu.
- Không xem được nhân viên phòng ban khác hoặc kỳ bị hạn chế.
- Kỳ đã khóa không bị thay đổi khi task nguồn được sửa về sau, trừ luồng điều chỉnh có audit.
- Kiểm tra biên không chia cho 0, nhân viên mới và kỳ không có task.
