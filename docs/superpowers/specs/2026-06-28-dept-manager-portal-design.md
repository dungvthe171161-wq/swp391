# Dept Manager Portal Design

**Ngay chot:** 2026-06-28  
**Pham vi:** Hoan thien cong Dept Manager cho BetterHR  
**Cong nghe:** Java 17, Jakarta Servlet/JSP, JDBC DAO, MySQL 8.4

## 1. Muc tieu

Hoan thien khu vuc Dept Manager thanh mot cong quan ly phong ban thong nhat, co giao dien dong bo va du lieu gioi han theo phong ban cua truong phong.

Muc tieu chinh:

1. Bo giao dien Task Manager rieng le nhu man hinh Bootstrap hien tai.
2. Dung sidebar/dashboard shell cua Dept Manager lam bo cuc chung cho tat ca trang trong module.
3. Dam bao Dept Manager chi thay va thao tac du lieu cua phong ban minh quan ly.
4. Hoan thien cac trang: tong quan nhom, nhan vien phong ban, quan ly cong viec, lich nhom, yeu cau nghi phep, danh gia hieu suat, bao cao phong ban.
5. Them luong duyet don xin nghi cho nhan vien trong phong ban.
6. Sua cac diem bao mat hien co: controller phai kiem tra session/role/scope truoc khi xu ly action.

## 2. Quyet dinh nghiep vu

### 2.1 Role va scope

- Role `Dept Manager` duoc truy cap cong Dept Manager.
- Admin co the truy cap de kiem tra/he thong.
- Moi Dept Manager phai lien ket voi mot `EmployeeID`.
- Phong ban cua Dept Manager lay tu `Employee.DepartmentID` cua nhan vien dang gan voi tai khoan.
- Dept Manager chi duoc xem/sua/giao task va duyet nghi phep cho nhan vien co cung `DepartmentID`.
- Neu tai khoan Dept Manager chua gan Employee hoac Employee chua co DepartmentID, hien trang loi ro rang thay vi hien du lieu rong gay nham lan.

### 2.2 Dashboard chung

Sidebar trong anh tham chieu se la shell chung cho tat ca trang Dept Manager:

- BetterHR / He thong HRM noi bo.
- Tong quan nhom.
- Nhan vien phong ban.
- Quan ly cong viec.
- Lich nhom.
- Yeu cau nghi phep.
- Danh gia hieu suat.
- Bao cao phong ban.
- The thong tin nguoi dung o cuoi sidebar.

Trang nao trong Dept Manager cung phai giu sidebar nay, topbar cung style, va highlight dung menu dang active.

### 2.3 Bo giao dien Task Manager cu

Khong giu man hinh Task Manager Bootstrap rieng le nhu anh 1.

Thay vao do:

- Noi dung task list nam trong shell Dept Manager.
- Nut `Create New Task` doi thanh hanh dong trong topbar hoac toolbar cua trang Quan ly cong viec.
- Form loc task va bang task dung visual style BetterHR cua Dept Manager.
- Cac nut `View`, `View participating member`, `Send`, `Delete` duoc doi thanh action ro nghia hon:
  - Xem/Sua.
  - Thanh vien.
  - Gui/Chuyen trang thai neu can.
  - Xoa/Huy task neu nghiep vu cho phep.

## 3. Trang va chuc nang

### 3.1 Tong quan nhom

Route de xuat: `/dept`

Noi dung:

- KPI tong nhan vien trong phong ban.
- KPI nhan vien dang lam viec.
- KPI task dang cho, dang lam, hoan thanh, qua han.
- KPI don nghi phep cho duyet.
- Danh sach task gan han.
- Danh sach nhan vien phong ban moi/can chu y.
- Shortcut tao task va duyet nghi phep.

Du lieu khong duoc hard-code. Cac so hien dang gia trong `deptHome.jsp` phai duoc thay bang DAO/query that.

### 3.2 Nhan vien phong ban

Route de xuat: `/dept/employees`

Noi dung:

- Danh sach nhan vien cung phong ban.
- Loc theo ten, trang thai, vi tri.
- Cot hien thi: ma nhan vien, ho ten, email/dien thoai, vi tri, trang thai.
- Chi xem thong tin can thiet cho truong phong; khong sua ho so nhan su neu khong co quyen HR/Admin.

### 3.3 Quan ly cong viec

Route de xuat neu giu route cu: `/taskManager`, `/postTask`, `/viewTask`  
Route de xuat lau dai: `/dept/tasks`, `/dept/tasks/create`, `/dept/tasks/detail`

Chuc nang:

- Xem task do Dept Manager tao.
- Loc theo title, status, start date, due date.
- Phan trang.
- Tao task moi.
- Giao task cho mot hoac nhieu nhan vien trong cung phong ban.
- Xem danh sach nhan vien duoc giao.
- Sua task.
- Cap nhat danh sach nguoi duoc giao.
- Khong cho giao task cho nhan vien khac phong ban.
- Khong cho xem/sua task khong thuoc scope cua Dept Manager.

Validation:

- Title bat buoc, gioi han do dai.
- Description bat buoc hoac cho phep rong tuy UI quyet dinh, nhung phai gioi han do dai.
- Start date khong duoc sau due date.
- Task phai co it nhat mot assignee neu trang thai duoc gui cho nhan vien.
- Date format thong nhat giua form tao va form sua.

Trang thai task de xuat:

- `Draft`: Moi tao, chua gui.
- `Waiting`: Da gui/cho nhan vien nhan.
- `In Progress`: Nhan vien dang thuc hien.
- `Completed`: Hoan thanh.
- `Rejected`: Bi tu choi/huy.

Neu database hien tai chua can migration, co the tam dung status dang co va chuan hoa label tren UI.

### 3.4 Lich nhom

Route de xuat: `/dept/calendar`

Phien ban dau:

- Hien thi task theo ngay den han.
- Hien thi don nghi phep da duyet cua nhan vien trong phong ban.
- Loc theo tuan/thang.

Ngoai pham vi phien ban dau:

- Tao meeting that trong database neu chua co bang lich hop.
- Dong bo Google Calendar.

### 3.5 Yeu cau nghi phep

Route de xuat: `/dept/leaves`

Trang nay chua co trong Dept Manager hien tai va can duoc them.

Nguon du lieu:

- Bang `MailRequest`.
- Chi lay `RequestType = 'Leave'`.
- Join `Employee` de loc theo `Employee.DepartmentID` cua Dept Manager.

Chuc nang:

- Xem don nghi phep cua nhan vien trong phong ban.
- Loc theo `Pending`, `Approved`, `Rejected`, `All`.
- Hien thi nhan vien, email, loai nghi, tu ngay, den ngay, ly do, trang thai.
- Duyet don `Pending`.
- Tu choi don `Pending`.
- Ghi `ApprovedBy` bang EmployeeID cua Dept Manager dang thao tac.
- Khong cho duyet don cua nhan vien khac phong ban.
- Khong cho duyet lai don da `Approved` hoac `Rejected`.

Quan he voi HR Manager:

- HR Manager hien da co `/hr/leaves` duyet tat ca don.
- Dept Manager se co `/dept/leaves` chi duyet don trong phong ban.
- Neu ca HR Manager va Dept Manager cung co quyen duyet, cau lenh update phai kem dieu kien `Status = 'Pending'` de tranh duyet hai lan.

### 3.6 Danh gia hieu suat

Route de xuat: `/dept/performance`

Phien ban dau:

- Hien thi tong hop theo nhan vien: task duoc giao, task hoan thanh, task qua han, ty le hoan thanh.
- Khong tao bang danh gia moi neu scope can gon.

Ngoai pham vi phien ban dau:

- Quy trinh cham diem chinh thuc.
- Phieu danh gia ky/quy co approval.

### 3.7 Bao cao phong ban

Route de xuat: `/dept/reports`

Noi dung:

- Tong so nhan vien.
- Phan bo trang thai nhan vien.
- Thong ke task theo status.
- Thong ke don nghi phep theo status/thang.
- Danh sach task qua han.

Phien ban dau chi can hien thi tren UI, chua can export Excel/PDF.

## 4. Kien truc ung dung

### 4.1 Layout dung chung

Tao include JSP dung chung cho Dept Manager:

- `Views/DeptManager/_DeptManagerSidebar.jspf`
- `Views/DeptManager/_DeptManagerTopbar.jspf`
- `Views/DeptManager/_DeptManagerStyles.jspf` hoac CSS rieng trong `css/dept-manager.css`

Moi trang Dept Manager include chung cac file nay thay vi tu viet lai sidebar/topbar.

### 4.2 Controller

Controller phai theo thu tu:

1. Lay session.
2. Kiem tra `systemUser`.
3. Kiem tra role Admin hoac Dept Manager.
4. Lay Employee va DepartmentID cua user.
5. Neu action can task/leave cu the, kiem tra ban ghi do thuoc department scope.
6. Moi sau do moi xu ly action.

Khong co action nao duoc chay truoc buoc auth/scope. Diem nay can sua trong `TaskManager` hien tai.

### 4.3 DAO

Can bo sung hoac chuan hoa DAO:

- Task queries theo `AssignedBy` va department scope.
- Task assignee queries join `assignlist` voi `Employee`.
- Employee queries theo `DepartmentID`.
- MailRequest queries theo `DepartmentID` cho Dept Manager.
- Dashboard count queries cho task, employee, leave.

Khong nen tiep tuc gom them logic moi vao `DAO.java` neu co the tach sang DAO chuyen trach. Tuy nhien de giam rui ro, co the bo sung toi thieu vao DAO hien co neu module dang dung `DAO.getInstance()`.

## 5. Bao mat va phan quyen

Can sua cac diem sau:

- `TaskManager` khong duoc xu ly `viewAssignees`, `reject`, `send` truoc khi kiem tra session/role.
- `PostTask` phai kiem tra role Dept Manager/Admin, khong chi check login.
- `ViewTask` phai kiem tra role va task scope.
- Moi update task phai xac minh task thuoc Dept Manager hien tai.
- Moi assign task phai xac minh assignee thuoc cung department.
- `DeptLeaveController` phai update don nghi phep voi dieu kien department scope va `Status = 'Pending'`.

Neu request AJAX khong hop le, tra loi 401/403/404 phu hop thay vi redirect HTML neu co the.

## 6. Loi va thong bao

- Khong co department: hien thong bao "Tai khoan chua duoc gan phong ban".
- Khong co du lieu: hien empty state gon, dung style BetterHR.
- Input sai: hien loi tren form, giu lai du lieu da nhap.
- Ban ghi khong thuoc phong ban: tra ve Access Denied hoac 404.
- Duyet don da duoc xu ly: hien "Don nay da duoc cap nhat truoc do".

## 7. Thu tu trien khai de xuat

1. Tao shell chung Dept Manager: sidebar/topbar/style include.
2. Sua dashboard de dung du lieu that va include shell chung.
3. Refactor task pages vao shell chung, bo giao dien Bootstrap rieng le trong anh 1.
4. Sua bao mat TaskManager/PostTask/ViewTask: auth truoc action, scope theo department.
5. Them trang nhan vien phong ban.
6. Them trang yeu cau nghi phep va controller duyet/tu choi theo department.
7. Them trang lich nhom dua tren task due date va leave da duyet.
8. Them trang performance/report voi thong ke tu task/leave/employee.
9. Chay compile va test role-based access.

## 8. Tieu chi nghiem thu

1. Moi trang Dept Manager deu co sidebar/topbar chung nhu dashboard tham chieu.
2. Man hinh Task Manager cu trong anh 1 khong con xuat hien nhu mot layout rieng.
3. Dept Manager vao duoc dashboard va cac menu con.
4. Dept Manager chi thay nhan vien cung phong ban.
5. Dept Manager chi tao/giao/sua task trong phong ban minh.
6. Khong the xem/sua task cua phong ban khac bang cach sua URL.
7. Dept Manager xem duoc don nghi phep cua nhan vien trong phong ban.
8. Dept Manager duyet/tu choi duoc don `Pending`.
9. Dept Manager khong duyet duoc don cua phong ban khac.
10. Don da duyet/tu choi khong bi duyet lai.
11. HR Manager route `/hr/leaves` van hoat dong nhu hien tai.
12. Admin van co the truy cap module Dept Manager de kiem tra.
13. Employee/HR Staff/Guest bi chan khoi cac route Dept Manager.
14. Du an compile thanh cong bang Maven.

## 9. Ngoai pham vi phien ban dau

- Export bao cao Excel/PDF.
- Tao lich hop rieng va dong bo calendar.
- Bang danh gia hieu suat chinh thuc nhieu cap phe duyet.
- Notification realtime.
- Doi schema task lon neu co the hoan thanh bang schema hien tai.

## 10. Tom tat trang can lam

| Trang | Route de xuat | Trang thai hien tai | Viec can lam |
|---|---|---|---|
| Tong quan nhom | `/dept` | Da co nhung con du lieu gia | Gan du lieu that, dung shell chung |
| Nhan vien phong ban | `/dept/employees` | Chua co trang rieng ro rang | Tao danh sach theo DepartmentID |
| Quan ly cong viec | `/dept/tasks` hoac `/taskManager` | Da co nhung UI cu va thieu scope | Doi UI, sua auth/scope, validate |
| Tao cong viec | `/dept/tasks/create` hoac `/postTask` | Da co | Doi UI, validate, chan assignee khac phong ban |
| Chi tiet/sua task | `/dept/tasks/detail` hoac `/viewTask` | Da co | Doi UI, chan task ngoai scope |
| Lich nhom | `/dept/calendar` | Chua co | Hien task due date va leave da duyet |
| Yeu cau nghi phep | `/dept/leaves` | Chua co cho Dept Manager | Them list/approve/reject theo phong ban |
| Danh gia hieu suat | `/dept/performance` | Dashboard co mock | Lam thong ke task theo nhan vien |
| Bao cao phong ban | `/dept/reports` | Dashboard co mock | Lam report task/leave/employee |
