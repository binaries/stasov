package com.pocketmath.stasov.attributes;

import com.pocketmath.stasov.attributes.handler.base.AttributeHandler;

/**
 * Created by etucker on 2/7/16.
 */
public abstract class ThreeDimensionalRangeMatchAttributeHandler extends AttributeHandler {

    @Override
    public final long find(String input) {
        return CUSTOM_INDEX;
    }
/*
    @Override
    public abstract float score3DRange(
            final double x0, final double x1, final double x2,
            final double p0, final double p1, final double p2);
            */

}
