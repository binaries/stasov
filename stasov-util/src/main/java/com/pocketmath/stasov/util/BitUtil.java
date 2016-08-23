package com.pocketmath.stasov.util;

import javax.annotation.Nonnegative;

/**
 * Created by etucker on 4/1/16.
 */
public class BitUtil {

    public static long toMask(
            final @Nonnegative int startInclusive,
            final @Nonnegative int endExclusive) {

        assert (startInclusive >= 0);
        assert (startInclusive <= 63);
        assert (endExclusive >= 0);
        assert (endExclusive <= 63);
        assert (endExclusive > startInclusive);

        // build the mask
        final long x = 0xFFFFFFFFFFFFFFFFL >> startInclusive;
        final long y = 0xFFFFFFFFFFFFFFFFL << 64 - endExclusive;
        final long mask = x | y;

        return mask;
    }

    public static long toSet(
            final long l,
            final @Nonnegative int startInclusive,
            final @Nonnegative int endExclusive) {

        return l | toMask(startInclusive, endExclusive);
    }

    public static long toClear(
            final long l,
            final @Nonnegative int startInclusive,
            final @Nonnegative int endExclusive) {

        return l & ~ toMask(startInclusive, endExclusive);
    }

}
