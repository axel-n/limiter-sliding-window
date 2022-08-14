package io.github.axel_n.limiter;

import io.github.axel_n.limiter.exception.ReachedLimitException;
import java.time.Duration;
import java.util.concurrent.Callable;

public interface Limiter<T> {
    // manual check ability sent request
    boolean isPossibleSendRequest();
    void writeHistory();

    // execute or wait
    void executeOrWait(Runnable runnable) throws ReachedLimitException;
    void executeOrWait(Runnable runnable, Duration maxTimeWait) throws ReachedLimitException;
    T executeOrWait(Callable<T> callable) throws ReachedLimitException;
    T executeOrWait(Callable<T> callable, Duration maxTimeWait) throws ReachedLimitException;

    // execute or throw exception
    void executeOrThrowException(Runnable runnable) throws ReachedLimitException;
    T executeOrThrowException(Callable<T> callable) throws ReachedLimitException;
}
