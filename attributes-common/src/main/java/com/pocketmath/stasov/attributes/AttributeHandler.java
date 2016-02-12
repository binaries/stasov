package com.pocketmath.stasov.attributes;

import com.pocketmath.stasov.util.validate.ValidationException;
import com.pocketmath.stasov.util.validate.ValidationResult;

/**
 * Created by etucker on 3/13/15.
 */
public abstract class AttributeHandler {

    public static final long NOT_FOUND = -1;
    static final long CACHED_NOT_FOUND = -2;
    public static final long USE_3D_RANGE_MATCH = -3;

    protected static final float DEFAULT_NOT_MATCH_SCORE = 0f;
    protected static final float DEFAULT_MATCH_SCORE = 1f;

    public abstract long find(final String input);

    public float[][] findRanges(final String input) {
        throw new UnsupportedOperationException();
    }

    public double[] findRangedPoints(final String input) {
        throw new UnsupportedOperationException();
    }

    public float scoreRanged(final double[] point) {
        throw new UnsupportedOperationException();
    }

    public float score3DRange(
            final double x0, final double x1, final double x2,
            final double p0, final double p1, final double p2) {
        throw new UnsupportedOperationException();
    }

    public void validate(final String input) throws ValidationException {
        // by default do nothing
    }

    /**
     * To facilitate testing.
     *
     * @return
     * @param order
     */
    public Iterable<String> sampleValues(final Order order) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

}
