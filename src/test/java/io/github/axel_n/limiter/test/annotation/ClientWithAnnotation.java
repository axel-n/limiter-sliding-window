package io.github.axel_n.limiter.test.annotation;

import io.github.axel_n.limiter.annotation.ExecutionLimitType;
import io.github.axel_n.limiter.annotation.LimiterConfig;
import io.github.axel_n.limiter.annotation.TimeConfig;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ClientWithAnnotation {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @LimiterConfig(
            instanceName = "limiter1",
            limitType = ExecutionLimitType.EXECUTE_OR_WAIT,
            maxTimeWait = @TimeConfig(value = 1, interval = TimeUnit.SECONDS)
    )
    public void sendDataWithLimiter1() {
        System.out.println("time=" + dateFormat.format(new Date()) + ", sending data with limiter1...");
    }

    @LimiterConfig(
            instanceName = "limiter2",
            limitType = ExecutionLimitType.EXECUTE_OR_WAIT,
            maxTimeWait = @TimeConfig(value = 10, interval = TimeUnit.SECONDS)
    )
    public void sendDataWithLimiter2() {
        System.out.println("time=" + dateFormat.format(new Date()) + ", sending data with limiter2...");
    }
}
