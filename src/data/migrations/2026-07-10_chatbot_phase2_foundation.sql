-- BetterHR Chatbot Phase 2 foundation
-- Scope: DB-managed FAQ, chat history, and feedback.
-- Safe to run multiple times on MySQL 8.x.

CREATE TABLE IF NOT EXISTS ChatbotFaq (
    FaqID INT AUTO_INCREMENT PRIMARY KEY,
    Intent VARCHAR(80) NOT NULL,
    AudienceRole VARCHAR(80) NOT NULL DEFAULT 'All',
    Question VARCHAR(500) NOT NULL,
    Answer TEXT NOT NULL,
    Suggestions VARCHAR(1000) NULL,
    SortOrder INT NOT NULL DEFAULT 0,
    IsActive BOOLEAN NOT NULL DEFAULT TRUE,
    CreatedBy INT NULL,
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedBy INT NULL,
    UpdatedAt DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_chatbotfaq_created_by
        FOREIGN KEY (CreatedBy) REFERENCES SystemUser(UserID)
        ON DELETE SET NULL,
    CONSTRAINT fk_chatbotfaq_updated_by
        FOREIGN KEY (UpdatedBy) REFERENCES SystemUser(UserID)
        ON DELETE SET NULL,
    INDEX idx_chatbotfaq_intent_active (Intent, IsActive),
    INDEX idx_chatbotfaq_audience_active (AudienceRole, IsActive),
    UNIQUE KEY uq_chatbotfaq_intent_role_question (Intent, AudienceRole, Question(191))
);

CREATE TABLE IF NOT EXISTS ChatConversation (
    ConversationID BIGINT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NULL,
    RoleName VARCHAR(100) NULL,
    PagePath VARCHAR(255) NULL,
    Channel VARCHAR(40) NOT NULL DEFAULT 'web',
    Status ENUM('Open', 'Closed') NOT NULL DEFAULT 'Open',
    StartedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    LastMessageAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_chatconversation_user
        FOREIGN KEY (UserID) REFERENCES SystemUser(UserID)
        ON DELETE SET NULL,
    INDEX idx_chatconversation_user_started (UserID, StartedAt),
    INDEX idx_chatconversation_status (Status)
);

CREATE TABLE IF NOT EXISTS ChatMessage (
    MessageID BIGINT AUTO_INCREMENT PRIMARY KEY,
    ConversationID BIGINT NOT NULL,
    SenderType ENUM('User', 'Bot', 'System') NOT NULL,
    Intent VARCHAR(80) NULL,
    MessageText TEXT NOT NULL,
    IsFallback BOOLEAN NOT NULL DEFAULT FALSE,
    MetadataJson JSON NULL,
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_chatmessage_conversation
        FOREIGN KEY (ConversationID) REFERENCES ChatConversation(ConversationID)
        ON DELETE CASCADE,
    INDEX idx_chatmessage_conversation_created (ConversationID, CreatedAt),
    INDEX idx_chatmessage_intent (Intent),
    INDEX idx_chatmessage_fallback (IsFallback)
);

CREATE TABLE IF NOT EXISTS ChatbotFeedback (
    FeedbackID BIGINT AUTO_INCREMENT PRIMARY KEY,
    ConversationID BIGINT NOT NULL,
    MessageID BIGINT NULL,
    UserID INT NULL,
    Rating ENUM('Useful', 'NotUseful') NOT NULL,
    Comment VARCHAR(1000) NULL,
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_chatbotfeedback_conversation
        FOREIGN KEY (ConversationID) REFERENCES ChatConversation(ConversationID)
        ON DELETE CASCADE,
    CONSTRAINT fk_chatbotfeedback_message
        FOREIGN KEY (MessageID) REFERENCES ChatMessage(MessageID)
        ON DELETE SET NULL,
    CONSTRAINT fk_chatbotfeedback_user
        FOREIGN KEY (UserID) REFERENCES SystemUser(UserID)
        ON DELETE SET NULL,
    INDEX idx_chatbotfeedback_rating_created (Rating, CreatedAt)
);

INSERT INTO ChatbotFaq (Intent, AudienceRole, Question, Answer, Suggestions, SortOrder, IsActive)
VALUES
('apply_job', 'Public', 'Cách nộp hồ sơ', 'Bạn có thể vào mục Việc làm trên trang chủ để xem vị trí đang tuyển và nộp hồ sơ. Nếu đã có tài khoản ứng viên, hãy đăng nhập để theo dõi hồ sơ của mình.', 'Xem trạng thái ứng tuyển|Lịch phỏng vấn|Liên hệ HR', 10, TRUE),
('application_status', 'Public', 'Xem trạng thái ứng tuyển', 'Bạn hãy đăng nhập cổng ứng viên để xem trạng thái ứng tuyển của chính mình. Vì lý do bảo mật, chatbot không hiển thị chi tiết hồ sơ trực tiếp trong khung chat.', 'Cách nộp hồ sơ|Lịch phỏng vấn|Liên hệ HR', 20, TRUE),
('contact_hr', 'All', 'Liên hệ HR', 'Bạn có thể liên hệ HR qua mục Liên hệ trên trang chủ hoặc gửi yêu cầu cho bộ phận nhân sự trong hệ thống.', 'Cách đổi mật khẩu|Cách nộp hồ sơ|Cách xin nghỉ phép', 30, TRUE),
('leave_request', 'Employee', 'Cách xin nghỉ phép', 'Nhân viên có thể vào cổng nhân viên và chọn mục Nghỉ phép để tạo hoặc theo dõi đơn nghỉ.', 'Xem nhiệm vụ|Xem bảng lương|Liên hệ HR', 40, TRUE),
('payroll_view', 'Employee', 'Xem bảng lương', 'Bạn có thể vào cổng nhân viên và chọn mục Bảng lương để xem thông tin của mình. Chatbot không hiển thị số lương trực tiếp trong khung chat.', 'Cách xin nghỉ phép|Liên hệ HR|Xem hợp đồng', 50, TRUE),
('task_view', 'Employee', 'Xem nhiệm vụ', 'Bạn có thể vào cổng nhân viên hoặc khu vực quản lý phù hợp với vai trò hiện tại để xem nhiệm vụ được giao.', 'Cách xin nghỉ phép|Xem bảng lương|Liên hệ HR', 60, TRUE),
('contract_view', 'Employee', 'Xem hợp đồng', 'Bạn có thể xem hợp đồng trong khu vực hồ sơ nhân viên hoặc màn hình hợp đồng tương ứng với vai trò của mình. Chatbot không hiển thị chi tiết hợp đồng trong khung chat.', 'Xem bảng lương|Liên hệ HR|Cách đổi mật khẩu', 70, TRUE)
ON DUPLICATE KEY UPDATE
    Answer = VALUES(Answer),
    Suggestions = VALUES(Suggestions),
    SortOrder = VALUES(SortOrder),
    IsActive = VALUES(IsActive);
