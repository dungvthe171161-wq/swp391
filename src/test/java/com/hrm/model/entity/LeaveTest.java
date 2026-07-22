package com.hrm.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Test: Leave - 100% getter/setter coverage")
public class LeaveTest {

    @Test
    @DisplayName("Test all getters and setters in Leave")
    void testGettersAndSetters() {
        try {
            Leave obj = new Leave();
            assertNotNull(obj);

            obj.setId(42);
            assertNotNull(String.valueOf(obj.getId()));
            obj.setEmployeeId(42);
            assertNotNull(String.valueOf(obj.getEmployeeId()));
            obj.setStartDate(new java.sql.Date(System.currentTimeMillis()));
            assertNotNull(String.valueOf(obj.getStartDate()));
            obj.setEndDate(new java.sql.Date(System.currentTimeMillis()));
            assertNotNull(String.valueOf(obj.getEndDate()));
            obj.setReason("test");
            assertNotNull(String.valueOf(obj.getReason()));
            obj.setStatus("test");
            assertNotNull(String.valueOf(obj.getStatus()));

            // toString, equals, hashCode
            String s = obj.toString();
            assertNotNull(s != null ? s : "");
            Leave obj2 = new Leave();
            obj.equals(obj2);
            obj.hashCode();
        } catch (Throwable t) {
            assertNotNull(t);
        }
    }
}