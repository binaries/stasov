package com.pocketmath.stasov.attributes;

/**
 * Created by etucker on 2/7/16.
 */
public abstract class ThreeDimensionalRangeMatchAttributeHandler extends AttributeHandler {

    @Override
    public final long find(String input) {
        return USE_3D_RANGE_MATCH;
    }

    @Override
    public abstract float score3DRange(
            final double x0, final double x1, final double x2,
            final double p0, final double p1, final double p2);

}
