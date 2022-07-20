package com.example.limiter;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StatisticService {
    private final static ExecutorService executorService = Executors.newFixedThreadPool(1);
    private final static Queue<Long> timeReceivedRequests = new ConcurrentLinkedQueue();

    public void saveTimeRequest(long timeRequest) {
        executorService.execute(() -> timeReceivedRequests.add(timeRequest));
    }

    public int getCountCountReceivedRequests() {
        return timeReceivedRequests.size();
    }

    public Map<Long, Integer> getCountRequestsGroupedByTime() {
        Map<Long, Integer> requestsBySeconds = new HashMap<>();
        for (Long timeReceivedRequest : timeReceivedRequests) {
            long timeInSeconds = timeReceivedRequest / 1000;
            int previousCountByTime = requestsBySeconds.getOrDefault(timeInSeconds, 0);
            previousCountByTime++;
            requestsBySeconds.put(timeInSeconds, previousCountByTime);
        }

        return requestsBySeconds;
    }

    public int getMaxRequestsInSeconds() {
        return getCountRequestsGroupedByTime().values().stream().max(Integer::compareTo).orElse(0);
    }

    public void cleanHistory() {
        int size = timeReceivedRequests.size();

        for (int i = 0; i < size; i++) {
            timeReceivedRequests.poll();
        }
    }
}
