package com.example.limiter;

import java.util.Date;

public class TestExternalService {
    private final StatisticService statisticService;

    public TestExternalService(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    public void sendFakeRequest() {
        long timeReceivedRequest = System.currentTimeMillis();
        System.out.println(new Date(timeReceivedRequest) + " receive request");

        statisticService.saveTimeRequest(timeReceivedRequest);
    }
}