package com.pocketmath.stasov.util;

import java.util.Objects;

/**
 * Created by etucker on 8/29/15.
 */
public abstract class Weighted<V extends Comparable<V>> implements Comparable<Weighted<V>> {

    private double weight;

    public Weighted(final double weight) {
        if (weight < 0d) throw new IllegalArgumentException();
        if (weight > 1d) throw new IllegalArgumentException();
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public abstract V getValue();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        //if (o == null || getClass() != o.getClass()) return false;
        Weighted<?> weighted = (Weighted<?>) o;
        return Objects.equals(getValue(), weighted.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }

    public int compareTo(Weighted<V> o) {
        final int wCmp = Double.compare(getWeight(), o.getWeight());
        if (wCmp != 0) return wCmp;

        final int valCmp = getValue().compareTo(o.getValue());
        return valCmp;
    }
}
