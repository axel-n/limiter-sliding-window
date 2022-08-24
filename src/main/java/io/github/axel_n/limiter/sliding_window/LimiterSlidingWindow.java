package io.github.axel_n.limiter.sliding_window;

import io.github.axel_n.limiter.Limiter;
import io.github.axel_n.limiter.config.LimiterConfig;
import io.github.axel_n.limiter.dto.LimiterType;
import io.github.axel_n.limiter.exception.ReachedLimitException;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public class LimiterSlidingWindow implements Limiter {
    private final int maxRequests;
    private final int sizeWindowInMilliseconds;
    private final Queue<Long> historyRequests = new ConcurrentLinkedQueue<>();

    private final AtomicReference<Integer> counterRequests = new AtomicReference<>(0);

    private final long periodForCheckExecutionInMilliseconds;
    private final long maxAwaitExecutionTimeInMilliseconds;
    private final String instanceName;

    public LimiterSlidingWindow(LimiterConfig config) {
        this.maxRequests = config.getMaxRequestsInWindow();
        this.sizeWindowInMilliseconds = (int) config.getSizeWindowInMilliseconds();
        this.periodForCheckExecutionInMilliseconds = config.getPeriodForCheckExecutionInMilliseconds();
        this.maxAwaitExecutionTimeInMilliseconds = config.getMaxAwaitExecutionTimeInMilliseconds();
        this.instanceName = config.getInstanceName();

        ExecutorService cleanHistoryExecutor = Executors.newSingleThreadExecutor();
        cleanHistoryExecutor.execute(this::cleanHistory);
    }

    /**
     * Do not forget call method writeHistory() for keep history
     *
     * @return true is sent request in period lower than maxRequests
     */
    @Override
    public boolean isPossibleSendRequest() {
        int currentCounter = counterRequests.get();

        if (currentCounter > maxRequests) {
            return false;
        }

        while (!Thread.interrupted()) {
            int latestValue = counterRequests.get();

            if (latestValue >= maxRequests) {
                return false;
            }

            if (counterRequests.compareAndSet(latestValue, latestValue + 1)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void writeHistory() {
        writeHistory(System.currentTimeMillis());
    }

    private void writeHistory(long timestamp) {
        historyRequests.add(timestamp);
    }


    @Override
    public void executeOrWait(Runnable runnable) throws ReachedLimitException, TimeoutException {
        executeOrWait(runnable, maxAwaitExecutionTimeInMilliseconds);
    }


    @Override
    public void executeOrWait(Runnable runnable, long maxTimeWaitInMilliseconds) throws ReachedLimitException, TimeoutException {
        long timeBeforeAwait = System.currentTimeMillis();

        while (!isPossibleSendRequest()) {
            long now = System.currentTimeMillis();
            long diff = now - timeBeforeAwait;
            if (diff >= maxTimeWaitInMilliseconds) {
                throw new TimeoutException();
            }
            try {
                Thread.sleep(periodForCheckExecutionInMilliseconds);
            } catch (InterruptedException e) {
                throw new TimeoutException();
            }
        }

        long now = System.currentTimeMillis();
        runnable.run();
        writeHistory(now);
    }

    @Override
    public <T> T executeOrWait(Callable<T> callable) throws Exception {
        return executeOrWait(callable, maxAwaitExecutionTimeInMilliseconds);
    }

    @Override
    public <T> T executeOrWait(Callable<T> callable, long maxTimeWaitInMilliseconds) throws Exception {
        long timeBeforeAwait = System.currentTimeMillis();

        while (!isPossibleSendRequest()) {
            long now = System.currentTimeMillis();
            long diff = now - timeBeforeAwait;
            if (diff >= maxTimeWaitInMilliseconds) {
                throw new InterruptedException();
            }

            try {
                Thread.sleep(periodForCheckExecutionInMilliseconds);
            } catch (InterruptedException e) {
                throw new InterruptedException();
            }
        }
        long now = System.currentTimeMillis();
        T result = callable.call();
        writeHistory(now);
        return result;
    }

    @Override
    public void executeOrThrowException(Runnable runnable) throws ReachedLimitException {
        if (isPossibleSendRequest()) {
            runnable.run();
            writeHistory();
        } else {
            throw new ReachedLimitException();
        }
    }

    @Override
    public <T> T executeOrThrowException(Callable<T> callable) throws Exception {
        if (isPossibleSendRequest()) {
            T result = callable.call();
            writeHistory();
            return result;
        } else {
            throw new ReachedLimitException();
        }
    }

    @Override
    public LimiterType getLimiterType() {
        return LimiterType.SLIDING_WINDOW;
    }

    @Override
    public String getInstanceName() {
        return instanceName;
    }

    private void cleanHistory() {
        while (!Thread.interrupted()) {
            if (!historyRequests.isEmpty()) {
                long now = System.currentTimeMillis();

                for (long current : historyRequests) {
                    if (isOld(now, current, sizeWindowInMilliseconds)) {
                        historyRequests.poll();
                        decrementFirstCounter();
                    } else {
                        break;
                    }
                }
            }
        }
    }

    private void decrementFirstCounter() {
        while (!Thread.interrupted()) {
            int currentValue = counterRequests.get();

            if (counterRequests.compareAndSet(currentValue, currentValue - 1)) {
                return;
            }
        }
    }

    private boolean isOld(long now, long timeRequest, int maxInterval) {
        return (now - timeRequest) > maxInterval;
    }
}
