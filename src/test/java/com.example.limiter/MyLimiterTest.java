package com.example.limiter;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        int maxRequestsPerPeriod = 5;
        int allRequests = 25;
        long timeForeTest = System.currentTimeMillis();

        try (MyLimiter limiter = new MyLimiter(maxRequestsPerPeriod, 1)) {
            TestProducer producer = new TestProducer(limiter, externalService);

            while (statisticService.getCountCountReceivedRequests() != allRequests) {
                if (limiter.isPossibleSendRequest()) {
                    producer.sendFakeRequest();
                }
            }
        }

        long timeAfterTest = System.currentTimeMillis();

        int receivedMaxRequestsInSeconds = statisticService.getMaxRequestsInSeconds();
        assertEquals(maxRequestsPerPeriod, receivedMaxRequestsInSeconds);

        BigDecimal executionTimeSeconds = BigDecimal.valueOf(timeAfterTest - timeForeTest).divide(BigDecimal.valueOf(1000), 4, RoundingMode.HALF_UP);
        BigDecimal approximatedExecutionTime = BigDecimal.valueOf(4); // TODO how to calculate it?
        assertTrue(isGreaterOrEquals(executionTimeSeconds, approximatedExecutionTime));

        BigDecimal maxFloorForExecutionTime = calculateMaxFloorExecutionTime(executionTimeSeconds);
        assertTrue(isLowerOrEquals(executionTimeSeconds, maxFloorForExecutionTime));
    }

    private BigDecimal calculateMaxFloorExecutionTime(BigDecimal executionTimeSeconds) {
        // add 5 percent to value
        BigDecimal oneHundred = BigDecimal.valueOf(100);
        BigDecimal percentOfMaxFloor = BigDecimal.valueOf(0.1);

        BigDecimal fivePercent = executionTimeSeconds.divide(oneHundred, 4, RoundingMode.HALF_UP).multiply(percentOfMaxFloor);
        return fivePercent.add(executionTimeSeconds);
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

    private boolean isLowerOrEquals(BigDecimal executionTimeSeconds, BigDecimal maxFloorForExecutionTime) {
        System.out.println(String.format("executionTimeSeconds=%s, maxFloorForExecutionTime=%s", executionTimeSeconds, maxFloorForExecutionTime));
        return executionTimeSeconds.compareTo(maxFloorForExecutionTime) <= 0;
    }

    private boolean isGreaterOrEquals(BigDecimal executionTimeSeconds, BigDecimal approximatedExecutionTime) {
        System.out.println(String.format("executionTimeSeconds=%s, approximatedExecutionTime=%s", executionTimeSeconds, approximatedExecutionTime));
        return executionTimeSeconds.compareTo(approximatedExecutionTime) >= 0;
    }
}
