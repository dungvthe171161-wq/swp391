# Feature: Employee assigned tasks
Status: Approved
Actor: Employee
Owner: Dept Manager
Priority: High
Related code: `EmployeePortalController`, `TaskDAO`, `Views/Employee/Tasks.jsp`

## Goal
Employees can view tasks assigned to them and update their own task status.

## Routes
- Task list: `GET /employee/tasks`
- Update status: `POST /employee/tasks`

## Data Shown
- Task title.
- Description.
- Assigner.
- Assigned date / due date if available.
- Current status.

## Allowed Status Updates
Employee may update their assigned task to supported DB statuses:
- `Waiting`
- `In Progress`
- `Completed`

`Rejected` should be reserved for manager-side handling unless a separate rejection flow is approved.

## Main Flow
1. Employee opens `/employee/tasks`.
2. System loads tasks assigned to the employee's `EmployeeID`.
3. Employee changes status on a task.
4. System verifies the task is assigned to this employee.
5. System updates status through `TaskDAO`.
6. Page redirects back with success/error message.

## Security Rules
- Employee can only view tasks assigned to them.
- Employee can only update tasks assigned to them.
- Task ID tampering must not update another employee's task.

## Acceptance Criteria
- [ ] Employee sees assigned tasks.
- [ ] Employee can update status of their own tasks.
- [ ] Employee cannot update tasks assigned to another employee.
- [ ] Invalid task/status returns a friendly error.
