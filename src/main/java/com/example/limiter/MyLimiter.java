package com.example.limiter;

import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PreDestroy;

public class MyLimiter implements Cloneable {
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

                    while(!historyRequests.isEmpty()) {
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
                Thread.sleep(60);
            } catch (InterruptedException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    private boolean isOld(Long now, Long timeRequest, int maxInterval) {
       return (now - timeRequest) / 1000 > maxInterval;
    }


    @PreDestroy
    private void shutdown() {
        System.out.println("shutdown...");
        executor.shutdown();
    }

}
