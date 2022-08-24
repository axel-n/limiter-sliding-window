package io.github.axel_n.limiter.config;

import java.time.Duration;

public class LimiterConfig {

    private final Duration interval;
    private final int maxRequestsInInterval;
    private final Duration intervalForCheckExecution;
    private final Duration maxAwaitExecutionTime;
    private final String instanceName;

    public LimiterConfig(Duration interval, int maxRequestsInInterval, Duration intervalForCheckExecution, Duration maxAwaitExecutionTime, String instanceName) {
        this.interval = interval;
        this.maxRequestsInInterval = maxRequestsInInterval;
        this.intervalForCheckExecution = intervalForCheckExecution;
        this.maxAwaitExecutionTime = maxAwaitExecutionTime;
        this.instanceName = instanceName;
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

    public String getInstanceName() {
        return instanceName;
    }
}
