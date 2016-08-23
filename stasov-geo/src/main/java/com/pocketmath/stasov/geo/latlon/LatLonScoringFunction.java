package com.pocketmath.stasov.geo.latlon;

import com.pocketmath.stasov.scoring.ScoredResultWithMetaConsumer;
import com.pocketmath.stasov.scoring.Scorer2D;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * Created by etucker on 2/26/16.
 */
class LatLonScoringFunction<T extends LatLonMapValue> implements Scorer2D<T> {

    private final boolean fast;

    LatLonScoringFunction(final boolean fast) {
        this.fast = fast;
    }

    protected boolean isIncludable(final float score) {
        return score > 0f;
    }

    @Override
    public void scoreWithMeta(
            final double longitude,
            final double latitude,
            @Nonnull final T t,
            ScoredResultWithMetaConsumer resultsConsumer) {

        assert(t != null);

        final Area area = t.getArea();
        assert(area != null);

        final Serializable object = t.getObject();
        assert(object != null);

        // note the reverse order vs the lat and lon parameters of this method
        final LatLonScore scoreObj =
                (fast ? area.includesPoint(latitude, longitude) : area.fastScore(latitude, longitude));
        assert(scoreObj != null);

        final float score = scoreObj.getScore();

        if (scoreObj.getBogusLikelihood() > 0.5f) return;
        if (isIncludable(score)) resultsConsumer.accept(score, area, object);
    }

}
