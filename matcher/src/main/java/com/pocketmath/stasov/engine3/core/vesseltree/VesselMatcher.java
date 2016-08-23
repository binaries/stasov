package com.pocketmath.stasov.engine3.core.vesseltree;

import com.pocketmath.stasov.OpportunityData;
import com.pocketmath.stasov.engine.OpportunityDataBase;

import java.util.Collection;

/**
 * Created by etucker on 8/23/16.
 */
public class VesselMatcher {

    private static class Collector {
        Collection<Vessel> collection1, collection2;
        public void setRoot(final Vessel root) {
            assert root != null;
            assert collection1.isEmpty();
            collection1.add(root);
        }
        public void swap() {
            Collection<Vessel> t = collection1;
            collection1 = collection2;
            collection2 = t;
        }
        public boolean collection1Empty() {
            return collection1.isEmpty();
        }
    }

    Vessel root;

    public void match(final OpportunityDataBase opportunity) {
        final Collector collector = new Collector();
        collector.setRoot(root);
        while (!collector.collection1Empty()) {
            for (final Vessel vessel : collector.collection1) {
                for (final long attributeId : vessel.getAttributeIds()) {
                    throw new UnsupportedOperationException("not yet implemented");
                    //fdopportunity.getData(attributeId);
                    // TODO -- translate and cache values
                    //vessel.match(attributeId, valueIds, collector.collection2);
                }
            }
            collector.swap();
        }
        final Collection<Vessel> matchingVessels = collector.collection1;
    }

}
