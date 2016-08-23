package com.pocketmath.stasov.engine2.core;

/**
 * Created by etucker on 3/19/16.
 */
public interface Value {

    boolean in(final String input);

    boolean in(Value existingValue);
}
