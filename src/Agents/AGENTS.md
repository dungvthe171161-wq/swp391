# AGENTS.md - Ngữ cảnh dự án cho AI Agents
# Phiên bản: 1.5 | Cập nhật: 2026-06-12 | Dự án: Human Resources Management

## 1. Tổng quan dự án

Tên dự án: Human Resources Management (HRMS)

Loại dự án: Ứng dụng web quản lý nhân sự

Lĩnh vực: Quản trị nhân sự, tuyển dụng, hợp đồng, phân quyền, chấm công và lương

Giai đoạn: Đang phát triển

## 2. Công nghệ sử dụng

Backend: Java 17, Servlet + JSP, Jakarta EE, bắt buộc dùng `jakarta.servlet.*`

Frontend: JSP + HTML + CSS + JavaScript, có Bootstrap ở một số trang, dùng giao diện custom theo phong cách Starbucks/BetterHR

Giao diện: Nền Neutral Warm `#f2f0eb`, xanh chính `#006241`, CTA `#00754A`, dark band `#1E3932`, font thay thế là Inter

Database: MySQL 8.0, database chính là `hrm_db`

Truy cập dữ liệu: JDBC + DAO Pattern, bắt buộc dùng `PreparedStatement`

Server: Apache Tomcat 10.1.x

Build: Maven, đóng gói WAR

Xác thực: HTTP Session & Cookies, BCrypt cho mật khẩu, Google OAuth2 đang phát triển

Testing: JUnit 4 + JaCoCo là mục tiêu, hiện `pom.xml` chưa cấu hình đầy đủ

Logging: SLF4J đã có dependency, cần dần thay `System.out.println` bằng logging chuẩn

## 3. Kiến trúc hiện tại và định hướng

Kiến trúc hiện tại: MVC + DAO.

Controller: Nhận request, kiểm tra session/role, validate dữ liệu đầu vào, gọi DAO hoặc service nếu có, sau đó forward/redirect.

DAO: Chỉ xử lý tương tác database, dùng `PreparedStatement`, đóng tài nguyên bằng try-with-resources.

Model/Entity/DTO: Chứa dữ liệu nghiệp vụ, không chứa logic truy cập database.

Service layer: Là định hướng refactor cho các chức năng mới hoặc logic nghiệp vụ phức tạp. Không bắt buộc thêm service layer lớn khi chỉ sửa JSP/CSS.

API/JSON endpoint: Nếu tạo endpoint mới trả JSON thì thiết kế theo hướng RESTful và trả HTTP status code phù hợp.

## 4. Quy tắc đặt tên và cấu trúc

Class Java: PascalCase, ví dụ `EmployeeService.java`, `RecruitmentController.java`.

Method và biến: camelCase, ví dụ `getEmployeeById()`.

Route mới: ưu tiên kebab-case, ví dụ `/api/user-profile`.

Bảng database hiện tại có thể chưa đồng nhất; khi tạo bảng mới thì ưu tiên snake_case.

JSP: Giữ theo cấu trúc hiện tại trong `src/main/webapp/Views`.

CSS dùng chung: Ưu tiên đặt trong `src/main/webapp/css`.

## 5. Quy tắc bảo mật bắt buộc

- Không import `javax.servlet.*`; luôn dùng `jakarta.servlet.*` vì dự án chạy Tomcat 10.1.x.
- Không lưu password database, Google API Key, Google Client Secret hoặc secret khác dạng plain text trong Git.
- Không hardcode secret mới. Ưu tiên biến môi trường hoặc file cấu hình không commit.
- Không bỏ qua validate dữ liệu đầu vào ở cả frontend và backend.
- Không viết raw SQL bằng nối chuỗi input người dùng; luôn dùng `PreparedStatement`.
- Không xóa file trong `/data` hoặc `/uploads` nếu chưa có xác nhận rõ ràng từ người dùng.
- Không để logic nghiệp vụ phức tạp nằm trực tiếp trong controller khi viết chức năng mới.

## 6. Quy tắc giao diện

Giao diện phải theo phong cách Starbucks/BetterHR đã thống nhất:

- Canvas chính: `#f2f0eb`
- Card: `#ffffff`, bo góc 12px, shadow nhẹ
- Heading/brand: `#006241` hoặc `#1E3932`
- CTA chính: `#00754A`, chữ trắng, button bo pill 50px
- Dark band/nav: `#1E3932`
- Text chính: `rgba(0, 0, 0, 0.87)`
- Text phụ: `rgba(0, 0, 0, 0.58)`
- Font: Inter hoặc fallback `"Helvetica Neue", Helvetica, Arial, sans-serif`
- Không dùng gradient tím/xanh dương cũ cho layout mới
- Không dùng font Poppins/Segoe UI cho trang mới nếu không có lý do
- Khi sửa JSP/CSS, giữ nguyên logic, route, action và name của form

## 7. Definition of Done cho mỗi task

- File được lưu UTF-8.
- Không làm hỏng logic form/action/route hiện có.
- Không revert thay đổi không liên quan của người dùng.
- Nếu sửa backend, cần validate input và xử lý exception rõ ràng.
- Nếu sửa database access, dùng `PreparedStatement` và try-with-resources.
- Nếu có thể chạy test/build thì phải chạy và báo kết quả.
- Nếu không chạy được do thiếu Maven/Tomcat hoặc môi trường, phải báo rõ.
- Không để TODO mới trong code.

## 8. Git conventions

Branch đề xuất:

- `feat/[ten-chuc-nang]`
- `fix/[ten-loi]`
- `spec/[ten-spec]`

Commit đề xuất:

`[type]: [scope] - [description]`

Ví dụ:

`feat(auth): add Google OAuth2 sign-in`

## 9. Ngữ cảnh sprint hiện tại

Sprint: Sprint 1

Trọng tâm:

- Xác thực đăng nhập, đăng ký và Google Auth
- Dashboard tự điều hướng theo role
- Employee CRUD
- Đồng bộ giao diện HRMS theo bộ màu BetterHR/Starbucks

Spec liên quan:

- `/spec/authentication.md`
- `/spec/employee-management.md`

## 10. Ghi chú kỹ thuật hiện tại

- `pom.xml` đang dùng Java 17, chưa phải Java 21.
- Chưa có Maven Wrapper `mvnw`.
- Chưa thấy Tailwind CSS được cấu hình trong repo.
- Chưa thấy JUnit 4 và JaCoCo được cấu hình đầy đủ trong `pom.xml`.
- `db.properties` có tồn tại, nhưng một số code vẫn còn hardcode database URL.
- Repo vẫn còn một số `System.out.println`; cần refactor dần sang logging.
