package com.pocketmath.stasov.engine3.core;

import com.pocketmath.stasov.util.IndexAlgorithm;
import com.pocketmath.stasov.util.dynamicbitset.SBS3;
import com.pocketmath.stasov.util.multimaps.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Comparator;

/**
 * Created by etucker on 8/14/16.
 */
public class Vessel implements Comparable<Vessel> {

    private static class MapImpl extends Long2ObjectMultiValueHashReadCachedMap<Vessel> {
        public MapImpl() {
            super(IndexAlgorithm.HASH);
        }

        @Override
        protected Vessel[] newValuesArray(int size) {
            return new Vessel[size];
        }
    }

    private long id;

    private long attributeId;

    private SBS3 bitset = new SBS3();
    private MapImpl inclusions = null, exclusions = null;

    private static final Comparator<Vessel> COMPARATOR = new Comparator<Vessel>() {
        @org.jetbrains.annotations.Contract(pure = true)
        @Override
        public int compare(final Vessel o1, final Vessel o2) {
            final int retVal = Long.compare(o1.id, o2.id);
            assert retVal == 0 ? o1.attributeId == o2.attributeId : true;
            return retVal;
        }
    };

    public Vessel(final long id, final long attributeId) {
        if (id < 0) throw new IllegalArgumentException();
        if (attributeId < 0) throw new IllegalArgumentException();
        this.id = id;
        this.attributeId = attributeId;
    }

    public void addInclusion(final long valueId, @NotNull final Vessel vessel) {
        if (inclusions == null)
            inclusions = new MapImpl();
        inclusions.put(valueId, vessel);
    }

    public void addInclusions(final long[] valueIds, @NotNull final Vessel[] vessels) {
        if (valueIds.length != vessels.length)
            throw new IllegalArgumentException();
        for (int i = 0; i < valueIds.length; i++) {
            addInclusion(valueIds[i], vessels[i]);
        }
    }

    public void addExclusion(final long valueId, @NotNull final Vessel vessel) {
        if (exclusions == null)
            exclusions = new MapImpl();
        exclusions.put(valueId, vessel);
    }

    public void addExclusions(final long[] valueIds, @NotNull final Vessel vessel) {
        for (int i = 0; i < valueIds.length; i++) {
            addExclusion(valueIds[i], vessel);
        }
    }

    @Override
    public int compareTo(@NotNull final Vessel o) {
        return COMPARATOR.compare(this, o);
    }

    public void match(final long[] valueIds, @NotNull final Collection<Vessel> vessels) {
        for (final long valueId : valueIds) {
            {
                final Vessel[] subVessels = inclusions.getArray(valueId);
                for (Vessel subVessel : subVessels) vessels.add(subVessel);
            }
            {
                final Vessel[] subVessels = exclusions.getArray(valueId);
                for (final Vessel subVessel : subVessels) vessels.remove(subVessel);
            }
        }
    }

}
