package io.github.axel_n.limiter.config;

public class LimiterConfig {

    private final long sizeWindowInMilliseconds;
    private final int maxRequestsInWindow;
    private final long periodForCheckExecutionInMilliseconds;
    private final long maxAwaitExecutionTimeInMilliseconds;
    private final String instanceName;

    public LimiterConfig(long sizeWindowInMilliseconds,
                         int maxRequestsInWindow,
                         long periodForCheckExecutionInMilliseconds,
                         long maxAwaitExecutionTimeInMilliseconds,
                         String instanceName
    ) {
        this.sizeWindowInMilliseconds = sizeWindowInMilliseconds;
        this.maxRequestsInWindow = maxRequestsInWindow;
        this.periodForCheckExecutionInMilliseconds = periodForCheckExecutionInMilliseconds;
        this.maxAwaitExecutionTimeInMilliseconds = maxAwaitExecutionTimeInMilliseconds;
        this.instanceName = instanceName;
    }

    public long getSizeWindowInMilliseconds() {
        return sizeWindowInMilliseconds;
    }

    public int getMaxRequestsInWindow() {
        return maxRequestsInWindow;
    }

    public long getPeriodForCheckExecutionInMilliseconds() {
        return periodForCheckExecutionInMilliseconds;
    }

    public long getMaxAwaitExecutionTimeInMilliseconds() {
        return maxAwaitExecutionTimeInMilliseconds;
    }

    public String getInstanceName() {
        return instanceName;
    }
}
