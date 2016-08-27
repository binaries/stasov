package com.pocketmath.stasov.engine3.core.vesseltree;

import com.pocketmath.stasov.attributes.AttrSvcBase;
import com.pocketmath.stasov.engine.OpportunityDataBase;
import com.pocketmath.stasov.util.dynamicbitset.SBS3;

import java.util.*;

/**
 * Created by etucker on 8/23/16.
 */
public class VesselMatcher {

    private Vessel root;

    public VesselMatcher(Vessel root) {
        this.root = root;
    }

    public Results match(final OpportunityDataBase opportunity, final Results collector) {
        collector.setRoot(root);
        while (!collector.collection1Empty()) {
            for (final Vessel vessel : collector.getCollection1()) {
                for (final long attributeId : vessel.getAttributeIds()) {
                    final long[] valueIds = opportunity.getValueIds(attributeId);
                    vessel.match(attributeId, valueIds, collector.getCollection2());
                }
            }
            collector.swap();
        }
        final Collection<Vessel> matchingVessels = collector.getCollection1();

        final SBS3 matches = collector.getMatchesObject();
        for (final Vessel matchingVessel : matchingVessels) {
            final SBS3 vesselMatches = matchingVessel.getMatches();
            assert vesselMatches != null;
            matches.or(vesselMatches);
        }

        return collector;
    }

}
