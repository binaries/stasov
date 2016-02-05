package com.pocketmath.stasov.util.optimizedrangemap;

import java.util.Objects;

/**
 * Created by etucker on 2/2/16.
 */
class OptRMEntry<T extends Comparable<T>> implements Comparable<OptRMEntry<T>> {
    private final float x0;
    private final float x1;
    private final T t;

    public OptRMEntry(float x0, float x1, T t) {
        if (x0 > x1) throw new IllegalArgumentException();
        if (t == null) throw new IllegalArgumentException();
        this.x0 = x0;
        this.x1 = x1;
        this.t = t;
    }

    public float getX0() {
        return x0;
    }

    public float getX1() {
        return x1;
    }

    public T getT() {
        return t;
    }

    @Override
    public int compareTo(final OptRMEntry<T> o) {
        int r = Float.compare(x0, o.x0);
        if (r != 0) return r;
        r = Float.compare(x1, o.x1);
        if (r != 0) return r;
        return t.compareTo(o.t);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OptRMEntry<?> that = (OptRMEntry<?>) o;
        return Float.compare(that.getX0(), getX0()) == 0 &&
                Float.compare(that.getX1(), getX1()) == 0 &&
                Objects.equals(getT(), that.getT());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX0(), getX1(), getT());
    }
}
