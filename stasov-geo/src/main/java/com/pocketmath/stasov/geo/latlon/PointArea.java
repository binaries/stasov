package com.pocketmath.stasov.geo.latlon;

/**
 * Created by etucker on 2/28/16.
 */
class PointArea extends CircleArea {

    public static final double TOLERANCE_RADIUS_KM = 1d / 1000d; // 1 meter

    public PointArea(final double latitude, final double longitude) {
        super(latitude, longitude, TOLERANCE_RADIUS_KM);
    }

    @Override
    LatLonScore includesPoint(double latitude, double longitude) {
        if (latitude == getLatitude() && longitude == getLongitude()) {
            return LatLonScore.POSITIVE_MATCH;
        }
        return super.includesPoint(latitude, longitude);
    }

}
