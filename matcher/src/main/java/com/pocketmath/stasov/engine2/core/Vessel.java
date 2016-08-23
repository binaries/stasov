package com.pocketmath.stasov.engine2.core;

import com.pocketmath.stasov.util.multimaps.ILong2ObjectMultiValueMap;
import com.pocketmath.stasov.util.multimaps.Long2ObjectMultiValueHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;

/**
 * Created by etucker on 3/19/16.
 */
public class Vessel {

    /**
     * attrTypeIds to inputs
     */
    private final ILong2ObjectMultiValueMap<String> inputs = new Long2ObjectMultiValueHashMap<>();

    public ObjectSet<String> getInputs(final long attrTypeId) {
        return inputs.get(attrTypeId);
    }

    void exclude(final long internalId) {

    }

    void score(final long internalId, long aspect, double score) {

    }

}
