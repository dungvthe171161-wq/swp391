# Interview Result And Offer Flow Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Complete the post-application recruitment flow: HR updates/reschedules/cancels interviews, creates and sends offers, and Guests accept/reject offers.

**Architecture:** Reuse `Interview`, `Application`, `Offer`, `CandidateProfile`, and `Notification`. Extend the existing HR Staff scheduling controller into a small interview workflow controller, add an offer controller/JSP, and render pending offers in the Guest applications page using the existing `GuestPortalController` data.

**Tech Stack:** Java 17, Jakarta Servlet/JSP, MySQL, existing DAO pattern, existing `EmailSender`, existing `NotificationService`.

---

### Task 1: DAO Support

**Files:**
- Modify: `src/main/java/com/hrm/dao/InterviewDAO.java`
- Modify: `src/main/java/com/hrm/dao/OfferDAO.java`

- [x] Add `InterviewDAO.findById`.
- [x] Add `OfferDAO.findById`.
- [x] Add `OfferDAO.findViewById`.

### Task 2: HR Interview Workflow

**Files:**
- Modify: `src/main/java/com/hrm/controller/hrstaff/InterviewScheduleController.java`
- Modify: `src/main/webapp/Views/HrStaff/ScheduleInterview.jsp`

- [x] Allow selecting an existing interview for edit with `interviewId`.
- [x] Add actions `saveSchedule`, `updateResult`, and `cancelInterview`.
- [x] Reschedule keeps application in `Interview` and sends email + notification.
- [x] Passing marks interview completed/passed and application `Offered`.
- [x] Failing marks interview completed/failed and application `Rejected`.
- [x] Cancel marks interview `Cancelled`, sends notification/email, and keeps application in `Interview`.

### Task 3: HR Offer Workflow

**Files:**
- Create: `src/main/java/com/hrm/controller/hrstaff/OfferManagementController.java`
- Create: `src/main/webapp/Views/HrStaff/ManageOffer.jsp`
- Modify: `src/main/webapp/Views/HrStaff/ViewCandidate.jsp`

- [x] HR opens offer form for an application.
- [x] HR creates/updates draft offer with position, salary, start date, expiry, and note.
- [x] HR sends offer, updates `Application = Offered`, sends email, creates Guest notification.

### Task 4: Guest Offer Response

**Files:**
- Modify: `src/main/webapp/Views/Guest/Applications.jsp`
- Modify: `src/main/java/com/hrm/controller/guest/GuestPortalController.java`

- [x] Render `pendingOffers`.
- [x] Guest accepts or rejects.
- [x] Existing `OfferDAO.respondOffer` updates offer and application to `Hired` or `Rejected`.
- [x] Guest sees success/error messages.

### Task 5: Specs And Verification

**Files:**
- Modify: `.cursor/Spec/Guest/feature-guest-interview.spec.md`
- Modify: `.cursor/Spec/Guest/feature-guest-offer.spec.md`
- Modify: `.cursor/Spec/Guest/feature-guest-application-workflow.spec.md`
- Modify: `.cursor/Spec/Guest/feature-guest-phase2-implementation.spec.md`

- [x] Note completed parts and remaining gaps.
- [x] Run Maven compile.
- [x] Run Maven package.

## Result 2026-07-02

Completed:
- HR Staff can create, edit/reschedule, cancel interviews, and update interview result Pass/Fail.
- Interview schedule/result/cancel sends email and creates Guest notification when possible.
- HR Staff can save draft offer and send offer from an Application.
- Sending offer updates `Application.Status` and `Application.CurrentStep` to `Offered`, sends email, and creates Guest notification.
- Guest `/guest/applications` shows pending sent offers and supports Accept/Reject.
- Accept updates `Offer.Status = Accepted` and `Application = Hired`; Reject updates `Offer.Status = Rejected` and `Application = Rejected`.

Remaining:
- No central workflow transaction/service yet; DAO operations are still controller-orchestrated.
- No automatic Employee/onboarding creation after `Hired` yet.
- Manual browser testing on deployed Tomcat/MySQL is still needed after redeploy.
