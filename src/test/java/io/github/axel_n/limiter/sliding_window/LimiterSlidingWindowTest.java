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
        int intervalSeconds = 1;

        validateLimiter(allRequests, maxRequestsPerPeriod, intervalSeconds);
    }

    @Test
    public void test10RequestsPerSecond() {
        int maxRequestsPerPeriod = 10;
        int allRequests = 50;
        int intervalSeconds = 1;

        validateLimiter(allRequests, maxRequestsPerPeriod, intervalSeconds);
    }

    @Test
    public void test3RequestsPer3Second() {
        int maxRequestsPerPeriod = 3;
        int allRequests = 15;
        int intervalSeconds = 3;

        validateLimiter(allRequests, maxRequestsPerPeriod, intervalSeconds);
    }

    private void validateLimiter(int allRequests, int maxRequestsPerPeriod, int intervalSeconds) {
        long timeBeforeTest = System.currentTimeMillis();

        sendFakeRequestsWithLimiter(allRequests, maxRequestsPerPeriod, intervalSeconds);

        long timeAfterTest = System.currentTimeMillis();

        int receivedMaxRequestsInSeconds = statisticService.getMaxRequestsInSeconds();

        if (intervalSeconds == 1) {
            assertEquals(maxRequestsPerPeriod, receivedMaxRequestsInSeconds);
        } else {
            assertTrue(receivedMaxRequestsInSeconds >= 1);
            assertTrue(receivedMaxRequestsInSeconds <= maxRequestsPerPeriod);
        }

        double executionTimeSeconds = getExecutionTime(timeAfterTest, timeBeforeTest);
        double approximatedExecutionTime = getApproximatedExecutionTime(allRequests, maxRequestsPerPeriod);
        assertTrue(executionTimeSeconds >= approximatedExecutionTime);

        double maxFloorForExecutionTime = calculateMaxFloorExecutionTime(executionTimeSeconds);
        assertTrue(executionTimeSeconds <= maxFloorForExecutionTime);
    }

    private void sendFakeRequestsWithLimiter(int allRequests, int maxRequestsPerPeriod, int intervalSeconds) {

        try (LimiterSlidingWindow limiter = new LimiterSlidingWindow(maxRequestsPerPeriod, intervalSeconds)) {
            TestProducerMyLimiter producer = new TestProducerMyLimiter(limiter, externalService);

            while (statisticService.getCountCountReceivedRequests() != allRequests) {
                if (limiter.isPossibleSendRequest()) {
                    producer.sendFakeRequest();
                }
            }
        }
    }
}
