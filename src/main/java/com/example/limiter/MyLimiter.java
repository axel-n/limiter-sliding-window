package com.example.limiter;

import java.io.Closeable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyLimiter implements Closeable {
    private final int MAX_REQUESTS;
    private final int INTERVAL_MS;
    private final Queue<Long> historyRequests = new ConcurrentLinkedQueue<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();


    public MyLimiter(int maxRequests, int intervalSeconds) {
        MAX_REQUESTS = maxRequests;
        INTERVAL_MS = intervalSeconds * 1000;

        executor.execute(this::cleanHistory);
    }

    public boolean isPossibleSendRequest() {
        return historyRequests.size() < MAX_REQUESTS;
    }

    public void writeHistory() {
        historyRequests.add(System.currentTimeMillis());
    }

    private void cleanHistory() {
        while (!executor.isShutdown()) {
            if (!historyRequests.isEmpty()) {
                Long now = System.currentTimeMillis();

                for (Long current : historyRequests) {
                    if (isOld(now, current, INTERVAL_MS)) {
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
        executor.shutdown();
    }
}
