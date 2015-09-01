package com.pocketmath.stasov.util;

import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongComparator;

import java.util.*;

/**
 * Created by etucker on 4/4/15.
 */
public class StasovArrays {

    public static boolean isSorted(long[] array, LongComparator cmp) {
        final long[] tmpArray = LongArrays.copy(array);
        LongArrays.quickSort(tmpArray, cmp);
        return Arrays.equals(array, tmpArray);
    }

    private static int RANDOMIZE_ARRAY_MAX_INT = ((int) Math.sqrt(Integer.MAX_VALUE)) / 2;

    private static int computeRandomizeIterations(final int arrayLength) {
        if (arrayLength > RANDOMIZE_ARRAY_MAX_INT) throw new UnsupportedOperationException("arrays is too big; length = " + arrayLength + "; max supported value is " + RANDOMIZE_ARRAY_MAX_INT);
        final int n = arrayLength * arrayLength;
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
                final Object x = array[i];
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
                final long x = array[i];
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
                final int x = array[i];
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
                final short x = array[i];
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
                final boolean x = array[i];
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
                final char x = array[i];
                array[i] = array[j];
                array[j] = x;
            }
        }
    }

    public static long[] toPrimitiveLongArray(Long[] array) {
        final long[] newArray = new long[array.length];
        for (int i = 0; i < array.length; i++) { newArray[i] = array[i]; }
        return newArray;
    }

    public static long[] toSortedArray(Collection<Long> collection) {
        final Set<Long> set = new HashSet<Long>();
        set.addAll(collection);
        final Long[] objectLongArray = set.toArray(new Long[set.size()]);
        final long[] primitiveLongArray = toPrimitiveLongArray(objectLongArray);
        Arrays.sort(primitiveLongArray);
        return primitiveLongArray;
    }

    private static <V extends Comparable<V>> Weighted<V> chooseRandomWeightedPresorted(SortedSet<Weighted<V>> items) {
        final Random random = new Random();
        int n = (int) Math.floor((double)(random.nextDouble() * items.size()));
        final Iterator<Weighted<V>> itr = items.iterator();
        Weighted<V> item = null;
        for (int i = 0; i <= n && itr.hasNext(); i++) {
            item = itr.next();
        }
        return item;
    }

    public static <V extends Comparable<V>> Weighted<V> chooseRandomWeighted(Collection<Weighted<V>> items) {
        final TreeSet<Weighted<V>> ts = new TreeSet<Weighted<V>>();
        ts.addAll(items);
        return chooseRandomWeightedPresorted(ts);
    }

    public static <V extends Comparable<V>> V chooseRandomWeightedValue(Collection<Weighted<V>> items) {
        final Weighted<V> item = chooseRandomWeighted(items);
        if (item == null) return null;
        return item.getValue();
    }

}
