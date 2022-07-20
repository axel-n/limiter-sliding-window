package com.example.limiter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class StatisticService {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private final static Queue<Long> timeReceivedRequests = new ConcurrentLinkedQueue();

    public void saveTimeRequest(long timeRequest) {
        timeReceivedRequests.add(timeRequest);
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

    public List<Long> getListReceivedTime() {
        return new ArrayList<>(timeReceivedRequests);
    }

    public List<String> getHumanReadableStatistics() {
        List<Long> timestamps = getListReceivedTime();
        List<String> datetimeList = new ArrayList<>(timestamps.size());
        for (Long timestamp : timestamps) {
            datetimeList.add(dateFormat.format(timestamp));
        }

        return datetimeList;
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
