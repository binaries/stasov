package com.pocketmath.stasov.geo.latlon;

import com.pocketmath.stasov.util.StasovStrings;
import com.pocketmath.stasov.util.validate.NumberValidations;
import com.pocketmath.stasov.util.validate.ValidationException;
import com.spatial4j.core.distance.DistanceUtils;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Created by etucker on 2/9/16.
 */
class CircleArea extends Area {

    // in degrees
    private final double latitude, longitude;

    // in meters
    private final double radiusKM;

    public CircleArea(final double latitude, final double longitude, @Nonnegative final double radiusKM) {
        if (radiusKM <= 0d) throw new IllegalArgumentException();
        if (latitude > Float.MAX_VALUE) throw new IllegalArgumentException();

        try {
            NumberValidations.mustBeNumber(latitude);
            NumberValidations.mustBeNumber(longitude);
            NumberValidations.mustBeNumber(radiusKM);
        } catch (final ValidationException ve) {
            throw new IllegalArgumentException(ve);
        }

        // practical limits of what's computable
        if (latitude > Float.MAX_VALUE) throw new IllegalArgumentException();
        if (latitude < Float.MIN_VALUE) throw new IllegalArgumentException();
        if (longitude > Float.MAX_VALUE) throw new IllegalArgumentException();
        if (longitude < Float.MIN_VALUE) throw new IllegalArgumentException();

        this.latitude = latitude;
        this.longitude = longitude;
        this.radiusKM = radiusKM;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getRadiusKM() {
        return radiusKM;
    }

    @Override
    protected double getCenterLatitude() {
        return getLatitude();
    }

    @Override
    protected double getCenterLongitude() {
        return getCenterLongitude();
    }

    @Override
    LatLonScore fastScore(final double latitude, final double longitude) {

        if (latitude == 0d || longitude == 0d || latitude == longitude) return LatLonScore.NEGATIVE_MATCH_VERY_BOGUS;

        // TODO: Optimize?
        final double distanceRadians = DistanceUtils.distHaversineRAD(latitude, longitude, getLatitude(), getLongitude());
        final double distanceDegrees = DistanceUtils.toDegrees(distanceRadians);
        final double distanceKilometers = distanceDegrees * DistanceUtils.DEG_TO_KM;

        if (distanceKilometers > DistanceUtils.EARTH_EQUATORIAL_RADIUS_KM) return LatLonScore.NEGATIVE_MATCH_VERY_BOGUS;

        final double radius = getRadiusKM();
        return distanceKilometers < radius ? LatLonScore.POSITIVE_MATCH : LatLonScore.NEGATIVE_MATCH;
    }

    @Override
    LatLonScore includesPoint(final double latitude, final double longitude) {
        // TODO: Optimize?
        final double distanceRadians = DistanceUtils.distHaversineRAD(latitude, longitude, getLatitude(), getLongitude());
        final double distanceDegrees = DistanceUtils.toDegrees(distanceRadians);
        final double distanceKilometers = distanceDegrees * DistanceUtils.DEG_TO_KM;

        final double radiusKM = getRadiusKM();

        float score = 0f;
        float bogus = 0f;

        if (latitude == 0d) bogus += 0.5f;
        if (longitude == 0d) bogus += 0.5f;
        if (latitude == longitude) bogus += 0.5f;
        if (distanceKilometers > DistanceUtils.EARTH_EQUATORIAL_RADIUS_KM) bogus += 0.5f;

        if (distanceKilometers < radiusKM) {
            score = (float) (distanceKilometers / radiusKM);
        } else {
            if (distanceKilometers > Float.MAX_VALUE / 2) throw new IllegalStateException();
            score = -(float) ((distanceKilometers - radiusKM) / radiusKM);
        }

        assert(score < Float.POSITIVE_INFINITY);
        assert(score > Float.NEGATIVE_INFINITY);
        assert(score != Float.NaN);
        assert(score < Float.MAX_VALUE / 2);
        assert(score > Float.MIN_VALUE / 2);

        assert(bogus < Float.POSITIVE_INFINITY);
        assert(bogus > Float.NEGATIVE_INFINITY);
        assert(bogus != Float.NaN);
        assert(bogus < Float.MAX_VALUE / 2);
        assert(bogus > Float.MIN_VALUE / 2);

        return new LatLonScore(score, bogus);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CircleArea that = (CircleArea) o;
        return Double.compare(that.getLatitude(), getLatitude()) == 0 &&
                Double.compare(that.getLongitude(), getLongitude()) == 0 &&
                Double.compare(that.getRadiusKM(), getRadiusKM()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLatitude(), getLongitude(), getRadiusKM());
    }

    @Override
    public int compareTo(@Nonnull final Area o) {
        int r = super.compareTo(o);
        if ( r != 0 ) return r;
        final Class c1 = getClass(), c2 = o.getClass();

        if (! c1.equals(c2)) {
            final String s1 = c1.getCanonicalName();
            final String s2 = c2.getCanonicalName();
            return s1.compareTo(s2);
        }

        assert(o instanceof CircleArea);
        r = Double.compare(getRadiusKM(), ((CircleArea) o).getRadiusKM());
        assert( r != 0 );
        return r;
    }
}
