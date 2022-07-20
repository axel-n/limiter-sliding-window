package com.example.limiter;

import java.io.Closeable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyLimiter implements Closeable {
    private final int MAX_REQUESTS;
    private final int INTERVAL_SECONDS;
    private final Queue<Long> historyRequests = new ConcurrentLinkedQueue<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();


    public MyLimiter(int maxRequests, int intervalSeconds) {
        MAX_REQUESTS = maxRequests;
        INTERVAL_SECONDS = intervalSeconds;

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

                Long firstRequest = historyRequests.element();

                if (isOld(now, firstRequest, INTERVAL_SECONDS)) {
                    // первый запрос уже старый, можно его убрать и смотреть дальше
                    historyRequests.poll();

                    while (!historyRequests.isEmpty()) {
                        Long current = historyRequests.element();

                        if (isOld(now, current, INTERVAL_SECONDS)) {
                            historyRequests.poll();
                        } else {
                            break;
                        }
                    }

                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    private boolean isOld(Long now, Long timeRequest, int maxInterval) {
        long diffInMs = (now - timeRequest);
        return diffInMs / 1000 > maxInterval;
    }


    @Override
    public void close() {
        System.out.println("shutdown...");
        executor.shutdown();
    }
}
