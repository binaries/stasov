package com.pocketmath.stasov.engine;

import com.pocketmath.stasov.attributes.AttrSvcBase;
import com.pocketmath.stasov.util.Long2LongMultiValueMap;
import com.pocketmath.stasov.util.TreeAlgorithm;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import it.unimi.dsi.fastutil.longs.LongSortedSets;

import java.util.Set;

/**
 * Created by etucker on 3/29/15.
 */
public class OpportunityQueryBase {

    private final Long2LongMultiValueMap multiValueMap = new Long2LongMultiValueMap(TreeAlgorithm.REDBLACK);

    private OpportunityDataBase oppData = null;

    private final AttrSvcBase attrSvc;

    private static final LongSortedSet EMPTY_SET = LongSortedSets.EMPTY_SET;

    public OpportunityQueryBase(AttrSvcBase attrSvc) {
        this.attrSvc = attrSvc;
    }

    final LongSortedSet translateValues(final long attrTypeId) {
        final LongSortedSet r = multiValueMap.get(attrTypeId);
        if (r != null) return (r == EMPTY_SET) ? null : r;
        //final IAttributeHandler handler = attrSvc.lookupHandler(attrTypeId);
        //if (handler == null) throw new UnsupportedOperationException("No handler found for attrTypeId=" + attrTypeId);
        final Set<String> impDatas = oppData.getData(attrTypeId);
        if (impDatas != null) {
            for (final String value : impDatas) {
                final long valueId = attrSvc.findValue(attrTypeId, value);
                multiValueMap.put(attrTypeId, valueId);
            }
            return multiValueMap.get(attrTypeId);
        } else {
            multiValueMap.fastPut(attrTypeId, EMPTY_SET);
            return null;
        }
    }

    void clear() {
        multiValueMap.clear();
    }

    void load(final OpportunityDataBase oppData) {
        multiValueMap.clear();
        this.oppData = oppData;
    }
}