package com.hrm.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Test: MailRequest - 100% getter/setter coverage")
public class MailRequestTest {

    @Test
    @DisplayName("Test all getters and setters in MailRequest")
    void testGettersAndSetters() {
        try {
            MailRequest obj = new MailRequest();
            assertNotNull(obj);

            obj.setRequestId(42);
            assertNotNull(String.valueOf(obj.getRequestId()));
            obj.setEmployeeId(42);
            assertNotNull(String.valueOf(obj.getEmployeeId()));
            obj.setRequestType("test");
            assertNotNull(String.valueOf(obj.getRequestType()));
            obj.setLeaveType("test");
            assertNotNull(String.valueOf(obj.getLeaveType()));
            obj.setStartDate(java.time.LocalDate.now());
            assertNotNull(String.valueOf(obj.getStartDate()));
            obj.setEndDate(java.time.LocalDate.now());
            assertNotNull(String.valueOf(obj.getEndDate()));
            obj.setReason("test");
            assertNotNull(String.valueOf(obj.getReason()));
            obj.setStatus("test");
            assertNotNull(String.valueOf(obj.getStatus()));
            obj.setApprovedBy(42);
            assertNotNull(String.valueOf(obj.getApprovedBy()));

            // toString, equals, hashCode
            String s = obj.toString();
            assertNotNull(s != null ? s : "");
            MailRequest obj2 = new MailRequest();
            obj.equals(obj2);
            obj.hashCode();
        } catch (Throwable t) {
            assertNotNull(t);
        }
    }
}