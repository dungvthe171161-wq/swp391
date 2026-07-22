package com.hrm.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Test: Interview - 100% Branch Coverage")
public class InterviewTest {

    @Test
    void testStatusAndScheduledInputBranches() {
        Interview interview = new Interview();

        String[] statuses = {"SCHEDULED", "COMPLETED", "CANCELLED", "PASSED", "FAILED", "UNKNOWN", null};
        for (String st : statuses) {
            interview.setStatus(st);
            assertNotNull(interview.getStatusLabel());
        }

        assertEquals("", interview.getScheduledAtInputValue());
        interview.setScheduledAt(java.time.LocalDateTime.now());
        assertNotNull(interview.getScheduledAtInputValue());

        interview.toString(); interview.hashCode(); interview.equals(new Interview());
    }
}
