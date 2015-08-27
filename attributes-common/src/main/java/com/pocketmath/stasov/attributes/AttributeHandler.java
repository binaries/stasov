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
     */
    public abstract Iterable<String> sampleValues(final Order order);

}
