package com.hrm.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.hrm.dao.ChatbotHistoryDAO;
import com.hrm.dao.ChatbotHistoryDAO.RecordResult;
import com.hrm.model.entity.Role;
import com.hrm.model.entity.SystemUser;
import com.hrm.service.ChatbotContentSafety;
import com.hrm.service.AiChatService;
import com.hrm.service.ChatbotRoleDataService;
import com.hrm.service.ChatbotRateLimiter;
import com.hrm.service.ChatbotService;
import com.hrm.service.ChatbotService.ChatbotResponse;
import com.hrm.util.PermissionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "ChatbotController", urlPatterns = {"/api/chatbot/message"})
public class ChatbotController extends HttpServlet {

    public static final String CONVERSATION_SESSION_KEY = "chatbotConversationId";
    private static final Logger LOGGER = Logger.getLogger(ChatbotController.class.getName());
    private static final int MAX_MESSAGE_LENGTH = 500;
    private final Gson gson = new GsonBuilder().create();
    private final ChatbotService chatbotService = new ChatbotService();
    private final ChatbotRoleDataService roleDataService = new ChatbotRoleDataService();
    private final AiChatService aiChatService = new AiChatService();
    private final ChatbotRateLimiter rateLimiter = new ChatbotRateLimiter();
    private final ChatbotHistoryDAO historyDAO = new ChatbotHistoryDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        ChatbotMessageRequest chatbotRequest;

        try {
            chatbotRequest = gson.fromJson(request.getReader(), ChatbotMessageRequest.class);
        } catch (JsonSyntaxException ex) {
            sendJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    chatbotService.validationError("Dữ liệu gửi lên không hợp lệ."));
            return;
        }

        String message = chatbotRequest != null ? chatbotRequest.getMessage() : null;
        if (message == null || message.trim().isEmpty()) {
            sendJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    chatbotService.validationError("Vui lòng nhập câu hỏi để tôi hỗ trợ."));
            return;
        }

        if (message.trim().length() > MAX_MESSAGE_LENGTH) {
            sendJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    chatbotService.validationError("Vui lòng nhập câu hỏi ngắn hơn 500 ký tự."));
            return;
        }

        SystemUser currentUser = currentUser(request);
        String roleName = roleName(currentUser);
        boolean authenticated = currentUser != null;
        String page = chatbotRequest.getPage();

        ChatbotResponse chatbotResponse = chatbotService.answer(message, page, roleName, authenticated);
        String roleAwareReply = roleDataService
                .answer(chatbotResponse.getIntent(), currentUser, roleName)
                .orElse(null);
        if (roleAwareReply != null) {
            chatbotResponse = chatbotResponse.withReply(roleAwareReply);
        }
        if ("fallback".equals(chatbotResponse.getIntent())) {
            HttpSession rateLimitSession = request.getSession(true);
            if (rateLimiter.tryAcquire(rateLimitSession.getId(), request.getRemoteAddr())) {
                String aiReply = aiChatService
                        .answerFallback(chatbotResponse.getIntent(), message, roleName)
                        .orElse(null);
                if (aiReply != null) {
                    chatbotResponse = chatbotResponse.withReply(aiReply);
                }
            } else {
                chatbotResponse = chatbotResponse.withIntentAndReply("rate_limited",
                        "Bạn đã gửi quá nhiều câu hỏi AI. Vui lòng chờ khoảng một phút rồi thử lại.");
            }
        }
        boolean automaticGreeting = chatbotRequest.isGreetingEvent()
                && "greeting".equals(chatbotResponse.getIntent());
        if (!automaticGreeting) {
            chatbotResponse = recordHistory(
                    request, currentUser, roleName, page, message, chatbotResponse);
        }
        sendJson(response, HttpServletResponse.SC_OK, chatbotResponse);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Allow", "POST");
        sendJson(response, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                chatbotService.validationError("Vui lòng gửi câu hỏi bằng phương thức POST."));
    }

    private SystemUser currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object user = session != null ? session.getAttribute("systemUser") : null;
        return user instanceof SystemUser ? (SystemUser) user : null;
    }

    private String roleName(SystemUser user) {
        if (user == null) {
            return null;
        }
        Role role = user.getRole();
        if (role != null && role.getRoleName() != null && !role.getRoleName().isBlank()) {
            return role.getRoleName();
        }
        return switch (user.getRoleId()) {
            case PermissionUtil.ROLE_ADMIN -> "Admin";
            case PermissionUtil.ROLE_HR_MANAGER -> "HR Manager";
            case PermissionUtil.ROLE_DEPT_MANAGER -> "Dept Manager";
            case PermissionUtil.ROLE_HR_STAFF -> "HR Staff";
            case PermissionUtil.ROLE_EMPLOYEE -> "Employee";
            case 6 -> "Guest";
            default -> null;
        };
    }

    private ChatbotResponse recordHistory(HttpServletRequest request,
                                          SystemUser currentUser,
                                          String roleName,
                                          String page,
                                          String message,
                                          ChatbotResponse chatbotResponse) {
        HttpSession session = request.getSession(true);
        Long existingConversationId = sessionLong(session, CONVERSATION_SESSION_KEY);
        Integer userId = currentUser != null && currentUser.getUserId() > 0
                ? currentUser.getUserId() : null;
        String historyMessage = ChatbotContentSafety.sanitizeForHistory(message);

        try {
            RecordResult result = historyDAO.recordExchange(
                    existingConversationId,
                    userId,
                    roleName,
                    page,
                    historyMessage,
                    chatbotResponse.getIntent(),
                    chatbotResponse.getReply(),
                    "fallback".equals(chatbotResponse.getIntent()));
            session.setAttribute(CONVERSATION_SESSION_KEY, result.getConversationId());
            return chatbotResponse.withTracking(
                    result.getConversationId(), result.getBotMessageId());
        } catch (SQLException | RuntimeException ex) {
            LOGGER.log(Level.WARNING,
                    "Cannot store chatbot history; returning the answer without tracking IDs.", ex);
            return chatbotResponse;
        }
    }

    private Long sessionLong(HttpSession session, String key) {
        Object value = session.getAttribute(key);
        return value instanceof Number ? ((Number) value).longValue() : null;
    }

    private void sendJson(HttpServletResponse response, int status, ChatbotResponse chatbotResponse)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(chatbotResponse));
    }

    private static class ChatbotMessageRequest {
        private String message;
        private String page;
        private String eventType;

        public String getMessage() {
            return message;
        }

        public String getPage() {
            return page;
        }

        public boolean isGreetingEvent() {
            return "greeting".equalsIgnoreCase(eventType);
        }
    }
}

