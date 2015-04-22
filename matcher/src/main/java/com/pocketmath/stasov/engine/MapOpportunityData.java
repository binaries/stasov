package com.pocketmath.stasov.engine;

import com.pocketmath.stasov.attributes.AttrSvcBase;
import com.pocketmath.stasov.util.Long2ObjectMultiValueMap;
import com.pocketmath.stasov.util.TreeAlgorithm;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongComparators;

import java.util.Set;

/**
 * Created by etucker on 4/5/15.
 */
public class MapOpportunityData extends OpportunityDataBase {

    private final AttrSvcBase attrSvc;

    private Long2ObjectMultiValueMap<String> map = new Long2ObjectMultiValueMap<String>(String.CASE_INSENSITIVE_ORDER, TreeAlgorithm.AVL);

    MapOpportunityData(final AttrSvcBase attrSvc) {
        this.attrSvc = attrSvc;
    }

    public void put(final String typeName, final String value) {
        long typeId = attrSvc.findTypeId(typeName);
        map.put(typeId, value);
    }

    @Override
    public Set<String> getData(final long attrTypeId) {
        return map.get(attrTypeId);
    }
}
