package com.pocketmath.stasov.util.optimizedrangemap;

import com.pocketmath.stasov.util.validate.ValidationException;

import java.util.Objects;

/**
 * To be used as a reference implementation.  Use primitive-specific implementations where possible.
 *
 * Created by etucker on 2/2/16.
 */
class OptiObjectRMEntry<T extends Comparable<T>, K extends Comparable<K>> extends AbstractOptiRMEntry<T,K> {
    private final K x0;
    private final K x1;
    private final T t;

    public OptiObjectRMEntry(final K x0, final K x1, final T t) {
        if (x0.compareTo(x1) < 0) throw new IllegalArgumentException();
        if (t == null) throw new IllegalArgumentException();
        this.x0 = x0;
        this.x1 = x1;
        this.t = t;
    }

    @Override
    public K getX0() {
        return x0;
    }

    @Override
    public K getX1() {
        return x1;
    }

    @Override
    public T getT() {
        return t;
    }

    @Override
    public boolean inBounds(final K x) {
        final int cmp0 = x.compareTo(x0);
        final int cmp1 = x.compareTo(x1);
        return cmp0 >= 0 && cmp1 < 0;
    }

    @Override
    public void validate() throws ValidationException {
        if (x0.compareTo(x1) < 0) throw new ValidationException();
        if (t == null) throw new ValidationException();
    }

    /*
    @Override
    public int compareTo(final AbstractOptiRMEntry<T,K> o) {
        int r = x0.compareTo(o.getX0());
        if (r != 0) return r;
        r = x1.compareTo(o.getX1());
        if (r != 0) return r;
        return t.compareTo(o.getT());
    }
    */

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractOptiRMEntry<?,?> that = (AbstractOptiRMEntry<?,?>) o;
        return Objects.equals(getX0(), that.getX0()) &&
                Objects.equals(getX1(), that.getX1()) &&
                Objects.equals(getT(), that.getT());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX0(), getX1(), getT());
    }
}
