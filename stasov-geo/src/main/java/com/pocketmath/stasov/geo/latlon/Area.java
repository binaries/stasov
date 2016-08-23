package com.pocketmath.stasov.geo.latlon;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

/**
 * Created by etucker on 2/9/16.
 */
public abstract class Area implements Comparable<Area>, Serializable {

    abstract LatLonScore includesPoint(final double latitude, final double longitude);

    /**
     * It's recommended that subclasses override this method when it's possible to
     * be more efficient than their implementation of scoredIn.
     *
     * @param latitude
     * @param longitude
     * @return 0 = no opinion or undetermined, 1 = positive match, -1 is definitely not matched
     */
    LatLonScore fastScore(final double latitude, final double longitude) {
        final LatLonScore r = includesPoint(latitude, longitude);
        return r;
        /*
        final float includesPoint = r.getScore();
        if (includesPoint > 0)
            return r.getBogusLikelihood() <= 0 ? Scoring. : -1;
        else if (includesPoint == 0)
            return r.getBogusLikelihood() <= 0 ? 0 : -1;
        else {
            assert(includesPoint < 0);
            return -1;
        }
        */
    }

    /**
     *
     * It's recommended that subclasses override this method when it's possible to
     * be more efficient than other methods.
     *
     * @param latitude
     * @param longitude
     */
    boolean inBoundsBoolean(final double latitude, final double longitude) {
        final LatLonScore result = fastScore(latitude, longitude);
        return result.getScore() >= 0f && result.getBogusLikelihood() <= 0f;
    }

    /**
     * @return latitude at center.
     */
    protected abstract double getCenterLatitude();

    /**
     * @return longitude at center.
     */
    protected abstract double getCenterLongitude();

    private static final Comparator<Area> CENTER_COMPARATOR = new Comparator<Area>() {
        public int compare(@Nonnull final Area o1, @Nonnull final Area o2) {
            int r = Double.compare(o1.getCenterLatitude(), o2.getCenterLatitude());
            if (r != 0) return r;
            return Double.compare(o1.getCenterLongitude(), o1.getCenterLongitude());
        }
    };

    static final Comparator<Area> centerComparator() {
        return CENTER_COMPARATOR;
    }

    @Override
    public int compareTo(@Nonnull final Area o) {
        return CENTER_COMPARATOR.compare(this, o);
    }

    public static enum Match {
        ENCLOSED_BY, ENCLOSES, EXACT, NO
    }

    public Match match(@Nonnull final Area oArea) {
        Objects.requireNonNull(oArea);
        if (this instanceof PointArea) {
            if (oArea instanceof CircleArea)
                return oArea.includesPoint(getCenterLatitude(), getCenterLongitude()).getScore() > 0f ?
                        Match.ENCLOSED_BY : Match.NO;
            if (oArea instanceof PointArea)
                return oArea.includesPoint(getCenterLatitude(), getCenterLongitude()).getScore() > 0f ?
                        Match.EXACT : Match.NO;
            throw new UnsupportedOperationException();
        }
        if (this instanceof CircleArea) {
            if (oArea instanceof PointArea)
                return includesPoint(oArea.getCenterLatitude(), oArea.getCenterLongitude()).getScore() > 0f ?
                        Match.ENCLOSES : Match.NO;
            throw new UnsupportedOperationException();
        }
        throw new UnsupportedOperationException();
    }

}
