package io.github.axel_n.limiter.test.utils;

public class NumberUtils {
    public static double getExecutionTime(long timeAfterTest, long timeBeforeTest) {
       return  ((double) timeAfterTest - (double) timeBeforeTest) / 1_000 ;
    }

    public static double calculateMaxFloorExecutionTime(double executionTimeSeconds) {
        double percentOfMaxFloor = 2; // runner in github actions so slow
        // TODO provide max percent from environment

        double somePercent = (executionTimeSeconds / 100) * percentOfMaxFloor;
        return somePercent + executionTimeSeconds;
    }

    public static double getApproximatedExecutionTime(int allRequests, int maxRequestsPerPeriod, int intervalSeconds) {
        return ((double) allRequests / (double) maxRequestsPerPeriod - 1) * (double) intervalSeconds;
    }

}
