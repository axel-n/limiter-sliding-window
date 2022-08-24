package io.github.axel_n.limiter.sliding_window.concurrency;

import io.github.axel_n.limiter.config.LimiterConfigBuilder;
import io.github.axel_n.limiter.exception.ReachedLimitException;
import io.github.axel_n.limiter.sliding_window.LimiterSlidingWindow;
import io.github.axel_n.limiter.test.MockSender;
import io.github.axel_n.limiter.test.StatisticService;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ThrowExceptionLimiterConcurrencyTest {
    private final StatisticService statisticService = new StatisticService();
    private final MockSender mockSender = new MockSender(statisticService);

    @Test
    void sendRequestsWithLimiterInParallel() throws Exception {
        int maxRequestsInPeriod = 1;

        // 1 requests per 60 second
        LimiterSlidingWindow limiter = new LimiterSlidingWindow(
                new LimiterConfigBuilder()
                        .setSizeWindow(60, TimeUnit.SECONDS)
                        .setMaxRequestsInWindow(maxRequestsInPeriod)
                        .build()
        );

        // emulate already busy limiter
        limiter.executeOrWait(mockSender::sendFakeRequest);

        for (int i = 0; i < 5; i++) {
            assertThrows(ReachedLimitException.class, () -> {
                limiter.executeOrThrowException(() -> true);
            });
        }
    }
}
