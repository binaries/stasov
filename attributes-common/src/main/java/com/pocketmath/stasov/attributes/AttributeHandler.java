package com.pocketmath.stasov.attributes;

/**
 * Created by etucker on 3/13/15.
 */
public abstract class AttributeHandler {

    public static final long NOT_FOUND = -1;
    static long CACHED_NOT_FOUND = -2l;

    public void put(final String name, final long id) {
        throw new UnsupportedOperationException();
    }

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
