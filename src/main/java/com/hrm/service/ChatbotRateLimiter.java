package com.hrm.service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;

public class ChatbotRateLimiter {
    private static final int DEFAULT_SESSION_LIMIT = 10;
    private static final int DEFAULT_IP_LIMIT = 30;
    private static final long DEFAULT_WINDOW_MILLIS = Duration.ofMinutes(1).toMillis();

    private final int sessionLimit;
    private final int ipLimit;
    private final long windowMillis;
    private final LongSupplier clock;
    private final Map<String, Window> windows = new ConcurrentHashMap<>();
    private final AtomicLong requestCounter = new AtomicLong();

    public ChatbotRateLimiter() {
        this(DEFAULT_SESSION_LIMIT, DEFAULT_IP_LIMIT, DEFAULT_WINDOW_MILLIS, System::currentTimeMillis);
    }

    ChatbotRateLimiter(int sessionLimit, int ipLimit, long windowMillis, LongSupplier clock) {
        this.sessionLimit = sessionLimit;
        this.ipLimit = ipLimit;
        this.windowMillis = windowMillis;
        this.clock = clock;
    }

    public boolean tryAcquire(String sessionId, String remoteAddress) {
        long now = clock.getAsLong();
        String safeSessionId = sessionId == null || sessionId.isBlank() ? "unknown" : sessionId;
        String safeAddress = remoteAddress == null || remoteAddress.isBlank() ? "unknown" : remoteAddress;
        boolean sessionAllowed = acquire("session:" + safeSessionId, sessionLimit, now);
        boolean ipAllowed = acquire("ip:" + safeAddress, ipLimit, now);
        if ((requestCounter.incrementAndGet() & 255) == 0) {
            windows.entrySet().removeIf(entry -> now - entry.getValue().windowStartedAt >= windowMillis * 2);
        }
        return sessionAllowed && ipAllowed;
    }

    private boolean acquire(String key, int limit, long now) {
        boolean[] allowed = new boolean[1];
        windows.compute(key, (ignored, current) -> {
            Window window = current;
            if (window == null || now - window.windowStartedAt >= windowMillis) {
                window = new Window(now, 0);
            }
            if (window.count < limit) {
                window.count++;
                allowed[0] = true;
            }
            return window;
        });
        return allowed[0];
    }

    private static final class Window {
        private final long windowStartedAt;
        private int count;

        private Window(long windowStartedAt, int count) {
            this.windowStartedAt = windowStartedAt;
            this.count = count;
        }
    }
}
