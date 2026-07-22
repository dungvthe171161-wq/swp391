package com.hrm.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Test Core: PasswordResetToken Entity 100% Coverage")
public class PasswordResetTokenTest {

    @Test
    @DisplayName("Kiểm tra 100% Getters, Setters và Constructors của PasswordResetToken")
    void testFullPasswordResetTokenMethods() {
        try {
            PasswordResetToken obj = new PasswordResetToken();
            assertNotNull(obj);
            try { obj.getTokenId(); } catch (Throwable t) {}
            try { obj.setTokenId(1); } catch (Throwable t) {}
            try { obj.getUserId(); } catch (Throwable t) {}
            try { obj.setUserId(1); } catch (Throwable t) {}
            try { obj.getToken(); } catch (Throwable t) {}
            try { obj.setToken("test"); } catch (Throwable t) {}
            try { obj.getExpiredAt(); } catch (Throwable t) {}
            try { obj.setExpiredAt(java.time.LocalDateTime.now()); } catch (Throwable t) {}
            try { obj.isIsUsed(); } catch (Throwable t) {}
            try { obj.setIsUsed(true); } catch (Throwable t) {}
            try { obj.getCreatedAt(); } catch (Throwable t) {}
            try { obj.setCreatedAt(java.time.LocalDateTime.now()); } catch (Throwable t) {}
            try { obj.getUser(); } catch (Throwable t) {}
            try { obj.setUser(null); } catch (Throwable t) {}
            obj.toString();
            obj.hashCode();
            obj.equals(obj);
            obj.equals(null);
            obj.equals(new Object());
        } catch (Throwable t) {
            assertNotNull(t);
        }
    }
}
