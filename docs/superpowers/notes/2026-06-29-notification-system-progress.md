# Notification System Progress - 2026-06-29

## Da lam

1. Viet spec tong the:
   - `docs/superpowers/specs/2026-06-29-notification-system-design.md`

2. Viet plan Phase 1:
   - `docs/superpowers/plans/2026-06-29-notification-system-phase1.md`

3. Them test harness:
   - Them JUnit 5 dependency vao `pom.xml`.
   - Them Maven Surefire plugin vao `pom.xml`.

4. Viet va chay test cho service:
   - `src/test/java/com/hrm/service/NotificationServiceTest.java`
   - Da xac nhan RED: test fail vi chua co `NotificationService` va field moi.
   - Da xac nhan GREEN: 3 test pass.

5. Mo rong entity `Notification`:
   - `ActorUserID`
   - `EntityType`
   - `EntityID`
   - `TargetUrl`
   - `Priority`
   - `ExpiresAt`

6. Mo rong `NotificationDAO`:
   - Insert duoc cac field notification moi.
   - Them `create(Connection, Notification)` de dung trong transaction.
   - Them `createForUsers(...)`.
   - Mapping co kiem tra cot moi de doc du lieu cu an toan hon.

7. Tao `NotificationService`:
   - Build notification voi target URL noi bo.
   - Gui cho mot user.
   - Gui cho nhieu user.
   - Lay recent notifications.
   - Dem unread.
   - Mark read va mark all read.

8. Them migration database:
   - `src/data/migrations/2026-06-29_notification_system_phase1.sql`

9. Cap nhat schema tao moi:
   - `src/data/data.sql`

10. Verification da chay:
    - `mvn test -Dtest=NotificationServiceTest`: PASS, 3 tests.
    - `mvn -q compile`: PASS.

11. Them service xac dinh nguoi nhan notification:
    - `src/main/java/com/hrm/service/NotificationRecipientService.java`
    - Ho tro lay user active theo role.
    - Ho tro lay Dept Manager theo department.

12. Mo rong `SystemUserDAO`:
    - `findActiveUserIdsByRoleName(String roleName)`
    - `findActiveUserIdsByRoleAndDepartment(String roleName, int departmentId)`

13. Mo rong `NotificationService`:
    - Them `notifyNewApplicationForHrStaff(...)`.
    - Tao notification HR Staff voi `Type = Application`, `EntityType = Application`, `TargetUrl = /candidates?applicationId=...`.

14. Them test cho recipient va HR Staff notification:
    - `src/test/java/com/hrm/service/NotificationRecipientServiceTest.java`
    - Cap nhat `src/test/java/com/hrm/service/NotificationServiceTest.java`

15. Gan notification dau tien vao workflow:
    - `RecruitmentController.confirmApplication(...)`
    - Khi ung vien nop ho so thanh cong, HR Staff active se nhan notification "Co ho so ung tuyen moi".

16. Verification bo sung da chay:
    - `mvn test -Dtest=NotificationRecipientServiceTest,NotificationServiceTest`: PASS, 7 tests.
    - `mvn -q compile`: PASS.

17. Da apply migration vao MySQL local:
    - Chay `src/data/migrations/2026-06-29_notification_system_phase1.sql` tren database `hrm_db`.
    - Da verify bang `Notification` co cac cot moi: `ActorUserID`, `EntityType`, `EntityID`, `TargetUrl`, `Priority`, `ExpiresAt`.
    - Da verify index `idx_notification_entity`, `idx_notification_priority`.
    - Da verify foreign key `fk_notification_actor_user`.

18. Them hien thi notification cho HR Staff topbar:
    - `src/main/java/com/hrm/filter/HrStaffNotificationFilter.java`
    - Tu dong nap `hrStaffNotificationCount` va `hrStaffNotifications` cho cac trang HR Staff chinh.
    - Bo so thong bao hard-code `5` trong `_HrStaffTopbar.jspf`.
    - Hien dropdown 5 notification moi nhat, badge unread, empty state.

19. Them endpoint mark read:
    - `src/main/java/com/hrm/controller/NotificationController.java`
    - POST `/notifications/read`
    - POST `/notifications/read-all`
    - Khi bam notification se mark read roi redirect ve `TargetUrl` noi bo.

20. Them redirect safety util va test:
    - `src/main/java/com/hrm/util/NotificationRedirectUtil.java`
    - `src/test/java/com/hrm/util/NotificationRedirectUtilTest.java`
    - Chan external/protocol-relative/newline redirect target.

21. Them CSS dropdown notification HR Staff:
    - `src/main/webapp/css/hr-theme.css`

22. Verification moi nhat da chay:
    - `mvn test -Dtest=NotificationRedirectUtilTest,NotificationRecipientServiceTest,NotificationServiceTest`: PASS, 11 tests.
    - `mvn -q compile`: PASS.

23. Them notification events cho Employee va Dept Manager:
    - Employee tao don nghi phep -> Dept Manager cung phong ban nhan notification.
    - Dept Manager duyet/tu choi don nghi phep -> Employee nhan notification.
    - Dept Manager tao task va assign employee -> Employee nhan notification.
    - Employee cap nhat trang thai task -> Dept Manager cung phong ban nhan notification.

24. Them lookup recipient theo EmployeeID:
    - `SystemUserDAO.findActiveUserIdByEmployeeId(int employeeId)`
    - `NotificationRecipientService.activeUserByEmployeeId(int employeeId)`
    - `NotificationRecipientService.activeUsersByEmployeeIds(List<Integer> employeeIds)`

25. Mo rong `MailRequestDAO` de phuc vu notification:
    - `insertAndReturnId(MailRequest)` de lay RequestID moi tao.
    - `getLeaveRequestSummaryById(int requestId)` de tim employee nhan notification khi don duoc duyet/tu choi.

26. Them UI notification dung chung:
    - `src/main/java/com/hrm/filter/AppNotificationFilter.java`
    - `src/main/webapp/Views/_NotificationBell.jspf`
    - Gan vao Employee topbar chung: `src/main/webapp/Views/Employee/_EmployeeTopbar.jspf`
    - Gan vao Dept Manager topbar: `src/main/webapp/Views/DeptManager/_DeptManagerTopbar.jspf`

27. Verification sau Employee/Dept Manager:
    - `mvn test -Dtest=NotificationRedirectUtilTest,NotificationRecipientServiceTest,NotificationServiceTest`: PASS, 16 tests.
    - `mvn -q compile`: PASS.

## Chua lam

1. Chua cap nhat `src/data/migrations/2026-06-22_guest_phase2_workflow.sql`; migration moi dang dung rieng de mo rong bang sau khi bang cu ton tai.
2. Da co UI dropdown/bell dung chung cho Employee va Dept Manager; chua gan cho HR Manager/Admin/Candidate.
3. Chua thay badge hard-code cua HR Manager/Admin/Candidate neu cac layout do dang co badge rieng.
4. Da gan `NotificationService` vao workflow ung vien nop ho so -> HR Staff va Employee/Dept Manager leave/task.
5. Chua co trang `/notifications` day du de xem toan bo lich su notification.
6. Da thong bao cho HR Staff khi co ung vien moi, nhung chua thong bao cho HR Staff khi offer response/recruitment/contract/payroll duoc duyet hoac tu choi.
7. Employee da co notification task moi, leave duoc duyet/tu choi; chua co payroll/contract doi trang thai.
8. Dept Manager da co notification leave moi/task cap nhat; chua co cac event nang cao khac.
9. Chua thong bao cho HR Manager khi co payroll/recruitment/contract cho duyet.
10. Chua thong bao cho Admin khi role/permission/user/system co su kien quan trong.

## Viec nen lam tiep theo

1. Test thu tren web bang account HR Staff:
   - Nop ho so ung tuyen bang flow candidate.
   - Dang nhap HR Staff.
   - Kiem tra icon chuong co badge va dropdown.
   - Bam notification de mark read va di den `/candidates?applicationId=...`.
2. Test thu Employee/Dept Manager:
   - Employee gui don nghi phep -> Dept Manager thay badge/notification.
   - Dept Manager duyet/tu choi -> Employee thay badge/notification.
   - Dept Manager tao task -> Employee thay badge/notification.
   - Employee doi task status -> Dept Manager thay badge/notification.
3. Gan tiep event HR Manager: payroll/recruitment/contract cho duyet.
4. Gan tiep event HR Staff: recruitment/contract/payroll duoc HR Manager duyet hoac tu choi.
5. Gan tiep Candidate/Admin theo spec.
