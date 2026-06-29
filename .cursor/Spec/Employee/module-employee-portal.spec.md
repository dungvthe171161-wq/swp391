# Module Spec: Employee Portal
Status: Approved
Actor: Employee
Priority: High
Related code: `EmployeePortalController`, `Views/Employee/*`, `RoleRedirectUtil`, `HomepageController`

## Scope
Employee Portal is the self-service area for employees. It covers dashboard, profile, attendance, leave requests, payroll, assigned tasks, contract view, and later schedule/notifications.

## Routes
- `/employee`
- `/employee/profile`
- `/employee/attendance`
- `/employee/leaves`
- `/employee/payroll`
- `/employee/tasks`
- `/employee/contract`

## Shared UI Rules
- Employee sidebar is visible on every Employee page.
- UI labels are Vietnamese.
- Technical enum values stored in DB remain English where the schema/code expects them.
- Empty states should be friendly and clear.
- Pages should not rely on hardcoded business numbers.

## Shared Security Rules
- All pages require authenticated `SystemUser`.
- `SystemUser.EmployeeID` is required for Employee portal access.
- Employee data must always be filtered by the current employee's `EmployeeID`.
- Other roles should not access `/employee/*`.

## Data Ownership
- Employee owns viewing their own data.
- Dept Manager owns team task assignment and leave approval.
- HR Staff owns payroll preparation and contract creation.
- HR Manager owns payroll/contract/recruitment approvals.
- Payroll calculation consumes Employee attendance and approved leave.

## Feature Order
1. Dashboard with real data.
2. Attendance check-in/check-out and history.
3. Leave request submission and Dept Manager approval.
4. Payroll details and audit.
5. Assigned task viewing/status update.
6. Contract/profile polish.
7. Schedule and notifications.

## Acceptance Criteria
- [ ] Employee portal has a single controller entry point.
- [ ] Sidebar navigation works across Employee pages.
- [ ] All Employee pages use real database data.
- [ ] Data access is scoped to current employee.
- [ ] Leave approval ownership is documented as Dept Manager.
