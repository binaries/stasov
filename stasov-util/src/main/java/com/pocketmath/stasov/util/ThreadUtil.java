package com.pocketmath.stasov.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by etucker on 2/3/16.
 */
public class ThreadUtil {

    public static void runThreads(final Runnable runnable, final int threadsN, final String threadGroupName, final boolean waitForCompletionOrInterrupt) throws InterruptedException {
        final ThreadGroup threadGroup = new ThreadGroup(threadGroupName);
        final Thread[] threads = new Thread[threadsN];
        for (int i = 0; i < threadsN; i++) {
            final Thread thread = new Thread(threadGroup, runnable);
            threads[i] = thread;
        }

        for (int i = 0; i < threads.length; i++) {
            final Thread thread = threads[i];
            if (thread == null) throw new IllegalStateException();
            thread.start();
        }

        if (waitForCompletionOrInterrupt) {
            for (int i = 0; i < threads.length; i++) {
                final Thread thread = threads[i];
                if (thread == null) throw new IllegalStateException();
                thread.join();
            }
        }
    }

    public static void runThreadsAndWait(final Runnable runnable, final int threadsN, final String threadGroupName) throws InterruptedException {
        runThreads(runnable, threadsN, threadGroupName, true);
    }

    public static void runThreadsAndWait(final Runnable runnable, final int threadsN) throws InterruptedException {
        final StringBuilder sb = new StringBuilder();
        sb.append(Long.toString(System.currentTimeMillis()));
        sb.append('_');
        sb.append(Long.toString(ThreadLocalRandom.current().nextLong()));
        final String threadGroupName = sb.toString();
        runThreads(runnable, threadsN, threadGroupName, true);
    }

}
