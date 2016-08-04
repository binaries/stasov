//package com.pocketmath.stasov.util.dynamicbitset;
//
//import com.pocketmath.stasov.util.BitUtil;
//
//import javax.annotation.Nonnegative;
//import javax.annotation.Nonnull;
//import java.util.Arrays;
//
///**
// * Created by etucker on 4/4/16.
// */
//public class Set extends SBS2 {
//
//    protected boolean mustConvertOnSubRange(final boolean rangeIsSet) {
//        return !rangeIsSet;
//    }
//
//    @Override
//    protected long rawLongApply(long l0, long l1, int startBitInclusive, int endBitExclusive) {
//        return 0;
//    }
//
//    protected long rawLongApply(final long l, final int startBitInclusive, final int endBitExclusive) {
//        return BitUtil.toSet(l, startBitInclusive, endBitExclusive);
//    }
//
//    protected void rawLongApply(long[] longArray, int arrayIndexStartInclusive, int arrayIndexEndExclusive) {
//        Arrays.fill(longArray, arrayIndexStartInclusive, arrayIndexEndExclusive, 0xFFFFFFFFFFFFFFFFL);
//    }
//
//    @Override
//    protected void rawLongApply(long[] array0, long[] array1, int i0, int len) {
//
//    }
//
//    @Override
//    protected void rawLongApply(long[] array0, long[] array1, @Nonnegative int i0, @Nonnegative int len, @Nonnegative int i1) {
//
//    }
//
//    @Override
//    protected void rawLongApply(long[] array, boolean allSetOrClear) {
//
//    }
//
//}
