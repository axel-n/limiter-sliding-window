package io.github.axel_n.limiter.config;

import java.time.Duration;

public class LimiterConfigBuilder {
    private Duration interval;
    private int maxRequestsInInterval;
    private Duration intervalForCheckExecution;
    private Duration maxAwaitExecutionTime;

    private final Duration DEFAULT_INTERVAL_FOR_CHECK_EXECUTION = Duration.ofMillis(100);
    private final Duration DEFAULT_MAX_AWAIT_EXECUTION_TIME = Duration.ofSeconds(30);

    public LimiterConfigBuilder setInterval(Duration interval) {
        this.interval = interval;
        return this;
    }

    public LimiterConfigBuilder setMaxRequestsInInterval(int maxRequestsInInterval) {
        this.maxRequestsInInterval = maxRequestsInInterval;
        return this;
    }

    public LimiterConfigBuilder setMaxAwaitExecutionTime(Duration maxAwaitExecutionTime) {
        this.maxAwaitExecutionTime = maxAwaitExecutionTime;
        return this;
    }

    public LimiterConfigBuilder setIntervalForCheckExecution(Duration intervalForCheckExecution) {
        this.intervalForCheckExecution = intervalForCheckExecution;
        return this;
    }

    public LimiterConfig build() {
        if (intervalForCheckExecution == null) {
            intervalForCheckExecution = DEFAULT_INTERVAL_FOR_CHECK_EXECUTION;
        }

        if (maxAwaitExecutionTime == null) {
            maxAwaitExecutionTime = DEFAULT_MAX_AWAIT_EXECUTION_TIME;
        }

        return new LimiterConfig(interval, maxRequestsInInterval, intervalForCheckExecution, maxAwaitExecutionTime);
    }



}