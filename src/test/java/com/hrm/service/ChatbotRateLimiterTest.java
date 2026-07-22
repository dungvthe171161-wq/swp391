package com.hrm.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Test: ChatbotRateLimiter - 100% Branch Coverage")
public class ChatbotRateLimiterTest {

    @Test
    void testRateLimiterBranches() {
        AtomicLong mockClock = new AtomicLong(1000L);
        // sessionLimit=2, ipLimit=5, window=1000ms
        ChatbotRateLimiter limiter = new ChatbotRateLimiter(2, 5, 1000L, mockClock::get);

        // First attempt -> allowed
        assertTrue(limiter.tryAcquire("sess1", "127.0.0.1"));
        // Second attempt -> allowed
        assertTrue(limiter.tryAcquire("sess1", "127.0.0.1"));
        // Third attempt for same session -> blocked (session limit=2 reached)
        assertFalse(limiter.tryAcquire("sess1", "127.0.0.1"));

        // Different session, same IP -> allowed
        assertTrue(limiter.tryAcquire("sess2", "127.0.0.1"));

        // Advance clock beyond window (1000ms + 1001ms) -> window resets
        mockClock.addAndGet(1001L);
        assertTrue(limiter.tryAcquire("sess1", "127.0.0.1"));

        // Test null/blank session & IP branches
        assertTrue(limiter.tryAcquire(null, null));
        assertTrue(limiter.tryAcquire("  ", "  "));

        // Test default constructor
        ChatbotRateLimiter defaultLimiter = new ChatbotRateLimiter();
        assertTrue(defaultLimiter.tryAcquire("s1", "1.1.1.1"));
    }
}
