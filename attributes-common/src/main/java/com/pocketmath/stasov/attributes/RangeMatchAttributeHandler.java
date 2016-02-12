package com.pocketmath.stasov.attributes;

/**
 * Created by etucker on 2/2/16.
 */
public abstract class RangeMatchAttributeHandler extends AttributeHandler {

    @Override
    public final long find(String input) {
        return USE_3D_RANGE_MATCH;
    }

    @Override
    public abstract double[] findRangedPoints(final String input);

}
