package com.example.limiter;
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
