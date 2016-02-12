package com.pocketmath.stasov.util.optimizedrangemap;

import com.pocketmath.stasov.util.validate.ValidationException;

import java.util.Objects;

/**
 * Created by etucker on 2/7/16.
 */
public class OptiFloatRMEntry<T extends Comparable<T>> extends AbstractOptiRMEntry<T,Float> {

    final float x0;
    final float x1;
    final T t;

    public OptiFloatRMEntry(final float x0, final float x1, final T t) {
        if (x0 > x1) throw new IllegalArgumentException();
        if (t == null) throw new IllegalArgumentException();
        this.x0 = x0;
        this.x1 = x1;
        this.t = t;
    }

    @Override
    public Float getX0() {
        return x0;
    }

    public float getFloatX() {
        return x0;
    }

    @Override
    public Float getX1() {
        return x1;
    }

    public float getFloatX1() {
        return x1;
    }

    @Override
    public T getT() {
        return t;
    }

    @Override
    public boolean inBounds(final Float x) {
        return floatInBounds(x.floatValue());
    }

    public boolean floatInBounds(final float x) {
        return x >= x0 && x < x1;
    }

    @Override
    public void validate() throws ValidationException {
        if (x0 > x1) throw new ValidationException();
        if (t == null) throw new ValidationException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OptiFloatRMEntry<?> that = (OptiFloatRMEntry<?>) o;
        return Float.compare(that.getX0(), getX0()) == 0 &&
                Float.compare(that.getX1(), getX1()) == 0 &&
                Objects.equals(getT(), that.getT());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX0(), getX1(), getT());
    }
}
