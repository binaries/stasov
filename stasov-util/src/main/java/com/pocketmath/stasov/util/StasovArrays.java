package com.pocketmath.stasov.util;

import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongComparator;

import java.util.Arrays;

/**
 * Created by etucker on 4/4/15.
 */
public class StasovArrays {

    public static boolean isSorted(long[] array, LongComparator cmp) {
        long[] tmpArray = LongArrays.copy(array);
        LongArrays.quickSort(tmpArray, cmp);
        return Arrays.equals(array, tmpArray);
    }

}
