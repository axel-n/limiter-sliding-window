package io.github.axel_n.limiter.sliding_window.concurrency;

import io.github.axel_n.limiter.Limiter;
import io.github.axel_n.limiter.TestProducerMyLimiter;
import io.github.axel_n.limiter.config.LimiterConfigBuilder;
import io.github.axel_n.limiter.sliding_window.LimiterSlidingWindow;
import io.github.axel_n.limiter.test.StatisticService;
import io.github.axel_n.limiter.test.TestExternalService;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

import static io.github.axel_n.limiter.test.utils.ConcurrencyUtils.calculateCountThreads;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AutomaticWaitLimiterConcurrencyTest {
    private final StatisticService statisticService = new StatisticService();
    private final TestExternalService externalService = new TestExternalService(statisticService);

    @Test
        // TODO how to check cpus in junit
    void sendRequestsWithLimiterInParallel() {
        int threads = calculateCountThreads();

        int maxRequestsInPeriod = 2;

        LimiterSlidingWindow<Void> limiter = new LimiterSlidingWindow<>(new LimiterConfigBuilder()
                .setInterval(Duration.ofSeconds(1))
                .setMaxRequestsInInterval(maxRequestsInPeriod)
                .setMaxAwaitExecutionTime(Duration.ofSeconds(10))
                .setIntervalForCheckExecution(Duration.ofMillis(100))
                .build());

        TestProducerMyLimiter producer = new TestProducerMyLimiter(limiter, externalService);


        ExecutorService executorService = Executors.newFixedThreadPool(threads);

        List<Future<Boolean>> tasks = new ArrayList<>(threads);
        for (int i = 0; i < threads; i++) {
            Future<Boolean> future = executorService.submit(createProducer(limiter, producer));
            tasks.add(future);
        }

        for (Future<Boolean> task : tasks) {
            try {
                task.get(60, TimeUnit.SECONDS);
            } catch (Exception ignored) {
            }
        }

        executorService.shutdown();

        int maxRequestsInTest = statisticService.getMaxRequestsInSeconds();
        assertEquals(maxRequestsInPeriod, maxRequestsInTest);
    }

    private Callable<Boolean> createProducer(Limiter<Void> limiter, TestProducerMyLimiter producer) {
        return () -> {
            while (statisticService.getCountCountReceivedRequests() != 90) {
                limiter.executeOrWait(producer::sendFakeRequest);
            }

            return true;
        };
    }
}
