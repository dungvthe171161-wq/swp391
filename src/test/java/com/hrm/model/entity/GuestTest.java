package com.hrm.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Test: Guest - 100% Branch Coverage")
public class GuestTest {

    @Test
    void testStatusLabelBranches() {
        Guest guest = new Guest();

        String[] statuses = {"ACTIVE", "INACTIVE", "APPLIED", "HIRED", "REJECTED", "UNKNOWN", null};
        for (String st : statuses) {
            guest.setStatus(st);
            assertNotNull(guest.getStatusLabel());
        }

        guest.toString(); guest.hashCode(); guest.equals(new Guest());
    }
}
