# Notification System Phase 1 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the notification database/code foundation so future role-specific events can create and display in-app notifications.

**Architecture:** Keep the existing `Notification` table and extend it with entity routing fields. Add a focused `NotificationService` on top of `NotificationDAO`; controllers can call the service later without hand-building DAO calls.

**Tech Stack:** Java 17, Jakarta Servlet/JSP, JDBC DAO, MySQL 8.4, Maven, JUnit 5.

---

### Task 1: Test Harness

**Files:**
- Modify: `pom.xml`
- Create: `src/test/java/com/hrm/service/NotificationServiceTest.java`

- [x] **Step 1: Add JUnit 5 dependencies and Surefire plugin**

Add `org.junit.jupiter:junit-jupiter:5.10.2` with `test` scope and `maven-surefire-plugin:3.2.5`.

- [x] **Step 2: Write failing tests for NotificationService**

Create tests that expect:

```java
NotificationService service = new NotificationService(fakeDao);
Notification notification = service.buildNotification(
        7,
        3,
        "Task",
        12,
        "Task",
        "New task",
        "You have a new task",
        "/employee/tasks",
        "High"
);

assertEquals(7, notification.getUserId());
assertEquals(Integer.valueOf(3), notification.getActorUserId());
assertEquals("Task", notification.getEntityType());
assertEquals(Integer.valueOf(12), notification.getEntityId());
assertEquals("/employee/tasks", notification.getTargetUrl());
assertEquals("High", notification.getPriority());
```

- [x] **Step 3: Run test to verify RED**

Run: `mvn test -Dtest=NotificationServiceTest`  
Expected: compile failure because `NotificationService` and new `Notification` fields do not exist.

### Task 2: Notification Model And DAO Foundation

**Files:**
- Modify: `src/main/java/com/hrm/model/entity/Notification.java`
- Modify: `src/main/java/com/hrm/dao/NotificationDAO.java`
- Create: `src/main/java/com/hrm/service/NotificationService.java`

- [x] **Step 1: Add Notification fields**

Add fields/getters/setters:

```java
private Integer actorUserId;
private String entityType;
private Integer entityId;
private String targetUrl;
private String priority;
private LocalDateTime expiresAt;
```

- [x] **Step 2: Update NotificationDAO inserts and mapping**

Update `create` to insert:

```sql
UserID, ActorUserID, ApplicationID, EntityType, EntityID, Title, Message, Type, TargetUrl, Priority, IsRead, ExpiresAt
```

Add `create(Connection con, Notification notification)` for workflow transactions.

- [x] **Step 3: Implement NotificationService**

Implement:

```java
public Notification buildNotification(...)
public int notifyUser(Notification notification)
public int notifyUsers(List<Integer> userIds, Notification template)
public List<Notification> recentForUser(int userId, int limit)
public int unreadCount(int userId)
public boolean markRead(int notificationId, int userId)
public boolean markAllRead(int userId)
```

- [x] **Step 4: Run test to verify GREEN**

Run: `mvn test -Dtest=NotificationServiceTest`  
Expected: PASS.

### Task 3: Database Migration

**Files:**
- Create: `src/data/migrations/2026-06-29_notification_system_phase1.sql`

- [x] **Step 1: Add idempotent migration**

Create a migration that adds the new columns if missing and widens the `Type` enum.

- [x] **Step 2: Mirror schema in main data file**

Update `src/data/data.sql` so fresh database creation includes the extended `Notification` table.

### Task 4: Compile Verification And Work Notes

**Files:**
- Create: `docs/superpowers/notes/2026-06-29-notification-system-progress.md`

- [x] **Step 1: Run compile**

Run: Maven compile.  
Expected: build succeeds.

- [x] **Step 2: Write progress notes**

Record:

- Done.
- Not done.
- Files changed.
- Next suggested implementation phase.
