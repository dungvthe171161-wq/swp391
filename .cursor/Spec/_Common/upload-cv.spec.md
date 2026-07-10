# Cross-cutting Spec: Upload CV
Status: Approved
Priority: High
Related Code: `RecruitmentController`, `GuestDAO`, `Views/ApplyForm.jsp`, `Upload/cvs`

## Muc tieu
Dam bao ung vien public upload CV an toan khi nop don ung tuyen.

## File rule
- Dinh dang cho phep: `.pdf`, `.doc`, `.docx`.
- MIME type phai duoc validate o backend neu co the.
- Size toi da de xuat: 5MB.
- Ten file luu phai random/UUID, khong dung truc tiep ten file client.
- Khong cho path traversal bang `../` hoac absolute path.

## Luong upload
1. Ung vien submit application kem file CV.
2. Controller validate recruitment id va thong tin ca nhan.
3. Controller validate file.
4. Luu file vao folder upload hop le.
5. Luu path tuong doi vao database.
6. Thanh cong redirect success.

## Loi upload
- File rong neu CV bat buoc: bao loi validation.
- Sai dinh dang: bao loi validation.
- Qua dung luong: bao loi validation.
- Luu file that bai: khong tao application hoac rollback ban ghi neu da tao.

## Acceptance Criteria
- [ ] Khong chap nhan file ngoai `.pdf`, `.doc`, `.docx`.
- [ ] Khong luu ten file goc lam ten file chinh.
- [ ] Khong chap nhan path traversal.
- [ ] Submit loi khong tao application mo coi khong co CV neu CV bat buoc.

## Missing Work
- [ ] Xac nhan max file size trong `@MultipartConfig`.
- [ ] Them test upload file hop le/sai dinh dang/qua size.
