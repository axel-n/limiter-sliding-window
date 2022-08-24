package io.github.axel_n.limiter.sliding_window;

import io.github.axel_n.limiter.config.LimiterConfigBuilder;
import io.github.axel_n.limiter.test.MockSender;
import io.github.axel_n.limiter.test.StatisticService;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.github.axel_n.limiter.test.utils.NumberUtils.calculateMaxFloorExecutionTime;
import static io.github.axel_n.limiter.test.utils.NumberUtils.getApproximatedExecutionTime;
import static io.github.axel_n.limiter.test.utils.NumberUtils.getExecutionTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LimiterSlidingWindowTest {
    private final StatisticService statisticService = new StatisticService();
    private final MockSender mockSender = new MockSender(statisticService);

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
        double approximatedExecutionTime = getApproximatedExecutionTime(allRequests, maxRequestsPerPeriod, intervalSeconds);
        System.out.println(String.format("executionTime=%s(s), approximatedExecutionTime=%s(s)", executionTimeSeconds, approximatedExecutionTime));
        assertTrue(executionTimeSeconds >= approximatedExecutionTime);

        double maxFloorForExecutionTime = calculateMaxFloorExecutionTime(approximatedExecutionTime);
        System.out.println(String.format("executionTime=%s(s), maxFloorForExecutionTime=%s(s)", executionTimeSeconds, maxFloorForExecutionTime));
        assertTrue(executionTimeSeconds <= maxFloorForExecutionTime);
    }

    private void sendFakeRequestsWithLimiter(int allRequests, int maxRequestsPerPeriod, int intervalSeconds) {
        LimiterSlidingWindow limiter = new LimiterSlidingWindow(
                new LimiterConfigBuilder()
                        .setInterval(Duration.ofSeconds(intervalSeconds))
                        .setMaxRequestsInInterval(maxRequestsPerPeriod)
                        .build());

        while (statisticService.getCountCountReceivedRequests() < allRequests) {
            if (limiter.isPossibleSendRequest()) {
                mockSender.sendFakeRequest();
                limiter.writeHistory();
            }
        }
    }
}
