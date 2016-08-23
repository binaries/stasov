package com.pocketmath.stasov.scoring;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * Created by etucker on 2/27/16.
 */
public interface ScoredResultWithMetaConsumer
    <ResultType extends Serializable & Comparable<ResultType>, MetaValueType> {

    public void accept(final float score, @Nonnull final ResultType result, @Nonnull final MetaValueType meta);

}
