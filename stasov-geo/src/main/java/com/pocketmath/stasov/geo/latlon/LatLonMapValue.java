package com.pocketmath.stasov.geo.latlon;

import com.pocketmath.stasov.scoring.ScoredResultWithMeta;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by etucker on 2/12/16.
 */
class LatLonMapValue<AreaType extends Area, O extends Serializable & Comparable<O>>
        implements Comparable<LatLonMapValue<AreaType, O>>, Serializable {

    final private AreaType area;
    final private O object;

    /**
     * @return a comparator specifying by order of highest to lowest includesPoint
     */
//    public static ScoreComparator scoreComparator() {
//        return SCORE_COMPARATOR;
//    }

    LatLonMapValue(@Nonnull final AreaType area, @Nonnegative final O object) {
        if (area == null) throw new IllegalArgumentException();
        if (object == null) throw new IllegalArgumentException();
        this.area = area;
        this.object = object;
    }

    public AreaType getArea() {
        return area;
    }

    public O getObject() {
        return object;
    }

    @Override
    public int compareTo(@Nonnull final LatLonMapValue<AreaType, O> o) {
        return getArea().compareTo(o.getArea());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LatLonMapValue<?, ?> value = (LatLonMapValue<?, ?>) o;

        return area.equals(value.area);
    }

    @Override
    public int hashCode() {
        return area.hashCode();
    }
}
