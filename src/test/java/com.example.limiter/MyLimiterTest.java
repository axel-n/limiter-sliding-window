package com.example.limiter;

import org.junit.jupiter.api.Test;

public class MyLimiterTest {

    private final MyLimiter limiter = new MyLimiter(10, 5); // 10 request per 5 seconds
    private final TestExternalService externalService = new TestExternalService();
    private final TestProducer producer = new TestProducer(limiter, externalService);

    @Test
    public void test1() {
        int countSuccessfully = 0;

        while (countSuccessfully != 50) { // await 50 delivered requests
            if (limiter.isPossibleSendRequest()) {
                producer.sendFakeRequest();
                countSuccessfully++;
            }
        }
    }

}
