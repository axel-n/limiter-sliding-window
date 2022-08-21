package io.github.axel_n.limiter.sliding_window;

import io.github.axel_n.limiter.Limiter;
import io.github.axel_n.limiter.TestProducerMyLimiter;
import io.github.axel_n.limiter.config.LimiterConfigBuilder;
import io.github.axel_n.limiter.test.StatisticService;
import io.github.axel_n.limiter.test.TestExternalService;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LimiterConcurrencyTest {
    private final StatisticService statisticService = new StatisticService();
    private final TestExternalService externalService = new TestExternalService(statisticService);

    @Test // TODO how to check cpus in junit
    void sendRequestsWithLimiterInParallel() throws ExecutionException, TimeoutException {
        int threads = calculateCountThreads();

        if (threads >= 2) {
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
        } else {
            System.out.println("test sendRequestsWithLimiterInParallel skipped. does not hava enough cpus");
        }
    }

    private int calculateCountThreads() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();

        int threads = 2; // minimum
        if (availableProcessors >= 4) {
            threads = availableProcessors - 1;
        }

        if (threads >= 6) { // maximum
            threads = 6;
        }
        System.out.println("count threads=" + threads);
        return threads;
    }

    private Callable<Boolean> createProducer(Limiter<Void> limiter, TestProducerMyLimiter producer) {
        return () -> {
            int counter = 0;
            while (counter != 30) {
                System.out.println("thread " + Thread.currentThread().getName() + ".send new request");
                //limiter.executeOrWait(producer::sendFakeRequest);

                if (limiter.isPossibleSendRequest()) {
                    producer.sendFakeRequest();
                    limiter.writeHistory();
                }

                counter++;
            }

            return true;
        };
    }
}
