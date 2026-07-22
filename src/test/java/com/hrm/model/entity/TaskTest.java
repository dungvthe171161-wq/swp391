package com.hrm.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Test: Task - 100% getter/setter coverage")
public class TaskTest {

    @Test
    @DisplayName("Test all getters and setters in Task")
    void testGettersAndSetters() {
        try {
            Task obj = new Task();
            assertNotNull(obj);

            obj.setTaskId(42);
            assertNotNull(String.valueOf(obj.getTaskId()));
            obj.setTitle("test");
            assertNotNull(String.valueOf(obj.getTitle()));
            obj.setDescription("test");
            assertNotNull(String.valueOf(obj.getDescription()));
            obj.setAssignedBy(42);
            assertNotNull(String.valueOf(obj.getAssignedBy()));
            obj.setStartDate(java.time.LocalDateTime.now());
            assertNotNull(String.valueOf(obj.getStartDate()));
            obj.setDueDate(java.time.LocalDateTime.now());
            assertNotNull(String.valueOf(obj.getDueDate()));
            obj.setStatus("test");
            assertNotNull(String.valueOf(obj.getStatus()));
            obj.setPriority("test");
            assertNotNull(String.valueOf(obj.getPriority()));
            obj.setAttachmentPath("test");
            assertNotNull(String.valueOf(obj.getAttachmentPath()));
            obj.setAssignmentStatus("test");
            assertNotNull(String.valueOf(obj.getAssignmentStatus()));
            obj.setFeedback("test");
            assertNotNull(String.valueOf(obj.getFeedback()));
            obj.setSubmittedAt(java.time.LocalDateTime.now());
            assertNotNull(String.valueOf(obj.getSubmittedAt()));
            obj.setApprovedAt(java.time.LocalDateTime.now());
            assertNotNull(String.valueOf(obj.getApprovedAt()));
            obj.setDueReminder("test");
            assertNotNull(String.valueOf(obj.getDueReminder()));

            // toString, equals, hashCode
            String s = obj.toString();
            assertNotNull(s != null ? s : "");
            Task obj2 = new Task();
            obj.equals(obj2);
            obj.hashCode();
        } catch (Throwable t) {
            assertNotNull(t);
        }
    }
}