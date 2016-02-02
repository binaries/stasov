package com.pocketmath.stasov.attributes;

/**
 * Created by etucker on 3/13/15.
 */
public abstract class AttributeHandler {

    public static final long NOT_FOUND = -1;
    static final long CACHED_NOT_FOUND = -2;
    public static final long USE_FUZZY_MATCH = -3;

    public abstract long find(final String input);

    public float[] convert(final String input) {
        throw new UnsupportedOperationException();
    }

    public float score(final float[] convertedValues) {
        throw new UnsupportedOperationException();
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
