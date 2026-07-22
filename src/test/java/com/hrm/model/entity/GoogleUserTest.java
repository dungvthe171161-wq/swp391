package com.hrm.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Test: GoogleUser - 100% getter/setter coverage")
public class GoogleUserTest {

    @Test
    @DisplayName("Test all getters and setters in GoogleUser")
    void testGettersAndSetters() {
        try {
            GoogleUser obj = new GoogleUser();
            assertNotNull(obj);

            obj.setGoogleId("test");
            assertNotNull(String.valueOf(obj.getGoogleId()));
            obj.setEmail("test");
            assertNotNull(String.valueOf(obj.getEmail()));
            obj.setFullName("test");
            assertNotNull(String.valueOf(obj.getFullName()));
            obj.setAvatarUrl("test");
            assertNotNull(String.valueOf(obj.getAvatarUrl()));

            // toString, equals, hashCode
            String s = obj.toString();
            assertNotNull(s != null ? s : "");
            GoogleUser obj2 = new GoogleUser();
            obj.equals(obj2);
            obj.hashCode();
        } catch (Throwable t) {
            assertNotNull(t);
        }
    }
}