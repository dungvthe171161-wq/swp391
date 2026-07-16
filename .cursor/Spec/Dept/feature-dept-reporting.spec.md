# Tính năng Dept: Báo cáo phòng ban

Trạng thái: Màn hình tổng hợp sơ khai, đã rà soát theo code ngày 2026-07-14; chưa có report contract hoặc export hoàn chỉnh.
Ngôn ngữ: tiếng Việt có dấu. Spec này không mặc định báo cáo/export đã được triển khai.

## Actor và phạm vi
- Dept Manager xem báo cáo tổng hợp của đúng phòng ban mình quản lý.

## Route, controller và JSP liên quan
- `GET /dept?action=reports`.
- Controller: `DeptController`; dữ liệu hiện có từ `DeptDashboardDAO`, `EmployeeDAO`, `MailRequestDAO`.
- JSP: `Views/DeptManager/reports.jsp`.
- Nguồn tương lai có thể gồm task, work schedule và attendance sau khi được chốt.

## Hiện trạng code
- Controller hiện tải thuộc tính dùng chung rồi forward JSP.
- Chưa có route report riêng, date range, DTO, aggregation service hoặc export PDF/Excel.
- Số liệu hiện tại không nên được xem là báo cáo kiểm toán hay bảng lương chính thức.

## Quy tắc nghiệp vụ chuẩn
- Phải chốt từng loại báo cáo, nguồn dữ liệu, công thức, timezone và trạng thái bản ghi được tính.
- Mọi báo cáo bị scope theo `DepartmentID` từ session, không lấy phòng ban tùy ý từ client.
- Date range phải hợp lệ và có giới hạn; số liệu hiển thị phải nêu thời điểm tạo và bộ lọc.
- Export phải áp dụng cùng permission và scope như màn hình, đồng thời chống CSV formula injection.
- File export nhạy cảm phải tải qua endpoint có authorization, không lưu công khai trong web root.
- Báo cáo tổng hợp không được lộ dữ liệu cá nhân vượt nhu cầu quản lý.

## Code còn lệch spec hoặc cần bổ sung
- Chưa có catalog báo cáo, report service, permission riêng và audit export.
- Chưa chốt export format, giới hạn bản ghi hoặc xử lý tác vụ báo cáo lớn.
- Cần phân biệt dashboard realtime, báo cáo quản trị và báo cáo chính thức đã khóa kỳ.

## Kiểm thử tối thiểu
- Scope đúng phòng ban cho màn hình và file export.
- Date range sai/quá lớn bị từ chối; timezone và trạng thái được tính chính xác.
- Export không chứa công thức nguy hiểm hoặc trường nhạy cảm ngoài quyền.
- Tổng số liệu phải đối soát được với dữ liệu nguồn của cùng kỳ.
