package com.example.limiter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MyLimiterTest {
    private final StatisticService statisticService = new StatisticService();
    private final TestExternalService externalService = new TestExternalService(statisticService);

    @BeforeEach
    public void cleanHistory() {
        statisticService.cleanHistory();
    }

    @Test
    public void test5RequestsPerSecond() {
        try(MyLimiter limiter = new MyLimiter(5, 1)) {
            TestProducer producer = new TestProducer(limiter, externalService);

            while (statisticService.getCountCountReceivedRequests() != 50) {
                if (limiter.isPossibleSendRequest()) {
                    producer.sendFakeRequest();
                }
            }
        }

        // TODO add asserts
    }

    @Test
    public void test10RequestsPerSecond() {
        try(MyLimiter limiter = new MyLimiter(10, 1)) {
            TestProducer producer = new TestProducer(limiter, externalService);

            while (statisticService.getCountCountReceivedRequests() != 100) {
                if (limiter.isPossibleSendRequest()) {
                    producer.sendFakeRequest();
                }
            }
        }

        // TODO add asserts
    }

    @Test
    public void test3RequestsPer3Second() {
        try(MyLimiter limiter = new MyLimiter(3, 3)) {
            TestProducer producer = new TestProducer(limiter, externalService);

            while (statisticService.getCountCountReceivedRequests() != 100) {
                if (limiter.isPossibleSendRequest()) {
                    producer.sendFakeRequest();
                }
            }
        }

        // TODO add asserts
    }

}
