package io.github.axel_n.limiter.test.utils;

public class NumberUtils {
    public static double getExecutionTime(long timeAfterTest, long timeBeforeTest) {
       return  ((double) timeAfterTest - (double) timeBeforeTest) / 1_000 ;
    }

    public static double calculateMaxFloorExecutionTime(double executionTimeSeconds) {
        double percentOfMaxFloor = 0.1;

        double somePercent = (executionTimeSeconds / 100) * percentOfMaxFloor;
        return somePercent + executionTimeSeconds;
    }

}
