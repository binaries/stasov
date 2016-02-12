package com.pocketmath.stasov.attrs;

import com.google.common.collect.ImmutableMap;
import com.pocketmath.stasov.attributes.ThreeDimensionalRangeMatchAttributeHandler;
import com.pocketmath.stasov.util.validate.ValidationException;
import com.spatial4j.core.distance.DistanceUtils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Supports 1 meter resolution.
 *
 * Created by etucker on 2/5/16.
 */
public class LatLon extends ThreeDimensionalRangeMatchAttributeHandler {

    public static final float BOGUS_LAT_LON_SCORE = -DEFAULT_MATCH_SCORE;
    public static final float PROBABLE_BOGUS_MATCH_SCORE = DEFAULT_MATCH_SCORE / 2f;

    private static final double parseNextDouble(final String input, int i, int j) {
        assert(input != null);
        assert(i >= 0);
        assert(j >= 0);
        double r = Double.NaN;
        for (; i < input.length(); i++) {
            final char c = input.charAt(i);
            if (c == ',') {
                final String s = input.substring(j, i).trim(); // trim handles white spaces
                assert(!s.isEmpty());
                r = Double.parseDouble(s);
                break;
            }
        }
        j = i;
        if (r == Double.NaN) throw new IllegalStateException();
        return r;
    }

    @Override
    public double[] findRangedPoints(final String input) {
        int i = 0, j = 0;

        // double precision for 1 meter resolution
        final double lat = parseNextDouble(input, i, j);
        final double lon = parseNextDouble(input, i, j);

        // in meters
        final double resolution = parseNextDouble(input, i, j);

        return new double[]{lat, lon, resolution};
    }
/*
    public boolean find3DRange(final String input, double x, double y, double z) {
        int i = 0, j = 0;

        // double precision for 1 meter resolution
        final double lat = parseNextDouble(input, i, j);
        final double lon = parseNextDouble(input, i, j);

        // in meters
        final double resolution = parseNextDouble(input, i, j);

        x = lat;
        y = lon;
        z = resolution;

        return true;
    }
*/

    private static final Map<String,String> SPATIAL_CONTEXT_ARGS = ImmutableMap.<String,String>of(
            "geo", "true",
            "distCalculator", "haversine",
            "normWrapLongitude", "true"
    );

    @Override
    public float score3DRange(
            final double x, final double y, final double z,
            final double px, final double py, final double pz) {

        final double lat = x;
        final double lon = y;
        // z is discarded

        final double rlat = px;
        final double rlon = py;
        final double radius = pz;

        // TODO: Optimize?
        final double distanceRadians = DistanceUtils.distHaversineRAD(lat, lon, rlat, rlon);
        final double distanceDegrees = DistanceUtils.toDegrees(distanceRadians);
        final double distanceKilometers = distanceDegrees * DistanceUtils.DEG_TO_KM;
        final double distanceMeters = distanceKilometers / 1000d;

        if (distanceKilometers > DistanceUtils.EARTH_EQUATORIAL_RADIUS_KM) return BOGUS_LAT_LON_SCORE;

        if (distanceMeters <= radius) {
            if (lat == 0 || lat == 0 || distanceKilometers > DistanceUtils.EARTH_EQUATORIAL_RADIUS_KM)
                return PROBABLE_BOGUS_MATCH_SCORE;
            else
                return DEFAULT_MATCH_SCORE;
        }

        return DEFAULT_NOT_MATCH_SCORE;
    }

    private static final Pattern validationPattern = Pattern.compile("\\s*\\d+'.'\\d+\\s*','\\s*\\d+'.'\\d+\\s*','\\s*\\d+'.'\\d+\\s*");

    @Override
    public void validate(final String input) throws ValidationException {
        final Matcher matcher = validationPattern.matcher(input);
        if (!matcher.matches()) throw new ValidationException();
    }

}
