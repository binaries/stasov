package com.pocketmath.stasov.engine3.core.vesseltree;

import com.pocketmath.stasov.attributes.validation.AttributesValidation;
import com.pocketmath.stasov.util.dynamicbitset.SBS3;
import com.pocketmath.stasov.util.multimaps2.array.AbstractLong2Long2ArrayHashMap;
import com.pocketmath.stasov.util.multimaps2.array.IArraySet;
import com.pocketmath.stasov.util.multimaps2.array.StasovArraySet;
import com.pocketmath.stasov.util.validate.ValidationException;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

/**
 * Created by etucker on 8/14/16.
 */
public class Vessel implements Comparable<Vessel> {

    private static final transient Comparator<Vessel> COMPARATOR = new Comparator<Vessel>() {
        @org.jetbrains.annotations.Contract(pure = true)
        @Override
        public int compare(final Vessel o1, final Vessel o2) {
            final int retVal = Long.compare(o1.id, o2.id);
            return retVal;
        }
    };

    private static class SetImpl extends StasovArraySet<Vessel> {

        public SetImpl() {
            super(COMPARATOR, 1);
        }

        @Override
        protected Vessel[] newArray(int size) {
            return new Vessel[size];
        }
    }

    private static class MapImpl extends AbstractLong2Long2ArrayHashMap<Vessel> {

        @Override
        protected IArraySet<Vessel> newThirdLevelSet() {
            return new SetImpl();
        }
    }

    private long id;

    private SBS3 bitset = new SBS3();
    private MapImpl inclusions = null, exclusions = null;

    public Vessel(final long id) {
        if (id < 0) throw new IllegalArgumentException();
        this.id = id;
    }

    public void addInclusion(final long attributeId, final long valueId, @NotNull final Vessel vessel) throws VesselModificationException {
        try {
            AttributesValidation.validateAttributeId(attributeId);
            AttributesValidation.validateAttributeValidId(valueId);
        } catch (final ValidationException ve) {
            throw new VesselModificationException(ve);
        }
        Objects.requireNonNull(vessel);
        if (inclusions == null)
            inclusions = new MapImpl();
        inclusions.put(attributeId, valueId, vessel);
    }

    public void addInclusions(final long[] attributeIds, final long[] valueIds, @NotNull final Vessel[] vessels) throws VesselModificationException {
        if (valueIds.length != vessels.length)
            throw new IllegalArgumentException();
        for (int i = 0; i < valueIds.length; i++) {
            addInclusion(attributeIds[i], valueIds[i], vessels[i]);
        }
    }

    public void addExclusion(final long attributeId, final long valueId, @NotNull final Vessel vessel) throws VesselModificationException {
        try {
            AttributesValidation.validateAttributeId(attributeId);
            AttributesValidation.validateAttributeValidId(valueId);
        } catch (final ValidationException ve) {
            throw new VesselModificationException(ve);
        }
        if (exclusions == null)
            exclusions = new MapImpl();
        exclusions.put(attributeId, valueId, vessel);
    }

    public void addExclusions(final long[] attributeIds, @NotNull final long[] valueIds, @NotNull final Vessel vessel) throws VesselModificationException {
        for (int i = 0; i < valueIds.length; i++) {
            addExclusion(attributeIds[i], valueIds[i], vessel);
        }
    }

    @Override
    public int compareTo(@NotNull final Vessel o) {
        return COMPARATOR.compare(this, o);
    }

    public void match(final long attributeId, @NotNull final long[] valueIds, @NotNull final Collection<Vessel> vessels) {
        inclusions.addEach(attributeId, valueIds, vessels);
        exclusions.addEach(attributeId, valueIds, vessels);
    }

}
