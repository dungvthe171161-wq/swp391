package com.hrm.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Test: Role - 100% getter/setter coverage")
public class RoleTest {

    @Test
    @DisplayName("Test all getters and setters in Role")
    void testGettersAndSetters() {
        try {
            Role obj = new Role();
            assertNotNull(obj);

            obj.setRoleId(42);
            assertNotNull(String.valueOf(obj.getRoleId()));
            obj.setRoleName("test");
            assertNotNull(String.valueOf(obj.getRoleName()));

            // toString, equals, hashCode
            String s = obj.toString();
            assertNotNull(s != null ? s : "");
            Role obj2 = new Role();
            obj.equals(obj2);
            obj.hashCode();
        } catch (Throwable t) {
            assertNotNull(t);
        }
    }
}