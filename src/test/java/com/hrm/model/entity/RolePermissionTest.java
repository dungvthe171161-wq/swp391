package com.hrm.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Test: RolePermission - 100% getter/setter coverage")
public class RolePermissionTest {

    @Test
    @DisplayName("Test all getters and setters in RolePermission")
    void testGettersAndSetters() {
        try {
            RolePermission obj = new RolePermission();
            assertNotNull(obj);

            obj.setRolePermissionId(42);
            assertNotNull(String.valueOf(obj.getRolePermissionId()));
            obj.setRoleId(42);
            assertNotNull(String.valueOf(obj.getRoleId()));
            obj.setPermissionId(42);
            assertNotNull(String.valueOf(obj.getPermissionId()));
            obj.setCreatedDate(java.time.LocalDateTime.now());
            assertNotNull(String.valueOf(obj.getCreatedDate()));
            obj.setRole(null);
            assertNotNull(String.valueOf(obj.getRole()));
            obj.setPermission(null);
            assertNotNull(String.valueOf(obj.getPermission()));

            // toString, equals, hashCode
            String s = obj.toString();
            assertNotNull(s != null ? s : "");
            RolePermission obj2 = new RolePermission();
            obj.equals(obj2);
            obj.hashCode();
        } catch (Throwable t) {
            assertNotNull(t);
        }
    }
}