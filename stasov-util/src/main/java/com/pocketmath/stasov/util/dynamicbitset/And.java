//package com.pocketmath.stasov.util.dynamicbitset;
//
//import javax.annotation.Nonnegative;
//import javax.annotation.Nonnull;
//import java.util.Arrays;
//
///**
// * Created by etucker on 4/4/16.
// */
//public class And extends SBS2 {
//
//    @Override
//    protected boolean mustConvertOnSubRange(boolean rangeIsSet) {
//        return rangeIsSet;
//    }
//
//    @Override
//    protected long rawLongApply(long l0, long l1, int startBitInclusive, int endBitExclusive) {
//        return 0;
//    }
//
//    @Override
//    protected void rawLongApply(long[] longArray, int arrayIndexStartInclusive, int arrayIndexEndExclusive) {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    protected void rawLongApply(long[] array0, long[] array1, int i0, int len) {
//        for (int i = i0; i < len; i++) {
//            array0[i] = array0[i] & array1[i];
//        }
//    }
//
//    @Override
//    protected void rawLongApply(
//            final long[] array0,
//            final long[] array1,
//            final @Nonnegative int i0,
//            final @Nonnegative int len,
//            final @Nonnegative int i1) {
//
//        final int _len = i0 + len;
//        final int adj1 = i1 - i0;
//        for (int i = i0; i < _len; i++) {
//            array0[i] = array0[i] & array1[ adj1 + i ];
//        }
//    }
//
//    /**
//     *
//     * @param array
//     * @param allSetOrClear true is all set, false is all clear
//     */
//    @Override
//    protected void rawLongApply(long[] array, boolean allSetOrClear) {
//        if (!allSetOrClear)
//            Arrays.fill(array, 0L);
//    }
//
//    @Override
//    protected void sparseApply(
//            int index,
//            long raw,
//            int startPosition,
//            int endPosition,
//            int absLength,
//            int rawLength) {
//
//    }
//
//}
