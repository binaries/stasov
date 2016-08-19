package com.pocketmath.stasov.util.optimizedrangemap;

import com.pocketmath.stasov.util.validate.ValidationException;

import javax.annotation.Nonnull;

/**
 * Created by etucker on 2/7/16.
 */
public class GeoTranslator<T extends Comparable<T>> {

    private class Value implements Comparable<Value> {

        private final double lat, lon, resolution;

        public Value(final double lat, final double lon, final double resolution) {
            this.lat = lat;
            this.lon = lon;
            this.resolution = resolution;
        }

        @Override
        public int compareTo(Value o) {
            return 0;
        }
    }

    private class Entry<T extends Comparable<T>> extends AbstractOptiRMEntry<T,Value> {
        private final double lat, lon, resolution;

        public Entry(double lat, double lon, double resolution) {
            this.lat = lat;
            this.lon = lon;
            this.resolution = resolution;
        }

        public double getLat() {
            return lat;
        }

        public double getLon() {
            return lon;
        }

        public double getResolution() {
            return resolution;
        }

        @Override
        public Value getX0() {
            return null;
        }

        @Override
        public Value getX1() {
            return null;
        }

        @Override
        public T getT() {
            return null;
        }

        @Override
        public boolean inBounds(Value x) {
            return false;
        }

        @Override
        public void validate() throws ValidationException {

        }
    }

 //   private final OptiRangeMap<T,Value,Entry> {
//
//    }

//    private final AbstractOptiDoubleRangeMap<T> rmap = new AbstractOptiDoubleRangeMap<T>() {
//        @Override
//        protected OptiDoubleRMEntry<T> newEntry(@Nonnull Double x0, @Nonnull Double x1, @Nonnull T t) {
//            return new Entry<T>();
//        }
//    };

//    public void put(final double lat, final double lon, final double resolution) {
//        rmap
//    }

}
