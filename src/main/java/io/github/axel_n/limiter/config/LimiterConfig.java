package io.github.axel_n.limiter.config;

import java.time.Duration;

public class LimiterConfig {

    private final Duration interval;
    private final int maxRequestsInInterval;

    LimiterConfig(Duration interval, int maxRequestsInInterval) {
        this.interval = interval;
        this.maxRequestsInInterval = maxRequestsInInterval;
    }

    public Duration getInterval() {
        return interval;
    }

    public int getMaxRequestsInInterval() {
        return maxRequestsInInterval;
    }
}
