package com.pocketmath.stasov.attributes;

/**
 * Created by etucker on 2/2/16.
 */
public abstract class FuzzyMatchAttributeHandler extends AttributeHandler {

    @Override
    public final long find(String input) {
        return USE_FUZZY_MATCH;
    }

    public abstract float[] convert(final String input);

    public abstract float score(final float[] convertedValues);

}
