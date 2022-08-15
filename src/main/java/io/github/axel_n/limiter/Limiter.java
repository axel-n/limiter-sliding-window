package io.github.axel_n.limiter;

import io.github.axel_n.limiter.exception.ReachedLimitException;
import java.util.concurrent.Callable;

public interface Limiter<T> {
    // manual check ability sent request
    boolean isPossibleSendRequest();
    void writeHistory();


    // execute or wait
    void executeOrWait(Runnable runnable) throws ReachedLimitException, InterruptedException;
    void executeOrWait(Runnable runnable, long maxTimeWaitInMilliseconds) throws ReachedLimitException, InterruptedException;
    T executeOrWait(Callable<T> callable) throws Exception;
    T executeOrWait(Callable<T> callable, long maxTimeWaitInMilliseconds) throws Exception;


    // execute or throw exception
    void executeOrThrowException(Runnable runnable) throws ReachedLimitException;
    T executeOrThrowException(Callable<T> callable) throws Exception;
}
