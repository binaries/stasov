package com.pocketmath.stasov.engine2.core;

import it.unimi.dsi.fastutil.longs.LongSet;

import java.util.Set;

/**
 * Created by etucker on 3/19/16.
 */
public class AndGrou2Impl implements AndGrou2 {

    private MatchNod2[] nodes;

    private Set<Value> toValues(final Vessel vessel, final long attrTypeId) {
        checkCache: {
            final Set<Value> values = vessel.getCachedValues(attrTypeId);
            if (values != null) return values;
        }

        final Handler handler = null;
        Set<String> inputStrings = vessel.getInputs(attrTypeId);
        final Set<Value> values = handler.translateIntoValues(inputStrings);
        vessel.putCachedValues(attrTypeId, values);
        return values;
    }

    private LongSet toLongValues(final Vessel vessel, final long attrTypeId) {

    }

    @Override
    public void function(Vessel vessel) {
        for (int i = 0; i < nodes.length; i++) {
            final MatchNod2 node = nodes[i];
            final long attrTypeId = node.getAttrTypeId();
            if (attrTypeCustom) {
                final Set<Value> inputValues = toValues(vessel, attrTypeId);
                if (inputValues == null) continue;
                node.onInputValues(vessel, inputValues);
            } else {
                final LongSet inputValues = toLongValues(vessel, attrTypeId);
                if (inputValues == null) continue;
                node.onInputValues(vessel, inputValues);
            }
        }
    }

}
