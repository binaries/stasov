package com.pocketmath.stasov.scoring;

import javax.annotation.Nonnull;
import java.util.Comparator;

/**
 * Created by etucker on 2/13/16.
 */
public abstract class FloatScore implements Comparable<FloatScore> {

    public static final Comparator<FloatScore> DESCENDING_COMPARATOR = new Comparator<FloatScore>() {
        @Override
        public int compare(@Nonnull final FloatScore o1, @Nonnull final FloatScore o2) {
            return -1 * Float.compare(o2.getScore(), o1.getScore());
        }
    };

    private final float score;

    public FloatScore(final float score) {
        this.score = score;
    }

    public float getScore() {
        return score;
    }

    @Override
    public int compareTo(FloatScore o) {
        return DESCENDING_COMPARATOR.compare(this, o);
    }
}
