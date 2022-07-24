package com.axel_n.limiter;

public interface Limiter {

    boolean isPossibleSendRequest();

    void writeHistory();
}
