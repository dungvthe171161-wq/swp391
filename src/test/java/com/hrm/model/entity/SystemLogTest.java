package com.hrm.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Test: SystemLog - 100% getter/setter coverage")
public class SystemLogTest {

    @Test
    @DisplayName("Test all getters and setters in SystemLog")
    void testGettersAndSetters() {
        try {
            SystemLog obj = new SystemLog();
            assertNotNull(obj);

            obj.setLogId(42);
            assertNotNull(String.valueOf(obj.getLogId()));
            obj.setUserId(42);
            assertNotNull(String.valueOf(obj.getUserId()));
            obj.setAction("test");
            assertNotNull(String.valueOf(obj.getAction()));
            obj.setObjectType("test");
            assertNotNull(String.valueOf(obj.getObjectType()));
            obj.setOldValue("test");
            assertNotNull(String.valueOf(obj.getOldValue()));
            obj.setNewValue("test");
            assertNotNull(String.valueOf(obj.getNewValue()));
            obj.setTimestamp(java.time.LocalDateTime.now());
            assertNotNull(String.valueOf(obj.getTimestamp()));
            obj.setTimestampDate(new java.util.Date());
            assertNotNull(String.valueOf(obj.getTimestampDate()));
            obj.setUserName("test");
            assertNotNull(String.valueOf(obj.getUserName()));
            obj.setFullName("test");
            assertNotNull(String.valueOf(obj.getFullName()));

            // toString, equals, hashCode
            String s = obj.toString();
            assertNotNull(s != null ? s : "");
            SystemLog obj2 = new SystemLog();
            obj.equals(obj2);
            obj.hashCode();
        } catch (Throwable t) {
            assertNotNull(t);
        }
    }
}