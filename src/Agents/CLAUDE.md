# CLAUDE.md — Human Resources Management (HRMS) Project Memory
# Đọc file AGENTS.md trước để hiểu full project context và rules

## TL;DR (Đọc trước — 60 giây)
> Hệ thống quản lý nhân sự (HRMS) theo kiến trúc monolithic.
> Backend: Java 17 + Jakarta EE 10 (Servlet/JSP) chạy trên Apache Tomcat 10.1.x.
> Frontend: JSP + Tailwind CSS 3.x (UI Starbucks Design System, canvas #f2f0eb).
> Database: MySQL 8.0.
> Authentication: HTTP Session & Cookies + SHA-256 + Google OAuth2.
> Testing: JUnit 4 + JaCoCo.

## KIẾN TRÚC HỆ THỐNG
### Flow xử lý request cơ bản:
Client (Browser) → Controller (Servlet) → Service (Business Logic) → DAO (Database Access) → MySQL 8.0 Database.

### Phân luồng điều hướng (Routing):
Hệ thống sử dụng Auto-redirect dựa trên role tại `HomepageController`:
- Admin → `AdminHome.jsp`
- HR → `ProfileManagementController` → `HrHome.jsp`
- Employee → `EmployeeHome.jsp`

## QUYẾT ĐỊNH KIẾN TRÚC QUAN TRỌNG (ADR)
### ADR-001: Sử dụng Session & Cookies thay vì JWT
Lý do: Để tuân thủ chặt chẽ yêu cầu bảo mật ban đầu từ tài liệu SRS, hệ thống quản lý phiên đăng nhập theo kiểu truyền thống. Mật khẩu được mã hóa SHA-256.

### ADR-002: Sử dụng JDBC + Pattern thay vì các Framework ORM nặng
Lý do: Cần kiểm soát SQL rõ ràng, minh bạch và dễ dàng debug hơn. Tránh các vấn đề N+1 query thường gặp.

### ADR-003: Sử dụng Java 17 & Tomcat 10.1.x
Lý do: Java 17 là phiên bản LTS ổn định phù hợp với môi trường hiện tại của nhóm. Trade-off khi dùng Tomcat 10.1.x: Bắt buộc phải thay đổi toàn bộ namespace từ `javax.servlet.*` sang `jakarta.servlet.*`.

## PATTERNS ĐƯỢC SỬ DỤNG
### Kiến trúc 3 Lớp (Clean Architecture):
- **Repository/DAO Pattern:** Lớp DAO CHỈ xử lý tương tác Database. Luôn sử dụng Prepared Statements và try-with-resources.
- **Service Pattern:** Lớp Service CHỈ xử lý business logic, trung gian giữa Controller và DAO.
- **Thin Controller:** Lớp Controller CHỈ làm nhiệm vụ tiếp nhận Request và Auto-redirect.

## NHỮNG GÌ ĐÃ KHÔNG HOẠT ĐỘNG (Lessons Learned)
- **[Lỗi sập Context Tomcat]:** Đã từng import `javax.servlet.*` gây ra lỗi context failed to start. Đã giải quyết bằng cách thay thế toàn bộ bằng `jakarta.servlet.*`.
- **[Lỗi Font/Encoding Tiếng Việt]:** Đã fix bằng cách cấu hình UTF-8 ở 3 nơi: `-Dfile.encoding=UTF-8` trong JVM, trong `web.xml` và set header trên từng trang JSP.
- **[Lỗi Route Dashboard Trống Dữ Liệu]:** Đã fix bằng cách cho routing đi qua `ProfileManagementController` để load danh sách nhân viên trước khi forward sang View.
- **[Lỗi JDK Compiler]:** Ide báo lỗi "invalid target release". Fix bằng cách đồng bộ pom.xml `<java.version>17</java.version>` khớp với biến môi trường `JAVA_HOME`.
Bạn hãy kiểm tra lại và cập nhật những cấu hình này cho dự án nhé. Cần điều chỉnh 