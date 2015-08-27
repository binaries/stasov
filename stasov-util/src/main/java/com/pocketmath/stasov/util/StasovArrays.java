package com.pocketmath.stasov.util;

import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongComparator;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by etucker on 4/4/15.
 */
public class StasovArrays {

    public static boolean isSorted(long[] array, LongComparator cmp) {
        long[] tmpArray = LongArrays.copy(array);
        LongArrays.quickSort(tmpArray, cmp);
        return Arrays.equals(array, tmpArray);
    }

    private static int RANDOMIZE_ARRAY_MAX_INT = ((int) Math.sqrt(Integer.MAX_VALUE)) / 2;

    private static int computeRandomizeIterations(final int arrayLength) {
        if (arrayLength > RANDOMIZE_ARRAY_MAX_INT) throw new UnsupportedOperationException("arrays is too big; length = " + arrayLength + "; max supported value is " + RANDOMIZE_ARRAY_MAX_INT);
        int n = arrayLength * arrayLength;
        if (n < 0) throw new IllegalStateException(new ArithmeticException("overflow"));
        return arrayLength;
    }

    /**
     * Randomize the order of the elements in the array.
     *
     * @param array
     */
    public static void randomizeOrder(final Object[] array) {
        final Random random = new Random();
        final int n = computeRandomizeIterations(array.length);
        for (int i = 0; i < n; i++) {
            final int j = random.nextInt(array.length);
            if (i != j) {
                // swap
                Object x = array[i];
                array[i] = array[j];
                array[j] = x;
            }
        }
    }

    /**
     * Randomize the order of the elements in the array.
     *
     * @param array
     */
    public static void randomizeOrder(final long[] array) {
        final Random random = new Random();
        final int n = computeRandomizeIterations(array.length);
        for (int i = 0; i < n; i++) {
            final int j = random.nextInt(array.length);
            if (i != j) {
                // swap
                long x = array[i];
                array[i] = array[j];
                array[j] = x;
            }
        }
    }

    /**
     * Randomize the order of the elements in the array.
     *
     * @param array
     */
    public static void randomizeOrder(final int[] array) {
        final Random random = new Random();
        final int n = computeRandomizeIterations(array.length);
        for (int i = 0; i < n; i++) {
            final int j = random.nextInt(array.length);
            if (i != j) {
                // swap
                int x = array[i];
                array[i] = array[j];
                array[j] = x;
            }
        }
    }

    /**
     * Randomize the order of the elements in the array.
     *
     * @param array
     */
    public static void randomizeOrder(final short[] array) {
        final Random random = new Random();
        final int n = computeRandomizeIterations(array.length);
        for (int i = 0; i < n; i++) {
            final int j = random.nextInt(array.length);
            if (i != j) {
                // swap
                short x = array[i];
                array[i] = array[j];
                array[j] = x;
            }
        }
    }

    /**
     * Randomize the order of the elements in the array.
     *
     * @param array
     */
    public static void randomizeOrder(final boolean[] array) {
        final Random random = new Random();
        final int n = computeRandomizeIterations(array.length);
        for (int i = 0; i < n; i++) {
            final int j = random.nextInt(array.length);
            if (i != j) {
                // swap
                boolean x = array[i];
                array[i] = array[j];
                array[j] = x;
            }
        }
    }

    /**
     * Randomize the order of the elements in the array.
     *
     * @param array
     */
    public static void randomizeOrder(final char[] array) {
        final Random random = new Random();
        final int n = computeRandomizeIterations(array.length);
        for (int i = 0; i < n; i++) {
            final int j = random.nextInt(array.length);
            if (i != j) {
                // swap
                char x = array[i];
                array[i] = array[j];
                array[j] = x;
            }
        }
    }

}
