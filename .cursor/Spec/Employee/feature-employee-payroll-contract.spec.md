# Feature: Employee payroll and contract view
Status: Approved
Actor: Employee
Priority: High
Related code: `EmployeePortalController`, `PayrollDAO`, `ContractDAO`, `Views/Employee/Payroll.jsp`, `Views/Employee/Contract.jsp`

## Goal
Employees can view their own payroll slips and active/latest employment contract. Payroll detail must include calculation audit so the employee can understand attendance, leave, allowances, and deductions.

## Routes
- Payroll list/detail: `GET /employee/payroll`
- Payroll detail by id: `GET /employee/payroll?payrollId={id}`
- Contract view: `GET /employee/contract`

## Payroll Data
Payroll screen should show:
- Pay period.
- Base salary.
- Allowances.
- Deductions.
- Tax and insurance if available.
- Net salary.
- Status.
- Approval date if available.
- Audit values: working days, late/early counts, paid leave days, unpaid leave days.

## Contract Data
Contract screen should show:
- Contract ID.
- Contract type.
- Start date.
- End date.
- Base salary.
- Allowance.
- Status.
- Notes.

## Security Rules
- Employee can only view payroll records with their own `EmployeeID`.
- Employee can only view contracts with their own `EmployeeID`.
- If a payroll ID belongs to another employee, show a friendly error and do not render details.

## Payroll Business Rules
- Employee view is read-only.
- Payroll values are generated/managed by HR Staff and approved by HR Manager.
- Approved leave from `MailRequest` affects payroll calculation.
- Payroll detail should expose enough audit data to explain why the final salary changed.

## Acceptance Criteria
- [ ] Employee sees their payroll list.
- [ ] Employee can open payroll details.
- [ ] Payroll detail includes audit calculation data.
- [ ] Employee cannot access another employee's payroll.
- [ ] Employee can view their latest/current contract.
- [ ] Empty state appears when payroll or contract does not exist.
