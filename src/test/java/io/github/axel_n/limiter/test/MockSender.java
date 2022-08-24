package io.github.axel_n.limiter.test;

public class MockSender {
    private final StatisticService statisticService;

    public MockSender(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    public void sendFakeRequest() {
        System.out.println("thread=" + Thread.currentThread().getName() + ". send new request");

        long timeReceivedRequest = System.currentTimeMillis();

        statisticService.saveTimeRequest(timeReceivedRequest);
    }
}