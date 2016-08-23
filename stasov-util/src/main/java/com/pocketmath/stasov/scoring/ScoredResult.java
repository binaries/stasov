package com.pocketmath.stasov.scoring;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

/**
 * Created by etucker on 2/17/16.
 */
public class ScoredResult<ResultType extends Comparable<ResultType> & Serializable>
        extends FloatScore {

    private final ResultType result;

    public ScoredResult(final float score, @Nonnull final ResultType result) {
        super(score);
        this.result = result;
    }

    public static final Comparator<ScoredResult> DESCENDING_SCORE_COMPARATOR_WITH_RESULT_TIEBREAKING =
            new Comparator<ScoredResult>() {
                @Override
                public int compare(@Nonnull final ScoredResult o1, @Nonnull final ScoredResult o2) {
                    assert(o1 != null);
                    assert(o2 != null);
                    int r = FloatScore.DESCENDING_COMPARATOR.compare(o1, o2);
                    if (r != 0) return r;
                    final Comparable r1 = o1.getResult(), r2 = o2.getResult();
                    Objects.requireNonNull(r1);
                    Objects.requireNonNull(r2);
                    return -1 * r1.compareTo(r2);
                }
            };

    public ResultType getResult() {
        return result;
    }

    public int compareTo(@Nonnull final ScoredResult o) {
        return DESCENDING_SCORE_COMPARATOR_WITH_RESULT_TIEBREAKING.compare(this, o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScoredResult<?> that = (ScoredResult<?>) o;
        return Objects.equals(result, that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result);
    }
}
