package com.example.limiter;

import org.junit.jupiter.api.Test;

public class MyLimiterTest {
    private final MyLimiter limiter = new MyLimiter(1, 1); // 10 request per 5 seconds
    private final StatisticService statisticService = new StatisticService();
    private final TestExternalService externalService = new TestExternalService(statisticService);
    private final TestProducer producer = new TestProducer(limiter, externalService);

    @Test
    public void test1() {
        while (statisticService.getCountCountReceivedRequests() != 50) {
            if (limiter.isPossibleSendRequest()) {
                producer.sendFakeRequest();
            }
        }
    }

}
