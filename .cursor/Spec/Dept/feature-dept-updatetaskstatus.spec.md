# Feature: Update Task Status

Status: Approved
Actor: Dept Manager
Priority: Medium
Related Code: `TaskManager`, `DAO`, `Views/DeptManager/taskManager.jsp`

## Route
- `GET /taskManager?action=send&id={taskId}`
- `GET /taskManager?action=reject&id={taskId}`

## Luong Send
1. Dept Manager chon `Send`.
2. Controller nhan `taskId`.
3. Goi `updateTaskStatus`.
4. Cap nhat trang thai task thanh `Waiting`.
5. Hien thi thong bao thanh cong.

## Luong Reject
1. Dept Manager chon `Reject`.
2. Controller nhan `taskId`.
3. Goi `updateTaskStatus`.
4. Cap nhat trang thai task thanh `Rejected`.
5. Hien thi thong bao thanh cong.

## Hien trang code
- Da co `updateTaskStatus`.
- Da co xu ly `send`.
- Da co xu ly `reject`.

## Acceptance Criteria
- [ ] Cap nhat trang thai `Waiting` thanh cong.
- [ ] Cap nhat trang thai `Rejected` thanh cong.
- [ ] Chi Dept Manager duoc thao tac.