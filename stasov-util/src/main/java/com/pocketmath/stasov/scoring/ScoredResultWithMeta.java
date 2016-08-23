package com.pocketmath.stasov.scoring;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * Created by etucker on 2/17/16.
 */
public class ScoredResultWithMeta<ResultType extends Serializable & Comparable<ResultType>, MetaValueType extends Serializable>
    extends ScoredResult<ResultType> {

    private final MetaValueType meta;

    public ScoredResultWithMeta(final float score, @Nonnull final ResultType result, @Nonnull final MetaValueType meta) {
        super(score, result);
        this.meta = meta;
    }

    public MetaValueType getMeta() {
        return meta;
    }

}
