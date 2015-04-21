package com.pocketmath.stasov.attributes;

import com.google.common.primitives.Longs;
import com.pocketmath.stasov.util.Weights;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongRBTreeSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

import java.util.Set;

/**
 * Created by etucker on 4/3/15.
 */
public abstract class AttrSvcBase {

    private long[] attrTypeIds = null;
    private final Weights weights;
    private final AttrTypeIdWeightComparator attrComparator;

    private Object2LongMap<String> typeNameToTypeId = null;

    private boolean registered = false;

    protected AttrSvcBase() {
        this.weights = new Weights();
        this.attrComparator = new AttrTypeIdWeightComparator(weights);
    }

    protected abstract AttributeHandler lookupHandler(final long attrTypeId);

    public long findTypeId(final String typeName) {
        assert(registered);
        return typeNameToTypeId.getLong(typeName);
    }

    public long findValue(final long attrTypeId, final String input) {
        assert(registered);
        final AttributeHandler handler = lookupHandler(attrTypeId);
        if (handler == null) throw new UnsupportedOperationException("Handler not found for attrTypeId=" + attrTypeId);
        return handler.find(input);
    }

    long findValue(final String typeName, final String input) {
        assert(registered);
        final long typeId = findTypeId(typeName);
        if (typeId < 1) throw new UnsupportedOperationException();
        return findValue(typeId, input);
    }

    protected void register(final Set<AttrMeta> set) {
        if (registered) throw new IllegalStateException("already registered");
        final LongSet typeIds = new LongRBTreeSet();
        final Object2LongMap<String> typeNameToTypeId = new Object2LongOpenHashMap<String>();
        for (final AttrMeta am : set) {
            typeIds.add(am.getTypeId());
            typeNameToTypeId.put(am.getTypeName(), am.getTypeId());
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
}
