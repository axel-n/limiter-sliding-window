package io.github.axel_n.limiter.sliding_window.concurrency;

import io.github.axel_n.limiter.TestProducerMyLimiter;
import io.github.axel_n.limiter.config.LimiterConfigBuilder;
import io.github.axel_n.limiter.exception.ReachedLimitException;
import io.github.axel_n.limiter.sliding_window.LimiterSlidingWindow;
import io.github.axel_n.limiter.test.StatisticService;
import io.github.axel_n.limiter.test.TestExternalService;
import java.time.Duration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ThrowExceptionLimiterConcurrencyTest {
    private final StatisticService statisticService = new StatisticService();
    private final TestExternalService externalService = new TestExternalService(statisticService);

    @Test
    void sendRequestsWithLimiterInParallel() throws Exception {
        int maxRequestsInPeriod = 1;

        // 1 requests per 60 second
        // check every 100ms for execution. max wait 10s
        LimiterSlidingWindow<Boolean> limiter = new LimiterSlidingWindow<>(
                new LimiterConfigBuilder()
                        .setInterval(Duration.ofSeconds(60))
                        .setMaxRequestsInInterval(maxRequestsInPeriod)
                        .build()
        );

        TestProducerMyLimiter producer = new TestProducerMyLimiter(limiter, externalService);

        // emulate already busy limiter
        limiter.executeOrWait(producer::sendFakeRequest);

        for (int i = 0; i < 5; i++) {
            assertThrows(ReachedLimitException.class, () -> {
                limiter.executeOrThrowException(() -> true);
            });
        }
    }
}
