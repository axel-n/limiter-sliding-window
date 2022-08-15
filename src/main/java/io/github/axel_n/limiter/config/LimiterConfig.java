package io.github.axel_n.limiter.config;

import java.time.Duration;

public class LimiterConfig {

    private final Duration interval;
    private final int maxRequestsInInterval;
    private final Duration intervalForCheckExecution;
    private final Duration maxAwaitExecutionTime;

    public LimiterConfig(Duration interval, int maxRequestsInInterval, Duration intervalForCheckExecution, Duration maxAwaitExecutionTime) {
        this.interval = interval;
        this.maxRequestsInInterval = maxRequestsInInterval;
        this.intervalForCheckExecution = intervalForCheckExecution;
        this.maxAwaitExecutionTime = maxAwaitExecutionTime;
    }

    public Duration getInterval() {
        return interval;
    }

    public int getMaxRequestsInInterval() {
        return maxRequestsInInterval;
    }

    public Duration getMaxAwaitExecutionTime() {
        return maxAwaitExecutionTime;
    }

    public Duration getIntervalForCheckExecution() {
        return intervalForCheckExecution;
    }
}
