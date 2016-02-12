package com.pocketmath.stasov.engine;

import com.pocketmath.stasov.attributes.AttrSvcBase;
import com.pocketmath.stasov.attributes.AttributeHandler;
import com.pocketmath.stasov.util.multimaps.Long2LongMultiValueSortedMap;
import com.pocketmath.stasov.util.IndexAlgorithm;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import it.unimi.dsi.fastutil.longs.LongSortedSets;

import java.util.Set;

/**
 * Created by etucker on 3/29/15.
 */
public class OpportunityQueryBase {

    private final Long2LongMultiValueSortedMap multiValueMap = new Long2LongMultiValueSortedMap(IndexAlgorithm.REDBLACK);

    private OpportunityDataBase oppData = null;

    private final AttrSvcBase attrSvc;

    private static final LongSortedSet EMPTY_SET = LongSortedSets.EMPTY_SET;

    public OpportunityQueryBase(AttrSvcBase attrSvc) {
        this.attrSvc = attrSvc;
    }

    final LongSortedSet translateValues(final long attrTypeId) {
        final LongSortedSet r = multiValueMap.getSorted(attrTypeId);
        if (r != null) return (r == EMPTY_SET) ? null : r;
        //final IAttributeHandler handler = attrSvc.lookupHandler(attrTypeId);
        //if (handler == null) throw new UnsupportedOperationException("No handler found for attrTypeId=" + attrTypeId);
        final Set<String> impDatas = oppData.getData(attrTypeId);
        if (impDatas != null) {
            for (final String value : impDatas) {
                final long valueId = attrSvc.findValue(attrTypeId, value);
                assert(valueId >= 0 || valueId == AttributeHandler.USE_3D_RANGE_MATCH || valueId == AttributeHandler.NOT_FOUND);
                multiValueMap.put(attrTypeId, valueId);
            }
            return multiValueMap.getSorted(attrTypeId);
        } else {
            multiValueMap.fastPut(attrTypeId, EMPTY_SET);
            return null;
        }
    }

    final Set<String> getInputStrings(final long attrTypeId) {
        return oppData.getData(attrTypeId);
    }

    void clear() {
        multiValueMap.clear();
    }

    void load(final OpportunityDataBase oppData) {
        multiValueMap.clear();
        this.oppData = oppData;
    }
}