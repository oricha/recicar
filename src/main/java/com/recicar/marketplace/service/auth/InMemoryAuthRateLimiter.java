package com.recicar.marketplace.service.auth;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Simple in-memory sliding-window rate limiter by string key (e.g. client IP).
 */
@Component
public class InMemoryAuthRateLimiter {

    private final Map<String, List<Instant>> hits = new ConcurrentHashMap<>();

    public boolean allow(String key, int maxAttempts, java.time.Duration window) {
        Instant now = Instant.now();
        Instant cutoff = now.minus(window);
        List<Instant> list = hits.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>());
        synchronized (list) {
            list.removeIf(ts -> ts.isBefore(cutoff));
            if (list.size() >= maxAttempts) {
                return false;
            }
            list.add(now);
            return true;
        }
    }

    /** For tests */
    public void clear() {
        hits.clear();
    }
}
