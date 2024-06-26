package com.pocketmath.stasov.engine;

import com.pocketmath.stasov.attributes.AttrSvcBase;
import com.pocketmath.stasov.util.multimaps.Long2ObjectMultiValueSortedMap;
import com.pocketmath.stasov.util.IndexAlgorithm;

import java.util.Set;

/**
 * Intended principally for use in testing.  Permits the creation of opportunities via a map-like interface.
 *
 * Created by etucker on 4/5/15.
 */
public class MapOpportunityData extends OpportunityDataBase {

    private final AttrSvcBase attrSvc;

    private Long2ObjectMultiValueSortedMap<String> map = new Long2ObjectMultiValueSortedMap<String>(String.CASE_INSENSITIVE_ORDER, IndexAlgorithm.AVL);

    MapOpportunityData(final AttrSvcBase attrSvc) {
        this.attrSvc = attrSvc;
    }

    public void put(final String typeName, final String value) {
        long typeId = attrSvc.findTypeId(typeName);
        map.put(typeId, value);
    }

    @Override
    public Set<String> getData(final long attrTypeId) {
        return map.getSorted(attrTypeId);
    }

    @Override
    public String toString() {
        return "MapOpportunityData{" +
                "attrSvc=" + attrSvc +
                ", map=" + map +
                "} " + super.toString();
    }
}
