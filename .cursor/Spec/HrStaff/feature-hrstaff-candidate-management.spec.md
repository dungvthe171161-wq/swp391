# Feature: HR Staff - Recruitment & Candidate Management
Status: Approved | Priority: High
Related Files: PostRecruitmentController.java, DetailRecruitmentCreate.java, ViewCandidateController.java, ViewCV.java

## 1. Context & Goal
Cho phép HR Staff đăng bài tuyển dụng mới, quản lý danh sách ứng viên (Guest) và xem CV.

## 2. Actors & Roles
- **HR Staff**: Yêu cầu có quyền `VIEW_RECRUITMENT`.

## 3. Functional Requirements (EARS)
- WHEN HR Staff POST data lên `/detailRecruitmentCreate`, THE SYSTEM SHALL lấy thông tin (Title, Description, Requirement, Location, Salary, Applicant) và insert bản ghi `Recruitment` mới.
- WHILE xem danh sách ứng viên tại `/candidates`, THE SYSTEM SHALL phân trang với `pageSize = 5`.
- WHEN HR Staff filter ứng viên, THE SYSTEM SHALL hỗ trợ tìm kiếm kết hợp theo: `searchByName`, `filterStatus`, `startDate`, và `endDate`.
- WHEN HR Staff gọi `/viewCV` kèm ID hợp lệ, THE SYSTEM SHALL fetch dữ liệu ứng viên và forward đến JSP xem CV.

## 4. Non-Functional Requirements
- Encoding: Chuỗi thông báo redirect phải được encode UTF-8 bằng `URLEncoder.encode()`.

## 5. Data Model
- Entities: `Recruitment`, `Guest`. Status ứng viên: New, Reviewing, Interview, Passed, Rejected.

## 6. Error Handling (Unwanted Patterns)
- WHERE người dùng chưa đăng nhập, THE SYSTEM SHALL đẩy về `/login`.
- WHERE ID bị thiếu hoặc không đúng định dạng integer, THE SYSTEM SHALL catch `NumberFormatException`, log bằng `Logger` và redirect kèm URL param báo lỗi.

## 7. Acceptance Criteria
- [ ] Tìm kiếm ứng viên kết hợp cả Tên và Khoảng thời gian hoạt động đúng.
- [ ] Phân trang danh sách ứng viên chuẩn 5 items/page.
- [ ] Sau khi tạo bài tuyển dụng thành công, tự động quay về `/postRecruitments`.

## 8. Out of Scope
- KHÔNG xử lý luồng duyệt trúng tuyển chuyển ứng viên thành Employee trong module này.
