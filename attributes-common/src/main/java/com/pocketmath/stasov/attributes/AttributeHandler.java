package com.pocketmath.stasov.attributes;

/**
 * Created by etucker on 3/13/15.
 */
public abstract class AttributeHandler {

    public abstract long find(final String input);

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
