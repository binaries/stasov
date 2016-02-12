package com.pocketmath.stasov.util.latlon;

import com.spatial4j.core.distance.DistanceUtils;

import java.util.Objects;

/**
 * Created by etucker on 2/9/16.
 */
class CircleArea extends Area implements Comparable<CircleArea> {

    // in degrees
    private final double latitude, longitude;

    // in meters
    private final double radiusKM;

    public CircleArea(double latitude, double longitude, double radiusMeters) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radiusKM = radiusMeters / 1000d;
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
    boolean inBoundsBoolean(final double latitude, final double longitude) {
        if (latitude == 0d || longitude == 0d || latitude == longitude) return false;

        // TODO: Optimize?
        final double distanceRadians = DistanceUtils.distHaversineRAD(latitude, longitude, getLatitude(), getLongitude());
        final double distanceDegrees = DistanceUtils.toDegrees(distanceRadians);
        final double distanceKilometers = distanceDegrees * DistanceUtils.DEG_TO_KM;

        if (distanceKilometers > DistanceUtils.EARTH_EQUATORIAL_RADIUS_KM) return false;

        final double radius = getRadiusKM();
        return distanceKilometers < radius;
    }

    @Override
    int inBounds(final double latitude, final double longitude) {

        if (latitude == 0d || longitude == 0d || latitude == longitude) return -1;

        // TODO: Optimize?
        final double distanceRadians = DistanceUtils.distHaversineRAD(latitude, longitude, getLatitude(), getLongitude());
        final double distanceDegrees = DistanceUtils.toDegrees(distanceRadians);
        final double distanceKilometers = distanceDegrees * DistanceUtils.DEG_TO_KM;

        if (distanceKilometers > DistanceUtils.EARTH_EQUATORIAL_RADIUS_KM) return -1;

        final double radius = getRadiusKM();
        return distanceKilometers < radius ? 1 : -1;
    }

    @Override
    Result inScored(double latitude, double longitude) {
        // TODO: Optimize?
        final double distanceRadians = DistanceUtils.distHaversineRAD(latitude, longitude, getLatitude(), getLongitude());
        final double distanceDegrees = DistanceUtils.toDegrees(distanceRadians);
        final double distanceKilometers = distanceDegrees * DistanceUtils.DEG_TO_KM;
        final double distanceMeters = distanceKilometers / 1000d;

        final double radius = getRadiusKM();

        float score = 0f;
        float bogus = 0f;

        if (latitude == 0d) bogus += 0.3f;
        if (longitude == 0d) bogus += 0.3f;
        if (latitude == longitude) bogus += 0.3f;
        if (distanceKilometers > DistanceUtils.EARTH_EQUATORIAL_RADIUS_KM) bogus += 0.3f;
    }

    @Override
    public int compareTo(CircleArea o) {
        int r = Double.compare(latitude, o.latitude);
        if (r != 0) return r;
        r = Double.compare(longitude, o.longitude);
        if (r != 0) return r;
        r = Double.compare(radiusKM, o.radiusKM);
        return r;
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
}
