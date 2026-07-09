# HR Staff Recruitment Workflow Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Hoàn thiện workflow HR Staff từ Application → Screening → Interview → Offer → Create Employee, giữ lịch sử Guest và bảo đảm mọi thay đổi nghiệp vụ quan trọng chạy trong transaction.

**Architecture:** Giữ MVC/JDBC hiện tại nhưng đưa toàn bộ state transition vào `RecruitmentWorkflowService`. Controller chỉ validate HTTP và permission; DAO hỗ trợ nhận cùng một `Connection`; `Application.Status` là nguồn sự thật duy nhất. Các route cũ được giữ dưới dạng redirect tương thích trong giai đoạn đầu, không xóa ngay.

**Tech Stack:** Java 17, Jakarta Servlet 6, JSP/JSTL, JDBC, MySQL 8, Maven, JUnit 4, Mockito.

**Design reference:** `docs/superpowers/specs/2026-06-24-hr-staff-recruitment-workflow-design.md`

---

## 1. Kết quả audit code hiện có

### Giữ và mở rộng

| File/phần hiện có | Quyết định | Lý do |
|---|---|---|
| `Application`, `Interview`, `Offer`, `Notification` entities | Giữ, sửa field/status | Đúng hướng domain mới |
| `ApplicationDAO`, `InterviewDAO`, `OfferDAO`, `NotificationDAO` | Giữ, refactor | Có CRUD cơ bản; cần overload nhận `Connection` và query cho HR Staff |
| `GuestDAO.findProfileByUserOrEmail()` | Giữ tạm | Hỗ trợ legacy; bổ sung ưu tiên `UserID` và xử lý merge |
| `RecruitmentController` phần upload CV | Giữ | CV đã được đưa vào Application; chuyển orchestration sang service |
| `GuestPortalController` | Giữ, refactor | Đã đọc Application/Interview/Offer/Notification |
| `Views/HrStaff/ViewCandidate.jsp` | Dùng làm nền UI | Thay data model Guest bằng Application summary và đổi theme đúng BetterHR |
| `Views/hr/CreateEmployee.jsp` | Dùng lại form | Đổi đầu vào từ `guestId` sang `applicationId`, prefill từ Application/Guest/Offer |
| `/candidates`, `/viewCV` | Giữ redirect tương thích | Tránh làm hỏng menu/link cũ trong một lần triển khai |

### Phải thay thế hành vi

| File/phần hiện có | Vấn đề | Cách xử lý |
|---|---|---|
| `ViewCandidateController` | Đọc bảng Guest, dùng `VIEW_RECRUITMENT` | Chuyển thành redirect hoặc adapter sang `/hrstaff/applications`; controller mới dùng `MANAGE_APPLICANTS` |
| `ViewCV.java` | POST cập nhật Guest thẳng thành Hired/Rejected và gửi mail | Vô hiệu hóa POST; route cũ redirect sang Application detail |
| `ViewCV.jsp` | Đọc `Guest.CV` | Trang mới đọc `Application.CV` |
| `CreateEmployeeController` | Dùng `VIEW_EMPLOYEES`, tự phối hợp DAO, xóa Guest | Dùng `CREATE_EMPLOYEE`, gọi transaction service, tuyệt đối không xóa Guest |
| `Guest.Status`, `Guest.RecruitmentID`, `Guest.CV` | Trộn profile với workflow | Không ghi mới; giữ cột để tương thích migration |
| `OfferDAO.respondOffer()` | Transaction nằm trong DAO và chỉ đồng bộ một phần | Chuyển orchestration sang service |
| `Application.CurrentStep` | Trùng nghĩa với Status | Ngừng sử dụng; migration giữ cột tạm nếu cần |

### Chưa xóa trong đợt đầu

- Không xóa `Guest.Status`, `Guest.RecruitmentID`, `Guest.CV`.
- Không xóa ngay `ViewCV.java`, `ViewCV.jsp`, `ViewCandidateController`.
- Không xóa DAO/entity mới đang là untracked work của người dùng.
- Chỉ xóa code legacy ở một cleanup riêng sau khi `rg` xác nhận không còn caller và regression test đã pass.

---

## 2. Cấu trúc file mục tiêu

### Tạo mới

- `src/main/java/com/hrm/service/recruitment/ApplicationStatus.java`
- `src/main/java/com/hrm/service/recruitment/InterviewResult.java`
- `src/main/java/com/hrm/service/recruitment/OfferStatus.java`
- `src/main/java/com/hrm/service/recruitment/RecruitmentAction.java`
- `src/main/java/com/hrm/service/recruitment/RecruitmentTransitionPolicy.java`
- `src/main/java/com/hrm/service/recruitment/WorkflowException.java`
- `src/main/java/com/hrm/service/recruitment/WorkflowConflictException.java`
- `src/main/java/com/hrm/service/recruitment/WorkflowValidationException.java`
- `src/main/java/com/hrm/service/recruitment/ConnectionProvider.java`
- `src/main/java/com/hrm/service/recruitment/RecruitmentWorkflowService.java`
- `src/main/java/com/hrm/service/recruitment/WorkflowEmailDispatcher.java`
- `src/main/java/com/hrm/model/dto/ApplicationSummary.java`
- `src/main/java/com/hrm/model/dto/ApplicationDetail.java`
- `src/main/java/com/hrm/controller/hrstaff/ApplicationListController.java`
- `src/main/java/com/hrm/controller/hrstaff/ApplicationDetailController.java`
- `src/main/java/com/hrm/controller/hrstaff/ApplicationWorkflowController.java`
- `src/main/java/com/hrm/controller/hrstaff/OfferExpiryController.java`
- `src/main/webapp/Views/HrStaff/ApplicationList.jsp`
- `src/main/webapp/Views/HrStaff/ApplicationDetail.jsp`
- `src/main/webapp/css/hrstaff-recruitment.css`
- `src/data/migrations/2026-06-24_hrstaff_workflow_core.sql`
- `src/data/migrations/2026-06-24_hrstaff_guest_merge_report.sql`
- `src/test/java/com/hrm/service/recruitment/RecruitmentTransitionPolicyTest.java`
- `src/test/java/com/hrm/service/recruitment/RecruitmentWorkflowServiceTest.java`

### Sửa

- `pom.xml`
- `src/data/data.sql`
- `src/main/java/com/hrm/model/entity/Application.java`
- `src/main/java/com/hrm/model/entity/Interview.java`
- `src/main/java/com/hrm/model/entity/Offer.java`
- `src/main/java/com/hrm/dao/ApplicationDAO.java`
- `src/main/java/com/hrm/dao/InterviewDAO.java`
- `src/main/java/com/hrm/dao/OfferDAO.java`
- `src/main/java/com/hrm/dao/NotificationDAO.java`
- `src/main/java/com/hrm/dao/GuestDAO.java`
- `src/main/java/com/hrm/dao/EmployeeDAO.java`
- `src/main/java/com/hrm/dao/SystemUserDAO.java`
- `src/main/java/com/hrm/dao/SystemLogDAO.java`
- `src/main/java/com/hrm/controller/RecruitmentController.java`
- `src/main/java/com/hrm/controller/guest/GuestPortalController.java`
- `src/main/java/com/hrm/controller/hrstaff/ViewCandidateController.java`
- `src/main/java/com/hrm/controller/hr/ViewCV.java`
- `src/main/java/com/hrm/controller/hr/CreateEmployeeController.java`
- `src/main/java/com/hrm/filter/RoleAuthorizationFilter.java`
- `src/main/webapp/Views/Guest/Applications.jsp`
- `src/main/webapp/Views/Guest/Dashboard.jsp`
- `src/main/webapp/Views/hr/CreateEmployee.jsp`
- `src/main/webapp/Views/HrStaff/HrStaffHome.jsp`

---

### Task 1: Thiết lập test foundation và baseline

**Files:**
- Modify: `pom.xml`
- Create: `src/test/java/com/hrm/service/recruitment/RecruitmentTransitionPolicyTest.java`

- [ ] **Step 1: Thêm JUnit 4 và Mockito**

Thêm vào `<dependencies>`:

```xml
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.13.2</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.12.0</version>
    <scope>test</scope>
</dependency>
```

Thêm Surefire:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.2.5</version>
</plugin>
```

- [ ] **Step 2: Tạo failing smoke test**

```java
package com.hrm.service.recruitment;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class RecruitmentTransitionPolicyTest {
    @Test
    public void appliedCanStartScreening() {
        assertTrue(RecruitmentTransitionPolicy.isAllowed(
                ApplicationStatus.APPLIED,
                RecruitmentAction.START_SCREENING));
    }
}
```

- [ ] **Step 3: Chạy test để xác nhận fail**

Run:

```powershell
& 'C:\Program Files\NetBeans-17\netbeans\java\maven\bin\mvn.cmd' -q test
```

Expected: FAIL vì các class domain chưa tồn tại.

- [ ] **Step 4: Ghi baseline working tree**

Run:

```powershell
git status --short
git diff --stat
```

Expected: thấy các thay đổi Guest portal/entity/DAO hiện có của người dùng; không revert hoặc đưa nhầm vào commit của task nếu chưa chỉnh.

- [ ] **Step 5: Commit**

```powershell
git add pom.xml src/test/java/com/hrm/service/recruitment/RecruitmentTransitionPolicyTest.java
git commit -m "test(recruitment): add workflow test foundation"
```

---

### Task 2: Định nghĩa trạng thái và transition policy

**Files:**
- Create: `src/main/java/com/hrm/service/recruitment/ApplicationStatus.java`
- Create: `src/main/java/com/hrm/service/recruitment/InterviewResult.java`
- Create: `src/main/java/com/hrm/service/recruitment/OfferStatus.java`
- Create: `src/main/java/com/hrm/service/recruitment/RecruitmentAction.java`
- Create: `src/main/java/com/hrm/service/recruitment/RecruitmentTransitionPolicy.java`
- Create: `src/main/java/com/hrm/service/recruitment/WorkflowException.java`
- Create: `src/main/java/com/hrm/service/recruitment/WorkflowConflictException.java`
- Create: `src/main/java/com/hrm/service/recruitment/WorkflowValidationException.java`
- Modify: `src/test/java/com/hrm/service/recruitment/RecruitmentTransitionPolicyTest.java`

- [ ] **Step 1: Viết toàn bộ failing transition tests**

Bao phủ ít nhất:

```java
@Test public void appliedCanReject() { assertAllowed(APPLIED, CONFIRM_REJECT); }
@Test public void appliedCannotScheduleInterview() { assertDenied(APPLIED, SCHEDULE_INTERVIEW); }
@Test public void screeningCanScheduleInterview() { assertAllowed(SCREENING, SCHEDULE_INTERVIEW); }
@Test public void interviewScheduledCanRecordResult() { assertAllowed(INTERVIEW_SCHEDULED, RECORD_INTERVIEW_RESULT); }
@Test public void interviewCompletedCanCreateDraft() { assertAllowed(INTERVIEW_COMPLETED, CREATE_OFFER_DRAFT); }
@Test public void offerSentCanBeAccepted() { assertAllowed(OFFER_SENT, ACCEPT_OFFER); }
@Test public void offerAcceptedCanCreateEmployee() { assertAllowed(OFFER_ACCEPTED, CREATE_EMPLOYEE); }
@Test public void hiredIsTerminal() { assertDenied(HIRED, CONFIRM_REJECT); }
```

- [ ] **Step 2: Chạy test và xác nhận fail**

Expected: FAIL do policy chưa triển khai.

- [ ] **Step 3: Tạo enum dùng đúng database values**

```java
public enum ApplicationStatus {
    APPLIED("Applied"),
    SCREENING("Screening"),
    INTERVIEW_SCHEDULED("InterviewScheduled"),
    INTERVIEW_COMPLETED("InterviewCompleted"),
    OFFER_DRAFT("OfferDraft"),
    OFFER_SENT("OfferSent"),
    OFFER_ACCEPTED("OfferAccepted"),
    OFFER_DECLINED("OfferDeclined"),
    OFFER_EXPIRED("OfferExpired"),
    REJECTED("Rejected"),
    WITHDRAWN("Withdrawn"),
    HIRED("Hired");

    private final String dbValue;
    ApplicationStatus(String dbValue) { this.dbValue = dbValue; }
    public String dbValue() { return dbValue; }
}
```

Tạo enum tương tự cho `InterviewResult`, `OfferStatus`, `RecruitmentAction`.

- [ ] **Step 4: Triển khai policy bằng map bất biến**

```java
private static final Map<ApplicationStatus, Set<RecruitmentAction>> ALLOWED = Map.ofEntries(
    entry(APPLIED, EnumSet.of(START_SCREENING, CONFIRM_REJECT)),
    entry(SCREENING, EnumSet.of(SCHEDULE_INTERVIEW, CONFIRM_REJECT)),
    entry(INTERVIEW_SCHEDULED, EnumSet.of(RESCHEDULE_INTERVIEW, RECORD_INTERVIEW_RESULT, CONFIRM_REJECT)),
    entry(INTERVIEW_COMPLETED, EnumSet.of(CREATE_OFFER_DRAFT, CONFIRM_REJECT)),
    entry(OFFER_DRAFT, EnumSet.of(UPDATE_OFFER_DRAFT, SEND_OFFER, CANCEL_OFFER)),
    entry(OFFER_SENT, EnumSet.of(ACCEPT_OFFER, DECLINE_OFFER, EXPIRE_OFFER, CANCEL_OFFER)),
    entry(OFFER_DECLINED, EnumSet.of(CREATE_OFFER_DRAFT)),
    entry(OFFER_EXPIRED, EnumSet.of(CREATE_OFFER_DRAFT)),
    entry(OFFER_ACCEPTED, EnumSet.of(CREATE_EMPLOYEE))
);
```

Lưu ý: điều kiện Interview phải `Passed` trước khi tạo Offer nằm trong service, không chỉ trong status map.

- [ ] **Step 5: Chạy test**

Expected: tất cả test policy PASS.

- [ ] **Step 6: Commit**

```powershell
git add src/main/java/com/hrm/service/recruitment src/test/java/com/hrm/service/recruitment
git commit -m "feat(recruitment): define application transition policy"
```

---

### Task 3: Migration ổn định Guest/Application/Interview/Offer

**Files:**
- Create: `src/data/migrations/2026-06-24_hrstaff_guest_merge_report.sql`
- Create: `src/data/migrations/2026-06-24_hrstaff_workflow_core.sql`
- Modify: `src/data/data.sql`

- [ ] **Step 1: Tạo migration report không thay đổi dữ liệu**

Report phải trả:

```sql
SELECT UserID, COUNT(*) AS GuestCount, GROUP_CONCAT(GuestID ORDER BY GuestID) AS GuestIDs
FROM Guest
WHERE UserID IS NOT NULL
GROUP BY UserID
HAVING COUNT(*) > 1;
```

Thêm report trùng `(canonical Guest, RecruitmentID)` để biết trước các Application sẽ conflict khi merge.

- [ ] **Step 2: Tạo migration core idempotent**

Migration phải:

1. Mở rộng `Application.Status` với toàn bộ status trong spec.
2. Thêm `RejectedReason TEXT NULL`.
3. Thêm `HiredEmployeeID INT NULL`.
4. Thêm `HiredAt DATETIME NULL`.
5. Thêm FK `HiredEmployeeID → Employee(EmployeeID) ON DELETE SET NULL`.
6. Bỏ unique `uq_offer_application`.
7. Thêm generated column `ActiveApplicationID`, nhận `ApplicationID` khi status là `Draft`/`Sent`, ngược lại là null; đặt unique index trên cột này để MySQL cho phép nhiều Offer lịch sử nhưng chỉ một Offer đang hoạt động.
8. Thêm `CreatedByUserID` cho Offer.
9. Đổi Offer enum `Rejected` thành `Declined`.
10. Thêm unique `Interview(ApplicationID)`.
11. Sau khi merge sạch, thêm unique `Guest(UserID)`.

- [ ] **Step 3: Viết merge procedure có audit mapping**

Tạo bảng:

```sql
CREATE TABLE IF NOT EXISTS GuestMergeMap (
    SourceGuestID INT PRIMARY KEY,
    TargetGuestID INT NOT NULL,
    MergedAt DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

Không hard-delete Guest phụ trong migration đầu. Đặt `UserID = NULL` cho source sau khi chuyển quan hệ và lưu mapping.

- [ ] **Step 4: Đồng bộ schema bootstrap**

Cập nhật `src/data/data.sql` để database tạo mới giống migration cuối, nhưng vẫn giữ các cột Guest legacy.

- [ ] **Step 5: Kiểm tra SQL thủ công trên database copy**

Run các query:

```sql
SELECT UserID, COUNT(*) FROM Guest WHERE UserID IS NOT NULL GROUP BY UserID HAVING COUNT(*) > 1;
SELECT ApplicationID, Status, CurrentStep FROM Application;
SELECT ApplicationID, COUNT(*) FROM Interview GROUP BY ApplicationID HAVING COUNT(*) > 1;
SELECT ApplicationID, SUM(Status IN ('Draft','Sent')) ActiveOffers FROM Offer GROUP BY ApplicationID HAVING ActiveOffers > 1;
```

Expected: không còn duplicate UserID/Interview/active Offer.

- [ ] **Step 6: Commit**

```powershell
git add src/data/data.sql src/data/migrations/2026-06-24_hrstaff_guest_merge_report.sql src/data/migrations/2026-06-24_hrstaff_workflow_core.sql
git commit -m "feat(database): stabilize recruitment workflow schema"
```

---

### Task 4: Cập nhật entity và HR Staff query DTO

**Files:**
- Modify: `src/main/java/com/hrm/model/entity/Application.java`
- Modify: `src/main/java/com/hrm/model/entity/Interview.java`
- Modify: `src/main/java/com/hrm/model/entity/Offer.java`
- Create: `src/main/java/com/hrm/model/dto/ApplicationSummary.java`
- Create: `src/main/java/com/hrm/model/dto/ApplicationDetail.java`

- [ ] **Step 1: Viết test mapping/status label**

Thêm test xác nhận:

- `OfferDeclined` có label riêng.
- `OfferExpired` có label riêng.
- Offer `Declined` không dùng `Rejected`.
- Application không còn phụ thuộc `currentStep`.

- [ ] **Step 2: Sửa Application entity**

Thêm:

```java
private String rejectedReason;
private Integer hiredEmployeeId;
private LocalDateTime hiredAt;
```

Giữ getter/setter `currentStep` tạm thời chỉ để tương thích binary/source trong task này, đánh dấu `@Deprecated`; code mới không dùng.

- [ ] **Step 3: Sửa Offer entity**

Thêm `createdByUserId`; đổi label từ `Rejected` sang `Declined`.

- [ ] **Step 4: Tạo DTO**

`ApplicationSummary` chứa Application ID, candidate name/email, recruitment title, applied date, application/interview/offer statuses.

`ApplicationDetail` chứa `Application`, `Guest`, `Recruitment`, `Interview`, danh sách `Offer`, và `SystemUser` của candidate.

- [ ] **Step 5: Chạy test và compile**

Expected: PASS, compile không lỗi JSP Java references.

- [ ] **Step 6: Commit**

```powershell
git add src/main/java/com/hrm/model src/test
git commit -m "feat(recruitment): align workflow entities and DTOs"
```

---

### Task 5: Làm DAO dùng chung transaction Connection

**Files:**
- Modify: `src/main/java/com/hrm/dao/ApplicationDAO.java`
- Modify: `src/main/java/com/hrm/dao/InterviewDAO.java`
- Modify: `src/main/java/com/hrm/dao/OfferDAO.java`
- Modify: `src/main/java/com/hrm/dao/NotificationDAO.java`
- Modify: `src/main/java/com/hrm/dao/GuestDAO.java`
- Modify: `src/main/java/com/hrm/dao/EmployeeDAO.java`
- Modify: `src/main/java/com/hrm/dao/SystemUserDAO.java`
- Modify: `src/main/java/com/hrm/dao/SystemLogDAO.java`

- [ ] **Step 1: Viết DAO contract tests bằng mocked Connection**

Xác nhận method workflow không gọi `commit()`, `rollback()` hoặc `close()` trên Connection được truyền vào.

- [ ] **Step 2: Thêm Application locking và conditional transition**

```java
public Application findByIdForUpdate(Connection con, int applicationId) throws SQLException
```

SQL:

```sql
SELECT * FROM Application WHERE ApplicationID = ? FOR UPDATE
```

```java
public boolean transitionStatus(
        Connection con,
        int applicationId,
        String expectedStatus,
        String newStatus,
        String rejectedReason) throws SQLException
```

SQL phải có:

```sql
UPDATE Application
SET Status = ?, RejectedReason = ?, UpdatedDate = NOW()
WHERE ApplicationID = ? AND Status = ?
```

- [ ] **Step 3: Thêm HR Staff query**

`ApplicationDAO.searchSummaries(...)` và `countSummaries(...)` join Guest, Recruitment, Interview và active/latest Offer.

`ApplicationDAO.findDetailById(...)` trả `ApplicationDetail`.

- [ ] **Step 4: Sửa Interview DAO**

Thêm `findByApplicationId(Connection, ...)`, `create(Connection, ...)`, `updateSchedule(Connection, ...)`, `updateResult(Connection, ...)`.

`create` phải fail rõ khi unique ApplicationID bị vi phạm.

- [ ] **Step 5: Sửa Offer DAO**

Thêm:

- `findByIdForUpdate(Connection, offerId)`
- `findActiveByApplicationId(Connection, applicationId)`
- `findHistoryByApplicationId(...)`
- `create(Connection, offer)`
- `updateDraft(Connection, offer)`
- `updateStatus(Connection, offerId, expectedStatus, newStatus)`
- `findExpiredSentOffers(Connection, limit)`

Loại transaction khỏi `respondOffer()`; giữ method cũ tạm thời gọi service hoặc đánh dấu deprecated sau khi controller được migrate.

- [ ] **Step 6: Sửa DAO còn lại**

- `NotificationDAO.create(Connection, notification)`
- `SystemLogDAO.insertSystemLog(Connection, ...)`
- `EmployeeDAO.insert(Connection, employee)` trả generated EmployeeID
- `SystemUserDAO.findByIdForUpdate(Connection, userId)`
- `SystemUserDAO.promoteGuestToEmployee(Connection, ...)`
- `GuestDAO.findByIdForUpdate(Connection, guestId)`

- [ ] **Step 7: Chạy test và compile**

Expected: PASS; không DAO workflow nào tự transaction khi nhận external Connection.

- [ ] **Step 8: Commit**

```powershell
git add src/main/java/com/hrm/dao src/test
git commit -m "refactor(recruitment): support shared JDBC transactions"
```

---

### Task 6: RecruitmentWorkflowService — Screening, Reject và Interview

**Files:**
- Create: `src/main/java/com/hrm/service/recruitment/ConnectionProvider.java`
- Create: `src/main/java/com/hrm/service/recruitment/RecruitmentWorkflowService.java`
- Modify: `src/test/java/com/hrm/service/recruitment/RecruitmentWorkflowServiceTest.java`

- [ ] **Step 1: Viết failing service tests**

Test tối thiểu:

1. `startScreening` cập nhật Application, Notification, audit và commit.
2. Notification fail làm rollback.
3. `confirmReject` từ Applied yêu cầu reason.
4. Interview Failed không tự Reject.
5. Không tạo Interview thứ hai.
6. Conditional update trả 0 gây `WorkflowConflictException`.

- [ ] **Step 2: Tạo constructor injectable**

```java
@FunctionalInterface
public interface ConnectionProvider {
    Connection getConnection() throws SQLException;
}
```

```java
public RecruitmentWorkflowService(
        ConnectionProvider connectionProvider,
        ApplicationDAO applicationDAO,
        InterviewDAO interviewDAO,
        OfferDAO offerDAO,
        NotificationDAO notificationDAO,
        GuestDAO guestDAO,
        EmployeeDAO employeeDAO,
        SystemUserDAO systemUserDAO,
        SystemLogDAO systemLogDAO)
```

Tạo default constructor dùng `DBConnection::getConnection` cho servlet runtime.

- [ ] **Step 3: Tạo transaction helper**

```java
private <T> T inTransaction(SqlWork<T> work) {
    try (Connection con = connectionProvider.getConnection()) {
        con.setAutoCommit(false);
        try {
            T result = work.execute(con);
            con.commit();
            return result;
        } catch (Exception ex) {
            con.rollback();
            throw translate(ex);
        } finally {
            con.setAutoCommit(true);
        }
    }
}
```

- [ ] **Step 4: Triển khai methods**

- `startScreening(applicationId, actorUserId)`
- `confirmReject(applicationId, reason, actorUserId)`
- `scheduleInterview(applicationId, interview, actorUserId)`
- `rescheduleInterview(applicationId, interview, actorUserId)`
- `recordInterviewResult(applicationId, result, note, actorUserId)`

Mỗi method phải lock Application, gọi policy, conditional update, notification/audit đúng spec.

- [ ] **Step 5: Chạy test**

Expected: tất cả transaction tests PASS, Mockito verify `commit()` hoặc `rollback()` đúng nhánh.

- [ ] **Step 6: Commit**

```powershell
git add src/main/java/com/hrm/service/recruitment src/test/java/com/hrm/service/recruitment
git commit -m "feat(recruitment): add transactional screening and interview workflow"
```

---

### Task 7: RecruitmentWorkflowService — Offer và phản hồi ứng viên

**Files:**
- Modify: `src/main/java/com/hrm/service/recruitment/RecruitmentWorkflowService.java`
- Create: `src/main/java/com/hrm/service/recruitment/WorkflowEmailDispatcher.java`
- Modify: `src/test/java/com/hrm/service/recruitment/RecruitmentWorkflowServiceTest.java`
- Modify: `src/main/java/com/hrm/controller/guest/GuestPortalController.java`

- [ ] **Step 1: Viết failing Offer tests**

Test:

- Failed interview không tạo được Draft.
- Passed interview tạo Draft nhưng không Notification.
- Send Offer cập nhật Offer/Application và Notification.
- Accept → `OfferAccepted`, không tạo Employee.
- Decline → `OfferDeclined`.
- Expiry → `OfferExpired`.
- Tạo Draft mới sau Declined/Expired giữ Offer cũ.
- Không có hai Offer Draft/Sent đồng thời.

- [ ] **Step 2: Triển khai Offer service methods**

- `createOfferDraft`
- `updateOfferDraft`
- `sendOffer`
- `cancelOffer`
- `acceptOffer`
- `declineOffer`
- `expireOffers`

`sendOffer` validate:

```java
if (offer.getPosition() == null || offer.getPosition().isBlank()
        || offer.getOfferedSalary() == null
        || offer.getOfferedSalary().signum() <= 0
        || offer.getStartDate() == null
        || offer.getExpiredAt() == null
        || !offer.getExpiredAt().isAfter(LocalDateTime.now())) {
    throw new WorkflowValidationException("Offer information is incomplete or invalid.");
}
```

- [ ] **Step 3: Tách email sau commit**

`WorkflowEmailDispatcher` chỉ được gọi sau khi `inTransaction` trả về thành công. Email fail được log, không rollback database.

- [ ] **Step 4: Refactor GuestPortalController**

`/guest/offer/respond` gọi service thay vì `OfferDAO.respondOffer()`.

Controller không được tự cập nhật Offer/Application.

- [ ] **Step 5: Chạy test**

Expected: PASS; verify email dispatcher không chạy khi transaction rollback.

- [ ] **Step 6: Commit**

```powershell
git add src/main/java/com/hrm/service src/main/java/com/hrm/controller/guest src/test
git commit -m "feat(recruitment): add transactional offer workflow"
```

---

### Task 8: HR Staff Application list và detail

**Files:**
- Create: `src/main/java/com/hrm/controller/hrstaff/ApplicationListController.java`
- Create: `src/main/java/com/hrm/controller/hrstaff/ApplicationDetailController.java`
- Create: `src/main/webapp/Views/HrStaff/ApplicationList.jsp`
- Create: `src/main/webapp/Views/HrStaff/ApplicationDetail.jsp`
- Create: `src/main/webapp/css/hrstaff-recruitment.css`
- Modify: `src/main/java/com/hrm/controller/hrstaff/ViewCandidateController.java`
- Modify: `src/main/java/com/hrm/controller/hr/ViewCV.java`
- Modify: `src/main/webapp/Views/HrStaff/HrStaffHome.jsp`

- [ ] **Step 1: Viết controller permission tests**

Kiểm tra user thiếu `MANAGE_APPLICANTS` nhận 403; HR Staff có quyền được forward.

- [ ] **Step 2: Tạo routes chuẩn**

- `GET /hrstaff/applications`
- `GET /hrstaff/applications/detail?applicationId=...`

Cả hai dùng:

```java
PermissionUtil.ensureRolePermission(
    request,
    response,
    PermissionUtil.ROLE_HR_STAFF,
    "MANAGE_APPLICANTS",
    "This section is restricted to HR Staff.",
    "You do not have permission to manage applicants.");
```

Admin vẫn được phép qua logic hiện tại của `PermissionUtil`.

- [ ] **Step 3: Tạo list JSP**

Không copy toàn bộ inline CSS 598 dòng. Chuyển style dùng chung sang `hrstaff-recruitment.css` theo:

- canvas `#f2f0eb`
- heading `#1E3932`
- primary `#00754A`
- card trắng, radius 12px
- font Inter/Helvetica

List đọc `ApplicationSummary`, không đọc `Guest.status`.

- [ ] **Step 4: Tạo detail JSP**

Hiển thị:

- Guest profile.
- `application.cv`.
- Cover letter.
- Recruitment.
- Interview.
- Offer history.
- Action theo status và permission.

File CV link:

```jsp
${pageContext.request.contextPath}/Upload/cvs/${detail.application.cv}
```

- [ ] **Step 5: Chuyển route cũ thành redirect**

`ViewCandidateController`:

```java
response.sendRedirect(request.getContextPath() + "/hrstaff/applications");
```

`ViewCV` GET tìm Application mới nhất của `guestId` chỉ để redirect; POST trả `405 Method Not Allowed`. Không còn `updateCandidateStatus()` hoặc `EmailSender` ở servlet này.

- [ ] **Step 6: Cập nhật menu**

Mọi link HR Staff Candidate trỏ `/hrstaff/applications`.

- [ ] **Step 7: Chạy compile và test route**

Expected: link cũ không gãy; không route nào còn cập nhật Guest thành Hired/Rejected.

- [ ] **Step 8: Commit**

```powershell
git add src/main/java/com/hrm/controller/hrstaff src/main/java/com/hrm/controller/hr/ViewCV.java src/main/webapp/Views/HrStaff src/main/webapp/css/hrstaff-recruitment.css
git commit -m "feat(hrstaff): replace guest candidate view with application workflow"
```

---

### Task 9: HR Staff workflow action controller

**Files:**
- Create: `src/main/java/com/hrm/controller/hrstaff/ApplicationWorkflowController.java`
- Create: `src/main/java/com/hrm/controller/hrstaff/OfferExpiryController.java`
- Modify: `src/main/webapp/Views/HrStaff/ApplicationDetail.jsp`

- [ ] **Step 1: Viết failing action tests**

Test action mapping và validation:

- `start-screening`
- `reject`
- `schedule-interview`
- `reschedule-interview`
- `record-interview-result`
- `create-offer-draft`
- `update-offer-draft`
- `send-offer`
- `cancel-offer`

- [ ] **Step 2: Tạo một action controller**

Route:

```java
@WebServlet(urlPatterns = {
    "/hrstaff/applications/start-screening",
    "/hrstaff/applications/reject",
    "/hrstaff/applications/interview/schedule",
    "/hrstaff/applications/interview/reschedule",
    "/hrstaff/applications/interview/result",
    "/hrstaff/applications/offer/draft",
    "/hrstaff/applications/offer/update",
    "/hrstaff/applications/offer/send",
    "/hrstaff/applications/offer/cancel"
})
```

Mọi action dùng POST, `MANAGE_APPLICANTS`, và redirect PRG về detail.

- [ ] **Step 3: Parse input chặt**

- Reject reason: trim, không rỗng.
- Interview time: phải ở tương lai khi schedule.
- Interview result: chỉ `Passed`/`Failed`.
- Salary: `BigDecimal > 0`.
- Offer expiry: sau hiện tại.
- Start date: không trước ngày gửi Offer.

- [ ] **Step 4: Hiển thị action có điều kiện**

JSP dùng status + interview result + permission để render đúng button; service vẫn là lớp kiểm tra cuối.

- [ ] **Step 5: Tạo endpoint expiry có bảo vệ**

`POST /hrstaff/applications/offers/expire` dùng `MANAGE_APPLICANTS`; giai đoạn đầu gọi thủ công từ dashboard/admin job. Không chạy expiry trong GET page.

- [ ] **Step 6: Chạy test và compile**

Expected: PASS; GET không gây mutation.

- [ ] **Step 7: Commit**

```powershell
git add src/main/java/com/hrm/controller/hrstaff src/main/webapp/Views/HrStaff/ApplicationDetail.jsp src/test
git commit -m "feat(hrstaff): add application workflow actions"
```

---

### Task 10: Create Employee từ OfferAccepted trong một transaction

**Files:**
- Modify: `src/main/java/com/hrm/service/recruitment/RecruitmentWorkflowService.java`
- Modify: `src/main/java/com/hrm/controller/hr/CreateEmployeeController.java`
- Modify: `src/main/webapp/Views/hr/CreateEmployee.jsp`
- Modify: `src/test/java/com/hrm/service/recruitment/RecruitmentWorkflowServiceTest.java`

- [ ] **Step 1: Viết failing conversion tests**

Test:

- Chỉ `OfferAccepted` được tạo Employee.
- Tạo Employee + promote user + Application Hired + Notification + audit cùng transaction.
- User promotion fail làm rollback Employee.
- Guest không bị delete.
- Gọi lần hai bị conflict.

- [ ] **Step 2: Triển khai service method**

```java
public int createEmployeeFromAcceptedOffer(
        int applicationId,
        Employee employee,
        String username,
        String password,
        int actorUserId)
```

Method lock Application, Guest, SystemUser; insert Employee lấy generated ID; promote user; cập nhật `Hired`, `HiredEmployeeID`, `HiredAt`; notification/audit; commit.

- [ ] **Step 3: Refactor controller**

- Permission: `CREATE_EMPLOYEE`.
- GET bắt buộc `applicationId`.
- Load Application detail và xác nhận `OfferAccepted`.
- POST gửi toàn bộ sang service.
- Xóa mọi call `employeeDAO.delete()` và `guestDAO.delete()`.

- [ ] **Step 4: Refactor JSP**

Hidden field:

```jsp
<input type="hidden" name="applicationId" value="${detail.application.applicationId}">
```

Không còn danh sách chọn Guest Hired. Prefill từ Guest, Offer và Recruitment.

- [ ] **Step 5: Chạy test**

Expected: rollback test PASS; `rg "guestDAO.delete\\(guestId\\)"` không còn kết quả.

- [ ] **Step 6: Commit**

```powershell
git add src/main/java/com/hrm/service src/main/java/com/hrm/controller/hr/CreateEmployeeController.java src/main/webapp/Views/hr/CreateEmployee.jsp src/test
git commit -m "feat(hrstaff): create employee from accepted offer transactionally"
```

---

### Task 11: Permission và role routing

**Files:**
- Modify: `src/data/data.sql`
- Modify: `src/data/migrations/2026-06-24_hrstaff_workflow_core.sql`
- Modify: `src/main/java/com/hrm/filter/RoleAuthorizationFilter.java`
- Modify: `src/main/java/com/hrm/controller/hrstaff/HrStaffHomeController.java`
- Modify: `src/main/webapp/Views/HrStaff/HrStaffHome.jsp`

- [ ] **Step 1: Thêm permission seed đúng**

HR Staff có:

- `MANAGE_APPLICANTS`
- `CREATE_EMPLOYEE`

Nếu product owner sau này muốn HR Manager giữ quyền Create Employee riêng, thay role seed nhưng không đổi controller/service permission code.

- [ ] **Step 2: Sửa role filter**

Cho HR Staff role 4 truy cập:

- `/hrstaff/applications/*`
- `/hr/create-employee`

Role filter chỉ kiểm tra role thô; permission check vẫn nằm trong controller.

- [ ] **Step 3: Thêm dashboard stats**

Dashboard HR Staff lấy count Application theo status, không lấy Guest status.

- [ ] **Step 4: Thêm quick links**

- Applications cần xử lý.
- Interviews sắp tới.
- OfferAccepted chờ tạo Employee.

- [ ] **Step 5: Test permission matrix**

| Actor | Manage Application | Create Employee |
|---|---:|---:|
| Admin | Allow | Allow |
| HR Staff có đủ quyền | Allow | Allow |
| HR Staff bị user override deny | Deny | Deny tương ứng |
| Guest | Deny | Deny |
| Employee | Deny | Deny |

- [ ] **Step 6: Commit**

```powershell
git add src/data src/main/java/com/hrm/filter src/main/java/com/hrm/controller/hrstaff/HrStaffHomeController.java src/main/webapp/Views/HrStaff/HrStaffHome.jsp
git commit -m "feat(auth): enforce HR applicant and employee permissions"
```

---

### Task 12: Đồng bộ submit Application và Guest portal

**Files:**
- Modify: `src/main/java/com/hrm/controller/RecruitmentController.java`
- Modify: `src/main/java/com/hrm/service/recruitment/RecruitmentWorkflowService.java`
- Modify: `src/main/java/com/hrm/controller/guest/GuestPortalController.java`
- Modify: `src/main/webapp/Views/Guest/Applications.jsp`
- Modify: `src/main/webapp/Views/Guest/Dashboard.jsp`

- [ ] **Step 1: Viết failing submit test**

Test Application + Notification cùng transaction. Notification insert fail phải rollback Application; không tạo hồ sơ ứng tuyển dở dang.

- [ ] **Step 2: Chuyển submit sang service**

`RecruitmentController` vẫn xử lý multipart/upload file, sau đó gọi:

```java
workflowService.submitApplication(currentUser, requestData, cvFileName);
```

Sửa lỗi hiện tại:

```java
profile.setCv(profile.getCv());
```

Không cần ghi CV vào Guest; CV chỉ nằm ở Application.

- [ ] **Step 3: Ngừng ghi workflow legacy**

Không set:

- `Guest.Status`
- `Guest.RecruitmentID`
- `Guest.CV`

cho Application mới.

- [ ] **Step 4: Đồng bộ Guest portal labels/actions**

Hiển thị tất cả status mới. Chỉ Offer `Sent` hiện nút Accept/Decline. Draft không xuất hiện.

- [ ] **Step 5: Chạy test và compile**

Expected: submit rollback PASS; Guest portal compile và render đúng property names.

- [ ] **Step 6: Commit**

```powershell
git add src/main/java/com/hrm/controller/RecruitmentController.java src/main/java/com/hrm/controller/guest src/main/java/com/hrm/service src/main/webapp/Views/Guest src/test
git commit -m "refactor(recruitment): submit applications through workflow service"
```

---

### Task 13: Regression cleanup và xác minh toàn bộ

**Files:**
- Modify only files revealed by verification failures

- [ ] **Step 1: Tìm logic cũ còn sót**

Run:

```powershell
rg -n "updateCandidateStatus|guestDAO\\.delete|CurrentStep|Guest\\.Status|Guest\\.RecruitmentID|Guest\\.CV|Offer.*Rejected|VIEW_RECRUITMENT.*candidate|VIEW_EMPLOYEES.*create" src/main/java src/main/webapp
```

Expected:

- Không còn mutation bằng `updateCandidateStatus`.
- Không còn xóa Guest khi create employee.
- `CurrentStep` chỉ còn migration compatibility/deprecated mapping.
- Không Offer code mới dùng status `Rejected`.

- [ ] **Step 2: Chạy test**

```powershell
& 'C:\Program Files\NetBeans-17\netbeans\java\maven\bin\mvn.cmd' -q test
```

Expected: BUILD SUCCESS.

- [ ] **Step 3: Chạy package**

```powershell
& 'C:\Program Files\NetBeans-17\netbeans\java\maven\bin\mvn.cmd' -q package
```

Expected: tạo `target/HRMS.war`.

- [ ] **Step 4: Manual workflow smoke test**

Thực hiện trên database test:

1. Guest gửi Application.
2. HR Staff thấy Application và đúng CV.
3. Start Screening.
4. Schedule Interview.
5. Record Failed; xác nhận Application chưa Rejected.
6. Confirm Reject với reason.
7. Application khác: Passed → Draft → Send.
8. Candidate Decline → tạo Draft mới.
9. Send → Candidate Accept.
10. HR Staff Create Employee.
11. Xác nhận Guest vẫn tồn tại.
12. Xác nhận Application/Interview/Offer/Notification vẫn tồn tại.

- [ ] **Step 5: Kiểm tra transaction bằng fault injection**

Trong test, mock NotificationDAO/SystemUserDAO throw `SQLException`; xác nhận rollback và không có trạng thái trung gian.

- [ ] **Step 6: Quyết định cleanup legacy**

Chỉ khi:

```powershell
rg -n "/viewCV|Views/hr/ViewCV.jsp|/candidates|Views/HrStaff/ViewCandidate.jsp" src/main
```

không còn caller ngoài compatibility mapping, tạo task cleanup riêng để xóa. Không xóa trong workflow implementation commit.

- [ ] **Step 7: Final commit**

```powershell
git status --short
# Stage từng file thực sự được sửa trong bước verification; không dùng git add .
git commit -m "test(recruitment): verify HR staff workflow end to end"
```

---

## 3. Thứ tự thực thi khuyến nghị

Không nên bắt đầu bằng JSP. Thứ tự an toàn:

1. Test foundation.
2. State policy.
3. Migration.
4. Transaction-capable DAO.
5. Workflow service.
6. Offer workflow.
7. HR Staff list/detail.
8. Action controller.
9. Create Employee.
10. Permissions/dashboard.
11. Guest submit/portal.
12. Regression verification.

Sau Task 7 hệ thống đã có một lát cắt HR Staff đọc được dữ liệu đúng. Sau Task 10 workflow chính hoàn chỉnh. Task 11–13 khóa bảo mật, compatibility và regression.

## 4. Definition of Done

- Không xóa Guest khi tạo Employee.
- Không còn Guest trùng UserID sau migration.
- `Application.Status` là nguồn workflow duy nhất.
- Một Application chỉ có một Interview.
- Offer giữ lịch sử và chỉ có một Draft/Sent đang hoạt động.
- Mọi mutation workflow đi qua `RecruitmentWorkflowService`.
- Notification nằm trong transaction database.
- Email chỉ gửi sau commit.
- Controller dùng đúng `MANAGE_APPLICANTS`/`CREATE_EMPLOYEE`.
- Route cũ không còn mutation nguy hiểm.
- JUnit workflow tests, Maven test và Maven package đều thành công.
