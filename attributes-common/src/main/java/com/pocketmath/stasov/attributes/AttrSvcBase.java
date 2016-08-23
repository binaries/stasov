package com.pocketmath.stasov.attributes;

import com.google.common.primitives.Longs;
import com.pocketmath.stasov.attributes.handler.base.AttributeHandler;
import com.pocketmath.stasov.util.Weights;
import com.pocketmath.stasov.util.validate.ValidationException;
import com.pocketmath.stasov.util.validate.ValidationRuntimeException;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongRBTreeSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by etucker on 4/3/15.
 */
public abstract class AttrSvcBase<IdType extends Serializable & Comparable<IdType>, ValueType> {

    private static final boolean SILENT = false;

    private long[] attrTypeIds = null;
    private final Weights weights;
    private final AttrTypeIdWeightComparator attrComparator;

    private Object2LongMap<String> typeNameToTypeId = null;

    /**
     * For testing/debugging use only.
     */
    private Map<Long,String> typeIdToName = new HashMap<Long, String>();

    private boolean registered = false;

    protected AttrSvcBase() {
        this.weights = new Weights();
        this.attrComparator = new AttrTypeIdWeightComparator(weights);
    }

    public abstract AttributeHandler<IdType, ? extends Object, ValueType> lookupHandler(final long attrTypeId);

    public long findTypeId(final String typeName) {
        assert(registered);
        return typeNameToTypeId.getLong(typeName);
    }

    public long findValue(final long attrTypeId, final String input, final boolean validate) {
        assert(registered);
        if (input == null) throw new IllegalArgumentException("input was null");
        final AttributeHandler handler = lookupHandler(attrTypeId);
        if (validate) {
            try {
                handler.tryValidate(input);
            } catch (ValidationException ve) {
                throw new ValidationRuntimeException(ve);
            }
        }
        if (handler == null) throw new UnsupportedOperationException("Handler not found for attrTypeId=" + attrTypeId);
        return handler.find(input);
    }

    public long findValue(final long attrTypeId, final String input) {
        return findValue(attrTypeId, input, false);
    }

    long findValue(final String typeName, final String input) {
        assert(registered);
        if (typeName == null) throw new IllegalArgumentException("typeName was null");
        if (input == null) throw new IllegalArgumentException("input was null");
        final long typeId = findTypeId(typeName);
        if (typeId < 1) throw new UnsupportedOperationException();
        return findValue(typeId, input);
    }

    protected void register(final Set<AttrMeta> set) {
        if (registered) throw new IllegalStateException("already registered");
        final LongSet typeIds = new LongRBTreeSet();
        final Object2LongMap<String> typeNameToTypeId = new Object2LongOpenHashMap<String>();
        for (final AttrMeta am : set) {
            if (am.getTypeId() < 1L) throw new IllegalArgumentException();
            if (am.getTypeName() == null) throw new IllegalArgumentException();
            if (am.getTypeName().isEmpty()) throw new IllegalArgumentException();
            if (am.getClassName() == null) throw new IllegalArgumentException();
            if (am.getClassName().isEmpty()) throw new IllegalArgumentException();

            typeIds.add(am.getTypeId());
            typeNameToTypeId.put(am.getTypeName(), am.getTypeId());

            // for testing/debugging
            this.typeIdToName.put(am.getTypeId(), am.getTypeName());
        }

        final long[] attrTypeIdsPrimitives = Longs.toArray(typeIds);
        LongArrays.quickSort(attrTypeIdsPrimitives, attrComparator);

        this.typeNameToTypeId = typeNameToTypeId;
        this.attrTypeIds = attrTypeIdsPrimitives;

        registered = true;
    }

    public final long[] getAttrTypeIds() {
        assert(registered);
        return attrTypeIds;
    }

    public final LongComparator getAttrsComparator() {
        assert(registered);
        return attrComparator;
    }

    /**
     * For testing/debugging use only.
     *
     * @param attrTypeId
     * @return
     */
    public String getAttrName(final long attrTypeId) {
        if (!SILENT) Logger.getAnonymousLogger().log(Level.WARNING, "Testing/debugging method only!  Not for production use.");

        return typeIdToName.get(attrTypeId);
    }

    /**
     * For testing/debugging use only.
     */
    public Iterable<String> sampleValues(final long attrTypeId, final Order order) {
        if (!SILENT) Logger.getAnonymousLogger().log(Level.WARNING, "Testing/debugging method only!  Not for production use.");

        final AttributeHandler attributeHandler = lookupHandler(attrTypeId);
        if (attributeHandler == null) return null;
        return attributeHandler.sampleValues(order);
    }

}
