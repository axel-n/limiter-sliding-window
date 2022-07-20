package com.example.limiter;

public class TestExternalService {
    private final StatisticService statisticService;

    public TestExternalService(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    public void sendFakeRequest() {
        long timeReceivedRequest = System.currentTimeMillis();

        statisticService.saveTimeRequest(timeReceivedRequest);
    }
}