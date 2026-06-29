# Feature: Employee dashboard
Status: Approved
Actor: Employee
Priority: High
Related code: `EmployeePortalController`, `Views/Employee/EmployeeHome.jsp`, `AttendanceDAO`, `MailRequestDAO`, `PayrollDAO`, `TaskDAO`, `ContractDAO`

## Goal
Employee dashboard is the first screen after an Employee logs in. It must show real data from the database, not hardcoded numbers.

## Routes
- `GET /employee`
- Redirect from `/homepage` and role redirect should send Employee users to `/employee`.

## Data Shown
- Remaining paid leave days in the current year.
- Latest payroll period and net salary.
- Today's attendance status.
- Current/open tasks assigned to the employee.
- Latest contract summary.
- Recent attendance history.

## Main Flow
1. Employee logs in successfully.
2. System resolves `SystemUser.EmployeeID` from session.
3. Controller loads the matching `Employee`.
4. Controller queries dashboard data through DAO classes.
5. JSP renders the dashboard with the Employee sidebar visible.

## Business Rules
- Employee can only see data linked to their own `EmployeeID`.
- Leave remaining is calculated from approved leave requests in `MailRequest`.
- Payroll card uses the latest payroll record for that employee.
- Attendance card uses today's attendance record.
- Task card counts assigned tasks that are not completed.
- No dashboard number should be hardcoded.

## Acceptance Criteria
- [ ] Employee opens `/employee` and sees their own dashboard.
- [ ] Dashboard uses real database values.
- [ ] Other roles cannot access Employee-only pages.
- [ ] If data does not exist, UI shows a friendly empty state.
- [ ] Sidebar remains visible across Employee pages.
