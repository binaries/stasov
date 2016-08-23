package com.pocketmath.stasov.engine2.core;

import java.util.Set;

/**
 * Created by etucker on 3/19/16.
 */
public interface Handler {

    public Set<Value> translateIntoValues(final String input);

}
