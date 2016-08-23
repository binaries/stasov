package com.pocketmath.stasov.util.optimizedrangemap;

import com.pocketmath.stasov.util.validate.ValidationException;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by etucker on 2/7/16.
 */
public class OptiDoubleRMEntry<T extends Comparable<T>> extends AbstractOptiRMEntry<T, Double> implements Serializable {

    final double x0;
    final double x1;
    final T t;

    public OptiDoubleRMEntry(final double x0, final double x1, final T t) {
        if (x0 > x1) throw new IllegalArgumentException();
        if (t == null) throw new IllegalArgumentException();
        this.x0 = x0;
        this.x1 = x1;
        this.t = t;
    }

    @Override
    public Double getX0() {
        return x0;
    }

    public double getDoubleX0() {
        return x0;
    }

    @Override
    public Double getX1() {
        return x1;
    }

    public double getDoubleX1() {
        return x1;
    }

    @Override
    public T getT() {
        return t;
    }

    @Override
    public boolean inBounds(final Double x) {
        return doubleInBounds(x.doubleValue());
    }

    public boolean doubleInBounds(final double x) {
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
        OptiDoubleRMEntry<?> that = (OptiDoubleRMEntry<?>) o;
        return Double.compare(that.getX0(), getX0()) == 0 &&
                Double.compare(that.getX1(), getX1()) == 0 &&
                Objects.equals(getT(), that.getT());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX0(), getX1(), getT());
    }
}
