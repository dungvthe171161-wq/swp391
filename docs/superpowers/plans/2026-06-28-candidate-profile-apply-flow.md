# Candidate Profile Apply Flow Implementation Spec

Status: Implemented on 2026-06-28

## Goal
Build a reusable Guest candidate profile flow where first-time applicants verify email before saving their profile, then apply through a confirmation screen without re-entering data on later applications.

## Architecture
- Add `CandidateProfile` as the reusable applicant data model linked one-to-one to `Guest`.
- Link each `Application` to `CandidateProfileID`.
- Keep email verification state in `HttpSession`.
- Reuse existing email sender.
- Keep recruitment fields unchanged; do not add department/benefit/deadline for this flow.
- Keep legacy-compatible `Application.CV` so HR CV screens can display uploaded CV files.

## Database
Implemented files:
- `src/data/data.sql`
- `src/data/migrations/2026-06-28_candidate_profile_apply_flow.sql`

Tables/columns:
- `CandidateProfile`: `CandidateProfileID`, `GuestID`, `FullName`, `Phone`, `Email`, `DateOfBirth`, `Address`, `DesiredPosition`, `ExpectedSalary`, `WorkExperience`, `CVFilePath`, `EmailVerified`, `EmailVerifiedAt`, `CreatedDate`, `UpdatedDate`.
- `Application`: added `CandidateProfileID` and `Note`.

Rules:
- One `CandidateProfile` per `GuestID`.
- Unique `Application(GuestID, RecruitmentID)` blocks duplicate applications.
- No database table is used for email verification codes.

## Apply Controller Flow
Implemented in `src/main/java/com/hrm/controller/RecruitmentController.java`.

Actions:
- `apply`
- `saveCandidateProfile`
- `verifyCandidateProfileEmail`
- `resendCandidateProfileCode`
- `confirmApplication`

Flow:
1. `apply` requires login, validates recruitment, rejects duplicates, then routes to profile form or confirm page.
2. `saveCandidateProfile` validates profile fields and CV, saves uploaded CV, sends a 6-digit email code, and stores draft data in session.
3. `verifyCandidateProfileEmail` compares the submitted code with the session code and expiry; when valid, it persists `CandidateProfile`.
4. `confirmApplication` creates `Application` with `GuestID`, `RecruitmentID`, `CandidateProfileID`, `CV`, `Status = Applied`, `CurrentStep = Applied`, and `Source = Portal`.
5. Duplicate application message is exactly `Bạn đã ứng tuyển công việc này.`.

Apply session keys:
- `candidateProfileDraft`
- `candidateProfileVerifyCode`
- `candidateProfileVerifyExpiresAt`
- `candidateProfileVerifyRecruitmentId`

## Apply JSP Screens
Implemented files:
- `src/main/webapp/Views/ApplyForm.jsp`
- `src/main/webapp/Views/ApplyVerifyEmail.jsp`
- `src/main/webapp/Views/ApplyConfirm.jsp`
- `src/main/webapp/Views/Success.jsp`
- `src/main/webapp/Views/Recruitment.jsp`

Validation:
- Required full name, phone, email, address, desired position, work experience where applicable.
- Email format.
- Phone format.
- Date of birth not in the future.
- CV file extension: PDF, DOC, DOCX.
- CV max size: 10MB.

## Guest Profile Management
Implemented files:
- `src/main/java/com/hrm/controller/guest/GuestPortalController.java`
- `src/main/webapp/Views/Guest/Profile.jsp`
- `src/main/webapp/Views/Guest/ProfileEmailVerify.jsp`
- `src/main/webapp/css/guest.css`

Rules:
- Load `CandidateProfile` into Guest profile page.
- Add `Hồ sơ ứng tuyển` section.
- If profile email is unchanged and verified, save directly.
- If profile email changes or is not verified, send session verification code before saving.
- Preserve existing basic Guest profile behavior.

Profile session keys:
- `guestCandidateProfileDraft`
- `guestCandidateProfileVerifyCode`
- `guestCandidateProfileVerifyExpiresAt`

## Login Return
Implemented files:
- `src/main/java/com/hrm/controller/LoginController.java`
- `src/main/java/com/hrm/controller/GoogleAuthController.java`
- `src/main/java/com/hrm/filter/SessionSecurityFilter.java`

Rule:
- When a Guest starts applying while unauthenticated, store the target job URL and return there after login.

## HR CV Compatibility
Implemented in `src/main/java/com/hrm/controller/ViewCV.java`.

CV lookup order:
1. `Application.CV`.
2. `CandidateProfile.CVFilePath`.
3. Legacy Guest CV data if needed.

Recruitment lookup:
1. `Application.RecruitmentID`.
2. Legacy `Guest.RecruitmentID` fallback if needed.

## Verification
Completed:
- `mvn -q compile`
- `mvn -q package`
- MySQL schema check for `CandidateProfile`, `Application.CandidateProfileID`, and `Application.Note`.
