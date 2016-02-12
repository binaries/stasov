package com.pocketmath.stasov.util;

/**
 * Created by etucker on 1/25/16.
 */
public class TimeBenchmark {

    public static void main(String args[]) {
        final long start = System.currentTimeMillis();
        final int n = 1000*1000*10;
        for (int i = 0; i < n; i++) {
            System.currentTimeMillis();
            System.nanoTime();
        }
        final long stop = System.currentTimeMillis();
        final double t = ((double)(stop-start))/((double)n);

        System.out.println("avg time = " + t + "ms");
        System.out.println("total time = " + (stop-start) + "ms");
    }
}
