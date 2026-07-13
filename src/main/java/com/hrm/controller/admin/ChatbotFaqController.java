package com.hrm.controller.admin;

import com.hrm.dao.ChatbotFaqDAO;
import com.hrm.dao.ChatbotReviewDAO;
import com.hrm.model.entity.ChatbotFaq;
import com.hrm.model.entity.SystemUser;
import com.hrm.util.PermissionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "ChatbotFaqController", urlPatterns = {"/admin/chatbot-faqs"})
public class ChatbotFaqController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ChatbotFaqController.class.getName());
    private static final String CSRF_SESSION_KEY = "chatbotFaqCsrfToken";
    private static final List<String> AUDIENCES = List.of(
            "All", "Public", "Employee", "Dept Manager", "HR Staff", "HR Manager", "Admin");

    private final ChatbotFaqDAO faqDAO = new ChatbotFaqDAO();
    private final ChatbotReviewDAO reviewDAO = new ChatbotReviewDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SystemUser user = PermissionUtil.getCurrentUser(request);
        if (!canManageFaq(user)) {
            PermissionUtil.handleHtmlForbidden(
                    request, response, "Bạn không có quyền quản lý FAQ chatbot.");
            return;
        }

        String search = clean(request.getParameter("search"));
        String audience = clean(request.getParameter("audience"));
        String activeFilter = clean(request.getParameter("active"));
        Boolean active = parseActiveFilter(activeFilter);

        request.setAttribute("activePage", "chatbot-faqs");
        request.setAttribute("search", search);
        request.setAttribute("audienceFilter", audience);
        request.setAttribute("activeFilter", activeFilter);
        request.setAttribute("audiences", AUDIENCES);
        request.setAttribute("csrfToken", csrfToken(request));
        moveFlashToRequest(request);

        try {
            request.setAttribute("faqs", faqDAO.findAll(search, audience, active));
            request.setAttribute("reviewItems", reviewDAO.findRecentNeedsReview(40));
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Cannot load chatbot FAQ management page.", ex);
            request.setAttribute("faqs", Collections.emptyList());
            request.setAttribute("reviewItems", Collections.emptyList());
            request.setAttribute("errorMessage",
                    "Không thể đọc dữ liệu chatbot. Hãy kiểm tra kết nối database và migration Phase 2.");
        }

        request.getRequestDispatcher("/Views/AI/AI_Faq_Management.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        SystemUser user = PermissionUtil.getCurrentUser(request);
        if (!canManageFaq(user)) {
            PermissionUtil.handleHtmlForbidden(
                    request, response, "Bạn không có quyền quản lý FAQ chatbot.");
            return;
        }
        if (!isValidCsrf(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF token không hợp lệ.");
            return;
        }

        String action = clean(request.getParameter("action"));
        try {
            switch (action) {
                case "create" -> createFaq(request, user);
                case "update" -> updateFaq(request, user);
                case "activate" -> setFaqActive(request, user, true);
                case "deactivate" -> setFaqActive(request, user, false);
                default -> throw new IllegalArgumentException("Thao tác FAQ không hợp lệ.");
            }
        } catch (SQLIntegrityConstraintViolationException ex) {
            setFlash(request, false,
                    "FAQ bị trùng intent, vai trò và câu hỏi. Hãy sửa bản ghi hiện có.");
        } catch (IllegalArgumentException ex) {
            setFlash(request, false, ex.getMessage());
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Cannot update chatbot FAQ.", ex);
            setFlash(request, false, "Không thể cập nhật FAQ do lỗi database.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/chatbot-faqs");
    }

    private void createFaq(HttpServletRequest request, SystemUser user) throws SQLException {
        ChatbotFaq faq = readFaq(request, false);
        int faqId = faqDAO.create(faq, user.getUserId());
        if (faqId <= 0) {
            throw new SQLException("FAQ insert did not return an ID.");
        }
        setFlash(request, true, "Đã tạo FAQ mới.");
    }

    private void updateFaq(HttpServletRequest request, SystemUser user) throws SQLException {
        ChatbotFaq faq = readFaq(request, true);
        if (!faqDAO.update(faq, user.getUserId())) {
            throw new IllegalArgumentException("Không tìm thấy FAQ cần cập nhật.");
        }
        setFlash(request, true, "Đã cập nhật FAQ.");
    }

    private void setFaqActive(HttpServletRequest request, SystemUser user, boolean active)
            throws SQLException {
        int faqId = parsePositiveInt(request.getParameter("faqId"), "FAQ ID không hợp lệ.");
        if (!faqDAO.setActive(faqId, active, user.getUserId())) {
            throw new IllegalArgumentException("Không tìm thấy FAQ cần cập nhật trạng thái.");
        }
        setFlash(request, true, active ? "Đã bật FAQ." : "Đã ẩn FAQ.");
    }

    private ChatbotFaq readFaq(HttpServletRequest request, boolean requireId) {
        ChatbotFaq faq = new ChatbotFaq();
        if (requireId) {
            faq.setFaqId(parsePositiveInt(request.getParameter("faqId"), "FAQ ID không hợp lệ."));
        }

        String intent = clean(request.getParameter("intent")).toLowerCase();
        String audience = clean(request.getParameter("audienceRole"));
        String question = clean(request.getParameter("question"));
        String answer = clean(request.getParameter("answer"));
        String suggestions = clean(request.getParameter("suggestions"));
        int sortOrder = parseNonNegativeInt(request.getParameter("sortOrder"));

        if (!intent.matches("[a-z0-9_]{2,80}")) {
            throw new IllegalArgumentException(
                    "Intent chỉ gồm chữ thường không dấu, số, dấu gạch dưới và dài 2-80 ký tự.");
        }
        if (!AUDIENCES.contains(audience)) {
            throw new IllegalArgumentException("Vai trò nhận FAQ không hợp lệ.");
        }
        requireLength(question, "Câu hỏi", 1, 500);
        requireLength(answer, "Câu trả lời", 1, 4000);
        requireLength(suggestions, "Gợi ý", 0, 1000);
        if (sortOrder > 100000) {
            throw new IllegalArgumentException("Thứ tự phải nằm trong khoảng 0-100000.");
        }

        faq.setIntent(intent);
        faq.setAudienceRole(audience);
        faq.setQuestion(question);
        faq.setAnswer(answer);
        faq.setSuggestions(suggestions);
        faq.setSortOrder(sortOrder);
        faq.setActive("on".equals(request.getParameter("isActive"))
                || "true".equalsIgnoreCase(request.getParameter("isActive")));
        return faq;
    }

    private boolean canManageFaq(SystemUser user) {
        return user != null && user.getRoleId() == PermissionUtil.ROLE_ADMIN;
    }

    private Boolean parseActiveFilter(String value) {
        return switch (value) {
            case "active" -> Boolean.TRUE;
            case "inactive" -> Boolean.FALSE;
            default -> null;
        };
    }

    private int parsePositiveInt(String value, String message) {
        try {
            int parsed = Integer.parseInt(value);
            if (parsed > 0) {
                return parsed;
            }
        } catch (RuntimeException ignored) {
            // Converted to a user-facing validation error below.
        }
        throw new IllegalArgumentException(message);
    }

    private int parseNonNegativeInt(String value) {
        try {
            int parsed = Integer.parseInt(value);
            if (parsed >= 0) {
                return parsed;
            }
        } catch (RuntimeException ignored) {
            // Converted to a user-facing validation error below.
        }
        throw new IllegalArgumentException("Thứ tự phải là số nguyên không âm.");
    }

    private void requireLength(String value, String label, int min, int max) {
        int length = value == null ? 0 : value.length();
        if (length < min || length > max) {
            throw new IllegalArgumentException(
                    label + " phải dài từ " + min + " đến " + max + " ký tự.");
        }
    }

    private String csrfToken(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        Object current = session.getAttribute(CSRF_SESSION_KEY);
        if (current instanceof String && !((String) current).isBlank()) {
            return (String) current;
        }
        String token = UUID.randomUUID().toString();
        session.setAttribute(CSRF_SESSION_KEY, token);
        return token;
    }

    private boolean isValidCsrf(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object expected = session != null ? session.getAttribute(CSRF_SESSION_KEY) : null;
        String actual = request.getParameter("csrfToken");
        if (!(expected instanceof String) || actual == null) {
            return false;
        }
        return MessageDigest.isEqual(
                ((String) expected).getBytes(StandardCharsets.UTF_8),
                actual.getBytes(StandardCharsets.UTF_8));
    }

    private void setFlash(HttpServletRequest request, boolean success, String message) {
        request.getSession(true).setAttribute(
                success ? "chatbotFaqSuccess" : "chatbotFaqError", message);
    }

    private void moveFlashToRequest(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        Object success = session.getAttribute("chatbotFaqSuccess");
        Object error = session.getAttribute("chatbotFaqError");
        if (success != null) {
            request.setAttribute("successMessage", success);
            session.removeAttribute("chatbotFaqSuccess");
        }
        if (error != null) {
            request.setAttribute("errorMessage", error);
            session.removeAttribute("chatbotFaqError");
        }
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
