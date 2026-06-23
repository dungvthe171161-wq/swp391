# Cross-cutting Spec: UI Language And BetterHR Theme
Status: Approved
Priority: High
Related Code: `src/main/webapp/Views/**/*.jsp`, `src/main/webapp/css/*.css`, `src/main/webapp/Admin/**/*.jsp`

## Muc tieu
Chuan hoa giao dien BetterHR tren toan bo JSP: mau sac, font chu, ngon ngu hien thi va cach gan link/form phai dong bo ma khong lam thay doi logic controller hien co.

## Nguyen tac ngon ngu
- Moi text hien thi cho nguoi dung phai la tieng Viet.
- Ten thuong hieu giu nguyen la `BetterHR`.
- Khong de lai text tieng Anh trong nut, title, label, placeholder, alert, card, menu neu nguoi dung co the nhin thay.
- Khong de loi mojibake/ky tu replacement trong JSP/spec/email template.
- Gia tri ky thuat gui ve backend duoc giu nguyen neu code dang dung, vi du `Pending`, `Approved`, `Rejected`, `Paid`, `Employee`, `Google`, `LOCAL`.

## Nguyen tac giao dien
- Dung bang mau BetterHR: nen warm neutral, xanh la chinh, xanh dam cho heading, mau phu nhe cho badge/trang thai.
- Font uu tien dong bo voi cac trang da sua: `Hanken Grotesk`, fallback `Inter`, `Segoe UI`, sans-serif.
- Card va form phai co border radius vua phai, khong long card trong card neu khong can.
- Form phai ro label, placeholder, validation message, trang thai focus va disabled.
- Sidebar/topbar giu cau truc dieu huong ro, menu active phai co mau va chu de doc.

## Nguyen tac khong doi logic
- Khi chi sua giao dien, khong doi `action`, `method`, `name`, `value`, hidden input, JSTL condition, servlet route.
- Neu can doi route/link de dung luong controller, phai ghi ro trong spec va chi sua khi duoc yeu cau.
- Khong thay doi ten attribute ma controller set cho JSP.
- Khong chuyen truc tiep sang JSP neu controller can nap du lieu truoc.

## Source of truth
- Source chinh nam trong `src/main/webapp`.
- Thu muc `target/HRMS` va `src/target/HRMS` la ban build/deploy sinh ra, khong duoc coi la spec source.
- Neu CSS bi cache tren trinh duyet, duoc phep them query version nhu `hr-theme.css?v=...`.

## Acceptance Criteria
- [ ] Tat ca JSP public/auth/admin/hr/hrstaff/employee/dept hien text tieng Viet, tru `BetterHR`.
- [ ] Khong con text bi loi font/mojibake tren cac trang da sua.
- [ ] Form van submit ve dung servlet/action cu.
- [ ] Link dieu huong vao cac trang can data phai di qua controller.
- [ ] Mau sac va font chu dong bo voi BetterHR theme.
