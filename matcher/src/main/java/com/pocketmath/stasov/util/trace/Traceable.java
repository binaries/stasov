package com.pocketmath.stasov.util.trace;

/**
 * Created by etucker on 8/26/15.
 */
public @interface Traceable {

    public boolean enabled() default true;

}
