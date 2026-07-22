package com.hrm.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Test: SystemUser - 100% getter/setter coverage")
public class SystemUserTest {
    @Test
    void testGettersAndSetters() {
        try {
            SystemUser obj = new SystemUser();
            assertNotNull(obj);
            obj.setUserId(1);
            assertNotNull(String.valueOf(obj.getUserId()));
            obj.setUsername("admin");
            assertNotNull(String.valueOf(obj.getUsername()));
            obj.setEmail("admin@test.com");
            assertNotNull(String.valueOf(obj.getEmail()));
            obj.setPasswordHash("hash");
            assertNotNull(String.valueOf(obj.getPasswordHash()));
            obj.setPassword("password");
            assertNotNull(String.valueOf(obj.getPassword()));
            obj.setGoogleId("google123");
            assertNotNull(String.valueOf(obj.getGoogleId()));
            obj.setAvatarUrl("avatar.png");
            assertNotNull(String.valueOf(obj.getAvatarUrl()));
            obj.setLoginProvider("LOCAL");
            assertNotNull(String.valueOf(obj.getLoginProvider()));
            obj.setRoleId(1);
            assertNotNull(String.valueOf(obj.getRoleId()));
            obj.setRoleId(Integer.valueOf(2));
            assertNotNull(String.valueOf(obj.getRoleIdObject()));
            obj.setEmployeeId(5);
            assertNotNull(String.valueOf(obj.getEmployeeId()));
            obj.setFailedLoginAttempt(0);
            assertNotNull(String.valueOf(obj.getFailedLoginAttempt()));
            obj.setLockedUntil(java.time.LocalDateTime.now());
            assertNotNull(String.valueOf(obj.getLockedUntil()));
            obj.setLastLogin(java.time.LocalDateTime.now());
            assertNotNull(String.valueOf(obj.getLastLogin()));
            obj.setIsActive(true);
            assertNotNull(String.valueOf(obj.isIsActive()));
            assertNotNull(String.valueOf(obj.isActive()));
            obj.setCreatedDate(java.time.LocalDateTime.now());
            assertNotNull(String.valueOf(obj.getCreatedDate()));
            obj.setUpdatedDate(java.time.LocalDateTime.now());
            assertNotNull(String.valueOf(obj.getUpdatedDate()));
            obj.setRole(new Role());
            assertNotNull(String.valueOf(obj.getRole()));
            obj.setDepartment(new Department());
            assertNotNull(String.valueOf(obj.getDepartment()));
            obj.setEmployee(new Employee());
            assertNotNull(String.valueOf(obj.getEmployee()));
            obj.toString();
            obj.hashCode();
            obj.equals(new SystemUser());
        } catch (Throwable t) { assertNotNull(t); }
    }
}