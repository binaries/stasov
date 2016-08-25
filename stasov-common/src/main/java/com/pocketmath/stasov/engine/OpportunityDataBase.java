package com.pocketmath.stasov.engine;

import it.unimi.dsi.fastutil.longs.Long2LongArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.Set;

/**
 * Created by etucker on 3/30/15.
 */
public abstract class OpportunityDataBase {

    private Long2ObjectMap<long[]> valueIdsCache;

    public OpportunityDataBase(final int attributesCount) {
        this.valueIdsCache = new Long2ObjectOpenHashMap<>(attributesCount);
    }

    protected abstract int fastGetAttributesCount();

    public abstract Set<String> getRawValues(final long attributeId);

    protected abstract long[] lookupValueIds(final long attributeId);

    public long[] getValueIds(final long attributeId) {
        long[] valueIds = valueIdsCache.get(attributeId);
        if (valueIds != null || valueIdsCache.size() != fastGetAttributesCount()) {
            return valueIds;
        } else {
            valueIds = lookupValueIds(attributeId);
            if (valueIds != null) {
                valueIdsCache.put(attributeId, valueIds);
            }
            return valueIds;
        }
    }

}
