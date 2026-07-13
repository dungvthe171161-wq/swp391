-- Normalize chatbot FAQ seed data to Vietnamese with accents.
-- Run after 2026-07-10_chatbot_phase2_foundation.sql on existing databases.

SET NAMES utf8mb4;

UPDATE ChatbotFaq f
JOIN (
    SELECT 'apply_job' Intent, 'Public' AudienceRole,
           'Cách nộp hồ sơ' Question,
           'Bạn có thể vào mục Việc làm trên trang chủ để xem vị trí đang tuyển và nộp hồ sơ. Nếu đã có tài khoản ứng viên, hãy đăng nhập để theo dõi hồ sơ của mình.' Answer,
           'Xem trạng thái ứng tuyển|Lịch phỏng vấn|Liên hệ HR' Suggestions,
           10 SortOrder
    UNION ALL SELECT 'application_status', 'Public',
           'Xem trạng thái ứng tuyển',
           'Bạn hãy đăng nhập cổng ứng viên để xem trạng thái ứng tuyển của chính mình. Vì lý do bảo mật, chatbot không hiển thị chi tiết hồ sơ trực tiếp trong khung chat.',
           'Cách nộp hồ sơ|Lịch phỏng vấn|Liên hệ HR',
           20
    UNION ALL SELECT 'contact_hr', 'All',
           'Liên hệ HR',
           'Bạn có thể liên hệ HR qua mục Liên hệ trên trang chủ hoặc gửi yêu cầu cho bộ phận nhân sự trong hệ thống.',
           'Cách đổi mật khẩu|Cách nộp hồ sơ|Cách xin nghỉ phép',
           30
    UNION ALL SELECT 'leave_request', 'Employee',
           'Cách xin nghỉ phép',
           'Nhân viên có thể vào cổng nhân viên và chọn mục Nghỉ phép để tạo hoặc theo dõi đơn nghỉ.',
           'Xem nhiệm vụ|Xem bảng lương|Liên hệ HR',
           40
    UNION ALL SELECT 'payroll_view', 'Employee',
           'Xem bảng lương',
           'Bạn có thể vào cổng nhân viên và chọn mục Bảng lương để xem thông tin của mình. Chatbot không hiển thị số lương trực tiếp trong khung chat.',
           'Cách xin nghỉ phép|Liên hệ HR|Xem hợp đồng',
           50
    UNION ALL SELECT 'task_view', 'Employee',
           'Xem nhiệm vụ',
           'Bạn có thể vào cổng nhân viên hoặc khu vực quản lý phù hợp với vai trò hiện tại để xem nhiệm vụ được giao.',
           'Cách xin nghỉ phép|Xem bảng lương|Liên hệ HR',
           60
    UNION ALL SELECT 'contract_view', 'Employee',
           'Xem hợp đồng',
           'Bạn có thể xem hợp đồng trong khu vực hồ sơ nhân viên hoặc màn hình hợp đồng tương ứng với vai trò của mình. Chatbot không hiển thị chi tiết hợp đồng trong khung chat.',
           'Xem bảng lương|Liên hệ HR|Cách đổi mật khẩu',
           70
) seed ON seed.Intent = f.Intent AND seed.AudienceRole = f.AudienceRole
SET
    f.Question = seed.Question,
    f.Answer = seed.Answer,
    f.Suggestions = seed.Suggestions,
    f.SortOrder = seed.SortOrder,
    f.IsActive = TRUE;

INSERT INTO ChatbotFaq (Intent, AudienceRole, Question, Answer, Suggestions, SortOrder, IsActive)
SELECT seed.Intent, seed.AudienceRole, seed.Question, seed.Answer, seed.Suggestions, seed.SortOrder, TRUE
FROM (
    SELECT 'apply_job' Intent, 'Public' AudienceRole,
           'Cách nộp hồ sơ' Question,
           'Bạn có thể vào mục Việc làm trên trang chủ để xem vị trí đang tuyển và nộp hồ sơ. Nếu đã có tài khoản ứng viên, hãy đăng nhập để theo dõi hồ sơ của mình.' Answer,
           'Xem trạng thái ứng tuyển|Lịch phỏng vấn|Liên hệ HR' Suggestions,
           10 SortOrder
    UNION ALL SELECT 'application_status', 'Public',
           'Xem trạng thái ứng tuyển',
           'Bạn hãy đăng nhập cổng ứng viên để xem trạng thái ứng tuyển của chính mình. Vì lý do bảo mật, chatbot không hiển thị chi tiết hồ sơ trực tiếp trong khung chat.',
           'Cách nộp hồ sơ|Lịch phỏng vấn|Liên hệ HR',
           20
    UNION ALL SELECT 'contact_hr', 'All',
           'Liên hệ HR',
           'Bạn có thể liên hệ HR qua mục Liên hệ trên trang chủ hoặc gửi yêu cầu cho bộ phận nhân sự trong hệ thống.',
           'Cách đổi mật khẩu|Cách nộp hồ sơ|Cách xin nghỉ phép',
           30
    UNION ALL SELECT 'leave_request', 'Employee',
           'Cách xin nghỉ phép',
           'Nhân viên có thể vào cổng nhân viên và chọn mục Nghỉ phép để tạo hoặc theo dõi đơn nghỉ.',
           'Xem nhiệm vụ|Xem bảng lương|Liên hệ HR',
           40
    UNION ALL SELECT 'payroll_view', 'Employee',
           'Xem bảng lương',
           'Bạn có thể vào cổng nhân viên và chọn mục Bảng lương để xem thông tin của mình. Chatbot không hiển thị số lương trực tiếp trong khung chat.',
           'Cách xin nghỉ phép|Liên hệ HR|Xem hợp đồng',
           50
    UNION ALL SELECT 'task_view', 'Employee',
           'Xem nhiệm vụ',
           'Bạn có thể vào cổng nhân viên hoặc khu vực quản lý phù hợp với vai trò hiện tại để xem nhiệm vụ được giao.',
           'Cách xin nghỉ phép|Xem bảng lương|Liên hệ HR',
           60
    UNION ALL SELECT 'contract_view', 'Employee',
           'Xem hợp đồng',
           'Bạn có thể xem hợp đồng trong khu vực hồ sơ nhân viên hoặc màn hình hợp đồng tương ứng với vai trò của mình. Chatbot không hiển thị chi tiết hợp đồng trong khung chat.',
           'Xem bảng lương|Liên hệ HR|Cách đổi mật khẩu',
           70
) seed
WHERE NOT EXISTS (
    SELECT 1
    FROM ChatbotFaq existing
    WHERE existing.Intent = seed.Intent
      AND existing.AudienceRole = seed.AudienceRole
);
