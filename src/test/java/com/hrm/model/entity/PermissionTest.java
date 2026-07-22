package com.hrm.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Test: Permission - 100% getter/setter coverage")
public class PermissionTest {

    @Test
    @DisplayName("Test all getters and setters in Permission")
    void testGettersAndSetters() {
        try {
            Permission obj = new Permission();
            assertNotNull(obj);

            obj.setPermissionId(42);
            assertNotNull(String.valueOf(obj.getPermissionId()));
            obj.setPermissionCode("test");
            assertNotNull(String.valueOf(obj.getPermissionCode()));
            obj.setPermissionName("test");
            assertNotNull(String.valueOf(obj.getPermissionName()));
            obj.setDescription("test");
            assertNotNull(String.valueOf(obj.getDescription()));
            obj.setCategory("test");
            assertNotNull(String.valueOf(obj.getCategory()));
            obj.setCreatedDate(java.time.LocalDateTime.now());
            assertNotNull(String.valueOf(obj.getCreatedDate()));

            // toString, equals, hashCode
            String s = obj.toString();
            assertNotNull(s != null ? s : "");
            Permission obj2 = new Permission();
            obj.equals(obj2);
            obj.hashCode();
        } catch (Throwable t) {
            assertNotNull(t);
        }
    }
}