package io.github.axel_n.limiter.test;

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