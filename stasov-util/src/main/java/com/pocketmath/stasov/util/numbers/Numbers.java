package com.pocketmath.stasov.util.numbers;

/**
 * Created by etucker on 2/1/16.
 */
public class Numbers {

    private static final long MAX_INT = (long) Integer.MAX_VALUE;
    private static final long MIN_INT = (long) Integer.MIN_VALUE;

    /**
     * Convert long to int with exception on loss of precision or truncation.
     * @param l
     * @return
     */
    public static int toIntC(final long l) {
        if (l > MAX_INT) throw new NumberConversionOutOfBoundsException();
        if (l <= MIN_INT) throw new NumberConversionOutOfBoundsException();
        int i = (int)l;
        assert(i == l);
        return i;
    }

    /**
     * Convert long to int with exception on loss of precision or truncation or value equal to or below zero.
     */
    public static int toIntPosC(final long l) {
        if (l > MAX_INT) throw new NumberConversionOutOfBoundsException();
        if (l <= 0) throw new NumberConversionOutOfBoundsException();
        int i = (int)l;
        assert(i == l);
        return i;
    }
}
