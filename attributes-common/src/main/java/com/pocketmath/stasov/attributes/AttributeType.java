package com.pocketmath.stasov.attributes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by etucker on 3/28/15.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AttributeType {
    String name() default "";
    boolean cached() default false;
    int cacheSize() default -1;
}
