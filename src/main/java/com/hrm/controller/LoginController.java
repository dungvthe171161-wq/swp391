package com.hrm.controller;

import com.hrm.dao.DAO;
import com.hrm.dao.DBConnection;
import com.hrm.model.entity.SystemUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet(name = "LoginController", urlPatterns = {"/login"})
public class LoginController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                request.setAttribute(cookie.getName(), cookie.getValue());
            }
        }

        String error = request.getParameter("error");
        if (error != null) {
            request.setAttribute("mess", toLoginErrorMessage(error));
        }
        String success = request.getParameter("success");
        if ("registered".equals(success)) {
            request.setAttribute("successMess", "Tạo tài khoản thành công. Bạn có thể đăng nhập ngay.");
        } else if ("registered_mail_failed".equals(success)) {
            request.setAttribute("successMess", "Tạo tài khoản thành công. Chưa gửi được email xác nhận, nhưng bạn có thể đăng nhập ngay.");
        } else if ("password_changed".equals(success)) {
            request.setAttribute("successMess", "Đổi mật khẩu thành công. Bạn có thể đăng nhập bằng mật khẩu mới.");
        }

        request.getRequestDispatcher("/Views/Login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String login = trim(request.getParameter("user"));
        String password = trim(request.getParameter("pass"));
        String remember = request.getParameter("rememberMe");

        if (login == null || password == null || login.isEmpty() || password.isEmpty()
                || login.length() > 150 || password.length() > 100) {
            forwardLogin(request, response, "Tên đăng nhập/email hoặc mật khẩu không hợp lệ.");
            return;
        }

        if (!login.matches("^[A-Za-z0-9._@-]+$")) {
            forwardLogin(request, response, "Tên đăng nhập/email không đúng định dạng.");
            return;
        }

        if (!DBConnection.canConnect()) {
            forwardLogin(request, response, "Không kết nối được cơ sở dữ liệu. Vui lòng kiểm tra MySQL và db.properties.");
            return;
        }

        SystemUser user = DAO.getInstance().getAccountByUsernameOrEmail(login.toLowerCase());

        if (user != null && !user.isIsActive()) {
            forwardLogin(request, response, "Tài khoản đã bị vô hiệu hóa.");
            return;
        }

        if (user != null && user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            forwardLogin(request, response, "Tài khoản đang tạm khóa. Vui lòng thử lại sau.");
            return;
        }

        if (user != null && (user.getPasswordHash() == null || user.getPasswordHash().isBlank())) {
            forwardLogin(request, response, "Tài khoản này dùng đăng nhập Google. Vui lòng tiếp tục với Google.");
            return;
        }

        if (user != null && DAO.getInstance().checkPassword(password, user.getPasswordHash())) {
            DAO.getInstance().recordSuccessfulLogin(user.getUserId());
            user = DAO.getInstance().getAccountByUsernameOrEmail(login.toLowerCase());

            HttpSession oldSession = request.getSession(false);
            String redirectAfterLogin = oldSession != null
                    ? (String) oldSession.getAttribute("redirectAfterLogin")
                    : null;
            if (oldSession != null) {
                oldSession.invalidate();
            }

            HttpSession newSession = request.getSession(true);
            newSession.setAttribute("systemUser", user);

            Cookie userCookie = new Cookie("username", login);
            userCookie.setHttpOnly(true);
            userCookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
            userCookie.setMaxAge(remember != null ? 24 * 60 * 60 : 0);
            response.addCookie(userCookie);

            if (redirectAfterLogin != null && redirectAfterLogin.startsWith(request.getContextPath() + "/")) {
                response.sendRedirect(redirectAfterLogin);
            } else {
                response.sendRedirect(request.getContextPath() + "/homepage");
            }
            return;
        }

        if (user != null) {
            DAO.getInstance().recordFailedLogin(user.getUsername());
        }
        forwardLogin(request, response, "Sai tên đăng nhập/email hoặc mật khẩu.");
    }

    private void forwardLogin(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("mess", message);
        request.getRequestDispatcher("/Views/Login.jsp").forward(request, response);
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private String toLoginErrorMessage(String error) {
        return switch (error) {
            case "google_config" -> "Chưa cấu hình Google Client ID/Secret.";
            case "login_required" -> "Vui lòng đăng nhập để tiếp tục ứng tuyển.";
            case "google_denied" -> "Bạn đã hủy đăng nhập Google.";
            case "google_state", "google_code", "google_token", "google_profile", "google_account", "google" ->
                    "Không thể đăng nhập bằng Google. Vui lòng thử lại.";
            case "db" -> "Không kết nối được cơ sở dữ liệu. Vui lòng kiểm tra MySQL và db.properties.";
            case "inactive" -> "Tài khoản đã bị vô hiệu hóa.";
            case "locked" -> "Tài khoản đang tạm khóa. Vui lòng thử lại sau.";
            default -> "Đăng nhập không thành công. Vui lòng thử lại.";
        };
    }
}
