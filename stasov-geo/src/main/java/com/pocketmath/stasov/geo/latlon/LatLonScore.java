package com.pocketmath.stasov.geo.latlon;

import com.pocketmath.stasov.scoring.FloatScore;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Created by etucker on 2/12/16.
 */
class LatLonScore extends FloatScore {
    public static final LatLonScore POSITIVE_MATCH = new LatLonScore(1f, 0f);
    public static final LatLonScore POSITIVE_MATCH_VERY_BOGUS = new LatLonScore(1f, 1f);
    public static final LatLonScore NEGATIVE_MATCH = new LatLonScore(-1f, 0f);
    public static final LatLonScore NEGATIVE_MATCH_VERY_BOGUS = new LatLonScore(-1f, 1f);

    private final float bogusLikelihood;

    LatLonScore(final float score, final float bogusLikelihood) {
        super(score);
        this.bogusLikelihood = bogusLikelihood;
    }

    public float getBogusLikelihood() {
        return bogusLikelihood;
    }

    @Override
    public int compareTo(@Nonnull final FloatScore o) {
        return Float.compare(o.getScore(), getScore()); // use "reverse order" from highest to lowest
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LatLonScore result = (LatLonScore) o;
        return Float.compare(result.getScore(), getScore()) == 0 &&
                Float.compare(result.bogusLikelihood, bogusLikelihood) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBogusLikelihood(), bogusLikelihood);
    }

}
