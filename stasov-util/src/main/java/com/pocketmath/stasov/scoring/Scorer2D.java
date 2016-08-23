package com.pocketmath.stasov.scoring;

import javax.annotation.Nonnull;

/**
 * Created by etucker on 2/26/16.
 */
public interface Scorer2D<T> {

    void scoreWithMeta(final double x0, final double x1, final T t, @Nonnull final ScoredResultWithMetaConsumer resultsConsumer);

}
