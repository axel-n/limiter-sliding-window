package io.github.axel_n.limiter;

import io.github.axel_n.limiter.sliding_window.LimiterSlidingWindow;
import io.github.axel_n.limiter.test.TestExternalService;

public class TestProducerMyLimiter {
    private final LimiterSlidingWindow limiter;
    private final TestExternalService externalService;

    public TestProducerMyLimiter(LimiterSlidingWindow limiter, TestExternalService externalService) {
        this.limiter = limiter;
        this.externalService = externalService;
    }

    public void sendFakeRequest() {
        externalService.sendFakeRequest();

        // notify limiter
        limiter.writeHistory();
    }
}
