# Feature: HR Staff - Contract Management
Status: Approved | Priority: High
Related Files: ContractListController.java, CreateContractController.java

## 1. Context & Goal
Cho phép HR Staff quản lý hợp đồng lao động. Dữ liệu hợp đồng là cơ sở gốc để tính toán lương.

## 2. Actors & Roles
- **HR Staff**: Có `HttpSession` hợp lệ, role `ROLE_HR_STAFF` và quyền `VIEW_CONTRACTS`.

## 3. Functional Requirements (EARS)
- THE SYSTEM SHALL kiểm tra quyền truy cập thông qua `PermissionUtil.ensureRolePermission` cho mọi request.
- WHILE HR Staff truy cập `/hrstaff/contracts`, THE SYSTEM SHALL phân trang dữ liệu với kích thước cố định `PAGE_SIZE = 10`.
- WHEN HR Staff truy cập `/hrstaff/contracts/create`, THE SYSTEM SHALL query toàn bộ danh sách `Employee` để fill vào dropdown.
- WHEN HR Staff submit form tạo mới hợp đồng, THE SYSTEM SHALL gán trạng thái mặc định là `Draft` nếu không có tham số status truyền lên.

## 4. Non-Functional Requirements
- Tech Stack: Bắt buộc dùng `jakarta.servlet.*` cho Tomcat 10.1+.

## 5. Data Model
- Entities: `Contract`, `Employee`.
- Fields quan trọng: StartDate, EndDate, BaseSalary, Allowance, ContractType, Status.

## 6. Error Handling (Unwanted Patterns)
- WHERE người dùng thiếu quyền, THE SYSTEM SHALL chặn request và báo lỗi "You do not have permission to manage contracts".
- WHERE có lỗi `SQLException` hoặc Exception khác, THE SYSTEM SHALL thiết lập attribute `error`, forward về trang hiện tại và ghi log bằng `java.util.logging.Logger` (Tuyệt đối KHÔNG dùng `e.printStackTrace()`).

## 7. Acceptance Criteria
- [ ] Truy cập danh sách hợp đồng trả về đúng 10 records/trang.
- [ ] User không phải HR Staff bị chặn quyền.
- [ ] Không cho phép ngày kết thúc (EndDate) trước ngày bắt đầu (StartDate).

## 8. Out of Scope
- KHÔNG hỗ trợ việc chuyển status hợp đồng sang `Active` (thuộc quyền HR Manager).
- KHÔNG gộp chung workflow duyệt hợp đồng vào Spec này.
