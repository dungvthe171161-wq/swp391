package com.hrm.service;

import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatbotRateLimiterTest {
    @Test
    void limitsEachSessionWithinWindow() {
        AtomicLong now = new AtomicLong(1_000L);
        ChatbotRateLimiter limiter = new ChatbotRateLimiter(2, 10, 60_000L, now::get);

        assertTrue(limiter.tryAcquire("session-a", "127.0.0.1"));
        assertTrue(limiter.tryAcquire("session-a", "127.0.0.1"));
        assertFalse(limiter.tryAcquire("session-a", "127.0.0.1"));
        assertTrue(limiter.tryAcquire("session-b", "127.0.0.1"));
    }

    @Test
    void limitsSharedIpAcrossSessions() {
        AtomicLong now = new AtomicLong(1_000L);
        ChatbotRateLimiter limiter = new ChatbotRateLimiter(10, 2, 60_000L, now::get);

        assertTrue(limiter.tryAcquire("session-a", "127.0.0.1"));
        assertTrue(limiter.tryAcquire("session-b", "127.0.0.1"));
        assertFalse(limiter.tryAcquire("session-c", "127.0.0.1"));
    }

    @Test
    void resetsAfterWindowExpires() {
        AtomicLong now = new AtomicLong(1_000L);
        ChatbotRateLimiter limiter = new ChatbotRateLimiter(1, 1, 60_000L, now::get);

        assertTrue(limiter.tryAcquire("session-a", "127.0.0.1"));
        assertFalse(limiter.tryAcquire("session-a", "127.0.0.1"));
        now.addAndGet(60_000L);
        assertTrue(limiter.tryAcquire("session-a", "127.0.0.1"));
    }
}
