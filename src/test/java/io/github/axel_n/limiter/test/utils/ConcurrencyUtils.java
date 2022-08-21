package io.github.axel_n.limiter.test.utils;

public class ConcurrencyUtils {

    public static int calculateCountThreads() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();

        int threads = 2; // minimum
        if (availableProcessors >= 3) {
            threads = availableProcessors - 1;
        }

        if (threads >= 4) { // maximum
            threads = 4;
        }
        System.out.println("count threads=" + threads);

        return threads;
    }

}
