package io.github.axel_n.limiter.test.utils;

import java.math.BigDecimal;

public class NumberUtils {
    public static boolean isLowerOrEquals(BigDecimal executionTimeSeconds, BigDecimal maxFloorForExecutionTime) {
        return executionTimeSeconds.compareTo(maxFloorForExecutionTime) <= 0;
    }

    public static boolean isGreaterOrEquals(BigDecimal executionTimeSeconds, BigDecimal approximatedExecutionTime) {
        return executionTimeSeconds.compareTo(approximatedExecutionTime) >= 0;
    }
}
