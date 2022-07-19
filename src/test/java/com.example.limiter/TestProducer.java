package com.example.limiter;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PreDestroy;

public class TestProducer {
    private final MyLimiter limiter;
    private final TestExternalService externalService;

    public TestProducer(MyLimiter limiter, TestExternalService externalService) {
        this.limiter = limiter;
        this.externalService = externalService;
    }

    public void sendFakeRequest() {
        externalService.sendFakeRequest();
        // TODO add sleep?

        // notify limiter
        limiter.writeHistory();
    }
}
