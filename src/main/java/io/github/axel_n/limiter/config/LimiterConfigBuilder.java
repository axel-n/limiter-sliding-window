package io.github.axel_n.limiter.config;

import java.time.Duration;

public class LimiterConfigBuilder {
    private Duration interval;
    private int maxRequestsInInterval;

    public LimiterConfigBuilder setInterval(Duration interval) {
        this.interval = interval;
        return this;
    }

    public LimiterConfigBuilder setMaxRequestsInInterval(int maxRequestsInInterval) {
        this.maxRequestsInInterval = maxRequestsInInterval;
        return this;
    }

    public LimiterConfig build() {
        return new LimiterConfig(interval, maxRequestsInInterval);
    }
}