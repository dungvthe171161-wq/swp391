# Interview Email Notification Flow Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Let HR Staff schedule interviews for applications, notify the candidate by email and in-app notification, and show the next interview on the Guest dashboard.

**Architecture:** Reuse the existing `Interview`, `Application`, `CandidateProfile`, and `Notification` tables. Add application-centric DAO queries for HR, a focused HR Staff scheduling servlet, a JSP form, and dynamic Guest dashboard rendering from `InterviewDAO.findUpcomingByUserId`.

**Tech Stack:** Java 17, Jakarta Servlet/JSP, MySQL, existing DAO pattern, existing `EmailSender`, existing notification service.

---

### Task 1: Application DAO Support

**Files:**
- Modify: `src/main/java/com/hrm/dao/ApplicationDAO.java`

- [x] Add HR-facing application list query with candidate profile and recruitment data.
- [x] Add application detail query by `ApplicationID`.
- [x] Keep existing Guest-facing query unchanged.

### Task 2: HR Staff Interview Scheduling

**Files:**
- Create: `src/main/java/com/hrm/controller/hrstaff/InterviewScheduleController.java`
- Create: `src/main/webapp/Views/HrStaff/ScheduleInterview.jsp`

- [x] GET `/hrstaff/interviews/schedule?applicationId={id}` shows the schedule form.
- [x] POST `/hrstaff/interviews/schedule` validates application, round, time, location/link, interviewer, and note.
- [x] Insert `Interview`.
- [x] Update `Application.Status = Interview` and `Application.CurrentStep = Interview`.
- [x] Send email to `CandidateProfile.Email`, fallback `Guest.Email`.
- [x] Create `Notification` for Guest when `Guest.UserID` exists.

### Task 3: HR Staff Candidate List Entry Point

**Files:**
- Modify: `src/main/java/com/hrm/controller/hrstaff/ViewCandidateController.java`
- Modify: `src/main/webapp/Views/HrStaff/ViewCandidate.jsp`

- [x] Switch candidate list to application-centric records.
- [x] Show `ApplicationID`, candidate name/email/phone, job title, applied date, status, CV link, and schedule interview link.
- [x] Preserve search/status/date filters.

### Task 4: Guest Dashboard Dynamic Interview Card

**Files:**
- Modify: `src/main/webapp/Views/Guest/Dashboard.jsp`

- [x] Replace static interview placeholder with first item from `upcomingInterviews`.
- [x] Show round, date/time, job title, location, meeting link, and status.
- [x] Keep friendly empty state when no interview exists.

### Task 5: Specs And Verification

**Files:**
- Modify: `.cursor/Spec/Guest/feature-guest-interview.spec.md`
- Modify: `.cursor/Spec/Guest/feature-guest-application-workflow.spec.md`
- Modify: `.cursor/Spec/Guest/feature-guest-phase2-implementation.spec.md`

- [x] Note Phase 2A done: scheduling, email, notification, Guest dashboard.
- [x] Note Phase 2B still pending: Offer creation/send/accept/reject.
- [x] Run Maven compile.
- [x] Run Maven package.
