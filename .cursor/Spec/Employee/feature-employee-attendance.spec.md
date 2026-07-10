# Feature: Employee attendance
Status: Approved
Actor: Employee
Priority: High
Related code: `EmployeePortalController`, `AttendanceDAO`, `Views/Employee/Attendance.jsp`

## Goal
Employees can check in, check out, see today's attendance state, and review attendance history.

## Routes
- View attendance: `GET /employee/attendance`
- Check in/out: `POST /employee/attendance`

## Data Shown
- Today's status: not checked in, checked in, checked out.
- Check-in time.
- Check-out time.
- Late status.
- Early leave status.
- Monthly summary.
- Recent attendance history.

## Main Flow
1. Employee opens `/employee/attendance`.
2. System shows today's attendance status.
3. Employee clicks check in.
4. System creates today's attendance record if none exists.
5. Employee clicks check out.
6. System updates today's attendance record.
7. System shows full recent attendance history.

## Business Rules
- Employee can check in only once per day.
- Employee can check out only after checking in.
- Employee can check out only once per day.
- Late arrival is calculated from company shift start time.
- Early leave is calculated from company shift end time.
- Attendance history must be filtered by current `EmployeeID`.

## Acceptance Criteria
- [ ] Employee can check in once per day.
- [ ] Employee cannot check in multiple times.
- [ ] Employee can check out after check-in.
- [ ] Employee cannot check out before check-in.
- [ ] UI shows checked-in / not-checked-out state.
- [ ] History shows check-in, check-out, late, early leave values.
