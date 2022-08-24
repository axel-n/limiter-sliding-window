package io.github.axel_n.limiter;

import io.github.axel_n.limiter.dto.LimiterType;
import io.github.axel_n.limiter.exception.ReachedLimitException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

public interface Limiter {
    LimiterType getLimiterType();

    String getInstanceName();

    // manual check ability sent request
    boolean isPossibleSendRequest();
    void writeHistory();


    // execute or wait
    void executeOrWait(Runnable runnable) throws ReachedLimitException, TimeoutException;
    void executeOrWait(Runnable runnable, long maxTimeWaitInMilliseconds) throws ReachedLimitException, TimeoutException;
    <T> T executeOrWait(Callable<T> callable) throws Exception;
    <T> T executeOrWait(Callable<T> callable, long maxTimeWaitInMilliseconds) throws Exception;


    // execute or throw exception
    void executeOrThrowException(Runnable runnable) throws ReachedLimitException;
    <T> T executeOrThrowException(Callable<T> callable) throws Exception;
}
