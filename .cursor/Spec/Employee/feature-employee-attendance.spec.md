# Feature: Employee xem va ghi nhan cham cong
Status: Planned
Actor: Employee
Priority: Medium
Source: `G1_SE2014_SRS Document.docx.pdf`, `src/data/data.sql`
Related Code: `AttendanceDAO`, `Attendance`, `Views/Employee/*`

## Tinh chat spec
Day la target design/gia thiet de gen code sau. Chuc nang check-in/check-out chi nen implement neu team chot Employee duoc tu ghi nhan cham cong.

## Muc tieu
Cho phep Employee xem lich su cham cong cua chinh minh va, neu team kich hoat luong cham cong tu Employee, ghi nhan check-in/check-out trong ngay. SRS mo ta Employee dung he thong de record attendance, va payroll tinh luong dua tren attendance/leave/overtime.

## Route de xuat
- `GET /employee/attendance`: xem lich su cham cong.
- `GET /employee/attendance/today`: xem trang thai cham cong hom nay.
- `POST /employee/attendance/check-in`: ghi nhan check-in.
- `POST /employee/attendance/check-out`: ghi nhan check-out.
- `POST /employee/attendance/correction-request`: gui yeu cau dieu chinh cham cong neu team chua cho sua truc tiep.

## Controller/DAO/JSP de xuat
| Thanh phan | De xuat |
| --- | --- |
| Controller | `com.hrm.controller.employee.EmployeeAttendanceController` |
| DAO | `AttendanceDAO` |
| Entity | `Attendance` |
| JSP | `/Views/Employee/Attendance.jsp` |
| Bang | `Attendance`, `Employee`, `MailRequest` neu dung correction request |

## Luong xem lich su
1. Employee vao `/employee/attendance`.
2. Controller lay `systemUser` tu session.
3. Controller xac dinh `EmployeeID`.
4. DAO lay attendance theo `EmployeeID`, filter theo thang/nam neu co.
5. JSP hien ngay lam, check-in, check-out, working hours, overtime, status neu DB co.

## Luong check-in
1. Employee bam check-in trong ngay.
2. Controller xac dinh `EmployeeID` tu session.
3. Controller kiem tra chua co check-in cho ngay hien tai.
4. He thong tao/cap nhat record attendance ngay hien tai.
5. Redirect ve `/employee/attendance/today` voi message thanh cong.

## Luong check-out
1. Employee da check-in trong ngay.
2. Employee bam check-out.
3. Controller kiem tra da co check-in va chua co check-out.
4. He thong cap nhat check-out, tinh working hours/overtime neu DAO ho tro.
5. Redirect ve `/employee/attendance/today`.

## Luong correction request
1. Employee phat hien cham cong sai/thieu.
2. Employee gui correction request kem ngay, gio de nghi, ly do.
3. Neu chua co bang attendance correction rieng, co the luu bang `MailRequest` voi `RequestType = Petition`.
4. Quan ly/HR xu ly ngoai pham vi Employee spec nay.

## Luong loi
- Chua dang nhap: redirect `/login`.
- User khong co `EmployeeID`: hien loi khong co ho so nhan vien.
- Check-in lap trong cung ngay: validation error.
- Check-out khi chua check-in: validation error.
- Check-out lap: validation error.
- Employee khong duoc gui/cap nhat attendance cho employee khac.

## Hien trang code
- Da co `AttendanceDAO` va `Attendance` entity trong codebase.
- Chua thay Employee attendance controller/JSP rieng.
- `PayrollCalculateController` co dung attendance va `MailRequest` de tinh payroll.

## Out of Scope
- Khong dinh nghia GPS/IP/device tracking trong spec nay.
- Khong tu dong approve correction request.
- Khong thay doi cong thuc payroll trong feature Employee attendance.

## Acceptance Criteria
- [ ] Employee xem duoc lich su cham cong cua chinh minh.
- [ ] Employee khong xem duoc attendance cua employee khac.
- [ ] Check-in khong duoc tao trung trong cung ngay.
- [ ] Check-out chi hop le sau check-in.
- [ ] Attendance duoc ghi dung `EmployeeID` tu session.
- [ ] Loi validation hien tieng Viet va khong lo stack trace.

## Missing Work
- [ ] Xac nhan schema `Attendance` hien co co du cot check-in/check-out/working hours/overtime.
- [ ] Tao controller/JSP Employee attendance.
- [ ] Chot co cho Employee check-in/out truc tiep hay chi view + correction request.
- [ ] Them audit log cho check-in/check-out neu can.
