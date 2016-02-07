package com.pocketmath.stasov.util.optimizedrangemap;

import com.pocketmath.stasov.util.validate.ValidationException;

/**
 * Created by etucker on 2/7/16.
 */
public abstract class AbstractOptiRMEntry<T extends Comparable<T>, K extends Comparable<K>> {

    public abstract K getX0();

    public abstract K getX1();

    public abstract T getT();

    public abstract boolean inBounds(K x);

    public abstract void validate() throws ValidationException;

    /*
    @Override
    public int compareTo(AbstractOptiRMEntry<T, K> o) {
        int r = getX0().compareTo(o.getX0());
        if (r != 0) return r;
        r = getX1().compareTo(o.getX1());
        if (r != 0) return r;
        return getT().compareTo(o.getT());
    }
    */
}
