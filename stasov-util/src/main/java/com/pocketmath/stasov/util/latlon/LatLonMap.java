package com.pocketmath.stasov.util.latlon;

import com.pocketmath.stasov.util.optimizedrangemap.OptiDoubleRangeMap;

import java.util.Comparator;
import java.util.Objects;

/**
 * Created by etucker on 2/9/16.
 */
public class LatLonMap<O extends Comparable<O>> {

   // public static final float BOGUS_LAT_LON_SCORE = -DEFAULT_MATCH_SCORE;
   // public static final float PROBABLE_BOGUS_MATCH_SCORE = DEFAULT_MATCH_SCORE / 2f;

    static class Value<AreaType extends Area, O extends Comparable<O>> implements Comparable<Value<AreaType,O>> {
        final private AreaType area;
        final private O object;

        public Value(AreaType area, O object) {
            this.area = area;
            this.object = object;
        }

        @Override
        public int compareTo(Value<AreaType,O> o) {
            throw new UnsupportedOperationException("not yet implemented");
        }
    }

    //private OptiDoubleRangeMap<Value> map = new OptiDoubleRangeMap<>();

    /**
     *
     *
     * @return First element
     */
    public float[] inBounds() {
        throw new UnsupportedOperationException("not yet implemented");
    }

}
