package com.pocketmath.stasov.attrs;

import com.google.common.collect.ImmutableMap;
import com.pocketmath.stasov.attributes.AttributeType;
import com.pocketmath.stasov.attributes.handler.base.CustomIndexAttributeHandler;
import com.pocketmath.stasov.attributes.handler.base.MapMultiEntryPointCustomAttributeHandler;
import com.pocketmath.stasov.geo.latlon.LatLonMap;
import com.pocketmath.stasov.scoring.ScoredResultWithMetaConsumer;
import com.pocketmath.stasov.util.validate.ValidationException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Supports 1 meter resolution.
 *
 * Sample definition of latlon in PMTL:
 * <code>
 *     latlon={10,20,100}
 * </code>
 *
 * This would be latitude of 10, longitude of 20 with a radius of 100 meters.
 *
 * Created by etucker on 2/5/16.
 */
@AttributeType
public class LatLon<IdType extends Serializable & Comparable<IdType>, EntryPointType, ValueType extends Serializable & Comparable<ValueType>>
    extends MapMultiEntryPointCustomAttributeHandler<IdType, EntryPointType, ValueType> {

    private static class LatLonSeg<IdType extends Serializable & Comparable<IdType>, ValueType extends Serializable & Comparable<ValueType>> extends MapMultiEntryPointCustomAttributeHandler.SegmentHandler<IdType,ValueType> {

        private final LatLonMap<IdType> map = new LatLonMap<IdType>();

        private static final double parseNextDouble(final String input, int i, int j) {
            assert(input != null);
            assert(i >= 0);
            assert(j >= 0);
            double r = Double.NaN;
            for (; i < input.length(); i++) {
                final char c = input.charAt(i);
                if (Character.isWhitespace(c)) continue;
                assert(Character.isDigit(c) || c == ',');
                if (c == ',') {
                    final String s = input.substring(j, i);
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
        public void afterPrimaryIndex(
                @Nonnull final IdType id,
                @Nonnull final String input,
                @Nullable final ValueType exit) {

            //final Logger log = getIndexingLogger();

            int i = 0, j = 0;

            // double precision for 1 meter resolution
            // should be faster than String.split as it has less object creation
            final double lat = parseNextDouble(input, i, j);
            final double lon = parseNextDouble(input, i, j);

            // in meters
            final double radius = parseNextDouble(input, i, j);

            //log.log(Level.FINER, "id=" + id + "lat=" + lat + ", lon=" + lon + ", radius=" + radius);

            map.putCircle(lat, lon, radius, id);
        }

        @Override
        public void customIndexQueryInclusionary(
                @Nonnull final String input,
                @Nonnull final ScoredResultWithMetaConsumer resultsConsumer) {

            int i = 0, j= 0;

            // double precision for 1 meter resolution
            // should be faster than String.split as it has less object creation
            final double lat = parseNextDouble(input, i, j);
            final double lon = parseNextDouble(input, i, j);

            map.fastScore(lat, lon, resultsConsumer);
        }

        @Override
        public void customIndexQueryExclusionary(
                @Nonnull String input,
                @Nonnull ScoredResultWithMetaConsumer resultsConsumer) {

            throw new UnsupportedOperationException("not yet implemented");
        }

        @Override
        public void beforePrimaryRemove(@Nonnull IdType id) {

            throw new UnsupportedOperationException("not yet implemented");
        }
    }

    private static final Pattern validationPattern = Pattern.compile("\\s*\\d+'.'\\d+\\s*','\\s*\\d+'.'\\d+\\s*','\\s*\\d+'.'\\d+\\s*");

    @Override
    public void tryValidate(@Nonnull final String input) throws ValidationException {
        final Matcher matcher = validationPattern.matcher(input);
        if (!matcher.matches()) throw new ValidationException();
    }

    @Override
    protected SegmentHandler newSegment(@Nonnull EntryPointType entryPoint) {
        return new LatLonSeg<IdType,ValueType>();
    }
}
