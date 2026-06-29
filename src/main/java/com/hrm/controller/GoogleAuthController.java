package com.hrm.controller;

import com.hrm.dao.DAO;
import com.hrm.dao.DBConnection;
import com.hrm.model.entity.GoogleUser;
import com.hrm.model.entity.SystemUser;
import constant.Iconstant;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

@WebServlet(name = "GoogleAuthController", urlPatterns = {"/auth/google", "/auth/google/callback", "/loginByGmail"})
public class GoogleAuthController extends HttpServlet {

    private static final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Logger LOGGER = Logger.getLogger(GoogleAuthController.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        if ("/auth/google".equals(path)) {
            redirectToGoogle(request, response);
            return;
        }
        handleCallback(request, response);
    }

    private void redirectToGoogle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (Iconstant.GOOGLE_CLIENT_ID.isBlank() || Iconstant.GOOGLE_CLIENT_SECRET.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/login?error=google_config");
            return;
        }

        String state = generateState();
        String redirectUri = getRedirectUri(request);
        HttpSession session = request.getSession(true);
        session.setAttribute("googleOAuthState", state);
        session.setAttribute("googleRedirectUri", redirectUri);

        String authUrl = GOOGLE_AUTH_URL
                + "?client_id=" + encode(Iconstant.GOOGLE_CLIENT_ID)
                + "&redirect_uri=" + encode(redirectUri)
                + "&response_type=code"
                + "&scope=" + encode("openid email profile")
                + "&state=" + encode(state)
                + "&access_type=online"
                + "&prompt=select_account";

        response.sendRedirect(authUrl);
    }

    private void handleCallback(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String error = request.getParameter("error");
        if (error != null) {
            response.sendRedirect(request.getContextPath() + "/login?error=google_denied");
            return;
        }

        HttpSession session = request.getSession(false);
        String expectedState = session != null ? (String) session.getAttribute("googleOAuthState") : null;
        String actualState = request.getParameter("state");
        if (expectedState == null || actualState == null || !expectedState.equals(actualState)) {
            response.sendRedirect(request.getContextPath() + "/login?error=google_state");
            return;
        }
        session.removeAttribute("googleOAuthState");

        String code = request.getParameter("code");
        if (code == null || code.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/login?error=google_code");
            return;
        }

        try {
            String redirectUri = session != null && session.getAttribute("googleRedirectUri") != null
                    ? (String) session.getAttribute("googleRedirectUri")
                    : getRedirectUri(request);
            if (session != null) {
                session.removeAttribute("googleRedirectUri");
            }

            JSONObject tokenResponse = exchangeCodeForToken(code, redirectUri);
            String accessToken = tokenResponse.optString("access_token", "");
            String idToken = tokenResponse.optString("id_token", "");
            if (idToken.isBlank() || !isTokenValid(idToken)) {
                response.sendRedirect(request.getContextPath() + "/login?error=google_token");
                return;
            }

            JSONObject profile = fetchGoogleProfile(idToken, accessToken);
            String googleId = profile.optString("sub", profile.optString("id", ""));
            String email = profile.optString("email", "").toLowerCase();
            String name = profile.optString("name", email);
            String avatarUrl = profile.optString("picture", null);
            GoogleUser googleUser = new GoogleUser(googleId, email, name, avatarUrl);

            if (googleUser.getGoogleId().isBlank() || googleUser.getEmail().isBlank()) {
                response.sendRedirect(request.getContextPath() + "/login?error=google_profile");
                return;
            }

            if (!DBConnection.canConnect()) {
                response.sendRedirect(request.getContextPath() + "/login?error=db");
                return;
            }

            SystemUser user = resolveGoogleUser(googleUser);
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/login?error=google_account");
                return;
            }
            if (!user.isIsActive()) {
                response.sendRedirect(request.getContextPath() + "/login?error=inactive");
                return;
            }
            if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
                response.sendRedirect(request.getContextPath() + "/login?error=locked");
                return;
            }

            HttpSession oldSession = request.getSession(false);
            String redirectAfterLogin = oldSession != null
                    ? (String) oldSession.getAttribute("redirectAfterLogin")
                    : null;
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession newSession = request.getSession(true);
            newSession.setAttribute("systemUser", user);
            if (redirectAfterLogin != null && redirectAfterLogin.startsWith(request.getContextPath() + "/")) {
                response.sendRedirect(redirectAfterLogin);
            } else {
                response.sendRedirect(request.getContextPath() + "/homepage");
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Google login failed", ex);
            response.sendRedirect(request.getContextPath() + "/login?error=google");
        }
    }

    private SystemUser resolveGoogleUser(GoogleUser googleUser) {
        DAO dao = DAO.getInstance();
        SystemUser user = dao.getAccountByGoogleId(googleUser.getGoogleId());
        if (user != null) {
            dao.recordSuccessfulLogin(user.getUserId());
            return dao.getAccountByGoogleId(googleUser.getGoogleId());
        }

        user = dao.getAccountByEmail(googleUser.getEmail());
        if (user != null) {
            if (!dao.updateGoogleAccount(user.getUserId(), googleUser.getGoogleId(), googleUser.getAvatarUrl())) {
                return null;
            }
            return dao.getAccountByGoogleId(googleUser.getGoogleId());
        }

        int guestRoleId = dao.getOrCreateRoleIdByName("Guest");
        return dao.createGoogleUser(
                googleUser.getGoogleId(),
                googleUser.getEmail(),
                googleUser.getFullName(),
                googleUser.getAvatarUrl(),
                guestRoleId);
    }

    private JSONObject exchangeCodeForToken(String code, String redirectUri) throws IOException {
        String body = "code=" + encode(code)
                + "&client_id=" + encode(Iconstant.GOOGLE_CLIENT_ID)
                + "&client_secret=" + encode(Iconstant.GOOGLE_CLIENT_SECRET)
                + "&redirect_uri=" + encode(redirectUri)
                + "&grant_type=" + encode(Iconstant.GOOGLE_GRANT_TYPE);
        return postForm(Iconstant.GOOGLE_LINK_GET_TOKEN, body);
    }

    private boolean isTokenValid(String idToken) throws IOException {
        JSONObject tokenInfo = getJson(Iconstant.GOOGLE_LINK_TOKEN_INFO + encode(idToken));
        String audience = tokenInfo.optString("aud", tokenInfo.optString("audience", ""));
        return Iconstant.GOOGLE_CLIENT_ID.equals(audience);
    }

    private JSONObject fetchGoogleProfile(String idToken, String accessToken) throws IOException {
        JSONObject tokenInfo = getJson(Iconstant.GOOGLE_LINK_TOKEN_INFO + encode(idToken));
        if (!tokenInfo.optString("email", "").isBlank()) {
            return tokenInfo;
        }
        if (!accessToken.isBlank()) {
            return getJson(Iconstant.GOOGLE_LINK_GET_USER_INFO + encode(accessToken));
        }
        return tokenInfo;
    }

    private JSONObject postForm(String url, String body) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }
        return readJsonResponse(conn);
    }

    private JSONObject getJson(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        return readJsonResponse(conn);
    }

    private JSONObject readJsonResponse(HttpURLConnection conn) throws IOException {
        int status = conn.getResponseCode();
        InputStream stream = status >= 200 && status < 300 ? conn.getInputStream() : conn.getErrorStream();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            if (status < 200 || status >= 300) {
                throw new IOException("Google OAuth request failed with HTTP " + status + ": " + response);
            }
            return new JSONObject(response.toString());
        }
    }

    private String generateState() {
        byte[] bytes = new byte[24];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String getRedirectUri(HttpServletRequest request) {
        if (!Iconstant.GOOGLE_REDIRECT_URI.isBlank()) {
            return Iconstant.GOOGLE_REDIRECT_URI;
        }
        StringBuilder baseUrl = new StringBuilder();
        baseUrl.append(request.getScheme()).append("://").append(request.getServerName());
        if ((request.getScheme().equals("http") && request.getServerPort() != 80)
                || (request.getScheme().equals("https") && request.getServerPort() != 443)) {
            baseUrl.append(":").append(request.getServerPort());
        }
        baseUrl.append(request.getContextPath()).append("/auth/google/callback");
        return baseUrl.toString();
    }
}
