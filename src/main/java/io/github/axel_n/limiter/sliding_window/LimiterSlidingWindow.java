package io.github.axel_n.limiter.sliding_window;

import io.github.axel_n.limiter.Limiter;
import io.github.axel_n.limiter.config.LimiterConfig;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class LimiterSlidingWindow<T> implements Limiter<T> {
    private final int maxRequests;
    private final int intervalInMilliseconds;
    private final Queue<Long> historyRequests = new ConcurrentLinkedQueue<>();

    private final AtomicInteger counterRequests = new AtomicInteger(0);

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
        return counterRequests.incrementAndGet() <= maxRequests;
    }

    @Override
    public void writeHistory() {
        historyRequests.add(System.currentTimeMillis());
    }


//    @Override
//    public void executeOrWait(Runnable runnable) throws ReachedLimitException, TimeoutException {
//        executeOrWait(runnable, maxAwaitExecutionTimeInMilliseconds);
//    }
//
//
//    @Override
//    public void executeOrWait(Runnable runnable, long maxTimeWaitInMilliseconds) throws ReachedLimitException, TimeoutException {
//        long timeBeforeAwait = System.currentTimeMillis();
//
//        synchronized (this) {
//            // TODO refactor
//            while (!isPossibleSendRequest()) {
//                long now = System.currentTimeMillis();
//                long diff = now - timeBeforeAwait;
//                if (diff >= maxTimeWaitInMilliseconds) {
//                    throw new TimeoutException();
//                }
//                try {
//                    Thread.sleep(intervalForCheckExecutionInMilliseconds);
//                } catch (InterruptedException ignored) {}
//            }
//
//            runnable.run();
//            writeHistory();
//        }
//    }

//    @Override
//    public T executeOrWait(Callable<T> callable) throws Exception {
//        return executeOrWait(callable, maxAwaitExecutionTimeInMilliseconds);
//    }
//
//    @Override
//    public T executeOrWait(Callable<T> callable, long maxTimeWaitInMilliseconds) throws Exception {
//        long timeBeforeAwait = System.currentTimeMillis();
//
//        while (!isPossibleSendRequest()) {
//            long now = System.currentTimeMillis();
//            long diff = now - timeBeforeAwait;
//            if (diff >= maxTimeWaitInMilliseconds) {
//                throw new InterruptedException();
//            }
//
//            try {
//                Thread.sleep(intervalForCheckExecutionInMilliseconds);
//            } catch (Exception ignored) {}
//        }
//
//        T result = callable.call();
//        writeHistory();
//        return result;
//    }
//
//    @Override
//    public void executeOrThrowException(Runnable runnable) throws ReachedLimitException {
//        if (isPossibleSendRequest()) {
//            runnable.run();
//            writeHistory();
//        } else {
//            throw new ReachedLimitException();
//        }
//    }
//
//    @Override
//    public T executeOrThrowException(Callable<T> callable) throws Exception {
//        if (isPossibleSendRequest()) {
//            T result = callable.call();
//            writeHistory();
//            return result;
//        } else {
//            throw new ReachedLimitException();
//        }
//    }


    private void cleanHistory() {
        while (!Thread.interrupted()) {
            if (!historyRequests.isEmpty()) {
                long now = System.currentTimeMillis();

                for (long current : historyRequests) {
                    if (isOld(now, current, intervalInMilliseconds)) {
                        historyRequests.poll();
                        counterRequests.decrementAndGet();
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
