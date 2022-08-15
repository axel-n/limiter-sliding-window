package io.github.axel_n.limiter.sliding_window;

import io.github.axel_n.limiter.Limiter;
import io.github.axel_n.limiter.config.LimiterConfig;
import io.github.axel_n.limiter.exception.ReachedLimitException;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LimiterSlidingWindow<T> implements Limiter<T> {
    private final int maxRequests;
    private final int intervalInMilliseconds;
    private final Queue<Long> historyRequests = new ConcurrentLinkedQueue<>();

    private final long intervalForCheckExecutionInMilliseconds;
    private final long maxAwaitExecutionTimeInMilliseconds;

    public LimiterSlidingWindow(LimiterConfig config) {
        this.maxRequests = config.getMaxRequestsInInterval();
        this.intervalInMilliseconds = (int) config.getInterval().toMillis();
        this.intervalForCheckExecutionInMilliseconds = config.getIntervalForCheckExecution().toMillis();
        this.maxAwaitExecutionTimeInMilliseconds = config.getMaxAwaitExecutionTime().toMillis();

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
        return historyRequests.size() < maxRequests;
    }

    @Override
    public void writeHistory() {
        historyRequests.add(System.currentTimeMillis());
    }

    @Override
    public void executeOrWait(Runnable runnable) throws ReachedLimitException, InterruptedException {
        executeOrWait(runnable, maxAwaitExecutionTimeInMilliseconds);
    }

    @Override
    public void executeOrWait(Runnable runnable, long maxTimeWaitInMilliseconds) throws ReachedLimitException, InterruptedException {
        long timeBeforeAwait = System.currentTimeMillis();

        while (!isPossibleSendRequest()) {
            long now = System.currentTimeMillis();
            long diff = now - timeBeforeAwait;
            if (diff >= maxTimeWaitInMilliseconds) {
                throw new InterruptedException();
            }
            Thread.sleep(intervalForCheckExecutionInMilliseconds);
        }

        runnable.run();
    }

    @Override
    public T executeOrWait(Callable<T> callable) throws Exception {
        return executeOrWait(callable, maxAwaitExecutionTimeInMilliseconds);
    }

    @Override
    public T executeOrWait(Callable<T> callable, long maxTimeWaitInMilliseconds) throws Exception {
        long timeBeforeAwait = System.currentTimeMillis();

        while (!isPossibleSendRequest()) {
            long now = System.currentTimeMillis();
            long diff = now - timeBeforeAwait;
            if (diff >= maxTimeWaitInMilliseconds) {
                throw new InterruptedException();
            }
            Thread.sleep(intervalForCheckExecutionInMilliseconds);
        }

        return callable.call();
    }

    @Override
    public void executeOrThrowException(Runnable runnable) throws ReachedLimitException {
        if (isPossibleSendRequest()) {
            runnable.run();
        } else {
            throw new ReachedLimitException();
        }
    }

    @Override
    public T executeOrThrowException(Callable<T> callable) throws Exception {
        if (isPossibleSendRequest()) {
            return callable.call();
        } else {
            throw new ReachedLimitException();
        }
    }

    private void cleanHistory() {
        while (!Thread.interrupted()) {
            if (!historyRequests.isEmpty()) {
                long now = System.currentTimeMillis();

                for (long current : historyRequests) {
                    if (isOld(now, current, intervalInMilliseconds)) {
                        historyRequests.poll();
                    } else {
                        break;
                    }
                }
            }
        }
    }

    private boolean isOld(long now, long timeRequest, int maxInterval) {
        return (now - timeRequest) > maxInterval;
    }
}
