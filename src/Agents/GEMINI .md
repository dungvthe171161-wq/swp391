# GEMINI.md — Gemini Project Memory
# Đọc file AGENTS.md trước để hiểu full project context

## MANUAL MEMORY (human-maintained)

### Architecture Decisions (ADR)
# ADR-001: Use Traditional HTTP Session & Cookies for user authentication to strictly align with SRS requirements.
# ADR-002: Use JDBC + DAO Pattern instead of heavy ORMs for transparent SQL control and simpler debugging.
# ADR-003: Use MVC architecture separating Controller, Service, DAO, and Model responsibilities.
# ADR-004: Use MySQL 8.0 as the primary database (Database name: hrm_db).
# ADR-005: Integrate Google OAuth2 cho tính năng Sign-up/Login, account tạo mới tự động gán role Guest.
# ADR-006: Sử dụng JDK 17 và Apache Tomcat 10.1.x.
# ADR-007: Giao diện Auth áp dụng Design System tối giản (phong cách Starbucks: font Inter/Manrope, bo góc 50px, hover scale 0.95).
# ADR-008: Use JUnit 4 kết hợp với JaCoCo để đảm bảo đo lường được test coverage cho các class nghiệp vụ.

### Lessons Learned (from incidents & code reviews)
# LESSON-001: Always index foreign keys to avoid slow joins and N+1 query issues.
# LESSON-002: Đảm bảo Maven `<java.version>17</java.version>` trong file pom.xml để tương thích hoàn toàn với môi trường JDK 17 mặc định, tránh lỗi compiler "invalid target release".
# LESSON-003: Lỗi "context failed to start" trên Tomcat: Không bao giờ dùng `javax.servlet.*` trong Tomcat 10. Bắt buộc replace bằng `jakarta.servlet.*`.
# LESSON-004: Lỗi Font/Encoding tiếng Việt: Luôn set `-Dfile.encoding=UTF-8` cho JVM, thêm cấu hình UTF-8 vào đầu file JSP, web.xml và IDE.
# LESSON-005: Lỗi Route Dashboard: HomepageController phải chứa logic Auto-redirect dựa trên role (admin -> AdminHome, hr -> ProfileManagementController -> HrHome, employee -> EmployeeHome) thay vì redirect mù.
# LESSON-006: Hash passwords using SHA-256 algorithm to match the SRS security baseline — never store plain text passwords. Always use PreparedStatement for SQL.

### Current Sprint Notes
# Sprint 1 focus: Authentication and Employee Management module
# Completed:
- UI Đăng nhập Google & Clone thiết kế Starbucks.
- Fix lỗi route từ Homepage đến HR Dashboard.
- Cấu hình môi trường JDK 17 + Tomcat 10.1 + Maven 3.9.x.
- Sửa lỗi Encoding tiếng Việt (UTF-8).
# In Progress:
- Luồng Forgot/Reset/Change Password.
- Tích hợp HTTP Session logic thực tế với Frontend.
- Employee CRUD module (Employment Status, Task Management).
- Thiết lập environment cho JUnit 4 và cấu hình plugin JaCoCo trong `pom.xml`.
# Blocked:
- None
# Next:
- Hoàn thiện Role-based access control (Admin/HR/Dept/Employee/Guest).
- Pagination & Search/Filter cho Employee.

## PATTERNS TO FOLLOW
... (Giữ nguyên các Rule Naming, Testing, Error Handling như cũ)

## AI ASSISTANT NOTES
# Gemini/AI Agents should:
- Tuân thủ cấu trúc thư mục và naming convention.
- KHÔNG BAO GIỜ sinh ra mã chứa credentials.
- Luôn kiểm tra imports: Dùng `jakarta.*`, KHÔNG DÙNG `javax.*`.
- Thiết kế UI form cần bám sát hệ thống CSS Tailwind.
- Try-with-resources cho JDBC Code.
- Khi sinh code unit test, sử dụng syntax của JUnit 4 (`@Test`, `org.junit.Test`, `org.junit.Assert.*`) và target Java 17.