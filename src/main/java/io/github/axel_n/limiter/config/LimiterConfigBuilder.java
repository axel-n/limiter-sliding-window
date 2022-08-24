package io.github.axel_n.limiter.config;

import java.util.concurrent.TimeUnit;

public class LimiterConfigBuilder {
    private long sizeWindowInMilliseconds;
    private int maxRequestsInWindow;
    private long periodForCheckExecutionInMilliseconds;
    private long maxAwaitExecutionTimeInMilliseconds;
    private String instanceName;

    public LimiterConfigBuilder setSizeWindow(int sizeWindow, TimeUnit interval) {
        this.sizeWindowInMilliseconds = interval.toMillis(sizeWindow);
        return this;
    }

    public LimiterConfigBuilder setMaxRequestsInWindow(int maxRequestsInWindow) {
        this.maxRequestsInWindow = maxRequestsInWindow;
        return this;
    }

    public LimiterConfigBuilder setMaxAwaitExecutionTime(int maxAwaitExecutionTime, TimeUnit interval) {
        this.maxAwaitExecutionTimeInMilliseconds = interval.toMillis(maxAwaitExecutionTime);
        return this;
    }

    public LimiterConfigBuilder setPeriodForCheckExecution(int intervalForCheckExecution, TimeUnit interval) {
        this.periodForCheckExecutionInMilliseconds = interval.toMillis(intervalForCheckExecution);
        return this;
    }

    public LimiterConfigBuilder setInstanceName(String instanceName) {
        this.instanceName = instanceName;
        return this;
    }

    public LimiterConfig build() {
        if (periodForCheckExecutionInMilliseconds <= 0) {
            periodForCheckExecutionInMilliseconds = TimeUnit.MILLISECONDS.toMillis(100);
        }

        if (maxAwaitExecutionTimeInMilliseconds <= 0) {
            maxAwaitExecutionTimeInMilliseconds = TimeUnit.SECONDS.toMillis(30);
        }

        if (instanceName == null) {
            instanceName = "common";
        }

        return new LimiterConfig(
                sizeWindowInMilliseconds,
                maxRequestsInWindow,
                periodForCheckExecutionInMilliseconds,
                maxAwaitExecutionTimeInMilliseconds,
                instanceName
        );
    }



}