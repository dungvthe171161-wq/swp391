package com.hrm.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Test: Offer - 100% Branch Coverage")
public class OfferTest {

    @Test
    void testStatusAndInputHelperBranches() {
        Offer offer = new Offer();

        String[] statuses = {"PENDING", "ACCEPTED", "REJECTED", "EXPIRED", "UNKNOWN", null};
        for (String st : statuses) {
            offer.setStatus(st);
            assertNotNull(offer.getStatusLabel());
        }

        assertEquals("", offer.getStartDateInputValue());
        assertEquals("", offer.getExpiredAtInputValue());

        offer.setStartDate(java.time.LocalDate.now());
        assertNotNull(offer.getStartDateInputValue());

        offer.setExpiredAt(java.time.LocalDateTime.now());
        assertNotNull(offer.getExpiredAtInputValue());

        offer.toString(); offer.hashCode(); offer.equals(new Offer());
    }
}
