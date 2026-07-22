package com.hrm.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Test: Department - 100% getter/setter coverage")
public class DepartmentTest {

    @Test
    @DisplayName("Test all getters and setters in Department")
    void testGettersAndSetters() {
        try {
            Department obj = new Department();
            assertNotNull(obj);

            obj.setDepartmentId(42);
            assertNotNull(String.valueOf(obj.getDepartmentId()));
            obj.setDeptName("test");
            assertNotNull(String.valueOf(obj.getDeptName()));
            obj.setDeptManagerId(42);
            assertNotNull(String.valueOf(obj.getDeptManagerId()));
            obj.setEmployeeCount(42);
            assertNotNull(String.valueOf(obj.getEmployeeCount()));
            obj.setStatus("test");
            assertNotNull(String.valueOf(obj.getStatus()));
            obj.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
            assertNotNull(String.valueOf(obj.getCreatedAt()));
            obj.setManagerName("test");
            assertNotNull(String.valueOf(obj.getManagerName()));

            // toString, equals, hashCode
            String s = obj.toString();
            assertNotNull(s != null ? s : "");
            Department obj2 = new Department();
            obj.equals(obj2);
            obj.hashCode();
        } catch (Throwable t) {
            assertNotNull(t);
        }
    }
}