# Feature: Employee leave requests
Status: Approved
Actor: Employee
Approver: Dept Manager
Administrative viewer: HR Manager / HR Staff
Priority: High
Related code: `EmployeePortalController`, `MailRequestDAO`, `MailRequest`, `Views/Employee/Leaves.jsp`, future Dept Manager approval page

## Goal
Employees can submit leave requests, see their request history, and track approval status. Dept Manager is the primary approver because they manage team workload and schedule. HR/payroll uses approved requests for payroll calculation.

## Routes
- Employee create/view: `GET /employee/leaves`
- Employee submit: `POST /employee/leaves`
- Dept Manager approval route should be added as: `GET/POST /dept/leaves`
- HR route `/hr/leaves` may remain read-only or administrative, but should not be the primary approval owner.

## Database
Use existing table `MailRequest`.

Required fields:
- `EmployeeID`
- `RequestType = 'Leave'`
- `LeaveType`: technical values stay in English: `Annual`, `Sick`, `Maternity`, `Unpaid`, `Other`
- `StartDate`
- `EndDate`
- `Reason`
- `Status`: `Pending`, `Approved`, `Rejected`
- `ApprovedBy`: approver user/employee reference when available

Do not create a separate `Leave` table unless schema refactor is approved.

## Employee Form
Employee enters:
- Leave type.
- Leave detail: full day, morning, afternoon.
- Start date.
- End date.
- Handover person email.
- Handover work details.
- Reason.

The UI text is Vietnamese, but backend status/type values remain technical English to avoid breaking payroll logic.

## Main Flow
1. Employee opens `/employee/leaves`.
2. System shows the employee's own leave history.
3. Employee submits a new leave request.
4. System validates required fields and valid date range.
5. System saves the request to `MailRequest` with `Status = 'Pending'`.
6. Dept Manager reviews pending requests for employees in their department.
7. Dept Manager approves or rejects the request.
8. Approved leave requests are included in payroll leave calculation.

## Approval Rule
- Primary approver: Dept Manager.
- Dept Manager can only approve/reject requests from employees in their department.
- HR Manager can view all leave requests for audit/administration.
- Payroll reads only `Status = 'Approved'` leave requests.

## Validation Rules
- Start date is required.
- End date is required.
- Start date and end date cannot be in the past.
- End date cannot be before start date.
- Handover person email is required and must be a valid email.
- Handover work details are required.
- Reason is required.
- Duplicate or overlapping pending/approved leave ranges should be blocked.
- Rejected requests do not block a new request for the same date range.
- Paid leave requests cannot exceed the employee's remaining leave sessions.
- Full-day leave counts as 2 sessions per day.
- Morning or afternoon leave counts as 1 session per day.
- Pending paid leave requests are counted against remaining sessions to prevent overbooking.

## Payroll Rules
- Paid leave types: `Annual`, `Sick`, `Maternity`.
- Unpaid leave type: `Unpaid`.
- `Other` should be reviewed carefully by approver; payroll behavior must be explicit before production.
- Payroll must ignore `Pending` and `Rejected` requests.
- When a leave request is created successfully, the system sends an email to the handover person with the leave dates and handover work details.

## Acceptance Criteria
- [ ] Employee can submit a leave request.
- [ ] Employee can see only their own leave requests.
- [ ] New request defaults to `Pending`.
- [ ] Employee cannot submit leave dates in the past.
- [ ] Employee cannot submit paid leave beyond remaining leave sessions.
- [ ] Handover email, handover details, and reason are required.
- [ ] Handover email receives the handover details after successful submission.
- [ ] Dept Manager can approve/reject department requests.
- [ ] HR can view leave requests for tracking.
- [ ] Approved leave affects payroll calculation.
- [ ] UI labels are Vietnamese while DB enum values remain valid.
