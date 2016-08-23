package com.pocketmath.stasov.geo.latlon;

import com.pocketmath.stasov.scoring.ScoredResultWithMetaConsumer;
import com.pocketmath.stasov.util.optimizedrangemap.OptiDoubleRMEntry;
import com.pocketmath.stasov.util.optimizedrangemap.OptiDoubleRangeMap;
import com.spatial4j.core.distance.DistanceUtils;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

/**
 * Created by etucker on 2/9/16.
 */
public class LatLonMap<O extends Comparable<O> & Serializable> {

    private final OptiDoubleRangeMap<LatLonMapValue<Area,O>> map = new OptiDoubleRangeMap<>();

    protected void put(@Nonnull final CircleArea area, @Nonnull final O object) {
        Objects.requireNonNull(area);
        Objects.requireNonNull(object);
        final LatLonMapValue value = new LatLonMapValue(area, object);
        final double delta = DistanceUtils.calcLonDegreesAtLat(area.getLatitude(), DistanceUtils.KM_TO_DEG * area.getRadiusKM());
        final double x0 = area.getLongitude() - delta, x1 = area.getLongitude() + delta;
        map.put(x0, x1, value);
    }

    public void putCircle(final double latitude, final double longitude, final double radiusKM, final O object) {
        final CircleArea circle = new CircleArea(latitude, longitude, radiusKM);
        put(circle, object);
    }

    private class RemovalTestFunction
            implements Function<OptiDoubleRMEntry<LatLonMapValue<Area,O>>,Boolean> {

        final private Area area;
        final private Area.Match[] matches;

        public RemovalTestFunction(@Nonnull final Area area, @Nonnull final Area.Match[] matches) {
            Objects.requireNonNull(area);
            Objects.requireNonNull(matches);
            if (matches.length < 1) throw new IllegalArgumentException();
            this.area = area;
            this.matches = matches;
        }

        @Override
        public Boolean apply(@Nonnull final OptiDoubleRMEntry<LatLonMapValue<Area,O>> entry) {
            final LatLonMapValue<Area, O> t = entry.getT();

            final Area oArea = t.getArea();
            Area.Match m = area.match(oArea);

            return Arrays.binarySearch(matches, m) > -1;
        }
    }

    // TODO: Internal methods will not support circle within circle.
    public void removeAllTotallyEnclosedBy(final Area area) {
        map.removeConditionally(area.getCenterLongitude(),
                new RemovalTestFunction(area, new Area.Match[]{Area.Match.EXACT,Area.Match.ENCLOSES}));
    }

    public void removeExact(final Area area) {
        map.removeConditionally(area.getCenterLongitude(),
                new RemovalTestFunction(area, new Area.Match[]{Area.Match.EXACT}));
    }

    private final static LatLonScoringFunction FAST_SCORING_FUNCTION = new LatLonScoringFunction(true);

    public void fastScore(
            final double latitude,
            final double longitude,
            @Nonnull final ScoredResultWithMetaConsumer resultsConsumer) {
            //@Nonnull final Consumer<ScoredResultWithMeta<LatLonMapValue<Area,O>, O>> resultsConsumer) {

        map.forEachScored2DEntry(longitude, latitude, resultsConsumer, FAST_SCORING_FUNCTION);
    }

    private final static LatLonScoringFunction SCORING_FUNCTION = new LatLonScoringFunction(false);

    public void score(
            final double latitude,
            final double longitude,
            @Nonnull final ScoredResultWithMetaConsumer resultsConsumer) {
            //@Nonnull final Consumer<ScoredResultWithMeta<LatLonMapValue<Area,O>, O>> resultsConsumer) {

        map.forEachScored2DEntry(longitude, latitude, resultsConsumer, SCORING_FUNCTION);
    }

}