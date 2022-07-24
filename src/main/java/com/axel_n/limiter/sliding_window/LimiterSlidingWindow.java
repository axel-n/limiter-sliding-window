package com.axel_n.limiter.sliding_window;

import com.axel_n.limiter.Limiter;
import java.io.Closeable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LimiterSlidingWindow implements Limiter, Closeable {
    private final int maxRequests;
    private final int intervalSeconds;
    private final Queue<Long> historyRequests = new ConcurrentLinkedQueue<>();
    private final ExecutorService cleanHistoryExecutor = Executors.newSingleThreadExecutor();

    public LimiterSlidingWindow(int maxRequests, int intervalSeconds) {
        this.maxRequests = maxRequests;
        this.intervalSeconds = intervalSeconds * 1000;

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
                Long now = System.currentTimeMillis();

                for (Long current : historyRequests) {
                    if (isOld(now, current, intervalSeconds)) {
                        historyRequests.poll();
                    } else {
                        break;
                    }
                }
            }
        }
    }

    private boolean isOld(Long now, Long timeRequest, int maxInterval) {
        return (now - timeRequest) > maxInterval;
    }


    @Override
    public void close() {
        cleanHistoryExecutor.shutdown();
    }
}
