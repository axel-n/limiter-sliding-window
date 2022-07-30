package com.axel_n.limiter.sliding_window;

import com.axel_n.limiter.TestProducerMyLimiter;
import com.axel_n.limiter.test.StatisticService;
import com.axel_n.limiter.test.TestExternalService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static com.axel_n.limiter.test.utils.NumberUtils.isGreaterOrEquals;
import static com.axel_n.limiter.test.utils.NumberUtils.isLowerOrEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LimiterSlidingWindowTest {
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

        // TODO move logic for measure time to separated test
        long timeForeTest;

        try (LimiterSlidingWindow limiter = new LimiterSlidingWindow(maxRequestsPerPeriod, 1)) {
            TestProducerMyLimiter producer = new TestProducerMyLimiter(limiter, externalService);

            timeForeTest = System.currentTimeMillis();

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
        // add 0.1 percent to value
        BigDecimal oneHundred = BigDecimal.valueOf(100);
        BigDecimal percentOfMaxFloor = BigDecimal.valueOf(0.1);

        BigDecimal fivePercent = executionTimeSeconds.divide(oneHundred, 4, RoundingMode.HALF_UP).multiply(percentOfMaxFloor);
        return fivePercent.add(executionTimeSeconds);
    }


    @Test
    public void test10RequestsPerSecond() {
        int maxRequests = 10;
        try (LimiterSlidingWindow limiter = new LimiterSlidingWindow(maxRequests, 1)) {
            TestProducerMyLimiter producer = new TestProducerMyLimiter(limiter, externalService);

            while (statisticService.getCountCountReceivedRequests() != 50) {
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
        try (LimiterSlidingWindow limiter = new LimiterSlidingWindow(3, 3)) {
            TestProducerMyLimiter producer = new TestProducerMyLimiter(limiter, externalService);

            while (statisticService.getCountCountReceivedRequests() != 15) {
                if (limiter.isPossibleSendRequest()) {
                    producer.sendFakeRequest();
                }
            }
        }

        int receivedMaxRequestsInSeconds = statisticService.getMaxRequestsInSeconds();
        assertTrue(receivedMaxRequestsInSeconds >= 1);
        assertTrue(receivedMaxRequestsInSeconds <= maxRequests);


    }

    @Test
    void checkExecutionTime() {
        // TODO tests
    }
}
