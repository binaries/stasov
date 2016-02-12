package com.pocketmath.stasov.util.optimizedrangemap.multidimensional;

import com.pocketmath.stasov.util.optimizedrangemap.AbstractOptiDoubleRangeMap;
import com.pocketmath.stasov.util.optimizedrangemap.OptiDoubleRMEntry;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Created by etucker on 2/7/16.
 */
public class ThreeDimensionalOptiDoubleRangeMap<T extends Comparable<T>> {

    private static final int DIMENSIONS = 3;

    final MultiDimensionalComponentOptiDoubleRangeMap<T>
            mapX = new MultiDimensionalComponentOptiDoubleRangeMap<T>(DIMENSIONS),
            mapY = new MultiDimensionalComponentOptiDoubleRangeMap<T>(DIMENSIONS),
            mapZ = new MultiDimensionalComponentOptiDoubleRangeMap<T>(DIMENSIONS);

    public void put(
            final double x0, final double x1,
            final double y0, final double y1,
            final double z0, final double z1,
            final T t) {
        mapX.put(x0, x1, t);
        mapY.put(y0, y1, t);
        mapZ.put(z0, z1, t);
    }

    public void forEachEntry(
            final double x,
            final double y,
            final double z,
            final Consensus3DConsumer<T> consumer) {

        mapX.forEachEntry(x, consumer);
        mapY.forEachEntry(y, consumer);
        mapZ.forEachEntry(z, consumer);
    }

    private static class MultiDimensionalComponentOptiDoubleRangeMap<T extends Comparable<T>> extends AbstractOptiDoubleRangeMap<T, MultiDimensionalComponentOptiDoubleRMEntry<T>> {
        private final int dimensions;

        public MultiDimensionalComponentOptiDoubleRangeMap(double scaleFactor, double tuneFactor, long initialInterval, int newIntervalCalculationAfterNPuts, int dimensions) {
            super(scaleFactor, tuneFactor, initialInterval, newIntervalCalculationAfterNPuts);
            this.dimensions = dimensions;
        }

        public MultiDimensionalComponentOptiDoubleRangeMap(int dimensions) {
            super();
            this.dimensions = dimensions;
        }

        @Override
        protected MultiDimensionalComponentOptiDoubleRMEntry<T> newEntry(@Nonnull Double x0, @Nonnull Double x1, @Nonnull T t) {
            return new MultiDimensionalComponentOptiDoubleRMEntry<>(x0, x1, t, dimensions);
        }
    }

    public static class ThreeDimensionalResult<T> implements Comparable<ThreeDimensionalResult<T>> {
        private double x,y,z;
        private final T t;
        public void
    }


    public static class MultiDimensionalComponentOptiDoubleRMEntry<T extends Comparable<T>> extends OptiDoubleRMEntry<T> {
        private final int dimensions;
        private final boolean[] found;
        public MultiDimensionalComponentOptiDoubleRMEntry(final double x0, final double x1, final T t, final int dimensions) {
            super(x0, x1, t);
            if (dimensions <= 0) throw new IllegalArgumentException();
            this.dimensions = dimensions;
            this.found = new boolean[this.dimensions];
            Arrays.fill(this.found, false);
        }
        void found(final int dimension) {
            found[dimension] = true;
        }
        boolean isFoundAllDimensions() {
            for (int i = 0; i < found.length; i++) {
                if (!found[i]) return false;
            }
            return true;
        }
    }

    public static abstract class ConsensusConsumer<T extends Comparable<T>> implements Consumer<MultiDimensionalComponentOptiDoubleRMEntry<T>> {
        private ObjectSet<MultiDimensionalComponentOptiDoubleRMEntry<T>> matches = null;

        private int dimension;

        public ConsensusConsumer(@Nonnegative final int initialDimension) {
            setDimension(initialDimension);
        }

        public abstract boolean match(@Nonnull MultiDimensionalComponentOptiDoubleRMEntry<T> entry, @Nonnegative final int dimension);

        @Override
        public void accept(@Nonnull final MultiDimensionalComponentOptiDoubleRMEntry<T> entry) {
            if (match(entry, dimension)) {
                entry.found(dimension);
                if (entry.isFoundAllDimensions()) {
                    if (matches == null) matches = new ObjectArraySet<>();
                    if (matches.size() >= 32) { // if the data is getting big optimize for larger size
                        // TODO: metrics
                        // TODO: tuning
                        final ObjectSet<MultiDimensionalComponentOptiDoubleRMEntry<T>> newMatches =
                                new ObjectLinkedOpenHashSet<>();
                        newMatches.addAll(matches);
                        matches = newMatches;
                    }
                    matches.add(entry);
                }
            }
        }

        public void setDimension(@Nonnegative final int dimension) {
            if (dimension <= 0) throw new IllegalArgumentException();
            this.dimension = dimension;
        }

        protected @Nonnegative int getDimension() {
            return dimension;
        }

        public @Nullable ObjectSet<MultiDimensionalComponentOptiDoubleRMEntry<T>> getMatches() {
            return matches;
        }
    }

    /*
    public abstract static class Consensus3DConsumer<T extends Comparable<T>> extends ConsensusConsumer<T> {

        private final double x,y,z;

        public Consensus3DConsumer(@Nonnegative int initialDimension, double x, double y, double z) {
            super(initialDimension);
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
        }

    }
    */

}
