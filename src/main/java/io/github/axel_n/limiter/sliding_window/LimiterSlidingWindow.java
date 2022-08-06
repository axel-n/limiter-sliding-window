package io.github.axel_n.limiter.sliding_window;

import io.github.axel_n.limiter.Limiter;
import io.github.axel_n.limiter.config.LimiterConfig;
import java.io.Closeable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LimiterSlidingWindow implements Limiter, Closeable {
    private final int maxRequests;
    private final int intervalInMilliseconds;
    private final Queue<Long> historyRequests = new ConcurrentLinkedQueue<>();
    private final ExecutorService cleanHistoryExecutor = Executors.newSingleThreadExecutor();

    public LimiterSlidingWindow(LimiterConfig config) {
        this.maxRequests = config.getMaxRequestsInInterval();
        this.intervalInMilliseconds = (int) config.getInterval().toMillis();

        cleanHistoryExecutor.execute(this::cleanHistory);
    }

    @Override
    public boolean isPossibleSendRequest() {
        return historyRequests.size() < maxRequests;
    }

    @Override
    public void writeHistory() {
        historyRequests.add(System.currentTimeMillis());
    }

    private void cleanHistory() {
        while (!cleanHistoryExecutor.isShutdown()) {
            if (!historyRequests.isEmpty()) {
                long now = System.currentTimeMillis();

                for (long current : historyRequests) {
                    if (isOld(now, current, intervalInMilliseconds)) {
                        historyRequests.poll();
                    } else {
                        break;
                    }
                }
            }
        }
    }

    private boolean isOld(long now, long timeRequest, int maxInterval) {
        return (now - timeRequest) > maxInterval;
    }


    @Override
    public void close() {
        cleanHistoryExecutor.shutdown();
    }
}
