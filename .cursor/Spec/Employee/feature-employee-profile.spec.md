# Feature: Employee profile
Status: Approved
Actor: Employee
Priority: Medium
Related code: `EmployeePortalController`, `EmployeeDAO`, `Views/Employee/EmployeeProfile.jsp`

## Goal
Employees can view their own personal and employment information in the Employee portal.

## Routes
- `GET /employee/profile`

## Data Shown
- Employee code / ID.
- Full name.
- Department.
- Position.
- Email.
- Phone.
- Address.
- Employment status.
- Basic account context if available.

## Main Flow
1. Employee opens `/employee/profile`.
2. System resolves current user from session.
3. System loads `Employee` by `SystemUser.EmployeeID`.
4. JSP renders profile fields in read-only mode.

## Security Rules
- Employee can only view their own profile.
- No query parameter should allow switching to another employee profile.
- If session has no linked employee, redirect to login/homepage with a safe fallback.

## Future Scope
Self-editing profile data is not part of this feature. If added later, create a separate spec with validation and approval rules for editable fields.

## Acceptance Criteria
- [ ] Employee can open their profile page.
- [ ] Profile shows real data from the database.
- [ ] Employee cannot view another user's profile through URL manipulation.
- [ ] Sidebar remains visible.
