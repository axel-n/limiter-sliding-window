package io.github.axel_n.limiter.sliding_window;

import io.github.axel_n.limiter.TestProducerMyLimiter;
import io.github.axel_n.limiter.test.StatisticService;
import io.github.axel_n.limiter.test.TestExternalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.github.axel_n.limiter.test.utils.NumberUtils.calculateMaxFloorExecutionTime;
import static io.github.axel_n.limiter.test.utils.NumberUtils.getApproximatedExecutionTime;
import static io.github.axel_n.limiter.test.utils.NumberUtils.getExecutionTime;
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
        long timeBeforeTest;

        try (LimiterSlidingWindow limiter = new LimiterSlidingWindow(maxRequestsPerPeriod, 1)) {
            TestProducerMyLimiter producer = new TestProducerMyLimiter(limiter, externalService);

            timeBeforeTest = System.currentTimeMillis();

            while (statisticService.getCountCountReceivedRequests() != allRequests) {
                if (limiter.isPossibleSendRequest()) {
                    producer.sendFakeRequest();
                }
            }
        }
        long timeAfterTest = System.currentTimeMillis();

        int receivedMaxRequestsInSeconds = statisticService.getMaxRequestsInSeconds();
        assertEquals(maxRequestsPerPeriod, receivedMaxRequestsInSeconds);

        double executionTimeSeconds = getExecutionTime(timeAfterTest, timeBeforeTest);
        double approximatedExecutionTime = getApproximatedExecutionTime(allRequests, maxRequestsPerPeriod);
        assertTrue(executionTimeSeconds >= approximatedExecutionTime);

        double maxFloorForExecutionTime = calculateMaxFloorExecutionTime(executionTimeSeconds);
        assertTrue(executionTimeSeconds <= maxFloorForExecutionTime);
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
