package com.hrm.controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hrm.dao.ChatbotFeedbackDAO;
import com.hrm.model.entity.SystemUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "ChatbotFeedbackController", urlPatterns = {"/api/chatbot/feedback"})
public class ChatbotFeedbackController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ChatbotFeedbackController.class.getName());
    private static final Set<String> RATINGS = Set.of("Useful", "NotUseful");
    private static final int MAX_COMMENT_LENGTH = 1000;

    private final Gson gson = new Gson();
    private final ChatbotFeedbackDAO feedbackDAO = new ChatbotFeedbackDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        FeedbackRequest feedbackRequest;
        try {
            feedbackRequest = gson.fromJson(request.getReader(), FeedbackRequest.class);
        } catch (JsonSyntaxException ex) {
            sendJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    "error", "Dữ liệu phản hồi không hợp lệ.");
            return;
        }

        if (feedbackRequest == null
                || feedbackRequest.messageId == null
                || feedbackRequest.messageId <= 0
                || !RATINGS.contains(feedbackRequest.rating)) {
            sendJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    "error", "Message ID hoặc mức đánh giá không hợp lệ.");
            return;
        }

        String comment = feedbackRequest.comment == null ? "" : feedbackRequest.comment.trim();
        if (comment.length() > MAX_COMMENT_LENGTH) {
            sendJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    "error", "Nội dung phản hồi không được quá 1000 ký tự.");
            return;
        }

        HttpSession session = request.getSession(false);
        Long conversationId = sessionLong(
                session, ChatbotController.CONVERSATION_SESSION_KEY);
        if (conversationId == null) {
            sendJson(response, HttpServletResponse.SC_CONFLICT,
                    "error", "Phiên chat đã hết hạn. Hãy gửi lại câu hỏi trước khi đánh giá.");
            return;
        }

        SystemUser currentUser = currentUser(session);
        Integer userId = currentUser != null && currentUser.getUserId() > 0
                ? currentUser.getUserId() : null;
        try {
            boolean saved = feedbackDAO.saveOrUpdate(
                    conversationId,
                    feedbackRequest.messageId,
                    userId,
                    feedbackRequest.rating,
                    comment);
            if (!saved) {
                sendJson(response, HttpServletResponse.SC_FORBIDDEN,
                        "error", "Bạn không thể đánh giá câu trả lời ngoài phiên chat hiện tại.");
                return;
            }
            sendJson(response, HttpServletResponse.SC_OK,
                    "success", "Cảm ơn bạn đã phản hồi.");
        } catch (SQLException | RuntimeException ex) {
            LOGGER.log(Level.WARNING, "Cannot save chatbot feedback.", ex);
            sendJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "error", "Chưa thể lưu phản hồi lúc này.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Allow", "POST");
        sendJson(response, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                "error", "Vui lòng gửi phản hồi bằng phương thức POST.");
    }

    private SystemUser currentUser(HttpSession session) {
        Object user = session != null ? session.getAttribute("systemUser") : null;
        return user instanceof SystemUser ? (SystemUser) user : null;
    }

    private Long sessionLong(HttpSession session, String key) {
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute(key);
        return value instanceof Number ? ((Number) value).longValue() : null;
    }

    private void sendJson(HttpServletResponse response,
                          int status,
                          String result,
                          String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(new FeedbackResponse(result, message)));
    }

    private static class FeedbackRequest {
        private Long messageId;
        private String rating;
        private String comment;
    }

    private static class FeedbackResponse {
        private final String status;
        private final String message;

        private FeedbackResponse(String status, String message) {
            this.status = status;
            this.message = message;
        }
    }
}
