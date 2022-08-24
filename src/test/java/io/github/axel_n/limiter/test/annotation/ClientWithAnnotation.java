package io.github.axel_n.limiter.test.annotation;

import io.github.axel_n.limiter.annotation.ExecutionLimitType;
import io.github.axel_n.limiter.annotation.LimiterConfig;

public class ClientWithAnnotation {

    @LimiterConfig(instanceName = "limiter1", limitType = ExecutionLimitType.EXECUTE_OR_WAIT)
    public void sendData() {
        System.out.println("sending data...");
        System.out.println("finish send data...");
    }
}
