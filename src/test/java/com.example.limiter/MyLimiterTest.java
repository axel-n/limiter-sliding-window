package com.example.limiter;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MyLimiterTest {
    private final StatisticService statisticService = new StatisticService();
    private final TestExternalService externalService = new TestExternalService(statisticService);

    @BeforeEach
    public void cleanHistory() {
        statisticService.cleanHistory();
    }

    @Test
    public void test5RequestsPerSecond() {
        int maxRequests = 5;
        try (MyLimiter limiter = new MyLimiter(maxRequests, 1)) {
            TestProducer producer = new TestProducer(limiter, externalService);

            while (statisticService.getCountCountReceivedRequests() != 25) {
                if (limiter.isPossibleSendRequest()) {
                    producer.sendFakeRequest();
                }
            }
        }

        int receivedMaxRequestsInSeconds = statisticService.getMaxRequestsInSeconds();
        assertEquals(maxRequests, receivedMaxRequestsInSeconds);
    }

    @Test
    public void test10RequestsPerSecond() {
        int maxRequests = 10;
        try (MyLimiter limiter = new MyLimiter(maxRequests, 1)) {
            TestProducer producer = new TestProducer(limiter, externalService);

            while (statisticService.getCountCountReceivedRequests() != 100) {
                if (limiter.isPossibleSendRequest()) {
                    producer.sendFakeRequest();
                }
            }
        }

        int receivedMaxRequestsInSeconds = statisticService.getMaxRequestsInSeconds();
        assertEquals(maxRequests, receivedMaxRequestsInSeconds);
    }

    @Test
    public void test3RequestsPer3Second() {
        int maxRequests = 3;
        try (MyLimiter limiter = new MyLimiter(3, 3)) {
            TestProducer producer = new TestProducer(limiter, externalService);

            while (statisticService.getCountCountReceivedRequests() != 30) {
                if (limiter.isPossibleSendRequest()) {
                    producer.sendFakeRequest();
                }
            }
        }

        List<String> datetimeList = statisticService.getHumanReadableStatistics();
        for (String datetime : datetimeList) {
            System.out.println("datetime received request " + datetime);
        }

        int receivedMaxRequestsInSeconds = statisticService.getMaxRequestsInSeconds();
        assertTrue(receivedMaxRequestsInSeconds >= 1);
        assertTrue(receivedMaxRequestsInSeconds <= maxRequests);
    }

}
